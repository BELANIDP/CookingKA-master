package android.presenter.fragments.self_clean

import android.presenter.basefragments.AbstractNumericTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.PopUpBuilderUtils

/**
 * File       : jcom.whirlpool.cooking.ka.ktn.android.presenter.basefragments.AbstractNumericTumblerFragment
 * Brief      : This is common class used for double,single and combo oven cavity,
 * Author     :SINGHJ25
 * Created On : 01-03-2024
 * Details    : Add delay time to start the self clean process
 */
class SelfCleanDelayTimeTumblerFragment : AbstractNumericTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface {

    override fun initTumbler() {
        initDelayTumbler()
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
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.text =
            resources.getString(R.string.text_button_start_delay)
    }

    override fun manageRightButton() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setRightIconVisibility(false)
    }

    override fun setCtaRight() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnGhost?.visibility = View.GONE
    }

    override fun setHeaderLevel() {
        val productVariant = CookingViewModelFactory.getProductVariantEnum()
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setTitleText(
            resources.getString(
                R.string.text_header_ready_at
            )
        )
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setCustomOnClickListener(this)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setRightIconVisibility(false)
        if (productVariant == CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN) {
            tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setOvenCavityIconVisibility(
                false
            )
        } else if (productVariant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
            tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setOvenCavityIconVisibility(
                true
            )
            if (CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity) {
                tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
            } else {
                tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
            }
        } else if (productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO) {
            tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setOvenCavityIconVisibility(
                true
            )
            if (CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity) {
                tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
            } else {
                tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
            }
        }

    }

    override fun leftIconOnClick() {
        PopUpBuilderUtils.pressStartPopUp(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.id -> {
                if (tumblerViewHolderHelper?.provideNumericTumbler()?.selectedValue != null) {
                    val delayTime =
                        tumblerViewHolderHelper!!.provideNumericTumbler()!!.selectedValue.toLong()
                    getInScopeViewModel()?.recipeExecutionViewModel?.setDelayTime(delayTime)
                }
                CookingAppUtils.prepareOvenAndStartSelfClean(
                    this,
                    true
                )
            }
        }
    }

    override fun onHMILeftKnobClick() {
        onHMIKnobRightOrLeftClick()
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            delayTimerList?.apply {
                if (size > 0) {
                    manageKnobRotation(knobDirection)
                }
            }
        }
    }
    override fun provideScreenTimeoutValueInSeconds(): Int {
        return resources.getInteger(R.integer.session_short_timeout)
    }
}

