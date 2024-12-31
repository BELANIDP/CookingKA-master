/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.digital_unboxing

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.whirlpool.cooking.ka.R
import core.jbase.abstractViewHolders.AbstractUnboxingApplianceFeaturesGuideViewHolder
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils.Companion.handleTextScrollOnKnobRotateEvent
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.SettingsManagerUtils
import core.viewHolderHelpers.UnboxingApplianceFeaturesGuideViewProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * File        : android.presenter.fragments.digital_unboxing.UnboxingApplianceFeaturesGuideFragment
 * Brief       : Appliance Features guide Popup to inflate as Fragment
 * Author      : Nikki Gharde
 * Created On  : 04/Sep/2024
 * Details     : User can scroll to see the information of appliance features guide with serving tips
 */
class UnBoxingApplianceFeaturesGuideFragment : SuperAbstractTimeoutEnableFragment(),
    HMIKnobInteractionListener {
    private var viewProvider: AbstractUnboxingApplianceFeaturesGuideViewHolder? = null

    //Knob Implementation
    private var knobRotationCount = 0
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewProvider = UnboxingApplianceFeaturesGuideViewProvider(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return viewProvider?.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTimeoutApplicable(!SettingsManagerUtils.isUnboxing)
        setMeatProbeApplicable(!SettingsManagerUtils.isUnboxing)
        viewProvider?.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        viewProvider?.onDestroyView()
    }

    override fun onHMILeftKnobClick() {
        HMILogHelper.Logd("Unboxing", "onHMILeftKnobClick")
        onHmiRightOrLeftKnobClick()
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobClick() {
        HMILogHelper.Logd("Unboxing", "onHMIRightKnobClick")
        onHmiRightOrLeftKnobClick()
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
        if (knobId == AppConstants.LEFT_KNOB_ID || knobId == AppConstants.RIGHT_KNOB_ID) {
            if (viewProvider?.isInstructionWidgetSelected == true) {
                viewProvider?.selectedRotator = 0
                // Use the custom coroutine scope to launch the coroutine
                coroutineScope.launch {
                    val scrollView = viewProvider?.provideDescriptionTextScrollView()
                    val scrollViewContent = viewProvider?.provideFeaturesGuideDescriptionTextView()
                    val lineHeight =
                        scrollViewContent?.measuredHeight?.let { it / scrollViewContent.lineHeight }
                            ?: 0
                    if (scrollView != null && scrollViewContent != null) {
                        handleTextScrollOnKnobRotateEvent(scrollView, lineHeight, knobDirection)
                    }
                }
            } else {
                if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        viewProvider?.providePrimaryButtonTextView()?.background = null
                        viewProvider?.provideGhostButtonTextView()?.background = resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        viewProvider?.provideGhostButtonTextView()?.background = null
                        viewProvider?.providePrimaryButtonTextView()?.background = resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        viewProvider?.resetToUnBoxingApplianceFeaturesDefaultSelection()
    }

    private fun onHmiRightOrLeftKnobClick() {
        HMILogHelper.Logd("Knob", "onHMILeftKnobClick() called  : ${viewProvider?.selectedRotator}")
        when (viewProvider?.selectedRotator) {
            0 -> {
                viewProvider?.isInstructionWidgetSelected = false
                viewProvider?.selectedRotator = 1
                knobRotationCount = 1
                viewProvider?.providePrimaryButtonTextView()?.background = null
                viewProvider?.provideGhostButtonTextView()?.background = resources.let {
                    ResourcesCompat.getDrawable(
                        it, R.drawable.selector_textview_walnut, null
                    )
                }
            }

            1 -> {
                KnobNavigationUtils.knobForwardTrace = true
                viewProvider?.resetToUnBoxingApplianceFeaturesDefaultSelection()
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        viewProvider?.provideGhostButtonTextView()?.callOnClick()
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        viewProvider?.providePrimaryButtonTextView()?.callOnClick()
                    }
                }
            }
        }
    }
}