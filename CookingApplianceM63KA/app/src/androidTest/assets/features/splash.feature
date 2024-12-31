Feature: Splash
  Test splash screen

  Background:
    Given App has started
    And I navigate to splash screen

  @splash @success
  Scenario: Test 1 : Launch app to splash screen
    Then Splash screen will be visible

  @splash @success
  Scenario: Test 2 : Splash screen from video play to clock screen
    Then Splash screen video will be played
    Then I navigate to clock screen

  @splash @success
  Scenario: Test 3 : Splash screen nav destination will be clock screen
    Then Splash screen video will be played
    Then Nav destination will be clock screen

  @splash @failed
  Scenario: Test 4 : Splash screen nav destination will not be clock screen
    Then Splash screen video will be played
    Then Nav destination will not be clock screen