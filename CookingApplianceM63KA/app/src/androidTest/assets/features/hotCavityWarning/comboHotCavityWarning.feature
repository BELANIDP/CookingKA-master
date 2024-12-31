Feature: Hot cavity warning for Combo Oven

  Background:
    Given The app has started with Combo low end Variant
    And I click and navigate to cavity selection screen
    And I increase the oven temperature "<ComboOven>".

  @hotCavityWarning @comboHotCavityWarning @notRecommended @hotCavityPopupContent
  Scenario Outline: Test 1: Verify Hot cavity popup details (not recommended recipe)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Hot cavity popup details "<product>" "<cavity>" "<index>" "<recipename>" "<title>" "<font>" "<weight>" "<textSize>" "<lineHeight>" "<alignment>" "<color>" "<description>" "<font1>" "<weight1>" "<textSize1>" "<lineHeight1>" "<alignment1>" "<color1>" "<rightbutton>".
    Examples:
      | menu       | product | cavity    | index     | recipename | title                | font           | weight | textSize | lineHeight | alignment        | color   | description                                                                     | font1          | weight1 | textSize1 | lineHeight1 | alignment1       | color1  | rightbutton |
      | More Modes | Combo   | ComboOven | Keep Warm | keepWarm   | Oven is Cooling Down | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Oven is cooling, wait until the temperature decreases to place the food inside. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | START       |

  @hotCavityWarning @comboHotCavityWarning @notAllowed @ovenReadyPopupContent
  Scenario Outline: Test 2: Verify Hot cavity popup details (not allowed recipe)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Hot cavity popup details "<product>" "<cavity>" "<index>" "<recipename>" "<title>" "<font>" "<weight>" "<textSize>" "<lineHeight>" "<alignment>" "<color>" "<description>" "<font1>" "<weight1>" "<textSize1>" "<lineHeight1>" "<alignment1>" "<color1>" "<rightbutton>".
    Examples:
      | menu       | product | cavity    | index | recipename | title                      | font           | weight | textSize | lineHeight | alignment        | color   | description                                                                            | font1          | weight1 | textSize1 | lineHeight1 | alignment1       | color1  | rightbutton |
      | More Modes | Combo   | ComboOven | Proof | proof      | Lower Oven is Cooling Down | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Lower Oven is too hot. Wait until the temperature\ndecreases to place the food inside. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | NEXT        |

  @hotCavityWarning @comboHotCavityWarning @notRecommended
  Scenario Outline: Test 3: Verify Oven ready popup details (not recommended recipe)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Oven ready popup details "<product>" "<cavity>" "<index>" "<recipename>" "<title>" "<font>" "<weight>" "<textSize>" "<lineHeight>" "<alignment>" "<color>" "<description>" "<font1>" "<weight1>" "<textSize1>" "<lineHeight1>" "<alignment1>" "<color1>" "<rightbutton>".
    Examples:
      | menu       | product | cavity    | index     | recipename | title      | font           | weight | textSize | lineHeight | alignment        | color   | description                                                                           | font1          | weight1 | textSize1 | lineHeight1 | alignment1       | color1  | rightbutton |
      | More Modes | Combo   | ComboOven | Keep Warm | keepWarm   | Oven Ready | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Oven is ready for cooking, please place the food in and press <b>NEXT</b> to proceed. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | NEXT        |

  @hotCavityWarning @comboHotCavityWarning @notAllowed
  Scenario Outline: Test 4: Verify Oven ready popup details (not allowed recipe)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Oven ready popup details "<product>" "<cavity>" "<index>" "<recipename>" "<title>" "<font>" "<weight>" "<textSize>" "<lineHeight>" "<alignment>" "<color>" "<description>" "<font1>" "<weight1>" "<textSize1>" "<lineHeight1>" "<alignment1>" "<color1>" "<rightbutton>".
    Examples:
      | menu       | product | cavity    | index | recipename | title            | font           | weight | textSize | lineHeight | alignment        | color   | description                                                                           | font1          | weight1 | textSize1 | lineHeight1 | alignment1       | color1  | rightbutton |
      | More Modes | Combo   | ComboOven | Proof | proof      | Lower Oven Ready | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Lower Oven is ready for cooking, please place the\nfood in and press NEXT to proceed. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | NEXT        |

  @hotCavityWarning @comboHotCavityWarning @notRecommended
  Scenario Outline: Test 5: Verify happy flow for hot cavity warning for not recommended recipe.(user wait for cavity to cool down then start)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify happy flow for hot cavity warning for not recommended recipe, user wait for cavity to cool down "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index     | recipename |
      | More Modes | Combo   | ComboOven | Keep Warm | keepWarm   |

  @hotCavityWarning @comboHotCavityWarning @notAllowed
  Scenario Outline: Test 6: Verify happy flow for hot cavity warning for not allowed recipe.(user wait for cavity to cool down then start)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify happy flow for hot cavity warning for not recommended recipe, user wait for cavity to cool down "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index | recipename |
      | More Modes | Combo   | ComboOven | Proof | proof      |

  @hotCavityWarning @comboHotCavityWarning @notRecommended
  Scenario Outline: Test 7: Verify happy flow for hot cavity warning for not recommended recipe.(user not wait for cavity to cool down directly start cycle)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify happy flow for hot cavity warning for not recommended recipe, user not wait for cavity to cool down "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index     | recipename |
      | More Modes | Combo   | ComboOven | Keep Warm | keepWarm   |

  @hotCavityWarning @comboHotCavityWarning @notAllowed
  Scenario Outline: Test 8: Verify happy flow for hot cavity warning for not allowed recipe.(user not wait for cavity to cool down try to start cycle from hot cavity popup.)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify happy flow for hot cavity warning for not recommended recipe, user not wait for cavity to cool down "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index | recipename |
      | More Modes | Combo   | ComboOven | Proof | proof      |

  @hotCavityWarning @comboHotCavityWarning @notRecommended @cancelPress
  Scenario Outline: Test 9: Verify Cancel press on Oven ready popup for not recommended recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Cancel press on Oven ready popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index     | recipename |
      | More Modes | Combo   | ComboOven | Keep Warm | keepWarm   |

  @hotCavityWarning @comboHotCavityWarning @notAllowed @cancelPress
  Scenario Outline: Test 10: Verify Cancel press on Oven ready popup for not allowed recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Cancel press on Oven ready popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index | recipename |
      | More Modes | Combo   | ComboOven | Proof | proof      |

  @hotCavityWarning @comboHotCavityWarning @notRecommended @cancelPress
  Scenario Outline: Test 11: Verify Cancel press on hot cavity warning popup for not recommended recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Cancel press on hot cavity warning popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index     | recipename |
      | More Modes | Combo   | ComboOven | Keep Warm | keepWarm   |

  @hotCavityWarning @comboHotCavityWarning @notAllowed @cancelPress
  Scenario Outline: Test 12: Verify Cancel press on hot cavity warning popup for not allowed recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Cancel press on hot cavity warning popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index | recipename |
      | More Modes | Combo   | ComboOven | Proof | proof      |

  @hotCavityWarning @comboHotCavityWarning @notRecommended @doorOpen
  Scenario Outline: Test 13: Verify Door open on Oven ready popup and try to start cycle for not recommended recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Door open on Oven ready popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index     | recipename |
      | More Modes | Combo   | ComboOven | Keep Warm | keepWarm   |

  @hotCavityWarning @comboHotCavityWarning @notAllowed @doorOpen
  Scenario Outline: Test 14: Verify Door open on Oven ready popup and try to start cycle for not allowed recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Door open on Oven ready popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index | recipename |
      | More Modes | Combo   | ComboOven | Proof | proof      |

  @hotCavityWarning @comboHotCavityWarning @notRecommended @doorOpen
  Scenario Outline: Test 15: Verify Door open on hot cavity warning popup and try to start cycle for not recommended recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Door open on hot cavity popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index     | recipename |
      | More Modes | Combo   | ComboOven | Keep Warm | keepWarm   |

  @hotCavityWarning @comboHotCavityWarning @notAllowed @doorOpen
  Scenario Outline: Test 16: Verify Door open on hot cavity warning popup and try to start cycle for not allowed recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Door open on hot cavity popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity    | index | recipename |
      | More Modes | Combo   | ComboOven | Proof | proof      |
