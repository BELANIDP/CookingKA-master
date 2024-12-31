Feature: ListItem
  Test List Item View screen

  Background:
    Given App has started
    And I navigate to List View

  @listItemView @success
  Scenario: Test 1 : Navigate to listItemView
    Then List View Screen will be visible

  @listItemView @success
  Scenario: Test 2 : Check if first item is exist
    Then I verify that the content of first item is exist

  @listItemView @success
  Scenario: Test 3 : Check if item has visible title text
    Then I verify that the item has visible title text

  @listItemView @success
  Scenario: Test 4 : Verify if the title text of the item is hidden
    Then I verify that the title text of the item is hidden


  @listItemView @success
  Scenario: Test 5 : Check if item has visible sub text
    Then I verify that the item has visible sub text


  @listItemView @success
  Scenario: Test 6 : Verify if the sub text of the item is hidden
    Then I verify that the title sub of the item is hidden

  @listItemView @success
  Scenario: Test 7 : Check if item has visible right text
    Then I verify that the item has visible right text


  @listItemView @success
  Scenario: Test 8 : Verify if the right text of the item is hidden
    Then I verify that the title right of the item is hidden

  @listItemView @success
  Scenario: Test 9 : Check if item has visible Radio button
    Then I verify that the item has visible Radio button

  @listItemView @success
  Scenario: Test 10 : Verify if the Radio button of the item is hidden
    Then I verify that the Radio button of the item is hidden

  @listItemView @success
  Scenario: Test 11 : Check if item has visible Image View
    Then I verify that the item has visible icon image view

  @listItemView @success
  Scenario: Test 12 : Verify if the Image view of the item is hidden
    Then I verify that the icon image view of the item is hidden

  @listItemView @success
  Scenario: Test 13 : Check if item has visible right image View
    Then I verify that the item has visible right icon image view

  @listItemView @success
  Scenario: Test 14 : Verify if the right image view of the item is hidden
    Then I verify that the right icon image view of the item is hidden

  @listItemView @success
  Scenario: Test 15 : Verify if the radio button is enabled
    Then I verify that the radio button is enabled

  @listItemView @success
  Scenario: Test 16 : Verify if the radio button is disabled
    Then I verify that the radio button is disabled

  @listItemView @success
  Scenario: Test 17 : Verify if the radio button is checked
    Then I verify that the radio button is checked in list item

  @listItemView @success
  Scenario: Test 18 : Verify if the radio button is unchecked
    Then I verify that the radio button is unchecked in list item

  @listItemView @success
  Scenario: Test 19 : Verify if the raw is disable
    Then I verify that the raw is disabled

  @listItemView @success
  Scenario: Test 20 : Verify if the raw is disable and text color is grey
    Then I verify that the raw is disabled and title text color is grey

  @listItemView @success
  Scenario: Test 21 : Verify if title text size
    Then I verify that the text size is proper

  @listItemView @success
  Scenario: Test 22 : Verify if title text size is not proper
    Then I verify that the text size is not proper

  @listItemView @success
  Scenario: Test 23 : Verify if sub text size
    Then I verify that the sub text size is proper

  @listItemView @success
  Scenario: Test 24 : Verify if sub text size is not proper
    Then I verify that the sub text size is not proper

  @listItemView @success
  Scenario: Test 25 : Verify if right text size
    Then I verify that the right text size is proper

  @listItemView @success
  Scenario: Test 26 : Verify if right text size is not proper
    Then I verify that the right text size is not proper

  @listItemView @success
  Scenario: Test 27 : Verify if title text color proper
    Then I verify that the title text color proper

  @listItemView @success
  Scenario: Test 28 : Verify if title text color is not proper
    Then I verify that the title text color is not proper

  @listItemView @success
  Scenario: Test 29 : Verify if sub text color proper
    Then I verify that the sub text color proper

  @listItemView @success
  Scenario: Test 30 : Verify if sub text color is not proper
    Then I verify that the sub text color is not proper

  @listItemView @success
  Scenario: Test 31 : Verify if right text color proper
    Then I verify that the right text color proper

  @listItemView @success
  Scenario: Test 32 : Verify if right text color is not proper
    Then I verify that the right text color is not proper


  @listItemView @success
  Scenario: Test 33 : Verify if padding of main container is proper
    Then I verify that the padding of main container is proper

  @listItemView @success
  Scenario: Test 34 : Verify if padding of main container is not proper
    Then I verify that the padding of main container is not proper
