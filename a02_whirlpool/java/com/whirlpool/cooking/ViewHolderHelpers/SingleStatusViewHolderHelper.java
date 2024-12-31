package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractCookingSingleStatusViewHolder;
import com.whirlpool.cooking.databinding.FragmentSingleStatusBinding;
import com.whirlpool.cooking.widgets.ProgressSetCookTime;
import com.whirlpool.cooking.widgets.header.HeaderTop;

public class SingleStatusViewHolderHelper extends AbstractCookingSingleStatusViewHolder {

    private FragmentSingleStatusBinding fragmentSingleStatusBinding;

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
        fragmentSingleStatusBinding = FragmentSingleStatusBinding.inflate(inflater,container,false);
        return fragmentSingleStatusBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentSingleStatusBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access the binding class
     *
     * @return {@link FragmentSingleStatusBinding}
     */
    @Override
    public FragmentSingleStatusBinding getFragmentSingleStatusBinding() {
        return fragmentSingleStatusBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------------  Common Views  ------------------------------------------------
    // ================================================================================================================

    /**
     * TumblerHeader provides the interface to access header top widget
     *
     * @return {@link HeaderTop}
     */
    @Override
    public HeaderTop getStatusHeaderBarTop() {
        return fragmentSingleStatusBinding.widgetHeaderTop;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides interface to access the ProgressSetCookTime widget
     *
     * @return {@link ProgressSetCookTime}
     */
    @Override
    public ProgressSetCookTime getProgressSetCookTime() {
        return fragmentSingleStatusBinding.singleStatusProgressSetCookTime;
    }

    /**
     * Provides the interface to access readyAt TextView
     *
     * @return {@link TextView}
     */
    public TextView provideReadyAtTextView() {
        return fragmentSingleStatusBinding.textReadytime;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------
    /**
     * Provides interface to define cycle icon
     *
     * @return {@link View}
     */
    public ImageView getCycleIconView(){
        return fragmentSingleStatusBinding.iconOvenRunningscreen;
    }

    /**
     * Provides interface to define cycle icon
     *
     * @return {@link View}
     */
    public ImageView getErrorIconView(){
        return fragmentSingleStatusBinding.icon30pxPlaceholder3;
    }

    // ================================================================================================================

    @Override
    public Button getAddCookTimeRightButton(){
        return fragmentSingleStatusBinding.buttonSecondaryTextAddtime;
    }

    @Override
    public Button getEditCycleButton(){
        return fragmentSingleStatusBinding.buttonText3dots;
    }



    /*---------------------------------------------------X---X---X---------------------------------------------------*/

}
