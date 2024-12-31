package android.presenter.fragments.favorites

import android.os.Bundle
import android.presenter.basefragments.AbstractStringTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringItemClickInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringTumblerItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.TreeNode
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.AppConstants.RECIPE_SLOW_ROAST
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.rotateTumblerOnKnobEvents
import core.utils.FavoriteDataHolder
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.SharedViewModel
import core.utils.ToastUtils
import core.utils.gone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.function.Consumer


/**
 * File       : [android.presenter.fragments.favorites.FavoriteRecipeSelectionFragment]
 * Brief      : Implementation of [AbstractStringTumblerFragment] class to create new favorite options
 * Author     : PANDES18.
 * Created On : 30/09/2024
 * Details    :
 */

class FavoriteRecipeSelectionFragment : AbstractStringTumblerFragment(),
    TextStringItemClickInterface {

    private lateinit var manualModeList: ArrayList<String>
    protected var inScopeViewModel: CookingViewModel? = null
    private var defaultRecipeName = ""

    override fun initTumbler() {
        initTemperatureTumbler()
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return 0
    }

    /**
     * set primaryCavityViewModel according to the the selected product variant
     */
    override fun setCavityViewModelByProductVariant() {
        inScopeViewModel = CookingViewModelFactory.getInScopeViewModel()
        defaultRecipeName = getDefaultRecipeName(inScopeViewModel, isForFavorite = true)
    }

    /**
     * load the json data for the tumbler against pyro
     */
    override fun setTumblerStringTempData() {
        val rootNode = CookBookViewModel.getInstance()
            .getManualRecipesPresentationTreeFor(inScopeViewModel?.cavityName?.value)
        manualModeList = ArrayList()
        if (rootNode != null && rootNode.children.isNotEmpty()) {
            manualModeList.add(getString(R.string.text_see_video_assisted_cooking))
            rootNode.children.forEach(Consumer { treeNode: TreeNode<String> ->
                val cycleNames = treeNode.data
                if (cycleNames != AppConstants.EMPTY_STRING && cycleNames != AppConstants.QUICK_START) {
                    manualModeList.add(cycleNames)
                    tumblerViewHolderHelper?.provideNumericTumbler()?.itemViewHolder =
                        TextStringTumblerItem(cycleNames, this, font = R.font.roboto_light)

                }
            })
            lifecycleScope.launch {
                setListObject()
            }
        }
    }

    /*
* Set tumbler data
* */
    private suspend fun setListObject() {
        withContext(Dispatchers.IO) {
            val recipeTumblerList: ViewModelListInterface =
                getRecipeList(manualModeList)
            tumblerViewHolderHelper?.provideNumericTumbler()
                ?.setListObject(
                    recipeTumblerList,
                    defaultRecipeName,
                    true
                )
        }

        withContext(Dispatchers.Main) {
            tumblerViewHolderHelper?.provideNumericTumbler()?.smoothScrollToPosition(
                manualModeList.indexOf(defaultRecipeName),
                AppConstants.SMOOTH_SCROLL_ANIM_DELAY
            )
        }
    }

    override fun onStart() {
        super.onStart()
        CookingAppUtils.checkForActiveFaults(this)
    }


    /**
     * View model interface to interact in the tumbler view based on the temperature range value
     * @param tumblerDataValueList      tumbler Data list
     * @return ViewModelListInterface
     */
    private fun getRecipeList(
        tumblerDataValueList: ArrayList<String>
    ): ViewModelListInterface {
        return object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                return tumblerDataValueList
            }

            override fun getDefaultString(): String {
                return defaultRecipeName
            }

            override fun getValue(index: Int): Any {
                return tumblerDataValueList[index]
            }

            override fun isValid(value: Any): Boolean {
                return tumblerDataValueList.contains(value.toString())
            }
        }
    }


    /**
     * set the header bar widget data
     */
    override fun setHeaderBarViews() {
        setViewByProductVariant()
    }

    /**
     * set the header bar widget data according to the product variant
     */
    private fun setViewByProductVariant() {
        tumblerViewHolderHelper?.apply {
            provideHeaderBarWidget()?.apply {
                setInfoIconVisibility(false)
                setRightIconVisibility(false)
                setLeftIconVisibility(true)
                setOvenCavityIconVisibility(false)
                setTitleText(R.string.text_header_create_favorite)
                setCustomOnClickListener(object :
                    HeaderBarWidgetInterface.CustomClickListenerInterface {
                    override fun leftIconOnClick() {
                        //Fix for bug M63KA-2797, To reinitialize recipeExecutionViewModel when user
                        // is going back from editing a mode for a saved favorite.
                        if (FavoriteDataHolder.favoriteRecord.favoriteName?.isNotEmpty() == true) {
                            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.load(
                                FavoriteDataHolder.favoriteRecord
                            )
                            CookingAppUtils.updateParametersInViewModel(
                                FavoriteDataHolder.favoriteRecord,
                                CookingAppUtils.getRecipeOptions(),
                                CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                            )
                        }
                        super.leftIconOnClick()
                        NavigationViewModel.popBackStack(findNavController())
                    }
                })
            }

            provideGhostButton()?.gone()
            providePrimaryButton()?.gone()
            provideGhostImageView()?.gone()
            providePrimaryImageView()?.gone()

            provideMainImageBackgroundWidget()?.apply {
                visibility = View.VISIBLE
                background = AppCompatResources.getDrawable(requireContext(), R.drawable.background)
            }
        }
    }

    override fun onItemClick(index: Int, isKnobClick: Boolean) {
        val recipeName = manualModeList[index]
        SharedViewModel.getSharedViewModel(this.requireActivity()).setCurrentRecipeBeingProgrammed(recipeName)
        when (recipeName.lowercase()) {
            getString(R.string.text_see_video_assisted_cooking).lowercase() -> {
                HMILogHelper.Logd(
                    tag,
                    "navigating to action_recipeSelection_to_assistedMainCategory"
                )
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_favoriteRecipeSelectionFragment_to_favoritesFoodMainCategoryGridFragment,
                    null,
                    null
                )
            }

            AppConstants.RECIPE_PROBE,
            AppConstants.RECIPE_MORE_MODES.lowercase(),
            AppConstants.RECIPE_CONVECT -> {
                CookingAppUtils.navigateToSubChildRecipes(
                    this,
                    recipeName, Bundle()
                )
            }

            RECIPE_SLOW_ROAST -> {
                ToastUtils.showToast(requireContext(), "Under Development")
            }

            else -> {
                inScopeViewModel?.let {
                    NavigationUtils.navigateAfterFavoriteSelection(
                        this,
                        it,
                        recipeName,
                        isKnobClick
                    )
                }
            }
        }
    }

    override fun onHMIRightKnobClick() {
        onItemClick(tumblerViewHolderHelper?.provideNumericTumbler()?.selectedIndex ?: 0, true)
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            val tumbler = tumblerViewHolderHelper?.provideNumericTumbler() ?: return
            rotateTumblerOnKnobEvents(this, tumbler, knobDirection)
        }
    }
}