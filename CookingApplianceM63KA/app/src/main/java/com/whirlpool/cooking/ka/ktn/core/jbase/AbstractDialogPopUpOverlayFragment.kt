/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.jbase

import android.app.Dialog
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Fade
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.common.utils.TimeoutViewModel.TimeoutStatesEnum
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uicomponents.tools.util.DisplayUtils
import core.utils.CavityLightUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper


/**
 * File : core.jbase.AbstractDialogPopUpOverlayFragment
 *
 * Brief :  Abstract class for creating custom dialog pop-up overlay fragments.
 *
 * Author : PARMAM
 *
 * Created On : 09/02/2024
 */
abstract class AbstractDialogPopUpOverlayFragment : DialogFragment(),
    View.OnClickListener, HMIExpansionUtils.UserInteractionListener, HMIKnobInteractionListener{

    protected var timeoutViewModel: TimeoutViewModel? = null

    /**
     * Get the layout resource ID for the dialog fragment.
     *
     * @return The layout resource ID.
     */
    protected abstract val layoutResource: Int

    /**
     * Provide the view holder helper for managing the dialog fragment's views.
     *
     * @return The view holder helper.
     */
    abstract fun provideViewHolderHelper(): AbstractDialogPopupViewHolder?

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The view for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        enterTransition = Fade().apply {
            duration = 2000
        }
        super.onCreateView(inflater, container, savedInstanceState)
        return provideViewHolderHelper()?.onCreateView(
            inflater, container, savedInstanceState,
            layoutResource
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.hmiKeyEnableDisablePopupsConfiguration()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        setUpViewModel()
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
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimatioPopIn
        return dialog
    }

    /**
     * Called when the view previously created by onCreateView(LayoutInflater, ViewGroup, Bundle) has been detached from the fragment.
     */
    override fun onDestroyView() {
        HMIExpansionUtils.hmiKeyEnableDisableAfterPopupDestroyed()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        DisplayUtils.showFullScreenWindow(requireActivity().window)
        super.onDestroyView()
    }


    override fun onStart() {
        super.onStart()
        initTimeout()
    }


    /**
     * Method to call to initiate screen timeout
     */
    protected open fun initTimeout() {
        timeoutViewModel?.timeoutCallback?.observe(
            viewLifecycleOwner
        ) { timeoutStatesEnum: TimeoutStatesEnum ->
            HMILogHelper.Logd("TimeoutCallback: " + timeoutStatesEnum.name)
            if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                dismiss()
            }
        }
        timeoutViewModel?.setTimeout(
            CavityLightUtils.getProgrammingStateTimeoutValue(resources)
        )
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

    /**
     * Method to set up view models.
     */
    protected open fun setUpViewModel() {
        timeoutViewModel = ViewModelProvider(this)[TimeoutViewModel::class.java]
    }
}