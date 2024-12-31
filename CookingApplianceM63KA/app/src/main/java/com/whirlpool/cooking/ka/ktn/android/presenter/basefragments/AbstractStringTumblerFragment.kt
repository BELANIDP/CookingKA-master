/*
 *
 * * ************************************************************************************************
 * * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * * ************************************************************************************************
 *
 */
package android.presenter.basefragments

import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Visibility
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.utils.LogHelper
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.NAVIGATION_FROM_CREATE_FAV
import core.utils.AudioManagerUtils
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getNavigatedFrom
import core.utils.CookingAppUtils.Companion.rotateTumblerOnKnobEvents
import core.utils.CookingAppUtils.Companion.setNavigatedFrom
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.SharedViewModel
import core.viewHolderHelpers.StringTumblerViewHolderHelper


/**
 * File       : android.presenter.basefragments.AbstractStringTumblerFragment
 * Brief      : NumericTumbler CVT Abstract class.
 * Author     : SINGHJ25.
 * Created On : 05.Feb.2024
 * Details    :
 */
abstract class AbstractStringTumblerFragment : SuperAbstractTimeoutEnableFragment(),
    View.OnClickListener,
    HMIKnobInteractionListener {
    private var cavityNameText: String = ""
    protected var tumblerViewHolderHelper: StringTumblerViewHolderHelper? = null
    private var onClickListener: CustomClickListenerInterface? = null
    private var rotator = listOf(0, 1) // Tumbler, CTA's,
    private var selectedRotator = rotator[0]
    private var isTumblerSelected = true
    private var isGhostButtonVisible: Boolean = false
    private var isPrimaryButtonVisible: Boolean = false
    private var knobRotationCount = 0

    fun interface CustomClickListenerInterface {
        fun viewOnClick(view: View?)
    }

    enum class RecyclerViewType {
        MANUAL, VISUAL
    }

    private val tumblerListObject: ViewModelListInterface? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        enterTransition = Fade().apply {
            duration = resources.getInteger(R.integer.ms_250).toLong()
            mode = Visibility.MODE_IN
        }
        tumblerViewHolderHelper = StringTumblerViewHolderHelper()
        tumblerViewHolderHelper?.onCreateView(inflater, container, savedInstanceState)
        return tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.root
    }

    /**
     * Method to manage Learn More icons data
     */
    protected open fun initTemperatureTumbler() {
        tumblerViewHolderHelper?.providePrimaryButton()?.setOnClickListener(this)
        tumblerViewHolderHelper?.provideExtendedPrimaryButton()?.setOnClickListener(this)
        tumblerViewHolderHelper?.provideExtendedGhostButton()?.setOnClickListener(this)
        tumblerViewHolderHelper?.provideGhostButton()?.setOnClickListener(this)
        tumblerViewHolderHelper?.providePrimaryImageView()?.setOnClickListener(this)
        tumblerViewHolderHelper?.provideGhostImageView()?.setOnClickListener(this)
        tumblerViewHolderHelper?.provideNumericTumbler()?.setItemAnimator(null)
        tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.tumblerString?.baseItemAnimator =
            null
        tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.tumblerString?.modifierItemAnimator =
            null
        setCavityViewModelByProductVariant()
        loadRecipe()
        setTumblerStringTempData()
        setHeaderBarViews()
        setCavityNameText()
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.tick,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
    }

    fun updateHeader() {
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setLeftIconVisibility(true)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setCustomOnClickListener(object :
            HeaderBarWidgetInterface.CustomClickListenerInterface {
            override fun leftIconOnClick() {
                super.leftIconOnClick()
                navigateBack()
            }
        })
    }

    private fun navigateBack() {
        if (CookingAppUtils.isAnyCycleRunning() || getNavigatedFrom() == AppConstants.CLOCK_FAR_OR_VIDEO_VIEW_FRAGMENT ) {
            HMILogHelper.Logd("Cycle is running - Navigate to the status screen")
            CookingAppUtils.navigateToStatusOrClockScreen(this)
        } else {
            HMILogHelper.Logd("Cycle is not running - Navigate to the back screen")
            NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    NavigationUtils.getViewSafely(
                        this
                    ) ?: requireView()
                )
            )
        }
    }

    open fun setCavityNameText() {
        tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.headerBar?.setOvenCavityTitleText(
            getHeaderBarCavityTitleName()
        )
    }

    private fun getHeaderBarCavityTitleName(): String {
        cavityNameText = if (!CookingViewModelFactory.getInScopeViewModel().isOfTypeMicrowaveOven) {
            if (CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity) {
                getString(R.string.cavity_selection_upper_oven_all_caps)
            } else {
                getString(R.string.cavity_selection_lower_oven_all_cap)
            }
        } else {
            getString(R.string.cavity_selection_microwave_all_caps)
        }
        return cavityNameText
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        setTumblerItemDivider(
            R.drawable.ic_tumbler_item_separator,
            tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.tumblerString
        )
        setTumblerItemDivider(
            R.drawable.ic_tumbler_item_separator,
            tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.tumblerStringVision
        )
        setTumblerItemDivider(
            R.drawable.ic_tumbler_item_separator,
            tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.tumblerStringVisionBase
        )
        isPrimaryButtonVisible =
            tumblerViewHolderHelper?.providePrimaryButton()?.isVisible == true
        isGhostButtonVisible =
            tumblerViewHolderHelper?.provideGhostButton()?.isVisible == true
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
        }
    }

    protected open fun setTumblerItemDivider(
        itemDivider: Int = R.drawable.ic_tumbler_item_separator,
        tumbler: BaseTumbler?
    ) {
        tumbler?.apply {
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.HORIZONTAL
                ).apply {
                    ContextCompat.getDrawable(
                        requireContext(),
                        itemDivider
                    )?.let { setDrawable(it) }
                }
            )
        }
    }

    /**
     *  Method to enable sub text when recipe is Quick Start
     *  @param tumblerList recipe name list
     * */
    protected fun setQuickStartRecipe(tumblerList: ArrayList<String>) {
        tumblerViewHolderHelper?.provideNumericTumbler()
            ?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val selectedIndex =
                            tumblerViewHolderHelper?.provideNumericTumbler()?.selectedIndex
                        if (selectedIndex?.let { tumblerList[it] == AppConstants.QUICK_START } == true) {
                            tumblerViewHolderHelper?.provideSubTitle()?.visibility = View.VISIBLE
                            if (CookingViewModelFactory.getInScopeViewModel().isOfTypeOven)
                                tumblerViewHolderHelper?.provideSubTitle()?.text =
                                    getString(R.string.text_quick_start_oven_sub_text)
                            else
                                tumblerViewHolderHelper?.provideSubTitle()?.text =
                                    getString(R.string.text_Mwo30_sec)
                        } else
                            tumblerViewHolderHelper?.provideSubTitle()?.visibility = View.GONE
                    }
                }
            })
    }

    /**
     *  Method to enable sub text when recipe is Quick Start
     *  @param tumblerList recipe name list
     * */
    protected fun setQuickStartRecipeVision(tumblerList: ArrayList<String>) {
        tumblerViewHolderHelper?.provideNumericTumblerVisionBase()
            ?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val selectedIndex =
                            tumblerViewHolderHelper?.provideNumericTumblerVisionBase()?.selectedIndex
                        if (selectedIndex?.let { tumblerList[it] == AppConstants.QUICK_START } == true) {
                            tumblerViewHolderHelper?.provideVisionSubTitle()?.visibility = View.VISIBLE
                            if (CookingViewModelFactory.getInScopeViewModel().isOfTypeOven)
                                tumblerViewHolderHelper?.provideVisionSubTitle()?.text =
                                    getString(R.string.text_quick_start_oven_sub_text)
                            else
                                tumblerViewHolderHelper?.provideVisionSubTitle()?.text =
                                    getString(R.string.text_Mwo30_sec)
                        } else
                            tumblerViewHolderHelper?.provideVisionSubTitle()?.visibility = View.GONE
                    }
                }
            })
    }

    protected abstract fun initTumbler()

    /**
     * Method to get the Tumbler +5 | -5 visibility to be shown in the view
     */
    protected abstract fun provideTumblerModifierTextVisibility(): Int

    override fun onClick(v: View?) {
        onClickListener?.viewOnClick(v)
    }

    /**
     * Method to manage knob rotation
     * @param knobDirection: String?
     * */
    protected fun manageKnobRotation(knobDirection: String) {
        if (isTumblerSelected) {
            // Handle tumbler selection and knob events
            selectedRotator = 0
            val tumbler = getRecyclerView() ?: return
            rotateTumblerOnKnobEvents(this, tumbler, knobDirection)
        } else {
            when {
                isPrimaryButtonVisible && isGhostButtonVisible -> {
                    adjustKnobRotation(knobDirection)
                    updateButtonBackgrounds(knobRotationCount)
                }

                isPrimaryButtonVisible -> updateButtonBackgroundForSingleButton(
                    R.drawable.selector_textview_walnut,
                    isPrimaryButton = true
                )

                isGhostButtonVisible -> updateButtonBackgroundForSingleButton(
                    R.drawable.selector_textview_walnut,
                    isPrimaryButton = false
                )
            }
        }
    }

    /**
     * Adjust the knob rotation count based on the knob direction.
     */
    private fun adjustKnobRotation(knobDirection: String) {
        when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION -> {
                if (knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
            }
            KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> {
                if (knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            }
        }
    }

    /**
     * Update both buttons' backgrounds based on knobRotationCount.
     */
    private fun updateButtonBackgrounds(knobRotationCount: Int) {
        val (primaryButtonRes, ghostButtonRes) = when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> Pair(R.drawable.selector_textview_walnut, R.drawable.text_view_ripple_effect)
            AppConstants.KNOB_COUNTER_TWO -> Pair(R.drawable.text_view_ripple_effect, R.drawable.selector_textview_walnut)
            else -> Pair(R.drawable.selector_textview_walnut, R.drawable.selector_textview_walnut)
        }
        setButtonBackgrounds(ghostButtonRes, primaryButtonRes)
    }

    /**
     * Helper function to set the background for a single button (either primary or ghost).
     */
    private fun updateButtonBackgroundForSingleButton(drawableRes: Int, isPrimaryButton: Boolean) {
        val targetButton = if (isPrimaryButton) {
            tumblerViewHolderHelper?.providePrimaryButton()
        } else {
            tumblerViewHolderHelper?.provideGhostButton()
        }

        targetButton?.background = ResourcesCompat.getDrawable(resources, drawableRes, null)
    }

    /**
     * Helper function to set the backgrounds for both buttons.
     */
    private fun setButtonBackgrounds(ghostButtonRes: Int, primaryButtonRes: Int) {
        val resources = resources
        tumblerViewHolderHelper?.provideGhostButton()?.background =
            ResourcesCompat.getDrawable(resources, ghostButtonRes, null)
        tumblerViewHolderHelper?.providePrimaryButton()?.background =
            ResourcesCompat.getDrawable(resources, primaryButtonRes, null)
    }

    /**
     * Reset the backgrounds for both primary and ghost buttons to the ripple effect.
     */
    private fun resetButtonBackgrounds() {
        updateButtonBackgroundForSingleButton(R.drawable.text_view_ripple_effect, isPrimaryButton = true)
        updateButtonBackgroundForSingleButton(R.drawable.text_view_ripple_effect, isPrimaryButton = false)
    }

    /**
     * Handle knob clicks and update the state of selectedRotator and knobRotationCount.
     */
    fun onHMIKnobRightOrLeftClick() {
        HMILogHelper.Logd("Knob", "onHMIKnobRightOrLeftClick() called  : $selectedRotator")
        when (selectedRotator) {
            0 -> {
                isTumblerSelected = false
                selectedRotator = 1
                knobRotationCount = 1
                updateButtonBackgroundForSingleButton(
                    R.drawable.selector_textview_walnut,
                    isPrimaryButton = true
                )
            }

            1 -> {
                KnobNavigationUtils.knobForwardTrace = true
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        if (tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled == true)
                            onClick(tumblerViewHolderHelper?.providePrimaryButton())
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        if (tumblerViewHolderHelper?.provideGhostButton()?.isEnabled == true)
                            onClick(tumblerViewHolderHelper?.provideGhostButton())
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        clearMemory()
        super.onDestroyView()
    }

    private fun clearMemory() {
        HMILogHelper.Logi("clear memory called")
        isTumblerSelected = true
        selectedRotator = 0
        knobRotationCount = 0
        tumblerViewHolderHelper?.onDestroyView()
    }

    /**
     * show degree symbol along with values in the tumbler or not based on the input it will attach
     *
     * @return true/false
     */
    protected open fun isShowSuffixDecoration(): Boolean {
        return true
    }

    /**
     * Initialize the views here.
     */
    protected open fun initViews() {
        initTumbler()
    }

    /**
     * Getter pf Temperature tumbler fragment recycler view
     */
    protected open fun getRecyclerView(): BaseTumbler? {
        return tumblerViewHolderHelper?.provideNumericTumbler()
    }

    /**
     * Method to assign Learn More icons visibility
     */
    protected open fun setLearnMoreIcon(): Int {
        return View.GONE
    }

    /**
     * set cavity view model product variant when implemented
     */
    protected open fun setCavityViewModelByProductVariant() {}

    /**
     * load pyro recipe when implemented
     */
    protected open fun loadRecipe() {}

    /**
     * set tumbler data when implemented
     */
    protected open fun setTumblerStringTempData() {}

    /**
     * set header bar view when implemented
     */
    protected open fun setHeaderBarViews() {}

    /**
     * provides custom click events on the view
     * @param onClickListener [CustomClickListenerInterface]
     */
    protected open fun setCustomClickListener(onClickListener: CustomClickListenerInterface?) {
        this.onClickListener = onClickListener
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILeftKnobClick() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMIRightKnobClick() {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        resetButtonBackgrounds()
    }

    fun provideResources(): Resources {
        return tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.root?.resources as Resources
    }

    fun getDefaultRecipeName(cookingViewModel: CookingViewModel?, isForFavorite : Boolean = false): String {
        return when {
            cookingViewModel?.isOfTypeMicrowaveOven == true && isForFavorite -> AppConstants.RECIPE_MICROWAVE
            else -> SharedViewModel.getSharedViewModel(this.requireActivity())
                .getCurrentRecipeBeingProgrammed()
                .ifEmpty { if (cookingViewModel?.isOfTypeMicrowaveOven == true) AppConstants.QUICK_START else AppConstants.RECIPE_BAKE }
        }
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    /**
     *  Method to animate recipe selection view
     * */
    fun animateViews() {

        val primaryImageView = tumblerViewHolderHelper?.providePrimaryImageView()
        val ghostImageView = tumblerViewHolderHelper?.provideGhostImageView()
        val headerBarWidget = tumblerViewHolderHelper?.provideHeaderBarWidget()
        val ovenCavityTitleTextView = headerBarWidget?.getOvenCavityTitleTextView()
        val ovenCavityImageView = headerBarWidget?.getOvenCavityImageView()
        val primaryButton = tumblerViewHolderHelper?.providePrimaryButton()
        val ghostButton = tumblerViewHolderHelper?.provideGhostButton()

        // Set initial visibility
        listOf(
            primaryImageView,
            ghostImageView,
            ovenCavityImageView,
            ovenCavityTitleTextView,
            primaryButton,
            ghostButton
        ).forEach { it?.visibility = View.INVISIBLE }

        // Play slide-in animations for views
        playSlideInAnimation(ovenCavityTitleTextView,  R.anim.anim_top_side_widget_fade_in)
        playSlideInAnimation(ovenCavityImageView,  R.anim.anim_top_side_widget_fade_in)
        playSlideInAnimation(primaryImageView,  R.anim.anim_bottom_side_widget_fade_in)
        playSlideInAnimation(primaryButton,  R.anim.anim_bottom_side_widget_fade_in)
        playSlideInAnimation(ghostImageView,  R.anim.anim_bottom_side_widget_fade_in)
        playSlideInAnimation(ghostButton,  R.anim.anim_bottom_side_widget_fade_in)

        // Play fade-out animations for views
        listOf(
            ovenCavityTitleTextView,
            primaryButton,
            ghostButton
        ).forEach { playFadeOutAnimation(it, R.anim.fade_out, 3000,  1000) }
    }

    fun playSlideInAnimation(view: View?, animationResId: Int) {
        view?.let {
            it.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(context, animationResId)
            it.startAnimation(animation)
        }
    }

    fun playFadeOutAnimation(view: View?, animationResId: Int, duration: Long, delay: Long) {
        view?.let {
            val animation = AnimationUtils.loadAnimation(context, animationResId)
            animation.duration = duration
            it.postDelayed({
                it.startAnimation(animation)
            }, delay)
        }
    }

    fun handleFavoriteClick() {
        val favoriteCount = CookBookViewModel.getInstance().favoriteCount
        setNavigatedFrom(NAVIGATION_FROM_CREATE_FAV)
        when {
            favoriteCount == 0 -> {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_recipeSelectionFragment_to_createNewFavoriteFragment,
                    null,
                    null
                )
            }

            favoriteCount > 0 -> {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_recipeSelectionFragment_to_favoriteLandingFragment,
                    null,
                    null
                )
            }
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            if(isAdded) {
                HMILogHelper.Logd("HMI_KEY","Is Self Clean For on Duration Selection ${CookingAppUtils.isSelfCleanFlow()}")
                if (CookingAppUtils.isSelfCleanFlow()) {
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SELF_CLEAN)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SELF_CLEAN)
                } else {
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
                }
            }
        }, AppConstants.DELAY_CONFIGURATION_1000)
    }
}
