package android.presenter.fragments

import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.graphics.Typeface
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.OtaHomeViewholderBinding
import com.whirlpool.hmi.ota.utils.OTAStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.ota.viewmodel.OTAViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper.Logd
import core.utils.KnobDirection
import core.utils.NavigationUtils
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils

/**
 * File        : android.presenter.fragments.ota.OtaHomeViewHolder
 * Brief       : View Holder Class for OTA Home
 * Author      : Rajendra
 * Created On  : 15-July-2024
 * Details     : Provide View for Launching OTA screen, displaying target version, skip update and update now functionality
 */
class OtaCompleteNotificationView : Fragment(),HMIKnobInteractionListener, View.OnClickListener{
    private var otaViewModel: OTAViewModel? = null

    private var viewBinding: OtaHomeViewholderBinding? = null
    //Knob Implementation
    private var knobRotationCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = OtaHomeViewholderBinding.inflate(inflater, container, false)
        Logd(TAG, "Showing OTA Hxxxxxxxome View Holder")
        otaViewModel = OTAVMFactory.getOTAViewModel()
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.btnUpdateNow?.visibility = View.VISIBLE
        viewBinding?.btnUpdateNow?.text = resources.getString(R.string.text_button_finish)
        viewBinding?.btnUpdateNow?.setOnClickListener{onDismissClick()}
        onUpdateCompleted()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        managePreferencesCollectionHeaderBar()
        updateVersionInfo()
    }

//    private fun onDismissClick(){
//        NavigationUtils.navigateSafely(
//            this,
//            R.id.action_otaCompleteNotificationView_to_clockFragment,
//            null,
//            null
//        )
//    }
    private fun updateVersionInfo() {
            viewBinding?.softwareUpdateScheduled?.visibility = View.GONE
            viewBinding?.thisMayTakeMinutes?.visibility = View.GONE
    }

    private fun managePreferencesCollectionHeaderBar() {
        viewBinding?.titleBar?.apply {
            setLeftIconVisibility(true)
            setRightIconVisibility(true)
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setRightIcon(R.drawable.ic_close)
            getHeaderTitle()?.visibility = View.VISIBLE
            setCustomOnClickListener(object :
                HeaderBarWidgetInterface.CustomClickListenerInterface {
                override fun leftIconOnClick() {
                    onBackButtonClick()
                }
                override fun rightIconOnClick() {
                    onCloseButtonClick()
                }
            })
        }
    }

    private fun onDismissClick() {
        Logd(TAG,"OTA : Navigating to Clock Screen")
        NavigationUtils.navigateSafely(
            this,
            R.id.global_action_to_clockScreen,
            null,
            null
        )
    }

    override fun onDestroyView() {
        viewBinding = null
        super.onDestroyView()
    }

    private fun getSpannableStringBuilder(releaseInfoMap: HashMap<String, String>): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()
        val headingSizeSpan = AbsoluteSizeSpan(22)
        AbsoluteSizeSpan(18)
        val boldStyleSpan = StyleSpan(Typeface.BOLD)
        for (header in releaseInfoMap.keys) {
            val heading = SpannableString(header)
            heading.setSpan(headingSizeSpan, 0, header.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            heading.setSpan(boldStyleSpan, 0, heading.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val description = CookingAppUtils.extractSecondaryTextDescriptionFileContent(otaViewModel?.descriptionFileContent?.getValue())
            spannableStringBuilder.append(description)
        }
        return spannableStringBuilder
    }

    private fun onUpdateCompleted() {
        CookingAppUtils.setOTACompleteStatus(true)
        //  Remove notification: Software updated successfully
        NotificationManagerUtils.removeNotificationFromNotificationCenter(NotificationJsonKeys.NOTIFICATION_SW_UPDATED_SUCCESSFULLY)
        NotificationManagerUtils.removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_SW_UPDATED_SUCCESSFULLY)
        //  Remove notification: Software update available
        NotificationManagerUtils.removeNotificationFromNotificationCenter(NotificationJsonKeys.NOTIFICATION_SW_UPDATE_AVAILABLE)
        NotificationManagerUtils.removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_SW_UPDATE_AVAILABLE)
        viewBinding?.titleBar?.apply {
            setRightIconVisibility(false)
            setLeftIconVisibility(false)
            setTitleText("")
            getHeaderTitle()?.visibility = View.VISIBLE
            getHeaderTitle()?.text = provideResources().getString(R.string.text_header_software_update_successful)
        }
        viewBinding?.tvWhatIsNewTitle?.visibility = View.GONE
        viewBinding?.softwareUpdateScheduled?.visibility = View.GONE
        viewBinding?.thisMayTakeMinutes?.visibility = View.GONE
        viewBinding?.softwareUpdateCompleteVersionTitle?.visibility = View.VISIBLE
        viewBinding?.softwareUpdateCompleteVersionNumber?.apply {
            visibility = View.VISIBLE
            text = otaViewModel?.otaManager?.currentSystemVersion
        }

        viewBinding?.btnUpdateNow?.text = provideResources().getString(R.string.text_button_dismiss)
        val spannableStringBuilder = CookingAppUtils.extractSecondaryTextDescriptionFileContent(otaViewModel?.descriptionFileContent?.value)
        if (spannableStringBuilder.isNotEmpty()) {
            //Visible release info textview if release info text is available.
            viewBinding?.informationScrollablePrimaryTextView?.apply{
                visibility = View.VISIBLE
                text = provideResources().getString(R.string.text_sub_heading_new_feature)
            }
            viewBinding?.informationScrollableSecondaryTextView?.apply {
                visibility = View.VISIBLE
                movementMethod = ScrollingMovementMethod()
                text = spannableStringBuilder
            }
        } else {
            viewBinding?.informationScrollableSecondaryTextView?.visibility = View.GONE
            viewBinding?.informationScrollablePrimaryTextView?.apply {
                visibility = View.VISIBLE
                text = provideResources().getString(R.string.weMissedThat)
            }
        }
    }

    companion object {
        private val TAG: String = OtaCompleteNotificationView::class.java.simpleName
    }

    override fun onHMILeftKnobClick() {
        Logd("OTA", "OTA onHMILeftKnobClick")
        when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> {
                onDismissClick()
            }
        }
    }

    override fun onHMILongLeftKnobPress() {
        Logd("OTA", "OTA onHMILongLeftKnobPress")
    }

    override fun onHMIRightKnobClick() {
        Logd("OTA", "OTA onHMIRightKnobClick")
        viewBinding?.btnUpdateNow?.callOnClick()
        /*when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> {
                viewBinding?.btnUpdateNow?.callOnClick()
            }

            AppConstants.KNOB_COUNTER_TWO -> {
                viewBinding?.btnSkipUpdate?.callOnClick()
            }
        }*/
    }

    override fun onHMILongRightKnobPress() {
        Logd("OTA", "OTA onHMILongRightKnobPress")
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        Logd("OTA", "OTA onHMIRightKnobTickHoldEvent")
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        Logd("OTA", "OTA onKnobRotateEvent")
        Logd("Unboxing", "Unboxing onKnobRotateEvent")
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.MIN_KNOB_POSITION) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    viewBinding?.btnSkipUpdate?.background = null
                    viewBinding?.btnUpdateNow?.background =
                        resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                }
            }
        }
    }

    fun provideResources(): Resources {
        return viewBinding?.root?.resources as Resources
    }
    override fun onKnobSelectionTimeout(knobId: Int) {
        viewBinding?.btnUpdateNow?.background = null
        knobRotationCount = 0
    }

    private fun onBackButtonClick(){
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(
                    this
                ) ?: requireView()
            )
        )
    }

    private fun onCloseButtonClick(){
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(
                    this
                ) ?: requireView()
            )
        )
    }

    override fun onClick(v: View?) {
    }
}