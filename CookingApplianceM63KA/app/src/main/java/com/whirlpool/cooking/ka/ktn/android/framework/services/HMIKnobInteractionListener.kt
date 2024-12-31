package android.framework.services

interface HMIKnobInteractionListener {
    /**
     * Called when there is a Left Knob click event
     */
    fun onHMILeftKnobClick()

    /**
     * Called when there is a Long Left Knob click event
     */
    fun onHMILongLeftKnobPress()

    /**
     * Called when there is a Right Knob click event
     */
    fun onHMIRightKnobClick()

    /**
     * Called when there is a Long Right Knob click event
     */
    fun onHMILongRightKnobPress()

    /**
     * Called when there is a Long Right Knob click event
     */
    fun onHMIRightKnobTickHoldEvent(timeInterval: Int)

    /**
     * Called when there is a Long left Knob click event
     */
    fun onHMILeftKnobTickHoldEvent(timeInterval: Int)

    /**
     * Called when there is a knob rotate event on a Knobs
     * @param knobId  knob ID
     * @param knobDirection knob movement direction
     */
    fun onKnobRotateEvent(knobId: Int, knobDirection: String)

    /**
     * Called after 10 sec when there is no interaction on knob
     */
    fun onKnobSelectionTimeout(knobId: Int)
}