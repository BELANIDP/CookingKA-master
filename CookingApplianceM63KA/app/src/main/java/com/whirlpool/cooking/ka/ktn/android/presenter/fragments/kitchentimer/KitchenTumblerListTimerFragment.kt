package android.presenter.fragments.kitchentimer

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.os.Handler
import android.presenter.basefragments.AbstractKitchenTimerTumblerListFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentKitchenTimerListBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel
import com.whirlpool.hmi.uicomponents.widgets.progress.Stepper
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import core.jbase.abstractViewHolders.AbstractKitchenTimerWidgetListItemViewProvider
import core.utils.AppConstants
import core.utils.AppConstants.POP_UP_DISMISS
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils

/**
 * File       : android.presenter.fragments.kitchentimer.KitchenTimerFragment
 * Brief      : Kitchen Timer list
 * Author     : Hiren
 * Created On : 06/19/2024
 * Details    : To show dynamic list of Kitchen Timer, user can add, delete or pause a particular Kitchen Timer Widget
 **/

class KitchenTumblerListTimerFragment : AbstractKitchenTimerTumblerListFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface, HMIKnobInteractionListener {
    private var viewBinding: FragmentKitchenTimerListBinding? = null
    private var timeoutHandler: Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        viewBinding = FragmentKitchenTimerListBinding.inflate(inflater, container, false)
        viewBinding?.root?.setViewTreeLifecycleOwner(this)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageHeaderBar()
        registerKitchenTimerObservers()
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            knobSelectedAction = KT_WIDGET_ACTION.PLAY_PAUSE
            mKnobPositionLiveData.value = 0
        } else {
            knobSelectedAction = KT_WIDGET_ACTION.NONE
        }
        onDismissCompleteTimerFragmentResultListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }
    override fun onResume() {
        super.onResume()
        startTimeout()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
    }
    private fun startTimeout() {
        timeoutHandler?.removeCallbacksAndMessages(null) // Cancel any existing timeouts
        timeoutHandler = Handler().apply {
            postDelayed({
                kitchenTimerTumblerListExitAnimation()
            }, AppConstants.KT_TUMBLER_LIST_TIMEOUT_DURATION)
        }
    }

    private fun resetTimeout() {
        timeoutHandler?.removeCallbacksAndMessages(null)
        startTimeout()
    }

    override fun onStop() {
        timeoutHandler?.removeCallbacksAndMessages(null)
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }
    override fun navigateToSetTimerFragment() {
        if (provideAddKitchenTimersButton()?.isEnabled == true) NavigationUtils.navigateSafely(
            this, R.id.action_kitchenTimerFragment_to_setKTFragment, null, null
        )
    }

    override fun provideAddKitchenTimersButton(): ImageView? {
        return viewBinding?.headerBarKitchenTimer?.getBinding()?.ivRightIcon
    }

    override fun onKitchenTimerComplete(model: KitchenTimerViewModel?) {
        handleKitchenTimerCompleteOrCancel()
    }

    override fun onKitchenTimerCancelled() {
        handleKitchenTimerCompleteOrCancel()
    }

    override fun onKitchenTimerStarted() {
        handleNewKitchenTimerStarted()
    }

    override fun provideTumbler(): BaseTumbler? {
        return viewBinding?.ktTumblers
    }

    override fun kitchenTimerAvailable(state: Boolean) {
        provideAddKitchenTimersButton()?.isEnabled = state
        provideAddKitchenTimersButton()?.visibility = if(state) View.VISIBLE else View.GONE
    }

    override fun provideKitchenListItemViewProvider(): AbstractKitchenTimerWidgetListItemViewProvider {
        return KitchenTimerWidgetViewProvider()
    }

    override fun provideStepper(): Stepper? {
        return viewBinding?.stepperKitchenTimerWidget
    }

    override fun updateDotsVisibilityForItems(size: Int) {
        provideStepper()?.visibility = if(size < 2) View.GONE else View.VISIBLE
    }

    /**
     *  dismiss event listener when click on dismiss or timeout on complete timer popup
     * */
    private fun onDismissCompleteTimerFragmentResultListener() {
        // listener for dismiss of complete timer popup
        requireActivity().let {
            it.supportFragmentManager.setFragmentResultListener(
                POP_UP_DISMISS,
                this
            ) { _, bundle ->
                val result = bundle.getBoolean(POP_UP_DISMISS)
                if (result) {
                    HMILogHelper.Logd(
                        tag,
                        "setFragmentResultListener for dismiss of complete timer popup"
                    )
                    handleKitchenTimerCompleteOrCancel()
                }
            }
        }
    }

    private fun manageHeaderBar() {
        viewBinding?.headerBarKitchenTimer?.setInfoIconVisibility(false)
        viewBinding?.headerBarKitchenTimer?.setLeftIconVisibility(true)
        viewBinding?.headerBarKitchenTimer?.setOvenCavityIconVisibility(false)
        viewBinding?.headerBarKitchenTimer?.setTitleText(
            getString(
                R.string.text_header_kitchen_timer
            )
        )
        viewBinding?.headerBarKitchenTimer?.setRightIcon(R.drawable.ic_add_40)
        viewBinding?.headerBarKitchenTimer?.setCustomOnClickListener(this)
    }

    override fun leftIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }
    private fun kitchenTimerTumblerListExitAnimation() {
        val headerBar = viewBinding?.headerBarKitchenTimer?.getBinding()?.root
        val tumblers = viewBinding?.ktTumblers

        if (headerBar != null && tumblers != null) {
            // Load animations
            val headerBarAnimation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.anim_fade_out_to_top)
            val tumblersAnimation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.anim_fade_out_to_bottom)

            // Play header bar animation
            headerBar.startAnimation(headerBarAnimation)
            // Play tumblers animation
            tumblers.startAnimation(tumblersAnimation)

            // Set listener to handle visibility after animation
            headerBarAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    headerBar.visibility = View.GONE
                    CookingAppUtils.navigateToStatusOrClockScreen(this@KitchenTumblerListTimerFragment)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            tumblersAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    CookingAppUtils.navigateToStatusOrClockScreen(this@KitchenTumblerListTimerFragment)
                    tumblers.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
    }
    /******************************************************  Knob Related Methods **********************************************/
    override fun onHMILeftKnobClick() {
        val kitchenTimerViewModel = getKitchenTimerForIdentifier(
            KitchenTimerVMFactory.getKitchenTimerViewModels(), knobSelectedKitchenTimerKey
        )
        when (knobSelectedAction) {
            KT_WIDGET_ACTION.PLAY_PAUSE -> if (kitchenTimerViewModel?.timerStatus?.value == KitchenTimerViewModel.TimerStatus.RUNNING) kitchenTimerViewModel.pauseTimer() else if (kitchenTimerViewModel?.timerStatus?.value == KitchenTimerViewModel.TimerStatus.PAUSED) kitchenTimerViewModel.resumeTimer()

            KT_WIDGET_ACTION.ADD_ONE_MIN -> if(KitchenTimerUtils.isAbleToAddOneMinToKitchenTimer(kitchenTimerViewModel)) kitchenTimerViewModel?.addTime(60)

            KT_WIDGET_ACTION.NONE -> HMILogHelper.Loge(
                tag,
                "knobSelectedAction $knobSelectedAction came NONE do nothing on nob press"
            )
        }
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            manageKnobRotation(knobId, knobDirection)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            mKnobPositionLiveData.value = -1
            knobSelectedAction = KT_WIDGET_ACTION.NONE
        }
    }

    fun manageKnobRotation(knobId: Int, knobDirectionEvent: String) {
        if (knobId != AppConstants.LEFT_KNOB_ID) return

        // Get the total size of knob rotation (total KT widget's with play/pause, +1min)
        val size = (provideTumbler()?.tumblerItems?.size ?: 0) * 2

        // Update the live data value of current position
        val newKnobPosition = CookingAppUtils.getKnobPositionIndex(
            knobDirectionEvent,
            mKnobPositionLiveData.value ?: 0,
            size
        )
        mKnobPositionLiveData.value = newKnobPosition
        HMILogHelper.Logd(" mKnobPositionLiveData value ${mKnobPositionLiveData.value}")

        val tumbler = provideTumbler() ?: return

        // Define the range-to-scroll position mapping
        val scrollPositionMap = mapOf(
            0..1 to 0,
            2..3 to 1,
            4..5 to 2,
            6..7 to 3
        )

        // Find the scroll position based on knob position
        scrollPositionMap.forEach { (range, scrollPosition) ->
            if (newKnobPosition in range && tumbler.selectedIndex != scrollPosition) {
                tumbler.scrollToPosition(scrollPosition)
            }
        }
    }
    /******************************************************  Knob Related Methods **********************************************/

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if(CookingViewModelFactory.getInScopeViewModel() == null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, cookingViewModel)
            return
        }
        if (cookingViewModel != null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            launchProbeDetectedPopupAsPerVariant(cookingViewModel)
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        //Meat probe removed dialog shown here and updating the screen time out. Once probe inserted then reset the screen timeout as expected.
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            updateTimeoutValue(TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_STARTED,resources.getInteger(R.integer.session_short_timeout))
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel, onMeatProbeInsertionCallback = {
                updateTimeoutValue(TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_STARTED,provideScreenTimeoutValueInSeconds())
            })
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
    override fun onUserInteraction() {
        resetTimeout()
        super.onUserInteraction()
    }
}
