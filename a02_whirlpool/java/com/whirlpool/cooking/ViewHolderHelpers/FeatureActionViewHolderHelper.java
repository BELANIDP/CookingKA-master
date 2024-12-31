package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractFeatureActionViewHolder;
import com.whirlpool.cooking.databinding.ViewHolderProvisioningBlePairBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;

public class FeatureActionViewHolderHelper extends AbstractFeatureActionViewHolder {

    private ViewHolderProvisioningBlePairBinding featureActionBinding;

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
        featureActionBinding = ViewHolderProvisioningBlePairBinding.inflate(inflater);

        return featureActionBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        featureActionBinding = null;
    }

    /**
     *
     * @return Button
     */
    public Button getBottomRightButton() {
        return featureActionBinding.provisioningButtonPair;
    }

    /**
     *
     * @return Button
     */
    public Button getBottomLeftButton() {
        return featureActionBinding.buttonGhostLightText;
    }

    /** Provides access to the Top Header Bar
     *
     * @return HeaderSingleIconDoubleText
     */
    public HeaderSingleIconDoubleText getFeatureActionHeaderBarTop() {
        return featureActionBinding.headerBarInformation;
    }

    /**
     *
     * @return TextView
     */
    public TextView getTitleText() {
        return featureActionBinding.title;
    }

    /**
     *
     * @return TextView
     */
    public TextView getBodyText() {
        return featureActionBinding.provisioningPairDescription;
    }

    /**
     *
     * @return TextView
     */
    public TextView getDeviceValue() {
        return featureActionBinding.deviceValue;
    }

    /**
     *
     * @return TextView
     */
    public TextView getDeviceLabel() {
        return featureActionBinding.deviceLabel;
    }

    /**
     *
     * @return TextView
     */
    public TextView getSaidValue() {
        return featureActionBinding.saidValue;
    }

    /**
     *
     * @return TextView
     */
    public TextView getSaidLabel() {
        return featureActionBinding.saidLabel;
    }

    /**
     *
     * @return TextView
     */
    public TextView getPinValue() {
        return featureActionBinding.pinValue;
    }

    /**
     *
     * @return TextView
     */
    public TextView getPinLabel() {
        return featureActionBinding.pinLabel;
    }

    /**
     * Provides the interface to access the binding class
     *
     * @return {@link ViewHolderProvisioningBlePairBinding}
     */
    public ViewHolderProvisioningBlePairBinding getFragmentFeatureActionBinding() {
        return featureActionBinding;
    }
}
