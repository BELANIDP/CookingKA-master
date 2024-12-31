Feature: Radio Button
  Test  Radio Button screen

  Background:
    Given App has started
    Then I navigate to clock screen for radio button Screen
    And I navigate to Radio Button

  @Radio @success
  Scenario: Test 1 : Navigate to Radio Button Screen and Check Visibility
    Then Radio Button Screen will be visible

  @Radio @success
  Scenario: Test 2 : Check Radio Button is checked
    Then I verify in radio button widget that the radio button is checked

  @Radio @success
  Scenario: Test 3 : Check Radio Button is unchecked
    Then I verify in radio button widget that the radio button is unchecked

  @Radio @success
  Scenario: Test 4 : Check Radio Button is enable
    Then I verify that the radio button is enable

  @Radio @success
  Scenario: Test 5 : Check Radio Button is disable
    Then I verify that the radio button is disable