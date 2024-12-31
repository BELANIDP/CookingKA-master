package android.presenter.fragments.assisted

import android.presenter.basefragments.AbstractGridListFragment
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.TreeNode
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getRecipeNameText
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import java.util.Locale

/**
 * File        : android.presenter.fragments.assisted.FoodMainCategoryGridFragment
 * Brief       : Assisted main food category fragment
 * Author      : Hiren
 * Created On  : 05/07/2024
 * Details     : User can select food type from the grid list view
 */
open class FoodMainCategoryGridFragment : AbstractGridListFragment() {
    private val cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
    var rootNode: TreeNode<String>? = null
    var recipeList = arrayListOf<GridListItemModel>()
    var isComingFromProbeRecipeSelection = false

    override fun observeViewModels() {
        isComingFromProbeRecipeSelection = arguments?.containsKey("isForProbeRecipeOnly") == true
        loadAssistedCategoryList()
    }


    override fun provideListRecyclerViewTilesData(): ArrayList<GridListItemModel> {
        return recipeList
    }

    override fun provideListRecyclerViewSize(): Int {
        return recipeList.size
    }

    override fun provideHeaderBarTitle(): String {
        return getString(R.string.text_header_foodType)
    }

    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        val selectedFoodCategory = recipeList[position].gridDetails
        val nodeTopMode = getSubFoodTypesDataForSelectedMainFood(selectedFoodCategory, rootNode)
        CookBookViewModel.getInstance().setRootNodeForRecipes(nodeTopMode)
        HMILogHelper.Logd("Assisted Flow: Food category " + nodeTopMode?.data)
        if(nodeTopMode?.children?.size == 0){
            HMILogHelper.Logd(tag, "Assisted Flow: Food category has no children, so loading the recipe ${nodeTopMode.data}")
            NavigationUtils.navigateAfterAssistedFoodTypeSelection(
                this,
                CookingViewModelFactory.getInScopeViewModel(),
                selectedFoodCategory
            )
            return
        }
        NavigationUtils.navigateSafely(
            this,
            R.id.action_assisted_foodMainCategory_to_foodSubCategory,
            if (isComingFromProbeRecipeSelection) arguments else null,
            null
        )
    }


    override fun onListItemDeleteClick(view: View?, position: Int) {
        //Empty override
    }

    override fun onListItemImageClick(view: View?, position: Int) {
        //Empty override
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    /**
     * function to set list food category
     */
    private fun loadAssistedCategoryList() {
        val cavityName = cookingViewModel.cavityName.value
        CookBookViewModel.getInstance().setRootNodeForRecipes(
            CookBookViewModel.getInstance().getDefaultAssistedRecipesPresentationTreeFor(
                cavityName
            )
        )
        val currentRootNode = CookBookViewModel.getInstance().currentPresentationTreeRootNode.value
        if (currentRootNode != null) {
            rootNode = CookBookViewModel.getInstance()
                .getDefaultAssistedRecipesPresentationTreeFor(cavityName)
        }
        recipeList = getAssistedCookingDetails(rootNode)
        setNumberOfColumns(1)
        manageListRecyclerView()
    }

    /**
     * get Assisted cooking details
     * @param treeNode treeNode details
     * @return list of GridListItemModel
     */
    private fun getAssistedCookingDetails(
        treeNode: TreeNode<String>?,
    ): ArrayList<GridListItemModel> {
        val recipeList = java.util.ArrayList<GridListItemModel>()
        if (treeNode?.data?.isNotEmpty() == true && treeNode.children.isNotEmpty()) {
            for (i in treeNode.children.indices) {
                val rootNodeRecipeName = treeNode.children[i].data
                //skip one iteration if found out that meat probe is connected and category doesn't have any probe related recipes
                if (cookingViewModel.isOfTypeOven && (MeatProbeUtils.isMeatProbeConnected(
                        cookingViewModel
                    ) || isComingFromProbeRecipeSelection) && !isMainCategoryContainsProbeRecipe(
                        treeNode.children[i]
                    )
                )
                    continue
                val tileData = GridListItemModel(
                    getRecipeNameText(requireContext(), rootNodeRecipeName),
                    GridListItemModel.ASSISTED_RECIPE_TILE
                )
                HMILogHelper.Logd("recipe-category-name-- $rootNodeRecipeName")
                tileData.tileImageSrc = CookingAppUtils.getResIdFromResName(
                    context,
                    rootNodeRecipeName.lowercase(Locale.getDefault()) + AppConstants.TEXT_SMALL,
                    AppConstants.RESOURCE_TYPE_DRAWABLE
                )
                tileData.isActive = true
                tileData.gridDetails = rootNodeRecipeName
                recipeList.add(tileData)
            }
        }
        return recipeList
    }

    /**
     * find out if main category contains any probe recipe or not
     * @param categoryNode main category node containing child recipes
     * @return true if category has probe recipe, false otherwise
     */
    private fun isMainCategoryContainsProbeRecipe(categoryNode: TreeNode<String>): Boolean {
        var probeAvailable = false
        var recipeName: String
        if (categoryNode.data.isNotEmpty() && categoryNode.children.isNotEmpty()) {
            for (i in categoryNode.children.indices) {
                recipeName = categoryNode.children[i].data
                if (CookingAppUtils.isProbeRequiredForRecipe(
                        recipeName,
                        cookingViewModel.cavityName.value ?: ""
                    )
                ) {
                    probeAvailable = true
                    break
                }
            }
        }
        return probeAvailable
    }
    companion object{
        /**
         * function to return food category based on selected food
         *
         * @param foodCategoryName selected Food Category
         * @param rootNode         data node from assisted cycle
         * @return selected food category cycles
         */
        fun getSubFoodTypesDataForSelectedMainFood(
            foodCategoryName: String?,
            rootNode: TreeNode<String>?,
        ): TreeNode<String>? {
            var cycleTreeData: TreeNode<String>? = null
            if (rootNode != null && rootNode.children.isNotEmpty()) {
                for (treeNode in rootNode.children) {
                    if (treeNode != null && treeNode.data.isNotEmpty() && !foodCategoryName.isNullOrEmpty() && foodCategoryName.contentEquals(
                            treeNode.data
                        )
                    ) {
                        cycleTreeData = treeNode
                        break
                    }
                }
            } else if (rootNode != null) {
                return rootNode
            }
            return cycleTreeData
        }
    }
}