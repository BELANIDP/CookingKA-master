package android.presenter.fragments.assisted

import android.media.AudioManager
import android.os.Bundle
import android.presenter.basefragments.AbstractPreviewFragment
import android.presenter.customviews.widgets.preview.PreviewTileItem
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.model.capability.recipe.options.DoubleRange
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AppConstants.NEXT_BUTTON
import core.utils.AudioManagerUtils
import core.utils.BundleKeys.Companion.BUNDLE_NAVIGATED_FROM
import core.utils.CookingAppUtils
import core.utils.DoorEventUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils
import core.utils.ToolsMenuJsonKeys
import java.util.Locale

/**
 * File       : android.presenter.fragments.assisted.AssistedPreviewFragment
 * Brief      : Preview screen for assisted recipe selection
 * Author     : Hiren
 * Created On : 05/13/2024
 * Details    : To show assisted recipe selection tile list
 */
class AssistedPreviewFragment : AbstractPreviewFragment() {

    private val listOfTiles = arrayListOf<PreviewTileItem>()

    /**
     * show cooking guide based on Assisted day1 and day2 behaviour
     */
    private var showCookingGuide : Boolean = false

    /**
     * Method for setting the decision tiles data.
     * To be overridden  by derived class to update the recycler view data based on the use case
     *
     * @return ArrayList<DecisionTileData>
     */
    override fun provideRecyclerViewTilesData(): ArrayList<PreviewTileItem> {
        return generateAssistedRecipeOptionTiles()
    }

    private fun generateAssistedRecipeOptionTiles(): ArrayList<PreviewTileItem> {
        listOfTiles.clear()
        val requiredOptions = cookingViewModel.recipeExecutionViewModel.requiredOptions.value
        val virtualChefRequiredOptions =
            cookingViewModel.recipeExecutionViewModel.virtualChefRequiredOptions.value
        val rvm = cookingViewModel.recipeExecutionViewModel
        if (requiredOptions != null) {
            if (requiredOptions.contains(RecipeOptions.VIRTUAL_CHEF)) {
                handleVirtualChefOption(virtualChefRequiredOptions, rvm)
            } else {
                for (option in requiredOptions) {
                    when (option) {
                        RecipeOptions.WEIGHT -> {
                            listOfTiles.add(
                                PreviewTileItem(
                                    getString(R.string.text_header_weight),
                                    CookingAppUtils.displayWeightToUser(
                                        requireContext(),
                                        rvm.weight.value,
                                        rvm.weightOption.value?.displayUnits
                                    ).uppercase(Locale.ROOT),
                                    RecipeOptions.WEIGHT,
                                    PreviewTileItem.TileType.NORMAL_TILE
                                )
                            )
                        }

                        RecipeOptions.DONENESS -> {
                            handleDonenessOption(rvm)
                        }

                        RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> {
                            val temperature =
                                CookingAppUtils.displayProbeTemperatureToUser(requireContext(), rvm)
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
                                CookingAppUtils.displayTemperatureToUser(requireContext(), rvm)
                            listOfTiles.add(
                                PreviewTileItem(
                                    getString(R.string.text_subHeader_ovenTemp),
                                    temperature.uppercase(Locale.ROOT),
                                    RecipeOptions.TARGET_TEMPERATURE,
                                    PreviewTileItem.TileType.NORMAL_TILE
                                )
                            )
                        }

                        RecipeOptions.COOK_TIME -> {
                            val cookTime = CookingAppUtils.displayCookTimeToUser(
                                requireContext(),
                                rvm.cookTime.value,
                                true,
                                arrayOf(
                                    R.string.text_label_HR,
                                    R.string.text_label_MIN,
                                    R.string.text_label_SEC
                                )
                            )
                            listOfTiles.add(
                                PreviewTileItem(
                                    getString(R.string.text_header_enter_time_tumbler),
                                    cookTime.uppercase(Locale.ROOT),
                                    RecipeOptions.COOK_TIME,
                                    PreviewTileItem.TileType.NORMAL_TILE
                                )
                            )
                        }

                        RecipeOptions.AMOUNT -> {
                            val amountValue = (rvm.amount.value as? Number)?.toDouble() ?: 0.0
                            val formattedAmount =
                                if (amountValue == amountValue.toInt().toDouble()) {
                                    amountValue.toInt().toString()
                                } else {
                                    "%.1f".format(amountValue)
                                }

                            val resourceString = requireContext().getString(
                                CookingAppUtils.getResIdFromResName(
                                    context,
                                    AppConstants.TEXT_DONENESS_TILE + (rvm.amountOption.value as DoubleRange).displayUnits,
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
            }
        }
        listOfTiles[listOfTiles.lastIndex].isDividerHidden = true
        return listOfTiles
    }

    private fun handleVirtualChefOption(
        virtualChefRequiredOptions: MutableList<RecipeOptions>?,
        rvm: RecipeExecutionViewModel
    ) {
        listOfTiles.add(
            PreviewTileItem(
                getString(R.string.text_virtual_chef),
                getString(R.string.text_on),
                RecipeOptions.VIRTUAL_CHEF,
                PreviewTileItem.TileType.NORMAL_TILE
            )
        )

        virtualChefRequiredOptions?.takeIf { it.contains(RecipeOptions.DONENESS) }?.let {
            handleDonenessOption(rvm)
        }
    }

    private fun handleDonenessOption(rvm: RecipeExecutionViewModel) {
        val doneNessValue = requireContext().getString(
            CookingAppUtils.getResIdFromResName(
                context,
                AppConstants.TEXT_DONENESS_TILE + rvm.donenessOption.value?.defaultString,
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


    /**
     * provide text to show on the header bar title
     * @return string to show as title
     */
    override fun provideHeaderBarTitleText(): String {
        return CookingAppUtils.getHeaderTitleAsRecipeName(
            context,
            cookingViewModel.recipeExecutionViewModel.recipeName.value
        )
    }

    override fun provideHeaderBarLeftIconVisibility() = true

    /**
     * provide visibility of info icon
     * @return true if VISIBLE false if GONE
     */
    override fun provideHeaderBarInfoIconVisibility(): Boolean {
        val recipeRecord = CookBookViewModel.getInstance().getRecipeRecordById(cookingViewModel.recipeExecutionViewModel.recipeId)
        val infoIconVisibility = (NavigationUtils.isFirstTimeAssistedRecipeSelected(recipeRecord, cookingViewModel.recipeExecutionViewModel) && CookingAppUtils.cookingGuideList.isNotEmpty())

        return infoIconVisibility
    }
    override fun infoIconOnClick() {
        AudioManagerUtils.playOneShotSound(
            ContextProvider.getContext(),
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        navigateSafely(this, R.id.action_to_assisted_cookingGuideFragment, null, null)
    }
    /**
     * provide visibility of left action button, ex delay not applicable to mwo cycles
     * @return true if VISIBLE false if GONE
     */
    override fun provideLeftActionButtonVisibility(): Boolean {
        return CookingAppUtils.isRecipeOptionAvailable(
            cookingViewModel.recipeExecutionViewModel,
            RecipeOptions.DELAY_TIME)
    }

    override fun provideInfoIconRes(): Int? {
        return null
    }

    /**
     * Handle click event when left button is pressed for ex. delay if visibility is false then do nothing
     */
    override fun providePrimaryActionButtonClickEvent() {
        if (showCookingGuide) {
            val bundle = Bundle()
            bundle.putString(BUNDLE_NAVIGATED_FROM,NEXT_BUTTON)
            navigateSafely(this, R.id.action_to_assisted_cookingGuideFragment, bundle, null)
        } else {
            AudioManagerUtils.playOneShotSound(
                view?.context,
                R.raw.start_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            startAssistedRecipe()
        }
    }

    /**
     * start recipe related to assisted cycle only here
     *
     */
    private fun startAssistedRecipe() {
        if(cookingViewModel.isOfTypeMicrowaveOven){
            DoorEventUtils.startMicrowaveRecipeOrShowPopup(this, cookingViewModel)
            return
        }
        if(cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe && !MeatProbeUtils.isMeatProbeConnected(cookingViewModel)){
            PopUpBuilderUtils.insertMeatProbe(this, cookingViewModel, onMeatProbeConditionMet = { startAssistedRecipe() })
            return
        }
        val recipeExecuteErrorResponse =
            cookingViewModel.recipeExecutionViewModel.execute()
        HMILogHelper.Logd(tag, "start recipe from assistedPreview ${recipeExecuteErrorResponse.description}")
        if (recipeExecuteErrorResponse.isError) {
            CookingAppUtils.handleCookingError(
                this,
                cookingViewModel,
                recipeExecuteErrorResponse,
                false
            )
            return
        }else{
            HMIExpansionUtils.startOrStopKnobLEDFadeInLightAnimation(true)
        }
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }

    /**
     * provide the txt to display on button right/primary
     * @return text to show on right action button like start
     */
    override fun providePrimaryActionButtonText(): CharSequence {
        val recipeRecord = CookBookViewModel.getInstance().getRecipeRecordById(cookingViewModel.recipeExecutionViewModel.recipeId)
        showCookingGuide = (!NavigationUtils.isFirstTimeAssistedRecipeSelected(recipeRecord, cookingViewModel.recipeExecutionViewModel) && CookingAppUtils.cookingGuideList.isNotEmpty())
        return if(showCookingGuide) getString(R.string.text_button_next) else getString(R.string.text_button_start)
    }

    /**
     * Handle click event when right button is pressed for ex. start or next
     */
    override fun provideLeftActionButtonClickEvent() {
        navigateSafely(
            this, R.id.action_to_assistedDelayTumblerFragment, null, null
        )
    }

    /**
     * provide the txt to display on button left
     * @return text to show on left action button like delay, cancel
     */
    override fun provideLeftActionButtonText(): CharSequence {
        return getString(R.string.text_button_delay)
    }
    override fun onPreviewTileClick(view: View, position: Int) {
        var navigationAction = 0
        when(listOfTiles[position].recipeOptions){
            RecipeOptions.TARGET_TEMPERATURE -> {
                navigationAction =
                    if (CookingAppUtils.isTemperatureMapTextValue(cookingViewModel.recipeExecutionViewModel?.targetTemperatureOptions?.value)) R.id.action_to_manualMode_durationSelectionManualModeFragment else R.id.action_to_assisted_temperatureTumblerFragment
            }

            RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> {
                navigationAction = R.id.action_to_assistedProbeTemperatureTumbler
            }

            RecipeOptions.COOK_TIME -> {
                navigationAction = R.id.action_to_assisted_cookTimeFragment
            }
            RecipeOptions.DONENESS -> {
                navigationAction = R.id.action_to_assisted_pick_doneness_level
            }
            RecipeOptions.WEIGHT -> {
                navigationAction = R.id.action_to_assisted_pick_weight
            }
            RecipeOptions.AMOUNT -> {
                navigationAction = R.id.action_to_assisted_pick_amount
            }
            else -> {HMILogHelper.Loge(tag, "No recipeOption ${listOfTiles[position].recipeOptions} action specified for navigation")}
        }
        if(navigationAction != 0)
            navigateSafely(this, navigationAction, null, null)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}