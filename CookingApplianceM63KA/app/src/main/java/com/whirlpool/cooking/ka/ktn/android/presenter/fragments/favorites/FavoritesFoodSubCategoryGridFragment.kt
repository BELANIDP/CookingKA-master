package android.presenter.fragments.favorites

import android.presenter.fragments.assisted.FoodSubCategoryGridFragment
import android.view.View
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.HMILogHelper
import core.utils.NavigationUtils

/**
 * File        : android.presenter.fragments.favorites.FoodSubCategoryGridFragment
 * Brief       : Assisted sub food category fragment after selecting main food type
 * Author      : VYASM
 * Created On  : 23/10/2024
 * Details     : User can select food sub type from the grid list view for adding favorites
 */
class FavoritesFoodSubCategoryGridFragment : FoodSubCategoryGridFragment() {

    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        val selectedFoodCategory = recipeList[position].gridDetails
        HMILogHelper.Logd("Food SUB category $selectedFoodCategory")
        NavigationUtils.navigateAfterFavoriteSelection(
            this,
            CookingViewModelFactory.getInScopeViewModel(),
            selectedFoodCategory,
            isFromKnob
        )
    }
}

