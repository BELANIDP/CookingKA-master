package android.presenter.fragments.settings

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DiagnosticsResetConfirmationBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.diagnostics.services.cooking.CookingServiceViewModel
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.audio.WHRAudioManager
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.LogHelper
import com.whirlpool.hmi.utils.TreeNode
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.setFactoryRestoreStarted
import core.utils.CookingAppUtils.Companion.updatePopUpRightTextButtonBackground
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils
import core.utils.SharedPreferenceManager.resetViewSharedPreferenceData


/**
 * File       : com.whirlpool.cooking.diagnostic.SettingsResetRestoreConfirmationFragment
 * Brief      : SettingsResetRestoreConfirmationFragment instance for Factory Reset Confirmation Screen
 * Author     : Rajendra
 * Created On : 10-OCTOBER-2024
 * Details    : Factor restore/ Reset Confirmation Screen used for Factory Reset Confirmation
 */
class SettingsResetRestoreConfirmationFragment : SuperAbstractTimeoutEnableFragment(),
    HMIKnobInteractionListener,View.OnClickListener {
    private var restoreConfirmationViewBinding: DiagnosticsResetConfirmationBinding? = null
    private var isFactoryRestore: Boolean = false
    private var isResetInstructionDone: Boolean = false
    private var rebootTimer: CountDownTimer? = null
    private var restoreResetConfirmationPopup: ScrollDialogPopupBuilder? = null
    private var primaryRecipesToReset: ArrayList<String>? = null
    private var secondaryRecipesToReset: ArrayList<String>? = null
    private val productVariantEnum: CookingViewModelFactory.ProductVariantEnum =
            CookingViewModelFactory.getProductVariantEnum()

    //Knob Implementation
    private var knobRotationCount = 0

    companion object {
        private var TAG: String = SettingsResetRestoreConfirmationFragment::class.java.simpleName
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        restoreConfirmationViewBinding =
                DiagnosticsResetConfirmationBinding.inflate(inflater, container, false)
        return restoreConfirmationViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        isFactoryRestore =
                arguments != null && arguments?.getBoolean(BundleKeys.BUNDLE_RESTORE_FACTORY) == true

        initView()
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            knobRotationCount = 1
            restoreConfirmationViewBinding?.apply {
                primaryResetButton.background = resources.let {
                    ResourcesCompat.getDrawable(it, R.drawable.text_view_ripple_effect, null)
                }
                selectionLineSecondaryButton.visibility = View.VISIBLE
            }
        }
        if(arguments != null &&
            AppConstants.SETTINGS_DEMO_CODE == arguments?.getString(BundleKeys.BUNDLE_NAVIGATED_FROM)){
            startFactoryRestoreAndResetInfo()
        }
    }

    private fun initView() {
        if (isFactoryRestore) {
            HMILogHelper.Logd(TAG, "showing restore factory view")
            updateTitleAndDescriptionTextFactoryRestore()
        } else {
            primaryRecipesToReset = ArrayList()
            secondaryRecipesToReset = ArrayList()
            getRecipesToResetShowInstruction()
            HMILogHelper.Logd(TAG, "showing reset learn more view")
            updateTitleAndDescriptionResetLearnMore()
        }
        restoreConfirmationViewBinding?.apply {
            primaryResetButton.apply {
                setOnClickListener(this@SettingsResetRestoreConfirmationFragment) // Replace with actual activity/fragment context
                setText(R.string.text_button_cancel)
            }
            secondaryResetButton.apply {
                setOnClickListener(this@SettingsResetRestoreConfirmationFragment)
                setText(R.string.text_button_proceed)
            }
            icon112pxAlert.setImageResource(R.drawable.factory_reoobt_alert)
        }
    }


    private fun showConfirmationPopup() {
        if (restoreResetConfirmationPopup == null) {
            HMILogHelper.Logd("showing tools restore/reset confirmation popup")
            restoreResetConfirmationPopup = providePopupHeadLineString().let {
                providePopupBodyString().let { it1 ->
                    ScrollDialogPopupBuilder.Builder(R.layout.layout_restore_factory_popup_fragment)
                            .setHeaderTitle(R.string.text_dynamic_popup_content, it)
                            .setDescriptionMessage(it1).setRightButton(R.string.text_button_proceed) {
                            AudioManagerUtils.playOneShotSound(
                                ContextProvider.getContext(),
                                R.raw.start_press,
                                AudioManager.STREAM_SYSTEM,
                                true,
                                0,
                                1
                            )
                            if (isFactoryRestore &&
                                SettingsViewModel.DemoMode.DEMO_MODE_ENABLED == SettingsViewModel.getSettingsViewModel().demoMode.value
                            ) {
                                val bundle = Bundle()
                                bundle.apply {
                                    putString(
                                        BundleKeys.BUNDLE_NAVIGATED_FROM,
                                        AppConstants.SETTINGS_RESTORE_FACTORY
                                    )
                                    putBoolean(BundleKeys.BUNDLE_RESTORE_FACTORY, isFactoryRestore)
                                }
                                navigateSafely(
                                    this,
                                    R.id.action_settingsResetRestoreConfirmationFragment_to_demoModeCodeFragment,
                                    bundle,
                                    null
                                )
                            } else {
                                startFactoryRestoreAndResetInfo()
                            }
                            true
                        }.setLeftButton(R.string.text_button_cancel) {
                            AudioManagerUtils.playOneShotSound(
                                ContextProvider.getContext(),
                                R.raw.button_press,
                                AudioManager.STREAM_SYSTEM,
                                true,
                                0,
                                1
                            )
                            NavigationViewModel.popBackStack(
                                Navigation.findNavController(
                                    NavigationUtils.getViewSafely(this) ?: requireView()
                                )
                            )
                            true
                        }.setTopMarginForTitleText(if(isFactoryRestore) AppConstants.RESTORE_POPUP_VERTICAL_BOTTOM_SMALL_MARGIN else AppConstants.RESTORE_POPUP_VERTICAL_BOTTOM_HIGH_MARGIN)
                        .setHeaderViewCenterIcon(AppConstants.HEADER_RESTORE_LAYOUT_VIEW, false)
                        .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                            .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL).build()
                }
            }

            //Knob Interaction on popup
            var knobRotationCount = 0
            val hmiKnobListener =
                    PopUpBuilderUtils.observeHmiKnobListener(onKnobRotateEvent = { knobId, knobDirection ->
                        HMILogHelper.Logd("$TAG : onKnobRotateEvent: knobId:$knobId, knobDirection:$knobDirection")
                        if (knobId == AppConstants.LEFT_KNOB_ID) {
                            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                            HMILogHelper.Logd("$TAG : onKnobRotateEvent: knobRotationCount:$knobRotationCount")
                            when (knobRotationCount) {
                                AppConstants.KNOB_COUNTER_ONE -> {
                                    restoreResetConfirmationPopup?.provideViewHolderHelper()?.apply {
                                        rightTextButton?.background = null
                                        leftTextButton?.background = ContextProvider.getContext().let {
                                            ContextCompat.getDrawable(it, R.drawable.selector_textview_walnut_bottom)
                                        }
                                    }
                                }

                                AppConstants.KNOB_COUNTER_TWO -> {
                                    restoreResetConfirmationPopup?.provideViewHolderHelper()?.leftTextButton?.background =
                                            null
                                    restoreResetConfirmationPopup?.provideViewHolderHelper()?.rightTextButton?.background =
                                            ContextProvider.getContext().let {
                                                ContextCompat.getDrawable(
                                                        it, R.drawable.selector_textview_walnut_bottom
                                                )
                                            }
                                }
                            }
                        }
                    }, onHMIRightKnobClick = {
                        //Do nothing
                    }, onHMILeftKnobClick = {
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                restoreResetConfirmationPopup?.onHMILeftKnobClick()
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                KnobNavigationUtils.knobForwardTrace = true
                                restoreResetConfirmationPopup?.onHMIRightKnobClick()
                            }
                        }
                    }, onKnobSelectionTimeout = {
                        knobRotationCount = 0
                        restoreResetConfirmationPopup?.provideViewHolderHelper()?.apply {
                            leftTextButton?.background = null
                            rightTextButton?.background = null
                        }
                    })
            restoreResetConfirmationPopup?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    if (KnobNavigationUtils.knobForwardTrace) {
                        KnobNavigationUtils.knobForwardTrace = false
                        knobRotationCount = AppConstants.KNOB_COUNTER_TWO
                        updatePopUpRightTextButtonBackground(
                            this@SettingsResetRestoreConfirmationFragment,
                            restoreResetConfirmationPopup,
                            R.drawable.selector_textview_walnut_bottom
                        )
                    }
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(this@SettingsResetRestoreConfirmationFragment)
                    restoreResetConfirmationPopup?.let {
                        it.dismiss()
                        restoreResetConfirmationPopup = null
                    }
                }
            })
            restoreResetConfirmationPopup?.show(parentFragmentManager, "CONFIRMATION_POPUP")
        }
    }

    private fun startFactoryRestoreAndResetInfo() {
        if (isFactoryRestore) {
            //Disable the HMI key during the factory restore
            setFactoryRestoreStarted(true)
            startRebootTimer()
            restoreConfirmationViewBinding?.apply {
                doNotUnplugAppliance.apply {
                    setText(R.string.text_do_not_unplug_appliance_description)
                    visibility = View.VISIBLE
                }
                iconHourGlassTimer.apply {
                    setImageResource(R.drawable.hour_glass_timer)
                    visibility = View.VISIBLE
                }
                layout.visibility = View.GONE
            }
        } else {
            isResetInstructionDone = true

            restoreConfirmationViewBinding?.apply {
                icon120pxCheckmark.apply {
                    setImageResource(R.drawable.icon_120px_success)
                    visibility = View.VISIBLE
                }
                icon112pxAlert.visibility = View.GONE
                headerTextTitle.text = resources.getString(R.string.settings_reset_learn_more)
                descriptionText.text = resources.getString(R.string.your_cooking_info_was_restored_successfully)
                primaryResetButton.setText(R.string.text_button_exit)
                secondaryResetButton.setText(R.string.text_button_return_to_preferences)
            }
            if ((primaryRecipesToReset != null && primaryRecipesToReset?.size != 0)
                    || (secondaryRecipesToReset != null && secondaryRecipesToReset?.size != 0)
            ) {
                HMILogHelper.Logd(TAG, "Performing instruction reset for recipe")
                resetShowInstruction()
            } else {
                HMILogHelper.Logd(TAG, "No recipe to reset instruction ")
            }
        }
    }

    private fun startRebootTimer() {
        rebootTimer = object : CountDownTimer(
                AppConstants.SOFT_REBBOT_ANIMATION_DELAY, AppConstants.SOFT_REBBOT_ANIMATION_INTERVAL
        ) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                rebootTimer?.cancel()
                rebootTimer = null
                resetViewSharedPreferenceData()
                performFactoryRestoreAndSoftReboot()
            }
        }.start()
        WHRAudioManager.getInstance().playAudio(context, R.raw.power_off)
    }

    fun performFactoryRestoreAndSoftReboot() {
        if (isFactoryRestore) {
            CookingServiceViewModel.getCookingServiceViewModel()?.let {
                // Use the viewModel instance
                HMILogHelper.Logd("Factory restore: Performing factory restore")
                SettingsViewModel.getSettingsViewModel().factoryReset()
            }
        } else {
            HMILogHelper.Logd("Restore factory: wrong selection")
        }
    }

    private fun providePopupHeadLineString(): String {
        return if (isFactoryRestore) {
            resources.getString(R.string.text_header_restore_factory_defaults)
        } else {
            resources.getString(R.string.settings_reset_learn_more)
        }
    }

    private fun providePopupBodyString(): String {
        return if (isFactoryRestore) {
            resources.getString(R.string.text_layout_pop_up_decision_restore_factory_defaults)
        } else {
            resources.getString(R.string.text_layout_pop_up_decision_reset_learn_more)
        }
    }

    private fun resetRecipeArray() {
        if (primaryRecipesToReset != null) {
            primaryRecipesToReset?.clear()
            primaryRecipesToReset = null
        }
        if (secondaryRecipesToReset != null) {
            secondaryRecipesToReset?.clear()
            secondaryRecipesToReset = null
        }
    }


    private fun updateTitleAndDescriptionTextFactoryRestore() {
        restoreConfirmationViewBinding?.headerTextTitle?.text =
                resources.getString(R.string.restore_fatory_defaults)
        restoreConfirmationViewBinding?.descriptionText?.text =
                resources.getString(R.string.text_restore_factory_default_info)
    }

    private fun updateTitleAndDescriptionResetLearnMore() {
        restoreConfirmationViewBinding?.headerTextTitle?.text =
                resources.getString(R.string.settings_reset_learn_more)
        restoreConfirmationViewBinding?.descriptionText?.text =
                resources.getString(R.string.text_reset_learn_more_info)
    }

    //Build a list of the recipes which need to be reset
    private fun getRecipesToResetShowInstruction() {
        // get the root for primary manual node
        val rootPrimaryNode = CookBookViewModel.getInstance()
                .getManualRecipesPresentationTreeFor(CookingViewModelFactory.getPrimaryCavityViewModel().cavityName.value)

        when (productVariantEnum) {
            CookingViewModelFactory.ProductVariantEnum.COMBO, CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                // get the root secondary manual node
                val rootSecondaryNode = CookBookViewModel.getInstance()
                        .getManualRecipesPresentationTreeFor(CookingViewModelFactory.getSecondaryCavityViewModel().cavityName.value)
                //Use a recursive search to build a list of recipes to reset for primary cavity
                searchTreeForShowInstruction(rootPrimaryNode, true)
                //Use a recursive search to build a list of recipes to reset for secondary cavity
                searchTreeForShowInstruction(rootSecondaryNode, false)
            }

            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN, CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                //Use a recursive search to build a list of recipes to reset for primary cavity
                searchTreeForShowInstruction(rootPrimaryNode, true)
            }

            else -> {
                //Do nothing
            }
        }
        //If you need to display the number of recipes you can use the size of the list
        HMILogHelper.Logd("Number of recipes to reset for primary cavity: " + primaryRecipesToReset?.size)
        HMILogHelper.Logd("Number of recipes to reset for secondary cavity: " + secondaryRecipesToReset?.size)
    }

    //Recursive search finds all recipe leafs in manual tree
    private fun searchTreeForShowInstruction(
            startNode: TreeNode<String?>, isPrimaryCavity: Boolean
    ) {
        // loop through all nodes to get all children
        for (child in startNode.children) {
            //if child has children then check those
            if (child.children.isNotEmpty()) {
                searchTreeForShowInstruction(child, isPrimaryCavity)
            } else {
                if (isPrimaryCavity) {
                    //If we have reached the bottom of the tree then check the value of showInstruction
                    val record =
                            CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
                                    child.data,
                                    CookingViewModelFactory.getPrimaryCavityViewModel().cavityName.value
                            )

                    //if show instruction is false then add to recipe list
                    if (!record.showInstruction) {
                        //Add recipe name to list of recipes to reset
                        primaryRecipesToReset?.add(child.data)
                    }
                } else {
                    //If we have reached the bottom of the tree then check the value of showInstruction
                    val record =
                            CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
                                    child.data,
                                    CookingViewModelFactory.getSecondaryCavityViewModel().cavityName.value
                            )

                    //if show instruction is false then add to recipe list
                    if (!record.showInstruction) {
                        //Add recipe name to list of recipes to reset
                        secondaryRecipesToReset?.add(child.data)
                    }
                }
            }
        }
    }

    //If user confirms they want to reset the showInstruction
    private fun resetShowInstruction() {
        when (productVariantEnum) {
            CookingViewModelFactory.ProductVariantEnum.COMBO, CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                resetPrimaryCavityInstruction()
                resetSecondaryCavityInstruction()
            }

            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN, CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                resetPrimaryCavityInstruction()
            }

            else -> {
                //Do nothing
            }
        }
    }

    private fun resetPrimaryCavityInstruction() {
        for (recipe in primaryRecipesToReset!!) {
            CookBookViewModel.getInstance().setShowInstruction(
                    recipe, CookingViewModelFactory.getPrimaryCavityViewModel().cavityName.value, true
            )
        }
    }

    private fun resetSecondaryCavityInstruction() {
        for (recipe in secondaryRecipesToReset!!) {
            CookBookViewModel.getInstance().setShowInstruction(
                    recipe, CookingViewModelFactory.getSecondaryCavityViewModel().cavityName.value, true
            )
        }
    }

    override fun onClick(view: View) {
        if (isResetInstructionDone) {
            if (view.id == R.id.secondary_reset_button) {
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    navigateSafely(
                            it, R.id.global_action_to_preferences, null, null
                    )
                }
            } else if (view.id == R.id.primary_reset_button) {
                HMILogHelper.Logd(TAG, "Reset info : Navigating to Clock Screen")
                CookingAppUtils.setCookFlowGraphBasedOnVariant(view)
                Navigation.findNavController(view).navigate(R.id.global_action_to_clockScreen)
            }
        } else {
            if (view.id == R.id.secondary_reset_button) {
                try {
                    showConfirmationPopup()
                } catch (exp: Exception) {
                    HMILogHelper.Logd("Restore settings: onClick$exp")
                }
            } else if (view.id == R.id.primary_reset_button) {
                NavigationViewModel.popBackStack(restoreConfirmationViewBinding?.root?.let {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    Navigation.findNavController(
                            it
                    )
                })
            }
        }
    }

    override fun onHMILeftKnobClick() {
        HMILogHelper.Logd(TAG, "onHMILeftKnobClick")
        when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> {
                KnobNavigationUtils.knobForwardTrace = true
                restoreConfirmationViewBinding?.secondaryResetButton?.callOnClick()
            }

            AppConstants.KNOB_COUNTER_TWO -> {
                KnobNavigationUtils.knobBackTrace = true
                restoreConfirmationViewBinding?.primaryResetButton?.callOnClick()
            }
        }
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobClick() {
        //Do nothing
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    restoreConfirmationViewBinding?.apply {
                        selectionLinePrimaryButton.visibility = View.GONE
                        selectionLineSecondaryButton.visibility = View.VISIBLE
                    }
                }

                AppConstants.KNOB_COUNTER_TWO -> {
                    restoreConfirmationViewBinding?.apply {
                        selectionLineSecondaryButton.visibility = View.GONE
                        selectionLinePrimaryButton.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        restoreConfirmationViewBinding?.selectionLinePrimaryButton?.visibility = View.GONE
        restoreConfirmationViewBinding?.selectionLineSecondaryButton?.visibility = View.GONE
        knobRotationCount = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resetRecipeArray()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        restoreConfirmationViewBinding = null
    }
}