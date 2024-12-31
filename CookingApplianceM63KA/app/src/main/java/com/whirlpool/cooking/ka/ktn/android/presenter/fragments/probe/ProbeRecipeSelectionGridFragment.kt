package android.presenter.fragments.probe

import android.os.Bundle
import android.presenter.basefragments.BaseChildCyclesSelectionFragment
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants
import core.utils.AppConstants.RECIPE_MORE_MODES
import core.utils.CookingAppUtils
import core.utils.FavoriteDataHolder
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils

/**
 * File        : android.presenter.fragments.probe.ProbeRecipeSelectionGridFragment
 * Brief       : List of all probe related recipes only
 * Author      : Hiren
 * Created On  : 05/29/2024
 * Details     : User can select the recipes that are only probe related ex Bake, Convect, History, Favorites, Auto Cook
 */
class ProbeRecipeSelectionGridFragment : BaseChildCyclesSelectionFragment() {
    private lateinit var probeCyclesList : ArrayList<GridListItemModel>
    override fun provideBaseRecipeName(): String {
        CookBookViewModel.getInstance().setRootNodeForRecipes(
            CookingAppUtils.getNodeForCycle(RECIPE_MORE_MODES)
        )
        return AppConstants.RECIPE_PROBE
    }

    override fun provideHeaderBarTitle(): String {
        return getString(R.string.text_header_probe)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
        activity?.supportFragmentManager?.let { CookingAppUtils.dismissAllDialogs(it) }
        if(cookingViewModel.recipeExecutionViewModel.isRunning) {
            cookingViewModel.recipeExecutionViewModel.cancel()
            HMILogHelper.Logd("Cancel recipe if running for ${cookingViewModel.cavityName.value} on probe detected")
        }

    }

    override fun provideListRecyclerViewTilesData(): ArrayList<GridListItemModel> {
        probeCyclesList =  super.provideListRecyclerViewTilesData()
        probeCyclesList.add(GridListItemModel(getString(R.string.text_option_autoCook), GridListItemModel.GRID_CONVECT_RECIPE_TILE))
        probeCyclesList.add(GridListItemModel(getString(R.string.text_see_video_favorites), GridListItemModel.GRID_CONVECT_RECIPE_TILE))
        probeCyclesList.add(GridListItemModel(getString(R.string.history), GridListItemModel.GRID_CONVECT_RECIPE_TILE))
        return probeCyclesList
    }

    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        //Probe Recipe Selection Programing Mode: Button Configuration handling
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        when (probeCyclesList[position].titleText) {
            getString(R.string.text_option_autoCook) -> NavigationUtils.navigateSafely(
                this,
                R.id.action_recipeSelection_to_assistedMainCategory,
                null,
                null
            )

            getString(R.string.history) -> {
                FavoriteDataHolder.isProbeFlow = true
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_probeCyclesSelectionFragment_to_historyFragment,
                    null,
                    null
                )
            }

            getString(R.string.text_see_video_favorites) -> {
                FavoriteDataHolder.isProbeFlow = true
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_probeCyclesSelectionFragment_to_favoritesLandingFragment,
                    null,
                    null
                )
            }

            else -> {
                val recipeName = probeCyclesList[position].event
                NavigationUtils.navigateAfterProbeRecipeSelection(
                    this, CookingViewModelFactory.getInScopeViewModel(), recipeName, false, false
                )
            }
        }
    }

    override fun leftIconOnClick() {
        val cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
        if (MeatProbeUtils.isMeatProbeConnected(cookingViewModel)) {
            PopUpBuilderUtils.probeStillDetectedPopupBuilder(
                this, cookingViewModel
            ) {
                CookingAppUtils.changeScopeOfViewModelBasedOnRecipeId()
                leftIconOnClick()
            }
        } else if(CookingAppUtils.getNavigatedFrom() == AppConstants.NAVIGATION_FROM_MORE_MODES_PROBES){
            super.leftIconOnClick()
        }else
        {
            CookingAppUtils.navigateToStatusOrClockScreen(this)
            CookingAppUtils.clearRecipeData()
        }
        CookingAppUtils.setNavigatedFrom(AppConstants.EMPTY_STRING)
    }

    override fun onResume() {
        super.onResume()
        checkProbeIsConnectedAndConfigureHMIKeys()
    }

    private fun checkProbeIsConnectedAndConfigureHMIKeys() {
        val (isProbeConnected, connectedCavityViewModel) = MeatProbeUtils.isAnyCavityHasMeatProbeConnected()
        HMILogHelper.Logd("HMI_KEY", "on probe detected $isProbeConnected ")
        if (isProbeConnected) {
            //Probe Recipe : Button Configuration handling
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_HOME)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_HOME)
        } else {
            //Probe Recipe Selection Programing Mode: Button Configuration handling
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        }
    }
}