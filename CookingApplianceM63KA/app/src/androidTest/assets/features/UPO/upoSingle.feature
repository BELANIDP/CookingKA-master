Feature: Cooking - UPO flows for single oven
  Test UPO related features for the single oven

  Background:
    Given The app has started with Single low end Variant
    And Settings screen has started
    And I navigate to settings screen

  @upoSingle
  Scenario: Test 1 : Navigate to Preference option
    And I click on Show More in Preference option
    And I check the preference options

  @upoSingle
  Scenario: Test 2 : Temp calibration option
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    And I see temp calibration tumbler

  @upoSingle
  Scenario: Test 3 : Temp Calibration screen validation
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    And I see temp calibration tumbler
    And I validate the title text of tumbler
    Then I validate the sub text of tumbler

  @upoSingle
  Scenario: Test 4 : Temp Calibration screen back button validation
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    And I see temp calibration tumbler
    And I click on back button on temp calibration screen
    And I check the preference options

  @upoSingle
  Scenario: Test 5 : Temp Calibration screen set button validation
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    And I see temp calibration tumbler
    And I scroll the temp calibration tumbler to required calibration
    And I click on Set button on temp calibration screen
    And I check the preference options

  @upoSingle
  Scenario: Test 6 : Temp Calibration validation for upper cavity
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    And I see temp calibration tumbler
    And I scroll the temp calibration tumbler to required calibration
    Then I click on Set button on temp calibration screen
    And I check the preference options


#  @upoSingle
#  Scenario Outline: Test 12: I validate target temp is adjusted according to offset
#    Then I click on back button on settings screen
#    And I click and navigate to cavity selection screen
#    And I perform click on upper cavity btn
#    Then I scroll tumbler to targetText "<index>" and click
#    Then I see Instruction screen
#    Then I click on next button on recipe instructions selection screen
#    Then I click on start button
#    Then I see status screen with upper running
#    Then I check target temp is adjusted acc to offset
#    Examples:
#      | index |
#      | Bake  |