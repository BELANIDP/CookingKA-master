package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractCleanInstructionViewHolder;
import com.whirlpool.cooking.databinding.FragmentCleanInformationBinding;
import com.whirlpool.cooking.databinding.FragmentTumblerBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton;

public class CleanInstructionViewHolderHelper extends AbstractCleanInstructionViewHolder {

    FragmentCleanInformationBinding fragmentCleanInformationBinding;

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
        fragmentCleanInformationBinding = FragmentCleanInformationBinding.inflate(inflater);
        return fragmentCleanInformationBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentCleanInformationBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the binding class
     *
     * @return {@link FragmentTumblerBinding}
     */
    @Override
    public FragmentCleanInformationBinding getFragmentInformationBinding() {
        return fragmentCleanInformationBinding;
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
        return fragmentCleanInformationBinding.buttonLeft;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the HeaderSingleIconDoubleText widget
     *
     * @return {@link HeaderSingleIconDoubleText}
     */
    @Override
    public HeaderSingleIconDoubleText provideInstructionHeader() {
        return fragmentCleanInformationBinding.headerBarInformation;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Definition for the InstructionImageView
     *
     * @return {@link ImageView}
     */
    @Override
    public ImageView provideInstructionImageView() {
        return fragmentCleanInformationBinding.iconOvenMenucycleManualmodeConvectbake;
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

    /**
     * Provides the interface to access provideHydroCleanIconInstruction
     *
     * @return {@link TextView}
     */
    public int provideHydroCleanIconInstruction(){
        return R.drawable.hydro_instruction_icon;
    }
    @Override
    public TextView provideScrollInstructionsTextView1() {
        return fragmentCleanInformationBinding.text1;
    }
    @Override
    public TextView provideScrollInstructionsTextView2() {
        return fragmentCleanInformationBinding.text2;
    }
    @Override
    public TextView provideScrollInstructionsTextView3() {
        return fragmentCleanInformationBinding.text3;
    }
    @Override
    public TextView provideScrollInstructionsTextView4() {
        return fragmentCleanInformationBinding.text4;
    }

    @Override
    public TextView provideScrollInstructionsTextView5() {
        return fragmentCleanInformationBinding.text5;
    }

    /**
     * @return
     */
    public ScrollView provideInformationPrimaryScrollViewTextView() {
        return fragmentCleanInformationBinding.informationPrimaryScrollView;
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