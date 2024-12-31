/*
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package core.utils

import android.content.Context
import android.content.res.Resources
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.customviews.widgets.gridview.GridListItemModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.model.capability.recipe.options.TemperatureMap
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel

/**
 * Util class for the getting options for MoreOptionsPopUp.
 */
class MoreOptionsPopUpOptionHelper {
    companion object {
        /**
         * Method to get the cycle complete options to the grid View of more options popup
         *
         * @param isExtraBrownRecipe recipe is extra brown or not
         * @param isMicrowaveRecipe recipe is microwave based or not
         */
        fun getCycleCompleteOptions(
            context: Context,
            isExtraBrownRecipe: Boolean?,
            isMicrowaveRecipe: Boolean,
            isAssisted: Boolean = false,
            cookTimeOptionAvailable: Boolean,
            isSensingRecipe: Boolean
        ): ArrayList<GridListItemModel> {
            val resources = context.resources
            val gridListTileData: ArrayList<GridListItemModel> = ArrayList()
            if (!isAssisted) {
                val getResourceCycleTimerText : String = getTextForMicrowaveOrOvenRecipeComplete(resources,
                    isMicrowaveRecipe, cookTimeOptionAvailable, isSensingRecipe)

                if (getResourceCycleTimerText.isNotEmpty()) {
                    gridListTileData.add(
                        GridListItemModel(getResourceCycleTimerText, GridListItemModel.GRID_MORE_OPTIONS_TILE
                        ).apply {
                            tileImageSrc = if (isMicrowaveRecipe) R.drawable.ic_microwave_power_level
                                else R.drawable.ic_add_40
                            tileSubCategory = if (isMicrowaveRecipe) AppConstants.MoreOptionsSubCategory.TYPE_POWER_LEVEL.toString()
                                else AppConstants.MoreOptionsSubCategory.TYPE_COOK_TIME.toString()
                        }
                    )
                }

                if (isMicrowaveRecipe || isExtraBrownRecipe == true) {
                    if (getResourceCycleTimerText.isNotEmpty()) {
                        gridListTileData.add(
                            GridListItemModel(
                                getTextForMicrowaveOrExtraBrownRecipeComplete(
                                    resources,
                                    isMicrowaveRecipe,
                                    cookTimeOptionAvailable,
                                    isSensingRecipe
                                ),
                                GridListItemModel.GRID_MORE_OPTIONS_TILE
                            ).apply {
                                tileImageSrc = if(isMicrowaveRecipe) R.drawable.ic_add_40 else R.drawable.ic_timer
                                tileSubCategory = if (!isMicrowaveRecipe) AppConstants.MoreOptionsSubCategory.TYPE_EXTRA_BROWN.toString()
                                    else AppConstants.MoreOptionsSubCategory.TYPE_COOK_TIME.toString()
                            }
                        )
                    }
                }
            }
            return gridListTileData
        }

        /**
         * Method to get the cycle complete options to the grid View of more options popup
         *
         * @param isExtraBrownRecipe recipe is extra brown or not
         * @param isMicrowaveRecipe recipe is microwave based or not
         */
        @Suppress("UNUSED_PARAMETER")
        fun getCycleCompleteOptionsForProbe(
            context: Context,
            cookTime: Long?,
            tempMap: Any?,
            isExtraBrownRecipe: Boolean?,
            isMicrowaveRecipe: Boolean,
            viewModel: CookingViewModel,
            cookTimeOptionAvailable: Boolean,
            isSensingRecipe: Boolean
        ): ArrayList<GridListItemModel> {
            val resources = context.resources
            val gridListTileData: ArrayList<GridListItemModel> = ArrayList()

            val getResourceCycleTimerText : String = getTextForMicrowaveOrOvenRecipeComplete(resources,
                isMicrowaveRecipe, cookTimeOptionAvailable, isSensingRecipe)

            gridListTileData.add(
                GridListItemModel(
                    getResourceCycleTimerText,
                    GridListItemModel.GRID_MORE_OPTIONS_TILE
                ).apply {
                    tileImageSrc = if (isMicrowaveRecipe) R.drawable.ic_microwave_power_level else R.drawable.ic_add_40
                    tileSubCategory =
                        if (isMicrowaveRecipe) AppConstants.MoreOptionsSubCategory.TYPE_POWER_LEVEL.toString()
                        else AppConstants.MoreOptionsSubCategory.TYPE_COOK_TIME.toString()
                }
            )

            if (isMicrowaveRecipe || isExtraBrownRecipe == true) {
                gridListTileData.add(
                    GridListItemModel(
                        getTextForMicrowaveOrExtraBrownRecipeComplete(
                            resources,
                            isMicrowaveRecipe,
                            cookTimeOptionAvailable,
                            isSensingRecipe
                        ),
                        GridListItemModel.GRID_MORE_OPTIONS_TILE,
                    ).apply {
                        tileImageSrc = if(isMicrowaveRecipe) R.drawable.ic_add_40 else R.drawable.ic_timer
                        tileSubCategory = if(!isMicrowaveRecipe) AppConstants.MoreOptionsSubCategory.TYPE_EXTRA_BROWN.toString()
                            else AppConstants.MoreOptionsSubCategory.TYPE_COOK_TIME.toString()
                    }
                )
            }

            // Check whether target temperature is applicable to current recipe and add data
            if(!CookingAppUtils.isTimeBasedPreheatRecipe(viewModel)) {
                getTextForUpdateValuesRecipeInProgress(
                    resources,
                    tempMap,
                    isMicrowaveRecipe,
                    viewModel,
                    onRecipeCallback = { isActive, text, subCategory ->
                        val item = GridListItemModel(
                            text, GridListItemModel.GRID_MORE_OPTIONS_TILE
                        ).apply {
                            isEnable = isActive
                            tileImageSrc =
                                if (isMicrowaveRecipe) R.drawable.ic_microwave_power_level
                                else {
                                    if(tempMap !is TemperatureMap) R.drawable.ic_tempcalibration
                                    else R.drawable.ic_microwave_power_level
                                }
                            tileSubCategory = subCategory.toString()
                        }
                        gridListTileData.add(item)
                    }
                )
            }
            return gridListTileData
        }

        /**
         * Method to get the cycle in progress options to the grid View of more options popup
         *
         * @param tempMap recipe contains temperatureMap or temperatureInteger options
         * @param isMicrowaveRecipe recipe is microwave based or not
         */
        fun getCycleInProgressOptions(
            context: Context,
            cookTime: Long?,
            tempMap: Any?,
            isMicrowaveRecipe: Boolean,
            viewModel: CookingViewModel,
            isAssisted: Boolean = false,
            cookTimeOptionAvailable: Boolean,
            isSensingRecipe: Boolean
        ): ArrayList<GridListItemModel> {
            val resources = context.resources
            val gridListTileData: ArrayList<GridListItemModel> = ArrayList()
            // Check whether target temperature is applicable to current recipe and add data
            if (!((viewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(AppConstants.BROWNING_CONSTANT)) ||
                        (viewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                            AppConstants.ADD_BROWNING_CONSTANT
                        )))
            ) {
                if (!isAssisted)
                    if(!CookingAppUtils.isTimeBasedPreheatRecipe(viewModel)){
                        getTextForUpdateValuesRecipeInProgress(
                        resources,
                        tempMap,
                        isMicrowaveRecipe,
                        viewModel,
                        onRecipeCallback = { isActive, text, subCategory ->
                            val item = GridListItemModel(
                                text, GridListItemModel.GRID_MORE_OPTIONS_TILE
                            ).apply {
                                isEnable = isActive
                                tileSubCategory = subCategory.toString()
                                tileImageSrc =
                                    if (isMicrowaveRecipe) R.drawable.ic_microwave_power_level
                                    else {
                                        if(tempMap !is TemperatureMap) R.drawable.ic_tempcalibration
                                        else R.drawable.ic_microwave_power_level
                                    }
                            }
                            gridListTileData.add(item)
                        })
                    }

                var item = GridListItemModel(
                    if (cookTime?.toInt() == 0)
                        resources.getString(R.string.text_moreOptions_cookTime)
                    else resources.getString(R.string.text_moreOptions_cookTime),
                    GridListItemModel.GRID_MORE_OPTIONS_TILE
                ).apply {
                    tileImageSrc = R.drawable.ic_timer
                    tileSubCategory = AppConstants.MoreOptionsSubCategory.TYPE_COOK_TIME.toString()
                }
                //It was braking the sensing recipe which need to show cook time on more option popup
                if ((!cookTimeOptionAvailable)) {
                    item.titleText = AppConstants.EMPTY_STRING
                }
                item.isEnable = CookingAppUtils.isRecipeOptionAvailable(
                    viewModel.recipeExecutionViewModel,
                    RecipeOptions.COOK_TIME
                )
                //cook time can be editable in extended cooking, to make it generic based on nonEditable options ex for convect slow roast
                if(!item.isEnable && viewModel.recipeExecutionViewModel?.nonEditableOptions?.value?.containsKey(RecipeOptions.COOK_TIME) == true){
                    if(AbstractStatusFragment.isExtendedCookingForNonEditableCookTimeRecipe(viewModel)) {
                        item.titleText = resources.getString(R.string.text_moreOptions_cookTime)
                        item.isEnable = true
                    }
                    else item.titleText = AppConstants.EMPTY_STRING
                }
                if (!item.isEnable && viewModel.recipeExecutionViewModel.isProbeBasedRecipe
                    && CookingAppUtils.isRecipeOptionAvailable(
                        viewModel.recipeExecutionViewModel,
                        RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE
                    )
                ) {
                    item = GridListItemModel(
                        resources.getString(R.string.text_header_probe),
                        GridListItemModel.GRID_MORE_OPTIONS_TILE
                    ).apply {
                        tileImageSrc = R.drawable.ic_probe_oven
                        tileSubCategory = AppConstants.MoreOptionsSubCategory.TYPE_PROBE.toString()
                    }
                }

                if (item.titleText?.isNotEmpty() == true) {
                    gridListTileData.add(item)
                }
            }

            return gridListTileData
        }


        /**
         * Method to get the probe cycle in progress options to the grid View of more options popup
         *
         * @param tempMap recipe contains temperatureMap or temperatureInteger options
         * @param isMicrowaveRecipe recipe is microwave based or not
         */
        fun getCycleInProgressOptionsForProbe(
            context: Context,
            cookTime: Long?,
            tempMap: Any?,
            isMicrowaveRecipe: Boolean,
            viewModel: CookingViewModel
        ): ArrayList<GridListItemModel> {
            val resources = context.resources
            val gridListTileData: ArrayList<GridListItemModel> = ArrayList()

            val isAvailable = CookingAppUtils.isRecipeOptionAvailable(
                viewModel.recipeExecutionViewModel,
                RecipeOptions.COOK_TIME
            )
            var item: GridListItemModel? = null
            if(isAvailable) {
                 item = GridListItemModel(
                    if (cookTime?.toInt() == 0) resources.getString(R.string.text_moreOptions_cookTime)
                    else resources.getString(R.string.text_moreOptions_cookTime),
                    GridListItemModel.GRID_MORE_OPTIONS_TILE
                ).apply {
                    tileImageSrc = R.drawable.ic_add_40
                    tileSubCategory = AppConstants.MoreOptionsSubCategory.TYPE_COOK_TIME.toString()
                }
                item.isEnable = isAvailable
            }

            if (!isAvailable && viewModel.recipeExecutionViewModel.isProbeBasedRecipe
                && CookingAppUtils.isRecipeOptionAvailable(
                    viewModel.recipeExecutionViewModel,
                    RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE
                )
            ) {
                item = GridListItemModel(
                    resources.getString(R.string.text_moreOptions_probeTemp),
                    GridListItemModel.GRID_MORE_OPTIONS_TILE
                ).apply {
                    tileImageSrc = R.drawable.ic_probe_oven
                    tileSubCategory = AppConstants.MoreOptionsSubCategory.TYPE_PROBE.toString()
                }
            }

            if(item!=null) {
                gridListTileData.add(item)
            }
            // Check whether target temperature is applicable to current recipe and add data
                getTextForUpdateValuesRecipeInProgress(
                    resources,
                    tempMap,
                    isMicrowaveRecipe,
                    viewModel,
                    onRecipeCallback = { isActive, text, subCategory ->
                        val gridItem = GridListItemModel(
                            text, GridListItemModel.GRID_MORE_OPTIONS_TILE
                        ).apply {
                            isEnable = isActive
                            tileImageSrc =
                                if (isMicrowaveRecipe) R.drawable.ic_microwave_power_level else R.drawable.ic_tempcalibration
                            tileSubCategory = subCategory.toString()
                        }
                        gridListTileData.add(gridItem)
                    }
                )

            return gridListTileData
        }

        /**
         * Method to get the text on if it recipe is microwave or contains extra brown
         *
         * @param isMicrowaveRecipe recipe is microwave based or not
         */
        private fun getTextForMicrowaveOrExtraBrownRecipeComplete(
            resources: Resources,
            isMicrowaveRecipe: Boolean,
            cookTimeOptionAvailable: Boolean,
            isSensingRecipe: Boolean
        ): String {
            return if (isMicrowaveRecipe)
                if (isSensingRecipe && cookTimeOptionAvailable) {
                    resources.getString(R.string.text_moreOptions_addTime)
                } else if ((isSensingRecipe && (!cookTimeOptionAvailable))) {
                    return AppConstants.EMPTY_STRING
                } else {
                    resources.getString(R.string.text_moreOptions_addTime)
                } else
                resources.getString(R.string.weMissedThat)
        }

        /**
         * Method to get the text on if it recipe is microwave or oven based recipe
         *
         * @param isMicrowaveRecipe recipe is microwave based or not
         */
        private fun getTextForMicrowaveOrOvenRecipeComplete(
            resources: Resources,
            isMicrowaveRecipe: Boolean,
            cookTimeOptionAvailable: Boolean,
            isSensingRecipe: Boolean
        ): String {
            return if (isMicrowaveRecipe)
                resources.getString(R.string.text_moreOptions_powerLevel) else
                if (isSensingRecipe && cookTimeOptionAvailable) {
                    return resources.getString(R.string.text_moreOptions_addTime)
                } else if ((isSensingRecipe && (!cookTimeOptionAvailable))) {
                    return AppConstants.EMPTY_STRING
                } else {
                    resources.getString(R.string.text_moreOptions_addTime)
                }
        }

        /**
         * Method to get the update text based on recipe options while cycle is running
         *
         * @param isMicrowaveRecipe recipe is microwave based or not
         */
        private fun getTextForUpdateValuesRecipeInProgress(
            resources: Resources,
            tempMap: Any?,
            isMicrowaveRecipe: Boolean,
            viewModel: CookingViewModel,
            onRecipeCallback: (Boolean, String, AppConstants.MoreOptionsSubCategory) -> Unit
        ) {
            if (isMicrowaveRecipe) {
                onRecipeCallback(
                    true,
                    resources.getString(R.string.text_moreOptions_powerLevel),
                    AppConstants.MoreOptionsSubCategory.TYPE_POWER_LEVEL
                )
            } else if (tempMap is TemperatureMap) {
                onRecipeCallback(
                    true,
                    if (CookingAppUtils.isTimeBasedPreheatRecipe(viewModel))
                        resources.getString(R.string.text_moreOptions_temperature)
                    else resources.getString(R.string.text_moreOptions_broilLevel),
                    AppConstants.MoreOptionsSubCategory.TYPE_TEMPERATURE_LEVEL
                )
            } else {
                if (tempMap is IntegerRange) {
                    if ((tempMap.max == 0) || (tempMap.min == 0)) {
                        return
                    }
                }
                var changeTemperatureText =
                    resources.getString(R.string.text_moreOptions_temperature)
                if (viewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
                    changeTemperatureText =
                        resources.getString(R.string.text_moreOptions_ovenTemp)
                }

                if (CookingAppUtils.isRecipeOptionAvailable(
                        viewModel.recipeExecutionViewModel,
                        RecipeOptions.TARGET_TEMPERATURE
                    )
                ) {
                    onRecipeCallback(true, changeTemperatureText, AppConstants.MoreOptionsSubCategory.TYPE_CHANGE_TEMPERATURE)
                } else {
                    onRecipeCallback(false, changeTemperatureText, AppConstants.MoreOptionsSubCategory.TYPE_CHANGE_TEMPERATURE)
                }
            }
        }

        /**
         * Method to get the default user options
         *
         * @param context : context to access resources
         */
        fun getDefaultUserOptions(
            context: Context,
            isAlreadyFavoriteSaved: Boolean
        ): ArrayList<GridListItemModel> {
            val resources = context.resources

            val gridListTileData: ArrayList<GridListItemModel> = ArrayList()
            gridListTileData.add(GridListItemModel(
                resources.getString(R.string.text_moreOptions_favorite),
                GridListItemModel.GRID_MORE_OPTIONS_TILE
            ).apply {
                isMoreOptionDefaultLastTile = true
                tileImageSrc = if (isAlreadyFavoriteSaved) R.drawable.ic_favorites
                else R.drawable.ic_unsaved_favorite
                tileSubCategory = AppConstants.MoreOptionsSubCategory.TYPE_FAVORITES.toString()
            })

            val item = GridListItemModel(
                resources.getString(R.string.text_moreOptions_instruction),
                GridListItemModel.GRID_MORE_OPTIONS_TILE
            ).apply {
                isMoreOptionDefaultLastTile = true
                tileImageSrc = R.drawable.ic_info
                tileSubCategory = AppConstants.MoreOptionsSubCategory.TYPE_INSTRUCTIONS.toString()
            }
            gridListTileData.add(item)
            return gridListTileData
        }
    }
}