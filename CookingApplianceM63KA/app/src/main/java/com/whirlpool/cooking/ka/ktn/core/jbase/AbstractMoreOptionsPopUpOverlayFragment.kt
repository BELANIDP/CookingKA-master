/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.jbase

import android.app.Dialog
import android.framework.services.HMIKnobInteractionListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.common.utils.TimeoutViewModel.TimeoutStatesEnum
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.uicomponents.tools.util.DisplayUtils
import core.jbase.abstractViewHolders.AbstractMoreOptionsPopupViewHolder
import core.utils.AppConstants.PRIMARY_CAVITY_KEY
import core.utils.CavityLightUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedViewModel


/**
 * File : core.jbase.AbstractDialogPopUpOverlayFragment
 *
 * Brief :  Abstract class for creating custom dialog pop-up overlay fragments.
 *
 * Author : PARMAM
 *
 * Created On : 09/02/2024
 */
abstract class AbstractMoreOptionsPopUpOverlayFragment(val cavity : String = PRIMARY_CAVITY_KEY) : DialogFragment(),
    View.OnClickListener, HMIExpansionUtils.UserInteractionListener,
    GridRecyclerViewInterface.GridItemMoreOptionsClickListener, HMIKnobInteractionListener, MeatProbeUtils.MeatProbeListener {

    private var timeoutViewModel: TimeoutViewModel? = null
    protected var sharedViewModel: SharedViewModel? = null
    var cavityType = cavity

    /**
     * Provide the view holder helper for managing the dialog fragment's views.
     *
     * @return The view holder helper.
     */
    abstract fun provideViewHolderHelper(): AbstractMoreOptionsPopupViewHolder?
    abstract fun provideCookingViewModel(): CookingViewModel?

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
        super.onCreateView(inflater, container, savedInstanceState)
        return provideViewHolderHelper()?.onCreateView(
            inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.hmiKeyEnableDisablePopupsConfiguration()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        HMIExpansionUtils.setFragmentUserInteractionListener(this)
        setUpViewModel()
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
     * Called to create a dialog to be shown.
     *
     * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.let {
            it?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            DisplayUtils.showFullScreenWindow(it)
            it?.setGravity(Gravity.TOP)
        }

        dialog.setContentView(root)
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        val dialogAnimation : Int = if(cavityType == PRIMARY_CAVITY_KEY) {
            R.style.DialogAnimationDown
        } else R.style.DialogAnimationUp

        dialog.window?.attributes?.windowAnimations = dialogAnimation

        // Set the top margin
        val params = dialog.window?.attributes
        if (params != null) {
            params.y = 48
        } // Assuming you have a dimension resource
        dialog.window?.attributes = params

        return dialog
    }

    /**
     * Called when the view previously created by onCreateView(LayoutInflater, ViewGroup, Bundle) has been detached from the fragment.
     */
    override fun onDestroyView() {
        HMIExpansionUtils.hmiKeyEnableDisableAfterPopupDestroyed()
        DisplayUtils.showFullScreenWindow(requireActivity().window)
        super.onDestroyView()
    }

    override fun onDestroy() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        HMIExpansionUtils.removeFragmentUserInteractionListener(this)
        super.onDestroy()
    }


    override fun onStart() {
        super.onStart()
        initTimeout()
    }

    /**
     * Abstract Method to get the array of tile data of the list recycler view , it varies based on
     * screen
     */
    protected abstract fun provideCycleListRecyclerViewTilesData(): ArrayList<GridListItemModel>?

    /**
     * Abstract Method to get the size of recycler view tile data array, it varies based on screen
     */
    protected abstract fun provideCycleListRecyclerViewSize(): Int

    /**
     * Abstract Method to get the array of default tile data of the list recycler view , it varies based on
     * screen
     */
    protected abstract fun provideDefaultOptionsListRecyclerViewTilesData(): ArrayList<GridListItemModel>?

    /**
     * Abstract Method to get the size of recycler view tile data array, it varies based on screen
     */
    protected abstract fun provideDefaultOptionsListRecyclerViewSize(): Int

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
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if(provideCookingViewModel()?.cavityName?.value.contentEquals(cookingViewModel?.cavityName?.value)){
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, provideCookingViewModel())
        }else{
            if (cookingViewModel != null) {
                PopUpBuilderUtils.probeDetectedInOtherCavityMidWayRecipeRunning(this, cookingViewModel,provideCookingViewModel())
            }
        }
        dismiss()
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            //Handling scenario - More option popup come and same time probe removed then we have to wait both popup and stop timer.
            if (timeoutViewModel != null) {
                timeoutViewModel?.stop()
            }
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel, onMeatProbeDestroy = {
                //Handling scenario - once user insert probe then restart the timer and dismiss all popups.
                if (timeoutViewModel != null) {
                    timeoutViewModel?.restart()
                }
                dismiss()
            })
        }
    }
}