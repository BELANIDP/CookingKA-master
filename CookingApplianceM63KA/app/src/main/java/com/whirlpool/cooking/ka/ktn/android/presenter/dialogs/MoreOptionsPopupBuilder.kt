package android.presenter.dialogs

import android.annotation.SuppressLint
import android.graphics.Rect
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
import android.presenter.fragments.assisted.FoodMainCategoryGridFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButtonOnClickListener
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.timers.Timer
import core.jbase.AbstractMoreOptionsPopUpOverlayFragment
import core.jbase.abstractViewHolders.AbstractMoreOptionsPopupViewHolder
import core.utils.AppConstants
import core.utils.AppConstants.FAVORITE_DEFAULT_IMAGE
import core.utils.AppConstants.KEY_FAVORITE_FROM
import core.utils.AppConstants.KEY_FAVORITE_NAME
import core.utils.AppConstants.MAX_FAVORITE_COUNT
import core.utils.AppConstants.NAVIGATION_FROM_CREATE_FAV
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getRecipeNameText
import core.utils.CookingAppUtils.Companion.isRecipeAssisted
import core.utils.CookingAppUtils.Companion.loadCookingGuide
import core.utils.FavoriteDataHolder
import core.utils.FavoritesPopUpUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.NavigationUtils.Companion.navigateToShowInstructionFragment
import core.utils.PopUpBuilderUtils
import core.utils.ToastUtils
import core.viewHolderHelpers.MoreOptionsPopupViewHolderHelper
import kotlinx.coroutines.launch

class MoreOptionsPopupBuilder(cavityType: String) :
    AbstractMoreOptionsPopUpOverlayFragment(cavityType) {
    private var moreOptionsPopupViewHolder: MoreOptionsPopupViewHolderHelper? = null
    private var titleText = UNKNOWN_VALUE
    private var isOvenIconVisible = View.VISIBLE
    private var leftIcon = UNKNOWN_VALUE
    private var rightIcon = UNKNOWN_VALUE
    private var recyclerDefaultListItemData: ArrayList<GridListItemModel>? = null
    private var recyclerCycleOptionsItemData: ArrayList<GridListItemModel>? = null
    private var viewModel: CookingViewModel? = null
    private var fragment: Fragment? = null
    private var knobCounter = -1

    var recyclerItemDataListener: NavigationButtonOnClickListener? = null
    var leftIconClickListener: NavigationButtonOnClickListener? = null
    var rightIconClickListener: NavigationButtonOnClickListener? = null
    var transparentBackgroundLayoutListener: NavigationButtonOnClickListener? = null

    private var onDialogCreatedListener: OnDialogCreatedListener? = null

    override fun provideViewHolderHelper(): AbstractMoreOptionsPopupViewHolder? {
        return moreOptionsPopupViewHolder
    }

    override fun provideCookingViewModel(): CookingViewModel? {
        return viewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        moreOptionsPopupViewHolder = MoreOptionsPopupViewHolderHelper()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored into the view.
     *
     * @param view               The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageListRecyclerView()
        updateView()
        onDialogCreatedListener?.onDialogCreated()
        moreOptionsPopupViewHolder?.parentView?.setOnClickListener {
            //outside click event handling
            dismiss()
        }
        moreOptionsPopupViewHolder?.insideView?.setOnClickListener {
            //for handling inside click. Leave it blank
        }
        isPopupVisible = true
        if(KnobNavigationUtils.knobForwardTrace){
            KnobNavigationUtils.knobForwardTrace = false
            knobCounter = 0
            if(provideCycleListRecyclerViewSize() > 0)
                provideCycleListRecyclerViewTilesData()?.get(knobCounter)?.isSelected = true
            else
                provideDefaultOptionsListRecyclerViewTilesData()?.get(knobCounter)?.isSelected = true
            notifyDataSetChanged(moreOptionsPopupViewHolder?.gridCycleOptionsView)
        }
    }

    override fun onClick(view: View) {
        AudioManagerUtils.playOneShotSound(
            ContextProvider.getContext(),
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        when (view.id) {
            R.id.close_icon_left -> {
                postDialogDismissEvent(leftIconClickListener)
            }

            else -> {}
        }
    }

    private fun postDialogDismissEvent(navigationButtonOnClickListener: NavigationButtonOnClickListener?) {
        val handler = Handler(Looper.getMainLooper())
        navigationButtonOnClickListener?.executeOnClick()
        handler.postDelayed(
            requireDialog()::dismiss,
            AppConstants.POPUP_DISMISS_DELAY.toLong()
        )
    }

    interface OnDialogCreatedListener {
        fun onDialogCreated()
        fun onDialogDestroy()
    }

    fun setOnDialogCreatedListener(listener: OnDialogCreatedListener?) {
        onDialogCreatedListener = listener
    }

    private fun updateView() {
        if (titleText != UNKNOWN_VALUE) {
            moreOptionsPopupViewHolder?.moreOptionsTitleTextView?.setText(titleText)
        }
        if (leftIcon != UNKNOWN_VALUE) {
            moreOptionsPopupViewHolder?.closeLeftIcon?.setImageDrawable(
                ContextCompat.getDrawable(
                    moreOptionsPopupViewHolder?.closeLeftIcon!!.context, leftIcon
                )
            )
            moreOptionsPopupViewHolder?.closeLeftIcon?.setOnClickListener(this)
        }
        if (rightIcon != UNKNOWN_VALUE) {
            moreOptionsPopupViewHolder?.ovenCavityRightIcon?.setImageDrawable(
                ContextCompat.getDrawable(
                    moreOptionsPopupViewHolder?.ovenCavityRightIcon!!.context, rightIcon
                )
            )
            moreOptionsPopupViewHolder?.ovenCavityRightIcon?.setOnClickListener(this)
        }
        if (isOvenIconVisible != View.VISIBLE) {
            moreOptionsPopupViewHolder?.ovenCavityRightIcon?.visibility = View.GONE
        }
    }

    /**
     * Method to setup Recycler View
     */
    private fun manageListRecyclerView() {
        val gridListItem = provideCycleListRecyclerViewTilesData()
        val isGridListEmpty = gridListItem.isNullOrEmpty()

        moreOptionsPopupViewHolder?.gridCycleOptionsView?.visibility =
            if (isGridListEmpty) {
                View.GONE
            } else View.VISIBLE
        moreOptionsPopupViewHolder?.defaultOptionsSeparatorView?.visibility =
            if (isGridListEmpty) {
                View.GONE
            } else View.VISIBLE

        if (isGridListEmpty) {
            val defaultOptionsMarginTop = 32
            val defaultOptionsMarginBottom = 24
            moreOptionsPopupViewHolder?.gridDefaultOptionsView?.
            addItemDecoration(BottomMarginItemDecoration(defaultOptionsMarginBottom,defaultOptionsMarginTop))
        }

        if (!isGridListEmpty) {
            getCycleOptionsTileData(gridListItem!!)
        }
        getDefaultCycleOptionsTileData()
    }

    /**
     * To handle recyclerview objects
     */
    private fun setupGridView(
        gridView: RecyclerView,
        data: ArrayList<Any>
    ) {
        val gridLayoutManager = gridView.layoutManager as GridLayoutManager?
        gridLayoutManager?.spanSizeLookup =  MySpanSizeLookup(data as? ArrayList<GridListItemModel> ?: emptyList())
        gridView.visibility = if (data.isEmpty()) View.INVISIBLE else View.VISIBLE
    }

    /**
     * To show cycle More options eg: Set cook time, Change Temperature etc...
     */
    private fun getCycleOptionsTileData(gridListItem: ArrayList<GridListItemModel>) {
        val listItems = ArrayList<Any>(gridListItem)
        val gridRecyclerViewInterface = GridRecyclerViewInterface(gridListItem, itemMoreOptionsClickListener = this)
        moreOptionsPopupViewHolder?.gridCycleOptionsView?.let {
            it.setupGridWithObjects(listItems, gridRecyclerViewInterface)
            setupGridView(it, listItems)
        }
    }

    /**
     * To show More Default options like Save as favorite, View instructions
     */
    private fun getDefaultCycleOptionsTileData() {
        provideDefaultOptionsListRecyclerViewTilesData()?.let { gridListItem ->
            val listItems = ArrayList<Any>(gridListItem)
            val gridRecyclerViewInterface = GridRecyclerViewInterface(gridListItem, itemMoreOptionsClickListener = this)
            moreOptionsPopupViewHolder?.gridDefaultOptionsView?.let {
                it.setupGridWithObjects(listItems, gridRecyclerViewInterface)
                setupGridView(it, listItems)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isPopupVisible = false
        onDialogCreatedListener?.onDialogDestroy()
        onDialogCreatedListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        isPopupVisible = false
    }
    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun onListItemMoreOptionsClick(
        view: View?,
        position: Int,
        isFromKnob: Boolean,
        isMoreOptionDefaultTileClick: Boolean
    ) {
        val itemSubCategory: String? = if (isMoreOptionDefaultTileClick)
            provideDefaultOptionsListRecyclerViewTilesData()?.get(position)?.tileSubCategory else
            provideCycleListRecyclerViewTilesData()?.get(position)?.tileSubCategory

        val itemName: String? = if (isMoreOptionDefaultTileClick)
            provideDefaultOptionsListRecyclerViewTilesData()?.get(position)?.titleText else
            provideCycleListRecyclerViewTilesData()?.get(position)?.titleText

        when (itemSubCategory) {
            AppConstants.MoreOptionsSubCategory.TYPE_CHANGE_TEMPERATURE.toString()-> {
                CookingViewModelFactory.setInScopeViewModel(viewModel)
                if(isFromKnob){
                    val bundle = Bundle()
                    viewModel?.recipeExecutionViewModel?.targetTemperature?.value?.toInt()?.let {
                        bundle.putInt(
                            BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE,
                            it
                        )
                    }
                    CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                        navigateSafely(
                            it,
                            R.id.action_status_to_manualModeTemperatureTumblerFragment,
                            bundle,
                            null
                        )
                    }
                } else {
                    CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                        navigateSafely(
                            it,
                            R.id.action_manualModeTemperatureTumblerFragment_to_temperature_numpad,
                            null,
                            null
                        )
                    }
                }
            }

            AppConstants.MoreOptionsSubCategory.TYPE_TEMPERATURE_LEVEL.toString() -> {
                CookingViewModelFactory.setInScopeViewModel(viewModel)
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    navigateSafely(
                        it,
                        R.id.action_to_manualMode_durationSelectionManualModeFragment,
                        null,
                        null
                    )
                }
            }

            AppConstants.MoreOptionsSubCategory.TYPE_INSTRUCTIONS.toString() -> {
                CookingViewModelFactory.setInScopeViewModel(viewModel)
                if(isRecipeAssisted(
                        viewModel?.recipeExecutionViewModel?.recipeName?.value,
                        viewModel?.cavityName?.value
                    )){
                    viewModel?.recipeExecutionViewModel?.recipeName?.value?.let {
                        loadCookingGuide(
                            it
                        )
                    }
                    if (CookingAppUtils.cookingGuideList.isNotEmpty()){
                        CookingAppUtils.clearOrEraseCookingGuideList()
                        navigateSafely(this, R.id.global_action_to_showAssistedInstructionFragment, null, null)
                    }
                }
                else{
                    if(CookingAppUtils.checkIfInstructionAvailable(this, viewModel!!))
                    navigateToShowInstructionFragment(activity)
                }
            }

            AppConstants.MoreOptionsSubCategory.TYPE_POWER_LEVEL.toString() -> {
                CookingViewModelFactory.setInScopeViewModel(viewModel)
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    navigateSafely(
                        it,
                        if(CookingAppUtils.isRecipeReheatInProgramming(viewModel)) R.id.action_to_manualMode_mwoPowerTumblerFragment else R.id.action_status_to_mwoPowerLevelChange,
                        null,
                        null
                    )
                }
            }

            AppConstants.MoreOptionsSubCategory.TYPE_COOK_TIME.toString() -> {
                dismiss()
                CookingViewModelFactory.setInScopeViewModel(viewModel)
                if (provideCycleListRecyclerViewTilesData()?.get(position)?.isEnable == false)
                    return
                if (isFromKnob) {
                    val bundle = Bundle()
                    bundle.putString(BundleKeys.BUNDLE_PROVISIONING_TIME, "${viewModel?.recipeExecutionViewModel?.cookTime?.value?.toInt()}")
                    checkAndValidateProbeExtendedCycle {
                        HMILogHelper.Logd(tag,"Knob checkAndValidateProbeExtendedCycle callback Success : $it")
                        if (it) {
                            HMILogHelper.Logd(tag,"Knob checkAndValidateProbeExtendedCycle -->${CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)}")
                            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let { fragment ->
                                navigateSafely(
                                    fragment,
                                    R.id.action_status_to_verticalTumblerFragment,
                                    bundle,
                                    null
                                )
                            }
                        }

                    }

                } else {
                    checkAndValidateProbeExtendedCycle {
                        HMILogHelper.Logd(tag,"checkAndValidateProbeExtendedCycle callback Success : $it")
                        if (it) {
                            HMILogHelper.Logd(tag,"checkAndValidateProbeExtendedCycle -->${CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)}")
                            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let { fragment ->
                                navigateSafely(
                                    fragment,
                                    R.id.action_recipeExecutionFragment_to_cookTimeFragment,
                                    null,
                                    null
                                )
                            }
                        }

                    }

                }

            }

            AppConstants.MoreOptionsSubCategory.TYPE_EXTRA_BROWN.toString() -> {
                viewModel?.recipeExecutionViewModel?.acceptExtraBrowningAcknowledgement()
            }

            AppConstants.MoreOptionsSubCategory.TYPE_TURN_OVEN_MWO_OFF.toString() -> {
                viewModel?.recipeExecutionViewModel?.cancel()
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    navigateSafely(
                        it,
                        R.id.global_action_to_clockScreen,
                        null,
                        null
                    )
                }
            }

            AppConstants.MoreOptionsSubCategory.TYPE_PROBE.toString() -> {
                CookingViewModelFactory.setInScopeViewModel(viewModel)
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    navigateSafely(
                        it,
                        if (isFromKnob) R.id.action_to_probeTemperatureTumbler else R.id.action_to_probeTemperatureNumPad,
                        null,
                        null
                    )
                }
            }

            AppConstants.MoreOptionsSubCategory.TYPE_FAVORITES.toString() -> {
                dismiss()
                var recordChanged = true
                if (CookBookViewModel.getInstance().favoriteCount < MAX_FAVORITE_COUNT) {
                    CookBookViewModel.getInstance().allFavoriteRecords?.value?.let { favoriteList->
                        for (favoriteRecord in favoriteList) {
                            recordChanged = !(CookingAppUtils.compareFavoriteRecordAndViewModel(
                                    favoriteRecord,
                                    CookingAppUtils.getRecipeOptions(),
                                    CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                            ))
                            if(!recordChanged) break
                        }
                    }

                    val cookTimerState =
                            provideCookingViewModel()?.recipeExecutionViewModel?.cookTimerState?.value
                    if (cookTimerState == Timer.State.RUNNING || cookTimerState == Timer.State.PAUSED || cookTimerState == Timer.State.IDLE) {
                        if (recordChanged) {
                            var cycleName: String
                            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.apply {
                                cycleName = getRecipeNameText(
                                    requireContext(),
                                    recipeName.value.toString()
                                )
                            }
                            val favoriteName = CookingAppUtils.updateRecordName(cycleName)
                            provideCookingViewModel()?.recipeExecutionViewModel?.markFavorite(
                                    favoriteName,
                                    FAVORITE_DEFAULT_IMAGE
                            ).let {
                                it?.let { it1 ->
                                    FavoriteDataHolder.updateMarkFavorite(it1)
                                }
                            }
                        }else{
                            FavoriteDataHolder.updateMarkFavorite(false)
                        }
                    } else {
                        CookingAppUtils.setNavigatedFrom(NAVIGATION_FROM_CREATE_FAV)
                        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                            navigateSafely(
                                it,
                                R.id.global_action_to_keyboard_fragment,
                                Bundle().apply {
                                    putString(
                                        KEY_FAVORITE_NAME,
                                        viewModel?.recipeExecutionViewModel?.recipeName?.value.toString()
                                    )
                                    putSerializable(
                                        KEY_FAVORITE_FROM,
                                        AppConstants.FavoriteFrom.STATUS_SCREEN
                                    )
                                },
                                null
                            )
                        }
                    }
                }else {
                    FavoritesPopUpUtils.maxFavoriteReached(activity?.supportFragmentManager,fragment)
                }
            }

            AppConstants.MoreOptionsSubCategory.TYPE_AUTO_COOK.toString() -> {
                HMILogHelper.Logd(fragment?.tag, "${viewModel?.cavityName?.value} reheat auto cook sub child recipe")
                val nodeTopMode = FoodMainCategoryGridFragment.getSubFoodTypesDataForSelectedMainFood(AppConstants.RECIPE_REHEAT, CookBookViewModel.getInstance()
                    .getDefaultAssistedRecipesPresentationTreeFor(viewModel?.cavityName?.value))
                CookBookViewModel.getInstance().setRootNodeForRecipes(nodeTopMode)
                HMILogHelper.Logd("Assisted Flow: Food category " + nodeTopMode?.data)
                navigateSafely(
                    this,
                    R.id.action_manual_cookTime_to_assisted_foodSubCategory,
                    null,
                    null
                )
            }
            else -> {
                ToastUtils.showToast(this.context, "$itemName $itemSubCategory")
            }
        }
        postDialogDismissEvent(recyclerItemDataListener)
    }

    override fun onListItemDeleteClick(view: View?, position: Int) {
        // TODO("Not yet implemented")
    }

    override fun onListItemImageClick(view: View?, position: Int) {
        // TODO("Not yet implemented")
    }

    override fun provideCycleListRecyclerViewTilesData(): ArrayList<GridListItemModel>? {
        return recyclerCycleOptionsItemData
    }

    override fun provideCycleListRecyclerViewSize(): Int {
        return recyclerCycleOptionsItemData?.size ?: 0
    }

    override fun provideDefaultOptionsListRecyclerViewTilesData(): ArrayList<GridListItemModel>? {
        return recyclerDefaultListItemData
    }

    override fun provideDefaultOptionsListRecyclerViewSize(): Int {
        return recyclerDefaultListItemData?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyDataSetChanged(gridMoreOptionsView: com.whirlpool.hmi.uicomponents.widgets.grid.GridView?) {
        gridMoreOptionsView?.adapter?.notifyDataSetChanged()
    }

    class Builder(viewModel: CookingViewModel) {
        private var moreOptionsPopupBuilder: MoreOptionsPopupBuilder = MoreOptionsPopupBuilder(viewModel.cavityName.value.toString())

        /**
         * Sets the title text of the header.
         * @param titleText The resource ID of the title text.
         * @return The Builder instance.
         */
        fun setHeaderTitle(titleText: Int): Builder {
            moreOptionsPopupBuilder.titleText = titleText
            return this
        }

        fun setInScopeViewModel(cookingViewModel: CookingViewModel?): Builder {
            moreOptionsPopupBuilder.viewModel = cookingViewModel
            return this
        }

        fun setParentFragment(fragment: Fragment?): Builder {
            moreOptionsPopupBuilder.fragment = fragment
            return this
        }

        fun setLeftIcon(
            leftImageResource: Int,
            listener: NavigationButtonOnClickListener?
        ): Builder {
            moreOptionsPopupBuilder.leftIcon = leftImageResource
            moreOptionsPopupBuilder.leftIconClickListener = listener
            return this
        }

        fun setRightIcon(
            rightImageResource: Int,
            listener: NavigationButtonOnClickListener?
        ): Builder {
            moreOptionsPopupBuilder.rightIcon = rightImageResource
            moreOptionsPopupBuilder.rightIconClickListener = listener
            return this
        }

        fun setBackgroundLayoutListener(listener: NavigationButtonOnClickListener?): Builder {
            moreOptionsPopupBuilder.transparentBackgroundLayoutListener = listener
            return this
        }

        @Suppress("unused")
        fun setOvenIconVisibility(isOvenIconVisible: Int): Builder {
            moreOptionsPopupBuilder.isOvenIconVisible = isOvenIconVisible
            return this
        }

        /**
         * Set the View data type object array using this method.
         * If the more options popup is using the default Grid layout the
         * Object array shall be the string elements as the default grid
         * would assume the objects are of String type.
         * @param itemData
         */
        fun setCycleOptionsRecyclerItemData(
            itemData: ArrayList<GridListItemModel>?,
            listener: NavigationButtonOnClickListener?
        ): Builder {
            moreOptionsPopupBuilder.recyclerCycleOptionsItemData = itemData
            moreOptionsPopupBuilder.recyclerItemDataListener = listener
            return this
        }

        /**
         * Set the View data type object array using this method.
         * If the more options popup is using the default Grid layout the
         * Object array shall be the string elements as the default grid
         * would assume the objects are of String type.
         * @param itemData
         */
        fun setDefaultOptionsRecyclerItemData(
            itemData: ArrayList<GridListItemModel>?,
            listener: NavigationButtonOnClickListener?
        ): Builder {
            moreOptionsPopupBuilder.recyclerDefaultListItemData = itemData
            moreOptionsPopupBuilder.recyclerItemDataListener = listener
            return this
        }

        /**
         * method to complete the builder pattern and return the dialog object.
         */
        fun build(): MoreOptionsPopupBuilder {
            return moreOptionsPopupBuilder
        }
    }

    companion object {
        //maintaining this to make it true when dialog gets created and false on destroy
        private var isPopupVisible: Boolean = false
        private const val UNKNOWN_VALUE = -1

        /**
         * to know if any popup is visible though out the app and regardless of builder object
         * @return true if popup is visible, false otherwise
         */
        fun isAnyPopupShowing(): Boolean {
            return isPopupVisible
        }
    }

    override fun onHMILeftKnobClick() {

    }

    override fun onHMILongLeftKnobPress() {

    }

    override fun onHMIRightKnobClick() {
        if (knobCounter > -1) {
            KnobNavigationUtils.knobForwardTrace = true
            val cycleOptionsRecyclerViewSize = provideCycleListRecyclerViewSize()
            if(!recyclerCycleOptionsItemData.isNullOrEmpty() && knobCounter < cycleOptionsRecyclerViewSize)
                onListItemMoreOptionsClick(moreOptionsPopupViewHolder?.gridCycleOptionsView, knobCounter, true,
                    isMoreOptionDefaultTileClick = false)
            else {
//                If cycle options are there then knob count will come as both tile size, to select default tile below cases handled
                val defaultOptionsIndex = if (recyclerCycleOptionsItemData.isNullOrEmpty()) {
                    knobCounter
                } else {
                    knobCounter - cycleOptionsRecyclerViewSize
                }
                onListItemMoreOptionsClick(moreOptionsPopupViewHolder?.gridDefaultOptionsView, defaultOptionsIndex, true,
                isMoreOptionDefaultTileClick = true)
            }
        }
    }

    override fun onHMILongRightKnobPress() {

    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (knobId == AppConstants.RIGHT_KNOB_ID) {
                var moreOptionsPopupItemSize = provideCycleListRecyclerViewSize()
                moreOptionsPopupItemSize += provideDefaultOptionsListRecyclerViewSize()
                knobCounter = CookingAppUtils.getKnobPositionIndex(
                    knobDirection,
                    knobCounter,
                    moreOptionsPopupItemSize
                )
            }

            if(knobCounter>=0) {
                val cycleOptionsRecyclerViewSize = provideCycleListRecyclerViewSize()
                if(!recyclerCycleOptionsItemData.isNullOrEmpty() && knobCounter < cycleOptionsRecyclerViewSize) {
//                    If the knob moves to cycle items selection, deselect the default tile selection if anything is selected
                    if(!recyclerDefaultListItemData.isNullOrEmpty()) {
                        recyclerDefaultListItemData?.let {
                            deselectListItem(
                                it,
                                moreOptionsPopupViewHolder?.gridDefaultOptionsView
                            )
                        }
                    }
                    recyclerCycleOptionsItemData?.forEachIndexed { index, gridListItemModel ->
                        gridListItemModel.isSelected = index == knobCounter
                    }
                    notifyDataSetChanged(moreOptionsPopupViewHolder?.gridCycleOptionsView)
                } else {
//                    If the knob moves to default items selection, deselect the cycle options tile selection if anything is selected
                    if(!recyclerCycleOptionsItemData.isNullOrEmpty()) {
                        recyclerCycleOptionsItemData?.let {
                            deselectListItem(it, moreOptionsPopupViewHolder?.gridCycleOptionsView)
                        }
                    }
//                    calculate to get the default tile position based on the availability of cycle options
                    val defaultOptionsIndex = if (provideCycleListRecyclerViewSize() <= 0) {
                        knobCounter
                    } else {
                        knobCounter - cycleOptionsRecyclerViewSize
                    }
                    recyclerDefaultListItemData?.forEachIndexed { index, gridListItemModel ->
                        gridListItemModel.isSelected = index == defaultOptionsIndex
                    }
                    notifyDataSetChanged(moreOptionsPopupViewHolder?.gridDefaultOptionsView)
                }
            }
            else{
                HMILogHelper.Logd("Invalid knob rotation, Current position is 0 ")
                knobCounter = 0
            }
        }
    }

    /**
     * On Knob rotation to select an deselect the previous recycler view items
     */
    private fun deselectListItem(
        gridListItem: ArrayList<GridListItemModel>,
        gridMoreOptionsRecyclerView: com.whirlpool.hmi.uicomponents.widgets.grid.GridView?
    ) {
        var updateListView = false
        gridListItem.forEach {
            if (it.isSelected == true) {
                it.isSelected = false
                updateListView = true
            }
        }
        if (updateListView) notifyDataSetChanged(gridMoreOptionsRecyclerView)
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            knobCounter = -1
            recyclerCycleOptionsItemData?.forEach {
                if (it.isSelected == true) { it.isSelected = false}
            }
            notifyDataSetChanged(moreOptionsPopupViewHolder?.gridCycleOptionsView)
            recyclerDefaultListItemData?.forEach {
                if (it.isSelected == true) {it.isSelected = false}
            }
            notifyDataSetChanged(moreOptionsPopupViewHolder?.gridDefaultOptionsView)
        }
    }

    /**
     * Method is responsible for checking the is there any probe cycle running or not.
     * If yes then check probe temperature reached
     * If yes then it is probe extended cycle - Time base recipe
     * Need to show remove probe popup
     */
    private fun checkAndValidateProbeExtendedCycle(onValidationCallBack: (isSuccess:Boolean) -> Unit) {
        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
            HMILogHelper.Logd(tag,"More Option isProbeBasedRecipe --${viewModel?.recipeExecutionViewModel?.isProbeBasedRecipe}")
            if (viewModel?.recipeExecutionViewModel?.isProbeBasedRecipe == true
                && viewModel?.recipeExecutionViewModel?.targetMeatProbeTemperatureReached?.value == true
            ) {
                val isMeatProbeConnected = MeatProbeUtils.isMeatProbeConnected(viewModel)
                HMILogHelper.Logd(tag, "More Option probe extended isMeatProbeConnected = $isMeatProbeConnected")
                if (isMeatProbeConnected) {
                    PopUpBuilderUtils.removeProbeToContinueExtendedCycle(it,viewModel, onContinueButtonClick = {
                        onValidationCallBack(false)
                    } )
                } else {
                    onValidationCallBack(true)
                }
            } else {
                HMILogHelper.Logd(tag,"More Option - normal cycle running provide onValidationCallBack() ")
                onValidationCallBack(true)
            }
        }
    }

}

class MySpanSizeLookup(private val data: List<GridListItemModel>?) :
    GridLayoutManager.SpanSizeLookup() {

    override fun getSpanSize(position: Int): Int {
        val item = data?.get(position)
        // Implement your logic to determine span size based on data
        if (item != null) {
            return if (item.isMoreOptionPopUpLargeTile) {
                2 // Large item spans 2 columns
            } else {
                1 // Regular item spans 1 column
            }
        }

        return 1
    }
}

class BottomMarginItemDecoration(private val bottomMargin: Int, private val topMargin: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = bottomMargin
        outRect.top = topMargin
    }
}

