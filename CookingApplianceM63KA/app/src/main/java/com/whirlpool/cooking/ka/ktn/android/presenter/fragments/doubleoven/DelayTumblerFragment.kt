package android.presenter.fragments.doubleoven

import android.media.AudioManager
import android.os.Bundle
import android.presenter.basefragments.AbstractNumericTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.View
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.NavigationUtils

/**
 * File       : android.presenter.fragments.combooven.DelayTumblerFragment
 * Brief      : Delay start for recipe selection
 * Author     : Hiren
 * Created On : 08-01-2024
 * Details    : Add delay time to start recipe
 */
class DelayTumblerFragment : AbstractNumericTumblerFragment(),
    HeaderBarWidgetInterface.LeftIconClickListenerInterface {

    private var isSteamCleanDelay = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null && arguments?.getBoolean(BundleKeys.DELAY_STEAM_CLEAN) == true) {
            isSteamCleanDelay = true
        }
    }

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
        tumblerViewHolderHelper?.fragmentTumblerBinding?.constraintLeftButton?.visibility = View.GONE
    }

    override fun manageRightButton() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setRightIconVisibility(false)
    }

    override fun setCtaRight() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.text =
            resources.getString(R.string.text_button_start_delay)
    }

    override fun setHeaderLevel() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setTitleText(
            if (isDelayWithCookTime) resources.getString(
                R.string.text_running_ready_at
            ) else resources.getString(
                R.string.text_header_start_after
            )
        )
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setLeftIconClickListener(this)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setRightIconVisibility(false)
        if (getCookingViewModel()?.isPrimaryCavity == true) {
            getBinding()?.headerBar?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
        } else {
            getBinding()?.headerBar?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
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
                    val delayTime = tumblerViewHolderHelper?.provideNumericTumbler()?.selectedValue?.toLong()?:0
                    getCookingViewModel()?.let {
                        NavigationUtils.startDelayRecipe(this,
                            it, delayTime)
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
                    val delayTime = tumblerViewHolderHelper?.provideNumericTumbler()?.selectedValue?.toLong()?:0
                    getCookingViewModel()?.let {
                        NavigationUtils.startDelayRecipe(this,
                            it, delayTime)
                    }
                }
            }
        }
    }

    override fun onHMIRightKnobClick() {
        if (!isSteamCleanDelay) {
            onHMIKnobRightOrLeftClick()
        }
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if ((knobId == AppConstants.RIGHT_KNOB_ID) && !isSteamCleanDelay) {
            handleKnobRotate(knobDirection)
        } else if ((knobId == AppConstants.LEFT_KNOB_ID) && isSteamCleanDelay) {
            handleKnobRotate(knobDirection)
        }
    }

    private fun handleKnobRotate(knobDirection: String) {
        delayTimerList?.apply {
            if (size > 0) {
                manageKnobRotation(knobDirection)
            }
        }
    }

    override fun onHMILeftKnobClick() {
        if (isSteamCleanDelay) {
            onHMIKnobRightOrLeftClick()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isSteamCleanDelay) {
            handleTimeoutCallbackListener()
        }
    }
}