Feature: Control Lock Feature

  Background:
    Given App has started

    @controllock
    Scenario: Test 1 : Validate the flow of Control lock for idle screen
      And Settings screen has started
      And I navigate to settings screen
      And I click on control lock
      And I validate the control lock popup
      And I click on continue
      And The clock screen is visible with control lock

    @controllock
    Scenario: Test 2 : Validate the flow of Control lock for idle screen via left knob
      Then I rotate left knob clockwise
      Then I rotate left knob clockwise
      Then I rotate left knob clockwise
      And I click left knob
      And I click on control lock
      And I click on continue
      And The clock screen is visible with control lock
      And I perform click on clock screen
      And The sliding bar to unlock is visible
      And I slide all the way right to unlock
      And the clock screen is visible
      And I perform click on clock screen
      And I navigate to cavity selection screen

    @controllock
    Scenario Outline: Test 3 : Validate the flow of Control lock for running screen
      And I click and navigate to cavity selection screen
      And I perform click on upper cavity btn
      And I see tumbler screen
      Then I scroll tumbler to the "<index1>" and click
      Then  I see Instruction screen
      Then I click on next button on recipe instructions selection screen
      Then I click on start button
      And I see Set lower oven button
      And I click on Set lower oven button
      Then I scroll tumbler to the "<index2>" and click
      Then I click on next button on recipe instructions selection screen
      Then I click on start button
      Then I see status screen with both cavity running
      And Settings screen has started
      And I navigate to settings screen
      And I click on control lock
      And I validate the control lock popup
      And I click on continue
      And I see control lock status screen with both cavity running
      Then I click on set cook time
      And The sliding bar to unlock is visible
      And I slide all the way right to unlock
      And I see status screen with both cavity running
      Then I click on set cook time
      And I see cooktime numpad
      Examples:
        | index1 | index2 |
        | 3      | 2      |


  @controllock
  Scenario Outline: Test 4 : Validate the flow of Control lock for running screen via left knob
    And I click and navigate to cavity selection screen
    And I perform click on upper cavity btn
    And I see tumbler screen
    Then I scroll tumbler to the "<index1>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I click on start button
    And I see Set lower oven button
    And I click on Set lower oven button
    Then I scroll tumbler to the "<index2>" and click
    Then I click on next button on recipe instructions selection screen
    Then I click on start button
    Then I see status screen with both cavity running
    Then I rotate left knob clockwise
    Then I rotate left knob clockwise
    Then I rotate left knob clockwise
    And I click left knob
    And I click on control lock
    And I validate the control lock popup
    And I click on continue
    And I see control lock status screen with both cavity running
    Then I click on set cook time
    And The sliding bar to unlock is visible
    And I slide all the way right to unlock
    And I see status screen with both cavity running
    Then I click on set cook time
    And I see cooktime numpad
    Examples:
      | index1 | index2 |
      | 3      | 2      |


  @controllock
  Scenario Outline: Test 5 : To verify cancel in control lock for both idle and running screen
    And Settings screen has started
    And I navigate to settings screen
    And I click on control lock
    And I click on cancel
#    And The preference screen is visible
    And I click on Left Icon Single Header
    And the clock screen is visible
    And I perform click on clock screen
    And I navigate to cavity selection screen
    And I perform click on upper cavity btn
    And I see tumbler screen
    Then I scroll tumbler to the "<index1>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I click on start button
    And I see Set lower oven button
    And I click on Set lower oven button
    Then I scroll tumbler to the "<index2>" and click
    Then I click on next button on recipe instructions selection screen
    Then I click on start button
    Then I see status screen with both cavity running
    And Settings screen has started
    And I navigate to settings screen
    And I click on control lock
    And I click on cancel
#    And The preference screen is visible
    And I click on Left Icon Single Header
    Then I see status screen with both cavity running
    Examples:
      | index1 | index2 |
      | 3      | 2      |




