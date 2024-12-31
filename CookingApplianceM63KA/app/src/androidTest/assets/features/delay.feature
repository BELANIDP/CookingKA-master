Feature: Delay Feature

  Background:
    Given App has started

  @delay
  Scenario Outline: Test 1 : Validate the delay flow in self clean
    And Settings screen has started
    And I navigate to settings screen
    And I click on self clean
    And I select the "<cavity>" as given variant
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    Then I check door has opened closed screen delay "<button>" text
    Then I check door has opened closed screen delay button text color
    Then I check door has opened closed screen delay button text size
    Then I check door has opened closed screen delay button view is enabled
    Then I check door has opened closed screen delay button view is clickable
    And I click on delay button on door has opened closed screen
    Then I expect it should navigate to start after delay screen
    Then I check delay tumbler screen content for self clean
    Then I check oven icon matches the selected "<cavity>"
    Examples:
      | cavity | soilLevel | button |
      | upper  | low       | DELAY  |


  @delay
  Scenario Outline: Test 2 : Validate the button clicks on delay flow in self clean
    And Settings screen has started
    And I navigate to settings screen
    And I click on self clean
    And I select the "<cavity>" as given variant
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    And I click on delay button on door has opened closed screen
    Then I expect it should navigate to start after delay screen
    Then I click on back button on doneness tumbler
    Then I expect it should navigate to door hasn't opened closed screen
    And I click on delay button on door has opened closed screen
    Then I click on Start delay button
    Then I see delayed until screen for self clean
    Examples:
      | cavity | soilLevel |
      | upper  | low       |


  @delay
  Scenario Outline: Test 3 : Validate delayed until screen in self clean flow
    And Settings screen has started
    And I navigate to settings screen
    And I click on self clean
    And I select the "<cavity>" as given variant
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    And I click on delay button on door has opened closed screen
    Then I expect it should navigate to start after delay screen
    Then I click on Start delay button
    Then I see delayed until screen for self clean
    Then I validate delay running screen
    Examples:
      | cavity | soilLevel |
      | upper  | low       |

    @delay
    Scenario Outline: test 4 : Validate delay flow for manual modes
      And I click and navigate to cavity selection screen
      And I perform click on upper cavity btn
      And I see tumbler screen
      Then I scroll tumbler to targetText "<index>" and click
      Then I see Instruction screen
      Then I click on next button on recipe instructions selection screen
      Then I click on delay button on horizontal tumbler
      Then I expect it should navigate to start after delay screen
      Examples:
        | index |
        | Bake  |

  @delay
    Scenario Outline: test 5 : Validate delay screen for manual modes
      And I click and navigate to cavity selection screen
      And I perform click on upper cavity btn
      And I see tumbler screen
      Then I scroll tumbler to targetText "<index>" and click
      Then I see Instruction screen
      Then I click on next button on recipe instructions selection screen
      Then I click on delay button on horizontal tumbler
      Then I expect it should navigate to start after delay screen
      Then I check delay tumbler screen content for self clean
      Examples:
        | index |cavity|
        | Bake  |upper |

  @delay
    Scenario Outline: test 6 : Validate delay running screen for manual modes
      And I click and navigate to cavity selection screen
      And I perform click on upper cavity btn
      And I see tumbler screen
      Then I scroll tumbler to targetText "<index>" and click
      Then I see Instruction screen
      Then I click on next button on recipe instructions selection screen
      Then I click on delay button on horizontal tumbler
      Then I expect it should navigate to start after delay screen
      Then I click on Start delay button
      Then I validate delay running screen for manual modes
      Examples:
        | index |
        | Bake  |


