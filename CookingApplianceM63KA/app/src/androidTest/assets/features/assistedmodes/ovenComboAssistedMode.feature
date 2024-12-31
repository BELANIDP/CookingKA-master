Feature: Assisted mode automation for Oven in Combo

  Background:
    Given The app has started with Combo low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on lower oven btn
    And I see tumbler screen
    And I scroll to Assisted Cooking Option

#----------------------------Food Type Selection Grid View---------------------#

  @ovenComboAssistedMode @foodTypeSelectionGridView
  Scenario: Test 1: Assisted Food Type Selection screen validation and back button navigation for combo oven
    Then I see recipe grid screen
    Then I validate title text of Food Type Selection grid
    Then I validate title text size of Food Type Selection grid
    Then I validate title text color of Food Type Selection grid
    Then I click on back button on Food Type Selection screen grid
    And I see tumbler screen


    #----------------------------Recipe Selection Grid View---------------------#

  @ovenComboAssistedMode @recipeSelectionGridView
  Scenario Outline: Test 1: Assisted Recipe Selection screen visibility and validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I see recipe grid screen
    And I validate title text is same as "<recipeName>"
    Examples:
      | index       | recipeName  |
      | Baked Goods | Baked Goods |
      | Meats       | Meats       |


#-----------------------------------Temp Tumbler---------------------#

  @ovenComboAssistedMode @tempTumbler
  Scenario Outline: Test 1: Assisted temperature tumbler validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I validate Temperature tumbler title text
    Then I validate Temperature tumbler subtitle text
    Then I validate Temperature tumbler subtitle text size
    Then I validate Temperature tumbler subtitle text color
    Then I validate Temperature tumbler oven icon visible
    Examples:
      | index       |
      | Baked Goods |

  @ovenComboAssistedMode @tempTumbler
  Scenario Outline: Test 2: Assisted temperature tumbler numpad icon click validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I validate Temperature tumbler numpad icon is clickable
    Examples:
      | index       |
      | Baked Goods |

  @ovenComboAssistedMode @tempTumbler
  Scenario Outline: Test 3: Assisted temperature tumbler back icon click validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I validate Temperature tumbler back icon is clickable
    Then I see recipe grid screen
    Examples:
      | index       |
      | Baked Goods |

  @ovenComboAssistedMode @tempTumbler
  Scenario Outline: Test 4: Assisted temperature tumbler scroll validation and Next button validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I validate Temperature tumbler is scrolled to 7 servings
    Then I validate Temperature tumbler next button is visible
    Then I validate Temperature tumbler next button is enabled
    Then I validate Temperature tumbler next button is clickable
    Examples:
      | index       |
      | Baked Goods |


#-----------------------------------Cooktime Numpad----------------------------------------#

  @ovenComboAssistedMode @cooktimeNumpad
  Scenario Outline: Test 1: Assisted cooktime numpad visibility and delete button navigation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I click on delete button on numpad screen
    Examples:
      | index       |
      | Baked Goods |

  @ovenComboAssistedMode @cooktimeNumpad
  Scenario Outline: Test 2: Assisted cooktime numpad tumbler icon validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I click on tumbler icon
    And I see vertical tumbler
    Examples:
      | index       |
      | Baked Goods |

  @ovenComboAssistedMode @cooktimeNumpad
  Scenario Outline: Test 3: Assisted cooktime numpad cooktime set validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |


#-----------------------------------Cooktime Tumbler----------------------------------------#

  @ovenComboAssistedMode @cooktimeTumbler
  Scenario Outline: Test 1: Assisted cooktime numpad cooktime set validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I click on tumbler icon
    Then I click on numpad button on vertical tumbler screen
    And I see numpad
    Examples:
      | index       |
      | Baked Goods |

  @ovenComboAssistedMode @cooktimeTumbler
  Scenario Outline: Test 2: Assisted cooktime numpad cooktime set validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I click on tumbler icon
    Then I set the CookTime to "<cookTime>"
    Then I click the Next Button
    Then I see preview screen
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |

#-----------------------------------Preview Screen----------------------------------------#

  @ovenComboAssistedMode @previewScreen
  Scenario Outline: Test 1: Assisted preview screen visibility and back button navigation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    Then I click on back button on preview screen
    Then I see numpad
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |

  @ovenComboAssistedMode @previewScreen
  Scenario Outline: Test 2: Assisted preview screen oven icon visibility and amount click validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    Then I validate oven icon is visible on preview screen
    Then I click on Temp section on preview screen
    Then I see temp tumbler
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |

  @ovenComboAssistedMode @previewScreen
  Scenario Outline: Test 3: Assisted preview screen next button visibility and doneness click validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    Then I validate preview screen next button is visible
    Then I click on Time section on preview screen
    Then I see numpad
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |

  @ovenComboAssistedMode @previewScreen
  Scenario Outline: Test 4: Assisted preview screen Next button Validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    Then I validate preview screen next button is enabled
    Then I validate preview screen next button is clickable
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |


#-----------------------------------Cooking Guide----------------------------------------#

  @ovenComboAssistedMode @cookingGuide
  Scenario Outline: Test 1: Assisted Cooking guide visibility and validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    And I click on Next button on preview screen
    And I validate cooking guide text is visible
    And I validate cooking guide image is visible
    And I validate cooking guide title text is visible
    And I validate cooking guide title text is correct
    And I validate cooking guide title text size is correct
    And I validate cooking guide title text color is correct
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |

  @ovenComboAssistedMode @cookingGuide
  Scenario Outline: Test 2: Assisted cooking guide Next button validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    And I click on Next button on preview screen
    Then I validate cooking guide next button is visible
    Then I validate cooking guide next button is enabled
    Then I validate cooking guide next button is clickable
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |

#-----------------------------------Status Screen----------------------------------------#

  @ovenComboAssistedMode @statusScreenAssisted
  Scenario Outline: Test 1: Assisted status screen visibility and validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    And I click on Next button on preview screen
    Then I click on Next button on cooking guide
    Then I click on Start button on cooking guide
    And I see status screen for assisted cooking
    And I see the oven icon besides recipe name
    Then I see the recipe name on status screen
    Then I validate the remaining time visible on status screen
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |

  @ovenComboAssistedMode @statusScreenAssisted
  Scenario Outline: Test 2: Assisted status screen three doted for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    And I click on Next button on preview screen
    Then I click on Next button on cooking guide
    Then I click on Start button on cooking guide
    And I see status screen for assisted cooking
    And I click on three dots icon
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |

  @ovenComboAssistedMode @statusScreenAssisted
  Scenario Outline: Test 3: Assisted status screen Turn off button for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    And I click on Next button on preview screen
    Then I click on Next button on cooking guide
    Then I click on Start button on cooking guide
    And I see status screen for assisted cooking
    Then I click on Turn OFF button
    And I expect clock screen should be visible
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |

  @ovenComboAssistedMode @statusScreenAssisted
  Scenario Outline: Test 4: Assisted status screen set lower oven button validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on Biscuits recipe
    And I see temp tumbler
    Then I click on Next button on temp tumbler
    Then I see numpad
    Then I set the CookTime to "<cookTime>" via Numpad
    Then I click the Next Button on numpad
    Then I see preview screen
    And I click on Next button on preview screen
    Then I click on Next button on cooking guide
    Then I click on Start button on cooking guide
    And I see status screen for assisted cooking
    And I see Set Microwave oven button
    And I click on Set Mwo button
    And I see tumbler screen
    Examples:
      | index       | cookTime |
      | Baked Goods | 30       |
