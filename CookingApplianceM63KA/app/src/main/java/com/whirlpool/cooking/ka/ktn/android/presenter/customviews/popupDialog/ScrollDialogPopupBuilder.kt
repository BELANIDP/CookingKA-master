/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.customviews.popupDialog

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import com.whirlpool.cooking.common.utils.TimeoutViewModel.TimeoutStatesEnum
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButtonOnClickListener
import core.jbase.AbstractDialogPopUpOverlayFragment
import core.jbase.AbstractDialogPopupViewHolder
import core.jbase.DialogPopupScrollViewHolderHelper
import core.utils.AppConstants
import core.utils.AppConstants.HEADER_VIEW_CENTER_ICON_GONE
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.SharedViewModel


/**
 * File : android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
 *
 * Brief : Builder class for creating a scrollable dialog pop-up.
 *
 * Author : PARMAM
 *
 * Created On : 07/02/2024
 */
class ScrollDialogPopupBuilder(private var dialogLayoutResource: Int) :
    AbstractDialogPopUpOverlayFragment() {

    private var dialogPopupViewHolder: DialogPopupScrollViewHolderHelper? = null
    private var titleText = UNKNOWN_VALUE
    private var titleTextString = AppConstants.EMPTY_STRING
    private var descriptionText = UNKNOWN_VALUE
    private var descriptionTextString = AppConstants.EMPTY_STRING
    private var descriptionSpannableTextString:SpannableStringBuilder? = null
    private var notificationText = UNKNOWN_VALUE
    private var headerViewCenterIcon = UNKNOWN_VALUE
    private var isHeaderViewCenterIconAnimation = false
    private var imageTextDescription = UNKNOWN_VALUE
    private var leftButtonText = UNKNOWN_VALUE
    private var rightButtonText = UNKNOWN_VALUE
    private var centerButtonText = UNKNOWN_VALUE
    private var isLeftButtonEnabled = true
    private var isRightButtonEnabled = true
    private var isCenterButtonEnabled = true
    private var isProgressBarVisible = false
    private var progressPercentage = 0
    private var isPopupCenterAligned = true
    private var popupInfoImageResourceId = UNKNOWN_VALUE
    private var bodyTextWithHotCavityTemp = AppConstants.EMPTY_STRING
    private var isTitleTextDrawable = false
    private var titleTextDrawable = UNKNOWN_VALUE
    private var titleTextTopMargin = 48
    private var descriptionTextTopMargin = UNKNOWN_VALUE
    private var descriptionTextWidth = UNKNOWN_VALUE
    private var descriptionTextHorizontalMargin = UNKNOWN_VALUE
    private var titleTextHorizontalMargin = UNKNOWN_VALUE
    private var layoutMainTopPadding = 43
    private var layoutMainBottomPadding = UNKNOWN_VALUE
    private var isCancelableOutSideTouch = true


    var leftButtonClickListener: NavigationButtonOnClickListener? = null
    var rightButtonClickListener: NavigationButtonOnClickListener? = null
    var centerButtonClickListener: NavigationButtonOnClickListener? = null

    private var onDialogCreatedListener: OnDialogCreatedListener? = null
    private var onTimeoutObserverListener: OnTimeoutObserverListener? = null
    private var timeout = 0
    private var lastClickTime: Long = 0
    private val doubleClickDelayMillis = 1000 // Adjust the delay as needed


    var titleTextFormatArgs: String? = null

    private var descriptionTextGravity = UNKNOWN_VALUE
    private var descriptionTextFont:Typeface? = null
    private var titleTextGravity = UNKNOWN_VALUE

    private var headerIconTopMargin = UNKNOWN_VALUE

    /**
     * Provides the layout resource ID for the dialog pop-up.
     *
     * @return The layout resource ID.
     */
    override val layoutResource: Int
        get() = dialogLayoutResource

    /**
     * Provides the ViewHolderHelper for managing the views of the dialog pop-up.
     *
     * @return The ViewHolderHelper.
     */
    override fun provideViewHolderHelper(): AbstractDialogPopupViewHolder? {
        return dialogPopupViewHolder
    }

    /**
     * Creates the view hierarchy for the dialog pop-up.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root view of the dialog pop-up.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dialogPopupViewHolder = DialogPopupScrollViewHolderHelper()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored into the view.
     *
     * @param view               The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateView()
        onDialogCreatedListener?.onDialogCreated()
        dialogPopupViewHolder?.transparentBackgroundLayout?.setOnClickListener {
            //outside click event handling
            if (isCancelableOutSideTouch)
                dismiss()
        }
        dialogPopupViewHolder?.innerConstraintLayout?.setOnClickListener {
            //for handling inside click. Leave it blank
        }
        isPopupVisible = true
    }

    /**
     * Interface definition for a callback to be invoked when the dialog is created or destroyed.
     */
    interface OnDialogCreatedListener {
        fun onDialogCreated()
        fun onDialogDestroy()
    }

    /**
     * Sets the listener to be notified when the dialog is created or destroyed.
     *
     * @param listener The listener to set.
     */
    fun setOnDialogCreatedListener(listener: OnDialogCreatedListener?) {
        onDialogCreatedListener = listener
    }

    fun interface OnTimeoutObserverListener {
        fun onTimeout(timeoutStatesEnum: TimeoutStatesEnum?)
    }

    fun setTimeoutCallback(
        onTimeoutObserverListener: OnTimeoutObserverListener?,
        timeInSeconds: Int,
    ) {
        this.onTimeoutObserverListener = onTimeoutObserverListener
        timeout = timeInSeconds
    }

    /**
     * to restart the timeout on popup
     */
    fun restartTimeout() {
        HMILogHelper.Logd(tag, "restarting timeout for popup")
        timeoutViewModel?.setTimeout(timeout)
    }

    /**
     * Updates the view based on the builder's properties.
     */
    private fun updateView() {

        if (titleText != UNKNOWN_VALUE) {
            dialogPopupViewHolder?.titleTextView?.visibility = View.VISIBLE
            dialogPopupViewHolder?.titleTextView?.setText(titleText)
        }
        if (titleTextString != AppConstants.EMPTY_STRING) {
            dialogPopupViewHolder?.titleTextView?.visibility = View.VISIBLE
            dialogPopupViewHolder?.titleTextView?.text = titleTextString
        }
        if(isTitleTextDrawable){
            dialogPopupViewHolder?.titleTextView?.setCompoundDrawablesWithIntrinsicBounds(titleTextDrawable,0,0,0)
            val padding = resources.getDimensionPixelSize(R.dimen.title_drawable_icon_padding)
            dialogPopupViewHolder?.titleTextView?.compoundDrawablePadding=padding
        }

        if (titleTextFormatArgs != null) {
            dialogPopupViewHolder?.titleTextView?.visibility = View.VISIBLE
            dialogPopupViewHolder?.titleTextView?.text = getString(titleText, titleTextFormatArgs)
        }

        if (descriptionText != UNKNOWN_VALUE) {
            dialogPopupViewHolder?.scrollView?.visibility = View.VISIBLE
            dialogPopupViewHolder?.descriptionTextView?.setText(descriptionText)
        }
        if (descriptionTextString != AppConstants.EMPTY_STRING) {
            dialogPopupViewHolder?.scrollView?.visibility = View.VISIBLE
            dialogPopupViewHolder?.descriptionTextView?.text = descriptionTextString
        }
        if (descriptionSpannableTextString != null) {
            dialogPopupViewHolder?.scrollView?.visibility = View.VISIBLE
            dialogPopupViewHolder?.descriptionTextView?.text = descriptionSpannableTextString
        }

        if (popupInfoImageResourceId != UNKNOWN_VALUE) {
            dialogPopupViewHolder?.popupInfoImage?.visibility = View.VISIBLE
            dialogPopupViewHolder?.popupInfoImage?.setImageResource(popupInfoImageResourceId)
            dialogPopupViewHolder?.titleTextView?.gravity = Gravity.CENTER_HORIZONTAL
        }

        if (notificationText != UNKNOWN_VALUE) {
            dialogPopupViewHolder?.notificationTextView?.visibility = View.VISIBLE
            dialogPopupViewHolder?.notificationTextView?.setText(notificationText)
        }

        if(layoutMainBottomPadding != UNKNOWN_VALUE){
            dialogPopupViewHolder?.innerConstraintLayout?.setPadding(0,layoutMainTopPadding,0, layoutMainBottomPadding)
        }

        if (headerViewCenterIcon == HEADER_VIEW_CENTER_ICON_GONE){
            dialogPopupViewHolder?.headerCenterImageView?.visibility = View.GONE
            val param = (dialogPopupViewHolder?.titleTextView?.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0,titleTextTopMargin,0,0)
            }
            dialogPopupViewHolder?.titleTextView?.layoutParams = param
            if (titleTextGravity != UNKNOWN_VALUE) {
                dialogPopupViewHolder?.titleTextView?.gravity =
                    titleTextGravity
            }
            if(descriptionTextTopMargin!= UNKNOWN_VALUE){
                val descriptionTextParam = (dialogPopupViewHolder?.descriptionTextView?.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0,descriptionTextTopMargin,0,0)
                }
                dialogPopupViewHolder?.descriptionTextView?.layoutParams = descriptionTextParam
            }

            if (descriptionTextWidth != UNKNOWN_VALUE) {
                val descriptionTextParam1 = (dialogPopupViewHolder?.scrollView?.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
                    width = descriptionTextWidth
                }
                dialogPopupViewHolder?.scrollView?.layoutParams = descriptionTextParam1
            }

           if (descriptionTextHorizontalMargin != UNKNOWN_VALUE){
               val descriptionScrollView = (dialogPopupViewHolder?.scrollView?.layoutParams as ViewGroup.MarginLayoutParams).apply {
                   setMargins(descriptionTextHorizontalMargin, this.topMargin, descriptionTextHorizontalMargin,0)
               }
               dialogPopupViewHolder?.scrollView?.layoutParams = descriptionScrollView
           }

            if (titleTextHorizontalMargin != UNKNOWN_VALUE){
               val titleText = (dialogPopupViewHolder?.titleTextView?.layoutParams as ViewGroup.MarginLayoutParams).apply {
                   setMargins(titleTextHorizontalMargin, this.topMargin, titleTextHorizontalMargin,0)
               }
               dialogPopupViewHolder?.titleTextView?.layoutParams = titleText
           }

        }else if (headerViewCenterIcon != UNKNOWN_VALUE) {
            dialogPopupViewHolder?.headerCenterImageView?.visibility = View.VISIBLE
            if (isHeaderViewCenterIconAnimation) {
                dialogPopupViewHolder?.headerCenterImageView?.setAnimation(headerViewCenterIcon)
                dialogPopupViewHolder?.headerCenterImageView?.playAnimation()
            } else {
                dialogPopupViewHolder?.headerCenterImageView?.setImageResource(headerViewCenterIcon)
            }
            dialogPopupViewHolder?.headerCenterImageView?.setOnClickListener(this)
            dialogPopupViewHolder?.headerCenterImageView?.addAnimatorListener(
                animatorListenerForHeaderIcon
            )
        } else {
            dialogPopupViewHolder?.headerCenterImageView?.visibility = View.INVISIBLE
        }
        if (bodyTextWithHotCavityTemp != AppConstants.EMPTY_STRING) {
            dialogPopupViewHolder?.bodyTextWithHotCavityTemp?.visibility = View.VISIBLE
            dialogPopupViewHolder?.ivRampDownHotCavity?.visibility = View.VISIBLE
            dialogPopupViewHolder?.bodyTextWithHotCavityTemp?.text = bodyTextWithHotCavityTemp
            dialogPopupViewHolder?.bodyTextWithHotCavityTemp?.background = null
            val layoutParams =
                dialogPopupViewHolder?.scrollView?.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.bottomToTop =
                dialogPopupViewHolder?.bodyTextWithHotCavityTemp?.id ?: View.NO_ID
            dialogPopupViewHolder?.scrollView?.layoutParams = layoutParams
        } else {
            dialogPopupViewHolder?.bodyTextWithHotCavityTemp?.visibility = View.GONE
            dialogPopupViewHolder?.ivRampDownHotCavity?.visibility = View.GONE
        }

        if (imageTextDescription != UNKNOWN_VALUE) {
            dialogPopupViewHolder?.imageTextDescriptionView?.visibility = View.VISIBLE
            dialogPopupViewHolder?.imageTextDescriptionView?.setImageResource(imageTextDescription)
        }

        if (leftButtonText != UNKNOWN_VALUE) {
            dialogPopupViewHolder?.leftTextButton?.visibility = View.VISIBLE
            dialogPopupViewHolder?.leftTextButton?.setOnClickListener(this)
            dialogPopupViewHolder?.leftTextButton?.setTextButtonText(leftButtonText)
            dialogPopupViewHolder?.leftTextButton?.visibility = View.VISIBLE
            dialogPopupViewHolder?.leftTextButton?.isEnabled = isLeftButtonEnabled
            if (headerViewCenterIcon != AppConstants.HEADER_RESTORE_LAYOUT_VIEW){
                val layoutParams =
                    dialogPopupViewHolder?.scrollView?.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.bottomToTop = dialogPopupViewHolder?.leftTextButton?.id ?: View.NO_ID
                dialogPopupViewHolder?.scrollView?.layoutParams = layoutParams
            }
        } else {
            dialogPopupViewHolder?.leftTextButton?.visibility = View.GONE
        }

        if (rightButtonText != UNKNOWN_VALUE) {
            dialogPopupViewHolder?.rightTextButton?.visibility = View.VISIBLE
            dialogPopupViewHolder?.rightTextButton?.setOnClickListener(this)
            dialogPopupViewHolder?.rightTextButton?.setTextButtonText(rightButtonText)
            dialogPopupViewHolder?.rightTextButton?.isEnabled = isRightButtonEnabled
            if (headerViewCenterIcon != AppConstants.HEADER_RESTORE_LAYOUT_VIEW) {
                val layoutParams =
                    dialogPopupViewHolder?.scrollView?.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.bottomToTop = dialogPopupViewHolder?.rightTextButton?.id ?: View.NO_ID
                dialogPopupViewHolder?.scrollView?.layoutParams = layoutParams
            }
        } else {
            dialogPopupViewHolder?.rightTextButton?.visibility = View.GONE
        }

        if (headerViewCenterIcon == AppConstants.HEADER_RESTORE_LAYOUT_VIEW) {
//            To handle vertical center align for Restore Settings/Learn More popup
            val param =
                (dialogPopupViewHolder?.scrollView?.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    setMargins(0, 0, 0, titleTextTopMargin)
                }
            dialogPopupViewHolder?.scrollView?.layoutParams = param
        }

        if (centerButtonText != UNKNOWN_VALUE) {
            dialogPopupViewHolder?.centerTextButton?.visibility = View.VISIBLE
            dialogPopupViewHolder?.centerTextButton?.setOnClickListener(this)
            dialogPopupViewHolder?.centerTextButton?.setTextButtonText(centerButtonText)
            dialogPopupViewHolder?.centerTextButton?.isEnabled = isCenterButtonEnabled
            val layoutParams =
                dialogPopupViewHolder?.scrollView?.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.bottomToTop = dialogPopupViewHolder?.centerTextButton?.id ?: View.NO_ID
            dialogPopupViewHolder?.scrollView?.layoutParams = layoutParams
        } else {
            dialogPopupViewHolder?.centerTextButton?.visibility = View.GONE
        }

        if (isProgressBarVisible) {
            dialogPopupViewHolder?.progressbar?.visibility = View.VISIBLE
            dialogPopupViewHolder?.progressbar?.max = progressPercentage
        }

        if (!isPopupCenterAligned) {
            dialogPopupViewHolder?.headerCenterImageView?.layoutParams =
                ConstraintLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.popup_header_center_small_image_width),
                    resources.getDimensionPixelSize(R.dimen.popup_header_center_small_image_height)
                )

            val params =
                dialogPopupViewHolder?.headerCenterImageView?.layoutParams as ConstraintLayout.LayoutParams

            params.setMargins(
                resources.getDimensionPixelSize(R.dimen.margin_popup_container_margin_and_padding),
                resources.getDimensionPixelSize(R.dimen.margin_popup_container_margin_and_padding),
                resources.getDimensionPixelSize(R.dimen.margin_popup_container_margin_and_padding),
                0
            )

            dialogPopupViewHolder?.headerCenterImageView?.layoutParams = params

            val lottieImageViewParam = dialogPopupViewHolder?.headerCenterImageView?.layoutParams

            if (lottieImageViewParam is ConstraintLayout.LayoutParams) {
                lottieImageViewParam.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                lottieImageViewParam.endToEnd = ConstraintLayout.LayoutParams.UNSET
                lottieImageViewParam.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                lottieImageViewParam.topToBottom = ConstraintLayout.LayoutParams.UNSET
                dialogPopupViewHolder?.headerCenterImageView?.layoutParams = lottieImageViewParam
            }

            val textViewParam = dialogPopupViewHolder?.titleTextView?.layoutParams

            if (textViewParam is ConstraintLayout.LayoutParams) {
                textViewParam.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                if (headerViewCenterIcon != UNKNOWN_VALUE) {
                    textViewParam.setMargins(
                        resources.getDimensionPixelSize(R.dimen.margin_popup_container_small_padding),
                        0,
                        0,
                        0
                    )
                    textViewParam.startToStart = ConstraintLayout.LayoutParams.UNSET
                    textViewParam.endToEnd = ConstraintLayout.LayoutParams.UNSET
                    textViewParam.startToEnd =
                        dialogPopupViewHolder?.headerCenterImageView?.id ?: View.NO_ID
                    textViewParam.topToTop =
                        dialogPopupViewHolder?.headerCenterImageView?.id ?: View.NO_ID
                    textViewParam.bottomToBottom =
                        dialogPopupViewHolder?.headerCenterImageView?.id ?: View.NO_ID
                } else {
                    textViewParam.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                    textViewParam.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    textViewParam.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                    textViewParam.topToBottom = ConstraintLayout.LayoutParams.UNSET
                    textViewParam.setMargins(
                        resources.getDimensionPixelSize(R.dimen.margin_popup_container_margin_and_padding),
                        resources.getDimensionPixelSize(R.dimen.margin_popup_container_margin_and_padding),
                        resources.getDimensionPixelSize(R.dimen.margin_popup_container_margin_and_padding),
                        resources.getDimensionPixelSize(R.dimen.margin_popup_container_margin_and_padding)
                    )
                }
                dialogPopupViewHolder?.titleTextView?.layoutParams = textViewParam
                dialogPopupViewHolder?.titleTextView?.gravity = Gravity.START
                if (descriptionTextGravity != UNKNOWN_VALUE) dialogPopupViewHolder?.descriptionTextView?.gravity =
                    descriptionTextGravity else dialogPopupViewHolder?.descriptionTextView?.gravity =
                    Gravity.START
            }
        }
        if (descriptionTextGravity != UNKNOWN_VALUE) {
            dialogPopupViewHolder?.descriptionTextView?.gravity = descriptionTextGravity
        }
        if (descriptionTextFont!=null) {
            dialogPopupViewHolder?.descriptionTextView?.typeface = descriptionTextFont
        }
        if(headerIconTopMargin != UNKNOWN_VALUE){
            val param = (dialogPopupViewHolder?.headerCenterImageView?.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0,headerIconTopMargin,0,0)
            }
            dialogPopupViewHolder?.headerCenterImageView?.layoutParams = param
        }
    }

    override fun onClick(view: View) {
        val handler = Handler(Looper.getMainLooper())
        when (view.id) {
            R.id.text_button_left -> {
                if (!isDoubleClick()) {
                    leftButtonClickListener?.executeOnClick()
                    handler.postDelayed(this::dismiss, AppConstants.POPUP_DISMISS_DELAY.toLong())
                }
            }

            R.id.text_button_right -> {
                if (!isDoubleClick()) {
                    rightButtonClickListener?.executeOnClick()
                    handler.postDelayed(this::dismiss, AppConstants.POPUP_DISMISS_DELAY.toLong())
                }
            }

            R.id.text_button_center -> {
                if (!isDoubleClick()) {
                    centerButtonClickListener?.executeOnClick()
                    handler.postDelayed(this::dismiss, AppConstants.POPUP_DISMISS_DELAY.toLong())
                }
            }

            else -> {}
        }


    }

    override fun onHMILeftKnobClick() {
        if (dialogPopupViewHolder?.leftTextButton?.isEnabled == true) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(
                {
                    NavigationUtils.getVisibleFragment()?.activity?.let {
                        SharedViewModel.getSharedViewModel(it)
                            .setIsNavigatedFromKnobClick(true)
                    }
                },
                AppConstants.POPUP_KNOB_DISMISS_DELAY.toLong()
            )
            onClick(dialogPopupViewHolder?.leftTextButton as View)
        }
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
        if (dialogPopupViewHolder?.rightTextButton?.isEnabled == true) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed(
                {
                    NavigationUtils.getVisibleFragment()?.activity?.let {
                        SharedViewModel.getSharedViewModel(it)
                            .setIsNavigatedFromKnobClick(true)
                    }
                },
                AppConstants.POPUP_KNOB_DISMISS_DELAY.toLong()
            )
            onClick(dialogPopupViewHolder?.rightTextButton as View)
        }
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
    }

    private fun isDoubleClick(): Boolean {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastClickTime
        lastClickTime = currentTime
        return elapsedTime < doubleClickDelayMillis
    }


    private val animatorListenerForHeaderIcon: AnimatorListenerAdapter =
        object : AnimatorListenerAdapter() {
            @Suppress("RedundantOverride")
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
            }
        }

    override fun onDestroyView() {
        onDialogCreatedListener?.onDialogDestroy()
        onDialogCreatedListener = null
        isPopupVisible = false
        super.onDestroyView()
    }

    class Builder(id: Int) {
        private var popupBuilder: ScrollDialogPopupBuilder = ScrollDialogPopupBuilder(id)

        /**
         * Sets the title text of the header.
         * @param titleText The resource ID of the title text.
         * @return The Builder instance.
         */
        fun setHeaderTitle(titleText: Int): Builder {
            popupBuilder.titleText = titleText
            popupBuilder.titleTextFormatArgs = null
            return this
        }

        /**
         * Method to set the popup title.
         * @param title Resource id for the title string
         * @param formatArgs The format arguments that will be used for substitution.
         * @return
         */
        fun setHeaderTitle(
            title: Int,
            formatArgs: String,
        ): Builder {
            popupBuilder.titleText = title
            popupBuilder.titleTextFormatArgs = formatArgs
            return this
        }


        /**
         * Sets the title text.
         * @param titleText The resource String of the title text.
         * @return The Builder instance.
         */
        fun setHeaderTitle(titleText: String): Builder {
            popupBuilder.titleTextString = titleText
            return this
        }

        /**
         * Method to set the popup title top margin
         * @param marginTop int for title top margin
         * @return
         */
        fun setTopMarginForTitleText(
            marginTop: Int,
        ):Builder{
            popupBuilder.titleTextTopMargin = marginTop
            return this
        }

        /**
         * Method to set the popup header icon top margin
         * @param marginTop int for title top margin
         * @return
         */
        fun setTopMarginForHeaderIcon(
            marginTop: Int,
        ):Builder{
            popupBuilder.headerIconTopMargin = marginTop
            return this
        }
        /**
         * Method to set the popup description top margin
         * @param marginTop int for description top margin
         * @return
         */
        fun setTopMarginForDescriptionText(
            marginTop: Int,
        ):Builder{
            popupBuilder.descriptionTextTopMargin = marginTop
            return this
        }

        /**
         * Method to set the popup description width
         * @param marginTop int for description top margin
         * @return
         */
        fun setWidthForDescriptionText(
            width: Int,
        ):Builder{
            popupBuilder.descriptionTextWidth = width
            return this
        }

        /**
         * Method to set the popup description horizontal margin
         * @param marginHorizontal int for description top margin
         * @return
         */
        fun setHorizontalMarginForDescriptionText(
            marginHorizontal: Int,
        ):Builder{
            popupBuilder.descriptionTextHorizontalMargin = marginHorizontal
            return this
        }

        /**
         * Method to set the popup Title horizontal margin
         * @param marginHorizontal int for Title Horizontal margin
         * @return
         */
        fun setHorizontalMarginForTitleText(
            marginHorizontal: Int,
        ):Builder{
            popupBuilder.titleTextHorizontalMargin = marginHorizontal
            return this
        }


        /**
         * Method to set the popup inner layout top padding
         * @param paddingTop int inner layout top padding
         * @return
         */
        @Suppress("unused")
        fun setTopPaddingForInnerLayout(
            paddingTop: Int,
        ):Builder{
            popupBuilder.layoutMainTopPadding = paddingTop
            return this
        }


        /**
         * Method to set the inner layout bottom padding
         * @param paddingBottom int for inner layout bottom padding
         * @return
         */
        fun setBottomPaddingForInnerLayout(
            paddingBottom: Int,
        ):Builder{
            popupBuilder.layoutMainBottomPadding = paddingBottom
            return this
        }

        /**
         * Sets the description message text.
         * @param descriptionText The resource ID of the description text.
         * @return The Builder instance.
         */
        fun setDescriptionMessage(descriptionText: Int): Builder {
            popupBuilder.descriptionText = descriptionText
            return this
        }

        /**
         * Sets the description message text.
         * @param descriptionText The resource ID of the description text.
         * @return The Builder instance.
         */
        fun setDescriptionMessage(descriptionText: String): Builder {
            popupBuilder.descriptionTextString = descriptionText
            return this
        }

        /**
         * Sets the description message text.
         * @param descriptionText The resource ID of the description text.
         * @return The Builder instance.
         */
        fun setSpannableDescriptionMessage(descriptionText: SpannableStringBuilder): Builder {
            popupBuilder.descriptionSpannableTextString = descriptionText
            return this
        }

        /**
         * Sets the notification text.
         * @param notificationText The resource ID of the notification text.
         * @return The Builder instance.
         */
        fun setNotificationText(notificationText: Int): Builder {
            popupBuilder.notificationText = notificationText
            return this
        }

        /**
         * Sets the center icon of the header view.
         * @param headerViewCenterIcon The resource ID of the center icon.
         * @return The Builder instance.
         */
        fun setHeaderViewCenterIcon(headerViewCenterIcon: Int, isAnimation: Boolean): Builder {
            popupBuilder.headerViewCenterIcon = headerViewCenterIcon
            popupBuilder.isHeaderViewCenterIconAnimation = isAnimation
            return this
        }


        /**
         * Sets the text
         * @param bodyTextWithHotCavityTemp The resource ID of the left button text.
         * @return The Builder instance.
         */
        @Suppress("unused")
        fun setbodyTextWithHotCavityTemp(
            bodyTextWithHotCavityTemp: String,
        ): Builder {
            popupBuilder.bodyTextWithHotCavityTemp = bodyTextWithHotCavityTemp
            return this
        }

        /**
         * Sets the text and click listener for the left button.
         * @param leftButtonText The resource ID of the left button text.
         * @param listener The listener for click events on the left button.
         * @return The Builder instance.
         */
        fun setLeftButton(
            @StringRes leftButtonText: Int,
            listener: NavigationButtonOnClickListener?,
        ): Builder {
            popupBuilder.leftButtonText = leftButtonText
            popupBuilder.leftButtonClickListener = listener
            return this
        }

        /**
         * Sets the text and click listener for the right button.
         * @param rightButtonText The resource ID of the right button text.
         * @param listener The listener for click events on the right button.
         * @return The Builder instance.
         */
        fun setRightButton(
            @StringRes rightButtonText: Int,
            listener: NavigationButtonOnClickListener?,
        ): Builder {
            popupBuilder.rightButtonText = rightButtonText
            popupBuilder.rightButtonClickListener = listener
            return this
        }

        fun setTitleTextDrawable(
            isDrawalbleVisible: Boolean,
            drawable: Int,
        ): Builder{
            popupBuilder.isTitleTextDrawable=isDrawalbleVisible
            popupBuilder.titleTextDrawable=drawable
            return this
        }

        /**
         * Sets the text and click listener for the center button.
         * @param centerButtonText The resource ID of the center button text.
         * @param listener The listener for click events on the center button.
         * @return The Builder instance.
         */
        fun setCenterButton(
            @StringRes centerButtonText: Int,
            listener: NavigationButtonOnClickListener?,
        ): Builder {
            popupBuilder.centerButtonText = centerButtonText
            popupBuilder.centerButtonClickListener = listener
            return this
        }

        /**
         * Sets whether the left button is enabled.
         * @param isLeftButtonEnabled True if the left button is enabled, false otherwise.
         * @return The Builder instance.
         */
        fun setIsLeftButtonEnable(isLeftButtonEnabled: Boolean): Builder {
            popupBuilder.isLeftButtonEnabled = isLeftButtonEnabled
            return this
        }

        /**
         * Sets whether the right button is enabled.
         * @param isRightButtonEnabled True if the right button is enabled, false otherwise.
         * @return The Builder instance.
         */
        fun setIsRightButtonEnable(isRightButtonEnabled: Boolean): Builder {
            popupBuilder.isRightButtonEnabled = isRightButtonEnabled
            return this
        }

        fun setCancellableOutSideTouch(isCancelableOutSideTouch: Boolean): Builder{
            popupBuilder.isCancelableOutSideTouch = isCancelableOutSideTouch
            return this
        }

        /**
         * Sets whether the center button is enabled.
         * @param isCenterButtonEnabled True if the center button is enabled, false otherwise.
         * @return The Builder instance.
         */
        @Suppress("unused")
        fun setIsCenterButtonEnable(isCenterButtonEnabled: Boolean): Builder {
            popupBuilder.isCenterButtonEnabled = isCenterButtonEnabled
            return this
        }

        /**
         * Sets whether the progress bar is visible.
         * @param isProgressBarVisible True if the progress bar is visible, false otherwise.
         * @return The Builder instance.
         */
        fun setIsProgressVisible(isProgressBarVisible: Boolean): Builder {
            popupBuilder.isProgressBarVisible = isProgressBarVisible
            return this
        }

        /**
         * Sets the progress percentage of the progress bar.
         * @param progressPercentage The progress percentage value.
         * @return The Builder instance.
         */
        fun setProgressPercentage(progressPercentage: Int): Builder {
            popupBuilder.progressPercentage = progressPercentage
            return this
        }

        /**
         * Sets whether the popup is center aligned.
         * @param isPopupCenterAligned True if the popup is center aligned, false otherwise.
         * @return The Builder instance.
         */
        fun setIsPopupCenterAligned(isPopupCenterAligned: Boolean): Builder {
            popupBuilder.isPopupCenterAligned = isPopupCenterAligned
            return this
        }

        /**
         * Sets description text font
         * @return The Builder instance.
         */
        fun setDescriptionTextFont(font: Typeface?): Builder {
            popupBuilder.descriptionTextFont = font
            return this
        }
        /**
         * Sets description text gravity
         * @param gravity - Gravity.START,Gravity.END etc
         * @return The Builder instance.
         */
        fun setDescriptionTextGravity(gravity: Int): Builder {
            popupBuilder.descriptionTextGravity = gravity
            return this
        }

        /**
         * Sets title text gravity
         * @param gravity - Gravity.START,Gravity.END etc
         * @return The Builder instance.
         */
        fun setTitleTextGravity(gravity: Int): Builder {
            popupBuilder.titleTextGravity = gravity
            return this
        }


        /**
         * Clear the current instance properties, so we can rebuild the current instance again.
         */
        @Suppress("unused")
        fun reset(): Builder {
            popupBuilder.titleText = UNKNOWN_VALUE
            popupBuilder.titleTextString = AppConstants.EMPTY_STRING
            popupBuilder.descriptionText = UNKNOWN_VALUE
            popupBuilder.descriptionTextString = AppConstants.EMPTY_STRING
            popupBuilder.notificationText = UNKNOWN_VALUE
            popupBuilder.headerViewCenterIcon = UNKNOWN_VALUE
            popupBuilder.isHeaderViewCenterIconAnimation = false
            popupBuilder.leftButtonText = UNKNOWN_VALUE
            popupBuilder.isLeftButtonEnabled = true
            popupBuilder.centerButtonText = UNKNOWN_VALUE
            popupBuilder.isCenterButtonEnabled = true
            popupBuilder.rightButtonText = UNKNOWN_VALUE
            popupBuilder.isRightButtonEnabled = true
            popupBuilder.isPopupCenterAligned = true
            popupBuilder.isProgressBarVisible = false
            popupBuilder.titleTextFormatArgs = null
            popupBuilder.bodyTextWithHotCavityTemp = AppConstants.EMPTY_STRING
            popupBuilder.descriptionTextGravity = UNKNOWN_VALUE
            popupBuilder.titleTextGravity = UNKNOWN_VALUE
            popupBuilder.descriptionTextFont = null
            return this
        }

        /**
         * method to complete the builder pattern and return the dialog object.
         */
        fun build(): ScrollDialogPopupBuilder {
            return popupBuilder
        }

        fun setPopupImageResource(imageIcon: Int): Builder {
            popupBuilder.imageTextDescription = imageIcon
            return this
        }
    }

    /**
     * Updates the progress of the ProgressBar in the dialog pop-up.
     *
     * @param progress The progress value.
     */
    fun updateProgressBar(progress: Int) {
        if (dialogPopupViewHolder != null && dialogPopupViewHolder?.progressbar != null && isProgressBarVisible) {
            HMILogHelper.Logd(tag, "JET Start progress: $progress")
            dialogPopupViewHolder?.progressbar?.progress = progress
            if (progress < AppConstants.DIGIT_SIX) dialogPopupViewHolder?.progressbarUpdateText?.text =
                AppConstants.DIGIT_THREE.toString()
            else if (progress < AppConstants.DIGIT_TEN) dialogPopupViewHolder?.progressbarUpdateText?.text =
                AppConstants.DIGIT_TWO.toString()
            else if (progress < AppConstants.DIGIT_FIFTEEN) dialogPopupViewHolder?.progressbarUpdateText?.text =
                AppConstants.DIGIT_ONE.toString()
        }
    }

    /**
     * reversing aborted JET start dialog,
     * the handler is going to post runnable every  millis until progress is made 0
     * and then dismissing the dialog
     */
    fun updateAbortedProgressBar(onAbortProgressBar: () -> Unit) {
        try {
            if (dialogPopupViewHolder != null && dialogPopupViewHolder?.progressbar != null && isProgressBarVisible) {
                var progress = dialogPopupViewHolder?.progressbar?.progress ?: 0
                HMILogHelper.Logd(tag, "JET Start progress: $progress")
                val handler = Handler(Looper.getMainLooper())
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(
                    {
                        progress--
                        dialogPopupViewHolder?.progressbar?.progress = progress
                        if (progress == 0) {
                            handler.removeCallbacksAndMessages(null)
                            HMILogHelper.Logd(
                                tag,
                                "Reversing  JET Start progress 0, dismissing JET start Flow"
                            )
                            onAbortProgressBar()
                        } else {
                            updateAbortedProgressBar(onAbortProgressBar)
                        }
                    }, AppConstants.DIGIT_FIVE.toLong()
                )
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            HMILogHelper.Loge(
                "Error in aborting JET start progress ${exception.message}"
            )
        }
    }
    /**
     * complete progressbar
     * the handler is going to post runnable to check if the progress if filled then
     * going to call onCompleteProgressBar which would dismiss the dialog
     * Reason: Behind the scene holding interval will be published to make sure that user has
     * hold for a solid 1.5 sec fill while ring with 0 text after getting onHMILongRightKnobPress
     */
    fun updateCompletedProgressBar(fragment: Fragment, onCompleteProgressBar: () -> Unit) {
        try {
            if (dialogPopupViewHolder != null && dialogPopupViewHolder?.progressbar != null && isProgressBarVisible) {
                val handler = Handler(Looper.getMainLooper())
                handler.removeCallbacksAndMessages(null)
                val progress = dialogPopupViewHolder?.progressbar?.progress ?: 0
                HMILogHelper.Logd(tag, "JET Start progress: $progress")
                dialogPopupViewHolder?.progressbar?.progress = AppConstants.DIGIT_FIFTEEN
                dialogPopupViewHolder?.progressbarUpdateText?.text =
                    AppConstants.DIGIT_ZERO.toString()
                dialogPopupViewHolder?.progressbar?.context?.getColor(R.color.color_white)?.let {
                        dialogPopupViewHolder?.progressbar?.progressDrawable?.colorFilter =
                            (BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                                it, BlendModeCompat.SRC_ATOP
                            ))
                    }
                if (progress < AppConstants.DIGIT_FIFTEEN) {
                    handler.postDelayed(
                        {
                            updateCompletedProgressBar(fragment, onCompleteProgressBar)
                        }, AppConstants.DIGIT_FIFTEEN.toLong()
                    )
                } else {
                    //fallback code to dismiss JET start popup after some time, if any issue happens in near then uncomment this code
/*                    handler.postDelayed({
                        HMILogHelper.Logd(
                            fragment.tag,
                            "dismissPopupByTag POPUP_TAG_JET_START from handler"
                        )
                        if (isAnyPopupShowing()) PopUpBuilderUtils.dismissPopupByTag(
                            activity?.supportFragmentManager,
                            AppConstants.POPUP_TAG_JET_START
                        )
                    }, AppConstants.POPUP_DISMISS_DELAY.toLong())*/
                    onCompleteProgressBar()
                }
            }
        }catch (exception: Exception){
            exception.printStackTrace()
            HMILogHelper.Loge(fragment.tag, "Error in completing JET start progress ${exception.message}")
        }
    }

    companion object {
        //maintaining this to make it true when dialog gets created and false on destroy
        private var isPopupVisible: Boolean = false
        private const val UNKNOWN_VALUE = -1

        /**
         * to know if any popup is visible though out the app and regardless of builder object
         * @return true if popup is visible, false otherwise
         */
        fun isAnyPopupShowing(): Boolean {
            return isPopupVisible
        }
    }

    /**
     * Method to initialise the timeouts
     */
    override fun initTimeout() {
        if (onTimeoutObserverListener != null) {
            timeoutViewModel?.timeoutCallback?.observe(
                viewLifecycleOwner
            ) { timeoutStatesEnum: TimeoutStatesEnum ->
                HMILogHelper.Logd("TimeoutCallback: " + timeoutStatesEnum.name)
                if (timeoutStatesEnum == TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    onTimeoutObserverListener!!.onTimeout(timeoutStatesEnum)
                }
            }
            timeoutViewModel?.setTimeout(timeout)
        } else if (timeout >= 0) {
            //if not set to negative, set the default timeout
            super.initTimeout()
        }
    }
}