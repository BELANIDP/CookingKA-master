package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractTimeNumpadViewHolder;
import com.whirlpool.cooking.databinding.FragmentTimeNumpadBinding;
import com.whirlpool.cooking.widgets.header.HeaderDoubleIconSingleText;
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton;
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView;

public class TimeNumpadViewHolderHelper extends AbstractTimeNumpadViewHolder {

    private FragmentTimeNumpadBinding fragmentTimeNumpadBinding;

    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================

    /**
     * onCreateView provides the interface for the Fragment to be able to inflate the customized view.
     *
     * @param inflater           {@link LayoutInflater}
     * @param container          {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentTimeNumpadBinding = FragmentTimeNumpadBinding.inflate(inflater, container, false);
        return fragmentTimeNumpadBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    public void onDestroyView() {
        fragmentTimeNumpadBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access the binding class
     *
     * @return {@link FragmentTimeNumpadBinding}
     */
    public FragmentTimeNumpadBinding getFragmentTimeNumpadBinding() {
        return fragmentTimeNumpadBinding;
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
        return fragmentTimeNumpadBinding.numpadTextViewBottomRight;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Method to return bottom left button
     *
     * @return {@link TextView}
     */
    @Override
    public TextView getNumpadBottomLeftButton() {
        return fragmentTimeNumpadBinding.numpadTextViewBottomLeft;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Method to return the keyboard
     *
     * @return {@link KeyboardView}
     */
    @Override
    public KeyboardView getNumpadKeyBoardView() {
        return fragmentTimeNumpadBinding.keyboardView;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Method to return tumbler navigation icon
     * <p>
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
     * @return {@link HeaderDoubleIconSingleText}
     */
    @Override
    public HeaderDoubleIconSingleText getNumpadHeaderDoubleIconSingleText() {
        return fragmentTimeNumpadBinding.HeaderDoubleIconSingleText;
    }

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
    public int provideKeypadcancelIcon() {
        return R.drawable.icon_40px_keypadcancel;
    }

    /**
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
     * @return ConstraintLayout
     */
    public ConstraintLayout getButtonSecondaryIconRight() {
        return null;
    }

    /**
     * @return TextView
     */
    public TextView getPowerLevelTextView() {
        return null;
    }

    public View getPowerLevelIcon() {
        return null;
    }

    /**
     * Provides the interface to access Numpad Bottom 30 Sec Button
     *
     * @return {@link NavigationButton}
     */
    public NavigationButton getNumpadBottom30SecButton() {
        return fragmentTimeNumpadBinding.numpadButtonBottom30sec;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access Numpad Bottom 30 Sec Button
     *
     * @return {@link NavigationButton}
     */
    public NavigationButton getNumpadBottom5MinButton() {
        return fragmentTimeNumpadBinding.numpadButtonBottom5Min;
    }
    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    /**
     * Provides the interface to access Start Button functionality
     *
     * @return
     */
    @Override
    public boolean getBottomStartButtonFunctionality() {
        return false;
    }

    /**
     * Provides the interface to access Save for Later Button functionality
     *
     * @return
     */
    @Override
    public boolean getBottomSaveForLaterButtonFunctionality() {
        return false;
    }

    /**
     * Provides the interface to access Left Icon Navigation
     *
     * @return
     */
    @Override
    public int getLeftIconNavigationId() {
        return R.id.action_singleovencooktimenumpad_to_singleOvenCycleSummary;
    }

}
