package android.presenter.fragments.favorites

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.adapters.favorites.FavoritesFromAdapter
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentFavoritesFromBinding
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.FavoriteDataHolder
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.SharedViewModel
import kotlinx.coroutines.launch

/**
 * File       : [android.presenter.fragments.favorites.FavoriteFromFragment]
 * Brief      : Implementation [Fragment] class for create new favorite options
 * Author     : PANDES18.
 * Created On : 30/09/2024
 * Details    :
 */
class FavoriteFromFragment : SuperAbstractTimeoutEnableFragment(), HMIKnobInteractionListener {
    private var _binding: FragmentFavoritesFromBinding? = null
    private val binding get() = _binding!!
    private var currentPosition = -1
    private var lastItemSelectedPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesFromBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        observeKnobBackTrace()
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highlightNewSelection(lastItemSelectedPos, true)
        }
        if (KnobNavigationUtils.isBackPress()) {
            KnobNavigationUtils.removeLastAction()
        }
        binding.apply {
            headerBar.apply {
                setTitleText(R.string.text_see_video_favorites)
                setRightIconVisibility(false)
                setInfoIconVisibility(false)
                setOvenCavityIconVisibility(false)
                setCustomOnClickListener(object :
                    HeaderBarWidgetInterface.CustomClickListenerInterface {
                    override fun leftIconOnClick() {
                        NavigationViewModel.popBackStack(
                            Navigation.findNavController(
                                NavigationUtils.getViewSafely(this@FavoriteFromFragment)
                                    ?: requireView()
                            )
                        )
                    }
                })
            }
            listFavoritesFrom.apply {
                layoutManager = LinearLayoutManager(this@FavoriteFromFragment.requireContext())
                adapter = FavoritesFromAdapter(getFavoriteFromList()) {
                    when (it) {
                        getString(R.string.text_options_manualModes) -> {
                            NavigationUtils.navigateSafely(
                                this@FavoriteFromFragment,
                                R.id.action_favoriteFromFragment_to_favoriteRecipeSelectionFragment,
                                null,
                                null
                            )
                        }

                        getString(R.string.text_option_autoCook) -> {
                            NavigationUtils.navigateSafely(
                                this@FavoriteFromFragment,
                                R.id.action_favoriteFromFragment_to_assisted_food_main_category,
                                null,
                                null
                            )
                        }

                        getString(R.string.history) -> {
                            NavigationUtils.navigateSafely(
                                this@FavoriteFromFragment,
                                R.id.action_favoriteFromFragment_to_historyFragment,
                                null,
                                null
                            )
                        }
                    }
                }
            }
        }
        FavoriteDataHolder.selectedImageIndex = null
    }

    private fun getFavoriteFromList(): List<String> {
        return listOf(
            getString(R.string.text_options_manualModes),
            getString(R.string.text_option_autoCook),
            getString(R.string.history)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }

    private fun observeKnobBackTrace() {
        SharedViewModel.getSharedViewModel(this.requireActivity()).isNavigatedFromKnobClick()
            .observe(viewLifecycleOwner) { value ->
                value?.let { navigated ->
                    if (navigated && KnobNavigationUtils.knobBackTrace) {
                        // Log the last action if available
                        HMILogHelper.Logd("last saved action: ${KnobNavigationUtils.lastTimeSelectedData()}")
                        KnobNavigationUtils.knobBackTrace = false
                        currentPosition = KnobNavigationUtils.lastTimeSelectedData()
                        lastItemSelectedPos = KnobNavigationUtils.lastTimeSelectedData()
                        highlightNewSelection(lastItemSelectedPos, true)
                        KnobNavigationUtils.removeLastAction()
                    } else {
                        HMILogHelper.Logd("livedata observer: not navigated form Knob")
                    }
                }
            }
    }

    private fun highlightNewSelection(position: Int, isGridView: Boolean) {
        if (isGridView && position in 0 until binding.listFavoritesFrom.size) {
            binding.listFavoritesFrom[position].isSelected = true
            notifyGridItemChanged(position)
        } else if (!isGridView) {
            val listPosition = position - binding.listFavoritesFrom.size
            if (listPosition in 0 until binding.listFavoritesFrom.size) {
                binding.listFavoritesFrom[listPosition]
                    .setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
            }
        }
    }

    private fun notifyGridItemChanged(position: Int) {
        binding.listFavoritesFrom.adapter?.notifyItemChanged(
            position
        )
    }

    override fun onHMILeftKnobClick() {
        // do nothing
    }

    override fun onHMILongLeftKnobPress() {
        // do nothing
    }

    override fun onHMIRightKnobClick() {
        if (lastItemSelectedPos != -1) {
            KnobNavigationUtils.knobForwardTrace = true
            binding.listFavoritesFrom[lastItemSelectedPos].callOnClick()
        }
    }

    override fun onHMILongRightKnobPress() {
        //do nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        // do nothing
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        // do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (knobId == AppConstants.RIGHT_KNOB_ID) {
                    currentPosition = CookingAppUtils.getKnobPositionIndex(
                        knobDirection,
                        currentPosition,
                        binding.listFavoritesFrom.size
                    )
                    if (currentPosition >= 0) {
                        HMILogHelper.Logd("RIGHT_KNOB: rotate right current knob index = $currentPosition")
                        binding.listFavoritesFrom.smoothScrollToPosition(currentPosition)

                        binding.listFavoritesFrom.postDelayed({
                            if (lastItemSelectedPos != -1) {
                                val viewHolder = binding.listFavoritesFrom.findViewHolderForAdapterPosition(lastItemSelectedPos)
                                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
                            }
                            lastItemSelectedPos = currentPosition
                            val viewHolderOld = binding.listFavoritesFrom.findViewHolderForAdapterPosition(lastItemSelectedPos)
                            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
                        }, 50) // Adjust delay as needed

                    }else{
                        HMILogHelper.Logd("RIGHT_KNOB: rotate left current knob index = $currentPosition")
                        currentPosition = 0
                    }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            if (lastItemSelectedPos != -1) {
                val viewHolder =
                    binding.listFavoritesFrom.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = -1
        }
    }
}


