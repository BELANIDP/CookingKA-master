package core.viewHolderHelpers

import android.content.Context
import android.presenter.basefragments.abstract_view_helper.AbstractFarStatusWidgetHelper
import android.presenter.customviews.widgets.status.CookingFarStatusWidget
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.cooking.ka.databinding.FarViewItemStatusWidgetBinding
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.HMILogHelper

/**
 * File        : core.viewHolderHelpers.StatusWidgetHelper
 * Brief       : Helper class to provide widget component access
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing individual widget status view components
 */
class FarStatusWidgetHelper : AbstractFarStatusWidgetHelper() {
    private val tag = "FarStatusWidgetHelper"
    private lateinit var itemStatusBinding: FarViewItemStatusWidgetBinding
    override fun inflateView(
        context: Context,
        statusWidget: CookingFarStatusWidget,
        ovenType: String
    ) {
        val layoutInflater = LayoutInflater.from(context)
        itemStatusBinding =
            FarViewItemStatusWidgetBinding.inflate(layoutInflater, statusWidget, true)
        itemStatusBinding.tvRecipeWithTemperature.isSelected = true
        HMILogHelper.Logd(tag, "loading statusWidgetHelper: $ovenType")
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

    override fun getProgressbarInfinite(): LottieAnimationView {
        return itemStatusBinding.ivProgressbarInfinite
    }

    override fun getTemperatureRampIcon(): ImageView {
        return itemStatusBinding.ivTemperatureRamp
    }

    override fun tvCookTimeCompletedSince(): ResourceTextView {
        return itemStatusBinding.tvCookTimeCompleted
    }

    override fun getTemperatureProbeIcon(): ImageView {
        return itemStatusBinding.ivProbeIcon
    }


    /**
     *provide the cavity icon visibility
     *
     * @param isCavityIconVisible - true/false
     */
    override fun provideVisibilityOfCavityIcon(isCavityIconVisible: Boolean) {
        itemStatusBinding.isCavityIconVisible = isCavityIconVisible
    }

    override fun getFarViewParentLayout(): ConstraintLayout {
        return itemStatusBinding.farViewParentView
    }
    override fun getFarViewChildLayout(): ConstraintLayout {
        return itemStatusBinding.clModeLayout
    }
}