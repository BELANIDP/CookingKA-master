Feature: Cooking - Probe for combo oven

  @probeCombo
  Scenario: Test 1 : Insert probe on clock screen
    Given The app has started with Combo low end Variant
    And I insert a probe in lower cavity
    And I see probe recipe gridview
    Then I remove a probe in lower cavity

  @probeCombo
  Scenario: Test 2 : Probe recipe selection gridview validation
    Given The app has started with Combo low end Variant
    And I insert a probe in lower cavity
    And I see probe recipe gridview
    And I check probe recipe selection gridview title text
    And I click on back button on Probe recipe Selection screen grid
    And I see probe still detected popup
    Then I remove a probe in lower cavity

  @probeCombo
  Scenario: Test 3 : probe still detected popup validation
    Given The app has started with Combo low end Variant
    And I insert a probe in lower cavity
    And I see probe recipe gridview
    And I click on back button on Probe recipe Selection screen grid
    And I see probe still detected popup
    And I validate the title text and description of probe still detected popup
    And I validate the return to probe modes button
    And I click on return to probe modes button
    And I see probe recipe gridview
    Then I remove a probe in lower cavity

  @probeCombo
  Scenario Outline: Test 4 : Probe temp tumbler validation
    Given The app has started with Combo low end Variant
    And I insert a probe in lower cavity
    And I see probe recipe gridview
    And I click on "<recipeName>"
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I see probe temp tumbler
    And I validate title text on probe temp tumbler
    And T click on numpad icon
    And I see temp numpad for probe
    And I click on tumbler icon
    And I set probe temp at "<80>"
    And I click on Next button on temp tumbler
    And I see oven temp tumbler
    And I remove a probe in lower cavity
    Examples:
      |recipeName|80|
      |Bake      |5 |

  @probeCombo
  Scenario Outline: Test 5 : oven temp tumbler validation
    Given The app has started with Combo low end Variant
    And I insert a probe in lower cavity
    And I see probe recipe gridview
    And I click on "<recipeName>"
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I see probe temp tumbler
    And I set probe temp at "<80>"
    And I click on Next button on temp tumbler
    And I see oven temp tumbler
    And T click on numpad icon
    And I see temp numpad for probe
    And I click on tumbler icon
    Then I set oven temp at "<125>"
    And I click on Next button on temp tumbler
    And I see probe preview screen
    And I remove a probe in lower cavity
    Examples:
      |recipeName|80|125|
      |Bake      |5 |9  |

  @probeCombo
  Scenario Outline: Test 6 : Probe preview screen validation
    Given The app has started with Combo low end Variant
    And I insert a probe in lower cavity
    And I see probe recipe gridview
    And I click on "<recipeName>"
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I see probe temp tumbler
    And I set probe temp at "<80>"
    And I click on Next button on temp tumbler
    And I see oven temp tumbler
    Then I set oven temp at "<125>"
    And I click on Next button on temp tumbler
    And I see probe preview screen
    And I click on Probe temp section
    And I see probe temp tumbler
    And I click on Next button on temp tumbler
    And I click on oven temp section
    And I see oven temp tumbler
    And I click on Next button on temp tumbler
    And I click on Start button on probe preview screen
    And I see status screen for probe
    And I remove a probe in lower cavity
    Examples:
      |recipeName|80|125|
      |Bake      |5 |9  |

  @probeCombo
  Scenario Outline: Test 7 : Probe status screen validation
    Given The app has started with Combo low end Variant
    And I insert a probe in lower cavity
    And I see probe recipe gridview
    And I click on "<recipeName>"
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I see probe temp tumbler
    And I set probe temp at "<80>"
    And I click on Next button on temp tumbler
    And I see oven temp tumbler
    Then I set oven temp at "<125>"
    And I click on Next button on temp tumbler
    And I see probe preview screen
    And I click on Start button on probe preview screen
    And I see status screen for probe
    And I validate the probe status screen on combo
    And I remove a probe in lower cavity
    Examples:
      |recipeName|80|125|
      |Bake      |5 |9  |

  @probeCombo
  Scenario Outline: Test 8 : Insert temp probe validation after probe is removed in between
    Given The app has started with Combo low end Variant
    And I insert a probe in lower cavity
    And I see probe recipe gridview
    And I remove a probe in lower cavity
    And I click on "<recipeName>"
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I see probe temp tumbler
    And I set probe temp at "<80>"
    And I click on Next button on temp tumbler
    And I see oven temp tumbler
    Then I set oven temp at "<125>"
    And I click on Next button on temp tumbler
    And I see probe preview screen
    And I click on Start button on probe preview screen
    And I see insert temp probe popup
    And I validate the insert temp probe popup
    Examples:
      |recipeName|80|125|
      |Bake      |5 |9  |

  @probeCombo
  Scenario: Test 9 : Probe detected popup validation
    Given The app has started with Combo low end Variant
    And I click and navigate to cavity selection screen
    And I insert a probe in lower cavity
    And I see probe detected popup
    And I validate probe detected popup
    And I click on yes button
    And I see probe recipe gridview
    And I remove a probe in lower cavity

  @probeCombo
  Scenario: Test 10 : Probe detected popup validation
    Given The app has started with Combo low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on upper cavity btn
    And I insert a probe in lower cavity
    And I see probe detected popup
    And I remove a probe in lower cavity

  @probeCombo
  Scenario: Test 11 : Probe detected yes button validation
    Given The app has started with Combo low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on upper cavity btn
    And I insert a probe in lower cavity
    And I see probe detected popup
    And I validate probe detected popup
    And I click on yes button
    And I see probe recipe gridview
    And I remove a probe in lower cavity

  @probeCombo
  Scenario Outline: Test 12 : Probe removed on status screen pop up validation
    Given The app has started with Combo low end Variant
    And I insert a probe in lower cavity
    And I see probe recipe gridview
    And I click on "<recipeName>"
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I see probe temp tumbler
    And I set probe temp at "<80>"
    And I click on Next button on temp tumbler
    And I see oven temp tumbler
    Then I set oven temp at "<125>"
    And I click on Next button on temp tumbler
    And I see probe preview screen
    And I click on Start button on probe preview screen
    And I see status screen for probe
    And I remove a probe in lower cavity
    And I see probe removed popup
    And I validate probe removed popup
    Examples:
      |recipeName|80|125|
      |Bake      |5 |9  |

  @probeCombo
  Scenario: Test 13 : Probe detected in lower cavity popup validation
    Given The app has started with Combo low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on lower oven btn
    And I insert a probe in lower cavity
    And I see probe detected popup
    And I validate probe detected popup
    And I click on yes button
    And I see probe recipe gridview
    And I remove a probe in lower cavity

  @probeComboProduct
  Scenario Outline: Test 14 : Perform click on clock screen when probe is already inserted.
    Given The app has started with Combo low end Variant
    And I perform click on clock screen
    And I perform click on "<cavity>" cavity btn
    And I see probe recipe gridview
    Examples:
      | cavity    |
      | ComboOven |

  @probeComboProduct
  Scenario Outline: Test 15 : Probe recipe selection gridview validation on product
    Given The app has started with Combo low end Variant
    And I perform click on clock screen
    And I perform click on "<cavity>" cavity btn
    And I see probe recipe gridview
    And I check probe recipe selection gridview title text
    And I see Oven Icon on Probe recipe Selection screen grid
    And I click on back button on Probe recipe Selection screen grid
    And I see probe still detected popup
    Examples:
      | cavity    |
      | ComboOven |

  @probeComboProduct
  Scenario Outline: Test 16 : probe still detected popup validation on product
    Given The app has started with Combo low end Variant
    And I perform click on clock screen
    And I perform click on "<cavity>" cavity btn
    And I see probe recipe gridview
    And I click on back button on Probe recipe Selection screen grid
    And I see probe still detected popup
    And I validate the title text and description of probe still detected popup
    And I validate the return to probe modes button
    And I click on return to probe modes button
    And I see probe recipe gridview
    Examples:
      | cavity    |
      | ComboOven |

  @probeComboProduct
  Scenario Outline: Test 17 : Probe temp tumbler validation on product
    Given The app has started with Combo low end Variant
    And I perform click on clock screen
    And I perform click on "<cavity>" cavity btn
    And I see probe recipe gridview
    And I click on "<recipeName>"
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I see probe temp tumbler
    And I validate title text on probe temp tumbler
    And T click on numpad icon
    And I see temp numpad for probe
    And I click on tumbler icon
    And I set probe temp at "<80>"
    And I click on Next button on temp tumbler
    And I see oven temp tumbler
    Examples:
      | cavity    | recipeName | 80 |
      | ComboOven | Bake       | 5  |

  @probeComboProduct
  Scenario Outline: Test 18 : oven temp tumbler validation on product
    Given The app has started with Combo low end Variant
    And I perform click on clock screen
    And I perform click on "<cavity>" cavity btn
    And I see probe recipe gridview
    And I click on "<recipeName>"
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I see probe temp tumbler
    And I set probe temp at "<80>"
    And I click on Next button on temp tumbler
    And I see oven temp tumbler
    And T click on numpad icon
    And I see temp numpad for probe
    And I click on tumbler icon
    Then I set oven temp at "<125>"
    And I click on Next button on temp tumbler
    And I see probe preview screen
    Examples:
      | cavity    | recipeName | 80 | 125 |
      | ComboOven | Bake       | 5  | 9   |

  @probeComboProduct
  Scenario Outline: Test 19 : Probe preview screen validation on product
    Given The app has started with Combo low end Variant
    And I perform click on clock screen
    And I perform click on "<cavity>" cavity btn
    And I see probe recipe gridview
    And I click on "<recipeName>"
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I see probe temp tumbler
    And I set probe temp at "<80>"
    And I click on Next button on temp tumbler
    And I see oven temp tumbler
    Then I set oven temp at "<125>"
    And I click on Next button on temp tumbler
    And I see probe preview screen
    And I click on Probe temp section
    And I see probe temp tumbler
    And I click on Next button on temp tumbler
    And I click on oven temp section
    And I see oven temp tumbler
    And I click on Next button on temp tumbler
    And I click on Start button on probe preview screen
    And I see status screen for probe
    Examples:
      | cavity    | recipeName | 80 | 125 |
      | ComboOven | Bake       | 5  | 9   |

  @probeComboProduct
  Scenario Outline: Test 20 : Probe status screen validation on product
    Given The app has started with Combo low end Variant
    And I perform click on clock screen
    And I perform click on "<cavity>" cavity btn
    And I see probe recipe gridview
    And I click on "<recipeName>"
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I see probe temp tumbler
    And I set probe temp at "<80>"
    And I click on Next button on temp tumbler
    And I see oven temp tumbler
    Then I set oven temp at "<125>"
    And I click on Next button on temp tumbler
    And I see probe preview screen
    And I click on Start button on probe preview screen
    And I see status screen for probe
    And I validate the probe status screen on combo
    Examples:
      | cavity    | recipeName | 80 | 125 |
      | ComboOven | Bake       | 5  | 9   |
