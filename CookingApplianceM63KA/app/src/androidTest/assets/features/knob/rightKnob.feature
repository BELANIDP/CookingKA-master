Feature: Right Knob
  Test Right Knob Feature

  Background:
    Given The app has started with Double low end Variant
    And I navigate to clock screen for verify knob events

  @rightKnob @manualModeDoubleOven @success
  Scenario: Test 1 : Verify Manual mode Double Oven flow via Right Knob
    Then I rotate right knob clockwise
    #  --------------------- Cavity Screen ----------------------------- #
    And I navigate to cavity selection screen
    Then I rotate right knob clockwise
    And I expect upper cavity should hover
    Then I rotate right knob clockwise
    And I expect lower cavity should hover
    Then I rotate right knob clockwise
    And I click right knob
    #  ----------------------- Duration Screen ------------------------- #
    And I navigate to duration tumbler screen
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob counter clockwise
    Then I rotate right knob counter clockwise
    Then I rotate right knob counter clockwise
    Then I rotate right knob counter clockwise
    And I click right knob
    Then I rotate right knob clockwise
    And I click right knob
    #  ------------------------ Temperature Screen ------------------------ #
    And I navigate to temperature screen
    Then I rotate right knob clockwise
    Then I rotate right knob counter clockwise
    Then I rotate right knob counter clockwise
    Then I rotate right knob counter clockwise
    Then I rotate right knob counter clockwise
    Then I rotate right knob counter clockwise
    Then I rotate right knob counter clockwise
    Then I rotate right knob counter clockwise
    And I click right knob
    #  ------------------------- Status Screen -------------------------- #
    And I navigate to manual mode status screen
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    And I click right knob
    #  ------------------------- More Option Popup Screen ---------------- #
    And I navigate to manual mode more options screen
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    And I click right knob
    #  -------------------------- Vertical Tumbler Screen ------------------- #
    And I navigate to manual mode vertical cook time screen
    Then I rotate right knob clockwise
    And I click right knob
    Then I rotate right knob counter clockwise
    And I click right knob
    #  ------------------------ Selecting Lower Oven From Status Screen -------- #
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    And I click right knob
    # ------------------------ Repeat Manual Mode duration screen ---------------- #
    And I navigate to duration tumbler screen
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob counter clockwise
    Then I rotate right knob counter clockwise
    And I click right knob
    Then I rotate right knob clockwise
    And I click right knob
    # ------------------------- Temperature Screen -------------------- #
    And I navigate to temperature screen
    Then I rotate right knob clockwise
    Then I rotate right knob counter clockwise
    And I click right knob
    # ------------------------  Navigate to manual mode status screen  ----------------- #
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    And I click right knob
    #  ------------------------  Navigate to manual mode more options screen  ------------ #
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    Then I rotate right knob clockwise
    And I click right knob
    #  ---------------------- Navigate to manual mode vertical cook time screen ------------- #
    Then I rotate right knob clockwise
    And I click right knob
    Then I rotate right knob counter clockwise
    And I click right knob
    #  ----------------------- Cancel Both Cycle ------------------------- #
    Then I click turn off cycle