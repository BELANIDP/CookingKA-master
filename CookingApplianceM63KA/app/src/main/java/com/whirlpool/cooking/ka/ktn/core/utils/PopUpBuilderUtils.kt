package core.utils

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.basefragments.AbstractGridListFragment
import android.presenter.basefragments.AbstractStringTumblerFragment
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.dialogs.MoreOptionsPopupBuilder
import android.presenter.fragments.kitchentimer.KitchenTumblerListTimerFragment
import android.presenter.fragments.self_clean.SelfCleanInstructionsFragment
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.common.utils.TimeoutViewModel.TimeoutStatesEnum
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.utils.Constants
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory.ProductVariantEnum
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel
import com.whirlpool.hmi.ota.ui.OtaUiManager
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.timers.Timer
import core.utils.AppConstants.COMMON_POPUP_DESCRIPTION_WIDTH
import core.utils.AppConstants.CONSTANT_SIXTY
import core.utils.AppConstants.CONTROL_UNLOCK_FROM_POPUP
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AppConstants.HEADER_VIEW_CENTER_ICON_GONE
import core.utils.AppConstants.HOT_CAVITY_WARNING_OFFSET_CELCIUS
import core.utils.AppConstants.HOT_CAVITY_WARNING_OFFSET_FAHRENHEIT
import core.utils.AppConstants.LEFT_KNOB_ID
import core.utils.AppConstants.POPUP_DESCIPTION_TOP_MARGIN_70PX
import core.utils.AppConstants.POPUP_DESCRIPTION_HORIZONTAL_MARGIN_32PX
import core.utils.AppConstants.POPUP_DESCRIPTION_TOP_MARGIN_105PX
import core.utils.AppConstants.POPUP_DESCRIPTION_TOP_MARGIN_8PX
import core.utils.AppConstants.POPUP_FORGET_NETWORK_DESCRIPTION_TITLE_TOP_MARGIN
import core.utils.AppConstants.POPUP_OTA_DESCRIPTION_TOP_SMALL_MARGIN
import core.utils.AppConstants.POPUP_OTA_TITLE_TOP_SMALL_MARGIN
import core.utils.AppConstants.POPUP_TITLE_TOP_MARGIN
import core.utils.AppConstants.POP_UP_DISMISS
import core.utils.AppConstants.RIGHT_KNOB_ID
import core.utils.AppConstants.TIME_OUT_DEFAULT
import core.utils.CookingAppUtils.Companion.handleErrorAndStartCooking
import core.utils.CookingAppUtils.Companion.isRecipeOptionAvailable
import core.utils.CookingAppUtils.Companion.navigateToStatusOrClockScreen
import core.utils.CookingAppUtils.Companion.setLeftAndRightButtonBackgroundNull
import core.utils.CookingAppUtils.Companion.updatePopUpLeftTextButtonBackground
import core.utils.CookingAppUtils.Companion.updatePopUpRightTextButtonBackground
import core.utils.DoorEventUtils.Companion.lowerDoorDialogPopupBuilder
import core.utils.DoorEventUtils.Companion.upperDoorDialogPopupBuilder
import core.utils.HMIExpansionUtils.Companion.setBothKnobLightOff
import core.utils.HMIExpansionUtils.Companion.userInteractWithinFastBlinkingTimeoutElapsed
import core.utils.HMIExpansionUtils.HMICancelButtonInteractionListener
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NavigationUtils.Companion.getVisibleFragment
import core.utils.NavigationUtils.Companion.navigateSafely
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Objects


class PopUpBuilderUtils {
    companion object {
        private var popupBuilder: ScrollDialogPopupBuilder? = null
        private var prepareOvenPopupBuilder: ScrollDialogPopupBuilder? = null
        private var hotCavityPopupBuilder: ScrollDialogPopupBuilder? = null
        private var hotCavityDoorClosePopup: ScrollDialogPopupBuilder? = null
        private var featureUnavailablePopupBuilder: ScrollDialogPopupBuilder? = null
        private var steamCyclePopupBuilder: ScrollDialogPopupBuilder? = null
        private var popupBuilderMwo: ScrollDialogPopupBuilder? = null
        private var jetStartPopupBuilder: ScrollDialogPopupBuilder? = null

        /**
         * JET start microwave 30 SEC,  it also shows open/close door popup based on door interaction specification
         *
         * @param fragment parent fragment to re-register knob listener in case knob is released before starting cycle
         */
        fun jetStartMWOBakeRecipe(
            fragment: Fragment
        ) {
            val cookingViewModel = CookingAppUtils.getCookingViewModelForJetStartRecipe()
            if(cookingViewModel.recipeExecutionViewModel.isRunning){
                HMILogHelper.Loge(fragment.tag,"JET start ${cookingViewModel.cavityName.value} is RUNNING so not execution jetStartMWOBakeRecipe")
                return
            }
            cookingViewModel.recipeExecutionViewModel.cancel()
            HMILogHelper.Logd("cancelled recipe ${cookingViewModel.cavityName.value} from jetStartMWOBakeRecipe()")
            //only show open/close popup and do not start cycle unless LongPress event detected
            if (DoorEventUtils.startJetRecipeOrShowPopup(
                    fragment,
                    cookingViewModel,
                    false
                )
            ) {
                dismissDialogs()
                dismissPopupByTag(fragment.activity?.supportFragmentManager, AppConstants.POPUP_TAG_JET_START)
                jetStartPopupBuilder =
                    ScrollDialogPopupBuilder.Builder(R.layout.popup_jetstart)
                        .setHeaderTitle(
                            fragment.resources.getString(
                                R.string.text_dynamic_popup_content,
                                provideJetStartTitle(cookingViewModel, fragment)
                            )
                        )
                        .setNotificationText(R.string.text_sub_description_Jet_start)
                        .setIsLeftButtonEnable(false).setIsRightButtonEnable(false)
                        .setIsPopupCenterAligned(true).setIsPopupCenterAligned(true)
                        .setIsProgressVisible(true).setProgressPercentage(15).build()

                val hmiKnobListener: HMIKnobInteractionListener =
                    (object : HMIKnobInteractionListener {
                        override fun onHMILeftKnobClick() {
                            //Do Nothing
                        }

                        override fun onHMILongLeftKnobPress() {
                            //Do Nothing
                        }

                        override fun onHMIRightKnobClick() {
                            HMILogHelper.Logd(
                                "JET start",
                                "onHMIRightKnobClick released dismissing JET start"
                            )
                            jetStartPopupBuilder?.updateAbortedProgressBar{
                                jetStartPopupBuilder?.dismiss()
                                HMILogHelper.Logd(fragment.tag, "JET start is aborted,navigating to ${cookingViewModel.cavityName.value} recipeSelection")
                                if(fragment !is AbstractStringTumblerFragment && fragment !is AbstractGridListFragment) {
                                    if (cookingViewModel.isPrimaryCavity) {
                                        NavigationUtils.navigateToUpperRecipeSelection(fragment)
                                    } else {
                                        NavigationUtils.navigateToLowerRecipeSelection(fragment)
                                    }
                                }
                            }
                        }

                        override fun onKnobSelectionTimeout(knobId: Int) {

                        }

                        override fun onHMILongRightKnobPress() {
                            HMILogHelper.Logd(
                                "JET start", "onHMILongRightKnobPress pressed start cycle"
                            )
                            jetStartPopupBuilder?.updateCompletedProgressBar(fragment) {
                                HMILogHelper.Logd(
                                    "JET start", "onHMILongRightKnobPress completed updateCompletedProgressBar"
                                )
                                //start microwave/oven cycle as it has satisfied hold interval
                                DoorEventUtils.startJetRecipeOrShowPopup(
                                    fragment, cookingViewModel, true
                                )
                            }
                        }

                        override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
                            if (timeInterval != 0) {
                                HMILogHelper.Logd(
                                    "knob",
                                    "onHMIRightKnobTickHoldEvent: $timeInterval"
                                )
                                //tick Interval will come from 2,3...28 and update progress based on that to match 100% progress
                                jetStartPopupBuilder?.updateProgressBar(timeInterval)
                            }
                        }

                        override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
                            //Do nothing
                        }

                        override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
                            //Do Nothing
                        }

                    })

                jetStartPopupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        //register for knob events once dialog is visible
                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    }

                    override fun onDialogDestroy() {
                        //remove knob events after dialog gets dismissed
                        HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                        CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    }
                })
                if(hotCavityDoorClosePopup == null) {
                    jetStartPopupBuilder?.show(
                        fragment.parentFragmentManager,
                        AppConstants.POPUP_TAG_JET_START
                    )
                }
            }
        }

        /**
         * dismiss JET start dialog if stuck in showing
         * this is intermittent and if press and hold multiple times very frequently then action has
         * taken but due to animation if gets stuck then use this method onDestroyView to get it dismissed based on TAG
         *
         * @param manager
         */
        fun dismissPopupByTag(manager: FragmentManager?, popupTag: String){
            val fragments = manager?.fragments
            if (fragments != null) {
                for (fragment in fragments) {
                    if (fragment is DialogFragment) {
                        if(fragment.tag.contentEquals(popupTag)) {
                            HMILogHelper.Logd(
                                fragment.tag, "Dismissing $popupTag as it is still showing"
                            )
                            fragment.dismissAllowingStateLoss()
                        }
                    }
                    if (!fragment.isAdded) return
                    val childFragmentManager = fragment.childFragmentManager
                    dismissPopupByTag(childFragmentManager, popupTag)
                }
            }
            if(popupTag.contentEquals(AppConstants.POPUP_TAG_JET_START)) jetStartPopupBuilder?.dismiss()
        }

        private fun provideJetStartTitle(
            cookingViewModel: CookingViewModel,
            fragment: Fragment
        ): String {
            return if (SharedPreferenceManager.getKnobAssignFavoritesCycleStatusIntoPreference()
                    .toBoolean()
            ) {
                fragment.resources.getString(
                    R.string.text_description_Jet_start_favorites_cycle,
                    SharedPreferenceManager.getKnobAssignFavoritesCycleNameIntoPreference()
                )
            } else {
                if (cookingViewModel.isOfTypeMicrowaveOven) {
                    fragment.resources.getString(R.string.text_jetStart_starting_mwo)
                } else {
                    fragment.resources.getString(R.string.text_jetStart_starting_bake)
                }
            }
        }


        fun prepareOvenPopup(fragment: Fragment) {
            dismissDialogs()
            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    //T0DO:  Play Invalid Click Audio
                },
                onKnobSelectionTimeout = {}
            )
            //if meat probe connected for the same cavity then do not proceed with self clean start flow
            if (MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getInScopeViewModel())) {
                probeDetectedFromSameCavityPopupBuilder(
                    fragment,
                    CookingViewModelFactory.getInScopeViewModel()
                )
                return
            }
            //prevent self clean flow is any kitchen timer is running
            if (KitchenTimerVMFactory.isAnyKitchenTimerRunning()) {
                //Handling the self clean flow cancel by popup pressing no button.
                CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = false)
                allKitchenTimerCancelPopup(
                    fragment,
                    onCancellingAllKitchenTimers = {
                        prepareOvenPopup(fragment)
                        //Handling the self clean flow start by pressing continue buttn.
                        CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = true)
                    },
                    originalKnobListener = hmiKnobListener
                )
                return
            }

            val handler = Handler(Looper.getMainLooper())
            prepareOvenPopupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                    .setHeaderTitle(R.string.text_header_prepare_oven)
                    .setDescriptionMessage(R.string.text_description_prepare_oven_self_clean)
                    .setIsLeftButtonEnable(false)
                    .setIsRightButtonEnable(false)
                    .setIsPopupCenterAligned(true)
                    .setCancellableOutSideTouch(false)
                    .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_104PX)
                    .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                    .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setDescriptionTextFont(
                        ResourcesCompat.getFont(
                            ContextProvider.getContext(),
                            R.font.roboto_light
                        )
                    )
                    .build()


            prepareOvenPopupBuilder?.setTimeoutCallback({
                CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.cancel()
                HMILogHelper.Logd("canceled recipe ${CookingViewModelFactory.getInScopeViewModel().cavityName.value} on prepareOvenPopup self clean TIMEOUT")
                navigateSafely(fragment, R.id.settingsLandingFragment, null, null)
                handler.postDelayed(
                    { prepareOvenPopupBuilder?.dismiss() },
                    AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }, fragment.resources.getInteger(R.integer.self_clean_popup_timeout))

            val hmiCleanButtonInteractionListener: HMIExpansionUtils.HMICleanButtonInteractionListener =
                (HMIExpansionUtils.HMICleanButtonInteractionListener {
                    doorOpenClosePopup(fragment)
                    handler.postDelayed({
                        prepareOvenPopupBuilder?.dismiss()
                    }, fragment.resources.getInteger(R.integer.ms_10).toLong())
                })

            prepareOvenPopupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMICleanButtonInteractionListener(
                        hmiCleanButtonInteractionListener
                    )
                    HMIExpansionUtils.startOrStopCleanButtonLightBlinkAnimation(true)
                    HMIExpansionUtils.makeCleanButtonValid()
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICleanButtonInteractionListener(
                        hmiCleanButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    HMIExpansionUtils.startOrStopCleanButtonLightBlinkAnimation(false)
                    HMIExpansionUtils.makeCleanButtonInValid()
                    HMIExpansionUtils.setLightForCleanButton(false)
                    handler.removeCallbacksAndMessages(null)
                    if (prepareOvenPopupBuilder != null) {
                        prepareOvenPopupBuilder = null
                    }
                }
            })
            if(hotCavityDoorClosePopup == null) {
                prepareOvenPopupBuilder?.show(
                    fragment.parentFragmentManager,
                    SelfCleanInstructionsFragment::class.java.simpleName
                )
            }
        }

        fun doorOpenClosePopup(fragment: Fragment) {
            var fragmentInstance = fragment
            if (fragment == null){
                fragmentInstance = getVisibleFragment()!!
            }
            dismissDialogs()
            val cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
            val handler = Handler(Looper.getMainLooper())
            val dialogPopupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_icon_cavity)
                    .setHeaderTitle(R.string.text_header_prepare_oven)
                    .setDescriptionMessage(R.string.text_description_prepare_oven_self_clean_Door_OC)
                    .setIsLeftButtonEnable(false).setIsRightButtonEnable(false)
                    .setIsPopupCenterAligned(true)
                    .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_90PX)
                    .setTopMarginForDescriptionText(AppConstants.POPUP_DESCRIPTION_TOP_MARGIN_6PX)
                    .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setDescriptionTextFont(
                        ResourcesCompat.getFont(
                            fragmentInstance.requireContext(),
                            R.font.roboto_light
                        )
                    )
                    .setCancellableOutSideTouch(false)
                    .build()
            val hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener =
                (HMICancelButtonInteractionListener {
                    dialogPopupBuilder.dismiss()
                    CookingAppUtils.cancelProgrammedCyclesAndNavigate(
                        fragmentInstance,
                        navigateToSabbathClock = false,
                        navigateToClockScreen = true
                    )
                })

            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    //T0DO:  Play Invalid Click Audio
                },
                onKnobSelectionTimeout = {}
            )

            dialogPopupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    //HMI Key :- Self clean flow clean button invalid tone defect fixes
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    if (!cookingViewModel.recipeExecutionViewModel.isAllSafetyRelevantCheckCompleted.hasActiveObservers()) {
                        cookingViewModel.recipeExecutionViewModel.isAllSafetyRelevantCheckCompleted.observe(
                            dialogPopupBuilder.viewLifecycleOwner
                        ) { isSafetyCheck: Boolean ->
                            HMILogHelper.Logd("isSafetyCheck", "$isSafetyCheck")
                            if (isSafetyCheck) {
                                dialogPopupBuilder.dismiss()
                                pressStartPopUp(
                                    fragmentInstance
                                )
                            }
                        }
                    }
                }

                override fun onDialogDestroy() {
                    handler.removeCallbacksAndMessages(null)
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                }
            })

            dialogPopupBuilder.setTimeoutCallback({
                cookingViewModel.recipeExecutionViewModel.cancel()
                HMILogHelper.Logd("canceled recipe of ${cookingViewModel.cavityName.value} on doorOpenClosePopup timeout")
                navigateSafely(
                    fragmentInstance,
                    R.id.settingsLandingFragment,
                    null,
                    null
                )
                handler.postDelayed(
                    { dialogPopupBuilder.dismiss() }, AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }, fragment.resources.getInteger(R.integer.self_clean_popup_timeout))
            if(hotCavityDoorClosePopup == null) {
                dialogPopupBuilder.show(
                    fragmentInstance.parentFragmentManager,
                    SelfCleanInstructionsFragment::class.java.simpleName
                )
            }
        }

        fun pressStartPopUp(fragment: Fragment) {
            val handler = Handler(Looper.getMainLooper())
            val cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
            //correction check for door open state -> go back
            var knobRotationCount = 0
            val dialogPopupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                    .setHeaderTitle(R.string.text_header_press_start)
                    .setDescriptionMessage(R.string.text_description_Press_Start_self_clean)
                    .setLeftButton(R.string.text_button_delay) {
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        // check for door state ->open -> disable delay button
                        navigateSafely(
                            fragment,
                            R.id.action_selfCleanInstructionsFragment_to_selfCleanDelayTimeTumblerFragment,
                            null,
                            null
                        )
                        true
                    }.setRightButton(R.string.text_button_start) {
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.start_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        CookingAppUtils.prepareOvenAndStartSelfClean(fragment, false)
                        true
                    }.setIsLeftButtonEnable(true).setIsRightButtonEnable(true)
                    .setCancellableOutSideTouch(false)
                    .setIsPopupCenterAligned(true).build()

            val hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener =
                (HMICancelButtonInteractionListener {
                    dialogPopupBuilder.dismiss()
                    CookingAppUtils.cancelProgrammedCyclesAndNavigate(
                        fragment,
                        navigateToSabbathClock = false,
                        navigateToClockScreen = true
                    )
                })
            //Added Knob implementation to start self clean cycle
            //Knob Implementation
            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            navigateSafely(
                                fragment,
                                R.id.action_selfCleanInstructionsFragment_to_selfCleanDelayTimeTumblerFragment,
                                null,
                                null
                            )
                            dialogPopupBuilder.dismiss()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            CookingAppUtils.prepareOvenAndStartSelfClean(fragment, false)
                            dialogPopupBuilder.dismiss()
                        }

                        else -> {
                            CookingAppUtils.prepareOvenAndStartSelfClean(fragment, false)
                            dialogPopupBuilder.dismiss()
                        }
                    }
                },
                onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    dialogPopupBuilder.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                },
                onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--

                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    dialogPopupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    dialogPopupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    dialogPopupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    dialogPopupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }
            )

            dialogPopupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    //HMI Key :- Self clean flow clean button invalid tone defect fixes
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    val inScopeViewModel = CookingViewModelFactory.getInScopeViewModel()
                    if (!inScopeViewModel.doorState.hasActiveObservers()) {
                        inScopeViewModel.doorState.observe(
                            dialogPopupBuilder.viewLifecycleOwner
                        ) { isOpen: Boolean ->
                            if (isOpen) {
                                handler.postDelayed(
                                    { dialogPopupBuilder.dismiss() },
                                    AppConstants.POPUP_DISMISS_DELAY.toLong()
                                )
                                doorOpenClosePopup(fragment)
                            }
                        }
                    }
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    handler.removeCallbacksAndMessages(null)
                }
            })

            dialogPopupBuilder.setTimeoutCallback({
                cookingViewModel.recipeExecutionViewModel.cancel()
                HMILogHelper.Logd("canceled recipe ${cookingViewModel.cavityName.value} on pressStartPopUp timeout")
                navigateSafely(
                    fragment,
                    R.id.settingsLandingFragment,
                    null,
                    null
                )
                handler.postDelayed(
                    { dialogPopupBuilder.dismiss() }, AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }, fragment.resources.getInteger(R.integer.self_clean_popup_timeout))
            if(hotCavityDoorClosePopup == null) {
                dialogPopupBuilder.show(
                    fragment.parentFragmentManager,
                    SelfCleanInstructionsFragment::class.java.simpleName
                )
            }
        }

        fun runningFailPopupBuilder(fragment: Fragment) {
            val dialogPopupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                    .setHeaderTitle(R.string.text_layout_information_recipe_not_available)
                    .setDescriptionMessage(R.string.text_layout_information_system_not_start_recipe)
                    .setCenterButton(R.string.text_button_dismiss) {
                        CookingAppUtils.dismissDialogAndNavigateToStatusOrClockScreen(fragment)
                        true
                    }.build()
            if(hotCavityDoorClosePopup == null) {
                //Running failed popup sometimes not attached with parent fragment so added is added chec over here.
                if(fragment?.isAdded == true) {
                    dialogPopupBuilder.show(fragment.parentFragmentManager, "runningFailPopUp")
                }
            }
        }

        fun showOtherFeatureRunningPopup(fragment: Fragment) {
            val dialogPopupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                    .setHeaderTitle(R.string.tryAgainLater)
                    .setDescriptionMessage(R.string.inactiveFeatureGenericMessage)
                    .setCenterButton(R.string.text_button_dismiss) {
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        CookingAppUtils.dismissDialogAndNavigateToStatusOrClockScreen(fragment)
                        true
                    }.build()
            if(hotCavityDoorClosePopup == null) {
                dialogPopupBuilder.show(fragment.parentFragmentManager, "showOtherFeatureRunningPopup")
            }
        }

        fun doorLockErrorPopupBuilder(fragment: Fragment) {
            var knobSelection = false
            popupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                    .setHeaderTitle(R.string.text_cancelling_cycle_self_clean)
                    .setTitleTextDrawable(true, R.drawable.ic_alert)
                    .setDescriptionMessage(R.string.text_cancelling_cycle_error_self_clean)
                    .setIsLeftButtonEnable(false)
                    .setIsRightButtonEnable(true).setIsPopupCenterAligned(true)
                    .setCancellableOutSideTouch(false)
                    .setRightButton(R.string.text_button_ok) {
                        CookingAppUtils.cancelProgrammedCyclesAndNavigate(
                            fragment,
                            navigateToSabbathClock = false,
                            navigateToClockScreen = true
                        )
                        true
                    }.build()

            //Popup doesn't have timeout, User input is required to move out of this popup.
            popupBuilder?.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)

            //Cancel Key Listener, need to cancel programmed cycle and exit to clock screen
            val hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener =
                (HMICancelButtonInteractionListener {
                    CookingAppUtils.cancelProgrammedCyclesAndNavigate(
                        fragment,
                        navigateToSabbathClock = false,
                        navigateToClockScreen = true
                    )
                })

            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    if (knobSelection) popupBuilder?.provideViewHolderHelper()?.rightTextButton?.callOnClick()
                },
                onKnobRotateEvent = { knobId, _ ->
                    if (knobId == LEFT_KNOB_ID) {
                        knobSelection = true
                        updatePopUpRightTextButtonBackground(
                            fragment,
                            popupBuilder,
                            R.drawable.selector_textview_walnut
                        )
                    }
                },
                onKnobSelectionTimeout = {
                    knobSelection = false
                    popupBuilder?.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            null,
                            this.rightTextButton
                        )
                    }
                }
            )
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(
                        hmiKnobListener
                    )
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(
                        hmiKnobListener
                    )
                }
            })

            if (popupBuilder?.isVisible == false) {
                if(hotCavityDoorClosePopup == null) {
                    popupBuilder?.show(fragment.parentFragmentManager, "doorLockError")
                }
            }
        }

        fun cookTimeNotAvailablePopup(fragment: Fragment, description: String) {
            val dialogPopupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment_warning)
                    .setHeaderTitle(R.string.text_cannot_add_cooktime_title)
                    .setDescriptionMessage(description)
                    .setIsLeftButtonEnable(false)
                    .setIsRightButtonEnable(true)
                    .setRightButton(R.string.text_button_ok) {
                        true
                    }.build()

            dialogPopupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                }

                override fun onDialogDestroy() {
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })
            if (!dialogPopupBuilder.isVisible && hotCavityDoorClosePopup == null) {
                dialogPopupBuilder.show(fragment.parentFragmentManager, "cookTimeAddError")
            }
        }

        fun selfCleanUnavailablePopup(fragment: Fragment) {
            popupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_icon_cavity)
                    .setHeaderTitle(R.string.text_header_appliance_is_busy)
                    .setDescriptionMessage(R.string.text_description_try_again_later)
                    .setIsLeftButtonEnable(false)
                    .setIsPopupCenterAligned(true)
                    .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_104PX)
                    .setRightButton(R.string.text_button_ok){
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        true
                    }
                    .build()
            popupBuilder?.isCancelable = true

            popupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    popupBuilder?.dismiss()
                }
            }, fragment.resources.getInteger(R.integer.modal_popup_timeout))

            val knobInteractionListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    popupBuilder?.dismiss()
                },
                onHMIRightKnobClick = {
                    popupBuilder?.dismiss()
                },
                onKnobSelectionTimeout = {}
            )

            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(knobInteractionListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(knobInteractionListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })
            if(hotCavityDoorClosePopup == null) {
                popupBuilder?.show(
                    fragment.parentFragmentManager,
                    SelfCleanInstructionsFragment::class.java.simpleName
                )
            }
        }

        fun removeProbeToStartSelfClean(fragment: Fragment, onMeatProbeConditionMet: () -> Unit) {
            val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                (object : MeatProbeUtils.MeatProbeListener {
                    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                    }

                    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                        popupBuilder?.dismiss()
                        onMeatProbeConditionMet()

                    }

                })
            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_icon_cavity)
                .setHeaderTitle(R.string.text_header_remove_probe)
                .setDescriptionMessage(R.string.text_description_remove_probe)
                .setIsLeftButtonEnable(false)
                .setIsPopupCenterAligned(true)
                .build()
            popupBuilder?.isCancelable = true
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    //Popup enabled the HMI cancel button
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                }

                override fun onDialogDestroy() {
                    MeatProbeUtils.removeMeatProbeListener()
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })
            if(hotCavityDoorClosePopup == null) {
                popupBuilder?.show(
                    fragment.parentFragmentManager,
                    SelfCleanInstructionsFragment::class.java.simpleName
                )
            }
        }

        /**
         * popup to remove probe to Start a JET start recipe through knob
         * @param fragment to navigate
         * @param runningCookingViewModel cooking model of JET start recipe that is about to start
         * @param onContinueButtonClick if probe is successfully removed
         */
        fun removeProbeToContinueCooking(
            fragment: Fragment,
            runningCookingViewModel: CookingViewModel?,
            recipeName: String,
            onContinueButtonClick: () -> Unit) {
            dismissDialogs()
            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(R.string.text_remove_probe_to_continue)
                .setDescriptionMessage(fragment.getString(R.string.text_description_remove_probe_to_continue, recipeName))
                .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_10PX)
                .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextFont(
                    ResourcesCompat.getFont(
                        fragment.requireContext(),
                        R.font.roboto_light
                    )
                )
                .setIsRightButtonEnable(false)
                .setCancellableOutSideTouch(false)
                .setRightButton(R.string.text_button_continue) {
                    popupBuilder?.dismiss()
                    onContinueButtonClick()
                    false
                }.build()

            //Knob Implementation
            val hmiKnobListener =
                observeHmiKnobListener(
                    onHMIRightKnobClick = {
                        popupBuilder?.onHMIRightKnobClick()
                    }, onKnobSelectionTimeout = {}, onHMILongRightKnobPress = {}, onHMIRightKnobTickHoldEvent = {})
            val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                (object : MeatProbeUtils.MeatProbeListener {
                    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                        HMILogHelper.Logd(fragment.tag, "JET Start Probe Inserted, for ${cookingViewModel?.cavityName?.value}, dialog initiated from parentCavity ${runningCookingViewModel?.cavityName?.value}")
                        if(MeatProbeUtils.isMeatProbeConnected(runningCookingViewModel)) {
                            popupBuilder?.provideViewHolderHelper()?.rightTextButton?.isEnabled =
                                false
                        }
                    }

                    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                        HMILogHelper.Logd(fragment.tag, "JET Start Probe, removing probe for ${cookingViewModel?.cavityName?.value}, dialog initiated from parentCavity ${runningCookingViewModel?.cavityName?.value}")
                        if (!(MeatProbeUtils.isMeatProbeConnected(runningCookingViewModel))) {
                            popupBuilder?.provideViewHolderHelper()?.rightTextButton?.isEnabled =
                                true
                        }
                    }

                })
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    //Popup enabled the HMI cancel button
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    popupBuilder?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                        false
                }

                override fun onDialogDestroy() {
                    MeatProbeUtils.removeMeatProbeListener()
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                }
            })
            //No timeout for probe extended popup
            popupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    HMILogHelper.Logd(fragment.tag, "removeProbeToJETStart JET Start Timeout ${runningCookingViewModel?.cavityName?.value}")
                    runningCookingViewModel?.recipeExecutionViewModel?.cancel()
                    MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                        popupBuilder, fragment
                    )
                    navigateToStatusOrClockScreen(fragment)
                }
            },fragment.resources.getInteger(R.integer.session_long_timeout))

            if (popupBuilder?.isVisible == false && hotCavityDoorClosePopup == null) {
                fragment.parentFragmentManager.let {
                    popupBuilder?.show(
                        it,
                        "probeAlreadyPresentJETStartRecipe"
                    )
                }
            }
        }
        fun featureUnavailablePopup(fragment: Fragment) {
            dismissDialogs()
            if (featureUnavailablePopupBuilder == null) {
                featureUnavailablePopupBuilder =
                    ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_probe_fragment)
                        .setHeaderTitle(R.string.text_header_features_unavailable_self_clean)
                        .setDescriptionMessage(R.string.text_description_features_unavailable_self_clean)
                        .setRightButton(R.string.text_button_ok) {
                            AudioManagerUtils.playOneShotSound(
                                ContextProvider.getContext(),
                                R.raw.button_press,
                                AudioManager.STREAM_SYSTEM,
                                true,
                                0,
                                1
                            )
                            false
                        }.setIsRightButtonEnable(true)
                        .setCancellableOutSideTouch(false)
                        .setHorizontalMarginForDescriptionText(
                            POPUP_DESCRIPTION_HORIZONTAL_MARGIN_32PX
                        )
                        .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                        .setTopMarginForTitleText(POPUP_DESCIPTION_TOP_MARGIN_70PX)
                        .setHorizontalMarginForTitleText(POPUP_DESCRIPTION_HORIZONTAL_MARGIN_32PX)
                        .setTitleTextGravity(Gravity.START)
                        .setDescriptionTextGravity(Gravity.START)
                        .setIsPopupCenterAligned(true).build()

                featureUnavailablePopupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                    if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                        featureUnavailablePopupBuilder?.dismiss()
                        HMILogHelper.Logd(
                            "featureUnavailablePopup",
                            "Dismissing feature unavailable popup in SelfCleanStatusFragment on timeout"
                        )
                    }
                }, fragment.resources.getInteger(R.integer.modal_popup_timeout))

                featureUnavailablePopupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {}

                    override fun onDialogDestroy() {
                        if (featureUnavailablePopupBuilder != null) {
                            featureUnavailablePopupBuilder = null
                        }
                        CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    }
                })

                featureUnavailablePopupBuilder?.show(
                    fragment.parentFragmentManager,
                    SelfCleanInstructionsFragment::class.java.simpleName
                )
            }
        }

        fun moreOptionsPopup(
            fragment: Fragment,
            viewModel: CookingViewModel,
            isAssisted: Boolean = false
        ) {
            val tempMap = viewModel.recipeExecutionViewModel?.targetTemperatureOptions?.value
            val cookTime = viewModel.recipeExecutionViewModel?.cookTime?.value
            val isExtraBrownRecipe =
                viewModel.recipeExecutionViewModel?.userInstruction?.value?.containsExtraBrowningPrompt()
            val isMicrowaveRecipe = isRecipeOptionAvailable(
                viewModel.recipeExecutionViewModel,
                RecipeOptions.MWO_POWER_LEVEL
            )
            val isSensingRecipe =
                ((viewModel.recipeExecutionViewModel.isSensingRecipe) && (!viewModel.recipeExecutionViewModel.isProbeBasedRecipe))
            val cookTimeOptionAvailable = isRecipeOptionAvailable(
                viewModel.recipeExecutionViewModel,
                RecipeOptions.COOK_TIME
            )
            val cycleOptionsDataArrayList: ArrayList<GridListItemModel>
            when (viewModel.recipeExecutionViewModel?.cookTimerState?.value) {
                Timer.State.COMPLETED -> {
                    cycleOptionsDataArrayList = MoreOptionsPopUpOptionHelper.getCycleCompleteOptions(
                        fragment.requireContext(),
                        isExtraBrownRecipe,
                        isMicrowaveRecipe,
                        isAssisted = isAssisted,
                        cookTimeOptionAvailable,
                        isSensingRecipe
                    )
                }

                else -> {
                    if (viewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
                        if (viewModel.recipeExecutionViewModel?.targetMeatProbeTemperatureReached?.value == true) {
                            cycleOptionsDataArrayList =
                                MoreOptionsPopUpOptionHelper.getCycleCompleteOptionsForProbe(
                                    fragment.requireContext(),
                                    cookTime,
                                    tempMap,
                                    isExtraBrownRecipe,
                                    isMicrowaveRecipe,
                                    viewModel,
                                    cookTimeOptionAvailable,
                                    isSensingRecipe
                                )
                        } else {
                            cycleOptionsDataArrayList =
                                MoreOptionsPopUpOptionHelper.getCycleInProgressOptionsForProbe(
                                    fragment.requireContext(),
                                    cookTime,
                                    tempMap,
                                    isMicrowaveRecipe,
                                    viewModel
                                )
                        }

                    } else {
                        cycleOptionsDataArrayList = MoreOptionsPopUpOptionHelper.getCycleInProgressOptions(
                            fragment.requireContext(),
                            cookTime,
                            tempMap,
                            isMicrowaveRecipe,
                            viewModel,
                            isAssisted = isAssisted,
                            cookTimeOptionAvailable,
                            isSensingRecipe
                        )
                    }
                }
            }

            val isAlreadyFavoriteSaved = CookBookViewModel.getInstance().allFavoriteRecords.value?.find {
                it.name == viewModel.recipeExecutionViewModel?.recipeName?.value
            } != null
//            Default user options such as favorite and view instructions etc...
            val defaultOptionsDataArrayList: ArrayList<GridListItemModel> =
                MoreOptionsPopUpOptionHelper.getDefaultUserOptions(fragment.requireContext(),isAlreadyFavoriteSaved)

            moreOptionsGenericPopup(
                fragment,
                viewModel,
                cycleOptionsDataArrayList,
                defaultOptionsDataArrayList
            )
        }

        /**This is a generic popup function which will be populated according to the main popup builder function**/
        private fun moreOptionsGenericPopup(
            fragment: Fragment,
            viewModel: CookingViewModel?,
            cycleOptionsDataArrayList: ArrayList<GridListItemModel>?,
            defaultOptionsDataArrayList: ArrayList<GridListItemModel>?
        ) {
            val productVariant = CookingViewModelFactory.getProductVariantEnum()
            val showCavityIcon = (productVariant == ProductVariantEnum.COMBO || productVariant == ProductVariantEnum.DOUBLEOVEN)
            val moreOptionsPopupBuilder =
                MoreOptionsPopupBuilder.Builder(viewModel!!)
                    .setHeaderTitle(R.string.text_header_more_options)
                    .setLeftIcon(R.drawable.ic_back_arrow){
                        true
                    }
                    .setRightIcon(
                        if(showCavityIcon) if (viewModel.isPrimaryCavity) R.drawable.ic_oven_cavity else R.drawable.ic_lower_cavity
                        else -1
                    ) {
                        ToastUtils.showToast(fragment.requireContext(), "Oven Icon")
                        true
                    }
                    .setOvenIconVisibility(isOvenIconVisible = if(showCavityIcon) View.VISIBLE else View.GONE )
                    .setBackgroundLayoutListener {
                        true
                    }
                    .setInScopeViewModel(viewModel)
                    .setParentFragment(fragment)
                    .setCycleOptionsRecyclerItemData(cycleOptionsDataArrayList) {
                        true
                    }
                    .setDefaultOptionsRecyclerItemData(defaultOptionsDataArrayList){
                        true
                    }
                    .build()

            moreOptionsPopupBuilder.setOnDialogCreatedListener(object :
                MoreOptionsPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    //Popup enabled the HMI cancel button
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                }

                override fun onDialogDestroy() {
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                }
            })
            if(hotCavityDoorClosePopup == null) {
                moreOptionsPopupBuilder.show(fragment.parentFragmentManager, "test")
            }
        }

        /**
         * MWO - Door open closed popup implementation
         */
        @SuppressLint("StringFormatInvalid")
        @Synchronized
        fun mwoDoorOpenPopup(fragment: Fragment, cookingViewModel: CookingViewModel,onDoorCloseEventAction: () -> Unit = {}) {
            if (cookingViewModel.recipeExecutionViewModel.cookTimerState.value == Timer.State.COMPLETED) {
                HMILogHelper.Logd(
                    "doorOpen",
                    "microwave cook time completed or running, not showing resume popup"
                )
                return
            }
            val remainingCookTimeValue =
                cookingViewModel.recipeExecutionViewModel.remainingCookTime.value

            val getCookTimeRemain = remainingCookTimeValue?.let {
                CookingAppUtils.spannableMwoTimeRemainingText(
                    fragment.requireContext(),
                    it
                )
            }
            fragment.lifecycleScope.launch(Dispatchers.Main) {
                dismissMwoPopup()
                withContext(Dispatchers.Main) {
                    // Build the popup dialog
                    if (popupBuilderMwo == null) {
                        HMILogHelper.Logd("MWO - door open popup showing")
                        popupBuilderMwo =
                            ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                                .setDescriptionMessage(
                                    if (remainingCookTimeValue != null && remainingCookTimeValue < CONSTANT_SIXTY) {
                                        fragment.getString(
                                            R.string.text_description_MWO_cook_door_open_sec,
                                            getCookTimeRemain.toString()
                                        )
                                    } else {
                                        fragment.getString(
                                            R.string.text_description_MWO_cook_door_open_Min_Sec,
                                            getCookTimeRemain.toString()
                                        )
                                    }
                                )
                                .setHeaderTitle(R.string.text_header_paused)
                                .setLeftButton(R.string.text_button_cancel) {
                                    cookingViewModel.recipeExecutionViewModel.cancel()
                                    HMILogHelper.Logd(
                                        "doorOpen",
                                        "Microwave cancel button pressed, cancelling recipeExecutionViewModel"
                                    )
                                    false
                                }
                                .setRightButton(R.string.text_button_MWO_cook_door_open_resume) {
                                    if (SettingsViewModel.getSettingsViewModel().controlLock.value == true) {
                                        val bundle = Bundle()
                                        bundle.putBoolean(CONTROL_UNLOCK_FROM_POPUP, true)
                                        navigateSafely(
                                            fragment,
                                            R.id.action_to_controlUnlockFragment,
                                            bundle,
                                            null
                                        )
                                    } else {
                                        cookingViewModel.recipeExecutionViewModel.resume()
                                    }
                                    false
                                }
                                .build()

                        popupBuilderMwo?.setTimeoutCallback({ timeoutStatesEnum ->
                            if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                                dismissMwoPopup()
                                cookingViewModel.recipeExecutionViewModel.cancel()
                                HMILogHelper.Logd(
                                    "doorOpen",
                                    "microwave timeOut exceeded, cancelling recipeExecutionViewModel"
                                )
                            }
                        }, fragment.resources.getInteger(R.integer.session_long_timeout))

                        //Knob Implementation
                        var knobRotationCount = 0
                        val hmiKnobListener = observeHmiKnobListener(
                            onKnobRotateEvent = { knobId, knobDirection ->
                                if (knobId == AppConstants.RIGHT_KNOB_ID) {
                                    if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                                    else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                                    when (knobRotationCount) {
                                        AppConstants.KNOB_COUNTER_ONE -> {
                                            popupBuilderMwo?.provideViewHolderHelper()?.rightTextButton?.background =
                                                null
                                            updatePopUpLeftTextButtonBackground(
                                                fragment,
                                                popupBuilderMwo,
                                                R.drawable.selector_textview_walnut
                                            )
                                        }

                                        AppConstants.KNOB_COUNTER_TWO -> {
                                            popupBuilderMwo?.provideViewHolderHelper()?.leftTextButton?.background =
                                                null
                                            updatePopUpRightTextButtonBackground(
                                                fragment,
                                                popupBuilderMwo,
                                                R.drawable.selector_textview_walnut
                                            )
                                        }
                                    }
                                }
                            },
                            onHMIRightKnobClick = {
                                when (knobRotationCount) {
                                    AppConstants.KNOB_COUNTER_ONE -> {
                                        popupBuilderMwo?.onHMILeftKnobClick()
                                    }

                                    AppConstants.KNOB_COUNTER_TWO -> {
                                        popupBuilderMwo?.onHMIRightKnobClick()
                                    }
                                }
                            },
                            onKnobSelectionTimeout = {
                                knobRotationCount = 0
                                popupBuilderMwo?.provideViewHolderHelper()?.apply {
                                    setLeftAndRightButtonBackgroundNull(
                                        this.leftTextButton,
                                        this.rightTextButton
                                    )
                                }
                            }
                        )

                        // Optional Listener to listen when the dialog is created and if the dialog need to observer any events
                        popupBuilderMwo?.setOnDialogCreatedListener(object :
                            ScrollDialogPopupBuilder.OnDialogCreatedListener {
                            override fun onDialogCreated() {
                                // Observe any door events if needed and change the popup buttons visibility accordingly
                                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                                getVisibleFragment()?.viewLifecycleOwner.let { lifecycleOwner ->
                                    if (lifecycleOwner != null) {
                                        cookingViewModel.doorState.observe(
                                            lifecycleOwner
                                        ) { isOpen: Boolean ->
                                            popupBuilderMwo?.provideViewHolderHelper()?.rightTextButton?.let {
                                                it.isEnabled = isOpen.not()
                                                it.isClickable = isOpen.not()
                                            }
                                        }
                                    }
                                }
                                popupBuilderMwo?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                                    false
                            }

                            override fun onDialogDestroy() {
                                if (Objects.nonNull(popupBuilderMwo)) {
                                    try {
                                        popupBuilderMwo?.view?.let {
                                            getVisibleFragment()?.viewLifecycleOwner.let {
                                                if (it != null) {
                                                    cookingViewModel.doorState.removeObservers(
                                                        it
                                                    )
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                                CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                                onDoorCloseEventAction()
                            }
                        })
                        if(hotCavityDoorClosePopup == null) {
                            popupBuilderMwo?.show(
                                fragment.parentFragmentManager,
                                AppConstants.DOOR_OPEN_POPUP
                            )
                        }

                    }
                }
            }
        }

        private fun dismissMwoPopup() {
            if (Objects.nonNull(popupBuilderMwo)) {
                HMILogHelper.Logd("MWO - dismiss MWO door open popup ")
                popupBuilderMwo?.dismiss()
                popupBuilderMwo = null
            }
        }

        /**
         * to check if popup is showing uses to restart the timeout in status screen
         * @return true if visible false otherwise
         */
        fun isPopupShowing(): Boolean {
            popupBuilder?.let {
                return it.isVisible
            }

            return false
        }

        /**
         * to check if steam popup is showing uses to restart the timeout in status screen
         * @return true if visible false otherwise
         */
        fun isSteamCleanPopupShowing(): Boolean {
            steamCyclePopupBuilder?.let {
                return it.isVisible
            }
            upperDoorDialogPopupBuilder?.let {
                return it.isVisible
            }
            lowerDoorDialogPopupBuilder?.let {
                return it.isVisible
            }
            return false
        }

        /**
         * Method responsible for dismiss the door open close dialog popup
         */
        private fun dismissDialogs() {
            try {
                if (Objects.nonNull(popupBuilder)) {
                    HMILogHelper.Logd("dismissing popupBuilder")
                    popupBuilder?.dismiss()
                    popupBuilder = null
                }
                if (Objects.nonNull(prepareOvenPopupBuilder)) {
                    prepareOvenPopupBuilder?.dismiss()
                    prepareOvenPopupBuilder = null
                }
            } catch (e: Exception) {
                HMILogHelper.Logd("dismissDialogs error =$e")
            }
        }

        private fun dismissSteamCompletedPopup() {
            if (Objects.nonNull(steamCyclePopupBuilder)) {
                steamCyclePopupBuilder?.dismiss()
                steamCyclePopupBuilder = null
            }
        }


        /**
         * Brief: PopUp to Show the Fault CategoryB details and Instruction
         */
        fun faultCategoryBPopupBuilder(
            fragment: Fragment,
            faultName: String,
            faultCode: String,
            faultCategory: Int,
            cavityName: String?,
        ) {
            dismissDialogs()
            val faultCodeTitle = if (faultCode.length > 4) faultCode.substring(0, 4) else faultCode

            val sharedViewModel =
                ContextProvider.getFragmentActivity()?.let { ViewModelProvider(it) }
                    ?.get(SharedViewModel::class.java)
            //layout_popup_fault_error_fragment - Use for handling multiline title textview
            val dialogPopupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fault_error_fragment)
                    .setDescriptionMessage(R.string.text_fault_description_b)
                    .setHeaderTitle(
                        R.string.text_counter_value_string,
                        "$faultName${AppConstants.EMPTY_SPACE}$faultCodeTitle"
                    ).setCancellableOutSideTouch(false)
                    .setLeftButton(R.string.text_button_dismiss) {
                        if (CookingAppUtils.isPyroliticClean()) {
                            //Need to dismiss all popup before navigate to the clock screen
                            dismissDialogs()
                            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.cancel()
                            HMILogHelper.Logd("canceled recipe ${CookingViewModelFactory.getInScopeViewModel().cavityName.value} faultCategoryBPopupBuilder dismiss button click")
                            navigateToStatusOrClockScreen(fragment)
                        } else {
                            if (!CookingAppUtils.isSabbathFlow()) {
//                                CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.cancel()
                                if (cavityName.contentEquals(Constants.PRIMARY_CAVITY_KEY)) {
                                    CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.cancel()
                                } else if (cavityName.contentEquals(Constants.SECONDARY_CAVITY_KEY)) {
                                    CookingAppUtils.getSecondaryCookingViewModel()?.recipeExecutionViewModel?.cancel()
                                }
                                navigateToStatusOrClockScreen(
                                    fragment
                                )
                            }
                        }
                        CookingAppUtils.manageHMIPanelLights(
                            homeLight = false,
                            cancelLight = false,
                            false
                        )
                        userInteractWithinFastBlinkingTimeoutElapsed()
                        false
                    }
                    .setRightButton(
                        R.string.text_button_contact_service
                    ) {
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        val bundle = Bundle()
                        bundle.putString(BundleKeys.BUNDLE_FAULT_CODE, faultCode)
                        bundle.putInt(BundleKeys.BUNDLE_FAULT_CATEGORY, faultCategory)
                        navigateSafely(
                            fragment,
                            R.id.global_action_go_to_error_screen,
                            bundle,
                            null
                        )
                        setBothKnobLightOff()
                        false
                    }.build()

            dialogPopupBuilder.show(fragment.parentFragmentManager, "faultCategoryB")
            dialogPopupBuilder.setTimeoutCallback({
                // do nothing
            }, fragment.resources.getInteger(R.integer.to_do_popup_timeout))

            val hmiCancelButtonInteractionListener = HMICancelButtonInteractionListener {
                if (CookingAppUtils.isSelfCleanFlow() && java.lang.Boolean.TRUE == CookingViewModelFactory.getInScopeViewModel()
                        .doorLockState.value
                ) {
                    navigateSafely(fragment, R.id.action_goToSelfCleanStatus, null, null)
                } else {
                    CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                        .cancel()
                    navigateToStatusOrClockScreen(fragment)
                }
                dialogPopupBuilder.dismiss()
                CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = false, false)
            }

            dialogPopupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    //Handling Fault B/B2 popup coming during running/complete/programming screen.
                    //Disable/enable HMI buttons for fault popup.
                    Handler(Looper.getMainLooper()).postDelayed({
                        if(dialogPopupBuilder.isAdded){
                            HMILogHelper.Logd("HMI_KEY","Fault B/B2 Popup Showing")
                            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_FAULT_B2)
                            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_FAULT_B2)
                        }
                    },AppConstants.DELAY_CONFIGURATION_1000)

                    if (CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.DOUBLEOVEN
                        || CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.COMBO
                    )
                        sharedViewModel?.setFaultPopUpOpen(true)
                    CookingAppUtils.manageHMIPanelLights(
                        homeLight = false,
                        cancelLight = true,
                        false
                    )
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                }

                override fun onDialogDestroy() {
                    CookingAppUtils.setErrorPresentOnHMIScreen(false)
                    CookingAppUtils.startGattServer(getVisibleFragment())
                    if (cavityName == Constants.PRIMARY_CAVITY_KEY) {
                        CookingViewModelFactory.getPrimaryCavityViewModel().recoverFault()
                    } else if (cavityName == Constants.SECONDARY_CAVITY_KEY && CookingAppUtils.getSecondaryCookingViewModel() != null) {
                        CookingAppUtils.getSecondaryCookingViewModel()?.recoverFault()
                    }
                    sharedViewModel?.setFaultPopUpOpen(false)
                    if (sharedViewModel?.isShowOtherCavityFaultPopUp() == true) {
                        sharedViewModel.instantiateFaultPopUpForOtherCavity(cavityName)
                        sharedViewModel.handleFaultPopUpForOtherCavity(
                            fragment.resources.getInteger(
                                R.integer.brownout_recovery_timeout_millisec
                            )
                        )
                    }
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                }
            })
        }

        /**
         * Insert meat probe dialog
         *
         * @param fragment
         */
        fun insertMeatProbe(
            fragment: Fragment,
            programmingCookingViewModel: CookingViewModel,
            onMeatProbeConditionMet: () -> Unit
        ) {
            dismissDialogs()
            val handler = Handler(Looper.getMainLooper())
            popupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_probe_fragment)
                    .setHeaderTitle(R.string.text_header_insert_probe)
                    .setDescriptionMessage(R.string.text_description_insert_probe)
                    .setIsRightButtonEnable(false)
                    .setPopupImageResource(R.drawable.img_assisted_cooking_guide_leaf_small)
                    .setIsPopupCenterAligned(true)
                    .setCancellableOutSideTouch(false)
                    .setDescriptionTextGravity(Gravity.START)
                    .setDescriptionTextFont(
                        ResourcesCompat.getFont(
                            fragment.requireContext(),
                            R.font.roboto_light
                        )
                    )
                    .build()
            val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                (object : MeatProbeUtils.MeatProbeListener {
                    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                        MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                            popupBuilder,
                            fragment
                        )
                        if (cookingViewModel?.cavityName?.value?.contentEquals(
                                programmingCookingViewModel.cavityName.value
                            ) == true
                        ) {
                            onMeatProbeConditionMet()
                        } else {
                            //show wrong cavity programing dialog here
                            HMILogHelper.Loge("onMeatProbeInsertion for wrong cavity ${cookingViewModel?.cavityName?.value}")
                            probeDetectedInOtherCavityMidWayRecipeRunning(
                                fragment,
                                cookingViewModel,
                                programmingCookingViewModel
                            )
                        }
                    }

                    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                        //no op
                    }

                })

            val hmiCancelButtonInteractionListener = HMICancelButtonInteractionListener {
                MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(popupBuilder, fragment)
                HMIExpansionUtils.cancelButtonPressEventFromAnyScreen(fragment)
                popupBuilder?.let { builder ->
                    handler.postDelayed(
                        { builder.dismiss() },
                        AppConstants.POPUP_DISMISS_DELAY.toLong()
                    )
                }
            }

            //Knob Implementation
            val hmiKnobListener = observeHmiKnobListener(
                onHMIRightKnobClick = {
                    //do nothing leave it blank
                },
                onHMILeftKnobClick = {
                    //do nothing leave it blank
                },
                onKnobSelectionTimeout = {
                    //do nothing leave it blank
                }
            )

            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    //Hot cavity popup enabled the HMI cancel button
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    //Handling the left align cavity icon. Below code added bcz no other layout has left align cavity icon
                    when (CookingViewModelFactory.getProductVariantEnum()) {
                        ProductVariantEnum.DOUBLEOVEN -> {
                            popupBuilder?.provideViewHolderHelper()?.cavityIconParentImageview?.visible()
                            val cavityIcon = if (programmingCookingViewModel.isPrimaryCavity) {
                                R.drawable.ic_oven_cavity_large
                            } else {
                                R.drawable.ic_lower_cavity_large
                            }
                            popupBuilder?.provideViewHolderHelper()?.cavityIconImageview?.setImageResource(
                                cavityIcon
                            )
                        }

                        ProductVariantEnum.COMBO -> {
                            popupBuilder?.provideViewHolderHelper()?.cavityIconParentImageview?.visible()
                            popupBuilder?.provideViewHolderHelper()?.cavityIconImageview?.setImageResource(
                                R.drawable.ic_lower_cavity_large
                            )

                        }

                        else -> popupBuilder?.provideViewHolderHelper()?.cavityIconParentImageview?.gone()
                    }
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })
            popupBuilder?.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)
            if(hotCavityDoorClosePopup == null) {
                popupBuilder?.show(
                    fragment.parentFragmentManager, "insertMeatProbeBeforeStartingRecipe"
                )
            }
        }


        /**
         * Brief : for Handling probe instruction when probe is inserted.
         */
        fun probeDetectedFromSameCavityPopupBuilder(
            fragment: Fragment,
            parentCookingViewModel: CookingViewModel?
        ) {
            dismissDialogs()
            val handler = Handler(Looper.getMainLooper())
            var headerTitle =
                if (parentCookingViewModel?.isPrimaryCavity == true) R.string.text_header_probe_detected else R.string.text_header_probe_detected

            var cavityIcon = HEADER_VIEW_CENTER_ICON_GONE
            var layoutid = R.layout.layout_popup_fragment
            when {
                CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.DOUBLEOVEN -> {
                    layoutid = R.layout.layout_popup_icon_cavity
                    cavityIcon =
                        if (CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity) {
                            R.drawable.ic_large_upper_cavity
                        } else {
                            R.drawable.ic_large_lower_cavity
                        }
                }

                CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.COMBO -> {
                    layoutid = R.layout.layout_popup_icon_cavity
                    cavityIcon = R.drawable.ic_large_lower_cavity
                    headerTitle = R.string.text_header_probe_detected
                }

                CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.SINGLEOVEN -> {
                    layoutid = R.layout.layout_popup_fragment
                    headerTitle = R.string.text_header_probe_detected
                }
            }
            popupBuilder =
                ScrollDialogPopupBuilder.Builder(layoutid)
                    .setHeaderTitle(headerTitle)
                    .setHeaderViewCenterIcon(cavityIcon, false)
                    .setCancellableOutSideTouch(false)
                    .setDescriptionMessage(R.string.text_description_probe_detected2)
                    .setDescriptionTextFont(
                        ResourcesCompat.getFont(
                            fragment.requireContext(),
                            R.font.roboto_light
                        )
                    )
                    .setIsPopupCenterAligned(true)
                    .setLeftButton(R.string.text_button_no){
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        false
                    }
                    .setRightButton(R.string.text_button_yes) {
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.start_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        //For use case when meat probe inserted during favorites flow & user choses yes to use probe
                        CookingAppUtils.setNavigatedFrom(EMPTY_STRING)
                        //Setting the head to the top of the probe tree
                        CookingViewModelFactory.setInScopeViewModel(parentCookingViewModel)
                        if (CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.COMBO) {
                            CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getSecondaryCavityViewModel())
                        }
                        navigateSafely(
                            fragment,
                            R.id.action_to_probeCyclesSelectionFragment,
                            null,
                            null
                        )
                        MeatProbeUtils.removeMeatProbeListener()
                        false
                    }.build()

            popupBuilder?.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)

            //Knob Implementation
            val hmiKnobListener = observeHmiKnobListener(
                onHMIRightKnobClick = {
                    popupBuilder?.onHMIRightKnobClick()
                }, onKnobSelectionTimeout = {}
            )
            val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                (object : MeatProbeUtils.MeatProbeListener {
                    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                        MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                            popupBuilder,
                            fragment
                        )
                        if (cookingViewModel?.cavityName?.value?.contentEquals(
                                parentCookingViewModel?.cavityName?.value
                            ) == false
                        ) {
                            //probe inserted in other cavity
                            probeDetectedInOtherCavityMidWayRecipeRunning(
                                fragment,
                                cookingViewModel,
                                parentCookingViewModel
                            )
                        }
                    }

                    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                        MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                            popupBuilder,
                            fragment
                        )
                    }

                })
            val hmiCancelButtonInteractionListener = HMICancelButtonInteractionListener {
                HMIExpansionUtils.cancelButtonPressEventFromAnyScreen(fragment)
                popupBuilder?.let { builder ->
                    handler.postDelayed(
                        { builder.dismiss() },
                        AppConstants.POPUP_DISMISS_DELAY.toLong()
                    )
                }
            }

            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                }
            })
            if (popupBuilder?.isVisible == false && hotCavityDoorClosePopup == null) {
                popupBuilder?.show(fragment.parentFragmentManager, "cookTimeAddError")
            }
        }

        /**
         * Brief : for Handling probe instruction when probe is inserted.
         */
        fun probeDetectedInOtherCavityMidWayRecipeRunning(
            fragment: Fragment,
            insertedProbeCookingViewModel: CookingViewModel?,
            existingCookingViewModel: CookingViewModel?,
        ) {
            if(insertedProbeCookingViewModel?.cavityId?.value == existingCookingViewModel?.cavityId?.value && insertedProbeCookingViewModel?.recipeExecutionViewModel?.isProbeBasedRecipe == true){
                HMILogHelper.Logd(fragment.tag, "${insertedProbeCookingViewModel.cavityName.value} probe inserted in same cavity and programming recipe is Probe based so skipping probeDetectedInOtherCavityMidWayRecipeRunning")
                return
            }
            dismissDialogs()
            val handler = Handler(Looper.getMainLooper())
            val headerTitle: Int
            var cavityIcon = HEADER_VIEW_CENTER_ICON_GONE
            val layoutid: Int
            when (CookingViewModelFactory.getProductVariantEnum()) {
                ProductVariantEnum.DOUBLEOVEN -> {
                    headerTitle =
                        if (insertedProbeCookingViewModel?.isPrimaryCavity == true) R.string.text_header_probe_detected else R.string.text_header_probe_detected
                    layoutid = R.layout.layout_popup_icon_cavity
                    cavityIcon = if (insertedProbeCookingViewModel?.isPrimaryCavity == true) {
                        R.drawable.ic_large_upper_cavity
                    } else {
                        R.drawable.ic_large_lower_cavity
                    }
                }

                ProductVariantEnum.COMBO -> {
                    layoutid = R.layout.layout_popup_icon_cavity
                    cavityIcon = R.drawable.ic_large_lower_cavity
                    headerTitle = R.string.text_header_probe_detected
                }

                ProductVariantEnum.SINGLEOVEN -> {
                    layoutid = R.layout.layout_popup_fragment
                    headerTitle = R.string.text_header_probe_detected
                }

                else -> {
                    layoutid = R.layout.layout_popup_fragment
                    headerTitle = R.string.text_header_probe_detected
                }
            }
            popupBuilder =
                ScrollDialogPopupBuilder.Builder(layoutid)
                    .setHeaderTitle(headerTitle)
                    .setDescriptionMessage(R.string.text_description_probe_detected2)
                    .setIsPopupCenterAligned(true)
                    .setHeaderViewCenterIcon(cavityIcon, false)
                    .setCancellableOutSideTouch(false)
                    .setDescriptionTextFont(
                        fragment.resources.getFont(R.font.roboto_light)
                    )
                    .setLeftButton(R.string.text_button_no) {
                        MeatProbeUtils.removeMeatProbeListener()
                        if (existingCookingViewModel?.recipeExecutionViewModel?.isRunning == true && existingCookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe && !MeatProbeUtils.isMeatProbeConnected(
                                existingCookingViewModel
                            )
                        ) {
                            probeRemovedDuringRecipeRunning(
                                fragment, existingCookingViewModel
                            )
                        } else {
                            fragment.onResume()
                        }
                        false
                    }.setRightButton(R.string.text_button_yes) {
                        //Setting other cavity model as in scope view model
                        MeatProbeUtils.removeMeatProbeListener()
                        CookingViewModelFactory.setInScopeViewModel(insertedProbeCookingViewModel)
                        navigateSafely(
                            fragment,
                            R.id.action_to_probeCyclesSelectionFragment,
                            null,
                            null
                        )
                        false
                    }.build()

            popupBuilder?.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)

            //Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener = observeHmiKnobListener(
                onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == AppConstants.RIGHT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                popupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                                    null
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                    null
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                },
                onHMIRightKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            popupBuilder?.onHMILeftKnobClick()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            popupBuilder?.onHMIRightKnobClick()
                        }
                    }
                }, onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder?.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                }
            )

            val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                (object : MeatProbeUtils.MeatProbeListener {
                    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                        MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                            popupBuilder,
                            fragment,
                            onDialogDismissCallback = {
                                handler.postDelayed(
                                    {
                                        if (cookingViewModel?.cavityName?.value?.contentEquals(
                                                insertedProbeCookingViewModel?.cavityName?.value
                                            ) == false
                                        ) {
                                            //probe inserted in other cavity
                                            when (CookingViewModelFactory.getProductVariantEnum()) {
                                                ProductVariantEnum.SINGLEOVEN,
                                                ProductVariantEnum.COMBO -> {
                                                    probeDetectedFromSameCavityPopupBuilder(
                                                        fragment,
                                                        CookingViewModelFactory.getInScopeViewModel()
                                                    )
                                                }

                                                ProductVariantEnum.DOUBLEOVEN -> {
                                                    probeDetectedInOtherCavityMidWayRecipeRunning(
                                                        fragment,
                                                        cookingViewModel,
                                                        CookingViewModelFactory.getInScopeViewModel()
                                                    )
                                                }

                                                else -> {}
                                            }
                                        }
                                    },
                                    AppConstants.POPUP_DISMISS_DELAY.toLong()
                                )

                            }
                        )

                    }

                    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                        if (cookingViewModel?.cavityName?.value?.contentEquals(
                                insertedProbeCookingViewModel?.cavityName?.value
                            ) == true
                        ) {
                            MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                                popupBuilder, fragment
                            )
                        }
                    }

                })
            val hmiCancelButtonInteractionListener = HMICancelButtonInteractionListener {
                HMIExpansionUtils.cancelButtonPressEventFromAnyScreen(fragment)
                popupBuilder?.let { builder ->
                    handler.postDelayed(
                        { builder.dismiss() },
                        AppConstants.POPUP_DISMISS_DELAY.toLong()
                    )
                }
            }
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                }
            })
            if (popupBuilder?.isVisible == false && hotCavityDoorClosePopup == null) {
                (if (fragment is DialogFragment) getVisibleFragment()?.parentFragmentManager else fragment.parentFragmentManager)?.let {
                    popupBuilder?.show(
                        it, "probeDetectedInOtherCavity"
                    )
                }
            }
        }

        /**
         * Brief : When probe is inserted, and user tries to navigate out of probe menu, this popup occurs
         */
        fun probeStillDetectedPopupBuilder(
            fragment: Fragment,
            parentCookingViewModel: CookingViewModel,
            onMeatProbeRemoveConditionMet: () -> Unit,
        ) {
            dismissDialogs()
            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_probe_fragment)
                .setDescriptionMessage(R.string.text_description_probe_detected1)
                .setHeaderTitle(R.string.text_header_probe_detected)
                .setIsPopupCenterAligned(true)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_80PX)
                .setTopMarginForDescriptionText(AppConstants.POPUP_DESCIPTION_TOP_MARGIN_10PX)
                .setCancellableOutSideTouch(false)
                .setRightButton(R.string.text_button_return_to_probe_modes) {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    false
                }
                .build()


            //Knob Implementation
            val hmiKnobListener = observeHmiKnobListener(
                onHMIRightKnobClick = {
                    popupBuilder?.onHMIRightKnobClick()
                }, onKnobSelectionTimeout = {}
            )

            val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                (object : MeatProbeUtils.MeatProbeListener {
                    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                        MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                            popupBuilder,
                            fragment
                        )
                        if (cookingViewModel?.cavityName?.value?.contentEquals(
                                parentCookingViewModel.cavityName.value
                            ) == false
                        ) {
                            //probe inserted in other cavity
                            probeDetectedInOtherCavityMidWayRecipeRunning(
                                fragment,
                                cookingViewModel,
                                parentCookingViewModel
                            )
                        }
                    }

                    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                        if (cookingViewModel?.cavityName?.value?.contentEquals(
                                parentCookingViewModel.cavityName.value
                            ) == true
                        ) {
                            //probe removed in same cavity
                            MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                                popupBuilder,
                                fragment
                            )
                            onMeatProbeRemoveConditionMet()
                        }
                        //else probe removed in other cavity, in this case do nothing and keep showing popup
                    }
                })

            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)

                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })

            popupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    popupBuilder?.dismiss()
                    parentCookingViewModel.recipeExecutionViewModel.cancel()
                    navigateToStatusOrClockScreen(fragment)
                    HMILogHelper.Logd("Probe still detected timeOut exceeded for  ${parentCookingViewModel.cavityName.value}, cancelling recipeExecutionViewModel")
                }
            }, fragment.resources.getInteger(R.integer.to_do_popup_timeout))

            if (popupBuilder?.isVisible == false && hotCavityDoorClosePopup == null) {
                fragment.parentFragmentManager.let {
                    popupBuilder?.show(
                        it,
                        "OtherCavityProbeDetectedPopUp"
                    )
                }
            }
        }


        /**
         * Brief : for Handling probe missing popup instruction when trying to access from tools menu
         * pausing the delay timer when meat probe is removed and resuming when attached
         */

        fun probeRemovedDuringRecipeRunning(
            fragment: Fragment,
            runningCookingViewModel: CookingViewModel?,
            onMeatProbeInsertionCallback: () -> Unit = {},
            onMeatProbeDestroy: () -> Unit = {}
        ) {
            dismissDialogs()
            val handler = Handler(Looper.getMainLooper())
            var cavityIcon = HEADER_VIEW_CENTER_ICON_GONE
            var layoutid = R.layout.layout_popup_fragment
            when {
                CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.DOUBLEOVEN -> {
                    layoutid = R.layout.layout_popup_icon_cavity
                    cavityIcon = if (runningCookingViewModel?.isPrimaryCavity == true) {
                        R.drawable.ic_large_upper_cavity
                    } else {
                        R.drawable.ic_large_lower_cavity
                    }
                }

                CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.COMBO -> {
                    layoutid = R.layout.layout_popup_icon_cavity
                    cavityIcon = R.drawable.ic_large_lower_cavity
                }
            }
            popupBuilder = ScrollDialogPopupBuilder.Builder(layoutid)
                .setDescriptionMessage(R.string.text_description_probe_removed)
                .setHeaderTitle(R.string.text_header_probe_removed)
                .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_104PX)
                .setTopMarginForHeaderIcon(AppConstants.SELF_CLEAN_POPUP_TITLE_TOP_MARGIN_74PX)
                .setTopMarginForDescriptionText(AppConstants.POPUP_DESCRIPTION_TOP_MARGIN_6PX)
                .setHeaderViewCenterIcon(cavityIcon, false)
                .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextFont(
                    ResourcesCompat.getFont(
                        fragment.requireContext(),
                        R.font.roboto_light
                    )
                )
                .build()

            val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                (object : MeatProbeUtils.MeatProbeListener {
                    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                        MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                            popupBuilder,
                            fragment
                        )
                        if (!runningCookingViewModel?.cavityName?.value.contentEquals(
                                cookingViewModel?.cavityName?.value
                            )
                        ) {
                            probeDetectedInOtherCavityMidWayRecipeRunning(
                                fragment,
                                cookingViewModel,
                                runningCookingViewModel
                            )
                        }
                        onMeatProbeInsertionCallback()
                    }

                    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                    }

                })
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    popupBuilder?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                        false
                }

                override fun onDialogDestroy() {
                    MeatProbeUtils.removeMeatProbeListener()
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                    onMeatProbeDestroy()
                }
            })
            popupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    HMILogHelper.Logd("cancelling recipe on timeout of probe missing popup for ${runningCookingViewModel?.cavityName?.value}")
                    MeatProbeUtils.removeMeatProbeListenerAndDismissPopup(
                        popupBuilder,
                        fragment
                    )
                    runningCookingViewModel?.recipeExecutionViewModel?.cancel()
                }
            }, fragment.resources.getInteger(R.integer.session_long_timeout))

            if (popupBuilder?.isVisible == false && hotCavityDoorClosePopup == null) {
                fragment.parentFragmentManager.let {
                    popupBuilder?.show(
                        it,
                        "probeRemovePopupRunningCookingBuilder"
                    )
                }
            }
        }

        /**
         * Method to show popup for user instruction step
         *
         * @param cookingViewModel in scope cooking view model
         * @param text             user instruction key from capability file
         */
        fun displayCookingInstructionPopUp(
            fragment: Fragment,
            cookingViewModel: CookingViewModel,
            text: String?,
        ) {
            var instructions = 0
            var doorInteractionInstructions = 0
            when (text) {
                AppConstants.INSERT_FOOD -> {
                    instructions = R.string.text_cooking_instruction_insertFood
                    doorInteractionInstructions = R.string.text_cooking_instruction_insertFood_done
                }

                AppConstants.STIR_FOOD -> {
                    instructions = R.string.text_cooking_instruction_stir
                    doorInteractionInstructions = R.string.text_cooking_instruction_stir_done
                }

                AppConstants.TURN_FOOD -> {
                    instructions = R.string.text_cooking_instruction_turn_food_done
                    doorInteractionInstructions = R.string.text_cooking_instruction_turn_done
                }

                AppConstants.ADD_INGREDIENT -> {
                    instructions = R.string.text_cooking_instruction_addIngredient
                    doorInteractionInstructions = R.string.text_add_ingredient_press_done
                }

                AppConstants.FLIP_FOOD -> {
                    instructions = R.string.text_description_cooking_instruction
                    doorInteractionInstructions =
                        R.string.text_description_cooking_instruction_flip_food
                }
            }
            if (instructions == 0) {
                HMILogHelper.Loge("userInstruction", "not found for $text, so not showing dialog")
                return
            }
            if (popupBuilder?.tag?.contentEquals("userInstructionCookingBuilder") == true) {
                HMILogHelper.Loge(
                    "userInstruction",
                    "userInstructionCookingBuilder is already showing text $text, caused by multiple livedata update"
                )
                return
            } else {
                dismissDialogs()
            }
            HMILogHelper.Logd("userInstruction", "showing userInstruction Dialog text=$text")
            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setDescriptionMessage(instructions).setIsPopupCenterAligned(true)
                .setIsRightButtonEnable(true)
                .setCancellableOutSideTouch(false)
                .setRightButton(R.string.text_button_dismiss) {
                    cookingViewModel.recipeExecutionViewModel.acceptUserAcknowledgement()
                    cookingViewModel.recipeExecutionViewModel.resume()
                    HMILogHelper.Logd(
                        "userInstruction",
                        "acceptUserAcknowledgement true by pressing right button"
                    )
                    popupBuilder?.dismiss()
                    false
                }
                .setHeaderTitle(R.string.text_header_cooking_instructions).build()
            popupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    if (cookingViewModel.doorState.value == true) {
                        HMILogHelper.Logd(
                            "userInstruction",
                            "acceptUserAcknowledgement false by timeout, because door is open"
                        )
                    } else {
                        cookingViewModel.recipeExecutionViewModel.acceptUserAcknowledgement()
                        cookingViewModel.recipeExecutionViewModel.resume()
                        HMILogHelper.Logd(
                            "userInstruction",
                            "acceptUserAcknowledgement true by timeout"
                        )
                        popupBuilder?.dismiss()
                    }
                }
            }, fragment.resources.getInteger(R.integer.duration_status_mode_text_oven_ready_2_min))
            val doorObserver = Observer<Boolean> { isDoorOpen ->
                if (isDoorOpen) {
                    HMILogHelper.Logd(
                        "userInstruction",
                        "currentDoorStatus=open, doorInteractionInstructions $doorInteractionInstructions"
                    )
                    popupBuilder?.provideViewHolderHelper()?.descriptionTextView?.setText(
                        doorInteractionInstructions
                    )
                    popupBuilder?.provideViewHolderHelper()?.rightTextButton?.setTextButtonText(R.string.text_button_done)
                } else {
                    HMILogHelper.Logd(
                        "userInstruction",
                        "currentDoorStatus=closed, restarting timeout"
                    )
                    popupBuilder?.restartTimeout()
                }
                popupBuilder?.provideViewHolderHelper()?.rightTextButton?.isEnabled = !isDoorOpen
                popupBuilder?.provideViewHolderHelper()?.rightTextButton?.isClickable = !isDoorOpen
            }
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    cookingViewModel.doorState?.observe(fragment, doorObserver)
                }

                override fun onDialogDestroy() {
                    //remove door state observer
                    cookingViewModel.doorState?.removeObserver(doorObserver)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    popupBuilder = null
                }
            })
            if (popupBuilder?.isVisible == false && hotCavityDoorClosePopup == null) {
                fragment.parentFragmentManager.let {
                    popupBuilder?.show(
                        it,
                        "userInstructionCookingBuilder"
                    )
                }
            }
        }


        /**
         * Observes specific HMI knob listener events.
         *
         * This function provides a way to selectively observe specific HMI knob listener events
         * using higher-order functions. It allows you to avoid overriding all listener methods
         * and instead focus on the events you're interested in.
         *
         * Example observeHmiKnobListener(onHMILeftKnobClick {}): A callback function to be called when the right knob is clicked.
         * @return HMIKnobInteractionListener
         */
        fun observeHmiKnobListener(
            onHMILeftKnobClick: () -> Unit = {},
            onHMILongLeftKnobPress: () -> Unit = {},
            onHMIRightKnobClick: () -> Unit = {},
            onHMILongRightKnobPress: () -> Unit = {},
            onHMIRightKnobTickHoldEvent: (timeInterval: Int) -> Unit = {},
            onKnobRotateEvent: (knobId: Int, knobDirection: String) -> Unit = { _, _ -> },
            onKnobSelectionTimeout: () -> Unit
        ): HMIKnobInteractionListener {

            return object : HMIKnobInteractionListener {

                /**
                 * Called when there is a Left Knob click event
                 */
                override fun onHMILeftKnobClick() = onHMILeftKnobClick()

                /**
                 * Called when there is a Long Left Knob click event
                 */
                override fun onHMILongLeftKnobPress() = onHMILongLeftKnobPress()

                /**
                 * Called when there is a Right Knob click event
                 */
                override fun onHMIRightKnobClick() = onHMIRightKnobClick()

                /**
                 * Called when there is a Long Right Knob click event
                 */
                override fun onHMILongRightKnobPress() = onHMILongRightKnobPress()

                /**
                 * Called when there is a Long Right Knob click event
                 */
                override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) =
                    onHMIRightKnobTickHoldEvent(timeInterval)

                override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
                    //Do nothing
                }

                /**
                 * Called when there is a knob rotate event on a Knobs
                 * @param knobId  knob ID
                 * @param knobDirection knob movement direction
                 */
                override fun onKnobRotateEvent(knobId: Int, knobDirection: String) =
                    onKnobRotateEvent(knobId, knobDirection)

                /**
                 * Called when there is a timeout for knob selection
                 */
                override fun onKnobSelectionTimeout(knobId: Int) = onKnobSelectionTimeout()
            }
        }

        fun isEnterHotCavityDialogPopupVisible(): Boolean {
            return hotCavityPopupBuilder?.isVisible == true
        }

        /**
         * Brief: Method to raise Popup for hot cavity with Temp
         *
         * @param fragment         fragment name
         * @param cookingViewModel view model
         * @param hotCavityMessage cavity message
         * @param cookingIsAllowed is cooking Allowed
         * @param isSabbathError   is sabbath error
         */
        fun hotCavityCoolDownPopupBuilder(
            fragment: Fragment,
            cookingViewModel: CookingViewModel,
            hotCavityTitle: String,
            hotCavityMessage: String,
            cookingIsAllowed: Boolean?,
            isDelay: Boolean?,
            isSabbathError: Boolean?
        ) {
            val handler = Handler(Looper.getMainLooper())
            var cavityIcon = HEADER_VIEW_CENTER_ICON_GONE
            if ((CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.DOUBLEOVEN) ||
                (CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.COMBO)
            ) {
                cavityIcon = if (CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity) {
                    R.drawable.ic_large_upper_cavity
                } else {
                    R.drawable.ic_large_lower_cavity
                }
            }
            val timeoutViewModel = ViewModelProvider(fragment)[TimeoutViewModel::class.java]

            val builder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_hot_cavity)
                .setHeaderTitle(hotCavityTitle)
                .setDescriptionMessage(hotCavityMessage)
            if (cavityIcon != HEADER_VIEW_CENTER_ICON_GONE) {
                builder.setHeaderViewCenterIcon(cavityIcon, false)
            } else {
                builder.setTopMarginForTitleText(POPUP_TITLE_TOP_MARGIN)
                builder.setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
            }
            if (cookingIsAllowed == true) {
                builder.setIsRightButtonEnable(true)
                builder.setRightButton(R.string.text_button_start) {
                    isSabbathError?.let {
                        handleErrorAndStartCooking(
                            fragment, cookingViewModel,
                            it, false
                        )
                    }
                    true
                }
//             Delay condition
                if (isDelay == true) {
                    builder.setIsLeftButtonEnable(true)
                    builder.setLeftButton(R.string.text_button_delay) {
                        true
                    }
                }
            } else {
                builder.setIsRightButtonEnable(false)
                builder.setRightButton(R.string.text_button_next) {
                    false
                }
            }
            hotCavityPopupBuilder = builder.build()

            hotCavityPopupBuilder?.setTimeoutCallback(
                {
                    cookingViewModel.recipeExecutionViewModel.cancel()
                    HMILogHelper.Logd("cancelling recipe of ${cookingViewModel.cavityName.value} hotCavityCoolDownPopupBuilder timeout")
                    hotCavityPopupBuilder?.let { builder ->
                        handler.postDelayed(
                            {
                                builder.dismiss()
                            },
                            AppConstants.POPUP_DISMISS_DELAY.toLong()
                        )
                    }
                    CavityStateUtils.onTimeoutProgrammingState(fragment)
                    navigateToStatusOrClockScreen(fragment)
                },
                fragment.resources.getInteger(R.integer.cool_down_confirmation_timeout)
            )

            val hmiCancelButtonInteractionListener = HMICancelButtonInteractionListener {
                timeoutViewModel.setTimeout(TIME_OUT_DEFAULT)
                HMIExpansionUtils.cancelButtonPressEventFromAnyScreen(fragment)
                hotCavityPopupBuilder?.let { builder ->
                    handler.postDelayed(
                        {
                            builder.dismiss()
                        },
                        AppConstants.POPUP_DISMISS_DELAY.toLong()
                    )
                }
            }

            hotCavityPopupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    //Hot cavity popup enabled the HMI cancel button
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    hotCavityPopupBuilder?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                        false
                    if (!((cookingIsAllowed == true) && (isDelay == true))) {
                        hotCavityPopupBuilder?.provideViewHolderHelper()?.bodyTextWithHotCavityTemp?.visibility =
                            View.VISIBLE
                        hotCavityPopupBuilder?.provideViewHolderHelper()?.ivRampDownHotCavity?.visibility =
                            View.VISIBLE
                        hotCavityPopupBuilder?.provideViewHolderHelper()?.bodyTextWithHotCavityTemp?.background =
                            null
                        val maxAllowedTemperature =
                            cookingViewModel.recipeExecutionViewModel?.maxStartTemperature?.toInt()

                        hotCavityPopupBuilder?.getViewLifecycleOwner()?.let {
                            cookingViewModel.ovenTemperature?.observe(
                                it
                            ) { hotCavityTemp: Int ->
                                val (unit, offset) = if (SettingsViewModel.getSettingsViewModel().temperatureUnit.getValue() == SettingsViewModel.TemperatureUnit.CELSIUS) {
                                    hotCavityPopupBuilder?.getString(R.string.text_tiles_list_celsius_value)
                                        .toString() to HOT_CAVITY_WARNING_OFFSET_CELCIUS
                                } else {
                                    hotCavityPopupBuilder?.getString(R.string.text_tiles_list_fahrenheit_value)
                                        .toString() to HOT_CAVITY_WARNING_OFFSET_FAHRENHEIT
                                }

                                if (cookingIsAllowed == false) {
                                    hotCavityPopupBuilder?.provideViewHolderHelper()?.rightTextButton?.visibility =
                                        View.VISIBLE
                                    hotCavityPopupBuilder?.provideViewHolderHelper()?.rightTextButton?.setTextButtonText(
                                        R.string.text_button_next
                                    )
                                    hotCavityPopupBuilder?.provideViewHolderHelper()?.rightTextButton?.isEnabled =
                                        false
                                    hotCavityPopupBuilder?.provideViewHolderHelper()?.rightTextButton?.isClickable =
                                        false
                                    hotCavityPopupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                                        null
                                }

                                val hotCavityTempText =
                                    "$hotCavityTemp$unit / $maxAllowedTemperature$unit"

                                hotCavityPopupBuilder?.provideViewHolderHelper()?.bodyTextWithHotCavityTemp?.text =
                                    hotCavityTempText

                                val maxAllowedAfterTemperature =
                                    cookingViewModel.recipeExecutionViewModel?.maxStartTemperature?.toInt()

                                maxAllowedAfterTemperature?.let { maxTemp ->
                                    if (hotCavityTemp <= maxTemp - offset) {
                                        hotCavityPopupBuilder?.let { builder ->
                                            handler.postDelayed(
                                                {
                                                    builder.dismiss()
                                                },
                                                AppConstants.POPUP_DISMISS_DELAY.toLong()
                                            )
                                        }
                                        if (cookingViewModel.recipeExecutionViewModel?.optionalOptions?.getValue()
                                                ?.contains(RecipeOptions.DELAY_TIME) == true
                                        ) {
                                            hotCavityOvenReadyPopupBuilder(
                                                fragment,
                                                CookingAppUtils.getHotCavityTitleAndDescription(
                                                    fragment,
                                                    cookingViewModel,
                                                    AppConstants.HOT_CAVITY_WARNING_OVEN_READY,
                                                    AppConstants.HOT_CAVITY_WARNING_TITLE
                                                ),
                                                CookingAppUtils.getHotCavityTitleAndDescription(
                                                    fragment,
                                                    cookingViewModel,
                                                    AppConstants.HOT_CAVITY_WARNING_OVEN_READY,
                                                    AppConstants.HOT_CAVITY_WARNING_DESCRIPTION
                                                ),
                                                cookingViewModel,
                                                isDelay,
                                                isSabbathError
                                            )
                                        } else {
                                            hotCavityOvenReadyPopupBuilder(
                                                fragment,
                                                CookingAppUtils.getHotCavityTitleAndDescription(
                                                    fragment,
                                                    cookingViewModel,
                                                    AppConstants.HOT_CAVITY_WARNING_OVEN_READY,
                                                    AppConstants.HOT_CAVITY_WARNING_TITLE
                                                ),
                                                CookingAppUtils.getHotCavityTitleAndDescription(
                                                    fragment,
                                                    cookingViewModel,
                                                    AppConstants.HOT_CAVITY_WARNING_OVEN_READY,
                                                    AppConstants.HOT_CAVITY_WARNING_DESCRIPTION
                                                ),
                                                cookingViewModel,
                                                isDelay,
                                                isSabbathError
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    handler.removeCallbacksAndMessages(null)
                    popupBuilder = null
                }
            })
            if(hotCavityDoorClosePopup == null) {
                hotCavityPopupBuilder?.show(
                    fragment.getParentFragmentManager(),
                    "HotCavityPopupWithTemp"
                )
            }
        }

        /**
         * Brief: Method to raise Popup for hot cavity without Temp
         *
         * @param fragment               Fragment
         * @param popupTitle             Popup Title
         * @param hotCavityMessage       Hot Cavity Message
         * @param cookingViewModel       cookingViewModel
         * @param isSabbathError         is sabbath Error
         */
        fun hotCavityOvenReadyPopupBuilder(
            fragment: Fragment,
            popupTitle: String,
            hotCavityMessage: String,
            cookingViewModel: CookingViewModel,
            isDelay: Boolean?,
            isSabbathError: Boolean?
        ) {
            val handler = Handler(Looper.getMainLooper())
            val timeoutViewModel = ViewModelProvider(fragment)[TimeoutViewModel::class.java]
            var cavityIcon = HEADER_VIEW_CENTER_ICON_GONE
            if ((CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.DOUBLEOVEN) ||
                (CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.COMBO)
            ) {
                cavityIcon = if (CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity) {
                    R.drawable.ic_large_upper_cavity
                } else {
                    R.drawable.ic_large_lower_cavity
                }
            }

            val builder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_hot_cavity)
                .setHeaderTitle(popupTitle)
                .setDescriptionMessage(hotCavityMessage)
                .setRightButton(R.string.text_button_next) {
                    isSabbathError?.let {
                        handleErrorAndStartCooking(
                            fragment, cookingViewModel,
                            it, false
                        )
                    }
                    true
                }
            if (cavityIcon != HEADER_VIEW_CENTER_ICON_GONE) {
                builder.setHeaderViewCenterIcon(cavityIcon, false)
            } else {
                builder.setTopMarginForTitleText(POPUP_TITLE_TOP_MARGIN)
                builder.setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
            }
            hotCavityPopupBuilder = builder.build()

            val hmiCancelButtonInteractionListener = HMICancelButtonInteractionListener {
                timeoutViewModel.setTimeout(TIME_OUT_DEFAULT)
                cookingViewModel.recipeExecutionViewModel.cancel()
                HMILogHelper.Logd("cancelling recipe of ${cookingViewModel.cavityName.value} hotCavityOvenReadyPopupBuilder on HMI cancel button click")
                hotCavityPopupBuilder?.let { builder ->
                    handler.postDelayed(
                        {
                            builder.dismiss()
                        },
                        AppConstants.POPUP_DISMISS_DELAY.toLong()
                    )
                }
                HMIExpansionUtils.cancelButtonPressEventFromAnyScreen(fragment)
            }

            hotCavityPopupBuilder?.setTimeoutCallback({
                cookingViewModel.recipeExecutionViewModel.cancel()
                HMILogHelper.Logd("cancelled recipe on hotCavityOvenReadyPopupBuilder ${cookingViewModel.cavityName.value} timeout")
                hotCavityPopupBuilder?.let { builder ->
                    handler.postDelayed(
                        {
                            builder.dismiss()
                        },
                        AppConstants.POPUP_DISMISS_DELAY.toLong()
                    )
                }
                CavityStateUtils.onTimeoutProgrammingState(fragment)
                navigateToStatusOrClockScreen(fragment)
            }, fragment.resources.getInteger(R.integer.cool_down_confirmation_timeout))

            hotCavityPopupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    //Hot cavity popup enabled the HMI cancel button
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    hotCavityPopupBuilder?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                        false
                    if (isDelay == false) {
                        hotCavityPopupBuilder?.provideViewHolderHelper()?.bodyTextWithHotCavityTemp?.visibility =
                            View.VISIBLE
                        hotCavityPopupBuilder?.provideViewHolderHelper()?.ivRampDownHotCavity?.visibility =
                            View.VISIBLE
                        hotCavityPopupBuilder?.provideViewHolderHelper()?.bodyTextWithHotCavityTemp?.background =
                            null
                        val maxAllowedTemperature =
                            cookingViewModel.recipeExecutionViewModel?.maxStartTemperature?.toInt()

                        hotCavityPopupBuilder?.getViewLifecycleOwner()?.let {
                            cookingViewModel.ovenTemperature?.observe(
                                it
                            ) { hotCavityTemp: Int ->
                                val unit =
                                    if (SettingsViewModel.getSettingsViewModel().temperatureUnit.getValue() == SettingsViewModel.TemperatureUnit.CELSIUS) {
                                        hotCavityPopupBuilder?.getString(R.string.text_tiles_list_celsius_value)
                                    } else {
                                        hotCavityPopupBuilder?.getString(R.string.text_tiles_list_fahrenheit_value)
                                    }
                                val hotCavityTempText =
                                    "$hotCavityTemp$unit / $maxAllowedTemperature$unit"

                                hotCavityPopupBuilder?.provideViewHolderHelper()?.bodyTextWithHotCavityTemp?.text =
                                    hotCavityTempText

                            }
                        }
                    } else {
                        hotCavityPopupBuilder?.provideViewHolderHelper()?.leftTextButton?.visibility =
                            View.VISIBLE
                        hotCavityPopupBuilder?.provideViewHolderHelper()?.leftTextButton?.setTextButtonText(
                            R.string.text_button_delay
                        )
                        hotCavityPopupBuilder?.provideViewHolderHelper()?.leftTextButton?.setOnClickListener {
                            NavigationUtils.navigateToDelayScreen(hotCavityPopupBuilder!!.requireParentFragment())
                            hotCavityPopupBuilder?.dismiss()
                        }
                    }
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    popupBuilder = null
                }
            })
            if(hotCavityDoorClosePopup == null) {
                hotCavityPopupBuilder?.show(fragment.getParentFragmentManager(), "HotCavityPopup")
            }
        }

        /**
         * Brief : for showing Kitchen Timer Complete Popup
         */
        fun kitchenTimerCompletedPopup(
            fragment: Fragment,
            kitchenTimerViewModel: KitchenTimerViewModel,
        ) {
            val ktName = kitchenTimerViewModel.timerName
            val timeSetTo = kitchenTimerViewModel.timeSetTo.value
            val ktPopupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setIsPopupCenterAligned(true)
                .setHeaderTitle(R.string.text_header_timer_complete, ktName)
                .setDescriptionMessage(
                    fragment.getString(
                        R.string.text_description_timer_complete,
                        KitchenTimerUtils.convertTimerCompletedToShortString(
                            fragment.requireContext(),
                            timeSetTo?.toLong() ?: 0
                        )
                    )
                ).setLeftButton(R.string.text_button_repeat) {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    //START SAME KITCHEN TIMER WITH REPEAT ACTION
                    HMILogHelper.Logd("kitchenTimer", "$ktName completed, repeating $timeSetTo ")
                    if (timeSetTo != 0 && kitchenTimerViewModel.setTimer(timeSetTo ?: 0)) {
                        if (CookingAppUtils.getVisibleFragment(ContextProvider.getFragmentActivity()?.supportFragmentManager) !is KitchenTumblerListTimerFragment) {
                            HMILogHelper.Logd(
                                "kitchenTimer",
                                "$ktName completed is not KitchenTumblerListTimerFragment, moving to KT Tumbler List"
                            )
                            fragment.activity?.supportFragmentManager?.let {
                                CookingAppUtils.dismissAllDialogs(
                                    it
                                )
                            }
                            navigateSafely(
                                fragment,
                                R.id.global_action_to_kitchenTimerFragment,
                                null,
                                null
                            )
                        }
                    } else {
                        HMILogHelper.Loge(
                            "kitchenTimer",
                            "unable to repeat $ktName with value $timeSetTo"
                        )
                    }
                    false
                }.setRightButton(R.string.text_button_dismiss) {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    //dismiss the kitchen timer
                    kitchenTimerViewModel.resetTimerStatus()
                    HMILogHelper.Logd("kitchenTimer", "$ktName resetTimerStatus")
                    CookingAppUtils.startGattServer(fragment)
                    false
                }.build()
            ktPopupBuilder.setTimeoutCallback({ timeoutStatesEnum ->
                if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    kitchenTimerViewModel.resetTimerStatus()
                    ktPopupBuilder.dismiss()
                }
            }, fragment.resources.getInteger(R.integer.pop_up_timeout_10_sec))

            //Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener =
                observeHmiKnobListener(onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                ktPopupBuilder.provideViewHolderHelper()?.rightTextButton?.background =
                                    null
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    ktPopupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                ktPopupBuilder.provideViewHolderHelper()?.leftTextButton?.background =
                                    null
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    ktPopupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }, onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            ktPopupBuilder.onHMILeftKnobClick()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            ktPopupBuilder.onHMIRightKnobClick()
                        }
                    }
                }, onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    ktPopupBuilder.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                })

            ktPopupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.long_notification_1,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    ktPopupBuilder.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                        false
                }
                override fun onDialogDestroy() {
                    AudioManagerUtils.stopAudio(
                        R.raw.long_notification_1,
                        AudioManager.STREAM_SYSTEM,
                        0
                    )
                    getVisibleFragment()?.parentFragmentManager?.setFragmentResult(
                        POP_UP_DISMISS, bundleOf(
                            POP_UP_DISMISS to true
                        )
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })
            if(hotCavityDoorClosePopup == null) {
                (if (fragment is DialogFragment) getVisibleFragment()?.parentFragmentManager else fragment.parentFragmentManager)?.let {
                    ktPopupBuilder.show(
                        it, "kitchenTimerCompletedPopup"
                    )
                }
            }
        }


        /**
         * Brief : for showing Kitchen Timer cancel Popup
         */
        fun kitchenTimerCancelPopup(
            fragment: Fragment,
            kitchenTimerViewModel: KitchenTimerViewModel,
            onCancelKitchenTimer: () -> Unit,
            restartScreenTimeout: () -> Unit
        ) {
            if (popupBuilder?.isVisible == true) popupBuilder?.dismiss()
            val ktName = kitchenTimerViewModel.timerName
            HMILogHelper.Logd(
                "KitchenTimer",
                "cancel popup for $ktName kitchenTimer state ${kitchenTimerViewModel.timerStatus.value}"
            )
            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setIsPopupCenterAligned(true)
                .setHeaderTitle(R.string.text_header_cancel_timer).setDescriptionMessage(
                    fragment.getString(
                        R.string.text_description_cancel_timer_only1, ktName
                    )
                ).setLeftButton(R.string.text_button_no) {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    //no action on pressing "No" just dismiss the dialog
                    HMILogHelper.Logd(
                        "kitchenTimer",
                        "$ktName cancel, pressing no dismissing dialog"
                    )
                    restartScreenTimeout()
                    false
                }.setRightButton(R.string.text_button_yes) {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    //pressing yes, will cancel the given KitchenTimer and if no timers are running then move to clock screen, otherwise will stay on the same fragment
                    HMILogHelper.Logd(
                        "kitchenTimer",
                        "$ktName cancel, pressing yes cancelling and dismissing dialog"
                    )
                    onCancelKitchenTimer()
                    false
                }.build()
            val timeoutDuration = if (fragment is KitchenTumblerListTimerFragment) {
                fragment.resources.getInteger(R.integer.modal_popup_timeout)
            } else {
                fragment.resources.getInteger(R.integer.pop_up_timeout_30_sec)
            }
            popupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    popupBuilder?.dismiss()
                    if (fragment !is KitchenTumblerListTimerFragment) {
                        // Navigate to the clock fragment here
                        navigateSafely(
                            fragment,
                            R.id.clockFragment,
                            null,
                            null
                        )
                    }
                    restartScreenTimeout()
                }
            }, timeoutDuration)

            //Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener =
                observeHmiKnobListener(onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == AppConstants.LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                popupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                                    null
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                    null
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }, onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            popupBuilder?.onHMILeftKnobClick()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            popupBuilder?.onHMIRightKnobClick()
                        }
                    }
                }, onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder?.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                })

            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    popupBuilder?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                        false
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    popupBuilder = null
                }
            })
            if(hotCavityDoorClosePopup == null) {
                (if (fragment is DialogFragment) getVisibleFragment()?.parentFragmentManager else fragment.parentFragmentManager)?.let {
                    popupBuilder?.show(
                        it, "kitchenTimerCancelPopup"
                    )
                }
            }
        }

        /**
         * Brief : for showing to perform any operation that KitchenTimer can restrict
         */
        fun allKitchenTimerCancelPopup(
            fragment: Fragment, onCancellingAllKitchenTimers: () -> Unit,
            originalKnobListener: HMIKnobInteractionListener?
        ) {
            //do not perform operation if no kitchen timer running
            if (!KitchenTimerVMFactory.isAnyKitchenTimerRunning()) return
            val runningKtName = StringBuilder()
            KitchenTimerVMFactory.getKitchenTimerViewModels()?.let {
                for ((index, ktModel) in it.withIndex()) if (ktModel.isRunning) {
                    if (KitchenTimerVMFactory.getKitchenTimerViewModels()
                            ?.filter { it1 -> it1.isRunning }?.size!! > 1 && index != 0
                    ) runningKtName.append(", ")
                    runningKtName.append(ktModel.timerName)
                }
            }
            HMILogHelper.Logd(
                "KitchenTimer", "cancelAll KitchenTimer $runningKtName"
            )
            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setIsPopupCenterAligned(true)
                .setHeaderTitle(R.string.text_header_cancel_timer).setDescriptionMessage(
                    fragment.getString(
                        R.string.text_description_cancel_timer_only1, runningKtName.toString()
                    )
                ).setLeftButton(R.string.text_button_no) {
                    //no action on pressing "No" just dismiss the dialog
                    HMILogHelper.Logd(
                        "kitchenTimer",
                        "$runningKtName cancelAll, pressing no dismissing dialog"
                    )
                    originalKnobListener?.let { HMIExpansionUtils.setHMIKnobInteractionListener(it) }
                    false
                }.setRightButton(R.string.text_button_yes) {
                    //pressing yes, will cancel the given KitchenTimer and if no timers are running then move to clock screen, otherwise will stay on the same fragment
                    HMILogHelper.Logd(
                        "kitchenTimer",
                        "$runningKtName cancelAll, pressing yes cancelling and dismissing dialog"
                    )
                    KitchenTimerVMFactory.stopAllKitchenTimers()
                    onCancellingAllKitchenTimers()
                    false
                }.build()
            val timeoutDuration = if (fragment is KitchenTumblerListTimerFragment) {
                fragment.resources.getInteger(R.integer.modal_popup_timeout)
            } else {
                fragment.resources.getInteger(R.integer.pop_up_timeout_30_sec)
            }
            popupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    popupBuilder?.dismiss()
                    if (fragment !is KitchenTumblerListTimerFragment) {
                        // Navigate to the clock fragment here
                        navigateSafely(
                            fragment,
                            R.id.clockFragment,
                            null,
                            null
                        )
                    }
                }
            }, timeoutDuration)

            //Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener =
                observeHmiKnobListener(onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                popupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                                    null
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                    null
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }, onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            popupBuilder?.onHMILeftKnobClick()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            popupBuilder?.onHMIRightKnobClick()
                        }
                    }
                }, onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder?.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                })

            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    popupBuilder?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                        false
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    popupBuilder = null
                }
            })
            if(hotCavityDoorClosePopup == null) {
                (if (fragment is DialogFragment) getVisibleFragment()?.parentFragmentManager else fragment.parentFragmentManager)?.let {
                    popupBuilder?.show(
                        it,
                        "allKitchenTimerCancelPopup"
                    )
                }
            }
        }
//
//        /**
//         * show popup to set time and date after blackout from delay flow
//         *
//         * @param fragment where the popup is showing
//         */
//        fun setTimeAndDatePopup(fragment: Fragment) {
//            HMILogHelper.Logd(
//                "setDateAndTime", "building set date and time popup"
//            )
//            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
//                .setIsPopupCenterAligned(true).setHeaderTitle(R.string.setTimeAndDate)
//                .setDescriptionMessage(
//                    R.string.setTimeAndDateDescription
//                ).setLeftButton(R.string.text_button_dismiss) {
//                    //no action on pressing "No" just dismiss the dialog
//                    HMILogHelper.Logd(
//                        "setDateAndTime", "pressing Dismiss dismissing dialog"
//                    )
//                    false
//                }.setRightButton(R.string.text_button_set_time_and_date) {
//                    //pressing yes, open date and time settings
//                    HMILogHelper.Logd(
//                        "setDateAndTime", "opening settings to set time and date"
//                    )
//                    //TODO navigate to regional settings fragment
//                    false
//                }.build()
//            popupBuilder?.setTimeoutCallback(
//                { timeoutStatesEnum -> if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) popupBuilder?.dismiss() },
//                fragment.resources.getInteger(R.integer.session_short_timeout)
//            )
//
//            //Knob Implementation
//            var knobRotationCount = 0
//            val hmiKnobListener =
//                observeHmiKnobListener(onKnobRotateEvent = { knobId, knobDirection ->
//                    if (knobId == LEFT_KNOB_ID) {
//                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
//                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
//                        when (knobRotationCount) {
//                            AppConstants.KNOB_COUNTER_ONE -> {
//                                popupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
//                                    null
//                                updatePopUpLeftTextButtonBackground(
//                                    fragment,
//                                    popupBuilder,
//                                    R.drawable.selector_textview_walnut
//                                )
//                            }
//
//                            AppConstants.KNOB_COUNTER_TWO -> {
//                                popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
//                                    null
//                                updatePopUpRightTextButtonBackground(
//                                    fragment,
//                                    popupBuilder,
//                                    R.drawable.selector_textview_walnut
//                                )
//                            }
//                        }
//                    }
//                }, onHMILeftKnobClick = {
//                    when (knobRotationCount) {
//                        AppConstants.KNOB_COUNTER_ONE -> {
//                            popupBuilder?.onHMILeftKnobClick()
//                        }
//
//                        AppConstants.KNOB_COUNTER_TWO -> {
//                            popupBuilder?.onHMIRightKnobClick()
//                        }
//                    }
//                }, onKnobSelectionTimeout = {
//                    knobRotationCount = 0
//                    popupBuilder?.provideViewHolderHelper()?.apply {
//                        setLeftAndRightButtonBackgroundNull(
//                            this.leftTextButton,
//                            this.rightTextButton
//                        )
//                    }
//                })
//
//            popupBuilder?.setOnDialogCreatedListener(object :
//                ScrollDialogPopupBuilder.OnDialogCreatedListener {
//                override fun onDialogCreated() {
//                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
//                    popupBuilder?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
//                        false
//                }
//
//                override fun onDialogDestroy() {
//                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
//                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
//                    popupBuilder = null
//                }
//            })
//            (if (fragment is DialogFragment) getVisibleFragment()?.parentFragmentManager else fragment.parentFragmentManager)?.let {
//                popupBuilder?.show(
//                    it,
//                    "setTimeAndDatePopup"
//                )
//            }
//        }

        /**         * start OTA flow
         *
         * @param fragment Fragment view's reference, can be null if there is no need to launch OTA manager
         */
        fun initAndStartOtaFlow(
            fragment: Fragment, showApplianceBusyStatePopup: Boolean,
            view: View? = null, isViewVisible: Boolean = false
        ) {
            val applianceState: Boolean = CookingAppUtils.setApplianceOtaState()
            if (applianceState && popupBuilder == null && showApplianceBusyStatePopup) {
                HMILogHelper.Logd("OTA Popup: Cannot be started as appliance is busy")
                popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_ota_popup_fragment)
                    .setDescriptionMessage(R.string.text_description_appliance_busy)
                    .setHeaderTitle(R.string.text_header_appliance_is_busy)
                    .setTopMarginForTitleText(POPUP_OTA_TITLE_TOP_SMALL_MARGIN)
                    .setTopMarginForDescriptionText(POPUP_OTA_DESCRIPTION_TOP_SMALL_MARGIN)
                    .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setRightButton(R.string.text_button_ok) {
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        false
                    }
                    .build()
                //Knob Implementation
                val hmiKnobListener = observeHmiKnobListener(
                    onHMIRightKnobClick = {
                        popupBuilder?.onHMIRightKnobClick()
                    }, onKnobSelectionTimeout = {}
                )
                popupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                    }

                    override fun onDialogDestroy() {
                        getVisibleFragment()?.parentFragmentManager?.clearFragmentResult(AppConstants.CONNECTIVITYLIST_FRAGMENT)
                        getVisibleFragment()?.parentFragmentManager?.clearFragmentResultListener(AppConstants.CONNECTIVITYLIST_FRAGMENT)
                        HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                        if (popupBuilder != null) {
                            popupBuilder?.dismiss()
                            popupBuilder = null
                        }
                    }
                })
                popupBuilder?.show(
                    fragment.getParentFragmentManager(), "applianceBusySoftwareUpdatePopupBuilder"
                )
                return
            }
            //Dismiss the blackout power loss popup if it is showing because after OTA successful,
            // we are considering as blackout after OTA successful and in that case power loss popup is triggering but as per the requirement
            // we should show the OTA complete popup only, so dismiss the blackout popup,
            if (BlackoutUtils.dialogPopupBuilder != null) {
                BlackoutUtils.dialogPopupBuilder?.dismiss()
                BlackoutUtils.dialogPopupBuilder = null
            }
            var requiredView = fragment.requireView()
            if (isViewVisible && view != null) {
                requiredView = view
            }
            OtaUiManager.getInstance()
                .launchOtaUi(
                    requiredView,
                    R.id.global_action_to_clockScreen,
                    R.id.global_action_to_clockScreen
                )
        }

        /*
        * Details : to show control lock popup & get user Acknowledgement
        * args : fragment - Fragment name SettingsLandingFragment
        * controlLockToggle - Unit : callback function to reset toggle button.
        */
        fun showControlLockPopup(
            fragment: Fragment,
            controlLockToggle: () -> Unit
        ) {
        ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
            .setIsPopupCenterAligned(true)
            .setHeaderTitle(R.string.control_lock)
            .setDescriptionMessage(R.string.text_description_controlLock)
            .setTopMarginForTitleText(POPUP_TITLE_TOP_MARGIN)
            .setTopMarginForDescriptionText(AppConstants.POPUP_DESCIPTION_TOP_MARGIN_10PX)
            .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
            .setWidthForDescriptionText(COMMON_POPUP_DESCRIPTION_WIDTH)
            .setLeftButton(R.string.text_button_cancel) {
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                controlLockToggle()
                false
            }
            .setRightButton(R.string.text_button_continue) {
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.start_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                SettingsViewModel.getSettingsViewModel().setControlLock(true)
                CookingAppUtils.stopGattServer()
                false
            }
            .build().also { popupBuilder = it }

            popupBuilder?.setTimeoutCallback(
                { timeoutStatesEnum -> if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) popupBuilder?.dismiss() },
                fragment.resources.getInteger(R.integer.session_short_timeout)
            )

            //Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener =
                observeHmiKnobListener(onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                popupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                                    null
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                    null
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }, onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            KnobNavigationUtils.knobBackTrace = true
                            popupBuilder?.onHMILeftKnobClick()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            popupBuilder?.onHMIRightKnobClick()
                        }
                    }
                }, onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder?.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                })

            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    if (KnobNavigationUtils.knobForwardTrace) {
                        KnobNavigationUtils.knobForwardTrace = false
                        knobRotationCount = AppConstants.KNOB_COUNTER_TWO
                        updatePopUpRightTextButtonBackground(
                            fragment,
                            popupBuilder,
                            R.drawable.selector_textview_walnut
                        )
                    }
                    popupBuilder?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                        false
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    popupBuilder = null
                }
            })
            popupBuilder?.show(
                fragment.parentFragmentManager,
                "showControlLockPopup"
            )
        }

        /**
         * Brief : for showing popup for demo mode detailed instructions while entering
         */
        fun demoModeEntryInstructionPopUp(fragment: Fragment) {
            dismissDialogs()
            val handler = Handler(Looper.getMainLooper())
            popupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_probe_fragment)
                    .setHeaderTitle(R.string.text_header_enter_demo_mode)
                    .setDescriptionMessage(R.string.text_description_enter_demo_mode)
                    .setLeftButton(R.string.text_button_cancel) {
                        navigateToStatusOrClockScreen(fragment)
                        true
                    }
                    .setRightButton(R.string.text_button_continue) {
                        navigateSafely(
                            fragment,
                            R.id.DemoModeCodeFragment,
                            null,
                            null
                        )
                        true
                    }
                    .setTopMarginForDescriptionText(AppConstants.DEMO_ENTRY_DESCRIPTION_MARGIN)
                    .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setTopMarginForTitleText(AppConstants.DEMO_ENTRY_TITLE_MARGIN)
                    .setIsLeftButtonEnable(true)
                    .setIsRightButtonEnable(true)
                    .build()

            val hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener =
                (HMICancelButtonInteractionListener { popupBuilder?.dismiss() })
            //Added Knob implementation to start self clean cycle
            //Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            KnobNavigationUtils.knobBackTrace = true
                            popupBuilder?.onHMILeftKnobClick()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            KnobNavigationUtils.knobForwardTrace = true
                            popupBuilder?.onHMIRightKnobClick()
                        }
                    }
                },
                onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder?.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                },
                onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--

                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }
            )
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    if (KnobNavigationUtils.knobForwardTrace) {
                        KnobNavigationUtils.knobForwardTrace = false
                        knobRotationCount = AppConstants.KNOB_COUNTER_TWO
                        //popupBuilder?.provideViewHolderHelper()?.rightTextButton?.setBottomViewVisible(true)
                        updatePopUpRightTextButtonBackground(
                            fragment,
                            popupBuilder,
                            R.drawable.selector_textview_walnut
                        )
                    }
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    handler.removeCallbacksAndMessages(null)
                    if (popupBuilder != null) {
                        popupBuilder = null
                    }
                }
            })
            popupBuilder?.setTimeoutCallback({
                popupBuilder?.let {
                    navigateSafely(
                        it,
                        R.id.settingsLandingFragment,
                        null,
                        null
                    )
                }
                handler.postDelayed(
                    { popupBuilder?.dismiss() }, AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }, fragment.resources.getInteger(R.integer.self_clean_popup_timeout))
            if(hotCavityDoorClosePopup == null) {
                popupBuilder?.show(fragment.parentFragmentManager, "DemoModePopUp")
            }
        }

        /**
         * Brief : for showing popup for demo mode detailed instructions while exiting
         */
        fun demoModeExitInstructionPopUp(fragment: Fragment) {
            dismissDialogs()
            val handler = Handler(Looper.getMainLooper())
            popupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                    .setHeaderTitle(R.string.text_header_exit_demo_mode)
                    .setDescriptionMessage(R.string.text_description_exit_demo_mode)
                    .setLeftButton(R.string.text_button_cancel) {
                        val navId : Int = if (CookingAppUtils.getNavigatedFrom() == AppConstants.DEMOLANDING_FRAGMENT) {
                            R.id.demoModeLandingFragment
                        } else {
                            R.id.global_action_to_clockScreen
                        }
                        navigateSafely(
                            fragment,
                            navId,
                            null,
                            null
                        )
                        true
                    }
                    .setRightButton(R.string.text_button_continue) {
                        navigateSafely(
                            fragment,
                            R.id.DemoModeCodeFragment,
                            null,
                            null
                        )
                        true
                    }
                    .setTopMarginForTitleText(AppConstants.DEMO_EXIT_TITLE_MARGIN)
                    .setTopMarginForDescriptionText(AppConstants.DEMO_EXIT_DESCRIPTION_MARGIN)
                    .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setIsLeftButtonEnable(true)
                    .setIsRightButtonEnable(true)
                    .setWidthForDescriptionText(COMMON_POPUP_DESCRIPTION_WIDTH)
                    .build()

            val hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener =
                (HMICancelButtonInteractionListener { popupBuilder?.dismiss() })
            //Added Knob implementation to start self clean cycle
            //Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            KnobNavigationUtils.knobBackTrace = true
                            popupBuilder?.onHMILeftKnobClick()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            popupBuilder?.onHMIRightKnobClick()
                        }
                    }
                    popupBuilder?.dismiss()
                },
                onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder?.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                },
                onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--

                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }
            )
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    if (KnobNavigationUtils.knobForwardTrace) {
                        KnobNavigationUtils.knobForwardTrace = false
                        knobRotationCount = AppConstants.KNOB_COUNTER_TWO
                        updatePopUpRightTextButtonBackground(
                            fragment,
                            popupBuilder,
                            R.drawable.selector_textview_walnut
                        )
                    }
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    handler.removeCallbacksAndMessages(null)
                    if (popupBuilder != null) {
                        popupBuilder = null
                    }
                }
            })
            popupBuilder?.setTimeoutCallback({
                popupBuilder?.let {
                    navigateSafely(
                        it,
                        R.id.global_action_to_clockScreen,
                        null,
                        null
                    )
                }
                handler.postDelayed(
                    { popupBuilder?.dismiss() }, AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }, fragment.resources.getInteger(R.integer.to_do_popup_timeout))
            if(hotCavityDoorClosePopup == null) {
                popupBuilder?.show(fragment.parentFragmentManager, "DemoModePopUp")
            }
        }

        /**
         * Brief : for showing popup for demo mode detailed instructions while entering demo during recipe running
         */
        fun featureNotAvailablePopUp(fragment: Fragment) {
            val handler = Handler(Looper.getMainLooper())
            val popupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_probe_fragment)
                    .setHeaderTitle(R.string.text_header_appliance_is_busy)
                    .setDescriptionMessage(R.string.text_description_try_again_later)
                    .setRightButton(R.string.text_button_ok){
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        navigateToStatusOrClockScreen(fragment)
                        true
                    }
                    .setTopMarginForDescriptionText(AppConstants.DEMO_ENTRY_DESCRIPTION_MARGIN)
                    .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setTopMarginForTitleText(AppConstants.DEMO_ENTRY_TITLE_MARGIN)
                    .setIsLeftButtonEnable(false)
                    .setIsRightButtonEnable(true)
                    .build()

            val hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener =
                (HMICancelButtonInteractionListener { popupBuilder.dismiss() })
            //Added Knob implementation to start self clean cycle
            //Knob Implementation
            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    navigateToStatusOrClockScreen(fragment)
                    popupBuilder.dismiss()
                },
                onKnobSelectionTimeout = {
                    //Do Nothing
                },
            )
            popupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    handler.removeCallbacksAndMessages(null)
                }
            })
            popupBuilder.setTimeoutCallback({
                navigateToStatusOrClockScreen(fragment)
                handler.postDelayed(
                    { popupBuilder.dismiss() }, AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }, fragment.resources.getInteger(R.integer.demo_mode_popup_timeout))
            if(hotCavityDoorClosePopup == null) {
                popupBuilder.show(fragment.parentFragmentManager, "DemoModePopUp")
            }
        }

        /**
         * Brief : for showing popup for demo mode detailed instructions while entering demo during KT running
         */
        fun demoModeKTRunningEntryInstructionPopUp(fragment: Fragment) {
            val numOfTimers = KitchenTimerUtils.isKitchenTimersRunning()
            var description = fragment.getString(
                R.string.text_description_cancel_timer_common_only1,
                numOfTimers.toString()
            )
            if (numOfTimers > 1) {
                description = fragment.getString(R.string.text_description_cancel_timer_common_moreThan1)
            }

            val handler = Handler(Looper.getMainLooper())
            val popupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_probe_fragment)
                    .setHeaderTitle(R.string.text_header_cancel_timer)
                    .setDescriptionMessage(description)
                    .setRightButton(R.string.text_button_yes) {
                        popupBuilder?.dismiss()
                        CookingAppUtils.cancelIfAnyKitchenTimersRunning()
                        demoModeEntryInstructionPopUp(fragment)
                        true
                    }
                    .setLeftButton(R.string.text_button_no) {
                        navigateToStatusOrClockScreen(fragment)
                        true
                    }
                    .setTopMarginForDescriptionText(AppConstants.DEMO_ENTRY_DESCRIPTION_MARGIN)
                    .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setTopMarginForTitleText(AppConstants.DEMO_ENTRY_TITLE_MARGIN)
                    .setIsLeftButtonEnable(true)
                    .setIsRightButtonEnable(true)
                    .build()


            val hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener =
                (HMICancelButtonInteractionListener { popupBuilder.dismiss() })
            //Added Knob implementation to start self clean cycle
            //Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            navigateToStatusOrClockScreen(fragment)
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            CookingAppUtils.cancelIfAnyKitchenTimersRunning()
                            demoModeEntryInstructionPopUp(fragment)
                        }
                    }
                    popupBuilder.dismiss()
                },
                onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                },
                onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--

                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }
            )
            popupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    handler.removeCallbacksAndMessages(null)
                }
            })
            popupBuilder.setTimeoutCallback({
                navigateSafely(
                    popupBuilder,
                    R.id.clockFragment,
                    null,
                    null
                )
                handler.postDelayed(
                    { popupBuilder.dismiss() }, AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }, fragment.resources.getInteger(R.integer.demo_mode_popup_timeout))
            if(hotCavityDoorClosePopup == null) {
                popupBuilder.show(fragment.parentFragmentManager, "DemoModePopUp")
            }
        }

        /**
         * Brief : for showing popup for demo mode detailed instructions while exiting demo during recipe/KT running
         */
        fun demoModeCycleRunningExitInstructionPopUp(fragment: Fragment) {
            val handler = Handler(Looper.getMainLooper())
            val popupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                    .setHeaderTitle(R.string.text_header_exit_demo_mode)
                    .setDescriptionMessage(R.string.text_description_exit_demo_mode
)
                    .setLeftButton(R.string.text_button_cancel) {
                        navigateToStatusOrClockScreen(fragment)
                        true
                    }
                    .setRightButton(R.string.text_button_continue) {
                        navigateSafely(
                            fragment,
                            R.id.DemoModeCodeFragment,
                            null,
                            null
                        )
                        true
                    }
                    .setTopMarginForTitleText(AppConstants.DEMO_EXIT_TITLE_MARGIN)
                    .setTopMarginForDescriptionText(AppConstants.DEMO_EXIT_DESCRIPTION_MARGIN)
                    .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setIsLeftButtonEnable(true)
                    .setIsRightButtonEnable(true)
                    .setWidthForDescriptionText(COMMON_POPUP_DESCRIPTION_WIDTH)
                    .build()

            val hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener =
                (HMICancelButtonInteractionListener { popupBuilder.dismiss() })
            //Added Knob implementation to start self clean cycle
            //Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            navigateToStatusOrClockScreen(fragment)
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            navigateSafely(
                                fragment,
                                R.id.DemoModeCodeFragment,
                                null,
                                null
                            )
                        }
                    }
                    popupBuilder.dismiss()
                },
                onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                },
                onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--

                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }
            )
            popupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    handler.removeCallbacksAndMessages(null)
                }
            })
            popupBuilder.setTimeoutCallback({
                navigateSafely(
                    popupBuilder,
                    R.id.settingsLandingFragment,
                    null,
                    null
                )
                handler.postDelayed(
                    { popupBuilder.dismiss() }, AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }, fragment.resources.getInteger(R.integer.to_do_popup_timeout))
            if(hotCavityDoorClosePopup == null) {
                popupBuilder.show(fragment.parentFragmentManager, "DemoModePopUp")
            }
            }

        /**
         * Brief : for showing popup for demo mode detailed instructions while entering demo during KT running
         */
        fun updateDateAndTimeNotificationInstructionPopUp(fragment: Fragment) {
            val numOfTimers = KitchenTimerUtils.isKitchenTimersRunning()
            var description = R.string.text_description_update_date_time

            val handler = Handler(Looper.getMainLooper())
            val popupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_probe_fragment)
                    .setHeaderTitle(R.string.text_header_update_date_time)
                    .setDescriptionMessage(description)
                    .setRightButton(R.string.text_button_wifi_setup){
                        CookingAppUtils.startProvisioning(
                            getViewSafely(fragment),
                            false,
                            isFromConnectivityScreen = true,
                            isAoBProvisioning = false
                        )
                        true
                    }
                    .setLeftButton(R.string.text_button_manual_setup) {
                        navigateSafely(fragment, R.id.settingsTimeAndDateFragment, null, null)
                        true
                    }
                    .setTopMarginForDescriptionText(AppConstants.DEMO_ENTRY_DESCRIPTION_MARGIN)
                    .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setTopMarginForTitleText(AppConstants.DEMO_ENTRY_TITLE_MARGIN)
                    .setIsLeftButtonEnable(true)
                    .setIsRightButtonEnable(true)
                    .build()


            val hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener =
                (HMICancelButtonInteractionListener { popupBuilder.dismiss() })
            //Added Knob implementation to start self clean cycle
            //Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            navigateSafely(fragment, R.id.settingsTimeAndDateFragment, null, null)
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            CookingAppUtils.startProvisioning(
                                getViewSafely(fragment),
                                false,
                                isFromConnectivityScreen = true,
                                isAoBProvisioning = false
                            )
                        }
                    }
                    popupBuilder.dismiss()
                },
                onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                },
                onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--

                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }
            )
            popupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                    handler.removeCallbacksAndMessages(null)
                }
            })
            popupBuilder.setTimeoutCallback({
                navigateSafely(
                    popupBuilder,
                    R.id.settingsLandingFragment,
                    null,
                    null
                )
                handler.postDelayed(
                    { popupBuilder.dismiss() }, AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }, fragment.resources.getInteger(R.integer.demo_mode_popup_timeout))
            if(hotCavityDoorClosePopup == null) {
                popupBuilder.show(fragment.parentFragmentManager, "DemoModePopUp")
            }
        }
        /**
         * Brief : for Handling probe removed popup during probe extended cycle
         *
         */
        @Suppress("UNUSED_PARAMETER")
        fun removeProbeToContinueExtendedCycle(
            fragment: Fragment,
            runningCookingViewModel: CookingViewModel?,
            onContinueButtonClick: () -> Unit,
            onMeatProbeDestroy: () -> Unit = {}

        ) {
            dismissDialogs()
            val handler = Handler(Looper.getMainLooper())

            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(R.string.text_remove_probe_to_continue)
                .setDescriptionMessage(R.string.text_remove_probe_to_continue_description)
                .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_10PX)
                .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextFont(
                    ResourcesCompat.getFont(
                        fragment.requireContext(),
                        R.font.roboto_light
                    )
                )
                .setIsRightButtonEnable(false)
                .setCancellableOutSideTouch(false)
                .setRightButton(R.string.text_button_continue) {
                    popupBuilder?.dismiss()
                    onContinueButtonClick()
                    false
                }.build()

            //Knob Implementation
            val hmiKnobListener =
                observeHmiKnobListener(
                    onHMIRightKnobClick = {
                        popupBuilder?.onHMIRightKnobClick()
                    }, onKnobSelectionTimeout = {})
            val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                (object : MeatProbeUtils.MeatProbeListener {
                    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                        if(MeatProbeUtils.isMeatProbeConnected(runningCookingViewModel)) {
                            popupBuilder?.provideViewHolderHelper()?.rightTextButton?.isEnabled =
                                false
                        }
                    }

                    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                        if (!(MeatProbeUtils.isMeatProbeConnected(runningCookingViewModel))) {
                            popupBuilder?.provideViewHolderHelper()?.rightTextButton?.isEnabled =
                                true
                        }
                    }

                })
            val hmiCancelButtonInteractionListener = HMICancelButtonInteractionListener {
                HMIExpansionUtils.cancelButtonPressEventFromAnyScreen(fragment)
                popupBuilder?.let { builder ->
                    handler.postDelayed(
                        { builder.dismiss() },
                        AppConstants.POPUP_DISMISS_DELAY.toLong()
                    )
                }
            }
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    popupBuilder?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                        false
                }

                override fun onDialogDestroy() {
                    MeatProbeUtils.removeMeatProbeListener()
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                    CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                    onMeatProbeDestroy()
                }
            })
            //No timeout for probe extended popup
            popupBuilder?.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)

            if (popupBuilder?.isVisible == false && hotCavityDoorClosePopup == null) {
                fragment.parentFragmentManager.let {
                    popupBuilder?.show(
                        it,
                        "probeRemovedToExtendedCycleRunning"
                    )
                }
            }
        }

        /**
         * Brief : appliance feature exit confirmation popup
         */
        fun exploreFeaturesExitPopup(
            fragment: Fragment,
            onContinueButtonClick: () -> Unit = {}
        ) {
            dismissDialogs()
            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(R.string.text_header_explore_features_later)
                .setDescriptionMessage(R.string.text_description_explore_features_later)
                .setIsPopupCenterAligned(true)
                .setCancellableOutSideTouch(false)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_54PX)
                .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                .setRightButton(R.string.text_button_continue) {
                    SharedPreferenceManager.setSkipExploreFeatureFlag(AppConstants.TRUE_CONSTANT)
                    onContinueButtonClick()
                    false
                }
                .build()

            //Knob Interaction on popup
            var knobRotationCount = 0
            //Knob Implementation
            val hmiKnobListener = observeHmiKnobListener(
                onKnobRotateEvent = { knobId, knobDirection ->
                    HMILogHelper.Logd("Unboxing : onKnobRotateEvent: knobId:$knobId, knobDirection:$knobDirection")
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.MIN_KNOB_POSITION) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                        HMILogHelper.Logd("Unboxing : onKnobRotateEvent: knobRotationCount:$knobRotationCount")
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                    null
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                },
                onHMIRightKnobClick = {},
                onHMILeftKnobClick = {
                    popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                        null
                    updatePopUpRightTextButtonBackground(
                        fragment,
                        popupBuilder,
                        R.drawable.selector_textview_walnut
                    )
                    popupBuilder?.onHMILeftKnobClick()
                    KnobNavigationUtils.knobForwardTrace = true
                    SharedPreferenceManager.setSkipExploreFeatureFlag(AppConstants.TRUE_CONSTANT)
                    onContinueButtonClick()
                },
                onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder?.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                }
            )
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    if(KnobNavigationUtils.knobForwardTrace){
                        KnobNavigationUtils.knobForwardTrace = false
                        knobRotationCount = AppConstants.KNOB_COUNTER_ONE
                        updatePopUpRightTextButtonBackground(
                            fragment,
                            popupBuilder,
                            R.drawable.selector_textview_walnut
                        )
                    }
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })
            popupBuilder?.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)
            if(hotCavityDoorClosePopup == null) {
                fragment.parentFragmentManager.let {
                    popupBuilder?.show(
                        it,
                        "exploreFeaturesExitPopup"
                    )
                }
            }
        }

        /**
         * Brief : warning popup during unboxing if user user right knob
         */
        fun userLeftKnobWarningPopup(
            fragment: Fragment
        ) {
            dismissDialogs()
            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_probe_fragment)
                .setHeaderTitle(
                    fragment.getString(
                        R.string.text_dynamic_popup_content,
                        provideKnobWarningPopupTitle(fragment)
                    )
                )
                .setDescriptionMessage(
                    fragment.getString(
                        R.string.text_dynamic_popup_content,
                        provideKnobWarningPopupBody(fragment)
                    )
                )
                .setIsPopupCenterAligned(true)
                .setCancellableOutSideTouch(false)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_116PX)
                .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                .setRightButton(R.string.text_button_ok) {
                    false
                }
                .build()

            //Knob Interaction on popup
            var knobRotationCount = 0
            //Knob Implementation
            val hmiKnobListener = observeHmiKnobListener(
                onKnobRotateEvent = { knobId, knobDirection ->
                    HMILogHelper.Logd("Unboxing : onKnobRotateEvent: knobId:$knobId, knobDirection:$knobDirection")
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.MIN_KNOB_POSITION) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                        HMILogHelper.Logd("Unboxing : onKnobRotateEvent: knobRotationCount:$knobRotationCount")
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                    null
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                },
                onHMIRightKnobClick = {},
                onHMILeftKnobClick = {
                    popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                        null
                    updatePopUpRightTextButtonBackground(
                        fragment,
                        popupBuilder,
                        R.drawable.selector_textview_walnut
                    )
                    popupBuilder?.onHMILeftKnobClick()
                },
                onKnobSelectionTimeout = {}
            )
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })

            popupBuilder?.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)
            if(hotCavityDoorClosePopup == null) {
                fragment.parentFragmentManager.let {
                    popupBuilder?.show(
                        it,
                        "userLeftKnobWarningPopup"
                    )
                }
            }
        }

        private fun provideKnobWarningPopupTitle(fragment: Fragment): String {
            return if (LEFT_KNOB_ID == fragment.resources.getInteger(R.integer.integer_range_0)) {
                fragment.getString(R.string.text_header_use_left_knob_for_setup)
            } else {
                fragment.getString(R.string.text_header_use_right_knob_swap)
            }
        }

        private fun provideKnobWarningPopupBody(fragment: Fragment): String {
            return if (LEFT_KNOB_ID == fragment.resources.getInteger(R.integer.integer_range_0)) {
                fragment.getString(R.string.text_description_use_left_knob_for_setup)
            } else {
                fragment.getString(R.string.text_description_use_right_knob_swap)
            }
        }

        /**
         * Brief : for Handling probe removed popup during probe extended cycle
         *
         */

        fun showBroilCanNotModifyPopup(
            fragment: Fragment,
            runningCookingViewModel: CookingViewModel?,
        ) {
            dismissDialogs()
            val handler = Handler(Looper.getMainLooper())

            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(R.string.text_header_broil_level)
                .setDescriptionMessage(R.string.text_description_broil_level)
                .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_10PX)
                .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextFont(
                    ResourcesCompat.getFont(
                        fragment.requireContext(),
                        R.font.roboto_light
                    )
                )
                .setIsRightButtonEnable(true)
                .setRightButton(R.string.text_button_ok) {
                    popupBuilder?.dismiss()
                    false
                }.build()

            //Knob Implementation
            val hmiKnobListener =
                observeHmiKnobListener(
                    onHMIRightKnobClick = {
                        popupBuilder?.onHMIRightKnobClick()
                    }, onKnobSelectionTimeout = {})

            val hmiCancelButtonInteractionListener = HMICancelButtonInteractionListener {
                HMIExpansionUtils.cancelButtonPressEventFromAnyScreen(fragment)
                popupBuilder?.let { builder ->
                    handler.postDelayed(
                        { builder.dismiss() },
                        AppConstants.POPUP_DISMISS_DELAY.toLong()
                    )
                }
            }
            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    popupBuilder?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                        false
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })
            popupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    HMILogHelper.Logd("cancelling recipe on timeout of probe missing popup for ${runningCookingViewModel?.cavityName?.value}")
                    popupBuilder?.dismiss()
                }
            }, fragment.resources.getInteger(R.integer.session_long_timeout))

            if (popupBuilder?.isVisible == false && hotCavityDoorClosePopup == null) {
                fragment.parentFragmentManager.let {
                    popupBuilder?.show(
                        it,
                        "probeRemovedToExtendedCycleRunning"
                    )
                }
            }
        }

        /**
         * Brief: Warning popup for Connect to Network if cooking cycle or kitchen timer running in background.
         */
        fun provisioningUnavailablePopup(fragment: Fragment, title: Int, description: String) {
            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_ota_popup_fragment)
                .setHeaderTitle(title)
                .setDescriptionMessage(description)
                .setIsPopupCenterAligned(true)
                .setTopMarginForTitleText(POPUP_FORGET_NETWORK_DESCRIPTION_TITLE_TOP_MARGIN)
                .setTopMarginForDescriptionText(POPUP_OTA_DESCRIPTION_TOP_SMALL_MARGIN)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setRightButton(R.string.text_button_ok) {
                    navigateToStatusOrClockScreen(fragment)
                    true
                }
                .build()

            val hmiCancelButtonInteractionListener =
                HMICancelButtonInteractionListener { popupBuilder?.dismiss() }

            // Knob Implementation
            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    KnobNavigationUtils.knobBackTrace = true
                    popupBuilder?.dismiss()
                },
                onKnobSelectionTimeout = {
                    updatePopUpRightTextButtonBackground(
                        fragment,
                        popupBuilder,
                        R.drawable.text_view_ripple_effect
                    )
                }
            )

            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    if (KnobNavigationUtils.knobForwardTrace) {
                        KnobNavigationUtils.knobForwardTrace = false
                        updatePopUpRightTextButtonBackground(
                            fragment,
                            popupBuilder,
                            R.drawable.selector_textview_walnut
                        )
                    }
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })

            popupBuilder?.setTimeoutCallback(
                { popupBuilder?.dismiss() },
                fragment.resources.getInteger(R.integer.modal_popup_timeout)
            )

            if(hotCavityDoorClosePopup == null) {
                fragment.parentFragmentManager.let {
                    popupBuilder?.show(
                        it,
                        "provisioningUnavailablePopup"
                    )
                }
            }
        }

        /**
         * Method to show popup for wifi set up to use Remote Enable feature
         *
         * @param fragment fragment to show the popup
         */
        fun wifiSetUpPopupBuilder(fragment: Fragment) {
            popupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_ota_popup_fragment)
                .setHeaderTitle(R.string.text_header_set_up_wifi_to_use_this_feature)
                .setDescriptionMessage(R.string.text_layout_pop_up_decision_turn_on_remote_enable)
                .setIsPopupCenterAligned(true)
                .setTopMarginForTitleText(POPUP_FORGET_NETWORK_DESCRIPTION_TITLE_TOP_MARGIN)
                .setTopMarginForDescriptionText(POPUP_OTA_DESCRIPTION_TOP_SMALL_MARGIN)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setLeftButton(R.string.text_button_connectLater) {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    true
                }
                .setRightButton(R.string.text_button_connectNow) {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.start_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    if (!CookingAppUtils.isApplianceIdleForProvisioning()) {
                        provisioningUnavailablePopup(
                            fragment,
                            R.string.text_header_appliance_is_busy,
                            fragment.getString(R.string.text_description_try_again_later)
                        )
                    } else if (KitchenTimerUtils.isKitchenTimersRunning() != 0) {
                        provisioningUnavailablePopup(
                            fragment,
                            R.string.text_header_cancel_timer,
                            CookingAppUtils.getKitchenTimerCancelPopupDescription(fragment)
                        )
                    } else {
                        CookingAppUtils.startProvisioning(
                            getVisibleFragment()?.let { getViewSafely(it) },
                            false,
                            isFromConnectivityScreen = true,
                            false
                        )
                    }
                    true
                }
                .build()

            val hmiCancelButtonInteractionListener =
                HMICancelButtonInteractionListener { popupBuilder?.dismiss() }

            // Knob Implementation
            var knobRotationCount = 0
            val hmiKnobListener = observeHmiKnobListener(
                onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            KnobNavigationUtils.knobBackTrace = true
                            popupBuilder?.dismiss()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            if (CookingAppUtils.isAnyCycleRunning()) {
                                KnobNavigationUtils.knobForwardTrace = true
                                popupBuilder?.dismiss()
                                provisioningUnavailablePopup(
                                    fragment,
                                    R.string.text_header_appliance_is_busy,
                                    fragment.getString(R.string.text_description_try_again_later)
                                )
                            } else if (KitchenTimerUtils.isKitchenTimersRunning() != 0) {
                                KnobNavigationUtils.knobForwardTrace = true
                                popupBuilder?.dismiss()
                                provisioningUnavailablePopup(
                                    fragment,
                                    R.string.text_header_cancel_timer,
                                    CookingAppUtils.getKitchenTimerCancelPopupDescription(fragment)
                                )
                            } else {
                                popupBuilder?.dismiss()
                                CookingAppUtils.startProvisioning(
                                    getVisibleFragment()?.let { getViewSafely(it) },
                                    false,
                                    isFromConnectivityScreen = true,
                                    false
                                )
                            }
                        }
                    }
                },
                onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBuilder?.provideViewHolderHelper()?.apply {
                        setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                },
                onKnobRotateEvent = { knobId, knobDirection ->
                    if (knobId == LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) {
                            knobRotationCount++
                        } else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) {
                            knobRotationCount--
                        }
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                updatePopUpLeftTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.text_view_ripple_effect
                                )
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    popupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                            }
                        }
                    }
                }
            )

            popupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    if (KnobNavigationUtils.knobForwardTrace) {
                        KnobNavigationUtils.knobForwardTrace = false
                        knobRotationCount = AppConstants.KNOB_COUNTER_TWO
                        updatePopUpRightTextButtonBackground(
                            fragment,
                            popupBuilder,
                            R.drawable.selector_textview_walnut
                        )
                    }
                    HMIExpansionUtils.setHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                        hmiCancelButtonInteractionListener
                    )
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })

            popupBuilder?.setTimeoutCallback(
                { popupBuilder?.dismiss() },
                fragment.resources.getInteger(R.integer.demo_session_timeout)
            )

            fragment.parentFragmentManager.let { popupBuilder?.show(it, "wifiSetUpPopupBuilder") }
        }


        fun showAssignFavoritesPopupBuilder(fragment: Fragment, bodyText: SpannableStringBuilder) {
            dismissDialogs()
            if (popupBuilder == null) {
                HMILogHelper.Logd("showing assign favorites popup")
                popupBuilder =
                    bodyText.let {
                        ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                            .setHeaderTitle(R.string.text_assign_favorites)
                            .setSpannableDescriptionMessage(it)
                            .setRightButton(R.string.text_button_ok) {
                                AudioManagerUtils.playOneShotSound(
                                    ContextProvider.getContext(),
                                    R.raw.button_press,
                                    AudioManager.STREAM_SYSTEM,
                                    true,
                                    0,
                                    1
                                )
                                true
                            }
                            .setTopMarginForTitleText(AppConstants.POPUP_ASSIGN_FVA_TITLE_TOP_SMALL_MARGIN)
                            .setHeaderViewCenterIcon(
                                HEADER_VIEW_CENTER_ICON_GONE,
                                false
                            )
                            .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                            .setDescriptionTextGravity(Gravity.START).build()
                    }
                //Knob Interaction on popup
                var knobRotationCount = 0
                val hmiKnobListener =
                    observeHmiKnobListener(onKnobRotateEvent = { knobId, knobDirection ->
                        if (knobId == LEFT_KNOB_ID) {
                            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.MIN_KNOB_POSITION) knobRotationCount++
                            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                            when (knobRotationCount) {
                                AppConstants.KNOB_COUNTER_ONE -> {
                                    popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                        null
                                    updatePopUpRightTextButtonBackground(
                                        fragment,
                                        popupBuilder,
                                        R.drawable.selector_textview_walnut
                                    )
                                }
                            }
                        }
                    }, onHMIRightKnobClick = {
                        //Do nothing
                    }, onHMILeftKnobClick = {
                        updatePopUpRightTextButtonBackground(
                            fragment,
                            popupBuilder,
                            R.drawable.selector_textview_walnut
                        )
                        popupBuilder?.onHMILeftKnobClick()
                    }, onKnobSelectionTimeout = {
                        knobRotationCount = 0
                        popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                            null
                        popupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                            null
                    })
                popupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    }

                    override fun onDialogDestroy() {
                        HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                        CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                        dismissDialogs()
                    }
                })
                popupBuilder?.show(fragment.parentFragmentManager, "ASSIGN_FAVORITES_POPUP")
            }
        }

        fun probeIncompatiblePopup(fragment: Fragment, onMeatProbeConditionMet: () -> Unit = {}) {
            dismissSteamCompletedPopup()
            if(steamCyclePopupBuilder == null) {
                steamCyclePopupBuilder =
                    ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                        .setHeaderTitle(R.string.text_header_remove_probe_steam_clean)
                        .setDescriptionMessage(R.string.text_description_remove_probe_steam_clean)
                        .setIsLeftButtonEnable(false)
                        .setIsRightButtonEnable(false)
                        .setIsPopupCenterAligned(true)
                        .setCancellableOutSideTouch(false)
                        .setTopMarginForTitleText(AppConstants.POPUP_PROBE_TITLE_TOP_MARGIN_87PX)
                        .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                        .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                        .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                        .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                        .setDescriptionTextFont(
                            ResourcesCompat.getFont(
                                ContextProvider.getContext(),
                                R.font.roboto_light
                            )
                        )
                        .build()

                steamCyclePopupBuilder?.setTimeoutCallback({
                    navigateToStatusOrClockScreen(fragment)
                }, fragment.resources.getInteger(R.integer.session_long_timeout))

                val hmiMeatProbeListener: MeatProbeUtils.MeatProbeListener =
                    (object : MeatProbeUtils.MeatProbeListener {
                        override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
                            //Do nothing
                        }

                        override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
                            dismissSteamCompletedPopup()
                            onMeatProbeConditionMet()
                        }

                    })
                steamCyclePopupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                        MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    }

                    override fun onDialogDestroy() {
                        MeatProbeUtils.removeMeatProbeListener()
                    }
                })
                if(hotCavityDoorClosePopup == null) {
                    steamCyclePopupBuilder?.show(
                        fragment.parentFragmentManager,
                        SelfCleanInstructionsFragment::class.java.simpleName
                    )
                }
            }
        }

        fun steamCleanCompletePopup(fragment: Fragment, cavity: Int, cavityPosition: Int) {
            fragment.lifecycleScope.launch(Dispatchers.Main) {
                dismissSteamCompletedPopup()
                fragment.parentFragmentManager.findFragmentByTag(AppConstants.TAG_STEAM_POPUP)?.let {
                    (it as DialogFragment).dismiss()
                }
                withContext(Dispatchers.Main) {
                    if(steamCyclePopupBuilder == null) {
                        steamCyclePopupBuilder =
                            ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_icon_cavity)
                                .setHeaderTitle(R.string.text_header_steam_clean_complete)
                                .setDescriptionMessage(R.string.text_description_steam_clean_complete)
                                .setIsLeftButtonEnable(false)
                                .setIsRightButtonEnable(true)
                                .setIsPopupCenterAligned(true)
                                .setCancellableOutSideTouch(false)
                                .setTopMarginForHeaderIcon(AppConstants.POPUP_TITLE_TOP_IMAGE_MARGIN_23PX)
                                .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_191PX)
                                .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_105PX)
                                .setHeaderViewCenterIcon(
                                    if (CookingViewModelFactory.getProductVariantEnum() ==
                                        ProductVariantEnum.SINGLEOVEN
                                    ) HEADER_VIEW_CENTER_ICON_GONE else cavity, false
                                )
                                .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                                .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                                .setDescriptionTextFont(
                                    ResourcesCompat.getFont(
                                        ContextProvider.getContext(),
                                        R.font.roboto_light
                                    )
                                ).setRightButton(R.string.text_button_ok) {
                                    CookingAppUtils.cancelCompletedSteamCycle(cavityPosition)
                                    navigateToStatusOrClockScreen(fragment)
                                    true
                                }.build()

                        steamCyclePopupBuilder?.setTimeoutCallback({
                            CookingAppUtils.cancelCompletedSteamCycle(cavityPosition)
                            navigateToStatusOrClockScreen(fragment)
                        }, fragment.resources.getInteger(R.integer.session_long_timeout))

                        //Knob Interaction on popup
                        var knobRotationCount = 0
                        val hmiKnobListener =
                            observeHmiKnobListener(onKnobRotateEvent = { knobId, knobDirection ->
                                if (knobId == RIGHT_KNOB_ID) {
                                    if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.MIN_KNOB_POSITION) knobRotationCount++
                                    else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                                    when (knobRotationCount) {
                                        AppConstants.KNOB_COUNTER_ONE -> {
                                            steamCyclePopupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                                null
                                            updatePopUpRightTextButtonBackground(
                                                fragment,
                                                steamCyclePopupBuilder,
                                                R.drawable.selector_textview_walnut
                                            )
                                        }
                                    }
                                }
                            }, onHMIRightKnobClick = {
                                updatePopUpRightTextButtonBackground(
                                    fragment,
                                    steamCyclePopupBuilder,
                                    R.drawable.selector_textview_walnut
                                )
                                steamCyclePopupBuilder?.onHMIRightKnobClick()
                            }, onHMILeftKnobClick = {
                                //Do nothing
                            }, onKnobSelectionTimeout = {
                                knobRotationCount = 0
                                steamCyclePopupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                    null
                                steamCyclePopupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                                    null
                            })

                        steamCyclePopupBuilder?.setOnDialogCreatedListener(object :
                            ScrollDialogPopupBuilder.OnDialogCreatedListener {
                            override fun onDialogCreated() {
                                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                            }

                            override fun onDialogDestroy() {
                                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                                CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                            }
                        })
                        if(hotCavityDoorClosePopup == null) {
                            steamCyclePopupBuilder?.show(
                                fragment.parentFragmentManager,
                                AppConstants.TAG_STEAM_POPUP
                            )
                        }
                    }
                }
            }

        }

        /**
         * Hot Cavity Popup view to display hot cavity door close message
         *
         * @param fragment: fragment to access resource and previous popups
         */
        fun typeHotCavityWarningPopupBuilder(cookingViewModel: CookingViewModel, fragment: Fragment){
            if(hotCavityDoorClosePopup == null) {
                dismissDialogs()
                hotCavityDoorClosePopup =
                    ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                        .setHeaderTitle(R.string.text_header_close_door)
                        .setDescriptionMessage(R.string.text_popup_door_warning_message)
                        .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_10PX)
                        .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                        .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                        .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                        .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                        .setDescriptionTextFont(
                            ResourcesCompat.getFont(
                                fragment.requireContext(),
                                R.font.roboto_light
                            )
                        )
                        .setIsRightButtonEnable(true)
                        .setCancellableOutSideTouch(false)
                        .setRightButton(R.string.text_button_dismiss) {
                            hotCavityDoorClosePopup?.dismiss()
                            hotCavityDoorClosePopup = null
                            if (CookingAppUtils.isAnyCycleRunning()) {
                                if (cookingViewModel.doorState.value == true &&
                                    (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING
                                            || cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING_EXT)
                                ) {
                                    getVisibleFragment()?.let {
                                        DoorEventUtils.upperCloseDoorToContinueAction(
                                            it, cookingViewModel, onDoorCloseEventAction = {
                                                cookingViewModel.recipeExecutionViewModel.resume()
                                                lowerOvenHotCavityDoorClosePopupHandle()
                                            }
                                        )
                                    }
                                }
                                lowerOvenHotCavityDoorClosePopupHandle()
                            }
                            true
                        }.build()

                val hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener =
                    (HMICancelButtonInteractionListener {
                        hotCavityDoorClosePopup?.dismiss()
                        hotCavityDoorClosePopup = null
                        dismissDialogs()
                        if (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING
                                    || cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING_EXT)
                        {
                            if (CookingAppUtils.isPrimaryCavityCycleRunning()) {
                                cookingViewModel.recipeExecutionViewModel.cancel()
                            }
                            val lowerOvenViewModel =
                                CookingViewModelFactory.getSecondaryCavityViewModel()
                            if (lowerOvenViewModel != null && CookingAppUtils.isSecondaryCavityCycleRunning(lowerOvenViewModel)) {
                                if (lowerOvenViewModel.doorState.value == true) {
                                    lowerOvenHotCavityDoorClosePopupHandle()
                                } else {
                                    navigateToStatusOrClockScreen(fragment)
                                }
                            } else {
                                navigateToStatusOrClockScreen(fragment)
                            }
                        } else {
                            var recipeErrorResponse: RecipeErrorResponse? = null
                            if (cookingViewModel.doorState.value == true && (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING ||
                                        cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING_EXT) &&
                                (cookingViewModel.recipeExecutionViewModel.cookTimerState.value != Timer.State.COMPLETED)
                            ) {
                                cookingViewModel.recipeExecutionViewModel.pauseForCancel()
                                recipeErrorResponse =
                                    cookingViewModel.recipeExecutionViewModel.pauseCookTimer()
                                AudioManagerUtils.playOneShotSound(
                                    fragment.context,
                                    R.raw.attention,
                                    AudioManager.STREAM_SYSTEM,
                                    true,
                                    0,
                                    1
                                )
                            } else {
                                cookingViewModel.recipeExecutionViewModel.cancel()
                            }
                            if (recipeErrorResponse?.isError == true) runningFailPopupBuilder(
                                fragment
                            )
                            lowerOvenHotCavityDoorClosePopupHandle()
                        }
                    })

                //Knob Implementation
                val hmiKnobListener =
                    observeHmiKnobListener(
                        onHMIRightKnobClick = {
                            hotCavityDoorClosePopup?.onHMIRightKnobClick()
                        }, onKnobSelectionTimeout = {})

                hotCavityDoorClosePopup?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        HMIExpansionUtils.setHMICancelButtonInteractionListener(
                            hmiCancelButtonInteractionListener
                        )
                        AudioManagerUtils.playOneShotSound(
                            fragment.context,
                            R.raw.audio_critical_alert,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        HMIExpansionUtils.setHMICancelButtonInteractionListener(
                            hmiCancelButtonInteractionListener
                        )
//                        Door Event Observer
                        hotCavityDoorClosePopup?.getViewLifecycleOwner()?.let {
                            cookingViewModel.doorState?.observe(it) { isDoorOpen: Boolean? ->
                                if (!isDoorOpen!!) {
                                    hotCavityDoorClosePopup?.dismiss()
                                    hotCavityDoorClosePopup = null
                                    lowerOvenHotCavityDoorClosePopupHandle()
                                }
                            }
                        }

                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                        hotCavityDoorClosePopup?.provideViewHolderHelper()?.transparentBackgroundLayout?.isEnabled =
                            false
                    }

                    override fun onDialogDestroy() {
                        HMIExpansionUtils.removeHMICancelButtonInteractionListener(
                            hmiCancelButtonInteractionListener
                        )
                        HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                        CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                        hotCavityDoorClosePopup = null
                    }
                })
                //No timeout for hot cavity door open popup
                hotCavityDoorClosePopup?.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)

                if (hotCavityDoorClosePopup?.isVisible == false) {
                    fragment.parentFragmentManager.let {
                        hotCavityDoorClosePopup?.show(
                            it,
                            "HotCavityDoorOpenClosePopup"
                        )
                    }
                }
            } else {
                HMILogHelper.Loge("Hot Cavity Door Close popup is already showing")
            }
        }

        /**
         * To handle door close popup for Double cavity
         */
        fun lowerOvenHotCavityDoorClosePopupHandle() {
            if(CookingViewModelFactory.getProductVariantEnum() == ProductVariantEnum.DOUBLEOVEN) {
                if (CookingViewModelFactory.getSecondaryCavityViewModel() != null) {
                    val lowerOvenViewModel =
                        CookingViewModelFactory.getSecondaryCavityViewModel()
                    if (lowerOvenViewModel.doorState.value == true &&
                        (lowerOvenViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING
                                || lowerOvenViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING_EXT)
                    ) {
                        getVisibleFragment()?.let {
                            DoorEventUtils.lowerCloseDoorToContinueAction(
                                it,
                                lowerOvenViewModel,
                                onDoorCloseEventAction = {
                                    lowerOvenViewModel.recipeExecutionViewModel.resume()
                                }
                            )
                        }
                    }
                }
            }
        }

        /**
         * show more option popup for reheat recipe in programming
         */
        fun showReheatMoreOptionPopup(fragment: Fragment, viewModel: CookingViewModel?) {
            HMILogHelper.Logd(fragment.tag, "${viewModel?.cavityName?.value} showing reheat more option popup")
            val resources = fragment.requireContext().resources

            val gridListTileData: ArrayList<GridListItemModel> = ArrayList()
            gridListTileData.add(GridListItemModel(
                resources.getString(R.string.text_button_auto_cook),
                GridListItemModel.GRID_MORE_OPTIONS_TILE
            ).apply {
                tileImageSrc = R.drawable.ic_auto_cook
                tileSubCategory = AppConstants.MoreOptionsSubCategory.TYPE_AUTO_COOK.toString()
            })

            val item = GridListItemModel(
                resources.getString(R.string.text_sub_mode_power),
                GridListItemModel.GRID_MORE_OPTIONS_TILE
            ).apply {
                tileImageSrc = R.drawable.ic_microwave_power_level
                tileSubCategory = AppConstants.MoreOptionsSubCategory.TYPE_POWER_LEVEL.toString()
            }
            gridListTileData.add(item)
            moreOptionsGenericPopup(
                fragment,
                viewModel,
                gridListTileData,
                null
            )
        }

        fun isHotCavityDoorOpenPopupVisible() : Boolean{
            return hotCavityDoorClosePopup == null
        }
    }
}
