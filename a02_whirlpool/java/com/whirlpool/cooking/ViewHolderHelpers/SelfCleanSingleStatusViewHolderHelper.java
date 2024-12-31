package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractSelfCleanSingleStatusViewHolder;
import com.whirlpool.cooking.databinding.FragmentSelfcleanSingleStatusBinding;
import com.whirlpool.cooking.widgets.ProgressSetCookTime;
import com.whirlpool.cooking.widgets.header.HeaderTop;

public class SelfCleanSingleStatusViewHolderHelper extends AbstractSelfCleanSingleStatusViewHolder {

    private FragmentSelfcleanSingleStatusBinding fragmentSelfCleanSingleStatusBinding;

    public FragmentSelfcleanSingleStatusBinding getSelfCleanFragmentSingleStatusBinding() {
        return fragmentSelfCleanSingleStatusBinding;
    }

    /**
     *
     * @param inflater {@link LayoutInflater}
     * @param container {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentSelfCleanSingleStatusBinding = FragmentSelfcleanSingleStatusBinding.inflate(inflater, container, false);
        return fragmentSelfCleanSingleStatusBinding.getRoot();
    }

    public void onDestroyView() {
        fragmentSelfCleanSingleStatusBinding = null;
    }

    public HeaderTop getStatusHeaderBarTop() {
        return fragmentSelfCleanSingleStatusBinding.widgetHeaderTop;
    }

    public ProgressSetCookTime getProgressSetCookTime() {
        return fragmentSelfCleanSingleStatusBinding.singleStatusProgressSetCookTime;
    }


    public TextView getStatusBottomRightButton() {
        return fragmentSelfCleanSingleStatusBinding.buttonSecondary1minAddtime;
    }

    public TextView getStatusBottom3dotsButton() {
        return fragmentSelfCleanSingleStatusBinding.buttonSecondary3dots;
    }

    public ImageView getDoorLockImageView(){
        return fragmentSelfCleanSingleStatusBinding.icon30pxPlaceholder3;
    }

    public TextView getDoorLockTextView(){
        return fragmentSelfCleanSingleStatusBinding.textReadytime;
    }

    public int provideDoorLockIcon() {
        return R.drawable.icon_30px_locker_locked;
    }

    public ImageView provideCleanStatusImageView() {
        return fragmentSelfCleanSingleStatusBinding.iconRunningscreenMwo;
    }

    public int providePyroCleanIcon() {
        return R.drawable.icon_50px_pyro;
    }

    public int providePyroCleanRunningIcon() {
        return R.drawable.pyro_running;
    }

    public int provideHydroCleanRunningIcon() {
        return R.drawable.hydro_running_icon;
    }

}
