package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractKitchenTimerRunningViewHolder;
import com.whirlpool.cooking.databinding.FragmentKitchenTimerRunningStatusBinding;
import com.whirlpool.cooking.databinding.FragmentTimerVerticalTumblerBinding;
import com.whirlpool.cooking.widgets.header.HeaderBar;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;
import com.whirlpool.hmi.kitchentimer.uicomponents.widgets.KitchenTimerTextView;
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton;

public class KitchenTimerRunningStatusHolderHelper extends AbstractKitchenTimerRunningViewHolder {

    private FragmentKitchenTimerRunningStatusBinding fragmentKitchenTimerRunningStatusBinding;

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
        fragmentKitchenTimerRunningStatusBinding = FragmentKitchenTimerRunningStatusBinding.inflate(inflater);

        return fragmentKitchenTimerRunningStatusBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        fragmentKitchenTimerRunningStatusBinding = null;
    }

    @Override
    public HeaderSingleIconDoubleText getKitchenTimerRunningHeaderBar() {
        return fragmentKitchenTimerRunningStatusBinding.HeaderBar;
    }

    @Override
    public TextView getKitchenTimerBottomCancelButton() {
        return fragmentKitchenTimerRunningStatusBinding.buttonCancel;
    }

    @Override
    public TextView getKitchenTimerBottomPauseButton() {
        return fragmentKitchenTimerRunningStatusBinding.buttonResume;
    }

    @Override
    public TextView getKitchenTimerBottomAdd1minButton() {
        return fragmentKitchenTimerRunningStatusBinding.button1minAddtime;
    }

    @Override
    public KitchenTimerTextView getKitchenTimerCountDownTextView() {
        return fragmentKitchenTimerRunningStatusBinding.textCooktime;
    }

    /**
     * Provides the interface to access the binding class
     *
     * @return {@link FragmentKitchenTimerRunningStatusBinding}
     */
    @Override
    public FragmentKitchenTimerRunningStatusBinding getFragmentKitchenTimerRunningStatusBinding() {
        return fragmentKitchenTimerRunningStatusBinding;
    }
}
