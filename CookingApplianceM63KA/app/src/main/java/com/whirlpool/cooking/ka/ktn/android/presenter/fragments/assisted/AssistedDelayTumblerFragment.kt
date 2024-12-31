package android.presenter.fragments.assisted

import android.media.AudioManager
import android.os.Bundle
import android.presenter.basefragments.AbstractNumericTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.View
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateSafely

/**
 * File       : android.presenter.fragments.combooven.DelayTumblerFragment
 * Brief      : Delay start for recipe selection
 * Author     : Hiren
 * Created On : 08-01-2024
 * Details    : Add delay time to start recipe
 */
class AssistedDelayTumblerFragment : AbstractNumericTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface {

    private var showCookingGuide : Boolean = false

    override fun initTumbler() {
        updateDelayTumblerEveryMinute()
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return 1
    }

    override fun setSuffixDecoration(): String {
        return ""
    }


    override fun updateCtaRightButton() {
        setCtaLeft()
    }


    override fun setCtaLeft() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnGhost?.visibility = View.GONE
    }

    override fun manageRightButton() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setRightIconVisibility(false)
    }

    override fun setCtaRight() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.visibility = View.VISIBLE
        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.recipeName.value?.let {
            CookingAppUtils.loadCookingGuide(
                it
            )
        }
        val recipeRecord = CookBookViewModel.getInstance().getRecipeRecordById(CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.recipeId)
        showCookingGuide = (!NavigationUtils.isFirstTimeAssistedRecipeSelected(recipeRecord, CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel) && CookingAppUtils.cookingGuideList.isNotEmpty())
        CookingAppUtils.clearOrEraseCookingGuideList()
        if(showCookingGuide) getString(R.string.text_button_next) else getString(R.string.text_button_start)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.text =  if(showCookingGuide) getString(R.string.text_button_next) else resources.getString(R.string.text_button_start_delay)
    }

    override fun setHeaderLevel() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setTitleText(
            if (isDelayWithCookTime) resources.getString(
                R.string.text_running_ready_at
            ) else resources.getString(
                R.string.text_header_start_after
            )
        )
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setCustomOnClickListener(this)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setRightIconVisibility(false)
        val productVariant = CookingViewModelFactory.getProductVariantEnum()
        if (productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO || productVariant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
            getBinding()?.headerBar?.setOvenCavityIcon(if (getCookingViewModel()?.isPrimaryCavity == true) R.drawable.ic_oven_cavity_large else R.drawable.ic_lower_cavity_large)
        } else {
            getBinding()?.headerBar?.setOvenCavityIconVisibility(false)
        }
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setOvenCavityIconVisibility(false)
    }

    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(
                    this
                ) ?: requireView()
            )
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.id -> {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.start_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                if (tumblerViewHolderHelper?.provideNumericTumbler()?.selectedValue != null) {
                    val delayTime = tumblerViewHolderHelper?.provideNumericTumbler()?.selectedValue?.toLong() ?: 0
                    if(showCookingGuide){
                        val bundle = Bundle()
                        bundle.putLong(BundleKeys.BUNDLE_NAVIGATED_ASSISTED_DELAY_COOKING_GUIDE, delayTime)
                        navigateSafely(this, R.id.action_to_assisted_cookingGuideFragment_from_assistedDelay, bundle, null)
                    }else {
                        getCookingViewModel()?.let {
                            NavigationUtils.startDelayRecipe(
                                this, it, delayTime
                            )
                        }
                    }
                }
            }
            tumblerViewHolderHelper?.fragmentTumblerBinding?.constraintRightButton?.id -> {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.start_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                if (tumblerViewHolderHelper?.provideNumericTumbler()?.selectedValue != null) {
                    val delayTime = tumblerViewHolderHelper?.provideNumericTumbler()?.selectedValue?.toLong() ?: 0
                    if(showCookingGuide){
                        val bundle = Bundle()
                        bundle.putLong(BundleKeys.BUNDLE_NAVIGATED_ASSISTED_DELAY_COOKING_GUIDE, delayTime)
                        navigateSafely(this, R.id.action_to_assisted_cookingGuideFragment_from_assistedDelay, bundle, null)
                    }else {
                        getCookingViewModel()?.let {
                            NavigationUtils.startDelayRecipe(
                                this, it, delayTime
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobSelectionTimeout(knobId: Int) {

    }

    override fun onHMIRightKnobClick() {
        onClick(tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary)
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            delayTimerList?.apply {
                if (size > 0) {
                    manageKnobRotation(knobDirection)
                }
            }
        }
    }
}

