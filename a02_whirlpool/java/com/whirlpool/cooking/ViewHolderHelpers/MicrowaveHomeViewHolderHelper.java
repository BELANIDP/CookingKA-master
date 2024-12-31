package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractMicrowaveHomeViewHolder;
import com.whirlpool.cooking.databinding.FragmentMicrowaveHomeBinding;
import com.whirlpool.cooking.widgets.header.HeaderTop;

public class MicrowaveHomeViewHolderHelper extends AbstractMicrowaveHomeViewHolder {

    private FragmentMicrowaveHomeBinding fragmentMicrowaveHomeBinding;

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
        fragmentMicrowaveHomeBinding = FragmentMicrowaveHomeBinding.inflate(inflater, container, false);
        return fragmentMicrowaveHomeBinding.getRoot();    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     */
    @Override
    public void onDestroyView() {
        fragmentMicrowaveHomeBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the binding class
     *
     * @return {@link FragmentMicrowaveHomeBinding}
     */
    @Override
    public FragmentMicrowaveHomeBinding getFragmentMicrowaveHomeBinding() {
        return fragmentMicrowaveHomeBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------  Common Views Definitions  ------------------------------------------
    // ================================================================================================================

    /**
     *
     * @return HeaderTop
     */
    @Override
    public HeaderTop provideMicrowaveHomeHeaderTop() {
        return fragmentMicrowaveHomeBinding.widgetHeaderTop;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return View
     */
    @Override
    public View provideMicrowaveHomeManualModeIconView() {
        return fragmentMicrowaveHomeBinding.homeTodayIcon1Line;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return View
     */
    @Override
    public View provideMicrowaveHomeAssistedModeIconView() {
        return fragmentMicrowaveHomeBinding.homeTodayIconLine;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return View
     */
    @Override
    public TextView provideMicrowaveHomeManualModeTextView() {
        return fragmentMicrowaveHomeBinding.homeTodayIcon1ManualFunctions;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return View
     */
    @Override
    public TextView provideMicrowaveHomeAssistedModeTextView() {
        return fragmentMicrowaveHomeBinding.homeTodayIconManualFunctions;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return View
     */
    @Override
    public ConstraintLayout provideMicrowaveHomeManualModeConstraintLayout() {
        return fragmentMicrowaveHomeBinding.textViewHomeScreenManualMode;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return View
     */
    @Override
    public ConstraintLayout provideMicrowaveHomeAssistedModeConstraintLayout() {
        return fragmentMicrowaveHomeBinding.textViewAssistedCookingMode;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------  Modality Specific Views Definitions -------------------------------------
    // ================================================================================================================

    /**
     *
     * @return View
     */
    @Override
    public View provideMicrowaveHomeManualModeTileView() {
        return fragmentMicrowaveHomeBinding.homeTodayIcon1IconOvenHometileManualmode;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return View
     */
    @Override
    public View provideMicrowaveHomeAssistedModeTileView() {
        return fragmentMicrowaveHomeBinding.homeTodayIconIconOvenHometile6thsense;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

}
