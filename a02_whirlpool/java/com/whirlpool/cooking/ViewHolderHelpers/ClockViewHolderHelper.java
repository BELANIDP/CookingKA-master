package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import android.widget.ImageView;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.hmi.uicomponents.widgets.clock.ClockTextView;

import com.whirlpool.cooking.databinding.FragmentClockBinding;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractClockViewHolder;

public class ClockViewHolderHelper extends AbstractClockViewHolder {

    private FragmentClockBinding fragmentClockBinding;

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
        fragmentClockBinding = FragmentClockBinding.inflate(inflater, container, false);
        return fragmentClockBinding.getRoot();    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentClockBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the binding class
     *
     * @return {@link FragmentClockBinding}
     */
    @Override
    public FragmentClockBinding getFragmentClockBinding() {
        return fragmentClockBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/


    // ================================================================================================================
    // ------------------------------------------  Common Views Definitions  ------------------------------------------
    // ================================================================================================================

    /**
     * Definition for the WelcomeTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideDateTimeTextView() {
        return fragmentClockBinding.date;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the ClockTextView widget
     *
     * @return {@link ClockTextView}
     */
    @Override
    public ClockTextView provideClockTextView() {
        return fragmentClockBinding.textViewDate;
    }

    /**
     * Definition for the Clock Description TextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideClockDescriptionTextView() {
        return fragmentClockBinding.clockDescription;
    }

    /**
     * Definition for the Clock Description TextView
     *
     * @return {@link android.widget.ImageView}
     */
    @Override
    public ImageView provideImageView() {
        return fragmentClockBinding.icon30pxLockerLocked;
    }

    public int provideCycleSummaryDoorOpenClosePopup() { return R.layout.fragment_popup_buttons; }
    /*---------------------------------------------------X---X---X---------------------------------------------------*/


    // ================================================================================================================
    // -------------------------------------  Modality Specific Views Definitions -------------------------------------
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/


}
