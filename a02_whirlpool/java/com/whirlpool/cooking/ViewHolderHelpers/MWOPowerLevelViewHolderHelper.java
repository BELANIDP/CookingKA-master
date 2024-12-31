package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractMWOPowerLevelViewHolder;
import com.whirlpool.cooking.databinding.FragmentPowerLevelPopupBinding;
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler;

public class MWOPowerLevelViewHolderHelper extends AbstractMWOPowerLevelViewHolder {
    private FragmentPowerLevelPopupBinding fragmentPowerLevelPopupBinding;

    public FragmentPowerLevelPopupBinding getFragmentPowerLevelPopupBinding() {
        return fragmentPowerLevelPopupBinding;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentPowerLevelPopupBinding = FragmentPowerLevelPopupBinding.inflate(inflater,container,false);

        return fragmentPowerLevelPopupBinding.getRoot();
    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public FragmentPowerLevelPopupBinding getFragmentNumpadBinding() {
        return fragmentPowerLevelPopupBinding;
    }

    @Override
    public TextView getPowerLevelTextView() {
        return fragmentPowerLevelPopupBinding.powerLevelText;
    }

    @Override
    public View getPowerLevelArrowIcon() {
        return fragmentPowerLevelPopupBinding.powerCloseIcon;
    }

    @Override
    public BaseTumbler getPowerLevelTumbler() {
        return fragmentPowerLevelPopupBinding.powerLevels;
    }
}
