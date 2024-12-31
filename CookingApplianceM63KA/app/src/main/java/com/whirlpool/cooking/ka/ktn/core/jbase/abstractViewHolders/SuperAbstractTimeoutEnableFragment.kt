package core.jbase.abstractViewHolders

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.isDemoModeEnabled
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils


/**
 * File        : com.whirlpool.cooking.base.SuperAbstractTimeoutEnableFragment<br></br>
 * Brief       : Parent class for having screen timeout behaviour<br></br>
 * Author      : WAGHA <br></br>
 * Details     : Parent to handle the screen timeout. It implements user interaction touch lister and reset the timer<br></br>
 * DO NOT INCLUDE any functionality that is not related to timeout behaviour
 * Override provideScreenTimeoutValueInSeconds is different than R.integer.session_short_timeout<br></br>
 * Override provideTimeoutHandlerCallback if behaviour is different tan specify in parent clas.<br></br>
 */
abstract class SuperAbstractTimeoutEnableFragment : Fragment(),
    HMIExpansionUtils.UserInteractionListener, MeatProbeUtils.MeatProbeListener {
    private val tag: String = SuperAbstractTimeoutEnableFragment::class.java.simpleName
    var timeoutViewModel: TimeoutViewModel? = null
    private var isTimeoutApplicable = true
    private var isMeatProbeApplicable = true
    private var setGraphAndNavigateToClock = false

    /**
     * Override this set screen timeout value only if value differ than session_short_timeout
     * @return timeout value in seconds, default is 600 seconds
     */
    open fun provideScreenTimeoutValueInSeconds(): Int {
        return resources.getInteger(R.integer.session_short_timeout)
    }


    /**
     * override if fragment doesn't require timeout functionality, default is true
     * @return true if Fragment needs timeout functionality, false otherwise
     */
    private fun isTimeoutApplicable(): Boolean {
        return isTimeoutApplicable
    }

    /**
     * if fragment doesn't require timeout functionality, default is true
     * set true if Fragment needs timeout functionality, false otherwise
     */
    fun setTimeoutApplicable(isTimeoutApplicable: Boolean) {
        this.isTimeoutApplicable = isTimeoutApplicable
    }

    /**
     * if fragment don't know the destination for ex. we are on the X graph and want to
     * navigate on the clock screen with correct product variant graph then set this variable as true,
     * default is false, set true if Fragment needs timeout functionality with proper graph set, false otherwise
     */
    fun setGraphAndNavigateToClock(setGraphAndNavigateToClock: Boolean) {
        this.setGraphAndNavigateToClock = setGraphAndNavigateToClock
    }

    /**
     * override if fragment doesn't require KitchenTimeOut functionality, default is true
     * @return true if Fragment needs KT complete functionality, false otherwise
     */
    protected open fun isKitchenTimerEventsApplicable(): Boolean {
        return true
    }

    /**
     * if fragment doesn't require meat probe functionality, default is true
     * set true if Fragment needs meat probe functionality, false otherwise
     */
    fun setMeatProbeApplicable(isMeatProbeApplicable: Boolean) {
        this.isMeatProbeApplicable = isMeatProbeApplicable
    }
    /**
     * once timeout happens this function will be called to navigate to different screen or any other functionality
     * default will be onTimeoutProgrammingState if not override by child classes
     */
    private fun provideTimeoutHandlerCallback() {
        HMILogHelper.Logd(
            tag,
            "timeout happened, moving to clock screen as default timeout handler callback"
        )
        // Fragment result will publish on timeout callback those who need they can utilise the result call back
        requireActivity().supportFragmentManager.setFragmentResult(
            AppConstants.TIMEOUT_CALLBACK, bundleOf(
                AppConstants.TIMEOUT_CALLBACK to true)
        )
        if (setGraphAndNavigateToClock) {
            CookingAppUtils.setNavGraphAndNavigateToClock(this)
        } else {
            if (isDemoModeEnabled() && !CookingAppUtils.isAnyCavityRunningRecipeOrDelayedStateOrPausedState()) {
                navigateSafely(
                    this,
                    R.id.demoModeLandingFragment,
                    null,
                    null
                )
                return
            }
            CookingAppUtils.navigateToStatusOrClockScreen(this)
        }
    }

    //observer to keep track on timer. Only calls when timer timeout happens or disables
    private var timeoutElapsedObserver: Observer<TimeoutViewModel.TimeoutStatesEnum>? = null

    /**
     * Calls when Fragment is visible to user and ready to get interacted
     */
    override fun onStart() {
        super.onStart()
//        OTAVMFactory.getOTAViewModel().setApplianceBusyState(true)
        HMILogHelper.Logd(tag, "Make Appliance Busy As TRUE onStart of TimeoutEnableFragment")
        HMILogHelper.Logd(tag, "is timeout applicable: ${isTimeoutApplicable()}")
        if (isTimeoutApplicable()) {
            timeoutViewModel = ViewModelProvider(this)[TimeoutViewModel::class.java]
            //setting and starting timeout
            if(isDemoModeEnabled() && provideScreenTimeoutValueInSeconds()>120){ //2 mins timeout if demo is enabled
                timeoutViewModel?.setTimeout(resources.getInteger(R.integer.demo_session_timeout))
            }else {
                //else set timeout
                timeoutViewModel?.setTimeout(provideScreenTimeoutValueInSeconds())
            }
            timeoutElapsedObserver =
                Observer { timeoutStatesEnum: TimeoutViewModel.TimeoutStatesEnum ->
                    //callback method when timeout does happen

                    HMILogHelper.Logd("timeoutStatesEnum: $timeoutStatesEnum")
                    if (timeoutStatesEnum === TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                        val isEnterHotCavityDialogPopupVisible =
                            PopUpBuilderUtils.isEnterHotCavityDialogPopupVisible()
                        if (!isEnterHotCavityDialogPopupVisible) {
                            HMILogHelper.Logd(
                                tag,
                                "timeout happened, moving timeout handler callback"
                            )
                            provideTimeoutHandlerCallback()
                        } else {
                            timeoutViewModel?.restart()
                        }
                    }
                }
            //observer to take action when timeout happens
            timeoutViewModel?.timeoutCallback?.observe(this, timeoutElapsedObserver!!)
            //to receive callbacks in the fragment onUserInteraction
            HMIExpansionUtils.setFragmentUserInteractionListener(this)
        }
        if (isKitchenTimerEventsApplicable()) KitchenTimerUtils.onKitchenTimerListener(this,
            onKTCompleteCallback = { PopUpBuilderUtils.kitchenTimerCompletedPopup(this, it) })
    }

    override fun onResume() {
        super.onResume()
        if (isMeatProbeApplicable)
            MeatProbeUtils.setMeatProbeListener(this)
    }

    override fun onPause() {
        super.onPause()
        if (isMeatProbeApplicable)
            MeatProbeUtils.removeMeatProbeListener()
    }

    /**
     * Method to update timeout value and state
     *
     * @param timeoutState     STOP/DEFAULT
     * @param timeoutValue     updated timeout value or default timeout value
     */
    fun updateTimeoutValue(timeoutState: TimeoutViewModel.TimeoutStatesEnum, timeoutValue: Int) {
        HMILogHelper.Logd("timeoutElapsedObserver: $timeoutState timeoutValue: $timeoutValue")
        if (timeoutState == TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_STARTED) {
                timeoutViewModel?.setTimeout(timeoutValue)
            }
        }


    /**
     * Calls when Fragment is no longer visible to user. Disable to handler, timer and wait for next Fragment's onStart events
     */
    override fun onStop() {
        super.onStop()
        timeoutViewModel?.let { it ->
            it.stop()
            timeoutElapsedObserver?.let {
                timeoutViewModel?.timeoutCallback?.removeObserver(it)
            }
        }
        timeoutViewModel = null
        timeoutElapsedObserver = null
        HMIExpansionUtils.removeFragmentUserInteractionListener(this)
        KitchenTimerUtils.removeKitchenTimerListener(this)
    }

    /**
     * Use this to receive callbacks in fragment whenever user interacts with HMI\'s LCD screen
     */
    override fun onUserInteraction() {
        if (isTimeoutApplicable() && timeoutViewModel != null) {
            //reset the timer and enabled timer from beginning
            timeoutViewModel?.restart()
        }
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if(CookingViewModelFactory.getInScopeViewModel() == null) {
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, cookingViewModel)
            return
        }
        if (cookingViewModel != null) {
            launchProbeDetectedPopupAsPerVariant(cookingViewModel)
        }
    }


    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
    }
    fun launchProbeDetectedPopupAsPerVariant(cookingViewModel: CookingViewModel?) {
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(
                    this,
                    CookingViewModelFactory.getInScopeViewModel()
                )
            }

            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                PopUpBuilderUtils.probeDetectedInOtherCavityMidWayRecipeRunning(
                    this,
                    cookingViewModel,
                    CookingViewModelFactory.getInScopeViewModel()
                )
            }

            else -> {}
        }
    }

}
