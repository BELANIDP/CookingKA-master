Feature: Hot cavity warning for Double Oven

  Background:
    Given The app has started with Double low end Variant
    And I click and navigate to cavity selection screen
    And I increase the oven temperature "<Both>".

  @hotCavityWarning @doubleHotCavityWarning @notRecommended @hotCavityPopupContent
  Scenario Outline: Test 1: Verify Hot cavity popup details (not recommended recipe)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And For Double Oven Verify Hot cavity popup details "<product>" "<cavity>" "<index>" "<recipename>" "<icon>" "<iconHeight>" "<iconWidth>" "<title>" "<font>" "<weight>" "<textSize>" "<lineHeight>" "<alignment>" "<color>" "<description>" "<font1>" "<weight1>" "<textSize1>" "<lineHeight1>" "<alignment1>" "<color1>" "<rightbutton>".
    Examples:
      | menu       | product | cavity | index     | recipename | icon  | iconHeight | iconWidth | title                | font           | weight | textSize | lineHeight | alignment        | color   | description                                                                     | font1          | weight1 | textSize1 | lineHeight1 | alignment1       | color1  | rightbutton |
      | More Modes | Double  | Upper  | Keep Warm | keepWarm   | Upper | 64         | 64        | Oven is Cooling Down | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Oven is cooling, wait until the temperature decreases to place the food inside. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | START       |
      | More Modes | Double  | Lower  | Keep Warm | keepWarm   | Lower | 64         | 64        | Oven is Cooling Down | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Oven is cooling, wait until the temperature decreases to place the food inside. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | START       |

  @hotCavityWarning @doubleHotCavityWarning @notAllowed @ovenReadyPopupContent
  Scenario Outline: Test 2: Verify Hot cavity popup details (not allowed recipe)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And For Double Oven Verify Hot cavity popup details "<product>" "<cavity>" "<index>" "<recipename>" "<icon>" "<iconHeight>" "<iconWidth>" "<title>" "<font>" "<weight>" "<textSize>" "<lineHeight>" "<alignment>" "<color>" "<description>" "<font1>" "<weight1>" "<textSize1>" "<lineHeight1>" "<alignment1>" "<color1>" "<rightbutton>".
    Examples:
      | menu       | product | cavity | index | recipename | icon  | iconHeight | iconWidth | title                      | font           | weight | textSize | lineHeight | alignment        | color   | description                                                                            | font1          | weight1 | textSize1 | lineHeight1 | alignment1       | color1  | rightbutton |
      | More Modes | Double  | Upper  | Proof | proof      | Upper | 64         | 64        | Upper Oven is Cooling Down | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Upper Oven is too hot. Wait until the temperature\ndecreases to place the food inside. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | NEXT        |
      | More Modes | Double  | Lower  | Proof | proof      | Lower | 64         | 64        | Lower Oven is Cooling Down | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Lower Oven is too hot. Wait until the temperature\ndecreases to place the food inside. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | NEXT        |

  @hotCavityWarning @doubleHotCavityWarning @notRecommended
  Scenario Outline: Test 3: Verify Oven ready popup details (not recommended recipe)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And For Double Oven Verify Oven ready popup details "<product>" "<cavity>" "<index>" "<recipename>" "<icon>" "<iconHeight>" "<iconWidth>" "<title>" "<font>" "<weight>" "<textSize>" "<lineHeight>" "<alignment>" "<color>" "<description>" "<font1>" "<weight1>" "<textSize1>" "<lineHeight1>" "<alignment1>" "<color1>" "<rightbutton>".
    Examples:
      | menu       | product | cavity | index     | recipename | icon  | iconHeight | iconWidth | title      | font           | weight | textSize | lineHeight | alignment        | color   | description                                                                           | font1          | weight1 | textSize1 | lineHeight1 | alignment1       | color1  | rightbutton |
      | More Modes | Double  | Upper  | Keep Warm | keepWarm   | Upper | 64         | 64        | Oven Ready | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Oven is ready for cooking, please place the food in and press <b>NEXT</b> to proceed. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | NEXT        |
      | More Modes | Double  | Lower  | Keep Warm | keepWarm   | Lower | 64         | 64        | Oven Ready | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Oven is ready for cooking, please place the food in and press <b>NEXT</b> to proceed. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | NEXT        |

  @hotCavityWarning @doubleHotCavityWarning @notAllowed
  Scenario Outline: Test 4: Verify Oven ready popup details (not allowed recipe)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And For Double Oven Verify Oven ready popup details "<product>" "<cavity>" "<index>" "<recipename>" "<icon>" "<iconHeight>" "<iconWidth>" "<title>" "<font>" "<weight>" "<textSize>" "<lineHeight>" "<alignment>" "<color>" "<description>" "<font1>" "<weight1>" "<textSize1>" "<lineHeight1>" "<alignment1>" "<color1>" "<rightbutton>".
    Examples:
      | menu       | product | cavity | index | recipename | icon  | iconHeight | iconWidth | title            | font           | weight | textSize | lineHeight | alignment        | color   | description                                                                           | font1          | weight1 | textSize1 | lineHeight1 | alignment1       | color1  | rightbutton |
      | More Modes | Double  | Upper  | Proof | proof      | Upper | 64         | 64        | Upper Oven Ready | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Upper Oven is ready for cooking, please place the\nfood in and press NEXT to proceed. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | NEXT        |
      | More Modes | Double  | Lower  | Proof | proof      | Lower | 64         | 64        | Lower Oven Ready | roboto_regular | 400    | 40       | 48         | centerHorizontal | #FFFFFF | Lower Oven is ready for cooking, please place the\nfood in and press NEXT to proceed. | roboto_regular | 300     | 30        | 36          | centerHorizontal | #FFFFFF | NEXT        |

  @hotCavityWarning @doubleHotCavityWarning @notRecommended
  Scenario Outline: Test 5: Verify happy flow for hot cavity warning for not recommended recipe.(user wait for cavity to cool down then start)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify happy flow for hot cavity warning for not recommended recipe, user wait for cavity to cool down "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index     | recipename |
      | More Modes | Double  | Upper  | Keep Warm | keepWarm   |
      | More Modes | Double  | Lower  | Keep Warm | keepWarm   |

  @hotCavityWarning @doubleHotCavityWarning @notAllowed
  Scenario Outline: Test 6: Verify happy flow for hot cavity warning for not allowed recipe.(user wait for cavity to cool down then start)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify happy flow for hot cavity warning for not recommended recipe, user wait for cavity to cool down "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index | recipename |
      | More Modes | Double  | Upper  | Proof | proof      |
      | More Modes | Double  | Lower  | Proof | proof      |

  @hotCavityWarning @doubleHotCavityWarning @notRecommended
  Scenario Outline: Test 7: Verify happy flow for hot cavity warning for not recommended recipe.(user not wait for cavity to cool down directly start cycle)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify happy flow for hot cavity warning for not recommended recipe, user not wait for cavity to cool down "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index     | recipename |
      | More Modes | Double  | Upper  | Keep Warm | keepWarm   |
      | More Modes | Double  | Lower  | Keep Warm | keepWarm   |

  @hotCavityWarning @doubleHotCavityWarning @notAllowed
  Scenario Outline: Test 8: Verify happy flow for hot cavity warning for not allowed recipe.(user not wait for cavity to cool down try to start cycle from hot cavity popup.)
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify happy flow for hot cavity warning for not recommended recipe, user not wait for cavity to cool down "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index | recipename |
      | More Modes | Double  | Upper  | Proof | proof      |
      | More Modes | Double  | Lower  | Proof | proof      |

  @hotCavityWarning @doubleHotCavityWarning @notRecommended @cancelPress
  Scenario Outline: Test 9: Verify Cancel press on Oven ready popup for not recommended recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Cancel press on Oven ready popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index     | recipename |
      | More Modes | Double  | Upper  | Keep Warm | keepWarm   |
      | More Modes | Double  | Lower  | Keep Warm | keepWarm   |

  @hotCavityWarning @doubleHotCavityWarning @notAllowed @cancelPress
  Scenario Outline: Test 10: Verify Cancel press on Oven ready popup for not allowed recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Cancel press on Oven ready popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index | recipename |
      | More Modes | Double  | Upper  | Proof | proof      |
      | More Modes | Double  | Lower  | Proof | proof      |

  @hotCavityWarning @doubleHotCavityWarning @notRecommended @cancelPress
  Scenario Outline: Test 11: Verify Cancel press on hot cavity warning popup for not recommended recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Cancel press on hot cavity warning popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index     | recipename |
      | More Modes | Double  | Upper  | Keep Warm | keepWarm   |
      | More Modes | Double  | Lower  | Keep Warm | keepWarm   |

  @hotCavityWarning @doubleHotCavityWarning @notAllowed @cancelPress
  Scenario Outline: Test 12: Verify Cancel press on hot cavity warning popup for not allowed recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Cancel press on hot cavity warning popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index | recipename |
      | More Modes | Double  | Upper  | Proof | proof      |
      | More Modes | Double  | Lower  | Proof | proof      |

  @hotCavityWarning @doubleHotCavityWarning @notRecommended @doorOpen
  Scenario Outline: Test 13: Verify Door open on Oven ready popup and try to start cycle for not recommended recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Door open on Oven ready popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index     | recipename |
      | More Modes | Double  | Upper  | Keep Warm | keepWarm   |
      | More Modes | Double  | Lower  | Keep Warm | keepWarm   |

  @hotCavityWarning @doubleHotCavityWarning @notAllowed @doorOpen
  Scenario Outline: Test 14: Verify Door open on Oven ready popup and try to start cycle for not allowed recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Door open on Oven ready popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index | recipename |
      | More Modes | Double  | Upper  | Proof | proof      |
      | More Modes | Double  | Lower  | Proof | proof      |

  @hotCavityWarning @doubleHotCavityWarning @notRecommended @doorOpen
  Scenario Outline: Test 15: Verify Door open on hot cavity warning popup and try to start cycle for not recommended recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Door open on hot cavity popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index     | recipename |
      | More Modes | Double  | Upper  | Keep Warm | keepWarm   |
      | More Modes | Double  | Lower  | Keep Warm | keepWarm   |

  @hotCavityWarning @doubleHotCavityWarning @notAllowed @doorOpen
  Scenario Outline: Test 16: Verify Door open on hot cavity warning popup and try to start cycle for not allowed recipe.
    And I perform click on "<cavity>" cavity btn
    Then I scroll tumbler to targetText "<menu>" and click
    And I click on more modes "<index>"
    And I click on next button on recipe instructions selection screen
    And I closed the door "<cavity>".
    And I click on start button
    And Verify Door open on hot cavity popup "<product>" "<cavity>" "<index>" "<recipename>".
    Examples:
      | menu       | product | cavity | index | recipename |
      | More Modes | Double  | Upper  | Proof | proof      |
      | More Modes | Double  | Lower  | Proof | proof      |
