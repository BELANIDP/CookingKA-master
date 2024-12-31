package com.whirlpool.cooking.ViewHolderHelpers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.HeaderBarViewHolder;

public class HeaderBarHelper extends HeaderBarViewHolder {

    private View parentView;

    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================

    /**
     *
     * @param parentview RootView
     */
    @Override
    public void setParentView( View parentview) {
        parentView = parentview;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------------  Common Views  ------------------------------------------------
    // ================================================================================================================

    /**
     *
     * @return TextView
     */
    @Override
    public TextView getTitleTextView() {
        return parentView.findViewById(R.id.text_title);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return ImageView
     */
    @Override
    public ImageView getRightIconView() {
        return parentView.findViewById(R.id.header_hour_bar_right_icon);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return View
     */
    @Override
    public View getKeypadCancelIcon() {
        return parentView.findViewById(R.id.icon_50px_keypadcancel);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return ImageView
     */
    @Override
    public ImageView getLeftIconView() {
        return parentView.findViewById(R.id.header_hour_bar_left_icon);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return TextView
     */
    @Override
    public TextView getSubTitleTextView() {
        return parentView.findViewById(R.id.text_subtitle);
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/
}
