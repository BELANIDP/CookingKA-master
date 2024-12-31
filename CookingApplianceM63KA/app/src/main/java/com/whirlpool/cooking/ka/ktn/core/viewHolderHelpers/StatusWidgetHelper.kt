package core.viewHolderHelpers

import android.content.Context
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.basefragments.abstract_view_helper.AbstractStatusWidgetHelper
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.ItemStatusWidgetBinding
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.CookingAppUtils
import core.utils.HMILogHelper

/**
 * File        : core.viewHolderHelpers.StatusWidgetHelper
 * Brief       : Helper class to provide widget component access
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing individual widget status view components
 */
class StatusWidgetHelper : AbstractStatusWidgetHelper() {
    private val tag = "StatusWidgetHelper"
    private lateinit var itemStatusBinding: ItemStatusWidgetBinding
    private var isRecipeAllowedInHotCavity = false
    override fun inflateView(
        context: Context,
        statusWidget: CookingStatusWidget,
        ovenType: String
    ) {
        val layoutInflater = LayoutInflater.from(context)
        itemStatusBinding =
            ItemStatusWidgetBinding.inflate(layoutInflater, statusWidget, true)
        itemStatusBinding.tvRecipeWithTemperature.isSelected = true
        itemStatusBinding.cookingStatusWidget = statusWidget
        HMILogHelper.Logd(tag, "loading statusWidgetHelper: $ovenType")
        val cookingViewModel: CookingViewModel = if (ovenType.contentEquals(
                    (context.getString(R.string.cavity_selection_lower_oven)))) {
            CookingViewModelFactory.getSecondaryCavityViewModel()
        } else {
            CookingViewModelFactory.getPrimaryCavityViewModel()
        }
        isRecipeAllowedInHotCavity = cookingViewModel.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.COOKING && CookingAppUtils.isRequiredTargetAvailable(
                cookingViewModel, RecipeOptions.TARGET_TEMPERATURE
            ) && CookingAppUtils.isRecipeAllowedForHotCavity(cookingViewModel)
        val isVisionRecipe = cookingViewModel.recipeExecutionViewModel.isVirtualchefBasedRecipe
        var hideAddCookTimeView = !CookingAppUtils.isCookTimeOptionAvailable(cookingViewModel)
        if(hideAddCookTimeView && AbstractStatusFragment.isExtendedCookingForNonEditableCookTimeRecipe(cookingViewModel)) hideAddCookTimeView = false
        HMILogHelper.Logd(tag, "Status Widget For ${cookingViewModel.cavityName.value} hideAddCookTimeView:$hideAddCookTimeView")
        itemStatusBinding.cookingViewModel = cookingViewModel
        itemStatusBinding.isCookTimeAvailable = hideAddCookTimeView
        itemStatusBinding.isVisionRecipe = isVisionRecipe
    }

    override fun getCavityIcon(): ImageView {
        return itemStatusBinding.ivOvenCavity
    }

    override fun tvRecipeWithTemperature(): ResourceTextView {
        return itemStatusBinding.tvRecipeWithTemperature
    }

    override fun tvCookTimeRemaining(): ResourceTextView {
        return itemStatusBinding.tvCookTimeRemaining
    }

    override fun getStatusProgressBar(): ProgressBar {
        return itemStatusBinding.progressBarRecipeCookTime
    }

    override fun tvOvenStateAction(): ResourceTextView {
        return itemStatusBinding.tvOvenStateAction
    }

    override fun tvSetCookTime(): ResourceTextView {
        return itemStatusBinding.tvSetOvenCookTime
    }

    override fun getCavityMoreMenu(): ImageView {
        return itemStatusBinding.ivOvenCavityMoreMenu
    }

    override fun getProgressbarInfinite(): LottieAnimationView {
        return itemStatusBinding.ivProgressbarInfinite
    }

    override fun getTemperatureRampIcon(): ImageView {
        return itemStatusBinding.ivTemperatureRamp
    }

    override fun tvResumeCooking(): ResourceTextView {
        return itemStatusBinding.tvResumeCooking
    }

    override fun clParentWidgetAction(): ConstraintLayout {
        return itemStatusBinding.clWidgetAction
    }

    override fun tvSabbathTemperatureDown(): ResourceTextView? {
        return null
    }

    override fun tvSabbathTemperatureUp(): ResourceTextView? {
        return null
    }

    override fun isRecipeAllowedInHotCavity(): Boolean {
        return isRecipeAllowedInHotCavity
    }

    override fun updateRecipeAllowedInHotCavity(shouldShowOvenCoolingMessage: Boolean) {
        HMILogHelper.Logd("HotCavity", "updating the updateRecipeAllowedInHotCavity to $shouldShowOvenCoolingMessage")
        isRecipeAllowedInHotCavity = shouldShowOvenCoolingMessage
    }

    /**
     * update the probe extend time cycle
     *
     * @param isCookTimeAvailable
     */
    override fun shouldShowCookTimeForDividerView(isCookTimeAvailable: Boolean) {
        itemStatusBinding.isCookTimeAvailable = isCookTimeAvailable
    }

    override fun isCookTimeNotAllowed(): Boolean? {
        return itemStatusBinding.isCookTimeAvailable
    }

    override fun getTemperatureProbeIcon(): ImageView {
        return itemStatusBinding.ivProbeIcon
    }

    override fun getStatusParent(): ConstraintLayout {
        return itemStatusBinding.statusPrentView
    }

    override fun getStatusTopTextContentView(): ConstraintLayout {
        return itemStatusBinding.clModeLayout
    }

    override fun getStatusBottomOptionsView(): ConstraintLayout {
        return itemStatusBinding.clWidgetAction
    }

    override fun getLiveLookInIcon(): ImageView {
        return itemStatusBinding.liveLookIn
    }
}