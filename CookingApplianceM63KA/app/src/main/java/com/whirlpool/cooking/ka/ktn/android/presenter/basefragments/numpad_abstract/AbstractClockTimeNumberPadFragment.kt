package android.presenter.basefragments.numpad_abstract

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.os.Bundle
import android.presenter.customviews.NumpadHelper
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.whirlpool.cooking.ka.BuildConfig
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentClockTimeNumberpadBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView
import core.utils.*
import java.util.Calendar

/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */ /**
 * File       : java/com/whirlpool/cooking/ka/ktn/android/presenter/basefragments/numpad_abstract/AbstractClockTimeNumberPadFragment.kt
 * Brief      : extends with AbstractNumpadViewHolder
 * Author     : Gaurav Pete
 * Created On : 04-01-2024
 * Details    : AbstractNumpadViewHolder provides the common abstract funtions that could be used in feature implemented abstract class
 */
@Suppress("unused")
abstract class AbstractClockTimeNumberPadFragment : AbstractNumpadViewHolder(),
    KeyboardInputManagerInterface, View.OnClickListener {

    private var fragmentClockTimeNumberpadBinding: FragmentClockTimeNumberpadBinding? = null
    private var settingsViewModel: SettingsViewModel? = null
    private var currentOptionIndex = -1
    private var isTwelveHrFormat = false
    private var numpadHelper: NumpadHelper? = null
    private var digitCount = 0
    private var isScreenFreshlyLoaded = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentClockTimeNumberpadBinding = FragmentClockTimeNumberpadBinding.inflate(inflater)
        fragmentClockTimeNumberpadBinding?.lifecycleOwner = this
        return fragmentClockTimeNumberpadBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        numpadHelper = NumpadHelper()
        numpadHelper?.initNumpad(this, requireContext())
        numpadHelper?.keyboardCommonVM?.onTextEntry()?.observe(
            viewLifecycleOwner
        ) { text: CharSequence ->
            numPadInput(
                text
            )
        }
        setUpViewModels()
        initViews()
        updateCurrentOptionIndex()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        numpadHelper?.clearKeyboardViewModel()
        clearMemory()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.image_view_backspace_icon -> {
                onBackSpaceIconClick()
            }
            R.id.text_view_units_modifier -> {
                toggleAmPm()
            }
            R.id.next_btn -> {
                isValidTime()
                onSetButtonClicked(NavigationUtils.getViewSafely(this) ?: requireView())
            }
        }
    }

    override fun getKeyboardView(): KeyboardView? {
        return providesKeyBoardView()
    }

    override fun providesKeyBoardView(): KeyboardView? {
        return fragmentClockTimeNumberpadBinding?.keyboardview
    }

    override fun providesTemperatureTextView(): KeypadTextView? {
        //  Provides Temperature Textview
        return null
    }

    override fun providesClockHoursTextView(): TextView? {
        return fragmentClockTimeNumberpadBinding?.textViewClockTimeHour
    }

    override fun providesClockMinutesTextView(): TextView? {
        return fragmentClockTimeNumberpadBinding?.textViewClockTimeMinute
    }

    override fun providesClockSecondTextView(): TextView? {
        //  Provides Clock Second Textview
        return null
    }

    override fun providesKeypadDateTextView(): KeypadTextView? {
        //provide date textview
        return null
    }

    override fun providesClockUnitsTextView(): TextView? {
        return fragmentClockTimeNumberpadBinding?.textViewUnitsModifier
    }

    override fun providesErrorHelperTextView(): TextView? {
        return fragmentClockTimeNumberpadBinding?.errorTextHelper
    }

    override fun providesBackSpaceImageView(): ImageView? {
        return fragmentClockTimeNumberpadBinding?.imageViewBackspaceIcon
    }

    protected open fun getFragmentClockTimeNumberPadBinding(): FragmentClockTimeNumberpadBinding? {
        return fragmentClockTimeNumberpadBinding
    }

    /*
    * show the text on text view after press the key on keypad
    * - set the visibility for error Helper text
    * */
    protected open fun numPadInput(text: CharSequence) {
        updateClockInputContent(text)
        setValidTimeHelper(true)
    }

    /*
    * use to update the clock input content
    * */
    protected open fun updateClockInputContent(text: CharSequence) {
        val currentInputFieldContent = providesClockHoursTextView()?.text.toString() +
                providesClockMinutesTextView()?.text.toString()
        /*Whenever user presses a key, append the user selected key at the last of previous value
         * clock input filed has 4 char, to append char to last add the first three char along
         * with newly entered char */
        val updatedContent: String
        /*When user enter 5th digit it will clear the text and and 5th digit goes to beginning   of clock time*/digitCount++
        if (digitCount > 4) {
            digitCount = 1
            updatedContent = AppConstants.DEFAULT_TRIPLE_ZERO +
                    text[text.length - 1]
        } else {
            if (isScreenFreshlyLoaded && currentInputFieldContent != AppConstants.EMPTY_STRING) {
                isScreenFreshlyLoaded = false
                updatedContent = AppConstants.DEFAULT_TRIPLE_ZERO + text
            } else updatedContent = currentInputFieldContent.substring(1, 4) +
                    text[text.length - 1]
        }
        setClockTime(updatedContent)
    }

    /* set the generated time in to text view
    * */
    protected open fun setClockTime(clockTimeText: String) {
        providesClockHoursTextView()?.text = clockTimeText.substring(0, 2)
        providesClockMinutesTextView()?.text = clockTimeText.substring(2, 4)
    }

    /*
    * use to set up the view models
    * */
    protected open fun setUpViewModels() {
        fragmentClockTimeNumberpadBinding?.cookingViewModel =
            CookingViewModelFactory.getInScopeViewModel()
        fragmentClockTimeNumberpadBinding?.numberPadFragment = this
        settingsViewModel = SettingsViewModel.getSettingsViewModel()
        isTwelveHrFormat =
            settingsViewModel?.getUserDataStringValue(AppConstants.KEY_TIME_FORMAT, false)
                .equals(getString(R.string.text_12_HR), ignoreCase = true)
    }

    /*to get the setting view model*/
    protected open fun getSettingsViewModel(): SettingsViewModel? {
        return settingsViewModel
    }


    /*initialized the click listener*/
    protected open fun initViews() {
        providesBackSpaceImageView()?.setOnClickListener(this)
        providesClockUnitsTextView()?.setOnClickListener(this)
        fragmentClockTimeNumberpadBinding?.nextBtn?.setOnClickListener(this)
    }

    /*
    * To update the current index value*/
    protected open fun updateCurrentOptionIndex() {
        if (arguments != null && requireArguments().containsKey(AppConstants.BUNDLE_NEXT_MANDATORY_OPTION_INDEX)) {
            currentOptionIndex =
                requireArguments().getInt(AppConstants.BUNDLE_NEXT_MANDATORY_OPTION_INDEX)
        }
    }

    /*At the beginning when user set the clock time whether it's 12 hours or 24 hours according to that We need make the validation and set visibility of Am/Pm text on screen */
    protected open fun initClockTime() {
        var time =
            getSettingsViewModel()?.getUserDataStringValue(AppConstants.KEY_TIME, false) // HHMM
        if (isTwelveHrFormat) {
            time =
                NumpadUtils.convertTo12(time) //If time is not valid , default value will be set in the
            setAmPmVisibility(true)
            if (time.length >= 6) {
                setAmPm(
                    time.substring(4, 6).equals(
                        getString(R.string.text_label_am),
                        ignoreCase = true
                    )
                )
            }
        } else {
            setAmPmVisibility(false)
        }
        if (time != null) {
            setInitialClockTime(time)
        }
    }

    /*Verify the input clock time is valid or not*/
    open fun isValidTime(): Boolean {
        val hour = providesClockHoursTextView()?.text.toString().toInt()
        val min = providesClockMinutesTextView()?.text.toString().toInt()
        val isValid = NumpadHelper.isValidTime(hour, min, isTwelveHrFormat)
        setValidTimeHelper(isValid)
        return isValid
    }

    /*
    * validate and show or hide the error helper text*/
    open fun setValidTimeHelper(isValid: Boolean) {
        if (isValid) {
            providesErrorHelperTextView()?.visibility = View.GONE
        } else {
            providesErrorHelperTextView()?.visibility = View.VISIBLE
        }
    }

    /*While setting the clock time for 12 hours, we need to provide the facility to change Am text to Pm or Pm to Am on click of Am/Pm text */
    protected open fun toggleAmPm() {
        var isAm = providesClockUnitsTextView()?.text.toString()
            .equals(getString(R.string.text_label_am), ignoreCase = true)
        isAm = !isAm
        if (isAm) {
            animateUnitsModifierText(getString(R.string.text_label_am))
        } else {
            animateUnitsModifierText(getString(R.string.text_label_pm))
        }
    }

    /*To animate the Am/Pm units modifiers */
    open fun animateUnitsModifierText(text: String) {
        val unitsModifierAnimator = AnimatorSet()
        val fadeOutAnimator = AnimatorInflater.loadAnimator(
            context,
            R.animator.animator_numpad_units_modifier_text_fade_out
        )
        fadeOutAnimator.setTarget(providesClockUnitsTextView())
        fadeOutAnimator.interpolator = EaseInOutExpoInterpolator()
        fadeOutAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                setSpannableUnitsModifierText(text)
            }
        })
        val fadeInAnimator = AnimatorInflater.loadAnimator(
            context,
            R.animator.animator_numpad_units_modifier_text_fade_in
        )
        fadeInAnimator.setTarget(providesClockUnitsTextView())
        fadeInAnimator.interpolator = EaseInOutExpoInterpolator()
        unitsModifierAnimator.play(fadeOutAnimator).with(fadeInAnimator)
        unitsModifierAnimator.start()
    }

    /*
    * TO remove the text from clock text views one by one
    * */
    protected open fun onBackSpaceIconClick() {
        if (digitCount != 0) {
            digitCount--
        }
        val currentInputFieldContent =
            fragmentClockTimeNumberpadBinding?.textViewClockTimeHour?.text.toString() +
                    providesClockMinutesTextView()?.text.toString()
        /*When backspace is clicked,last char in the string is deleted and first three char
         will be shifted to right by one position*/
        val updatedContent = "0" + currentInputFieldContent.substring(0, 3)
        isScreenFreshlyLoaded = false
        setClockTime(updatedContent)
        setValidTimeHelper(true)
    }

    /*To get or extract the text which is enter in Clock text view*/
    open fun extractEnteredTime(): String {
        val hour = getFragmentClockTimeNumberPadBinding()?.textViewClockTimeHour?.text.toString()
        val min = getFragmentClockTimeNumberPadBinding()?.textViewClockTimeMinute?.text.toString()
        return hour + min
    }

    /* this method is use to 24 or 12  hours time formate*/
    protected open fun handleClockSetButtonClick(): Boolean {
        var enteredTime = extractEnteredTime()
        if (isValidTime()) {
            if (isTwelveHrFormat) {
                enteredTime = convertTo24(
                    enteredTime,
                    providesClockUnitsTextView()?.text.toString()
                        .equals(
                            getString(R.string.text_label_am),
                            ignoreCase = true
                        )
                )
                if (enteredTime == AppConstants.EMPTY_STRING) {
                    //As time is not valid , don't allow to store in settings view model
                    HMILogHelper.Logi("invalid 24Hours time")
                    return false
                }
            }

            //Don't see a good way to move this to Utils, since its single use keep here!
            val hours = enteredTime.substring(0, 2).toInt()
            val minutes = enteredTime.substring(2, 4).toInt()
            val currentTime = Calendar.getInstance()
            val year = currentTime[Calendar.YEAR]
            val month = currentTime[Calendar.MONTH]
            val day = currentTime[Calendar.DAY_OF_MONTH]
            var settingResponse: Boolean =
                getSettingsViewModel()?.setTimeModeManual(year, month, day, hours, minutes) == true
            settingResponse = settingResponse and (getSettingsViewModel()?.setUserDataStringValue(
                AppConstants.KEY_TIME, enteredTime, false
            ) == true)
            return if (BuildConfig.IS_REAL_ACU_BUILD) {
                settingResponse
            } else {
                true
            }
        }
        HMILogHelper.Loge("invalid time")
        return false
    }

    /*
    * to set up the initial clock time
    * */
    protected open fun setInitialClockTime(clockLblText: String) {
        var clockText = clockLblText
        if (clockText.length < resources.getInteger(R.integer.clock_time_max_length)) {
            clockText = AppConstants.DEFAULT_CLOCK_TIME
        }
        setClockTime(clockText)
    }

    /*
    * To clear the view Binding*/
    open fun clearMemory() {
        fragmentClockTimeNumberpadBinding = null
    }


    /* when the text convert from 12 hours to 24 hours
    * */
    protected open fun convertTo24(hourMin: String, isAm: Boolean): String {
        return NumpadUtils.convertTo24H(hourMin + getString(if (isAm) R.string.text_label_am else R.string.text_label_pm))
    }

    /*set the visibility of Am/Pm text*/
    protected open fun setAmPmVisibility(isVisible: Boolean) {
        if (isVisible) {
            providesClockUnitsTextView()?.visibility = View.VISIBLE
        } else {
            providesClockUnitsTextView()?.visibility = View.GONE
        }
    }

    /*Set the Am/Pm*/
    protected open fun setAmPm(isAm: Boolean) {
        if (View.VISIBLE != providesClockUnitsTextView()?.visibility) {
            return
        }
        if (isAm) {
            setSpannableUnitsModifierText(getString(R.string.text_label_am))
        } else {
            setSpannableUnitsModifierText(getString(R.string.text_label_pm))
        }
    }

    /*	This method show the line under the AM/Pm Text*/
    protected open fun setSpannableUnitsModifierText(string: String) {
        val content = SpannableString(string)
        content.setSpan(
            providesClockUnitsTextView()?.let {
                CustomUnderlineSpan(
                    requireContext(),
                    it
                )
            },
            0, string.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        providesClockUnitsTextView()?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        providesClockUnitsTextView()?.text = content
    }


    /* need implement for time validity*/
    protected abstract fun checkValidity(): Boolean

    /*for navigation thing*/
    protected abstract fun onSetButtonClicked(view: View?)


}