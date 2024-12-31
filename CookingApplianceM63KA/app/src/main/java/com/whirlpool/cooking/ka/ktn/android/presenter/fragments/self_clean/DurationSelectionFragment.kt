package android.presenter.fragments.self_clean

import android.media.AudioManager
import android.os.Bundle
import android.presenter.basefragments.AbstractStringTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.stringtumbler.StringTumblerItem
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.TimeMap
import com.whirlpool.hmi.cooking.utils.PyroLevel
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.AudioManagerUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import java.util.Locale


class DurationSelectionFragment : AbstractStringTumblerFragment(),
    AbstractStringTumblerFragment.CustomClickListenerInterface,
    HeaderBarWidgetInterface.CustomClickListenerInterface {
    private var cookTimeOptionList: HashMap<String, Long>? = null
    private var inScopeViewModel: CookingViewModel? = null
    private var productVariant: CookingViewModelFactory.ProductVariantEnum? = null
    override fun initTumbler() {
        initTemperatureTumbler()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMarginTopToHeaderView()
        observeLiveRecipeExecutionState()
    }
    private fun addMarginTopToHeaderView() {
        val constraintSet = ConstraintSet()
        val constraintLayoutId = tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.parentViewTumbler
        constraintSet.clone(constraintLayoutId)
        val tumblerTopMargin = provideResources().getDimension(R.dimen.string_tumbler_top_margin).toInt()
        constraintSet.setMargin(R.id.tumblerString, ConstraintSet.TOP, tumblerTopMargin)
        constraintSet.applyTo(constraintLayoutId)
    }


    override fun setTumblerItemDivider(itemDivider: Int, tumbler: BaseTumbler?) {
        super.setTumblerItemDivider(
            R.drawable.tumbler_divider,
            tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.tumblerString
        )
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return 0
    }

    /**
     * set primaryCavityViewModel according to the the selected product variant
     */
    override fun setCavityViewModelByProductVariant() {
        productVariant = CookingViewModelFactory.getProductVariantEnum()
        inScopeViewModel = CookingViewModelFactory.getInScopeViewModel()
    }

    /**
     * load pyro recipe for the cavity
     */
    override fun loadRecipe() {
        //correction handle if inScopeViewModel == null and error response
        if (inScopeViewModel != null) {
            inScopeViewModel?.recipeExecutionViewModel?.loadPyroRecipe()
        }
    }

    /**
     * load the json data for the tumbler against pyro
     */
    override fun setTumblerStringTempData() {
        //correction check inScopeViewModel == null
        if (inScopeViewModel != null) {
            val cookTimeOption =
                inScopeViewModel?.recipeExecutionViewModel?.cookTimeOption?.value
            if (cookTimeOption is TimeMap) {
                cookTimeOptionList = cookTimeOption.timeMap
                for ((key, value) in cookTimeOptionList!!) {
                    HMILogHelper.Loge(
                        "TAG--------------->", "$key $value"
                    )
                    tumblerViewHolderHelper?.provideNumericTumbler()?.itemViewHolder =
                        StringTumblerItem(cookTimeOptionList)
                    tumblerViewHolderHelper?.provideNumericTumbler()
                        ?.setListObject(cookTimeOption as ViewModelListInterface?, true)
                }
            }
        }
    }

    /**
     * set the header bar widget data
     */
    override fun setHeaderBarViews() {
        setViewByProductVariant(productVariant)
    }

    /**
     * set the header bar widget data according to the product variant
     *
     * @param productVariantEnum [CookingViewModelFactory.ProductVariantEnum]
     */
    private fun setViewByProductVariant(productVariantEnum: CookingViewModelFactory.ProductVariantEnum?) {
        if (productVariantEnum == CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN) {
            tumblerViewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(false)
            tumblerViewHolderHelper?.provideHeaderBarWidget()?.setRightIconVisibility(false)
            tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(false)
            tumblerViewHolderHelper?.provideGhostButton()?.visibility = View.GONE
            tumblerViewHolderHelper?.provideHeaderBarWidget()
                ?.setTitleText(resources.getString(R.string.text_header_soil_level))
            tumblerViewHolderHelper?.providePrimaryButton()?.text =
                resources.getString(R.string.text_button_next)
            tumblerViewHolderHelper?.provideHeaderBarWidget()?.setCustomOnClickListener(this)
        } else if (productVariant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN || productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO) {
            tumblerViewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(false)
            tumblerViewHolderHelper?.provideHeaderBarWidget()?.setRightIconVisibility(false)
            tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(true)
            if (inScopeViewModel!!.isPrimaryCavity) {
                tumblerViewHolderHelper?.provideHeaderBarWidget()
                    ?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
            } else {
                tumblerViewHolderHelper?.provideHeaderBarWidget()
                    ?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
            }
            tumblerViewHolderHelper?.provideGhostButton()?.visibility = View.GONE
            tumblerViewHolderHelper?.provideHeaderBarWidget()
                ?.setTitleText(resources.getString(R.string.text_header_soil_level))
            tumblerViewHolderHelper?.providePrimaryButton()?.text =
                resources.getString(R.string.text_button_next)
            tumblerViewHolderHelper?.provideHeaderBarWidget()?.setCustomOnClickListener(this)
        }
        setCustomClickListener(this)
    }

    override fun setCavityNameText() {
        tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.headerBar?.setOvenCavityTitleText(
            AppConstants.EMPTY_STRING
        )
    }

    /**
     * @param view on the which the click listener to be applied.
     */
    override fun viewOnClick(view: View?) {
        val id = view?.id
        var pyroLevel = ""
        if (id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.btnPrimary?.id) {
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SELF_CLEAN)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SELF_CLEAN)
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            val selectedTimeValue =
                tumblerViewHolderHelper?.provideNumericTumbler()?.listObject?.getValue(
                    tumblerViewHolderHelper?.provideNumericTumbler()!!.selectedIndex
                ) as Long
            HMILogHelper.Loge("selected value", selectedTimeValue.toString())
            for ((key, value) in cookTimeOptionList!!) {
                if (selectedTimeValue == value) {
                    pyroLevel = key
                    HMILogHelper.Loge("pyroLevel", pyroLevel)
                    break
                }
            }
            if (inScopeViewModel == null || inScopeViewModel?.recipeExecutionViewModel == null) {
                showFailedToStartRecipePopUp()
            } else {
                val bundle = Bundle()
                bundle.putString(BundleKeys.SELF_CLEAN_PYRO_LEVEL, pyroLevel)
                bundle.putLong(BundleKeys.SELF_CLEAN_COOK_TIME, selectedTimeValue)
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_durationSelectionFragment_to_selfCleanInstructionsFragment,
                    bundle,
                    null
                )
            }
        }
    }

    override fun leftIconOnClick() {
        //Handling the self clean flow cancel by back arrow.
        CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = false)
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(this) ?: requireView()
            )
        )
    }

    override fun onHMILeftKnobClick() {
        onHMIKnobRightOrLeftClick()
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            manageKnobRotation(knobDirection)
        }
    }

    /**
     * show failed to start recipe pop up in case of inScopeViewModel is null or RecipeExecutionError occurs
     */
    private fun showFailedToStartRecipePopUp() {
        PopUpBuilderUtils.runningFailPopupBuilder(this)
    }
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        //Handling Self clean flow user insert probe need to enabled and disabled HMI keys
        CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = false)
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
        //Handling Self clean flow user removed probe need to enabled and disabled HMI keys
        if(arguments!=null && arguments?.getBoolean(BundleKeys.BUNDLE_NAVIGATED_FROM_SELF_CLEAN) == true){
            CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = true)
        }
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    private fun observeLiveRecipeExecutionState() {
        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.recipeExecutionState.observe(
            getViewLifecycleOwner()
        ) { state: RecipeExecutionState ->
            if (state == RecipeExecutionState.RUNNING_FAILED) {
                HMILogHelper.Logd("Pyro Clean RecipeExecutionState: RUNNING_FAILED, Cancelling Pyro & navigating to clock\n")
                CookingAppUtils.setIsSelfCleanFlow(false)
                CookingAppUtils.cancelProgrammedCyclesAndNavigate(this,
                    navigateToSabbathClock = false,
                    navigateToClockScreen = true)
            }
        }
    }
}