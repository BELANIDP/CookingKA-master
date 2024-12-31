package android.presenter.fragments.digital_unboxing

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentDevicePairInformationBinding
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils
import core.utils.PopUpBuilderUtils

/**
 * File       : android.presenter.fragments.digital_unboxing.UnboxingConnectToNetworkFragment
 * Brief      : fragment to connect to network
 * Author     : Rajendra
 * Created On : 10-05-2024
 */
class UnBoxingConnectToNetworkFragment : Fragment(), HMIKnobInteractionListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface {
    private var viewBinding: FragmentDevicePairInformationBinding? = null

    //Knob Implementation
    private var knobRotationCount = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = inflater.let {
            FragmentDevicePairInformationBinding.inflate(
                it
            )
        }
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        manageChildViews()
        viewBinding?.textViewPin?.visibility = View.GONE
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            knobRotationCount = 1
            viewBinding?.navigationButtonLeft?.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.selector_textview_walnut, null
                )
            }
        }
    }

    /**
     * Method to manage Views of the Fragment
     */
    private fun manageChildViews() {
        manageHeaderBar()
        manageNavigationButtons()
        updateNetworkIconTitleBody()
    }


    /**
     * Method to manage header bar View of the Fragment
     */
    private fun manageHeaderBar() {
        viewBinding?.headerBar?.apply {
            setTitleText(R.string.connect_to_network)
            setLeftIconVisibility(true)
            viewBinding?.headerBar?.setLeftIcon(R.drawable.ic_back_arrow)
            setInfoIconVisibility(false)
            setRightIconVisibility(false)
            setOvenCavityIconVisibility(false)
            setCustomOnClickListener(this@UnBoxingConnectToNetworkFragment)
        }
    }

    /**
     * Method to manage NAVIGATION Buttons of the Fragment
     */
    private fun manageNavigationButtons() {
        viewBinding?.navigationButtonRight?.visibility = View.VISIBLE
        viewBinding?.navigationButtonRight?.text = resources.getString(R.string.text_button_connectNow)
        viewBinding?.navigationButtonLeft?.visibility = View.VISIBLE
        viewBinding?.navigationButtonLeft?.text = resources.getString(R.string.text_button_connectLater)
        viewBinding?.navigationButtonRight?.setOnClickListener {
            HMILogHelper.Logd(TAG, "Unboxing: navigate to the provisioning flow")
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.start_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            CookingAppUtils.startProvisioning(NavigationUtils.getVisibleFragment()?.let {
                NavigationUtils.getViewSafely(
                    it
                )
            }, false, isFromConnectivityScreen = false, false)
        }
        viewBinding?.navigationButtonLeft?.setOnClickListener {
            HMILogHelper.Logd(TAG, "Unboxing: navigate to the regional settings flow")
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
           //For smooth transition between fragment we have added navOption with anim parameter
            val navOptions = NavOptions
                .Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .build()
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                NavigationUtils.navigateSafely(
                    it,
                    R.id.action_unboxingConnectToNetworkFragment_to_unboxingRegionalSettingsFragment,
                    null,
                    navOptions
                )
            }
        }
    }

    override fun leftIconOnClick() {
        //send back result to previous fragment - explore features
        this.parentFragmentManager.setFragmentResult(
            BundleKeys.BUNDLE_EXTRA_COMING_FROM_CONNECT_NETWORK_FLOW,
            bundleOf(BundleKeys.BUNDLE_EXTRA_COMING_FROM_CONNECT_NETWORK_FLOW to true)
        )
        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
            NavigationUtils.navigateSafely(
                it, R.id.action_unboxingConnectToNetworkFragment_to_unboxingExploreFeaturesInfoFragment, null, null
            )
        }
    }
    /**
     * Method to manage QR code Image of the Fragment
     */
    private fun updateNetworkIconTitleBody() {
        viewBinding?.apply {
            pinInfo.visibility = View.GONE
            qrCode.visibility = View.VISIBLE
            qrCode.setBackgroundResource(R.drawable.icon_connect_network)
            viewBinding?.textViewSAID?.visibility = View.GONE
            val param = (textViewDescription.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(
                    resources.getInteger(R.integer.integer_range_0),
                    resources.getInteger(R.integer.integer_range_0),
                    resources.getInteger(R.integer.integer_range_0),
                    resources.getInteger(R.integer.integer_range_0)
                )
            }
            textViewDescription.layoutParams = param
            textViewDescription.text =
                resources.getString(R.string.text_description_connect_to_network)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        viewBinding = null
    }

    override fun onHMILeftKnobClick() {
        KnobNavigationUtils.knobForwardTrace = true
        when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> {
                viewBinding?.navigationButtonLeft?.callOnClick()
            }

            AppConstants.KNOB_COUNTER_TWO -> {
                viewBinding?.navigationButtonRight?.callOnClick()
            }
        }
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobClick() {
        PopUpBuilderUtils.userLeftKnobWarningPopup(this)
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    viewBinding?.navigationButtonRight?.background = null
                    viewBinding?.navigationButtonLeft?.background = resources.let {
                        ResourcesCompat.getDrawable(
                            it, R.drawable.selector_textview_walnut, null
                        )
                    }
                }

                AppConstants.KNOB_COUNTER_TWO -> {
                    viewBinding?.navigationButtonLeft?.background = null
                    viewBinding?.navigationButtonRight?.background = resources.let {
                        ResourcesCompat.getDrawable(
                            it, R.drawable.selector_textview_walnut, null
                        )
                    }
                }
            }
        } else if (knobId == AppConstants.RIGHT_KNOB_ID) {
            PopUpBuilderUtils.userLeftKnobWarningPopup(this)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        knobRotationCount = 0
        viewBinding?.navigationButtonLeft?.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
        viewBinding?.navigationButtonRight?.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
    }

    companion object {
        private val TAG: String = UnBoxingConnectToNetworkFragment::class.java.simpleName
    }
}
