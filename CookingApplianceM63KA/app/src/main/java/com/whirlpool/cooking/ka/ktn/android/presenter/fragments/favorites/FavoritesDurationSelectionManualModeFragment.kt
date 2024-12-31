package android.presenter.fragments.favorites

import android.presenter.basefragments.manualmodes.AbstractDurationSelectionManualModeFragment

/**
 * File : android.presenter.fragments.favorites.FavoritesDurationSelectionManualModeFragment
 * Author : VYASM
 * Created On : 23/10/2024
 * Details : provides duration selection tumbler for broilLevelTemperature for saving as favorite
 */
class FavoritesDurationSelectionManualModeFragment : AbstractDurationSelectionManualModeFragment() {

    override fun setViewsByProductVariant() {
        super.setViewsByProductVariant()
        viewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(false)
    }

    override fun updateCtaLeftButton() {
        //do nothing
    }
    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}