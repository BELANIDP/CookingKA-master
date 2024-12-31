package android.presenter.fragments.favorites

import android.presenter.fragments.assisted.FoodMainCategoryGridFragment
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.CookingAppUtils
import core.utils.FavoriteDataHolder
import core.utils.HMILogHelper
import core.utils.NavigationUtils

/**
 * File        : android.presenter.fragments.FavoritesFoodMainCategoryGridFragment
 * Brief       : Assisted main food category fragment for Favorite & History feature.
 * Author      : VYASM
 * Created On  : 23/10/2024
 * Details     : User can select food type from the grid list view for saving as favorite
 */
class FavoritesFoodMainCategoryGridFragment : FoodMainCategoryGridFragment() {

    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        val selectedFoodCategory = recipeList[position].gridDetails
        val nodeTopMode = getSubFoodTypesDataForSelectedMainFood(selectedFoodCategory, rootNode)
        CookBookViewModel.getInstance().setRootNodeForRecipes(nodeTopMode)
        HMILogHelper.Logd("Food category " + nodeTopMode?.data)
        if(nodeTopMode?.children?.size == 0){
            HMILogHelper.Logd(tag, "Assisted Flow: Food category has no children, so loading the recipe ${nodeTopMode.data}")
            NavigationUtils.navigateAfterFavoriteSelection(
                this,
                CookingViewModelFactory.getInScopeViewModel(),
                selectedFoodCategory,
                isFromKnob
            )
            return
        }
        NavigationUtils.navigateSafely(
            this,
            R.id.action_favoritesFoodMainCategoryGridFragment_to_favoritesFoodSubCategoryGridFragment,
            if (isComingFromProbeRecipeSelection) arguments else null,
            null
        )
    }

    override fun leftIconOnClick() {
        // Fix for bug M63KA-2797, To reinitialize recipeExecutionViewModel when user is going back
        // from editing a mode for a saved favorite.
        if (FavoriteDataHolder.favoriteRecord.favoriteName?.isNotEmpty() == true) {
            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.load(
                FavoriteDataHolder.favoriteRecord
            )
            CookingAppUtils.updateParametersInViewModel(
                FavoriteDataHolder.favoriteRecord,
                CookingAppUtils.getRecipeOptions(),
                CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
            )
        }
        super.leftIconOnClick()
    }
}
