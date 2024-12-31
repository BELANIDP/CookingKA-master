package android.presenter.fragments.settings

import android.presenter.customviews.listView.AbstractListFragment
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.ListViewHolderInterface
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedPreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * File       : com.whirlpool.cooking.settings.SettingsAssignFavoriteToKnobFragment
 * Brief      : Handles show Settings Knob list Fragment
 * Author     : Rajendra Paymode
 * Created On : 10-OCT-2024
 */
class SettingsAssignFavoriteToKnobFragment : AbstractListFragment(),
    ListViewHolderInterface.ListItemClickListener {
    /**
     * Method to add the Knob settings to a List
     */
    private var knobQuickStartCycleList: ArrayList<ListTileData>? = null
    private fun prepareAndDisplayKnobSettingsList(): ArrayList<ListTileData> {
        knobQuickStartCycleList = ArrayList()

        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN,
            -> {
                addQuickMicrowave()
            }
            CookingViewModelFactory.ProductVariantEnum.COMBO,
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
            -> {
                addQuickBake350()
            }

            else -> {
                //Do nothing
            }
        }
        //Start a Favorite cycle
        addQuickFavorites()
        return knobQuickStartCycleList as ArrayList<ListTileData>
    }

    private fun addQuickFavorites() {
        val favoritesInfo = ListTileData()
        val favoritesRadioButtonData: ListTileData.RadioButtonData = ListTileData.RadioButtonData()
        favoritesRadioButtonData.apply {
            visibility = View.VISIBLE
            isEnabled = true
            isChecked = SharedPreferenceManager.getKnobAssignFavoritesCycleStatusIntoPreference()
                .toBoolean()
        }
        favoritesInfo.apply {
            titleText = resources.getString(R.string.assign_quick_fav_cycle)
            itemIconVisibility = View.VISIBLE
            subTextVisibility = View.GONE
            listItemDividerViewVisibility = View.VISIBLE
            rightIconVisibility = View.VISIBLE
            radioButtonData = favoritesRadioButtonData
            rightIconID = R.drawable.ic_rightarrowicon
            listItemDividerViewVisibility = View.GONE
        }
        knobQuickStartCycleList?.add(favoritesInfo)
    }

    private fun addQuickMicrowave() {
        val quickMicrowaveInfo = ListTileData()
        val quickMicrowaveRadioButtonData: ListTileData.RadioButtonData =
            ListTileData.RadioButtonData()
        quickMicrowaveRadioButtonData.apply {
            visibility = View.VISIBLE
            isEnabled = true
            isChecked = (!SharedPreferenceManager.getKnobAssignFavoritesCycleStatusIntoPreference()
                .toBoolean())
        }
        quickMicrowaveInfo.apply {
            titleText = resources.getString(R.string.assign_quick_microwave_30_sec)
            itemIconVisibility = View.VISIBLE
            subTextVisibility = View.GONE
            listItemDividerViewVisibility = View.VISIBLE
            rightIconVisibility = View.GONE
            radioButtonData = quickMicrowaveRadioButtonData
        }
        knobQuickStartCycleList?.add(quickMicrowaveInfo)
    }

    private fun addQuickBake350() {
        val quickBakeInfo = ListTileData()
        val quickBakeRadioButtonData: ListTileData.RadioButtonData = ListTileData.RadioButtonData()
        quickBakeRadioButtonData.apply {
            visibility = View.VISIBLE
            isEnabled = true
            isChecked = (!SharedPreferenceManager.getKnobAssignFavoritesCycleStatusIntoPreference()
                .toBoolean())
        }
        quickBakeInfo.apply {
            titleText = resources.getString(R.string.assign_quick_bake_350)
            itemIconVisibility = View.VISIBLE
            subTextVisibility = View.GONE
            rightIconVisibility = View.GONE
            listItemDividerViewVisibility = View.VISIBLE
            radioButtonData = quickBakeRadioButtonData
        }
        knobQuickStartCycleList?.add(quickBakeInfo)
    }

    override fun setUpViews() {
        //Do nothing
    }

    override fun provideHeaderBarRightIconVisibility(): Boolean {
        return true
    }

    override fun provideHeaderBarLeftIconVisibility(): Boolean {
        return true
    }

    override fun provideHeaderBarInfoIconVisibility(): Boolean {
        return true
    }

    override fun provideHeaderBarTitleText(): String {
        return getString(R.string.text_assign_favorites)
    }

    override fun provideListRecyclerViewTilesData(): ArrayList<ListTileData> {
        return prepareAndDisplayKnobSettingsList()
    }

    override fun onListViewItemClick(view: View?, position: Int) {
        onViewClickTile(position)
    }

    private fun onViewClickTile(position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            knobQuickStartCycleList?.let {
                val selectedItem: ListTileData = it[position]
                for (i in 0 until it.size) {
                    val listItemModel: ListTileData = it[i]
                    val radioData = listItemModel.radioButtonData
                    radioData.visibility = View.VISIBLE
                    radioData.isEnabled = true
                    radioData.isChecked = selectedItem == listItemModel
                }
            }
            withContext(Dispatchers.Main) {
                notifyDataSetChanged()
                when (position) {
                    0 -> {
                        // ToDo: Update the knob behavior click for this toggle sheet
                        SharedPreferenceManager.setKnobAssignFavoritesCycleStatusIntoPreference(
                            AppConstants.FALSE_CONSTANT
                        )
                        SharedPreferenceManager.setKnobAssignFavoritesCycleNameIntoPreference(
                            AppConstants.EMPTY_STRING
                        )
                    }

                    1 -> {
                        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)
                            ?.let {
                                NavigationUtils.navigateSafely(
                                    it,
                                    R.id.action_settingsAssignFavoriteToKnobFragment_to_assignKnobFavoritesListFragment,
                                    null,
                                    null
                                )
                            }
                    }
                }
            }
        }
    }


    override fun observeViewModels() {
        // Implement view model observers if necessary
    }

    override fun headerBarOnClick(view: View?, buttonType: Int) {
        when (buttonType) {
            ICON_TYPE_LEFT -> {
                KnobNavigationUtils.setBackPress()
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    NavigationUtils.navigateSafely(
                        it,
                        R.id.action_settingsAssignFavoriteToKnobFragment_to_settingsKnobFragment,
                        null,
                        null
                    )
                }
            }

            ICON_TYPE_RIGHT -> {
                CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
            }

            ICON_TYPE_INFO -> {
                PopUpBuilderUtils.showAssignFavoritesPopupBuilder(this,providePopupBodyString())
            }

        }
    }

    private fun providePopupBodyString(): SpannableStringBuilder {
        return if (AppConstants.LEFT_KNOB_ID == resources.getInteger(R.integer.integer_range_0)) {
            addBulletPointsWithDescription(resources.getInteger(R.integer.integer_range_2))
        } else {
            addBulletPointsWithDescription(resources.getInteger(R.integer.integer_range_1))
        }
    }

    /**
     * add bullets point each \n statement
     */
    private fun addBulletPointsWithDescription(arrayPosition: Int): SpannableStringBuilder {
        // List of statements to add bullets
        var arrayList = resources.getStringArray(R.array.appliance_assign_favorites_guide_message_1)
        when (arrayPosition) {
            1 -> arrayList = resources.getStringArray(R.array.appliance_assign_favorites_guide_message_1)
            2 -> arrayList = resources.getStringArray(R.array.appliance_assign_favorites_guide_message_2)
        }

        // Create a SpannableStringBuilder to hold the text with bullets
        val spannableStringBuilder = SpannableStringBuilder()

        // Define bullet gap width (space between bullet and text)
        val bulletGapWidth = AppConstants.BULLET_POINTS_GAP

        // Loop through each statement and add a BulletSpan
        for (statement in arrayList) {
            val start = spannableStringBuilder.length
            spannableStringBuilder.append(statement).append("\n")
            spannableStringBuilder.setSpan(
                BulletSpan(bulletGapWidth,
                    ResourcesCompat.getColor(resources, R.color.color_white, null),AppConstants.BULLET_RADIUS),
                start,
                spannableStringBuilder.length,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannableStringBuilder
    }

    override fun onRightNavigationButtonClick(view: View?, buttonType: Int) {
        //DO nothing
    }

    override fun onLeftNavigationButtonClick(view: View?, buttonType: Int) {
        //DO nothing
    }

    override fun onHMILeftKnobClick() {
        super.onHMILeftKnobClick()
        KnobNavigationUtils.addTraversingData(lastItemSelectedPos,false)
    }

    override fun onHMILongLeftKnobPress() {
        //DO nothing
    }

    override fun onHMIRightKnobClick() {
        //DO nothing
    }

    override fun onHMILongRightKnobPress() {
        //DO nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //DO nothing
    }

    override fun setRightButton(): String {
        return ""
    }

    override fun setLeftButton(): String {
        return ""
    }

    override fun setGradientView(): Boolean {
        return false
    }

    override fun onToggleSwitchClick(position: Int, isChecked: Boolean) {
        //on Radio button click listener
        onViewClickTile(position)
    }
}
