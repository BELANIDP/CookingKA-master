/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
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
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceImageView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView

/**
 * File : core.jbase.AbstractDialogPopupViewHolder
 *
 * Brief :  Abstract class for managing the views of a dialog pop-up.
 *
 * Author : PARMAM
 *
 * Created On : 09/02/2024
 */
abstract class AbstractDialogPopupViewHolder {

    /**
     * Creates the view hierarchy for the dialog pop-up.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @param layoutId          The layout resource ID for the dialog pop-up.
     * @return The root view of the dialog pop-up.
     */
    abstract fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
        layoutId: Int
    ): View?

    /**
     * Called when the view hierarchy previously created by onCreateView(LayoutInflater, ViewGroup, Bundle) has been detached from the fragment.
     */
    abstract fun onDestroyView()

    /**
     * Gets the transparent background layout of the dialog pop-up.
     *
     * @return The transparent background layout.
     */
    abstract val transparentBackgroundLayout: ConstraintLayout?

    /**
     * Gets the transparent inner layout of the dialog pop-up.
     *
     * @return The inner background layout.
     */
    abstract val innerConstraintLayout: ConstraintLayout?

    /**
     * Gets the center header ImageView of the dialog pop-up.
     *
     * @return The center header ImageView.
     */
    abstract val headerCenterImageView: LottieAnimationView?

    /**
     * Gets the title TextView of the dialog pop-up.
     *
     * @return The title TextView.
     */
    abstract val titleTextView: AppCompatTextView?

    /**
     * Gets the ProgressBar of the dialog pop-up.
     *
     * @return The ProgressBar.
     */
    abstract val progressbar: ProgressBar?

    /**
     * Gets the notification TextView of the dialog pop-up.
     *
     * @return The notification TextView.
     */
    abstract val notificationTextView: AppCompatTextView?

    /**
     * Gets the instruction image TextView of the dialog pop-up.
     *
     * @return The ResourceImageView of scrollView text.
     */
    abstract val popupInfoImage: ResourceImageView?

    /**
     * Gets the ScrollView of the dialog pop-up.
     *
     * @return The ScrollView.
     */
    abstract val scrollView: ScrollView?

    /**
     * Gets the description TextView of the dialog pop-up.
     *
     * @return The description TextView.
     */
    abstract val descriptionTextView: AppCompatTextView?

    /**
     * Gets the left TextButton of the dialog pop-up.
     *
     * @return The left TextButton.
     */
    abstract val leftTextButton: TextButton?

    /**
     * Gets the center TextButton of the dialog pop-up.
     *
     * @return The center TextButton.
     */
    abstract val centerTextButton: TextButton?

    /**
     * Gets the right TextButton of the dialog pop-up.
     *
     * @return The right TextButton.
     */
    abstract val rightTextButton: TextButton?

    /**
     * Gets the center title TextView of the scrollable dialog pop-up.
     *
     * @return The center title TextView.
     */
    abstract val centerTitleTextView: AppCompatTextView?


    /**
     * Gets the center description TextView of the scrollable dialog pop-up.
     *
     * @return The center title TextView.
     */
    abstract val centerDescriptionTextView: AppCompatTextView?

    /**
     * Gets the ramp down temperature text of the dialog pop-up.
     *
     * @return The right TextButton.
     */
    abstract val bodyTextWithHotCavityTemp: ResourceTextView?
    abstract val ivRampDownHotCavity: ImageView?
    abstract val imageTextDescriptionView: AppCompatImageView?
    abstract val cavityIconImageview: ImageView?
    abstract val cavityIconParentImageview: FrameLayout?
    abstract val progressbarUpdateText: TextView?
}