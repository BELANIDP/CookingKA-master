Feature: Cooking - UPO flows for double oven
  Test UPO related features for the double oven

  Background:
    Given The app has started with Double low end Variant
    And Settings screen has started
    And I navigate to settings screen

  @upoDouble
  Scenario: Test 1 : Navigate to Preference option
    And I click on Show More in Preference option
    And I check the preference options

  @upoDouble
  Scenario: Test 2 : Temp calibration option
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    Then I see cavity selection screen

  @upoDouble
  Scenario: Test 3 : Cavity selection
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    Then I see cavity selection screen

  @upoDouble
  Scenario: Test 4 : Cavity name and default subtext validation
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    Then I see cavity selection screen
    And I perform click on upper cavity btn
    And I see temp calibration tumbler
    And I scroll the temp calibration tumbler to required calibration
    Then I click on Set button on temp calibration screen
    And I perform click on upper cavity btn
    And I see temp calibration tumbler
    And I scroll the temp calibration tumbler to default temp
    Then I click on Set button on temp calibration screen
    And I see cavity selection screen
    And I validate the cavity name and subtext as default

  @upoDouble
  Scenario: Test 5 : Back button validation
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    Then I see cavity selection screen
    And I click on back button on cavity selection screen
    And I check the preference options

  @upoDouble
  Scenario: Test 6 : cavity selection buttons validation
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    Then I see cavity selection screen
    And I validate the height and width of cavity selection buttons

  @upoDouble
  Scenario: Test 7 : Temp Calibration screen validation
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    Then I see cavity selection screen
    And I perform click on upper cavity btn
    And I see temp calibration tumbler
    And I validate the title text of tumbler
    Then I validate the sub text of tumbler

  @upoDouble
  Scenario: Test 8 : Temp Calibration screen back button validation
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    Then I see cavity selection screen
    And I perform click on upper cavity btn
    And I see temp calibration tumbler
    And I click on back button on temp calibration screen
    And I see cavity selection screen

  @upoDouble
  Scenario: Test 9 : Temp Calibration screen set button validation
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    Then I see cavity selection screen
    And I perform click on upper cavity btn
    And I see temp calibration tumbler
    And I scroll the temp calibration tumbler to required calibration
    And I click on Set button on temp calibration screen
    And I see cavity selection screen

  @upoDouble
  Scenario: Test 10 : Temp Calibration validation for upper cavity
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    Then I see cavity selection screen
    And I perform click on upper cavity btn
    And I see temp calibration tumbler
    And I scroll the temp calibration tumbler to required calibration
    Then I click on Set button on temp calibration screen
    And I see cavity selection screen
    And I validate that subtext on upper oven button is modified

  @upoDouble
  Scenario: Test 11 : Temp Calibration validation for lower cavity
    And I click on Show More in Preference option
    And I check the preference options
    And I click on Temp calibration option
    Then I see cavity selection screen
    And I perform click on lower oven btn
    And I see temp calibration tumbler
    And I scroll the temp calibration tumbler to required calibration
    Then I click on Set button on temp calibration screen
    And I see cavity selection screen
    And I validate that subtext on lower oven button is modified


#  @upoDouble1
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