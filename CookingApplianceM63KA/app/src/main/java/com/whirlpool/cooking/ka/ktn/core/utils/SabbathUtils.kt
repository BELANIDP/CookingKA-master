package core.utils

import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.view.Gravity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.whirlpool.cooking.common.utils.TimeoutViewModel.TimeoutStatesEnum
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.timers.Timer
import core.utils.AppConstants.HEADER_VIEW_CENTER_ICON_GONE
import core.utils.NavigationUtils.Companion.TAG
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils.Companion.observeHmiKnobListener

/**
 * File       : core/utils/SabbathUtils.java
 * Brief      : Contains helper utility methods which provides APIs related to Sabbath Mode and Navigation
 * Author     : Hiren
 * Created On : 4/2/24
 * Details    : This Util class is used for Sabbath related methods which includes Set, stop navigate recipes to sabbath, etc.
 */
class SabbathUtils {
    companion object {
        /**
         * use this to find out whether to show instruction  screen for SabbathMode or not
         *
         * @return true if Instruction screen needs to be displayed otherwise false
         */
        fun isSabbathModeInstructionScreenShown(): Boolean {
            return SettingsViewModel.getSettingsViewModel()
                .getUserDataBooleanValue(AppConstants.RECIPE_INSTRUCTION_SABBATH_MODE, true)
        }

        /**
         * use this to find out whether to show instruction  screen for SabbathMode or not
         *
         * @return true if Instruction screen needs to be displayed otherwise false
         */
        fun isSabbathBakeInstructionScreenShown(): Boolean {
            return SettingsViewModel.getSettingsViewModel()
                .getUserDataBooleanValue(AppConstants.RECIPE_INSTRUCTION_SABBATH_BAKE, true)
        }

        /**
         * starts Sabbath Mode
         *
         */
        fun startSabbathMode(fragment: Fragment) {
            if (SettingsViewModel.getSettingsViewModel()
                    .setSabbathMode(SettingsViewModel.SabbathMode.SABBATH_COMPLIANT)
            ) {
                HMILogHelper.Logd(
                    fragment.tag,
                    "sabbath mode is SABBATH_COMPLIANT, navigating to clock screen"
                )
                CookingAppUtils.navigateToSabbathStatusOrClockScreen(fragment)
            } else {
                HMILogHelper.Logd(fragment.tag, "sabbath mode is NOT SABBATH_COMPLIANT")
            }
        }

        /**
         * TODOto check is loaded recipe is Sabbath Bake or not
         *
         * @param cookingVM
         * @return
         */
        fun isSabbathRecipe(cookingVM: CookingViewModel): Boolean {
            return cookingVM.recipeExecutionViewModel.recipeName.value == AppConstants.RECIPE_INSTRUCTION_SABBATH_BAKE
        }

        /**
         * Method to handle the right text button click in the oven cooking
         *
         * @param cookTimeSec cook timer in seconds
         */
        fun handleSabbathCookTimeRightTextButtonClick(
            cookingViewModel: CookingViewModel?,
            fragment: Fragment,
            cookTimeSec: Int,
        ) {/* Scenario : If user is in cookTime numberPad screen and if the cooking completes,
              cookTime complete popup will be shown and user selects see later in the popup,now from
              the cook  time numberPad user can edit the time and start the cooking again. for that
              addCookTime should be used instead of setCookTime.*/
            val recipeViewModel = cookingViewModel?.recipeExecutionViewModel
            val recipeCookingState = recipeViewModel?.recipeCookingState?.value
            if (recipeCookingState == RecipeCookingState.KEEPING_WARM || recipeCookingState == RecipeCookingState.TURNING_OFF || recipeCookingState == RecipeCookingState.STAYING_ON) {
                if (recipeViewModel.cookTimerState?.value == Timer.State.RUNNING) {
                    //Set Cook Timer
                    if (recipeViewModel.setCookTime(cookTimeSec.toLong()).isError) return
                } else {
                    //add Cook Timer
                    if (recipeViewModel.addCookTime(cookTimeSec.toLong()) == RecipeErrorResponse.NO_ERROR) {
                        HMILogHelper.Logi("addCookTime$cookTimeSec success")
                    } else {
                        HMILogHelper.Loge("addCookTime$cookTimeSec failed")
                        return
                    }
                }
            } else {
                if (recipeViewModel?.cookTimerState?.value == Timer.State.IDLE) {
                    //Set Cook Timer
                    if (recipeViewModel.setCookTime(cookTimeSec.toLong()).isError) return
                    if (recipeCookingState != RecipeCookingState.COOKING) {
                        if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
                            HMILogHelper.Logd(
                                fragment.tag,
                                "navigating to sabbathDoubleCavityStartPreview after cookTime set for ${cookingViewModel.cavityName.value}"
                            )
                            navigateSafely(
                                fragment,
                                R.id.action_cookTimeFragment_to_sabbathDoubleCavityStartPreview,
                                null,
                                null
                            )
                            return
                        }
                        val (isProbeConnected, connectedCavityViewModel) = MeatProbeUtils.isAnyCavityHasMeatProbeConnected()
                        if (isProbeConnected) {
                            HMILogHelper.Loge(fragment.tag, "Meat Probe is connected for cavity ${connectedCavityViewModel?.cavityName?.value}, not executing recipe until probe is removed")
                            probeDetectedBeforeSabbathProgramming(
                                fragment,
                                connectedCavityViewModel,
                                {},
                                {
                                    HMILogHelper.Loge(fragment.tag, "Meat Probe is just removed for cavity ${connectedCavityViewModel?.cavityName?.value}, executing recipe since probe is removed")
                                    handleSabbathCookTimeRightTextButtonClick(
                                        cookingViewModel,
                                        fragment,
                                        cookTimeSec,
                                    )
                                })
                            return
                        }
                        val isSabbathSuccess =
                            SettingsViewModel.getSettingsViewModel().setSabbathMode(
                                SettingsViewModel.SabbathMode.SABBATH_COMPLIANT
                            )
                        HMILogHelper.Logd(
                            fragment.tag,
                            "Sabbath is isSabbathSuccess $isSabbathSuccess"
                        )
                        val recipeErrorResponse = recipeViewModel.execute()
                        if (isSabbathSuccess && recipeErrorResponse == RecipeErrorResponse.NO_ERROR) {
                            HMILogHelper.Logi("startCookTimer$cookTimeSec success")
                        } else {
                            HMILogHelper.Loge("startCookTimer$cookTimeSec failed")
                            if (recipeErrorResponse != null) {
                                cookingViewModel.let {
                                    CookingAppUtils.handleCookingError(
                                        fragment, it, recipeErrorResponse, false
                                    )
                                }

                            }
                            return
                        }
                    } else {
                        //Set Cook Timer
                        if (java.lang.Boolean.FALSE == cookingViewModel.doorState?.value) {
                            if (recipeViewModel.startCookTimer() == RecipeErrorResponse.NO_ERROR) {
                                HMILogHelper.Logi("startCookTimer$cookTimeSec success")
                            } else {
                                HMILogHelper.Loge("startCookTimer$cookTimeSec failed")
                                return
                            }
                        }
                    }
                } else {
                    //Set Cook Timer
                    if (recipeViewModel?.setCookTime(cookTimeSec.toLong())?.isError == true) return
                }
            }
            //Navigate to clock screen or status screen
            CookingAppUtils.navigateToSabbathStatusOrClockScreen(fragment)
        }

        /**
         * set temperature for Sabbath Bake recipe and navigate to Set Cook Time
         */
        fun sabbathSetTemperature(
            fragment: Fragment,
            cookingViewModel: CookingViewModel?,
            temperature: Float,
            isFromKnob: Boolean
        ) {
            val recipeErrorResponse =
                cookingViewModel?.recipeExecutionViewModel?.setTargetTemperature(temperature)
            HMILogHelper.Logd(
                "$TAG Sabbath setTemperature",
                "$temperature with error response= ${recipeErrorResponse?.name}"
            )
            if (recipeErrorResponse != null && recipeErrorResponse.isError) {
                CookingAppUtils.handleCookingError(
                    fragment, cookingViewModel, recipeErrorResponse, true
                )
                return
            }
            if (isFromKnob)
                navigateSafely(fragment, R.id.action_to_sabbathCookTimeTumblerFragment, null, null)
            else
                navigateSafely(fragment, R.id.action_to_sabbathCookTimeNumPadFragment, null, null)
            return
        }

        /**
         * Brief : for Handling probe instruction when probe is inserted.
         */
        fun probeAlreadyPresentBeforeSabbathMenu(
            fragment: Fragment,
            parentCookingViewModel: CookingViewModel?,
            onMeatProbeRemoved: () -> Unit
        ) {
            fragment.activity?.supportFragmentManager?.let { CookingAppUtils.dismissAllDialogs(it) }
            val popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(R.string.text_title_remove_probe)
                .setTopMarginForTitleText(AppConstants.SELF_CLEAN_POPUP_TITLE_TOP_MARGIN_74PX)
                .setTopMarginForDescriptionText(AppConstants.POPUP_DESCRIPTION_TOP_MARGIN_6PX)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextFont(ResourcesCompat.getFont(fragment.requireContext(), R.font.roboto_light))
                .setDescriptionMessage(R.string.text_description_remove_probe_for_sabbath)
                .setIsPopupCenterAligned(true).build()

            popupBuilder.setTimeoutCallback(object :
                ScrollDialogPopupBuilder.OnTimeoutObserverListener {
                override fun onTimeout(timeoutStatesEnum: TimeoutStatesEnum?) {
                    if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                        HMILogHelper.Logd("probeAlreadyPresentBeforeSabbathMenu Timeout ${parentCookingViewModel?.cavityName?.value}")
                        MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                            popupBuilder, fragment
                        )
                        CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                    }
                }
            }, fragment.resources.getInteger(R.integer.session_long_timeout))

            val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                (object : MeatProbeUtils.MeatProbeListener {
                    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                        HMILogHelper.Logd("probeAlreadyPresentBeforeSabbathMenu, Sabbath probe Inserted, for ${cookingViewModel?.cavityName?.value}, dialog initiated from parentCavity ${parentCookingViewModel?.cavityName?.value}")
                    }

                    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                        HMILogHelper.Logd("probeAlreadyPresentBeforeSabbathMenu, Sabbath probe detected, removing probe for ${cookingViewModel?.cavityName?.value}, dialog initiated from parentCavity ${parentCookingViewModel?.cavityName?.value}")
                        onMeatProbeRemoved()
                        MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                            popupBuilder, fragment
                        )
                    }
                })

            popupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    popupBuilder.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled = false
                }

                override fun onDialogDestroy() {
                }
            })
            if (!popupBuilder.isVisible) {
                popupBuilder.show(
                    fragment.parentFragmentManager, "probeAlreadyPresentBeforeSabbathMenu"
                )
            }
        }

        /**
         * Brief : for Handling probe instruction when probe is inserted during Sabbath recipe programming
         */
        fun probeDetectedBeforeSabbathProgramming(
            fragment: Fragment,
            parentCookingViewModel: CookingViewModel?,
            onLeftButtonClick: () -> Unit,
            onMeatProbeRemoved: () -> Unit,
        ) {
            fragment.activity?.supportFragmentManager?.let { CookingAppUtils.dismissAllDialogs(it) }
            val headerTitle = R.string.text_header_probe_detected
            val popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(headerTitle)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setDescriptionMessage(R.string.text_description_probe_detected2)
                .setIsPopupCenterAligned(true).setLeftButton(R.string.text_button_no) {
                    onLeftButtonClick()
                    HMILogHelper.Logd(fragment.tag, "Sabbath Probe detected, dialog pressing NO")
                    false
                }.setRightButton(R.string.text_button_yes) {
                    //Setting the head to the top of the probe tree
                    parentCookingViewModel?.recipeExecutionViewModel?.cancel()
                    CookingViewModelFactory.setInScopeViewModel(parentCookingViewModel)
                    HMILogHelper.Logd(fragment.tag, "Sabbath Probe detected, dialog pressing YES")
                    navigateSafely(
                        fragment, R.id.action_to_probeCyclesSelectionFragment, null, null
                    )
                    MeatProbeUtils.removeMeatProbeListener()
                    false
                }.build()

            popupBuilder.setTimeoutCallback(object :
                ScrollDialogPopupBuilder.OnTimeoutObserverListener {
                override fun onTimeout(timeoutStatesEnum: TimeoutStatesEnum?) {
                    if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                        HMILogHelper.Logd("Sabbath Programming Probe Detected Timeout ${parentCookingViewModel?.cavityName?.value}")
                        onLeftButtonClick()
                        MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                            popupBuilder, fragment
                        )
                        CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                    }
                }
            }, fragment.resources.getInteger(R.integer.session_long_timeout))

            //Knob Implementation
            val hmiKnobListener = observeHmiKnobListener(onHMIRightKnobClick = {
                popupBuilder.onHMIRightKnobClick()
            }, onKnobSelectionTimeout = {})
            val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                (object : MeatProbeUtils.MeatProbeListener {
                    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                        HMILogHelper.Logd("Sabbath probe Inserted, for ${cookingViewModel?.cavityName?.value}, dialog initiated from parentCavity ${parentCookingViewModel?.cavityName?.value}")
                    }

                    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                        HMILogHelper.Logd("Sabbath probe detected, removing probe for ${cookingViewModel?.cavityName?.value}, dialog initiated from parentCavity ${parentCookingViewModel?.cavityName?.value}")
                        onMeatProbeRemoved()
                        MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                            popupBuilder, fragment
                        )
                    }
                })

            popupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                   popupBuilder.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled = false
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })
            if (!popupBuilder.isVisible) {
                popupBuilder.show(
                    fragment.parentFragmentManager, "probeDetectedBeforeSabbathProgramming"
                )
            }
        }

        /**
         * method to return to Sabbath selection fragment ex if Meat Probe is inserted and pressing NO or Timeout will move to
         * Sabbath Settings selection Fragment
         */
        fun navigateToSabbathSettingSelection(fragment: Fragment) {
            if (!NavigationViewModel.popBackStack(
                    Navigation.findNavController(
                        NavigationUtils.getViewSafely(
                            fragment
                        ) ?: fragment.requireView()
                    ), R.id.settingsLandingFragment, false
                )
            ) {
                HMILogHelper.Loge(
                    fragment.tag,
                    "settingsLandingFragment is not in the stack, using global action to navigate settingsLandingFragment"
                )
                navigateSafely(fragment, R.id.settingsLandingFragment, null, null)
            }
        }
    }
}