package android.presenter.fragments.settings

import android.media.AudioManager
import android.presenter.basefragments.AbstractNumericTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringItemClickInterface
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedViewModel

class TemperatureCalibrationTumbler : AbstractNumericTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface, TextStringItemClickInterface {

    private var temperatureCalibrationValue: String? = null
    override fun setCtaLeft() {
        getBinding()?.btnGhost?.visibility = INVISIBLE
    }

    override fun updateCtaRightButton() {
        //do nothing, added override to avoid Abstract fragment logic that sets a different string on the button.
    }

    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    override fun setCtaRight() {
        getBinding()?.btnPrimary?.text = getString(R.string.text_button_set)
        getBinding()?.btnPrimary?.visibility = VISIBLE
        getBinding()?.tumblerNumericBased?.setOnScrollChangeListener { v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            getBinding()?.tumblerNumericBased?.selectedIndex?.let {
                if (getBinding()?.tumblerNumericBased?.getValueForIndex(it) == temperatureCalibrationValue) {
                    getBinding()?.btnPrimary?.setTextColor(
                        resources.getColor(
                            R.color.text_button_disabled_grey,
                            null
                        )
                    )
                    getBinding()?.btnPrimary?.isEnabled = false
                    getBinding()?.btnPrimary?.setOnClickListener(null)
                } else {
                    getBinding()?.btnPrimary?.setTextColor(
                        resources.getColor(
                            R.color.color_white,
                            null
                        )
                    )
                    getBinding()?.btnPrimary?.isEnabled = true
                    getBinding()?.btnPrimary?.setOnClickListener(this@TemperatureCalibrationTumbler)
                }
            }
        }
    }

    override fun manageRightButton() {
        super.manageRightButton()
        getBinding()?.headerBar?.setRightIconVisibility(false)
    }

    override fun setHeaderLevel() {
        getBinding()?.headerBar?.setCustomOnClickListener(this)
        getBinding()?.headerBar?.setInfoIconVisibility(false)
        getBinding()?.headerBar?.setOvenCavityIconVisibility(true)
        getBinding()?.headerBar?.setOvenCavityTitleTextVisibility(true)
        getBinding()?.headerBar?.setTitleText(getString(R.string.temperature_calibration))
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN,
            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                getBinding()?.headerBar?.setOvenCavityIconVisibility(false)
            }

            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                if (CookingViewModelFactory.getInScopeViewModel()?.isPrimaryCavity == true) {
                    getBinding()?.headerBar?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
                } else {
                    getBinding()?.headerBar?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
                }
            }

            else -> {}
        }
    }

    override fun initTumbler() {
        isShowSuffixDecoration = true
        initCalibrationTemperatureTumblerForRange()
        initTumblerLabels()
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return INVISIBLE
    }

    override fun setSuffixDecoration(): String {
        return AppConstants.DEGREE_SYMBOL
    }

    override fun onClick(view: View?) {
        if (view?.id == tumblerViewHolderHelper?.providePrimaryButton()?.id) {
            val selectedIndex = tumblerViewHolderHelper?.provideNumericTumbler()?.selectedIndex
            if (selectedIndex != null) {
                onItemClick(selectedIndex)
            }
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onHMILeftKnobClick() {
        KnobNavigationUtils.knobBackTrace = true
        onHMIKnobRightOrLeftClick()
    }

    override fun onHMILongRightKnobPress() {
        //TBD
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            manageKnobRotation(knobDirection)
        }
    }

    override fun leftIconOnClick() {
        super.leftIconOnClick()
        KnobNavigationUtils.setBackPress()
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(this) ?: requireView()
            )
        )
    }

    override fun onItemClick(index: Int, isKnobClick: Boolean) {
        val calibrationValue = getBinding()?.tumblerNumericBased?.getValueForIndex(index)
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.start_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                calibrationValue?.let { it1 ->
                    SettingsViewModel.getSettingsViewModel().setUPOPrimaryCavity(
                        it1.toInt()
                    )
                }
            }

            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity.let {
                    if (it) {
                        calibrationValue?.let { it1 ->
                            SettingsViewModel.getSettingsViewModel().setUPOPrimaryCavity(
                                it1.toInt()
                            )
                        }
                    } else {
                        calibrationValue?.let { it1 ->
                            SettingsViewModel.getSettingsViewModel().setUPOSecondaryCavity(
                                it1.toInt()
                            )
                        }
                    }
                }
            }

            else -> {}
        }
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(this) ?: requireView()
            )
        )
    }

    private fun initCalibrationTemperatureTumblerForRange() {
        val upoOption = IntegerRange()
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                SettingsViewModel.getSettingsViewModel()?.upoPrimaryCavity?.value?.let {
                    upoOption.defaultValue = it
                    temperatureCalibrationValue = it.toString()
                }
            }

            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity.let { isPrimaryCavity ->
                    if (isPrimaryCavity) {
                        SettingsViewModel.getSettingsViewModel()?.upoPrimaryCavity?.value?.let {
                            upoOption.defaultValue = it
                            temperatureCalibrationValue = it.toString()
                        }
                    } else {
                        SettingsViewModel.getSettingsViewModel()?.upoSecondaryCavity?.value?.let {
                            upoOption.defaultValue = it
                            temperatureCalibrationValue = it.toString()
                        }
                    }
                }
            }

            else -> {
                upoOption.defaultValue =
                    requireContext().resources.getInteger(R.integer.temperature_default_value)
                temperatureCalibrationValue = upoOption.defaultValue.toString()
            }
        }
        SettingsViewModel.getSettingsViewModel().temperatureUnit.observe(this) {
            if (SettingsViewModel.getSettingsViewModel()?.temperatureUnit?.value == SettingsViewModel.TemperatureUnit.FAHRENHEIT) {
                upoOption.setMax(
                    requireContext().resources.getInteger(R.integer.fahrenheit_max_value).toDouble()
                )
                upoOption.setMin(
                    requireContext().resources.getInteger(R.integer.fahrenheit_min_value).toDouble()
                )
                upoOption.step =
                    requireContext().resources.getInteger(R.integer.fahrenheit_step_size)
                getBinding()?.tempeatureUnitLabel?.setText(R.string.text_temperature_unit_fahrenheit)
            } else {
                upoOption.setMax(
                    requireContext().resources.getInteger(R.integer.celsius_max_value).toDouble()
                )
                upoOption.setMin(
                    requireContext().resources.getInteger(R.integer.celsius_min_value).toDouble()
                )
                upoOption.step = requireContext().resources.getInteger(R.integer.celsius_step_size)
                getBinding()?.tempeatureUnitLabel?.setText(R.string.text_temperature_unit_celsius)
            }
            isPlusSymbolEnabled(true)
            initTumbler(upoOption)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SettingsViewModel.getSettingsViewModel().temperatureUnit.removeObservers(this)
    }

    private fun initTumblerLabels() {
        getBinding()?.degreesType?.visibility = GONE
        getBinding()?.frameUPOLabels?.visibility = VISIBLE
        if (SettingsViewModel.getSettingsViewModel()?.temperatureUnit?.value == SettingsViewModel.TemperatureUnit.FAHRENHEIT) {
            getBinding()?.tempeatureUnitLabel?.text =
                getString(R.string.text_temperature_unit_fahrenheit)
        } else {
            getBinding()?.tempeatureUnitLabel?.text =
                getString(R.string.text_temperature_unit_celsius)
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        //Meat probe removed dialog shown here and updating the screen time out. Once probe inserted then reset the screen timeout as expected.
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }
}
