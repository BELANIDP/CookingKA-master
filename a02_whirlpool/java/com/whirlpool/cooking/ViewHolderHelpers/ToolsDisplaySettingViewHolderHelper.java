package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractToolsDisplaySettingViewHolder;
import com.whirlpool.cooking.databinding.FragmentToolsDisplaySettingsBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;

public class ToolsDisplaySettingViewHolderHelper extends AbstractToolsDisplaySettingViewHolder {

    private FragmentToolsDisplaySettingsBinding mBinding;

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
        mBinding = FragmentToolsDisplaySettingsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public FragmentToolsDisplaySettingsBinding getToolsDisplaySettingsBinding() {
        return mBinding;
    }

    @Override
    public HeaderSingleIconDoubleText getHeaderBar() {
        return mBinding.toolsShowAllHeaderBar;
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
    public TextView getTitle1TextView() {
        return mBinding.item1Title;
    }

    @Override
    public TextView getTitle1TextViewValue() {
        return mBinding.item1Value;
    }

    @Override
    public TextView getTitle2TextView() {
        return mBinding.item2Title;
    }

    @Override
    public TextView getTitle2TextViewValue() {
        return mBinding.item2Value;
    }

    @Override
    public TextView getTitle3TextView() {
        return mBinding.item3Title;
    }

    @Override
    public TextView getTitle3TextViewValue() {
        return mBinding.item3Value;
    }

    @Override
    public View getShadowView() {
        return mBinding.shadowView;
    }

    @Override
    public TextView getEcoModeTextView() {
        return mBinding.txtEcomodeActive;
    }

    @Override
    public Button getEditButton() {
        return mBinding.buttonEdit;
    }
}
