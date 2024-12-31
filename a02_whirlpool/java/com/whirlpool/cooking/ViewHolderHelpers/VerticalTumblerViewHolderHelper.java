package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractVerticalTumblerViewHolder;
import com.whirlpool.cooking.databinding.FragmentVerticalTumblerBinding;
import com.whirlpool.cooking.widgets.header.HeaderBar;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler;

public class VerticalTumblerViewHolderHelper extends AbstractVerticalTumblerViewHolder {

    private FragmentVerticalTumblerBinding fragmentVerticalTumblerBinding;

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

        fragmentVerticalTumblerBinding = FragmentVerticalTumblerBinding.inflate(inflater);

        return fragmentVerticalTumblerBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentVerticalTumblerBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access the binding class
     *
     * @return {@link FragmentVerticalTumblerBinding}
     */
    @Override
    public FragmentVerticalTumblerBinding getFragmentVerticalTumblerBinding() {
        return fragmentVerticalTumblerBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------------  Common Views  ------------------------------------------------
    // ================================================================================================================

    /**
     * Provides the interface to access HeaderBar widget
     *
     * @return {@link HeaderBar}
     */
    @Override
    public HeaderSingleIconDoubleText provideVerticalTumblerHeaderBar() {
        return fragmentVerticalTumblerBinding.widgetHeaderTopmenu;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access HoursBaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    @Override
    public BaseTumbler provideVerticalHoursTumbler() {
        return fragmentVerticalTumblerBinding.tumblerNumericBasedHours;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access MinutesBaseTumbler widget
     *
     * @return {@link BaseTumbler}
     */
    @Override
    public BaseTumbler provideVerticalMinutesTumbler() {
        return fragmentVerticalTumblerBinding.tumblerNumericBasedMins;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access SaveTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideVerticalTumblerSaveTextView() {
        return fragmentVerticalTumblerBinding.cooktimeButtonBottomLeft;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access StartTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideVerticalTumblerStartTextView() {
        return fragmentVerticalTumblerBinding.cooktimeButtonBottomRight;
    }

    public TextView provideLeftUnitTextView() {
        return fragmentVerticalTumblerBinding.txtUnitLeft;
    }

    public TextView provideRightUnitTextView() {
        return fragmentVerticalTumblerBinding.txtUnitRight;
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
