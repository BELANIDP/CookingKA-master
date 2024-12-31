Feature: Manual mode automation for Single Oven

  Background:
    Given The app has started with Single low end Variant
    And I click and navigate to cavity selection screen
    And I see tumbler screen

  @singleManualMode @recipeSelection @PrimaryCavity
  Scenario Outline: Test 1: Recipe selection screen validation for Primary Cavity single oven
    Then I see tumbler screen
    Then I verify that the string tumbler item title text size is proper
    Then I verify String tumbler height is proper
    Then I verify String tumbler width is proper
    Then I scroll tumbler to "<index1>"
    Then I scroll tumbler to the "<index2>" and click
    Examples:
      | index1 | index2 |
      | 3      | 4      |

#-----------------------Instruction Screen-------------------------#
  @singleManualMode @PrimaryCavity @instructionScreen
  Scenario Outline: Test 1: I validate Instruction screen for recipe for Primary Cavity single oven
    Then I scroll tumbler to "<index>" and click
    Then  I see Instruction screen
    Then I validate the text size for the title of the Instruction screen
    Then I validate the text size for the description of the Instruction screen
    Examples:
      | index |
      | 3     |

  @singleManualMode @PrimaryCavity @instructionScreen
  Scenario Outline: Test 2: I validate Instruction screen back button for recipe for Primary Cavity single oven
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on back button on instruction screen
    Then I see tumbler screen
    Examples:
      | index |
      | Broil |

#-----------------------Broil Cycle-------------------------#
  @singleManualMode @PrimaryCavity @Broil
  Scenario Outline: Test 1: I validate back button on horizontal tumbler screen for Primary Cavity single oven
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on back button on horizontal tumbler screen
    And I see tumbler screen
    Examples:
      | index |
      | Broil |

  @singleManualMode @PrimaryCavity @Broil
  Scenario Outline: Test 2: I validate Status screen for Broil Primary Cavity single oven
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I scroll numberic tumbler to "<High>"
    Then I click on start button
    Then I see status screen with upper running
    Examples:
      | index | High |
      | Broil | 2    |

  #---------------------Status Screen----------------------------#

  @singleManualMode @PrimaryStatus
  Scenario Outline: Test 1: I validate More Options single oven
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on start button
    Then I see status screen with upper running
    Then I see status screen
    Then I see the recipe name on status screen
    Then I validate the recipe name text size on status screen
    Then I validate the recipe name text color on status screen
    Then I click on three dots icon
    And I see more options popup
    Examples:
      | index |
      | Broil |

  @singleManualMode @PrimaryStatus
  Scenario Outline: Test 2: I validate Status screen Set Cook Time for Primary Cavity single oven
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on start button
    Then I see status screen with upper running
    Then I click on Set Cook Time button
    Then I see cooktime numpad
    Examples:
      | index |
      | Broil |


  @singleManualMode @PrimaryStatus
  Scenario Outline: Test 3: I validate Status screen Turn OFf for Primary Cavity single oven
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on start button
    Then I see status screen with upper running
    Then I click on Turn OFF button
    And I expect clock screen should be visible
    Examples:
      | index |
      | Broil |

#----------------------------------More Options Popup-------------------------------#
  @singleManualMode @moreOptions
  Scenario Outline: Test 1: More options pop visible for status screen for single oven
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on start button
    Then I see status screen with upper running
    Then I click on three dots icon
    And I see more options popup
    And I validate title text for more options popup
    And I validate title text size for more options popup
    And I validate title text color for more options popup
    And I click on Change Temperature button
    Examples:
      | index |
      | Broil |

  @singleManualMode @moreOptions
  Scenario Outline: Test 2: More options pop change cooktime level validation for single oven
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on start button
    Then I see status screen with upper running
    Then I click on three dots icon
    And I see more options popup
    And I click on Change Cooktime button
    And I see cooktime numpad
    Examples:
      | index |
      | Broil |

  @singleManualMode @moreOptions
  Scenario Outline: Test 3: More options pop set as fav validation for single oven
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on start button
    Then I see status screen with upper running
    Then I click on three dots icon
    And I see more options popup
    And I click on Set as fav button
    Examples:
      | index |
      | Broil |

  @singleManualMode @moreOptions
  Scenario Outline: Test 4: More options pop view instrunctions validation for single oven
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on start button
    Then I see status screen with upper running
    Then I click on three dots icon
    And I see more options popup
    And I click on View Instructions button
    And I see instruction popup
    And I click on Ok button on instruction popup
    And I see status screen
    Examples:
      | index |
      | Broil |