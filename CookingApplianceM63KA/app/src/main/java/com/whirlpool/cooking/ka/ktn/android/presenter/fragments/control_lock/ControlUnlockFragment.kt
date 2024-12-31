package android.presenter.fragments.control_lock

import android.os.Bundle
import android.presenter.basefragments.AbstractStatusFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentControlUnlockBinding
import com.whirlpool.cooking.widgets.CustomControlLockSeekBar
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants.CONTROL_UNLOCK_FROM_POPUP
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils

class ControlUnlockFragment : Fragment(), CustomControlLockSeekBar.OnSeekBarChangeListener,
    HMIExpansionUtils.UserInteractionListener, View.OnClickListener{

    private var fragmentControlUnlockBinding: FragmentControlUnlockBinding? = null
    private var timeoutViewModel: TimeoutViewModel? = null
    private var TAG = "ControlUnlockFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        timeoutViewModel = ViewModelProvider(this)[TimeoutViewModel::class.java]
        timeoutViewModel?.timeoutCallback?.observe(
            viewLifecycleOwner
        ) { timeoutStatesEnum: TimeoutViewModel.TimeoutStatesEnum ->
            HMILogHelper.Logd("$TAG: TimeoutCallback: " + timeoutStatesEnum.name)
            if (timeoutStatesEnum == TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                navigateBack()
            }
        }
        val timeoutInMin = CookingAppUtils.getTimeoutValueBasedOnStringLength(requireContext(), getString(R.string.text_description_unlock))/resources.getInteger(R.integer.ms_1000)
        HMILogHelper.Logd("$TAG: timeoutVal: $timeoutInMin")
        timeoutViewModel?.setTimeout(timeoutInMin)
        fragmentControlUnlockBinding = FragmentControlUnlockBinding.inflate(inflater)
        fragmentControlUnlockBinding?.lifecycleOwner = this.viewLifecycleOwner
        return fragmentControlUnlockBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateView()
        fragmentControlUnlockBinding?.controlLockPopup?.setOnClickListener(this)
    }

    override fun onStop() {
        timeoutViewModel?.stop()
        super.onStop()
    }

    override fun onDestroy() {
        HMIExpansionUtils.setKnobEventFlag(true)
        fragmentControlUnlockBinding = null
        if (timeoutViewModel != null) {
            timeoutViewModel?.stop()
        }
        super.onDestroy()
    }

    private fun updateView() {
        HMILogHelper.Logd("$TAG: updateView called")
        fragmentControlUnlockBinding?.controlLockPopup?.visibility = View.VISIBLE
        fragmentControlUnlockBinding?.textViewPopupControlLockMessage?.text = getString(R.string.text_description_unlock)
        fragmentControlUnlockBinding?.controlLockCustomSeekBar
                ?.setOnSeekBarChangeListener(
                    this
                )
    }

    override fun updateControlLockView(progressValues: Int) {
        this.onUserInteraction()
        //do nothing
    }

    override fun unlockControlLock() {
        HMILogHelper.Logd("$TAG: unlockControlLock called")
        SettingsViewModel.getSettingsViewModel().setControlLock(false)
        navigateBack()
    }

    private fun navigateBack()
    {
        arguments?.let { bundle ->
            bundle.getBoolean(CONTROL_UNLOCK_FROM_POPUP).let {
                if (it){
                    Navigation.findNavController(NavigationUtils.getViewSafely(this) ?: requireView()).previousBackStackEntry?.savedStateHandle?.set(CONTROL_UNLOCK_FROM_POPUP,true)
                }
            }
        }
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(this) ?: requireView()
            )
        )
    }
    override fun onUserInteraction() {
        HMILogHelper.Logd("$TAG: onUserInteraction")
        timeoutViewModel?.restart()
    }

    override fun onClick(p0: View?) {
        HMILogHelper.Logd("$TAG: onClick, calling onUserInteraction")
        onUserInteraction()
    }

}