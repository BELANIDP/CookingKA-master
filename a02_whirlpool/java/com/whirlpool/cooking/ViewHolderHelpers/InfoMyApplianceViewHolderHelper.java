package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractInfoMyApplianceViewHolder;
import com.whirlpool.cooking.databinding.FragmentToolsInfoMyapplianceBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;

public class InfoMyApplianceViewHolderHelper extends AbstractInfoMyApplianceViewHolder {

    private FragmentToolsInfoMyapplianceBinding mBinding;

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
        mBinding = FragmentToolsInfoMyapplianceBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public FragmentToolsInfoMyapplianceBinding getFragmentInfoMyApplianceBinding() {
        return mBinding;
    }

    @Override
    public HeaderSingleIconDoubleText getHeaderBar() {
        return mBinding.myapplianceHeader;
    }

    /**
     * @return ApplianceNumber textView
     */
    public TextView provideApplianceNumber() {
        return mBinding.textValue1;
    }

    /**
     * @return SerialNumber textView
     */
    public TextView provideSerialNumber() {
        return mBinding.textValue2;
    }

    /**
     * @return SKU textView
     */
    public TextView provideSku() {
        return mBinding.textValue3;
    }

    /**
     * @return lastUpdate textView
     */
    public TextView provideLastupdate() {
        return mBinding.textValue4;
    }

}
