package android.presenter.basefragments.abstract_view_helper

import android.content.Context
import android.presenter.customviews.widgets.status.CookingFarStatusWidget
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView

abstract class AbstractFarStatusWidgetHelper {
    /**
     * Function to set the parent view
     * @param context [Context]
     */
    abstract fun inflateView(
        context: Context,
        statusWidget: CookingFarStatusWidget,
        ovenType: String
    )
    abstract fun getCavityIcon(): ImageView?

    abstract fun tvRecipeWithTemperature(): ResourceTextView?

    abstract fun tvCookTimeRemaining(): ResourceTextView?

    abstract fun getStatusProgressBar(): ProgressBar?
    abstract fun getProgressbarInfinite(): LottieAnimationView?
    abstract fun getTemperatureRampIcon(): ImageView?

    abstract fun tvCookTimeCompletedSince(): ResourceTextView

    abstract fun getTemperatureProbeIcon() : ImageView


    /**
     *provide the cavity icon visibility
     *
     * @param isCavityIconVisible - true/false
     */
    abstract fun provideVisibilityOfCavityIcon(isCavityIconVisible : Boolean)

    abstract fun getFarViewParentLayout(): ConstraintLayout
    abstract fun getFarViewChildLayout(): ConstraintLayout

}