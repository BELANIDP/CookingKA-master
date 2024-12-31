/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */

package android.presenter.basefragments

import android.os.Bundle
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.view.View
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.TreeNode
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getRecipeNameText
import core.utils.CookingAppUtils.Companion.getRecipeNameTextForConvectGridList
import core.utils.HMILogHelper
import core.utils.NavigationUtils

/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.basefragments.BaseConvectCyclesSelectionFragment
 * Brief       : base fragment for convect cycles list (sub children flow)
 * Author      : BHIMAR
 * Created On  : 20/03/2024
 * Details     : User can select convect cycles from this screen for manual mode
 */
abstract class BaseChildCyclesSelectionFragment : AbstractGridListFragment() {

    protected var convectCyclesList = arrayListOf<GridListItemModel>()
    private val cookBookViewModel = CookBookViewModel.getInstance()

    /**
     * provide the base recipe name to set the cook book view model root node
     * @return recipe name convect, probe
     */
    abstract fun provideBaseRecipeName(): String

    override fun provideListRecyclerViewTilesData(): ArrayList<GridListItemModel> {
        val rootNode: TreeNode<String>? = cookBookViewModel.currentPresentationTreeRootNode.value
        val list: ArrayList<GridListItemModel> = ArrayList()
        if (rootNode != null && rootNode.children.isNotEmpty()) {
            rootNode.children.forEach { treeNode ->
                val cycleName: String = treeNode.data
                val itemTitleText: String =
                    if (provideBaseRecipeName() == AppConstants.RECIPE_CONVECT || provideBaseRecipeName() == AppConstants.RECIPE_PROBE) {
                        getRecipeNameTextForConvectGridList(requireContext(), cycleName)
                    } else {
                        getRecipeNameText(requireContext(), cycleName)
                    }
                val item = GridListItemModel(
                    itemTitleText,
                    GridListItemModel.GRID_CONVECT_RECIPE_TILE
                ).apply {
                    event = cycleName
                }
                list.add(item)
            }
        }
        convectCyclesList = list
        return list
    }

    override fun provideListRecyclerViewSize(): Int {
        return convectCyclesList.size
    }

    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        val recipeName = convectCyclesList[position].event
        val rootNode = CookBookViewModel.getInstance().getManualRecipesPresentationTreeFor(
            CookingViewModelFactory.getInScopeViewModel().cavityName.value
        )
        if (recipeName.contentEquals(AppConstants.RECIPE_SLOW_ROAST) && CookingAppUtils.isChildrenAvailableForRecipe(
                recipeName,
                rootNode
            )
        ) {
            NavigationUtils.navigateSlowRoastSelection(
                this,
                CookingViewModelFactory.getInScopeViewModel(),
                false,
                null
            )
        } else if (recipeName.contentEquals(AppConstants.RECIPE_PROBE) && CookingAppUtils.isChildrenAvailableForRecipe(
                recipeName,
                rootNode
            )
        ) {
            recipeName?.let {
                CookingAppUtils.navigateToSubChildRecipes(
                    this,
                    it, Bundle()
                )
            }
            CookingAppUtils.setNavigatedFrom(AppConstants.NAVIGATION_FROM_MORE_MODES_PROBES)
        } else {
            NavigationUtils.navigateAfterRecipeSelection(
                this, CookingViewModelFactory.getInScopeViewModel(), recipeName, false
            )
        }
    }

    override fun observeViewModels() {
        CookBookViewModel.getInstance().setRootNodeForRecipes(
            CookBookViewModel.getInstance()
                .getManualRecipesPresentationTreeFor(CookingViewModelFactory.getInScopeViewModel().cavityName.value)
        )
        val selectedNode: TreeNode<String>? =
            CookingAppUtils.getNodeForCycle(provideBaseRecipeName())
        HMILogHelper.Logd(
            "SubChildRecipes",
            "cycleName= " + provideBaseRecipeName() + ": selectedNode=" + selectedNode?.name
        )
        CookBookViewModel.getInstance().setRootNodeForRecipes(selectedNode)
        setNumberOfColumns(2)
        manageListRecyclerView()
    }

    override fun onListItemDeleteClick(view: View?, position: Int) {
        //Empty override
    }

    override fun onListItemImageClick(view: View?, position: Int) {
        //Empty override
    }
}