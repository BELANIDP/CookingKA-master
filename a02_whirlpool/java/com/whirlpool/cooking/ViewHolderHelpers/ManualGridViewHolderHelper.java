package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.base.AbstractViewHolders.AbstractManualGridViewHolder;
import com.whirlpool.cooking.databinding.FragmentManualModesBinding;
import com.whirlpool.cooking.widgets.header.HeaderSingleIconDoubleText;
import com.whirlpool.hmi.uicomponents.widgets.grid.GridView;

public class ManualGridViewHolderHelper extends AbstractManualGridViewHolder {

    private FragmentManualModesBinding fragmentManualModesBinding;

    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================

    /**
     *
     * @param inflater {@link LayoutInflater}
     * @param container {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     * @return {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManualModesBinding = FragmentManualModesBinding.inflate(inflater,container,false);
        return fragmentManualModesBinding.getRoot();
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     */
    @Override
    public void onDestroyView() {
        fragmentManualModesBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return BindingInstance
     */
    @Override
    public FragmentManualModesBinding getFragmentManualModesBinding() {
        return fragmentManualModesBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // ------------------------------------------------  Common Views  ------------------------------------------------
    // ================================================================================================================

    /**
     *
     * @return HeaderBarInstance
     */
    @Override
    public HeaderSingleIconDoubleText provideManualGridHeaderBar() {
        return fragmentManualModesBinding.HeaderBar;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return RecyclerViewInstance
     */
    @Override
    public GridView provideManualGridRecyclerView() {
        return fragmentManualModesBinding.recyclerView;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     *
     * @return {@link int}
     */
    @Override
    public int provideLeftIcon() {
        return R.drawable.icon_50px_backarrow;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

}
