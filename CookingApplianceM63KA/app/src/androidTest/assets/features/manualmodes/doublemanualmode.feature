Feature: Manual mode automation for Double Oven

  Background:
    Given The app has started with Double low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on upper cavity btn
    And I see tumbler screen

  @doubleManualMode @recipeSelection @PrimaryCavity
  Scenario Outline: Test 1: Recipe selection screen validation for Primary Cavity double oven
    Then I see tumbler screen
    Then I verify that the string tumbler item title text size is proper
    Then I verify String tumbler height is proper
    Then I verify String tumbler width is proper
    Then I scroll tumbler to "<index>"
    Then I scroll tumbler to the "<index1>" and click
    Examples:
      | index | index1 |
      | 3     | 4      |

#-----------------------Instruction Screen-------------------------#
  @doubleManualMode @PrimaryCavity @instructionScreen
  Scenario Outline: Test 1: I validate Instruction screen and back button navigation for recipe for Primary Cavity double oven
    Then I scroll tumbler to "<index>" and click
    Then  I see Instruction screen
    Then I validate the text size for the title of the Instruction screen
    Then I validate the text size for the description of the Instruction screen
    Then I click on back button on instruction screen
    Then I see tumbler screen
    Examples:
      | index |
      | 3     |

#-----------------------Broil Cycle-------------------------#
  @doubleManualMode @PrimaryCavity @Broil
  Scenario Outline: Test 1: I validate back button on horizontal tumbler screen for Primary Cavity double oven
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on back button on horizontal tumbler screen
    And I see tumbler screen
    Examples:
      | index |
      | Broil |

  @doubleManualMode @PrimaryCavity @Broil
  Scenario Outline: Test 2: I select different level in horizontal tumbler screen for Primary Cavity double oven
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I scroll numberic tumbler to "<High>"
    Examples:
      | index | High |
      | Broil | 2    |

  @doubleManualMode @PrimaryCavity @Broil
  Scenario Outline: Test 3: I validate start button in horizontal tumbler screen for Primary Cavity double oven
    Then I scroll tumbler to targetText "<index>" and click
    Then  I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on start button
    Then I see status screen with upper running
    Examples:
      | index |
      | Broil |

  #---------------------Status Screen----------------------------#

  @doubleManualMode @PrimaryStatus
  Scenario Outline: Test 1: I validate Status screen and More Options for Primary Cavity double oven
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal tumbler
    Then I click on start button
    Then I see status screen with upper running
    Then I see the recipe name on status screen
    Then I validate the recipe name text size on status screen
    Then I validate the recipe name text color on status screen
    Then I see the oven icon besides recipe name
    Then I click on three dots icon
    And I see more options popup
    Examples:
      | index |
      | Broil |

  @doubleManualMode @PrimaryStatus
  Scenario Outline: Test 2: I validate Status screen Set Cook Time for Primary Cavity double oven
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

  @doubleManualMode @PrimaryStatus
  Scenario Outline: Test 3: I validate Status screen Turn OFf for Primary Cavity double oven
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
 #---------------------Status Screen Lower Cavity----------------------------#
  @doubleManualMode @LowerCavity
  Scenario Outline: Test 1: I validate Lower Oven in status screen for double oven
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal temp tumbler
    Then I click on start button
    Then I see status screen with upper running
    And I see Set lower oven button
    And I click on Set lower oven button
    And I see tumbler screen
    Then I scroll tumbler to targetText "<index1>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal temp tumbler
    Then I click on start button
    Then I see status screen with both cavity running
    Examples:
      | index | index1 |
      | Bake  | Bake   |

#----------------------------------More Options Popup-------------------------------#
  @doubleManualMode @moreOptions
  Scenario Outline: Test 1: More options pop validation for status screen for double oven
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

  @doubleManualMode @moreOptions
  Scenario Outline: Test 2: More options pop change cooktime level validation for double oven
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

  @doubleManualMode @moreOptions
  Scenario Outline: Test 3: More options pop set as fav validation for double oven
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

  @doubleManualMode @moreOptions
  Scenario Outline: Test 4: More options pop view instrunctions validation for double oven
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


