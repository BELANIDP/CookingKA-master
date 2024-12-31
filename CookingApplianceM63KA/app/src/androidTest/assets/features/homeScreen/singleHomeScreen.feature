Feature: Home Screen automation for Single Oven

  Background:
    Given The app has started with Single low end Variant

  @homeScreen @singleOvenHomeScreen @tabOnClockScreen
  Scenario: Test 1: Verify tab on screen
    And I perform click on clock screen
    Then I see tumbler screen

  @homeScreen @singleOvenHomeScreen @clickHomeButton
  Scenario: Test 2: Verify click of home button
    And I perform click on home button
    Then I see tumbler screen

  @homeScreen @singleOvenHomeScreen @rotateRightKnob
  Scenario: Test 3: Verify rotation of right knob.
    Then I rotate right knob clockwise
    Then I see tumbler screen

  @homeScreen @singleOvenHomeScreen @openDoor
  Scenario Outline: Test 4: Verify door open event on clock screen.
    And I opened the door "<cavity1>".
    And I closed the door "<cavity1>".
    Then I see tumbler screen
    Examples:
      | cavity1 |
      | Single  |