package android.presenter.fragments.settings

import android.os.Bundle
import android.presenter.basefragments.AbstractStringTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringItemClickInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringTumblerItem
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.audio.WHRAudioManager
import com.whirlpool.hmi.uicomponents.audio.WHRAudioManager.ALARM_TIMER_SOUND
import com.whirlpool.hmi.uicomponents.audio.WHRAudioManager.BUTTON_EFFECT_SOUND
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.AppConstants.ALERTS_AND_TIMERS
import core.utils.AppConstants.BUTTONS_AND_EFFECTS
import core.utils.AppConstants.CONSTANT_MAX_INTENSITY
import core.utils.AppConstants.DISPLAY_BRIGHTNESS
import core.utils.BundleKeys
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.setListObjectWithDefaultSelection

/**
 * File        : android.presenter.fragments.settings.IntensitySelectionFragment
 * Brief       : Instance of Abstract String Tumbler fragment to represent the Sound / Display options on horizontal tumbler
 * Author      : Vijay Shinde
 * Created On  : 11-October-2024
 * Details     : Instance of Abstract String Tumbler fragment to represent the Sound / Display options on horizontal tumbler
 */
class IntensitySelectionFragment : AbstractStringTumblerFragment(),
        AbstractStringTumblerFragment.CustomClickListenerInterface,
        HeaderBarWidgetInterface.CustomClickListenerInterface, TextStringItemClickInterface,View.OnScrollChangeListener {
    private var intensityOptionList: HashMap<String, Long>? = null
    override fun initTumbler() {
        initTemperatureTumbler()
    }

    /**
     * Method to manage Learn More icons data
     */
    override fun initTemperatureTumbler() {
        tumblerViewHolderHelper?.let { helper ->
            helper.providePrimaryImageView()?.setOnClickListener(this)
            helper.provideGhostImageView()?.setOnClickListener(this)
            helper.provideNumericTumbler()?.itemAnimator = null
            helper.provideNumericTumbler()?.setOnScrollChangeListener(this)
        }
        tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.tumblerString?.apply {
            baseItemAnimator = null
            modifierItemAnimator = null
        }
        setTumblerStringTempData()
        setHeaderBarViews()
    }

    override fun setTumblerItemDivider(itemDivider: Int,  tumbler: BaseTumbler?) {
        super.setTumblerItemDivider(itemDivider, tumbler)
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return 0
    }

    private fun getIntensityType(): Int? {
        return arguments?.getInt(BundleKeys.BUNDLE_INTENSITY_TYPE)
    }

    /**
     * return intensity type ID
     */
    private fun getIntensityTypeStringId(): Int {
        return when (getIntensityType()) {
            ALERTS_AND_TIMERS -> R.string.alerts_and_timers
            BUTTONS_AND_EFFECTS -> R.string.buttons_and_effects
            DISPLAY_BRIGHTNESS -> R.string.display_and_brightness
            else -> R.string.alerts_and_timers
        }
    }

    /**
     * return intensity key array ID
     */
    private fun getIntensityKeyArrayId(): Int {
        return when (getIntensityType()) {
            ALERTS_AND_TIMERS, BUTTONS_AND_EFFECTS -> R.array.sound_button
            DISPLAY_BRIGHTNESS -> R.array.display_brightness_array
            else -> R.array.sound_button //default case
        }
    }

    /**
     * return intensity values array ID
     */
    private fun getIntensityValuesArrayId(): Int {
        return when (getIntensityType()) {
            ALERTS_AND_TIMERS, BUTTONS_AND_EFFECTS -> R.array.sound_button_values
            DISPLAY_BRIGHTNESS -> R.array.display_brightness_values
            else -> R.array.sound_button_values // Default case
        }
    }

    /**
     * return intensity stored values from sdk
     */
    private fun getIntensityStoredValues(): Int? {
        return when (getIntensityType()) {
            ALERTS_AND_TIMERS -> SettingsViewModel.getSettingsViewModel().alarmTimerVolumePercentage.value
            BUTTONS_AND_EFFECTS -> SettingsViewModel.getSettingsViewModel().buttonVolumePercentage.value
            DISPLAY_BRIGHTNESS -> SettingsViewModel.getSettingsViewModel().brightness.value
            else -> CONSTANT_MAX_INTENSITY // Default case
        }
    }

    /**
     * set the header bar widget data according to the option
     *
     */
    override fun setHeaderBarViews() {
        tumblerViewHolderHelper?.let { helper ->
            val headerBarWidget = helper.provideHeaderBarWidget()
            headerBarWidget?.apply {
                setInfoIconVisibility(false)
                setRightIconVisibility(false)
                setOvenCavityIconVisibility(false)
                setTitleText(resources.getString(getIntensityTypeStringId()))
                setCustomOnClickListener(this@IntensitySelectionFragment)
            }

            helper.provideGhostButton()?.visibility = View.GONE
            helper.providePrimaryButton()?.visibility  =View.GONE
        }
        setCustomClickListener(this)
    }

    /**
     * load the json data for the tumbler against pyro
     */
    override fun setTumblerStringTempData() {
        // Fetch the string-array from the resources
        val intensityOption = resources.getStringArray(getIntensityKeyArrayId())
        val intensityValues = resources.getIntArray(getIntensityValuesArrayId()).map { it.toLong() }.toTypedArray()
        // Initialize a HashMap<String, Long>
        intensityOptionList = HashMap()

        // Fill the HashMap with key-value pairs (key: sound name, value: long value)
        intensityOption.forEachIndexed { index, option ->
            intensityOptionList!![option] = intensityValues[index]
        }

        intensityOption.forEach {
            tumblerViewHolderHelper?.provideNumericTumbler()?.itemViewHolder =
                    TextStringTumblerItem(it, this, font = R.font.roboto_light)
        }

        val currentStoredValue = getIntensityStoredValues()
        val keyForValue = intensityOptionList!!.filterValues {
            it == (currentStoredValue?.toLong() ?: CONSTANT_MAX_INTENSITY)
        }.keys.firstOrNull()
        val intensityTumblerList: ViewModelListInterface =
                getIntensityList(intensityOption)
        tumblerViewHolderHelper?.provideNumericTumbler()
                ?.setListObjectWithDefaultSelection(
                        intensityTumblerList,
                        keyForValue
                )
        setTumblerItemDivider(R.drawable.tumbler_divider, tumblerViewHolderHelper?.provideNumericTumbler() )
    }

    /**
     * return list interface that will pop in to tumbler
     */
    private fun getIntensityList(
            tumblerDataValueList: Array<String>
    ): ViewModelListInterface {
        return object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                val tumblerOption = ArrayList<String>()
                tumblerDataValueList.forEach {
                    tumblerOption.add(it)
                }
                return tumblerOption
            }

            override fun getDefaultString(): String {
                return tumblerDataValueList[(tumblerDataValueList.size + 1) / 2]
            }

            override fun getValue(index: Int): Any {
                return tumblerDataValueList[index]
            }

            override fun isValid(value: Any): Boolean {
                return tumblerDataValueList.contains(value.toString())
            }
        }
    }

    /**
     * set intensity value for different type of settings
     */
    private fun setIntensityValue(intensity: Int) {
        when (getIntensityType()) {
            ALERTS_AND_TIMERS -> {
                SettingsViewModel.getSettingsViewModel().setAlarmTimerVolume(intensity)
                playAlertsAndTimerSound(intensity)
            }

            BUTTONS_AND_EFFECTS -> {
                SettingsViewModel.getSettingsViewModel()
                    .setButtonVolume(intensity)
                playButtonAndEffect(intensity)
            }

            DISPLAY_BRIGHTNESS -> SettingsViewModel.getSettingsViewModel().setBrightness(intensity)
        }
    }

    private fun playButtonAndEffect(intensity: Int) {
        if ((SettingsViewModel.getSettingsViewModel().buttonVolumePercentage.value != intensity) &&
            (intensity != resources.getInteger(R.integer.integer_range_0))) {
            HMILogHelper.Logd("BUTTONS_AND_EFFECTS - Playing notification with intensity = $intensity")
            WHRAudioManager.getInstance()
                .playAudio(context, R.raw.brand_event, BUTTON_EFFECT_SOUND)
        } else {
            if (intensity == resources.getInteger(R.integer.integer_range_0)) {
                WHRAudioManager.getInstance().stopAudio(R.raw.brand_event)
            }
        }
    }

    private fun playAlertsAndTimerSound(intensity: Int) {
        if ((SettingsViewModel.getSettingsViewModel().alarmTimerVolumePercentage.value != intensity) &&
            (intensity != resources.getInteger(R.integer.integer_range_0))) {
            HMILogHelper.Logd("ALERTS_AND_TIMERS - Playing notification with intensity = $intensity ")
            WHRAudioManager.getInstance()
                .playAudio(context, R.raw.long_notification_1, ALARM_TIMER_SOUND)
        } else {
            if (intensity == resources.getInteger(R.integer.integer_range_0)) {
                WHRAudioManager.getInstance().stopAudio(R.raw.long_notification_1)
            }
        }
    }


    /**
     * return bundle information to navigate
     */
    private fun getBundleInformation(): Bundle {
        val bundle = Bundle()
        bundle.putString(BundleKeys.BUNDLE_NAVIGATED_FROM, arguments?.getString(BundleKeys.BUNDLE_NAVIGATED_FROM))
        when (getIntensityType()) {
            ALERTS_AND_TIMERS,
            BUTTONS_AND_EFFECTS -> {
                bundle.putInt(BundleKeys.BUNDLE_SOUND_DISPLAY, AppConstants.SOUND_VOLUME)
            }

            DISPLAY_BRIGHTNESS -> {
                bundle.putInt(BundleKeys.BUNDLE_SOUND_DISPLAY, AppConstants.DISPLAY_AND_BRIGHTNESS)
            }
        }
        return bundle
    }

    /**
     * @param view on the which the click listener to be applied.
     */
    override fun viewOnClick(view: View?) {
        val id = view?.id
        if (id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.btnPrimary?.id) {
            val selectedKey =
                    tumblerViewHolderHelper?.provideNumericTumbler()?.selectedIndex?.let {
                        tumblerViewHolderHelper?.provideNumericTumbler()?.listObject?.getValue(
                                it
                        )
                    }

            val value = intensityOptionList?.get(selectedKey)
            if (value != null) {
                setIntensityValue(value.toInt())
            }
            NavigationUtils.navigateSafely(
                    this,
                    R.id.action_intensitySelectionFragment_to_soundVolumeDisplayFragment,
                    getBundleInformation(),
                    null
            )
        }
    }

    override fun leftIconOnClick() {
        KnobNavigationUtils.setBackPress()
        NavigationUtils.navigateSafely(
            this,
            R.id.action_intensitySelectionFragment_to_soundVolumeDisplayFragment,
            getBundleInformation(),
            null
        )
    }

    override fun onHMILeftKnobClick() {
        KnobNavigationUtils.knobBackTrace = true
        onClick(tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.btnPrimary)
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            val listOfKeys: ArrayList<String>? =
                    intensityOptionList?.keys?.let { ArrayList(it) }
            if (listOfKeys != null) {
                manageKnobRotation(knobDirection)
            }
        }
    }

    override fun onItemClick(index: Int, isKnobClick: Boolean) {
        if (isKnobClick) {
            NavigationUtils.navigateSafely(
                    this,
                    R.id.action_intensitySelectionFragment_to_soundVolumeDisplayFragment,
                    getBundleInformation(),
                    null
            )
        }
    }

    override fun onScrollChange(view: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        if (view?.id == R.id.tumblerString ) {
            val selectedKey =
                tumblerViewHolderHelper?.provideNumericTumbler()?.selectedIndex?.let {
                    tumblerViewHolderHelper?.provideNumericTumbler()?.listObject?.getValue(
                        it
                    )
                }

            val value = intensityOptionList?.get(selectedKey)
            if (value != null) {
                HMILogHelper.Logd("Setting intensity SelectedKey = $selectedKey,Value = $value")
                setIntensityValue(value.toInt())
            }
        }
    }

    override fun onStop() {
        super.onStop()
        //once we destroy the fragment stop playing the long notification
        WHRAudioManager.getInstance().stopAudio(R.raw.long_notification_1)
    }
}