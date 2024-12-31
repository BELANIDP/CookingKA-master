package android.presenter.fragments.settings

import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentInformationServiceAndSupportBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils

/**
 * File        : android.presenter.fragments.settings.SettingsSoftwareTermAndConditionFragment
 * Brief       : Appliance Features guide Popup to inflate as Fragment
 * Author      : Rajendra Paymode
 * Created On  : 16/OCT/2024
 * Details     : User can scroll to see the information of appliance features guide with serving tips
 */
class SettingsSoftwareTermAndConditionFragment : SuperAbstractTimeoutEnableFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface, MeatProbeUtils.MeatProbeListener {
    private var viewBinding: FragmentInformationServiceAndSupportBinding? =
        null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding =
            FragmentInformationServiceAndSupportBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        MeatProbeUtils.setMeatProbeListener(this)
    }

    /**
     * Method to set the view models
     */
    private fun setUpViewModels() {
        setTimeoutApplicable(true)
        viewBinding?.enterDiagnosticsBtn?.visibility = View.GONE
        viewBinding?.headerBar?.apply {
            setRightIcon(R.drawable.ic_close)
            setRightIconVisibility(true)
            setLeftIcon(R.drawable.ic_back_arrow)
            setLeftIconVisibility(true)
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setTitleText(R.string.software_terms_and_conditions)
        }
        viewBinding?.headerBar?.setCustomOnClickListener(this)
        viewBinding?.textViewDescription?.text =
            resources.getString(R.string.appliance_software_term_and_condition_info)
        viewBinding?.textViewTitle?.visibility = View.GONE
        val param = viewBinding?.textViewDescription?.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(0, 0, 0, 0)
        viewBinding?.textViewDescription?.layoutParams = param

        viewBinding?.qrCode?.apply {
            visibility = View.VISIBLE
            CookingAppUtils.getResIdFromResName(
                view?.context,
                AppConstants.SERVICE_SUPPORT_QR_CODE + AppConstants.TEXT_SQUARE,
                AppConstants.RESOURCE_TYPE_DRAWABLE
            ).takeIf { it > 0 }?.let {
                this.setImageBitmap(
                    view?.context?.resources?.let { it1 ->
                        CookingAppUtils.resizeBitmapUsingMatrix(
                            it1, it, AppConstants.IMAGE_SIZE_200, AppConstants.IMAGE_SIZE_200
                        )
                    }
                )
            }
        }
    }


    override fun rightIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }

    override fun leftIconOnClick() {
        NavigationUtils.navigateSafely(
            this,
            R.id.action_settingsSoftwareTermAndConditionFragment_to_infoFragment,
            null,
            null
        )
    }


    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if (CookingViewModelFactory.getInScopeViewModel() == null) {
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
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    override fun onDestroyView() {
        MeatProbeUtils.removeMeatProbeListener()
        viewBinding = null
        super.onDestroyView()
    }
}