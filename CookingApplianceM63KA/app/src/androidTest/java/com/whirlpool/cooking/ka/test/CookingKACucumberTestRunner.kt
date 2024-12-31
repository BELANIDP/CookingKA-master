package com.whirlpool.cooking.ka.test

import io.cucumber.android.runner.CucumberAndroidJUnitRunner
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["features"],
    glue = ["com.whirlpool.cooking.ka.test"],
    tags = ["@mwoComboAssistedMode, @ovenComboAssistedMode, @ovenDoubleAssistedMode, @ovenSingleAssistedMode, @selfCleanComboOven, @selfCleanDoubleOven, @selfCleanSingleOven, @doubleManualMode, @mwoComboManualMode, @mwoManualMode, @singleManualMode, @probeCombo, @probeDouble, @probeSingle, @upoCombo, @upoDouble, @upoSingle, @hotCavityWarning, @kitchenTimer, @homeScreen, @controllock, @demoMode , @delay, @Sabbath, @tools"]
)
class CookingKACucumberTestRunner : CucumberAndroidJUnitRunner()
