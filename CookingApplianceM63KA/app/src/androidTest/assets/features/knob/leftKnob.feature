Feature: Left Knob
  Test Left Knob Feature

  Background:
    Given The app has started with Double low end Variant
    And I navigate to clock screen for verify left knob events

  @leftKnob @drawer @success
  Scenario: Test 1 : Verify cavity light flow via left knob click
    # --------- Click on Clock for turn on cavity light ------------ #
    And I click left knob

  @leftKnob @drawer @success
  Scenario: Test 2 : Verify drawer flow via left knob rotate event
    # --------- Open drawer -------------------- #
    Then I rotate left knob clockwise
    # --------- Hover drawer options -------------------- #
    Then I rotate left knob clockwise
    Then I rotate left knob clockwise
    Then I rotate left knob clockwise
    Then I rotate left knob clockwise
    # ---------  Click on drawer oven light option  -------------------- #
    And I click left knob

