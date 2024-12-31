package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;


import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractTumblerViewHolder;
import com.whirlpool.cooking.databinding.FragmentTumblerBinding;
import com.whirlpool.cooking.widgets.header.HeaderBar;
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler;

public class TumblerViewHolderHelper extends AbstractTumblerViewHolder{

    private FragmentTumblerBinding fragmentTumblerBinding;

    // ================================================================================================================
    // -----------------------------------------  General Methods Definitions  ----------------------------------------
    // ================================================================================================================

    /**
     * Inflate the customized view
     *
     * @param inflater {@link LayoutInflater}
     * @param container {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     * @return {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentTumblerBinding = FragmentTumblerBinding.inflate(inflater);
        return fragmentTumblerBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentTumblerBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the binding class
     *
     * @return {@link FragmentTumblerBinding}
     */
    @Override
    public FragmentTumblerBinding getFragmentTumblerBinding() {
        return fragmentTumblerBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------  Common Views Definitions  ------------------------------------------
    // ================================================================================================================

    /**
     * Definition for the bottom right button
     *
     * @return {@link int}
     */
    @Override
    public Button providePrimaryButton() {
        return fragmentTumblerBinding.buttonCtaPrimaryActive;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the bottom left button
     *
     * @return {@link int}
     */
    @Override
    public Button provideGhostButton() {
        return fragmentTumblerBinding.buttonCtaGhostLightText;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the BaseTumbler widget
     *
     * @return {@link int}
     */
    @Override
    public BaseTumbler provideNumericTumbler() {
        return fragmentTumblerBinding.tumblerNumericBased;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the HeaderBar widget
     *
     * @return {@link HeaderBar}
     */
    @Override
    public HeaderBar provideTumblerHeader() {
        return fragmentTumblerBinding.widgetHeaderTopNavigation;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the header bar's right icon
     *
     * @return {@link int}
     */
    @Override
    public int provideTumblerHeaderRightIcon() {
        return R.drawable.icon_40px_keypad;
    }

    /**
     *
     * @return {@link int}
     */
    @Override
    public int provideLeftIcon() {
        return R.drawable.icon_40px_backarrow;
    }

    /**
     * Provides the interface to access DegreeTypeTextView
     *
     * @return {@link TextView}
     */
    public TextView provideDegreeTypeTextView() {
        return fragmentTumblerBinding.degreesType;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------  Modality Specific Views Definitions -------------------------------------
    /**
     * Provides the interface to access Next screen
     *
     * @return
     */
    @Override
    public int provideSummaryScreenAction() {
        return R.id.action_singleoventumbler_to_singleOvenCycleSummary;
    }

    /**
     * Provides the interface to access Next screen for delay
     *
     * @return
     */
    @Override
    public int provideDelaySetScreenAction() {
        return R.id.action_singleOvenDelayTimeTumbler_to_singleOvenCycleSummary;
    }

    /**
     * Provides the interface to access Next screen for delay
     *
     * @return
     */
    @Override
    public int provideDelaySetScreenActionMW() {
        return R.id.action_microwaveDelayTimeTumbler_to_mwomodessummary;
    }
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

}