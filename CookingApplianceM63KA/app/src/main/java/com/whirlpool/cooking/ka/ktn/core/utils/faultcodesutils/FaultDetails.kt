package core.utils.faultcodesutils

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.presenter.fragments.FaultErrorTypeAFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.utils.Constants
import com.whirlpool.hmi.cooking.utils.FaultSubCategory
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.jbase.BaseInformationServiceAndSupportFragment
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.dismissAllDialogs
import core.utils.CookingAppUtils.Companion.getCategoryAFaultCodesMap
import core.utils.CookingAppUtils.Companion.getCategoryB2FaultCodesMap
import core.utils.CookingAppUtils.Companion.getCategoryBFaultCodesMap
import core.utils.CookingAppUtils.Companion.getCategoryCFaultCodesMap
import core.utils.CookingAppUtils.Companion.getResIdFromResName
import core.utils.CookingAppUtils.Companion.getSaveOnlyFaultCodesList
import core.utils.CookingAppUtils.Companion.getStringFromResourceId
import core.utils.CookingAppUtils.Companion.getTextPosition
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedPreferenceUtils
import core.utils.SharedViewModel
import core.utils.TimeUtils
import java.util.Calendar
import java.util.concurrent.TimeUnit

class FaultDetails(private var faultCode: String) {
    private var doubleOvenFaultPriority = 0
    private var comboOvenFaultPriority = 0
    private val faultNameAndTime = HashMap<String, ArrayList<Long>>()
    private var handler: Handler? = null
    private var mCavity: String? = AppConstants.EMPTY_STRING
    private var mFaultCategory: Int = 0
    private var mFragment: Fragment? = null
    private var mModelNumber: String? = AppConstants.EMPTY_STRING
    private var mSerialNumber: String? = AppConstants.EMPTY_STRING

    private var mNavHostFragment: Fragment? = null
    private var mBundle: Bundle? = null

    init {
        initHandler()
    }

    companion object {
        fun getInstance(code: String): FaultDetails {
            // Create a new instance of FaultDetails with the given faultCode
            val newInstance = FaultDetails(code)
            HMILogHelper.Logd(tag,"Fault Code --> $code")
            return newInstance
        }
        private var tag = "FAULT_LOG:"
        private const val MESSAGE_TYPE_1 = 1
        private const val MESSAGE_TYPE_2 = 2
        private const val MESSAGE_TYPE_3 = 3
        @Suppress("ConstPropertyName")
        private const val handlerDelay: Long = 1000

    }

    /**
     * Method get Fault code
     */
    fun getFaultCode(): String {
        return faultCode
    }

    private fun faultDetails(fragment: Fragment, resourceName: String): String {
        val messageID: Int = getResIdFromResName(
            fragment.context, resourceName, AppConstants.RESOURCE_TYPE_STRING
        )
        return getStringFromResourceId(fragment.requireContext(), messageID)
    }

    fun handlePrimaryFaultNavigation(
        faultDetails: FaultDetails,
        primaryCookingViewModel: CookingViewModel,
        secondaryCookingViewModel: CookingViewModel?,
        fragmentActivity: FragmentActivity?
    ) {
        val saveOnlyFault = faultDetails.getSaveOnlyFaults().contains(faultCode)
        HMILogHelper.Logd(tag,"Primary save Only Fault : $saveOnlyFault")
        if (!saveOnlyFault) {
            val faultCategory: Int = faultDetails.getFaultCategory()
            HMILogHelper.Logd(tag,"Primary Fault Category : $faultCategory")
            if (faultCategory != FaultSubCategory.NOT_APPLICABLE.ordinal) {
                //Cancel the recipe only for Fault Category C or A
                //For Other Fault Category, ACU cancel the recipe and publish the recipe state as CANCELLED_EXT.
                //Application layer only cancel the recipe if receive the recipe state is CANCELLED_EXT
                if ((faultCategory == FaultSubCategory.CATEGORY_C.ordinal ||
                            faultCategory == FaultSubCategory.CATEGORY_A.ordinal)
                    && primaryCookingViewModel.recipeExecutionViewModel.isRunning
                ) {
                    HMILogHelper.Logd(tag,"Canceling recipe of primaryCavity View Model from handlePrimaryFaultNavigation")
                    primaryCookingViewModel.recipeExecutionViewModel.cancel()
                }
                CookingAppUtils.navigateToHighestPriorityFault(
                    Constants.PRIMARY_CAVITY_KEY,
                    faultDetails,
                    fragmentActivity?.supportFragmentManager?.primaryNavigationFragment,
                    secondaryCookingViewModel,
                    faultCategory
                )
                //AudioManagerUtils.playAudioQueue(ContextProvider.getApplication().applicationContext)
            } else {
                HMILogHelper.Logd(tag,"Save only fault received for primary $faultCode")
            }
        }
    }

    fun handleSecondaryFaultNavigation(
        faultDetails: FaultDetails,
        primaryCookingViewModel: CookingViewModel,
        secondaryCookingViewModel: CookingViewModel?,
        fragmentActivity: FragmentActivity?
    ) {
        val saveOnlyFaults = getSaveOnlyFaults().contains(faultCode)
        HMILogHelper.Logd(tag,"Secondary save Only Fault : $saveOnlyFaults")
        if (!saveOnlyFaults) {
            val faultCategory = faultDetails.getFaultCategory()
            HMILogHelper.Logd(tag,"Secondary Fault Category : $faultCategory")
            if (faultCategory != FaultSubCategory.NOT_APPLICABLE.ordinal) {
                //Cancel the recipe only for Fault Category C or A
                //For Other Fault Category, ACU cancel the recipe and publish the recipe state as CANCELLED_EXT.
                //Application layer only cancel the recipe if receive the recipe state is CANCELLED_EXT
                if ((faultCategory == FaultSubCategory.CATEGORY_C.ordinal ||
                            faultCategory == FaultSubCategory.CATEGORY_A.ordinal) &&
                    secondaryCookingViewModel?.recipeExecutionViewModel?.isRunning == true
                ) {
                    secondaryCookingViewModel.recipeExecutionViewModel?.cancel()
                }
                CookingAppUtils.navigateToHighestPriorityFault(
                    Constants.SECONDARY_CAVITY_KEY,
                    faultDetails,
                    fragmentActivity?.supportFragmentManager?.primaryNavigationFragment,
                    primaryCookingViewModel,
                    faultCategory
                )
                //AudioManagerUtils.playCriticalAlert(ContextProvider.getApplication().applicationContext)
            } else {
                HMILogHelper.Logd(tag,"Save only fault received for secondary $faultCode")
            }
        }
    }

    fun getFaultCategory(): Int {
        if (getCategoryB2FaultCodes().toString().contains(faultCode)) {
            return FaultSubCategory.CATEGORY_B2.ordinal
        } else if (getCategoryBFaultCodes().toString().contains(faultCode)) {
            return FaultSubCategory.CATEGORY_B.ordinal
        } else if (getCategoryAFaultCodes().toString().contains(faultCode)) {
            return FaultSubCategory.CATEGORY_A.ordinal
        } else if (getCategoryCFaultCodes().toString().contains(faultCode)) {
            return FaultSubCategory.CATEGORY_C.ordinal
        }
        return FaultSubCategory.NOT_APPLICABLE.ordinal
    }

    /**
     * Method to get fault code name
     */
    fun getFaultName(fragment: Fragment): String {
        val commonMessagePosition: Int = getTextPosition(
            NavigationUtils.getViewSafely(fragment) ?: fragment.requireView(),
            R.array.fault_names_common_messages,
            faultCode
        )
        return if (commonMessagePosition > 0) {
            faultDetails(
                fragment, AppConstants.TEXT_COMMON_MESSAGE_FAULT_NAME + commonMessagePosition
            )
        } else {
            faultDetails(fragment, AppConstants.FAULT_ERROR_NAME_TAG + faultCode)
        }
    }

    /**
     * Method to get fault code description
     */
    @Suppress("unused")
    fun getFaultDescription(fragment: Fragment): String {
        return faultDetails(
            fragment, AppConstants.FAULT_ERROR_DESCRIPTION_TAG + faultCode
        )
    }

    /**
     * Method to get fault code instructions
     */
    fun getFaultInstructions(fragment: Fragment): String {
        return faultDetails(
            fragment, AppConstants.FAULT_ERROR_INSTRUCTIONS_TAG + faultCode
        )
    }


    /**
     * Method to get category A fault codes
     */
    private fun getCategoryAFaultCodes(): ArrayList<String> {
        val categoryAFaultMap: Map<String, ArrayList<Int>> = getCategoryAFaultCodesMap()
        setFaultPriorities(categoryAFaultMap)
        return ArrayList(categoryAFaultMap.keys)
    }

    /**
     * Method to get category B fault codes
     */
    private fun getCategoryBFaultCodes(): ArrayList<String> {
        val categoryBFaultMap: Map<String, ArrayList<Int>> = getCategoryBFaultCodesMap()
        setFaultPriorities(categoryBFaultMap)
        return ArrayList(categoryBFaultMap.keys)
    }

    /**
     * Method to get category B2 fault codes
     */
    private fun getCategoryB2FaultCodes(): ArrayList<String> {
        val categoryB2FaultMap: Map<String, ArrayList<Int>> = getCategoryB2FaultCodesMap()
        setFaultPriorities(categoryB2FaultMap)
        return ArrayList(categoryB2FaultMap.keys)
    }

    /**
     * Method to get category C fault codes
     */
    private fun getCategoryCFaultCodes(): ArrayList<String> {
        val categoryCFaultMap: Map<String, ArrayList<Int>> = getCategoryCFaultCodesMap()
        setFaultPriorities(categoryCFaultMap)
        return ArrayList(categoryCFaultMap.keys)
    }

    /**
     * Method to get save only faults
     */
    private fun getSaveOnlyFaults(): List<String> {
        return getSaveOnlyFaultCodesList()
    }

    /**
     * Method to get new fault code double oven priority
     */
    fun getDoubleOvenFaultPriority(): Int {
        return doubleOvenFaultPriority
    }

    /**
     * Method to set new fault code double oven priority
     */
    private fun setDoubleOvenFaultPriority(doubleOvenFaultPriority: Int) {
        this.doubleOvenFaultPriority = doubleOvenFaultPriority
    }

    /**
     * Method to get new fault code combo oven priority
     */
    fun getComboOvenFaultPriority(): Int {
        return comboOvenFaultPriority
    }

    /**
     * Method to set new fault code combo oven priority
     */
    private fun setComboOvenFaultPriority(comboOvenFaultPriority: Int) {
        this.comboOvenFaultPriority = comboOvenFaultPriority
    }

    /**
     * Method to set fault code priorities only if fault popup is open
     *
     * @param faultMap: Map of fault codes and their priorities
     */
    private fun setFaultPriorities(faultMap: Map<String, ArrayList<Int>>) {
        val sharedViewModel: SharedViewModel? =
            ContextProvider.getFragmentActivity()?.let {
                ViewModelProvider(it)[SharedViewModel::class.java]
            }
        if (CookingViewModelFactory.getProductVariantEnum() ==
            CookingViewModelFactory.ProductVariantEnum.COMBO
            || CookingViewModelFactory.getProductVariantEnum() ==
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
        ) {
            val arrayListValue = faultMap[faultCode]
            if (sharedViewModel?.isFaultPopUpOpen() == false)
                arrayListValue?.let {
                    setCurrentDisplayedFaultPriorityMap(it)
                }
            setDoubleOvenFaultPriority(arrayListValue?.get(0) ?: 0)
            setComboOvenFaultPriority(arrayListValue?.get(1) ?: 0)
        }
    }

    /**
     * Method to save current displayed fault popup priorities
     */
    private fun setCurrentDisplayedFaultPriorityMap(arrayListValue: ArrayList<Int>) {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.CURRENT_DISPLAYED_DOUBLE_FAULT_PRIORITY, arrayListValue[0].toString()
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.CURRENT_DISPLAYED_COMBO_FAULT_PRIORITY, arrayListValue[1].toString()
        )
    }

    /**
     * Method to get current displayed fault popup double oven priority
     */
    fun getCurrentDisplayedFaultDoubleOvenPriority(): Int {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.CURRENT_DISPLAYED_DOUBLE_FAULT_PRIORITY, "0"
        )?.toInt() ?: 0
    }

    /**
     * Method to get current displayed fault popup combo oven priority
     */
    fun getCurrentDisplayedFaultComboOvenPriority(): Int {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.CURRENT_DISPLAYED_COMBO_FAULT_PRIORITY, "0"
        )?.toInt() ?: 0
    }

    /**
     * Method to check if fault B2 repeated more than thrice in 24 hours
     *
     * @return true/false
     */
    private fun isB2FaultCodeRepeatedThriceWithin24Hr(faultCode: String): Boolean {
        var tempList: ArrayList<Long>
        if (faultNameAndTime.containsKey(faultCode)) {
            tempList = faultNameAndTime[faultCode].orEmpty() as ArrayList<Long>
            if (tempList.isEmpty()) {
                tempList = ArrayList()
            }
            tempList.add(Calendar.getInstance().timeInMillis)
            if (tempList.size > 3) {
                return if (TimeUnit.MILLISECONDS.toHours(tempList[0] - tempList[tempList.size - 1]) <= TimeUtils.HOURS_IN_A_DAY) {
                    tempList.clear()
                    faultNameAndTime[faultCode] = tempList
                    true
                } else {
                    // For the 4th time it clears previous time values since its greater than 24 hrs and adds latest time to the list.
                    tempList.clear()
                    tempList.add(Calendar.getInstance().timeInMillis)
                    faultNameAndTime[faultCode] = tempList
                    false
                }
            }
        } else {
            tempList = ArrayList()
            tempList.add(Calendar.getInstance().time.time)
        }
        faultNameAndTime[faultCode] = tempList
        return false
    }

    /**
     * Method to navigate Error Screen
     *
     * @param cavity: Primary Cavity / Secondary Cavity
     */
    fun navigateToErrorScreen(
        cavity: String?,
        faultCategory: Int,
        fragment: Fragment,
        modelNumber: String?,
        serialNumber: String?
    ) {
        ContextProvider.getFragmentActivity()?.supportFragmentManager?.let { dismissAllDialogs(it) }
        if (SettingsViewModel.getSettingsViewModel().isUnboxing.value != true) {
            CookingAppUtils.stopGattServer()
            CookingAppUtils.setErrorPresentOnHMIScreen(true)
        }
        mCavity = cavity
        mFaultCategory = faultCategory
        mModelNumber = modelNumber
        mSerialNumber = serialNumber
        mFragment = fragment
        handler?.removeMessages(MESSAGE_TYPE_1)
        handler?.sendEmptyMessageDelayed(MESSAGE_TYPE_1, handlerDelay)
    }

    /**
     * Method to be called when fault category is A or C and navigate
     */
    private fun faultCategoryAOrC(
        cavity: String?, fragment: Fragment, faultCategory: Int
    ) {
        val sharedViewModel =
            ViewModelProvider(fragment.requireActivity())[SharedViewModel::class.java]
        CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = false, false)
        val bundle = Bundle()
        bundle.putString(BundleKeys.BUNDLE_FAULT_CAVITY, cavity)
        bundle.putString(BundleKeys.BUNDLE_FAULT_CODE, faultCode)
        sharedViewModel.setApplianceInAOrCCategoryFault(true)
        mNavHostFragment = fragment
        if (faultCategory == FaultSubCategory.CATEGORY_C.ordinal) {
            bundle.putBoolean(BundleKeys.BUNDLE_IS_ERROR_FRAME, true)
            bundle.putInt(BundleKeys.BUNDLE_FAULT_CATEGORY, faultCategory)
            mBundle = bundle
            handler?.removeMessages(MESSAGE_TYPE_3)
            handler?.sendEmptyMessageDelayed(MESSAGE_TYPE_3, handlerDelay)
        } else {
            mBundle = bundle
            handler?.removeMessages(MESSAGE_TYPE_2)
            handler?.sendEmptyMessageDelayed(MESSAGE_TYPE_2, handlerDelay)
        }
    }


    /**
     * Init handler
     * msg.what - for execute the task as per providing task id
     */
    private fun initHandler() {
        handler = object : Handler(Looper.getMainLooper()) {

            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    // Handle different message types here
                    MESSAGE_TYPE_1 -> {
                        removeMessages(MESSAGE_TYPE_1)
                        launchPopupAccordingToPriority()
                    }

                    MESSAGE_TYPE_2 -> {
                        removeMessages(MESSAGE_TYPE_2)
                        navigateOrUpdateFaultErrorTypeAFragment()
                    }

                    MESSAGE_TYPE_3 -> {
                        removeMessages(MESSAGE_TYPE_3)
                        navigateOrUpdateServiceAndSupportFragment()
                    }

                    else -> {
                        // Handle unknown message types (optional)
                    }
                }
            }
        }
    }

    /**
     * Launch fault popup as per priority and other verification conditions
     * find out the category and verify
     * Category A or C popup launch
     * Category B popup launch
     */
    private fun launchPopupAccordingToPriority() {
        if (SettingsViewModel.getSettingsViewModel().isUnboxing.value != true) {
            HMIExpansionUtils.startOrStopKnobLEDFastBlinkAnimation(true)
            HMIExpansionUtils.startKnobFastBlinkingTimeout()
            val sharedViewModel =
                ContextProvider.getFragmentActivity()?.let { ViewModelProvider(it) }
                    ?.get(SharedViewModel::class.java)
            sharedViewModel?.setCurrentDisplayedFaultInPrimaryCavity(mCavity == Constants.PRIMARY_CAVITY_KEY)
            val faultName: String? = mFragment?.let { getFaultName(it) }
            val getFaultId = FaultSubCategory.getById(mFaultCategory)
            HMILogHelper.Logd(tag,"getFaultId --> $getFaultId")
            when (getFaultId) {
                FaultSubCategory.CATEGORY_B2 -> if (isB2FaultCodeRepeatedThriceWithin24Hr(
                        faultCode
                    )
                ) {
                    mFragment?.let {
                        faultCategoryAOrC(
                            mCavity, it, FaultSubCategory.CATEGORY_C.ordinal
                        )
                    }
                } else {
                    if (faultName != null) {
                        mFragment?.let {
                            PopUpBuilderUtils.faultCategoryBPopupBuilder(
                                it, faultName, faultCode, mFaultCategory, mCavity
                            )
                        }
                    }
                }

                FaultSubCategory.CATEGORY_B -> mFragment?.let {
                    if (faultName != null) {
                        PopUpBuilderUtils.faultCategoryBPopupBuilder(
                            it, faultName, faultCode, mFaultCategory, mCavity
                        )
                    }
                }

                FaultSubCategory.CATEGORY_C, FaultSubCategory.CATEGORY_A -> mFragment?.let {
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_FAULT_A_C)
                    faultCategoryAOrC(
                        mCavity, it, mFaultCategory
                    )
                }

                FaultSubCategory.NOT_APPLICABLE -> {}
                else -> {}
            }
        }
    }

    /**
     * Method to use for navigate to service and support fragment
     * Or fragment is already present at top then update the value
     */
    private fun navigateOrUpdateServiceAndSupportFragment() {
        val shouldNavigateToDetails =
            mNavHostFragment?.requireView()?.let { Navigation.findNavController(it) }
                ?.let { NavigationUtils.shouldNavigate(it, R.id.fragment_error_screen) }

        if (shouldNavigateToDetails == true) {
            mNavHostFragment?.let {
                NavigationUtils.navigateSafely(
                    it, R.id.global_action_go_to_error_screen, mBundle, null
                )
            }
        } else {
            val visibleFragment = NavigationUtils.getVisibleFragment()
            val currentFragment = visibleFragment?.childFragmentManager?.primaryNavigationFragment
            // Handle the case where you're already at the BaseInformationServiceAndSupportFragment
            if (currentFragment is BaseInformationServiceAndSupportFragment) {
                currentFragment.updateFaultValues(mBundle)
            }
            HMILogHelper.Logd("Already at destination: service and support fragment")
        }
    }

    /**
     * Method to use for navigate to fault type A fragment
     * Or fragment is already present at top then update the value
     */
    private fun navigateOrUpdateFaultErrorTypeAFragment() {
        val shouldNavigateToDetails =
            mNavHostFragment?.requireView()?.let { Navigation.findNavController(it) }?.let {
                NavigationUtils.shouldNavigate(
                    it, R.id.faultErrorTypeAFragment
                )
            }

        if (shouldNavigateToDetails == true) {
            mNavHostFragment?.let {
                NavigationUtils.navigateSafely(
                    it, R.id.global_action_to_fault_error_type_a, mBundle, null
                )
            }
        } else {
            val visibleFragment = NavigationUtils.getVisibleFragment()
            val currentFragment = visibleFragment?.childFragmentManager?.primaryNavigationFragment

            // Handle the case where you're already at the FaultErrorTypeAFragment
            if (currentFragment is FaultErrorTypeAFragment) {
                currentFragment.updateFaultValues(mBundle)
            }
            HMILogHelper.Logd( "Already at destination: fault type A fragment")
        }
    }
}