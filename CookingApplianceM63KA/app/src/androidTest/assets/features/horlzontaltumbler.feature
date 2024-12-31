Feature: Horizontal tumbler
  Test Horizontal tumbler View screen

  Background:
    Given App has started
    And I navigate to horizontal tumbler test screen

  @HorizontalTumbler @success
  Scenario: Test 1 : Navigate to Horizontal Tumbler base Screen
    Then Horizontal tumbler test Screen will be visible


  @HorizontalTumbler @success
  Scenario: Test 2 :  Navigate to Horizontal Tumbler Screen
    Then Click on tumbler test button

  @HorizontalTumbler @success
  Scenario: Test 3 : Horizontal Tumbler Screen will be open
    Then Horizontal tumbler Screen will be visible

  @HorizontalTumbler @success
  Scenario: Test 4 : verify tumbler height
    Then I verify tumbler height is proper

  @HorizontalTumbler @success
  Scenario: Test 5 : verify tumbler width
    Then I verify tumbler width is proper

  @HorizontalTumbler @success
  Scenario: Test 6 : Verify if padding of tumbler container is proper
    Then I verify that the padding of tumbler container is proper

  @HorizontalTumbler @success
  Scenario: Test 7 : Verify if padding of tumbler container is not proper
    Then I verify that the padding of tumbler container is not proper

  @HorizontalTumbler @success
  Scenario: Test 8 : Verify if text size of tumbler container is proper
    Then I verify that text size of tumbler container is proper

  @HorizontalTumbler @success
  Scenario: Test 9 : Check if tumbler scroll left
    Then I verify that the tumbler scroll left


  @HorizontalTumbler @success
  Scenario: Test 10 : Check if tumbler scroll right
    Then I verify that the tumbler scroll right

  @HorizontalTumbler @success
  Scenario: Test 11 : Check if tumbler first item is exist
    Then I verify that tumbler first item is exist

  @HorizontalTumbler @success
  Scenario: Test 12 : Check if item has visible title text
    Then I verify that the tumbler item has visible title text

  @HorizontalTumbler @success
  Scenario: Test 13 : Verify if the title text of the item is hidden
    Then I verify that the tumbler title text of the item is hidden

  @HorizontalTumbler @success
  Scenario: Test 14 : Verify if  text color proper
    Then I verify that the tumbler item text text color proper

  @HorizontalTumbler @success
  Scenario: Test 15 : Verify if selected item text color is proper
    Then I verify that the tumbler selected item text color is proper