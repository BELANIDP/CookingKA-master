/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.singleoven

import android.media.AudioManager
import android.os.Bundle
import android.presenter.basefragments.AbstractStringTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringItemClickInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringTumblerItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.whirlpool.cooking.ka.BuildConfig
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.utils.TreeNode
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AudioManagerUtils
import core.utils.CommonAnimationUtils.playAnimation
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.rotateTumblerOnKnobEvents
import core.utils.CookingAppUtils.Companion.setNavigatedFrom
import core.utils.DoorEventUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedViewModel
import core.utils.ToastUtils
import core.utils.setListObjectWithDefaultSelection
import kotlinx.coroutines.launch
import java.util.function.Consumer

/**
 * File       : android.presenter.fragments.singleoven.RecipeSelectionFragment.
 * Brief      : implementation fragment class for Recipe selection screen for manual modes.
 * Author     : BHIMAR.
 * Created On : 15/03/2024
 * Details    :
 */
class RecipeSelectionFragment : AbstractStringTumblerFragment(),
    AbstractStringTumblerFragment.CustomClickListenerInterface,
    HeaderBarWidgetInterface.CustomClickListenerInterface, TextStringItemClickInterface {

    private lateinit var manualModeList: ArrayList<String>
    private var visualModeList: ArrayList<String> = ArrayList()

    private var inScopeViewModel: CookingViewModel? = null
    private var productVariant: CookingViewModelFactory.ProductVariantEnum? = null

    private var defaultRecipeName = EMPTY_STRING
    private var recipeName = EMPTY_STRING

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (CookingAppUtils.getNavigatedFrom() != AppConstants.CLOCK_FAR_OR_VIDEO_VIEW_FRAGMENT)
            setNavigatedFrom(EMPTY_STRING)
        super.onViewCreated(view, savedInstanceState)
        animateViews()
        observeVisionData()
    }

    override fun initTumbler() {
        initTemperatureTumbler()
        super.updateHeader()
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
        defaultRecipeName = getDefaultRecipeName(inScopeViewModel)
    }

    /**
     * load the json data for the tumbler against pyro
     */
    override fun setTumblerStringTempData() {
        val rootNode = CookBookViewModel.getInstance()
            .getManualRecipesPresentationTreeFor(inScopeViewModel?.cavityName?.value)
        manualModeList = ArrayList()
        if (rootNode != null && rootNode.children.isNotEmpty()) {
            manualModeList.add(getString(R.string.text_see_video_assisted_cooking))
            rootNode.children.forEach(Consumer { treeNode: TreeNode<String> ->
                val cycleNames = treeNode.data
                if (cycleNames != AppConstants.EMPTY_STRING) {
                    manualModeList.add(cycleNames)
                    tumblerViewHolderHelper?.provideNumericTumbler()?.itemViewHolder =
                        TextStringTumblerItem(
                            cycleNames, this,
                            font = R.font.roboto_light
                        )

                }
            })
            setListObject(manualModeList, tumblerViewHolderHelper?.provideNumericTumbler())
        }
        setQuickStartRecipe(manualModeList)
    }

    /**
     * Observer for vision detection of recipes
     */
    private fun observeVisionData() {
        if (BuildConfig.IS_VISION_BUILD)
            inScopeViewModel?.recipeExecutionViewModel?.recognitionResult?.observe(
                viewLifecycleOwner
            ) { recipes ->
                if (recipes.isNullOrEmpty()) {
                    HMILogHelper.Logd("Recognition result list is empty.")
                    return@observe
                }
                HMILogHelper.Logd("Recipes list  + $recipes")
                visualModeList.clear()
                tumblerViewHolderHelper?.provideNumericTumbler()?.clearOnScrollListeners()
                setTumblerStringTempDataVision(recipes)
            }
    }

    /**
     * load the tumbler data for the recipes recognized and base recipes
     */
    private fun setTumblerStringTempDataVision(recipes: ArrayList<String>) {
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.background = null
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setLeftIconVisibility(false)
        tumblerViewHolderHelper?.providePrimaryButton()?.visibility = View.GONE
        tumblerViewHolderHelper?.providePrimaryImageView()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideNumericTumbler()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideSubTitle()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideNumericTumblerVision()?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.provideNumericTumblerVisionBase()?.visibility = View.VISIBLE
        recipes.forEach { eachItem ->
            visualModeList.add(eachItem)
            tumblerViewHolderHelper?.provideNumericTumblerVision()?.itemViewHolder =
                TextStringTumblerItem(
                    eachItem,
                    this,
                    font = R.font.roboto_light,
                    isVisionView = true
                )
        }
        tumblerViewHolderHelper?.provideNumericTumblerVisionBase()?.itemViewHolder =
            TextStringTumblerItem(
                manualModeList.toString(),
                this,
                font = R.font.roboto_light,
                true,
                isVisionView = true
            )
        lifecycleScope.launch {
            val visualModeJob = launch {
                setListObjectVision(visualModeList, tumblerViewHolderHelper?.provideNumericTumblerVision(), true)
            }
            val manualModeJob = launch {
                setListObjectVision(manualModeList, tumblerViewHolderHelper?.provideNumericTumblerVisionBase())
            }

            visualModeJob.join()
            manualModeJob.join()

            setQuickStartRecipeVision(manualModeList)
        }
        tumblerViewHolderHelper?.providePrimaryImageViewBig()?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.providePrimaryImageViewBig()?.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_assisted_big
            )
        )
    }

    /**
     * Set tumbler data
     */
    private fun setListObjectVision(
        list: ArrayList<String>,
        tumbler: BaseTumbler?,
        isVisionRecipe: Boolean = false
    ) {
        val recipeTumblerList: ViewModelListInterface =
            getRecipeList(list)
        requireView().post {
            tumbler?.setListObjectWithDefaultSelection(
                recipeTumblerList,
                if (isVisionRecipe) null else defaultRecipeName,
            )
        }
    }

    /**
     * Set tumbler data
     */
    private fun setListObject(
        list: ArrayList<String>,
        tumbler: BaseTumbler?
    ) {
        val recipeTumblerList: ViewModelListInterface =
            getRecipeList(list)
        tumbler?.setListObjectWithDefaultSelection(
            recipeTumblerList,
            defaultRecipeName
        )
    }

    override fun onStart() {
        super.onStart()
        CookingAppUtils.checkForActiveFaults(this)
        if (!CookingAppUtils.isDemoModeEnabled()) {
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_HOME)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_HOME)
        }
    }

    /**
     * View model interface to interact in the tumbler view based on the temperature range value
     *
     * @param tumblerDataValueList      tumbler Data list
     * @return ViewModelListInterface
     */
    private fun getRecipeList(
        tumblerDataValueList: ArrayList<String>
    ): ViewModelListInterface {
        return object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                return tumblerDataValueList
            }

            override fun getDefaultString(): String {
                return defaultRecipeName
            }

            override fun getValue(index: Int): Any {
                return tumblerDataValueList[index]
            }

            override fun isValid(value: Any): Boolean {
                return tumblerDataValueList.contains(value.toString())
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
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setRightIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setLeftIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setTitleText("")
        tumblerViewHolderHelper?.provideGhostButton()?.text =
            resources.getString(R.string.text_button_favorite)
        tumblerViewHolderHelper?.providePrimaryButton()?.text =
            resources.getString(R.string.text_button_auto_cook)
        tumblerViewHolderHelper?.provideMainImageBackgroundWidget()?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.provideMainImageBackgroundWidget()?.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.background)
        tumblerViewHolderHelper?.provideGhostImageView()?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.providePrimaryImageView()?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.provideGhostImageView()?.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_favorites
            )
        )
        tumblerViewHolderHelper?.providePrimaryImageView()?.setImageDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_assisted
            )
        )

        if (productVariantEnum == CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN) {
            tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(false)
        } else if (productVariant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
            || productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO
        ) {
            tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(true)
            if (inScopeViewModel?.isPrimaryCavity == true) {
                tumblerViewHolderHelper?.provideHeaderBarWidget()
                    ?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
            } else {
                tumblerViewHolderHelper?.provideHeaderBarWidget()
                    ?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
            }
        }
        setCustomClickListener(this)
    }

    override fun viewOnClick(view: View?) {
        val id = view?.id
        if ((id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.btnPrimary?.id) ||
            (id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.imgPrimary?.id) ||
            (id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.constraintPrimaryButton?.id)
        ) {
            HMILogHelper.Logd(
                tag,
                "navigating to action_recipeSelection_to_assistedMainCategory"
            )
            NavigationUtils.navigateSafely(
                this,
                R.id.action_recipeSelection_to_assistedMainCategory,
                null,
                null
            )
        }
        if ((id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.btnGhost?.id) ||
            (id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.imgGhost?.id) ||
            (id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.constraintGhostButton?.id)
        ) {
            super.handleFavoriteClick()
        }
    }

    override fun leftIconOnClick() {

    }

    override fun onHMIRightKnobClick() {
        PopUpBuilderUtils.dismissPopupByTag(activity?.supportFragmentManager, AppConstants.POPUP_TAG_JET_START)
        onItemClick(tumblerViewHolderHelper?.provideNumericTumbler()?.selectedIndex ?: 0, true)
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            val tumbler = tumblerViewHolderHelper?.provideNumericTumbler() ?: return
            rotateTumblerOnKnobEvents(this, tumbler, knobDirection)
        }
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        if (timeInterval == AppConstants.TIME_INTERVAL_JET_START) {
            HMILogHelper.Logd("Launching jetStartMWORecipe on onHMIRightKnobTickHoldEvent=1")
            PopUpBuilderUtils.jetStartMWOBakeRecipe(
                this
            )
        }
    }

    override fun onItemClickVision(
        index: Int,
        recyclerViewType: RecyclerViewType,
        isKnobClick: Boolean
    ) {
        super.onItemClickVision(index, recyclerViewType, isKnobClick)
        HMILogHelper.Logd("onItemClickVision called ${recyclerViewType.ordinal}")
        recipeName = if (recyclerViewType == RecyclerViewType.VISUAL) {
            visualModeList[index]
        } else {
            manualModeList[index]
        }
        handleRecipeClick(recipeName, isKnobClick, recyclerViewType)
    }

    override fun onItemClick(index: Int, isKnobClick: Boolean) {
        HMILogHelper.Logd("onItemClick called $index")
        recipeName = manualModeList[index]
        handleRecipeClick(recipeName, isKnobClick)
    }


    private fun handleRecipeClick(recipeName: String, isKnobClick: Boolean, recyclerViewType: RecyclerViewType = RecyclerViewType.MANUAL) {
        HMILogHelper.Logd("recipe name $recipeName")
        if (isKnobClick) {
            KnobNavigationUtils.knobForwardTrace = true
        }
        if (!CookingAppUtils.isDemoModeEnabled()) {
            //Enabled and Disable HMI key after recipe selected
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        }

        SharedViewModel.getSharedViewModel(this.requireActivity())
            .setCurrentRecipeBeingProgrammed(recipeName)
        playAnimation(
            tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.root!!,
            R.anim.fade_out
        )
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        when (recipeName.lowercase()) {
            getString(R.string.text_see_video_assisted_cooking).lowercase() -> {
                HMILogHelper.Logd(
                    tag,
                    "navigating to action_recipeSelection_to_assistedMainCategory"
                )
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_recipeSelection_to_assistedMainCategory,
                    null,
                    null
                )
            }

            AppConstants.QUICK_START.lowercase() -> {
                inScopeViewModel?.let {
                    DoorEventUtils.startQuickRecipe(
                        this, it
                    )
                }
            }

            AppConstants.RECIPE_PROBE,
            AppConstants.RECIPE_MORE_MODES.lowercase(),
            AppConstants.RECIPE_CONVECT -> {
                CookingAppUtils.navigateToSubChildRecipes(
                    this,
                    recipeName, Bundle()
                )
            }

            AppConstants.RECIPE_SLOW_ROAST -> {
                ToastUtils.showToast(requireContext(), "Under Development")
            }

            else -> {
                if (visualModeList.isNotEmpty() && recyclerViewType == RecyclerViewType.VISUAL) {
                    inScopeViewModel?.let {
                        NavigationUtils.navigateAfterAssistedFoodTypeSelection(
                            this,
                            it,
                            recipeName
                        )
                    }
                } else {
                    NavigationUtils.navigateAfterRecipeSelection(
                        this,
                        inScopeViewModel,
                        recipeName,
                        false,
                        isKnobClick
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        PopUpBuilderUtils.dismissPopupByTag(
            activity?.supportFragmentManager,
            AppConstants.POPUP_TAG_JET_START
        )
        setNavigatedFrom(EMPTY_STRING)
        manualModeList.clear()
        visualModeList.clear()
        super.onDestroyView()
    }
}