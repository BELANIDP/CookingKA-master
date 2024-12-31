package android.presenter.fragments.favorites

import android.os.Bundle
import android.presenter.basefragments.AbstractPreviewFragment
import android.presenter.customviews.widgets.preview.PreviewTileItem
import android.text.TextUtils
import android.view.View
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.model.capability.recipe.options.DoubleRange
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants
import core.utils.AppConstants.EMPTY_STRING
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getRecipeOptions
import core.utils.CookingAppUtils.Companion.setNavigatedFrom
import core.utils.DoorEventUtils
import core.utils.FavoritesPopUpUtils
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.ToolsMenuJsonKeys
import java.util.Locale

/**
 * File       : [android.presenter.fragments.favorites.HistoryPreviewFragment]
 * Brief      : Preview screen for favorite preview from the history
 * Author     : PANDES18
 * Created On : 04/10/2024
 * Details    : To show favorite recipe selection tile list
 */
class HistoryPreviewFragment : AbstractPreviewFragment() {

    private val listOfTiles = arrayListOf<PreviewTileItem>()
    private lateinit var recipeExecutionViewModel: RecipeExecutionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeExecutionViewModel =
            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
    }

    override fun provideRecyclerViewTilesData(): ArrayList<PreviewTileItem> {
        return generateFavoritesRecipeOptionTiles()
    }

    private fun generateFavoritesRecipeOptionTiles(): ArrayList<PreviewTileItem> {
        listOfTiles.clear()
        val requiredOptions = getRecipeOptions().toMutableList()

        if (RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE in requiredOptions) {
            // move meat probe temperature to first
            requiredOptions.remove(RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE)
            requiredOptions.add(0, RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE)
        }
        listOfTiles.add(
            PreviewTileItem(
                "Mode",
                provideRecipeModeText().uppercase(Locale.ROOT),
                RecipeOptions.DONENESS,
                PreviewTileItem.TileType.NORMAL_TILE
            )
        )
        for (option in requiredOptions) {
            when (option) {

                RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> {
                    val temperature =
                        CookingAppUtils.displayProbeTemperatureToUser(
                            requireContext(),
                            recipeExecutionViewModel
                        )
                    listOfTiles.add(
                        PreviewTileItem(
                            getString(R.string.text_subHeader_probeTemp),
                            temperature.uppercase(Locale.ROOT),
                            RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE,
                            PreviewTileItem.TileType.NORMAL_TILE
                        )
                    )
                }

                RecipeOptions.TARGET_TEMPERATURE -> {
                    val temperature =
                        CookingAppUtils.displayTemperatureToUser(
                            requireContext(),
                            recipeExecutionViewModel
                        )
                    listOfTiles.add(
                        PreviewTileItem(
                            getString(R.string.text_subHeader_ovenTemp),
                            temperature.uppercase(Locale.ROOT),
                            RecipeOptions.TARGET_TEMPERATURE,
                            PreviewTileItem.TileType.NORMAL_TILE
                        )
                    )
                }

                RecipeOptions.DONENESS -> {
                    val doneNessValue = requireContext().getString(
                        CookingAppUtils.getResIdFromResName(
                            context,
                            AppConstants.TEXT_DONENESS_TILE + recipeExecutionViewModel.donenessOption.value?.defaultString,
                            ToolsMenuJsonKeys.RESOURCE_TYPE_STRING
                        )
                    ).uppercase(Locale.ROOT)

                    listOfTiles.add(
                        PreviewTileItem(
                            getString(R.string.text_header_doneness),
                            doneNessValue,
                            RecipeOptions.DONENESS,
                            PreviewTileItem.TileType.NORMAL_TILE
                        )
                    )
                }

                RecipeOptions.WEIGHT -> {
                    listOfTiles.add(
                        PreviewTileItem(
                            getString(R.string.text_header_weight),
                            CookingAppUtils.displayWeightToUser(
                                requireContext(),
                                recipeExecutionViewModel.weight.value,
                                recipeExecutionViewModel.weightOption.value?.displayUnits
                            ).uppercase(Locale.ROOT),
                            RecipeOptions.WEIGHT,
                            PreviewTileItem.TileType.NORMAL_TILE
                        )
                    )
                }

                RecipeOptions.COOK_TIME -> {
                    val cookTime = CookingAppUtils.displayCookTimeToUser(
                        requireContext(),
                        recipeExecutionViewModel.cookTime.value,
                        true,
                        arrayOf(R.string.text_label_HR, R.string.text_label_MIN, R.string.text_label_SEC
                        )
                    )
                    if (!TextUtils.isEmpty(cookTime)) {
                        listOfTiles.add(
                            PreviewTileItem(
                                getString(R.string.text_time),
                                cookTime,
                                RecipeOptions.COOK_TIME,
                                PreviewTileItem.TileType.NORMAL_TILE
                            )
                        )
                    }
                }

                RecipeOptions.MWO_POWER_LEVEL -> {
                    val mwoPowerLevel =
                        recipeExecutionViewModel.mwoPowerLevel.value.toString() + "%"
                    listOfTiles.add(
                        PreviewTileItem(
                            getString(R.string.text_header_power),
                            mwoPowerLevel.uppercase(Locale.ROOT),
                            RecipeOptions.MWO_POWER_LEVEL,
                            PreviewTileItem.TileType.NORMAL_TILE
                        )
                    )
                }

                RecipeOptions.AMOUNT -> {
                    val amountValue =
                        (recipeExecutionViewModel.amount.value as? Number)?.toDouble() ?: 0.0
                    val formattedAmount = if (amountValue == amountValue.toInt().toDouble()) {
                        amountValue.toInt().toString()
                    } else {
                        "%.1f".format(amountValue)
                    }

                    val resourceString = requireContext().getString(
                        CookingAppUtils.getResIdFromResName(
                            context,
                            AppConstants.TEXT_DONENESS_TILE + (recipeExecutionViewModel.amountOption.value as DoubleRange).displayUnits,
                            ToolsMenuJsonKeys.RESOURCE_TYPE_STRING
                        )
                    )

                    val amountToDisplay = String.format(resourceString, formattedAmount)
                        .uppercase(Locale.ROOT)

                    listOfTiles.add(
                        PreviewTileItem(
                            getString(R.string.text_header_amount),
                            amountToDisplay,
                            RecipeOptions.AMOUNT,
                            PreviewTileItem.TileType.NORMAL_TILE
                        )
                    )
                }

                else -> {
                    HMILogHelper.Loge(
                        tag,
                        "given recipeOption $option not available to load tile"
                    )
                }
            }
        }

        listOfTiles[listOfTiles.lastIndex].isDividerHidden = true
        return listOfTiles
    }

    /**
     * provide text to show on the header bar title
     * @return string to show as title
     */
    override fun provideHeaderBarTitleText(): String =
        CookingAppUtils.getRecipeNameText(requireContext(), recipeExecutionViewModel.recipeName.value.toString())

    override fun provideHeaderBarLeftIconVisibility() = true

    /**
     * provide text to show on the tile 1
     * @return string to show as title
     */
    private fun provideRecipeModeText(): String {
        return CookingAppUtils.getHeaderTitleAsRecipeName(
            context,
            cookingViewModel.recipeExecutionViewModel.recipeName.value
        )
    }

    /**
     * provide visibility of info icon
     * @return true if VISIBLE false if GONE
     */
    override fun provideHeaderBarInfoIconVisibility() = false


    override fun infoIconOnClick() {
    }

    /**
     * provide visibility of left action button, ex delay not applicable to mwo cycles
     * @return true if VISIBLE false if GONE
     */
    override fun provideLeftActionButtonVisibility() = true

    override fun provideInfoIconRes(): Int? {
        return null
    }

    /**
     * Handle click event when Right button is pressed
     */
    override fun providePrimaryActionButtonClickEvent() {
        setNavigatedFrom(EMPTY_STRING)
        //for Microwave recipes
        if (cookingViewModel.isOfTypeMicrowaveOven) {
            DoorEventUtils.startMicrowaveRecipeOrShowPopup(
                this, cookingViewModel
            )
            return
        }

        //for probe based cycles
        if (recipeExecutionViewModel.isProbeBasedRecipe) {
            if (MeatProbeUtils.isMeatProbeConnected(cookingViewModel))
                startProbeRecipe()
            else {
                onStop()
                updateTimeoutValue(
                    TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_STARTED,
                    AppConstants.TIME_OUT_STOP
                )
                PopUpBuilderUtils.insertMeatProbe(
                    this,
                    cookingViewModel,
                    onMeatProbeConditionMet = {
                        // once user insert the probe then reset the timeout to the default provided time
                        updateTimeoutValue(
                            TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_STARTED,
                            provideScreenTimeoutValueInSeconds()
                        )
                        startProbeRecipe()
                    })
            }
            return
        }

        //User Loaded already existing record from Favorite Landing to Run a favorite Recipe
        val recipeExecuteErrorResponse =
            cookingViewModel.recipeExecutionViewModel.execute()
        if (recipeExecuteErrorResponse.isError) {
            CookingAppUtils.handleCookingError(
                this, cookingViewModel, recipeExecuteErrorResponse, false
            )
            return
        }
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }


    /**
     * provide the txt to display on button right/primary
     * @return text to show on right action button like start
     */
    override fun providePrimaryActionButtonText() = getString(R.string.text_button_start)


    /**
     * Handle click event when Left button is pressed for ex. start or next
     */
    override fun provideLeftActionButtonClickEvent() {
        if (CookBookViewModel.getInstance().favoriteCount < AppConstants.MAX_FAVORITE_COUNT) {
            NavigationUtils.navigateSafely(
                this,
                R.id.action_historyPreviewFragment_to_favoritesPreviewFragment,
                null,
                null
            )
        }else{
            FavoritesPopUpUtils.maxFavoriteReached(
                activity?.supportFragmentManager,
                this
            )
        }
    }


    /**
     * provide the txt to display on button left
     * @return text to show on left action button like delay, cancel
     */
    override fun provideLeftActionButtonText() = getString(R.string.text_moreOptions_favorite)

    /**
     * Handles user click on recipe attribute tiles
     * @param: view-Current View ; position: Index of tile being clicked
     */
    override fun onPreviewTileClick(view: View, position: Int) {
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        /* Use Case 1:
            When the user selects the upper cavity and then inserts the probe in the upper cavity on the preview screen,
            the probe insertion dialog should not be shown.
           Use Case 2:
            When the user selects the upper cavity and then inserts the probe in the lower cavity on the preview screen,
            the probe insertion dialog should be shown.
         */

        val existingCookingViewModel = CookingViewModelFactory.getInScopeViewModel()
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                if (cookingViewModel?.cavityName?.value?.contentEquals(
                        existingCookingViewModel?.cavityName?.value
                    ) == false
                ) {
                    HMILogHelper.Logd("TEST_", "Other cavity probe inserted show dialog")
                    PopUpBuilderUtils.probeDetectedInOtherCavityMidWayRecipeRunning(
                        this,
                        cookingViewModel,
                        CookingViewModelFactory.getInScopeViewModel()
                    )
                } else {
                    HMILogHelper.Logd("TEST_", "Same cavity probe inserted ignore dialog")
                }
            }

            else -> {
                HMILogHelper.Logd("TEST_", "Same cavity probe inserted ignore dialog")
            }
        }
    }

    private fun startProbeRecipe() {
        val recipeExecuteErrorResponse =
            cookingViewModel.recipeExecutionViewModel.execute()
        HMILogHelper.Logd(
            tag,
            "start recipe from assistedPreview ${recipeExecuteErrorResponse.description}"
        )
        if (recipeExecuteErrorResponse.isError) {
            CookingAppUtils.handleCookingError(
                this,
                cookingViewModel,
                recipeExecuteErrorResponse,
                false,
                onMeatProbeConditionMet = { startProbeRecipe() }
            )
            return
        }
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }
}