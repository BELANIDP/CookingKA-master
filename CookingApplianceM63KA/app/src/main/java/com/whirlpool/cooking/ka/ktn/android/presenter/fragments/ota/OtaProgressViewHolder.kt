package android.presenter.fragments.ota

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.LayoutOtaErrorPopupFragmentBinding
import com.whirlpool.cooking.ka.databinding.OtaProgressViewholderBinding
import com.whirlpool.hmi.ota.ui.viewproviders.AbstractOtaPopupViewProvider
import com.whirlpool.hmi.ota.ui.viewproviders.AbstractOtaProgressViewProvider
import com.whirlpool.hmi.ota.utils.OTAStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.progress.BaseSeekBar
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper.Logd
import core.utils.KnobDirection
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils.isUnboxing

/**
 * File        : android.presenter.fragments.ota.OtaProgressViewHolder
 * Brief       : View Holder Class to show the progress update
 * Author      : Rajendra
 * Created On  : 18-July-2024
 * Details     : View responsible to provide downloading, installing, rebooting view and strings
 */
class OtaProgressViewHolder : AbstractOtaProgressViewProvider() {
    private var viewBinding: OtaProgressViewholderBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = OtaProgressViewholderBinding.inflate(inflater, container, false)
        Logd(TAG, "Showing OTA Progress ViewHolder")
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        OTAVMFactory.getOTAViewModel().targetSystemVersion.value?.let {
            Logd(
                "OtaProgressViewHolder",
                it
            )
        }
        viewBinding?.titleBar?.setLeftIconVisibility(false)
        managePreferencesCollectionHeaderBar()
    }

    override fun provideErrorIconImageResource(): Int {
        return R.drawable.icon_alert
    }

    private fun managePreferencesCollectionHeaderBar() {
        viewBinding?.titleBar?.setLeftIconVisibility(false)
        viewBinding?.titleBar?.setRightIconVisibility(false)
        viewBinding?.titleBar?.setOvenCavityIconVisibility(false)
        viewBinding?.titleBar?.setInfoIconVisibility(false)
        viewBinding?.titleBar?.getHeaderTitle()?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        viewBinding = null
    }

    override fun provideResources(): Resources {
        return viewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): TextView? {
        return viewBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideSubTitleTextView(): TextView? {
        return null
    }

    override fun provideTitleText(): CharSequence? {
        //Do nothing
        return null
    }

    override fun provideSubTitleText(): CharSequence? {
        return null
    }

    override fun provideLeftNavigationView(): View? {
        return viewBinding?.titleBar?.getLeftImageView()
    }

    override fun provideDescriptionMainTextView(): TextView? {
        return viewBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideDescriptionSubTextView(): TextView? {
        return viewBinding?.detailText
    }

    override fun providePositiveButton(): NavigationButton? {
        return null
    }

    override fun provideNegativeButton(): NavigationButton? {
        return null
    }


    override fun provideProgressBar(): ProgressBar? {
        return viewBinding?.progressBarUpdate
    }

    override fun provideSeekProgressbar(): BaseSeekBar? {
        return null
    }

    override fun provideUpdateHourglassView(): View? {
        //This should be invisible by default
        return viewBinding?.ivHourGlass
    }

    override fun provideHourglassViewBackgroundResource(): Int {
        viewBinding?.titleBar?.visibility = View.GONE
        return R.drawable.icon_hour_glass
    }

    override fun provideUpdatingToVersionTextView(): TextView? {
        //This should be invisible by default
        return viewBinding?.tvOtaVersion
    }

    override fun provideErrorImageView(): ImageView? {
        return viewBinding?.imageViewError
    }

    override fun onUnrecoverableError() {
        val errorFatalDescription =
            provideResources().getString(R.string.text_description_error500)
        //Split the error code 500 String between main error and sub error
        val stringSplit: Array<String> =
            errorFatalDescription.split(AppConstants.NEXT_LINE.toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        if (stringSplit.size > 1) {
            Logd("Fatal Error Description " + stringSplit[0] + stringSplit[1])
            viewBinding?.textViewMainError?.visibility = View.VISIBLE
            viewBinding?.textViewMainError?.text = stringSplit[0]
            viewBinding?.textViewSubError?.visibility = View.VISIBLE
            viewBinding?.textViewSubError?.text = stringSplit[1]
        }
    }

    override fun provideErrorMainTextView(): TextView? {
        return viewBinding?.textViewMainError
    }

    override fun provideErrorSubTextView(): TextView? {
        return viewBinding?.textViewSubError
    }

    override fun provideDownloadingDescription(): CharSequence {
        return provideResources().getString(R.string.text_description_downloading_update)
    }

    override fun provideDownloadingTitle(): CharSequence {
        return provideResources().getString(R.string.text_header_downloading_update)
    }

    override fun provideInstallingTitle(): CharSequence {
        return provideResources().getString(R.string.text_header_reboot)
    }

    override fun provideInstallingDescription(): CharSequence {
        return provideResources().getString(R.string.text_description_reboot)
    }

    override fun provideRebootingTitle(): CharSequence {
        return provideResources().getString(R.string.text_header_reboot)
    }

    override fun provideRebootingDescription(): CharSequence {
        return provideResources().getString(R.string.text_description_reboot)
    }

    override fun provideUpdatingToVersionText(): CharSequence {
        return provideResources().getString(R.string.text_updating_to_version)
    }

    override fun provideUnrecoverableErrorTitleText(): CharSequence {
        return provideResources().getString(R.string.text_layout_pop_up_header_error500)
    }

    override fun handleOtaExitNavigationExternally(view: View?, otaStatus: OTAStatus?): Boolean {
        if (view != null) {
            if (isUnboxing){
                Navigation.findNavController(view).navigate(R.id.global_action_to_unboxingRegionalSettingsFragment)
            }else {
                setGraphAndNavigateToClock(view)
            }
        }
        return true
    }

    private fun setGraphAndNavigateToClock(view: View) {
        Logd(TAG,"OTA : Navigating to Clock Screen")
        CookingAppUtils.setCookFlowGraphBasedOnVariant(view)
        Navigation.findNavController(view).navigate(R.id.global_action_to_clockScreen)
    }

    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun onOtaStatusChange(status: OTAStatus?, version: CharSequence?) {
        super.onOtaStatusChange(status, version)
        if (status != OTAStatus.ERROR) {
            viewBinding?.textViewMainError?.visibility = View.GONE
            viewBinding?.textViewSubError?.visibility = View.GONE
            viewBinding?.imageViewError?.visibility = View.GONE
        }
    }

    override fun provideOtaErrorPopupView(): AbstractOtaPopupViewProvider {
        return object : AbstractOtaPopupViewProvider() {
            private var popupBinding: LayoutOtaErrorPopupFragmentBinding? = null
            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                popupBinding = LayoutOtaErrorPopupFragmentBinding.inflate(inflater, container, false)
                popupBinding?.textViewTitle?.visibility = View.VISIBLE
                return popupBinding?.root
            }

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
            }

            override fun onDestroyView() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                popupBinding = null
            }

            //Knob Interaction on popup
            var knobRotationCount = 0
            val hmiKnobListener = PopUpBuilderUtils.observeHmiKnobListener(
                onKnobRotateEvent = { knobId, knobDirection ->
                    Logd("OTA : onKnobRotateEvent: knobId:$knobId, knobDirection:$knobDirection")
                    if (knobId == AppConstants.LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                        Logd("OTA : onKnobRotateEvent: knobRotationCount:$knobRotationCount")
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                popupBinding?.textButtonRight?.background =
                                    null
                                popupBinding?.textButtonLeft?.background =
                                    ContextProvider.getContext().let {
                                        ContextCompat.getDrawable(
                                            it, R.drawable.selector_textview_walnut
                                        )
                                    }
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                popupBinding?.textButtonLeft?.background =
                                    null
                                popupBinding?.textButtonRight?.background =
                                    ContextProvider.getContext().let {
                                        ContextCompat.getDrawable(
                                            it, R.drawable.selector_textview_walnut
                                        )
                                    }
                            }
                        }
                    }
                },
                onHMILeftKnobClick = {
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            popupBinding?.textButtonLeft?.callOnClick()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            popupBinding?.textButtonRight?.callOnClick()
                        }
                    }
                },
                onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    popupBinding?.apply {
                        CookingAppUtils.setLeftAndRightButtonBackgroundNull(
                            this.textButtonRight,
                            this.textButtonLeft
                        )
                    }
                })

            override fun provideResources(): Resources {
                return popupBinding?.root?.resources as Resources
            }

            override fun providePrimaryButton(): NavigationButton? {
                return popupBinding?.textButtonRight
            }

            override fun provideSecondaryButton(): NavigationButton? {
                return popupBinding?.textButtonLeft
            }

            override fun provideTitleTextView(): TextView? {
                return popupBinding?.textViewTitle
            }

            override fun provideSubTitleTextView(): TextView? {
                return null
            }

            override fun provideDescriptionTextView(): TextView? {
                return popupBinding?.textViewDescription
            }

            override fun provideOtaFailedPopupPrimaryButtonText(): CharSequence {
                return provideResources().getString(R.string.text_button_retry)
            }

            override fun provideOtaFailedPopupSecondaryButtonText(): CharSequence {
                return this.provideResources().getString(R.string.text_button_dismiss)
            }

            override fun provideOtaFailedTitle(): CharSequence {
                return provideResources().getString(R.string.text_header_software_installation_failed)
            }

            override fun provideOtaFailedDescription(): CharSequence {
                return provideResources().getText(R.string.text_layout_pop_up_decision_an_error_was_occurred_during_the_download)
            }
        }
    }

    companion object {
        private val TAG: String = OtaProgressViewHolder::class.java.simpleName
    }
}