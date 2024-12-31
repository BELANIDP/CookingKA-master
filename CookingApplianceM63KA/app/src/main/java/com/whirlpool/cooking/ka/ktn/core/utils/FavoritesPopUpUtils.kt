package core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.text.SpannableStringBuilder
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.cookbook.records.FavoriteRecord
import core.utils.AppConstants.COMMON_POPUP_DESCRIPTION_WIDTH
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AppConstants.FALSE_CONSTANT
import core.utils.AppConstants.HEADER_VIEW_CENTER_ICON_GONE
import core.utils.AppConstants.PRIMARY_CAVITY_KEY
import core.utils.CookingAppUtils.Companion.updatePopUpLeftTextButtonBackground
import core.utils.CookingAppUtils.Companion.updatePopUpRightTextButtonBackground
import core.utils.PopUpBuilderUtils.Companion.observeHmiKnobListener

/**
 * File       : [core.utils.FavoritesPopUpUtils]
 * Brief      :   Utility class for favorite pop up and images
 * Author     : PANDES18
 * Created On : 14/10/2024
 */
object FavoritesPopUpUtils {
    fun maxFavoriteReached(
        fragmentManager: FragmentManager?,
        visibleFragment: Fragment?
    ) {
        val dialogPopupBuilder: ScrollDialogPopupBuilder =
            ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(R.string.text_header_favorite__max_limit)
                .setDescriptionMessage(descriptionText = R.string.text_description_favorite__max_limit)
                .setCancellableOutSideTouch(false)
                .setRightButton(R.string.text_button_ok) {
                    false
                }
                .build()


        //Knob Implementation
        val hmiKnobListener = observeHmiKnobListener(
            onKnobRotateEvent = { _, _ ->
            }, onHMIRightKnobClick = {
                dialogPopupBuilder.dismiss()
            }, onKnobSelectionTimeout = {
            }
        )

        dialogPopupBuilder.setOnDialogCreatedListener(object :
            ScrollDialogPopupBuilder.OnDialogCreatedListener {
            override fun onDialogCreated() {
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
            }

            override fun onDialogDestroy() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
            }
        })
        fragmentManager?.let { dialogPopupBuilder.show(it, "maxFavoriteReached") }
        visibleFragment?.resources?.getInteger(R.integer.to_do_popup_timeout)?.let {
            dialogPopupBuilder.setTimeoutCallback(onTimeoutObserverListener = {
                dialogPopupBuilder.dismiss()
            }, it)
        }
    }

    fun deleteFavorite(
        visibleFragment: Fragment,
        descriptionMessage: SpannableStringBuilder,
        recordId: Int,
        onNoClick: () -> Unit,
        onclickYes: () -> Unit
    ) {
        val dialogPopupBuilder: ScrollDialogPopupBuilder =
            ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(R.string.text_header_delete_favorite)
                .setSpannableDescriptionMessage(descriptionMessage)
                .setLeftButton(R.string.text_button_no) {
                    onNoClick.invoke()
                    false
                }
                .setRightButton(R.string.text_button_yes) {
                    if (SharedPreferenceManager.getKnobAssignFavoritesCycleNameIntoPreference()
                            .equals(
                                CookBookViewModel.getInstance()
                                    .getFavoriteRecordByFavoriteId(recordId).favoriteName
                            )
                    ) {
                        SharedPreferenceManager.setKnobAssignFavoritesCycleNameIntoPreference(
                            EMPTY_STRING
                        )
                        SharedPreferenceManager.setKnobAssignFavoritesCycleStatusIntoPreference(
                            FALSE_CONSTANT
                        )
                    }
                    CookBookViewModel.getInstance().deleteFavoriteRecordById(recordId)
                    onclickYes.invoke()
                    false
                }
                .build()

        dialogPopupBuilder.setTimeoutCallback(
            {
                dialogPopupBuilder.dismiss()
            }, visibleFragment.resources.getInteger(R.integer.to_do_popup_timeout)
        )
        //Knob Implementation
        var knobRotationCount = 0
        val hmiKnobListener = observeHmiKnobListener(
            onKnobRotateEvent = { knobId, knobDirection ->
                if (knobId == AppConstants.RIGHT_KNOB_ID) {
                    if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                    else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            dialogPopupBuilder.provideViewHolderHelper()?.rightTextButton?.background =
                                null
                            updatePopUpLeftTextButtonBackground(
                                visibleFragment,
                                dialogPopupBuilder,
                                R.drawable.selector_textview_walnut
                            )
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            dialogPopupBuilder.provideViewHolderHelper()?.leftTextButton?.background =
                                null
                            updatePopUpRightTextButtonBackground(
                                visibleFragment,
                                dialogPopupBuilder,
                                R.drawable.selector_textview_walnut
                            )
                        }
                    }
                }
            },
            onHMIRightKnobClick = {
                when(knobRotationCount){
                    AppConstants.KNOB_COUNTER_TWO ->{
                        dialogPopupBuilder.onHMIRightKnobClick()
                    }
                    AppConstants.KNOB_COUNTER_ONE ->{
                        dialogPopupBuilder.onHMILeftKnobClick()
                    }
                }
            },
            onKnobSelectionTimeout = {}
        )
        dialogPopupBuilder.setOnDialogCreatedListener(object :
            ScrollDialogPopupBuilder.OnDialogCreatedListener {
            override fun onDialogCreated() {
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
            }

            override fun onDialogDestroy() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
            }
        })
        dialogPopupBuilder.show(
            visibleFragment.parentFragmentManager,
            "deleteFavorite"
        )
    }

    fun leaveImageSelection(
        visibleFragment: Fragment,
        onclickYes: () -> Unit
    ) {
        val dialogPopupBuilder: ScrollDialogPopupBuilder =
            ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(R.string.text_header_cancel_image_selection)
                .setDescriptionMessage(R.string.text_description_cancel_image_selection)
                .setLeftButton(R.string.text_button_no) {
                    false
                }
                .setRightButton(R.string.text_button_yes) {
                    onclickYes.invoke()
                    false
                }
                .build()

        dialogPopupBuilder.setTimeoutCallback(
            { dialogPopupBuilder.dismiss() },
            visibleFragment.resources.getInteger(R.integer.to_do_popup_timeout)
        )
        //Knob Implementation
        var knobRotationCount = 0
        val hmiKnobListener = observeHmiKnobListener(
            onKnobRotateEvent = { knobId, knobDirection ->
                if (knobId == AppConstants.RIGHT_KNOB_ID) {
                    if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                    else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            dialogPopupBuilder.provideViewHolderHelper()?.rightTextButton?.background =
                                null
                            updatePopUpLeftTextButtonBackground(
                                visibleFragment,
                                dialogPopupBuilder,
                                R.drawable.selector_textview_walnut
                            )
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            dialogPopupBuilder.provideViewHolderHelper()?.leftTextButton?.background =
                                null
                            updatePopUpRightTextButtonBackground(
                                visibleFragment,
                                dialogPopupBuilder,
                                R.drawable.selector_textview_walnut
                            )
                        }
                    }
                }
            },
            onHMIRightKnobClick = {
                dialogPopupBuilder.onHMIRightKnobClick()
            },
            onKnobSelectionTimeout = {},
            onHMILeftKnobClick = {
                dialogPopupBuilder.onHMILeftKnobClick()
            }
        )
        dialogPopupBuilder.setOnDialogCreatedListener(object :
            ScrollDialogPopupBuilder.OnDialogCreatedListener {
            override fun onDialogCreated() {
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
            }

            override fun onDialogDestroy() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
            }
        })
        dialogPopupBuilder.show(
            visibleFragment.parentFragmentManager,
            "leaveImageSelection"
        )
    }

    fun favoriteAlreadyExist(
        visibleFragment: Fragment,
        onclickOkay: () -> Unit
    ) {
        val dialogPopupBuilder: ScrollDialogPopupBuilder =
            ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_probe_fragment)
                .setHeaderTitle(R.string.text_header_favorite_exist)
                .setDescriptionMessage(R.string.text_desription_favorite_exist)
                .setHeaderViewCenterIcon(HEADER_VIEW_CENTER_ICON_GONE, false)
                .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_MARGIN_114PX)
                .setTopMarginForDescriptionText(AppConstants.POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                .setRightButton(R.string.text_button_ok) {
                    onclickOkay.invoke()
                    false
                }
                .build()

        dialogPopupBuilder.setTimeoutCallback(
            { dialogPopupBuilder.dismiss() },
            visibleFragment.resources.getInteger(R.integer.to_do_popup_timeout)
        )
        //Knob Implementation
        val hmiKnobListener = observeHmiKnobListener(
            onHMIRightKnobClick = {
                dialogPopupBuilder.onHMIRightKnobClick()
            }, onKnobSelectionTimeout = {}
        )
        dialogPopupBuilder.setOnDialogCreatedListener(object :
            ScrollDialogPopupBuilder.OnDialogCreatedListener {
            override fun onDialogCreated() {
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
            }

            override fun onDialogDestroy() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
            }
        })
        dialogPopupBuilder.show(
            visibleFragment.parentFragmentManager,
            "leaveImageSelection"
        )
    }

    @SuppressLint("StringFormatInvalid")
    fun showCavityRunningPopup(
        fragment: Fragment,
        recipeExecutionViewModel: RecipeExecutionViewModel,
        cavity: String?
    ) {
        val recipeName = CookingAppUtils.getRecipeNameText(
            fragment.requireContext(),
            recipeExecutionViewModel.recipeName.value.toString()
        )
        val cavityLabel = updateCavityLabel(cavity,fragment)

        val dialogPopupBuilder =
            ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(
                    fragment.resources.getString(R.string.popupAlreadyRunningTitle,recipeName)
                )
                .setDescriptionMessage(
                    fragment.resources.getString(R.string.popupAlreadyRunningDescription,
                        recipeName,
                        cavityLabel
                    )
                )
                .setWidthForDescriptionText(COMMON_POPUP_DESCRIPTION_WIDTH)
                .setRightButton(R.string.text_button_ok) {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    CookingAppUtils.dismissDialogAndNavigateToStatusOrClockScreen(fragment)
                    true
                }.build()
        dialogPopupBuilder.show(fragment.parentFragmentManager, "showOtherFeatureRunningPopup")
    }

    private fun updateCavityLabel(cavity: String?, fragment: Fragment) : String {
        when(CookingViewModelFactory.getProductVariantEnum()){
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                return fragment.resources.getString(R.string.cavity_selection_oven_all_caps)
            }
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                return if (cavity?.equals(PRIMARY_CAVITY_KEY) == true) {
                    fragment.resources.getString(R.string.cavity_selection_upper_oven_all_caps)
                } else {
                    fragment.resources.getString(R.string.cavity_selection_lower_oven_all_cap)
                }
            }
            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                return if (cavity?.equals(PRIMARY_CAVITY_KEY) == true) {
                    fragment.resources.getString(R.string.cavity_selection_microwave_all_caps)
                } else {
                    fragment.resources.getString(R.string.cavity_selection_lower_oven_all_cap)
                }
            }
            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                return fragment.resources.getString(R.string.cavity_selection_microwave_all_caps)
            }
            else -> {
                return fragment.resources.getString(R.string.cavity_selection_oven_all_caps)
            }
        }
    }
    /**
     *Images set to be used for favorite images
     */
    val favoritesImageNames = listOf(
        "favoritebakedgoods",
        "favoritemeat1",
        "favoritesnacks",
        "favoritecasseroles",
        "favoritemeat2",
        "favoriteveggies",
        "favoritepoultry",
        "favoritedesserts",
        "favoriteseafood",
        "favoritedefault"
    )

    /**
     *Images set to be used for Square favorite images
     */
    val favoritesSquareImageNames = listOf(
        "favoritebakedgoods_square",
        "favoritemeat1_square",
        "favoritesnacks_square",
        "favoritecasseroles_square",
        "favoritemeat2_square",
        "favoriteveggies_square",
        "favoritepoultry_square",
        "favoritedesserts_square",
        "favoriteseafood_square",
        "favoritedefault_square"
    )

    /**
     *Images set to be used for Large favorite images
     */
    val favoritesLargeImageNames = listOf(
        "favoritebakedgoods_large",
        "favoritemeat1_large",
        "favoritesnacks_large",
        "favoritecasseroles_large",
        "favoritemeat2_large",
        "favoriteveggies_large",
        "favoritepoultry_large",
        "favoritedesserts_large",
        "favoriteseafood_large",
        "favoritedefault_large"
    )


    /**
     * Method to get images from the drawable folder
     */
    @SuppressLint("DiscouragedApi")
    fun getDrawableImagesByName(context: Context, imageNames: List<String?>): List<Drawable?> {
        return imageNames.map { imageName ->
            // Get resource ID by image name from the drawable folder
            val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
            // Fetch the drawable using the resource ID
            if (resId != 0) {
                ContextCompat.getDrawable(context, resId)
            } else {
                ContextCompat.getDrawable(context, R.drawable.favoritedefault)
            }
        }
    }
}

/**
 * File       : [core.utils.FavoriteDataHolder]
 * Singleton class to hold favorite data
 */
object FavoriteDataHolder {
    var isNotificationFlow: Boolean = false
    var isProbeFlow: Boolean = false
    var isSettingsFlow: Boolean = false
    var isFromKnobClick: Boolean = false

    private val _markFavorite = MutableLiveData<Boolean?>(null)
    val markFavorite: LiveData<Boolean?> get() = _markFavorite
    fun updateMarkFavorite(status: Boolean?) {
        _markFavorite.value = status
    }

    @JvmStatic
    var selectedImageIndex: Int? = null
    @JvmStatic
    var favoriteName: String = EMPTY_STRING
    @JvmStatic
    var favoriteRecord = FavoriteRecord()
}
