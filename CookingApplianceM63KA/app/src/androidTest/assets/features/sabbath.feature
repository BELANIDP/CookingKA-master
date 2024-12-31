Feature: Cooking - Sabbath feature for Double Oven Low End
  Test Sabbath related features for the Double Oven

  Background:
    Given The app has started with Double low end Variant
    And Settings screen has started
    And I navigate to settings screen
    And I click on Sabbath
    Then I see Sabbath mode selection screen


#--------------------------SABBATH MODE SELECTION SCREEN APPEARANCE---------------------
    @Sabbath
    Scenario Outline: Test 1 - Check sabbath header text view
      Then I check the Sabbath header "<title>" text view
      Then I check the Sabbath header text view alignment
      Then I check the Sabbath header text view size
      Then I check the Sabbath header text view color
      Examples:
        | title   |
        | Sabbath |


    @Sabbath
    Scenario Outline: Test 2 - Validating Sabbath Mode text appearance of the Sabbath main screen
      Then I check the Sabbath mode "<title>" text view
      Then I check the Sabbath mode text view size
      Then I check the Sabbath mode text view color
      Then I check the Sabbath mode subtitle "<title1>" text view
      Then I check the Sabbath mode subtitle text view size
      Then I check the Sabbath mode subtitle text view color
      Examples:
        | title        | title1                  |
        | Sabbath Mode | Sabbath without cooking |


    @Sabbath
    Scenario Outline: Test 3 - Validating Sabbath Bake text appearance of the Sabbath main screen
      Then I check the Sabbath bake "<title>" text view
      Then I check the Sabbath bake text view size
      Then I check the Sabbath bake text view color
      Then I check the Sabbath bake subtitle "<title1>" text view
      Then I check the Sabbath bake subtitle text view size
      Then I check the Sabbath bake subtitle text view color
      Examples:
        | title        | title1               |
        | Sabbath Bake | Sabbath with cooking |


    @Sabbath
    Scenario: Test 4 - Validating Back button and Cancel button on the header
      Then I check the Back button is clickable
      Then I check the Back button is enabled
      Then I check the background of the back button
      Then I click the back button
      Then I see settings screen
      And I click on Sabbath
      Then I see Sabbath mode selection screen
      Then I check the Cancel button is not clickable


#--------------------------SABBATH MODE APPEARANCE----------------------------
    @Sabbath
    Scenario Outline: Test 5 - Validating Sabbath Mode feature instruction screen
      Then I click on Sabbath Mode
      Then I see Instruction screen for Sabbath Mode
      Then I check the header text "<title>" of Sabbath Mode screen
      Then I check the Sabbath Mode Screen header text view alignment
      Then I check the Sabbath Mode Screen header text view size
      Then I check the Sabbath Mode Screen header text view color
      Then I check the Sabbath Mode Screen header text view font
      Then I check the Back button is clickable
      Then I check the Back button is enabled
      Then I check the background of the back button
      Then I check the don't show again check box is visible
      Then I check the don't show again check box is enabled
      Then I check the text view of don't show again check box
      Then I check the Start button is visible
      Then I check the Start button is clickable
      Then I check the Start button is enabled
      Then I check the Start button text
      Examples:
        | title        |
        | Sabbath Mode |


#--------------------------SABBATH MODE INSTRUCTION SCREEN APPEARANCE----------------------------
    @Sabbath
    Scenario: Test 6 - Validating instruction screen for Sabbath Mode
      Then I click on Sabbath Mode
      Then I see Instruction screen for Sabbath Mode
      Then I validate the description of Sabbath Mode feature
      Then I validate the description text size of Sabbath Mode feature
      Then I validate the description text color of Sabbath Mode feature
      Then I validate the description text font of Sabbath Mode feature


#-----------------------------SABBATH MODE ON SCREEN ----------------------------------------------
    @Sabbath
    Scenario Outline: Test 7 - Validating Sabbath Mode ON screen for Sabbath Mode
      Then I click on Sabbath Mode
      Then I see Instruction screen for Sabbath Mode
      Then I click on Start Button for Sabbath Mode
      Then I see Sabbath Mode is ON
      Then I check the Sabbath "<title>" text view
      Then I check the Sabbath text view size
      Then I check the Sabbath text view color
      Then I check the Sabbath text view font
      Then I check the Sabbath text view alignment
      Then I check the Sabbath text view line height and weight
      Then I check the Sabbath icon visibility
      Then I check the Sabbath icon size
      Then I check the Sabbath press and hold text view
      Then I check the Sabbath press and hold text view size
      Then I check the Sabbath press and hold text view color
      Then I check the Sabbath press and hold text view font
      Then I check the Sabbath press and hold text view line height and weight
      Then I check the Sabbath press and hold text view alignment
      Examples:
        | title   |
        | Sabbath |


#-----------------------------------SABBATH MODE NAVIGATION----------------------------------------
    @Sabbath
    Scenario: Test 8 - Validating navigation for Sabbath Mode feature screen
      Then I click on Sabbath Mode
      Then I see Instruction screen for Sabbath Mode
      Then I click the back button
      Then I see Sabbath mode selection screen
      Then I click on Sabbath Mode
      Then I see Instruction screen for Sabbath Mode
      Then I click on Start Button for Sabbath Mode
      Then I see Sabbath Mode is ON
      Then I press and hold on the screen for three seconds
      Then I see clock screen


#---------------------------------------SABBATH BAKE APPEARANCE-------------------------------------
    @Sabbath
    Scenario Outline: Test 9 - Validating Sabbath Bake feature screen
      Then I click on Sabbath Bake
      Then I see Instruction screen for Sabbath Bake
      Then I validate the header text "<title>" of Sabbath Bake screen
      Then I check the Sabbath Bake mode header text view alignment
      Then I check the Sabbath Bake mode header text view size
      Then I check the Sabbath Bake mode header text view color
      Then I check the Sabbath Bake mode header text view font
      Then I check the Back button is clickable
      Then I check the Back button is enabled
      Then I check the background of the back button
      Then I check the don't show again check box is visible
      Then I check the don't show again check box is enabled
      Then I check the text view of don't show again check box
      Then I check the Next button is visible
      Then I check the Next button is clickable
      Then I check the Next button is enabled
      Then I check the Next button text
        Examples:
          | title        |
          | Sabbath Bake |


    @Sabbath
    Scenario: Test 10 - Validating instruction screen for Sabbath Bake
      Then I click on Sabbath Bake
      Then I see Instruction screen for Sabbath Bake
      Then I validate the description of Sabbath Bake feature
      Then I validate the description text size of Sabbath Bake feature
      Then I validate the description text color of Sabbath Bake feature
      Then I validate the description text font of Sabbath Bake feature


    @Sabbath
    Scenario Outline: Test 11 - Validating navigation for Sabbath Bake timed feature screen
      Then I click on Sabbath Bake
      Then I see Instruction screen for Sabbath Bake
      Then I click on Next Button for Sabbath Bake
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click the back button
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I scroll to select the temperature for Sabbath bake
      Then I click the Next Button on tumbler screen
      Then I see cooktime numpad for Sabbath
      Then I set the CookTime to "<cookTime>" on numpad
      Then I click on Cancel button
      Then I click on Cancel button
      Then I set the CookTime to "<cookTime>" on numpad
      Then I see Set Timed Button is enabled and clickable
      Then I click on Set Timed Button
      Then I see both cavity status screen for Sabbath Bake with time if set timed
      Then I click on Start Button for Sabbath Bake
      Then I see status screen running with recipe name and selected temperature
      Examples:
        | cavity | cookTime |
        | upper  | 3        |
        | lower  | 4        |


  @Sabbath
    Scenario Outline: Test 12 - Validating navigation for Sabbath Bake timed feature screen by setting temperature by num pad
      Then I click on Sabbath Bake
      Then I see Instruction screen for Sabbath Bake
      Then I click on Next Button for Sabbath Bake
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click the back button
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click on numpad icon button
      Then I see temperature numpad
      Then I click on the tumbler icon
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click on numpad icon button
      Then I set the temperature "<temp>"
      Then I click on Next Button on the numpad screen
      Then I see cooktime numpad for Sabbath
      Then I click on the tumbler icon
      Then I see vertical tumbler for Sabbath
      Then I scroll the vertical tumbler to required duration
      Then I see Set Timed Button is enabled and clickable on Vertical tumbler screen
      Then I click on Set Timed Button on Vertical tumbler screen
      Then I see both cavity status screen for Sabbath Bake with time if set timed
      Then I click on Start Button for Sabbath Bake
      Then I see status screen running with recipe name and selected temperature
      Examples:
        | cavity | temp |
        | upper  | 175  |
        | lower  | 210  |


  @Sabbath
    Scenario Outline: Test 13 - Validating navigation for Sabbath Bake untimed feature screen
      Then I click on Sabbath Bake
      Then I see Instruction screen for Sabbath Bake
      Then I click on Next Button for Sabbath Bake
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click the back button
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I scroll to select the temperature for Sabbath bake
      Then I click the Next Button on tumbler screen
      Then I see cooktime numpad for Sabbath
      Then I set the CookTime to "<cookTime>" on numpad
      Then I click on Cancel button
      Then I click on Cancel button
      Then I set the CookTime to "<cookTime>" on numpad
      Then I click on Set Untimed button
      Then I see both cavity status screen for Sabbath Bake with time if set timed
      Then I click on Start Button for Sabbath Bake
      Then I see status screen running with recipe name and selected temperature
      Examples:
        | cavity | cookTime |
        | upper  | 3        |
        | lower  | 4        |


  @Sabbath
    Scenario Outline: Test 14 - Validating navigation for Sabbath Bake untimed feature screen by setting cooktime using vertical tumbler
      Then I click on Sabbath Bake
      Then I see Instruction screen for Sabbath Bake
      Then I click on Next Button for Sabbath Bake
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click the back button
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click on numpad icon button
      Then I see temperature numpad
      Then I click on the tumbler icon
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click the Next Button on tumbler screen
      Then I see cooktime numpad for Sabbath
      Then I click on the tumbler icon
      Then I see vertical tumbler for Sabbath
      Then I see Set Timed Button is not clickable on Vertical tumbler screen
      Then I scroll the vertical tumbler to required duration
      Then I click on numpad icon button
      Then I see cooktime numpad for Sabbath
      Then I click on the tumbler icon
      Then I see vertical tumbler for Sabbath
      Then I see Set Timed Button is enabled and clickable on Vertical tumbler screen
      Then I click on Set Untimed button on Vertical tumbler screen
      Then I see both cavity status screen for Sabbath Bake with time if set timed
      Then I click on Start Button for Sabbath Bake
      Then I see status screen running with recipe name and selected temperature
      Examples:
        | cavity |
        | upper  |
        | lower  |


  @Sabbath
    Scenario Outline: Test 15 - Validating navigation for Sabbath Bake timed feature screen by setting cooktime using vertical tumbler
      Then I click on Sabbath Bake
      Then I see Instruction screen for Sabbath Bake
      Then I click on Next Button for Sabbath Bake
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click the back button
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click on numpad icon button
      Then I see temperature numpad
      Then I click on the tumbler icon
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click the Next Button on tumbler screen
      Then I see cooktime numpad for Sabbath
      Then I click on the tumbler icon
      Then I see vertical tumbler for Sabbath
      Then I see Set Timed Button is not clickable on Vertical tumbler screen
      Then I scroll the vertical tumbler to required duration
      Then I see Set Timed Button is enabled and clickable on Vertical tumbler screen
      Then I click on numpad icon button
      Then I see cooktime numpad for Sabbath
      Then I click the back button on cooktime numpad
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click the Next Button on tumbler screen
      Then I see cooktime numpad for Sabbath
      Then I click on the tumbler icon
      Then I see vertical tumbler for Sabbath
      Then I click the back button
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I click the Next Button on tumbler screen
      Then I see cooktime numpad for Sabbath
      Then I click on the tumbler icon
      Then I see vertical tumbler for Sabbath
      Then I scroll the vertical tumbler to required duration
      Then I see Set Timed Button is enabled and clickable on Vertical tumbler screen
      Then I click on Set Timed Button on Vertical tumbler screen
      Then I see both cavity status screen for Sabbath Bake with time if set timed
      Then I click on Start Button for Sabbath Bake
      Then I see status screen running with recipe name and selected temperature
      Examples:
        | cavity |
        | upper  |
        | lower  |


#-----------------------------------CAVITY SELECTION SCREEN APPEARANCE------------------------------
  @Sabbath
    Scenario: Test 16 - Validating the cavity selection screen
      Then I click on Sabbath Bake
      Then I see Instruction screen for Sabbath Bake
      Then I click on Next Button for Sabbath Bake
      Then I see the cavity selection screen for Sabbath
      Then I check the Back button is clickable
      Then I check the Back button is enabled
      Then I check the background of the back button
      Then I check the Upper Oven text view
      Then I check the Upper Oven icon visibility
      Then I check the Upper Oven text view layout
      Then I check the Lower Oven text view
      Then I check the Lower Oven icon visibility
      Then I check the Lower Oven text view layout


#--------------------------------TEMPERATURE TUMBLER SCREEN APPEARANCE------------------------------
  @Sabbath
    Scenario Outline: Test 17 - Validating the Temperature Tumbler screen
      Then I click on Sabbath Bake
      Then I see Instruction screen for Sabbath Bake
      Then I click on Next Button for Sabbath Bake
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I check the Back button is enabled
      Then I check the Back button is clickable
      Then I check the background of the back button
      Then I check the numpad icon
      Then I validate the header text of Sabbath Bake screen
      Then I check the Temperature tumbler subtitle text
      Then I check Temperature tumbler is scrolled to all the temperature
      Then I check the Next button of the tumbler screen
      Then I check the Next button text of the tumbler screen
      Examples:
        | cavity |
        | upper  |
        | lower  |


#------------------------------------- COOKTIME NUMPAD SCREEN APPEARANCE------------------------------
  @Sabbath
    Scenario Outline: Test 18 - Validating the Cooktime numpad screen
      Then I click on Sabbath Bake
      Then I see Instruction screen for Sabbath Bake
      Then I click on Next Button for Sabbath Bake
      Then I see the cavity selection screen for Sabbath
      Then I select the "<cavity>" for Sabbath
      Then I see the horizontal tumbler screen for Sabbath Bake
      Then I scroll to select the temperature for Sabbath bake
      Then I click the Next Button on tumbler screen
      Then I see cooktime numpad for Sabbath
      Then I check the keyboard view header
      Then I check the Back button on Cooktime numpad screen
      Then I check the tumbler icon
      Then I check the cancel button
      Then I set the CookTime to "<cookTime>" on numpad
      Then I check the Set timed button
      Then I check the Set Untimed button
      Examples:
        | cavity | cookTime |
        | upper  | 3        |
        | lower  | 4        |


#------------------------------------- CAVITY SELECTION SCREEN APPEARANCE------------------------------
  @Sabbath
    Scenario Outline: Test 19 - Validating the Cavity status screen for Set Untimed
    Then I click on Sabbath Bake
    Then I see Instruction screen for Sabbath Bake
    Then I click on Next Button for Sabbath Bake
    Then I see the cavity selection screen for Sabbath
    Then I select the "<cavity>" for Sabbath
    Then I see the horizontal tumbler screen for Sabbath Bake
    Then I scroll to select the temperature for Sabbath bake
    Then I click the Next Button on tumbler screen
    Then I see cooktime numpad for Sabbath
    Then I set the CookTime to "<cookTime>" on numpad
    Then I click on Set Untimed button
    Then I see both cavity status screen for Sabbath Bake with time if set timed
    Then I check the header text of Cavity status screen for Set Untimed
    Then I check the Back button for Cavity status screen for Set Untimed
    Then I check the Start button text for Cavity status screen for Set Untimed
    Then I check the Start button icon for Cavity status screen for Set Untimed
    Then I check Upper Oven status text
    Then I check the Upper Oven icon on Cavity status screen
    Then I check the Upper Oven status layout
    Then I check Lower Oven status text
    Then I check the Lower Oven icon on Cavity status screen
    Then I check the Lower Oven status layout
    Examples:
      | cavity | cookTime |
      | upper  | 3        |
      | lower  | 4        |


#------------------------------------- STATUS SCREEN SCREEN APPEARANCE------------------------------
  @Sabbath
  Scenario Outline: Test 20 - Validating the Cavity status screen for Set Untimed
    Then I click on Sabbath Bake
    Then I see Instruction screen for Sabbath Bake
    Then I click on Next Button for Sabbath Bake
    Then I see the cavity selection screen for Sabbath
    Then I select the "<cavity>" for Sabbath
    Then I see the horizontal tumbler screen for Sabbath Bake
    Then I scroll to select the temperature for Sabbath bake
    Then I click the Next Button on tumbler screen
    Then I see cooktime numpad for Sabbath
    Then I set the CookTime to "<cookTime>" on numpad
    Then I click on Set Timed Button
    Then I see both cavity status screen for Sabbath Bake with time if set timed
    Then I click on Start Button for Sabbath Bake
    Then I see status screen running with recipe name and selected temperature
    Then I check status bar
    Then I check the recipe name text view
    Then I check the remaining cook time is displayed if set timed
    Then I check temperature Up button
    Then I check temperature Up button text
    Then I check temperature down button
    Then I check temperature down button text
    Examples:
      | cavity | cookTime |
      | upper  | 3        |
      | lower  | 4        |


#----------------------- BOTH CAVITY RUNNING AT SAME TIME NAVIGATION AND SCREEN APPEARANCE------------------------------
  @Sabbath
  Scenario: Test 21 - Validation if both cavity are running at the same time
    Then I click on Sabbath Bake
    Then I see Instruction screen for Sabbath Bake
    Then I click on Next Button for Sabbath Bake
    Then I see the cavity selection screen for Sabbath
    Then I click on Upper Oven
    Then I see the horizontal tumbler screen for Sabbath Bake
    Then I scroll to select the temperature for Sabbath bake
    Then I click the Next Button on tumbler screen
    Then I see cooktime numpad for Sabbath
    Then I click on Set Untimed button
    Then I see both cavity status screen for Sabbath Bake with time if set timed
    Then I click on Lower Oven to set it
    Then I see the horizontal tumbler screen for Sabbath Bake
    Then I scroll to select the temperature for Sabbath bake
    Then I click the Next Button on tumbler screen
    Then I see cooktime numpad for Sabbath
    Then I click on Set Untimed button
    Then I see both cavity status screen for Sabbath Bake with time if set timed
    Then I click on Start Button for Sabbath Bake
    Then I see status screen running with recipe name and selected temperature for both cavity
    Then I check the text view of both cavity








