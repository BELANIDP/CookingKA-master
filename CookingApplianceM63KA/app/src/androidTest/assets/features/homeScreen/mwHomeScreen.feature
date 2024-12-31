Feature: Home Screen automation for Microwave

  Background:
    Given The app has started with Microwave low end Variant

  @homeScreen @mwHomeScreen @tabOnClockScreen
  Scenario: Test 1: Verify tab on screen and click of home button
    And I perform click on clock screen
    Then I see tumbler screen
    And I perform click on home button
    And the clock screen is visible

  @homeScreen @mwHomeScreen @rotateRightKnob
  Scenario: Test 2: Verify rotation of right knob.
    Then I rotate right knob clockwise
    Then I see tumbler screen

  @homeScreen @mwHomeScreen @openDoor
  Scenario Outline: Test 3: Verify door open event on clock screen.
    And I opened the door "<cavity1>".
    And I closed the door "<cavity1>".
    Then I see tumbler screen
    Examples:
      | cavity1   |
      | Microwave |