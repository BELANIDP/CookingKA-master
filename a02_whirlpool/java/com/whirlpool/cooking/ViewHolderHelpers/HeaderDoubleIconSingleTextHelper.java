package com.whirlpool.cooking.ViewHolderHelpers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.HeaderDoubleIconSingleTextViewHolder;

public class HeaderDoubleIconSingleTextHelper extends HeaderDoubleIconSingleTextViewHolder {

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
    public TextView getTitleFirstTextView() {
        return parentView.findViewById(R.id.text_title_single_text_double_icon);
    }


    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return ImageView
     */
    @Override
    public ImageView getRightIconView() {
        return parentView.findViewById(R.id.icon_40px_placeholder_1);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return View
     */
    @Override
    public View getKeypadCancelIcon() {
        return parentView.findViewById(R.id.icon_40px_placeholder);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return ImageView
     */
    @Override
    public ImageView getLeftIconView() {
        return parentView.findViewById(R.id.icon_40px_backarrow);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return TextView
     */
   @Override
    public TextView getTitleSecondtTextView() {
        return parentView.findViewById(R.id.text_title_1);
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/
}
