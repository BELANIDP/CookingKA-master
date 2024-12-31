package core.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.activity.KitchenAidLauncherActivity
import android.presenter.adapters.TumblerElement
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import android.presenter.dialogs.MoreOptionsPopupBuilder
import android.presenter.fragments.provisioning.BleConnectViewHolder
import android.presenter.fragments.provisioning.BlePairViewHolder
import android.presenter.fragments.provisioning.CompletedViewHolder
import android.presenter.fragments.provisioning.ErrorViewHolder
import android.presenter.fragments.provisioning.WifiConnectViewHolder
import android.presenter.fragments.service_diagnostic.AutoDiagnosticsListSelection
import android.presenter.fragments.service_diagnostic.AutoDiagnosticsResultListScreen
import android.presenter.fragments.service_diagnostic.AutoDiagnosticsResultsDetailScreen
import android.presenter.fragments.service_diagnostic.AutoDiagnosticsStatusScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsComponentActivationScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsComponentProgrammingScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsComponentStatusScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsEditSystemInfoScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsEntryScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsErrorCodeScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsHomeScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsResetConfirmationScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsResetOptionSelectionScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsSensorDetailsScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsSystemInfoDetailsScreen
import android.presenter.fragments.service_diagnostic.DiagnosticsSystemInfoScreen
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.navOptions
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.common.utils.SettingsUtils
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.model.capability.recipe.options.TemperatureMap
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.FaultSubCategory
import com.whirlpool.hmi.cooking.utils.PowerInterruptState
import com.whirlpool.hmi.cooking.utils.PreheatType
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.diagnostics.models.DiagnosticsManager
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.provisioning.ProvisioningViewModel
import com.whirlpool.hmi.provisioning.manager.ProvisioningManager
import com.whirlpool.hmi.provisioning.ui.fragments.FragmentProvisioningHome
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.settings.SettingsViewModel.TemperatureUnit.FAHRENHEIT
import com.whirlpool.hmi.uicomponents.tools.util.Constants
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.utils.BuildInfo
import com.whirlpool.hmi.utils.CapabilityKeys
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.LogHelper
import com.whirlpool.hmi.utils.TreeNode
import com.whirlpool.hmi.utils.cookbook.records.CookbookRecord
import com.whirlpool.hmi.utils.cookbook.records.FavoriteRecord
import com.whirlpool.hmi.utils.cookbook.records.RecipeRecord
import com.whirlpool.hmi.utils.timers.Timer
import core.utils.AppConstants.COUNT_DOWN_SPANNABLE_TEXT_HOUR_LENGTH_10
import core.utils.AppConstants.COUNT_DOWN_SPANNABLE_TEXT_MINUTE_LENGTH_10
import core.utils.AppConstants.COUNT_DOWN_SPANNABLE_TEXT_MINUTE_LENGTH_60
import core.utils.AppConstants.COUNT_DOWN_SPANNABLE_TEXT_SECOND_LENGTH_10
import core.utils.AppConstants.CYCLE_END_TIME
import core.utils.AppConstants.CYCLE_END_TIME_HOUR
import core.utils.AppConstants.DEFAULT_LEVEL
import core.utils.AppConstants.DEFAULT_MAX_START_TEMPERATURE
import core.utils.AppConstants.DEGREE_SYMBOL
import core.utils.AppConstants.EMPTY_SPACE
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AppConstants.FALSE_CONSTANT
import core.utils.AppConstants.HOT_CAVITY_WARNING_COOLING_DOWN
import core.utils.AppConstants.HOT_CAVITY_WARNING_DESCRIPTION
import core.utils.AppConstants.HOT_CAVITY_WARNING_OFFSET_CELCIUS
import core.utils.AppConstants.HOT_CAVITY_WARNING_OFFSET_FAHRENHEIT
import core.utils.AppConstants.HOT_CAVITY_WARNING_TITLE
import core.utils.AppConstants.PERCENTAGE_SYMBOL
import core.utils.AppConstants.PRIMARY_CAVITY_KEY
import core.utils.AppConstants.RECIPE_CONVECT
import core.utils.AppConstants.RECIPE_MORE_MODES
import core.utils.AppConstants.RECIPE_PROBE
import core.utils.AppConstants.SECONDARY_CAVITY_KEY
import core.utils.AppConstants.SYMBOL_FORWARD_SLASH
import core.utils.AppConstants.TEXT_TEMP
import core.utils.HMIExpansionUtils.Companion.isFastBlinkingKnobTimeoutActive
import core.utils.HMIExpansionUtils.Companion.isSlowBlinkingKnobTimeoutActive
import core.utils.HMILogHelper.Logd
import core.utils.HMILogHelper.Loge
import core.utils.HMILogHelper.Logi
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.SettingsManagerUtils.isUnboxing
import core.utils.faultcodesutils.FaultCodeDetailsJsonParser
import core.utils.faultcodesutils.FaultCodesJsonKeys
import core.utils.faultcodesutils.FaultDetails
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Objects
import kotlin.math.ceil
import kotlin.math.min


class CookingAppUtils {

    companion object {
        @Suppress("unused")
        private const val VOLUME_LEVEL_INTERVAL = 2.5

        private val TAG: String = CookingAppUtils::class.java.simpleName

        private const val PRODUCT_VARIANT_NULL = "Product variant not found!..."
        private lateinit var toolsMenuJsonParser: ToolsMenuJsonParser
        private lateinit var faultCodeDetailsJsonParser: FaultCodeDetailsJsonParser
        private val isOTAComplete = MutableLiveData<Boolean>(false)
        //To identify whether user is in self-clean Flow (remove this variable once the entire spec for
        // exit animation is available)
        private var isSelfCleanFlow = false

        private var isSabbathFlow = false
        private var scrollJob: Job? = null

        private var navigatedFrom = EMPTY_STRING

        private val activeNotificationChanged = MutableLiveData<Boolean>(false)

        private var isSettingsFlow = false

        private var isRestoreFactoryStared = false

        private var isErrorPresentOnHMI = false
        val cookingGuideList = mutableListOf<String>()

        private var progressBarDetails:Pair<Boolean,Boolean> ?= null

        private var timeHasBeenSet = false

        private var historyIdToRemove = -1

        private var wasRecipeAQuickStart = false

        private var connectToNwIsToBeTriggered = false

        private var dateHasBeenSet = false

        /*---------------------------------------------------------------------------------------------------------------*/
        fun loadToolsStructureJson(context: Context?) {
            toolsMenuJsonParser = ToolsMenuJsonParser()
            if (context?.let {
                    toolsMenuJsonParser.loadJson(
                        it,
                        ToolsMenuJsonKeys.TOOLS_MENU_JSON_FILE_NAME
                    )
                } == true
            ) {
                Logd("Successful in loading the Tools Menu")
            } else {
                Loge("Loading of JSON File not successful")
            }
        }

        /**
         * Load Fault codes from fault json file
         * @param context The view context
         */
        fun loadFaultCodesJson(context: Context) {
            faultCodeDetailsJsonParser = FaultCodeDetailsJsonParser()
            if (faultCodeDetailsJsonParser.loadJson(
                    context,
                    FaultCodesJsonKeys.FAULTS_JSON_FILE_NAME
                )
            ) {
                //parse the fault data from the json file
                faultCodeDetailsJsonParser.parseFaultListIntoCategory()
                Logd("Successful in loading the fault codes")
            } else {
                Loge("Loading of fault codes JSON File not successful")
            }
        }

        /**
         * Sets the kitchen count down spannable text on a TextView.
         *
         * @param view        The TextView on which to set the spannable text.
         * @param time        The time value.
         * @param kitchenTime The kitchen time string.
         */
        @Suppress("unused")
        fun setKTCountDownSpannableText(view: TextView, time: Int, kitchenTime: String) {
            var startDigit = 0
            var endDigit = 0
            val selectedTime = SettingsUtils.convertTime(time.toLong())
            if (selectedTime.hours < COUNT_DOWN_SPANNABLE_TEXT_HOUR_LENGTH_10 && selectedTime.hours != 0) {
                if (kitchenTime.substring(0, 1).toInt() == 0) {
                    startDigit = 0
                    endDigit = 1
                }
            }
            if (selectedTime.hours == 0 && selectedTime.minutes < COUNT_DOWN_SPANNABLE_TEXT_MINUTE_LENGTH_60 && selectedTime.minutes != 0) {
                if (kitchenTime.substring(0, 1).toInt() == 0) {
                    startDigit = 0
                    endDigit = 2
                }
            }
            if (selectedTime.hours == 0 && selectedTime.minutes < COUNT_DOWN_SPANNABLE_TEXT_MINUTE_LENGTH_10 && selectedTime.minutes != 0) {
                if (kitchenTime.substring(0, 1).toInt() == 0) {
                    startDigit = 0
                    endDigit = 4
                }
            }
            if (selectedTime.hours == 0 && selectedTime.minutes == 0 && selectedTime.seconds != 0) {
                if (kitchenTime.substring(0, 2)
                        .toInt() == 0 && selectedTime.seconds >= COUNT_DOWN_SPANNABLE_TEXT_SECOND_LENGTH_10
                ) {
                    startDigit = 0
                    endDigit = 5
                } else if (selectedTime.seconds < COUNT_DOWN_SPANNABLE_TEXT_SECOND_LENGTH_10 && selectedTime.seconds != 0) {
                    startDigit = 0
                    endDigit = 7
                }
            }
            setSpannableText(view, kitchenTime, startDigit, endDigit)
        }

        /**
         * Method to set the different color for the Seconds in the cook time numPad
         *
         * @param view  Text View
         * @param text  text to set
         * @param start start of the text
         * @param end   end of the text
         */
        private fun setSpannableText(view: TextView, text: String?, start: Int, end: Int) {
            val spannableString = SpannableStringBuilder(text)
            spannableString.setSpan(
                ForegroundColorSpan(Color.GRAY),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
            view.text = spannableString
        }

        fun getToolsItemsForKey(key: String): ArrayList<String>? {
            return toolsMenuJsonParser.parseToolsItemsJsonForKey(key)
        }

        /**
         * Method to know cook time is available or not
         *
         * @param cookingViewModel view model to access current recipe data
         * @return true/ false
         */
        fun isCookTimeAvailable(cookingViewModel: CookingViewModel?): Boolean {
            return (cookingViewModel?.recipeExecutionViewModel != null
                    && cookingViewModel.recipeExecutionViewModel?.cookTime?.value != null
                    && (cookingViewModel.recipeExecutionViewModel.cookTime.value ?: 0) > 0)
        }

        /**
         * Method to check whether power level option is available for the recipe or not
         */
        fun isRecipeOptionAvailable(
            recipeExecutionViewModel: RecipeExecutionViewModel,
            recipeOptions: RecipeOptions,
        ): Boolean {
            val requireOptions = recipeExecutionViewModel.requiredOptions.value
            val optionalOptions = recipeExecutionViewModel.optionalOptions.value
            return requireOptions != null && requireOptions.contains(recipeOptions) || optionalOptions != null && optionalOptions.contains(
                recipeOptions
            )
        }

        fun getTimeInHours(durationInSeconds: Long): String {
            val durationListInHours: String
            val tDouble = durationInSeconds / 3600.0
            durationListInHours = if (tDouble % 1 == 0.0) {
                tDouble.toInt().toString()
            } else {
                tDouble.toInt().toString()
            }
            return durationListInHours
        }

        /**
         * Method to check is SelfClean/Pyro cycle is allowed
         */
        fun isPyroAllowed(cookingViewModel: CookingViewModel): Boolean {
            val pyroAllowedInSecs =
                cookingViewModel.recipeExecutionViewModel.pyroCleanNotAllowedUntilTimeRemaining.value
            return if (pyroAllowedInSecs == null) {
                true // Pyro allowed if remaining time is not available
            } else {
                pyroAllowedInSecs <= 0 // Pyro allowed if remaining time is less than or equal to 0 seconds
            }
        }

        /**
         * If SelfClean/Pyro cycle is not allowed then show Time strings
         */
        fun getPyroNotAllowedMessage(context: Context, allowedSeconds: Long?): String {
            val pyroNotAllowedString: String
            val hours = (allowedSeconds!! / 3600).toInt()
            val mins = (allowedSeconds / 60 - hours * 60).toInt()
            val secs = (allowedSeconds - hours * 3600 - mins * 60).toInt()
            pyroNotAllowedString = if (hours > 0) {
                context.getString(
                    R.string.text_self_clean_available_in_hours,
                    (hours.toString() + EMPTY_SPACE + context.getString(
                        R.string.text_label_hour
                    ))
                )
            } else if ( mins > 0) {
                context.getString(
                    R.string.text_self_clean_available_in_hours,
                    (mins.toString() + EMPTY_SPACE + context.getString(
                        R.string.text_label_minutes
                    ))
                )
            } else {
                context.getString(
                    R.string.text_self_clean_available_in_hours,
                    (secs.toString() + EMPTY_SPACE + context.getString(
                        R.string.text_label_seconds
                    ))
                )
            }
            return pyroNotAllowedString
        }

        fun dismissDialogAndNavigateToStatusOrClockScreen(fragment: Fragment): Boolean {
            if (null == CookingViewModelFactory.getInScopeViewModel()) {
                navigateSafely(
                    fragment,
                    R.id.global_action_to_clockScreen,
                    null,
                    null
                )
            } else {
                val inScopeViewModel =
                    CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                when (CookingViewModelFactory.getProductVariantEnum()) {
                    CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN, CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> if (RecipeExecutionState.IDLE != inScopeViewModel.recipeExecutionState.value) {
                        // Cavity is Running or Delayed.
                        return false
                    } else {
                        navigateSafely(
                            fragment,
                            R.id.global_action_to_clockScreen,
                            null,
                            null
                        )
                    }

                    CookingViewModelFactory.ProductVariantEnum.COMBO, CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                        val outScopeViewModel =
                            CookingViewModelFactory.getOutOfScopeCookingViewModel().recipeExecutionViewModel
                        val inScopeExecutionState = inScopeViewModel.recipeExecutionState.value
                        val outScopeExecutionState = outScopeViewModel.recipeExecutionState.value
                        if (RecipeExecutionState.IDLE != outScopeExecutionState &&
                            RecipeExecutionState.IDLE != inScopeExecutionState
                        ) {
                            return false
                        } else if (RecipeExecutionState.IDLE != inScopeExecutionState ||
                            RecipeExecutionState.IDLE != outScopeExecutionState
                        ) {  // Either of 1 cavity is running or delayed
                            return false
                        } else {
                            navigateSafely(
                                fragment,
                                R.id.global_action_to_clockScreen,
                                null,
                                null
                            )
                        }
                    }

                    else -> Loge(PRODUCT_VARIANT_NULL)
                }
            }
            return true
        }


        /**
         * Checks if the current visible fragment is Clock Screen
         *
         * @return true if current visible fragment is Clock Screen, else false
         */
        fun isClockScreen(fragmentManager: FragmentManager): Boolean {
            val fragment = getVisibleFragment(fragmentManager)
            if (fragment != null) {
                val currentScreenId =
                    fragmentManager.primaryNavigationFragment?.requireView()?.let {
                        Navigation.findNavController(
                            it
                        ).currentDestination?.id
                    } //
                return currentScreenId == R.id.clockFragment
            }
            return false
        }

        /**
         * Method to get the current visible fragment.
         *
         * @return the current visible fragment. Returns null if there are no
         * visible fragments.
         */
        fun getVisibleFragment(fragmentManager: FragmentManager?): Fragment? {
            val fragments =
                fragmentManager?.findFragmentById(R.id.fragmentContainerView)?.childFragmentManager?.fragments
            if (fragments != null) {
                for (fragment in fragments) {
                    if (fragment != null && fragment.isVisible) return fragment
                }
            }
            return null
        }

        /**
         * method to prepare and start self clean
         *
         * @param fragment    current visible fragment
         * @param isFromDelay true if flow is from delay
         */
        fun prepareOvenAndStartSelfClean(fragment: Fragment, isFromDelay: Boolean) {
            val pyroExecutionViewModel =
                CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
            val response =
                if (isFromDelay) pyroExecutionViewModel.startDelay() else pyroExecutionViewModel.executePyro()
            if (!response.isError) {
                navigateSafely(
                    fragment,
                    R.id.door_locking_fragment,
                    null,
                    null
                )
            } else {
                Loge("SDK: executePyro() failed")
                PopUpBuilderUtils.runningFailPopupBuilder(fragment)
            }
        }

        /**
         * provides startDelay or executePyro functionalities based on isFromDelay flag
         * @param isFromDelay true or false for delay to run or not
         */
        fun startSelfClean(isFromDelay: Boolean) {
            val errorResponse: RecipeErrorResponse
            val pyroExecutionViewModel =
                CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
            errorResponse = if (isFromDelay) {
                pyroExecutionViewModel.startDelay()
            } else {
                pyroExecutionViewModel.executePyro()
            }
            if (errorResponse.isError) {
                Loge("SDK: executePyro() failed")
            }
        }

        /**
         * This method returns the index at which the new item should be inserted. If the `newTemp`
         * is already present in the list of temperature values, then its index is returned.
         *
         * @param newTemp New value to insert in the existing list.
         * @param range   IntegerRange range represented in integer values for example temperature
         * @return Index at which the new item should be added; -1 if the new value is not within the accepted range.
         */
        fun getIndexForNewItemFromIntegerRange(range: IntegerRange, newTemp: Int): Int {
            return if (newTemp >= range.min && newTemp <= range.max) {    // newTemp lies within the accepted range.
                ceil(((newTemp - range.min) / range.step.toFloat()).toDouble()).toInt()
            } else {
                Constants.NOT_IMPLEMENTED
            }
        }

        /**
         * @return Current cavity state as defined by [CavityLightUtils.CavityStateEnum].
         */
        fun getCavityState(): Enum<*> {
            val inScopeViewModel = CookingViewModelFactory.getInScopeViewModel()
                ?: return CavityLightUtils.CavityStateEnum.CAVITY_STATE_NONE_RUNNING
            val primaryExecutionState =
                inScopeViewModel.recipeExecutionViewModel.recipeExecutionState.value
            return if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN || CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN
            ) {
                if (primaryExecutionState == RecipeExecutionState.IDLE) {
                    CavityLightUtils.CavityStateEnum.CAVITY_STATE_NONE_RUNNING
                } else {
                    CavityLightUtils.CavityStateEnum.CAVITY_STATE_PRIMARY_RUNNING
                }
            } else {
                val secondaryExecutionState =
                    CookingViewModelFactory.getOutOfScopeCookingViewModel().recipeExecutionViewModel.recipeExecutionState.value
                if (primaryExecutionState == RecipeExecutionState.IDLE && secondaryExecutionState == RecipeExecutionState.IDLE
                ) {
                    CavityLightUtils.CavityStateEnum.CAVITY_STATE_NONE_RUNNING
                } else if (primaryExecutionState != RecipeExecutionState.IDLE
                    && secondaryExecutionState != RecipeExecutionState.IDLE
                ) {
                    CavityLightUtils.CavityStateEnum.CAVITY_STATE_BOTH_RUNNING
                } else if (primaryExecutionState != RecipeExecutionState.IDLE) {
                    CavityLightUtils.CavityStateEnum.CAVITY_STATE_PRIMARY_RUNNING
                } else {
                    CavityLightUtils.CavityStateEnum.CAVITY_STATE_SECONDARY_RUNNING
                }
            }
        }

        /**
         * Dismiss all DialogFragments added to given FragmentManager and child fragments
         */
        fun dismissAllDialogs(manager: FragmentManager) {
            HMILogHelper.Logd("------ Warning - > Dismissing the all dismissAllDialogs -----")
            val handler = Handler(Looper.getMainLooper())
            val fragments = manager.fragments
            for (fragment in fragments) {
                if (fragment is DialogFragment) {
                    handler.postDelayed(
                        { fragment.dismissAllowingStateLoss() },
                        AppConstants.POPUP_DISMISS_DELAY.toLong()
                    )
                }
                if (!fragment.isAdded) return
                val childFragmentManager = fragment.childFragmentManager
                dismissAllDialogs(childFragmentManager)
            }
        }

        /**
         * Method to close all available dialogs in current visible fragment
         * @param popupReferenceFragment current Popup reference
         */
        fun closeAllVisibleDialogs(popupReferenceFragment: Fragment?) {
            var fragments: List<Fragment?> = emptyList<Fragment>()
            val lastVisibleFragment = NavigationUtils.getVisibleFragment()
            if (popupReferenceFragment != null) {
                fragments = popupReferenceFragment.parentFragmentManager.fragments
                fragments.addAll(popupReferenceFragment.childFragmentManager.fragments)

                val visibleFragment: Fragment =
                    getVisibleFragment(popupReferenceFragment.parentFragmentManager)!!
                if (visibleFragment != null) {
                    fragments.addAll(visibleFragment.parentFragmentManager.fragments)
                    fragments.addAll(visibleFragment.childFragmentManager.fragments)
                }

                if (lastVisibleFragment != null) {
                    fragments.addAll(
                        lastVisibleFragment.getParentFragmentManager().fragments
                    )
                    fragments.addAll(
                        lastVisibleFragment.getChildFragmentManager().fragments
                    )
                }
            } else {
                if (lastVisibleFragment != null) {
                    lastVisibleFragment.getParentFragmentManager().fragments
                    lastVisibleFragment.getChildFragmentManager().fragments
                }
            }

            for (fragment in fragments) {
                if (fragment is DialogFragment) {
                    fragment.dismiss()
                }
            }
        }

        /**
         * to check if recipe is in user instruction phase or not
         * displaying a popup will be handle separately by Status screen binding this is to handle some scenario like not displaying dialogs if it is showing
         * @return true if user instruction is showing
         */
        fun isUserInstructionRequired(cookingVM: CookingViewModel): Boolean {
            val userInstruction = cookingVM.recipeExecutionViewModel.userInstruction.value
            if (userInstruction?.text?.isNotEmpty() == true && userInstruction.isTypeEnumeration && !(userInstruction.containsExtraBrowningPrompt() || userInstruction.containsTimePrompt())) {
                Logd("userInstruction", "mwo door opens but userInstruction is showing, skipping mwoDoorOpenPopup")
                return true
            }
            return false
        }

        /**
         * To check in settings which Temperature unit is assigned
         *
         * @return true if F else C
         */
        fun isFAHRENHEITUnitConfigured(): Boolean {
            val localTemperatureUnit =
                SettingsViewModel.getSettingsViewModel().temperatureUnit.value
            return localTemperatureUnit == FAHRENHEIT
        }

        private const val DEFAULT_QUICK_TEMPERATURE_FAHRENHEIT_VALUE = 350.00
        private const val DEFAULT_QUICK_TEMPERATURE_CELCIUS_VALUE = 176.667

        /**
         * Based on app setting quick start values is assigned
         */
        @Suppress("unused")
        fun getQuickStartTemperatureValue(): Double {
            if (isFAHRENHEITUnitConfigured())
                return DEFAULT_QUICK_TEMPERATURE_FAHRENHEIT_VALUE
            return DEFAULT_QUICK_TEMPERATURE_CELCIUS_VALUE
        }

        fun navigateToStatusOrClockScreen(fragment: Fragment) {
            var moveToClock = false
            if(isSabbathMode()) {
                Logd(fragment.tag, "Sabbath mode is enabled, using navigateToSabbathStatusOrClockScreen function")
                navigateToSabbathStatusOrClockScreen(fragment)
                return
            }
            if(isPyroliticClean()) {
                val recipeCookingState = CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel?.recipeCookingState?.value

                recipeCookingState?.let { state ->
                    when (state) {
                        RecipeCookingState.CLEANING, RecipeCookingState.COOLING -> {
                            Logd(fragment.tag, "Self Clean is enabled, navigating to self clean status")
                            navigateSafely(fragment, R.id.global_action_to_self_clean_status, null, null)
                            return
                        }
                        else -> {
                            Logd(fragment.tag, "Self clean NO Cavities are RUNNING, navigating to global_action_to_clockScreen")
                            navigateSafely(
                                fragment, R.id.global_action_to_clockScreen, null, null
                            )
                            return
                        }
                    }
                }
                return
            }
            if (CookingViewModelFactory.getInScopeViewModel() == null) {
                moveToClock = true
            } else {
                val inScopeViewModel =
                    CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                when (CookingViewModelFactory.getProductVariantEnum()) {
                    CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
                    CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN,
                    -> {
                        if (RecipeExecutionState.IDLE != inScopeViewModel.recipeExecutionState.value) {
                            // Cavity is Running or Delayed.
                            moveToSingleStatusOrClockScreen(fragment)
                        } else {
                            moveToClock = true
                        }
                    }

                    else -> {
                        val outScopeViewModel =
                            CookingViewModelFactory.getOutOfScopeCookingViewModel().recipeExecutionViewModel
                        val inScopeExecutionState = inScopeViewModel.recipeExecutionState.value
                        val outScopeExecutionState = outScopeViewModel.recipeExecutionState.value
                        if (RecipeExecutionState.IDLE != outScopeExecutionState && RecipeExecutionState.IDLE != inScopeExecutionState) {
                            navigateSafely(
                                fragment, R.id.global_action_to_double_status_screen, null, null
                            )
                        } else if (RecipeExecutionState.IDLE != inScopeExecutionState || RecipeExecutionState.IDLE != outScopeExecutionState) {  // Either of 1 cavity is running or delayed
                            if (RecipeExecutionState.IDLE == inScopeExecutionState) {
                                //Set Non idle cavity as In Scope, if current Inscope is Idle
                                CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getOutOfScopeCookingViewModel())
                            }
                            moveToSingleStatusOrClockScreen(fragment)
                        } else {
                            moveToClock = true
                        }
                    }
                }
            }
            if (moveToClock) {
                fragment.activity?.supportFragmentManager?.let { dismissAllDialogs(it) }
                navigateSafely(
                    fragment, R.id.global_action_to_clockScreen, null, null
                )
            }
        }

        /**
         * navigate to sabbath status screen if any of the cavity running
         * else move to clock screen
         */
        fun navigateToSabbathStatusOrClockScreen(fragment: Fragment) {
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                    val primaryVM = CookingViewModelFactory.getPrimaryCavityViewModel()
                    val secondaryVM = CookingViewModelFactory.getSecondaryCavityViewModel()
                    if (primaryVM.recipeExecutionViewModel.isRunning && secondaryVM.recipeExecutionViewModel.isRunning) {
                        Logd(fragment.tag, "Sabbath primary and secondary cavities are running, navigating to global_action_to_double_sabbath_status_screen")
                        navigateSafely(
                            fragment, R.id.global_action_to_double_sabbath_status_screen, null, null
                        )
                        return
                    }
                    if (primaryVM.recipeExecutionViewModel.isRunning) {
                        Logd(fragment.tag, "Sabbath primary is running, navigating to global_action_to_single_sabbath_status_screen")
                        navigateSafely(
                            fragment, R.id.global_action_to_single_sabbath_status_screen, null, null
                        )
                        return
                    }
                    if (secondaryVM.recipeExecutionViewModel.isRunning) {
                        Logd(fragment.tag, "Sabbath secondary is running, navigating to global_action_to_single_sabbath_lower_status_screen")
                        navigateSafely(
                            fragment, R.id.global_action_to_single_sabbath_lower_status_screen, null, null
                        )
                        return
                    }
                }
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    val secondaryVM = CookingViewModelFactory.getSecondaryCavityViewModel()
                    if (secondaryVM.recipeExecutionViewModel.isRunning) {
                        Logd(fragment.tag, "Sabbath secondary is running, navigating to global_action_to_single_sabbath_lower_status_screen")
                        navigateSafely(
                            fragment, R.id.global_action_to_single_sabbath_lower_status_screen, null, null
                        )
                        return
                    }
                }
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                    val primaryVM = CookingViewModelFactory.getPrimaryCavityViewModel()
                    if (primaryVM.recipeExecutionViewModel.isRunning) {
                        Logd(fragment.tag, "Sabbath primary is running, navigating to global_action_to_single_sabbath_status_screen")
                        navigateSafely(
                            fragment, R.id.global_action_to_single_sabbath_status_screen, null, null
                        )
                        return
                    }
                }

                else -> {
                    Loge(fragment.tag, "Sabbath Mode is enable but NOT applicable to MICROWAVE variant")
                }
            }
            Logd(fragment.tag, "Sabbath NO Cavities are RUNNING, navigating to global_action_to_clockScreen")
            navigateSafely(
                fragment, R.id.global_action_to_clockScreen, null, null
            )
        }


        private fun moveToSingleStatusOrClockScreen(fragment: Fragment) {
            if (CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.IDLE
                && CookingViewModelFactory.getInScopeViewModel().isSecondaryCavity
            ) {
                navigateSafely(
                    fragment,
                    R.id.global_action_to_single_lower_status_screen,
                    null,
                    null
                )
                return
            }
            navigateSafely(
                fragment,
                R.id.global_action_to_single_status_screen,
                null,
                null
            )
        }

        /** Gets the resource ID and returns the string
         *
         * @param context   - The view context
         * @param resourceId The resource id for which the string needs to be retrieved
         * @return Returns the string for that resource
         */
        fun getStringFromResourceId(context: Context, resourceId: Int): String {
            return context.resources.getString(resourceId)
        }

        /**
         * Method to set helper text and color.
         *
         * @param helperTextView, helper text view.
         * @param textColor,      color of helper text.
         */
        fun setHelperTextColor(
            helperTextView: TextView?,
            @NumPadHelperTextColor textColor: Int,
        ) {
            if (NumPadHelperTextColor.NORMAL_TEXT_COLOR == textColor) {
                helperTextView?.setTextColor(
                    helperTextView.resources.getColor(R.color.subtext_light_grey, null)
                )
            } else if (NumPadHelperTextColor.ERROR_TEXT_COLOR == textColor) {
                helperTextView?.setTextColor(
                    helperTextView.resources.getColor(R.color.notification_red, null)
                )
            }
        }

        /**
         * Method to append the units in the cook timer string
         *
         * @param cookTimeText cook timer string without units in format (eg: 000000)
         * @param context      context to get the string
         * @param isMicrowave  true if it is microwave cook timer
         * @return output in the format if microwave "00m 00s" else "00h 00s"
         */
        @Suppress("unused")
        fun appendUnitsInCookTimerString(
            cookTimeText: String?,
            context: Context, isMicrowave: Boolean,
        ): String {
            var timerValue: String = EMPTY_STRING
            if (cookTimeText != null && cookTimeText.length > 3) {
                timerValue = if (isMicrowave) {
                    cookTimeText.substring(0, 2) + context.getString(R.string.text_label_M) +
                            cookTimeText.substring(
                                2,
                                4
                            ) + context.getString(R.string.text_label_S)
                } else {
                    if (cookTimeText.length <= 4 || cookTimeText == AppConstants.DEFAULT_COOK_TIME) {
                        cookTimeText.substring(0, 2) + context.getString(R.string.text_label_H) +
                                cookTimeText.substring(
                                    2,
                                    4
                                ) + context.getString(R.string.text_label_M)
                    } else if (cookTimeText.length <= 5) {
                        cookTimeText.substring(0, 2) + context.getString(R.string.text_label_H) +
                                cookTimeText.substring(
                                    2,
                                    4
                                ) + context.getString(R.string.text_label_M)
                    } else {
                        cookTimeText.substring(0, 2) + context.getString(R.string.text_label_H) +
                                cookTimeText.substring(
                                    2,
                                    4
                                ) + context.getString(R.string.text_label_M) +
                                cookTimeText.substring(
                                    4,
                                    6
                                ) + context.getString(R.string.text_label_S)
                    }
                }
            }
            return timerValue
        }

        /**
         * Method to append the units in the cook timer string
         *
         * @param cookTimeText cook timer string without units in format (eg: 000000)
         * @param context      context to get the string
         * @param isMicrowave  true if it is microwave cook timer
         * @return output in the format if microwave "00m 00s" else "00h 00s"
         */
        fun appendUnitsInCookTimerHourAndMinutesString(
            cookTimeText: String?,
            context: Context, isMicrowave: Boolean,
        ): String {
            var timerValue: String = EMPTY_STRING
            if (cookTimeText != null && cookTimeText.length > 3) {
                timerValue = if (isMicrowave) {
                    cookTimeText.substring(0, 2) + context.getString(R.string.text_label_M) +
                            cookTimeText.substring(
                                2,
                                4
                            ) + context.getString(R.string.text_label_S)
                } else {
                    if (cookTimeText.length <= 4 || cookTimeText == AppConstants.DEFAULT_COOK_TIME) {
                        cookTimeText.substring(0, 2) + context.getString(R.string.text_label_H) +
                                cookTimeText.substring(
                                    2,
                                    4
                                ) + context.getString(R.string.text_label_M)
                    } else if (cookTimeText.length <= 5) {
                        cookTimeText.substring(0, 2) + context.getString(R.string.text_label_H) +
                                cookTimeText.substring(
                                    2,
                                    4
                                ) + context.getString(R.string.text_label_M)
                    } else {
                        cookTimeText.substring(0, 2) + context.getString(R.string.text_label_H) +
                                cookTimeText.substring(
                                    2,
                                    4
                                ) + context.getString(R.string.text_label_M)
                    }
                }
            }
            return timerValue
        }

        /**
         * Method to calculate the timer in seconds from the time string
         *
         * @param cookTimeString : time string of format (00h00m00s)
         * @return total cook timer in seconds
         */
        fun getCookTimerStringAsSeconds(cookTimeString: String?, isMicrowaveCase: Boolean): Int {
            if (cookTimeString != null && cookTimeString.length > 3) {
                val hours: Int =
                    parseWithIntegerDefault(
                        cookTimeString.substring(0, 2),
                        0
                    )
                var minutes: Int =
                    parseWithIntegerDefault(
                        cookTimeString.substring(2, 4),
                        0
                    )
                val seconds: Int
                return if (isMicrowaveCase) {
                    hours * 3600 + minutes * 60
                } else {
                    if (cookTimeString.length > 5) {
                        seconds =
                            parseWithIntegerDefault(
                                cookTimeString.substring(4, 6),
                                0
                            )
                        hours * 3600 + minutes * 60 + seconds
                    } else {
                        minutes =
                            parseWithIntegerDefault(
                                cookTimeString.substring(0, 2),
                                0
                            )
                        seconds =
                            parseWithIntegerDefault(
                                cookTimeString.substring(2, 4),
                                0
                            )
                        minutes * 60 + seconds
                    }
                }
            }
            return 0
        }

        /**
         * Given string value to Integer parsing
         *
         * @param number     string to Integer value
         * @param defaultVal default value if number not valid
         * @return int value
         */
        @Suppress("SameParameterValue")
        private fun parseWithIntegerDefault(number: String, defaultVal: Int): Int {
            return try {
                number.toInt()
            } catch (e: NumberFormatException) {
                defaultVal
            }
        }


        /**
         * To check whether the cookTime option is Mandatory or not
         *
         * @param cookingViewModel current View model to get the required option
         * @return true/false
         */
        fun isCookTimeOptionMandatory(cookingViewModel: CookingViewModel): Boolean {
            if (cookingViewModel.recipeExecutionViewModel == null || cookingViewModel.recipeExecutionViewModel.requiredOptions == null) return false
            val recipeOptions = cookingViewModel.recipeExecutionViewModel.requiredOptions.value
            return !recipeOptions.isNullOrEmpty() && recipeOptions.contains(
                RecipeOptions.COOK_TIME
            )
        }

        /**
         * To check whether the recipe has Time based preheat option or not
         *
         * @return true means recipe will progress based on Time preHeat data, false otherwise on Temperature
         */
        fun isTimeBasedPreheatRecipe(cookingVM: CookingViewModel?): Boolean {
            return cookingVM?.recipeExecutionViewModel?.preheatType == PreheatType.TIME_BASED_PREHEAT
        }

        /**
         * To check if a Time based preheat recipe is running its preheat timer or not
         *  Until, preheat is done the cooTimeRemaining will be published as time preheat timer remaining and starting CookTime is not allowed
         * @return true/false
         */
        fun isTimePreheatRunning(cookingVM: CookingViewModel?): Boolean {
            if(isTimeBasedPreheatRecipe(cookingVM)){
                val recipeCookingState = cookingVM?.recipeExecutionViewModel?.recipeCookingState?.value
                val cookTimerState = cookingVM?.recipeExecutionViewModel?.cookTimerState?.value
                if(recipeCookingState == RecipeCookingState.PREHEATING && (cookTimerState == Timer.State.RUNNING || cookTimerState == Timer.State.PAUSED)){
                    return true
                }
                if(recipeCookingState == RecipeCookingState.COOKING && cookTimerState == Timer.State.PAUSED && cookingVM.recipeExecutionViewModel.cookTime.value == 0L){
                    return true
                }
                if((recipeCookingState == RecipeCookingState.COOKING || recipeCookingState == RecipeCookingState.PREHEATING) && cookTimerState == Timer.State.COMPLETED && cookingVM.recipeExecutionViewModel.cookTime.value == 0L){
                    return true
                }
            }
            Logd("timePreheat", "${cookingVM?.cavityName?.value} has TimePreheat NOT RUNNING")
            return false
        }

        /**
         * To check if a Time based preheat is just completed similar to preheatComplete since time based preheat recipes are using same timer
         * once preheat is completed cookTimeState is coming as completed and flipping to paused later so to escape this if
         * recipeCookingState is preheating then it means preheat is completed
         * @return true/false
         */
        fun isTimePreheatJustCompleted(cookingVM: CookingViewModel?): Boolean {
            return cookingVM?.recipeExecutionViewModel?.cookTimerState?.value == Timer.State.COMPLETED && isTimeBasedPreheatRecipe(
                cookingVM
            ) && cookingVM.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.PREHEATING
        }

        /**
         * Method to check whether power level option is available for the recipe or not
         */
        fun isCookTimeOptionAvailable(cookingVM: CookingViewModel?): Boolean {
            val requireOptions = cookingVM?.recipeExecutionViewModel?.requiredOptions?.value
            val optionalOptions = cookingVM?.recipeExecutionViewModel?.optionalOptions?.value
            return (requireOptions != null && requireOptions.contains(RecipeOptions.COOK_TIME)) || (optionalOptions != null && optionalOptions.contains(
                RecipeOptions.COOK_TIME
            ))
        }

        /**
         * Method handle recipe execution errors
         *
         * @param fragment         current Fragment for navigation
         * @param cookingViewModel CookingViewModel
         * @param isSabbathError   true/false
         */
        fun handleCookingError(
            fragment: Fragment,
            cookingViewModel: CookingViewModel?,
            error: RecipeErrorResponse,
            isSabbathError: Boolean,
            onMeatProbeConditionMet: () -> Unit = {}
        ) {
            Loge("error: $error")
            when (error) {
                RecipeErrorResponse.ERROR_RECIPE_START_NOT_ALLOWED -> cookingViewModel?.let {
                    PopUpBuilderUtils.hotCavityCoolDownPopupBuilder(
                        fragment,
                        it,
                        getHotCavityTitleAndDescription(fragment,it, HOT_CAVITY_WARNING_COOLING_DOWN, HOT_CAVITY_WARNING_TITLE),
                        getHotCavityTitleAndDescription(fragment,it, HOT_CAVITY_WARNING_COOLING_DOWN, HOT_CAVITY_WARNING_DESCRIPTION),
                        cookingIsAllowed = false,
                        false,
                        isSabbathError
                    )
                }

                RecipeErrorResponse.ERROR_RECIPE_START_NOT_RECOMMENDED -> cookingViewModel?.let {
                    PopUpBuilderUtils.hotCavityCoolDownPopupBuilder(
                        fragment,
                        it,
                        getHotCavityTitleAndDescription(fragment,it, HOT_CAVITY_WARNING_COOLING_DOWN, HOT_CAVITY_WARNING_TITLE),
                        getHotCavityTitleAndDescription(fragment,it, HOT_CAVITY_WARNING_COOLING_DOWN, HOT_CAVITY_WARNING_DESCRIPTION),
                        cookingIsAllowed = true,
                        false,
                        isSabbathError
                    )
                }

                RecipeErrorResponse.ERROR_MWO_RECIPE_DOOR_IS_NOT_CLOSED, RecipeErrorResponse.ERROR_RECIPE_DOOR_IS_NOT_CLOSED -> {
                    cookingViewModel?.let {
                        DoorEventUtils.cycleStartDoorOpenPopup(
                            fragment,
                            it,
                            false,
                            onMeatProbeConditionMet
                        )
                    }
                }

                RecipeErrorResponse.ERROR_MEAT_PROBE_NOT_INSERTED -> {
                    cookingViewModel?.let {
                        PopUpBuilderUtils.insertMeatProbe(fragment,
                            it, onMeatProbeConditionMet)
                    }
                }

                RecipeErrorResponse.ERROR_DOOR_OPEN_CLOSE_ACTION_NEEDED -> {
//                    DoorEventUtils.startMicrowaveRecipeOrShowPopup(fragment, cookingViewModel)
                }

                else -> if (!isSabbathError) {
                    cookingViewModel?.recipeExecutionViewModel?.cancel()
                    PopUpBuilderUtils.runningFailPopupBuilder(fragment)
                } else {
                    //Showing toast in case of error during sabbath cooking.
                    Toast.makeText(fragment.requireContext(), error.description, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        /**
         * Retrieves the title and description for the hot cavity warning based on the product variant,
         * the state of the cavity, and the type of warning text.
         *
         * @param fragment The fragment used to access the context and resources.
         * @param cookingViewModel The view model containing the cooking state.
         * @param state The current state of the cavity (e.g., cooling down).
         * @param text The type of warning text (e.g., HOT_CAVITY_WARNING_TITLE).
         * @return The appropriate string resource based on the provided parameters.
         */
        fun getHotCavityTitleAndDescription(
            fragment: Fragment,
            cookingViewModel: CookingViewModel,
            state: String,
            text: String
        ): String {
            val context = fragment.requireContext() // Get the context from the fragment

            // Mapping of product variants to cavity names
            val cavityMap = mapOf(
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN to listOf(
                    R.string.cavity_selection_upper_oven,
                    R.string.cavity_selection_lower_oven
                ),
                CookingViewModelFactory.ProductVariantEnum.COMBO to listOf(
                    R.string.microwave ,
                    R.string.cavity_selection_lower_oven
                ),
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN to listOf(
                    R.string.cavity_selection_oven
                ),
                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN to listOf(
                    R.string.microwave
                )
            )

            // Retrieve the appropriate cavity name based on the product variant and cavity state
            val productVariant = CookingViewModelFactory.getProductVariantEnum()
            val cavityNames = cavityMap[productVariant] ?: return ""

            val cavityNameResId = if (cookingViewModel.isPrimaryCavity && cavityNames.size > 1) {
                cavityNames[0] // Upper oven or primary cavity
            } else {
                cavityNames.last() // Lower oven or single cavity
            }

            // Determine the appropriate string resource to return based on the text and state
            return when {
                text == HOT_CAVITY_WARNING_TITLE -> {
                    when (state) {
                        HOT_CAVITY_WARNING_COOLING_DOWN -> context.getString(
                            R.string.text_header_cooling_down, context.getString(cavityNameResId)
                        )
                        else -> context.getString(
                            R.string.text_header_oven_ready, context.getString(cavityNameResId)
                        )
                    }
                }
                else -> {
                    when (state) {
                        HOT_CAVITY_WARNING_COOLING_DOWN -> {
                            if (productVariant == CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN) {
                                context.getString(R.string.text_hot_cavity_oven_cooling_food_inside_message_single_cavity, context.getString(cavityNameResId))
                            } else {
                                context.getString(R.string.text_hot_cavity_oven_cooling_food_inside_message_double_cavity, context.getString(cavityNameResId))
                            }
                        }
                        else -> {
                            if (productVariant == CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN) {
                                context.getString(R.string.text_hot_cavity_oven_ready_message_single_cavity, context.getString(cavityNameResId))
                            } else {
                                context.getString(R.string.text_hot_cavity_oven_ready_message_double_cavity, context.getString(cavityNameResId))
                            }
                        }
                    }
                }
            }
        }

        /**
         * Method to initialize Service Diagnostics View Providers
         */
        fun initializeServiceDiagnosticsViewProviders(kitchenAidLauncherActivity: KitchenAidLauncherActivity) {
            val diagnosticsManager = DiagnosticsManager.getInstance()
            diagnosticsManager.setUseDefaultViewProviders(false)
            diagnosticsManager.loadRunningTimer =
                kitchenAidLauncherActivity.resources.getInteger(R.integer.service_load_time)
            diagnosticsManager.exitDiagnosticsNavigationId = R.id.global_action_to_clockScreen
            diagnosticsManager.timeoutNavigationId = R.id.global_action_to_clockScreen
            diagnosticsManager.diagnosticsEntryViewProvider = DiagnosticsEntryScreen()
            diagnosticsManager.diagnosticsHomeViewProvider = DiagnosticsHomeScreen()
            diagnosticsManager.diagnosticsErrorCodeViewProvider = DiagnosticsErrorCodeScreen()
            diagnosticsManager.diagnosticsComponentActivationViewProvider =
                DiagnosticsComponentActivationScreen()
            diagnosticsManager.diagnosticsComponentStatusViewProvider =
                DiagnosticsComponentStatusScreen()
            diagnosticsManager.diagnosticsComponentProgrammingViewProvider =
                DiagnosticsComponentProgrammingScreen()
            diagnosticsManager.autoDiagnosticsSelectionListViewProvider =
                AutoDiagnosticsListSelection()
            diagnosticsManager.autoDiagnosticsStatusViewProvider = AutoDiagnosticsStatusScreen()
            diagnosticsManager.autoDiagnosticsResultListViewProvider =
                AutoDiagnosticsResultListScreen()
            diagnosticsManager.autoDiagnosticsResultCavitySelectionViewProvider =
                AutoDiagnosticsResultListScreen()
            diagnosticsManager.autoDiagnosticsResultDetailsViewProvider =
                AutoDiagnosticsResultsDetailScreen()
            diagnosticsManager.diagnosticsSensorDetailsViewProvider =
                DiagnosticsSensorDetailsScreen()
            diagnosticsManager.diagnosticsSystemInfoListViewProvider = DiagnosticsSystemInfoScreen()
            diagnosticsManager.diagnosticsSystemInfoDetailsViewProvider =
                DiagnosticsSystemInfoDetailsScreen()
            diagnosticsManager.diagnosticsResetOptionSelectionViewProvider =
                DiagnosticsResetOptionSelectionScreen()
            diagnosticsManager.diagnosticsConfirmationViewProvider =
                DiagnosticsResetConfirmationScreen()
            diagnosticsManager.diagnosticsEditSystemInfoViewProvider =
                DiagnosticsEditSystemInfoScreen()
//            diagnosticsManager.cancelEntryNavigationId = R.id.settingsLandingFragment
//            diagnosticsManager.diagnosticsCommonPopupViewProvider = DiagnosticsPopupProvider()
        }

        /**
         * is PyroliticClean or not
         *
         * @return true/false
         */
        fun isPyroliticClean(): Boolean {
            var value = false
            val primaryCavityViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
            val secondaryCavityViewModel = getSecondaryCookingViewModel()
            if (primaryCavityViewModel?.recipeExecutionViewModel?.isPyroliticClean == true ||
                secondaryCavityViewModel?.recipeExecutionViewModel?.isPyroliticClean == true
            ) {
                value = true
            }
            return value
        }

        @Suppress("unused")
        fun setNavGraphId(destinationId: Int) {
            if (NavigationUtils.getVisibleFragment()?.requireView()?.let {
                    Navigation.findNavController(
                        it
                    ).graph.id
                } != destinationId
            ) {
                NavigationUtils.getVisibleFragment()?.requireView()?.let {
                    Navigation.findNavController(it)
                        .setGraph(destinationId)
                }
            }
        }

        /**
         * Method to get secondary cooking view model
         *
         * @return secondaryCookingViewModel
         */
        fun getSecondaryCookingViewModel(): CookingViewModel? {
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN, CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> return null
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN, CookingViewModelFactory.ProductVariantEnum.COMBO -> return CookingViewModelFactory.getSecondaryCavityViewModel()
                else -> {}
            }
            return null
        }

        /**
         * check if any of the cavity has active fault regardless of the cavity
         * @return true if has fault, false otherwise
         */
        fun isAnyCavityHasFault(): Boolean{
            var cookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
            if(cookingViewModel.faultId.value != 0){
                Logd("primaryCavity has fault ${cookingViewModel.faultId.value}")
                return true
            }
            cookingViewModel = getSecondaryCookingViewModel()
            if(cookingViewModel != null && cookingViewModel.faultId.value != 0){
                Logd("SecondaryCavity has fault ${cookingViewModel.faultId.value}")
                return true
            }
            return false
        }

        /**
         * to get knob position Index
         *
         * @param knobDirectionEvent knob rotate direction
         * @param knobCurrentPosition to give the position of current knob
         * @param itemsSize size of items in the given collection elements
         * @return currentPosition gives position after knob rotation
         */
        fun getKnobPositionIndex(
            knobDirectionEvent: String,
            knobCurrentPosition: Int,
            itemsSize: Int,
        ): Int {
            val newPosition = when (knobDirectionEvent) {
                KnobDirection.CLOCK_WISE_DIRECTION -> if (knobCurrentPosition == itemsSize - 1) knobCurrentPosition else knobCurrentPosition + 1 // Limit to list size
                KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> if (knobCurrentPosition <= 0) 0 else knobCurrentPosition - 1
                else -> {
                    0
                }
            }
            return newPosition
        }

        /**
         * to get knob position Index
         *
         * @param scrollView scrollView ID
         * @param knobDirectionEvent knob rotate direction
         * @param knobCurrentPosition to give the position of current knob
         * @param textLinesSize to give the size of text
         */
        fun getKnobPositionIndexForScrollViewText(
            scrollView: ScrollView,
            knobDirectionEvent: String,
            knobCurrentPosition: Int,
            textLinesSize: Int
        ): Int {
            val newPosition = when (knobDirectionEvent) {
                KnobDirection.CLOCK_WISE_DIRECTION -> if (scrollView.canScrollVertically(1)) knobCurrentPosition + 1 else textLinesSize
                KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> if (scrollView.canScrollVertically(-1) && knobCurrentPosition > 0) knobCurrentPosition - 1 else 0
                else -> {
                    Loge("Cant scroll further")
                    0
                }
            }
            return newPosition
        }

        // Method to validate if a given resource ID is valid for a drawable resource
        fun isValidDrawableResource(@DrawableRes resId: Int, context: Context): Boolean {
            return try {
                // Attempt to retrieve the drawable resource with the given ID
                ContextCompat.getDrawable(context, resId)
                // If no exception is thrown, the resource ID is valid
                true
            } catch (e: Resources.NotFoundException) {
                // If a Resources.NotFoundException is thrown, the resource ID is invalid
                false
            }
        }

        /**
         * Gets the resource ID of resource name. If the resource is not found, it will return a default text
         *
         * @param context   The instance of view context
         * @param resourceName The name of the resource which needs to be searched
         * @param defType      The type of the resource (string, anim etc)
         * @return Returns the ID of the resource
         */
        @SuppressLint("DiscouragedApi")
        fun getResIdFromResName(context: Context?, resourceName: String?, defType: String?): Int {
            val packageName = context?.packageName
            var resId = context?.resources?.getIdentifier(resourceName, defType, packageName)
            if (0 == resId) {
                Loge("ResId with resource name $resourceName Not found")
                resId = context?.resources?.getIdentifier("weMissedThat", defType, packageName)
            }
            if (resId == null) return 0
            return resId
        }

        /**
         * Method to get sub array position from a given array
         * @param resourceId Parent array id
         * @param recipeName Recipe name which will available on sub arrays
         * @return position
         */
        fun getTextPosition(view: View, resourceId: Int, recipeName: String): Int {
            val mainArray = view.resources.obtainTypedArray(resourceId)
            val length = mainArray.length()
            for (i in 0 until length) {
                val id = mainArray.getResourceId(i, 0)
                val subArray = view.resources.getStringArray(id)
                val cookGuideMessageList = ArrayList(listOf(*subArray))
                if (cookGuideMessageList.contains(recipeName)) {
                    return i + 1
                }
            }
            mainArray.recycle()
            return 0
        }

        /**
         * Method to format the temperature as string with degree
         *
         * @param temperature temperature to be formatted
         * @param context     context to access the resource value
         * @return temperature as string
         */
        fun getRecipeModeWithTemperatureAsString(
            context: Context,
            mode: String?,
            temperature: Int?,
            cookingViewModel: CookingViewModel,
        ): String? {
            if (temperature != null) {
                if (temperature > 0) {
                    return if (isTemperatureMapTextValue(cookingViewModel.recipeExecutionViewModel.targetTemperatureOptions.value)) {
                        mode?.let {
                            StringBuilder(it).append(EMPTY_SPACE)
                                .append(
                                    getSelectedBroilOption(
                                        context,
                                        cookingViewModel.recipeExecutionViewModel
                                    )
                                ).toString()
                        }
                    } else {
                        mode + EMPTY_SPACE + temperature + DEGREE_SYMBOL
                    }
                }
            }
            return EMPTY_STRING
        }

        /**
         * Method to format the temperature as string with degree
         *
         * @param temperature temperature to be formatted
         * @param context     context to access the resource value
         * @return temperature as string
         */
        fun getRecipeModeWithTemperatureAsStringFarView(
            context: Context,
            mode: String?,
            temperature: Int?,
            cookingViewModel: CookingViewModel,
        ): String? {
            if (temperature != null) {
                if (temperature > 0) {
                    return if (isTemperatureMapTextValue(cookingViewModel.recipeExecutionViewModel.targetTemperatureOptions.value)) {
                        mode?.let {
                            StringBuilder(it)
                                .append(
                                    getSelectedBroilOption(
                                        context,
                                        cookingViewModel.recipeExecutionViewModel
                                    )
                                ).toString()
                        }
                    } else {
                        mode + EMPTY_SPACE + temperature + DEGREE_SYMBOL
                    }
                }
            }
            return EMPTY_STRING
        }

        /**
         * Method to format the temperature as string with degree for hot cavity warning
         *
         * @param temperature temperature to be formatted
         * @param context     context to access the resource value
         * @return temperature as string
         */
        fun getRecipeModeWithTemperatureAsStringForHotTemperature(
            context: Context,
            mode: String?,
            temperature: Int?,
            cookingViewModel: CookingViewModel
        ): String {
            val broilLevel = getBroilOptionForHotTemperature(
                context,
                temperature,
                cookingViewModel.recipeExecutionViewModel
            )

            if (temperature != null && temperature > 0) {
                return if (isTemperatureMapTextValue(cookingViewModel.recipeExecutionViewModel.targetTemperatureOptions.value)) {
                    mode?.let {
                        val stringBuilder = StringBuilder(it)

                        if (broilLevel != EMPTY_STRING) {
                            stringBuilder.append(EMPTY_SPACE).append(broilLevel)
                            stringBuilder.append(EMPTY_SPACE).append(SYMBOL_FORWARD_SLASH)
                        }
                        stringBuilder.toString()
                    } ?: EMPTY_STRING
                } else {
                    mode + EMPTY_SPACE + temperature + DEGREE_SYMBOL + EMPTY_SPACE + SYMBOL_FORWARD_SLASH
                }
            }
            return EMPTY_STRING
        }

        /**
         * Method to get the selected Broil Level
         * @return Empty String / Selected Broil Level ex Low, Medium, High
         */
        private fun getSelectedBroilOption(
            context: Context,
            recipeExecutionViewModel: RecipeExecutionViewModel
        ): String {
            val temperatureMap =
                recipeExecutionViewModel.targetTemperatureOptions
                    .value as TemperatureMap
            val targetTemperature: Int? =
                recipeExecutionViewModel.targetTemperature.value
            var broilLevel: String
            broilLevel = context.getString(
                getResIdFromResName(
                    context,
                    TEXT_TEMP + temperatureMap.defaultValue,
                    AppConstants.RESOURCE_TYPE_STRING
                )
            )
            if (targetTemperature != 0) {
                for ((key, value) in temperatureMap.temperatureMap) {
                    if (targetTemperature == value) {
                        broilLevel = key
                        return context.getString(
                            getResIdFromResName(
                                context,
                                TEXT_TEMP + broilLevel,
                                AppConstants.RESOURCE_TYPE_STRING
                            )
                        )
                    }
                }
            }
            return broilLevel
        }

        /**
         * Method to get the Broil Level base on oven current temperature (used for hot cavity warning).
         * @return Empty String / Selected Broil Level ex Low, Medium, High
         */
        private fun getBroilOptionForHotTemperature(
            context: Context,
            temperature: Int?,
            recipeExecutionViewModel: RecipeExecutionViewModel
        ): String {
            if (temperature == null || temperature == 0) return EMPTY_STRING

            val temperatureOptions = recipeExecutionViewModel.targetTemperatureOptions.value
            if (temperatureOptions !is TemperatureMap) return EMPTY_STRING

            val temperatureMap = temperatureOptions.temperatureMap
            val sortedTemperatureMap = temperatureMap.toList().sortedBy { it.second }.toMap()

            val userSelectedBroilLevel = getSelectedBroilOption(context, recipeExecutionViewModel)
            var selectedBroilLevel = EMPTY_STRING

            for ((key, value) in sortedTemperatureMap) {
                if (temperature <= value) {
                    selectedBroilLevel = key
                    break
                }
            }

            if (selectedBroilLevel == EMPTY_STRING) {
                selectedBroilLevel = sortedTemperatureMap.keys.last()
            }

            val broilLevelString = context.getString(
                getResIdFromResName(context, TEXT_TEMP + selectedBroilLevel, AppConstants.RESOURCE_TYPE_STRING)
            )

            return if (userSelectedBroilLevel == broilLevelString) {
                EMPTY_STRING
            } else {
                broilLevelString
            }
        }


        /**
         * Method to format the temperature as string with degree
         *
         * @param mode
         * @param context     context to access the resource value
         * @param powerLevel  - MWO powe level
         * @return temperature as string
         */
        private fun getRecipeModeWithPowerLevelAsString(
            @Suppress("UNUSED_PARAMETER")
            context: Context,
            mode: String,
            powerLevel: String?,
        ): String {
            if (powerLevel != null) {
                return mode + EMPTY_SPACE + powerLevel + PERCENTAGE_SYMBOL
            }
            return EMPTY_STRING
        }


        /**
         * @param rvm recipe execution view model
         * @return power level either null or int value
         */
        private fun getMWOPowerLevel(rvm: RecipeExecutionViewModel): String? {
            val powerLevel = rvm.mwoPowerLevel.value
            if (powerLevel != null && powerLevel != 0) return powerLevel.toString()
            val isPowerLevelNonEditable =
                rvm.nonEditableOptions.value != null && rvm.nonEditableOptions.value != null && rvm.nonEditableOptions.value!![RecipeOptions.MWO_POWER_LEVEL] != null && rvm.nonEditableOptions.value!![RecipeOptions.MWO_POWER_LEVEL]!!.isNotEmpty()
            return if (isPowerLevelNonEditable) rvm.nonEditableOptions.value!![RecipeOptions.MWO_POWER_LEVEL]!!.toDouble()
                .toInt().toString() else null
        }


        /**``
         * To check whether the given resource name available in local
         * @param view view to get the context to access resources
         * @param resourceName name to check in resource files
         * @param defType resource file type
         * @return return 0 / resource id
         */
        @SuppressLint("DiscouragedApi")
        fun checkResIdAvailable(view: View, resourceName: String?, defType: String?): Int {
            val packageName = view.context.packageName
            return view.context.resources.getIdentifier(resourceName, defType, packageName)
        }

        /**
         * method to get Image id for popup
         *
         * @param recipeName current selected recipe
         * @param context       Fragment's view reference
         * @return current recipe related image id
         */
        fun getImageIdToShowOnPopup(recipeName: String, context: Context): Int {
            if (AppConstants.RECIPE_PIZZA.equals(recipeName, true)) {
                val drawableId: Int =
                    getResIdFromResName(
                        context,
                        recipeName.replace(
                            EMPTY_SPACE,
                            EMPTY_STRING
                        )
                            .lowercase(
                                Locale.getDefault()
                            ) + AppConstants.TEXT_POPUP,
                        AppConstants.RESOURCE_TYPE_DRAWABLE
                    )
                return if (drawableId > 0) drawableId else R.drawable.instruction_image_place_holder
            }
            return 0
        }

        // Method to validate if a given resource ID is valid for a string resource
        fun isValidStringResource(@StringRes resId: Int, context: Context): Boolean {
            return try {
                // Attempt to retrieve the string resource with the given ID
                val string = context.resources.getString(resId)
                string.isNotEmpty()
                // If no exception is thrown, the resource ID is valid
            } catch (e: Resources.NotFoundException) {
                // If a Resources.NotFoundException is thrown, the resource ID is invalid
                false
            }
        }


        /**
         *
         * @param cookTimeValues Cook time range
         * @return List<TumblerElement>
        </TumblerElement> */
        fun populateTumblersWithCookTimeValues(cookTimeValues: IntegerRange): List<TumblerElement> {
            val tumblerElements: MutableList<TumblerElement> = ArrayList()
            var i = cookTimeValues.min
            while (i <= cookTimeValues.max) {
                val localArray: MutableList<String> = ArrayList()
                if (i < 10) {
                    localArray.add("0$i")
                } else {
                    localArray.add(i.toString())
                }
                tumblerElements.add(
                    TumblerElement(
                        tumblerData = localArray, group = "Single", labelName = "",
                        type = TumblerElement.NUMERIC_TYPE, newlyAdded = false
                    )
                )
                i += cookTimeValues.step
            }
            return tumblerElements
        }

        /**
         * This function takes the tumblets and converts the text in a list of String elements
         *
         * @param tumblerElements The list of tumbler elements
         * @return An array list of converted string
         */
        fun convertTumblersToStringList(tumblerElements: List<TumblerElement>): ArrayList<String> {
            val tumblerStrings = ArrayList<String>()
            for (element in tumblerElements) {
                tumblerStrings.add(element.tumblerData?.get(0) ?: "")
            }
            return tumblerStrings
        }

        /**
         * utility method to return etr in spannable strings when cycle is running
         *
         * @param context to fetch resources
         * @param etr     in minutes
         * @return spannable etr of the provided value in minutes
         * examples etr = 130 the return value would be 02:10, the first digit of hour if 0 then would be gray
         * examples etr = 120 the return value would be 02:00, the last digits of minutes '00' in gray
         * As per new alignment with GCD, HMI need to show remaining cook time in HH:MM format if remaining time is more than 1Hr
         * and MM:SS format if it's less than 1Hr. This is applicable to all Oven and MWO cavity.
         */
        fun spannableETRRunning(
            @Suppress("UNUSED_PARAMETER")
            context: Context?,
            etr: Int,
        ): SpannableStringBuilder {
            val sb = SpannableStringBuilder()
            val hours = etr / (60 * 60)
            var minutes = etr / 60 % 60
            val seconds = etr % 60
            if (etr == 0) return sb.append(CYCLE_END_TIME)
            if(etr >= 3600) {
                if (hours == 0 && minutes == 0) {
                    minutes =
                            1 //if etr comes less than 1 minute then make etr as minutes for not display 00:00 in etr preview
                }
                if (hours < 10) {
                    sb.append("0")
                }
                sb.append(hours.toString()).append(":")
                if (minutes < 10) {
                    sb.append("0")
                }
                sb.append(minutes.toString())
            } else {
                if (minutes < 10) {
                    sb.append("0")
                }
                sb.append(minutes.toString()).append(":")
                if (seconds < 10) {
                    sb.append("0")
                }
                sb.append(seconds.toString())
            }
//            sb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.light_grey)),0,if (hours == 0) 2 else if (hours < 10) 1 else 0,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return sb
        }

        /**
         * utility method to return etr in spannable strings when cycle is running
         *
         * @param context to fetch resources
         * @param etr     in minutes
         * @return spannable etr of the provided value in minutes
         * examples etr = 130 the return value would be 02:10, the first digit of hour if 0 then would be gray
         * examples etr = 120 the return value would be 02:00, the last digits of minutes '00' in gray
         */
        fun spannableMwoETRRunning(
            @Suppress("UNUSED_PARAMETER")
            context: Context?,
            etr: Int,
            setCookTimeValue: Long?,
        ): SpannableStringBuilder {
            val sb = SpannableStringBuilder()
            val hours = etr / (60 * 60)
            val minutes = etr / 60 % 60
            val seconds = etr % 60
            val isHrAvailable = (setCookTimeValue?.toInt()?.div((60 * 60)) ?: false) != 0
            if (etr == 0) return sb.append(
                if (isHrAvailable) CYCLE_END_TIME_HOUR else CYCLE_END_TIME
            )
            if (isHrAvailable) {
                if (hours < 10) {
                    sb.append("0")
                }
                sb.append(hours.toString()).append(":")
            }
            if (minutes < 10) {
                sb.append("0")
            }
            sb.append(minutes.toString()).append(":")
            if (seconds < 10) {
                sb.append("0")
            }
            sb.append(seconds.toString())
            return sb
        }

        /**
         * utility method to return etr in spannable strings when cycle is running
         *
         * @param context to fetch resources
         * @param etr     in minutes
         * @return spannable etr of the provided value in minutes
         * examples etr = 130 the return value would be 02:10, the first digit of hour if 0 then would be gray
         * examples etr = 120 the return value would be 02:00, the last digits of minutes '00' in gray
         */
        fun spannableMwoTimeRemainingText(
            context: Context,
            etr: Long,
        ): SpannableStringBuilder {
            val sb = SpannableStringBuilder()
            //MAF_2741 : Defect fixed where System is showing the wrong remaining cook time on door open pop up.
            val minutes = etr / 60
            val seconds = etr % 60
            if (minutes.toInt() != 0) sb.append(minutes.toString()).append(EMPTY_SPACE)
                .append(context.getString(R.string.text_label_MIN).lowercase())
            if (seconds.toInt() != 0) {
                if (minutes.toInt() != 0) sb.append(EMPTY_SPACE)
                sb.append(seconds.toString()).append(EMPTY_SPACE)
                    .append(context.getString(R.string.text_label_SEC).lowercase())
            }
            return sb
        }

        /** load and navigate to subchild only screen
         * @param fragment fragment view
         * @param cycleName selected cycle
         * @param bundle to pass the data
         */
        fun navigateToSubChildRecipes(
            fragment: Fragment,
            cycleName: String,
            bundle: Bundle,
        ) {
            if (cycleName.equals(RECIPE_CONVECT, ignoreCase = true)) {
                navigateSafely(
                    fragment,
                    R.id.action_recipeSelectionFragment_to_ConvectCyclesSelectionFragment,
                    bundle,
                    null
                )
                return
            }
            if (cycleName.equals(RECIPE_PROBE, ignoreCase = true)) {
                navigateSafely(
                    fragment,
                    R.id.action_recipeSelectionFragment_to_probeCyclesSelectionFragment,
                    bundle,
                    null
                )
            }
            if (cycleName.equals(RECIPE_MORE_MODES, ignoreCase = true)) {
                navigateSafely(
                    fragment,
                    R.id.action_recipeSelectionFragment_to_moreModesCyclesSelectionFragment,
                    bundle,
                    null
                )
            }
        }

        /**
         * Method to get the tree node of the given cycle name
         *
         * @param cycleName eg: AirFry, Broil.. etc
         * @return treeNode of the given Cycle name
         */
        fun getNodeForCycle(cycleName: String): TreeNode<String>? {
            val rootNode: TreeNode<String>? = if (cycleName != EMPTY_STRING) {
                CookBookViewModel.getInstance().currentPresentationTreeRootNode.value
            } else {
                CookBookViewModel.getInstance()
                    .getManualRecipesPresentationTreeFor(CookingViewModelFactory.getInScopeViewModel().cavityName.value)
            }
            if (rootNode != null && rootNode.children.isNotEmpty()) {
                for (treeNode in rootNode.children) {
                    if (treeNode.data == cycleName || treeNode.children.stream()
                            .anyMatch { node: TreeNode<String> ->
                                node.data.contains(cycleName)
                            }
                    ) {
                        return treeNode
                    }
                }
            } else if (rootNode != null) {
                return rootNode
            }
            Loge("getNodeForCycle: Unable to find the current selected cycle in nodes list")
            return null
        }

        /**
         * Use this common method to set Header as recipe name
         *  @return HeaderTitle name string
         */
        @SuppressLint("DiscouragedApi")
        fun getHeaderTitleAsRecipeName(context: Context?, itemIdentifier: String?): String {
            val resources = context?.resources

            @Suppress("SimplifiableCallChain")
            val identifier = listOf(
                AppConstants.TEXT_HEADER + itemIdentifier,
                itemIdentifier,
                AppConstants.TEXT_MODE + itemIdentifier
            ).mapNotNull { name ->
                resources?.getIdentifier(
                    name,
                    AppConstants.RESOURCE_TYPE_STRING,
                    context.packageName
                ).takeIf { it != 0 }
            }.firstOrNull()

            return (identifier?.let { resources?.getString(it) } ?: itemIdentifier).toString()
        }

        /**
         * Use this common method to set Header as recipe name
         *  @return HeaderTitle string Id
         */
        @SuppressLint("DiscouragedApi")
        fun getHeaderTitleResId(context: Context?, itemIdentifier: String?): Int {
            val resources = context?.resources

            @Suppress("SimplifiableCallChain") val identifier = listOf(
                AppConstants.TEXT_HEADER + itemIdentifier,
                itemIdentifier,
                AppConstants.TEXT_MODE + itemIdentifier
            ).mapNotNull { name ->
                resources?.getIdentifier(
                    name,
                    AppConstants.RESOURCE_TYPE_STRING,
                    context.packageName
                ).takeIf { it != 0 }
            }.firstOrNull() ?: 0

            return if (identifier == 0) {
                getResIdFromResName(context, itemIdentifier, AppConstants.RESOURCE_TYPE_STRING)
            } else {
                identifier
            }
        }

        /**
         * Use this common method to set recipe name
         * @return recipe name string
         */
        @SuppressLint("DiscouragedApi")
        fun getRecipeNameText(context: Context, itemIdentifier: String): String {
            val resources = context.resources

            @Suppress("SimplifiableCallChain") val identifier = listOf(
                itemIdentifier,
                AppConstants.TEXT_MODE + itemIdentifier,
                AppConstants.TEXT_HEADER + itemIdentifier
            ).mapNotNull { name ->
                resources.getIdentifier(
                    name,
                    AppConstants.RESOURCE_TYPE_STRING,
                    context.packageName
                ).takeIf { it != 0 }
            }.firstOrNull()

            return identifier?.let { resources.getString(it) } ?: itemIdentifier
        }

        /**
         * Use this common method to set recipe name for convect cluster
         * @return recipe name string
         */
        @SuppressLint("DiscouragedApi")
        fun getRecipeNameTextForConvectGridList(context: Context, itemIdentifier: String): String {
            val resources = context.resources
            @Suppress("SimplifiableCallChain") val identifier = listOf(
                AppConstants.TEXT_MODE + itemIdentifier,
                itemIdentifier,
                AppConstants.TEXT_HEADER + itemIdentifier
            ).mapNotNull { name ->
                resources.getIdentifier(
                    name,
                    AppConstants.RESOURCE_TYPE_STRING,
                    context.packageName
                ).takeIf { it != 0 }
            }.firstOrNull()

            return identifier?.let { resources.getString(it) } ?: itemIdentifier
        }

        /**
         * useful to identify whether the temperature value coming as Integer or Text for example in case of Broil recipe his would be true
         */
        fun isTemperatureMapTextValue(tempOptions: Any?): Boolean {
            return if (tempOptions != null) {
                tempOptions is TemperatureMap
            } else false
        }

        /**
         * To given recipe options is available or not in presentation tree
         *
         * @param cookingViewModel view model to get the required options
         * @param recipeOptions    recipe options to check available
         * @return true/false based on given recipe options
         */
        fun isRequiredTargetAvailable(
            cookingViewModel: CookingViewModel?,
            recipeOptions: RecipeOptions,
        ): Boolean {
            var isRecipeAvailable = false
            val requiredRecipeOptions =
                cookingViewModel?.recipeExecutionViewModel?.requiredOptions?.value
            if (!requiredRecipeOptions.isNullOrEmpty() && requiredRecipeOptions.contains(
                    recipeOptions
                )
            ) {
                isRecipeAvailable = true
            }
            return isRecipeAvailable
        }

        /**
         * Use this common method to set header bar text title from recipe name
         */
        fun setHeaderTitleAsRecipeName(
            headerBarWidget: HeaderBarWidget?,
            cookingViewModel: CookingViewModel?,
        ) {
            val resId = getResIdFromResName(
                headerBarWidget?.context,
                AppConstants.TEXT_HEADER + cookingViewModel?.recipeExecutionViewModel?.recipeName?.value,
                AppConstants.RESOURCE_TYPE_STRING
            )
            if (headerBarWidget?.context == null) return
            if (isValidStringResource(resId, headerBarWidget.context)) {
                headerBarWidget.setTitleText(resId)
                return
            }
            val resIdWithoutHeader = getResIdFromResName(
                headerBarWidget.context,
                cookingViewModel?.recipeExecutionViewModel?.recipeName?.value,
                AppConstants.RESOURCE_TYPE_STRING
            )
            if (isValidStringResource(resIdWithoutHeader, headerBarWidget.context)) {
                headerBarWidget.setTitleText(resIdWithoutHeader)
                return
            }
            headerBarWidget.setTitleText(cookingViewModel?.recipeExecutionViewModel?.recipeName?.value)
        }

        /**
         * To check give recipe name contains any child recipes or not from manual presentation tree
         *
         * @param recipeName recipe Name to check
         * @return true/false for provided recipe contains children
         */
        fun isChildrenAvailableForRecipe(
            recipeName: String?,
            manualRecipesPresentationTreeFor: TreeNode<String>?,
        ): Boolean {
            if (manualRecipesPresentationTreeFor == null) {
                return false //Based on how we are traversing, if we are here, possibly at start
            }
            for (treeNode in manualRecipesPresentationTreeFor.children) {
                if (treeNode.data.equals(recipeName, ignoreCase = true)) {
                    return treeNode.children.isNotEmpty()
                }
                if (treeNode.children.isNotEmpty()) {
//          To check if sub tree have children then iterating children find whether required cycle is there or not
                    for (subTreeNode in treeNode.children) {
                        if (subTreeNode.data.equals(recipeName, ignoreCase = true)) {
                            return subTreeNode.children.isNotEmpty()
                        } else {
//                      To check multiple subtree for required cycle
                            isChildrenAvailableForRecipe(recipeName, subTreeNode)
                        }
                    }
                }
            }
            // To satisfy lint, shouldn't reach here, if anyways reached here, we default to true.
            return false
        }

        /**
         * Based on the Given Recipe Tree and recipe name provide matched recipe name tree data
         *
         * @param startNode  tree data to check recipes
         * @param recipeName recipe name
         * @return matched recipe data
         */
        fun getRecipeData(startNode: TreeNode<String>?, recipeName: String?): TreeNode<String>? {
            if (startNode == null) {
                return null
            }
            for (treeNode in startNode.children) {
                if (treeNode.data.equals(recipeName, ignoreCase = true)) {
                    return treeNode
                }
                if (treeNode.children.isNotEmpty()) {
                    for (subTreeNode in treeNode.children) {
                        if (subTreeNode.data.equals(recipeName, ignoreCase = true)) {
                            return subTreeNode
                        } else {
                            getRecipeData(subTreeNode, recipeName)
                        }
                    }
                }
            }
            return startNode
        }

        /**
         * Get weight value from sdk unit
         *
         * @param context       fragment context to access resources
         * @param weightUnitSdk unit to display
         * @param isUpperCase   for capitalize string
         * @return user facing weight unit value
         */
        fun getWeightUnitStringIdFromDisplayUnit(
            context: Context,
            weightUnitSdk: String?,
            isUpperCase: Boolean,
        ): String {
            val weightUnit: String = when (weightUnitSdk) {
                CapabilityKeys.OUNCES_VALUE -> context.getString(
                    R.string.text_tile_decision_large_ouces,
                    EMPTY_STRING
                )

                CapabilityKeys.LBS_VALUE -> context.getString(
                    R.string.text_tile_decision_large_lb,
                    EMPTY_STRING
                )

                CapabilityKeys.KILOGRAMS_VALUE -> context.getString(
                    R.string.text_tile_decision_large_killogram,
                    EMPTY_STRING
                )

                CapabilityKeys.GRAMS_VALUE -> context.getString(
                    R.string.text_tile_decision_large_gram,
                    EMPTY_STRING
                )

                else -> EMPTY_STRING
            }
            return if (isUpperCase) weightUnit.uppercase(Locale.getDefault()) else weightUnit
        }

        /**
         * convert list of array string in numeric value without trailing zeros
         * @param tumblerStrings arraylist of strings
         * @return list without trailing zero [339.0, 452.0, 565.0, 678.0] -> [339, 452, 565, 678]
         */
        fun removeTrailingZerosInList(tumblerStrings: ArrayList<String?>): ArrayList<String> {
            val newTumblerStrings = ArrayList<String>(tumblerStrings.size)
            Logd("list", "Existing: $tumblerStrings")
            for (i in tumblerStrings.indices) {
                newTumblerStrings.add(i, removeTrailingZeroInString(tumblerStrings[i]))
            }
            Logd("list", "Converted: $newTumblerStrings")
            return newTumblerStrings
        }

        /**
         * @param string value to be remove trailing zero
         * @return without trailing zero for ex 1.00 ->1, 1.0->1, 2.60->2.6
         */
        fun removeTrailingZeroInString(string: String?): String {
            if (string == null) return EMPTY_STRING
            return if (!string.contains(AppConstants.DOT_DECIMAL)) string else string.replace(
                AppConstants.STRING_REGREX_ZERO.toRegex(),
                EMPTY_STRING
            ).replace(AppConstants.STRING_REGREX_DECIMAL.toRegex(), EMPTY_STRING)
        }

        /**
         * get Selected weight details from Assisted cooking
         *
         * @param currentWeight current weight details
         * @param context       fragment instance
         * @return weight in unit values
         */
        fun displayWeightToUser(
            context: Context,
            currentWeight: Float?,
            weightDisplayUnit: String?,
        ): String {
            val selectedWeight = java.lang.StringBuilder()
            Logd("Weight value received from SDK is: $currentWeight")
            val setWeightInRecipe: String = removeTrailingZeroInString(
                String.format(
                    context.getString(R.string.text_format_up_to_two_decimal),
                    currentWeight
                )
            )
            selectedWeight.append(setWeightInRecipe)
            Logd("Weight value converted: $setWeightInRecipe")
            var unit = if (weightDisplayUnit.equals(
                    AppConstants.WEIGHT_STRING_OUNCES,
                    ignoreCase = true
                )
            ) context.getString(
                R.string.text_tile_decision_large_ouces,
                EMPTY_STRING
            ) else if (weightDisplayUnit.equals(
                    AppConstants.WEIGHT_STRING_GRAMS,
                    ignoreCase = true
                )
            ) context.getString(
                R.string.text_tile_decision_large_gram,
                EMPTY_STRING
            ) else if (weightDisplayUnit.equals(
                    AppConstants.WEIGHT_STRING_KILOGRAMS,
                    ignoreCase = true
                )
            ) context.getString(
                R.string.text_tile_decision_large_killogram,
                EMPTY_STRING
            ) else context.getString(R.string.text_tile_decision_large_lb, EMPTY_STRING)
            return selectedWeight.append(EMPTY_SPACE).append(unit).toString()
        }

        /**
         * get Temperature To Display weight details from Assisted cooking
         *
         * @param recipeExecutionViewModel recipe execution view model of current cavity selected
         * @param context       fragment instance
         * @return weight in unit values
         */
        fun displayTemperatureToUser(
            context: Context,
            recipeExecutionViewModel: RecipeExecutionViewModel,
        ): String {
            return if (isTemperatureMapTextValue(recipeExecutionViewModel.targetTemperatureOptions.value)) {
                getSelectedBroilOption(context, recipeExecutionViewModel)
            } else {
                val temperatureString = StringBuilder()
                temperatureString.append(recipeExecutionViewModel.targetTemperature.value.toString())
                    .append(EMPTY_SPACE)
                temperatureString.append(
                    if (isFAHRENHEITUnitConfigured()) context.getString(
                        R.string.text_tiles_list_fahrenheit_value
                    ) else context.getString(
                        R.string.text_tiles_list_celsius_value
                    )
                )
                return temperatureString.toString()
            }
        }

        /**
         * get Temperature To Display weight details from Assisted cooking
         *
         * @param recipeExecutionViewModel recipe execution view model of current cavity selected
         * @param context       fragment instance
         * @return weight in unit values
         */
        fun displayProbeTemperatureToUser(
            context: Context,
            recipeExecutionViewModel: RecipeExecutionViewModel,
        ): String {
            val temperatureString = StringBuilder()
            temperatureString.append(recipeExecutionViewModel.meatProbeTargetTemperature.value.toString())
                .append(EMPTY_SPACE)
            temperatureString.append(
                if (isFAHRENHEITUnitConfigured()) context.getString(
                    R.string.text_tiles_list_fahrenheit_value
                ) else context.getString(
                    R.string.text_tiles_list_celsius_value
                )
            )
            return temperatureString.toString()
        }


        /**
         * get Temperature To Display weight details from Assisted cooking
         *
         * @param cookTime recipe execution view model of current cavity selected
         * @param context       fragment instance
         * @param showSeconds true if seconds need to show false to ignore
         * @param appendString arrayOf(R.string.text_tile_preview_display_hour_lower, R.string.text_tile_preview_display_min_lower, R.string.text_tile_preview_display_sec_lower)
         * @return weight in unit values
         */
        fun displayCookTimeToUser(
            context: Context,
            cookTime: Long?,
            showSeconds: Boolean,
            appendString: Array<Int>
        ): String {
            return if (cookTime != null) {
                val hour = (cookTime.div(3600)).toString()
                val min = ((cookTime.div(60)) % 60).toString()
                val sec = (cookTime.rem(60)).toString()
                if (hour != DEFAULT_LEVEL && min != DEFAULT_LEVEL) {
                    val hourString = hour + EMPTY_SPACE +
                        context.getString(appendString[0])
                    hourString + EMPTY_SPACE + min + EMPTY_SPACE + context.getString(
                        appendString[1]
                    )
                } else if (hour != DEFAULT_LEVEL) {
                    return hour + EMPTY_SPACE + context.getString(appendString[0])
                } else if (showSeconds && min != DEFAULT_LEVEL && sec != DEFAULT_LEVEL) {
                    String.format( min + EMPTY_SPACE +
                        context.getString(
                            appendString[1]
                        ) + EMPTY_SPACE + sec + EMPTY_SPACE + context.getString(
                            appendString[2]
                        )
                    )
                } else if (min != DEFAULT_LEVEL) {
                    String.format(min + EMPTY_SPACE + context.getString(appendString[1]))
                } else if (sec != DEFAULT_LEVEL) {
                    String.format(sec + EMPTY_SPACE + context.getString(appendString[2]))
                } else {
                    EMPTY_STRING
                }
            } else {
                EMPTY_STRING
            }
        }
        /**
         * Checks if the current visible fragment is Clock Screen or splash screen
         *
         * @return true if current visible fragment is Clock Screen or splash screen, else false
         */
        fun isSystemIsIdle(activity: FragmentActivity): Boolean {
            val fragmentManager = activity.supportFragmentManager
            val fragment = getVisibleFragment(fragmentManager)
            if (fragment != null) {
                (fragmentManager.primaryNavigationFragment?.let {
                    getViewSafely(it)
                }
                    ?: fragmentManager.primaryNavigationFragment?.requireView())?.let {
                    val currentScreenId = Navigation.findNavController(
                        it
                    ).currentDestination?.id
                    return currentScreenId == R.id.clockFragment ||
                            currentScreenId == R.id.splashFragment
                }
            }
            return false
        }

        fun getCategoryAFaultCodesMap(): Map<String, ArrayList<Int>> {
            return faultCodeDetailsJsonParser.parseCategoryAFaultCodesJson()
        }

        fun getCategoryBFaultCodesMap(): Map<String, ArrayList<Int>> {
            return faultCodeDetailsJsonParser.parseCategoryBFaultCodesJson()
        }

        fun getCategoryB2FaultCodesMap(): Map<String, ArrayList<Int>> {
            return faultCodeDetailsJsonParser.parseCategoryB2FaultCodesJson()
        }

        fun getCategoryCFaultCodesMap(): Map<String, ArrayList<Int>> {
            return faultCodeDetailsJsonParser.parseCategoryCFaultCodesJson()
        }

        fun getSaveOnlyFaultCodesList(): List<String> {
            return faultCodeDetailsJsonParser.parseSaveOnlyFaultsListJson()
        }

        /**
         * Method to determine highest priority fault and navigate to respective Error Screen
         *
         * @param cavity : Primary Cavity / Secondary Cavity
         */
        fun navigateToHighestPriorityFault(
            cavity: String,
            faultDetails: FaultDetails,
            fragment: Fragment?,
            otherCavityCookingViewModel: CookingViewModel?,
            faultCategory: Int,
        ) {
            if (fragment != null) {
                val sharedViewModel =
                    ViewModelProvider(fragment)[SharedViewModel::class.java]
                val modelNumber = SettingsViewModel.getSettingsViewModel().model.value
                val serialNumber = SettingsViewModel.getSettingsViewModel().serialNumber.value
                if (otherCavityCookingViewModel?.recipeExecutionViewModel?.isRunning == true &&
                    (faultCategory == FaultSubCategory.CATEGORY_A.ordinal ||
                            faultCategory == FaultSubCategory.CATEGORY_C.ordinal)
                ) {
                    otherCavityCookingViewModel.recipeExecutionViewModel.cancel()
                    Logd(
                        "CookinAppUtils Cancel",
                        "cancelled recipe for ${otherCavityCookingViewModel.cavityName.value} in navigateToHighestPriorityFault"
                    )
                }
                val isNewFaultInSameCavity =
                    (cavity == PRIMARY_CAVITY_KEY &&
                            sharedViewModel.isCurrentDisplayedFaultInPrimaryCavity()
                            || cavity == SECONDARY_CAVITY_KEY &&
                            !sharedViewModel.isCurrentDisplayedFaultInPrimaryCavity())
                if (sharedViewModel.isFaultPopUpOpen() && !isNewFaultInSameCavity) {
                    if (CookingViewModelFactory.getProductVariantEnum() ==
                        CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
                    ) {
                        if (faultDetails.getDoubleOvenFaultPriority() <
                            faultDetails.getCurrentDisplayedFaultDoubleOvenPriority()
                        ) {
                            faultDetails.navigateToErrorScreen(
                                cavity,
                                faultCategory,
                                fragment,
                                modelNumber,
                                serialNumber
                            )
                        } else {
                            sharedViewModel.setShowOtherCavityFaultPopUp(true)
                        }
                    } else {
                        if (faultDetails.getComboOvenFaultPriority() <
                            faultDetails.getCurrentDisplayedFaultComboOvenPriority()
                        ) {
                            faultDetails.navigateToErrorScreen(
                                cavity,
                                faultCategory,
                                fragment,
                                modelNumber,
                                serialNumber
                            )
                        } else {
                            sharedViewModel.setShowOtherCavityFaultPopUp(true)
                        }
                    }
                } else {
                    faultDetails.navigateToErrorScreen(
                        cavity,
                        faultCategory,
                        fragment,
                        modelNumber,
                        serialNumber
                    )
                }
            }
        }

        @Suppress("unused")
        fun setIsSelfCleanFlow(isSelfCleanFlow: Boolean) {
            Logi("isSelfCleanFlow $isSelfCleanFlow")
            this.isSelfCleanFlow = isSelfCleanFlow
        }

        fun isSelfCleanFlow(): Boolean {
            return isSelfCleanFlow
        }

        @Suppress("unused")
        fun setIsSabbathFlow(isSelfCleanFlow: Boolean) {
            Logi("isSabbathFlow $isSelfCleanFlow")
            this.isSabbathFlow = isSelfCleanFlow
        }

        fun isSabbathFlow(): Boolean {
            return isSabbathFlow
        }

        /**
         * Method to manage Cavity Light
         */
        @Suppress("unused")
        fun manageCavityLights() {
            if (CookingViewModelFactory.getProductVariantEnum() ==
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN
                || CookingViewModelFactory.getProductVariantEnum() ==
                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN
            ) {
                //Enabling the CANCEL button & disabling the SET button
                HMIExpansionUtils.setLightForHomeButton(false)
            }
            HMIExpansionUtils.setLightForCancelButton(true)
        }

        fun manageHMIPanelLights(
            homeLight: Boolean,
            cancelLight: Boolean,
            cleanLight: Boolean
        ) {
            HMIExpansionUtils.setLightForCancelButton(cancelLight)
            HMIExpansionUtils.setLightForHomeButton(homeLight)
            //removing handling of clean button backlight from here as the interaction is only in case of popup
            Logd("clean light state $cleanLight")
        }

        /**
         * Method to check whether the secondary cavity is idle or not
         *
         * @return true if the other oven is in idle state
         */
        @Suppress("unused")
        private fun isOutOfScopeCavityIdle(): Boolean {
            return when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO,
                -> RecipeCookingState.IDLE == CookingViewModelFactory.getOutOfScopeCookingViewModel().recipeExecutionViewModel.recipeCookingState.value && Timer.State.IDLE == CookingViewModelFactory.getOutOfScopeCookingViewModel().recipeExecutionViewModel.delayTimerState.value

                else -> true
            }
        }

        /**
         * Method to cancel sabbath programming and navigate to Clock or Sabbath idle screen
         * @param fragment current running fragment
         * @param navigateToSabbathClock true/false
         * @param navigateToClockScreen true/false
         */
        @Suppress("unused")
        fun cancelProgrammedCyclesAndNavigate(
            fragment: Fragment,
            @Suppress("UNUSED_PARAMETER")
            navigateToSabbathClock: Boolean,
            navigateToClockScreen: Boolean,
        ) {
            val primaryCavityViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
            primaryCavityViewModel.recipeExecutionViewModel.cancel()
            Logd(
                "CookinAppUtils Cancel",
                "cancelled recipe for primaryCavity in cancelProgrammedCyclesAndNavigate"
            )
            val productVariantEnum = CookingViewModelFactory.getProductVariantEnum()
            if (CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN == productVariantEnum || CookingViewModelFactory.ProductVariantEnum.COMBO == productVariantEnum) {
                val secondaryCavityViewModel = CookingViewModelFactory.getSecondaryCavityViewModel()
                secondaryCavityViewModel.recipeExecutionViewModel.cancel()
                Logd(
                    "CookinAppUtils Cancel",
                    "cancelled recipe for secondaryCavity in cancelProgrammedCyclesAndNavigate"
                )
            }
            HMIExpansionUtils.setLightForCancelButton(false)
            if (navigateToClockScreen) {
                navigateSafely(
                    fragment,
                    R.id.global_action_to_clockScreen,
                    null,
                    null
                )
            }
            //TODO:Uncomment below code when sabbathclock mode implemented
            /*if (navigateToSabbathClock) {
                    navigateSafely(
                        fragment,
                        R.id.global_action_sabbath_mode_clock,
                        null,
                        null
                    )
            }*/
        }

        /**
         * get navigation graph id based on variants
         *
         * @param productVariantEnum product variant enum
         * @return navigation graph id
         */
        @Suppress("unused")
        fun getNavigationGraphId(productVariantEnum: CookingViewModelFactory.ProductVariantEnum?): Int {
            var id: Int = R.navigation.manual_cooking_single_oven
            when (productVariantEnum) {
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> id =
                    R.navigation.manual_cooking_double_oven

                CookingViewModelFactory.ProductVariantEnum.COMBO -> id =
                    R.navigation.manual_cooking_combo

                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> id =
                    R.navigation.manual_cooking_mwo_oven

                else -> {}
            }
            return id
        }

        /**
         * Method to check if demo mode is enabled or not
         *
         * @return true/false
         */
        fun isDemoModeEnabled(): Boolean {
            val settingsViewModel = SettingsViewModel.getSettingsViewModel()
            return (settingsViewModel.demoMode.value != null
                    && settingsViewModel.demoMode.value == SettingsViewModel.DemoMode.DEMO_MODE_ENABLED)
        }

        /**
         * Method to check wheather active faults if appliance is coming from IDLE to programming
         */
        fun checkForActiveFaults(fragment: Fragment) {
            if (!isDemoModeEnabled()) {
                val primaryCookingVM = CookingViewModelFactory.getPrimaryCavityViewModel()
                val secondaryCookingVM: CookingViewModel? = getSecondaryCookingViewModel()

                val isPrimaryCavity = CookingViewModelFactory.getInScopeViewModel()?.isPrimaryCavity == true
                val isSecondaryCavity = CookingViewModelFactory.getInScopeViewModel()?.isSecondaryCavity == true

                val primaryFaultDetails = getFaultDetails(primaryCookingVM.faultCode.value)
                val primaryCommFaultDetails = getFaultDetails(getCommunicationFaultCode(primaryCookingVM.communicationFaultCode.value))

                val secondaryFaultDetails = getFaultDetails(secondaryCookingVM?.faultCode?.value)
                val secondaryCommFaultDetails = getFaultDetails(getCommunicationFaultCode(secondaryCookingVM?.communicationFaultCode?.value))

                // Handle Primary Cavity Faults
                if (isPrimaryCavity) {
                    handlePrimaryCavityFaults(primaryCookingVM, secondaryCookingVM, fragment, primaryFaultDetails, primaryCommFaultDetails, secondaryFaultDetails, secondaryCommFaultDetails)
                }

                // Handle Secondary Cavity Faults
                if (isSecondaryCavity) {
                    handleSecondaryCavityFaults(primaryCookingVM, secondaryCookingVM, fragment, primaryFaultDetails, primaryCommFaultDetails, secondaryFaultDetails, secondaryCommFaultDetails)
                }
            }
        }

        private fun getCommunicationFaultCode(faultCode: String?): String {
            return if (faultCode?.length == 4) faultCode + "0" else faultCode ?: ""
        }

        private fun getFaultDetails(faultCode: String?): FaultDetails? {
            return faultCode?.let { FaultDetails.getInstance(it) }
        }

        private fun handlePrimaryCavityFaults(
            primaryCookingVM: CookingViewModel,
            secondaryCookingVM: CookingViewModel?,
            fragment: Fragment,
            primaryFaultDetails: FaultDetails?,
            primaryCommFaultDetails: FaultDetails?,
            secondaryFaultDetails: FaultDetails?,
            secondaryCommFaultDetails: FaultDetails?
        ) {
            if (primaryCookingVM.faultId?.value != 0) {
                primaryFaultDetails?.handlePrimaryFaultNavigation(primaryFaultDetails, primaryCookingVM, secondaryCookingVM, fragment.requireActivity())
            } else if (primaryCookingVM.communicationFaultCode?.value != AppConstants.FAULT_AS_NONE) {
                primaryCommFaultDetails?.handlePrimaryFaultNavigation(primaryCommFaultDetails, primaryCookingVM, secondaryCookingVM, fragment.requireActivity())
            } else if (secondaryCookingVM?.faultId?.value != 0 && isFaultAorC(secondaryFaultDetails)) {
                secondaryFaultDetails?.handleSecondaryFaultNavigation(secondaryFaultDetails, primaryCookingVM, secondaryCookingVM, fragment.requireActivity())
            } else if (secondaryCookingVM?.communicationFaultCode?.value != AppConstants.FAULT_AS_NONE && isFaultAorC(secondaryCommFaultDetails)) {
                secondaryCommFaultDetails?.handleSecondaryFaultNavigation(secondaryCommFaultDetails, primaryCookingVM, secondaryCookingVM, fragment.requireActivity())
            }
        }

        private fun handleSecondaryCavityFaults(
            primaryCookingVM: CookingViewModel,
            secondaryCookingVM: CookingViewModel?,
            fragment: Fragment,
            primaryFaultDetails: FaultDetails?,
            primaryCommFaultDetails: FaultDetails?,
            secondaryFaultDetails: FaultDetails?,
            secondaryCommFaultDetails: FaultDetails?
        ) {
            if (isFaultAorC(primaryFaultDetails)) {
                primaryFaultDetails?.handlePrimaryFaultNavigation(primaryFaultDetails, primaryCookingVM, secondaryCookingVM, fragment.requireActivity())
            } else if (isFaultAorC(primaryCommFaultDetails)) {
                primaryCommFaultDetails?.handlePrimaryFaultNavigation(primaryCommFaultDetails, primaryCookingVM, secondaryCookingVM, fragment.requireActivity())
            } else if (secondaryCookingVM?.faultId?.value != 0) {
                secondaryFaultDetails?.handleSecondaryFaultNavigation(secondaryFaultDetails, primaryCookingVM, secondaryCookingVM, fragment.requireActivity())
            } else if (secondaryCookingVM?.communicationFaultCode?.value != AppConstants.FAULT_AS_NONE) {
                secondaryCommFaultDetails?.handleSecondaryFaultNavigation(secondaryCommFaultDetails, primaryCookingVM, secondaryCookingVM, fragment.requireActivity())
            }
        }

        /**
         * determine if the given fault is category A or C
         * @param faultDetails for the fault code
         * @return true if fault is type A or C, false otherwise
         */
         fun isFaultAorC(faultDetails: FaultDetails?): Boolean {
            return faultDetails?.getFaultCategory() == FaultSubCategory.CATEGORY_C.ordinal
                    || faultDetails?.getFaultCategory() == FaultSubCategory.CATEGORY_A.ordinal
        }

        /**
         * determine if the given cavity has fault or not
         * @param cookingVM for the cavity
         * @return true if there are no fault, false otherwise
         */
        fun isCavityFaultNone(cookingVM: CookingViewModel?): Boolean {
            return cookingVM?.faultCode?.value?.contentEquals(AppConstants.FAULT_AS_NONE) == true && cookingVM.communicationFaultCode.value?.contentEquals(AppConstants.FAULT_AS_NONE) == true
        }

        /**
         * Find if the record is required to have probe
         * NOTE: Use this when you want to traverse through node (capability) file without loading recipes
         * for already loaded recipe USE CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.isProbeBasedRecipe() API
         *
         * @param recipeName name of the recipe
         * @param cavityName cavity type, primaryCavity, secondaryCavity
         * @return true is record requires probe or probe recipe, false otherwise
         */
        fun isProbeRequiredForRecipe(recipeName: String, cavityName: String): Boolean {
            return try {
                val recipeRecord = CookBookViewModel.getInstance()
                    .getDefaultRecipeRecordByNameAndCavity(
                        recipeName,
                        cavityName
                    )
                val jsonObject = JSONObject(recipeRecord.content)
                jsonObject.getBoolean(AppConstants.KEY_WHR_TEMPERATURE_PROBE_REQUIRED)
            } catch (e: Exception) {
                Loge(
                    "RecipeParsing",
                    "not able to parse recipe content because: " + e.message
                )
                false
            }
        }

        /**
         * Find if the record is Assisted or not
         * to check of the loaded recipe belongs to assisted or not.
         * Note: Do not traverse through TreeNode as calling multiple times impact performance
         * @param recipeName name of the recipe
         * @param cavityName cavity type, primaryCavity, secondaryCavity
         * @return true if recipe belongs to assisted tree, false otherwise
         */
        fun isRecipeAssisted(recipeName: String?, cavityName: String?): Boolean {
            try {
                val assistedNode = CookBookViewModel.getInstance()
                    .getDefaultAssistedRecipesPresentationTreeFor(cavityName)
                if (assistedNode?.data?.isNotEmpty() == true && assistedNode.children.isNotEmpty()) {
                    for (i in assistedNode.children.indices) {
                        val subNode = assistedNode.children[i]
                        if(subNode.children.size == 0 && subNode.data.contentEquals(recipeName)) return true
                        if (subNode.data.isNotEmpty() && subNode.children.isNotEmpty()) {
                            for (j in subNode.children.indices) {
                                val subNodeRecipeName = subNode.children[j].data
                                if (subNodeRecipeName.contentEquals(recipeName)) return true
                            }
                        }
                    }
                }
            } catch (exception: Exception) {
                Loge(
                    "assistedRecipe",
                    " error in traversing assisted: " + exception.message
                )
                exception.printStackTrace()
            }
            return false
        }

        /**
         * Find if the record is Assisted or not on root node
         * to check of the loaded recipe belongs to assisted or not.
         * Note: Do not traverse through TreeNode as calling multiple times impact performance
         * @param recipeName name of the recipe
         * @param cavityName cavity type, primaryCavity, secondaryCavity
         * @return true if recipe belongs to assisted tree, false otherwise
         */
        fun isRecipeAssistedOnRootNode(recipeName: String?, cavityName: String?): Boolean {
            try {
                val assistedNode = CookBookViewModel.getInstance()
                    .getDefaultAssistedRecipesPresentationTreeFor(cavityName)
                if (assistedNode?.data?.isNotEmpty() == true && assistedNode.children.isNotEmpty()) {
                    for (children in assistedNode.children) {
                        if (children.data.contentEquals(recipeName)) return true
                    }
                }
            } catch (exception: Exception) {
                Loge(
                    "assistedRecipe",
                    " error in traversing assisted: " + exception.message
                )
                exception.printStackTrace()
            }
            return false
        }

        /**
         * Method to set the required view holders and start provisioning
         *
         * @param view               view from navigation
         * @param isFromRemoteEnable true if navigated from remote enable option
         */
        fun startProvisioning(view: View?, @Suppress("UNUSED_PARAMETER") isFromRemoteEnable: Boolean, isFromConnectivityScreen: Boolean, isAoBProvisioning: Boolean) {
            if (!SettingsViewModel.getSettingsViewModel().isWifiEnabled) SettingsViewModel.getSettingsViewModel().setWifiEnabled(true)
            val provisioningManager = ProvisioningManager.getInstance()
            provisioningManager.bleConnectViewHolder = BleConnectViewHolder()
            provisioningManager.blePairViewHolder = BlePairViewHolder()
            provisioningManager.wifiConnectViewHolder = WifiConnectViewHolder()
            provisioningManager.errorViewHolder = ErrorViewHolder()
            provisioningManager.provisioningCompletedViewHolder = CompletedViewHolder()
            SettingsViewModel.getSettingsViewModel().resetWifi()
            if (SettingsViewModel.getSettingsViewModel().isUnboxing.value != null &&
                SettingsViewModel.getSettingsViewModel().isUnboxing.value == true
            ) {
                Logd("Unboxing", "isUnboxing =$isUnboxing")
                ProvisioningManager.getInstance().startProvisioningFromToolsMenu(
                    view,
                    R.id.action_unboxingConnectToNetworkFragment_to_provisioningBleConnect,
                    R.id.global_action_to_unboxingRegionalSettingsFragment,
                    R.id.global_action_to_unboxingRegionalSettingsFragment
                )
            } else {
                if (isAoBProvisioning) {
                    Logd("AoBProvisioning", "AoBProvisioning =$isAoBProvisioning")
                    ProvisioningManager.getInstance().startProvisioningFromToolsMenu(
                        view,
                        R.id.action_global_provisioningBleConnectAoB,
                        R.id.global_action_to_clockScreen, R.id.global_action_to_clockScreen
                    )
                }
                if (isFromConnectivityScreen) {
                    ProvisioningManager.getInstance().startProvisioningFromToolsMenu(
                        view,
                        R.id.action_connectivityListFragment_to_provisioningBleConnect,
                        R.id.global_action_to_clockScreen, R.id.global_action_to_clockScreen
                    )
                } else {
                    ProvisioningManager.getInstance().startProvisioningFromToolsMenu(
                        view,
                        R.id.action_settingsLandingFragment_to_provisioningBleConnect,
                        R.id.global_action_to_clockScreen, R.id.global_action_to_clockScreen
                    )
                }
            }
        }

        /**
         * Method to start AOB sever
         */
        fun startGattServer(fragment: Fragment?) {
            //Start the GATT server
            if (!SettingsManagerUtils.isApplianceProvisioned() &&
                !ProvisioningViewModel.getProvisioningViewModel().isServerStarted &&
                SettingsViewModel.getSettingsViewModel().controlLock.value == false &&
                SettingsViewModel.getSettingsViewModel().isWifiEnabled &&
                !isDemoModeEnabled() &&
                isApplianceIdleForProvisioning() &&
                !KitchenTimerVMFactory.isAnyKitchenTimerRunning()
            ) {
                Logi("Starting Server...")

                if (BuildInfo.isRunningOnEmulator() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Android 12 (SDK 31) or higher
                    if (ContextCompat.checkSelfPermission(
                            ContextProvider.getApplication(),
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        HMILogHelper.Logw("we do not start GATT server for Android SDK " + Build.VERSION.SDK_INT + " if we don't have BLUETOOTH_CONNECT permission granted")
                    } else {
                        Logi("calling start GATT server for Android SDK " + Build.VERSION.SDK_INT + " when we have BLUETOOTH_CONNECT permission granted")
                        startAOB()
                    }
                } else {
                    startAOB()
                }
            }
        }

        private fun startAOB() {
            Handler(Looper.getMainLooper()).postDelayed({
                val provisioningViewModel =
                    ProvisioningViewModel.getProvisioningViewModel()
                if (!provisioningViewModel.isServerStarted) {
                    Logi("Starting the BLE server from application side")
                    provisioningViewModel.startGattServer(FragmentProvisioningHome.FIRMWARE_REVISION)
                }
            }, 1000)
        }

        /**
         * Method to stop AOB sever
         */
        fun stopGattServer() {
            //stop the Gatt Server service.
            if (ProvisioningViewModel.getProvisioningViewModel().isServerStarted) {
                ProvisioningViewModel.getProvisioningViewModel().stopServer()
                Logi("GATT Server stopped successfully!!")
            }
        }

        fun appendPlusSignToString(item: String): String {
            val formattedString: String = if (!item.startsWith("-") && item.toInt() > 0) {
                "+$item"
            } else {
                item
            }
            return formattedString
        }


        /**
         * Sets HMI knob listener by displaying a popup dialog.
         * This function allows the selection of an HMI knob listener through a popup dialog.
         * @param fragment The currently visible fragment where the dialog will be displayed.
         */
        fun setHmiKnobListenerAfterDismissDialog(fragment: Fragment) {
            fragment.let {
                if (fragment is HMIKnobInteractionListener && fragment.isAdded) {
                    val knobListener = fragment as HMIKnobInteractionListener?
                    if (knobListener != null) {
                        HMIExpansionUtils.setHMIKnobInteractionListener(knobListener)
                    }
                }
            }
        }

        /**
         * Sets Meat probe listener by displaying a popup dialog.
         * This function allows the selection of an Meat probe listener through a popup dialog.
         * @param fragment The currently visible fragment where the dialog will be displayed.
         */
        @Suppress("unused")
        fun setMeatProbeListenerAfterDismissDialog(fragment: Fragment) {
            fragment.let {
                if (fragment is MeatProbeUtils.MeatProbeListener && fragment.isAdded) {
                    val hmiMeatProbeListener = fragment as MeatProbeUtils.MeatProbeListener?
                    if (hmiMeatProbeListener != null) {
                        MeatProbeUtils.setMeatProbeListener(hmiMeatProbeListener)
                    }
                }
            }
        }

        /**
         * change scope of view mode if recipe id is coming as null for COMBO and DOUBLE variants only
         *
         */
        fun changeScopeOfViewModelBasedOnRecipeId() {
            if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN || CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO) {
                val primaryCookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
                val secondaryCookingViewModel =
                    CookingViewModelFactory.getSecondaryCavityViewModel()
                val recipeRecord = CookBookViewModel.getInstance()
                    .getRecipeRecordById(CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.recipeId)
                // if current in scope model is only null then find which cavity view model has recipe loaded
                if (recipeRecord == null) {
                    val primaryRecord = CookBookViewModel.getInstance()
                        .getRecipeRecordById(primaryCookingViewModel.recipeExecutionViewModel.recipeId)
                    if (primaryRecord == null) {
                        val secondaryRecord = CookBookViewModel.getInstance()
                            .getRecipeRecordById(secondaryCookingViewModel.recipeExecutionViewModel.recipeId)
                        if (secondaryRecord != null) {
                            //secondary cavity found recipe record so set as in scope
                            CookingViewModelFactory.setInScopeViewModel(secondaryCookingViewModel)
                        }
                    } else {
                        //primary cavity found recipe record so set as in scope
                        CookingViewModelFactory.setInScopeViewModel(primaryCookingViewModel)
                    }
                }
            }
        }

        /**
         * Method to start Delay and handle recipe execution errors
         *
         * @param fragment         current Fragment for navigation
         * @param cookingViewModel CookingViewModel
         * @param isSabbath        true/false
         */
        @Suppress("unused")
        fun handleDelayErrorAndStartCooking(
            fragment: Fragment?,
            cookingViewModel: CookingViewModel,
            isSabbath: Boolean
        ) {
            val recipeErrorResponse =
                if (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED) cookingViewModel.recipeExecutionViewModel.overrideDelay() else cookingViewModel.recipeExecutionViewModel.startDelay()
            if (recipeErrorResponse == RecipeErrorResponse.NO_ERROR) {
                Logd("Cooking started from SDK ...")
                if (isSabbath) CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
                fragment?.let { navigateToStatusOrClockScreen(it) }
            } else {
                Loge("SDK not able to start cooking")
                fragment?.let {
                    handleDelayCookingError(
                        it,
                        cookingViewModel,
                        recipeErrorResponse,
                        isSabbath
                    )
                }
            }
        }

        fun handleDelayCookingError(
            fragment: Fragment,
            cookingViewModel: CookingViewModel,
            error: RecipeErrorResponse,
            isSabbathError: Boolean?
        ) {
            when (error) {
                RecipeErrorResponse.ERROR_RECIPE_START_NOT_ALLOWED -> cookingViewModel.let {
                    PopUpBuilderUtils.hotCavityCoolDownPopupBuilder(
                        fragment,
                        it,
                        getHotCavityTitleAndDescription(fragment,it, HOT_CAVITY_WARNING_COOLING_DOWN, HOT_CAVITY_WARNING_TITLE),
                        getHotCavityTitleAndDescription(fragment,it, HOT_CAVITY_WARNING_COOLING_DOWN, HOT_CAVITY_WARNING_DESCRIPTION),
                        cookingIsAllowed = false,
                        true,
                        isSabbathError
                    )
                }

                RecipeErrorResponse.ERROR_RECIPE_START_NOT_RECOMMENDED -> cookingViewModel.let {
                    PopUpBuilderUtils.hotCavityCoolDownPopupBuilder(
                        fragment,
                        it,
                        getHotCavityTitleAndDescription(fragment,it, HOT_CAVITY_WARNING_COOLING_DOWN, HOT_CAVITY_WARNING_TITLE),
                        getHotCavityTitleAndDescription(fragment,it, HOT_CAVITY_WARNING_COOLING_DOWN, HOT_CAVITY_WARNING_DESCRIPTION),
                        cookingIsAllowed = true,
                        true,
                        isSabbathError
                    )
                }


                RecipeErrorResponse.ERROR_RECIPE_DOOR_IS_NOT_CLOSED ->  cookingViewModel.let {
                    DoorEventUtils.cycleStartDoorOpenPopup(
                        fragment, it, true
                    )
                }
                RecipeErrorResponse.ERROR_MEAT_PROBE_NOT_INSERTED -> {
                    cookingViewModel.let {
                        PopUpBuilderUtils.insertMeatProbe(
                            fragment, it
                        ) {}
                    }
                }

                else -> if (isSabbathError == true) {
                    cookingViewModel.recipeExecutionViewModel.cancel()
                    Logd(
                        "CookingAppUtils recipe cancel",
                        "Canceling ${cookingViewModel.cavityName.value} in handleDelayCookingError"
                    )
                    PopUpBuilderUtils.runningFailPopupBuilder(fragment)
                } else {
                    //Showing toast in case of error during sabbath cooking.
                    Toast.makeText(fragment.requireContext(), error.description, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        /**
         * Method to start cooking cycle and handle recipe execution errors
         *
         * @param fragment         current Fragment for navigation
         * @param cookingViewModel CookingViewModel
         * @param isSabbath        true/false
         */
        fun handleErrorAndStartCooking(
            fragment: Fragment?,
            cookingViewModel: CookingViewModel,
            isSabbath: Boolean,
            @Suppress("UNUSED_PARAMETER")
            isQuickstartRecipe: Boolean
        ) {
            if (cookingViewModel.recipeExecutionViewModel.isRunning) {
                Logd("Cycle is already runing so not need to execute recipe")
            } else {
                Logd("Cycle execute recipe from handleErrorAndStartCooking")
                val recipeErrorResponse: RecipeErrorResponse =
                    cookingViewModel.recipeExecutionViewModel.execute()
                if (recipeErrorResponse == RecipeErrorResponse.NO_ERROR) {
                    Logd("Cooking started from SDK ...")
                    if (isSabbath && CookingViewModelFactory.getProductVariantEnum() != CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN) CookingViewModelFactory.setInScopeViewModel(
                        cookingViewModel
                    )
                    fragment?.let {
                        navigateToStatusOrClockScreen(
                            it
                        )
                    }
                    HMIExpansionUtils.startOrStopKnobLEDFadeInLightAnimation(true)
                } else {
                    Loge("SDK not able to start cooking")
                    fragment?.let {
                        handleCookingError(
                            it,
                            cookingViewModel,
                            recipeErrorResponse,
                            isSabbath
                        )
                    }
                }
            }
        }

        /**
         * calls when on click of clock screen or rotating the knob
         * in case of single oven/mwo navigate directly to recipe selection and for other variants move to cavity selection
         */
        fun openCavitySelectionScreen(fragment: Fragment, isFromHmiButton:Boolean = false) {
            Logd(fragment.tag, "opening cavity selection screen")
            if (SettingsViewModel.getSettingsViewModel().controlLock.value == true){
                navigateSafely(
                    fragment,
                    R.id.action_to_controlUnlockFragment,
                    null,
                    null
                )
            }
            val productVariantEnum: CookingViewModelFactory.ProductVariantEnum =
                CookingViewModelFactory.getProductVariantEnum()
            val navBuilder = NavOptions.Builder()
            navBuilder.setEnterAnim(R.anim.fade_in).setExitAnim(R.anim.fade_out)
                .setPopEnterAnim(R.anim.fade_in).setPopExitAnim(R.anim.fade_out)
            if (productVariantEnum == CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN || productVariantEnum == CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN) {
                HMIExpansionUtils.setLightForCancelButton(true)
                NavigationUtils.navigateToUpperRecipeSelection(fragment)
            } else {
                Logd("HMI_KEY","action_global_navigate_to_cavitySelectionFragment")
                if (isFromHmiButton) {
                    navigateSafely(
                        fragment, R.id.action_global_cavity_selection,
                        null, null
                    )
                }else {
                    navigateSafely(
                        fragment, R.id.action_clockFragment_to_manualmodeCavitySelectionFragment,
                        null, null
                    )
                }
            }
        }

        /**
         * Method to cancel if any recipe is running on both cavities
         */
        fun cancelIfAnyRecipeIsRunning() {
            when(CookingViewModelFactory.getProductVariantEnum()){
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    if(CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.isRunning) {
                        Logd("HMIExpansionUtils", "Cancelling SECONDARY recipeExecutionViewModel")
                        CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.cancel()
                    }
                }
                else -> {}
            }
            if(CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.isRunning) {
                Logd("HMIExpansionUtils", "Cancelling PRIMARY recipeExecutionViewModel")
                CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.cancel()
            }
        }

        /**
         * Method to cancel Kitchen Timers if available
         */
        fun cancelIfAnyKitchenTimersRunning() {
            if (KitchenTimerVMFactory.getKitchenTimerViewModels() != null && (KitchenTimerVMFactory.getKitchenTimerViewModels()?.size
                    ?: 0) > 0
            ) {
                cancelKitchenTimers()
            }
        }

        /**
         * Method to cancel existing Kitchen Timers
         */
        private fun cancelKitchenTimers(): Boolean {
            Logd("Cancelling all kitchen timers")
            var allTimersStopped = true
            KitchenTimerVMFactory.getKitchenTimerViewModels().let {
                if (it != null) {
                    for (ktModel in it) {
                        if (Objects.equals(
                                ktModel.timerStatus.value,
                                KitchenTimerViewModel.TimerStatus.PAUSED
                            ) ||
                            Objects.equals(
                                ktModel.timerStatus.value,
                                KitchenTimerViewModel.TimerStatus.RUNNING
                            )
                        ) {
                            if (ktModel.stopTimer()) {
                                Logi("Kitchen timer stopped: " + ktModel.timerName)
                            } else {
                                Logi("Failed to stop Kitchen timer: " + ktModel.timerName)
                                allTimersStopped = false
                            }
                        }
                    }
                }
            }

            return allTimersStopped
        }

        fun isAnyCycleRunning(): Boolean {
            var cycleRunning = false
            // When brownout happens on the cycle complete screen, recipeCookingState will be Idle. This logic is added
            // to handle the bronout scenaro in the case of cycle complete screen
            if ((CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeCookingState?.value != RecipeCookingState.IDLE) ||
                (RecipeExecutionState.DELAYED == CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value) ||
                (((CookingViewModelFactory.getPowerInterruptState() == PowerInterruptState.BROWNOUT) &&
                        (RecipeCookingState.IDLE == CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeCookingState?.value) &&
                        (RecipeExecutionState.RUNNING_EXT == CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value)))
            ) {
                cycleRunning = true
            }

            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO ->
                    if ((CookingViewModelFactory.getSecondaryCavityViewModel()?.recipeExecutionViewModel?.recipeCookingState?.value != RecipeCookingState.IDLE) ||
                        (RecipeExecutionState.DELAYED == CookingViewModelFactory.getSecondaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value) ||
                        (((CookingViewModelFactory.getPowerInterruptState() == PowerInterruptState.BROWNOUT) &&
                                (RecipeCookingState.IDLE == CookingViewModelFactory.getSecondaryCavityViewModel()?.recipeExecutionViewModel?.recipeCookingState?.value) &&
                                (RecipeExecutionState.RUNNING_EXT == CookingViewModelFactory.getSecondaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value)))
                    ) {
                        cycleRunning = true
                    }

                else -> {}
            }
            return cycleRunning
        }

        fun isProductInRecipePauseState(): Boolean {
            var isProductPaused = false

            if ((RecipeExecutionState.PAUSED == CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value) ||
                (RecipeExecutionState.PAUSED_EXT == CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value) ||
                (RecipeExecutionState.PAUSED_FAILED == CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value)
            ) {
                isProductPaused = true
            }

            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    if ((RecipeExecutionState.PAUSED == CookingViewModelFactory.getSecondaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value) ||
                        (RecipeExecutionState.PAUSED_EXT == CookingViewModelFactory.getSecondaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value) ||
                        (RecipeExecutionState.PAUSED_FAILED == CookingViewModelFactory.getSecondaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value)
                    ) {
                        isProductPaused = true
                    }
                }

                else -> {}
            }
            return isProductPaused
        }

        fun isProductInIdleState(): Boolean {
            var isProductIdle = false

            if ((RecipeExecutionState.IDLE == CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value) &&
                (CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeCookingState?.value == RecipeCookingState.IDLE)
            ) {
                isProductIdle = true
            }

            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    if ((RecipeExecutionState.IDLE == CookingViewModelFactory.getSecondaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value) &&
                        (CookingViewModelFactory.getSecondaryCavityViewModel()?.recipeExecutionViewModel?.recipeCookingState?.value == RecipeCookingState.IDLE)
                    ) {
                        isProductIdle = true
                    }
                }

                else -> {}
            }
            return isProductIdle
        }

        /**
         * to check if appliance is busy or not
         *
         * @return true if busy and false other wise (safe to do OTA and enter into sleep mode)
         */
        fun checkApplianceBusyState(): Boolean {
            val isAnyKitchenTimerRunning = KitchenTimerVMFactory.isAnyKitchenTimerRunning()
            val productVariantEnum = CookingViewModelFactory.getProductVariantEnum()
            val applianceBusyPrimaryCavity: Boolean = checkApplianceBusyStateBasedOnCavity(
                CookingViewModelFactory.getPrimaryCavityViewModel()
            )
            Logd(
                TAG,
                "applianceBusyPrimaryCavity: $applianceBusyPrimaryCavity"
            )
            if (applianceBusyPrimaryCavity) return true
            if (productVariantEnum == CookingViewModelFactory.ProductVariantEnum.COMBO ||
                productVariantEnum == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
            ) {
                val applianceBusySecondaryCavity: Boolean =
                    checkApplianceBusyStateBasedOnCavity(
                        CookingViewModelFactory.getSecondaryCavityViewModel()
                    )
                Logd(
                    TAG,
                    "applianceBusySecondaryCavity: $applianceBusySecondaryCavity"
                )
                if (applianceBusySecondaryCavity) return true
            }
            if (isSabbathMode()) {
                Logd(
                    TAG,
                    "Make Appliance Busy As TRUE isSabbathMode enable"
                )
                return true
            }
            Logd(
                TAG,
                "isKitchenTimersRunning: $isAnyKitchenTimerRunning"
            )
            return isAnyKitchenTimerRunning
        }

        /**
         * To check if appliance is running any process in the background by cavity for ex Recipe,
         * Cooking State, Cook Timer, cavity temperature, cooling fan.
         * @param cookingViewModel ViewModel that drive, control and observe the appliance's cooking process
         * @return true if cavity is busy, false otherwise
         */
        private fun checkApplianceBusyStateBasedOnCavity(cookingViewModel: CookingViewModel?): Boolean {
            val recipeExecutionState =
                cookingViewModel?.recipeExecutionViewModel?.recipeExecutionState?.value
            val recipeCookingState =
                cookingViewModel?.recipeExecutionViewModel?.recipeCookingState?.value
            val cookTimerState = cookingViewModel?.recipeExecutionViewModel?.cookTimerState?.value
            val ovenTemperatureInCelsius = cookingViewModel?.ovenTemperatureInCelsius?.value
            if (recipeExecutionState != RecipeExecutionState.IDLE ||
                recipeCookingState != RecipeCookingState.IDLE || cookTimerState != Timer.State.IDLE
            ) {
                Logd(
                    TAG,
                    "Set Appliance Busy as TRUE RecipeExecutionState: " + recipeExecutionState +
                            "RecipeCookingState: " + recipeCookingState +
                            "CookTimerState: " + cookTimerState
                )
                return true
            }
            if (ovenTemperatureInCelsius != null &&
                ovenTemperatureInCelsius > AppConstants.OVEN_SAFE_TEMPERATURE_CELSIUS_VALUE
            ) {
                Logd(
                    TAG,
                    "Set Appliance Busy as TRUE OvenTemperatureInCelsius: $ovenTemperatureInCelsius"
                )
                return true
            }
            Logd(
                TAG,
                "CoolingFanState: " + cookingViewModel.coolingFanState?.value
            )
            return java.lang.Boolean.TRUE == cookingViewModel.coolingFanState?.value
        }

        /**
         * is Sabbath Mode enabled or not
         *
         * @return true/false
         */
        fun isSabbathMode(): Boolean {
            return SettingsViewModel.getSettingsViewModel().sabbathMode.value ==
                    SettingsViewModel.SabbathMode.SABBATH_COMPLIANT
        }

        /**
         * Common function to update recipe name text with parameters
         *
         * @param cookingVM
         * @return text to be updated on status widget
         */
        fun getRecipeNameWithParameters(context: Context, cookingVM: CookingViewModel): String {
            var recipeName = getRecipeNameText(
                context,
                cookingVM.recipeExecutionViewModel.recipeName.value.toString()
            )
            if (isRequiredTargetAvailable(cookingVM, RecipeOptions.DONENESS) && isRequiredTargetAvailable(
                    cookingVM,
                    RecipeOptions.TARGET_TEMPERATURE
                )) {
                recipeName = getRecipeModeWithTemperatureAsString(
                    context,
                    recipeName,
                    cookingVM.recipeExecutionViewModel.targetTemperature.value,
                    cookingVM
                )?:EMPTY_STRING
                val setDoneNess = context.getString(
                    getResIdFromResName(
                        context,
                        AppConstants.TEXT_DONENESS_TILE + cookingVM.recipeExecutionViewModel.donenessOption.value?.defaultString,
                        ToolsMenuJsonKeys.RESOURCE_TYPE_STRING
                    )
                )
                recipeName = "$recipeName | $setDoneNess"
            }else if (isRequiredTargetAvailable(
                    cookingVM,
                    RecipeOptions.TARGET_TEMPERATURE
                )
            ) {
                recipeName = getRecipeModeWithTemperatureAsString(
                    context,
                    recipeName,
                    cookingVM.recipeExecutionViewModel.targetTemperature.value,
                    cookingVM
                )?:EMPTY_STRING
            } else if (cookingVM.isOfTypeMicrowaveOven && isRequiredTargetAvailable(
                    cookingVM,
                    RecipeOptions.MWO_POWER_LEVEL
                )
            ) {
                recipeName = getRecipeModeWithPowerLevelAsString(
                    context,
                    recipeName,
                    getMWOPowerLevel(cookingVM.recipeExecutionViewModel)
                )
            }else if (cookingVM.isOfTypeMicrowaveOven && isRequiredTargetAvailable(
                    cookingVM,
                    RecipeOptions.DONENESS
                )
            ) {
                val setDoneNess = context.getString(
                    getResIdFromResName(
                        context,
                        AppConstants.TEXT_DONENESS_TILE + cookingVM.recipeExecutionViewModel.donenessOption.value?.defaultString,
                        ToolsMenuJsonKeys.RESOURCE_TYPE_STRING
                    )
                )
                recipeName = "$recipeName | $setDoneNess"
            }
            return recipeName
        }

        /**
         * Common function to update recipe name text with parameters
         *
         * @param cookingVM
         * @return text to be updated on status widget
         */
        fun getRecipeNameWithParametersVision(context: Context, cookingVM: CookingViewModel): String {
            var recipeName = getRecipeNameText(
                context,
                cookingVM.recipeExecutionViewModel.recipeName.value.toString()
            )
            if (isRequiredTargetAvailable(cookingVM, RecipeOptions.VIRTUAL_CHEF)) {
                val setDoneNess = context.getString(
                    getResIdFromResName(
                        context,
                        AppConstants.TEXT_DONENESS_TILE + cookingVM.recipeExecutionViewModel.donenessOption.value?.defaultString,
                        ToolsMenuJsonKeys.RESOURCE_TYPE_STRING
                    )
                )
                recipeName = "$recipeName | $setDoneNess"
            }
            return recipeName
        }

        /**
         * Common function to update recipe name text with parameters
         *
         * @param cookingVM
         * @return text to be updated on status widget
         */
        fun getRecipeNameWithParametersFarView(
            context: Context,
            ovenDisplayTemperature: Int,
            targetTemperature: Int,
            cookingVM: CookingViewModel
        ): String {
            var recipeName = ""
            if (isRequiredTargetAvailable(cookingVM, RecipeOptions.DONENESS) && isRequiredTargetAvailable(
                    cookingVM,
                    RecipeOptions.TARGET_TEMPERATURE
                )) {
                Logd("FAR_VIEW","----------- DONENESS -------> ")
                val modeName = getRecipeModeWithTemperatureAsStringFarView(
                    context,
                    recipeName,
                    cookingVM.recipeExecutionViewModel.targetTemperature.value,
                    cookingVM
                )?:EMPTY_STRING
                recipeName = buildString {
                    append(ovenDisplayTemperature)
                    append(DEGREE_SYMBOL)
                    append(SYMBOL_FORWARD_SLASH)
                    append(modeName)

                }

                val setDoneNess = context.getString(
                    getResIdFromResName(
                        context,
                        AppConstants.TEXT_DONENESS_TILE + cookingVM.recipeExecutionViewModel.donenessOption.value?.defaultString,
                        ToolsMenuJsonKeys.RESOURCE_TYPE_STRING
                    )
                )
                recipeName = "$recipeName | $setDoneNess"
            }else if (isRequiredTargetAvailable(
                    cookingVM,
                    RecipeOptions.TARGET_TEMPERATURE
                )
            ) {
                Logd("FAR_VIEW","----------- TARGET_TEMPERATURE -------> ")
                if (!isTemperatureMapTextValue(cookingVM.recipeExecutionViewModel.targetTemperatureOptions.value)) {
                    val modeName = getRecipeModeWithTemperatureAsStringFarView(
                        context,
                        recipeName,
                        cookingVM.recipeExecutionViewModel.targetTemperature.value,
                        cookingVM
                    )?:EMPTY_STRING
                    recipeName = buildString {
                        append(ovenDisplayTemperature)
                        append(DEGREE_SYMBOL)
                        append(SYMBOL_FORWARD_SLASH)
                        append(modeName)

                    }
                    val isTemperatureReached = (ovenDisplayTemperature >= targetTemperature)
                    if (isTemperatureReached) {
                        recipeName = buildString {
                            append(targetTemperature)
                            append(DEGREE_SYMBOL)
                        }
                    }
                    Logd("FAR_VIEW","----------- Temperature Text -------> $recipeName")
                } else {
                    recipeName = getRecipeModeWithTemperatureAsStringFarView(
                        context,
                        recipeName,
                        cookingVM.recipeExecutionViewModel.targetTemperature.value,
                        cookingVM
                    )?:EMPTY_STRING
                    Logd("FAR_VIEW","----------- Temperature else Text -------> $recipeName")
                }

            } else if (cookingVM.isOfTypeMicrowaveOven && isRequiredTargetAvailable(
                    cookingVM,
                    RecipeOptions.MWO_POWER_LEVEL
                )
            ) {
                Logd("FAR_VIEW","----------- MWO_POWER_LEVEL -------> ")
                recipeName = getRecipeModeWithPowerLevelAsString(
                    context,
                    recipeName,
                    getMWOPowerLevel(cookingVM.recipeExecutionViewModel)
                )
            }else if (cookingVM.isOfTypeMicrowaveOven && isRequiredTargetAvailable(
                    cookingVM,
                    RecipeOptions.DONENESS
                )
            ) {
                Logd("FAR_VIEW","----------- MicrowaveOven  DONENESS -------> ")
                val setDoneNess = context.getString(
                    getResIdFromResName(
                        context,
                        AppConstants.TEXT_DONENESS_TILE + cookingVM.recipeExecutionViewModel.donenessOption.value?.defaultString,
                        ToolsMenuJsonKeys.RESOURCE_TYPE_STRING
                    )
                )
                recipeName = "$recipeName | $setDoneNess"
            } else {
                Logd("FAR_VIEW","----------- Other Recipe -------> ")
                recipeName = getRecipeNameText(
                    context,
                    cookingVM.recipeExecutionViewModel.recipeName.value.toString()
                )
            }
            return recipeName
        }
        /**
         * Method to check whether the ota error code is fetal code or not
         * fetal error code wil be in range 207-211, 500
         */
        fun isFetalError(): Boolean {
            val otaViewModel = OTAVMFactory.getOTAViewModel() ?: return false

            val errorCode =
                if (otaViewModel.errorCode != null) otaViewModel.errorCode.value else null
            Logd("isFetalError", "Fetal Error Code $errorCode")
            if (errorCode == null) {
                return false
            }

            return ((errorCode >= AppConstants.OTA_ERROR_CODE_FATAL_MIN_RANGE
                    && errorCode <= AppConstants.OTA_ERROR_CODE_FATAL_MAX_RANGE)
                    || errorCode == AppConstants.OTA_ERROR_CODE_BRICK_STATE)
        }


        /**
         * Should be called based on every observer that is responsible for Appliance state
         */
        fun setApplianceOtaState(): Boolean {
            val isApplianceBusy: Boolean = checkApplianceBusyStateForOTA()
            Logd(TAG, "OTA : checking appliance isBusy : $isApplianceBusy")
            OTAVMFactory.getOTAViewModel().setApplianceBusyState(isApplianceBusy)
            return isApplianceBusy
        }

        /**
         * to check if appliance is busy or not
         *
         * @return true if busy and false other wise (safe to do OTA)
         */
        fun checkApplianceBusyStateForOTA(): Boolean {
            val productVariantEnum = CookingViewModelFactory.getProductVariantEnum()
            val applianceBusyPrimaryCavity: Boolean =
                checkApplianceBusyStateBasedOnCavityForOta(CookingViewModelFactory.getPrimaryCavityViewModel())
            Logd(TAG, "OTA : applianceBusyPrimaryCavity: $applianceBusyPrimaryCavity")
            if (applianceBusyPrimaryCavity) return true
            if (productVariantEnum == CookingViewModelFactory.ProductVariantEnum.COMBO || productVariantEnum == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
                val applianceBusySecondaryCavity: Boolean =
                    checkApplianceBusyStateBasedOnCavityForOta(CookingViewModelFactory.getSecondaryCavityViewModel())
                Logd(TAG, "OTA : applianceBusySecondaryCavity: $applianceBusySecondaryCavity")
                if (applianceBusySecondaryCavity) return true
            }
            if (isSabbathMode()) {
                Logd(TAG, "OTA : Make Appliance Busy As TRUE isSabbathMode enable")
                return true
            }
            Logd(TAG, "OTA : isKitchenTimersRunning: " + KitchenTimerUtils.isKitchenTimersRunning())
            return isAllKitchenTimerIsIDLE() != 0
        }

        private fun isAllKitchenTimerIsIDLE(): Int {
            var count = 0
            for (kitchenTimerViewModel in KitchenTimerVMFactory.getKitchenTimerViewModels()!!) {
                if (Objects.requireNonNull(kitchenTimerViewModel.timerStatus.value) !=
                    (KitchenTimerViewModel.TimerStatus.IDLE)
                ) {
                    count++
                }
            }
            return count
        }

        /**
         * To check if appliance is running any process in the background by cavity for ex Recipe, Cooking State, Cook Timer, cavity temperature, cooling fan
         *
         * @param cookingViewModel ViewModel that drive, control and observe the appliance's cooking process
         * @return true if cavity is busy, false otherwise
         */
        private fun checkApplianceBusyStateBasedOnCavityForOta(cookingViewModel: CookingViewModel?): Boolean {
            if (cookingViewModel != null) {
                if (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.IDLE ||
                    cookingViewModel.recipeExecutionViewModel.recipeCookingState.value != RecipeCookingState.IDLE ||
                    cookingViewModel.recipeExecutionViewModel.cookTimerState.value != Timer.State.IDLE
                ) {
                    Logd(
                        TAG,
                        "OTA : Set Appliance Busy as TRUE RecipeExecutionState: " + cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value +
                                "RecipeCookingState: " + cookingViewModel.recipeExecutionViewModel.recipeCookingState.value +
                                "CookTimerState: " + cookingViewModel.recipeExecutionViewModel.cookTimerState.value
                    )
                    return true
                }
                if (cookingViewModel.ovenTemperatureInCelsius != null && cookingViewModel.ovenTemperatureInCelsius.value != null
                    && (cookingViewModel.ovenTemperatureInCelsius?.value!! > AppConstants.OVEN_SAFE_TEMPERATURE_CELSIUS_VALUE)
                ) {
                    Logd(
                        TAG,
                        "OTA : Set Appliance Busy as TRUE OvenTemperatureInCelsius: " +
                                cookingViewModel.ovenTemperatureInCelsius.value
                    )
                    return true
                }
                Logd(TAG, "OTA : CoolingFanState: " + cookingViewModel.coolingFanState.value)
                return cookingViewModel.coolingFanState != null && java.lang.Boolean.TRUE ==
                        cookingViewModel.coolingFanState.value
            }
            return false
        }


        /**
         * to check if appliance is busy or not
         *
         * @return false if not Idle and true other wise (safe to do provisioning)
         */
        fun isApplianceIdleForProvisioning(): Boolean {
            val productVariantEnum = CookingViewModelFactory.getProductVariantEnum()
            if (isApplianceInBusyStateForProvisioning(
                    CookingViewModelFactory.getPrimaryCavityViewModel()
                )
            ) return false
            if (productVariantEnum == CookingViewModelFactory.ProductVariantEnum.COMBO
                || productVariantEnum == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
            ) {
                if (isApplianceInBusyStateForProvisioning(
                        CookingViewModelFactory.getSecondaryCavityViewModel()
                    )
                ) return false
            }
            return !isSabbathMode()
        }

        /**
         * To check if appliance is running any process in the background by cavity for ex Recipe, Cooking State, Cook Timer
         *
         * @param cookingViewModel ViewModel that drive, control and observe the appliance's cooking process
         * @return true if busy, false otherwise
         */
        private fun isApplianceInBusyStateForProvisioning(cookingViewModel: CookingViewModel?): Boolean {
            return cookingViewModel != null &&
                    (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.IDLE
                            || cookingViewModel.recipeExecutionViewModel.recipeCookingState.value != RecipeCookingState.IDLE
                            || cookingViewModel.recipeExecutionViewModel.cookTimerState.value != Timer.State.IDLE)
        }


        fun extractSecondaryTextDescriptionFileContent(description: String?): String {
            val jsonObject: JSONObject
            var secondaryText = ""
            try {
                if (description != null) {
                    jsonObject = JSONObject(description)
                    if (jsonObject.has(AppConstants.OTA_JSON_KEY_SECONDARY_TEXT)) {
                        secondaryText = jsonObject.getString(AppConstants.OTA_JSON_KEY_SECONDARY_TEXT)
                        if (secondaryText != "") {
                            secondaryText = secondaryText.replace("[", "")
                            secondaryText = secondaryText.replace("]", "")
                            secondaryText = secondaryText.replace("\"", "")
                            val arrayChar =
                                secondaryText.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()

                            val stringBuilder = java.lang.StringBuilder()
                            for (text in arrayChar) {
                                stringBuilder.append("\u2022  $text").append("\r\n")
                            }
                            secondaryText = stringBuilder.toString() + "\n"
                        }
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return secondaryText
        }

        /**
         * Method to set the navigation graph based on variant
         *
         * @param fragment fragment for navigation
         */
        fun setCookFlowGraphBasedOnVariant(view: View) {
            var navGraphDestination = -1
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> navGraphDestination =
                    R.navigation.manual_cooking_single_oven

                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> navGraphDestination =
                    R.navigation.manual_cooking_mwo_oven

                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> navGraphDestination =
                    R.navigation.manual_cooking_double_oven

                CookingViewModelFactory.ProductVariantEnum.COMBO -> navGraphDestination =
                    R.navigation.manual_cooking_combo

                else -> {}
            }
            if (navGraphDestination > 0) {
                Loge("OTA: Nav graph set destination successfully")
                Navigation.findNavController(view).setGraph(navGraphDestination)
            } else {
                Loge("OTA: Nav graph destination not available")
            }
        }

        /**
         * Navigate to next screen based on entered values
         *
         * @param timeValue         timeString to update it in Settings viewmodel
         * @param settingsViewModel settings viewmodel to store the time data
         */
        fun updateDefaultTimeOnBlackOut(timeValue: Date?, settingsViewModel: SettingsViewModel) {
            if (timeValue != null) {
                val calendar = Calendar.getInstance()
                calendar.time = timeValue
                //Calendar time needs to be 00:00/12:00 AM so by default assigning 24hr format, 12 values handled in display part
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.MINUTE] = 0
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0
                settingsViewModel.setTimeModeManual(calendar.time)
            } else {
                Loge("Unable to set the default time")
            }
        }

        /**
         *  to check if the recipe is executed from Hot cavity popup
         * @param cookingVM
         * @return false if do not want to show hot cavity/oven cooling message on status screen, true means oven cooling message would be shown is oven current temperature is greater that target temperature
         */
        fun isRecipeAllowedForHotCavity(cookingVM: CookingViewModel): Boolean {
            return try {
                if(SabbathUtils.isSabbathRecipe(cookingVM)) return false
                val recipeRecord = CookBookViewModel.getInstance()
                    .getDefaultRecipeRecordByNameAndCavity(
                        cookingVM.recipeExecutionViewModel.recipeName.value,
                        cookingVM.cavityName.value
                    )
                val whrStartNotAllowed = JSONObject(recipeRecord.content).getJSONObject(AppConstants.KEY_WHR_PRE_START_CONFIGURATION).getBoolean(AppConstants.KEY_WHR_START_NOT_ALLOWED)
                Logd("HotCavity", "${cookingVM.recipeExecutionViewModel.recipeName.value} whrStartNotAllowed $whrStartNotAllowed")
                return !whrStartNotAllowed
            } catch (e: JSONException) {
                Loge(
                    "HotCavity",
                    "${cookingVM.recipeExecutionViewModel.recipeName.value} is not design for Hot Cavity message " + e.message
                )
                false
            }
        }

        /**
         * to check if the current oven temperature is hotter than target temperature
         *
         * @param cookingVM
         * @return true if a particular cavity is hot, false otherwise
         */
        fun isCavityHot(cookingVM: CookingViewModel): Boolean {
            val maxAllowedTemperature = cookingVM.recipeExecutionViewModel?.maxStartTemperature
            if (maxAllowedTemperature == DEFAULT_MAX_START_TEMPERATURE){
                return false
            }
            val ovenCurrentTemperature = cookingVM.ovenTemperature.value
            val offset =
                if (SettingsViewModel.getSettingsViewModel().temperatureUnit.getValue() == SettingsViewModel.TemperatureUnit.CELSIUS) HOT_CAVITY_WARNING_OFFSET_CELCIUS
                else HOT_CAVITY_WARNING_OFFSET_FAHRENHEIT
            Logd("AbstractStatusFragment HotCavity", "maxAllowedTemperature $maxAllowedTemperature, ovenCurrentTemperature $ovenCurrentTemperature ")
            return (ovenCurrentTemperature ?: 0) > (maxAllowedTemperature?.toInt()?.minus(offset) ?: 0)
        }

        /**
         * to check if the current oven temperature is less than target temperature
         *
         * @param cookingVM
         * @return true if a particular cavity is cool, false otherwise
         */
        fun isCavityCooled(cookingVM: CookingViewModel): Boolean {
            val targetTemperature = cookingVM.recipeExecutionViewModel.targetTemperature.value
            val ovenCurrentTemperature = cookingVM.ovenTemperature.value
            Logd("AbstractStatusFragment HotCavity", "targetTemperature $targetTemperature, ovenCurrentTemperature $ovenCurrentTemperature ")
            return (ovenCurrentTemperature ?: 0) <= (targetTemperature ?: 0)
        }

        fun getTypeFace(context: Context, font : Int) : Typeface? {
            return ResourcesCompat.getFont(context, font)
        }

        /*Method to manage OTA complete state*/
        fun setOTACompleteStatus(state: Boolean) {
            isOTAComplete.value = state
        }

        /*
        *
        * Method to get OTA complete state
        */
        fun getOTACompleteComplete(): LiveData<Boolean> {
            return isOTAComplete
        }

        fun setActiveNotificationChanged(state: Boolean) {
            activeNotificationChanged.value = state
        }

        fun getActiveNotificationChanged(): LiveData<Boolean> {
            return activeNotificationChanged
        }

        /**
         * Method is responsible for is probe recipe and targte meat probe temperature is rechaed or not
         * return - true/false
         */
        fun isProbeBasedRecipeAndTemperatureReached(recipeViewModel: RecipeExecutionViewModel?): Boolean {
            return recipeViewModel?.isProbeBasedRecipe == true
                    && recipeViewModel.targetMeatProbeTemperatureReached?.value == true

        }

        /**
         * Method responsible for providing integer range for probe extended cycle
         * 99 hr
         */
        fun provideProbeIntegerRange(): IntegerRange {
            // min 1 minute and max is 12 hours
            val maxTime = AppConstants.MAX_COUNT_PROBE_TIMER_IN_SECONDS.toDouble()
            val minTime = AppConstants.ADD_COOK_TIME_ONE_MINUTE.toDouble()
            val integerRange = IntegerRange()
            integerRange.setMax(maxTime)
            integerRange.setMin(minTime)
            return integerRange
        }
        fun getTimeoutValueBasedOnStringLength(context: Context, text: String): Int {
            return min(text.length * context.resources.getInteger(R.integer.integer_range_50)+ context.resources.getInteger(R.integer.ms_2000),context.resources.getInteger(R.integer.ms_7000))
        }

        fun isControlUnlockScreen(fragmentManager: FragmentManager): Boolean {
            return (getVisibleFragment(fragmentManager)?.id == R.id.controlUnlockFragment)
        }

        /*
        * Set left and right pop up button background null
        * */
        fun setLeftAndRightButtonBackgroundNull(
            leftTextButton: View?, rightTextButton: View?
        ) {
            leftTextButton?.background = leftTextButton?.context.let {
                it?.let { it1 ->
                    ContextCompat.getDrawable(
                        it1, R.drawable.text_view_ripple_effect
                    )
                }
            }
            rightTextButton?.background = rightTextButton?.context.let {
                it?.let { it1 ->
                    ContextCompat.getDrawable(
                        it1, R.drawable.text_view_ripple_effect
                    )
                }
            }
        }

        /**
         * Method to save cancel button press during self clean
         */
        fun setCancelButtonPressDuringSelfClean(isCancelledPress:String) {
            val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
                    ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
            )
            sharedPreferenceUtils?.saveValue(
                    AppConstants.CANCEL_BUTTON_PRESS_DURING_SELF_CLEAN, isCancelledPress)
        }

        /**
         * Method to get cancel button is pressed during self clean. It is required if brownout happen during self clean
         */
        fun getCancelButtonPressDuringSelfClean(): String? {
            val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
                    ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
            )
            return sharedPreferenceUtils?.getValue(
                    AppConstants.CANCEL_BUTTON_PRESS_DURING_SELF_CLEAN, FALSE_CONSTANT)
        }

        /**
         * Reset learn more instructions when app reset happens or
         *
         * @param cavityType            Cavity Type Primary/Secondary
         * @param cookBookViewModel     Cooking View model
         * @param lifeCycleOwnerFragment  lifecycle owner for observe the recipe record cavity
         */
        fun resetInstructionsForCavity(
            cavityType: String?,
            cookBookViewModel: CookBookViewModel,
            lifeCycleOwnerFragment: Fragment?
        ) {
            lifeCycleOwnerFragment.let {
                it?.viewLifecycleOwner?.let { it1 ->
                    cookBookViewModel.getDefaultRecipeRecordsByCavity(cavityType).observe(
                        it1
                    ) { recipes: List<RecipeRecord> ->
                        for (recipe in recipes) {
                            if (!recipe.showInstruction) {
                                cookBookViewModel.setShowInstruction(recipe.name, cavityType, true)
                            }
                        }
                    }
                }
            }

        }
        fun isTechnicianModeEnabled(): Boolean {
            return isUnboxing && SharedPreferenceManager.getCurrentUserRoleIntoPreference() == AppConstants.TRUE_CONSTANT
        }

        /**
         * Method responsible for set the cooking navigation graph and navigate to clock screen
         */
        fun setNavGraphAndNavigateToClock(fragment: Fragment) {
            @Suppress("ComplexRedundantLet")
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN ->
                    (fragment.let {
                        getViewSafely(
                            it
                        )
                    } ?: fragment.requireView()).let {
                        Navigation.findNavController(
                            it
                        ).setGraph(R.navigation.manual_cooking_single_oven)
                    }

                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN ->
                    (fragment.let {
                        getViewSafely(
                            it
                        )
                    } ?: fragment.requireView()).let {
                        Navigation.findNavController(
                            it
                        ).setGraph(R.navigation.manual_cooking_mwo_oven)
                    }

                CookingViewModelFactory.ProductVariantEnum.COMBO ->
                    (fragment.let {
                        getViewSafely(
                            it
                        )
                    } ?: fragment.requireView()).let {
                        Navigation.findNavController(
                            it
                        ).setGraph(R.navigation.manual_cooking_combo)
                    }

                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN ->
                    (fragment.let {
                        getViewSafely(
                            it
                        )
                    } ?: fragment.requireView()).let {
                        Navigation.findNavController(
                            it
                        ).setGraph(R.navigation.manual_cooking_double_oven)
                    }

                else -> Loge("Null Variant", "Variant not handled")
            }
            navigateSafely(
                fragment,
                R.id.global_action_to_clockScreen,
                null,
                navOptions {
                    popUpTo(R.id.global_action_to_clockScreen) {
                        inclusive = true
                    }
                }
            )
        }

/******************* Date and Time methods ********************/

        /**
         * string to Integer values
         *
         * @param valueString string value to convert to int
         * @return inter value
         */
        fun convertStringToInt(valueString: String?):Int {
            if (valueString != null) {
                try {
                    // the String to int conversion happens here
                    return Integer.parseInt(valueString.trim())
                } catch (nfe: java.lang.NumberFormatException) {
                    LogHelper.Loge("NumberFormatException: " + nfe.message)
                    return -1
                }
            }
            return -1
        }

        /**
         * get hour value from given string values
         *
         * @param fullValue string value
         * @return int  hour of day
         */
        fun getHour(fullValue: String): Int {
            var sectionText = AppConstants.DEFAULT_DOUBLE_ZERO
            if (fullValue != EMPTY_STRING) {
                if (fullValue.length > AppConstants.DIGIT_THREE) {
                    sectionText = fullValue.substring(
                        AppConstants.DIGIT_ZERO,
                        AppConstants.DIGIT_TWO
                    )
                } else if (fullValue.length > AppConstants.DIGIT_TWO) {
                    sectionText = fullValue.substring(
                        AppConstants.DIGIT_ZERO,
                        AppConstants.DIGIT_ONE
                    )
                }
                return sectionText.toInt()
            }
            return AppConstants.DIGIT_ZERO
        }

        /**
         * get Minute value from given string values
         *
         * @param fullValue string value
         * @return int minute of day
         */
        fun getMinute(fullValue: String): Int {
            var sectionText = AppConstants.DEFAULT_DOUBLE_ZERO
            if (fullValue != EMPTY_STRING) {
                if (fullValue.length > AppConstants.DIGIT_THREE) {
                    sectionText = fullValue.substring(AppConstants.DIGIT_TWO, AppConstants.DIGIT_FOUR)
                } else if (fullValue.length > AppConstants.DIGIT_TWO) {
                    sectionText = fullValue.substring(AppConstants.DIGIT_ONE, AppConstants.DIGIT_THREE)
                }
                return convertStringToInt(sectionText)
            }
            return AppConstants.DIGIT_ZERO
        }
        fun isTimeModeAM(timeValue: String): Boolean {
            if (timeValue.length >= AppConstants.DIGIT_TWO) {
                val amPmText = timeValue.substring(timeValue.length - AppConstants.DIGIT_TWO)
                return TimeUtils.TEXT_AM.equals(
                    amPmText,
                    ignoreCase = true
                )
            }
            return false
        }

        /**
         * To provide provisioning and unboxing date, time error values on text entry due to SDK have
         *
         * @param validEntry keyboard entry observed error from SDK enum
         * @param context    context of the view
         * @return time / date error values
         */
        fun getTimeDateErrorMessage(validEntry: InputError, context: Context): String {
            var errorMessage = ""
            val startingValue = AppConstants.DIGIT_ONE
            var endingValue = AppConstants.DIGIT_MINUS_ONE
            var isValueRangeModified = true
            when (validEntry) {
                InputError.INVALID_DAY_RANGE_1D_28D -> {
                    errorMessage = context.resources.getString(R.string.text_dateError_message)
                    endingValue = AppConstants.INVALID_DAY_RANGE_1D_28D
                }

                InputError.INVALID_DAY_RANGE_1D_29D -> {
                    errorMessage = context.resources.getString(R.string.text_dateError_message)
                    endingValue = AppConstants.INVALID_DAY_RANGE_1D_29D
                }

                InputError.INVALID_DAY_RANGE_1D_30D -> {
                    errorMessage = context.resources.getString(R.string.text_dateError_message)
                    endingValue = AppConstants.INVALID_DAY_RANGE_1D_30D
                }

                InputError.INVALID_DAY_RANGE_1D_31D -> {
                    errorMessage = context.resources.getString(R.string.text_dateError_message)
                    endingValue = AppConstants.INVALID_DAY_RANGE_1D_31D
                }
                else -> isValueRangeModified = false
            }
            val formattedString = String.format(
                Locale.getDefault(), errorMessage, startingValue, endingValue
            )
            return if (isValueRangeModified) formattedString else validEntry.name ?: EMPTY_STRING
        }

        /**
         * To provide provisioning, unboxing and Settings, time error values on text entry due to SDK have
         *
         * @param validEntry keyboard entry observed error from SDK enum
         * @param context    context of the view
         * @return time / date error values
         */
        fun getTimeErrorMessage(validEntry: InputError, context: Context): String {
            val errorMessage = context.resources.getString(R.string.text_error_time_format)
            var endingValue = AppConstants.DIGIT_MINUS_ONE
            var isValueRangeModified = true
            val timeFormat: SettingsManagerUtils.TimeFormatSettings = SettingsManagerUtils.getTimeFormat()
            if (timeFormat == SettingsManagerUtils.TimeFormatSettings.H_12) {
                when (validEntry) {
                    InputError.INVALID_CLOCK_TIME,
                    InputError.INVALID_CLOCK_RANGE_0M_59M,
                    InputError.INVALID_CLOCK_RANGE_1H_12H -> {
                       endingValue = AppConstants.INVALID_CLOCK_RANGE_1H_12H
                    }
                    else -> isValueRangeModified = false
                }
            } else {
                when (validEntry) {
                    InputError.INVALID_CLOCK_TIME,
                    InputError.INVALID_CLOCK_RANGE_0M_59M,
                    InputError.INVALID_CLOCK_RANGE_0H_23H -> {
                        endingValue = AppConstants.DIGIT_TWENTY_FOUR
                    }
                    else -> isValueRangeModified = false
                }
            }
            val formattedString = String.format(
                    Locale.getDefault(), errorMessage, endingValue
            )
            return if (isValueRangeModified) formattedString else validEntry.name ?: EMPTY_STRING
        }

        /******************* Date and Time methods ********************/

        /******************* knob rotation methods ********************/
        /**
         * Method used for rotate selected tumbler
         *
         * @param fragment: fragment instance
         * @param tumbler: BaseTumbler Id
         * @param knobDirection: Knob event direction
         * @param delay: Knob scroll delay
         */
        fun rotateTumblerOnKnobEvents(
            fragment: Fragment,
            tumbler: BaseTumbler,
            knobDirection: String,
            delay:Int = 0
        ) {
            val adapter = tumbler.adapter ?: return
            val itemCount = adapter.itemCount
            // Early return if itemCount is invalid
            if (itemCount <= 0) return
            scrollJob?.cancel()
            scrollJob = fragment.lifecycleScope.launch {
                when (knobDirection) {
                    KnobDirection.CLOCK_WISE_DIRECTION -> {
                        withContext(Dispatchers.Main) {
                            tumbler.smoothScrollWithDirection(true, true, delay)
                        }
                        // Wait for scroll to complete
                        waitForScrollCompletion(tumbler)
                    }

                    KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> {
                        withContext(Dispatchers.Main) {
                            tumbler.smoothScrollWithDirection(false, true, delay)
                        }
                        // Wait for scroll to complete
                        waitForScrollCompletion(tumbler)
                    }

                    else -> return@launch // Handle unknown or null direction
                }
            }
        }

        /**
         * Method to suspend coroutine until scroll is complete
         * @param tumbler: RecyclerView Id
         * */
        private suspend fun waitForScrollCompletion(tumbler: RecyclerView) {
            val deferred = CompletableDeferred<Unit>()
            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        tumbler.removeOnScrollListener(this)
                        deferred.complete(Unit)
                    }
                }
            }
            tumbler.addOnScrollListener(scrollListener)
            deferred.await()
        }

        fun checkIfInstructionAvailable(fragment: Fragment,cookingViewModel: CookingViewModel): Boolean{
            val recipeName = cookingViewModel.recipeExecutionViewModel.recipeName.value.toString()
            if(!TextUtils.isEmpty(recipeName)) {
                val recipeRecord: RecipeRecord =
                    CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
                        recipeName, cookingViewModel.cavityName.value
                    )
                val isMWORecipe =
                    CookingViewModelFactory.getInScopeViewModel().isOfTypeMicrowaveOven
                var textInformation = AppConstants.TEXT_INFORMATION
                if (isMWORecipe) {
                    textInformation = AppConstants.TEXT_INFORMATION_MWO
                }
                val textToShow: Int = getResIdFromResName(
                    fragment.requireContext(),
                    textInformation + recipeRecord.recipeName,
                    AppConstants.RESOURCE_TYPE_STRING
                )
                return textToShow != R.string.weMissedThat
            }
            return false
        }

        /**
         * Method used for scrolling text on Instruction Widget
         *
         * @param fragment: fragment instance
         * @param scrollView: ScrollView Id
         * @param textView: textView Id inside scrollView
         * @param currentPosition: Knob position
         */
        fun handleTextScrollOnKnobEvent(
            fragment: Fragment,
            scrollView: ScrollView,
            textView: TextView,
            currentPosition: Int
        ) {
            // Coroutine to scroll the text
            fragment.viewLifecycleOwner.lifecycleScope.launch {
                fragment.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val y: Int = textView.layout.getLineTop(
                            currentPosition
                        )
                    scrollView.smoothScrollTo(0, y)
                }
            }
        }

        /**
         * Method used for scrolling text on Instruction Widget
         *
         * @param scrollView: ScrollView Id
         * @param lineHeight: height of the text view
         * @param knobDirection: Knob direction
         */
        fun handleTextScrollOnKnobRotateEvent(
            scrollView: ScrollView,
            lineHeight: Int,
            knobDirection: String
        ) {
            when (knobDirection) {
                KnobDirection.CLOCK_WISE_DIRECTION -> {
                    if (scrollView.canScrollVertically(RecyclerView.VERTICAL)) {
                        // Scroll down
                        scrollView.smoothScrollBy(RecyclerView.TOUCH_SLOP_DEFAULT, lineHeight)
                    }
                }

                KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> {
                    if (scrollView.canScrollVertically(RecyclerView.NO_POSITION)) {
                        // Scroll up
                        scrollView.smoothScrollBy(RecyclerView.TOUCH_SLOP_DEFAULT, -lineHeight)
                    }
                }

                else -> {
                    Loge("Unknown Direction $knobDirection")
                }
            }
        }
        /******************* knob rotation methods ********************/

        /**
         * FAVORITES & HISTORY FEATURE : Secondary Cooking
         * Following section contains Supporting API's for Favorites & History
         */

        fun updateRecordName(recordName: String?): String {
            // Fetch the records within the method
            val records = CookBookViewModel.getInstance().allFavoriteRecords.value

            // Assign a default name if the input recordName is null or empty
            val baseRecordName = if (recordName.isNullOrEmpty()) AppConstants.DEFAULT_FAVORITE_NAME else recordName

            // Regular expression to parse the base name and optional square brackets with a number
            val regex = Regex("(.*?)(\\[(\\d+)])?$") // Matches "name" and optionally "[number]"

            // Normalize baseRecordName by removing square brackets and the number inside them
            val normalizedBaseName = regex.find(baseRecordName)?.groups?.get(1)?.value?.trim() ?: baseRecordName.trim()

            // Extract all numbers in brackets associated with the normalized base name
            val existingNumbers = mutableSetOf<Int>()
            var exactMatchExists = false // Flag to track if an exact match (non-bracketed) exists

            records?.forEach { record ->
                val recordName = record.favoriteName
                if (recordName != null) {
                    val matchResult = regex.find(recordName)
                    if (matchResult != null) {
                        val (namePart, _, numberStr) = matchResult.destructured
                        val normalizedRecordName = namePart.trim()

                        if (normalizedRecordName == normalizedBaseName) {
                            // Check for exact match (e.g., "Bake 175°")
                            if (numberStr.isEmpty()) {
                                exactMatchExists = true
                            }

                            // Add the bracket number to the set, if available
                            val number = numberStr.toIntOrNull()
                            if (number != null) {
                                existingNumbers.add(number)
                            }
                        }
                    }
                }
            }

            // If no conflicts exist and no exact match exists, return the base name as is
            if (!exactMatchExists && existingNumbers.isEmpty()) {
                return baseRecordName
            }

            // Find the smallest available number (filling gaps)
            val nextNumber = (1..Int.MAX_VALUE).first { it !in existingNumbers }

            // Return the base name with the next available number in brackets
            return "$normalizedBaseName [$nextNumber]"
        }


        fun getRecipeOptions(): List<RecipeOptions> {
            val mutableListOf = mutableListOf<RecipeOptions>()
            val recipeRequiredOptions =
                CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.requiredOptions.value!!
            val recipeOptionalOptions =
                CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.optionalOptions.value!!
            mutableListOf.addAll(Objects.requireNonNull(recipeRequiredOptions))
            mutableListOf.addAll(Objects.requireNonNull(recipeOptionalOptions))
            return mutableListOf
        }

        fun updateParametersInFavoriteRecord(
            record: FavoriteRecord,
            options: List<RecipeOptions?>,
            recipeExecutionViewModel: RecipeExecutionViewModel
        ): FavoriteRecord {
            for (option in options) {
                when (option) {
                    RecipeOptions.TARGET_TEMPERATURE -> record.targetTemperature =
                        Objects.requireNonNull(recipeExecutionViewModel.targetTemperature.value.toString())

                    RecipeOptions.BROIL_POWER_LEVEL -> record.broilPowerLevel =
                        recipeExecutionViewModel.broilPowerLevel.value.toString()

                    RecipeOptions.COOK_TIME -> record.cookTime =
                        recipeExecutionViewModel.cookTime.value.toString()

                    RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> record.targetMeatProbeTemperature =
                        recipeExecutionViewModel.meatProbeTargetTemperature.value.toString()

                    RecipeOptions.DONENESS -> record.doneness =
                        recipeExecutionViewModel.donenessOption.value!!.defaultString.toString()

                    RecipeOptions.PAN_SIZE -> record.panSize =
                        recipeExecutionViewModel.panSizeOption.value!!.defaultString.toString()

                    RecipeOptions.AMOUNT -> record.amount =
                        recipeExecutionViewModel.amount.value.toString()

                    RecipeOptions.WEIGHT -> record.weight =
                        recipeExecutionViewModel.weight.value.toString()

                    RecipeOptions.PREHEAT -> record.preheat =
                        recipeExecutionViewModel.preheatOption.value!!.defaultString.toString()

                    RecipeOptions.WHEN_DONE_ACTION -> record.whenDone =
                        recipeExecutionViewModel.whenDoneOption.value!!.defaultString.toString()

                    RecipeOptions.MWO_POWER_LEVEL -> record.mwoPowerLevel =
                        recipeExecutionViewModel.mwoPowerLevel.value.toString()

                    RecipeOptions.BROWNING -> record.browning =
                        recipeExecutionViewModel.browningOption.value!!.defaultString.toString()

                    RecipeOptions.FOOD_SIZE -> record.foodSize =
                        recipeExecutionViewModel.foodSize.value.toString()

                    RecipeOptions.FOOD_TYPE -> record.foodType =
                        recipeExecutionViewModel.foodTypeOption.value!!.defaultString.toString()

                    RecipeOptions.STEAM_LEVEL -> record.steamLevel =
                        recipeExecutionViewModel.steamLevel.value

                    RecipeOptions.RISING -> record.rising =
                        recipeExecutionViewModel.risingOption.value!!.defaultString.toString()

                    else -> {}
                }
            }
            return record
        }

        fun updateParametersInViewModel(
            record: CookbookRecord,
            options: List<RecipeOptions?>,
            recipeExecutionViewModel: RecipeExecutionViewModel
        ) {
            for (option in options) {
                when (option) {
                    RecipeOptions.TARGET_TEMPERATURE -> if (!TextUtils.isEmpty(record.targetTemperature)) {
                        recipeExecutionViewModel.setTargetTemperature(record.targetTemperature!!.toFloat())
                    } else {
                        Loge("Target Temperature is not available in favorite record")
                    }

                    RecipeOptions.BROIL_POWER_LEVEL -> if (!TextUtils.isEmpty(record.broilPowerLevel)) {
                        recipeExecutionViewModel.setBroilPowerLevel(record.broilPowerLevel!!.toInt())
                    } else {
                        Loge("Broil Power is not available in favorite record")
                    }

                    RecipeOptions.COOK_TIME -> if (!TextUtils.isEmpty(record.cookTime)) {
                        recipeExecutionViewModel.setCookTime(record.cookTime!!.toLong())
                    } else {
                        Loge("Cook Time is not available in favorite record")
                    }

                    RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> if (!TextUtils.isEmpty(record.targetMeatProbeTemperature)) {
                        recipeExecutionViewModel.setTargetMeatProbeTemperature(
                            record.targetMeatProbeTemperature!!.toFloat()
                        )
                    } else {
                        Loge("Meat Probe Target Temperature is not available in favorite record")
                    }

                    RecipeOptions.DONENESS -> if (!TextUtils.isEmpty(record.doneness)) {
                        recipeExecutionViewModel.setDoneness(record.doneness)
                    } else {
                        Loge("Doneness is not available in favorite record")
                    }

                    RecipeOptions.PAN_SIZE -> if (!TextUtils.isEmpty(record.panSize)) {
                        recipeExecutionViewModel.setPanSize(record.panSize)
                    } else {
                        Loge("Pan size is not available in favorite record")
                    }

                    RecipeOptions.AMOUNT -> if (!TextUtils.isEmpty(record.amount)) {
                        recipeExecutionViewModel.setAmount(record.amount!!.toDouble())
                    } else {
                        Loge("Pan size is not available in favorite record")
                    }

                    RecipeOptions.WEIGHT -> if (!TextUtils.isEmpty(record.weight)) {
                        recipeExecutionViewModel.setWeight(record.weight!!.toFloat())
                    } else {
                        Loge("Pan size is not available in favorite record")
                    }

                    RecipeOptions.PREHEAT -> if (!TextUtils.isEmpty(record.preheat)) {
                        recipeExecutionViewModel.setPreheat(record.preheat)
                    } else {
                        Loge("Preheat is not available in favorite record")
                    }

                    RecipeOptions.WHEN_DONE_ACTION -> if (!TextUtils.isEmpty(record.whenDone)) {
                        recipeExecutionViewModel.setWeight(record.whenDone!!.toFloat())
                    } else {
                        Loge("When done is not available in favorite record")
                    }

                    RecipeOptions.MWO_POWER_LEVEL -> if (!TextUtils.isEmpty(record.mwoPowerLevel)) {
                        recipeExecutionViewModel.setMwoPowerLevel(record.mwoPowerLevel!!.toFloat().toInt())
                    } else {
                        Loge("MWO Power Level is not available in favorite record")
                    }

                    RecipeOptions.BROWNING -> if (!TextUtils.isEmpty(record.browning)) {
                        recipeExecutionViewModel.setBrowning(record.browning)
                    } else {
                        Loge("Browning is not available in favorite record")
                    }

                    RecipeOptions.FOOD_SIZE -> if (!TextUtils.isEmpty(record.foodSize)) {
                        recipeExecutionViewModel.setFoodSize(record.foodSize!!.toDouble())
                    } else {
                        Loge("Food Size is not available in favorite record")
                    }

                    RecipeOptions.FOOD_TYPE -> if (!TextUtils.isEmpty(record.foodType)) {
                        recipeExecutionViewModel.setFoodType(record.foodType)
                    } else {
                        Loge("Food Type is not available in favorite record")
                    }

                    RecipeOptions.STEAM_LEVEL -> if (!TextUtils.isEmpty(record.steamLevel)) {
                        recipeExecutionViewModel.setSteamLevel(record.steamLevel)
                    } else {
                        Loge("Steam Level is not available in favorite record")
                    }

                    RecipeOptions.RISING -> if (!TextUtils.isEmpty(record.rising)) {
                        recipeExecutionViewModel.setRising(record.rising)
                    } else {
                        Loge("Rising is not available in favorite record")
                    }
                    else->{}
                }
            }
        }

        fun checkIfRecipePresentInFavorite(
            recipeExecutionViewModel: RecipeExecutionViewModel
        ) : Boolean {
            var recipePresentInFavList: Boolean
            val listOfFavorites = CookBookViewModel.getInstance()?.allFavoriteRecords
            listOfFavorites?.value?.forEach {
                if (recipeExecutionViewModel.recipeName.value == it?.name) {
                    it?.let {
                        recipePresentInFavList = compareFavoriteRecordAndViewModel(
                            it,
                            getRecipeOptions(),
                            recipeExecutionViewModel
                        )
                        if (recipePresentInFavList) return true
                    }
                }
            }
            return false
        }

        fun checkIfRecipePresentInHistory(
            recipeExecutionViewModel: RecipeExecutionViewModel
        ) : Boolean {
            var recipePresentInHistList: Boolean
            val listOfHistory = CookBookViewModel.getInstance()?.allHistoryRecords
            var count = 0

            listOfHistory?.value?.forEach {
                if (recipeExecutionViewModel.recipeName.value == it?.name) {
                    it?.let {
                        recipePresentInHistList = compareFavoriteRecordAndViewModel(
                            it,
                            getRecipeOptions(),
                            recipeExecutionViewModel
                        )
                        if (recipePresentInHistList) {
                            count++
                        }
                        //Recipe already present two time in history.
                        //User ran recipe 3rd time.
                        // Condition met to trigger Save to fav notification
                        if (count == AppConstants.DIGIT_TWO) {
                            return true
                        }
                    }

                }
            }

            return false
        }

        fun compareFavoriteRecordAndViewModel(
            record: CookbookRecord,
            options: List<RecipeOptions?>,
            recipeExecutionViewModel: RecipeExecutionViewModel
        ) : Boolean {
            var status = true
            for (option in options) {
                when (option) {
                    RecipeOptions.TARGET_TEMPERATURE -> {
                        // record.targetTemperature when saved using markFavorites API stores temperature as float value
                        // To compare it with REVM.targetTemperature which is an integer need to convert string to double & then to Int
                        status = record.targetTemperature
                            ?.toDoubleOrNull() // Convert to Double, handling invalid input safely
                            ?.toInt() == recipeExecutionViewModel.targetTemperature.value
                    }

                    RecipeOptions.BROIL_POWER_LEVEL -> {
                        status = record.broilPowerLevel?.equals(recipeExecutionViewModel.broilPowerLevel.value)
                            ?: false
                    }

                    RecipeOptions.COOK_TIME -> {
                        if ((recipeExecutionViewModel.cookTime.value.toString()) != DEFAULT_LEVEL)
                            status =
                                record.cookTime?.equals(recipeExecutionViewModel.cookTime.value.toString()) == true
                    }

                    RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> {
                        status = record.targetMeatProbeTemperature
                            ?.toDoubleOrNull() // Convert to Double, handling invalid input safely
                            ?.toInt() == recipeExecutionViewModel.meatProbeTargetTemperature.value
                    }

                    RecipeOptions.DONENESS -> {
                        status =
                            record.doneness?.equals(recipeExecutionViewModel.donenessOption.value?.defaultString) == true
                    }

                    RecipeOptions.TURNTABLE -> {
                    }

                    RecipeOptions.PAN_SIZE -> {
                        status =
                            record.panSize?.equals(recipeExecutionViewModel.panSizeOption.value.toString()) == true
                    }

                    RecipeOptions.AMOUNT -> {
                        status =
                            record.amount?.equals(recipeExecutionViewModel.amount.value.toString()) == true
                    }

                    RecipeOptions.WEIGHT -> {
                        status =
                            record.weight?.equals(recipeExecutionViewModel.weight.value.toString()) == true
                    }

                    RecipeOptions.PREHEAT -> {
                        status =
                            record.preheat?.equals(recipeExecutionViewModel.preheatOption.value.toString()) == true
                    }

                    RecipeOptions.WHEN_DONE_ACTION -> {
                        status =
                            record.whenDone?.equals(recipeExecutionViewModel.whenDoneOption.value.toString()) == true
                    }

                    RecipeOptions.CONVECT_CONVERT -> {
                    }

                    RecipeOptions.MWO_POWER_LEVEL -> {
                        if (recipeExecutionViewModel.mwoPowerLevel.value != null) {
                            status = record.mwoPowerLevel
                                ?.toDoubleOrNull() // Convert to Double, handling invalid input safely
                                ?.toInt() == recipeExecutionViewModel.mwoPowerLevel.value
                        }
                    }

                    RecipeOptions.BROWNING -> {
                        status =
                            record.browning?.equals(recipeExecutionViewModel.browningOption.value.toString()) == true
                    }

                    RecipeOptions.FOOD_SIZE -> {
                        status =
                            record.foodSize?.equals(recipeExecutionViewModel.foodSize.value.toString()) == true
                    }

                    RecipeOptions.FOOD_TYPE -> {
                        status =
                            record.foodType?.equals(recipeExecutionViewModel.foodTypeOption.value.toString()) == true
                    }

                    RecipeOptions.VIRTUAL_CHEF -> {
                        status =
                            record.virtualChef?.equals(recipeExecutionViewModel.virtualchefOption.value.toString()) == true

                    }

                    RecipeOptions.STEAM_LEVEL -> {
                        status =
                            record.steamLevel?.equals(recipeExecutionViewModel.steamLevel.value.toString()) == true
                    }

                    RecipeOptions.RISING -> {
                        status =
                            record.rising?.equals(recipeExecutionViewModel.risingOption.value.toString()) == true
                    }

                    null -> {
                        Loge("Recipe Option list is empty")
                    }

                    else -> {}
                }
                if (!status) break
            }
            if (status) status = record.recipeName == recipeExecutionViewModel.recipeName.value

            if (CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity) {
                if (record.cavity != PRIMARY_CAVITY_KEY) {
                    status = false
                }
            } else {
                if (record.cavity != SECONDARY_CAVITY_KEY) {
                    status = false
                }
            }
            return status
        }

        fun setNavigatedFrom(navigatedFrom: String) {
            this.navigatedFrom = navigatedFrom
        }

        fun getNavigatedFrom(): String {
            return this.navigatedFrom
        }

        fun isFavoriteRecipeFlow(): Boolean {
            return !TextUtils.isEmpty(navigatedFrom) && navigatedFrom == AppConstants.NAVIGATION_FROM_CREATE_FAV || navigatedFrom == AppConstants.NAVIGATION_FROM_EXISTING_FAV
        }

        fun isCreateFavorite(): Boolean {
            return TextUtils.equals(getNavigatedFrom(), AppConstants.NAVIGATION_FROM_CREATE_FAV)
        }

        fun isCurrentCavityRunning(cavity : String?) : Boolean {
            return if (cavity == PRIMARY_CAVITY_KEY) {
                CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.isRunning
            } else {
                CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.isRunning
            }
        }
        /**
         * find out if any cavity is running recipe check based on the variants and not delayed
         *
         * @return true if recipeExecutionState is RUNNING or RUNNING_EXT
         */
        fun isAnyCavityRunningRecipe(): Boolean{
            when(CookingViewModelFactory.getProductVariantEnum()){
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO ->  if(CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING || CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING_EXT) return true
                else -> {}
            }
            if(CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING || CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING_EXT) return true
            Logd("None of the cavity is in RUNNING state")
            return false
        }

        /**
         * find out if any cavity is running recipe check based on the variants and not delayed
         *
         * @return true if recipeExecutionState is RUNNING or RUNNING_EXT
         */
        fun isAnyCavityRunningRecipeOrDelayedState(): Boolean {
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    val recipeExecutionStateValue = CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value
                    if (recipeExecutionStateValue == RecipeExecutionState.RUNNING
                        || recipeExecutionStateValue == RecipeExecutionState.RUNNING_EXT
                        || recipeExecutionStateValue == RecipeExecutionState.DELAYED) return true
                }

                else -> {}
            }
            val recipeExecutionStateValue = CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value
            if (recipeExecutionStateValue == RecipeExecutionState.RUNNING
                || recipeExecutionStateValue == RecipeExecutionState.RUNNING_EXT
                || recipeExecutionStateValue == RecipeExecutionState.DELAYED) return true
            Logd("None of the cavity is in RUNNING state")
            return false
        }
        /**
         * Checks if any cavity in the product (primary or secondary) is in a RUNNING, DELAYED, or PAUSED state.
         * This function evaluates the state of the recipe execution for the primary and secondary cavities
         * based on the product variant. It checks whether the state is one of the following:
         * - RUNNING
         * - RUNNING_EXT
         * - DELAYED
         * - PAUSED
         * - PAUSED_EXT
         *
         * @return `true` if either the primary or secondary cavity is in one of the specified states;
         *         otherwise, returns `false`.
         */
        fun isAnyCavityRunningRecipeOrDelayedStateOrPausedState(): Boolean {
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    val recipeExecutionStateValue = CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value
                    if (recipeExecutionStateValue == RecipeExecutionState.RUNNING
                        || recipeExecutionStateValue == RecipeExecutionState.RUNNING_EXT
                        || recipeExecutionStateValue == RecipeExecutionState.DELAYED
                        || recipeExecutionStateValue == RecipeExecutionState.PAUSED
                        ||recipeExecutionStateValue == RecipeExecutionState.PAUSED_EXT
                        ) return true
                }

                else -> {}
            }
            val recipeExecutionStateValue = CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value
            if (recipeExecutionStateValue == RecipeExecutionState.RUNNING
                || recipeExecutionStateValue == RecipeExecutionState.RUNNING_EXT
                || recipeExecutionStateValue == RecipeExecutionState.DELAYED
                || recipeExecutionStateValue == RecipeExecutionState.PAUSED
                ||recipeExecutionStateValue == RecipeExecutionState.PAUSED_EXT) return true
            Logd("None of the cavity is in RUNNING state")
            return false
        }

        /**
         * Set settings flow started
         * Scenario Handling - If any popup come then need to configure the HMI button after popup dismis
         * So if we are settings/tools so configure Settings HMI button
         */
        fun setSettingsFlow(isSettingsFlow: Boolean) {
            Logi("isSettingsFlow $isSettingsFlow")
            this.isSettingsFlow = isSettingsFlow
        }

        /**
         * get settings flow started
         */
        fun isSettingsFlow(): Boolean {
            return isSettingsFlow
        }

        /**
         * Factory restore  started
         * Scenario Handling - If any popup come then need to configure the HMI button after popup dismis
         * So if we are settings/tools so configure Settings HMI button
         */
        fun setFactoryRestoreStarted(isFactoryRestore: Boolean) {
            Logi("isFactoryRestore $isFactoryRestore")
            this.isRestoreFactoryStared = isFactoryRestore
        }

        /**
         * get settings flow started
         */
        fun getFactoryRestoreStarted(): Boolean {
            return isRestoreFactoryStared
        }

        /**
         * Resize bitmap resource using matrix
         * @param res: for accessing the current view resources
         * @param resId : resource which will decode
         * @param reqWidth : required width after decode
         * @param reqHeight: required height after decode
         * */
        fun resizeBitmapUsingMatrix(
            res: Resources,
            resId: Int,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap {
            val bitmap = BitmapFactory.decodeResource(res, resId)
            val scaleWidth = reqWidth.toFloat() / bitmap.width
            val scaleHeight = reqHeight.toFloat() / bitmap.height

            // Create a matrix for the manipulation
            val matrix = Matrix()
            // Resize the bitmap
            matrix.postScale(scaleWidth, scaleHeight)

            // Recreate the new Bitmap with the applied scaling
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        fun setKnobLightWhenCycleRunning(){
            if (!isFastBlinkingKnobTimeoutActive() && !isSlowBlinkingKnobTimeoutActive() && !isProductInRecipePauseState()){
                HMIExpansionUtils.setBothKnobLightOnDirectly()
            }
        }
        fun setKnobLightWhenPreheatCompleteAndCycleComplete(){
            if (!isFastBlinkingKnobTimeoutActive() && !isProductInRecipePauseState()){
                HMIExpansionUtils.startOrStopKnobLEDSlowBlinkAnimation(true)
                HMIExpansionUtils.startKnobSlowBlinkingTimeout()
            }
        }
        fun setKnobLightWhenCyclePaused(isDoorOpen: Boolean){
            if (isDoorOpen){
                HMIExpansionUtils.setBothKnobLightOffDirectly()
            }else if (isAnyCycleRunning()){
                HMIExpansionUtils.setBothKnobLightOnDirectly()
            }
        }

        /**
         * This method used to update the right text button background
         */
        fun updatePopUpRightTextButtonBackground(
            fragment: Fragment,
            popupBuilder: ScrollDialogPopupBuilder?,
            drawable: Int
        ) {
            popupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                fragment.requireContext().let {
                    ContextCompat.getDrawable(
                        it, drawable
                    )
                }
        }

        /**
         * This method used to update the left text button background
         */
        fun updatePopUpLeftTextButtonBackground(
            fragment: Fragment,
            popupBuilder: ScrollDialogPopupBuilder?,
            drawable: Int
        ) {
            popupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                fragment.activity?.baseContext?.let {
                    ContextCompat.getDrawable(
                        it, drawable
                    )
                }
        }

//        Fragments are changeing in background So we nned this
        fun isErrorPresentOnHMIScreen(): Boolean{
            return isErrorPresentOnHMI
        }

        fun setErrorPresentOnHMIScreen(error : Boolean){
            isErrorPresentOnHMI = error
        }

        /**
         * to know if the current recipe is ConvectSlowRoast
         * Convect Slow Roast is a special recipe which has 3 different recipes inside its tree, so need to be checked by recipeName
         * @return true if convect Slow Roast
         */
        fun isCookTimeProgrammed(cookingVM: CookingViewModel?): Pair<Boolean, Long> {
            if((cookingVM?.recipeExecutionViewModel?.cookTime?.value ?: 0) > 0) return Pair(true, cookingVM?.recipeExecutionViewModel?.cookTime?.value?:0)
            if(cookingVM?.recipeExecutionViewModel?.nonEditableOptions?.value?.containsKey(RecipeOptions.COOK_TIME) == true){
                return Pair(true, cookingVM.recipeExecutionViewModel?.nonEditableOptions?.value?.get(RecipeOptions.COOK_TIME)?.toLong()?:0)
            }
            return Pair(false, 0)
        }
        /**
         * @param step
         * @param def
         * @param min
         * @param max
         * @return
         * @throws JSONException
         */
        @Suppress("SameParameterValue")
        @Throws(JSONException::class)
        fun buildRangeJsonObject(step: Int, def: Int, min: Int, max: Int): JSONObject? {
            val obj = JSONObject()
            obj.put("step", step)
            obj.put("default", def)
            obj.put("min", min)
            obj.put("max", max)
            return obj
        }

        fun isUpperSteamCleanRunning(): Boolean {
                        return (CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.recipeCookingState.value ==
                    RecipeCookingState.CLEANING ||  CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.recipeName.value == CapabilityKeys.STEAM_CLEAN_KEY )
        }

        fun isLowerSteamCleanRunning(): Boolean {
            return CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.recipeCookingState.value ==
                    RecipeCookingState.CLEANING ||
                    CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.recipeName.value == CapabilityKeys.STEAM_CLEAN_KEY
        }

        fun cancelCompletedSteamCycle(cavityPosition: Int) {
            when (cavityPosition) {
                1 -> CookingViewModelFactory.getPrimaryCavityViewModel().cancel()
                2 -> CookingViewModelFactory.getSecondaryCavityViewModel().cancel()
            }
        }

        fun cancelCurrentVariantSteamCycle(
            cavityPosition: Int,
            fragment: Fragment
        ) {
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                    if ((cavityPosition == fragment.resources.getInteger(R.integer.integer_range_1)) &&
                        CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.cookTimerState.value == Timer.State.RUNNING
                    ) {
                        cancelCompletedSteamCycle(cavityPosition)
                    }
                    if ((cavityPosition == fragment.resources.getInteger(R.integer.integer_range_2)) &&
                        CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.cookTimerState.value == Timer.State.RUNNING
                    ) {
                        cancelCompletedSteamCycle(cavityPosition)
                    }
                }

                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                    cancelCompletedSteamCycle(cavityPosition)
                }
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {

                }

                else -> {
                    //Do nothing
                }
            }
        }

        /**
         * get the cooking view model of jet start recipe
         * 0 if no favorites have been assigned in that case pick up from default quick start recipe
         * else favorite id that has been assigned and return the view model
         */
        fun getCookingViewModelForJetStartRecipe(): CookingViewModel {
            val favoriteName =
                SharedPreferenceManager.getKnobAssignFavoritesCycleNameIntoPreference()
            val cavityName =
                if (favoriteName.isNullOrEmpty()) {
                    val cookingViewModel : CookingViewModel = when (CookingViewModelFactory.getProductVariantEnum()) {
                        CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN,
                        CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                        CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> CookingViewModelFactory.getPrimaryCavityViewModel()
                        else -> CookingViewModelFactory.getSecondaryCavityViewModel()
                    }
                    cookingViewModel.cavityName.value
                }
                else CookBookViewModel.getInstance()
                    .getFavoriteRecordByFavoriteName(favoriteName).cavity
            Logd("JET start", "assigned favoriteName $favoriteName cavityName $cavityName")
            return if (cavityName.contentEquals(SECONDARY_CAVITY_KEY)) CookingViewModelFactory.getSecondaryCavityViewModel()
            else CookingViewModelFactory.getPrimaryCavityViewModel()
        }
        /**
         * get the assigned favorites through knob settings
         *
         * @return 0 if no favorites have been assigned in that case pick up from default quick start recipe
         *          else favorite id that has been assigned
         */
        fun getAssignJetStartRecipeId(): Int {
            val favoriteName =
                SharedPreferenceManager.getKnobAssignFavoritesCycleNameIntoPreference()
            Logd("JET start", "assigned favoriteName $favoriteName")
            return if (favoriteName.isNullOrEmpty()) 0
            else CookBookViewModel.getInstance().getFavoriteRecordByFavoriteName(favoriteName).id
        }

        /**
         * get the notification String IDs
         */
        fun getNotificationStringId(notification: String, context: Context): String {
            val otaViewModel = OTAVMFactory.getOTAViewModel()
            return when (notification) {
                "GetToKnowYourAppliance" -> context.getString(R.string.text_notification_get_to_know_appliance)
                "SaveLastRecipeToFavorite"-> context.getString(R.string.text_notification_save_to_favorite)
                "SoftwareUpdatedSuccessfully"-> context.getString(R.string.text_notification_update_successful, otaViewModel?.otaManager?.currentSystemVersion)
                "ConnectToNetwork" -> context.getString(R.string.connect_to_network)
                "UpdateDateAndTime" -> context.getString(R.string.text_notification_update_date_time)
                "SoftwareUpdateIsAvailable"-> context.getString(R.string.text_notification_update_available)
                "OvenCooling" -> context.getString(R.string.text_notification_oven_cooling)
                "TapToBegin" -> context.getString(R.string.text_notification_tap_to_begin)
                "PressAndHoldToQuicklyStart" -> {
                    if(AppConstants.RIGHT_KNOB_ID == 1) {//default settings Right knob for recipe
                        context.getString(R.string.appliance_features_guide_step_3)
                    }else{//user changed settings left knob for recipe
                       context.getString(R.string.appliance_features_guide_step_3_swap)
                    }
                }
                "TurnYourOvenLightKnobPush" -> {
                    if(AppConstants.LEFT_KNOB_ID == 0) {//default settings Left knob for options
                        context.getString(R.string.appliance_features_guide_step_1)
                    }else{//user changed settings Right knob for options
                        context.getString(R.string.appliance_features_guide_step_1_swap)
                    }
                }
                "SwapYourKnobPreference" -> context.getString(R.string.text_notification_knob_preferences)
                "AddRecipesToFavoritesFromHistory" -> context.getString(R.string.text_notification_add_to_fav)
                "ExploreTheFreshPizzaRecipe" -> context.getString(R.string.text_notification_fresh_pizza)
                else -> context.getString(R.string.str_empty)
            }
        }

          /**
         * to capitalize all characters based on locale
         *
         * @param locale: optional param to convert based on locale or it will get default language
         */
        fun String.capitalizeAllChars(locale: Locale = Locale.getDefault()): String {
            return this.uppercase(locale)
        }


        fun timeHasBeenSet(value: Boolean){
            timeHasBeenSet = value
        }

        fun getTimeHasBeenSet():Boolean{
            return timeHasBeenSet
        }

        fun dateHasBeenSet(value: Boolean){
            dateHasBeenSet = value
        }

        fun getDateHasBeenSet():Boolean {
            return dateHasBeenSet
        }

        fun  setHistoryIDToRemove(historyId: Int){
            historyIdToRemove = historyId
        }

        fun  getHistoryIDToRemove():Int{
            return historyIdToRemove
        }

       fun setRunningRecipeIsQuickStart(value : Boolean){
           wasRecipeAQuickStart = value
        }

        fun getRunningRecipeIsQuickStart() : Boolean{
            return wasRecipeAQuickStart
        }


        fun setConnectToNwNotificationTriggerCheck(value : Boolean) {
            connectToNwIsToBeTriggered = value
        }

        fun getConnectToNwNotificationTriggerCheck() : Boolean {
            return connectToNwIsToBeTriggered
        }

        fun isItAmOrPm() :Boolean {
            val calendar = Calendar.getInstance()

            // Get the hour of the day (24-hour format)
            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

            // Check if the time is PM (12 or after)
            val isPM = hourOfDay >= 12

            Logd("It's PM is a $isPM statement")

            return isPM
        }

        fun handleTipsAndTricks(){
            var tipNumber = SharedPreferenceManager.getActiveTipNumber()?.toInt()

            if(tipNumber == 5){
                tipNumber = 1
                SharedPreferenceManager.setActiveTipNumber(tipNumber.toString())
            }
            else{
                if (tipNumber != null) {
                    tipNumber += 1
                    SharedPreferenceManager.setActiveTipNumber(tipNumber.toString())
                }
            }

            when(tipNumber){
                1->{
                    NotificationManagerUtils.removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_EXPLORE_FRESH_PIZZA)
                    NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_PRESS_HOLD_FOR_QUICK_START)
                }
                2->{
                    NotificationManagerUtils.removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_PRESS_HOLD_FOR_QUICK_START)
                    NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_TURN_LIGHT_KNOB_PUSH)
                }
                3->{
                    NotificationManagerUtils.removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_TURN_LIGHT_KNOB_PUSH)
                    NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_SWAP_KNOB_PREFERENCE)
                }
                4->{
                    NotificationManagerUtils.removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_SWAP_KNOB_PREFERENCE)
                    NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_ADD_RECIPE_TO_FAV_FROM_HISTORY)
                }
                5->{
                    NotificationManagerUtils.removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_ADD_RECIPE_TO_FAV_FROM_HISTORY)
                    NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_EXPLORE_FRESH_PIZZA)
                }
            }
        }

        fun handleNotificationReOccurance() {
            val allNotification = NotificationManagerUtils.getNotificationCenterListItems()
            val currentTime = android.icu.util.Calendar.getInstance() // Get the current time

            //Trigger Notification if SW update is available
            if (allNotification?.find { it.titleText == NotificationJsonKeys.NOTIFICATION_SW_UPDATE_AVAILABLE } != null) {
                val notification = allNotification.find { it.titleText == NotificationJsonKeys.NOTIFICATION_SW_UPDATE_AVAILABLE }
                val timeStamp = (notification?.rightText)?.toLong()
                val timeElapsedInMillis =
                    currentTime.timeInMillis - timeStamp!! // Get the elapsed time in milliseconds

                // Convert into days
                val elapsedDays =
                    timeElapsedInMillis / (1000 * 60 * 60 * 24) // Convert milliseconds to days

                // Check if the elapsed time is greater than or equal to 15 days
                if (elapsedDays >= 15) {
                    Logd("15 days or more have passed since the SW update is available notification")
                    NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_SW_UPDATE_AVAILABLE)
                }
            }

            //Trigger Notification if user did not set Date and time for 15 days
            if (allNotification?.find { it.titleText == NotificationJsonKeys.NOTIFICATION_UPDATE_DATE_AND_TIME } != null) {
                val notification = allNotification.find { it.titleText == NotificationJsonKeys.NOTIFICATION_UPDATE_DATE_AND_TIME }
                val timeStamp = (notification?.rightText)?.toLong()
                val timeElapsedInMillis =
                    currentTime.timeInMillis - timeStamp!! // Get the elapsed time in milliseconds

                // Convert into days
                val elapsedDays =
                    timeElapsedInMillis / (1000 * 60 * 60 * 24)  // Convert milliseconds to days

                // Check if the elapsed time is greater than or equal to 15 days
                if (elapsedDays >= 15) {
                    Logd("15 days or more have passed since the update date and time notification.")
                    NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_UPDATE_DATE_AND_TIME)
                }
            }

            //Trigger Notification for Connect to Network if its PM and cycle is ran by user
            if (allNotification?.find { it.titleText == NotificationJsonKeys.NOTIFICATION_CONNECT_TO_NW } != null) {
                val notification = allNotification.find { it.titleText == NotificationJsonKeys.NOTIFICATION_CONNECT_TO_NW }
                val timeStamp = (notification?.rightText)?.toLong()
                val timeElapsedInMillis =
                    currentTime.timeInMillis - timeStamp!! // Get the elapsed time in milliseconds

                // Convert into days
                val elapsedDays =
                    timeElapsedInMillis / (1000 * 60 * 60 * 24) // Convert milliseconds to days

                // Check if the elapsed time is greater than or equal to 15 days
                if (elapsedDays >= 15) {
                    Logd("15 days or more have passed since the connect to network notification.")
                    setConnectToNwNotificationTriggerCheck(true)
                }
            }
        }


        /**
         * Method to get Accessory Guide Popup Message.
         *
         * @param recipeName Current programming recipe.
         * @return The resource ID for the accessory guide message.
         */
        fun getPopupDataForAccessoryGuide(recipeName: String): Int {
            val visibleFragment = NavigationUtils.getVisibleFragment() ?: return R.string.weMissedThat
            val context = visibleFragment.context ?: return R.string.weMissedThat
            var recipe = recipeName
            if (CookingViewModelFactory.getInScopeViewModel().isOfTypeMicrowaveOven){
                recipe = recipeName + AppConstants.MW_RECIPE
            }

            val commonMessagePosition = getViewSafely(visibleFragment)?.let {
                getTextPosition(it, R.array.assisted_accessory_guide_common_messages, recipe)
            } ?: return R.string.weMissedThat

            if (commonMessagePosition > 0) {
                val accessoryGuideId = getResIdFromResName(
                    context,
                    AppConstants.TEXT_COMMON_MESSAGE_ACCESSORY_GUIDE + commonMessagePosition,
                    AppConstants.RESOURCE_TYPE_STRING
                )
                if (accessoryGuideId != R.string.weMissedThat) {
                    return accessoryGuideId
                }
            }

            return getResIdFromResName(
                context,
                recipe + AppConstants.TEXT_ACCESSORY_GUIDE,
                AppConstants.RESOURCE_TYPE_STRING
            ).takeIf { it != R.string.weMissedThat } ?: R.string.weMissedThat
        }

        /**
         * Method to get Cook Guide Popup Message.
         *
         * @param recipeName Current programming recipe.
         * @return The resource ID for the cook guide message.
         */
        fun getPopupDataForCookGuide(recipeName: String): Int {
            val visibleFragment = NavigationUtils.getVisibleFragment() ?: return R.string.weMissedThat
            val context = visibleFragment.context ?: return R.string.weMissedThat
            var recipe = recipeName
            if (CookingViewModelFactory.getInScopeViewModel().isOfTypeMicrowaveOven){
                recipe = recipeName + AppConstants.MW_RECIPE
            }

            val commonMessagePosition = getViewSafely(visibleFragment)?.let {
                getTextPosition(it, R.array.assisted_cook_guide_common_messages, recipe)
            } ?: return R.string.weMissedThat

            if (commonMessagePosition > 0) {
                val cookingGuideId = getResIdFromResName(
                    context,
                    AppConstants.TEXT_COMMON_MESSAGE_COOK_GUIDE + commonMessagePosition,
                    AppConstants.RESOURCE_TYPE_STRING
                )
                if (cookingGuideId != R.string.weMissedThat) {
                    return cookingGuideId
                }
            }

            return getResIdFromResName(
                context,
                recipe + AppConstants.TEXT_COOK_GUIDE,
                AppConstants.RESOURCE_TYPE_STRING
            ).takeIf { it != R.string.weMissedThat } ?: R.string.weMissedThat
        }

        fun loadCookingGuide (recipeName: String){
            cookingGuideList.clear()
            if (getPopupDataForCookGuide(recipeName) != R.string.weMissedThat){
                cookingGuideList.add(AppConstants.TEXT_COOK_GUIDE)
            }
            if (getPopupDataForAccessoryGuide(recipeName) != R.string.weMissedThat){
                cookingGuideList.add(AppConstants.TEXT_ACCESSORY_GUIDE)
            }
        }

        fun getCookingGuideListSize(): Int{
            return cookingGuideList.size
        }

        fun clearOrEraseCookingGuideList(){
            cookingGuideList.clear()
        }
        fun getProgreeBarDetails(): Pair<Boolean,Boolean>?{
            return progressBarDetails
        }

        fun setProgreeBarDetails(progressBarDetails : Pair<Boolean,Boolean>?){
            this.progressBarDetails = progressBarDetails
        }
        /**
         * This method used to Clear the recipe data if recipeExecutionState is IDLE
         */
        fun clearRecipeData()
        {
            //clear the recipe data
            val primaryModel = CookingViewModelFactory.getPrimaryCavityViewModel()
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    val secondaryModel = CookingViewModelFactory.getSecondaryCavityViewModel()
                    if (secondaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.IDLE) {
                        Logd("clearing the combo Lower recipe data")
                        secondaryModel.cancel()
                    }
                }

                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                    val secondaryModel = CookingViewModelFactory.getSecondaryCavityViewModel()
                    if (primaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.IDLE) {
                        Logd("clearing the double upper recipe data")
                        primaryModel.cancel()
                    }
                    if (secondaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.IDLE) {
                        Logd("clearing the double lower recipe data")
                        secondaryModel.cancel()
                    }
                }

                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                    if (primaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.IDLE) {
                        Logd("clearing the single recipe data")
                        primaryModel.cancel()
                    }
                }

                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                    if (primaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.IDLE) {
                        Logd("clearing the microwave recipe data")
                        primaryModel.cancel()
                    }
                }

                else -> {
                    Logd("All cavity running")
                }
            }
        }

        /**
         * special case for reheat as power level popup needs to be displayed and have option to assisted recipes
         * @param cookingVM
         * @return true if reheat recipe is programmed
         */
        fun isRecipeReheatInProgramming(cookingVM: CookingViewModel?): Boolean {
            return cookingVM?.recipeExecutionViewModel?.isNotRunning == true && cookingVM.recipeExecutionViewModel?.recipeName?.value?.contentEquals(
                AppConstants.RECIPE_REHEAT
            ) == true
        }
         /** Retrieves the description for the kitchen timer cancel popup.
         * @param fragment The fragment from which to retrieve the string resource.
         * @return The description string based on the number of running timers.
         */
        fun getKitchenTimerCancelPopupDescription(fragment: Fragment): String {
            val numOfTimers = KitchenTimerUtils.isKitchenTimersRunning()
            return if (numOfTimers > 1) {
                fragment.getString(R.string.text_description_cancel_timer_common_moreThan1)
            } else {
                fragment.getString(R.string.text_description_cancel_timer_common_only1, numOfTimers.toString())
            }
        }

        /**
         * find whether upper oven cycle is running or not
         *
         */
        fun isPrimaryCavityCycleRunning(): Boolean {
            var primaryCycleRunning = false
            if ((CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeCookingState?.value != RecipeCookingState.IDLE) ||
                (RecipeExecutionState.DELAYED == CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value) ||
                (((CookingViewModelFactory.getPowerInterruptState() == PowerInterruptState.BROWNOUT) &&
                        (RecipeCookingState.IDLE == CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeCookingState?.value) &&
                        (RecipeExecutionState.RUNNING_EXT == CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value)))
            ) {
                primaryCycleRunning = true
            }
            return primaryCycleRunning
        }

        /**
         * find whether Lower oven cycle is running or not
         *
         * @param lowerOvenViewModel: lower oven cooking view model
         */
        fun isSecondaryCavityCycleRunning(lowerOvenViewModel: CookingViewModel): Boolean {
            var secondaryCycleRunning = false
            if ((lowerOvenViewModel.recipeExecutionViewModel?.recipeCookingState?.value != RecipeCookingState.IDLE) ||
                (RecipeExecutionState.DELAYED == lowerOvenViewModel.recipeExecutionViewModel?.recipeExecutionState?.value) ||
                (((CookingViewModelFactory.getPowerInterruptState() == PowerInterruptState.BROWNOUT) &&
                        (RecipeCookingState.IDLE == lowerOvenViewModel.recipeExecutionViewModel?.recipeCookingState?.value) &&
                        (RecipeExecutionState.RUNNING_EXT == lowerOvenViewModel.recipeExecutionViewModel?.recipeExecutionState?.value)))
            ) {
                secondaryCycleRunning = true
            }
            return secondaryCycleRunning
        }
        /**
         * find whether any popup is showing or not
         *
         */
        fun isAnyPopupShowing(): Boolean {
            if (PopUpBuilderUtils.isPopupShowing() || MoreOptionsPopupBuilder.isAnyPopupShowing() || ScrollDialogPopupBuilder.isAnyPopupShowing()) {
                Logd("Popup Showing: ","Popup is still showing")
                return true
            }
            return false
        }
    }
}
