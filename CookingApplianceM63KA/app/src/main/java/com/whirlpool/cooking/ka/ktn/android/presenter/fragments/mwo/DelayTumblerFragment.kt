package android.presenter.fragments.mwo

import android.media.AudioManager
import android.presenter.basefragments.AbstractNumericTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.View
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.AudioManagerUtils
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
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.text =
            resources.getString(R.string.text_button_start_delay)
    }

    override fun setHeaderLevel() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setTitleText(
            resources.getString(
                R.string.text_header_start_after
            )
        )
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setLeftIconClickListener(this)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setRightIconVisibility(false)
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
        }
    }

    override fun onHMIRightKnobClick() {
        onHMIKnobRightOrLeftClick()
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