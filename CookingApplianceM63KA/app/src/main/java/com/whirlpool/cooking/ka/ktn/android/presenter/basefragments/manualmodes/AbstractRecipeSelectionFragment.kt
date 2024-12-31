/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.basefragments.manualmodes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import com.whirlpool.cooking.ka.databinding.FragmentRecipeSelectionBinding
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.CavityStateUtils

/**
 * File       : android.presenter.basefragments.manualmodes.AbstractRecipeSelectionFragment.
 * Brief      : Abstract class for Recipe selection screen for manual modes.
 * Author     : BHIMAR.
 * Created On : 14/03/2024
 * Details    :
 */
abstract class AbstractRecipeSelectionFragment : SuperAbstractTimeoutEnableFragment() {
    /**
     * To binding Fragment variables
     */
    protected var binding: FragmentRecipeSelectionBinding? = null
        private set

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeSelectionBinding.inflate(inflater)
        binding?.lifecycleOwner = this
        binding?.recipeSelectionKtn = this
        return binding?.root
    }
    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }
}