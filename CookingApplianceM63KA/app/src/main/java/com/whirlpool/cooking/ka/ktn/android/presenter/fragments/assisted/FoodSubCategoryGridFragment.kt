package android.presenter.fragments.assisted

import android.presenter.basefragments.AbstractGridListFragment
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.view.View
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
 * File        : android.presenter.fragments.assisted.FoodSubCategoryGridFragment
 * Brief       : Assisted sub food category fragment after selecting main food type
 * Author      : Hiren
 * Created On  : 05/07/2024
 * Details     : User can select food sub type from the grid list view
 */
open class FoodSubCategoryGridFragment : AbstractGridListFragment() {
    private var rootNode: TreeNode<String>? = null
    var recipeList = arrayListOf<GridListItemModel>()
    private val cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
    private var isComingFromProbeRecipeSelection = false

    override fun observeViewModels() {
        isComingFromProbeRecipeSelection = arguments?.containsKey("isForProbeRecipeOnly") == true
        loadAssistedSubCategoryList()
    }


    override fun provideListRecyclerViewTilesData(): ArrayList<GridListItemModel> {
        return recipeList
    }

    override fun provideListRecyclerViewSize(): Int {
        return recipeList.size
    }

    override fun provideHeaderBarTitle(): String {
        return CookingAppUtils.getHeaderTitleAsRecipeName(
            requireContext(),
            rootNode?.data
        )
    }

    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        val selectedFoodCategory = recipeList[position].gridDetails
        HMILogHelper.Logd("Assisted Flow: Food SUB category $selectedFoodCategory")
        NavigationUtils.navigateAfterAssistedFoodTypeSelection(
            this,
            CookingViewModelFactory.getInScopeViewModel(),
            selectedFoodCategory
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
    private fun loadAssistedSubCategoryList() {
        rootNode = CookBookViewModel.getInstance().currentPresentationTreeRootNode.value
        CookBookViewModel.getInstance().currentPresentationTreeRootNode.value = rootNode
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
        val recipeList = arrayListOf<GridListItemModel>()
        if (treeNode?.data?.isNotEmpty() == true && treeNode.children.isNotEmpty()) {
            for (i in treeNode.children.indices) {
                val rootNodeRecipeName = treeNode.children[i].data
                if (cookingViewModel.isOfTypeOven && (MeatProbeUtils.isMeatProbeConnected(
                        cookingViewModel
                    ) || isComingFromProbeRecipeSelection) && !CookingAppUtils.isProbeRequiredForRecipe(
                        rootNodeRecipeName,
                        cookingViewModel.cavityName.value ?: ""
                    )
                ) continue
                val tileData = GridListItemModel(
                    getRecipeNameText(requireContext(), rootNodeRecipeName),
                    GridListItemModel.ASSISTED_RECIPE_TILE
                )
                HMILogHelper.Logd("recipe-sub-category-name-- $rootNodeRecipeName")
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
}