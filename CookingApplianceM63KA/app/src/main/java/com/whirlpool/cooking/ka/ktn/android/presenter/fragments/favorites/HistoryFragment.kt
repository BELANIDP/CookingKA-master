package android.presenter.fragments.favorites

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.adapters.favorites.HistoryAdapter
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
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentHistoryBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.cookbook.records.HistoryRecord
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.PRIMARY_CAVITY_KEY
import core.utils.AppConstants.SECONDARY_CAVITY_KEY
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.isCurrentCavityRunning
import core.utils.FavoriteDataHolder
import core.utils.FavoritesPopUpUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.SharedViewModel
import core.utils.gone
import core.utils.visible
import kotlinx.coroutines.launch

/**
 * File       : [android.presenter.fragments.favorites.HistoryFragment]
 * Brief      : Implementation [Fragment] class for cycles history
 * Author     : PANDES18.
 * Created On : 30/09/2024
 * Details    :
 */
class HistoryFragment : SuperAbstractTimeoutEnableFragment(), HMIKnobInteractionListener {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private var currentPosition = -1
    private var lastItemSelectedPos = -1
    private var historyAdapter: HistoryAdapter? = null
    private var isProbeConnectedInUpperCavity = false
    private var isProbeConnectedInLowerCavity = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        observeKnobBackTrace()
        updateIsMeatProbeConnectedBasedOnVariant()
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
                setTitleText(R.string.history)
                setRightIconVisibility(false)
                setInfoIconVisibility(false)
                setOvenCavityIconVisibility(false)
                setCustomOnClickListener(object :
                    HeaderBarWidgetInterface.CustomClickListenerInterface {
                    override fun leftIconOnClick() {
                        NavigationViewModel.popBackStack(
                            Navigation.findNavController(
                                NavigationUtils.getViewSafely(this@HistoryFragment) ?: requireView()
                            )
                        )
                    }
                })
            }
            CookBookViewModel.getInstance().allHistoryRecords.observe(viewLifecycleOwner) { history ->
                if (history.isEmpty()) {
                    textNoHistory.visible()
                } else {
                    textNoHistory.gone()
                    listHistory.apply {
                        val allHistoryRecords =
                            CookBookViewModel.getInstance().allHistoryRecords.value
                        allHistoryRecords?.let {
                            var filteredHistoryList = updateListToRemoveNonProbeCycles(it, isProbeConnectedInUpperCavity, isProbeConnectedInLowerCavity)
                            historyAdapter = HistoryAdapter(
                                filteredHistoryList,
                                requireContext()
                            ) { historyRecord: HistoryRecord ->
                                if (isCurrentCavityRunning(historyRecord.cavity)) {
                                    FavoritesPopUpUtils.showCavityRunningPopup(
                                        this@HistoryFragment,
                                        getRecipeViewModelForCavity(historyRecord.cavity),
                                        historyRecord.cavity
                                    )
                                } else {
                                    CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity
                                    if (historyRecord.cavity == PRIMARY_CAVITY_KEY) {
                                        CookingViewModelFactory.setInScopeViewModel(
                                            CookingViewModelFactory.getPrimaryCavityViewModel()
                                        )
                                    } else {
                                        CookingViewModelFactory.setInScopeViewModel(
                                            CookingViewModelFactory.getSecondaryCavityViewModel()
                                        )
                                    }
                                    CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.load(
                                        historyRecord
                                    )
                                    CookingAppUtils.updateParametersInViewModel(
                                        historyRecord,
                                        CookingAppUtils.getRecipeOptions(),
                                        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                                    )
                                    CookingAppUtils.setNavigatedFrom(AppConstants.NAVIGATION_FROM_CREATE_FAV)
                                    NavigationUtils.navigateSafely(
                                        this@HistoryFragment,
                                        R.id.action_historyFragment_to_historyPreviewFragment,
                                        null,
                                        null
                                    )
                                }
                            }
                            adapter = historyAdapter
                        }
                    }
                }
            }
        }
        FavoriteDataHolder.selectedImageIndex = null
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
        if (isGridView && position in 0 until binding.listHistory.size) {
            binding.listHistory[position].isSelected = true
            notifyGridItemChanged(position)
        } else if (!isGridView) {
            val listPosition = position - binding.listHistory.size
            if (listPosition in 0 until binding.listHistory.size) {
                binding.listHistory[listPosition]
                    .setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
            }
        }
    }

    private fun notifyGridItemChanged(position: Int) {
        binding.listHistory.adapter?.notifyItemChanged(
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
            binding.listHistory[lastItemSelectedPos].callOnClick()
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
                        binding.listHistory.size
                    )
                    if (currentPosition >= 0) {
                        HMILogHelper.Logd("RIGHT_KNOB: rotate right current knob index = $currentPosition")
                        binding.listHistory.smoothScrollToPosition(currentPosition)

                        binding.listHistory.postDelayed({
                            if (lastItemSelectedPos != -1) {
                                val viewHolder = binding.listHistory.findViewHolderForAdapterPosition(lastItemSelectedPos)
                                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
                            }
                            lastItemSelectedPos = currentPosition
                            val viewHolderOld = binding.listHistory.findViewHolderForAdapterPosition(lastItemSelectedPos)
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
                    binding.listHistory.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = -1
        }
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        super.onMeatProbeInsertion(cookingViewModel)
        cookingViewModel?.let {
            if (it.isPrimaryCavity) isProbeConnectedInUpperCavity =
                true else isProbeConnectedInLowerCavity = true
        }
        refreshView()
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        super.onMeatProbeRemoval(cookingViewModel)
        cookingViewModel?.let {
            if (it.isPrimaryCavity) isProbeConnectedInUpperCavity =
                false else isProbeConnectedInLowerCavity = false
        }
        refreshView()
    }

    private fun updateListToRemoveNonProbeCycles(
        listHistoryRecord: MutableList<HistoryRecord>,
        isProbeConnectedInUpperCavity: Boolean,
        isProbeConnectedInLowerCavity: Boolean
    ): MutableList<HistoryRecord> {
        return listHistoryRecord.filter { record ->
            record.cavity?.let { cavityKey ->
                when (cavityKey) {
                    PRIMARY_CAVITY_KEY -> {
                        !isProbeConnectedInUpperCavity ||
                                CookingAppUtils.isProbeRequiredForRecipe(
                                    record.recipeName,
                                    cavityKey
                                )
                    }

                    AppConstants.SECONDARY_CAVITY_KEY -> {
                        !isProbeConnectedInLowerCavity ||
                                CookingAppUtils.isProbeRequiredForRecipe(
                                    record.recipeName,
                                    cavityKey
                                )
                    }

                    else -> true
                }
            } ?: true
        }.toMutableList()
    }

    private fun refreshView() {
        val filteredListForView = CookBookViewModel.getInstance().allHistoryRecords.value?.let {
            updateListToRemoveNonProbeCycles(
                it, isProbeConnectedInUpperCavity, isProbeConnectedInLowerCavity
            )
        }
        if (filteredListForView != null) {
            historyAdapter?.updateList(filteredListForView)
        }
    }

    private fun updateIsMeatProbeConnectedBasedOnVariant(){
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.NONE -> {
                HMILogHelper.Loge("ProductVariant not found")
            }
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                isProbeConnectedInUpperCavity = MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getPrimaryCavityViewModel())
            }
            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                isProbeConnectedInLowerCavity = MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getSecondaryCavityViewModel())
            }
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                isProbeConnectedInUpperCavity = MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getPrimaryCavityViewModel())
                isProbeConnectedInLowerCavity = MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getSecondaryCavityViewModel())
            }
            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {}
        }
    }

    private fun getRecipeViewModelForCavity(cavityKey: String?) : RecipeExecutionViewModel {
        return when(cavityKey) {
            PRIMARY_CAVITY_KEY -> CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel
            SECONDARY_CAVITY_KEY -> CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel
            else -> {
                return CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
            }
        }
    }
    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }
}

