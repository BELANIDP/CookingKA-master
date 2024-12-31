package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractCycleSummaryViewHolder;
import com.whirlpool.cooking.databinding.FragmentCycleSummaryBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;

public class CycleSummaryViewHolderHelper extends AbstractCycleSummaryViewHolder {

    private FragmentCycleSummaryBinding fragmentCycleSummaryBinding;
    // ================================================================================================================
    // -----------------------------------------  General Methods Definitions  ----------------------------------------
    // ================================================================================================================


    /**
     * onCreateView provides the interface for the Fragment to be able to inflate the customized view.
     *
     * @param inflater           {@link LayoutInflater}
     * @param container          {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     * @return {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentCycleSummaryBinding = FragmentCycleSummaryBinding.inflate(inflater, container, false);
        return fragmentCycleSummaryBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentCycleSummaryBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access the binding class
     *
     * @return {@link FragmentCycleSummaryBinding}
     */
    @Override
    public FragmentCycleSummaryBinding getFragmentCycleSummaryBinding() {
        return fragmentCycleSummaryBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------  Common Views Definitions  ------------------------------------------
    // ================================================================================================================

    /**
     * Provides the interface to access HeaderBar widget
     *
     * @return {@link HeaderSingleIconDoubleText}
     */
    @Override
    public HeaderSingleIconDoubleText provideCycleSummaryHeaderBar() {
        return fragmentCycleSummaryBinding.widgetHeaderTopmenu;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookTemperatureHeaderTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummaryCookTemperatureHeaderTextView() {
        return fragmentCycleSummaryBinding.text1;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookPowerHeaderTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummaryCookPowerHeaderTextView() {
        return fragmentCycleSummaryBinding.text2;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookTimeTextHeaderView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummaryCookTimeHeaderTextView() {
        return fragmentCycleSummaryBinding.text3;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookTemperatureConstraintLayout
     *
     * @return {@link ConstraintLayout}
     */
    @Override
    public ConstraintLayout provideCycleSummaryCookTemperatureConstraintLayout() {
        return fragmentCycleSummaryBinding.parameters1;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access SelectedCookTemperatureTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummarySelectedCookTemperatureTextView() {
        return fragmentCycleSummaryBinding.parameters1Text;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookPowerLevelConstraintLayout
     *
     * @return {@link ConstraintLayout}
     */
    @Override
    public ConstraintLayout provideCycleSummaryCookPowerLevelConstraintLayout() {
        return fragmentCycleSummaryBinding.parameters2;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access SelectedCookPowerLevelTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummarySelectedCookPowerLevelTextView() {
        return fragmentCycleSummaryBinding.parameters2Text;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookTimeConstraintLayout
     *
     * @return {@link ConstraintLayout}
     */
    @Override
    public ConstraintLayout provideCycleSummaryCookTimeConstraintLayout() {
        return fragmentCycleSummaryBinding.parametersAdd3;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access SelectedCookTimeTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummarySelectedCookTimeTextView() {
        return fragmentCycleSummaryBinding.parametersAdd3AddTime;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access DelayTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummaryDelayTextView() {
        return fragmentCycleSummaryBinding.buttonCtaGhostLightText;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access LeftIcon
     *
     * @return {@link int}
     */
    @Override
    public int provideSummaryLeftIcon() {
        return R.drawable.icon_40px_backarrow;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access LeftIcon
     *
     * @return {@link int}
     */
    @Override
    public int provideSummaryRightIcon() {
        return R.drawable.icon_40px_info;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------  Modality Specific Views Definitions -------------------------------------
    public int provideCycleSummaryDoorOpenClosePopup() { return R.layout.fragment_popup_instruction; }
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    @Override
    public SwitchMaterial provideCycleSummaryToggle() {
        return fragmentCycleSummaryBinding.toggle;
    }

    /**
     * Provides the interface to access CookTimeConstraintLayout
     *
     * @return {@link ConstraintLayout}
     */
    @Override
    public ConstraintLayout provideCycleSummaryTextToggleOption() {
        return fragmentCycleSummaryBinding.group1387;
    }

    @Override
    public FrameLayout provideCycleSummaryTemperatureFrame() {
        return fragmentCycleSummaryBinding.component16;
    }
}
