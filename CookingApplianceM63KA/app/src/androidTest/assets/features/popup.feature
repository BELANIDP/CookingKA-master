Feature: Popup
  Test popup screen

  Background:
    Given App has started for header screen
    And I navigate popup testing button screen

  @popup @success
  Scenario: Test 1 : Perform any action to first button of Popup
    Then I click on first button

  @popup @success
  Scenario: Test 2 : Popup should be open with Title
    Then I click on first button
    Then I expect that popup should be visible with Title

  @popup @success
  Scenario: Test 3 : Popup should be open with Image
    Then I click on first button
    Then I expect that popup should be visible with Image

  @popup @success
  Scenario: Test 4 : Popup should be open with scroll
    Then I click on first button
    Then I expect that popup should be visible with scroll

  @popup @success
  Scenario: Test 5 : Popup should be open with Description
    Then I click on first button
    Then I expect that popup should be visible with Description

  @popup @success
  Scenario: Test 6 : Checked Title size
    Then I click on first button
    Then I checked the Title text size

  @popup @success
  Scenario: Test 7 : Checked Description size
    Then I click on first button
    Then I checked the Description text size

  @popup @success
  Scenario: Test 8 : checked Left Side Button
    Then I click on first button
    Then I expect that popup should be visible with Left Side Button

  @popup @success
  Scenario: Test 9 : checked Left Button click
    Then I click on first button
    Then I click on Left Side button
    Then I expect that popup should be dismiss on Left Side Button click

  @popup @success
  Scenario: Test 10 : checked Right Side Button
    Then I click on first button
    Then I expect that popup should be visible with Right Side Button

  @popup @success
  Scenario: Test 11 : checked Right Side Button click
    Then I click on first button
    Then I click on Right Side button
    Then I expect that popup should be dismiss on Right Side Button click

  @popup @success
  Scenario: Test 12 : Perform any action to second button of Popup
    Then I click on second button

  @popup @success
  Scenario: Test 13 : Popup should be open with Title
    Then I click on second button
    Then I expect that popup should be visible with Title

  @popup @success
  Scenario: Test 14 : Popup should be open with Image
    Then I click on second button
    Then I expect that popup should be visible with Image

  @popup @success
  Scenario: Test 15 : Popup should be open with scroll
    Then I click on second button
    Then I expect that popup should be visible with scroll

  @popup @success
  Scenario: Test 16 : Popup should be open with Description
    Then I click on second button
    Then I expect that popup should be visible with Description

  @popup @success
  Scenario: Test 17 : Checked Title size
    Then I click on second button
    Then I checked the Title text size

  @popup @success
  Scenario: Test 18 : Checked Description size
    Then I click on second button
    Then I checked the Description text size

  @popup @success
  Scenario: Test 19 : checked Left Side Button
    Then I click on second button
    Then I expect that popup should be visible with Left Side Button

  @popup @success
  Scenario: Test 20 : checked Left Button click
    Then I click on second button
    Then I click on Left Side button
    Then I expect that popup should be dismiss on Left Side Button click

  @popup @success
  Scenario: Test 21 : checked Right Side Button
    Then I click on second button
    Then I expect that popup should be visible with Right Side Button

  @popup @success
  Scenario: Test 22 : checked Right Side Button click
    Then I click on second button
    Then I click on Right Side button
    Then I expect that popup should be dismiss on Right Side Button click

  @popup @success
  Scenario: Test 23 : Perform any action to first button of Popup
    Then I click on third button

  @popup @success
  Scenario: Test 24 : Popup should be open with Notification
    Then I click on third button
    Then I expect that popup should be visible with Notification

  @popup @success
  Scenario: Test 25 : Checked Notification size
    Then I click on third button
    Then I checked the Notification text size

  @popup @success
  Scenario: Test 26 : Popup should be open with ProgressBar
    Then I click on third button
    Then I expect that popup should be visible with ProgressBar

  @popup @success
  Scenario: Test 27 : Popup should be open with Title
    Then I click on third button
    Then I expect that popup should be visible with Title

  @popup @success
  Scenario: Test 28 : Popup should be open with Image
    Then I click on third button
    Then I expect that popup should be visible with Image

  @popup @success
  Scenario: Test 29 : Perform any action to first button of Popup
    Then I click on fourth button

  @popup @success
  Scenario: Test 30 : Popup should be open with Title
    Then I click on fourth button
    Then I expect that popup should be visible with Title

  @popup @success
  Scenario: Test 31 : Popup should be open with Image
    Then I click on fourth button
    Then I expect that popup should be visible with Image


  @popup @success
  Scenario: Test 32 : Popup should be open with scroll
    Then I click on fourth button
    Then I expect that popup should be visible with scroll

  @popup @success
  Scenario: Test 33 : Popup should be open with Description
    Then I click on fourth button
    Then I expect that popup should be visible with Description

  @popup @success
  Scenario: Test 34 : Checked Title size
    Then I click on first button
    Then I checked the Title text size

  @popup @success
  Scenario: Test 35 : Checked Description size
    Then I click on first button
    Then I checked the Description text size

  @popup @success
  Scenario: Test 36 : checked Right Side Button click
    Then I click on fourth button
    Then I click on Right Side button
    Then I expect that popup should be dismiss on Right Side Button click
