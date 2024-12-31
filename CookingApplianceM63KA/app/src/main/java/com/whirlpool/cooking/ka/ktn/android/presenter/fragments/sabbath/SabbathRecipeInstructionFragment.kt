/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.sabbath

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.Navigation
import androidx.transition.Fade
import androidx.transition.Visibility
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.SabbathInstructionFragmentBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.LogHelper
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.CAVITY_SELECTION_KNOB_SIZE
import core.utils.AppConstants.KNOB_COUNTER_ONE
import core.utils.AppConstants.KNOB_COUNTER_TWO
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.SabbathUtils

/**
 * File       : android.presenter.fragments.singleoven.RecipeInstructionFragment.
 * Brief      : implementation fragment class for Recipe instructions screen for Sabbath.
 * Author     : Hiren
 * Created On : 15/03/2024
 * Details    : Show Sabbath related instruction
 */
class SabbathRecipeInstructionFragment : SuperAbstractTimeoutEnableFragment(),
    HMIKnobInteractionListener {
    /**
     * To binding Fragment variables
     */
    private var fragmentManualModeInstructionBinding: SabbathInstructionFragmentBinding? = null
    private var isKnobRotated = false
    private var isRightOptionSelected = false
    lateinit var recipeName: String
    private var knobRotationCount = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        exitTransition = Fade().apply {
            duration = 150
            mode = Visibility.MODE_OUT
        }
        fragmentManualModeInstructionBinding = SabbathInstructionFragmentBinding.inflate(inflater)
        fragmentManualModeInstructionBinding?.lifecycleOwner = this
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        return fragmentManualModeInstructionBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            isRightOptionSelected = true
            isKnobRotated = true
            fragmentManualModeInstructionBinding?.textButtonRight?.setBottomViewVisible(true)
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    /**
     * Sets the visibility of the oven cavity view within the header bar widget.
     *
     * @param visibility The visibility to be set for the oven cavity view within the header bar widget.
     */
    private fun setHeaderBarOvenCavityViewVisibility(visibility: Int) {
        fragmentManualModeInstructionBinding?.headerBar?.getBinding()?.clOvenCavity?.visibility =
            visibility
    }


    /**
     * Sets the visibility of the title icon within the header bar widget.
     *
     * @param visibility The visibility to be set for the title icon within the header bar widget.
     */
    private fun setHeaderBarTitleIconVisibility(visibility: Int) {
        fragmentManualModeInstructionBinding?.headerBar?.getBinding()?.ivInfo?.visibility =
            visibility
    }

    /**
     * Sets the title of the header bar widget using a string.
     *
     * @param title The string to set as the title.
     */
    private fun setHeaderTitle(title: String?) {
        fragmentManualModeInstructionBinding?.headerBar?.getBinding()?.tvTitle?.visibility =
            View.VISIBLE
        fragmentManualModeInstructionBinding?.headerBar?.getBinding()?.tvTitle?.text = title
    }

    /**
     * Sets the instruction text using the string resource ID.
     *
     * @param resId The resource ID of the string to set as the instruction text.
     */
    private fun setInstructionText(@StringRes resId: Int) {
        if (CookingAppUtils.isValidStringResource(resId, requireContext())) {
            fragmentManualModeInstructionBinding?.descriptionText?.visibility = View.VISIBLE
            fragmentManualModeInstructionBinding?.descriptionText?.setText(resId)
        } else {
            fragmentManualModeInstructionBinding?.descriptionText?.visibility = View.GONE
            LogHelper.Loge("Invalid string resource ID: $resId")
        }
    }

    private fun loadData() {
        recipeName = arguments?.getString(BundleKeys.RECIPE_NAME) ?: ""
        HMILogHelper.Logd(tag, "sabbath recipeName $recipeName")
        setHeaderBarOvenCavityViewVisibility(View.GONE)
        fragmentManualModeInstructionBinding?.headerBar?.getBinding()?.flLeftIcon?.setOnClickListener {
            onBackButtonClick()
        }
        //Header bar
        fragmentManualModeInstructionBinding?.headerBar?.getBinding()?.flRightIcon?.visibility =
            View.GONE
        setHeaderBarTitleIconVisibility(View.INVISIBLE)
        fragmentManualModeInstructionBinding?.textButtonLeft?.visibility = View.VISIBLE

        if (recipeName.contentEquals(AppConstants.RECIPE_INSTRUCTION_SABBATH_MODE)) {
            val sabbathModeShown = SabbathUtils.isSabbathModeInstructionScreenShown()
            HMILogHelper.Logd(tag, "sabbath sabbathModeShow $sabbathModeShown")
            setHeaderTitle(getString(R.string.sabbathMode))
            setInstructionText(
                CookingAppUtils.getResIdFromResName(
                    requireContext(),
                    AppConstants.TEXT_INFORMATION + recipeName,
                    AppConstants.RESOURCE_TYPE_STRING
                )
            )
            //Bottom view

            if (!sabbathModeShown) setLeftButtonImage(R.drawable.ic_check_mark_un_checked)
            else setLeftButtonImage(R.drawable.ic_check_mark_checked)
            fragmentManualModeInstructionBinding?.textButtonLeft?.setOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    view?.context, R.raw.button_press, AudioManager.STREAM_SYSTEM, true, 0, 1
                )
                val sabbathModeShownClick = SabbathUtils.isSabbathModeInstructionScreenShown()
                HMILogHelper.Logd(tag, "sabbathModeShowClick $sabbathModeShownClick")
                if (sabbathModeShownClick) setLeftButtonImage(R.drawable.ic_check_mark_un_checked)
                else setLeftButtonImage(R.drawable.ic_check_mark_checked)
                SettingsViewModel.getSettingsViewModel().setUserDataBooleanValue(
                    AppConstants.RECIPE_INSTRUCTION_SABBATH_MODE, !sabbathModeShownClick, true
                )
            }
            fragmentManualModeInstructionBinding?.textButtonRight?.setOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    view?.context, R.raw.button_press, AudioManager.STREAM_SYSTEM, true, 0, 1
                )
                HMILogHelper.Logd(tag, "navigating to activate SabbathMode")
                SabbathUtils.startSabbathMode(this)
            }
            fragmentManualModeInstructionBinding?.textButtonRight?.setTextButtonText(getString(R.string.text_button_start))
        } else {
            val sabbathBakeShown = SabbathUtils.isSabbathBakeInstructionScreenShown()
            HMILogHelper.Logd(tag, "sabbath sabbathBakeShown $sabbathBakeShown")
            setHeaderTitle(getString(R.string.sabbathBake))
            setInstructionText(
                CookingAppUtils.getResIdFromResName(
                    requireContext(),
                    AppConstants.TEXT_INFORMATION + recipeName,
                    AppConstants.RESOURCE_TYPE_STRING
                )
            )
            //Bottom view

            if (!sabbathBakeShown) setLeftButtonImage(R.drawable.ic_check_mark_un_checked)
            else setLeftButtonImage(R.drawable.ic_check_mark_checked)
            fragmentManualModeInstructionBinding?.textButtonLeft?.setOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    view?.context, R.raw.button_press, AudioManager.STREAM_SYSTEM, true, 0, 1
                )
                val sabbathBakeShownClick = SabbathUtils.isSabbathBakeInstructionScreenShown()
                HMILogHelper.Logd(tag, "sabbathBakeShownClick $sabbathBakeShownClick")
                if (sabbathBakeShownClick) setLeftButtonImage(R.drawable.ic_check_mark_un_checked)
                else setLeftButtonImage(R.drawable.ic_check_mark_checked)
                SettingsViewModel.getSettingsViewModel().setUserDataBooleanValue(
                    AppConstants.RECIPE_INSTRUCTION_SABBATH_BAKE, !sabbathBakeShownClick, true
                )
            }
            fragmentManualModeInstructionBinding?.textButtonRight?.setOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    view?.context, R.raw.button_press, AudioManager.STREAM_SYSTEM, true, 0, 1
                )
                if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
                    HMILogHelper.Logd(tag, "Sabbath : navigating to SabbathCavitySelection")
                    NavigationUtils.navigateSafely(
                        this,
                        R.id.action_sabbathInstructionFragment_to_sabbathCavitySelectionFragment,
                        null,
                        null
                    )
                } else {
                    HMILogHelper.Logd(tag, "Sabbath : navigating to SabbathBake recipe options")
                    NavigationUtils.navigateAfterSabbathRecipeSelection(
                        this,
                        CookingViewModelFactory.getInScopeViewModel()
                    )
                }
            }
        }
    }

    /**
     * Sets the image resource of the left button.
     *
     * @param resId The resource ID of the drawable to set as the image resource of the left button.
     */
    private fun setLeftButtonImage(@DrawableRes resId: Int) {
        if (CookingAppUtils.isValidDrawableResource(resId, requireContext())) {
            fragmentManualModeInstructionBinding?.textButtonLeft?.visibility = (View.VISIBLE)
            fragmentManualModeInstructionBinding?.textButtonLeft?.setImageResource(resId)
        } else {
            fragmentManualModeInstructionBinding?.textButtonLeft?.visibility = (View.GONE)
            LogHelper.Loge("Invalid drawable resource ID: $resId")
        }
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        super.onDestroyView()
        fragmentManualModeInstructionBinding = null
    }

    /**
     * back header bar navigation
     */
    private fun onBackButtonClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(
                    this
                ) ?: requireView()
            )
        )
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        SabbathUtils.probeDetectedBeforeSabbathProgramming(this, cookingViewModel, {
            SabbathUtils.navigateToSabbathSettingSelection(this)
        }, {})
    }

    /*************************** KNOB Related Methods ******************************/

    override fun onHMILeftKnobClick() {
        if (isKnobRotated) {
            if (isRightOptionSelected) {
                KnobNavigationUtils.knobForwardTrace = true
                fragmentManualModeInstructionBinding?.textButtonRight?.callOnClick()
            } else {
                fragmentManualModeInstructionBinding?.textButtonLeft?.callOnClick()
            }
        }
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            isKnobRotated = false
            fragmentManualModeInstructionBinding?.textButtonRight?.setBottomViewVisible(false)
            fragmentManualModeInstructionBinding?.textButtonLeft?.setBottomViewVisible(false)
        }
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < CAVITY_SELECTION_KNOB_SIZE) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > 0) knobRotationCount--
            isKnobRotated = true
            when (knobRotationCount) {
                KNOB_COUNTER_ONE -> {
                    isRightOptionSelected = true
                    fragmentManualModeInstructionBinding?.textButtonRight?.setBottomViewVisible(true)
                    fragmentManualModeInstructionBinding?.textButtonLeft?.setBottomViewVisible(false)
                }

                KNOB_COUNTER_TWO -> {
                    isRightOptionSelected = false
                    fragmentManualModeInstructionBinding?.textButtonRight?.setBottomViewVisible(
                        false
                    )
                    fragmentManualModeInstructionBinding?.textButtonLeft?.setBottomViewVisible(true)
                }
            }
        }
    }
}