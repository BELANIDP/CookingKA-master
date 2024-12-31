Feature: Tools Feature
  Background:
    Given The app has started with Single low end Variant
    And the clock screen is visible
    And I rotate left knob clockwise
    And I open settings from top drawer

  @tools
  Scenario: Test 1 : Validate the Settings Menu Screen
    Then Setting menu is visible
    And I validate the header of settings menu
    And I validate the grid view of settings menu
    And I validate the list view of settings menu
    And I click on Show More in Preference option
    And I validate the preference screen
    And I perform click on back button
    Then Setting menu is visible
    And I click on Show more in Network Settings
    And I validate the Network Settings screen
    And I perform click on back button
    Then Setting menu is visible
    And I click on Show More in Info option
    And I validate the Info screen
    And I perform click on back button
    Then Setting menu is visible
    And I perform click on back button
    And the clock screen is visible

  @tools
  Scenario: Test 2 : Validate the flow of Knob Settings
    Then Setting menu is visible
    And I click on Show More in Preference option
    And I click on Knob Settings
    And I validate the Knob Settings screen
    And I click on Swap Knob Functions
    Then I validate Swap Knob Functions screen
    And I click on Swap
    Then I validate Swap Knob Functions screen after swapping
    And I click on back button of Swap Knob Functions
    And The Knob Settings screen is visible
    And I click on Knob Functions Info
    And I validate the Knob Functions Info screen for left knob
    And I click on Next button on Knob Functions info
    And I validate the Knob Functions Info screen for right knob
    And I click on Done button on Knob Functions info
    And The Knob Settings screen is visible
    Then I click on Knob Light toggle button
    And I validate the Knob Settings screen with Knob Light off
    And I click on close icon of Knob Settings screen
    And the clock screen is visible

  @tools
  Scenario: Test 3 : Validate the flow of Assign favorite of Knob Settings
    Then Setting menu is visible
    And I click on Show More in Preference option
    And I click on Knob Settings
    And I click on Assign Favorite
    And I validate Assign Favorite screen
    And I click on info icon of Assign Favorite
    Then I validate the Assign Favorite info popup
    And I click on Okay in assign favorite info popup
    And The Assign favorite screen is visible
    And I click on Start a Favorite cycle option
    And I validate the Favorites screen with no favorites
    And I click on backarrow of Favorites screen
    And I click on Assign Favorite close icon
    And the clock screen is visible
    And I perform click on clock screen
    And I click on Bake recipe
    And I click on Next button of Bake recipe
    And I click on Start button of Bake recipe
    And I click on three dots in Bake recipe running cycle
    And I click on Save as Favorite
    And I click on Turn Off button of Bake recipe
    And the clock screen is visible
    And I open settings from top drawer
    And I click on Show More in Preference option
    And I click on Knob Settings
    And I click on Assign Favorite
    And I click on Start a Favorite cycle option
    And I validate the Favorites screen with Bake recipe in it
    And I click on Bake in Favorites
    And I click on backarrow of Assign Favorite
    And I validate the Knob Settings screen with Bake recipe in assign favorite
    And I click on backarrow of Knob Settings screen
    And I click on backarrow of Knob Settings screen
    And Setting menu is visible

  @tools
  Scenario: Test 4: Validate the flow of Setting Time
    Then Setting menu is visible
    And I click on Show More in Preference option
    And I click on Time & Date
    And I validate the Time & Date menu screen
    And I click on Set Time
    And I validate the Set Time vertical tumbler screen
    And I click on Set button on vertical tumbler
    And I click on Set Time
    And I click on numpad icon on Set time vertical tumbler
    And I validate the Set time numpad screen
    And I click on 24H
    And the Set time 24H numpad screen is visible
    And I click on 12H
    And the Set time 12H numpad screen is visible
    And I set the time to 12:12 on Set Time numpad
    And I click on backspace on Set Time numpad
    And I click on backspace on Set Time numpad
    And I click on backspace on Set Time numpad
    And I click on toggle icon on Set Time numpad
    And the Set Time vertical tumbler screen is visible
    And I click on numpad icon on Set time vertical tumbler
    And the Set Time numpad screen is visible
    And I set the time to 22:22 on Set Time numpad
    And I click on Set button on numpad
    And I validate the Set time numpad screen with error message
    And I set the time to 12:12 on Set Time numpad
    And I click on Set button on numpad
    And I click on close icon of Time & date menu screen
    And the clock screen is visible

  @tools
  Scenario: Test 5: Validate the flow of Sound Volume
    Then Setting menu is visible
    And I click on Show More in Preference option
    And I click on Sound Volume
    And I validate the Sound Volume menu screen
    And I click on Alerts & Timers
    And I validate Alerts & Timers tumbler screen
    And I rotate Left knob counter clockwise
    And I rotate Left knob counter clockwise
    And I click left knob
    And I validate the Sound Volume menu screen
    And I click on backarrow of Alerts & Timers tumbler screen
    And I click on backarrow of Alerts & Timers tumbler screen
    And I click on Show More in Preference option
    And I click on Sound Volume
    And I click on Buttons And Effects
    And I rotate Left knob counter clockwise
    And I click left knob
    And I validate the Sound Volume menu screen
    And I click on close icon of Sound Volume menu screen
    And the clock screen is visible

  @tools
  Scenario: Test 6: Validate the flow of Sound Volume when Mute
    Then Setting menu is visible
    And I perform click on mute
    And I click on Show More in Preference option
    And I click on Sound Volume
    And I validate the Sound Volume menu screen when mute
    And I click on Mute toggle button
    And I validate the Sound Volume menu screen
    And I click on close icon of Sound Volume menu screen
    And the clock screen is visible

  @tools
  Scenario: Test 7: Validate the flow of Regional Settings
    Then Setting menu is visible
    And I click on Show More in Preference option
    And I click on Regional Settings
    And I validate Regional Settings menu screen
    And I click on Language
    And I validate Language menu screen
    And I click on Spanish
    And I validate Language menu screen
    And I click on English
    And I click on backarrow of Language menu screen
    And I validate Regional Settings menu screen
    And I change Temperature Unit to degree Celsius
    And I change Weight Unit to grams
    And I change Time Format to degree 24hrs
    And I change Date Format to DDMM
    And I validate Regional Settings menu screen after changing
    And I click on backarrow of Regional Settings menu screen
    And I click on backarrow of Regional Settings menu screen
    And I click on Show More in Preference option
    And I click on Regional Settings
    And I click on close icon of Regional Settings
    And the clock screen is visible

  @tools
  Scenario: Test 8: Validate the flow of Display & Brightness
    Then Setting menu is visible
    And I click on Show More in Preference option
    And I click on Display & Brightness
    And I validate Display & Brightness menu screen
    And I click on Display Brightness
    And I rotate left knob clockwise
    And I click left knob
    And I click on backarrow of Display Brightness
    And I click on backarrow of Display Brightness
    And I click on Show More in Preference option
    And I click on Display & Brightness
    And I click on close icon of Display & Brightness
    And the clock screen is visible

  @tools
  Scenario: Test 9: Validate the flow of Temperature Calibration
    Then Setting menu is visible
    And I click on Show More in Preference option
    And I click on Temperature Calibration
    And I validate the Temperature Calibration tumbler screen
    And I set the temperature on temperature calibration tumbler
    And I validate the Temperature Calibration tumbler screen
    And I click on Set button of temperature Calibration
    And I click on Temperature Calibration
    And I click on backarrow of Temperature Calibration screen
    And I click on close icon of Knob Settings screen
    And the clock screen is visible

  @tools
  Scenario: Test 10: Validate the flow of Restore Settings
    Then Setting menu is visible
    And I click on Show More in Preference option
    And I click on Restore Settings
    And I validate Restore Settings menu screen
    And I click on Restore Factory Defaults
    And I validate Restore Factory Defaults screen
    And I click on cancel of Restore Factory Defaults
    And the Restore Settings menu screen is visible
    And I click on Restore Factory Defaults
    And I click on proceed of Restore Factory Defaults
    And I validate the popup of Restore factory defaults
    And I click on cancel of Restore Factory Defaults popup
    And I click on proceed of Restore Factory Defaults
    And I click on proceed of Restore Factory Defaults popup
    And I validate the do not unplug

  @tools
  Scenario: Test 11: Validate the flow of Cavity Light
    Then Setting menu is visible
    And I click on Show More in Preference option
    And I click on Cavity Light
    And I validate the Cavity Light menu screen
    And I click on Manually control lights
    And I validate the Cavity Light menu screen
    And I click on backarrow of Cavity Light menu screen
    And I click on Cavity Light
    And I click on close icon of Cavity Light menu screen
    And the clock screen is visible

  @tools
  Scenario: Test 12: Validate the flow of Network Settings
    Then Setting menu is visible
    And I click on Show more in Network Settings
    And I click on Wifi toggle icon
    And I validate the Network Settings screen with Wifi Off
    And I click on Wifi toggle icon
    And I click on Remote Enable
    And I validate Set up Wifi popup
    And I click on connect later
    And I click on backarrow of Network Settings menu screen
    And I click on Show more in Network Settings
    And I click on close icon of Network Settings menu screen
    And the clock screen is visible

  @tools
  Scenario: Test 13: Validate the flow of Info
    Then Setting menu is visible
    And I click on Show More in Info option
    And I click on Software Terms and Conditions
    And I validate Software Terms and Conditions screen
    And I click on backarrow of Software Terms and Conditions
    And I click on Service and Support
    And I validate Service and Support screen
    And I click on Enter Diagnostics
    And I validate Enter code screen
    And I Enter code 121212121
    And I click on Next button of Enter Code screen
    And I validate Enter code screen with error message
    And I Enter code 123123123
    And I click on backspace of Enter code screen
    And I click on backspace of Enter code screen
    And I click on backspace of Enter code screen
    And I click on backspace of Enter code screen
    And I click on backspace of Enter code screen
    And I click on backspace of Enter code screen
    And I click on backspace of Enter code screen
    And I click on backspace of Enter code screen
    And I click on backspace of Enter code screen
    And I Enter code 123123123
    And I click on Next button of Enter Code screen
#    And I validate Enter Service Diagnostics popup
#    And I click on Dismiss of Enter Service Diagnostic popup
#    And the Enter code screen is visible
#    And I click on Next button of Enter Code screen
#    And I click on Enter button of Enter Service Diagnostic popup
#    And I validate Service Diagnostics menu screen
#    And I click on close icon of Service Diagnostics menu screen
#    And I validate End Service Diagnostics popup
#    And I click on Dismiss button of End Service Diagnostics popup
#    And the Service Diagnostics menu screen is visible
#    And I click on close icon of Service Diagnostics menu screen
#    And I click on Exit button of End Service Diagnostics popup
#    And the clock screen is visible

#  @tools
#  Scenario: Test 14: Validate the Service Diagnostics Menu screen
#    Then Setting menu is visible
#    And I click on Show More in Info option
#    And the Info menu screen is visible
#    And I click on Service and Support
#    And I click on Enter Diagnostics
#    And I Enter code 123123123
#    And I click on Next button of Enter Code screen
#    And I click on Enter button of Enter Service Diagnostic popup
#    And I click on Error codes history
#    And I validate Error Code History screen
#    And I click on backarrow of Error codes history
#    And the Service Diagnostics menu screen is visible
#    And I click on Auto diagnostics
#    And I validate Start Auto Diagnostics popup
#    And I click on Dismiss button of Auto Diagnostics popup
#    And the Service Diagnostics menu screen is visible
#    And I click on Component Activation
#    And I validate Component Activation menu screen
#    And I click on backarrow of Component Activation
#    And the Service Diagnostics menu screen is visible
#    And I click on HMI verification
#    And I validate the HMI verification screen
#    And I click on close icon of HMI verification
#    And the Service Diagnostics menu screen is visible
#    And I click on System Info
#    And I validate System info screen
#    And I click on product
#    And I validate product screen
#    And I click on backarrow of product
#    And the System info menu screen is visible
#    And I click on Wifi
#    And I validate Wifi screen
#    And I click on backarrow of Wifi
#    And the System info menu screen is visible
#    And I click on Software
#    And I validate Software screen
#    And I click on backarrow of Software
#    And the System info menu screen is visible
#    And I click on backarrow of Software
#    And I click on Restore factory Defaults in Service Diagnostics
#    And I validate Reboot and Reset screen
#    And I click on backarrow of Reboot and Restart
#    And the Service Diagnostics menu screen is visible
#    And I click on close icon of Service Diagnostics menu screen
#    And I click on Exit button of End Service Diagnostics popup
#    And the clock screen is visible














