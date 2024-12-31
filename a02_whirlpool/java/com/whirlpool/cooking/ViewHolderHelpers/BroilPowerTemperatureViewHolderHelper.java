package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractBroilPowerTemperatureChangeViewHolder;
import com.whirlpool.cooking.databinding.FragmentBroilPowerTemperatureChangeBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;

public class BroilPowerTemperatureViewHolderHelper extends AbstractBroilPowerTemperatureChangeViewHolder {

    private FragmentBroilPowerTemperatureChangeBinding fragmentBroilPowerTemperatureChangeBinding;
    // ================================================================================================================
    // -----------------------------------------  General Methods Definitions  ----------------------------------------
    // ================================================================================================================


    /**
     * onCreateView provides the interface for the Fragment to be able to inflate the customized view.
     *
     * @param inflater           {@link LayoutInflater}
     * @param container          {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     * @return {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentBroilPowerTemperatureChangeBinding = FragmentBroilPowerTemperatureChangeBinding.inflate(inflater, container, false);
        return fragmentBroilPowerTemperatureChangeBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentBroilPowerTemperatureChangeBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access the binding class
     *
     * @return {@link FragmentBroilPowerTemperatureChangeBinding}
     */
    @Override
    public FragmentBroilPowerTemperatureChangeBinding getFragmentBroilPowerTemperatureChangeBinding() {
        return fragmentBroilPowerTemperatureChangeBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------  Common Views Definitions  ------------------------------------------
    // ================================================================================================================

    /**
     * Provides the interface to access HeaderBar widget
     *
     * @return {@link HeaderSingleIconDoubleText}
     */
    @Override
    public HeaderSingleIconDoubleText provideBroilPowerTemperatureChangeHeaderBar() {
        return fragmentBroilPowerTemperatureChangeBinding.widgetHeaderTopmenu;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookTemperatureHeaderTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummaryCookTemperatureHeaderTextView() {
        return fragmentBroilPowerTemperatureChangeBinding.text1;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookPowerHeaderTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummaryCookPowerHeaderTextView() {
        return fragmentBroilPowerTemperatureChangeBinding.text2;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookTimeTextHeaderView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummaryCookTimeHeaderTextView() {
        return fragmentBroilPowerTemperatureChangeBinding.text3;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookTemperatureConstraintLayout
     *
     * @return {@link ConstraintLayout}
     */
    @Override
    public ConstraintLayout provideCycleSummaryCookTemperatureConstraintLayout() {
        return fragmentBroilPowerTemperatureChangeBinding.parameters1;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access SelectedCookTemperatureTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummarySelectedCookTemperatureTextView() {
        return fragmentBroilPowerTemperatureChangeBinding.parameters1Text;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookPowerLevelConstraintLayout
     *
     * @return {@link ConstraintLayout}
     */
    @Override
    public ConstraintLayout provideCycleSummaryCookPowerLevelConstraintLayout() {
        return fragmentBroilPowerTemperatureChangeBinding.parameters2;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access SelectedCookPowerLevelTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummarySelectedCookPowerLevelTextView() {
        return fragmentBroilPowerTemperatureChangeBinding.parameters2Text;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access CookTimeConstraintLayout
     *
     * @return {@link ConstraintLayout}
     */
    @Override
    public ConstraintLayout provideCycleSummaryCookTimeConstraintLayout() {
        return fragmentBroilPowerTemperatureChangeBinding.parametersAdd3;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access SelectedCookTimeTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideCycleSummarySelectedCookTimeTextView() {
        return fragmentBroilPowerTemperatureChangeBinding.parametersAdd3AddTime;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the interface to access LeftIcon
     *
     * @return {@link int}
     */
    @Override
    public int provideSummaryLeftIcon() {
        return R.drawable.icon_40px_backarrow;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /*---------------------------------------------------X---X---X---------------------------------------------------*/
}
