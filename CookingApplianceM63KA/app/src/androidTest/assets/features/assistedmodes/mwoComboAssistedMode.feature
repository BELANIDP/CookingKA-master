Feature: Assisted mode automation for MWO in Combo

  Background:
    Given The app has started with Combo low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on microwave btn
    And I see tumbler screen
    And I scroll to Assisted Cooking Option

#----------------------------Food Type Selection Grid View---------------------#

  @mwoComboAssistedMode @foodTypeSelectionGridView
  Scenario: Test 1: Assisted Food Type Selection screen validation and back icon navigation for combo oven
    Then I see recipe grid screen
    Then I validate title text of Food Type Selection grid
    Then I validate title text size of Food Type Selection grid
    Then I validate title text color of Food Type Selection grid
    Then I click on back button on Food Type Selection screen grid
    And I see tumbler screen

  @mwoComboAssistedMode @foodTypeSelectionGridView
  Scenario: Test 2: Assisted Food Type Selection oven icon validation for combo oven
    Then I see recipe grid screen


#    ----------------------------Recipe Selection Grid View---------------------#

  @mwoComboAssistedMode @recipeSelectionGridView
  Scenario Outline: Test 1: Assisted Recipe Selection screen visibility and validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I see recipe grid screen
    And I validate title text is same as "<recipeName>"
    Examples:
      | index   | recipeName |
      | Meats   | Meats      |
      | Poultry | Poultry    |


    #-----------------------------------Servings Tumbler---------------------#

  @mwoComboAssistedMode @servingTumbler
  Scenario Outline: Test 1: Assisted number of servings tumbler validation and back icon navigation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    And I see No. of serving tumbler
    Then I validate Serving tumbler title text
    Then I validate Serving tumbler subtitle text
    Then I validate Serving tumbler subtitle text size
    Then I validate Serving tumbler subtitle text color
    Then I validate Serving tumbler back icon is clickable
    Then I see recipe grid screen
    Examples:
      | index   |
      | Poultry |

  @mwoComboAssistedMode @servingTumbler
  Scenario Outline: Test 2: Assisted number of servings tumbler Next button visible for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    And I see No. of serving tumbler
    Then I validate Serving tumbler next button is visible
    Then I validate Serving tumbler next button is enabled
    Then I validate Serving tumbler next button is clickable
    Examples:
      | index   |
      | Poultry |

  @mwoComboAssistedMode @servingTumbler
  Scenario Outline: Test 3: Assisted number of servings tumbler scroll validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    And I see No. of serving tumbler
    Then I validate Serving tumbler is scrolled to 7 servings
    Examples:
      | index   |
      | Poultry |


#-----------------------------------Doneness Tumbler----------------------------------------#

  @mwoComboAssistedMode @donenessTumbler
  Scenario Outline: Test 1: Assisted doneness tumbler validation and back button navigation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    And I see doneness tumbler
    And I validate title text of doneness tumbler
    And I validate title text size of doneness tumbler
    And I validate title text color of doneness tumbler
    And I click on back button on doneness tumbler
    And I see No. of serving tumbler
    Examples:
      | index   |
      | Poultry |

  @mwoComboAssistedMode @donenessTumbler
  Scenario Outline: Test 2: Assisted doneness tumbler Next button visible for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    And I see doneness tumbler
    Then I validate Doneness tumbler next button is visible
    Then I validate Doneness tumbler next button is enabled
    Then I validate Doneness tumbler next button is clickable
    Examples:
      | index   |
      | Poultry |

  @mwoComboAssistedMode @donenessTumbler
  Scenario Outline: Test 3: Assisted doneness tumbler scroll validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    And I see doneness tumbler
    Then I scroll doneness tumbler to Dark
    Examples:
      | index   |
      | Poultry |



#-----------------------------------Preview Screen----------------------------------------#

  @mwoComboAssistedMode @previewScreen
  Scenario Outline: Test 1: Assisted preview screen visibility and back button validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    Then I scroll doneness tumbler to Dark
    And I see preview screen
    Then I click on back button on preview screen
    Then I see doneness tumbler
    Examples:
      | index   |
      | Poultry |

  @mwoComboAssistedMode @previewScreen
  Scenario Outline: Test 2: Assisted preview screen oven icon visibility and amount click validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    Then I scroll doneness tumbler to Dark
    And I see preview screen
    Then I validate oven icon is visible on preview screen
    Then I click on Amount section on preview screen
    Then I see No. of serving tumbler
    Examples:
      | index   |
      | Poultry |

  @mwoComboAssistedMode @previewScreen
  Scenario Outline: Test 3: Assisted preview screen doneness click validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    Then I scroll doneness tumbler to Dark
    And I see preview screen
    Then I click on doneness section on preview screen
    Then I see doneness tumbler
    Examples:
      | index   |
      | Poultry |

  @mwoComboAssistedMode @previewScreen
  Scenario Outline: Test 4: Assisted preview screen Next button visibility and validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    Then I scroll doneness tumbler to Dark
    And I see preview screen
    Then I validate preview screen next button is visible
    Then I validate preview screen next button is enabled
    Then I validate preview screen next button is clickable
    Examples:
      | index   |
      | Poultry |


#-----------------------------------Cooking Guide----------------------------------------#

  @mwoComboAssistedMode @cookingGuide
  Scenario Outline: Test 1: Assisted Cooking guide visibility and validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    Then I scroll doneness tumbler to Dark
    And I see preview screen
    And I click on Next button on preview screen
    And I validate cooking guide text is visible
    And I validate cooking guide image is visible
    And I validate cooking guide title text is visible
    And I validate cooking guide title text is correct
    And I validate cooking guide title text size is correct
    And I validate cooking guide title text color is correct
    Then I validate cooking guide next button is visible
    Examples:
      | index   |
      | Poultry |

  @mwoComboAssistedMode @cookingGuide
  Scenario Outline: Test 2: Assisted cooking guide Next button enabled for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    Then I scroll doneness tumbler to Dark
    And I see preview screen
    And I click on Next button on preview screen
    Then I validate cooking guide next button is enabled
    Then I validate cooking guide next button is clickable
    Examples:
      | index   |
      | Poultry |


#-----------------------------------Status Screen----------------------------------------#

  @mwoComboAssistedMode @statusScreenAssisted
  Scenario Outline: Test 1: Assisted status screen visibility and validations for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    Then I scroll doneness tumbler to Dark
    And I see preview screen
    And I click on Next button on preview screen
    Then I click on Next button on cooking guide
    Then I click on Start button on cooking guide
    And I see door open close popup
    And I open and close the door of "<cavity>"
    And I click on Start button on the door open close popup
    And I see status screen for assisted cooking
    Then I see the recipe name on status screen
    And I see the oven icon besides recipe name
    Then I validate the remaining time visible on status screen
    Examples:
      | index   | cavity |
      | Poultry | upper  |


  @mwoComboAssistedMode @statusScreenAssisted
  Scenario Outline: Test 2: Assisted status screen three doted for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    Then I scroll doneness tumbler to Dark
    And I see preview screen
    And I click on Next button on preview screen
    Then I click on Next button on cooking guide
    Then I click on Start button on cooking guide
    And I see door open close popup
    And I open and close the door of "<cavity>"
    And I click on Start button on the door open close popup
    And I see status screen for assisted cooking
    And I click on three dots icon
    Examples:
      | index   | cavity |
      | Poultry | upper  |

  @mwoComboAssistedMode @statusScreenAssisted
  Scenario Outline: Test 3: Assisted status screen Turn off button for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    Then I scroll doneness tumbler to Dark
    And I see preview screen
    And I click on Next button on preview screen
    Then I click on Next button on cooking guide
    Then I click on Start button on cooking guide
    And I see door open close popup
    And I open and close the door of "<cavity>"
    And I click on Start button on the door open close popup
    And I see status screen for assisted cooking
    Then I click on Turn OFF button
    And I expect clock screen should be visible
    Examples:
      | index   | cavity |
      | Poultry | upper  |

  @mwoComboAssistedMode @statusScreenAssisted
  Scenario Outline: Test 4: Assisted status screen set lower oven button visibility and validation for combo oven
    Then I see recipe grid screen
    Then I scroll to targetText "<index>" and click
    And I click on  Frozen Chicken Nugget recipe
    Then I validate Serving tumbler is scrolled to 7 servings
    Then I scroll doneness tumbler to Dark
    And I see preview screen
    And I click on Next button on preview screen
    Then I click on Next button on cooking guide
    Then I click on Start button on cooking guide
    And I see door open close popup
    And I open and close the door of "<cavity>"
    And I click on Start button on the door open close popup
    And I see status screen for assisted cooking
    And I see Set lower oven button
    And I click on Set lower oven button
    And I see tumbler screen
    Examples:
      | index   | cavity |
      | Poultry | upper  |

