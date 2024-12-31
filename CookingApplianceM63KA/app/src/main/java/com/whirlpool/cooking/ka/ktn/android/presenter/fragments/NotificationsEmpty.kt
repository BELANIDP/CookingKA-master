package android.presenter.fragments

import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentNotificationEmptyBinding
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.NavigationUtils

/**
 * File       : [android.presenter.fragments.NotificationsEmpty]
 * Brief      : Implementation [Fragment] class for showing empty Notification center, when forward arrow is clicked from clock view
 * Author     : DUGAMAS.
 * Created On : 24/10/2024
 * Details    :
 */
class NotificationsEmpty : SuperAbstractTimeoutEnableFragment() {
    private var _binding: FragmentNotificationEmptyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationEmptyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            headerBar.apply {
                setTitleText(R.string.text_header_notification)
                setRightIconVisibility(false)
                setRightIcon(R.drawable.ic_placeholder)
                setOvenCavityIconVisibility(false)
                setInfoIconVisibility(false)
                setCustomOnClickListener(object :
                    HeaderBarWidgetInterface.CustomClickListenerInterface {
                    override fun leftIconOnClick() {
                        NavigationUtils.navigateSafely(
                            this@NotificationsEmpty,
                            R.id.action_NotificationEmptyFragment_to_clockFragment,
                            null,
                            null
                        )
                    }
                })
            }
        }
    }
}