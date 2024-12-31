Feature: ScrollViewPager
  Test ScrollViewPager widget

  Background:
    Given App has started
    Then I navigate to clock screen for scroll view screen
    And I navigate to scroll view screen

  @scrollViewPager @success
  Scenario: Test 1 : Launch app to scrollview screen
    Then Scroll view screen will be visible

  @scrollViewPager @success
  Scenario: Test 2 : Check is vertical scroll is not enabled
    Then I checked that vertical scroll is not enabled

  @scrollViewPager @success
  Scenario: Test 3 : Check is vertical scroll is enabled after touching
    Then I checked that vertical scroll is enabled

  @scrollViewPager @success
  Scenario: Test 4 : Check if scrolling downwards works
    When I scroll to the bottom
    Then I verify that the content is at the bottom

  @scrollViewPager @success
  Scenario: Test 5 : Check if scrolling upwards works
    When I scroll to the top
    Then I verify that the content is at the top

  @scrollViewPager @success
  Scenario: Test 6 : Checked description text view alignment matched
    Then I checked the description text view alignment

  @scrollViewPager @success
  Scenario: Test 7 : Checked description text view alignment not matched
    Then I checked the description text view alignment not matched

  @scrollViewPager @success
  Scenario: Test 8 : Checked description text view size
    Then I checked the description text view size

  @scrollViewPager @success
  Scenario: Test 9 : Checked description text view size not matched
    Then I checked the description text view size not matched

  @scrollViewPager @success
  Scenario: Test 10 : Checked description text view color
    Then I checked the description text view color

  @scrollViewPager @success
  Scenario: Test 11 : Checked description text view color not matched
    Then I checked the description text view color not matched