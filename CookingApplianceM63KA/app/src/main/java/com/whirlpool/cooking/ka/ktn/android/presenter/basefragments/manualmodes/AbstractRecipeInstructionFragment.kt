/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.basefragments.manualmodes

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.textButton.IconTextButton
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.transition.Fade
import androidx.transition.Visibility
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentManualModeInstructionBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.LogHelper
import com.whirlpool.hmi.utils.cookbook.records.RecipeRecord
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.CAVITY_SELECTION_KNOB_SIZE
import core.utils.AppConstants.KNOB_COUNTER_ONE
import core.utils.AppConstants.KNOB_COUNTER_TWO
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils

/**
 * File       : android.presenter.basefragments.manualmodes.AbstractRecipeInstructionFragment.
 * Brief      : Abstract class for Recipe instructions screen for manual modes.
 * Author     : BHIMAR.
 * Created On : 14/03/2024
 * Details    :
 */
abstract class AbstractRecipeInstructionFragment : SuperAbstractTimeoutEnableFragment(),
    HMIKnobInteractionListener {

    /**
     * To binding Fragment variables
     */
    private var fragmentManualModeInstructionBinding: FragmentManualModeInstructionBinding? = null
    private var isKnobRotated = false
    private var isRightOptionSelected = false
    private lateinit var recipeRecord: RecipeRecord
    lateinit var recipeName: String
    lateinit var recipeType: String
    private var knobRotationCount = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        exitTransition = Fade().apply {
            duration = 150
            mode = Visibility.MODE_OUT
        }
        fragmentManualModeInstructionBinding =
            FragmentManualModeInstructionBinding.inflate(inflater)
        fragmentManualModeInstructionBinding?.lifecycleOwner = this
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        return fragmentManualModeInstructionBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(KnobNavigationUtils.knobForwardTrace){
            KnobNavigationUtils.knobForwardTrace = false
            isRightOptionSelected = true
            isKnobRotated = true
            setLeftButtonViewVisibility(false)
            setLeftConstraintViewVisibility(false)
            setRightButtonViewVisibility(true)
            setRightConstraintViewVisibility(true)
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        if (!recipeRecord.showInstruction) {
            NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    NavigationUtils.getViewSafely(this) ?: requireView()
                )
            )
        }
    }

    /**
     * Gets the binding object for the fragment.
     *
     * @return The binding object for the fragment.
     */
    protected open fun getBinding(): FragmentManualModeInstructionBinding? {
        return fragmentManualModeInstructionBinding
    }

    /**
     * Gets the header bar widget.
     *
     * @return The header bar widget.
     */
    open fun getHeaderBar(): HeaderBarWidget? {
        return getBinding()?.headerBar
    }

    /**
     * Gets the title icon ImageView within the header bar widget.
     *
     * @return The title icon ImageView within the header bar widget.
     */
    open fun getHeaderBarTitleIcon(): ImageView? {
        return getHeaderBar()?.getBinding()?.ivInfo
    }

    /**
     * Gets the title TextView within the header bar widget.
     *
     * @return The title TextView within the header bar widget.
     */
    open fun getHeaderBarTitleTextView(): TextView? {
        return getHeaderBar()?.getBinding()?.tvTitle
    }

    /**
     * Gets the left icon view within the header bar widget.
     *
     * @return The left icon view within the header bar widget.
     */
    open fun getHeaderBarLeftIconView(): FrameLayout? {
        return getHeaderBar()?.getBinding()?.flLeftIcon
    }

    /**
     * Gets the right icon view within the header bar widget.
     *
     * @return The right icon view within the header bar widget.
     */
    open fun getHeaderBarRightIconView(): FrameLayout? {
        return getHeaderBar()?.getBinding()?.flRightIcon
    }

    /**
     * Gets the left image view.
     *
     * @return The left image view.
     */
    open fun getLeftImageView(): AppCompatImageView? {
        return getBinding()?.leftImageView
    }

    /**
     * Gets the description text view.
     *
     * @return The description text view.
     */
    open fun getDescriptionTextView(): AppCompatTextView? {
        return getBinding()?.descriptionText
    }

    /**
     * Gets the right button.
     *
     * @return The right button.
     */
    open fun getRightButton(): IconTextButton? {
        return getBinding()?.textButtonRight
    }

    open fun getRightConstraint(): ConstraintLayout? {
        return getBinding()?.constraintInstructionRight
    }

    /**
     * Gets the left button.
     *
     * @return The left button.
     */
    open fun getLeftButton(): IconTextButton? {
        return getBinding()?.textButtonLeft
    }

    open fun getLeftConstraint(): ConstraintLayout? {
        return getBinding()?.constraintInstructionLeft
    }
    /**
     * Sets the visibility of the title icon within the header bar widget.
     *
     * @param visibility The visibility to be set for the title icon within the header bar widget.
     */
    open fun setHeaderBarTitleIconVisibility(visibility: Int) {
        getHeaderBarTitleIcon()?.visibility =
            visibility
    }

    /**
     * Sets the visibility of the oven cavity view within the header bar widget.
     *
     * @param visibility The visibility to be set for the oven cavity view within the header bar widget.
     */
    open fun setHeaderBarOvenCavityViewVisibility(visibility: Int) {
        getHeaderBar()?.getBinding()?.clOvenCavity?.visibility =
            visibility
    }

    /**
     * Sets the visibility of the right icon view within the header bar widget.
     *
     * @param visibility The visibility to be set for the right icon view within the header bar widget.
     */
    open fun setHeaderBarRightIconViewVisibility(visibility: Int) {
        getHeaderBarRightIconView()?.visibility =
            visibility
    }

    /**
     * Sets the visibility of the left image view.
     *
     * @param visibility The visibility to be set for the left image view.
     */
    open fun setLeftImageViewVisibility(visibility: Int) {
        getLeftImageView()?.visibility = visibility
    }

    /**
     * Sets the visibility of the description text view.
     *
     * @param visibility The visibility to be set for the description text view.
     */
    open fun setDescriptionTextViewVisibility(visibility: Int) {
        getDescriptionTextView()?.visibility = visibility
    }

    /**
     * Sets the visibility of the right button.
     *
     * @param visibility The visibility to be set for the right button.
     */
    open fun setRightButtonVisibility(visibility: Int) {
        getRightButton()?.visibility = visibility
    }

    open fun setRightConstraintVisibility(visibility: Int) {
        getRightConstraint()?.visibility = visibility
    }

    /**
     * Sets the visibility of the left button.
     *
     * @param visibility The visibility to be set for the left button.
     */
    open fun setLeftButtonVisibility(visibility: Int) {
        getLeftButton()?.visibility = visibility
    }

    open fun setLeftConstraintVisibility(visibility: Int) {
        getLeftConstraint()?.visibility = visibility
    }

    /**
     * Sets the title of the header bar widget using a string.
     *
     * @param title The string to set as the title.
     */
    open fun setHeaderTitle(title: String?) {
        getHeaderBarTitleTextView()?.visibility =
            View.VISIBLE
        getHeaderBarTitleTextView()?.text = title
    }

    /**
     * Sets the oven cavity icon of the header bar widget using the drawable resource ID.
     *
     * @param resId The resource ID of the drawable to set as the oven cavity icon.
     */
    open fun setHeaderBarOvenCavityIcon(@DrawableRes resId: Int) {
        if (CookingAppUtils.isValidDrawableResource(
                resId,
                requireContext()
            )
        ) {
            getHeaderBar()?.getBinding()?.ivOvenCavity?.visibility =
                View.VISIBLE
            getHeaderBar()?.getBinding()?.ivOvenCavity?.setImageResource(
                resId
            )
        } else {
            getHeaderBar()?.getBinding()?.ivOvenCavity?.visibility =
                View.GONE
            LogHelper.Loge("Invalid drawable resource ID: $resId")
        }
    }

    /**
     * Sets the image resource of the left image view.
     *
     * @param resId The resource ID of the drawable to set as the image resource of the left image view.
     */
    open fun setLeftImageResource(@DrawableRes resId: Int) {
        if (CookingAppUtils.isValidDrawableResource(resId, requireContext())
        ) {
            setLeftImageViewVisibility(View.VISIBLE)
            getLeftImageView()?.setImageResource(resId)
        } else {
            setLeftImageViewVisibility(View.GONE)
            LogHelper.Loge("Invalid drawable resource ID: $resId")
        }
    }

    /**
     * Sets the instruction text using the string resource ID.
     *
     * @param resId The resource ID of the string to set as the instruction text.
     */
    open fun setInstructionText(@StringRes resId: Int) {
        if (CookingAppUtils.isValidStringResource(resId, requireContext())
        ) {
            setDescriptionTextViewVisibility(View.VISIBLE)
            getDescriptionTextView()?.setText(resId)
        } else {
            setDescriptionTextViewVisibility(View.GONE)
            LogHelper.Loge("Invalid string resource ID: $resId")
        }
    }

    /**
     * Sets the visibility of the bottom view of the right button.
     *
     * @param isVisible True if the bottom view should be visible, false otherwise.
     */
    open fun setRightButtonViewVisibility(isVisible: Boolean) {
        setRightButtonVisibility(View.VISIBLE)
        getRightButton()?.setBottomViewVisible(isVisible)
    }

    open fun setRightConstraintViewVisibility(isVisible: Boolean) {
        setRightConstraintVisibility(View.VISIBLE)
    }

    /**
     * Sets the visibility of the bottom view of the left button.
     *
     * @param isVisible True if the bottom view should be visible, false otherwise.
     */
    open fun setLeftButtonViewVisibility(isVisible: Boolean) {
        setLeftButtonVisibility(View.VISIBLE)
        getLeftButton()?.setBottomViewVisible(isVisible)
    }

    open fun setLeftConstraintViewVisibility(isVisible: Boolean) {
        setLeftConstraintVisibility(View.VISIBLE)
    }

    /**
     * Sets the image resource of the left button.
     *
     * @param resId The resource ID of the drawable to set as the image resource of the left button.
     */
    open fun setLeftButtonImage(@DrawableRes resId: Int) {
        if (CookingAppUtils.isValidDrawableResource(resId, requireContext())
        ) {
            setLeftButtonVisibility(View.VISIBLE)
            getLeftButton()?.setImageResource(resId)
        } else {
            setLeftButtonVisibility(View.GONE)
            LogHelper.Loge("Invalid drawable resource ID: $resId")
        }
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        super.onDestroyView()
    }

    protected abstract fun manageHeaderBarParametersBasedOnVariant()


    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing here override in child class if necessary
    }

    @Suppress("KotlinConstantConditions")
    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < CAVITY_SELECTION_KNOB_SIZE) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > 0) knobRotationCount--
            isKnobRotated = true
            when (knobRotationCount) {
                KNOB_COUNTER_ONE -> {
                    isRightOptionSelected = false
                    setLeftButtonViewVisibility(true)
                    setRightButtonViewVisibility(false)
                }
                KNOB_COUNTER_TWO -> {
                    isRightOptionSelected = true
                    setRightButtonViewVisibility(true)
                    setLeftButtonViewVisibility(false)
                }
            }
        }
    }

    override fun onHMILeftKnobClick() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMIRightKnobClick() {
        @Suppress("KotlinConstantConditions")
        if (isKnobRotated && isRightOptionSelected) {
            KnobNavigationUtils.knobForwardTrace = true
            if (recipeType == BundleKeys.PROBE_BASED){
                navigateAfterProbeRecipeIsSelected(recipeName, true)
            } else {
                navigateAfterRecipeIsSelected(recipeName, true)
            }
        } else if (isKnobRotated && !isRightOptionSelected) {
            manageRecipeInstructionFragmentPreference(recipeName)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            isKnobRotated = false
            isRightOptionSelected = false
            setLeftButtonViewVisibility(false)
            setRightButtonViewVisibility(false)
        }
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

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    /**
     * load data on recipe instruction fragment launch
     */
    @SuppressLint("ClickableViewAccessibility")
    fun loadData() {
        recipeName = arguments?.getString(BundleKeys.RECIPE_NAME) ?: ""
        recipeType = arguments?.getString(BundleKeys.RECIPE_TYPE) ?: ""
        recipeRecord = CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
            recipeName, CookingViewModelFactory.getInScopeViewModel().cavityName.value
        )
        var isMWORecipe = CookingViewModelFactory.getInScopeViewModel().isOfTypeMicrowaveOven
        var textInformation = AppConstants.TEXT_INFORMATION
        if (isMWORecipe){
            textInformation = AppConstants.TEXT_INFORMATION_MWO
        }
        val textToShow: Int = CookingAppUtils.getResIdFromResName(
            (NavigationUtils.getViewSafely(this) ?: requireView()).context,
            textInformation + recipeRecord.recipeName,
            AppConstants.RESOURCE_TYPE_STRING
        )
        if (textToShow != R.string.weMissedThat) {
            val imageResourceId = CookingAppUtils.getImageIdToShowOnPopup(
                recipeRecord.recipeName,
                (NavigationUtils.getViewSafely(this) ?: requireView()).context
            )
            //Header bar
            setHeaderBarRightIconViewVisibility(View.GONE)
            setHeaderBarTitleIconVisibility(View.INVISIBLE)
            getHeaderBarLeftIconView()?.setOnClickListener {
                onBackButtonClick()
            }
            getHeaderBarLeftIconConstraintLayout()?.setOnClickListener{
                onBackButtonClick()
            }
            setHeaderTitle(
                CookingAppUtils.getHeaderTitleAsRecipeName(
                    requireContext(),
                    recipeRecord.recipeName
                )
            )
            manageHeaderBarParametersBasedOnVariant()

            //BODY
            setInstructionText(textToShow)
            setLeftImageResource(imageResourceId)

            //Bottom view
            setLeftButtonVisibility(View.VISIBLE)
            setLeftConstraintVisibility(View.VISIBLE)
            if (recipeRecord.showInstruction) {
                setLeftButtonImage(R.drawable.ic_check_mark_un_checked)
            } else {
                setLeftButtonImage(R.drawable.ic_check_mark_checked)
            }
            getLeftButton()?.setOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    if(getLeftButton()?.getImageResource() == R.drawable.ic_check_mark_checked) R.raw.toggle_off else R.raw.toggle_on,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                manageRecipeInstructionFragmentPreference(recipeName)
            }
            getLeftConstraint()?.setOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    if(getLeftButton()?.getImageResource() == R.drawable.ic_check_mark_checked) R.raw.toggle_off else R.raw.toggle_on,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                manageRecipeInstructionFragmentPreference(recipeName)
            }
            getRightButton()?.setOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                if(recipeType == BundleKeys.PROBE_BASED){
                    navigateAfterProbeRecipeIsSelected(recipeName)
                } else {
                    navigateAfterRecipeIsSelected(recipeName)
                }
            }
            getRightConstraint()?.setOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                if(recipeType == BundleKeys.PROBE_BASED){
                    navigateAfterProbeRecipeIsSelected(recipeName)
                } else {
                    navigateAfterRecipeIsSelected(recipeName)
                }
            }
        }
    }

    /**
     * navigate on click of next button on screen
     */
    private fun navigateAfterRecipeIsSelected(recipeName: String, isKnobClick: Boolean = false) {
        NavigationUtils.navigateAfterRecipeSelection(
            this,
            CookingViewModelFactory.getInScopeViewModel(),
            recipeName,
            true,
            isKnobClick
        )
    }

    /**
     * navigate on click of next button on screen
     */
    private fun navigateAfterProbeRecipeIsSelected(recipeName: String, isKnobClick: Boolean = false) {
        NavigationUtils.navigateAfterProbeRecipeSelection(
            this,
            CookingViewModelFactory.getInScopeViewModel(),
            recipeName,
            true,
            isKnobClick
        )
    }

    /**
     * set user instruction to display or not based on user preference
     */
    private fun manageRecipeInstructionFragmentPreference(recipeName: String) {
        val cavityName: String =
            CookingViewModelFactory.getInScopeViewModel().cavityName.value.toString()
        val recipeRecord: RecipeRecord? =
            CookBookViewModel.getInstance()?.getDefaultRecipeRecordByNameAndCavity(
                recipeName, cavityName
            )
        if (recipeRecord?.showInstruction == true) {
            setLeftButtonImage(R.drawable.ic_check_mark_checked)
            CookBookViewModel.getInstance()?.setShowInstruction(
                recipeName,
                cavityName,
                false
            )
        } else {
            setLeftButtonImage(R.drawable.ic_check_mark_un_checked)
            CookBookViewModel.getInstance()?.setShowInstruction(
                recipeName,
                cavityName,
                true
            )
        }
    }

    open fun getHeaderBarLeftIconConstraintLayout(): ConstraintLayout? {
        return getHeaderBar()?.getBinding()?.leftIconWithTouchImprovement
    }
}