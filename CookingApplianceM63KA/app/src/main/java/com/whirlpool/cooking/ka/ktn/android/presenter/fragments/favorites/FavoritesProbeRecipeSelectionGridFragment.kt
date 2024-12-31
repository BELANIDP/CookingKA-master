package android.presenter.fragments.favorites

import android.os.Bundle
import android.presenter.basefragments.BaseChildCyclesSelectionFragment
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.view.View
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils

/**
 * File        : android.presenter.fragments.favorites.FavoritesProbeRecipeSelectionGridFragment
 * Brief       : List of all probe related recipes only
 * Author      : VYASM
 * Created On  : 23/10/2024
 * Details     : User can select the recipes that are only probe related ex Bake, Convect, Auto Cook for Favorites
 */
class FavoritesProbeRecipeSelectionGridFragment : BaseChildCyclesSelectionFragment() {
    private lateinit var probeCyclesList : ArrayList<GridListItemModel>
    override fun provideBaseRecipeName(): String {
        CookBookViewModel.getInstance().setRootNodeForRecipes(
            CookingAppUtils.getNodeForCycle(AppConstants.RECIPE_MORE_MODES)
        )
        return AppConstants.RECIPE_PROBE
    }

    override fun provideHeaderBarTitle(): String {
        return getString(R.string.text_header_probe)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
        if(cookingViewModel.recipeExecutionViewModel.isRunning) {
            cookingViewModel.recipeExecutionViewModel.cancel()
            HMILogHelper.Logd("Cancel recipe if running for ${cookingViewModel.cavityName.value} on probe detected")
        }
    }

    override fun provideListRecyclerViewTilesData(): ArrayList<GridListItemModel> {
        probeCyclesList =  super.provideListRecyclerViewTilesData()
        return probeCyclesList
    }

    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        when(probeCyclesList[position].titleText){
            getString(R.string.text_option_autoCook) -> NavigationUtils.navigateSafely(
                this,
                R.id.action_recipeSelection_to_assistedMainCategory,
                null,
                null
            )
            getString(R.string.history) -> HMILogHelper.Logd(
                tag,
                "Load history probe cycles"
            )
            getString(R.string.text_see_video_favorites) -> HMILogHelper.Logd(
                tag,
                "Load favorites probe cycles"
            )
            else -> {
                val recipeName = probeCyclesList[position].event
                NavigationUtils.navigateAfterFavoriteSelection(
                    this, CookingViewModelFactory.getInScopeViewModel(), recipeName
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
        } else {
            NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    NavigationUtils.getViewSafely(
                        this
                    ) ?: requireView()
                )
            )
        }
    }
}