Feature: Manual mode automation for MWO in Combo

  Background:
    Given The app has started with Combo low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on microwave btn
    And I see tumbler screen

#--------------------Cavity Selection Screen-----------------#

#  @mwoComboManualMode @cavitySelection
#  Scenario Outline: Test 1: Cavity selection screen verification for combo oven
#    Given The app has started with Combo low end Variant
#    When I click and navigate to cavity selection screen
#    And I verify "<cavity>" button text name, "<cavity text color>" button text color, "<cavity text size>" button text size, "<cavity btn image resource>" button image resource for combo
#    Examples:
#      | cavity         | cavity text color | cavity text size | cavity btn image resource |
#      | Set Microwave | #ffffff           | 36               | ic_oven_cavity            |
#      | Set Lower Oven | #ffffff           | 36               | ic_lower_cavity           |
#
#  @mwoComboManualMode @cavitySelection
#  Scenario: Test 2: Cavity selection screen click event for combo oven
#    Given The app has started with Combo low end Variant
#    When I click and navigate to cavity selection screen
#    And I perform click on microwave btn



#--------------------Recipe Selection Tumbler-----------------------#
  @mwoComboManualMode @recipeSelection
  Scenario Outline: Test 1: Recipe selection screen validation for combo oven
    Then I see tumbler screen
    Then I verify that the string tumbler item title text size is proper
    Then I verify String tumbler height is proper
    Then I verify String tumbler width is proper
    Then I scroll tumbler to "<index>"
    Then I scroll tumbler to "<index>" and click
    Examples:
      | index |
      | 3     |

##-----------------------Instruction Screen-------------------------#
#  @mwoComboManualMode1 @instructionScreen
#  Scenario Outline: Test 1: I validate Instruction screen title text size for recipe for combo oven
#    Then I scroll tumbler to "<index>" and click
#
#    Then I validate the text size for the title of the Instruction screen
#    Examples:
#      | index |
#      | 1     |
#
#  @mwoComboManualMode @instructionScreen
#  Scenario Outline: Test 2: I validate Instruction screen description text size for recipe for combo oven
#    Then I scroll tumbler to "<index>" and click
#
#    Then I validate the text size for the description of the Instruction screen
#    Examples:
#      | index |
#      | 1     |
#
#  @mwoComboManualMode @instructionScreen
#  Scenario Outline: Test 3: I validate Instruction screen back button for recipe for combo oven
#    Then I scroll tumbler to "<index>" and click
#
#    Then I click on back button on instruction screen
#    Then I see tumbler screen
#    Examples:
#      | index |
#      | 1     |

#-----------------------Numpad Screen-------------------------#
  @mwoComboManualMode @numpadScreen
  Scenario Outline: Test 1: I validate back button on numpad for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on back button on numpad screen
    And I see recipe screen
    Examples:
      | index |
      | 1     |

  @mwoComboManualMode @numpadScreen
  Scenario Outline: Test 2: I validate delete button on numpad is clickable for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on delete button on numpad screen
    Examples:
      | index |
      | 1     |

  @mwoComboManualMode @numpadScreen
  Scenario Outline: Test 3: I validate tumbler button on numpad for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    And I see vertical tumbler
    Examples:
      | index |
      | 1     |

  @mwoComboManualMode @numpadScreen
  Scenario Outline: Test 4: I set cooktime via numpad for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see door open close popup
    Examples:
      | index | cookTime|
      | 1     |    30   |

#-------------------Vertical Tumbler Screen--------------#

  @mwoComboManualMode @verticalTumblerScreen
  Scenario Outline: Test 1: I validate back button on vertical tumbler for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I click on back button on vertical tumbler screen
    And I see tumbler screen
    Examples:
      | index |
      | 1     |

  @mwoComboManualMode @verticalTumblerScreen
  Scenario Outline: Test 2: I validate numpad button on vertical tumbler for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I click on numpad button on vertical tumbler screen
    And I see numpad
    Examples:
      | index |
      | 1     |

  @mwoComboManualMode @verticalTumblerScreen
  Scenario Outline: Test 3: I see door open close popup for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    Examples:
      | index | cookTime|
      | 1     | 30      |


#-----------------------------------Prepare Mwo Popup---------------------------#

  @mwoComboManualMode @prepareMwoPopup
  Scenario Outline: Test 1: I validate Mwo popup for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
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
      | index | cookTime |
      | 1     | 30       |

    #-----------------------------------Door open and close popup---------------------------#

  @mwoComboManualMode @doorOpenClosePopupMwo
  Scenario Outline: Test 1: I validate door open and close popup for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
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
      | index | cookTime | cavity |
      | 1     | 30       | upper  |

  @mwoComboManualMode @doorOpenClosePopupMwo
  Scenario Outline: Test 2: I validate start button is disabled on door open and close popup for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open the door of "<cavity>" for mwo manual mode
    And I see that Start button is disabled
    Examples:
      | index | cookTime| cavity|
      | 1     | 30      | upper |

  @mwoComboManualMode @doorOpenClosePopupMwo
  Scenario Outline: Test 3: I validate start button is enabled on door open and close popup for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open and close the door of "<cavity>" for mwo manual mode
    And I see that Start button is enabled
    Examples:
      | index | cookTime| cavity|
      | 1     | 30      | upper |

  @mwoComboManualMode @doorOpenClosePopupMwo
  Scenario Outline: Test 4: I validate text of start button on door open and close popup for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
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
      | index | cookTime | cavity |
      | 1     | 30       | upper  |

#  @mwoComboManualMode @doorOpenClosePopupMwo
#  Scenario Outline: Test 12: I validate start button is clickable on door open and close popup for combo oven
#    Then I scroll tumbler to "<index>" and click
#
#
#    Then I see numpad
#    Then I click on tumbler icon
#    Then I set the CookTime to "<cookTime>"
#    Then I click the Next Button
#    Then I see door open close popup
#    And I open and close the door of "<cavity>" for mwo manual mode
#    And I validate Start button is clickable
#    Examples:
#      | index | cookTime| cavity|
#      | 1     | 30      | upper |


#---------------------Status Screen----------------------------#

  @mwoComboManualMode @statusScreen
  Scenario Outline: Test 1: I validate status screen for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
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
    Then I see the oven icon besides recipe name
    Then I validate the remaining time visible on status screen
    Then I click on three dots icon
    And I see more options popup
    Examples:
      | index | cookTime | cavity |
      | 1     | 30       | upper  |

  @mwoComboManualMode @statusScreen
  Scenario Outline: Test 2: +5 min button working for status screen for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
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
      | index | cookTime| cavity|
      | 1     | 30      | upper |

  @mwoComboManualMode @statusScreen
  Scenario Outline: Test 3: Turn Off button working for status screen for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
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
      | index | cookTime| cavity|
      | 1     | 30      | upper |

  @mwoComboManualMode @statusScreen
  Scenario Outline: Test 4: Set lower oven button visible on status screen for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see door open close popup
    And I open and close the door of "<cavity>" for mwo manual mode
    Then I click on Start button on the door open close popup
    Then I see status screen
    And I see Set lower oven button
    And I click on Set lower oven button
    And I see tumbler screen
    Examples:
      | index | cookTime | cavity |
      | 1     | 30       | upper  |


#----------------------------------More Options Popup-------------------------------#
  @mwoComboManualMode @moreOptions
  Scenario Outline: Test 1: More options pop validation for status screen for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
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
    And I click on Change Temperature button
    Examples:
      | index | cookTime | cavity |
      | 1     | 30       | upper  |
#
  @mwoComboManualMode @moreOptions
  Scenario Outline: Test 2: More options pop up Change cooktime button clickable for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
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
    And I click on Change Cooktime button in MWO
    And I see cooktime numpad
    Examples:
      | index | cookTime| cavity|
      | 1     | 30      | upper |

  @mwoComboManualMode @moreOptions
  Scenario Outline: Test 3: More options pop up Set as Fav button clickable for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
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
    And I click on Set as fav button
    Examples:
      | index | cookTime| cavity|
      | 1     | 30      | upper |

  @mwoComboManualMode @moreOptions
  Scenario Outline: Test 4: More options pop up view instructions button clickable for combo oven
    Then I scroll tumbler to "<index>" and click
    Then I see Instruction screen
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
    And I click on View Instructions button in MWO
    And I see instruction popup
    And I click on Ok button on instruction popup
    And I see status screen
    Examples:
      | index | cookTime | cavity |
      | 1     | 30       | upper  |

