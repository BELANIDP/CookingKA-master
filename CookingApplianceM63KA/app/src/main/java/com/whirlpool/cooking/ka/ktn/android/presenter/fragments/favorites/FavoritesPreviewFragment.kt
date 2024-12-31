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
import com.whirlpool.hmi.utils.cookbook.records.FavoriteRecord
import core.utils.AppConstants
import core.utils.AppConstants.DEFAULT_FAVORITE_NAME
import core.utils.AppConstants.DIGIT_ZERO
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AppConstants.FAVORITE_DEFAULT_IMAGE
import core.utils.AppConstants.KEY_FAVORITE_FROM
import core.utils.AppConstants.KEY_FAVORITE_NAME
import core.utils.AppConstants.NAVIGATION_FROM_CREATE_FAV
import core.utils.AppConstants.NAVIGATION_FROM_EXISTING_FAV
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.compareFavoriteRecordAndViewModel
import core.utils.CookingAppUtils.Companion.getRecipeOptions
import core.utils.CookingAppUtils.Companion.isCreateFavorite
import core.utils.CookingAppUtils.Companion.isRecipeOptionAvailable
import core.utils.CookingAppUtils.Companion.setNavigatedFrom
import core.utils.DoorEventUtils
import core.utils.FavoriteDataHolder
import core.utils.FavoritesPopUpUtils
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateToDelayScreen
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils
import core.utils.PopUpBuilderUtils
import core.utils.ToolsMenuJsonKeys
import java.util.Locale

/**
 * File       : [android.presenter.fragments.favorites.FavoritesPreviewFragment]
 * Brief      : Preview screen for favorite recipe selection
 * Author     : PANDES18
 * Created On : 04/10/2024
 * Details    : To show favorite recipe selection tile list
 */
class FavoritesPreviewFragment : AbstractPreviewFragment() {

    private val listOfTiles = arrayListOf<PreviewTileItem>()
    private lateinit var recipeExecutionViewModel: RecipeExecutionViewModel
    private lateinit var favoriteRecord: FavoriteRecord
    private var isRecordUnchanged : Boolean = false
    private var imageURI : String = EMPTY_STRING

    /**
     * Method for setting the decision tiles data.
     * To be overridden  by derived class to update the recycler view data based on the use case
     *
     * @return ArrayList<DecisionTileData>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeExecutionViewModel =
            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
        // check navigated from and accordingly update the favorite record
        initializeFavoriteRecord()
    }

    override fun onResume() {
        super.onResume()
        initializeFavoriteRecord()
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
                getString(R.string.text_subHeader_mode),
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
                        arrayOf(R.string.text_label_HR, R.string.text_label_MIN, R.string.text_label_SEC)
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
    override fun provideHeaderBarTitleText(): String = getFavoriteName()

    override fun provideHeaderBarLeftIconVisibility() = false

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
    override fun provideHeaderBarInfoIconVisibility() = true


    override fun infoIconOnClick() {

        NavigationUtils.navigateSafely(
            this,
            R.id.action_favoritesPreviewFragment_to_keyboardFragment,
            Bundle().apply {
                putString(KEY_FAVORITE_NAME, provideHeaderBarTitleText())
                putSerializable(KEY_FAVORITE_FROM, AppConstants.FavoriteFrom.PREVIEW_SCREEN)
            },
            null
        )
    }

    /**
     * provide visibility of left action button, ex delay not applicable to mwo cycles
     * @return true if VISIBLE false if GONE
     */
    override fun provideLeftActionButtonVisibility() = provideLeftActionButtonText().isNotEmpty()

    override fun provideInfoIconRes(): Int {
        return R.drawable.ic_edit
    }

    /**
     * Handle click event when Right button is pressed
     */
    override fun providePrimaryActionButtonClickEvent() {
        if (isCreateFavorite()) {
            saveFavoriteRecord()
        } else {
            startFavoriteRecord()
        }
    }


    /**
     * provide the txt to display on button right/primary
     * @return text to show on right action button like start
     */
    override fun providePrimaryActionButtonText(): CharSequence {
        return if (isCreateFavorite())
            getString(R.string.text_button_save)
        else
            getString(R.string.text_button_start).uppercase(Locale.getDefault())
    }

    /**
     * Handle click event when Left button is pressed for ex. start or next
     */
    override fun provideLeftActionButtonClickEvent() {
        if (isCreateFavorite()) {
            NavigationUtils.navigateSafely(this, R.id.action_favoritesPreviewFragment_to_chooseSmallImageFragment, null, null)
        } else {
            setNavigatedFrom(EMPTY_STRING)
            navigateToDelayScreen(this)
        }
     }

    /**
     * provide the txt to display on button left
     * @return text to show on left action button like delay, cancel
     */
    override fun provideLeftActionButtonText(): String {
        FavoriteDataHolder.selectedImageIndex?.let {
            imageURI = FavoritesPopUpUtils.favoritesImageNames[it]
        }
        return when {
            isCreateFavorite() -> {
                if (imageURI.isNotBlank()) {
                    getString(R.string.text_button_update_image)
                } else {
                    getString(R.string.text_button_choose_image)
                }
            }
            isRecipeOptionAvailable(recipeExecutionViewModel, RecipeOptions.DELAY_TIME) -> {
                getString(R.string.text_button_delay)
            }
            else -> {
                EMPTY_STRING
            }
        }
    }

    /**
     * Handles user click on recipe attribute tiles
     * @param: view-Current View ; position: Index of tile being clicked
     */
    override fun onPreviewTileClick(view: View, position: Int) {
        var navigationAction = 0
        var isAlreadySavedFavorite = false

        //Added to facilitate navigation in case of editing already saved favorite.
        if (recipeExecutionViewModel.favoriteName.value.toString().isNotEmpty() && position == DIGIT_ZERO && isRecordUnchanged){
            isAlreadySavedFavorite = true
            setNavigatedFrom(NAVIGATION_FROM_EXISTING_FAV)
        }
        var bundle : Bundle? = null
        when (listOfTiles[position].recipeOptions) {
            RecipeOptions.TARGET_TEMPERATURE -> {
                navigationAction =
                    if (CookingAppUtils.isTemperatureMapTextValue(
                            cookingViewModel.recipeExecutionViewModel?.targetTemperatureOptions?.value
                        )
                    ) R.id.action_to_favoritesDurationSelectionTumbler
                    else R.id.action_to_favoritesTemperatureNumPadFragment

                bundle = Bundle().apply {
                    cookingViewModel.recipeExecutionViewModel.targetTemperature.value?.toInt()
                        ?.let { putInt(BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE, it) }
                }
            }

            RecipeOptions.DONENESS -> {
                navigationAction = if (position != DIGIT_ZERO) {
                    R.id.action_to_favoritesDoneNessFragment
                } else {
                    setNavigatedFrom(if (isAlreadySavedFavorite) NAVIGATION_FROM_EXISTING_FAV else NAVIGATION_FROM_CREATE_FAV)
                    if (CookingAppUtils.isRecipeAssisted(
                            cookingViewModel.recipeExecutionViewModel.recipeName.value,
                            favoriteRecord.cavity
                        )
                    ) {
                        R.id.action_to_favoritesFoodMainCategoryGridFragment
                    } else {
                        R.id.action_to_favoriteRecipeSelectionFragment
                    }
                }
            }

            RecipeOptions.COOK_TIME -> {
                navigationAction = R.id.action_to_favoritesCookTimeNumberPadFragment
            }

            RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE -> {
                navigationAction = R.id.action_to_meatProbeTemperatureTumblerFragment
            }

            RecipeOptions.MWO_POWER_LEVEL -> {
                navigationAction = R.id.action_to_favoritesMicrowavePowerTumblerFragment
            }

            RecipeOptions.AMOUNT -> {
                navigationAction = R.id.action_to_favoritesAmountFragment
            }

            RecipeOptions.WEIGHT -> {
                navigationAction = R.id.action_to_favoritesWeightFragment
            }

            else -> {
                HMILogHelper.Loge(
                    tag,
                    "No recipeOption ${listOfTiles[position].recipeOptions} action specified for navigation"
                )
            }
        }
        if (navigationAction != 0)
            NavigationUtils.navigateSafely(this, navigationAction, bundle, null)
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

    private fun updateFavoriteRecord(): FavoriteRecord {
        val record = CookingAppUtils.updateParametersInFavoriteRecord(
            favoriteRecord,
            getRecipeOptions(),
            recipeExecutionViewModel
        )
        record.recipeId = recipeExecutionViewModel.recipeId
        record.cavity = CookingViewModelFactory.getInScopeViewModel().cavityName.value
        return record
    }

    private fun checkNameInList(favoriteRecordsList: List<FavoriteRecord>, nameToCheck: String): Boolean {
        for (favoriteRecord in favoriteRecordsList) {
            if (favoriteRecord.favoriteName == nameToCheck) {
                return true // Return true as soon as a match is found
            }
        }
        return false // Return false if no match is found
    }

    private fun isFavoriteNameUpdated() : Boolean {
        if (arguments?.getString(KEY_FAVORITE_NAME)?.isNotEmpty() == true) {
            return favoriteRecord.favoriteName?.equals(
                arguments?.getString(
                    KEY_FAVORITE_NAME
                ).toString()) != true
        }
        return false
    }

    private fun getFavoriteName() : String {
        return if (arguments?.getString(KEY_FAVORITE_NAME)?.isNotEmpty() == true && isFavoriteNameUpdated()) {
            CookingAppUtils.updateRecordName(arguments?.getString(
                KEY_FAVORITE_NAME
            ).toString())
        } else {
            if (!TextUtils.isEmpty(FavoriteDataHolder.favoriteName)){
                FavoriteDataHolder.favoriteName
            } else if (!TextUtils.isEmpty(favoriteRecord.favoriteName)) {
                CookingAppUtils.updateRecordName(favoriteRecord.favoriteName)
            } else {
                CookingAppUtils.updateRecordName(DEFAULT_FAVORITE_NAME)
            }
        }
    }

    private fun saveFavoriteRecord() {
        val cookBookViewModel = CookBookViewModel.getInstance()
        favoriteRecord.imageUrl = imageURI.ifEmpty { FAVORITE_DEFAULT_IMAGE }
        // Load record with the Name currently selected by user
        favoriteRecord.favoriteName = provideHeaderBarTitleText()

        var isUpdateFavorite = false
        //check if the favorite with the same name exists in the allFavoriteRecords
        cookBookViewModel.allFavoriteRecords.value?.let { favoriteRecord.favoriteName?.let { it1 ->
            isUpdateFavorite = checkNameInList(it,
                it1
            )

            for (favoriteRecord in it){
                isRecordUnchanged = isRecordUnchanged || compareFavoriteRecordAndViewModel( favoriteRecord, getRecipeOptions(), recipeExecutionViewModel)
                if (isRecordUnchanged) break
            }
        } }.let {
            if (isUpdateFavorite){
                //Update the parameters in the favoriteRecord before updating the database copy
                updateFavoriteRecord()
                cookBookViewModel.updateFavoriteRecord(favoriteRecord)
            } else {
                if (isRecordUnchanged){
                    //show favorite with same parameters exist.
                    FavoritesPopUpUtils.favoriteAlreadyExist(this@FavoritesPreviewFragment) {}
                } else {
                    //New Favorite Record, to be inserted in database.
                    cookBookViewModel.insertFavoriteRecord(favoriteRecord)
                }
            }
        }
        FavoriteDataHolder.selectedImageIndex = null
        FavoriteDataHolder.favoriteName = EMPTY_STRING

        // Dismiss notification save to favorite
        NotificationManagerUtils.removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_SAVE_LAST_RECIPE_FAVORITE)
        NotificationManagerUtils.removeNotificationFromNotificationCenter(NotificationJsonKeys.NOTIFICATION_SAVE_LAST_RECIPE_FAVORITE)

        FavoriteDataHolder.favoriteRecord = FavoriteRecord()

        // cancel if any cycle running, to avoid issue while coming from cycle completed state.
        if (CookingAppUtils.isAnyCycleRunning()) {
            CookingAppUtils.navigateToStatusOrClockScreen(this@FavoritesPreviewFragment)
        }
        //After Updating the favorite records in database Navigate to Favorite Landing
        NavigationUtils.navigateSafely(
            this,
            R.id.action_favoritesPreviewFragment_to_favoriteLandingFragment,
            null,
            null
        )
    }

    private fun startFavoriteRecord() {
        //to clear flag data on favorite start press
        resetPersistentData()
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

    private fun startProbeRecipe() {
        val recipeExecuteErrorResponse =
            cookingViewModel.recipeExecutionViewModel.execute()
        HMILogHelper.Logd(tag, "start recipe from assistedPreview ${recipeExecuteErrorResponse.description}")
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

    private fun resetPersistentData() {
        setNavigatedFrom(EMPTY_STRING)
        FavoriteDataHolder.favoriteName = EMPTY_STRING
        FavoriteDataHolder.favoriteRecord = FavoriteRecord()
    }

    private fun initializeFavoriteRecord() {
        favoriteRecord = FavoriteDataHolder.favoriteRecord
        if (isCreateFavorite()) {
            favoriteRecord = updateFavoriteRecord()
        } else {
            favoriteRecord = if (TextUtils.isEmpty(recipeExecutionViewModel.favoriteName.value)) {
                CookBookViewModel.getInstance()
                    .getFavoriteRecordByFavoriteName(FavoriteDataHolder.favoriteName)
            } else {
                CookBookViewModel.getInstance()
                    .getFavoriteRecordByFavoriteName(recipeExecutionViewModel.favoriteName.value)
            }
            isRecordUnchanged = compareFavoriteRecordAndViewModel(
                favoriteRecord,
                getRecipeOptions(),
                recipeExecutionViewModel
            )

            if (!isRecordUnchanged || isFavoriteNameUpdated()) {
                setNavigatedFrom(NAVIGATION_FROM_CREATE_FAV)
            } else {
                setNavigatedFrom(NAVIGATION_FROM_EXISTING_FAV)
            }
        }
    }
}