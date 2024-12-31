package android.presenter.fragments


import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.whirlpool.cooking.ka.databinding.FragmentShowInstructionBinding
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getHeaderTitleResId
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.PopUpBuilderUtils

/**
 * File       : android.presenter.fragments.ShowInstructionFragment.
 * Brief      : implementation fragment class for show instructions screens from info icon and more options popup
 * Author     : DUNGAS
 * Created On : 25/10/2024
 * Details    :
 */


class ShowInstructionFragment : Fragment(), HMIKnobInteractionListener {
    private var showInstructionBinding: FragmentShowInstructionBinding? = null
    val inScopeViewModel: RecipeExecutionViewModel =
        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel

    /**
     * Observe whether CookingViewModelFactory is initialized or not.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        showInstructionBinding = FragmentShowInstructionBinding.inflate(inflater, container, false)
        return showInstructionBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageChildViews()
    }

    override fun onDestroy() {
        showInstructionBinding = null
        super.onDestroy()
    }

    private fun manageChildViews(){
        manageHeaderBar()
        setHmiKnobListener()
        setRecipeNameTitleAndInstruction()
        setRightButtonClickListener()
    }


    private fun setRightButtonClickListener() {
        showInstructionBinding?.textButtonRightOk?.setOnClickListener(View.OnClickListener {
            if (RecipeExecutionState.IDLE == inScopeViewModel.recipeExecutionState.value) {
                NavigationViewModel.popBackStack(findNavController())
            } else {
                CookingAppUtils.navigateToStatusOrClockScreen(this)
            }
        })
    }

    private fun setRecipeNameTitleAndInstruction() {
        val recipeName = arguments?.getString(BundleKeys.RECIPE_NAME) ?: AppConstants.EMPTY_STRING
        if (recipeName == AppConstants.RECIPE_SLOW_ROAST){
            val resIdForName = getHeaderTitleResId(requireContext(), AppConstants.RECIPE_SLOW_ROAST)
            val resIdForInstruction = CookingAppUtils.getResIdFromResName(
                context,
                AppConstants.TEXT_INFORMATION + AppConstants.RECIPE_SLOW_ROAST,
                AppConstants.RESOURCE_TYPE_STRING
            )
            showInstructionBinding?.headerBar?.setTitleText(resIdForName)
            showInstructionBinding?.descriptionText?.setText(resIdForInstruction)
            return
        }
        else{
            var isMWORecipe = CookingViewModelFactory.getInScopeViewModel().isOfTypeMicrowaveOven
            var textInformation = AppConstants.TEXT_INFORMATION
            if (isMWORecipe){
                textInformation = AppConstants.TEXT_INFORMATION_MWO
            }
            val resIdForName = getHeaderTitleResId(requireContext(), inScopeViewModel.recipeName?.value.toString())
            val resIdForInstruction = CookingAppUtils.getResIdFromResName(
                context,
                textInformation + inScopeViewModel.recipeName?.value,
                AppConstants.RESOURCE_TYPE_STRING
            )
            showInstructionBinding?.headerBar?.setTitleText(resIdForName)
            showInstructionBinding?.descriptionText?.setText(resIdForInstruction)
            return
        }
    }

    private fun manageHeaderBar() {
        showInstructionBinding?.headerBar?.apply {
            setLeftIconVisibility(false)
            setRightIconVisibility(false)
            setInfoIconVisibility(false)
            setOvenCavityIconVisibility(false)
        }
    }


    private fun setHmiKnobListener() {
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
    }
    override fun onHMILeftKnobClick() {
        //do nothing
    }

    override fun onHMILongLeftKnobPress() {
        //do nothing
    }

    override fun onHMIRightKnobClick() {
        showInstructionBinding?.textButtonRightOk?.callOnClick()
    }

    override fun onHMILongRightKnobPress() {
        //do nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //do nothing
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        //do nothing
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        //do nothing
    }
    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            if(isAdded) {
                HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
                HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
            }
        }, AppConstants.DELAY_CONFIGURATION_1000)
    }
}
