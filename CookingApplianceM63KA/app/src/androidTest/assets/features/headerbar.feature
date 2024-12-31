Feature: Headerbar
  Test Headerbar Widget

  Background:
    Given App has started for header screen
    Then I navigate to header screen
    Then I see Headerbar views

  @header @success
  Scenario: Test 1 : Splash screen to Headerbar
    Then I see Headerbar views

  @header @success
  Scenario: Test 2 : Left Icon Visible Single Header
    Then I see Left Icon Single Header

  @header @success
  Scenario: Test 3 : Left Icon Clickable single header
    And I see Left Icon Single Header
    And I click on Left Icon Single Header

  @header @success
  Scenario: Test 4 : Oven Icon Visible
    Then I see Oven Icon

  @header @success
  Scenario: Test 5 : Oven Icon Clickable
    And I see Oven Icon
    Then I click on Oven Icon

  @header @success
  Scenario: Test 6 : Right Icon Visible Single Header
    Then I see Right Icon Single Header

  @header @success
  Scenario: Test 7 : Right Icon Clickable Single Header
    And I see Right Icon Single Header
    And I click on Right Icon Single Header

  @header @success
  Scenario: Test 8 : Left Icon Visible Double Header
    Then I see Left Icon Double Header

  @header @success
  Scenario: Test 9 : Left Icon Clickable Double Header
    And I see Left Icon Double Header
    And I click on Left Icon Double Header

  @header @success
  Scenario: Test 10 : Right Icon Visible Double Header
    Then I see Right Icon Double Header

  @header @success
  Scenario: Test 11 : Right Icon Clickable Single Header
    And I see Right Icon Single Header
    And I click on Right Icon Single Header

  @header @success
  Scenario: Test 12 : Title text Visible Single Header
    Then I see Title text Single Header
  @header @success
  Scenario: Test 13 : Title text Visible Double Header
    Then I see Title text Double Header

  @header @success
  Scenario: Test 14 : Title text Visible Single Header Size Validation
    Then I see Title text Single Header
    Then I validate Title size of Single Header

  @header @success
  Scenario: Test 15 : Title text Visible Double Header Size Validation
    Then I see Title text Double Header
    Then I validate Title size of Double Header
#
  @header @success
  Scenario: Test 16 : Title text Visible Single Header Color Validation
    Then I see Title text Single Header
    Then I validate Title color of Single Header
#
  @header @success
  Scenario: Test 16 : Title text Visible Double Header Color Validation
    Then I see Title text Double Header
    Then I validate Title color of Double Header

  @header @success
  Scenario: Test 17 : Title text Visible Single Header Font Family Validation
    Then I see Title text Single Header
    Then I validate Title Font Family of Single Header

  @header @success
  Scenario: Test 18 : Title text Visible Double Header Font Family Validation
    Then I see Title text Double Header
    Then I validate Title Font Family of Double Header

  @header @success
  Scenario: Test 19 : Left Icon Single Header Size Validation
    Then I see Left Icon Single Header
    Then I validate Size of Left Icon of Single Header

  @header @success
  Scenario: Test 20 : Left Icon Double Header Size Validation
    Then I see Left Icon Double Header
    Then I validate Size of Left Icon of Double Header

  @header @success
  Scenario: Test 21 : Oven Cavity Icon Size Validation
    Then I see Oven Icon
    Then I validate Size of Oven Icon

  @header @success
  Scenario: Test 22 : Right Icon Single Header size validation
    Then I see Right Icon Single Header
    Then I validate Size of Right Icon of Single Header

  @header @success
  Scenario: Test 23 : Right Icon Double Header size validation
     Then I see Right Icon Double Header
    Then I validate Size of Right Icon of Double Header

  @header @success
  Scenario: Test 24 : Status Icon 1 visible
    Then I see Status Icon 1

  @header @success
  Scenario: Test 25 : Status Icon 1 visible
    Then I see Status Icon 2

  @header @success
  Scenario: Test 26 : Status Icon 1 visible
    Then I see Status Icon 3

  @header @success
  Scenario: Test 27 : Status Icon 1 visible
    Then I see Status Icon 4

  @header @success
  Scenario: Test 28 : Horizontal Line visible
    Then I see horizontal line

  @header @success
  Scenario: Test 29 : Time is visible
     Then I see Time

  @header @success
  Scenario: Test 30 : Info Icon size validation
    Then I see Info Icon
    Then I validate Size of Info Icon

  @header @success
  Scenario: Test 31 : Status Icon size validation
    Then I see Status Icon 1
    Then I validate Size of Status Icon

  @header @success
  Scenario: Test 32 : Horizontal Line size validation
    Then I see horizontal line
    Then I validate Size of Horizontal Line

  @header @success
  Scenario: Test 33 : Clock view size validation
    Then I see Time
    Then I validate Size of clock view




