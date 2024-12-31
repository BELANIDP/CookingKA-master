package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractTimeVerticalTumblerViewHolder;
import com.whirlpool.cooking.databinding.FragmentTimeVerticalTumblerBinding;
import com.whirlpool.cooking.widgets.header.HeaderBar;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler;

public class TimeVerticalTumblerViewHolderHelper extends AbstractTimeVerticalTumblerViewHolder {

    private FragmentTimeVerticalTumblerBinding fragmentTimeVerticalTumblerBinding;

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

        fragmentTimeVerticalTumblerBinding = fragmentTimeVerticalTumblerBinding.inflate(inflater);

        return fragmentTimeVerticalTumblerBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentTimeVerticalTumblerBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access the binding class
     *
     * @return {@link FragmentTimeVerticalTumblerBinding}
     */
    @Override
    public FragmentTimeVerticalTumblerBinding getFragmentTimeVerticalTumblerBinding() {
        return fragmentTimeVerticalTumblerBinding;
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
    public HeaderSingleIconDoubleText provideTimeVerticalTumblerHeaderBar() {
        return fragmentTimeVerticalTumblerBinding.widgetHeaderTime;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access HoursBaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    @Override
    public BaseTumbler provideTimeVerticalHoursTumbler() {
        return fragmentTimeVerticalTumblerBinding.widgetTumblerHour;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access MinutesBaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    @Override
    public BaseTumbler provideTimeVerticalMinutesTumbler() {
        return fragmentTimeVerticalTumblerBinding.widgetTumblerMinute;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access AmPmBaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    @Override
    public BaseTumbler provideTimeVerticalAmPmTumbler() {
        return fragmentTimeVerticalTumblerBinding.widgetTumblerAmpm;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access StartTextView
     *
     * @return {@link TextView}
     */

    public TextView provideTimerVerticalTumblerStartTextView() {
        return null;
    }

    public TextView provideLeftUnitTextView() {
        return fragmentTimeVerticalTumblerBinding.h;
    }

    public TextView provideMiddleUnitTextView() {
        return fragmentTimeVerticalTumblerBinding.h1;
    }

    public TextView provide5minsUnitTextView() {
        return fragmentTimeVerticalTumblerBinding.buttonSecondary5min;
    }

    public TextView provideSetUnitTextView() {
        return fragmentTimeVerticalTumblerBinding.button;
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
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/
}
