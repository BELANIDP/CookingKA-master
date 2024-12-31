package core.utils

import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.settings.SettingsViewModel
import core.utils.HMILogHelper.Logd
import core.utils.PopUpBuilderUtils.Companion.observeHmiKnobListener

object RemoteUiPopUpUtils {
    fun activateRemoteEnablePopUpBuilder(
        fragmentManager: FragmentManager,
        cookingViewModel: CookingViewModel,
        visibleFragment: Fragment?
    ) {
        val recipeName = cookingViewModel.recipeExecutionViewModel.recipeName.value
        val popUpDescription = visibleFragment?.getString(R.string.text_description_activate_remote_enable)?.let {
                String.format(
                    it,
                    recipeName)
            }
        val dialogPopupBuilder: ScrollDialogPopupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
            .setHeaderTitle(R.string.text_header_activate_remote_enable)
            .setDescriptionMessage(descriptionText = popUpDescription.toString())
            .setCancellableOutSideTouch(false)
            .setLeftButton(R.string.text_button_cancel) { cookingViewModel.recipeExecutionViewModel.cancel()
                Logd(
                    "CookingAppUtils recipe cancel",
                    "Canceling ${cookingViewModel.cavityName.value} in activateRemoteEnablePopUpBuilder"
                )
                HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CONNECT_TO_NETWORK)
                false
            }
            .setRightButton(R.string.text_button_continue) {
                SettingsViewModel.getSettingsViewModel().setRemoteStartEnable(true)
                if ( visibleFragment != null) {
                    activatedRemoteEnableConfirmationPopUpBuilder(
                        cookingViewModel,
                        visibleFragment
                    )
                }
                false
            }
            .build()

        dialogPopupBuilder.setTimeoutCallback(
            null, R.integer.session_short_timeout
        )
        //Knob Implementation
        var knobRotationCount = 0
        val hmiKnobListener = observeHmiKnobListener(
            onKnobRotateEvent = { knobId, knobDirection ->
                if (knobId == AppConstants.RIGHT_KNOB_ID) {
                    if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                    else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                    when(knobRotationCount){
                        AppConstants.KNOB_COUNTER_ONE -> {
                            dialogPopupBuilder.provideViewHolderHelper()?.rightTextButton?.background = null
                            dialogPopupBuilder.provideViewHolderHelper()?.leftTextButton?.background =
                                visibleFragment?.requireContext().let {
                                    ContextCompat.getDrawable(
                                        it!!, R.drawable.selector_textview_walnut_bottom
                                    )
                                }
                        }
                        AppConstants.KNOB_COUNTER_TWO -> {
                            dialogPopupBuilder.provideViewHolderHelper()?.leftTextButton?.background = null
                            dialogPopupBuilder.provideViewHolderHelper()?.rightTextButton?.background =
                                visibleFragment?.requireContext().let {
                                    ContextCompat.getDrawable(
                                        it!!, R.drawable.selector_textview_walnut_bottom
                                    )
                                }
                        }
                    }
                }
            }, onHMIRightKnobClick = {
                when(knobRotationCount){
                    AppConstants.KNOB_COUNTER_ONE -> {
                        dialogPopupBuilder.dismiss()
                    }
                    AppConstants.KNOB_COUNTER_TWO -> {
                        dialogPopupBuilder.dismiss()
                        SettingsViewModel.getSettingsViewModel().setRemoteStartEnable(true)
                        if ( visibleFragment != null) {
                            activatedRemoteEnableConfirmationPopUpBuilder(
                                cookingViewModel,
                                visibleFragment
                            )
                        }
                    }
                }
            }, onKnobSelectionTimeout = {

            }
        )

        dialogPopupBuilder.setOnDialogCreatedListener(object : ScrollDialogPopupBuilder.OnDialogCreatedListener {
            override fun onDialogCreated() {
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CONNECT_TO_NETWORK)
            }

            override fun onDialogDestroy() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
            }
        })
        dialogPopupBuilder.show(fragmentManager, "activateRemoteEnable")
    }

    private fun activatedRemoteEnableConfirmationPopUpBuilder(
        cookingViewModel: CookingViewModel?,
        visibleFragment: Fragment
    ) {
        val dialogPopupBuilder: ScrollDialogPopupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
            .setHeaderTitle(R.string.text_header_remote_enable_activated)
            .setDescriptionMessage(R.string.text_description_remote_enable_activated)
            .setCancellableOutSideTouch(false)
            .setRightButton(R.string.text_button_got_it) {
                CookingAppUtils.handleErrorAndStartCooking(
                    visibleFragment,
                    cookingViewModel!!,
                    CookingAppUtils.isSabbathMode(),
                    false
                )
                false
            }
            .build()
        dialogPopupBuilder.show(
            visibleFragment.parentFragmentManager,
            "activatedRemoteEnableConfirmation"
        )
        //Knob Implementation
        val hmiKnobListener = observeHmiKnobListener(
            onHMIRightKnobClick = {
                dialogPopupBuilder.onHMIRightKnobClick()
            }, onKnobSelectionTimeout = {}
        )
        dialogPopupBuilder.setOnDialogCreatedListener(object :
            ScrollDialogPopupBuilder.OnDialogCreatedListener {
            override fun onDialogCreated() {
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CONNECT_TO_NETWORK)
            }

            override fun onDialogDestroy() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CONNECT_TO_NETWORK)
            }
        })
    }
}
