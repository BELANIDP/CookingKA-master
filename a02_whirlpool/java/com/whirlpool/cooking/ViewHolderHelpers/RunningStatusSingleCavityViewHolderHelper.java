package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractCookingSingleStatusViewHolder;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractRunningStatusSingleCavityViewHolder;
import com.whirlpool.cooking.databinding.FragmentRunningStatusSingleCavityBinding;
import com.whirlpool.cooking.widgets.ProgressCookTime;
import com.whirlpool.cooking.widgets.ProgressSetCookTime;
import com.whirlpool.cooking.widgets.header.HeaderTop;

public class RunningStatusSingleCavityViewHolderHelper extends AbstractRunningStatusSingleCavityViewHolder {

    private FragmentRunningStatusSingleCavityBinding fragmentRunningStatusSingleCavityBinding;

    public FragmentRunningStatusSingleCavityBinding getRunningFragmentStatusSingleCavityBinding() {
        return fragmentRunningStatusSingleCavityBinding;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentRunningStatusSingleCavityBinding = FragmentRunningStatusSingleCavityBinding.inflate(inflater,container,false);
        return fragmentRunningStatusSingleCavityBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        fragmentRunningStatusSingleCavityBinding = null;
    }

    @Override
    public HeaderTop getStatusHeaderBarTop() {
        return fragmentRunningStatusSingleCavityBinding.widgetHeaderTop;
    }

    @Override
    public ProgressCookTime getProgressCookTime() {
        return fragmentRunningStatusSingleCavityBinding.singleStatusProgressCookTime;
    }

}
