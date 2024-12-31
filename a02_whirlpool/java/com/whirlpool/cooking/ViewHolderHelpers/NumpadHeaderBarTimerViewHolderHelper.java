package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractNumpadHeaderBarTimerViewHolder;
import com.whirlpool.cooking.databinding.FragmentNumpadWithHeaderbarTimerBinding;
import com.whirlpool.cooking.widgets.header.HeaderBar;
import com.whirlpool.cooking.widgets.header.HeaderBarTimer;
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton;
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView;

public class NumpadHeaderBarTimerViewHolderHelper extends AbstractNumpadHeaderBarTimerViewHolder {

    private FragmentNumpadWithHeaderbarTimerBinding fragmentNumpadWithHeaderbarTimerBinding;

    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================

    /**
     * onCreateView provides the interface for the Fragment to be able to inflate the customized view.
     *
     * @param inflater {@link LayoutInflater}
     * @param container {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){
        fragmentNumpadWithHeaderbarTimerBinding = FragmentNumpadWithHeaderbarTimerBinding.inflate(inflater,container,false);
        return fragmentNumpadWithHeaderbarTimerBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    public void onDestroyView(){
        fragmentNumpadWithHeaderbarTimerBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access the binding class
     *
     * @return {@link FragmentNumpadWithHeaderbarTimerBinding}
     */
    public FragmentNumpadWithHeaderbarTimerBinding getFragmentNumpadWithHeaderbarTimerBinding(){
        return fragmentNumpadWithHeaderbarTimerBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------------  Common Views  ------------------------------------------------
    // ================================================================================================================

    /**
     * method to return the bottom right button
     *
     * @return {@link TextView}
     */
    @Override
    public TextView getNumpadBottomRightButton() {
        return fragmentNumpadWithHeaderbarTimerBinding.numpadTextViewBottomRight;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Method to return bottom left button
     *
     * @return {@link TextView}
     */
    @Override
    public TextView getNumpadBottomLeftButton() {
        return fragmentNumpadWithHeaderbarTimerBinding.numpadTextViewBottomLeft;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Method to return the keyboard
     *
     * @return {@link KeyboardView}
     */
    @Override
    public KeyboardView getNumpadKeyBoardView() {
        return fragmentNumpadWithHeaderbarTimerBinding.keyboardView;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Method to return tumbler navigation icon
     *
     * * @return {@link int}
     */
    @Override
    public int getTumblerNavigationIcon() {
        return R.drawable.icon_40px_tumbler;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Method to return header bar
     *
     * @return {@link HeaderBar}
     */
    @Override
    public HeaderBarTimer getNumpadHeaderBarTimer() {
        return fragmentNumpadWithHeaderbarTimerBinding.HeaderBarTimer;
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
     *
     * @return {@link int}
     */
    @Override
    public int provideKeypadcancelIcon() {
        return R.drawable.icon_40px_keypadcancel;
    }

    /**
     *
     * @return {@link int}
     */
    @Override
    public int provideTumblerIcon() {
        return R.drawable.icon_40px_tumbler;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------
    // ================================================================================================================

    /**
     * method to return the bottom Time Range Warning Text View
     *
     * @return {@link TextView}
     */
    @Override
    public TextView getNumpadTimeRangeWarning() {
        return fragmentNumpadWithHeaderbarTimerBinding.textWarning;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    /**
     * Provides the interface to access Start Button functionality
     * @return
     */
    @Override
    public boolean getBottomStartButtonFunctionality(){
        return false;
    }

    /**
     * Provides the interface to access Save for Later Button functionality
     * @return
     */
    @Override
    public boolean getBottomSaveForLaterButtonFunctionality(){
        return false;
    }

    /**
     * Provides the interface to access Left Icon Navigation
     * @return
     */
    @Override
    public int getLeftIconNavigationId(){
        return R.id.action_singleovencooktimenumpad_to_singleOvenCycleSummary;
    }

}
