package com.whirlpool.cooking.ViewHolderHelpers;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractProgressSetCookTimeViewHolder;

import pl.droidsonroids.gif.GifImageView;

public class ProgressSetCookTimeViewHolderHelper extends AbstractProgressSetCookTimeViewHolder {

    private View parentView;

    // ================================================================================================================
    // -----------------------------------------  General Methods Definitions  ----------------------------------------
    // ================================================================================================================

    /**
     * @param parentview rootView
     */
    @Override
    public void setParentView(View parentview) {
        parentView = parentview;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        parentView = null;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------  Common Views Definitions  ------------------------------------------
    // ================================================================================================================

//    /**
//     * Provides the interface to access function TextView
//     *
//     * @return {@link TextView}
//     */
//    @Override
//    public TextView provideCookFunctionTextView() {
//        return parentView.findViewById(R.id.text_function);
//    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access temperature TextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCookTemperatureTextView() {
        return parentView.findViewById(R.id.text_temperature);
    }

    /**
     * Provides the interface to access temperature TextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCookPowerTextView() {
        return parentView.findViewById(R.id.text_power_level);
    }

    @Override
    public TextView providePowerTemperatureTextView() {
        return parentView.findViewById(R.id.text_power_temperature);
    }

    @Override
    public TextView provideCookTemperaturePowerTextView() {
        return parentView.findViewById(R.id.text_power_temperature);
    }

    @Override
    public TextView provideCookTemperatureWithIcon() {
        return parentView.findViewById(R.id.text_temperature_with_icon);
    }

    @Override
    public GifImageView provideTemperatureWithIcon(){
        return parentView.findViewById(R.id.icon_temperature_with_text);
    }

    @Override
    public LottieAnimationView provideUntimedProgressBar(){
        return parentView.findViewById(R.id.untimed_progressbar);
    }

    @Override
    public TextView provideRampUpDownTemperatureTextView() {
        return parentView.findViewById(R.id.ramp_up_down_temperature);
    }

    @Override
    public GifImageView provideIconWithRampUpDoneIcon(){
        return parentView.findViewById(R.id.ramp_up_down_icon);
    }
    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access cook time TextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCookTimeTextView() {
        return parentView.findViewById(R.id.text_cooktime);
    }
    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access readyAt TextView
     *
     * @return {@link TextView}
     */
    public TextView provideReadyAtTextView() {
        return null;
    }
    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access description TextView
     *
     * @return {@link TextView}
     */
    public TextView provideDescriptionTextView() {
        return parentView.findViewById(R.id.text_function_name);
    }
    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access progressBar widget
     *
     * @return {@link ProgressBar}
     */
    @Override
    public ProgressBar provideProgressBar() {
        return parentView.findViewById(R.id.progressbar_cook);
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access progressSetCookTime layout
     *
     * @return {@link int}
     */
    @Override
    public int provideProgressSetCookTimeLayout() {
        return R.layout.item_progress_set_cook_time;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------  Modality Specific Views Definitions -------------------------------------

    /**
     * Provides the interface to access function TextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleViewPreheatPhase() {
        return parentView.findViewById(R.id.text_function_name);
    }
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

}