package core.utils

data class TraversingAction(val lastSelected: Int, val isBackPress: Boolean)
class KnobNavigationUtils {
    enum class ClickState(val count: Int) {
        FIRST(1),
        SECOND(2)
    }
    companion object {
        /************************ For default knob selections ***************************/
        private val mutableActionsForTraversing = mutableListOf(TraversingAction(-1, false))

        // for tracking knob click to move to next screen
        var knobForwardTrace: Boolean = false

        // for tracking knob click to move to previous screen
        var knobBackTrace: Boolean = false

        // method to add selected position on Knob click
        fun addTraversingData(lastSelected: Int, isBackPress: Boolean) {
            mutableActionsForTraversing.add(TraversingAction(lastSelected, isBackPress))
        }

        // Method to remove the last action
        fun removeLastAction() {
            mutableActionsForTraversing.let { actions ->
                if (actions.isNotEmpty()) {
                    actions.removeAt(actions.lastIndex)
                }
            }
        }

        // method to remove last saved action and updated the action with back button press true
        fun setBackPress() {
            knobForwardTrace = false
            mutableActionsForTraversing.let { actions ->
                if (actions.isNotEmpty()) {
                    val lastAction = actions.removeAt(actions.lastIndex)
                    actions.add(lastAction.copy(isBackPress = true))
                }
            }
        }

        // method to get selected position before and after navigating to new fragment
        fun lastTimeSelectedData(): Int {
            return mutableActionsForTraversing.lastOrNull()?.lastSelected ?: -1
        }

        // method to get last saved value as back press
        fun isBackPress(): Boolean {
            return mutableActionsForTraversing.lastOrNull()?.isBackPress ?: false
        }

        // method to clear knob back or forward navigation flags and other actions
        fun clearState() {
            knobForwardTrace = false
            knobBackTrace = false
            mutableActionsForTraversing.clear()  // Clear the list
        }
    }
}