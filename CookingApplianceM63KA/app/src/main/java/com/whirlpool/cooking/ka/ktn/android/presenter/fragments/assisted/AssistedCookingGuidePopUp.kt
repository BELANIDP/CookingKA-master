package android.presenter.fragments.assisted

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.tools.util.DisplayUtils
import core.utils.CavityLightUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.PopUpBuilderUtils
import core.viewHolderHelpers.AssistedCookingGuidePopUpViewProvider


/**
 * File        : android.presenter.fragments.assisted.AssistedCookingGuidePopUp
 * Brief       : Assisted Cooking guide Popup after recipe has already been started in Day2 scenario
 * Author      : Hiren
 * Created On  : 05/20/2024
 * Details     : User can scroll to see the information of cooking guide with serving tips
 */
class AssistedCookingGuidePopUp: DialogFragment(),  HMIExpansionUtils.UserInteractionListener, MeatProbeUtils.MeatProbeListener{
    private var viewProvider : AssistedCookingGuidePopUpViewProvider? = null
    private var timeoutViewModel: TimeoutViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewProvider = AssistedCookingGuidePopUpViewProvider(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return viewProvider?.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewProvider?.onViewCreated(view, savedInstanceState)
        viewProvider?.closeGuidePopup()?.setOnClickListener { dismiss() }
    }

    override fun onResume() {
        super.onResume()
        MeatProbeUtils.setMeatProbeListener(this)
    }

    override fun onPause() {
        super.onPause()
        MeatProbeUtils.removeMeatProbeListener()
    }

    /**
     * Called when the view previously created by onCreateView(LayoutInflater, ViewGroup, Bundle) has been detached from the fragment.
     */
    override fun onDestroyView() {
        DisplayUtils.showFullScreenWindow(requireActivity().window)
        super.onDestroyView()
        CookingAppUtils.clearOrEraseCookingGuideList()
        viewProvider?.onDestroyView()
        viewProvider = null
    }
    /**
     * Called to create a dialog to be shown.
     *
     * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(root)
        dialog.window.let {
            it?.setBackgroundDrawableResource(R.drawable.vector_popup_background)
            it?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            DisplayUtils.showFullScreenWindow(it)
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        timeoutViewModel = ViewModelProvider(this)[TimeoutViewModel::class.java]
        initTimeout()
    }


    /**
     * Method to call to initiate screen timeout
     */
    private fun initTimeout() {
        timeoutViewModel?.setTimeout(
            CavityLightUtils.getProgrammingStateTimeoutValue(resources)
        )
        timeoutViewModel?.timeoutCallback?.observe(
            viewLifecycleOwner
        ) { timeoutStatesEnum: TimeoutViewModel.TimeoutStatesEnum ->
            HMILogHelper.Logd("TimeoutCallback: " + timeoutStatesEnum.name)
            if (timeoutStatesEnum == TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                dismiss()
            }
        }
    }

    /**
     * Method to call when user interacts with screen and restart timeout
     */
    override fun onUserInteraction() {
        HMILogHelper.Logd("onUserInteraction")
        timeoutViewModel?.restart()
    }

    override fun onStop() {
        super.onStop()
        if (timeoutViewModel != null) {
            timeoutViewModel?.stop()
        }
    }
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if(CookingViewModelFactory.getInScopeViewModel().cavityName.value.contentEquals(cookingViewModel?.cavityName?.value)){
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, CookingViewModelFactory.getInScopeViewModel())
        }else{
            if (cookingViewModel != null) {
                viewProvider?.closeGuidePopup()?.performClick()
                PopUpBuilderUtils.probeDetectedInOtherCavityMidWayRecipeRunning(this, cookingViewModel, CookingViewModelFactory.getInScopeViewModel())
            }
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
    }
}