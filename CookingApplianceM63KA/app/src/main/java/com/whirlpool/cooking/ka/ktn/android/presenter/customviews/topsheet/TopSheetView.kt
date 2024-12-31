package android.presenter.customviews.topsheet

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DrawerWidgetBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.widgets.grid.ItemSpacingDecoration
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.CavityLightUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.SharedPreferenceManager
import core.utils.SharedPreferenceManager.getNoOfUsesOfSwipeDown
import core.utils.customText
import core.utils.gone
import core.utils.visible
import kotlin.properties.Delegates

private const val TOP_SHEET_TRANSLATION_Y = -57f
private const val CAVITY_LIGHT_TOP_SHEET_TRANSLATION_Y = -54f

class TopSheetView : CoordinatorLayout, GridRecyclerViewInterface.GridItemClickListener {
    var topSheetBehavior: TopSheetBehavior<View>? = null
    var drawerWidgetBinding: DrawerWidgetBinding? = null
    var handlers = Handler(Looper.getMainLooper())
    var handlerForNotification = Handler(Looper.getMainLooper())
    var isTopSheetAvailable: Boolean = true
    var topSheetView: TopSheetView? = null
    private lateinit var drawerData: List<GridListItemModel>

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun setTopSheetDimBehavior(topSheetBehavior: TopSheetBehavior<View>) {
        this.topSheetBehavior = topSheetBehavior
        var delegateState by Delegates.notNull<Int>()
        topSheetBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                delegateState = newState
                if (newState == TopSheetBehavior.STATE_EXPANDED) {
                    handlers.postDelayed(
                        { collapseDrawerBar() },
                        AppConstants.CLOCK_SCREEN_ACTION_SHEET_TIME_OUT.toLong()
                    )
                    registerUsageForSwipeDown()
                }

                if (newState == TopSheetBehavior.STATE_HIDDEN || newState == TopSheetBehavior.STATE_COLLAPSED) {
                    drawerWidgetBinding?.topSheetOutside?.visibility = GONE

                    if((getNoOfUsesOfSwipeDown()?.toInt() == 2) || (getNoOfUsesOfSwipeDown()?.toInt() == -1)){
                        drawerWidgetBinding?.clockHelperText?.visibility = View.GONE
                    }
                    else if(getNoOfUsesOfSwipeDown()?.toInt() != -1){
                        drawerWidgetBinding?.clockHelperText?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (SettingsViewModel.getSettingsViewModel().controlLock.value == true){
                    topSheetBehavior.setState(TopSheetBehavior.STATE_COLLAPSED)
                    try {
                        NavigationViewModel.navigateSafely(
                            bottomSheet,
                            R.id.controlUnlockFragment
                        )
                    } catch (e: Exception) {
                        HMILogHelper.Logd("Not able to find Navcontroller:" + e.message)
                    }
                } else if (delegateState == TopSheetBehavior.STATE_DRAGGING || delegateState == TopSheetBehavior.STATE_SETTLING) {
                    drawerWidgetBinding?.topSheetOutside?.visibility = VISIBLE
                    drawerWidgetBinding?.topSheetOutside?.alpha = slideOffset / 1.5f
                    drawerWidgetBinding?.clockHelperText?.visibility = View.GONE
                }
            }
        })
    }

    private fun init(context: Context) {

        drawerWidgetBinding = DrawerWidgetBinding.inflate(LayoutInflater.from(context), this, true)
        drawerWidgetBinding?.topSheet.let {
            topSheetBehavior = TopSheetBehavior.from(drawerWidgetBinding?.topSheet as View)
        }
        topSheetBehavior?.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == TopSheetBehavior.STATE_COLLAPSED) {
                    drawerWidgetBinding?.topSheetOutside?.visibility = GONE
                } else {
                    drawerWidgetBinding?.topSheetOutside?.visibility = VISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        drawerWidgetBinding?.handle?.setOnClickListener {
            if (SettingsViewModel.getSettingsViewModel().controlLock.value == true) {
                NavigationViewModel.navigateSafely(
                    it,
                    R.id.controlUnlockFragment
                )
            }
        }
        drawerWidgetBinding?.topSheetOutside?.setOnClickListener {
            if (topSheetBehavior?.getState() == TopSheetBehavior.STATE_EXPANDED) {
                collapseDrawerBar()
                findViewById<View>(R.id.top_sheet).translationY = 0f
                resetToOriginalView()
            }
            topSheetView?.let {
                this.isVisible = isTopSheetAvailable
            }
        }

        topSheetBehavior?.let { setTopSheetDimBehavior(it) }
        topSheetBehavior?.setState(TopSheetBehavior.STATE_COLLAPSED)
        manageDrawer()
    }

    private fun resetToOriginalView() {
        drawerWidgetBinding?.recyclerViewGridList?.visibility = VISIBLE
        drawerWidgetBinding?.tvLightState?.isVisible = false
        drawerWidgetBinding?.handle?.visible()
    }

    private fun registerUsageForSwipeDown(){
        var noOfUsesOfSwipeDown = getNoOfUsesOfSwipeDown()?.toInt()!!
        if(noOfUsesOfSwipeDown != -1 && noOfUsesOfSwipeDown < 2) {
            noOfUsesOfSwipeDown = noOfUsesOfSwipeDown.plus(1)
            SharedPreferenceManager.setNoOfUsesOfSwipeDown((noOfUsesOfSwipeDown).toString())
            HMILogHelper.Logd("Number of usage of swipe down for settings: $noOfUsesOfSwipeDown")
        }
    }

    private fun manageDrawer() {
        drawerData = provideListRecyclerViewTilesData()
        drawerWidgetBinding?.recyclerViewGridList?.isVerticalScrollBarEnabled = false
        val gridRecyclerViewInterface = GridRecyclerViewInterface(drawerData, this)
        val listItems = ArrayList<Any>(drawerData)
        drawerWidgetBinding?.recyclerViewGridList?.addItemDecoration(ItemSpacingDecoration(24))
        drawerWidgetBinding?.recyclerViewGridList?.setLayoutManager(
            LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        drawerWidgetBinding?.recyclerViewGridList?.setupGridWithObjects(
            listItems,
            gridRecyclerViewInterface
        )
        drawerWidgetBinding?.recyclerViewGridList?.visibility = VISIBLE
    }


    private fun provideListRecyclerViewTilesData(): List<GridListItemModel> {
        val gridListTileData: MutableList<GridListItemModel> = ArrayList()
        val item1 = GridListItemModel(
            context.getString(R.string.oven_light_top_drawer),
            GridListItemModel.GRID_DRAWER_TILE
        )
        item1.tileImageSrc = R.drawable.selector_oven_lights
        item1.isSelected = false
        val item2 = GridListItemModel(
            context.getString(R.string.timer),
            GridListItemModel.GRID_DRAWER_TILE
        )
        item2.apply {
            tileImageSrc = R.drawable.selector_timer
        }
        item2.isSelected = false
        val item3 = GridListItemModel(
            context.getString(R.string.text_top_drawer_settings),
            GridListItemModel.GRID_DRAWER_TILE
        )
        item3.tileImageSrc = R.drawable.selector_settings
        item3.isSelected = false
        val item4 = GridListItemModel(
            context.getString(R.string.close),
            GridListItemModel.GRID_DRAWER_TILE
        )
        item4.tileImageSrc = R.drawable.selector_close
        item4.isSelected = false
        gridListTileData.add(item1)
        gridListTileData.add(item2)
        gridListTileData.add(item3)
        gridListTileData.add(item4)
        return gridListTileData
    }

    private fun collapseDrawerBar() {
        if (topSheetBehavior?.getState() == TopSheetBehavior.STATE_EXPANDED) {
            topSheetBehavior?.setState(TopSheetBehavior.STATE_COLLAPSED)
            handlers.removeCallbacksAndMessages(null)
        }
    }

    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        Handler(Looper.getMainLooper()).postDelayed({
            when (position) {
                0 -> {
                    // Handle OVEN LIGHT click
                    handlers.removeCallbacksAndMessages(null)
                    drawerWidgetBinding?.topSheet?.translationY = -70f
                    AudioManagerUtils.playOneShotSound(
                        view?.context,
                        if(CavityLightUtils.getPrimaryCavityLightState()) R.raw.toggle_off else R.raw.toggle_on,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    performOvenLightOperation()
                }

                1 -> {
                    //Enable/Disable HMI keys during Kitchen Timer
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_KITCHEN_TIMER)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_KITCHEN_TIMER)
                    if (KitchenTimerUtils.isAnyKitchenTimerRunningOrPaused())
                        NavigationViewModel.navigateSafely(
                            view,
                            R.id.kitchenTimerFragment
                        )
                    else
                        NavigationUtils.getVisibleFragment()?.requireView().let {
                            NavigationViewModel.navigateSafely(
                                it, R.id.setKTFragment
                            )
                        }
                    AudioManagerUtils.playOneShotSound(
                        view?.context,
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                }

                2 -> {
                    try {// Handle SETTINGS click
                        NavigationViewModel.navigateSafely(
                            view,
                            R.id.settingsLandingFragment
                        )
                    } catch (e: Exception) {
                       HMILogHelper.Logd("Handled double click navigation")
                    }
                }

                3 ->                 // Handle CLOSE click
                    collapseDrawerBar()
            }
        }, 500)
    }

    override fun onListItemDeleteClick(view: View?, position: Int) {

    }

    fun performOvenLightOperation() {
        try {
            handleCavityLight()
            handlers.postDelayed({
                findViewById<View>(R.id.top_sheet).translationY = 0f
                collapseDrawerBar()
                resetToOriginalView()
            }, AppConstants.CLOCK_SCREEN_ACTION_SHEET_TIME_OUT_OVEN_LIGHT.toLong())
        } catch (e: IllegalArgumentException) {
            HMILogHelper.Loge("Unexpected productVariant: " + e.message)
        }
    }
    fun performMarkFavoriteStatus(messageToShow: String) {
        try {
            handleMarkFavoriteText(messageToShow = messageToShow)
            handlers.postDelayed({
                findViewById<View>(R.id.top_sheet).translationY = 0f
                collapseDrawerBar()
                resetToOriginalView()
            }, AppConstants.CLOCK_SCREEN_ACTION_SHEET_TIME_OUT.toLong())
        } catch (e: IllegalArgumentException) {
            HMILogHelper.Loge("Unexpected productVariant: " + e.message)
        }
    }

    override fun onListItemImageClick(view: View?, position: Int) {}

    /**
     * On tap of light icon in clock, Cavity light should be ON if it is OFF & vice versa
     * */
    private fun handleCavityLight() {
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN, CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                findViewById<View>(R.id.top_sheet).translationY = CAVITY_LIGHT_TOP_SHEET_TRANSLATION_Y
                HMILogHelper.Logi("PrimaryCavityLight getLightState() :" + CavityLightUtils.getPrimaryCavityLightState())
                drawerWidgetBinding?.recyclerViewGridList?.visibility = GONE
                if (CavityLightUtils.getPrimaryCavityLightState()) {
                    CavityLightUtils.setPrimaryCavityLightState(false)
                    drawerWidgetBinding?.tvLightState?.customText =
                        context.getString(R.string.text_oven_light_off)
                } else {
                    CavityLightUtils.setPrimaryCavityLightState(true)
                    drawerWidgetBinding?.tvLightState?.customText =
                        context.getString(R.string.text_oven_light_on)
                }
            }

            CookingViewModelFactory.ProductVariantEnum.COMBO, CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                findViewById<View>(R.id.top_sheet).translationY = CAVITY_LIGHT_TOP_SHEET_TRANSLATION_Y
                HMILogHelper.Logi(
                    "PrimaryCavityLight getLightState() :" + CavityLightUtils.getPrimaryCavityLightState() +
                            " SecondaryCavityLight getLightState() :" + CavityLightUtils.getSecondaryCavityLightState()
                )
                drawerWidgetBinding?.recyclerViewGridList?.visibility = GONE
                if (CavityLightUtils.getPrimaryCavityLightState() && CavityLightUtils.getSecondaryCavityLightState()) {
                    CavityLightUtils.setPrimaryCavityLightState(false)
                    CavityLightUtils.setSecondaryCavityLightState(false)
                    drawerWidgetBinding?.tvLightState?.customText =
                        context.getString(R.string.text_oven_light_off)
                } else {
                    CavityLightUtils.setPrimaryCavityLightState(true)
                    CavityLightUtils.setSecondaryCavityLightState(true)
                    drawerWidgetBinding?.tvLightState?.customText =
                        context.getString(R.string.text_oven_light_on)
                }
            }

            else -> throw IllegalArgumentException("Unexpected productVariant: ${CookingViewModelFactory.getProductVariantEnum()}")
        }
    }

    private fun handleMarkFavoriteText(messageToShow : String) {
        findViewById<View>(R.id.top_sheet).translationY = -70f
        drawerWidgetBinding?.recyclerViewGridList?.gone()
        drawerWidgetBinding?.handle?.visibility = View.INVISIBLE
        drawerWidgetBinding?.tvLightState?.visible()
        drawerWidgetBinding?.tvLightState?.customText = messageToShow
    }

    fun isShowingNotification() = drawerWidgetBinding?.tvLightState?.isVisible

    @SuppressLint("NotifyDataSetChanged")
    fun manageKnobRotation(counter: Int) {
        handlers.removeCallbacksAndMessages(null)
        if (drawerWidgetBinding?.tvLightState?.isVisible == true)
            resetHandlers(AppConstants.CLOCK_SCREEN_ACTION_SHEET_TIME_OUT_OVEN_LIGHT)
        else
            resetHandlers(AppConstants.CLOCK_SCREEN_ACTION_SHEET_TIME_OUT)

        drawerData.forEachIndexed { index, gridListItemModel ->
            gridListItemModel.isSelected = index == counter
        }
        drawerWidgetBinding?.recyclerViewGridList?.adapter?.notifyDataSetChanged()
    }

    @SuppressLint("SuspiciousIndentation")
    fun manageLeftKnobClick(counter: Int) {
        if (counter > -1) {
            if (counter == 1 || counter == 2) KnobNavigationUtils.knobForwardTrace = true
            onListItemClick(drawerWidgetBinding?.recyclerViewGridList, counter)
        }
    }

    /**
     *  Reset the handlers in case of interaction of view
     * */
    private fun resetHandlers(resetTimeOut: Int) {
        handlers.postDelayed(
            { collapseDrawerBar() },
            resetTimeOut.toLong()
        )
    }

    fun showNotification(notificationText: String, timeout: Long, topSheetView: TopSheetView?,
                         isTopSheetAvailable: Boolean = false) {
        // Hide the recycler_view_grid_list and handle views
        this.isTopSheetAvailable = isTopSheetAvailable
        this.topSheetView = topSheetView
        drawerWidgetBinding?.recyclerViewGridList?.visibility = View.GONE
        drawerWidgetBinding?.handle?.visibility = View.GONE
        findViewById<View>(R.id.top_sheet).translationY = TOP_SHEET_TRANSLATION_Y
        // Set the notification text
        drawerWidgetBinding?.tvLightState?.text = notificationText
        drawerWidgetBinding?.topSheetOutside?.visibility = VISIBLE
        drawerWidgetBinding?.topSheetOutside?.alpha = 0.6178862f
        drawerWidgetBinding?.tvLightState?.visibility = View.VISIBLE
        topSheetBehavior?.setState(TopSheetBehavior.STATE_EXPANDED)

        // Collapse the drawer or notification after the timeout duration
        handlerForNotification.removeCallbacksAndMessages(null)
        handlerForNotification.postDelayed({

            // Collapse the drawer
            topSheetBehavior?.setState(TopSheetBehavior.STATE_COLLAPSED)
            // Reset the drawer to its normal state
            drawerWidgetBinding?.recyclerViewGridList?.visibility = View.GONE
            drawerWidgetBinding?.handle?.visibility = View.GONE
            drawerWidgetBinding?.tvLightState?.visibility = View.GONE
            topSheetView?.isVisible = false
        }, timeout)
    }
}
