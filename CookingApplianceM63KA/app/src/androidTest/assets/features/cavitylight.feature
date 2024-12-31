Feature: Cooking - Cavity light flows
  Test Cavity light flow

  @cavitylight @success
  Scenario: Test1: To verify cavity light on flow
    Given App has started
    Then Settings screen has started for cavity light
    Then I check notification view visible
    And I wait for 4 seconds
    Then Settings screen has started for cavity light off
    Then I check off notification view visible
    And I wait for 4 seconds
    Then I open the Lower Oven Door
    Then I close the Lower Oven Door
    Then I open the Upper Oven Door
    Then I close the Upper Oven Door