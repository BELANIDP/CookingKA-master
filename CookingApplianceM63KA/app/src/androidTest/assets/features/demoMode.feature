Feature: Cooking - Demo Mode flows
  Test Demo Mode flow

  Background:
    Given App has started
    And Demo mode settings screen has started
    And I navigate to demo mode settings screen
    And I click on demo mode

  @demoMode @success
  Scenario Outline: Test1: Demo Mode Entry scenario
    And I navigate to demo mode instructions screen
    Then I check demo instructions screen header "<titleText>" text
    And I validate description text on demo mode instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    Then I click on next button of demo mode code screen
    Then I see demo mode landing screen
    Examples:
      | titleText         |demoCode |
      | Showcase in Store |2345     |

  @demoMode @success
  Scenario Outline: Test2: Check if Control Lock option is disabled in settings menu during demo mode
    And I navigate to demo mode instructions screen
    Then I check demo instructions screen header "<titleText>" text
    And I validate description text on demo mode instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    Then I click on next button of demo mode code screen
    Then I see demo mode landing screen
    Then I click explore product button on demo landing screen
    And I open settings from top drawer
    Then Setting menu is visible
    And I click on Control lock option
    Then I see Control Lock not available notification
    Examples:
      | titleText         |demoCode |
      | Showcase in Store |2345     |


  @demoMode @success
  Scenario Outline: Test3: Check if Remote Enable option is disabled in settings menu during demo mode
    And I navigate to demo mode instructions screen
    Then I check demo instructions screen header "<titleText>" text
    And I validate description text on demo mode instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    Then I click on next button of demo mode code screen
    Then I see demo mode landing screen
    Then I click explore product button on demo landing screen
    And I open settings from top drawer
    Then Setting menu is visible
    And I click on Remote Enable option
    Then I see Remote Enable not available notification
    Examples:
      | titleText         |demoCode |
      | Showcase in Store |2345     |


  @demoMode @success
  Scenario Outline: Test4: Check if Self Clean option is disabled in settings menu during demo mode
    And I navigate to demo mode instructions screen
    Then I check demo instructions screen header "<titleText>" text
    And I validate description text on demo mode instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    Then I click on next button of demo mode code screen
    Then I see demo mode landing screen
    Then I click explore product button on demo landing screen
    And I open settings from top drawer
    Then Setting menu is visible
    And I click on Self Clean option
    Then I see Self Clean not available notification
    Examples:
      | titleText         |demoCode |
      | Showcase in Store |2345     |

  @demoMode @success
  Scenario Outline: Test5: Check if Connect to Network option is disabled in settings menu during demo mode
    And I navigate to demo mode instructions screen
    Then I check demo instructions screen header "<titleText>" text
    And I validate description text on demo mode instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    Then I click on next button of demo mode code screen
    Then I see demo mode landing screen
    Then I click explore product button on demo landing screen
    And I open settings from top drawer
    Then Setting menu is visible
    And I click on Connect to Network option
    Then I see Connect to Network not available notification
    Examples:
      | titleText         |demoCode |
      | Showcase in Store |2345     |

  @demoMode @success
  Scenario Outline: Test6: Check if Temperature Calibration option is disabled in settings menu during demo mode
    And I navigate to demo mode instructions screen
    Then I check demo instructions screen header "<titleText>" text
    And I validate description text on demo mode instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    Then I click on next button of demo mode code screen
    Then I see demo mode landing screen
    Then I click explore product button on demo landing screen
    And I open settings from top drawer
    Then Setting menu is visible
    And I click on Show More in Preference option when demo is enabled
    And I check the preference options when demo is enabled
    And I click on Temp calibration option when demo is enabled
    Then I see Temp calibration option not available notification
    Examples:
      | titleText         |demoCode |
      | Showcase in Store |2345     |

  @demoMode @success
  Scenario Outline: Test7: Check if Network Settings option are disabled in settings menu during demo mode
    And I navigate to demo mode instructions screen
    Then I check demo instructions screen header "<titleText>" text
    And I validate description text on demo mode instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    Then I click on next button of demo mode code screen
    Then I see demo mode landing screen
    Then I click explore product button on demo landing screen
    And I open settings from top drawer
    Then Setting menu is visible
    And I click on Show More in Network Settings option when demo is enabled
    And I check the Network Settings options when demo is enabled
    And I click on Network Settings option when demo is enabled
    Then I see Network Settings option not available notification
    Examples:
      | titleText         |demoCode |
      | Showcase in Store |2345     |

    @demoMode @success
  Scenario Outline: Test8: Check if Enter Service option is disabled in settings menu during demo mode
    And I navigate to demo mode instructions screen
    Then I check demo instructions screen header "<titleText>" text
    And I validate description text on demo mode instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    Then I click on next button of demo mode code screen
    Then I see demo mode landing screen
    Then I click explore product button on demo landing screen
    And I open settings from top drawer
    Then Setting menu is visible
    And I click on Service Diagnostic option when demo is enabled
    And I check the Service Diagnostic Instruction screen when demo is enabled
    And I click on Enter Diagnostic button when demo is enabled
    Then I see Enter Diagnostic option not available notification
    Examples:
      | titleText         |demoCode |
      | Showcase in Store |2345     |

  @demoMode @success
  Scenario Outline: Test9: Timeout from Clock screen to Demo Mode landing screen
    And I navigate to demo mode instructions screen
    Then I check demo instructions screen header "<titleText>" text
    And I validate description text on demo mode instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    Then I click on next button of demo mode code screen
    Then I see demo mode landing screen
    Then I click explore product button on demo landing screen
    Then I check Clock view with Demo mode icon
    And I wait for 10 seconds to see if screen timeouts to demo landing screen
    Then I see demo mode landing screen
    Examples:
      | titleText         |demoCode |
      | Showcase in Store |2345     |

  @demoMode @success
  Scenario Outline: Test10: Demo Mode exit scenario
    And I navigate to demo mode instructions screen
    Then I check demo instructions screen header "<titleText>" text
    And I validate description text on demo mode instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    Then I click on next button of demo mode code screen
    Then I see demo mode landing screen
    Then I click explore product button on demo landing screen
    Then I check Clock view with Demo mode icon
    And I open settings from top drawer
    Then Setting menu is visible
    And I click on Demo Mode option when demo is enabled
    And I navigate to demo mode instructions screen
    And I validate description text on demo mode Exit instructions screen
    And I click on continue button on demo mode instruction screen
    Then I expect it should navigate to demo mode code screen
    Then I enter demo mode code "<demoCode>" on numpad screen
    And I click on next button of demo mode code screen
    Then I see the clock screen with demo mode disabled
    Examples:
      | titleText         |demoCode |
      | Showcase in Store |2345     |


