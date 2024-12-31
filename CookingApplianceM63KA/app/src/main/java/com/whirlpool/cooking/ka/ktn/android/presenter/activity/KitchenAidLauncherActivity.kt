package android.presenter.activity

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.fragments.ota.OtaHomeViewHolder
import android.presenter.fragments.ota.OtaProgressViewHolder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.BuildConfig
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.ActivityKitchenAidLauncherBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.utils.PowerInterruptState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.cookingsimulator.Simulator
import com.whirlpool.hmi.cookinguicomponents.viewmodel.CookingNavigationViewModel
import com.whirlpool.hmi.diagnostics.models.DiagnosticsManager
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModelFactory
import com.whirlpool.hmi.fvt_testing.model.FVTViewModel
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.ota.ui.OtaUiManager
import com.whirlpool.hmi.ota.utils.OTAStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.provisioning.ProvisioningViewModel
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.base.GenericCallbackInterface
import com.whirlpool.hmi.uicomponents.base.PopupWindowManagerInterface
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.tools.util.DisplayUtils
import com.whirlpool.hmi.vision.VisionViewModel
import com.whirlpool.hmi.vision.VisionViewModelFactory
import com.whirlpool.hmi.vision.vision2.CameraMode
import com.whirlpool.hmi.vision.vision2.CameraViewModel
import com.whirlpool.hmi.vision.vision2.CameraViewModelFactory
import core.utils.AppConstants
import core.utils.AppConstants.SERVICE_DIAGNOSTIC_ENTRY
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.isAnyCycleRunning
import core.utils.DoorEventUtils
import core.utils.HMIExpansionUtils
import core.utils.HMIExpansionUtils.Companion.isFastBlinkingKnobTimeoutActive
import core.utils.HMIExpansionUtils.Companion.isSlowBlinkingKnobTimeoutActive
import core.utils.HMIExpansionUtils.Companion.setBothKnobLightOff
import core.utils.HMILogHelper
import core.utils.HMILogHelper.Logd
import core.utils.LowPowerModeUtils.Companion.initLowPowerMode
import core.utils.LowPowerModeUtils.Companion.postResumeSleep
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils
import core.utils.NotificationManagerUtils.addNotificationToQueue
import core.utils.PopUpBuilderUtils
import core.utils.RemoteUiHelper
import core.utils.RemoteUiPopUpUtils
import core.utils.SettingsManagerUtils.isUnboxing
import core.utils.SharedPreferenceManager
import core.utils.SharedViewModel
import core.utils.SoundEventUtils
import core.utils.faultcodesutils.FaultDetails


class KitchenAidLauncherActivity : AppCompatActivity(), PopupWindowManagerInterface {
    private var simulatorService: Intent? = null
    private var popupWindow: PopupWindow? = null
    private var binding: ActivityKitchenAidLauncherBinding? = null
    private var settingsViewModel: SettingsViewModel? = null
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var primaryCookingViewModel: CookingViewModel
    private lateinit var primaryVisionViewModel: VisionViewModel
    private var cameraViewModel: CameraViewModel? = null
    private var secondaryCookingViewModel: CookingViewModel? = null
    private val MY_PERMISSIONS_REQUEST_CODE = 101
    private var firstBootUp = false
    private var otaState: OTAStatus? = OTAStatus.IDLE
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.CAMERA,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setKnobPreference()
        settingsViewModel = SettingsViewModel.getSettingsViewModel()
        grantPermissionForBLE()
        binding = ActivityKitchenAidLauncherBinding.inflate(layoutInflater)
        CookingAppUtils.loadToolsStructureJson(applicationContext)
        CookingAppUtils.loadFaultCodesJson(applicationContext)
        NotificationManagerUtils.loadNotificationJson(applicationContext)
        setContentView(binding!!.root)
        DisplayUtils.showFullScreenWindow(window)
        startSimulatorService()
        init()
        observeCookingViewModelFactoryReadyLiveData()
        //Remote command and Control initialization
        settingsViewModel?.let {
            RemoteUiHelper.initRemoteUi(it)
        }
        val provisioningViewModel = ProvisioningViewModel.getProvisioningViewModel()
        provisioningViewModel.enableAoB(true)

        // To disable Heads-up notifications
        //Settings.Global.putString(getContentResolver(), "heads_up_notifications_enabled", "0");
        //Observe pairing request and navigate to provisioning flow.
        ProvisioningViewModel.getProvisioningViewModel().pairingPin.observe(
            this
        ) { pin: String ->
            if (!pin.isEmpty()) {
                Logd("Navigating to BlePair for confirming pairing request")
                CookingAppUtils.startProvisioning(
                    NavigationUtils.getVisibleFragment()?.let { getViewSafely(it) },
                    false,
                    false,
                    true
                )
            }
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        postResumeSleep(this)
    }

    /**
     * This method will check required permission state also this method will request permissions
     */
    private fun grantPermissionForBLE() {
        //Permissions for BLE
        val mBluetoothManager = this.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val mBluetoothAdapter = mBluetoothManager.adapter
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            this.startActivity(enableBtIntent)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CODE)
        } else { //Todo
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CODE) {
            //TODO
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Creates simulator service which is necessary for SDK initialization
     */
    fun startSimulatorService() {
        try {
            if (!BuildConfig.IS_REAL_ACU_BUILD) {
                simulatorService = Intent(applicationContext, Simulator::class.java)
                startService(simulatorService)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Creates the instance of CookingViewModelFactory which is necessary for SDK initialization
     */
    fun init() {
        try {
            CookingViewModelFactory.initialize(BuildConfig.IS_REAL_ACU_BUILD)
            HmiExpansionViewModelFactory.initialize(BuildConfig.IS_REAL_ACU_BUILD)
            KitchenTimerVMFactory.instantiateKitchenTimerViewModels()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Stops Simulator service
     */
    private fun stopSimulatorService() {
        if (simulatorService != null) {
            try {
                stopService(simulatorService)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        stopSimulatorService()
        HMIExpansionUtils.removeObservers(this)
        OTAVMFactory.getOTAViewModel().otaState.removeObservers(this);
        binding = null
        cameraViewModel = null
        super.onDestroy()
    }
    /**
     * This method will apply the current set language
     */
    private fun updateAppLanguage() {
        val settingsVModel = SettingsViewModel.getSettingsViewModel()
        if (settingsVModel.appLanguage != null) {
            settingsVModel.setAppLanguage(settingsViewModel?.appLanguage?.value)
        }
    }

    /**
     * This method will observe the live data
     */
    private fun observeCookingViewModelFactoryReadyLiveData() {
        CookingViewModelFactory.isReady().observe(
            this
        ) { isReady: Boolean ->
            if (isReady) {
                updateAppLanguage()
                primaryCookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
                secondaryCookingViewModel = CookingAppUtils.getSecondaryCookingViewModel()
                sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
                Logd("Product Variant is: " + CookingViewModelFactory.getProductVariantEnum())
                Logd("Content Management Project is: " + CookingViewModelFactory.getContentManagementProject())
                Logd("Power Interrupt State is: " + CookingViewModelFactory.getPowerInterruptState())
                CookBookViewModel.getInstance() //instantiating here to get recipes data temporarily
                //To avoid multiple observations, observing from MainActivity and not from each fragment
                HMIExpansionUtils.observeLiveData(this)
                //observe door state for microwave cavity, useful to show door open/close dialog before starting recipe
                DoorEventUtils.observeDoorEvents(this)
                //observe sound events for preheat complete, recipe complete.
                SoundEventUtils.observePreheatCompleteSoundEvents(this)
                //observe meat probe events for oven
                MeatProbeUtils.observeMeatProbeEvents(this)
                NavigationUtils.setKitchenAidLauncherActivity(this)
                // Low power mode support for MWO
                initialiseOTAViewHolder()
                initLowPowerMode()
                CookingAppUtils.initializeServiceDiagnosticsViewProviders(this)
                observeFvtMode()
                setupViewModel()
                //Initialize Vision Module
                if (BuildConfig.IS_VISION_BUILD) {
                    Logd("Loading Primary Vision Model for Single/Double")
                    primaryVisionViewModel =
                        VisionViewModelFactory.getInstance().primaryCavityVisionViewModel
                    cameraViewModel = CameraViewModelFactory.getInstance(CameraMode.SIMULATOR)
                        .createCameraViewModel()
                }
                otaState = OTAVMFactory.getOTAViewModel().initialOtaStatus
                if (!CookingAppUtils.isFetalError() && otaState != OTAStatus.DOWNLOADING &&
                    otaState != OTAStatus.INSTALLING && otaState != OTAStatus.COMPLETED && otaState != OTAStatus.REBOOTING) {
                    Logd("OTA","isReady = $isReady and initial t OTA state is = $otaState , " +
                            " - OTA is not running navigate to the clock screen or Digital unboxing")
                    navigateNext()
                } else {
                    if (isUnboxing && (otaState == OTAStatus.INSTALLING || otaState == OTAStatus.COMPLETED || otaState == OTAStatus.REBOOTING)) {
                        Logd("OTA", "OTA is completed in digital unboxing -> Settings the digital_unboxing_nav_graph")
                        OTAVMFactory.getOTAViewModel().setOTAToIDLEState()
                        navigateNext()
                    } else {
                        Logd("OTA","isReady = $isReady and initial t OTA state is = $otaState - OTA is running")
                    }
                }
                observeErrorCodes(this@KitchenAidLauncherActivity,primaryCookingViewModel)
                observeErrorCodes(this@KitchenAidLauncherActivity,secondaryCookingViewModel)
                remoteUiObserver(primaryCookingViewModel)
                secondaryCookingViewModel?.let { remoteUiObserver(it) }
                SettingsViewModel.getSettingsViewModel().isUnboxing.observe(this) { unboxingState: Boolean ->
                    if (!unboxingState) {
                        Logd("OTA", "Started observing the OTA status")
                        registerObserversForOtaStatus()
                    }
                }
                //Observe door status for status screen recipe selection screen
                observeDoorStatus()
            }
        }
    }

    private fun observeDoorStatus() {
        observeDoorInteraction(CookingViewModelFactory.getPrimaryCavityViewModel())
        when(CookingViewModelFactory.getProductVariantEnum() ) {
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.COMBO ->{
                observeDoorInteraction(CookingViewModelFactory.getSecondaryCavityViewModel())
            }
            else ->{}
        }
    }

    private fun initialiseOTAViewHolder() {
        Logd("OTA", "Skinnable ViewHolder is setting")
        OtaUiManager.getInstance().setOtaHomeViewProvider(OtaHomeViewHolder())
        OtaUiManager.getInstance().otaProgressViewProvider = OtaProgressViewHolder()
        Logd("OTA", "Skinnable ViewHolder set")
    }

    private fun remoteUiObserver(cookingViewModel: CookingViewModel) {
        //Remote Control Lock Handled here
        settingsViewModel?.controlLock?.observe(this){
            CookingAppUtils.getVisibleFragment(this@KitchenAidLauncherActivity.supportFragmentManager)
                ?.let { it1 -> CookingAppUtils.navigateToStatusOrClockScreen(it1) }
        }
        CookingNavigationViewModel.getInstance()
            .setIdleFragmentId(R.id.global_action_to_clockScreen)
            .setSingleCavityNavigationActionId(R.id.global_action_to_single_status_screen)
            .setDoubleCavityNavigationActionId(R.id.global_action_to_double_status_screen)
            .observeInteractionStateChangesFor(NavigationViewModel.getNavigationViewModel(), this)
            .observeExecutionStateChangesFor(cookingViewModel, this)
            .observeSetOnDisplayStatusChangesFor(cookingViewModel, this)

        cookingViewModel.recipeExecutionViewModel.setOnDisplayStatus.observe(
            this
        ) { isSetOnDisplay: Boolean ->
            if (isSetOnDisplay) {
                val visibleFragment = NavigationViewModel.getVisibleFragment(this)
                if (visibleFragment != null) {
                    RemoteUiPopUpUtils.activateRemoteEnablePopUpBuilder(
                        this.supportFragmentManager,
                        cookingViewModel,
                        visibleFragment
                    )
                }
            }
        }
    }

    /*
    * setupViewModel sets up default inScopeViewModel to be used by the App to PrimaryVM to avoid null pointer exception in case of SO & MWO.
    */
    private fun setupViewModel () {
        CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
    }
    private fun observeFvtMode() {
        val fragment: Fragment? = CookingAppUtils.getVisibleFragment(this.supportFragmentManager)
        FVTViewModel.getInstance()?.externalModeEngaged?.observe(
            this
        ) { fvtMode: Boolean ->
            if (fvtMode) {
                HMIExpansionUtils.removeObservers(this)
                firstBootUp = true
                CookingAppUtils.stopGattServer()
                Logd("FVT Mode: $fvtMode--> Navigating FVT graph")
                if (fragment != null) {
                    navigateSafely(fragment,R.id.action_global_navigate_to_fvtFragment,null, null)
                }
            } else {
                if (firstBootUp) {
                    Logd("FVT Mode: Soft reboot Application")
                    SettingsViewModel.getSettingsViewModel().restartApp()
                }
            }
        }
    }

    /**
     * Method to Observe Error Codes and to navigate Error screen
     */
    private fun observeErrorCodes(activity: FragmentActivity,viewModel: CookingViewModel?) {
        //Observer for the normal faults which comes under the category type A,B,C
        viewModel?.faultId?.observe(this) { faultId: Int ->
            Logd((if (viewModel == primaryCookingViewModel) "Primary" else "Secondary")
                    + " Fault ID: " + faultId)
            HMIExpansionUtils.getHMIErrorCodesListener()
                ?.onHMIFaultId(viewModel, faultId)
                ?: run {
                    if (checkingFaultToGenerate(activity)) {
                        handleFaultError(faultId, viewModel)
                    } else {
                        Logd("Fault Code: Service diagnostic or OTA is running")
                        Logd("OTA", "OTA Error Code ${OTAVMFactory.getOTAViewModel().errorCode.value}")
                    }
                }
        }
        //Observer for the communication faults which comes under the category type C
        viewModel?.communicationFaultCode?.observe(this) { faultCode: String ->
            Logd((if (viewModel == primaryCookingViewModel) "Primary" else "Secondary")
                        + " Communication Fault Code: " + faultCode)
            if (faultCode == AppConstants.FAULT_AS_NONE
                && sharedViewModel.isApplianceInAOrCCategoryFault()
                && !DiagnosticsManager.getInstance().isDiagnosticsModeActive
                && !OTAVMFactory.getOTAViewModel().isOTARunning
                && !CookingAppUtils.isDemoModeEnabled()
                && OTAVMFactory.getOTAViewModel().errorCode.value != AppConstants.OTA_ERROR_CODE_BRICK_STATE
            ) {
                navigateToClockScreenOnEndTimeOut()
            } else {
                HMIExpansionUtils.getHMIErrorCodesListener()?.onHMICommunicationFaultCode(
                    viewModel,
                    faultCode
                ) ?: run {
                    if (checkingFaultToGenerate(activity)) {
                        handleFaultNavigation(faultCode, viewModel)
                    } else {
                        Logd("Communication Fault : Service diagnostic or OTA is running")
                        Logd("OTA", "OTA Error Code ${OTAVMFactory.getOTAViewModel().errorCode.value}")
                    }
                }
            }
        }
    }

    /**
     * Method to check fault to generate or not.
     * Fault should not generate for below cases,
     * Demo mode is enable,
     * System is in IDLE mode (on clock screen/clock far screen)
     * OTA is not in running state
     * Diagnostics mode is active
     * if is sabbath mode
     * @return  boolean : if all above condition meet else return false
     */
    private fun checkingFaultToGenerate(activity: FragmentActivity): Boolean {
        return (!CookingAppUtils.isDemoModeEnabled()
                && CookingViewModelFactory.getInScopeViewModel() != null
                && !CookingAppUtils.isSystemIsIdle(activity)
                && !OTAVMFactory.getOTAViewModel().isOTARunning
                && OTAVMFactory.getOTAViewModel().errorCode.value != AppConstants.OTA_ERROR_CODE_BRICK_STATE
                && !DiagnosticsManager.getInstance().isDiagnosticsModeActive)
                && !CookingAppUtils.isSabbathMode()
    }

    private fun navigateToClockScreenOnEndTimeOut() {
        if (settingsViewModel?.sabbathMode
                ?.value == SettingsViewModel.SabbathMode.SABBATH_COMPLIANT
        ) {
            // ToDo: navigate to Sabbath Clock when Implemented
        } else {
            CookingAppUtils.setErrorPresentOnHMIScreen(false)
            CookingAppUtils.getVisibleFragment(this.supportFragmentManager)?.let {
                navigateSafely(it, R.id.global_action_to_clockScreen, null, null)
            }
        }
    }

    /**
     * Method to handle normal faults
     */
    private fun handleFaultError(faultId: Int, viewModel: CookingViewModel) {
        if (viewModel == primaryCookingViewModel) {
            handlePrimaryError(faultId)
        } else {
            handleSecondaryError(faultId)
        }
    }

    /**
     * Method to handle communication faults
     */
    private fun handleFaultNavigation(
        commFaultCode: String,
        viewModel: CookingViewModel
    ) {
        var commFaultCode = commFaultCode
        commFaultCode = if (commFaultCode.length == 4) commFaultCode + "0" else commFaultCode
        val faultDetails = FaultDetails.getInstance(commFaultCode)
        if (viewModel == primaryCookingViewModel) {
            faultDetails.handlePrimaryFaultNavigation(
                faultDetails,
                primaryCookingViewModel,
                secondaryCookingViewModel,
                this
            )
        } else {
            faultDetails.handleSecondaryFaultNavigation(
                faultDetails,
                primaryCookingViewModel,
                secondaryCookingViewModel,
                this
            )
        }
    }

    /**
     * Method to handle Primary Errors
     * @param faultId : primary fault code
     */
    private fun handlePrimaryError(faultId: Int) {
        if (faultId != 0) {
            val faultCode = primaryCookingViewModel.faultCode.value
            val faultDetails = faultCode?.let { FaultDetails.getInstance(it) }
            faultDetails?.handlePrimaryFaultNavigation(
                faultDetails,
                primaryCookingViewModel,
                secondaryCookingViewModel,
                this
            )
        }
    }

    /**
     * Method to handle Secondary Errors
     * @param faultId : secondary fault code
     */
    private fun handleSecondaryError(faultId: Int) {
        if (faultId != 0) {
            val faultCode = secondaryCookingViewModel?.faultCode?.value
            val faultDetails = faultCode?.let { FaultDetails.getInstance(it) }
            faultDetails?.handleSecondaryFaultNavigation(
                faultDetails,
                primaryCookingViewModel,
                secondaryCookingViewModel,
                this
            )
        }
    }

    override fun onUserInteraction() {
        HMIExpansionUtils.onUserInteraction()
    }

    private fun navigateNext() {
        val navController = Navigation.findNavController(this, R.id.fragmentContainerView)
        //Digital unboxing flow added
        Logd("Unboxing","isUnboxing =${isUnboxing}")
        if (isUnboxing) {
            OTAVMFactory.getOTAViewModel().setOTAToIDLEState()
            Logd("Unboxing","Unboxing: Settings the digital_unboxing_nav_graph and return")
            navController.graph =  navController.navInflater.inflate(R.navigation.digital_unboxing_nav_graph)
            return
        }
        Logd("Launcher : Product Variant is: " + CookingViewModelFactory.getProductVariantEnum(),
            "Power Interrupt State is: " + CookingViewModelFactory.getPowerInterruptState())
        //If appliance is in the Brick state error code 500 do not navigate to the clock screen
        // because one of the node in our system is stopped working
        if (OTAVMFactory.getOTAViewModel().errorCode.value == AppConstants.OTA_ERROR_CODE_BRICK_STATE) {
            Logd("OTA : Appliance is in bricked sate: OTA Error Code 500")
            return
        }
        val productVariantEnum = CookingViewModelFactory.getProductVariantEnum()
        var navGraph: NavGraph?
        navGraph = navController.navInflater.inflate(R.navigation.manual_cooking_double_oven)
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN ->
                navGraph =
                    navController.navInflater.inflate(R.navigation.manual_cooking_single_oven)

            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN ->
                navGraph =
                    navController.navInflater.inflate(R.navigation.manual_cooking_mwo_oven)

            CookingViewModelFactory.ProductVariantEnum.COMBO ->
                navGraph =
                    navController.navInflater.inflate(R.navigation.manual_cooking_combo)

            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN ->
                navGraph =
                    navController.navInflater.inflate(R.navigation.manual_cooking_double_oven)

            else -> {}
        }
        if (productVariantEnum == CookingViewModelFactory.ProductVariantEnum.NONE) {
            HMILogHelper.Loge("Error in Processing the Product Variants in the ACU-SDK")
            //ToDO:// Show error screen and exit gracefully
            finishAndRemoveTask()
        } else if (CookingViewModelFactory.getPowerInterruptState() == PowerInterruptState.BROWNOUT) {
            Logd("Notification", "Notification brownout")
            NotificationManagerUtils.handleBrownout()
            sharedViewModel.instantiateBrownOutHandler(navController, navGraph)
            sharedViewModel.handleBrownOutRecovery(
                this,
                navController,
                navGraph,
                resources.getInteger(R.integer.brownout_recovery_timeout_millisec)
            )
        } else if (CookingViewModelFactory.getPowerInterruptState() == PowerInterruptState.BLACKOUT) {
            Logd("Notification", "Notification brownout")
            NotificationManagerUtils.handleBrownout()
            sharedViewModel.instantiateBlackOutHandler(this,navController, navGraph)
            sharedViewModel.handleBlackOutRecovery(resources.getInteger(R.integer.brownout_recovery_timeout_millisec))
        } else {
            navController.graph = navGraph
        }
    }

    override fun getPopupWindow(): PopupWindow? {
        return popupWindow
    }

    override fun displayPopup(
        popupView: View,
        parentView: View?,
        onWindowShownListener: GenericCallbackInterface?
    ) {
        dismissPopup()
        popupView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        // create the popup window
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        setPopupWindow(PopupWindow(popupView, width, height, false))
        if (getPopupWindow() != null) {
            getPopupWindow()?.isTouchable = true
            getPopupWindow()?.showAtLocation(parentView, Gravity.CENTER, 0, 0)
        }
        onWindowShownListener?.callback()
    }

    override fun setPopupWindow(popupWindow: PopupWindow?) {
        this.popupWindow = popupWindow
    }

    override fun dismissPopup() {
        if (popupWindow != null && popupWindow!!.isShowing) {
            popupWindow?.dismiss()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        // Reset timer whenever there is touch event detected, to prolong the timeout interaction
        if (ev.action == MotionEvent.ACTION_DOWN) {
            //Added below code to handle the timeout reset for the service diagnostic screen interaction
            if (DiagnosticsManager.getInstance().isDiagnosticsModeActive || SERVICE_DIAGNOSTIC_ENTRY) {
                //Restart the screen timeout
                Logd("Diagnostic mode is active, reset screen timeout on touch")
                NavigationViewModel.getNavigationViewModel().handleInteraction()
            }
            onHMIScreenTouched()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun onHMIScreenTouched() {
        if ((CookingViewModelFactory.isReady().value == true) && !isFastBlinkingKnobTimeoutActive() && !isSlowBlinkingKnobTimeoutActive() && !isAnyCycleRunning() ){
            setBothKnobLightOff()
        }
        if (isSlowBlinkingKnobTimeoutActive()) HMIExpansionUtils.userInteractWithinSlowBlinkingTimeoutElapsed()
    }

    /**
     * List here all the observer that should be check before OTA gets executed
     */
    private fun registerObserversForOtaStatus() {
        OTAVMFactory.getOTAViewModel().otaState.observe(
            this
        ) { otaStatus: OTAStatus ->
            if(otaStatus == OTAStatus.ERROR){
                addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_SW_UPDATE_AVAILABLE)
            }
            Logd("OTA", "OTAStatus is in : $otaStatus")
            if (CookingAppUtils.isFetalError() || otaStatus == OTAStatus.DOWNLOADING || otaStatus == OTAStatus.INSTALLING || otaStatus == OTAStatus.COMPLETED) {
                CookingAppUtils.getVisibleFragment(this@KitchenAidLauncherActivity.supportFragmentManager)?.let {
                    PopUpBuilderUtils.initAndStartOtaFlow(it,false,binding?.fragmentContainerView,true)
                }
            }
        }
    }

    private fun setKnobPreference() {
        val knobEntity = SharedPreferenceManager.getLeftAndRightKnobPositionIntoPreference()
        AppConstants.LEFT_KNOB_ID = knobEntity.leftKnob
        AppConstants.RIGHT_KNOB_ID = knobEntity.rightKnob
        Logd("Updating initial knob value after boot-up HMI : ${AppConstants.LEFT_KNOB_ID} and ${AppConstants.RIGHT_KNOB_ID}")
    }

    private fun observeDoorInteraction(cookingViewModel: CookingViewModel) {
        cookingViewModel.doorState.observe(this) { doorState ->
            if (doorState && cookingViewModel.recipeExecutionViewModel.isNotRunning &&
                CookingAppUtils.getVisibleFragment(this.supportFragmentManager) is AbstractStatusFragment &&
                SettingsViewModel.getSettingsViewModel().sabbathMode.value == SettingsViewModel.SabbathMode.NOT_SABBATH_COMPLIANT
            ) {
                this.supportFragmentManager.let { CookingAppUtils.dismissAllDialogs(it) }
                if (cookingViewModel.isOfTypeOven) SharedViewModel.getSharedViewModel(this)
                    .setCurrentRecipeBeingProgrammed(AppConstants.QUICK_START)
                NavigationUtils.getVisibleFragment().let {
                    if (cookingViewModel.isPrimaryCavity) {
                        Logd("IDLE DOOR and ON STATUS SCREEN - Door opened for upper cavity")
                        this.supportFragmentManager.setFragmentResult(
                            AppConstants.UPPER_FRAGMENT, bundleOf(
                                AppConstants.UPPER_FRAGMENT to true
                            )
                        )
                    } else {
                        Logd("IDLE DOOR and ON STATUS SCREEN - Door opened for Lower cavity")
                        this.supportFragmentManager.setFragmentResult(
                            AppConstants.LOWER_FRAGMENT, bundleOf(
                                AppConstants.LOWER_FRAGMENT to true
                            )
                        )
                    }
                }
            } else {
                Logd("Prerequisites is not matching in order to navigate to recipe selection screen")
            }
        }
    }
}