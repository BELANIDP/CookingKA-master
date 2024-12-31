package core.jbase

import android.os.Bundle
import android.presenter.customviews.textButton.TextButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceImageView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView

/**
 * File:core.jbase.DialogPopupScrollViewHolderHelper
 *
 * Brief: Helper class for managing the views of a scrollable dialog pop-up.
 *
 * Author: PARMAM
 *
 * Created On: 09/02/2024
 */
class DialogPopupScrollViewHolderHelper : AbstractDialogPopupViewHolder() {
    private var scrollPopupView: View? = null

    /**
     * Creates the view hierarchy for the scrollable dialog pop-up.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @param layoutId          The layout resource ID for the dialog pop-up.
     * @return The root view of the dialog pop-up.
     */
    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
        layoutId: Int
    ): View? {
        scrollPopupView = inflater?.inflate(layoutId, container, false)
        return scrollPopupView?.rootView
    }

    /**
     * Called when the view hierarchy previously created by onCreateView(LayoutInflater, ViewGroup, Bundle) has been detached from the fragment.
     */
    override fun onDestroyView() {
        scrollPopupView = null
    }

    /**
     * Gets the transparent background layout of the scrollable dialog pop-up.
     *
     * @return The transparent background layout.
     */
    override val transparentBackgroundLayout: ConstraintLayout?
        get() = scrollPopupView?.findViewById(R.id.transparent_background_layout)


    /**
     * Gets the transparent background layout of the scrollable dialog pop-up.
     *
     * @return The transparent background layout.
     */
    override val innerConstraintLayout: ConstraintLayout?
        get() = scrollPopupView?.findViewById(R.id.popup_with_scroll)


    /**
     * Gets the title TextView of the scrollable dialog pop-up.
     *
     * @return The title TextView.
     */
    override val titleTextView: AppCompatTextView?
        get() = scrollPopupView?.findViewById(R.id.text_view_title)

    /**
     * Gets the ProgressBar of the scrollable dialog pop-up.
     *
     * @return The ProgressBar.
     */
    override val progressbar: ProgressBar?
        get() = scrollPopupView?.findViewById(R.id.popup_progressbar)

    override val progressbarUpdateText: TextView?
        get() = scrollPopupView?.findViewById(R.id.text_view_progress)
    /**
     * Gets the notification TextView of the scrollable dialog pop-up.
     *
     * @return The notification TextView.
     */
    override val notificationTextView: AppCompatTextView?
        get() = scrollPopupView?.findViewById(R.id.text_view_notification)

    /**
     * Gets the center header ImageView of the scrollable dialog pop-up.
     *
     * @return The center header ImageView.
     */
    override val headerCenterImageView: LottieAnimationView?
        get() = scrollPopupView?.findViewById(R.id.image_view_header_center)

    /**
     * Gets the ScrollView of the scrollable dialog pop-up.
     *
     * @return The ScrollView.
     */
    override val scrollView: ScrollView?
        get() = scrollPopupView?.findViewById(R.id.scroll_view)

    /**
     * Gets the description TextView of the scrollable dialog pop-up.
     *
     * @return The description TextView.
     */
    override val descriptionTextView: AppCompatTextView?
        get() = scrollPopupView?.findViewById(R.id.text_view_description)

    /**
     * Gets the left TextButton of the scrollable dialog pop-up.
     *
     * @return The left TextButton.
     */
    override val leftTextButton: TextButton?
        get() = scrollPopupView?.findViewById(R.id.text_button_left)

    /**
     * Gets the center TextButton of the scrollable dialog pop-up.
     *
     * @return The center TextButton.
     */
    override val centerTextButton: TextButton?
        get() = scrollPopupView?.findViewById(R.id.text_button_center)

    /**
     * Gets the right TextButton of the scrollable dialog pop-up.
     *
     * @return The right TextButton.
     */
    override val rightTextButton: TextButton?
        get() = scrollPopupView?.findViewById(R.id.text_button_right)

    override val popupInfoImage: ResourceImageView?
        get() = scrollPopupView?.findViewById(R.id.ivPopupInstructionImage)

    /**
     * Gets the center title TextView of the scrollable dialog pop-up.
     *
     * @return The center title TextView.
     */
    override val centerTitleTextView: AppCompatTextView?
        get() = scrollPopupView?.findViewById(R.id.text_view_center_title)

    /**
     * Gets the center title TextView of the scrollable dialog pop-up.
     *
     * @return The center title TextView.
     */
    override val imageTextDescriptionView: AppCompatImageView?
        get() = scrollPopupView?.findViewById(R.id.image_view_text_center)


    /**
     * Gets the center description TextView of the scrollable dialog pop-up.
     *
     * @return The center title TextView.
     */
    override val centerDescriptionTextView: AppCompatTextView?
        get() = scrollPopupView?.findViewById(R.id.text_view_center_description)

    /**
     * Gets the bodyTextWithHotCavityTemp ResourceTextView of the scrollable dialog pop-up.
     *
     * @return The bodyTextWithHotCavityTemp ResourceTextView.
     */
    override val bodyTextWithHotCavityTemp: ResourceTextView?
        get() = scrollPopupView?.findViewById(R.id.bodyTextWithHotCavityTemp)

    override val ivRampDownHotCavity: ImageView?
        get() = scrollPopupView?.findViewById(R.id.ivRampDownHotCavity)

    override val cavityIconParentImageview: FrameLayout?
        get() = scrollPopupView?.findViewById(R.id.flOvenCavityIcon)
    override val cavityIconImageview: ImageView?
        get() = scrollPopupView?.findViewById(R.id.ivOvenCavity)
}