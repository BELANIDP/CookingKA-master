Feature: Keyboard widget test

  The keyboard widget fragment that consist of
  keyboard widget view is visible or not
  The keyboard widget view is visible or not.
  The key pressed on Keyboard widget matches the
  text on the text view.

  @keyboard
  Scenario Outline: navigate to keyboard widget test fragment
    Given App started with KeyboardTestFragment
    And Keyboard widget view is visible
    Then I type a <word>
    Examples:
    | word  |
    | android |