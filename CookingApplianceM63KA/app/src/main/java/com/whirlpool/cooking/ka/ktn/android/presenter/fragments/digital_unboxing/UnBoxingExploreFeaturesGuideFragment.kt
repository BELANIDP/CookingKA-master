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
import core.jbase.abstractViewHolders.AbstractUnboxingExploreFeaturesGuideViewHolder
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.HMIExpansionUtils
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.viewHolderHelpers.ApplianceUnboxingExploreGuideViewProvider

/**
 * File        : android.presenter.fragments.digital_unboxing.UnboxingExploreFeaturesGuideFragment
 * Brief       : Appliance Explore Features guide Popup to inflate as Fragment
 * Author      : Nikki Gharde
 * Created On  : 04/Sep/2024
 * Details     : User can scroll to see the information of appliance features guide with serving tips
 */
class UnBoxingExploreFeaturesGuideFragment : SuperAbstractTimeoutEnableFragment(),
    HMIKnobInteractionListener {
    private var viewProvider: AbstractUnboxingExploreFeaturesGuideViewHolder? = null

    //Knob Implementation
    private var knobRotationCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewProvider = ApplianceUnboxingExploreGuideViewProvider(this)
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
            knobRotationCount = 2
            viewProvider?.providePrimaryButtonTextView()?.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.selector_textview_walnut, null
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        viewProvider?.onDestroyView()
    }

    override fun onHMILeftKnobClick() {
        if (viewProvider?.provideStepperView()?.currentStep == resources.getInteger(R.integer.integer_range_0) ||
            viewProvider?.provideStepperView()?.currentStep == resources.getInteger(R.integer.integer_range_3)) {
            viewProvider?.provideGhostButtonTextView()?.background = null
            viewProvider?.providePrimaryButtonTextView()?.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.selector_textview_walnut, null
                )
            }
            KnobNavigationUtils.knobForwardTrace = true
            viewProvider?.providePrimaryButtonTextView()?.callOnClick()
        } else if (viewProvider?.provideStepperView()?.currentStep == resources.getInteger(R.integer.integer_range_1) ||
            viewProvider?.provideStepperView()?.currentStep == resources.getInteger(R.integer.integer_range_2)) {
            KnobNavigationUtils.knobForwardTrace = true
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

    override fun onHMILongLeftKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobClick() {
        PopUpBuilderUtils.userLeftKnobWarningPopup(this)
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
            if (viewProvider?.provideStepperView()?.currentStep == resources.getInteger(R.integer.integer_range_0) ||
                viewProvider?.provideStepperView()?.currentStep == resources.getInteger(R.integer.integer_range_3)) {
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        viewProvider?.provideGhostButtonTextView()?.background = null
                        viewProvider?.providePrimaryButtonTextView()?.background = resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                    }
                }
            } else if (viewProvider?.provideStepperView()?.currentStep == resources.getInteger(R.integer.integer_range_1) ||
                viewProvider?.provideStepperView()?.currentStep == resources.getInteger(R.integer.integer_range_2)) {
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
        } else if (knobId == AppConstants.RIGHT_KNOB_ID) {
            PopUpBuilderUtils.userLeftKnobWarningPopup(this)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        knobRotationCount = 0
        viewProvider?.provideGhostButtonTextView()?.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
        viewProvider?.providePrimaryButtonTextView()?.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
    }
}