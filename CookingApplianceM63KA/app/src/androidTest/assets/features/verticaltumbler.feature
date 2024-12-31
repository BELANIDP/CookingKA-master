Feature: Vertical tumbler
  Test Horizontal tumbler View screen

  Background:
    Given App has started
    And I navigate to test widget screen for vertical tumbler

  @VerticalTumbler @success
  Scenario: Test 1 : Navigate to vertical Tumbler Screen
    Then I navigate to vertical tumbler screen

  @VerticalTumbler @success
  Scenario: Test 2 : Vertical Tumbler Screen will be open
    Then Vertical tumbler tumbler Screen will be visible

  @VerticalTumbler @success
  Scenario: Test 3 : verify left tumbler height
    Then I verify left tumbler height is proper

  @VerticalTumbler @success
  Scenario: Test 4 : verify left tumbler width
    Then I verify left tumbler width is proper

  @VerticalTumbler @success
  Scenario: Test 5 : Verify if padding of left tumbler container is proper
    Then I verify that the padding of left tumbler container is proper

  @VerticalTumbler @success
  Scenario: Test 6 : Verify if padding of left tumbler container is not proper
    Then I verify that the padding of left tumbler container is not proper

  @VerticalTumbler @success
  Scenario: Test 7 : Check if left tumbler scroll top
    Then I verify that the left tumbler scroll top

  @VerticalTumbler @success
  Scenario: Test 8 : Check if left tumbler scroll bottom
    Then I verify that the left tumbler scroll bottom

  @VerticalTumbler @success
  Scenario: Test 9 : Verify if text size of left tumbler container is proper
    Then I verify that text size of left tumbler container is proper

  @VerticalTumbler @success
  Scenario: Test 10 : Check if left tumbler item has visible title text
    Then I verify that the left tumbler item has visible title text

  @VerticalTumbler @success
  Scenario: Test 11 : Verify if the left tumbler title text of the item is hidden
    Then I verify that the left tumbler title text of the item is hidden

  @VerticalTumbler @success
  Scenario: Test 12 : Verify if the left tumbler title text size is proper
    Then I verify that the left tumbler item title text size is proper

  @VerticalTumbler @success
  Scenario: Test 13 : Verify if the left tumbler title text size is not proper
    Then I verify that the left tumbler item title text size is not proper

  @VerticalTumbler @success
  Scenario: Test 14 : Check if left tumbler first item is exist
    Then I verify that left tumbler first item is exist

  @VerticalTumbler @success
  Scenario: Test 15 : Verify if  left tumbler item text color proper
    Then I verify that the left tumbler item text color proper

  @VerticalTumbler @success
  Scenario: Test 16 : Verify if  left tumbler item text color is not proper
    Then I verify that the left tumbler item text color is not proper

  @VerticalTumbler @success
  Scenario: Test 17 : Verify if left tumbler selected item text color is proper
    Then I verify that the left tumbler selected item text color is proper

  @VerticalTumbler @success
  Scenario: Test 19 : Navigate to test widget screen
    Then I navigate to clock screen for vertical tumbler screen



