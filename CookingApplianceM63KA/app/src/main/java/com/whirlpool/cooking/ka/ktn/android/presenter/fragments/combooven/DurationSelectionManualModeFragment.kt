/*
 * File :
 * Author : SINGHA80.
 * Created On : 3/28/24, 6:45 PM
 * Details :
 */
package android.presenter.fragments.combooven

import android.presenter.basefragments.manualmodes.AbstractDurationSelectionManualModeFragment

/*
 * File : android.presenter.fragments.combooven.DurationSelectionManualModeFragment
 * Author : SINGHA80.
 * Created On : 3/28/24
 * Details : provides duration selection tumbler for broilLevelTemperature
 */
class DurationSelectionManualModeFragment : AbstractDurationSelectionManualModeFragment() {
    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}