package com.whirlpool.cooking.ViewHolderHelpers;

import android.view.View;
import android.widget.ImageView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.HeaderBarTopViewHolder;
import com.whirlpool.hmi.uicomponents.widgets.clock.ClockTextView;

public class HeaderBarTopHelper extends HeaderBarTopViewHolder {

    private View parentView;

    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================

    /**
     *
     * @param parentview RootView
     */
    @Override
    public void setParentView(View parentview) {
        parentView = parentview;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------------  Common Views  ------------------------------------------------
    // ================================================================================================================

    /**
     * @return View
     */
    @Override
    public View getHeaderBarTopLeftIcon() {
        return parentView.findViewById(R.id.icon_30px_wififeatures);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return TextView
     */
    public ClockTextView getHeaderBarTopClockView(){
        return parentView.findViewById(R.id.text_time) ;
    }

    /**
     *
     * @return ImageView
     */
    public ImageView getHeaderBarTopLockView(){
        return parentView.findViewById(R.id.icon_30px_locker_locked) ;
    }
    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------

    /**
     *
     * @return Wi-Fi Icon
     */
    public int provideHeaderBarTopLeftIcon() {
        return R.drawable.icon_30px_wififeatures;
    }
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/
}