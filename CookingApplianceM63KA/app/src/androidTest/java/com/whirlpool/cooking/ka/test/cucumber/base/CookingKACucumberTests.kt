/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package com.whirlpool.cooking.ka.test.cucumber.base

import android.Manifest
import android.app.Activity
import android.content.Context
import android.presenter.activity.KitchenAidLauncherActivity
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.whirlpool.cooking.common.utils.EspressoIdlingResource
import com.whirlpool.cooking.ka.BuildConfig
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.allowGrantedPermission
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.disableMeatProbe
import com.whirlpool.hmi.cookingsimulator.ProductVariant
import com.whirlpool.hmi.cookinguitesting.helpers.CookingConfigurationTestHelper
import com.whirlpool.hmi.cookinguitesting.helpers.ProductVariantEnum
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.base.DebugTestHelper
import com.whirlpool.hmi.uitesting.performance.BaselinePerformanceTest
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.SettingsManagerUtils
import core.utils.SharedPreferenceManager.setTechnicianTestDoneStatusIntoPreference
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith


/**
 * CookingCucumberTests is the central location to run the test found in the .feature file. The CookingCucumberTests provides
 * the setup and tear down for the Instrumentation Test that you would find if you used an Espresso test instead of this Cucumber test.
 */
@RunWith(AndroidJUnit4::class)
open class CookingKACucumberTests : BaselinePerformanceTest() {

    private var VARIENT = "variant"

    @JvmField
    @Rule
    var permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADVERTISE,
    )

    protected var scenario: ActivityScenario<KitchenAidLauncherActivity>? = null
    protected var scenarioJava: ActivityScenario<KitchenAidLauncherActivity>? = null

    @Given("App has started")
    fun startApp() {
        val variant = InstrumentationRegistry.getArguments().getString(
            VARIENT, ProductVariantEnum.DOUBLEOVENLOWEND.ordinal.toString()
        ).toInt()
        HMILogHelper.Logd("TEST_", "variant-->$variant")
        scenarioJava = ActivityScenario.launch(KitchenAidLauncherActivity::class.java)
        scenarioJava?.onActivity { activity: KitchenAidLauncherActivity? ->
            mainActivity = activity
            setUp()
            ContextProvider.replaceActivity(mainActivity)
            SettingsManagerUtils.isUnboxing = false
            setTechnicianTestDoneStatusIntoPreference(AppConstants.FALSE_CONSTANT)
            CookingAppUtils.loadToolsStructureJson(mainActivity)
            if (BuildConfig.IS_REAL_ACU_BUILD.not())
                ProductVariant.setProductVariantInPersistentMemory(variant)
        }
        CookingConfigurationTestHelper.getInstance().loadProductVariant(
            ApplicationProvider.getApplicationContext(),
            ProductVariantEnum.getProductVariant(variant)
        )
        allowGrantedPermission()
        UiTestingUtils.sleep(3000)
    }

    @Given("The app has started with Single low end Variant")
    fun startAppWithSingleLowEndVariant() {
        val variant = InstrumentationRegistry.getArguments().getString(
            VARIENT, ProductVariantEnum.SINGLEOVENLOWEND.ordinal.toString()
        ).toInt()
        HMILogHelper.Logd("TEST_", "variant-->$variant")
        scenarioJava = ActivityScenario.launch(KitchenAidLauncherActivity::class.java)
        scenarioJava?.onActivity { activity: KitchenAidLauncherActivity? ->
            mainActivity = activity
            setUp()
            ContextProvider.replaceActivity(mainActivity)
            SettingsManagerUtils.isUnboxing = false
            CookingAppUtils.loadToolsStructureJson(mainActivity)
            if (BuildConfig.IS_REAL_ACU_BUILD.not())
                ProductVariant.setProductVariantInPersistentMemory(variant)
        }
        CookingConfigurationTestHelper.getInstance().loadProductVariant(
            ApplicationProvider.getApplicationContext(),
            ProductVariantEnum.getProductVariant(variant)
        )
        allowGrantedPermission()
        UiTestingUtils.sleep(3000)
        disableMeatProbe()
    }

    @Given("The app has started with Microwave low end Variant")
    fun startAppWithMicrowaveLowEndVariant() {

        val variant = InstrumentationRegistry.getArguments().getString(
            VARIENT, ProductVariantEnum.MICROWAVEOVEN.ordinal.toString()
        ).toInt()
        HMILogHelper.Logd("TEST_", "variant-->$variant")
        scenarioJava = ActivityScenario.launch(KitchenAidLauncherActivity::class.java)
        scenarioJava?.onActivity { activity: KitchenAidLauncherActivity? ->
            mainActivity = activity
            setUp()
            ContextProvider.replaceActivity(mainActivity)
            SettingsManagerUtils.isUnboxing = false
            CookingAppUtils.loadToolsStructureJson(mainActivity)
            if (BuildConfig.IS_REAL_ACU_BUILD.not())
                ProductVariant.setProductVariantInPersistentMemory(variant)
        }
        CookingConfigurationTestHelper.getInstance().loadProductVariant(
            ApplicationProvider.getApplicationContext(),
            ProductVariantEnum.getProductVariant(variant)
        )
        allowGrantedPermission()
        UiTestingUtils.sleep(3000)
        disableMeatProbe()
    }

    @Given("The app has started with Double low end Variant")
    fun startAppWithDoubleLowEndVariant() {
        val variant = InstrumentationRegistry.getArguments().getString(
            VARIENT, ProductVariantEnum.DOUBLEOVENLOWEND.ordinal.toString()
        ).toInt()
        HMILogHelper.Logd("TEST_", "variant-->$variant")
        scenarioJava = ActivityScenario.launch(KitchenAidLauncherActivity::class.java)
        scenarioJava?.onActivity { activity: KitchenAidLauncherActivity? ->
            mainActivity = activity
            setUp()
            ContextProvider.replaceActivity(mainActivity)
            SettingsManagerUtils.isUnboxing = false
            CookingAppUtils.loadToolsStructureJson(mainActivity)
            if (BuildConfig.IS_REAL_ACU_BUILD.not())
                ProductVariant.setProductVariantInPersistentMemory(variant)
        }
        CookingConfigurationTestHelper.getInstance().loadProductVariant(
            ApplicationProvider.getApplicationContext(),
            ProductVariantEnum.getProductVariant(variant)
        )
        allowGrantedPermission()
        UiTestingUtils.sleep(3000)
        disableMeatProbe()
    }

    @Given("The app has started with Combo low end Variant")
    fun startAppWithComboLowEndVariant() {
        val variant = InstrumentationRegistry.getArguments().getString(
            VARIENT, ProductVariantEnum.COMBOLOWEND.ordinal.toString()
        ).toInt()
        HMILogHelper.Logd("TEST_", "variant-->$variant")
        scenarioJava = ActivityScenario.launch(KitchenAidLauncherActivity::class.java)
        scenarioJava?.onActivity { activity: KitchenAidLauncherActivity? ->
            mainActivity = activity
            ContextProvider.replaceActivity(mainActivity)
            SettingsManagerUtils.isUnboxing = false
            CookingAppUtils.loadToolsStructureJson(mainActivity)
            if (BuildConfig.IS_REAL_ACU_BUILD.not())
               ProductVariant.setProductVariantInPersistentMemory(variant)
        }
        CookingConfigurationTestHelper.getInstance().loadProductVariant(
            ApplicationProvider.getApplicationContext(),
            ProductVariantEnum.getProductVariant(variant)
        )
        allowGrantedPermission()
        UiTestingUtils.sleep(5000)
        disableMeatProbe()
    }

    @Before
    @Throws(Exception::class)
    fun setup() {
        val permission = Manifest.permission.BLUETOOTH_CONNECT
        InstrumentationRegistry.getInstrumentation().uiAutomation.grantRuntimePermission(BuildConfig.APPLICATION_ID, permission)
        EspressoIdlingResource.getInstance().registerIdlingRes()
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand("pm clear ${BuildConfig.APPLICATION_ID}").close()
    }

    @After
    fun tearDown() {
        if (scenario != null) {
            scenario?.close()
        }
        if (scenarioJava != null) {
            scenarioJava?.close()
        }
        reportFrameMetrics()
        EspressoIdlingResource.getInstance().unregisterIdlingRes()
    }

    override fun isCapturingLoadingFrames(): Boolean {
        return false
    }

    override fun getLoadingFrameCount(): Int {
        return 0
    }

    override fun getDebugTestHelper(): DebugTestHelper {
        return DebugTestHelper()
    }



    @And("I navigate to splash screen")
    fun navigatedToSplashScreen() {
    }

    companion object {
        var mainActivity: Activity? = null
            protected set
        val context: Context
            get() = ApplicationProvider.getApplicationContext()
    }
}
