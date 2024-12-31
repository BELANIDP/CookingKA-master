Feature: Cooking - Self clean flows for combo
  Test Self clean related features for the Combo

  Background:
    Given The app has started with Combo low end Variant
    And Settings screen has started for combo
    And I navigate to settings screen
    And I click on self clean


  # ======================================================================= #
  # =========================  Soil Level Screen Test Cases =============== #
  # ======================================================================= #


  @selfCleanComboOven @soilLevel @success
  Scenario Outline: Test 1: To verify the soil level screen content
    And I navigate to soil level screen from single oven mode
    Then I check soil level screen header "<title>" text
    Then I check soil level screen header title text size
    Then I check soil level screen header title text alignment
    Then I check soil level screen header title text color
    Then I check soil level screen header title view should not clickable
    Then I check soil level screen header title view is enabled
    Then I check soil level screen header back arrow view is enabled
    Then I check soil level screen header back arrow view is clickable
    Then I check soil level screen header back arrow background
    Then I check soil level screen header cavity icon is visible
    And I click on soil level screen header back arrow
    Examples:
      | title      |
      | Soil Level |



  # --------------------- Soil Level Type Low ---------------------------- #
  # --------------------- Soil Level Type Medium ------------------------- #
  # --------------------- Soil Level Type High --------------------------- #

  @selfCleanComboOven @soilLevel @success
  Scenario Outline: Test 2: To verify the soil level self clean type
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    Then I expect it should display selected soil level clean type
    Then I check soil level screen next button text
    Then I check soil level screen next button color
    Then I check soil level screen next button size
    Then I check soil level screen next button text alignment
    Then I check soil level screen next button is clickable
    Then I check soil level screen next button is enabled
    And I click on next button on soil level selection screen
    Then I expect it should navigate to instructions screen
    Examples:
      | soilLevel |
      | low       |
      | medium    |
      | high      |


  # ======================================================================= #
  # ====================== Instructions Screen Test Cases ================= #
  # ======================================================================= #


  @selfCleanComboOven @instructions @success
  Scenario Outline: Test 3: To verify the instructions screen content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    Then I check instructions screen header "<title>" text
    Then I check instructions screen header title text size
    Then I check instructions screen header title text color
    Then I check instructions screen header title text alignment
    Then I check instructions screen header title view should not clickable
    Then I check instructions screen header title view is enabled
    Then I check instructions screen header back arrow view is enabled
    Then I check instructions screen header back arrow view is clickable
    Then I check instructions screen header back arrow background
    Then I check instructions screen vertical scroll is enabled
    And I click on instructions screen header back arrow
    Then I expect it should navigate to soil level screen
    Examples:
      | soilLevel | title        |
      | low       | Instructions |
      | medium    | Instructions |
      | high      | Instructions |



  # --------------------- Instructions Scroll Behaviour ------------------------------- #

  @selfCleanComboOven @instructions @success
  Scenario Outline: Test 4: To verify the instructions screen vertical scroll content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    Then I check instructions screen vertical scroll is enabled
    Then I expect instructions screen vertical scroll downwards should working
    Then I expect instructions screen vertical scroll upwards should working
    Examples:
      | soilLevel |
      | low       |
      | medium    |
      | high      |


  # --------------------- Instructions Description Appearance ------------------------- #

  @selfCleanComboOven @instructions @success
  Scenario Outline: Test 5: To verify the instructions screen description content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    Then I check instructions screen description text
    Then I check instructions screen description color
    Then I check instructions screen description size
    Examples:
      | soilLevel |
      | low       |
      | medium    |
      | high      |


  # --------------------- Instructions Navigation --------------------------- #

  @selfCleanComboOven @instructions @success
  Scenario Outline: Test 6: To verify the instruction next button text
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    Then I check instructions next button text
    Then I check instructions next button color
    Then I check instructions next button size
    Then I check instructions next button text alignment
    Then I check instructions screen next button is clickable
    Then I check instructions screen next button is enabled
    And I click on next button on instructions selection screen
    Then I expect it should navigate to prepare oven screen
    Examples:
      | soilLevel |
      | low       |
      | medium    |
      | high      |



  # ======================================================================= #
  # ====================== Prepare Oven Screen Test Cases ================= #
  # ======================================================================= #


  @selfCleanComboOven @prepareOven @success
  Scenario Outline: Test 7: To verify the prepare oven screen
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    Then I expect prepare oven screen should visible
    Then I check prepare oven title text
    Then I check prepare oven title color
    Then I check prepare oven title size
    Then I check prepare oven title view should not clickable
    Then I check prepare oven title view is enabled
    Examples:
      | soilLevel |
      | low       |


# ------------------ Prepare Oven Description Appearance ---------------- #


  @selfCleanComboOven @prepareOven @success
  Scenario Outline: Test 8: To verify the prepare oven description text content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    Then I check prepare oven description text
    Then I check prepare oven description color
    Then I check prepare oven description size
    Then I check prepare oven description view should not clickable
    Then I check prepare oven description view is enabled
    Examples:
      | soilLevel |
      | low       |



  # ------------------ Prepare Oven Tapping Behaviour ---------------- #

  @selfCleanComboOven @prepareOven @success
  Scenario Outline: Test 9: To verify the prepare oven screen tapping on the black area will not take user back to the previous screen
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I tap on black area of the screen
    Then I check it should not take user back to the previous screen from prepare oven screen
    Examples:
      | soilLevel |
      | low       |


  # ------------------ Prepare Oven Clean Button Behaviour ---------------- #

  @selfCleanComboOven @prepareOven @error
  Scenario Outline: Test 10: To verify the prepare oven screen clean button timeout and stop flashing
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I did not perform any action till 30 second
    Then I check clean button timeout and stop flashing
    Examples:
      | soilLevel |
      | low       |

  @selfCleanComboOven @prepareOven @error
  Scenario Outline: Test 11: To verify the prepare oven screen clean button back navigation
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I did not perform any action till 30 second
    Then I check clean button timeout and should navigate to settings screen
    Examples:
      | soilLevel |
      | low       |

  @selfCleanComboOven @prepareOven @error
  Scenario Outline: Test 12: To verify the prepare oven screen clean button forward navigation
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    Then I expect it should navigate to door hasn't opened closed screen
    Examples:
      | soilLevel |
      | low       |



  # ======================================================================= #
  # =============== Door has opened closed Screen Test Cases ============== #
  # ======================================================================= #

  @selfCleanComboOven @doorHasOpenedClosed @success
  Scenario Outline: Test 13: To verify the door has opened and closed screen content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    Then I expect the door has opened and closed screen should visible
    Then I check door has opened closed screen title "<title>" text
    Then I check door has opened closed screen title color
    Then I check door has opened closed screen title size
    Examples:
      | cavity | soilLevel | title       |
      | lower  | low       | Press Start |



# --------------- Door has opened closed Screen Description Appearance ------------ #

  @selfCleanComboOven @doorHasOpenedClosed @success
  Scenario Outline: Test 14: To verify the door has opened closed screen description text content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    Then I check door has opened closed screen description "<notification>" text
    Then I check door has opened closed screen description color
    Then I check door has opened closed screen description size
    Then I check door has opened closed screen description view is not clickable
    Then I check door has opened closed screen description view is enabled
    Examples:
      | cavity | soilLevel | notification                                                          |
      | lower  | low       | Press DELAY to schedule a Self Clean or START to\nbegin. |



  # ------------- Door has opened closed Screen Start Button Navigation -------------------- #

  @selfCleanComboOven @doorHasOpenedClosed @success
  Scenario Outline: Test 15: To verify the door has opened closed screen start button text content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    Then I check door has opened closed screen start "<button>" text
    Then I check door has opened closed screen start button text color
    Then I check door has opened closed screen start button text size
    Then I check door has opened closed screen start button view is enabled
    Then I check door has opened closed screen start button view is clickable
    Examples:
      | cavity | soilLevel | button |
      | lower  | low       | START  |


  @selfCleanComboOven @doorHasOpenedClosed @success
  Scenario Outline: Test 16: To verify the door has opened closed screen start button navigation
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    And I click on start button on door has opened closed screen
    Then I expect it should navigate to locking oven door screen
    Examples:
      | cavity | soilLevel |
      | lower  | medium    |



 # ------------- Door has opened closed Screen DELAY Button Navigation -------------------- #

  @selfCleanComboOven @doorHasOpenedClosed @success
  Scenario Outline: Test 17: To verify the door has opened closed screen delay button text content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    Then I check door has opened closed screen delay "<button>" text
    Then I check door has opened closed screen delay button text color
    Then I check door has opened closed screen delay button text size
    Then I check door has opened closed screen delay button view is enabled
    Then I check door has opened closed screen delay button view is clickable
    Examples:
      | cavity | soilLevel | button |
      | lower  | low       | DELAY  |


  @selfCleanComboOven @doorHasOpenedClosed @success
  Scenario Outline: Test 18: To verify the door has opened closed screen delay button navigation
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    And I click on delay button on door has opened closed screen
    Then I expect it should navigate to start after delay screen
    Examples:
      | cavity | soilLevel |
      | lower  | medium    |

  @selfCleanComboOven @doorHasOpenedClosed @success
  Scenario Outline: Test 19: To verify the delay screen content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    And I click on delay button on door has opened closed screen
    Then I check delay tumbler screen content
    Examples:
      | cavity | soilLevel |
      | lower  | medium    |


  @selfCleanComboOven @doorHasOpenedClosed @success
  Scenario Outline: Test 21: To verify the delay screen cancel button navigation
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    And I click on delay button on door has opened closed screen
    Then I check delay tumbler screen content
    Examples:
      | cavity | soilLevel |
      | lower  | medium    |
 # ------------------ Door has opened closed Screen Behaviour ---------------- #

  @selfCleanComboOven @doorHasOpenedClosed @error
  Scenario Outline: Test 22: To verify the door has opened and closed screen timeout
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    And I navigate to door has opened and closed screen
    And I did not perform any action
    Then I check global sleep timeout
    Examples:
      | cavity | soilLevel |
      | lower  | low       |



  # ======================================================================= #
  # =============== Door hasn't opened closed Screen Test Cases ============== #
  # ======================================================================= #

  @selfCleanComboOven @doorHasntOpenedClosed @success
  Scenario Outline: Test 23: To verify the door hasn't opened and closed content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    Then I expect the door hasn't opened and closed screen should visible
    Then I check door hasn't opened closed screen title "<title>" text
    Then I check door hasn't opened closed screen title color
    Then I check door hasn't opened closed screen title size

    Examples:
      | soilLevel | title        |
      | low       | Prepare oven |



  # --------------- Door hasn't opened closed Screen Description Appearance ------------ #

  @selfCleanComboOven @doorHasntOpenedClosed @success
  Scenario Outline: Test 24: To verify the door hasn't opened closed screen description text content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    Then I check door hasn't opened closed screen description "<description>" text
    Then I check door hasn't opened closed screen description color
    Then I check door hasn't opened closed screen description size
    Then I check door hasn't opened closed screen description view is not clickable
    Then I check door hasn't opened closed screen description view is enabled
    Examples:
      | soilLevel | description                                           |
      | low       | Before starting, please open and close the oven door. |


  # ------------------ Door hasn't opened closed Screen Behaviour ---------------- #

  @selfCleanComboOven @doorHasntOpenedClosed @error
  Scenario Outline: Test 25: To verify the door hasn't opened and closed screen timeout
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I navigate to door hasn't opened and closed screen
    And I did not perform any action
    Then I check global sleep timeout
    Examples:
      | soilLevel |
      | low       |



  # ======================================================================= #
  # =============== Locking Oven Door Test Cases ============== #
  # ======================================================================= #


  # --------------- Locking Oven Door Title Appearance ------------ #


  @selfCleanComboOven @lockingOvenDoor @success
  Scenario Outline: Test 27: To verify the locking oven door screen content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    And I unlocked the "<cavity>" door
    And I click on start button on door has opened closed screen
    Then I check locking oven door screen title "<title>" text
    Then I check locking oven door screen title text color
    Then I check locking oven door screen title text size
    Then I check locking oven door screen title text view is not clickable
    Then I check locking oven door screen title text view is enabled
    Examples:
      | cavity | soilLevel | title             |
      | lower  | low       | Locking Oven Door |



 # --------------- Locking Oven Door Notification Appearance ------------ #

  @selfCleanComboOven @lockingOvenDoor @success
  Scenario Outline: Test 28: To verify the locking oven door screen notification text
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    And I unlocked the "<cavity>" door
    And I click on start button on door has opened closed screen
    Then I check locking oven door screen notification "<notification>" text
    Then I check locking oven door screen notification text color
    Then I check locking oven door screen notification text size
    Then I check locking oven door screen notification text view is not clickable
    Then I check locking oven door screen notification text view is enabled
    Examples:
      | cavity | soilLevel | notification                        |
      | lower  | low       | Please do not try to open the door. |



# --------------- Locking Oven Door Behaviour ------------ #

  @selfCleanComboOven @lockingOvenDoor @success
  Scenario Outline: Test 29: To verify the locking oven door screen tapping on the black area will not take user back to the previous screen
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    And I unlocked the "<cavity>" door
    And I click on start button on door has opened closed screen
    And I navigate to locking oven door screen
    And I tap on black area of the screen
    Then I check it should not take user back to the previous screen from locking oven screen
    Examples:
      | cavity | soilLevel |
      | lower  | low       |



  # --------------- Locking Oven Door Navigation ------------ #

  @selfCleanComboOven @lockingOvenDoor @success
  Scenario Outline: Test 30: To verify the locking oven door screen navigation
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    And I unlocked the "<cavity>" door
    And I click on start button on door has opened closed screen
    And I navigate to locking oven door screen
    Then I check after defined time it should navigate to self clean status screen
    Examples:
      | cavity | soilLevel |
      | lower  | low       |


 # ======================================================================= #
  # =============== Self Clean Status Test Cases ============== #
  # ======================================================================= #

  @selfCleanComboOven @selfCleanCompleted11 @success
  Scenario Outline: Test 32: To verify the self clean complete screen content
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant
    And I open and close the door of "<cavity>"
    And I click on start button on door has opened closed screen
    And I navigate to locking oven door screen
    And After defined time from combo variant
    Then I cancel the self clean cycle
#    Then I expect self clean completed screen should visible
#    Then I check self clean status screen cycle completed "<text>" matched
#    Then I check self clean status screen cycle completed text size
#    Then I check self clean status screen cycle completed text color
#    Then I check self clean status screen cycle completed description "<description>" matched
#    Then I check self clean status screen cycle completed description text size
#    Then I check self clean status screen cycle completed description text color
#    Then I check self clean complete cavity icon is not visible
#    Then I check self clean complete icon is enabled
#    Then I check self clean complete icon is not clickable
#    Then I expect self clean completed screen should visible
#    Then I check self clean complete screen OK button is visible
#    Then I check self clean complete screen OK button size
#    Then I check self clean complete screen OK button text color
#    Then I check self clean complete screen OK button is enabled
#    Then I check self clean complete screen OK button is clickable
#    Then I click on OK button on self clean complete screen
#    Then I expect clock screen should visible

    Examples:
      | cavity | soilLevel | text                | description                                                                             |
      | lower  | medium    | Self Clean Complete | Caution: Oven may still be hot. Wipe down any residue and place racks back in the oven. |