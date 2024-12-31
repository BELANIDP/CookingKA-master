Feature: Cooking - Self clean flows for single oven
  Test Self clean related features for the single oven

  Background:
    Given The app has started with Single low end Variant
    And Settings screen has started
    And I navigate to settings screen
    And I click on self clean


  # ======================================================================= #
  # =========================  Soil Level Screen Test Cases =============== #
  # ======================================================================= #


  @selfCleanSingleOven @soilLevel @success
  Scenario Outline: Test 1: To verify the soil level screen content
    And I select the "<cavity>"
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
    Then I check soil level screen header cavity icon is not visible
    And I click on soil level screen header back arrow
    Then I expect it should navigate to previous screen
    Examples:
      | cavity | title      |
      | upper  | Soil Level |



  # --------------------- Soil Level Type Low ---------------------------- #
  # --------------------- Soil Level Type Medium ------------------------- #
  # --------------------- Soil Level Type High --------------------------- #

  @selfCleanSingleOven @soilLevel @success
  Scenario Outline: Test 2: To verify the soil level self clean type
    And I select the "<cavity>"
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
      | cavity | soilLevel |
      | upper  | low       |
      | upper  | medium    |
      | upper  | high      |


  # ======================================================================= #
  # ====================== Instructions Screen Test Cases ================= #
  # ======================================================================= #


  @selfCleanSingleOven @instructions @success
  Scenario Outline: Test 3: To verify the instructions screen content
    And I select the "<cavity>"
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
    Then I check instructions screen header cavity icon is not visible
    Then I check instructions screen vertical scroll is enabled
    And I click on instructions screen header back arrow
    Then I expect it should navigate to soil level screen
    Examples:
      | cavity | soilLevel | title        |
      | upper  | low       | Instructions |
      | upper  | medium    | Instructions |
      | upper  | high      | Instructions |



  # --------------------- Instructions Scroll Behaviour ------------------------------- #

  @selfCleanSingleOven @instructions @success
  Scenario Outline: Test 4: To verify the instructions screen vertical scroll content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    Then I check instructions screen vertical scroll is enabled
    Then I expect instructions screen vertical scroll downwards should working
    Then I expect instructions screen vertical scroll upwards should working
    Examples:
      | cavity | soilLevel |
      | upper  | low       |
      | upper  | medium    |
      | upper  | high      |


  # --------------------- Instructions Description Appearance ------------------------- #

  @selfCleanSingleOven @instructions @success
  Scenario Outline: Test 5: To verify the instructions screen description content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    Then I check instructions screen description text
    Then I check instructions screen description color
    Then I check instructions screen description size
    Examples:
      | cavity | soilLevel |
      | upper  | low       |
      | upper  | medium    |
      | upper  | high      |



  # --------------------- Instructions Navigation --------------------------- #

  @selfCleanSingleOven @instructions @success
  Scenario Outline: Test 6: To verify the instruction next button text
    And I select the "<cavity>"
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
      | cavity | soilLevel |
      | upper  | low       |
      | upper  | medium    |
      | upper  | high      |



  # ======================================================================= #
  # ====================== Prepare Oven Screen Test Cases ================= #
  # ======================================================================= #


  @selfCleanSingleOven @prepareOven @success
  Scenario Outline: Test 7: To verify the prepare oven screen
    And I select the "<cavity>"
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
      | cavity | soilLevel |
      | upper  | low       |


# ------------------ Prepare Oven Description Appearance ---------------- #


  @selfCleanSingleOven @prepareOven @success
  Scenario Outline: Test 8: To verify the prepare oven description text content
    And I select the "<cavity>"
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
      | cavity | soilLevel |
      | upper  | low       |



  # ------------------ Prepare Oven Tapping Behaviour ---------------- #

  @selfCleanSingleOven @prepareOven @success
  Scenario Outline: Test 9: To verify the prepare oven screen tapping on the black area will not take user back to the previous screen
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I tap on black area of the screen
    Then I check it should not take user back to the previous screen from prepare oven screen
    Examples:
      | cavity | soilLevel |
      | upper  | low       |


  # ------------------ Prepare Oven Clean Button Behaviour ---------------- #

  @selfCleanSingleOven @prepareOven @error
  Scenario Outline: Test 10: To verify the prepare oven screen clean button timeout and stop flashing
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I did not perform any action till 30 second
    Then I check clean button timeout and stop flashing
    Examples:
      | cavity | soilLevel |
      | upper  | low       |

  @selfCleanSingleOven @prepareOven @error
  Scenario Outline: Test 11: To verify the prepare oven screen clean button back navigation
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I did not perform any action till 30 second
    Then I check clean button timeout and should navigate to settings screen
    Examples:
      | cavity | soilLevel |
      | upper  | low       |

  @selfCleanSingleOven @prepareOven @error
  Scenario Outline: Test 12: To verify the prepare oven screen clean button forward navigation
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    Then I expect it should navigate to door hasn't opened closed screen
    Examples:
      | cavity | soilLevel |
      | upper  | low       |



  # ======================================================================= #
  # =============== Door has opened closed Screen Test Cases ============== #
  # ======================================================================= #

  @selfCleanSingleOven @doorHasOpenedClosed @success
  Scenario Outline: Test 13: To verify the door has opened and closed screen content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    Then I expect the door has opened and closed screen should visible
    Then I check door has opened closed screen title "<title>" text
    Then I check door has opened closed screen title color
    Then I check door has opened closed screen title size
    Examples:
      | cavity | soilLevel | title       |
      | upper  | low       | Press Start |



# --------------- Door has opened closed Screen Description Appearance ------------ #

  @selfCleanSingleOven @doorHasOpenedClosed @success
  Scenario Outline: Test 14: To verify the door has opened closed screen description text content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    Then I check door has opened closed screen description "<notification>" text
    Then I check door has opened closed screen description color
    Then I check door has opened closed screen description size
    Then I check door has opened closed screen description view is not clickable
    Then I check door has opened closed screen description view is enabled
    Examples:
      | cavity | soilLevel | notification                                                          |
      | upper  | low       | Press DELAY to schedule a Self Clean or START to\nbegin. |



  # ------------- Door has opened closed Screen Start Button Navigation -------------------- #

  @selfCleanSingleOven @doorHasOpenedClosed @success
  Scenario Outline: Test 15: To verify the door has opened closed screen start button text content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    Then I check door has opened closed screen start "<button>" text
    Then I check door has opened closed screen start button text color
    Then I check door has opened closed screen start button text size
    Then I check door has opened closed screen start button view is enabled
    Then I check door has opened closed screen start button view is clickable
    Examples:
      | cavity | soilLevel | button |
      | upper  | low       | START  |


  @selfCleanSingleOven @doorHasOpenedClosed @success
  Scenario Outline: Test 16: To verify the door has opened closed screen start button navigation
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    And I click on start button on door has opened closed screen
    Then I expect it should navigate to locking oven door screen
    Examples:
      | cavity | soilLevel |
      | upper  | medium    |



 # ------------- Door has opened closed Screen DELAY Button Navigation -------------------- #

  @selfCleanSingleOven @doorHasOpenedClosed @success
  Scenario Outline: Test 17: To verify the door has opened closed screen delay button text content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    Then I check door has opened closed screen delay "<button>" text
    Then I check door has opened closed screen delay button text color
    Then I check door has opened closed screen delay button text size
    Then I check door has opened closed screen delay button view is enabled
    Then I check door has opened closed screen delay button view is clickable
    Examples:
      | cavity | soilLevel | button |
      | upper  | low       | DELAY  |


  @selfCleanSingleOven @doorHasOpenedClosed11 @success
  Scenario Outline: Test 18: To verify the door has opened closed screen delay button navigation
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    And I click on delay button on door has opened closed screen
    Then I expect it should navigate to start after delay screen
    Examples:
      | cavity | soilLevel |
      | upper  | medium    |

  @selfCleanSingleOven @doorHasOpenedClosed11 @success
  Scenario Outline: Test 19: To verify the delay screen content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    And I click on delay button on door has opened closed screen
    Then I check delay tumbler screen content
    Examples:
      | cavity | soilLevel |
      | upper  | medium    |

 # ------------------ Door has opened closed Screen Behaviour ---------------- #

  @selfCleanSingleOven @doorHasOpenedClosed @error
  Scenario Outline: Test 20: To verify the door has opened and closed screen timeout
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    And I navigate to door has opened and closed screen
    And I did not perform any action
    Then I check global sleep timeout
    Examples:
      | cavity | soilLevel |
      | upper  | low       |



  # ======================================================================= #
  # =============== Door hasn't opened closed Screen Test Cases ============== #
  # ======================================================================= #

  @selfCleanSingleOven @doorHasntOpenedClosed @success
  Scenario Outline: Test 21: To verify the door hasn't opened and closed content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    Then I expect the door hasn't opened and closed screen should visible
    Then I check door hasn't opened closed screen title "<title>" text
    Then I check door hasn't opened closed screen title color
    Then I check door hasn't opened closed screen title size

    Examples:
      | cavity | soilLevel | title        |
      | upper  | low       | Prepare oven |



  # --------------- Door hasn't opened closed Screen Description Appearance ------------ #

  @selfCleanSingleOven @doorHasntOpenedClosed @success
  Scenario Outline: Test 22: To verify the door hasn't opened closed screen description text content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    Then I check door hasn't opened closed screen description "<description>" text
    Then I check door hasn't opened closed screen description color
    Then I check door hasn't opened closed screen description size
    Then I check door hasn't opened closed screen description view is not clickable
    Then I check door hasn't opened closed screen description view is enabled


    Examples:
      | cavity | soilLevel | description                                           |
      | upper  | low       | Before starting, please open and close the oven door. |


  # ------------------ Door hasn't opened closed Screen Behaviour ---------------- #

  @selfCleanSingleOven @doorHasntOpenedClosed @error
  Scenario Outline: Test 23: To verify the door hasn't opened and closed screen timeout
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I navigate to door hasn't opened and closed screen
    And I did not perform any action
    Then I check global sleep timeout
    Examples:
      | cavity | soilLevel |
      | upper  | low       |


 # ------------- Door hasn't opened closed Screen Navigation -------------------- #

  @selfCleanSingleOven @doorHasntOpenedClosed @success
  Scenario Outline: Test 24: To verify the door hasn't opened closed screen navigation
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I did not perform any action
    And I navigate to door hasn't opened and closed screen
    And I open and close the door of "<cavity>"
    And I click on start button on door has opened closed screen
    Then I expect it should navigate to locking oven door screen
    Examples:
      | cavity | soilLevel |
      | upper  | medium    |




  # ======================================================================= #
  # =============== Locking Oven Door Test Cases ============== #
  # ======================================================================= #


  # --------------- Locking Oven Door Title Appearance ------------ #


  @selfCleanSingleOven @lockingOvenDoor @success
  Scenario Outline: Test 25: To verify the locking oven door screen content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
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
      | upper  | low       | Locking Oven Door |



 # --------------- Locking Oven Door Notification Appearance ------------ #

  @selfCleanSingleOven @lockingOvenDoor @success
  Scenario Outline: Test 26: To verify the locking oven door screen notification text
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
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
      | upper  | low       | Please do not try to open the door. |



# --------------- Locking Oven Door Behaviour ------------ #

  @selfCleanSingleOven @lockingOvenDoor @success
  Scenario Outline: Test 27: To verify the locking oven door screen tapping on the black area will not take user back to the previous screen
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    And I unlocked the "<cavity>" door
    And I click on start button on door has opened closed screen
    And I navigate to locking oven door screen
    And I tap on black area of the screen
    Then I check it should not take user back to the previous screen from locking oven screen
    Examples:
      | cavity | soilLevel |
      | upper  | low       |



  # --------------- Locking Oven Door Navigation ------------ #

  @selfCleanSingleOven @lockingOvenDoor @success
  Scenario Outline: Test 28: To verify the locking oven door screen navigation
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    And I unlocked the "<cavity>" door
    And I click on start button on door has opened closed screen
    And I navigate to locking oven door screen
    Then I check after defined time it should navigate to self clean status screen
    Examples:
      | cavity | soilLevel |
      | upper  | low       |


 # ======================================================================= #
  # =============== Self Clean Status Test Cases ============== #
  # ======================================================================= #

  @selfCleanSingleOven @selfCleanCompleted11 @success
  Scenario Outline: Test 30: To verify the self clean complete screen content
    And I select the "<cavity>"
    And I navigate to soil level screen from single oven mode
    And I select the soil level as "<soilLevel>"
    And I click on next button on soil level selection screen
    And I navigate to instructions screen
    And I click on next button on instructions selection screen
    And I navigate to prepare oven screen
    And I click on HMI key clean button on variant "<cavity>"
    And I open and close the door of "<cavity>"
    And I click on start button on door has opened closed screen
    And I navigate to locking oven door screen
    And After defined time
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
      | upper  | medium    | Self Clean Complete | Caution: Oven may still be hot. Wipe down any residue and place racks back in the oven. |
