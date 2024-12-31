Feature: Manual mode automation for MWO

  Background:
    Given The app has started with Microwave low end Variant
    And I perform click on clock screen
    Then I see tumbler screen

#--------------------Recipe Selection Tumbler-----------------------#
  @mwoManualMode @recipeSelection
  Scenario Outline: Test 1: Recipe selection screen validation for mwo
    Then I verify that the string tumbler item title text size is proper
    Then I verify String tumbler height is proper
    Then I verify String tumbler width is proper
    Then I scroll tumbler to "<index>"
    Then I scroll tumbler to "<index>" and click
    Examples:
      | index |
      | 3     |

#-----------------------Instruction Screen-------------------------#
  @mwoManualMode @instructionScreen
  Scenario Outline: Test 1: I validate Instruction screen for recipe for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I validate the text size for the title of the Instruction screen
    Then I validate the text size for the description of the Instruction screen
    Then I click on back button on instruction screen
    Then I see tumbler screen
    Examples:
      | index      |
      | Steam Cook |

#-----------------------Numpad Screen-------------------------#
  @mwoManualMode @numpadScreen
  Scenario Outline: Test 1: I validate back button on numpad for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on back button on numpad screen
    And I see recipe screen
    Examples:
      | index      |
      | Steam Cook |

  @mwoManualMode @numpadScreen
  Scenario Outline: Test 2: I validate delete button on numpad is clickable for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on delete button on numpad screen
    Examples:
      | index      |
      | Steam Cook |

  @mwoManualMode @numpadScreen
  Scenario Outline: Test 3: I validate tumbler button on numpad for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    And I see vertical tumbler
    Examples:
      | index      |
      | Steam Cook |

  @mwoManualMode @numpadScreen
  Scenario Outline: Test 4: I set cooktime via numpad for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see door open close popup
    Examples:
      | index      | cookTime |
      | Steam Cook | 30       |

#-------------------Vertical Tumbler Screen--------------#

  @mwoManualMode @verticalTumblerScreen
  Scenario Outline: Test 1: I validate back button on vertical tumbler for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I click on back button on vertical tumbler screen
    And I see recipe screen
    Examples:
      | index      |
      | Steam Cook |

  @mwoManualMode @verticalTumblerScreen
  Scenario Outline: Test 2: I validate numpad button on vertical tumbler for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I click on numpad button on vertical tumbler screen
    And I see numpad
    Examples:
      | index      |
      | Steam Cook |

  @mwoManualMode @verticalTumblerScreen
  Scenario Outline: Test 3: I see door open close popup for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    Examples:
      | index      | cookTime |
      | Steam Cook | 30       |


#-----------------------------------Prepare Mwo Popup---------------------------#

  @mwoManualMode @prepareMwoPopup
  Scenario Outline: Test 1: I validate on prepare Mwo popup for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I validate title text on prepare Mwo popup
    And I validate size of title text on prepare Mwo popup
    And I validate title text color on prepare Mwo popup
    And I validate description text on prepare Mwo popup
    And I validate size of description text on prepare Mwo popup
    And I validate description text color on prepare Mwo popup
    Examples:
      | index      | cookTime |
      | Steam Cook | 30       |

    #-----------------------------------Door open and close popup---------------------------#

  @mwoManualMode @doorOpenClosePopupMwo
  Scenario Outline: Test 1: I validate on door open and close popup for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open and close the door of "<cavity>" for mwo manual mode
    And I validate title text on door open and close popup
    And I validate size of title text on door open and close popup
    And I validate color of title text on door open and close popup
    And I validate description text on door open and close popup
    And I validate size of description text on door open and close popup
    And I validate color of description text on door open and close popup
    Examples:
      | index      | cookTime | cavity |
      | Steam Cook | 30       | upper  |

  @mwoManualMode @doorOpenClosePopupMwo
  Scenario Outline: Test 2: I validate start button is disabled on door open and close popup for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open the door of "<cavity>" for mwo manual mode
    And I see that Start button is disabled
    Examples:
      | index      | cookTime | cavity |
      | Steam Cook | 30       | upper  |

  @mwoManualMode @doorOpenClosePopupMwo
  Scenario Outline: Test 3: I validate start button is enabled on door open and close popup for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open and close the door of "<cavity>" for mwo manual mode
    And I see that Start button is enabled
    And I validate Start button is clickable
    Examples:
      | index      | cookTime | cavity |
      | Steam Cook | 30       | upper  |

  @mwoManualMode @doorOpenClosePopupMwo
  Scenario Outline: Test 4: I validate start button on door open and close popup for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open and close the door of "<cavity>" for mwo manual mode
    And I validate text of Start button
    And I validate text size of Start button
    And I validate text color of Start button
    Examples:
      | index      | cookTime | cavity |
      | Steam Cook | 30       | upper  |

#---------------------Status Screen----------------------------#

  @mwoManualMode @statusScreen
  Scenario Outline: Test 1: I validate status screen for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open and close the door of "<cavity>" for mwo manual mode
    Then I click on Start button on the door open close popup
    Then I see status screen
    Then I see the recipe name on status screen
    Then I validate the recipe name text size on status screen
    Then I validate the recipe name text color on status screen
    Then I validate the remaining time visible on status screen
    Then I click on three dots icon
    And I see more options popup
    Examples:
      | index      | cookTime | cavity |
      | Steam Cook | 30       | upper  |

  @mwoManualMode @statusScreen
  Scenario Outline: Test 2: +5 min button working for status screen for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open and close the door of "<cavity>" for mwo manual mode
    Then I click on Start button on the door open close popup
    Then I see status screen
    Then I click on +5 min button
    Examples:
      | index      | cookTime | cavity |
      | Steam Cook | 30       | upper  |

  @mwoManualMode @statusScreen
  Scenario Outline: Test 3: Turn Off button working for status screen for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open and close the door of "<cavity>" for mwo manual mode
    Then I click on Start button on the door open close popup
    Then I see status screen
    Then I click on Turn OFF button
    And I expect clock screen should be visible
    Examples:
      | index      | cookTime | cavity |
      | Steam Cook | 30       | upper  |

#----------------------------------More Options Popup-------------------------------#
  @mwoManualMode @moreOptions
  Scenario Outline: Test 1: More options pop visible for status screen for mwo
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open and close the door of "<cavity>" for mwo manual mode
    Then I click on Start button on the door open close popup
    Then I see status screen
    Then I click on three dots icon
    And I see more options popup
    And I validate title text for more options popup
    And I validate title text size for more options popup
    And I validate title text color for more options popup
    Examples:
      | index      | cookTime | cavity |
      | Steam Cook | 30       | upper  |

