package core.utils

import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.whirlpool.cooking.common.utils.TimeoutViewModel.TimeoutStatesEnum
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.utils.QuickstartRecipeParam
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.settings.SettingsViewModel.SabbathMode.SABBATH_COMPLIANT
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.cookbook.records.CookbookRecord
import com.whirlpool.hmi.utils.timers.CountDownTimer
import com.whirlpool.hmi.utils.timers.Timer
import core.utils.AppConstants.POPUP_BOTTOM_PADDING_16PX
import core.utils.AppConstants.POPUP_DESCRIPTION_TOP_MARGIN_8PX
import core.utils.AppConstants.POPUP_TITLE_TOP_MARGIN_52PX
import core.utils.AppConstants.POPUP_TITLE_TOP_MARGIN_72PX
import core.utils.AppConstants.RECIPE_BAKE_JET_START_TEMP_FAHRENHEIT
import core.utils.CookingAppUtils.Companion.manageHMIPanelLights
import core.utils.HMILogHelper.Logd
import core.utils.PopUpBuilderUtils.Companion.observeHmiKnobListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Objects

/**
 * File        : core.utils.DoorEventUtils
 * Brief       : Utility class and method for start cycle based on live door events
 * Author      : Hiren
 * Created On  : 12-04-2024
 * Description: Utils module to handle all the door events for various product configurations
 * Only designed for primary cavity only because mwo will always be a primary cavity either for combo or single microwave variant
 * Added code to handle lower cavity door events.
 */
class DoorEventUtils {
    companion object {
        private const val tag = "doorEventUtils"

        //handler for upper cavity
        private val doorHandler = Handler(Looper.getMainLooper())

        /**
         * used to store temporary value of door state based on cavity index 0=primary, 1=secondary
         * useful to detect the event in case if the door is open for that cavity and original state is
         * not same then move to recipe selection
         */
        var doorLastOpenStateForCavity: BooleanArray = BooleanArray(2)
        private var doorEventListener: DoorEventListener? = null

        private var countDownTimerPrimary: CountDownTimer? = null
        private var countDownTimerSecondary: CountDownTimer? = null
        var upperDoorDialogPopupBuilder: ScrollDialogPopupBuilder? = null
        var lowerDoorDialogPopupBuilder: ScrollDialogPopupBuilder? = null
        protected const val UPPER_OVEN = 0
        protected const val LOWER_OVEN = 1

        /**
         * To show Hot Cavity popup on certain conditions and on particular time
         */
        private var hotCavityPopupHandler: Handler = Handler(Looper.getMainLooper())

        /**
         * method to setup primary cavity doorEventListner
         *
         * */
        fun setDoorEventListener(doorEventListener: DoorEventListener) {
            Logd( "$tag : $doorEventListener")
            DoorEventUtils.doorEventListener = doorEventListener
        }

        fun removeDoorEventListener(
            listener: DoorEventListener,
        ) {
            if (doorEventListener === listener) {   // only the
                // owning fragment can make the listener null.
                doorEventListener = null
            }
        }

        /**
         * locally modified Door state events
         */
        enum class DOOR_STATE {
            INITIAL,//when the app starts and observe on the door state
            SHOW_OPEN_CLOSE_DOOR_POPUP,//to show the open close door dialog
            DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP// do not show door pen close dialog
        }

        private lateinit var doorShowPopupValue: DOOR_STATE

        /**
         * call this when KA main activity is ready from SDK side with all cooking view model to observe on Door state
         * @param fragmentActivity main activity of the KA app
         */
        fun observeDoorEvents(fragmentActivity: FragmentActivity?) {
            val upperOvenViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
            if (upperOvenViewModel.isOfTypeMicrowaveOven) {
                //assign initial value because when observer gets register getting a door state value and updating, we have a toggle only for doorShowPopupValue between show and do not show popup
                doorShowPopupValue = DOOR_STATE.INITIAL
                fragmentActivity?.let {
                    upperOvenViewModel.doorState.observe(it) { isDoorOpen: Boolean ->
                        doorHandler.removeCallbacksAndMessages(null)
                        //runnable to decide based on previous door state value
                        doorHandler.postDelayed(
                            runnableDoorStatus(isDoorOpen),
                            fragmentActivity.resources.getInteger(R.integer.duration_microwave_open_close_door_5_minutes_in_millis)
                                .toLong()
                        )
                        //in initial means app has launce always show popup and after that DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP toggle, flip back on runnable after timeout
                        doorShowPopupValue =
                            if (doorShowPopupValue == DOOR_STATE.INITIAL) DOOR_STATE.SHOW_OPEN_CLOSE_DOOR_POPUP else DOOR_STATE.DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP
                        Logd(
                            tag,
                            "currentDoorStatus $isDoorOpen showDoorOpenClosePopup $doorShowPopupValue"
                        )
                    }
                }
            } else {
                val productVariantEnum = CookingViewModelFactory.getProductVariantEnum()
                fragmentActivity?.let {
                    upperOvenViewModel.doorState.observe(
                        it
                    ) { isDoorOpen: Boolean ->
                        if (upperOvenViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING) AbstractStatusFragment.isDoorOpenClosedInPreHeat[0] =
                            false
                        handleDoorObserversBasedOnVariant(
                            productVariantEnum,
                            isDoorOpen,
                            upperOvenViewModel,
                            fragmentActivity,
                            UPPER_OVEN
                        )
                    }
                }
                when(productVariantEnum){
                    CookingViewModelFactory.ProductVariantEnum.COMBO,
                    CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                        val lowerOvenViewModel = CookingViewModelFactory.getSecondaryCavityViewModel()
                        lowerOvenViewModel?.let {
                            fragmentActivity?.let {
                                lowerOvenViewModel.doorState.observe(
                                    it
                                ) { isDoorOpen: Boolean ->
                                    if (lowerOvenViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING) AbstractStatusFragment.isDoorOpenClosedInPreHeat[1] =
                                        false
                                    handleDoorObserversBasedOnVariant(
                                        productVariantEnum,
                                        isDoorOpen,
                                        lowerOvenViewModel,
                                        fragmentActivity,
                                        LOWER_OVEN
                                    )
                                }
                            }
                        }
                    }

                    else -> {}
                }
            }
        }


        private fun handleDoorObserversBasedOnVariant(
            productVariantEnum: CookingViewModelFactory.ProductVariantEnum, isDoorOpen: Boolean,
            cookingViewModel: CookingViewModel, @Suppress("UNUSED_PARAMETER") fragmentActivity: FragmentActivity, ovenType: Int
        ) {
            //Update door open/close change status to the diagnostics manager
            Logd("Door state", ":$isDoorOpen ovenType: $ovenType")
            when (productVariantEnum) {
                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                    //Do nothing, handled in earlier part. check if we want to move MWO implementation here.
                }

                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    if (ovenType == LOWER_OVEN){
                        handleDoorEvents(
                            cookingViewModel,
                            isDoorOpen,
                            ovenType
                        )
                    }
                }
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                    handleDoorEvents(cookingViewModel, isDoorOpen, ovenType)
                    if (ovenType == UPPER_OVEN) {
                        handleHotCavityWarning(
                            cookingViewModel,
                            isDoorOpen,
                            fragmentActivity
                        )
                    }
                }

                else -> {}
            }
        }

        private fun handleDoorEvents(cookingViewModel: CookingViewModel, isDoorOpen: Boolean, ovenType: Int) {
            Logd("$tag : Cooking View Model : $cookingViewModel  Door State : $isDoorOpen Oven Type : $ovenType")
            if (doorEventListener != null) {
                doorEventListener!!.onDoorEvent(
                    cookingViewModel,
                    isDoorOpen,
                    ovenType
                )
            } else HMILogHelper
                .Logi("Door Event not registered")
        }

        /**
         * Runnable to change the door status after timeout=5 minutes
         */
        private fun runnableDoorStatus(
            lastDoorStatus: Boolean,
        ): Runnable {
            return Runnable {
                val cookingViewModel =
                    CookingViewModelFactory.getPrimaryCavityViewModel() ?: return@Runnable
                //change the doorShowPopupValue based on the door status coming form runnable(previous) and current door status
                doorShowPopupValue =
                    if (cookingViewModel.doorState.value == lastDoorStatus) DOOR_STATE.SHOW_OPEN_CLOSE_DOOR_POPUP else DOOR_STATE.DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP
                Logd(
                    tag,
                    "after 5 minutes showDoorOpenClosePopup $doorShowPopupValue"
                )
            }
        }

        /**
         * Use this method to start any microwave recipe, this method will handle internally to show open/close door popup or close door popup or start cycle in case door is closed
         *
         * @param fragment current fragment screen
         * @param cookingViewModel in scope view model most likely will be primary cooking view model for microwave
         * @return true if open close dialog is shown, false safe to start
         */
        fun startMicrowaveRecipeOrShowPopup(fragment: Fragment, cookingViewModel: CookingViewModel?) {
            if (cookingViewModel == null) return
            if (!cookingViewModel.isOfTypeMicrowaveOven) return
            if (isMWODoorSatisfy(cookingViewModel)) {
                //door is closed safe to start cycle
                val recipeErrorResponse = cookingViewModel.recipeExecutionViewModel.execute()
                Logd(
                    tag,
                    "recipeErrorResponse name=${recipeErrorResponse.name} description=${recipeErrorResponse.description}"
                )
                if (!recipeErrorResponse.isError){
                    CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                    HMIExpansionUtils.startOrStopKnobLEDFadeInLightAnimation(true)
                }
                else {
                    CookingAppUtils.handleCookingError(
                        fragment,
                        cookingViewModel,
                        recipeErrorResponse,
                        false
                    )
                }
            } else {
                //must show open/close door popup for microwave as there was no door interaction observed for 5 mins
                showMicrowaveOpenClosePopup(fragment, cookingViewModel, null)
            }
        }

        /**
         * to check if the microwave cavity door condition is safe to run recipe
         *
         * @param cookingViewModel of a microwave cavity
         * @return true if satisfy and safe to run recipe, false to show open/close door popup
         */
        private fun isMWODoorSatisfy(cookingViewModel: CookingViewModel): Boolean{
            if(!cookingViewModel.isOfTypeMicrowaveOven) return false
            val currentDoorStatus = cookingViewModel.doorState?.value
            Logd(tag, " $doorShowPopupValue and currentDoorStatus=$currentDoorStatus")
            return if (doorShowPopupValue == DOOR_STATE.DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP) {
                // it is okay not to show the open/close dialog as door was open or closed within 5 minutes timeframe
                currentDoorStatus != true
            } else {
                //must show open/close door popup for microwave as there was no door interaction observed for 5 mins
                false
            }
        }

        /**
         * Starting any quick start recipe for microwave use 100 power, 30 sec with quickStartParameter
         * for oven use bake 350 F using normal recipe loading
         */
        fun startQuickRecipe( fragment: Fragment, cookingViewModel: CookingViewModel){
            if(MeatProbeUtils.isMeatProbeConnected(cookingViewModel)) {
                HMILogHelper.Loge(fragment.tag, "${cookingViewModel.cavityName.value} has probe attached not allowing Quick Start recipe" )
                return
            }
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            val recipeRecord = CookBookViewModel.getInstance()
                .getDefaultRecipeRecordByNameAndCavity(
                    if (cookingViewModel.isOfTypeMicrowaveOven) AppConstants.RECIPE_MICROWAVE else AppConstants.RECIPE_BAKE,
                    cookingViewModel.cavityName.value
                )
            var recipeErrorResponse : RecipeErrorResponse
            if (cookingViewModel.isOfTypeMicrowaveOven) {
                val quickstartRecipeParam = QuickstartRecipeParam()
                quickstartRecipeParam.cookbookRecord = recipeRecord
                quickstartRecipeParam.cookTime = 30
                quickstartRecipeParam.powerLevel = 100
                if(!isMWODoorSatisfy(cookingViewModel)){
                    //must show open/close door popup for microwave as there was no door interaction observed for 5 mins
                    Logd(fragment.tag, "cavityName ${cookingViewModel.cavityName.value} microwave door condition not satisfied, showing pop")
                    showMicrowaveOpenClosePopup(fragment, cookingViewModel, quickstartRecipeParam)
                    return
                }
                CookingAppUtils.setRunningRecipeIsQuickStart(true)
                recipeErrorResponse = cookingViewModel.recipeExecutionViewModel.executeQuickstartRecipe(quickstartRecipeParam)
            }else{
                recipeErrorResponse = cookingViewModel.recipeExecutionViewModel.load(recipeRecord)
                Logd(
                    tag,
                    "cavityName ${cookingViewModel.cavityName.value}  loading recipeRecord ${recipeRecord.recipeName} recipeErrorResponse name=${recipeErrorResponse.name} description=${recipeErrorResponse.description}"
                )
                if(!recipeErrorResponse.isError) {
                    recipeErrorResponse= cookingViewModel.recipeExecutionViewModel.setTargetTemperature((if (SettingsViewModel.getSettingsViewModel().temperatureUnit.value == SettingsViewModel.TemperatureUnit.CELSIUS) AppConstants.RECIPE_BAKE_JET_START_TEMP_CELSIUS else RECIPE_BAKE_JET_START_TEMP_FAHRENHEIT))
                    Logd(
                        tag,
                        "cavityName ${cookingViewModel.cavityName.value}  setting targetTemperature recipeRecord ${recipeRecord.recipeName} recipeErrorResponse name=${recipeErrorResponse.name} description=${recipeErrorResponse.description}"
                    )
                    if(!recipeErrorResponse.isError) {
                        recipeErrorResponse = cookingViewModel.recipeExecutionViewModel.execute()
                        CookingAppUtils.setRunningRecipeIsQuickStart(true)
                    }
                }
            }
            Logd(
                tag,
                "cavityName ${cookingViewModel.cavityName.value}  executing recipeRecord ${recipeRecord.recipeName} recipeErrorResponse name=${recipeErrorResponse.name} description=${recipeErrorResponse.description}"
            )
            if (!recipeErrorResponse.isError){
                CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                HMIExpansionUtils.startOrStopKnobLEDFadeInLightAnimation(true)
            }
            else{
                CookingAppUtils.handleCookingError(
                    fragment,
                    cookingViewModel,
                    recipeErrorResponse,
                    false)
            }
        }


        /**
         * Use this method to start any microwave recipe, this method will handle internally to show open/close door popup or close door popup or start cycle in case door is closed
         *
         * @param fragment current fragment screen
         * @param cookingViewModel in scope view model most likely will be primary cooking view model for microwave
         * @param shouldStartIfConditionMet true if recipe should start if met all the condition, false otherwise to show popup and prepare the user
         * @return true if open close dialog is shown, false safe to start
         */
        fun startJetRecipeOrShowPopup(
            fragment: Fragment,
            cookingViewModel: CookingViewModel?,
            shouldStartIfConditionMet: Boolean
        ): Boolean {
            if (cookingViewModel == null) return false
            if(shouldStartIfConditionMet) CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            if(shouldStartIfConditionMet && MeatProbeUtils.isMeatProbeConnected(cookingViewModel)) {
                HMILogHelper.Loge(fragment.tag, "${cookingViewModel.cavityName.value} has probe attached not allowing JET Start recipe" )
                PopUpBuilderUtils.removeProbeToContinueCooking(fragment,cookingViewModel, fragment.getString(R.string.quickStart)){
                    startJetRecipeOrShowPopup(fragment, cookingViewModel, true)
                }
                return false
            }
            val jetRecipeId = CookingAppUtils.getAssignJetStartRecipeId()
            if(jetRecipeId == 0){
                Logd(fragment.tag, "cavityName ${cookingViewModel.cavityName.value} not JET start assigned, starting default JET start recipe through startQuickRecipe")
                if (cookingViewModel.isOfTypeMicrowaveOven && !isMWODoorSatisfy(cookingViewModel)) {
                    startQuickRecipe(fragment, cookingViewModel)
                    return false
                }
                if(shouldStartIfConditionMet) {
                    startQuickRecipe(fragment, cookingViewModel)
                }
                return true
            }
            var recipeErrorResponse : RecipeErrorResponse
            val cookbookRecord : CookbookRecord = CookBookViewModel.getInstance().getFavoriteRecordByFavoriteId(jetRecipeId)
            Logd(fragment.tag, "JET start recipe, jetRecipeId $jetRecipeId, recipeName ${cookbookRecord.recipeName}")
            recipeErrorResponse = cookingViewModel.recipeExecutionViewModel.load(cookbookRecord)
            if (recipeErrorResponse.isError) {
                HMILogHelper.Loge(fragment.tag, "JET start recipe error in loading $jetRecipeId, cookBookRecord ${cookbookRecord.recipeName}, cavity ${cookingViewModel.cavityName.value}")
                CookingAppUtils.handleCookingError(
                    fragment,
                    cookingViewModel,
                    recipeErrorResponse,
                    false)
                return false
            }
            CookingAppUtils.updateParametersInViewModel(
                cookbookRecord,
                CookingAppUtils.getRecipeOptions(),
                cookingViewModel.recipeExecutionViewModel
            )
            if (cookingViewModel.isOfTypeMicrowaveOven && !isMWODoorSatisfy(cookingViewModel)) {
                //must show open/close door popup for microwave as there was no door interaction observed for 5 mins
                Logd(fragment.tag, "JET start recipe, cavityName ${cookingViewModel.cavityName.value} microwave door condition not met, so showing showMicrowaveOpenClosePopup")
                showMicrowaveOpenClosePopup(fragment, cookingViewModel, null)
                return false
            } else if (shouldStartIfConditionMet) {
                //door is closed safe to start cycle
                recipeErrorResponse = cookingViewModel.recipeExecutionViewModel.execute()
                Logd(
                    tag,
                    "recipeErrorResponse name=${recipeErrorResponse.name} description=${recipeErrorResponse.description}"
                )
                if (recipeErrorResponse.isError) {
                    HMILogHelper.Loge(
                        fragment.tag,
                        "JET start recipe error in executing, jetRecipeId $jetRecipeId, cookBookRecord ${cookbookRecord.recipeName}, cavity ${cookingViewModel.cavityName.value}, recipeErrorResponse name=${recipeErrorResponse.name} description=${recipeErrorResponse.description}"
                    )
                    CookingAppUtils.handleCookingError(
                        fragment, cookingViewModel, recipeErrorResponse, false
                    )
                    return false
                }
            }
            return true
        }

        /**
         * show open/close or close popup
         * it has door observer as well to enable/disable start button
         * in case everything is met start cycle based on press event
         * @param fragment current fragment screen
         * @param cookingViewModel in scope view model most likely will be primary cooking view model for microwave
         */
        private fun showMicrowaveOpenClosePopup(
            fragment: Fragment,
            cookingViewModel: CookingViewModel,
            quickstartRecipeParam: QuickstartRecipeParam?
        ) {
            fragment.activity?.supportFragmentManager?.let { CookingAppUtils.dismissAllDialogs(it) }
            Logd(fragment.tag, "${cookingViewModel.cavityName.value} showMicrowaveOpenClosePopup")
            val doorOpenPopup = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(R.string.text_header_Prepare_MWO)
                .setHeaderViewCenterIcon(AppConstants.HEADER_VIEW_CENTER_ICON_GONE,false)
                .setDescriptionMessage(R.string.text_description_Prepare_MWO)
                .setDescriptionTextFont(ResourcesCompat.getFont(fragment.requireContext(), R.font.roboto_light))
                .setBottomPaddingForInnerLayout(POPUP_BOTTOM_PADDING_16PX)
                .setTopMarginForTitleText(POPUP_TITLE_TOP_MARGIN_52PX)
                .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                .setCancellableOutSideTouch(false)
                .setRightButton(R.string.text_button_start) {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.start_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    val recipeErrorResponse =
                        if (quickstartRecipeParam == null) {
                            cookingViewModel.recipeExecutionViewModel.execute()
                        } else {
                            CookingAppUtils.setRunningRecipeIsQuickStart(true)
                            cookingViewModel.recipeExecutionViewModel.executeQuickstartRecipe(
                                quickstartRecipeParam
                            )
                        }
                    Logd(
                        tag,
                        "recipeErrorResponse name=${recipeErrorResponse.name} description=${recipeErrorResponse.description}"
                    )
                    if (!recipeErrorResponse.isError){
                        CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                        HMIExpansionUtils.startOrStopKnobLEDFadeInLightAnimation(true)
                    }
                    else{
                        CookingAppUtils.handleCookingError(
                            fragment,
                            cookingViewModel,
                            recipeErrorResponse,
                            false)
                    }
                    true
                }.build()

            //Knob Implementation
            val hmiKnobListener = PopUpBuilderUtils.observeHmiKnobListener(
                onHMIRightKnobClick = {
                    if(doorOpenPopup.provideViewHolderHelper()?.rightTextButton?.isEnabled == true) doorOpenPopup.provideViewHolderHelper()?.rightTextButton?.callOnClick()
                },
                onHMILeftKnobClick = {
                    //do nothing leave it blank
                },
                onKnobSelectionTimeout = {
                    //do nothing leave it blank
                },
                onHMILongRightKnobPress = {
                    //do nothing
                },
                onHMIRightKnobTickHoldEvent = {
                    //do nothing
                }
            )
            doorOpenPopup.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    //Popup enabled the HMI cancel button
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    doorOpenPopup.viewLifecycleOwner.let {
                        cookingViewModel.doorState?.observe(it) { isOpen: Boolean ->
                            Logd(
                                tag,
                                "showMicrowaveOpenClosePopup $doorShowPopupValue and currentDoorStatus=$isOpen"
                            )
                            //update start button text and enable/disable based on door status and doorShowPopupValue from runnable
                            if (doorShowPopupValue == DOOR_STATE.SHOW_OPEN_CLOSE_DOOR_POPUP || isOpen) {
                                doorOpenPopup.provideViewHolderHelper()?.rightTextButton?.isEnabled =
                                    false
                                doorOpenPopup.provideViewHolderHelper()?.rightTextButton?.isClickable =
                                    false
                            } else {
                                doorOpenPopup.provideViewHolderHelper()?.rightTextButton?.isEnabled =
                                    true
                                doorOpenPopup.provideViewHolderHelper()?.rightTextButton?.isClickable =
                                    true
                            }
                            //update popup header title and body if DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP and door is open dynamically
                            if (doorShowPopupValue == DOOR_STATE.DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP ) {
                                val titleTextViewParams =
                                    doorOpenPopup.provideViewHolderHelper()?.titleTextView?.layoutParams as ViewGroup.MarginLayoutParams
                                titleTextViewParams.apply {
                                    setMargins(0,POPUP_TITLE_TOP_MARGIN_72PX,0,0)
                                }
                                doorOpenPopup.provideViewHolderHelper()?.titleTextView?.layoutParams = titleTextViewParams

                                if (isOpen){
                                    doorOpenPopup.provideViewHolderHelper()?.titleTextView?.text =
                                        doorOpenPopup.getString(R.string.text_header_Prepare_close_door)
                                    doorOpenPopup.provideViewHolderHelper()?.descriptionTextView?.text =
                                        doorOpenPopup.getString(R.string.text_description_close_door)
                                }else{
                                    doorOpenPopup.provideViewHolderHelper()?.titleTextView?.text =
                                        doorOpenPopup.getString(R.string.text_header_press_start)
                                    doorOpenPopup.provideViewHolderHelper()?.descriptionTextView?.text =
                                        doorOpenPopup.getString(R.string.text_description_Press_start)
                                }
                            }
                        }
                    }
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    //remove door state observer
                    doorOpenPopup.viewLifecycleOwner.let {
                        cookingViewModel.doorState?.removeObservers(
                            it
                        )
                    }
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                }
            })
            doorOpenPopup.show(fragment.parentFragmentManager, "showMicrowaveOpenClosePopup")
        }

        @Suppress("unused")
        fun primaryCavityPopUpObserver(fragment: Fragment) {
            val cookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
            if (cookingViewModel.recipeExecutionViewModel.isRunning) {
                showPopupAndObserveDoor(fragment,cookingViewModel)
            }
        }

        @Suppress("unused")
        fun secondaryCavityPopUpObserver(fragment: Fragment) {
            val cookingViewModel = CookingViewModelFactory.getSecondaryCavityViewModel()
            if (cookingViewModel.recipeExecutionViewModel.isRunning) {
                showPopupAndObserveDoor(fragment,cookingViewModel)
            }
        }

        fun cycleStartDoorOpenPopup(fragment: Fragment, cookingViewModel: CookingViewModel, isDelay : Boolean, onMeatProbeConditionMet: () -> Unit = {}) {
            if(PopUpBuilderUtils.isHotCavityDoorOpenPopupVisible()) {
                val dialogPopupBuilder = showPopUp(
                    R.layout.layout_popup_fragment,
                    R.string.text_header_Prepare_close_door,
                    R.string.text_description_popup_close_door
                )
                //Knob Implementation
                val hmiKnobListener = observeHmiKnobListener(onHMIRightKnobClick = {},
                    onHMILeftKnobClick = {},
                    onKnobSelectionTimeout = {},
                    onHMILongRightKnobPress = {},
                    onHMIRightKnobTickHoldEvent = {})
                lateinit var doorStateObserver: Observer<Boolean>

                doorStateObserver = Observer { isOpen ->
                    if (isOpen) {
                        if (!dialogPopupBuilder.isAdded) {
                            dialogPopupBuilder.show(
                                fragment.parentFragmentManager,
                                this::class.java.simpleName
                            )
                            dialogPopupBuilder.setOnDialogCreatedListener(object :
                                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                                override fun onDialogCreated() {
                                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                                    //Popup enabled the HMI cancel button
                                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                                }

                                override fun onDialogDestroy() {
                                    HMIExpansionUtils.removeHMIKnobInteractionListener(
                                        hmiKnobListener
                                    )
                                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                                }
                            })
                        }
                        manageCountDownTimerByVariant(
                            isOpen,
                            fragment.requireActivity(),
                            cookingViewModel
                        )
                    } else {
                        if (dialogPopupBuilder.isVisible) {
                            onMeatProbeConditionMet()
                            manageCountDownTimerByVariant(
                                isOpen,
                                fragment.requireActivity(),
                                cookingViewModel
                            )
                            fragment.lifecycleScope.launch {
                                if (isDelay)
                                    CookingAppUtils.handleDelayErrorAndStartCooking(
                                        fragment,
                                        cookingViewModel,
                                        false
                                    )
                                else
                                    CookingAppUtils.handleErrorAndStartCooking(
                                        fragment,
                                        cookingViewModel,
                                        false,
                                        false
                                    )
                                dialogPopupBuilder.dismiss()
                                // Remove the observer after starting cooking
                                cookingViewModel.doorState.removeObserver(doorStateObserver)
                            }
                        }
                    }
                }
                cookingViewModel.doorState.observe(fragment.viewLifecycleOwner, doorStateObserver)
            }
        }
        /**
         * se this popup to map any action upon door close event
         * @param onDoorCloseEventAction coroutine when door closes
         */
        fun upperCloseDoorToContinueAction(fragment: Fragment, cookingViewModel: CookingViewModel, onDoorCloseEventAction: () -> Unit = {}) {
            dismissUpperCavityDoorPopup()
            if (upperDoorDialogPopupBuilder == null && PopUpBuilderUtils.isHotCavityDoorOpenPopupVisible()) {
                Logd("Recipe: Showing upper cavity door popup")
                upperDoorDialogPopupBuilder = showPopUp(
                    R.layout.layout_popup_fragment,
                    R.string.text_header_Prepare_close_door,
                    R.string.text_description_popup_close_door
                )
                upperDoorDialogPopupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                    if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                        cookingViewModel.recipeExecutionViewModel.cancel()
                        Logd("closeDoorToContinueAction, cancelled recipe of ${cookingViewModel.cavityName.value} on doorOpenClosePopup timeout")
                        val variant = CookingViewModelFactory.getProductVariantEnum()
                        // Turn off primary cavity light if it is on
                        if (CavityLightUtils.getPrimaryCavityLightState()) {
                            CavityLightUtils.setPrimaryCavityLightState(false)
                        }
                        // If the product variant has a secondary oven, turn off the secondary cavity light if it is on
                        if (variant == CookingViewModelFactory.ProductVariantEnum.COMBO ||
                            variant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
                        ) {
                            if (CavityLightUtils.getSecondaryCavityLightState()) {
                                CavityLightUtils.setSecondaryCavityLightState(false)
                            }
                        }
                        dismissUpperCavityDoorPopup()
                    }
                }, fragment.resources.getInteger(R.integer.session_long_timeout))

                val hmiKnobListener = PopUpBuilderUtils.observeHmiKnobListener(
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

                val doorStateObserver: Observer<Boolean> = Observer { isOpen ->
                    if (isOpen) {
                        if (upperDoorDialogPopupBuilder?.isAdded != true) {
                            upperDoorDialogPopupBuilder?.show(
                                fragment.parentFragmentManager,
                                this::class.java.simpleName
                            )
                        }
                    } else {
                        if (upperDoorDialogPopupBuilder?.isVisible == true) {
                            onDoorCloseEventAction()
                            //M63KA-2776: Defect fixed where System Stuck on "Door Open/Close" Popup After Delay Cycle complete where user keep the door open
                            if((Objects.equals(cookingViewModel.recipeExecutionViewModel.recipeExecutionState.getValue(),
                                    RecipeExecutionState.DELAYED)) && (Objects.equals(cookingViewModel.recipeExecutionViewModel.
                                delayTimerState.getValue(), Timer.State.COMPLETED))) {
                                cookingViewModel.recipeExecutionViewModel.overrideDelay()
                            }
                            fragment.lifecycleScope.launch {
                                if (cookingViewModel.recipeExecutionViewModel.delayTimerState.value == Timer.State.COMPLETED)
                                    CookingAppUtils.handleDelayErrorAndStartCooking(
                                        fragment,
                                        cookingViewModel,
                                        false
                                    )
                                dismissUpperCavityDoorPopup()
                            }
                        }
                    }
                }
                upperDoorDialogPopupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    }

                    override fun onDialogDestroy() {
                        // Remove the observer after starting cooking
                        HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                        CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                        cookingViewModel.doorState.removeObserver(doorStateObserver)
                        CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                        onDoorCloseEventAction()
                        Logd("Recipe: closing upper cavity door popup")
                    }
                })
                cookingViewModel.doorState.observe(fragment.viewLifecycleOwner, doorStateObserver)
            }
        }


        fun lowerCloseDoorToContinueAction(
            fragment: Fragment,
            cookingViewModel: CookingViewModel,
            onDoorCloseEventAction: () -> Unit = {}
        ) {
            dismissLowerCavityDoorPopup()
            Log.d("DsK","lower isHotCavityDoorOpenPopupVisible ${PopUpBuilderUtils.isHotCavityDoorOpenPopupVisible()}")
            if (lowerDoorDialogPopupBuilder == null && PopUpBuilderUtils.isHotCavityDoorOpenPopupVisible()) {
                Logd("Recipe: Showing Lower cavity door popup")
                lowerDoorDialogPopupBuilder = showPopUp(
                    R.layout.layout_popup_fragment,
                    R.string.text_header_Prepare_close_door,
                    R.string.text_description_popup_close_door
                )
                lowerDoorDialogPopupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                    if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                        cookingViewModel.recipeExecutionViewModel.cancel()
                        Logd("closeDoorToContinueAction, cancelled recipe of ${cookingViewModel.cavityName.value} on doorOpenClosePopup timeout")
                        val variant = CookingViewModelFactory.getProductVariantEnum()
                        // Turn off primary cavity light if it is on
                        if (CavityLightUtils.getPrimaryCavityLightState()) {
                            CavityLightUtils.setPrimaryCavityLightState(false)
                        }
                        // If the product variant has a secondary oven, turn off the secondary cavity light if it is on
                        if (variant == CookingViewModelFactory.ProductVariantEnum.COMBO ||
                            variant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
                        ) {
                            if (CavityLightUtils.getSecondaryCavityLightState()) {
                                CavityLightUtils.setSecondaryCavityLightState(false)
                            }
                        }
                        dismissLowerCavityDoorPopup()
                    }
                }, fragment.resources.getInteger(R.integer.session_long_timeout))

                val hmiKnobListener = PopUpBuilderUtils.observeHmiKnobListener(
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

                val doorStateObserver: Observer<Boolean> = Observer { isOpen ->
                    if (isOpen) {
                        if (lowerDoorDialogPopupBuilder?.isAdded != true) {
                            lowerDoorDialogPopupBuilder?.show(
                                fragment.parentFragmentManager,
                                this::class.java.simpleName
                            )
                        }
                    } else {
                        if (lowerDoorDialogPopupBuilder?.isVisible == true) {
                            onDoorCloseEventAction()
                            //M63KA-2776: Defect fixed where System Stuck on "Door Open/Close" Popup After Delay Cycle complete where user keep the door open
                            if((Objects.equals(cookingViewModel.recipeExecutionViewModel.recipeExecutionState.getValue(),
                                    RecipeExecutionState.DELAYED)) && (Objects.equals(cookingViewModel.recipeExecutionViewModel.
                                delayTimerState.getValue(), Timer.State.COMPLETED))) {
                                cookingViewModel.recipeExecutionViewModel.overrideDelay()
                            }
                            fragment.lifecycleScope.launch {
                                if (cookingViewModel.recipeExecutionViewModel.delayTimerState.value == Timer.State.COMPLETED)
                                    CookingAppUtils.handleDelayErrorAndStartCooking(
                                        fragment,
                                        cookingViewModel,
                                        false
                                    )
                                dismissLowerCavityDoorPopup()
                            }
                        }
                    }
                }
                lowerDoorDialogPopupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    }

                    override fun onDialogDestroy() {
                        // Remove the observer after starting cooking
                        HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                        CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                        cookingViewModel.doorState.removeObserver(doorStateObserver)
                        CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                        onDoorCloseEventAction()
                        Logd("Recipe: closing Lower cavity door popup")
                    }
                })
                cookingViewModel.doorState.observe(fragment.viewLifecycleOwner, doorStateObserver)
            }
        }

        /**
         * se this popup to map any action upon door close event
         * upperSteamCloseDoorToContinueAction coroutine when door closes
         */
        fun upperSteamCloseDoorToContinueAction(
            fragment: Fragment,
            cookingViewModel: CookingViewModel,
            onDoorCloseEventAction: () -> Unit
        ) {
            dismissUpperCavityDoorPopup()
            if(upperDoorDialogPopupBuilder == null) {
                upperDoorDialogPopupBuilder =
                    ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                        .setHeaderTitle(R.string.text_header_steam_clean_paused)
                        .setDescriptionMessage(R.string.text_description_steam_clean_paused)
                        .setIsLeftButtonEnable(false)
                        .setIsRightButtonEnable(false)
                        .setIsPopupCenterAligned(true)
                        .setCancellableOutSideTouch(false)
                        .setTopMarginForTitleText(AppConstants.POPUP_PROBE_TITLE_TOP_MARGIN_87PX)
                        .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                        .setHeaderViewCenterIcon(AppConstants.HEADER_VIEW_CENTER_ICON_GONE, false)
                        .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                        .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                        .setDescriptionTextFont(
                            ResourcesCompat.getFont(
                                ContextProvider.getContext(),
                                R.font.roboto_light
                            )
                        )
                        .build()

                upperDoorDialogPopupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                    if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                        val variant = CookingViewModelFactory.getProductVariantEnum()
                        // Turn off primary cavity light if it is on
                        if (CavityLightUtils.getPrimaryCavityLightState()) {
                            CavityLightUtils.setPrimaryCavityLightState(false)
                        }
                        // If the product variant has a secondary oven, turn off the secondary cavity light if it is on
                        if (variant == CookingViewModelFactory.ProductVariantEnum.COMBO ||
                            variant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
                        ) {
                            if (CavityLightUtils.getSecondaryCavityLightState()) {
                                CavityLightUtils.setSecondaryCavityLightState(false)
                            }
                        }
                        fragment.lifecycleScope.launch(Dispatchers.Main) {
                            cookingViewModel.recipeExecutionViewModel.cancel()
                            withContext(Dispatchers.Main) {
                                Logd("steamCloseDoorToContinueAction, cancelled recipe of ${cookingViewModel.cavityName.value} on doorOpenClosePopup timeout")
                                CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                                dismissUpperCavityDoorPopup()
                            }
                        }
                    }
                }, fragment.resources.getInteger(R.integer.session_long_timeout))

                val hmiKnobListener = PopUpBuilderUtils.observeHmiKnobListener(
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

                val doorStateObserver: Observer<Boolean> = Observer { isOpen ->
                    if (isOpen) {
                        if (upperDoorDialogPopupBuilder?.isAdded != true) {
                            upperDoorDialogPopupBuilder?.show(
                                fragment.parentFragmentManager,
                                this::class.java.simpleName
                            )
                        }
                    } else {
                        if (upperDoorDialogPopupBuilder?.isVisible == true) {
                            onDoorCloseEventAction()
                            //M63KA-2776: Defect fixed where System Stuck on "Door Open/Close" Popup After Delay Cycle complete where user keep the door open
                            if((Objects.equals(cookingViewModel.recipeExecutionViewModel.recipeExecutionState.getValue(),
                                    RecipeExecutionState.DELAYED)) && (Objects.equals(cookingViewModel.recipeExecutionViewModel.
                                delayTimerState.getValue(), Timer.State.COMPLETED))) {
                                cookingViewModel.recipeExecutionViewModel.overrideDelay()
                            }
                            fragment.lifecycleScope.launch {
                                if (cookingViewModel.recipeExecutionViewModel.delayTimerState.value == Timer.State.COMPLETED)
                                    CookingAppUtils.handleDelayErrorAndStartCooking(
                                        fragment,
                                        cookingViewModel,
                                        false
                                    )
                                dismissUpperCavityDoorPopup()
                            }
                        }
                    }
                }

                upperDoorDialogPopupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    }

                    override fun onDialogDestroy() {
                        // Remove the observer after starting cooking
                        HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                        CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                        cookingViewModel.doorState.removeObserver(doorStateObserver)
                        CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                        onDoorCloseEventAction()
                        Logd("Steam clean: closing upper cavity door popup")
                    }
                })
                cookingViewModel.doorState.observe(fragment.viewLifecycleOwner, doorStateObserver)
            }
        }

        fun dismissUpperCavityDoorPopup() {
            try {
                if (Objects.nonNull(upperDoorDialogPopupBuilder)) {
                    upperDoorDialogPopupBuilder?.dismiss()
                    upperDoorDialogPopupBuilder = null
                }
            } catch (e: Exception) {
                Logd("Upper door closing error handling")
            }
        }


        /**
         * se this popup to map any action upon door close event
         * LowerSteamCloseDoorToContinueAction coroutine when door closes
         */
        fun lowerSteamCloseDoorToContinueAction(
            fragment: Fragment, cookingViewModel: CookingViewModel,
            onDoorCloseEventAction: () -> Unit = {}
        ) {
            dismissLowerCavityDoorPopup()
            if(lowerDoorDialogPopupBuilder == null && PopUpBuilderUtils.isHotCavityDoorOpenPopupVisible()) {
                lowerDoorDialogPopupBuilder =
                    ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                        .setHeaderTitle(R.string.text_header_steam_clean_paused)
                        .setDescriptionMessage(R.string.text_description_steam_clean_paused)
                        .setIsLeftButtonEnable(false)
                        .setIsRightButtonEnable(false)
                        .setIsPopupCenterAligned(true)
                        .setCancellableOutSideTouch(false)
                        .setTopMarginForTitleText(AppConstants.POPUP_PROBE_TITLE_TOP_MARGIN_87PX)
                        .setTopMarginForDescriptionText(POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                        .setHeaderViewCenterIcon(AppConstants.HEADER_VIEW_CENTER_ICON_GONE, false)
                        .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                        .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                        .setDescriptionTextFont(
                            ResourcesCompat.getFont(
                                ContextProvider.getContext(),
                                R.font.roboto_light
                            )
                        )
                        .build()

                lowerDoorDialogPopupBuilder?.setTimeoutCallback({ timeoutStatesEnum ->
                    if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                        val variant = CookingViewModelFactory.getProductVariantEnum()
                        // Turn off primary cavity light if it is on
                        if (CavityLightUtils.getPrimaryCavityLightState()) {
                            CavityLightUtils.setPrimaryCavityLightState(false)
                        }
                        // If the product variant has a secondary oven, turn off the secondary cavity light if it is on
                        if (variant == CookingViewModelFactory.ProductVariantEnum.COMBO ||
                            variant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
                        ) {
                            if (CavityLightUtils.getSecondaryCavityLightState()) {
                                CavityLightUtils.setSecondaryCavityLightState(false)
                            }
                        }
                        fragment.lifecycleScope.launch(Dispatchers.Main) {
                            cookingViewModel.recipeExecutionViewModel.cancel()
                            withContext(Dispatchers.Main) {
                                Logd("steamCloseDoorToContinueAction, cancelled recipe of ${cookingViewModel.cavityName.value} on doorOpenClosePopup timeout")
                                CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                                dismissLowerCavityDoorPopup()
                            }
                        }
                    }
                }, fragment.resources.getInteger(R.integer.session_long_timeout))

                val hmiKnobListener = PopUpBuilderUtils.observeHmiKnobListener(
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

                val doorStateObserver: Observer<Boolean> = Observer { isOpen ->
                    if (isOpen) {
                        if (lowerDoorDialogPopupBuilder?.isAdded != true) {
                            lowerDoorDialogPopupBuilder?.show(
                                fragment.parentFragmentManager,
                                this::class.java.simpleName
                            )
                        }
                    } else {
                        if (lowerDoorDialogPopupBuilder?.isVisible == true) {
                            onDoorCloseEventAction()
                            //M63KA-2776: Defect fixed where System Stuck on "Door Open/Close" Popup After Delay Cycle complete where user keep the door open
                            if((Objects.equals(cookingViewModel.recipeExecutionViewModel.recipeExecutionState.getValue(),
                                    RecipeExecutionState.DELAYED)) && (Objects.equals(cookingViewModel.recipeExecutionViewModel.
                                delayTimerState.getValue(), Timer.State.COMPLETED))) {
                                cookingViewModel.recipeExecutionViewModel.overrideDelay()
                            }
                            fragment.lifecycleScope.launch {
                                if (cookingViewModel.recipeExecutionViewModel.delayTimerState.value == Timer.State.COMPLETED)
                                    CookingAppUtils.handleDelayErrorAndStartCooking(
                                        fragment,
                                        cookingViewModel,
                                        false
                                    )
                                dismissLowerCavityDoorPopup()
                            }
                        }
                    }
                }
                lowerDoorDialogPopupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    }

                    override fun onDialogDestroy() {
                        // Remove the observer after starting cooking
                        HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                        CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                        cookingViewModel.doorState.removeObserver(doorStateObserver)
                        CookingAppUtils.setMeatProbeListenerAfterDismissDialog(fragment)
                        onDoorCloseEventAction()
                        Logd("Steam clean: closing Lower cavity door popup")
                    }
                })
                cookingViewModel.doorState.observe(fragment.viewLifecycleOwner, doorStateObserver)
            }
        }

        fun dismissLowerCavityDoorPopup() {
            try {
                if (Objects.nonNull(lowerDoorDialogPopupBuilder)) {
                    lowerDoorDialogPopupBuilder?.dismiss()
                    lowerDoorDialogPopupBuilder = null
                }
            } catch (e: Exception) {
                Logd("Lower door closing error handling")
            }
        }

        private fun showPopupAndObserveDoor(fragment: Fragment,cookingViewModel: CookingViewModel){
            if(PopUpBuilderUtils.isHotCavityDoorOpenPopupVisible()) {
                val dialogPopupBuilder = showPopUp(
                    R.layout.layout_popup_fragment,
                    R.string.text_header_Prepare_close_door,
                    R.string.text_description_popup_close_door
                )
                cookingViewModel.doorState.observe(
                    fragment.viewLifecycleOwner
                ) { isOpen: Boolean ->
                    if (isOpen) {
                        dialogPopupBuilder.show(
                            fragment.parentFragmentManager,
                            this::class.java.simpleName
                        )
                        manageCountDownTimerByVariant(
                            isOpen,
                            fragment.requireActivity(),
                            cookingViewModel
                        )
                    } else {
                        if (dialogPopupBuilder.isVisible) {
                            manageCountDownTimerByVariant(
                                isOpen,
                                fragment.requireActivity(),
                                cookingViewModel
                            )
                            dialogPopupBuilder.dismiss()
                            CookingAppUtils.setHmiKnobListenerAfterDismissDialog(fragment)
                        }
                    }
                }
            }
        }

        @Suppress("SameParameterValue")
        private fun showPopUp(layout: Int, title: Int, descMessage: Int): ScrollDialogPopupBuilder {
            return ScrollDialogPopupBuilder.Builder(layout).setHeaderTitle(title)
                .setDescriptionMessage(descMessage).setIsLeftButtonEnable(false).setCancellableOutSideTouch(false)
                .setIsRightButtonEnable(false).setIsPopupCenterAligned(true).build()
        }

        private fun startCountDownTimer(timeInMillis: Long = 1000L): CountDownTimer {
            return CountDownTimer().apply {
                setTime(timeInMillis)
                start()
            }
        }

        private fun manageCountDownTimerByVariant(
            isDoorOpen: Boolean,
            fragmentActivity: FragmentActivity,
            viewModel: CookingViewModel,
        ) {
            val variant = CookingViewModelFactory.getProductVariantEnum()
            when (variant) {
                CookingViewModelFactory.ProductVariantEnum.NONE -> TODO()
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO,
                -> {
                    fragmentActivity.lifecycleScope.launch(Dispatchers.IO) {
                        if (!viewModel.isOfTypeMicrowaveOven) {
                            if (viewModel.isPrimaryCavity) {
                                if (isDoorOpen) {
                                    countDownTimerPrimary = startCountDownTimer()
                                    HMILogHelper.Loge("timer set time primary####### ${countDownTimerPrimary?.timeRemaining}")
                                    HMILogHelper.Loge("timer state primary####### ${countDownTimerPrimary?.state?.name}")
                                } else {
                                    countDownTimerPrimary?.cancel()
                                    HMILogHelper.Loge("timer state primary####### ${countDownTimerPrimary?.state?.name}")
                                }

                            } else if (viewModel.isSecondaryCavity) {
                                if (isDoorOpen) {
                                    countDownTimerSecondary = startCountDownTimer()
                                    HMILogHelper.Loge("timer set time secondary####### ${countDownTimerSecondary?.timeRemaining}")
                                    HMILogHelper.Loge("timer state secondary####### ${countDownTimerSecondary?.state?.name}")
                                } else {
                                    countDownTimerSecondary?.cancel()
                                    HMILogHelper.Loge("timer state secondary####### ${countDownTimerSecondary?.state?.name}")
                                }

                            }
                        }
                    }
                }
                else -> {}
            }
        }
        /**
         * manage door interaction for ClockFragment
         * the opening and closing event has been triggered if in clock screen door was open before then closing door event would not triggered recipe selection
         * @param doorStatus true if door was opened, false otherwise
         * @param cookingViewModel for a particular cavity
         * @return DOOR_STATE which indicate to take an action
         */
        fun manageDoorInteractionWithRecipeSelection(fragment: Fragment, doorStatus: Boolean, cookingViewModel: CookingViewModel, doorStateToAction: Array<DOOR_STATE>): DOOR_STATE {
            var cavityDoorState = doorStateToAction[if (cookingViewModel.isPrimaryCavity) 0 else 1]
            //if initial then door observer was registered and ready to receive next events
            if (cavityDoorState == DOOR_STATE.INITIAL) {
                //if door is already open then we don't want to update its state, as closing the door would again got to recipe selection
                if (doorStatus) return DOOR_STATE.INITIAL
                cavityDoorState = DOOR_STATE.SHOW_OPEN_CLOSE_DOOR_POPUP
                return cavityDoorState
            }

            //if door is open and passed initial check then mark as ready
            if (doorStatus && cavityDoorState == DOOR_STATE.SHOW_OPEN_CLOSE_DOOR_POPUP) {
                cavityDoorState = DOOR_STATE.DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP
                return cavityDoorState
            }

            //If sabbath mode is activated then do not navigate to the recipe selection screen
            if (SettingsViewModel.getSettingsViewModel().sabbathMode.value == SABBATH_COMPLIANT) {
                Logd("Door closed : Sabbath mode is activated , do not navigate to the recipe selection screen")
                return cavityDoorState
            }

            if (ScrollDialogPopupBuilder.isAnyPopupShowing()) {
                Logd(
                    tag,
                    "Door open /close condition satisfy for ${cookingViewModel.cavityName.value}, but a DialogFragment is Visible so not moving to recipeSelectionFragment"
                )
                return cavityDoorState
            }

            //if door is closed and passed initial checks then move on to recipe selection
            if (!doorStatus && cavityDoorState == DOOR_STATE.DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP) {
                Logd(
                    tag,
                    "door opened and closed satisfied for ${cookingViewModel.cavityName.value}, moving to recipe selection fragment"
                )
                if (cookingViewModel.isOfTypeOven) SharedViewModel.getSharedViewModel(fragment.requireActivity())
                    .setCurrentRecipeBeingProgrammed(AppConstants.QUICK_START)
                if (cookingViewModel.isPrimaryCavity) {
                    NavigationUtils.navigateToUpperRecipeSelection(fragment)
                } else {
                    NavigationUtils.navigateToLowerRecipeSelection(fragment)
                }
                manageHMIPanelLights(homeLight = true, cancelLight = true, cleanLight = false)
            }
            return cavityDoorState
        }
        /**
         * Method to raise Oven Heat warning pop if door remains open after 1 min
         *
         * @param isDoorOpen         state of door opened/closed
         * @param cookingViewModel   view model of cavity
         * @param fragmentActivity   fragment activity
         */
        private fun handleHotCavityWarning(
            cookingViewModel: CookingViewModel,
            isDoorOpen: Boolean,
            fragmentActivity: FragmentActivity
        ) {
            if (isDoorOpen) {
                var conditionValue =
                    AppConstants.OVEN_HEAT_WARNING_TEMPERATURE_FAHRENHEIT_VALUE // FAHRENHEIT
                if (SettingsViewModel.getSettingsViewModel().temperatureUnit.value == SettingsViewModel.TemperatureUnit.CELSIUS) {
                    conditionValue =
                        AppConstants.OVEN_HEAT_WARNING_TEMPERATURE_CELSIUS_VALUE.toInt()
                }
                if (cookingViewModel.ovenTemperature?.value!! >= conditionValue) {
                    Logd("Door Opened on high temperature warning message Scheduled")
                    hotCavityPopupHandler.removeCallbacksAndMessages(null)
                    val fragment: Fragment? = NavigationUtils.getVisibleFragment()
                    hotCavityPopupHandler.postDelayed(
                        {
                            if (fragment != null) {
                                val sharedViewModel =
                                    SharedViewModel.getSharedViewModel(fragmentActivity)
                                if (CookingAppUtils.isSabbathMode() || sharedViewModel.isApplianceInAOrCCategoryFault() || sharedViewModel.isFaultPopUpOpen()) {
                                    hotCavityPopupHandler.removeCallbacksAndMessages(null)
                                } else {
                                    CookingAppUtils.closeAllVisibleDialogs(fragment)
                                    PopUpBuilderUtils.typeHotCavityWarningPopupBuilder(cookingViewModel,
                                        fragment
                                    )
                                    hotCavityPopupHandler.removeCallbacksAndMessages(null)
                                }
                            } else HMILogHelper.Loge("Unable to show hot cavity popup, no visible fragment available")
                        },
                        AppConstants.HOT_CAVITY_WARNING_POP_UP
                    )
                }
            } else {
                Logd("Removed oven heat warning call backs")
                hotCavityPopupHandler.removeCallbacksAndMessages(null)
            }
        }
    }

    interface DoorEventListener {
        fun onDoorEvent(cookingViewModel: CookingViewModel?, isDoorOpen: Boolean, ovenType: Int)
    }
}