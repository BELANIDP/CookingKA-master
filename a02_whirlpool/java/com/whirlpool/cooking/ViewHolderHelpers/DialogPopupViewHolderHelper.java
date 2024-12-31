package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractDialogPopupViewHolder;
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton;

import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class DialogPopupViewHolderHelper extends AbstractDialogPopupViewHolder {

    private View popupView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState,int layout_id) {
        popupView = inflater.inflate(layout_id,container,false);
        return popupView.getRootView();
    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public ImageView getIconPlaceHolder() {
        if(Objects.nonNull(popupView)){
            return popupView.findViewById(R.id.popup_icon_30px_placeholder);
        }else{
            return null;
        }
    }

    @Override
    public ImageView getRectangleIcon() {
        if(Objects.nonNull(popupView)){
            return popupView.findViewById(R.id.popup__rectangle_icon);
        }else{
            return null;
        }
    }

    @Override
    public TextView getHeadLineTextView() {
        if(Objects.nonNull(popupView)){
            return popupView.findViewById(R.id.popup_headline);
        }else{
            return null;
        }
    }

    @Override
    public TextView getContentTextView() {
        if(Objects.nonNull(popupView)){
            return popupView.findViewById(R.id.popup_body);
        }else{
            return null;
        }
    }

    @Override
    public NavigationButton getPopupLeftButton() {
        if(Objects.nonNull(popupView)){
            return popupView.findViewById(R.id.active_button_left);
        }else{
            return null;
        }
    }

    @Override
    public NavigationButton getPopupRightButton() {
        if(Objects.nonNull(popupView)){
            return popupView.findViewById(R.id.active_button_right);
        }else{
            return null;
        }
    }

    @Override
    public ProgressBar getProgressBar() {
        if(Objects.nonNull(popupView)){
            return popupView.findViewById(R.id.progressbar_popup);
        }else{
            return null;
        }
    }

    @Override
    public LottieAnimationView getAnimatedIconPlaceHolder() {
        if (Objects.nonNull(popupView)) {
            return popupView.findViewById(R.id.animated_icon);
        } else {
            return null;
        }
    }

    @Override
    public GifImageView getGifIconPlaceHolder() {
        if (Objects.nonNull(popupView)) {
            return popupView.findViewById(R.id.animated_icon);
        } else {
            return null;
        }
    }

}
