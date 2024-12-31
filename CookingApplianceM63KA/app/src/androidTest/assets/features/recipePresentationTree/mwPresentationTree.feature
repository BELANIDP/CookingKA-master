Feature: Recipe presentation tree automation for Microwave

  Background:
    Given The app has started with Microwave low end Variant

  @recipePresentationTree @mwPresentationTree
  Scenario Outline: Test 1: Check manual recipe names.
    And I perform click on clock screen
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

  @recipePresentationTree @mwPresentationTree
  Scenario Outline: Test 2: Check Convect menu recipes
    And I perform click on clock screen
    Then Check Convect menu recipes for mw "<position>" "<recipename>".
    Examples:
      | position | recipename |
      | 0        | Bake       |
      | 1        | Roast      |

  @recipePresentationTree @mwPresentationTree
  Scenario Outline: Test 3: Check More Modes menu recipes
    And I perform click on clock screen
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

  @recipePresentationTree @mwPresentationTree
  Scenario Outline: Test 4: Check Assisted Mode menu names
    And I perform click on clock screen
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

  @recipePresentationTree @mwPresentationTree
  Scenario Outline: Test 5: Check Assisted Mode menu recipes
    And I perform click on clock screen
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