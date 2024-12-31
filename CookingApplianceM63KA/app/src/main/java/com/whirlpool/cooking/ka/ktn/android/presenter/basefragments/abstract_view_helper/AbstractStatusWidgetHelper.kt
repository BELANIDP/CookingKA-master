package android.presenter.basefragments.abstract_view_helper

import android.content.Context
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView

abstract class AbstractStatusWidgetHelper {
    /**
     * Function to set the parent view
     * @param context [Context]
     */
    abstract fun inflateView(
        context: Context,
        statusWidget: CookingStatusWidget,
        ovenType: String
    )
    abstract fun getCavityIcon(): ImageView?

    abstract fun tvRecipeWithTemperature(): ResourceTextView?

    abstract fun tvCookTimeRemaining(): ResourceTextView?

    abstract fun getStatusProgressBar(): ProgressBar?
    abstract fun tvOvenStateAction(): ResourceTextView?
    abstract fun tvSetCookTime(): ResourceTextView?
    abstract fun getCavityMoreMenu(): ImageView?
    abstract fun getProgressbarInfinite(): LottieAnimationView?
    abstract fun getTemperatureRampIcon(): ImageView?
    abstract fun getTemperatureProbeIcon(): ImageView?
    abstract fun getLiveLookInIcon(): ImageView?


    abstract fun tvResumeCooking(): ResourceTextView?
    abstract fun clParentWidgetAction(): ConstraintLayout?

    abstract fun tvSabbathTemperatureDown(): ResourceTextView?
    abstract fun tvSabbathTemperatureUp(): ResourceTextView?

    /**
     * binding hot cavity variable to run Oven Cooling runnable
     *
     * @return
     */
    abstract fun isRecipeAllowedInHotCavity(): Boolean

    /**
     * update the hot cavity message for a particular status widget if oven cooling is done
     *
     * @param shouldShowOvenCoolingMessage
     */
    abstract fun updateRecipeAllowedInHotCavity(shouldShowOvenCoolingMessage : Boolean)

    /**
     * update the probe extend time cycle
     *
     * @param isCookTimeAvailable
     */
    abstract fun shouldShowCookTimeForDividerView(isCookTimeAvailable : Boolean)

    /**
     * get the availability of Cook Time view, some recipe doesn't allow adding cook time
     * this will be updating dynamically and will be managed by binding
     * @return true of the visibility is GONE and not allowed to press or add cook time to recipe
     *         false otherwise
     */
    abstract fun isCookTimeNotAllowed(): Boolean?
    abstract fun getStatusParent(): ConstraintLayout
    abstract fun getStatusTopTextContentView(): ConstraintLayout
    abstract fun getStatusBottomOptionsView(): ConstraintLayout
}