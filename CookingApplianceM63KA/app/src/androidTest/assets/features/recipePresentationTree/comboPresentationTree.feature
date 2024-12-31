Feature: Recipe presentation tree automation for Combo oven

  Background:
    Given The app has started with Combo low end Variant

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 1: Check manual recipe names in mw cavity.
    And I perform click on clock screen
    And I perform click on microwave btn
    Then Check manual recipe names "<position>" "<recipename>".
    Examples:
      | position | recipename       |
      | 0        | Assisted Cooking |
      | 1        | Defrost          |
      | 2        | Microwave        |
      | 3        | Popcorn          |
      | 4        | Convect          |
      | 5        | Steam Cook       |
      | 6        | Reheat           |
      | 7        | More Modes       |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 2: Check Convect menu recipes in mw cavity.
    And I perform click on clock screen
    And I perform click on microwave btn
    Then Check Convect menu recipes for mw "<position>" "<recipename>".
    Examples:
      | position | recipename |
      | 0        | Bake       |
      | 1        | Roast      |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 3: Check More Modes menu recipes in mw cavity.
    And I perform click on clock screen
    And I perform click on microwave btn
    Then Check More Modes menu recipes for mw "<position>" "<recipename>".
    Examples:
      | position | recipename    |
      | 0        | Boil & Simmer |
      | 1        | Crisp         |
      | 2        | Keep Warm     |
      | 3        | Melt          |
      | 4        | Soften        |
      | 5        | Broil         |
      | 6        | Toast         |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 4: Check Assisted Mode menu names in mw cavity.
    And I perform click on clock screen
    And I perform click on microwave btn
    Then Check Assisted Mode menu names "<position>" "<menu>".
    Examples:
      | position | menu                |
      | 0        | Poultry             |
      | 1        | Meats               |
      | 2        | Seafood             |
      | 3        | Vegetables & Grains |
      | 4        | BakedGoods          |
      | 5        | Casseroles          |
      | 6        | Defrost             |
      | 7        | Cook                |
      | 8        | Melt                |
      | 9        | Soften              |
      | 10       | Reheat              |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 5: Check Assisted Mode menu recipes in mw cavity.
    And I perform click on clock screen
    And I perform click on microwave btn
    Then Check Assisted Mode menu recipes "<position>" "<recipename>" "<menu>".
    Examples:
      | position | menu | recipename                |
      | 0        | 0    | Frozen Chicken Nuggets    |
      | 1        | 0    | Chicken Breasts           |
      | 2        | 0    | Bone In Chicken           |
      | 3        | 0    | Frozen Chicken Wings      |
      | 0        | 1    | Bacon                     |
      | 1        | 1    | Frozen Sausage            |
      | 0        | 2    | Steamed Fish Fillets      |
      | 1        | 2    | Frozen Fish Sticks        |
      | 0        | 3    | Steamed Frozen Vegetables |
      | 1        | 3    | Steamed Vegetables        |
      | 2        | 3    | Steamed Root Vegetables   |
      | 3        | 3    | Baked Potatoes            |
      | 4        | 3    | Frozen French Fries       |
      | 5        | 3    | Baked Sweet Potatoes      |
      | 0        | 4    | Frozen Pan Pizza          |
      | 1        | 4    | Frozen Thin Crust Pizza   |
      | 2        | 4    | Biscuits                  |
      | 3        | 4    | Cinnamon Rolls            |
      | 0        | 5    | Frozen Lasagna            |
      | 0        | 6    | Meat Defrost              |
      | 1        | 6    | Poultry Defrost           |
      | 0        | 7    | Hot Cereals               |
      | 1        | 7    | Scrambled Eggs            |
      | 0        | 8    | Butter / Margarine        |
      | 1        | 8    | Chocolate                 |
      | 0        | 9    | Ice Cream                 |
      | 1        | 9    | Cream Cheese              |
      | 2        | 9    | Butter / Margarine        |
      | 0        | 10   | Dinner Plate              |
      | 1        | 10   | Soup                      |
      | 2        | 10   | Beverage                  |
      | 3        | 10   | Pizza Slice               |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 6: Check manual recipe names in lower cavity.
    And I perform click on clock screen
    And I perform click on lower oven btn
    Then Check manual recipe names "<position>" "<recipename>".
    Examples:
      | position | recipename       |
      | 0        | Assisted Cooking |
      | 1        | Air Fry          |
      | 2        | Bake             |
      | 3        | Broil            |
      | 4        | Convect          |
      | 5        | Fresh Pizza      |
      | 6        | Steam Bake       |
      | 7        | More Modes       |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 7: Check Convect menu recipes in lower cavity.
    And I perform click on clock screen
    And I perform click on lower oven btn
    Then Check Convect menu recipes "<position>" "<recipename>".
    Examples:
      | position | recipename |
      | 0        | Slow Roast |
      | 1        | Bake       |
      | 2        | Roast      |
      | 3        | Broil      |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 8: Check More Modes menu recipes in lower cavity.
    And I perform click on clock screen
    And I perform click on lower oven btn
    Then Check More Modes menu recipes "<position>" "<recipename>".
    Examples:
      | position | recipename |
      | 0        | Keep Warm  |
      | 1        | Proof      |
      | 2        | Probe      |
      | 3        | Dehydrate  |
      | 4        | Slow Cook  |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 9: Check Probe menu recipes in More Modes menu in lower cavity.
    And I perform click on clock screen
    And I perform click on lower oven btn
    Then Check Probe menu recipes in More Modes menu "<position>" "<recipename>".
    Examples:
      | position | recipename    |
      | 0        | Bake          |
      | 1        | Convect Bake  |
      | 2        | Convect Roast |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 10: Check Assisted Mode menu names in lower cavity.
    And I perform click on clock screen
    And I perform click on lower oven btn
    Then Check Assisted Mode menu names "<position>" "<menu>".
    Examples:
      | position | menu                |
      | 0        | BakedGoods          |
      | 1        | Casseroles          |
      | 2        | Meats               |
      | 3        | Poultry             |
      | 4        | Seafood             |
      | 5        | Vegetables & Grains |
      | 6        | Pizza               |
      | 7        | ProbeCook           |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 11: Check Assisted Mode menu recipes in lower cavity.
    And I perform click on clock screen
    And I perform click on lower oven btn
    Then Check Assisted Mode menu recipes "<position>" "<recipename>" "<menu>".
    Examples:
      | position | menu | recipename         |
      | 0        | 0    | Biscuits           |
      | 1        | 0    | Cookies            |
      | 2        | 0    | Cake               |
      | 3        | 0    | Brownies           |
      | 4        | 0    | Muffins            |
      | 5        | 0    | Bread              |
      | 6        | 0    | Snacks             |
      | 0        | 1    | Frozen Lasagna     |
      | 1        | 1    | Casserole          |
      | 0        | 2    | Bacon              |
      | 1        | 2    | Beef Roast         |
      | 2        | 2    | Pork Loin          |
      | 3        | 2    | Crown Lamb Roast   |
      | 4        | 2    | Ham                |
      | 0        | 3    | Chicken Nuggets    |
      | 1        | 3    | Chicken Pieces     |
      | 2        | 3    | Chicken Whole      |
      | 3        | 3    | Turkey Breast      |
      | 4        | 3    | Turkey Whole       |
      | 0        | 4    | Salmon Fillet      |
      | 1        | 4    | Tuna Steak         |
      | 2        | 4    | Swordfish Fillet   |
      | 3        | 4    | Fish Fillet        |
      | 4        | 4    | Fish Sticks        |
      | 0        | 5    | Baked Potatoes     |
      | 1        | 5    | French Fries       |
      | 2        | 5    | Fresh French Fries |
      | 3        | 5    | Roasted Vegetables |
      | 0        | 6    | Frozen Pizza       |
      | 1        | 6    | Fresh Pizza        |
      | 0        | 7    | Beef Roast         |
      | 1        | 7    | Chicken Pieces     |
      | 2        | 7    | Chicken Whole      |
      | 3        | 7    | Turkey Breast      |
      | 4        | 7    | Turkey Whole       |
      | 5        | 7    | Pork Loin          |
      | 6        | 7    | Crown Lamb Roast   |
      | 7        | 7    | Ham                |
      | 8        | 7    | Fish Fillet        |
      | 9        | 7    | Salmon Fillet      |
      | 10       | 7    | Swordfish Fillet   |
      | 11       | 7    | Tuna Steak         |
      | 12       | 7    | Baked Potatoes     |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 12: Check recipes when probe is connected in lower cavity.
    And I insert a probe in lower cavity
    Then Check Assisted Mode menu recipes when probe is connected "<position>" "<recipename>".
    And I remove a probe in lower cavity
    Examples:
      | position | recipename    |
      | 0        | Bake          |
      | 1        | Convect Bake  |
      | 2        | Convect Roast |
      | 3        | Auto Cook     |
      | 4        | Favorites     |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 13: Check Auto cook menus when probe is connected in lower cavity.
    And I insert a probe in lower cavity
    Then Check Auto cook menus when probe is connected "<position>" "<menu>".
    And I remove a probe in lower cavity
    Examples:
      | position | menu                |
      | 0        | Meats               |
      | 1        | Poultry             |
      | 2        | Seafood             |
      | 3        | Vegetables & Grains |
      | 4        | ProbeCook           |

  @recipePresentationTree @comboPresentationTree
  Scenario Outline: Test 14: Check Auto cook menu recipes when probe connected in lower cavity.
    And I insert a probe in lower cavity
    Then Check Auto cook menu recipes when probe connected "<position>" "<recipename>" "<menu>".
    And I remove a probe in lower cavity
    Examples:
      | position | menu | recipename       |
      | 0        | 0    | Beef Roast       |
      | 1        | 0    | Pork Loin        |
      | 2        | 0    | Crown Lamb Roast |
      | 3        | 0    | Ham              |
      | 0        | 1    | Chicken Pieces   |
      | 1        | 1    | Chicken Whole    |
      | 2        | 1    | Turkey Breast    |
      | 3        | 1    | Turkey Whole     |
      | 0        | 2    | Salmon Fillet    |
      | 1        | 2    | Tuna Steak       |
      | 2        | 2    | Swordfish Fillet |
      | 3        | 2    | Fish Fillet      |
      | 0        | 3    | Baked Potatoes   |
      | 0        | 4    | Beef Roast       |
      | 1        | 4    | Chicken Pieces   |
      | 2        | 4    | Chicken Whole    |
      | 3        | 4    | Turkey Breast    |
      | 4        | 4    | Turkey Whole     |
      | 5        | 4    | Pork Loin        |
      | 6        | 4    | Crown Lamb Roast |
      | 7        | 4    | Ham              |
      | 8        | 4    | Fish Fillet      |
      | 9        | 4    | Salmon Fillet    |
      | 10       | 4    | Swordfish Fillet |
      | 11       | 4    | Tuna Steak       |
      | 12       | 4    | Baked Potatoes   |
