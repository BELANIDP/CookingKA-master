package core.utils

import android.content.Context
import android.os.Bundle
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.customviews.textButton.TextButton
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.model.capability.recipe.options.DoubleRange
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.model.capability.recipe.options.TemperatureMap
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.Constants.VIRTUAL_CHEF_ON
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.TreeNode
import com.whirlpool.hmi.utils.cookbook.records.RecipeRecord
import com.whirlpool.hmi.utils.timers.Timer
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants.MORE_OPTIONS
import core.utils.BundleKeys.Companion.BUNDLE_NAVIGATED_FROM
import core.utils.CookingAppUtils.Companion.getNavigatedFrom
import core.utils.CookingAppUtils.Companion.getRecipeOptions
import core.utils.CookingAppUtils.Companion.isFavoriteRecipeFlow

/**
 * File       : core/utils/NavigationUtils.java
 * Brief      : Contains helper utility methods which provides APIs to find Navigation Options when selection recipe options
 * Author     : Hiren
 * Created On : 4/2/24
 * Details    : This Util class is to find next navigation id for any fragment to navigate when selecting or loading any recipe
 */
class NavigationUtils {

    companion object {
        const val TAG = "NavigationUtils"

        private lateinit var kitchenAidLauncherActivity: FragmentActivity

        /**
         * method to set launcherActivity instance
         * */
        fun setKitchenAidLauncherActivity(fragmentActivity: FragmentActivity) {
            kitchenAidLauncherActivity = fragmentActivity
        }

        /**
         * method to get visibleFragment with launcher activity instance
         * */
        fun getVisibleFragment(): Fragment? {
            return NavigationViewModel.getVisibleFragment(kitchenAidLauncherActivity as AppCompatActivity)
        }

        /**
         * Common method to set recipe and load it, use isCurrentScreenInstruction as true if calling from instruction screen
         *  this method will find out whether to show instruction screen or not
         * @param fragment view of visible Fragment
         * @param cookingViewModel in scope view model
         * @param recipeName recipe name selected in RecipeSelection Fragment by user clicking
         * @param isCurrentScreenInstruction true if reference from Instruction screen, false otherwise
         * @param isKnobClick true if click from knob, false otherwise
         */
        fun navigateAfterRecipeSelection(
            fragment: Fragment,
            cookingViewModel: CookingViewModel?,
            recipeName: String?,
            isCurrentScreenInstruction: Boolean,
            isKnobClick: Boolean = false,
        ) {
            if (cookingViewModel == null) return
            if (recipeName == null) return
            CookBookViewModel.getInstance().setRootNodeForRecipes(
                CookBookViewModel.getInstance()
                    .getManualRecipesPresentationTreeFor(cookingViewModel.cavityName.value)
            )
            if (recipeName.contentEquals(AppConstants.RECIPE_INSTRUCTION_SLOW_ROAST)) {
                //this to show instruction screen for only once for low, medium, high. The recipe would not be loaded at this point
                navigateSlowRoastSelection(fragment, cookingViewModel, true, null)
                return
            }
            try {
                val recipeRecord: RecipeRecord =
                    CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
                        recipeName, cookingViewModel.cavityName.value
                    )
                var isMWORecipe = CookingViewModelFactory.getInScopeViewModel().isOfTypeMicrowaveOven
                var textInformation = AppConstants.TEXT_INFORMATION
                if (isMWORecipe){
                    textInformation = AppConstants.TEXT_INFORMATION_MWO
                }
                val textToShow: Int = CookingAppUtils.getResIdFromResName(
                    fragment.requireContext(),
                    textInformation + recipeRecord.recipeName,
                    AppConstants.RESOURCE_TYPE_STRING
                )
                if (!isCurrentScreenInstruction && recipeRecord.showInstruction && (textToShow != R.string.weMissedThat)) {
                    //come here only if recipe needs to be loaded in instruction screen
                    val bundle = Bundle()
                    bundle.putString(BundleKeys.RECIPE_NAME, recipeName)
                    navigateSafely(
                        fragment,
                        R.id.action_recipeSelectionFragment_to_recipeInstructionFragment,
                        bundle,
                        null
                    )
                } else {
                    val recipeErrorResponse =
                        cookingViewModel.recipeExecutionViewModel.load(recipeRecord)
                    HMILogHelper.Logd("$TAG loadRecipe", recipeErrorResponse.name)
                    if (!recipeErrorResponse.isError) {
                        val navigationAction = getNavigationIdForNextOption(cookingViewModel, null, isKnobClick)
                        if (navigationAction == 0) {
                            HMILogHelper.Logd(
                                "$TAG loadRecipe",
                                "no required recipe option available while loading recipe, executing $recipeName"
                            )
                            findNavigationOptionOrExecute(
                                fragment, cookingViewModel, recipeErrorResponse, null
                            )
                            return
                        }
                        navigateSafely(
                            fragment, navigationAction, null, null
                        )
                    } else {
                        HMILogHelper.Loge("$TAG loadRecipe", recipeErrorResponse.description)
                    }
                }
            } catch (exception: Exception) {
                HMILogHelper.Loge(
                    "$TAG loadRecipe",
                    "error in loading recipeName=${recipeName} with message ${exception.message}"
                )
                ToastUtils.showToast(getViewSafely(fragment)?.context, "Under Development")
            }
        }

        /**
         * method to load Sabbath recipe and navigate based on required option
         * @param fragment view of visible Fragment
         * @param cookingViewModel in scope view model
         */
        fun navigateAfterSabbathRecipeSelection(
            fragment: Fragment,
            cookingViewModel: CookingViewModel?) {
            if (cookingViewModel == null) return
            val cavityName = cookingViewModel.cavityName.value
            try {
                val recipeRecord: RecipeRecord =
                    CookBookViewModel.getInstance().getDefaultSabbathBakeRecordByCavity(cavityName)
                val recipeErrorResponse =
                    cookingViewModel.recipeExecutionViewModel.load(recipeRecord)
                HMILogHelper.Logd("${fragment.tag} Sabbath loadRecipe", "cavityName $cavityName ${recipeErrorResponse.name}")
                if (!recipeErrorResponse.isError) {
                    navigateSafely(
                        fragment, R.id.action_to_sabbathTemperatureTumblerFragment, null, null
                    )
                } else {
                    HMILogHelper.Loge("${fragment.tag} Sabbath loadRecipe", "cavityName $cavityName ${recipeErrorResponse.description}")
                }
            } catch (exception: Exception) {
                HMILogHelper.Loge(
                    "$TAG Sabbath loadRecipe",
                    "error in loading cavityName $cavityName with message ${exception.message}"
                )
                ToastUtils.showToast(getViewSafely(fragment)?.context, "Under Development")
            }
        }


        /**
         * Common method to set recipe and load it, use isCurrentScreenInstruction as true if calling from instruction screen
         *  this method will find out whether to show instruction screen or not,
         *  use this for probe recipe selection only, as next required option is probe temperature which is not prioritized in sdk functions
         * @param fragment view of visible Fragment
         * @param cookingViewModel in scope view model
         * @param recipeName recipe name selected in RecipeSelection Fragment by user clicking
         */
        fun navigateAfterProbeRecipeSelection(
            fragment: Fragment,
            cookingViewModel: CookingViewModel?,
            recipeName: String?,
            isCurrentScreenInstruction: Boolean,
            isKnobClick: Boolean = false,
        ) {
            if (cookingViewModel == null) return
            if (recipeName == null) return
            CookBookViewModel.getInstance().setRootNodeForRecipes(
                CookBookViewModel.getInstance()
                    .getManualRecipesPresentationTreeFor(cookingViewModel.cavityName.value)
            )
            try {
                val recipeRecord: RecipeRecord =
                    CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
                        recipeName, cookingViewModel.cavityName.value
                    )
                var isMWORecipe = CookingViewModelFactory.getInScopeViewModel().isOfTypeMicrowaveOven
                var textInformation = AppConstants.TEXT_INFORMATION
                if (isMWORecipe){
                    textInformation = AppConstants.TEXT_INFORMATION_MWO
                }
                val textToShow: Int = CookingAppUtils.getResIdFromResName(
                    fragment.requireContext(),
                    textInformation + recipeRecord.recipeName,
                    AppConstants.RESOURCE_TYPE_STRING
                )
                if (!isCurrentScreenInstruction && recipeRecord.showInstruction && (textToShow != R.string.weMissedThat)) {
                    //come here only if recipe needs to be loaded in instruction screen
                    val bundle = Bundle()
                    bundle.putString(BundleKeys.RECIPE_NAME, recipeName)
                    bundle.putString(BundleKeys.RECIPE_TYPE, BundleKeys.PROBE_BASED)
                    navigateSafely(
                        fragment,
                        R.id.action_probeCyclesGridFragment_to_recipeInstructionFragment,
                        bundle,
                        null
                    )
                } else{
                    val recipeErrorResponse =
                        cookingViewModel.recipeExecutionViewModel.load(recipeRecord)
                    HMILogHelper.Logd("$TAG loadRecipe", recipeErrorResponse.name)
                    if (!recipeErrorResponse.isError) {
                        val requiredOptions =
                            cookingViewModel.recipeExecutionViewModel?.requiredOptions?.value
                        val navigationAction =
                            if (requiredOptions?.contains(RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE) == true) R.id.action_to_probeTemperatureTumbler
                            else getNavigationIdForNextOption(cookingViewModel, null)
                        navigateSafely(
                            fragment, navigationAction, null, null
                        )
                    } else {
                        HMILogHelper.Loge("$TAG loadRecipe", recipeErrorResponse.description)
                    }
                }
            } catch (exception: Exception) {
                HMILogHelper.Loge(
                    "$TAG loadRecipe",
                    "error in loading recipeName=${recipeName} with message ${exception.message}"
                )
                ToastUtils.showToast(getViewSafely(fragment)?.context, "Under Development")
            }
        }

        /**
         * Common method to set recipe and load the food type fpr assisted recipes only
         * @param fragment view of visible Fragment
         * @param cookingViewModel in scope view model
         * @param foodType recipe name/ sub food type selected in RecipeSelection Fragment by user clicking
         */
        fun navigateAfterAssistedFoodTypeSelection(
            fragment: Fragment,
            cookingViewModel: CookingViewModel,
            foodType: String?,
        ) {
            try {
                val recipeRecord =
                    CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
                        foodType, cookingViewModel.cavityName.value
                    )
                val recipeErrorResponse =
                    cookingViewModel.recipeExecutionViewModel.load(recipeRecord)
                HMILogHelper.Logd("$TAG loadRecipe", "foodType $foodType ${recipeErrorResponse.name}")
                if (!recipeErrorResponse.isError) {
                    //hard code for special recipe of Fresh Pizza Assisted
                    if(CookingAppUtils.getResIdFromResName(
                            fragment.requireContext(),
                            AppConstants.TEXT_PREHEATING_TYPE + cookingViewModel.recipeExecutionViewModel.recipeName.value.toString(),
                            AppConstants.RESOURCE_TYPE_STRING
                        ) != R.string.weMissedThat && CookingAppUtils.isTimeBasedPreheatRecipe(cookingViewModel)){
                        HMILogHelper.Logd("$TAG loadRecipe", "foodType $foodType is FreshPizza recipe so not following Da1/Day2 behavior")
                        navigateAfterFreshPizzaSelection(fragment, cookingViewModel)
                        return
                    }

                    if (isFirstTimeAssistedRecipeSelected(
                            recipeRecord, cookingViewModel.recipeExecutionViewModel
                        ) && setSecondTimeAssistedRecipeSelection(
                            recipeRecord, cookingViewModel.recipeExecutionViewModel
                        ) && navigateSafely(fragment, R.id.action_to_assisted_preview, null, null)
                    ) {
                        return
                    }

                    navigateSafely(
                        fragment, getNavigationIdForNextOption(cookingViewModel, null), null, null
                    )
                } else {
                    HMILogHelper.Loge("$TAG loadRecipe", recipeErrorResponse.description)
                }
            } catch (exception: Exception) {
                HMILogHelper.Loge(
                    "$TAG loadRecipe",
                    "error in loading recipeName=${foodType} with message ${exception.message}"
                )
                ToastUtils.showToast(getViewSafely(fragment)?.context, "Under Development")
            }
        }

        /**
         * Use this method to navigate after fresh pizza has been selected,this will handle both assisted and manual recipe flow
         * in case of assisted it will set all parameters to load and move to preview screen as Day1/Day2 is not applicable
         * @param fragment
         * @param cookingViewModel
         */
        private fun navigateAfterFreshPizzaSelection(
            fragment: Fragment,
            cookingViewModel: CookingViewModel,
        ) {
            val recipeName = cookingViewModel.recipeExecutionViewModel.recipeName.value
            HMILogHelper.Logd(
                fragment.tag,
                "recipeName $recipeName Assisted Recipe Selected, setting all parameters and navigating to preview screen"
            )
            val requiredOptions = cookingViewModel.recipeExecutionViewModel.requiredOptions.value
            var recipeErrorResponse: RecipeErrorResponse? = null
            if (requiredOptions != null) {
                for (option in requiredOptions) {
                    when (option) {
                        RecipeOptions.WEIGHT -> {
                            val defaultWeight =
                                cookingViewModel.recipeExecutionViewModel.weightOption.value?.defaultValue
                            recipeErrorResponse =
                                cookingViewModel.recipeExecutionViewModel.setWeight(
                                    defaultWeight?.toFloat() ?: 0f
                                )
                            HMILogHelper.Logd(
                                fragment.tag,
                                "${cookingViewModel.cavityName.value} recipeName $recipeName assisted timePreheat, recipeErrorResponse ${recipeErrorResponse.description} defaultWeight $defaultWeight"
                            )
                        }

                        RecipeOptions.DONENESS -> {
                            val defaultDoneNess =
                                cookingViewModel.recipeExecutionViewModel.donenessOption.value?.defaultString
                            recipeErrorResponse =
                                cookingViewModel.recipeExecutionViewModel.setDoneness(
                                    defaultDoneNess
                                )
                            HMILogHelper.Logd(
                                fragment.tag,
                                "${cookingViewModel.cavityName.value} recipeName $recipeName assisted timePreheat, recipeErrorResponse ${recipeErrorResponse.description} defaultDoneNess $defaultDoneNess"
                            )
                        }

                        RecipeOptions.TARGET_TEMPERATURE -> {
                            val tempMap =
                                cookingViewModel.recipeExecutionViewModel?.targetTemperatureOptions?.value
                            if (tempMap != null) {
                                val defaultTemperature: Int = if (tempMap is TemperatureMap) {
                                    tempMap.temperatureMap.getOrDefault(tempMap.defaultValue, 0)
                                } else {
                                    (tempMap as IntegerRange).defaultValue
                                }
                                recipeErrorResponse =
                                    cookingViewModel.recipeExecutionViewModel.setTargetTemperature(
                                        defaultTemperature.toFloat()
                                    )
                                HMILogHelper.Logd(
                                    fragment.tag,
                                    "${cookingViewModel.cavityName.value} recipeName $recipeName assisted timePreheat, recipeErrorResponse ${recipeErrorResponse?.description} defaultTemperature $defaultTemperature"
                                )
                            }
                        }

                        RecipeOptions.COOK_TIME -> {
                            val defaultCookTime =
                                (cookingViewModel.recipeExecutionViewModel.cookTimeOption.value as IntegerRange).defaultValue
                            recipeErrorResponse =
                                cookingViewModel.recipeExecutionViewModel.setCookTime(
                                    defaultCookTime.toLong()
                                )
                            HMILogHelper.Logd(
                                fragment.tag,
                                "${cookingViewModel.cavityName.value} recipeName $recipeName assisted timePreheat, recipeErrorResponse ${recipeErrorResponse.description} defaultCookTime $defaultCookTime"
                            )
                        }

                        RecipeOptions.AMOUNT -> {
                            val defaultAmount =
                                (cookingViewModel.recipeExecutionViewModel.amountOption.value as DoubleRange).defaultValue
                            recipeErrorResponse =
                                cookingViewModel.recipeExecutionViewModel.setAmount(defaultAmount)
                            HMILogHelper.Logd(
                                fragment.tag,
                                "${cookingViewModel.cavityName.value} recipeName $recipeName assisted timePreheat, recipeErrorResponse ${recipeErrorResponse.description} defaultCookTime $defaultAmount"
                            )
                        }

                        else -> {
                            HMILogHelper.Loge("given recipeOption $option not available to set default value for time based preheat recipes")
                        }
                    }
                    if (recipeErrorResponse?.isError == true) {
                        HMILogHelper.Loge(
                            fragment.tag,
                            "${cookingViewModel.cavityName.value} recipeName $recipeName assisted timePreheat, recipeErrorResponse ${recipeErrorResponse.description} error in setting one of the requiredOption parameter showing handleCookingError"
                        )
                        CookingAppUtils.handleCookingError(
                            fragment,
                            cookingViewModel,
                            recipeErrorResponse,
                            false
                        )
                        return
                    }
                }
                HMILogHelper.Logd(
                    fragment.tag,
                    "Success in setting parameters ${cookingViewModel.cavityName.value} recipeName $recipeName assisted timePreheat, navigating to action_to_assisted_preview"
                )
                navigateSafely(fragment, R.id.action_to_assisted_preview, null, null)
            }
        }

        /**
         * Common method to set recipe and load it, use isCurrentScreenInstruction as true if calling from instruction screen
         *  this method will find out whether to show instruction screen or not
         * @param fragment view of visible Fragment
         * @param cookingViewModel in scope view model
         * @param isCurrentScreenInstruction true if reference from Instruction screen, false otherwise
         * @param slowRoastSelectedOption selected slow roast option, convectSlowRoastMedium, convectSlowRoastLow, convectSlowRoastHigh
         * and if NULL then will show option to select these low, medium, high slow roast options
         */
        fun navigateSlowRoastSelection(
            fragment: Fragment,
            cookingViewModel: CookingViewModel?,
            isCurrentScreenInstruction: Boolean,
            slowRoastSelectedOption: String?,
        ) {
            if (cookingViewModel == null) return
            try {
                val recipeRecord: RecipeRecord =
                    CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
                        AppConstants.RECIPE_INSTRUCTION_SLOW_ROAST,
                        cookingViewModel.cavityName.value
                    )
                if (!isCurrentScreenInstruction && recipeRecord.showInstruction) {
                    //come here only if recipe needs to be loaded in instruction screen
                    val bundle = Bundle()
                    bundle.putString(
                        BundleKeys.RECIPE_NAME, AppConstants.RECIPE_INSTRUCTION_SLOW_ROAST
                    )
                    navigateSafely(
                        fragment,
                        R.id.action_recipeSelectionFragment_to_recipeInstructionFragment,
                        bundle,
                        null
                    )
                } else if (slowRoastSelectedOption == null) {
                    navigateSafely(
                        fragment, R.id.action_manualMode_slowRoastFragment, null, null
                    )
                } else {
                    val recipeRecordByOption: RecipeRecord =
                        CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
                            slowRoastSelectedOption, cookingViewModel.cavityName.value
                        )
                    val recipeErrorResponse =
                        cookingViewModel.recipeExecutionViewModel.load(recipeRecordByOption)
                    HMILogHelper.Logd("$TAG loadRecipe", recipeErrorResponse.name)
                    if (!recipeErrorResponse.isError) {
                        navigateSafely(
                            fragment,
                            getNavigationIdForNextOption(cookingViewModel, null),
                            null,
                            null
                        )
                    } else {
                        HMILogHelper.Loge("$TAG loadRecipe", recipeErrorResponse.description)
                    }
                }
            } catch (exception: Exception) {
                HMILogHelper.Loge(
                    "$TAG loadRecipe",
                    "error in loading recipeName=${AppConstants.RECIPE_SLOW_ROAST} with message ${exception.message}"
                )
                ToastUtils.showToast(fragment.context, "Under Development")
            }

        }

        /**
         * use this to navigate back from sub  sub child recipe
         * we have to set root node to sub child recipe for ex manual -> convect -> slow roast -> 4, 8, 12 hrs
         *
         * @param fragment
         */
        fun navigateBackFromSubChildRecipes(fragment: Fragment) {
            CookBookViewModel.getInstance().setRootNodeForRecipes(
                CookBookViewModel.getInstance()
                    .getManualRecipesPresentationTreeFor(CookingViewModelFactory.getInScopeViewModel().cavityName.value)
            )
            val selectedNode: TreeNode<String>? =
                CookingAppUtils.getNodeForCycle(AppConstants.RECIPE_CONVECT)
            HMILogHelper.Logd(
                "SubChildRecipes",
                "cycleName= " + AppConstants.RECIPE_CONVECT + ": selectedNode=" + selectedNode?.name
            )
            CookBookViewModel.getInstance().setRootNodeForRecipes(selectedNode)
            NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    getViewSafely(
                        fragment
                    ) ?: fragment.requireView()
                )
            )
        }

        /**
         * Get navigation id of duration temperature of integer tumbler to show when recipe is running
         *
         * @param fragment current visible fragment generally it would be AbstractStatusFragment
         * @param viewModel in cope view model
         */
        fun navigateToTemperatureWhenRunning(
            fragment: Fragment,
            viewModel: CookingViewModel,
            isKnobClick: Boolean,
        ) {
            val executionState = viewModel.recipeExecutionViewModel.recipeExecutionState.value
            val activeStates = setOf(
                    RecipeExecutionState.RUNNING,
                    RecipeExecutionState.RUNNING_EXT,
                    RecipeExecutionState.DELAYED,
                    RecipeExecutionState.PAUSED,
                    RecipeExecutionState.PAUSED_EXT
            )
            if (executionState in activeStates) {
                val actionId: Int = if (CookingAppUtils.isTemperatureMapTextValue(
                        viewModel.recipeExecutionViewModel.targetTemperatureOptions.value
                    )
                ) R.id.action_to_manualMode_durationSelectionManualModeFragment
                else if (isKnobClick) R.id.action_to_manualMode_temperatureTumblerFragment
                else R.id.action_manualModeTemperatureTumblerFragment_to_temperature_numpad

                navigateSafely(
                    fragment, actionId, null, null
                )
                return
            }
            HMILogHelper.Logd(
                "TemperatureSelection",
                "Not ready to change temperature recipeCookingState =${viewModel.recipeExecutionViewModel.recipeCookingState.value}"
            )
        }

        /**
         * set temperature and find next option to navigate if none found then it would execute recipe
         *
         * @param fragment current visible fragment
         * @param inScopeViewModel cooking view model in scope
         * @param selectedTemperature selected temperature by user
         */
        fun navigateAndSetTemperature(
            fragment: Fragment,
            inScopeViewModel: CookingViewModel?,
            selectedTemperature: Float,
        ) {
            if (inScopeViewModel == null) return
            val recipeErrorResponse =
                inScopeViewModel.recipeExecutionViewModel?.setTargetTemperature(selectedTemperature)
            val requiredOptions = inScopeViewModel.recipeExecutionViewModel?.requiredOptions?.value
            //this is hard coded because SDK is not prioritizing MEAT probe temperature over oven temperature selection
            val recipeOptionToCheck =
                if (recipeErrorResponse?.isError == false && inScopeViewModel.recipeExecutionViewModel?.isProbeBasedRecipe == true && requiredOptions?.contains(
                        RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE
                    ) == true && (inScopeViewModel.recipeExecutionViewModel?.meatProbeTargetTemperature?.value
                        ?: 0) > 0
                ) RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE else RecipeOptions.TARGET_TEMPERATURE
            findNavigationOptionOrExecute(
                fragment, inScopeViewModel, recipeErrorResponse, recipeOptionToCheck
            )
            HMILogHelper.Loge(
                "$TAG setTemperature",
                "$selectedTemperature with error response= ${recipeErrorResponse?.name}"
            )
        }

        /**
         * set temperature and find next option to navigate if none found then it would execute recipe
         *
         * @param fragment current visible fragment
         * @param inScopeViewModel cooking view model in scope
         * @param selectedTemperature selected temperature by user
         */
        fun navigateAndSetProbeTemperature(
            fragment: Fragment,
            inScopeViewModel: CookingViewModel?,
            selectedTemperature: Float,
        ) {
            if (inScopeViewModel == null) return
            val recipeErrorResponse =
                inScopeViewModel.recipeExecutionViewModel?.setTargetMeatProbeTemperature(
                    selectedTemperature
                )
            val requiredOptions = inScopeViewModel.recipeExecutionViewModel?.requiredOptions?.value
            //this is hard coded because SDK is not prioritizing MEAT probe temperature over oven temperature selection
            if (recipeErrorResponse?.isError == false && requiredOptions?.contains(RecipeOptions.TARGET_TEMPERATURE) == true && (inScopeViewModel.recipeExecutionViewModel?.targetTemperature?.value
                    ?: 0) <= 0
            ) {
                navigateSafely(
                    fragment,
                    if (CookingAppUtils.isTemperatureMapTextValue(inScopeViewModel.recipeExecutionViewModel?.targetTemperatureOptions?.value)) R.id.action_to_manualMode_durationSelectionManualModeFragment else R.id.action_to_manualMode_temperatureTumblerFragment,
                    null,
                    null
                )
                return
            }
            findNavigationOptionOrExecute(
                fragment,
                inScopeViewModel,
                recipeErrorResponse,
                RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE
            )
            HMILogHelper.Loge(
                "$TAG setProbeTemperature",
                "$selectedTemperature with error response= ${recipeErrorResponse?.name}"
            )
        }

        /**
         * set cook time in seconds and find next option to navigate if none found then it would execute recipe
         *
         * @param fragment current visible fragment
         * @param inScopeViewModel cooking view model in scope
         * @param selectedCookTime selected cooking time by user
         */
        fun navigateAndSetCookTime(
            fragment: Fragment,
            inScopeViewModel: CookingViewModel?,
            selectedCookTime: Long,
        ) {
            if (inScopeViewModel == null) return
            val recipeErrorResponse =
                inScopeViewModel.recipeExecutionViewModel?.setCookTime(selectedCookTime)
            findNavigationOptionOrExecute(
                fragment, inScopeViewModel, recipeErrorResponse, RecipeOptions.COOK_TIME
            )
            HMILogHelper.Loge(
                "$TAG cookTime",
                "$selectedCookTime with error response= ${recipeErrorResponse?.name}"
            )
        }

        /**
         * set DoneNess and find next option to navigate if none found then it would execute recipe
         *
         * @param fragment current visible fragment
         * @param inScopeViewModel cooking view model in scope
         * @param selectedDoneNess selected doneNess by user
         */
        fun navigateAndSetDoneNess(
            fragment: Fragment,
            inScopeViewModel: CookingViewModel?,
            selectedDoneNess: String,
        ) {
            if (inScopeViewModel == null) return
            val recipeErrorResponse =
                inScopeViewModel.recipeExecutionViewModel?.setDoneness(selectedDoneNess)
            if(inScopeViewModel.recipeExecutionViewModel?.requiredOptions?.value?.contains(RecipeOptions.VIRTUAL_CHEF) == true){
                inScopeViewModel.recipeExecutionViewModel?.setVirtualchef(fragment.resources.getString(R.string.text_on))
            }
            findNavigationOptionOrExecute(
                fragment, inScopeViewModel, recipeErrorResponse, RecipeOptions.DONENESS
            )
            HMILogHelper.Loge(
                "$TAG setDoneNess",
                "$selectedDoneNess with error response= ${recipeErrorResponse?.name}"
            )
        }

        /**
         * set Weight and find next option to navigate if none found then it would execute recipe
         *
         * @param fragment current visible fragment
         * @param inScopeViewModel cooking view model in scope
         * @param selectedWeight selected doneNess by user
         */
        fun navigateAndSetWeight(
            fragment: Fragment,
            inScopeViewModel: CookingViewModel?,
            selectedWeight: Float,
        ) {
            if (inScopeViewModel == null) return
            val recipeErrorResponse =
                inScopeViewModel.recipeExecutionViewModel?.setWeight(selectedWeight)
            findNavigationOptionOrExecute(
                fragment, inScopeViewModel, recipeErrorResponse, RecipeOptions.WEIGHT
            )
            HMILogHelper.Loge(
                "$TAG selectedWeight",
                "$selectedWeight with error response= ${recipeErrorResponse?.name}"
            )
        }

        /**
         * set Amount and find next option to navigate if none found then it would execute recipe
         *
         * @param fragment current visible fragment
         * @param inScopeViewModel cooking view model in scope
         * @param selectedAmount selected doneNess by user
         */
        fun navigateAndSetAmount(
            fragment: Fragment,
            inScopeViewModel: CookingViewModel?,
            selectedAmount: Double,
        ) {
            if (inScopeViewModel == null) return
            val recipeErrorResponse =
                inScopeViewModel.recipeExecutionViewModel?.setAmount(selectedAmount)
            findNavigationOptionOrExecute(
                fragment, inScopeViewModel, recipeErrorResponse, RecipeOptions.AMOUNT
            )
            HMILogHelper.Loge(
                "$TAG selectedAmount",
                "$selectedAmount with error response= ${recipeErrorResponse?.name}"
            )
        }

        /**
         * find based on recipe option and recipe error response to whether execute the recipe or go to next navigation options
         *
         * @param fragment current visible fragment
         * @param inScopeViewModel cooking view model in scope
         * @param recipeErrorResponse reference of error response passed by setting recipe options
         * @param recipeOptions next recipe options to identify the requirements to start recipe
         */
        private fun findNavigationOptionOrExecute(
            fragment: Fragment,
            inScopeViewModel: CookingViewModel,
            recipeErrorResponse: RecipeErrorResponse?,
            recipeOptions: RecipeOptions?,
        ) {
            if (recipeErrorResponse != null) {
                if (!recipeErrorResponse.isError) {
                    if (isFavoriteRecipeFlow()) {
                        navigateNextForFavoritesRecipe(
                            fragment,
                            inScopeViewModel,
                            recipeOptions,
                            KnobNavigationUtils.knobForwardTrace
                        )
                        return
                    }
                    val nextOption = getNavigationIdForNextOption(inScopeViewModel, recipeOptions)
                    if (nextOption == 0) {
                        if (inScopeViewModel.recipeExecutionViewModel.isNotRunning) {
                            //for assisted recipes always got to preview screen
                            if (CookingAppUtils.isRecipeAssisted(
                                    inScopeViewModel.recipeExecutionViewModel.recipeName.value,
                                    inScopeViewModel.cavityName.value
                                )
                            ) {
                                navigateSafely(
                                    fragment, R.id.action_to_assisted_preview, null, null
                                )
                                return
                            }
                            // for microwave cavity show popup or start cycle
                            if (inScopeViewModel.isOfTypeMicrowaveOven) {
                                DoorEventUtils.startMicrowaveRecipeOrShowPopup(
                                    fragment, inScopeViewModel
                                )
                                return
                            }
                            // for microwave cavity show popup or start cycle
                            if (inScopeViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
                                startProbeRecipeOrPopUp(inScopeViewModel, fragment)
                                return
                            } else if (MeatProbeUtils.isMeatProbeConnected(inScopeViewModel)) {
                                PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(
                                    fragment,
                                    inScopeViewModel
                                )
                                return
                            }
                            val recipeExecuteErrorResponse =
                                inScopeViewModel.recipeExecutionViewModel.execute()
                            if (recipeExecuteErrorResponse.isError) {
                                CookingAppUtils.handleCookingError(
                                    fragment, inScopeViewModel, recipeExecuteErrorResponse, false
                                )
                                return
                            } else {
                                HMIExpansionUtils.startOrStopKnobLEDFadeInLightAnimation(true)
                            }
                        }
                        CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                        return
                    } else {
                        if (fragment.arguments?.getString(BundleKeys.BUNDLE_IS_FROM_PREVIEW_SCREEN)
                                ?.contentEquals("popToPreview") == true
                        ) {
                            NavigationViewModel.popBackStack(
                                Navigation.findNavController(
                                    getViewSafely(
                                        fragment
                                    ) ?: fragment.requireView()
                                )
                            )
                            return
                        }
                        if(inScopeViewModel.recipeExecutionViewModel?.requiredOptions?.value?.contains(RecipeOptions.VIRTUAL_CHEF) == true){
                            inScopeViewModel.recipeExecutionViewModel?.setVirtualchef(fragment.resources.getString(R.string.text_on))
                            return
                        }
                        if (inScopeViewModel.recipeExecutionViewModel.isRunning) {
                            CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                            return
                        }
                        navigateSafely(
                            fragment, nextOption, null, null
                        )
                        return
                    }
                }
                CookingAppUtils.handleCookingError(
                    fragment, inScopeViewModel, recipeErrorResponse, false
                )
            }
        }

        /**
         * start delay recipe, things to consider is delay is like start with prolonging time so every condition
         * that are applicable to start recipe also applies to delay start like opening closing door
         * @param fragment current visible fragment
         * @param inScopeViewModel cooking view model in scope
         * @param delayTime time to set delay in the recipe
         */
        fun startDelayRecipe(
            fragment: Fragment,
            inScopeViewModel: CookingViewModel,
            delayTime: Long,
        ) {
            var setDelayTime = delayTime
            val (isDelayWithCookTime, cookTimeValue) = CookingAppUtils.isCookTimeProgrammed(inScopeViewModel)
            if(isDelayWithCookTime) setDelayTime = delayTime - cookTimeValue
            HMILogHelper.Logd(
                TAG,
                "startDelayTime flow: ${inScopeViewModel.cavityName.value} delayTime $setDelayTime"
            )
            // for microwave cavity show popup or start cycle
            if (inScopeViewModel.isOfTypeMicrowaveOven) {
                DoorEventUtils.startMicrowaveRecipeOrShowPopup(
                    fragment, inScopeViewModel
                )
                return
            }
            if (MeatProbeUtils.isMeatProbeConnected(inScopeViewModel)) {
                if (!inScopeViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
                    PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(
                        fragment, inScopeViewModel
                    )
                    return
                }
            } else {
                if (inScopeViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
                    PopUpBuilderUtils.insertMeatProbe(
                        fragment, inScopeViewModel
                    ) {
                        startDelayRecipe(fragment, inScopeViewModel, setDelayTime)
                    }
                    return
                }
            }

            val recipeErrorResponse =
                inScopeViewModel.recipeExecutionViewModel.setDelayTime(setDelayTime)
            HMILogHelper.Logd(
                TAG,
                "setDelay $setDelayTime recipeErrorResponse ${recipeErrorResponse.description}"
            )
            if (recipeErrorResponse.isError) {
                CookingAppUtils.handleDelayCookingError(
                    fragment, inScopeViewModel, recipeErrorResponse, false
                )
                return
            }
            if(inScopeViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED)
                CookingAppUtils.navigateToStatusOrClockScreen(fragment)
            else
                CookingAppUtils.handleDelayErrorAndStartCooking(fragment, inScopeViewModel, false)
        }

        /**
         * set microwave power level and find next option to navigate if none found then it would execute recipe
         *
         * @param fragment current visible fragment
         * @param inScopeViewModel cooking view model in scope
         * @param selectedPowerValue selected power by user
         */
        fun navigateAndSetMwoPowerLevel(
            fragment: Fragment,
            inScopeViewModel: CookingViewModel?,
            selectedPowerValue: Double,
        ) {
            if (inScopeViewModel == null) return
            val recipeErrorResponse =
                inScopeViewModel.recipeExecutionViewModel?.setMwoPowerLevelInPercentage(
                    selectedPowerValue
                )
            findNavigationOptionOrExecute(
                fragment, inScopeViewModel, recipeErrorResponse, RecipeOptions.MWO_POWER_LEVEL
            )
            HMILogHelper.Loge(
                "$TAG powerLevel",
                "$selectedPowerValue with error response= ${recipeErrorResponse?.name}"
            )
        }

        /**
         * Use this method to find whether recipe is ready to start and has all the required parameters or not
         * @param cookingViewModel cooking view model in scope
         * @param currentOption pass the current selection option to find next value of recipe options , if passed null then it would return first parameter
         * @param isKnobClick true if click from knob, false otherwise. Default false
         * @return 0 if all recipe options are satisfy to start the recipe, actual navigation ID based on unfinished recipe options
         */
        fun getNavigationIdForNextOption(
            cookingViewModel: CookingViewModel?,
            currentOption: RecipeOptions?,
            isKnobClick: Boolean = KnobNavigationUtils.knobForwardTrace,
        ): Int {
            HMILogHelper.Logd("getNavigationIdForNextOption is knobClick $isKnobClick")
            if (cookingViewModel == null) return -1
            val nextOption = cookingViewModel.recipeExecutionViewModel?.getNextRequiredOption(
                currentOption
            )

            return when (nextOption) {
                RecipeOptions.TARGET_TEMPERATURE -> {
                    if (CookingAppUtils.isTemperatureMapTextValue(cookingViewModel.recipeExecutionViewModel?.targetTemperatureOptions?.value)) R.id.action_to_manualMode_durationSelectionManualModeFragment else R.id.action_to_manualMode_temperatureTumblerFragment
                }

                RecipeOptions.MWO_POWER_LEVEL -> {
                    R.id.action_to_manualMode_mwoPowerTumblerFragment
                }

                RecipeOptions.COOK_TIME -> {
                    if (isKnobClick)
                        R.id.action_manualMode_to_verticalTumblerFragment
                    else
                        R.id.action_to_manualMode_cookTimeFragment
                }

                RecipeOptions.DONENESS -> {
                    R.id.action_to_assisted_pick_doneness_level
                }

                RecipeOptions.WEIGHT -> {
                    R.id.action_to_assisted_pick_weight
                }

                RecipeOptions.AMOUNT -> {
                    R.id.action_to_assisted_pick_amount
                }

                RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> {
                    R.id.action_to_probeTemperatureTumbler
                }

                RecipeOptions.VIRTUAL_CHEF -> {
                    cookingViewModel.recipeExecutionViewModel.setVirtualchef(VIRTUAL_CHEF_ON)
                    R.id.action_to_assisted_pick_doneness_level
                }

                else -> 0
            }
        }

        /**
         * Use this method to find whether recipe is ready to start and has all the required parameters or not
         * get text based on this info if there are any remaining parameters to set then return "NEXT" if not then return "START", if cycle is already running then "Update"
         * @param cookingViewModel cooking view model in scope
         * @param currentOption pass the current selection option to find next value of recipe options , if passed null then it would return first parameter
         * @return 0 if all recipe options are satisfy to start the recipe, actual navigation ID based on unfinished recipe options
         */
        fun getRightButtonTextForRecipeOption(
            context: Context?,
            cookingViewModel: CookingViewModel?,
            currentOption: RecipeOptions?,
        ): String {
            if (cookingViewModel == null) return ""
            if (context == null) return ""
            val recipeExecutionState =
                cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value
            val isNext = getNavigationIdForNextOption(cookingViewModel, currentOption) > 0
            // for assisted recipes always text will be NEXT
            @Suppress("UNNECESSARY_SAFE_CALL") val startOrNextText =
                if (TextUtils.isEmpty(getNavigatedFrom())) {
                if (CookingAppUtils.isRecipeAssisted(
                    cookingViewModel.recipeExecutionViewModel.recipeName.value,
                    cookingViewModel.cavityName.value
                )
            ) context.resources.getString(R.string.text_button_next)
            else if ((currentOption == RecipeOptions.COOK_TIME) && (cookingViewModel?.isOfTypeMicrowaveOven != true))
                context.resources.getString(R.string.text_start_now_button)
            else context.resources.getString(R.string.text_button_start)
                } else {
                    context.resources.getString(R.string.text_button_next)
                }

            when (currentOption) {
                RecipeOptions.COOK_TIME -> {
                    val cookTimerState =
                        cookingViewModel.recipeExecutionViewModel.cookTimerState.value
                    if (recipeExecutionState == RecipeExecutionState.IDLE) {
                        return if (isNext) context.resources.getString(R.string.text_button_next) else startOrNextText
                    }
                    //for time preheat recipe, cookTimerState will be RUNNING but it is actually preheatTimerState
                    if (CookingAppUtils.isTimePreheatRunning(cookingViewModel)){
                        HMILogHelper.Logd("Time preheat is running, right text button will be set based on getCookTime ${cookingViewModel.cavityName.value}, cookTimerState $cookTimerState" )
                        return if (cookingViewModel.recipeExecutionViewModel.cookTime.value == 0L) context.resources.getString(
                            R.string.text_button_set
                        ) else context.resources.getString(
                            R.string.text_button_update
                        )
                    }
                    return if (cookTimerState == Timer.State.IDLE) startOrNextText else context.resources.getString(
                        R.string.text_button_update
                    )
                }

                else -> {
                    if (recipeExecutionState == RecipeExecutionState.IDLE) {
                        return if (isNext) context.resources.getString(R.string.text_button_next) else startOrNextText
                    }
                    return context.resources.getString(R.string.text_button_update)
                }
            }
        }

        /**
         * Use this method to find whether te fragment is able to show "wait for preheat", "remove timer" option or not
         * if cook timer is running show remove,in case for oven for wait for preheat
         * @param cookingViewModel cooking view model in scope
         * @param currentOption pass the current selection option to find next value of recipe options , if passed null then it would return first parameter
         */
        fun manageLeftButtonForRecipeOption(
            cookingViewModel: CookingViewModel?,
            currentOption: RecipeOptions?,
            textButton: TextButton?,
        ) {
            if (cookingViewModel == null) return
            if (textButton == null) return
            textButton.visibility = View.GONE
            textButton.isEnabled = false
            when (currentOption) {
                RecipeOptions.COOK_TIME -> {
                    val cookTimerState =
                        cookingViewModel.recipeExecutionViewModel.cookTimerState.value
                    if (cookingViewModel.isOfTypeMicrowaveOven) return
                    if(CookingAppUtils.isTimeBasedPreheatRecipe(cookingViewModel) || AbstractStatusFragment.isExtendedCookingForNonEditableCookTimeRecipe(cookingViewModel)){
                        HMILogHelper.Logd("Time preheat is running, hiding left button option for ${cookingViewModel.cavityName.value}, cookTimerState $cookTimerState" )
                        textButton.visibility = View.GONE
                        return
                    }
                    if (cookTimerState == Timer.State.RUNNING || cookTimerState == Timer.State.PAUSED) {
                        if(!CookingAppUtils.isCookTimeOptionMandatory(cookingViewModel)) {
                            HMILogHelper.Logd("Cook Time left text button is non mandatory, setting REMOVE TIMER text")
                            textButton.visibility = View.VISIBLE
                            textButton.isEnabled = true
                            textButton.setTextButtonText(textButton.context.getString(R.string.text_button_remove_timer))
                        }
                        return
                    }
                    if (CookingAppUtils.isRequiredTargetAvailable(
                            cookingViewModel, RecipeOptions.TARGET_TEMPERATURE
                        )
                    ) {
                        val cookingState =
                            cookingViewModel.recipeExecutionViewModel.recipeCookingState.value
                        if (cookingState == RecipeCookingState.PREHEATING) {
                            textButton.visibility = View.VISIBLE
                            textButton.setTextButtonText(textButton.context.getString(R.string.text_button_wait_for_preheat))
                            textButton.isEnabled = true
                            return
                        } else {
                            textButton.isEnabled = false
                            return
                        }
                    }
                }

                else -> {}
            }
        }

        /**
         * Method to get view safely
         * @return view of a given fragment or current visible fragment
         */
        fun getViewSafely(fragment: Fragment): View? {
            var view: View? = null
            var visibleFragment = getVisibleFragment()
            try {
                view = if (fragment is DialogFragment && visibleFragment != null) {
                    visibleFragment.requireView()
                } else {
                    fragment.requireView()
                }
                HMILogHelper.Logd("Returning require view")
            } catch (e: Exception) {
                HMILogHelper.Logd("caught exception, checking for parent fragment view")
                if (fragment.parentFragment != null) {
                    view = fragment.requireParentFragment().view
                }
            }
            if (view == null && visibleFragment != null) {
                view = visibleFragment.view
                HMILogHelper.Logd("Returning current visible view")
            }
            if (view == null) {
                visibleFragment =
                    CookingAppUtils.getVisibleFragment(kitchenAidLauncherActivity.supportFragmentManager)
                if (visibleFragment != null) {
                    view = visibleFragment.view
                    HMILogHelper.Logd("Returning current visible view from activity")
                }
            }
            return view
        }

        /**
         * Method to navigate screens safely
         * @param fragment current view instance
         * @param actionId destination id
         * @param bundle arguments
         * @param navOptions forward/backward animations
         * @return true/false
         */
        fun navigateSafely(
            fragment: Fragment,
            actionId: Int,
            bundle: Bundle?,
            navOptions: NavOptions?,
        ): Boolean {
            val view = getViewSafely(fragment)
            try {
                if (view != null) {
                    return if (navOptions != null) {
                        NavigationViewModel.navigateSafely(view, actionId, bundle, navOptions)
                    } else if (bundle != null) {
                        NavigationViewModel.navigateSafely(view, actionId, bundle)
                    } else {
                        NavigationViewModel.navigateSafely(view, actionId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                HMILogHelper.Loge(e.message?:"")
            }
            return false
        }

        /**
         * For Assisted Cooking to Check Day 1/ Day2 behavior based on the recipe options
         *
         * @param recipeRecord             recipe record to get the data from DB for previous value
         * @param recipeExecutionViewModel recipe view model to set the data
         * @return is Day1/ Day 2 based on the recipe data availability
         */
        fun isFirstTimeAssistedRecipeSelected(
            recipeRecord: RecipeRecord,
            recipeExecutionViewModel: RecipeExecutionViewModel,
        ): Boolean {
            val options = recipeExecutionViewModel.requiredOptions.value
            var isAnyFieldEmpty = false
            if (options != null) {
                for (option in options) {
                    if (isAnyFieldEmpty) return false
                    when (option) {
                        RecipeOptions.TARGET_TEMPERATURE -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.targetTemperature)

                        RecipeOptions.BROIL_POWER_LEVEL -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.broilPowerLevel)

                        RecipeOptions.COOK_TIME -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.cookTime)

                        RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> isAnyFieldEmpty =
                            TextUtils.isEmpty(
                                recipeRecord.targetMeatProbeTemperature
                            )

                        RecipeOptions.DONENESS -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.doneness)

                        RecipeOptions.PAN_SIZE -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.panSize)

                        RecipeOptions.AMOUNT -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.amount)

                        RecipeOptions.WEIGHT -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.weight)

                        RecipeOptions.PREHEAT -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.preheat)

                        RecipeOptions.WHEN_DONE_ACTION -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.whenDone)

                        RecipeOptions.MWO_POWER_LEVEL -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.mwoPowerLevel)

                        RecipeOptions.BROWNING -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.browning)

                        RecipeOptions.FOOD_SIZE -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.foodSize)

                        RecipeOptions.FOOD_TYPE -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.foodType)

                        RecipeOptions.STEAM_LEVEL -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.steamLevel)

                        RecipeOptions.RISING -> isAnyFieldEmpty =
                            TextUtils.isEmpty(recipeRecord.rising)

                        else -> return false
                    }
                }
                return !isAnyFieldEmpty
            }
            return false
        }

        /**
         * For Assisted Cooking to set Day2 data based on the recipe options
         *
         * @param recipeRecord             recipe record to get the data from DB for previous value
         * @param rvm recipe view model to set the data
         * To set Day 2 details for assisted cooking based on the recipe data availability
         */
        fun setSecondTimeAssistedRecipeSelection(
            recipeRecord: RecipeRecord,
            rvm: RecipeExecutionViewModel,
        ): Boolean {
            val options = rvm.requiredOptions.value
            var recipeErrorResponse: RecipeErrorResponse? = null
            if (options != null) {
                for (option in options) {
                    try {
                        if (recipeErrorResponse != null && recipeErrorResponse.isError) return false
                        when (option) {
                            RecipeOptions.TARGET_TEMPERATURE -> if (recipeRecord.targetTemperature.isNullOrBlank()) return false
                            else recipeErrorResponse =
                                rvm.setTargetTemperature(recipeRecord.targetTemperature!!.toFloat())

                            RecipeOptions.BROIL_POWER_LEVEL -> if (recipeRecord.broilPowerLevel.isNullOrBlank()) return false
                            else recipeErrorResponse =
                                rvm.setBroilPowerLevel(recipeRecord.broilPowerLevel!!.toInt())

                            RecipeOptions.COOK_TIME -> if (recipeRecord.cookTime.isNullOrBlank()) return false
                            else recipeErrorResponse =
                                rvm.setCookTime(recipeRecord.cookTime!!.toLong())

                            RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> if (recipeRecord.targetMeatProbeTemperature.isNullOrBlank()) return false
                            else recipeErrorResponse =
                                rvm.setTargetMeatProbeTemperature(recipeRecord.targetMeatProbeTemperature!!.toFloat())

                            RecipeOptions.DONENESS -> if (recipeRecord.doneness.isNullOrBlank()) return false
                            else recipeErrorResponse = rvm.setDoneness(recipeRecord.doneness)

                            RecipeOptions.PAN_SIZE -> if (recipeRecord.panSize.isNullOrBlank()) return false
                            else recipeErrorResponse = rvm.setPanSize(recipeRecord.panSize)

                            RecipeOptions.AMOUNT -> if (recipeRecord.amount.isNullOrBlank()) return false
                            else recipeErrorResponse =
                                rvm.setAmount(recipeRecord.amount!!.toDouble())

                            RecipeOptions.WEIGHT -> if (recipeRecord.weight.isNullOrBlank()) return false
                            else recipeErrorResponse =
                                rvm.setWeight(recipeRecord.weight!!.toFloat())

                            RecipeOptions.PREHEAT -> if (recipeRecord.preheat.isNullOrBlank()) return false
                            else recipeErrorResponse = rvm.setPreheat(recipeRecord.preheat)

                            RecipeOptions.WHEN_DONE_ACTION -> if (recipeRecord.whenDone.isNullOrBlank()) return false
                            else recipeErrorResponse = rvm.setWhenDone(recipeRecord.whenDone)

                            RecipeOptions.MWO_POWER_LEVEL -> if (recipeRecord.mwoPowerLevel.isNullOrBlank()) return false
                            else recipeErrorResponse =
                                rvm.setMwoPowerLevel(recipeRecord.mwoPowerLevel!!.toInt())

                            RecipeOptions.BROWNING -> if (recipeRecord.browning.isNullOrBlank()) return false
                            else recipeErrorResponse = rvm.setBrowning(recipeRecord.browning)

                            RecipeOptions.FOOD_SIZE -> if (recipeRecord.foodSize.isNullOrBlank()) return false
                            else recipeErrorResponse =
                                rvm.setFoodSize(recipeRecord.foodSize!!.toDouble())

                            RecipeOptions.FOOD_TYPE -> if (recipeRecord.foodType.isNullOrBlank()) return false
                            else recipeErrorResponse = rvm.setFoodType(recipeRecord.foodType)

                            RecipeOptions.STEAM_LEVEL -> if (recipeRecord.steamLevel.isNullOrBlank()) return false
                            else recipeErrorResponse = rvm.setSteamLevel(recipeRecord.steamLevel)

                            RecipeOptions.RISING -> if (recipeRecord.rising.isNullOrBlank()) return false
                            else recipeErrorResponse = rvm.setRising(recipeRecord.rising)

                            else -> {
                                return false
                            }
                        }
                    } catch (exception: Exception) {
                        HMILogHelper.Loge(
                            TAG, "error in setting assisted stored parameters= ${exception.message}"
                        )
                        exception.printStackTrace()
                    }
                }
            }
            if (options == null || recipeErrorResponse == null) return false
            return !recipeErrorResponse.isError
        }

        /**
         * This function takes two arguments:
         * @param navController: The NavController instance for your navigation graph.
         * @param destinationId: The ID of the destination you want to navigate to.
         * It compares the id of the currentDestination with the destinationId.
         * The function returns true if the IDs are not equal, indicating navigation is necessary.
         * It returns false if the IDs are equal, indicating you're already at the desired destination.
         */
        fun shouldNavigate(navController: NavController, destinationId: Int): Boolean {
            return navController.currentDestination?.id != destinationId
        }

        /**
         * common navigation method to navigate to upper cavity recipe selection fragment
         * useful to determine if probe is connected then go to probe recipe selection
         * @param fragment
         */
        fun navigateToUpperRecipeSelection(fragment: Fragment) {
            val primaryCookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
            val navigateSuccess: Boolean
            CookingViewModelFactory.setInScopeViewModel(primaryCookingViewModel)
            if (primaryCookingViewModel.isOfTypeOven && MeatProbeUtils.isMeatProbeConnected(
                    primaryCookingViewModel
                )
            ) {
                HMILogHelper.Logd(
                    TAG,
                    "primaryCavity MeatProbe is connected, navigating to probe selection recipe"
                )
                navigateSuccess = navigateSafely(
                    fragment, R.id.action_to_probeCyclesSelectionFragment, null, null
                )
            } else {
                HMILogHelper.Logd(TAG, "primaryCavity, navigating to all selection recipe")
                navigateSuccess = if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN
                    || CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN) {
                    navigateSafely(
                        fragment, R.id.action_global_cavity_selection, null, null
                    )
                } else {
                    navigateSafely(
                        fragment, R.id.action_to_recipeSelectionFragment, null, null
                    )
                }
            }
            if (navigateSuccess) CookingViewModelFactory.setInScopeViewModel(
                primaryCookingViewModel
            )
        }
        /**
         * common navigation method to navigate to lower cavity recipe selection fragment
         * useful to determine if probe is connected then go to probe recipe selection
         * @param fragment
         */
        fun navigateToLowerRecipeSelection(fragment: Fragment) {
            val secondaryCookingViewModel = CookingViewModelFactory.getSecondaryCavityViewModel()
            val navigateSuccess: Boolean
            if (MeatProbeUtils.isMeatProbeConnected(
                    secondaryCookingViewModel
                )
            ) {
                HMILogHelper.Logd(
                    TAG,
                    "secondaryCavity MeatProbe is connected, navigating to probe selection recipe"
                )
                navigateSuccess = navigateSafely(
                    fragment, R.id.action_to_probeCyclesSelectionFragment, null, null
                )
            } else {
                HMILogHelper.Logd(TAG, "secondaryCavity, navigating to all selection recipe")
                navigateSuccess = navigateSafely(
                    fragment, R.id.action_to_recipeSelectionFragment, null, null
                )
            }
            if (navigateSuccess) CookingViewModelFactory.setInScopeViewModel(
                secondaryCookingViewModel
            )
        }

        /**
         * common navigation method to navigate to Steam Clean Instruction
         * useful to determine if probe is connected then show probe incompatible popup
         * @param fragment
         */
        fun navigateToSteamCleanInstruction(
            fragment: Fragment,
            cookingViewModel: CookingViewModel
        ) {
            if (MeatProbeUtils.isMeatProbeConnected(
                    cookingViewModel
                )
            ) {
                HMILogHelper.Logd(
                    TAG,
                    "secondaryCavity MeatProbe is connected, navigating to probe selection recipe"
                )
                //TODO:show probe popup
            } else {
                HMILogHelper.Logd(TAG, "secondaryCavity, navigating to all selection recipe")
                CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
                navigateSafely(
                    fragment, R.id.global_action_to_steamInstructionFragment, null, null
                )
            }
        }

        /**
         * navigate to delay tumbler fragment only of date and time is set
         *
         * @param fragment visible fragment
         */
        fun navigateToDelayScreen(fragment: Fragment, bundle: Bundle? = null) {
            navigateSafely(
                fragment, R.id.action_to_delayTumblerFragment, bundle, null
            )
        }

        fun navigateAfterFavoriteSelection(
            fragment: Fragment,
            cookingViewModel: CookingViewModel,
            recipeName: String?,
            isKnobClick: Boolean = false
        ) {
            HMILogHelper.Logd("recipeName $recipeName, isKnobClick $isKnobClick")
            try {
                val recipeRecord: RecipeRecord =
                    CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
                        recipeName, cookingViewModel.cavityName.value
                    )

                val recipeErrorResponse =
                    cookingViewModel.recipeExecutionViewModel.load(recipeRecord)
                HMILogHelper.Logd("$TAG loadRecipe", recipeErrorResponse.name)
                if (!recipeErrorResponse.isError) {
                    navigateNextForFavoritesRecipe(fragment, cookingViewModel, null, isKnobClick)
                } else {
                    HMILogHelper.Loge("$TAG loadRecipe", recipeErrorResponse.description)
                }
            } catch (exception: Exception) {
                HMILogHelper.Loge(
                    "$TAG loadRecipe",
                    "error in loading recipeName=${recipeName} with message ${exception.message}"
                )
                ToastUtils.showToast(getViewSafely(fragment)?.context, "Under Development")
            }
        }
        fun navigateNextForFavoritesRecipe (
            fragment: Fragment,
            inScopeViewModel: CookingViewModel,
            currentOption: RecipeOptions?,
            isKnobClick: Boolean = false) {

            val navigationAction = getNextNavigationForFavorites( inScopeViewModel, currentOption, isKnobClick)

            if (fragment.arguments?.getString(BundleKeys.BUNDLE_IS_FROM_PREVIEW_SCREEN)
                    ?.contentEquals("popToPreview") == true
            ) {
                navigateSafely(
                    fragment, R.id.action_to_favoritesPreviewFragment, null, null
                )
                return
            }

            if (navigationAction == 0) {
                navigateSafely(
                    fragment, R.id.action_to_favoritesPreviewFragment, null, null
                )
                return
            }
            navigateSafely(
                fragment, navigationAction, null, null
            )
        }

     private fun getNextNavigationForFavorites(
            cookingViewModel: CookingViewModel?,
            currentOption: RecipeOptions?,
            isKnobClick: Boolean = false,
        ): Int {
            HMILogHelper.Logd("currentOption: $currentOption, isKnobClick $isKnobClick")
            if (cookingViewModel == null) return -1
            var nextOption : RecipeOptions? = null
            getRecipeOptions().let {
                    if (it.isNotEmpty()) {
                        nextOption = if (currentOption in it) {
                            getNextOption(
                                currentOption.toString(),
                                it
                            )
                        } else {
                            it.first()
                        }
                    }
            }

         HMILogHelper.Logd("nextOption: $nextOption, isKnobClick $isKnobClick")
            return when (nextOption) {
                RecipeOptions.TARGET_TEMPERATURE -> {
                    if (CookingAppUtils.isTemperatureMapTextValue(cookingViewModel.recipeExecutionViewModel?.targetTemperatureOptions?.value)) {
                        R.id.action_to_favoritesDurationSelectionTumbler
                    } else {
                        if (isKnobClick)
                            R.id.action_to_favoritesTemperatureTumblerFragment
                        else
                            R.id.action_to_favoritesTemperatureNumPadFragment
                    }
                }

                RecipeOptions.MWO_POWER_LEVEL -> {
                    R.id.action_to_favoritesMicrowavePowerTumblerFragment
                }

                RecipeOptions.COOK_TIME -> {
                    if (isKnobClick)
                        R.id.action_to_favoritesCookTimeTumbler
                    else
                        R.id.action_to_favoritesCookTimeNumberPadFragment
                }

                RecipeOptions.DONENESS -> {
                    R.id.action_to_favoritesDoneNessFragment
                }

                RecipeOptions.WEIGHT -> {
                    R.id.action_to_favoritesWeightFragment
                }

                RecipeOptions.AMOUNT -> {
                    R.id.action_to_favoritesAmountFragment
                }

                RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> {
                    R.id.action_to_probeTemperatureTumbler
                }
                else -> 0
            }
        }
        private  fun getNextOption(currentOption: String, options: List<RecipeOptions>): RecipeOptions? {
            if (options.isEmpty()) return null

            val currentIndex = options.indexOfFirst { it.name == currentOption }

            // Check if the currentOption was found and it's not the last option
            return if (currentIndex in 0 until options.size - 1) {
                options[currentIndex + 1]
            } else {
                null
            }
        }

        fun navigateToShowInstructionFragment(activity: FragmentActivity?){
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                navigateSafely(
                    it,
                    R.id.global_action_to_showInstructionFragment,
                    null,
                    null
                )
            }
        }

        fun navigateToShowAssistedInstructionFragment(activity: FragmentActivity?){
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                val bundle = Bundle()
                bundle.putString(BUNDLE_NAVIGATED_FROM, MORE_OPTIONS)
                navigateSafely(
                    it,
                    R.id.global_action_to_showAssistedInstructionFragment,
                    bundle,
                    null
                )
            }
        }
        private fun startProbeRecipeOrPopUp(cookingViewModel:CookingViewModel, fragment: Fragment) {
            if (MeatProbeUtils.isMeatProbeConnected(cookingViewModel))
                startProbeRecipe(cookingViewModel,fragment)
            else {
                //Obsevation fixes - Activity is navigating to Clock screen but the pop-up us still there so updated the timeout
                if (fragment is SuperAbstractTimeoutEnableFragment){
                    fragment.updateTimeoutValue(TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_STARTED,AppConstants.TIME_OUT_STOP)
                }
                PopUpBuilderUtils.insertMeatProbe(
                    fragment,
                    cookingViewModel,
                    onMeatProbeConditionMet = {
                        // once user insert the probe then reset the timeout to the default provided time
                        if (fragment is SuperAbstractTimeoutEnableFragment){
                            fragment.updateTimeoutValue(TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_STARTED,fragment.provideScreenTimeoutValueInSeconds())
                        }
                        startProbeRecipe(cookingViewModel, fragment)
                    })
            }
        }

        /**
         * start recipe related to assisted cycle only here
         *
         */
        private fun startProbeRecipe(cookingViewModel: CookingViewModel, fragment: Fragment) {
            val recipeExecuteErrorResponse =
                cookingViewModel.recipeExecutionViewModel.execute()
            if (recipeExecuteErrorResponse.isError) {
                CookingAppUtils.handleCookingError(
                    fragment,
                    cookingViewModel,
                    recipeExecuteErrorResponse,
                    false,
                    onMeatProbeConditionMet = { startProbeRecipe(cookingViewModel, fragment) }
                )
                return
            }else{
                HMIExpansionUtils.startOrStopKnobLEDFadeInLightAnimation(true)
            }
            CookingAppUtils.navigateToStatusOrClockScreen(fragment)
        }
    }
}