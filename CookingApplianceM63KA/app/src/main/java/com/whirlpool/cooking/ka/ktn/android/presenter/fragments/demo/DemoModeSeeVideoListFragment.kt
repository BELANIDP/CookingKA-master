package android.presenter.fragments.demo

import android.os.Bundle
import android.presenter.basefragments.AbstractGridListFragment
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.SharedViewModel
import java.lang.Boolean.TRUE

/**
 * File        : android.presenter.fragments.demo.DemoModeSeeVideoListFragment
 * Brief       : Demo Mode See Video List fragment
 * Author      : Karthikeyan D S
 * Created On  : 05/12/2024
 * Details     : User can select the demo mode video list from the grid list view
 */
class DemoModeSeeVideoListFragment: AbstractGridListFragment(), HMIExpansionUtils.HMICancelButtonInteractionListener {

    private var demoModeVideoList = arrayListOf<GridListItemModel>()

    /**
     * To get recycler view tiles details
     */
    override fun provideListRecyclerViewTilesData(): ArrayList<GridListItemModel> {
        return demoModeVideoList
    }

    /**
     * To provide recycler view tile size
     */
    override fun provideListRecyclerViewSize(): Int {
        return demoModeVideoList.size
    }

    /**
     * load and handle data & View models
     */
    override fun observeViewModels() {
      demoModeVideoList =  loadDemoModeListData()
      setNumberOfColumns(1)
      manageListRecyclerView()
    }

    /**
     * Header bar title
     */
    override fun provideHeaderBarTitle(): String {
       return getString(R.string.text_header_see_video)
    }

    /**
     * On list item click navigation
     */
    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        val bundle = Bundle()
        bundle.putInt(BundleKeys.BUNDLE_VIDEO_OPTION, position)
        navigateSafely(this, R.id.action_demoModeSeeVideoListFragment_to_demoModeVideoPreviewFragment, bundle, null)
    }

    override fun onListItemDeleteClick(view: View?, position: Int) {
//        NA
    }

    override fun onListItemImageClick(view: View?, position: Int) {
//        NA
    }

    /**
     * To load demo mode list data to populate in recycler view
     */
    private fun loadDemoModeListData(): ArrayList<GridListItemModel>{
        val demoModeArrayList = resources.getStringArray(R.array.demo_mode_video_list)
        val recipeList = java.util.ArrayList<GridListItemModel>()
        demoModeArrayList.forEach { videoListTypeName ->
            val tileData = GridListItemModel(
                videoListTypeName,
                GridListItemModel.GRID_TILE_WITH_IMAGE
            )
            tileData.event = videoListTypeName
            tileData.showTileImage = false
            recipeList.add(tileData)
        }
        return recipeList
    }

    /**
     * Cancel button interaction listener to navigate to clock or status
     */
    override fun onHMICancelButtonInteraction() {
        val sharedViewModel: SharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        if (!sharedViewModel.isApplianceInAOrCCategoryFault()) {
            if (CookingAppUtils.isSelfCleanFlow() && TRUE == CookingViewModelFactory.getInScopeViewModel().doorLockState.value) {
                navigateSafely(this, R.id.action_goToSelfCleanStatus, null, null)
            } else {
                CookingAppUtils.navigateToStatusOrClockScreen(this)
            }
        }
    }
}