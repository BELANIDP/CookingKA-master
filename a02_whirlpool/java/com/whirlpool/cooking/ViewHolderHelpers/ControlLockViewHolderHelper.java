package com.whirlpool.cooking.ViewHolderHelpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whirlpool.cooking.base.AbstractViewHolders.AbstractControlLockViewHolder;
import com.whirlpool.cooking.databinding.FragmentControlLockBinding;

public class ControlLockViewHolderHelper extends AbstractControlLockViewHolder {

    private FragmentControlLockBinding fragmentControlLockBinding;

    // ================================================================================================================
    // -----------------------------------------  General Methods Definitions  ----------------------------------------
    // ================================================================================================================

    /**
     * Inflate the customized view
     *
     * @param inflater {@link LayoutInflater}
     * @param container {@link ViewGroup}
     * @param savedInstanceState {@link Bundle}
     * @return {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentControlLockBinding = fragmentControlLockBinding.inflate(inflater, container, false);
        return fragmentControlLockBinding.getRoot();    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Clean up the view holder when it is destroyed
     */
    @Override
    public void onDestroyView() {
        fragmentControlLockBinding = null;
    }

    /*---------------------------------------------------------------------------------------------------------------*/

    /**
     * Provides the binding class
     *
     * @return {@link FragmentControlLockBinding}
     */
    @Override
    public FragmentControlLockBinding getFragmentControlLockBinding() {
        return fragmentControlLockBinding;
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/


    // ================================================================================================================
    // ------------------------------------------  Common Views Definitions  ------------------------------------------
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/


    // ================================================================================================================
    // -------------------------------------  Modality Specific Views Definitions -------------------------------------
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/
}
