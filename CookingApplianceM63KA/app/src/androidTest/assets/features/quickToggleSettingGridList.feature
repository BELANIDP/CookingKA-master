Feature: QuickToggleSettingGridList
  Test QuickToggleSettingGridList Item View screen

  Background:
    Given App has started
    Then I navigate to clock screen for quick toggle settings list view screen
    And I navigate to Quick Toggle Setting Grid List View

  @quickToggleSettingGridList @success
  Scenario: Test 1 : Navigate to Quick Toggle Setting Grid List View
    Then Quick Toggle Setting Grid List View Screen will be visible

  @quickToggleSettingGridList @success
  Scenario: Test 2 : Check Quick Toggle Setting Grid List View Screen will be enable
    Then Check Quick Toggle Setting Grid List View Screen is enable

  @quickToggleSettingGridList @success
  Scenario Outline: Test 3 : Scroll Quick Toggle Setting Grid list to specific elements
    Then I verify Scroll Quick Toggle Setting Grid list to specific elements "<position>"
    Examples:
      | position |
      | 0        |
      | 1        |
      | 2        |

  @quickToggleSettingGridList @success
  Scenario Outline: Test 4 : Check Title text and All properties of Title of Quick Toggle Setting Grid
    Then Check Title text and All properties of Title of Quick Toggle Setting Grid "<position>" , "<visible>" , "<text>" , "<width>" , "<height>" , "<fontfamily>" , "<weight>" , "<size>" , "<lineheight>" , "<gravity>" , "<color>"
    Examples:
      | position | visible | text          | width | height | fontfamily     | weight | size | lineheight | gravity | color   |
      | 0        | true    | Control Lock  | 213   | 40     | roboto_regular | 400    | 30   | 36         | center  | #FFFFFF |
      | 1        | true    | Mute          | 213   | 40     | roboto_regular | 400    | 30   | 36         | center  | #FFFFFF |
      | 2        | true    | Remote Enable | 213   | 40     | roboto_regular | 400    | 30   | 36         | center  | #FFFFFF |

  @quickToggleSettingGridList @success
  Scenario Outline: Test 5 : Click on toggle button
    Then Verify click on toggle button "<position>"
    Examples:
      | position |
      | 0        |
      | 1        |
      | 2        |

  @quickToggleSettingGridList @success
  Scenario Outline: Test 6 : Check toggle Button's width, height ,ON/OFF and enabled/disabled
    Then Check toggle Button's width, height ,ON_OFF and enabled_disabled "<position>" "<visible>" "<width>" "<height>" "<on_off>" "<enable_disable>"
    Examples:
      | position | visible | width | height | on_off | enable_disable |
      | 0        | true    | 112   | 56     | false  | true           |
      | 1        | true    | 112   | 56     | false  | true           |
      | 2        | true    | 112   | 56     | false  | true           |
