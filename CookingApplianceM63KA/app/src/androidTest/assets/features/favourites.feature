Feature: Cooking - Favorites flows for double oven
  Test Favorites related features for the double oven

  Background:
    Given The app has started with Double low end Variant
    And I am on the Clock screen
    And I click and navigate to the cavity selection screen
    And I see the cavity selection screen

  # ======================================================================= #
  # =========================  Favorites Screen Test Cases  =============== #
  # ======================================================================= #

#----------------adding favorites manually (manual modes), running a favorite cycle----------------#
  @favorites
  Scenario Outline: Test 1: To verify adding favorites manually using manual modes with back navigation
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I see Favorites list screen with no favorites added
    Then I click back button
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I click on plus icon to Add Favorites
    Then I see Favorites screen
    Then I click back button
    Then I see Favorites list screen with no favorites added
    Then I click on plus icon to Add Favorites
    Then I see Favorites screen
    Then I click on Manual modes
    Then I see Create a favorite screen  with horizontal tumbler
    Then I click back button
    Then I see Favorites screen
    Then I click on Manual modes
    Then I see Create a favorite screen  with horizontal tumbler
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I see numpad view for setting temperature
    Then I click back button on numpad view
    Then I see Create a favorite screen  with horizontal tumbler
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I see numpad view for setting temperature
    Then I click on tumbler icon on numpad screen
    Then I see horizontal temperature tumbler
    Then I click on numpad icon
    Then I see numpad view for setting temperature
    Then I click on tumbler icon on numpad screen
    Then I see horizontal temperature tumbler
    Then I click back button
    Then I see Create a favorite screen  with horizontal tumbler
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I see numpad view for setting temperature
    Then I click next button on the temperature setting numpad view
    Then I see numpad view for setting cook time
    Then I click back button on numpad view
    Then I see numpad view for setting temperature
    Then I set the temperature "<temp>" on numpad view for setting temperature
    Then I click next button on the temperature setting numpad view
    Then I see numpad view for setting cook time
    Then I click on tumbler icon on numpad screen
    Then I see vertical tumbler for cooktime
    Then I click on numpad icon
    Then I see numpad view for setting cook time
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click back button
    Then I see numpad view for setting cook time
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    Then I see Favorites list screen and the recipe "<name>" is added to favorites
    Then I click on the recent added favorites cycle
    Then I see details of the favorites recipe screen
    Then I click on Start button on details of the favorites recipe screen
    Then I see recipe status screen
    Then I click on Start Timer button
    Then I see cycle has started
    Then I click Turn OFF button
    And I am on the Clock screen
    Examples:
      | cavity | index | temp | cookTime | name     |
      | upper  | 2     | 185  | 3        | Favorite |


#------------------------------adding favorites manually (auto cook)-------------------------------#
  @favorites
  Scenario Outline: Test 2: Favorites details screen validation for AutoCook with back navigation
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I see Favorites list screen with no favorites added
    Then I click back button
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I click on plus icon to Add Favorites
    Then I see Favorites screen
    Then I click back button
    Then I see Favorites list screen with no favorites added
    Then I click on plus icon to Add Favorites
    Then I see Favorites screen
    Then I click on AutoCook
    Then I see Food Type screen of Favorites
    Then I click back button
    Then I see Favorites screen
    Then I click on AutoCook
    Then I see Food Type screen of Favorites
    Then I scroll and click on meat
    Then I see all the Meat recipes
    Then I scroll and click on required "<index>"
    Then I see numpad view for setting temperature
    Then I set the temperature "<temp>" on numpad view for setting temperature
    Then I click next button on the temperature setting numpad view
    Then I see the Doneness tumbler screen
    Then I scroll Doneness level tumbler to "<level>"
    Then I click on Next button on the Doneness tumbler screen
    Then I see details of the favorites recipe screen
    Then I click on Save button
    Then I see Favorites list screen and the recipe "<name>" is added to favorites
    Examples:
      | cavity | index | level  | name     | temp |
      | lower  | 1     | Medium | Favorite | 190  |


#-----------------------------------editing favorites ---------------------------------------------#
  @favorites
  Scenario Outline: Test 3: Validating Editing Favorites &  Favorite already exists popup
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    Then I see Favorites list screen and the recipe "<name>" is added to favorites
    Then I click on the existing Favorites
    Then I click on Mode parameter
    Then I see the horizontal tumbler screen to set Mode parameter
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Oven temperature parameter
    Then I see numpad view for setting temperature
    Then I click next button on the temperature setting numpad view
    Then I see details of the favorites recipe screen
    Then I scroll and click on Time parameter "<parameter>"
    Then I see numpad view for setting cook time
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I check Delay button
    Then I check Start button
    Then I click on Delay button
    Then I expect it should navigate to start after delay screen
    Examples:
      | cavity | index | cookTime | name     | parameter |
      | upper  | 2     | 3        | Favorite | 2         |


#--------------------------------Adding Image, Leave Image Selection Popup ------------------------------------#
  @favorites
  Scenario Outline: Test 4: Favorites Adding image with back navigation
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Choose Image button
    Then I see choose an image screen
    Then I click back button
    Then I see details of the favorites recipe screen
    Then I click on Choose Image button
    Then I scroll and click any image to "<image>"
    Then I see Image tumbler
    Then I click back button
    Then I see Leave Image Selection Popup
    #-------checking popup appearance--------------#
    Then I check the Leave Image Selection Popup title text
    Then I check the Leave Image Selection Popup title view
    Then I check the Leave Image Selection Popup description text
    Then I check the Leave Image Selection Popup description view
    Then I check the YES button view
    Then I check the YES button text
    Then I check the NO button view
    Then I check the NO button text
    #-----------------------------------------------#
    Then I click Yes button on the Leave Image Selection Popup
    Then I see details of the favorites recipe screen
    Then I click on Choose Image button
    Then I scroll and click any image to "<image>"
    Then I see Image tumbler
    Then I scroll and click any image to "<image>"
    Then I click on Set button
    Then I see details of the favorites recipe screen with Image updated
    Then I click on Update Image button
    Then I scroll and click any image to "<image>"
    Then I see Image tumbler
    Then I click on Set button
    Then I click on Save button
    Then I see the image is set
    Then I see Favorites list screen and the recipe "<name>" is added to favorites
    Examples:
      | cavity | index | cookTime | image | name     |
      | upper  | 2     | 3        | 1     | Favorite |


#------------saving a favorite while running a cycle & Favorite already exist and user taps on favorite icon while cycle is running ------------------#
  @favorites
  Scenario Outline: Test 5: Favorites details screen validation for adding a fav while running a cycle
    And I select the required "<cavity>"
    Then I scroll tumbler to targetText "<menu>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal temperature tumbler
    Then I click on start button
    Then I see status screen with upper running
    Then I click on three dots icon
    And I see more options popup
    Then I click on Favorites on More Options popup
    Then I see recipe saved as favorites notification
    Then I check the notification text view
    Then I wait for ten seconds for the notification to go off
    Then I click on three dots icon
    Then I click on Favorites on More Options popup
    Then I see recipe already as favorites notification
    Examples:
      | cavity | menu |
      | upper  | Bake |


  @favorites
  Scenario Outline: Test 6: Appearance of Favorites list screen with no favorites added
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I see Favorites list screen with no favorites added
    Then I check the back button
    Then I check the plus icon
    Then I check the Header title text view of Favorites list screen with no favorites added
    Then I check the Favorites list screen with no favorites added header title text
    Then I check the description text view of Favorites list screen with no favorites added
    Then I check the Favorites list screen with no favorites added description text view
    Examples:
      | cavity |
      | upper  |


  @favorites
  Scenario Outline: Test 7: Appearance of Favorites screen
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I see Favorites list screen with no favorites added
    Then I click on plus icon to Add Favorites
    Then I see Favorites screen
    Then I check the back button
    Then I check the Header title text view of Favorites screen
    Then I check the Favorites screen Header title text
    Then I check the Manual modes title text view
    Then I check the Auto Cook title text view
    Then I check the History title text view
    Then I check the Manual modes title text
    Then I check the Auto Cook title text
    Then I check the History title text

    Examples:
      | cavity |
      | lower  |


  @favorites
  Scenario Outline: Test 8: Appearance of Create a Favorites screen
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I see Favorites list screen with no favorites added
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I see Create a favorite screen  with horizontal tumbler
    Then I check the Header title text view of Create a Favorites screen
    Then I check the Create a Favorites screen Header title text
    Then I check the back button
    Examples:
      | cavity |
      | lower  |


  @favorites
  Scenario Outline: Test 9: Validating Appearance of Favorites Preview screen
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I see Favorites list screen with no favorites added
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I check the Header title text view of Favorites Preview screen
    Then I check the Favorites Preview screen Header title text
    Then I check the back button
    Then I check the Edit icon
    Then I check the Mode parameter view
    Then I check the Mode parameter text view
    Then I check the Mode parameter subtitle text view
    Then I check the Oven Temperature parameter view
    Then I check the Oven Temperature parameter text view
    Then I check the Oven Temperature parameter subtitle text view
    Then I check the Time parameter view
    Then I check the Time parameter text view
    Then I check the Time parameter subtitle text view
    Then I check the Choose Image button view
    Then I check the Choose Image button text
    Then I check the Save button view
    Then I check the Save button text
    Examples:
      | cavity | index | cookTime |
      | lower  | 2     | 3        |


  @favorites
  Scenario Outline: Test 10: Validating Appearance of Choose an Image screen
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I see Favorites list screen with no favorites added
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I click on Choose Image button
    Then I see choose an image screen
    Then I check the header title view of Choose an Image screen
    Then I check the header title text view of Choose an Image screen
    Then I check the back button
    Then I check the Image Tumbler of Choose an Image screen
    Examples:
      | cavity | index | cookTime |
      | lower  | 2     | 3        |


  @favorites
  Scenario Outline: Test 11: Validating Appearance of Image tumbler screen
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I see Favorites list screen with no favorites added
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I click on Choose Image button
    Then I scroll and click any image to "<image>"
    Then I check the header title of Image tumbler screen
    Then I check the header title text of Image tumbler screen
    Then I check the back button
    Then I check the Set button view
    Then I check the Set button text
    Then I check the left holder arrow
    Then I check the right holder arrow
    Then I check the Stepper bar
    Then I click on Set button
    Then I click on Save button
    Then I see Favorites list screen and the recipe "<name>" is added to favorites
    Then I check the Newly Added Recipe view
    Then I check the Newly Added Recipe oven icon
    Examples:
      | cavity | index | cookTime | image | name     |
      | lower  | 2     | 3        | 1     | Favorite |


#----------------------probe not connected and user starts probe recipe ---------------------------#
  @favorites
  Scenario Outline: Test 12: Probe cycle navigation & appearance validation for favorites
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll tumbler to targetText "<menu>" and click
    Then I click on Probe
    Then I click on Bake
    Then I click next button on the temperature setting numpad view
    Then I click next button on probe temp tumbler
    Then I see Favorites preview screen for probe
    Then I validate the Probe temperature parameter view
    Then I validate the Probe temperature parameter text
    Then I validate the Probe temperature parameter subtitle view
    Then I validate the Probe temperature parameter subtitle text
    Then I check oven temp parameter
    Then I click on Save button
    Then I see Favorites list screen and the recipe "<name>" with the probe icon
    Then I click on the recent added favorites cycle
    Then I see Favorites preview screen for probe
    Then I click on Start button on details of the favorites recipe screen
    Then I see insert temp probe popup
    And I validate the insert temp probe popup
    And I insert a probe in upper cavity

    Examples:
      | cavity | name     | menu       |
      | upper  | Favorite | More Modes |


#----------------------Multiple favorites view & Max Limit reached popup for favorite reached ---------------------------#
  @favorites
  Scenario Outline: Test 13: Multiple favorites view and Max Limit reached appearance and validation
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    #1
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    #2
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I set the temperature to 180 degrees
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    #3
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime 5 min
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    #4
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I set the temperature to 190 degrees
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    #5
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to 1 min
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    #6
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I set the temperature to 200 degrees
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    #7
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to 7 min
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    #8
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I set the temperature to 210 degrees
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    #9
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to 10 min
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    #10
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I set the temperature to 235 degrees
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    #11
    Then I click on plus icon to Add Favorites
    Then I see Max Limit reached popup
    Then I check Max Limit reached popup Title view
    Then I check Max Limit reached popup Title text
    Then I check Max Limit reached popup description view
    Then I check Max Limit reached popup description text
    Then I check Max Limit reached popup OKAY button
    Then I click on OK button
    Then I see multiple favorites added view
    Examples:
      | cavity | index | cookTime |
      | upper  | 2     | 3        |


#---------------------------------------------From History------------------------------------------#
  @favorites
  Scenario Outline: Test 14: Saving favorites from history and validating its appearance
    And I select the required "<cavity>"
    Then I scroll tumbler to targetText "<menu>" and click
    Then I see Instruction screen
    Then I click on next button on recipe instructions selection screen
    Then I see horizontal temperature tumbler
    Then I click on start button
    Then I see status screen with upper running
    Then I click Turn OFF button
    And I am on the Clock screen
    And I click and navigate to the cavity selection screen
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I click on plus icon to Add Favorites
    Then I click on History
    Then I see history screen
    Then I click back button
    Then I see Favorites screen
    Then I click on History
    Then I see history screen
    Then I click on the recently run cycle for history list
    Then I see details of the favorites recipe screen for history
    Then I click back button
    Then I see history screen
    Then I check the History screen header title text
    Then I check the History screen header title view
    Then I check the back button
    Then I check the recent recipe text
    Then I check the recent recipe view
    Then I check the recent recipe subtitle text and view
    Then I check the recent recipe Time view
    Then I check the recent recipe oven icon
    Then I click on the recently run cycle for history list
    Then I see details of the favorites recipe screen for history
    Then I click on Save To Favorites
    Then I click back button
    Then I see details of the favorites recipe screen for history
    Then I click on Save To Favorites
    Then I click on Save button
    Examples:
      | cavity | menu |
      | upper  | Bake |


#--------------Favorite already exist and user taps on favorite icon while cycle is running-----------------------#
  @favorites1
  Scenario Outline: Test 15: Validating Editing Favorites &  Favorite already exists popup
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    Then I see Favorites list screen and the recipe "<name>" is added to favorites
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    Then I see Favorite already exists popup
    #-------checking Favorite already exists popup appearance--------------#
    Then I check the Favorite already exists Popup title text
    Then I check the Favorite already exists Popup title view
    Then I check the Favorite already exists Popup description text
    Then I check the Favorite already exists Popup description view
    Then I check the OK button view
    Then I check the OK button text
    #----------------------------------------------------------------------#
    Then I click the OK button on the Favorite already exists popup
    Then I see Favorites list screen and the recipe "<name>" is added to favorites
    Examples:
      | cavity | index | cookTime | name     |
      | upper  | 2     | 3        | Favorite |


#------------------------------Removing and deleting favorites----------------------------------#
  @favorites
  Scenario Outline: Test 16: Validating Removing and deleting favorites
    And I select the required "<cavity>"
    Then I see the horizontal tumbler screen
    Then I click on Favorites
    Then I click on plus icon to Add Favorites
    Then I click on Manual modes
    Then I scroll the horizontal tumbler to the "<index>" and click
    Then I click next button on the temperature setting numpad view
    Then I set the CookTime to "<cookTime>" on numpad view of setting cooktime
    Then I click next button on the cooktime setting numpad view
    Then I see details of the favorites recipe screen
    Then I click on Save button
    Then I see Favorites list screen and the recipe "<name>" is added to favorites
    Then I long click for 3 seconds on the recent added favorites cycle
    Then I see delete and cancel icon
    Then I click on delete icon
    Then I see delete favorites popup
#-------checking Favorite already exists popup appearance--------------#
    Then I check the Delete Favorites Popup title text
    Then I check the Delete Favorites Popup title view
    Then I check the Delete Favorites Popup description text
    Then I check the Delete Favorites Popup description view
    Then I check the YES button view
    Then I check the YES button text
    Then I check the NO button text
    Then I check the NO button view
#----------------------------------------------------------------#
    Then I click on NO button
    Then I long click for 3 seconds on the recent added favorites cycle
    Then I click on delete icon
    Then I see delete favorites popup
    Then I click on OK button
    Then I see Favorites list screen with no favorites added
    Examples:
      | cavity | index | cookTime | name     |
      | upper  | 2     | 3        | Favorite |































