package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractToolsLightingViewHolder;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractToolsSoundVolumeViewHolder;
import com.whirlpool.cooking.databinding.FragmentToolsLightingBinding;
import com.whirlpool.cooking.databinding.FragmentToolsSoundVolumeBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;

public class ToolsSoundVolumeViewHolderHelper extends AbstractToolsSoundVolumeViewHolder {

    private FragmentToolsSoundVolumeBinding mBinding;

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
        mBinding = FragmentToolsSoundVolumeBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public FragmentToolsSoundVolumeBinding getToolsSoundVolumeBinding() {
        return mBinding;
    }

    @Override
    public HeaderSingleIconDoubleText getHeaderBar() {
        return mBinding.toolsShowAllHeaderBar;
    }

    @Override
    public TextView getTitle2Value() {
        return mBinding.item2Value;
    }

    @Override
    public TextView getTitle3Value() {
        return mBinding.item3Value;
    }

    @Override
    public ConstraintLayout getRowItem1() {
        return mBinding.displayItem1;
    }

    @Override
    public ConstraintLayout getRowItem2() {
        return mBinding.displayItem2;
    }

    @Override
    public ConstraintLayout getRowItem3() {
        return mBinding.displayItem3;
    }

    @Override
    public View getShadowView() {
        return mBinding.shadowView;
    }

    @Override
    public SwitchMaterial getMuteToggle() {
        return mBinding.muteToggle;
    }

}
