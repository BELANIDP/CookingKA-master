Feature: Cooking - Probe for double oven

  @probeDouble
  Scenario: Test 1 : Insert probe on clock screen
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
    And I see probe recipe gridview
    Then I remove a probe in upper cavity

  @probeDouble
  Scenario: Test 2 : Probe recipe selection gridview validation
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
    And I see probe recipe gridview
    And I check probe recipe selection gridview title text
    And I click on back button on Probe recipe Selection screen grid
    And I see probe still detected popup
    Then I remove a probe in upper cavity

  @probeDouble
  Scenario: Test 3 : probe still detected popup validation
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
    And I see probe recipe gridview
    And I check probe recipe selection gridview title text
    And I click on back button on Probe recipe Selection screen grid
    And I see probe still detected popup
    And I validate the title text and description of probe still detected popup
    And I validate the return to probe modes button
    And I click on return to probe modes button
    And I see probe recipe gridview
    Then I remove a probe in upper cavity

  @probeDouble
  Scenario Outline: Test 4 : Probe temp tumbler validation
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
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
    And I remove a probe in upper cavity
    Examples:
    |recipeName|80|
    |Bake      |5 |

  @probeDouble
  Scenario Outline: Test 5 : oven temp tumbler validation
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
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
    And I remove a probe in upper cavity
    Examples:
      |recipeName|80|125|
      |Bake      |5 |9  |

  @probeDouble
  Scenario Outline: Test 6 : Probe preview screen validation
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
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
    And I remove a probe in upper cavity
    Examples:
      |recipeName|80|125|
      |Bake      |5 |9  |

  @probeDouble
  Scenario Outline: Test 7 : Probe status screen validation
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
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
    And I validate the probe status screen
    And I remove a probe in upper cavity
    Examples:
      |recipeName|80|125|
      |Bake      |5 |9  |

  @probeDouble
  Scenario Outline: Test 8 : Insert temp probe validation after probe is removed in between
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
    And I see probe recipe gridview
    And I remove a probe in upper cavity
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

  @probeDouble
  Scenario: Test 9 : Probe detected popup validation
    Given The app has started with Double low end Variant
    And I click and navigate to cavity selection screen
    And I insert a probe in upper cavity
    And I see probe detected popup
    And I validate probe detected popup
    And I click on yes button
    And I see probe recipe gridview
    And I remove a probe in upper cavity

  @probeDouble
  Scenario: Test 10 : Probe detected in upper cavity popup no button validation
    Given The app has started with Double low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on upper cavity btn
    And I insert a probe in upper cavity
    And I see probe detected in upper cavity popup
    And I validate probe detected in upper cavity popup
    And I click on no button
    And I see tumbler screen
    And I remove a probe in upper cavity

  @probeDouble
  Scenario: Test 11 : Probe detected in upper cavity popup yes button validation
    Given The app has started with Double low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on upper cavity btn
    And I insert a probe in upper cavity
    And I see probe detected in upper cavity popup
    And I validate probe detected in upper cavity popup
    And I click on yes button
    And I see probe recipe gridview
    And I remove a probe in upper cavity

  @probeDouble
  Scenario Outline: Test 12 : Probe removed on status screen pop up validation
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
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
    And I remove a probe in upper cavity
    And I see probe removed popup
    And I validate probe removed popup
    Examples:
      |recipeName|80|125|
      |Bake      |5 |9  |

  @probeDouble
  Scenario: Test 13 : Probe detected in lower cavity popup validation
    Given The app has started with Double low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on upper cavity btn
    And I insert a probe in lower cavity
    And I see probe detected in lower cavity popup
    And I validate probe detected in lower cavity popup
    And I click on yes button
    And I see probe recipe gridview
    And I remove a probe in lower cavity

  @probeDouble
  Scenario Outline: Test 14 : Probe detected in lower cavity popup validation if normal bake cycle is on temp tumbler screen in upper cavity
    Given The app has started with Double low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on upper cavity btn
    And I see tumbler screen
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    And I insert a probe in lower cavity
    And I see probe detected in lower cavity popup
    And I click on yes button
    And I see probe recipe gridview
    And I remove a probe in lower cavity
    Examples:
    |index|
    |Bake |

  @probeDouble
  Scenario Outline: Test 15 : Probe detected in lower cavity popup validation if normal bake cycle is running in upper cavity
    Given The app has started with Double low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on lower oven btn
    And I see tumbler screen
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I click on Next button on temp tumbler
    And I insert a probe in upper cavity
    And I see probe detected in upper cavity popup
    And I click on yes button
    And I see probe recipe gridview
    And I remove a probe in upper cavity
    Examples:
      |index|
      |Bake |

  @probeDouble
  Scenario Outline: Test 16 : Probe detected popup validation if normal bake cycle is running in upper cavity
    Given The app has started with Double low end Variant
    And I click and navigate to cavity selection screen
    And I perform click on upper cavity btn
    And I see tumbler screen
    Then I scroll tumbler to targetText "<index>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I click on Next button on temp tumbler
    And I insert a probe in upper cavity
    And I see probe detected popup
    And I click on yes button
    And I see probe recipe gridview
    And I remove a probe in upper cavity
    Examples:
      |index|
      |Bake |

  @probeDoubleProduct
  Scenario: Test 17 : Perform click on clock screen when probe is already inserted.
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
    And I see probe recipe gridview

  @probeDoubleProduct
  Scenario: Test 18 : Probe recipe selection gridview validation on product
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
    And I see probe recipe gridview
    And I check probe recipe selection gridview title text
    And I click on back button on Probe recipe Selection screen grid
    And I see probe still detected popup

  @probeDoubleProduct
  Scenario: Test 19 : probe still detected popup validation on product
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
    And I see probe recipe gridview
    And I check probe recipe selection gridview title text
    And I click on back button on Probe recipe Selection screen grid
    And I see probe still detected popup
    And I validate the title text and description of probe still detected popup
    And I validate the return to probe modes button
    And I click on return to probe modes button
    And I see probe recipe gridview

  @probeDoubleProduct
  Scenario Outline: Test 20 : Probe temp tumbler validation on product
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
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
      | recipeName | 80 |
      | Bake       | 5  |

  @probeDoubleProduct
  Scenario Outline: Test 21 : oven temp tumbler validation on product
    Given The app has started with Double low end Variant
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
    Examples:
      | recipeName | 80 | 125 |
      | Bake       | 5  | 9   |

  @probeDoubleProduct
  Scenario Outline: Test 22 : Probe preview screen validation on product
    Given The app has started with Double low end Variant
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
    Examples:
      | recipeName | 80 | 125 |
      | Bake       | 5  | 9   |

  @probeDoubleProduct
  Scenario Outline: Test 23 : Probe status screen validation on product
    Given The app has started with Double low end Variant
    And I insert a probe in upper cavity
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
    And I validate the probe status screen for double "<cavity>"
    Examples:
      | recipeName | 80 | 125 |
      | Bake       | 5  | 9   |
