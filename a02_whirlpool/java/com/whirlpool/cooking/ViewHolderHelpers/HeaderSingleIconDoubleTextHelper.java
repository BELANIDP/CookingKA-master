package com.whirlpool.cooking.ViewHolderHelpers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.HeaderSingleIconDoubleTextViewHolder;

public class HeaderSingleIconDoubleTextHelper extends HeaderSingleIconDoubleTextViewHolder {

    private View parentView;

    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================

    /**
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
     * @return TextView
     */
    @Override
    public TextView getTitleTextView() {
        return parentView.findViewById(R.id.single_icon_double_text_title);
    }

    /**
     * @return TextView
     */
    @Override
    public TextView getSubTitleTextView() {
        return parentView.findViewById(R.id.single_icon_double_text_subtitle);
    }
    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * @return ImageView
     */
    @Override
    public ImageView getLeftIconView() {
        return parentView.findViewById(R.id.single_icon_double_text_back_arrow);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * @return ImageView
     */
    @Override
    public ImageView getRightIconView() {
        return parentView.findViewById(R.id.single_icon_double_text_placeholder);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/
}
