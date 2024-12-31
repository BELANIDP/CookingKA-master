Feature: Kitchen Timer Feature
  Background:
    Given App has started
    And I click on kitchen timer

    @kitchenTimer
    Scenario: Test 1: KT tumbler is visible
      Then I see kitchen timer tumbler view

  @kitchenTimer
  Scenario: Test 2: KT hours tumbler validation
    Then I see kitchen timer tumbler view
    Then I validate the hours tumbler view

  @kitchenTimer
  Scenario: Test 3: KT minutes tumbler validation
    Then I see kitchen timer tumbler view
    Then I validate the minutes tumbler view

  @kitchenTimer
  Scenario: Test 4: KT seconds tumbler validation
    Then I see kitchen timer tumbler view
    Then I validate the seconds tumbler view

  @kitchenTimer
  Scenario Outline: Test 5: Set KT tumbler
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Examples:
    |minutes|seconds|
    | 01    |20     |

  @kitchenTimer
  Scenario Outline: Test 6: Set KT numpad
    Then I see kitchen timer tumbler view
    Then I click on numpad button on vertical tumbler screen
    Then I set "<minutes>" on numpad
    Then I click on Start on numpad
    Examples:
    |minutes|
    |01      |

  @kitchenTimer
  Scenario Outline: Test 7: KT widget validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Examples:
      |minutes|seconds|
      |01      |20     |

  @kitchenTimer
  Scenario Outline: Test 8: KT widget pause button click validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click on pause button
    Examples:
      |minutes|seconds|
      |01      |20     |

  @kitchenTimer
  Scenario Outline: Test 9: KT widget add one min button click validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click on 1 min button
    Examples:
      |minutes|seconds|
      |01      |20     |

  @kitchenTimer
  Scenario Outline: Test 10: KT widget cacel button click validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click cancel timer button
    Examples:
      |minutes|seconds|
      |01      |20     |

  @kitchenTimer
  Scenario Outline: Test 11: Cancel timer popup validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click cancel timer button
    Then I see cancel timer popup
    Then I validate cancel timer popup
    Then I click on no button
    Then I see KT widget
    Then I click cancel timer button
    Then I see cancel timer popup
    Then I click on yes button
    Examples:
      |minutes|seconds|
      |01      |20     |

  @kitchenTimer
  Scenario Outline: Test 12: KT widget back button click validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click on back button on preview screen
    Then I see the clock screen with running KT
    Examples:
      |minutes|seconds|
      |01      |20     |

  @kitchenTimer
  Scenario Outline: Test 13: KT widget add timer button click validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click on add timer button on KT widget
    Then I see kitchen timer tumbler view
    Examples:
      |minutes|seconds|
      |01      |20     |

  @kitchenTimer
  Scenario Outline: Test 14:Add two kitchen timers
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click on add timer button on KT widget
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT tumbler
    Examples:
      |minutes|seconds|
      |01      |20     |



  @kitchenTimer
  Scenario Outline: Test 15:Pause second timer
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click on add timer button on KT widget
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT tumbler
    Then I see KT widget
    Then I click on pause button on second timer
    Examples:
      |minutes|seconds|
      |01      |20     |

  @kitchenTimer
  Scenario Outline: Test 16:Add one min on second timer
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click on add timer button on KT widget
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT tumbler
    Then I see KT widget
    Then I click on 1 min button on second timer
    Examples:
      |minutes|seconds|
      |01      |20     |

  @kitchenTimer
  Scenario Outline: Test 17:Cancel second timer
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click on add timer button on KT widget
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT tumbler
    Then I see KT widget
    Then I click cancel timer button on second timer
    Then I click on no button
    Then I see KT tumbler
    Then I click cancel timer button on second timer
    Then I click on yes button
    Then I see KT tumbler
    Examples:
      |minutes|seconds|
      |01      |20     |


  @kitchenTimer
  Scenario Outline: Test 18: KT widget back button click validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I click on back button on preview screen
    Then I see the clock screen with running KT
    And I click on kitchen timer
    And I see KT tumbler
    Examples:
      |minutes|seconds|
      |01      |20     |

  @kitchenTimer
  Scenario Outline: Test 19: Timer completed popup validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I wait for "<seconds>" till timer completes
    Then I see the timer is completed popup
    Then I validate timer is completed popup
    Examples:
      |minutes|seconds|
      |00     |05     |

  @kitchenTimer
  Scenario Outline: Test 20: Timer completed popup repeat button validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I wait for "<seconds>" till timer completes
    Then I see the timer is completed popup
    Then I click on repeat button
    Then I see KT tumbler
    Examples:
      |minutes|seconds|
      |00     |12     |

  @kitchenTimer
  Scenario Outline: Test 21: Timer completed popup dismiss button validation
    Then I see kitchen timer tumbler view
    Then I set KT to "<minutes>" and "<seconds>"
    Then I click on Start
    Then I see KT widget
    Then I wait for "<seconds>" till timer completes
    Then I see the timer is completed popup
    Then I click on dismiss button
    Examples:
      |minutes|seconds|
      |00     |12     |


