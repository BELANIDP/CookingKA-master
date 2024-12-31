package android.presenter.fragments.favorites

import android.presenter.basefragments.BaseChildCyclesSelectionFragment
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.NavigationUtils

/**
 * File       : android.presenter.fragments.favorites.FavoritesConvectCyclesSelectionFragment.
 * Brief      : implementation fragment class for Convect cycles grid list for manual modes.
 * Author     : VYASM.
 * Created On : 23/10/2024
 * Details    : User can select convect cycle to save as favorite from this screen.
 */
class FavoritesConvectCyclesSelectionFragment : BaseChildCyclesSelectionFragment() {
    override fun provideBaseRecipeName(): String {
        return AppConstants.RECIPE_CONVECT
    }

    override fun provideHeaderBarTitle(): String {
        return getString(R.string.convect)
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
            NavigationUtils.navigateAfterFavoriteSelection(this@FavoritesConvectCyclesSelectionFragment,
                CookingViewModelFactory.getInScopeViewModel(),
                recipeName)
        } else {
            NavigationUtils.navigateSlowRoastSelection(
                this,
                CookingViewModelFactory.getInScopeViewModel(),
                true,
                null
            )
        }
    }
}
