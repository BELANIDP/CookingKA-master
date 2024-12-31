package android.presenter.fragments.favorites

import android.os.Bundle
import android.presenter.basefragments.BaseChildCyclesSelectionFragment
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.NavigationUtils

/**
 * File       : android.presenter.fragments.fragments.FavoritesMoreModesCyclesSelectionFragment.
 * Brief      : implementation fragment class for More modes cycles grid list for manual modes.
 * Author     : VYASM
 * Created On : 23/10/2024
 * Details    : Favorites more mode seleciton fragment.
 */
class FavoritesMoreModesCyclesSelectionFragment : BaseChildCyclesSelectionFragment() {
    override fun provideBaseRecipeName(): String {
        return AppConstants.RECIPE_MORE_MODES
    }

    override fun provideHeaderBarTitle(): String {
        return getString(R.string.text_header_more_modes)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        val recipeName = convectCyclesList[position].event
        val rootNode = CookBookViewModel.getInstance().getManualRecipesPresentationTreeFor(
            CookingViewModelFactory.getInScopeViewModel().cavityName.value
        )
        if (!CookingAppUtils.isChildrenAvailableForRecipe(
                recipeName,
                rootNode
            )) {
            NavigationUtils.navigateAfterFavoriteSelection(this@FavoritesMoreModesCyclesSelectionFragment,
                CookingViewModelFactory.getInScopeViewModel(),
                recipeName)
        } else {
            recipeName?.let {
                CookingAppUtils.navigateToSubChildRecipes(
                    this,
                    it, Bundle()
                )
            }
        }
    }
}
