package android.presenter.fragments.assisted

import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.jbase.abstractViewHolders.AbstractCookingGuideViewHolder
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.NEXT_BUTTON
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.viewHolderHelpers.AssistedCookingGuideFragmentViewProvider

/**
 * File        : android.presenter.fragments.assisted.AssistedCookingGuideFragment
 * Brief       : Assisted Cooking guide Popup to inflate as Fragment in Day1 scenario
 * Author      : Hiren
 * Created On  : 05/20/2024
 * Details     : User can scroll to see the information of cooking guide with serving tips
 */
class AssistedCookingGuideFragment: SuperAbstractTimeoutEnableFragment() {
    private var viewProvider : AbstractCookingGuideViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewProvider = AssistedCookingGuideFragmentViewProvider(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return viewProvider?.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewProvider?.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        updateHeader()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        CookingAppUtils.clearOrEraseCookingGuideList()
        viewProvider?.onDestroyView()
        viewProvider = null
    }

    private fun updateHeader(){
        viewProvider?.provideHeaderView()?.apply {
            val navigatedFrom = arguments?.getString(BundleKeys.BUNDLE_NAVIGATED_FROM)
            if (NEXT_BUTTON != navigatedFrom) {
                getLeftImageView()?.visibility = View.GONE
                getRightImageView()?.apply {
                    setOnClickListener {
                        AudioManagerUtils.playOneShotSound(
                            view?.context,
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        NavigationViewModel.popBackStack(findNavController())
                    }
                }
            } else {
                getLeftImageView()?.apply {
                    setOnClickListener {
                        AudioManagerUtils.playOneShotSound(
                            view?.context,
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        NavigationViewModel.popBackStack(findNavController())
                    }
                }
                getRightImageView()?.visibility = View.GONE
            }
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
        }
    }
}