Feature: Home Screen automation for Double oven

  Background:
    Given The app has started with Double low end Variant

  @homeScreen @doubleOvenHomeScreen @tabOnClockScreen
  Scenario Outline: Test 1: Verify tab on screen
    And I perform click on clock screen
    Then Verify cavity selection screen "<cavity1>" "<cavity2>"
    Examples:
      | cavity1    | cavity2    |
      | UPPER OVEN | LOWER OVEN |

  @homeScreen @doubleOvenHomeScreen @clickHomeButton
  Scenario Outline: Test 2: Verify click of home button
    And I perform click on home button
    Then Verify cavity selection screen "<cavity1>" "<cavity2>"
    Examples:
      | cavity1    | cavity2    |
      | UPPER OVEN | LOWER OVEN |

  @homeScreen @doubleOvenHomeScreen @rotateRightKnob
  Scenario Outline: Test 3: Verify rotation of right knob.
    Then I rotate right knob clockwise
    Then Verify cavity selection screen "<cavity1>" "<cavity2>"
    Examples:
      | cavity1    | cavity2    |
      | UPPER OVEN | LOWER OVEN |

  @homeScreen @doubleOvenHomeScreen @openDoor
  Scenario Outline: Test 4: Verify door open event on clock screen.
    And I opened the door "<cavity1>".
    And I closed the door "<cavity1>".
    Then I see tumbler screen
    Examples:
      | cavity1 |
      | Upper   |
      | Lower   |