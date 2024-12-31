package android.presenter.fragments.sabbath

import android.content.Context
import android.presenter.basefragments.abstract_view_helper.AbstractStatusWidgetHelper
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.SabbathItemStatusWidgetBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMILogHelper

/**
 * File        : core.viewHolderHelpers.SabbathStatusWidgetHelper
 * Brief       : Helper class to provide widget component access for Sabbath recipe only
 * Author      : Hiren
 * Created On  : 08/23/2024
 * Details     : This class provides  whole view containing individual widget status view components for Sabbath Recipe associated components only
 */
class StatusSabbathWidgetHelper : AbstractStatusWidgetHelper() {
    private val tag = "SabbathStatusWidgetHelper"
    private lateinit var itemStatusBinding: SabbathItemStatusWidgetBinding
    override fun inflateView(
        context: Context,
        statusWidget: CookingStatusWidget,
        ovenType: String
    ) {
        val layoutInflater = LayoutInflater.from(context)
        itemStatusBinding =
            SabbathItemStatusWidgetBinding.inflate(layoutInflater, statusWidget, true)
        itemStatusBinding.cookingStatusWidget = statusWidget
        HMILogHelper.Logd(tag, "loading statusWidgetHelper: $ovenType")
        val cookingViewModel: CookingViewModel = if (ovenType.contentEquals(
                    (context.getString(R.string.cavity_selection_lower_oven)))) {
            CookingViewModelFactory.getSecondaryCavityViewModel()
        } else {
            CookingViewModelFactory.getPrimaryCavityViewModel()
        }
        var temperatureUpValue = StringBuilder()
        if(CookingAppUtils.isFAHRENHEITUnitConfigured()) {
            itemStatusBinding.tvSabbathTemperatureUp.text = temperatureUpValue.append(AppConstants.PLUS_SYMBOL).append(AppConstants.SABBATH_FAHRENHEIT_TEMPERATURE_ALLOWED_VALUE).append(AppConstants.DEGREE_SYMBOL).toString()
            temperatureUpValue = StringBuilder()
            itemStatusBinding.tvSabbathTemperatureDown.text = temperatureUpValue.append(AppConstants.MINUS_SYMBOL).append(AppConstants.SABBATH_FAHRENHEIT_TEMPERATURE_ALLOWED_VALUE).append(AppConstants.DEGREE_SYMBOL).toString()
        }else{
            itemStatusBinding.tvSabbathTemperatureUp.text = temperatureUpValue.append(AppConstants.PLUS_SYMBOL).append(AppConstants.SABBATH_CELSIUS_TEMPERATURE_ALLOWED_VALUE).append(AppConstants.DEGREE_SYMBOL).toString()
            temperatureUpValue = StringBuilder()
            itemStatusBinding.tvSabbathTemperatureDown.text = temperatureUpValue.append(AppConstants.MINUS_SYMBOL).append(AppConstants.SABBATH_CELSIUS_TEMPERATURE_ALLOWED_VALUE).append(AppConstants.DEGREE_SYMBOL).toString()
        }
        HMILogHelper.Logd(tag, "Status Widget For ${cookingViewModel.cavityName.value}")
        itemStatusBinding.cookingViewModel = cookingViewModel
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

    override fun tvOvenStateAction(): ResourceTextView? {
        return null
    }

    override fun tvSetCookTime(): ResourceTextView? {
        return null
    }

    override fun getCavityMoreMenu(): ImageView? {
        return null
    }

    override fun getProgressbarInfinite(): LottieAnimationView? {
        return null
    }

    override fun getTemperatureRampIcon(): ImageView? {
        return null
    }

    override fun tvResumeCooking(): ResourceTextView? {
        return null
    }

    override fun clParentWidgetAction(): ConstraintLayout {
        return itemStatusBinding.clWidgetAction
    }

    override fun tvSabbathTemperatureDown(): ResourceTextView? {
        return itemStatusBinding.tvSabbathTemperatureDown
    }

    override fun tvSabbathTemperatureUp(): ResourceTextView? {
        return itemStatusBinding.tvSabbathTemperatureUp
    }

    override fun isRecipeAllowedInHotCavity(): Boolean {
        return false
    }

    override fun updateRecipeAllowedInHotCavity(shouldShowOvenCoolingMessage: Boolean) {
       //do nothing
    }

    override fun shouldShowCookTimeForDividerView(isCookTimeAvailable: Boolean) {
    }

    override fun isCookTimeNotAllowed(): Boolean {
        return false
    }

    override fun getTemperatureProbeIcon(): ImageView? {
        return null
    }

    override fun getLiveLookInIcon(): ImageView? {
        return null
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
}