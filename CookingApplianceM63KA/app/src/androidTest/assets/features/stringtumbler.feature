Feature: String tumbler
  Test Horizontal tumbler View screen

  Background:
    Given App has started
    And I navigate to test widget screen

  @StringTumbler @success
  Scenario: Test 1 : Navigate to String Tumbler Screen
    Then I navigate to String tumbler screen


  @StringTumbler @success
  Scenario: Test 2 : String Tumbler Screen will be open
    Then String tumbler tumbler Screen will be visible

  @StringTumbler @success
  Scenario: Test 3 : verify String tumbler height
    Then I verify String tumbler height is proper

  @StringTumbler @success
  Scenario: Test 4 : verify String tumbler width
    Then I verify String tumbler width is proper

  @StringTumbler @success
  Scenario: Test 5 : Verify if padding of String tumbler container is proper
    Then I verify that the padding of String tumbler container is proper

  @StringTumbler @success
  Scenario: Test 6 : Verify if padding of String tumbler container is not proper
    Then I verify that the padding of String tumbler container is not proper

  @StringTumbler @success
  Scenario: Test 7 : Verify if text size of String tumbler container is proper
    Then I verify that text size of String tumbler container is proper

  @StringTumbler @success
  Scenario: Test 8 : Check if String tumbler scroll left
    Then I verify that the string tumbler scroll left


  @StringTumbler @success
  Scenario: Test 9 : Check if String tumbler scroll right
    Then I verify that the string tumbler scroll right


  @StringTumbler @success
  Scenario: Test 10 : Check if String tumbler first item is exist
    Then I verify that String tumbler first item is exist

  @StringTumbler @success
  Scenario: Test 11 : Check if String tumbler item has visible title text
    Then I verify that the String tumbler item has visible title text

  @StringTumbler @success
  Scenario: Test 12 : Verify if the String tumbler title text of the item is hidden
    Then I verify that the String tumbler title text of the item is hidden


  @StringTumbler @success
  Scenario: Test 13 : Verify if the String tumbler title text size is proper
    Then I verify that the string tumbler item title text size is proper


  @StringTumbler @success
  Scenario: Test 14 : Verify if the String tumbler title text size is not proper
    Then I verify that the string tumbler item title text size is not proper


  @StringTumbler @success
  Scenario: Test 15 : Verify if  String tumbler item text color proper
    Then I verify that the String tumbler item text color proper

  @StringTumbler @success
  Scenario: Test 16 : Verify if  String tumbler item text color is not proper
    Then I verify that the String tumbler item text color is not proper

  @StringTumbler @success
  Scenario: Test 17 : Verify if String tumbler selected item text color is proper
    Then I verify that the String tumbler selected item text color is proper

  @StringTumbler @success
  Scenario: Test 18 : Check if String tumbler item has visible sub text
    Then I verify that the String tumbler item has visible sub text


  @StringTumbler @success
  Scenario: Test 19 : Verify if the String tumbler sub text size is proper
    Then I verify that the string tumbler item sub text size is proper


  @StringTumbler @success
  Scenario: Test 20 : Verify if  String tumbler item sub text color proper
    Then I verify that the String tumbler item sub text color proper


  @HorizontalTumbler @success
  Scenario: Test 21 : Navigate to test widget screen
    Then I navigate to clock screen for string tumbler screen




