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
import com.whirlpool.cooking.databinding.FragmentToolsLightingBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;

public class ToolsLightingViewHolderHelper extends AbstractToolsLightingViewHolder {

    private FragmentToolsLightingBinding mBinding;

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
        mBinding = FragmentToolsLightingBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public FragmentToolsLightingBinding getToolsLightBinding() {
        return mBinding;
    }

    @Override
    public HeaderSingleIconDoubleText getHeaderBar() {
        return mBinding.toolsShowAllHeaderBar;
    }

    /**
     * @return Title textView
     */
    @Override
    public TextView getTitleTextView() {
        return mBinding.layoutToolsItemWithToggle.toolsItemTitle;
    }

    /**
     * @return Divider View
     */
    @Override
    public View getDividerView() {
        return mBinding.layoutToolsItemWithToggle.itemDivider;
    }

    /**
     * @return switch material
     */
    @Override
    public SwitchMaterial getToggleSwitch() {
        return mBinding.layoutToolsItemWithToggle.toolsItemToggle;
    }

    /**
     * @return Ecomode textView
     */
    @Override
    public TextView getEcoModeActiveTextView() {
        return mBinding.txtEcomodeActive;
    }

    /**
     * @return Edit button
     */
    @Override
    public Button getEditButton() {
        return mBinding.buttonEdit;
    }

    /**
     * @return shadow View
     */
    @Override
    public View getShadowView() {
        return mBinding.shadowView;
    }

    /**
     * @return rowItem View
     */
    @Override
    public ConstraintLayout getRowItem() {
        return mBinding.layoutToolsItemWithToggle.itemLightToggle;
    }

    /**
     * @return ecoWarning layout
     */
    @Override
    public ConstraintLayout getEcoWarningLayout() {
        return mBinding.rootEcoWarning;
    }
}
