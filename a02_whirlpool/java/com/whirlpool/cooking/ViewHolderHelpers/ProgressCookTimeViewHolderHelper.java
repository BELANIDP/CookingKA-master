package com.whirlpool.cooking.ViewHolderHelpers;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractProgressCookTimeViewHolder;
import com.whirlpool.hmi.uicomponents.widgets.clock.ClockTextView;

public class ProgressCookTimeViewHolderHelper extends AbstractProgressCookTimeViewHolder {

    View parentView;

    // ================================================================================================================
    // -----------------------------------------  General Methods Definitions  ----------------------------------------
    // ================================================================================================================

    @Override
    public void setParentView(View parentview) {
        parentView = parentview;
    }

    /**
     * Provides the interface to access progressSetCookTime layout
     *
     * @return {@link int}
     */
    @Override
    public int provideProgressCookTimeLayout() {
        return R.layout.item_running_full_progress;
    }

    /**
     * Clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        parentView = null;
    }

    @Override
    public TextView provideCookProgramDescription() {
        return parentView.findViewById(R.id.text_description);
    }

    @Override
    public ImageView provideCookProgramIcon() {
        return parentView.findViewById(R.id.icon_40px_placeholder);
    }

    @Override
    public TextView provideCookTemperatureValue() {
        return parentView.findViewById(R.id.text_value);
    }

    @Override
    public ImageView provideCookTemperatureIcon() {
        return parentView.findViewById(R.id.icon_40px_placeholder_1);
    }

    @Override
    public TextView provideCookTime() {
        return parentView.findViewById(R.id.text_time);
    }

    /**
     * Provides the interface to access progressBar widget
     *
     * @return {@link ProgressBar}
     */
    @Override
    public ProgressBar provideProgressBar() {
        return parentView.findViewById(R.id.bar_progressbar);
    }

    @Override
    public TextView provideReadyAtText() {
        return parentView.findViewById(R.id.text_readyat);
    }

    @Override
    public TextView provideReadyAtTime() {
        return parentView.findViewById(R.id.text_readyat_1);
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------  Modality Specific Views Definitions -------------------------------------
    // ================================================================================================================

    @Override
    public Button provideSecondaryButton1() {
        return parentView.findViewById(R.id.button_secondary);
    }

    @Override
    public Button provideSecondaryButton2() {
        return parentView.findViewById(R.id.button_secondary_1);
    }

    @Override
    public Button provideSecondaryButton3() {
        return parentView.findViewById(R.id.button_secondary_2);
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/


}

