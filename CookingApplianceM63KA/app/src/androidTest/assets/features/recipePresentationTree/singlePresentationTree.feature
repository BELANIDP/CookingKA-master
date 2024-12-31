Feature: Recipe presentation tree automation for Single Oven

  Background:
    Given The app has started with Single low end Variant

  @recipePresentationTree @singleOvenPresentationTree
  Scenario Outline: Test 1: Check manual recipe names.
    And I perform click on clock screen
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

  @recipePresentationTree @singleOvenPresentationTree
  Scenario Outline: Test 2: Check Convect menu recipes
    And I perform click on clock screen
    Then Check Convect menu recipes "<position>" "<recipename>".
    Examples:
      | position | recipename |
      | 0        | Slow Roast |
      | 1        | Bake       |
      | 2        | Roast      |
      | 3        | Broil      |

  @recipePresentationTree @singleOvenPresentationTree
  Scenario Outline: Test 3: Check More Modes menu recipes
    And I perform click on clock screen
    Then Check More Modes menu recipes "<position>" "<recipename>".
    Examples:
      | position | recipename |
      | 0        | Keep Warm  |
      | 1        | Proof      |
      | 2        | Probe      |
      | 3        | Dehydrate  |
      | 4        | Slow Cook  |

  @recipePresentationTree @singleOvenPresentationTree
  Scenario Outline: Test 4: Check Probe menu recipes in More Modes menu.
    And I perform click on clock screen
    Then Check Probe menu recipes in More Modes menu "<position>" "<recipename>".
    Examples:
      | position | recipename    |
      | 0        | Bake          |
      | 1        | Convect Bake  |
      | 2        | Convect Roast |

  @recipePresentationTree @singleOvenPresentationTree
  Scenario Outline: Test 5: Check Assisted Mode menu names
    And I perform click on clock screen
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

  @recipePresentationTree @singleOvenPresentationTree
  Scenario Outline: Test 6: Check Assisted Mode menu recipes
    And I perform click on clock screen
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

  @recipePresentationTree @singleOvenPresentationTree
  Scenario Outline: Test 7: Check recipes when probe is connected.
    And I insert a probe in upper cavity
    Then Check Assisted Mode menu recipes when probe is connected "<position>" "<recipename>".
    And I remove a probe in upper cavity
    Examples:
      | position | recipename    |
      | 0        | Bake          |
      | 1        | Convect Bake  |
      | 2        | Convect Roast |
      | 3        | Auto Cook     |
      | 4        | Favorites     |

  @recipePresentationTree @singleOvenPresentationTree
  Scenario Outline: Test 8: Check Auto cook menus when probe is connected.
    And I insert a probe in upper cavity
    Then Check Auto cook menus when probe is connected "<position>" "<menu>".
    And I remove a probe in upper cavity
    Examples:
      | position | menu                |
      | 0        | Meats               |
      | 1        | Poultry             |
      | 2        | Seafood             |
      | 3        | Vegetables & Grains |
      | 4        | ProbeCook           |

  @recipePresentationTree @singleOvenPresentationTree
  Scenario Outline: Test 9: Check Auto cook menu recipes when probe connected.
    And I insert a probe in upper cavity
    Then Check Auto cook menu recipes when probe connected "<position>" "<recipename>" "<menu>".
    And I remove a probe in upper cavity
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
