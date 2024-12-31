/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.sabbath

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.SabbathDoubleCavityStartFragmentBinding
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.SabbathUtils
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * File       : android.presenter.fragments.singleoven.SabbathDoubleCavityPreviewFragment.
 * Brief      : implementation fragment class for Sabbath where Double Cavity presents
 * Author     : Hiren
 * Created On : 08/22/2024
 * Details    : Display both cavities and select the recipe instruction before starts
 */
class SabbathDoubleCavityPreviewFragment : SuperAbstractTimeoutEnableFragment(),
    HMIKnobInteractionListener, HeaderBarWidgetInterface.CustomClickListenerInterface {
        var knobCounter = 0
    /**
     * To binding Fragment variables
     */
    private var sabbathPreviewBinding: SabbathDoubleCavityStartFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        sabbathPreviewBinding = SabbathDoubleCavityStartFragmentBinding.inflate(inflater)
        sabbathPreviewBinding?.lifecycleOwner = this
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        return sabbathPreviewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        manageHeaderBar()
        manageChildViews()
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            knobCounter = 1
            sabbathPreviewBinding?.clSabbathUpperCavityStartSelection?.background =
                getDrawable(requireContext(), R.drawable.button_selected_ripple_effect)
        }
    }

    private fun manageHeaderBar() {
        sabbathPreviewBinding?.headerBar?.setOvenCavityIconVisibility(false)
        sabbathPreviewBinding?.headerBar?.setRightIconVisibility(false)
        sabbathPreviewBinding?.headerBar?.setInfoIconVisibility(false)
        sabbathPreviewBinding?.headerBar?.setTitleText(getString(R.string.sabbathBake))
        sabbathPreviewBinding?.headerBar?.setCustomOnClickListener(this)
    }

    private fun manageChildViews() {
        if (SabbathUtils.isSabbathRecipe(CookingViewModelFactory.getPrimaryCavityViewModel())) {
            sabbathPreviewBinding?.tvUpperOvenCavityRecipeInfo?.text =
                getRecipeDisplayInfo(CookingViewModelFactory.getPrimaryCavityViewModel())
        } else {
            CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.cancel()
            HMILogHelper.Logd("cancelling recipe of primary cavity SabbathDoubleCavityPreviewFragment")
        }
        if (SabbathUtils.isSabbathRecipe(CookingViewModelFactory.getSecondaryCavityViewModel())) {
            sabbathPreviewBinding?.tvLowerOvenCavityRecipeInfo?.text =
                getRecipeDisplayInfo(CookingViewModelFactory.getSecondaryCavityViewModel())
        } else {
            CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.cancel()
            HMILogHelper.Logd("cancelling recipe of secondary cavity SabbathDoubleCavityPreviewFragment")
        }
        sabbathPreviewBinding?.clSabbathUpperCavityStartSelection?.setOnClickListener {
            CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
            if(!SabbathUtils.isSabbathRecipe(CookingViewModelFactory.getPrimaryCavityViewModel())) NavigationUtils.navigateAfterSabbathRecipeSelection(this, CookingViewModelFactory.getPrimaryCavityViewModel())
            else NavigationUtils.navigateSafely(this, R.id.action_to_sabbathTemperatureTumblerFragment, null, null)
        }
        sabbathPreviewBinding?.clSabbathLowerCavityStartSelection?.setOnClickListener {
            CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getSecondaryCavityViewModel())
            if(!SabbathUtils.isSabbathRecipe(CookingViewModelFactory.getSecondaryCavityViewModel())) NavigationUtils.navigateAfterSabbathRecipeSelection(this, CookingViewModelFactory.getSecondaryCavityViewModel())
            else NavigationUtils.navigateSafely(this, R.id.action_to_sabbathTemperatureTumblerFragment, null, null)
        }
        sabbathPreviewBinding?.textButtonRight?.setOnClickListener { onCLickStartButton()}
    }

    private fun onCLickStartButton() {
        val (isProbeConnected, connectedCavityViewModel) = MeatProbeUtils.isAnyCavityHasMeatProbeConnected()
        if(isProbeConnected){
            HMILogHelper.Loge(tag, "Meat Probe is connected for cavity ${connectedCavityViewModel?.cavityName?.value}, not executing recipe until probe is removed")
            SabbathUtils.probeDetectedBeforeSabbathProgramming(this, connectedCavityViewModel, {}, {
                HMILogHelper.Loge(tag, "Meat Probe is just removed for cavity ${connectedCavityViewModel?.cavityName?.value}, executing recipe since probe is removed")
                onCLickStartButton()
            })
            return
        }
        var recipeErrorResponse : RecipeErrorResponse? = null
        val isSabbathSuccess = SettingsViewModel.getSettingsViewModel().setSabbathMode(
            SettingsViewModel.SabbathMode.SABBATH_COMPLIANT)
        HMILogHelper.Logd(tag, "Sabbath isSabbathSuccess $isSabbathSuccess")

        if (SabbathUtils.isSabbathRecipe(CookingViewModelFactory.getPrimaryCavityViewModel())) {
            recipeErrorResponse = CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.execute()
            HMILogHelper.Logd(tag, "Sabbath primaryCavity recipeErrorResponse ${recipeErrorResponse.description}")
        }
        if(recipeErrorResponse?.isError == true) {
            CookingAppUtils.handleCookingError(this, CookingViewModelFactory.getPrimaryCavityViewModel(), recipeErrorResponse, true)
            return
        }
        if (SabbathUtils.isSabbathRecipe(CookingViewModelFactory.getSecondaryCavityViewModel())) {
            recipeErrorResponse = CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.execute()
            HMILogHelper.Logd(tag, "Sabbath secondaryCavity recipeErrorResponse ${recipeErrorResponse.description}")
        }
        if(recipeErrorResponse?.isError == true) CookingAppUtils.handleCookingError(this, CookingViewModelFactory.getSecondaryCavityViewModel(), recipeErrorResponse, true)
        else CookingAppUtils.navigateToSabbathStatusOrClockScreen(this)
    }

    /**
     * create string based on set targeted temperature and cook time
     * @param viewModel for a cavity
     */
    private fun getRecipeDisplayInfo(viewModel: CookingViewModel): CharSequence {
        val stringBuilder = StringBuilder()
        stringBuilder.append(viewModel.recipeExecutionViewModel.targetTemperature.value)
            .append(AppConstants.DEGREE_SYMBOL)
        if ((viewModel.recipeExecutionViewModel.cookTime.value ?: 0) > 0) {
            stringBuilder.append(AppConstants.EMPTY_SPACE).append(AppConstants.VERTICAL_BAR).append(AppConstants.EMPTY_SPACE)
            stringBuilder.append(convertCookTimeToHourAndMin(viewModel.recipeExecutionViewModel.cookTime.value ?: 0))
        }
        HMILogHelper.Logd(
            tag,
            "Sabbath Double Cavity ${viewModel.cavityName.value} recipeInfo $stringBuilder"
        )
        return stringBuilder.toString()
    }

    /**
     * show time user facing COOK TIME TO 1 hr 20 min
     * @param timeInSeconds coming from KitchenTimerViewModel time remaining
     * @return time remaining text show hour and min if  > 0
     */
    private fun convertCookTimeToHourAndMin(timeInSeconds: Long): String {
        val stringBuild = StringBuilder()
        val hours = TimeUnit.SECONDS.toHours(timeInSeconds)
        val minutes =
            TimeUnit.SECONDS.toMinutes(timeInSeconds) - TimeUnit.HOURS.toMinutes(hours)
        if (hours > 0 && minutes > 0) {
            stringBuild.append(String.format(Locale.getDefault(), "%2d", hours)).append(AppConstants.EMPTY_SPACE).append(getString(R.string.text_label_hr)).append(AppConstants.EMPTY_SPACE)
            stringBuild.append(String.format(Locale.getDefault(), "%2d", minutes)).append(getString(R.string.text_label_min))
        }else if (hours > 0){
            stringBuild.append(String.format(Locale.getDefault(), "%2d", hours)).append(AppConstants.EMPTY_SPACE).append(getString(R.string.text_label_hr))
        }else if (minutes > 0){
            stringBuild.append(String.format(Locale.getDefault(), "%2d", minutes)).append(AppConstants.EMPTY_SPACE).append(getString(R.string.text_label_min))
        }
        HMILogHelper.Logd(tag, "Sabbath Converted CookTime $stringBuild")
        return stringBuild.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sabbathPreviewBinding = null
    }

    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                getViewSafely(this) ?: requireView()
            )
        )
    }


    /******************** Knob Related ***********************/
    override fun onHMILeftKnobClick() {
        if (knobCounter == 1) {
            sabbathPreviewBinding?.clSabbathUpperCavityStartSelection?.callOnClick()
        }
        if (knobCounter == 2) {
            sabbathPreviewBinding?.clSabbathLowerCavityStartSelection?.callOnClick()
        }
        if (knobCounter == 3 || knobCounter == 0) {
            sabbathPreviewBinding?.textButtonRight?.callOnClick()
        }
    }

    override fun onHMILongLeftKnobPress() {

    }

    override fun onHMIRightKnobClick() {
    }

    override fun onHMILongRightKnobPress() {

    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {

    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            knobCounter = 0
            sabbathPreviewBinding?.clSabbathUpperCavityStartSelection?.background =
                getDrawable(requireContext(), R.drawable.text_view_ripple_effect)
            sabbathPreviewBinding?.clSabbathLowerCavityStartSelection?.background =
                getDrawable(requireContext(), R.drawable.text_view_ripple_effect)
            sabbathPreviewBinding?.textButtonRight?.background = getDrawable(
                ContextProvider.getContext(), R.drawable.text_view_ripple_effect
            )
        }
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobCounter < 3) knobCounter++
            if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobCounter > 1) knobCounter--
            if (knobCounter <= 1) {
                sabbathPreviewBinding?.clSabbathUpperCavityStartSelection?.background =
                    getDrawable(requireContext(), R.drawable.button_selected_ripple_effect)
                sabbathPreviewBinding?.clSabbathLowerCavityStartSelection?.background = null
                sabbathPreviewBinding?.textButtonRight?.background = null
            }
            if (knobCounter == 2) {
                sabbathPreviewBinding?.clSabbathUpperCavityStartSelection?.background = null
                sabbathPreviewBinding?.clSabbathLowerCavityStartSelection?.background =
                    getDrawable(requireContext(), R.drawable.button_selected_ripple_effect)
                sabbathPreviewBinding?.textButtonRight?.background = null
            }
            if (knobCounter == 3) {
                sabbathPreviewBinding?.clSabbathUpperCavityStartSelection?.background = null
                sabbathPreviewBinding?.clSabbathLowerCavityStartSelection?.background = null
                sabbathPreviewBinding?.textButtonRight?.background = getDrawable(
                    ContextProvider.getContext(), R.drawable.selector_textview_walnut
                )
            }
        }
    }
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        SabbathUtils.probeDetectedBeforeSabbathProgramming(this, cookingViewModel, {
            SabbathUtils.navigateToSabbathSettingSelection(this)
        }, {})
    }
}