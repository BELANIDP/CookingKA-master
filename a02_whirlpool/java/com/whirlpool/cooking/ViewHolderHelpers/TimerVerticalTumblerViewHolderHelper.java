package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractTimerVerticalTumblerViewHolder;
import com.whirlpool.cooking.databinding.FragmentTimerVerticalTumblerBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler;

public class TimerVerticalTumblerViewHolderHelper extends AbstractTimerVerticalTumblerViewHolder {

    private FragmentTimerVerticalTumblerBinding fragmentTimerVerticalTumblerBinding;

    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
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

        fragmentTimerVerticalTumblerBinding = FragmentTimerVerticalTumblerBinding.inflate(inflater);

        return fragmentTimerVerticalTumblerBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentTimerVerticalTumblerBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access the binding class
     *
     * @return {@link FragmentTimerVerticalTumblerBinding}
     */
    @Override
    public FragmentTimerVerticalTumblerBinding getFragmentTimerVerticalTumblerBinding() {
        return fragmentTimerVerticalTumblerBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------------  Common Views  ------------------------------------------------
    // ================================================================================================================

    /**
     * Provides the interface to access HeaderBar widget
     *
     * @return {@link HeaderSingleIconDoubleText}
     */
    @Override
    public HeaderSingleIconDoubleText provideTimerVerticalTumblerHeaderBar() {
        return fragmentTimerVerticalTumblerBinding.widgetHeaderTopmenu;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access HoursBaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    @Override
    public BaseTumbler provideTimerVerticalHoursTumbler() {
        return fragmentTimerVerticalTumblerBinding.tumblerNumericBasedHours;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access MinutesBaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    @Override
    public BaseTumbler provideTimerVerticalMinutesTumbler() {
        return fragmentTimerVerticalTumblerBinding.tumblerNumericBasedMins;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access MinutesBaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    @Override
    public BaseTumbler provideTimerVerticalSecondsTumbler() {
        return fragmentTimerVerticalTumblerBinding.tumblerNumericBasedSeconds;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access StartTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideTimerVerticalTumblerStartTextView() {
        return fragmentTimerVerticalTumblerBinding.kitchenTimerSet;
    }

    public TextView provideLeftUnitTextView() {
        return fragmentTimerVerticalTumblerBinding.hoursText;
    }

    public TextView provideMiddleUnitTextView() {
        return fragmentTimerVerticalTumblerBinding.minutesText;
    }

    public TextView provideRightUnitTextView() {
        return fragmentTimerVerticalTumblerBinding.secondsText;
    }

    public TextView provide5minsUnitTextView() {
        return fragmentTimerVerticalTumblerBinding.button5minsAddtime;
    }

    public TextView provide30secUnitTextView() {
        return fragmentTimerVerticalTumblerBinding.button30secsAddtime;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------

    /**
     * @return {@link int}
     */
    @Override
    public int provideLeftIcon() {
        return R.drawable.icon_40px_backarrow;
    }

    /**
     * @return {@link int}
     */
    @Override
    public int provideNavRightIcon() {
        return R.drawable.icon_40px_keypad;
    }
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/
}
