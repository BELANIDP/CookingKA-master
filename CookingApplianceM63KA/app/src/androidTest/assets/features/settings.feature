Feature: Cooking - Self clean flows
  Test Self clean related features for the Combo

  Background:
    Given Settings screen has started
    And I navigate to settings screen

  @settings @success
  Scenario: Test: To verify the Settings screen
    Then I expect settings screen should visible

  @settings @success
  Scenario: Test: To verify the Settings screen menus
    Then I expect settings screen menu items should visible

  @settings @success
  Scenario: Test: To verify the Settings screen menus Self Clean click event
    And I click on Self Clean
    Then I check Self Clean menu should clickable

  @settings @ignore
  Scenario: Test: To verify Self Clean menu left icon visibility
    Then I expect Self Clean menu left icon should visible

  @settings @ignore
  Scenario: Test: To verify Self Clean menu right arrow icon visibility
    Then I expect Self Clean menu right arrow icon should visible
