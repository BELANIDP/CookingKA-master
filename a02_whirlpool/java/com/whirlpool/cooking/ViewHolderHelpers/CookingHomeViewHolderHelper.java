package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;


import com.whirlpool.cooking.base.AbstractViewHolders.AbstractCookingHomeViewHolder;
import com.whirlpool.cooking.databinding.FragmentHomeBinding;
import com.whirlpool.cooking.widgets.HomeCycleView;
import com.whirlpool.cooking.widgets.header.HeaderTop;

public class CookingHomeViewHolderHelper  extends AbstractCookingHomeViewHolder {

    private FragmentHomeBinding fragmentHomeBinding;

    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================

    /**
     * Inflate the customized view
     *
     * @param inflater           {@link LayoutInflater}
     * @param container          {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     * @return {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return fragmentHomeBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentHomeBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the binding class
     *
     * @return {@link FragmentHomeBinding}
     */
    @Override
    public FragmentHomeBinding getFragmentHomeBinding() {
        return fragmentHomeBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------------  Common Views  ------------------------------------------------
    // ================================================================================================================

    /**
     * Provides the interface to access HeaderTop widget
     *
     * @return {@link HeaderTop}
     */
    @Override
    public HeaderTop provideHomeHeaderTop() {
        return fragmentHomeBinding.widgetHeaderTop;
    }

    /**
     * Provides the interface to access HomeCycleTile1
     *
     * @return {@link HomeCycleView}
     */
    @Override
    public HomeCycleView provideHomeCycleTile1() {
        return fragmentHomeBinding.homeDirectaccesfunction;
    }

    /**
     * Provides the interface to access HomeCycleTile2
     *
     * @return {@link HomeCycleView}
     */
    @Override
    public HomeCycleView provideHomeCycleTile2() {
        return fragmentHomeBinding.homeDirectaccesfunction1;
    }

    /**
     * Provides the interface to access ManualMoreModeConstraintLayout
     *
     * @return {@link FrameLayout}
     */
    @Override
    public ConstraintLayout provideHomeManualMoreModeConstraintLayout() {
        return fragmentHomeBinding.homeMoremodes;
    }

    /**
     * Provides the interface to access ManualMoreModeTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideHomeManualMoreModeTextView() {
        return fragmentHomeBinding.homeMoremodesMoreModes;
    }

    /**
     * Provides the interface to access AssistedModeConstraintLayout
     *
     * @return {@link ConstraintLayout}
     */
    @Override
    public ConstraintLayout provideHomeAssistedModeConstraintLayout() {
        return fragmentHomeBinding.home6thfunctions;
    }

    /**
     * Provides the interface to access AssistedModeTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideHomeAssistedModeTextView() {
        return fragmentHomeBinding.home6thfunctionsFunctions;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

}