package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractInfoSwTermsViewHolder;
import com.whirlpool.cooking.databinding.FragmentToolsInfoSwTermsBinding;
import com.whirlpool.cooking.widgets.header.HeaderBar;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;

public class InfoSwTermsViewHolderHelper extends AbstractInfoSwTermsViewHolder {

    private FragmentToolsInfoSwTermsBinding mBinding;

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
        mBinding = FragmentToolsInfoSwTermsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public FragmentToolsInfoSwTermsBinding getFragmentSwTermsBinding() {
        return mBinding;
    }

    @Override
    public HeaderSingleIconDoubleText getHeaderBar() {
        return mBinding.myapplianceHeader;
    }

    /**
     * @return headline textView
     */
    public TextView provideHeadlineTextView() {
        return mBinding.textSwTcHeadline;
    }

    /**
     * @return content textView
     */
    public TextView provideContentTextView() {
        return mBinding.textSwTcContent;
    }

}
