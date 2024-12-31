package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractCycleInstructionViewHolder;
import com.whirlpool.cooking.databinding.FragmentCycleInstructionBinding;
import com.whirlpool.cooking.databinding.FragmentTumblerBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton;

public class CycleInstructionViewHolderHelper extends AbstractCycleInstructionViewHolder {

    FragmentCycleInstructionBinding fragmentCycleInstructionBinding;

    // ================================================================================================================
    // -----------------------------------------  General Methods Definitions  ----------------------------------------
    // ================================================================================================================

    /**
     * Inflate the customized view
     *
     * @param inflater           {@link LayoutInflater}
     * @param container          {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     * @return {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentCycleInstructionBinding = FragmentCycleInstructionBinding.inflate(inflater);
        return fragmentCycleInstructionBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentCycleInstructionBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the binding class
     *
     * @return {@link FragmentTumblerBinding}
     */
    @Override
    public FragmentCycleInstructionBinding getFragmentInformationBinding() {
        return fragmentCycleInstructionBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------  Common Views Definitions  ------------------------------------------
    // ================================================================================================================


    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the bottom left button
     *
     * @return {@link int}
     */
    @Override
    public NavigationButton provideGhostButton() {
        return fragmentCycleInstructionBinding.buttonCtaGhostLightText;
    }

    /**
     * Definition for the bottom left button
     *
     * @return {@link int}
     */
    @Override
    public NavigationButton providePrimaryButton() {
        return fragmentCycleInstructionBinding.buttonsPrimary;
    }
    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the HeaderBar widget
     *
     * @return {@link HeaderSingleIconDoubleText}
     */
    @Override
    public HeaderSingleIconDoubleText provideInstructionHeader() {
        return fragmentCycleInstructionBinding.widgetMultiheader;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the InstructionImageView
     *
     * @return {@link ImageView}
     */
    @Override
    public ImageView provideInstructionImageView() {
        return fragmentCycleInstructionBinding.iconOvenInstruction;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the provide the image
     *
     * @return {@link int}
     */
    @Override
    public int provideLeftIcon() {
        return R.drawable.icon_40px_backarrow;
    }

    /**
     * Definition for the provide the image icon
     *
     * @return {@link int}
     */
    @Override
    public int providePyroCleanIcon() {
        return R.drawable.icon_50px_pyro;
    }

    /**
     * Provides the interface to access ScrollInstructionsTextView
     *
     * @return {@link TextView}
     */
    @Override
    public TextView provideScrollInstructionsTextView1() {
        return fragmentCycleInstructionBinding.text1;
    }
    @Override
    public TextView provideScrollInstructionsTextView2() {
        return fragmentCycleInstructionBinding.text2;
    }
    @Override
    public TextView provideScrollInstructionsTextView3() {
        return fragmentCycleInstructionBinding.text3;
    }

    @Override
    public TextView provideScrollInstructionsTextView4() {
        return fragmentCycleInstructionBinding.text4;
    }

    @Override
    public TextView provideScrollInstructionsHeadlineTextView1() {
        return fragmentCycleInstructionBinding.headline1;
    }

    @Override
    public TextView provideScrollInstructionsHeadlineTextView2() {
        return fragmentCycleInstructionBinding.headline2;
    }

    @Override
    public TextView provideScrollInstructionsHeadlineTextView3() {
        return fragmentCycleInstructionBinding.headline3;
    }

    @Override
    public TextView provideScrollInstructionsHeadlineTextView4() {
        return fragmentCycleInstructionBinding.headline4;
    }


    /**
     * @return
     */
    public ScrollView provideInformationPrimaryScrollViewTextView() {
        return fragmentCycleInstructionBinding.informationPrimaryScrollView;
    }
    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------  Modality Specific Views Definitions -------------------------------------
    public int provideAnimatedPopupLayout(){
        return R.layout.fragment_popup_animated;
    }

    public int provideDoorOpenClosePopupLayout(){
        return R.layout.fragment_popup_instruction;
    }
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

}