package android.presenter.fragments.favorites

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.adapters.favorites.FavoriteLandingAdapter
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentFavoriteLandingBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.LogHelper.Loge
import com.whirlpool.hmi.utils.cookbook.records.FavoriteRecord
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.NAVIGATION_FROM_CREATE_FAV
import core.utils.AppConstants.NAVIGATION_FROM_EXISTING_FAV
import core.utils.AppConstants.PRIMARY_CAVITY_KEY
import core.utils.AppConstants.SECONDARY_CAVITY_KEY
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.isCurrentCavityRunning
import core.utils.CookingAppUtils.Companion.setNavigatedFrom
import core.utils.FavoriteDataHolder
import core.utils.FavoritesPopUpUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.SharedPreferenceManager
import core.utils.SharedViewModel
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * File       : [android.presenter.fragments.favorites.FavoriteLandingFragment]
 * Brief      : Implementation [Fragment] class for favorite landing screen
 * Author     : PANDES18.
 * Created On : 30/09/2024
 * Details    :
 */
class FavoriteLandingFragment : SuperAbstractTimeoutEnableFragment(), HMIKnobInteractionListener {
    private var _binding: FragmentFavoriteLandingBinding? = null
    private val binding get() = _binding!!
    private var favoriteLandingAdapter: FavoriteLandingAdapter? = null
    private var currentPosition = -1
    private var lastItemSelectedPos = -1
    private var isProbeConnectedInUpperCavity = false
    private var isProbeConnectedInLowerCavity = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteLandingBinding.inflate(inflater, container, false)
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
            highlightNewSelection(lastItemSelectedPos)
        }
        if (KnobNavigationUtils.isBackPress()) {
            KnobNavigationUtils.removeLastAction()
        }
        binding.apply {
            headerBar.apply {
                setTitleText(R.string.text_see_video_favorites)
                setRightIconVisibility(true)
                setRightIcon(R.drawable.ic_add_40)
                setInfoIconVisibility(false)
                setOvenCavityIconVisibility(false)
                setCustomOnClickListener(object :
                    HeaderBarWidgetInterface.CustomClickListenerInterface {
                    override fun leftIconOnClick() {
                        when (FavoriteDataHolder.isSettingsFlow) {
                            true -> navigateToHistoryFragment()
                            false -> navigateToDestinationBasedOnFlow()
                        }
                    }
                    override fun rightIconOnClick() {
                        val favoriteCount = CookBookViewModel.getInstance().favoriteCount
                        when {
                            favoriteCount < AppConstants.MAX_FAVORITE_COUNT -> {
                                setNavigatedFrom(NAVIGATION_FROM_CREATE_FAV)
                                NavigationUtils.navigateSafely(
                                    this@FavoriteLandingFragment,
                                    R.id.action_favoriteLandingFragment_to_favoriteFromFragment,
                                    null,
                                    null
                                )
                            }

                            else -> {
                                FavoritesPopUpUtils.maxFavoriteReached(
                                    parentFragmentManager, this@FavoriteLandingFragment
                                )
                            }
                        }
                    }
                })
            }

            listFavorites.apply {
                CookBookViewModel.getInstance().allFavoriteRecords.observe(viewLifecycleOwner) { allFavoriteRecords ->
                    allFavoriteRecords.sortBy {
                        it.timestamp
                    }
                    allFavoriteRecords.reverse()
                    val filteredListForView  = updateListToRemoveNonProbeCycles(allFavoriteRecords, isProbeConnectedInUpperCavity, isProbeConnectedInLowerCavity)
                    favoriteLandingAdapter = FavoriteLandingAdapter(filteredListForView,
                        viewLifecycleOwner,
                        onItemClick = { favoriteRecord: FavoriteRecord ->
                            if (isCurrentCavityRunning(favoriteRecord.cavity)) {
                                FavoritesPopUpUtils.showCavityRunningPopup(
                                    this@FavoriteLandingFragment,
                                    getRecipeViewModelForCavity(favoriteRecord.cavity),
                                    favoriteRecord.cavity
                                )
                            } else {
                                setNavigatedFrom(NAVIGATION_FROM_EXISTING_FAV)
                                FavoriteDataHolder.favoriteName =
                                    favoriteRecord.favoriteName.toString()
                                FavoriteDataHolder.selectedImageIndex =
                                    FavoritesPopUpUtils.favoritesImageNames.indexOf(favoriteRecord.imageUrl)
                                FavoriteDataHolder.favoriteRecord = favoriteRecord
                                if (favoriteRecord.cavity == PRIMARY_CAVITY_KEY) {
                                    CookingViewModelFactory.setInScopeViewModel(
                                        CookingViewModelFactory.getPrimaryCavityViewModel()
                                    )
                                } else {
                                    CookingViewModelFactory.setInScopeViewModel(
                                        CookingViewModelFactory.getSecondaryCavityViewModel()
                                    )
                                }
                                val errorResponse =
                                    CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.load(
                                        favoriteRecord
                                    )
                                if (errorResponse == RecipeErrorResponse.NO_ERROR) {
                                    CookingAppUtils.updateParametersInViewModel(
                                        favoriteRecord,
                                        CookingAppUtils.getRecipeOptions(),
                                        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                                    )
                                    NavigationUtils.navigateSafely(
                                        this@FavoriteLandingFragment,
                                        R.id.action_favoriteLandingFragment_to_favoritesPreviewFragment,
                                        null,
                                        null
                                    )
                                } else {
                                    Loge("Favorite could not be loaded")
                                }
                            }

                        },
                        onItemLongClick = { favoriteRecord: FavoriteRecord ->
                            val descriptionText =
                                if (favoriteRecord.favoriteName == SharedPreferenceManager.getKnobAssignFavoritesCycleNameIntoPreference()
                                        .toString()
                                ) {
                                    resources.getString(
                                        R.string.text_description_delete_favorite_knob_assigned, favoriteRecord.favoriteName
                                    )
                                } else String.format(
                                    Locale.US,
                                    getString(R.string.text_description_delete_favorite_no_knob_assigned),
                                    "<b>${favoriteRecord.favoriteName}</b>"
                                )

                            FavoritesPopUpUtils.deleteFavorite(this@FavoriteLandingFragment,
                                SpannableStringBuilder().append(Html.fromHtml(descriptionText, Html.FROM_HTML_MODE_LEGACY)),
                                favoriteRecord.favoriteId,
                                { favoriteLandingAdapter?.resetSelectedIItem() }) {
                                if (CookBookViewModel.getInstance().favoriteCount == 0) {
                                    //move to create fav message fragment
                                    NavigationUtils.navigateSafely(
                                        this@FavoriteLandingFragment,
                                        R.id.action_favoriteLandingFragment_to_createNewFavoriteFragment,
                                        null,
                                        NavOptions.Builder()
                                            .setPopUpTo(R.id.favoriteFromFragment, true).build()
                                    )
                                }
                            }
                        })
                    adapter = favoriteLandingAdapter
                }
            }
        }
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
                        highlightNewSelection(lastItemSelectedPos)
                        KnobNavigationUtils.removeLastAction()
                    } else {
                        HMILogHelper.Logd("livedata observer: not navigated form Knob")
                    }
                }
            }
    }

    private fun highlightNewSelection(position: Int) {
        binding.listFavorites.postDelayed({
            lastItemSelectedPos = position
            val viewHolderOld = binding.listFavorites.findViewHolderForAdapterPosition(lastItemSelectedPos)
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }, 50)
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
            binding.listFavorites[lastItemSelectedPos].callOnClick()
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
                        binding.listFavorites.size
                    )
                    if (currentPosition >= 0) {
                        HMILogHelper.Logd("RIGHT_KNOB: rotate right current knob index = $currentPosition")
                        binding.listFavorites.smoothScrollToPosition(currentPosition)

                        binding.listFavorites.postDelayed({
                            if (lastItemSelectedPos != -1) {
                                val viewHolder = binding.listFavorites.findViewHolderForAdapterPosition(lastItemSelectedPos)
                                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.very_dark_grey))
                            }
                            lastItemSelectedPos = currentPosition
                            val viewHolderOld = binding.listFavorites.findViewHolderForAdapterPosition(lastItemSelectedPos)
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
                    binding.listFavorites.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.very_dark_grey))
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

    private fun refreshView() {
        val filteredListForView = CookBookViewModel.getInstance().allFavoriteRecords.value?.let {
            updateListToRemoveNonProbeCycles(
                it, isProbeConnectedInUpperCavity, isProbeConnectedInLowerCavity
            )
        }
        if (filteredListForView != null) {
            favoriteLandingAdapter?.updateList(filteredListForView)
        }
    }

    private fun updateListToRemoveNonProbeCycles(
        listFavoriteRecords: MutableList<FavoriteRecord>,
        isProbeConnectedInUpperCavity: Boolean,
        isProbeConnectedInLowerCavity: Boolean
    ): MutableList<FavoriteRecord> {
        return listFavoriteRecords.filter { record ->
            record.cavity?.let { cavityKey ->
                when (cavityKey) {
                    PRIMARY_CAVITY_KEY -> {
                        !isProbeConnectedInUpperCavity ||
                                CookingAppUtils.isProbeRequiredForRecipe(
                                    record.recipeName,
                                    cavityKey
                                )
                    }

                    SECONDARY_CAVITY_KEY -> {
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

    private fun updateIsMeatProbeConnectedBasedOnVariant() {
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                isProbeConnectedInUpperCavity =
                    MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getPrimaryCavityViewModel())
            }

            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                isProbeConnectedInLowerCavity =
                    MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getSecondaryCavityViewModel())
            }

            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                isProbeConnectedInUpperCavity =
                    MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getPrimaryCavityViewModel())
                isProbeConnectedInLowerCavity =
                    MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getSecondaryCavityViewModel())
            }

            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {}
            else -> {
                HMILogHelper.Loge("ProductVariant not found")
            }
        }
    }
    private fun navigateToHistoryFragment() {
        FavoriteDataHolder.isSettingsFlow = false
        findNavController(this).navigate(
            R.id.action_favoriteLandingFragment_to_HistoryFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.historyFragment, true).build()
        )
    }

    private fun navigateToDestinationBasedOnFlow() {
        if (FavoriteDataHolder.isProbeFlow) {
            FavoriteDataHolder.isProbeFlow = false
            navigateSafely(
                this,
                R.id.action_to_probeCyclesSelectionFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.probeCyclesGridFragment, true).build()
            )
            return
        }
        if (FavoriteDataHolder.isNotificationFlow) {
            FavoriteDataHolder.isNotificationFlow = false
            findNavController(this).navigate(R.id.global_action_to_clockScreen)
        } else {
            findNavController(this).navigate(R.id.action_favoriteLandingFragment_to_recipeSelectionFragment)
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

