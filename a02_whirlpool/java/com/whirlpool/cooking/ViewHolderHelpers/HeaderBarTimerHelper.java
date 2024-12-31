package com.whirlpool.cooking.ViewHolderHelpers;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.HeaderBarTimerViewHolder;

public class HeaderBarTimerHelper extends HeaderBarTimerViewHolder {

    private View mParentView;

    /**
     * @param parentview rootView
     */
    @Override
    public void setParentView(View parentview) {
        mParentView = parentview;
    }

    @Override
    public ImageView getLeftIcon() {
        return (ImageView) getview(R.id.headerbar_timer_backarrow_icon);
    }

    @Override
    public TextView getPrimaryTextOne() {
        return (TextView) getview(R.id.primary_text_one);
    }

    @Override
    public TextView getPrimaryTextTwo() {
        return (TextView) getview(R.id.primary_text_two);
    }

    @Override
    public TextView getPrimaryTextThree() {
        return (TextView) getview(R.id.primary_text_three);
    }

    @Override
    public TextView getPrimaryTextUnitOne() {
        return (TextView) getview(R.id.primary_text_unit_one);
    }

    @Override
    public TextView getPrimaryTextUnitTwo() {
        return (TextView) getview(R.id.primary_text_unit_two);
    }

    @Override
    public TextView getPrimaryTextUnitThree() {
        return (TextView) getview(R.id.primary_text_unit_three);
    }

    @Override
    public TextView getSecondaryTextOne() {
        return (TextView) getview(R.id.secondary_text_one);
    }

    @Override
    public TextView getSecondaryTextTwo() {
        return (TextView) getview(R.id.secondary_text_two);
    }

    @Override
    public TextView getSecondaryTextThree() {
        return (TextView) getview(R.id.secondary_text_three);
    }

    @Override
    public ImageView getRightDeleteIcon() {
        return (ImageView) getview(R.id.headerbar_timer_delete_icon);
    }

    @Override
    public ImageView getRightToggleIcon() {
        return (ImageView) getview(R.id.headerbar_timer_toggle_icon);
    }

    @Override
    public LinearLayout getPrimaryLayout() {
        return (LinearLayout) getview(R.id.layout_primary);
    }

    @Override
    public LinearLayout getSecondaryLayout() {
        return (LinearLayout) getview(R.id.layout_secondary);
    }

    /**
     * @param widgetId
     * @return actual view mapped with widgetId
     */
    private View getview(int widgetId) {
        return mParentView.findViewById(widgetId);
    }
}
