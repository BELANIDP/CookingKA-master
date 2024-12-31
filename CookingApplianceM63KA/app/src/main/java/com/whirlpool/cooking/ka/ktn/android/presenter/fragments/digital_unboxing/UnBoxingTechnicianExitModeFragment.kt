/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.digital_unboxing

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentDemoModeLandingBinding
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.updatePopUpRightTextButtonBackground
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedPreferenceManager.setTechnicianTestDoneStatusIntoPreference
import core.utils.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * File        : android.presenter.fragments.digital_unboxing.UnboxingTechnicianExitModeFragment
 * Brief       : Technician Test exit mode and test product support
 * Author      : Rajendra
 * Created On  : 5.Sep.2024
 * Details     : Technician Test exit mode and test product support
 */
class UnBoxingTechnicianExitModeFragment : Fragment(), HMIKnobInteractionListener {
    private var viewBinding: FragmentDemoModeLandingBinding? = null
    private var technicianExitModePopup: ScrollDialogPopupBuilder? = null
    private val handler = Handler(Looper.getMainLooper())
    //Knob Implementation
    private var knobRotationCount = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentDemoModeLandingBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        setUpViewModels()
        observeKnobBackTrace()
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            knobRotationCount = 1
            viewBinding?.seeVideos?.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.selector_textview_walnut, null
                )
            }
        }
    }

    private fun observeKnobBackTrace() {
        SharedViewModel.getSharedViewModel(this.requireActivity()).isNavigatedFromKnobClick()
            .observe(viewLifecycleOwner) { value ->
                value?.let { navigated ->
                    if (navigated && KnobNavigationUtils.knobBackTrace) {
                        // Log the last action if available
                        HMILogHelper.Logd("last saved action: ${KnobNavigationUtils.lastTimeSelectedData()}")
                        KnobNavigationUtils.knobBackTrace = false
                        knobRotationCount = 1
                        viewBinding?.seeVideos?.background = resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                        KnobNavigationUtils.removeLastAction()
                    } else {
                        HMILogHelper.Logd("livedata observer: not navigated form Knob")
                    }
                }
            }
    }

    /**
     * Method to set the view models
     */
    private fun setUpViewModels() {
        viewBinding?.exitDemo?.visibility = View.GONE
        viewBinding?.exitDemo1?.visibility = View.GONE
        viewBinding?.seeVideos?.text = resources.getString(R.string.text_button_exitTestMode)
        viewBinding?.seeVideos?.setOnClickListener {
            showExitTechnicianModePopup()
        }
        viewBinding?.exploreProduct?.text = resources.getString(R.string.text_button_testProduct)
        viewBinding?.exploreProduct?.setOnClickListener {
            testProduct(it)
        }

    }

    private fun showExitTechnicianModePopup() {
        if (technicianExitModePopup == null) {
            HMILogHelper.Logd("showing technician Exit Mode Popup")
            technicianExitModePopup =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_ota_popup_fragment)
                    .setHeaderTitle(R.string.text_header_exit_technician_mode)
                    .setDescriptionMessage(R.string.text_description_exit_technician_mode)
                    .setLeftButton(R.string.text_button_cancel) {
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        true
                    }.setRightButton(R.string.text_button_proceed) {
                        technicianExitModePopup?.dismiss()
                        lifecycleScope.launch(Dispatchers.IO) {
                            setTechnicianTestDoneStatusIntoPreference(AppConstants.FALSE_CONSTANT)
                            withContext(Dispatchers.Main) {
                                AudioManagerUtils.playOneShotSound(
                                    ContextProvider.getContext(),
                                    R.raw.button_press,
                                    AudioManager.STREAM_SYSTEM,
                                    true,
                                    0,
                                    1
                                )
                                handler.postDelayed({
                                    HMILogHelper.Logd("Technician Exit Mode: performing Soft reboot Application")
                                    SettingsViewModel.getSettingsViewModel().restartApp()
                                }, resources.getInteger(R.integer.ms_300).toLong())
                            }
                        }
                        false
                    }.setTopMarginForTitleText(AppConstants.POPUP_DG_CD_TITLE_TOP_SMALL_MARGIN)
                    .setTopMarginForDescriptionText(AppConstants.POPUP_DG_TITLE_TOP_SMALL_MARGIN)
                    .setHeaderViewCenterIcon(AppConstants.HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL).build()
        }

        //Knob Interaction on popup
        var knobRotationCount = 0
        val hmiKnobListener =
            PopUpBuilderUtils.observeHmiKnobListener(onKnobRotateEvent = { knobId, knobDirection ->
                HMILogHelper.Logd("$TAG : onKnobRotateEvent: knobId:$knobId, knobDirection:$knobDirection")
                if (knobId == AppConstants.RIGHT_KNOB_ID || knobId == AppConstants.LEFT_KNOB_ID) {
                    if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                    else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                    HMILogHelper.Logd("$TAG : onKnobRotateEvent: knobRotationCount:$knobRotationCount")
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            technicianExitModePopup?.provideViewHolderHelper()?.rightTextButton?.background =
                                null
                            technicianExitModePopup?.provideViewHolderHelper()?.leftTextButton?.background =
                                ContextProvider.getContext().let {
                                    ContextCompat.getDrawable(
                                        it, R.drawable.selector_textview_walnut
                                    )
                                }
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            technicianExitModePopup?.provideViewHolderHelper()?.leftTextButton?.background =
                                null
                            technicianExitModePopup?.provideViewHolderHelper()?.rightTextButton?.background =
                                ContextProvider.getContext().let {
                                    ContextCompat.getDrawable(
                                        it, R.drawable.selector_textview_walnut
                                    )
                                }
                        }
                    }
                }
            }, onHMIRightKnobClick = {
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        KnobNavigationUtils.knobBackTrace = true
                        technicianExitModePopup?.onHMILeftKnobClick()
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        technicianExitModePopup?.onHMIRightKnobClick()
                    }
                }
            }, onHMILeftKnobClick = {
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        KnobNavigationUtils.knobBackTrace = true
                        technicianExitModePopup?.onHMILeftKnobClick()
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        technicianExitModePopup?.onHMIRightKnobClick()
                    }
                }
            }, onKnobSelectionTimeout = {
                //Do nothing
            })
        technicianExitModePopup?.setOnDialogCreatedListener(object :
            ScrollDialogPopupBuilder.OnDialogCreatedListener {
            override fun onDialogCreated() {
                if (KnobNavigationUtils.knobForwardTrace) {
                    KnobNavigationUtils.knobForwardTrace = false
                    knobRotationCount = AppConstants.KNOB_COUNTER_TWO
                    updatePopUpRightTextButtonBackground(
                        this@UnBoxingTechnicianExitModeFragment,
                        technicianExitModePopup,
                        R.drawable.selector_textview_walnut_bottom
                    )
                }
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
            }

            override fun onDialogDestroy() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                CookingAppUtils.setHmiKnobListenerAfterDismissDialog(this@UnBoxingTechnicianExitModeFragment)
                if (technicianExitModePopup != null) {
                    technicianExitModePopup?.dismiss()
                    technicianExitModePopup = null
                }
            }
        })
        technicianExitModePopup?.show(parentFragmentManager, "TECH_EXIT_POPUP")
    }

    private fun testProduct(view: View) {
        HMILogHelper.Logd(TAG, "OTA : Navigating to Clock Screen")
        CookingAppUtils.setCookFlowGraphBasedOnVariant(view)
        Navigation.findNavController(view).navigate(R.id.global_action_to_clockScreen)
    }

    override fun onStop() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        viewBinding = null
        super.onStop()
    }

    companion object {
        private var TAG: String = UnBoxingTechnicianExitModeFragment::class.java.simpleName
    }

    override fun onHMILeftKnobClick() {
        HMILogHelper.Logd(TAG, "onHMILeftKnobClick")
        KnobNavigationUtils.knobForwardTrace = true
        when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> {
                viewBinding?.seeVideos?.callOnClick()
            }

            AppConstants.KNOB_COUNTER_TWO -> {
                viewBinding?.exploreProduct?.callOnClick()
            }
        }
    }

    override fun onHMILongLeftKnobPress() {
        HMILogHelper.Logd(TAG, "onHMILongLeftKnobPress")
    }

    override fun onHMIRightKnobClick() {
        HMILogHelper.Logd(TAG, "onHMIRightKnobClick")
        KnobNavigationUtils.knobForwardTrace = true
        when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> {
                viewBinding?.seeVideos?.callOnClick()
            }

            AppConstants.KNOB_COUNTER_TWO -> {
                viewBinding?.exploreProduct?.callOnClick()
            }
        }
    }

    override fun onHMILongRightKnobPress() {
        HMILogHelper.Logd(TAG, "onHMILongRightKnobPress")
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        HMILogHelper.Logd(TAG, "onHMIRightKnobTickHoldEvent")
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID || knobId == AppConstants.LEFT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    viewBinding?.exploreProduct?.background = null
                    viewBinding?.seeVideos?.background = resources.let {
                        ResourcesCompat.getDrawable(
                            it, R.drawable.selector_textview_walnut, null
                        )
                    }
                }

                AppConstants.KNOB_COUNTER_TWO -> {
                    viewBinding?.seeVideos?.background = null
                    viewBinding?.exploreProduct?.background = resources.let {
                        ResourcesCompat.getDrawable(
                            it, R.drawable.selector_textview_walnut, null
                        )
                    }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        viewBinding?.exploreProduct?.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
        viewBinding?.seeVideos?.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
    }
}

