Feature: Toggle
  Test toggle widget

  Background:
    Given App has started
    Then I navigate to clock screen for Toggle widget screen
    And I navigate to Toggle widget screen

  @toggle @success
  Scenario: Test 1 : Launch app to toggle screen
    Then Toggle widget screen will be visible

  @toggle @success
  Scenario: Test 2 : Check toggle Button is checked
    Then Verify in toggle widget screen that the toggle button is checked

  @toggle @success
  Scenario: Test 3 : Check toggle Button is unchecked
    Then Verify in toggle widget screen that the toggle button is unchecked

  @toggle @success
  Scenario: Test 4 : Check toggle Button is enable
    Then Verify that the toggle button is enable

  @toggle @success
  Scenario: Test 5 : Check toggle Button is disable
    Then Verify that the toggle button is disable

  @toggle @success
  Scenario: Test 6 : Verify the toggle button size matched
    Then Verify the toggle button size matched

  @toggle @success
  Scenario: Test 7 : Check toggle Button size not match
    Then Verify the toggle button size not matched