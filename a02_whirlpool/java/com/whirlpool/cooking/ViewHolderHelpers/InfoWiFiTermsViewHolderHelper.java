package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractInfoTermsViewHolder;
import com.whirlpool.cooking.databinding.FragmentToolsInfoWifiTermsBinding;
import com.whirlpool.cooking.widgets.header.HeaderBar;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;

public class InfoWiFiTermsViewHolderHelper extends AbstractInfoTermsViewHolder {

    private FragmentToolsInfoWifiTermsBinding mBinding;

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
        mBinding = FragmentToolsInfoWifiTermsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public FragmentToolsInfoWifiTermsBinding getFragmentWiFiTermsBinding() {
        return mBinding;
    }

    @Override
    public HeaderSingleIconDoubleText getHeaderBar() {
        return mBinding.myapplianceHeader;
    }

    /**
     * @return primary textView
     */
    public TextView providePrimaryTextView() {
        return mBinding.textPrimaryWifiTc;
    }

    /**
     * @return headline textView
     */
    public TextView provideHeadlineTextView() {
        return mBinding.textWifiTcHeadline;
    }

    /**
     * @return content textView
     */
    public TextView provideContentTextView() {
        return mBinding.textWifiTcContent;
    }

    /**
     * @return placeholderImage
     */
    public ImageView provideQrPlaceholderImage() {
        return mBinding.imageQrPlaceholder;
    }

}
