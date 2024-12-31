/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.utils.CapabilityKeys

/**
 * File        : core.utils.CommonAnimationUtils <br>
 * Brief       : Class responsible for provide the animation utility methods <br>
 * Author      : GHARDNS/Nikki
 * Created On  : 18-03-2024 <br>
 */

object CommonAnimationUtils {

    private var ERROR_HELPER_TRANSLATE_MINUS_Y = -5f
    private var ERROR_HELPER_TRANSLATE_Y = 5f

    private var TRANSLATE_Y = "translationY"

    /**
     * Common function for Animation
     * @param context Context of the class
     * @param textView View on which animation has to apply
     * @param anim type of animation to be applied
     * @param duration timeout for animation
     * @param animationListener listener for animation
     */
    fun setViewAnimation(
        context: Context?,
        textView: View,
        anim: Int,
        @Suppress("UNUSED_PARAMETER") duration: Int,
        animationListener: Animation.AnimationListener?
    ) {
        val animation = AnimationUtils.loadAnimation(
            context,
            anim
        )
        textView.startAnimation(animation)
        animation.setAnimationListener(animationListener)
    }

    /**
     * Common function for Error Helper Animation
     * @param context Context of the class
     * @param helperText error helper text view
     * @param backSpaceIcon back icon for number pad fields
     * @param numPadInputField textview for number pad fields
     */
    @Suppress("unused")
    fun setErrorHelperAnimation(
        context: Context?,
        helperText: View?,
        backSpaceIcon: View?,
        numPadInputField: View?
    ) {
        if (helperText != null) {
            setViewAnimation(
                context,
                helperText,
                R.anim.anim_numpad_errorhelper_translate_y,
                context?.resources?.getInteger(R.integer.duration_error_helper_translate_y_anim)
                    ?: 0,
                null
            )
        }
        if (backSpaceIcon != null) {
            setViewObjectAnimation(
                backSpaceIcon,
                TRANSLATE_Y,
                ERROR_HELPER_TRANSLATE_MINUS_Y,
                null
            )
        }
        if (numPadInputField != null) {
            setViewObjectAnimation(
                numPadInputField,
                TRANSLATE_Y,
                ERROR_HELPER_TRANSLATE_MINUS_Y,
                object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        setViewAnimation(
                            context,
                            numPadInputField,
                            R.anim.anim_numpad_inputfield_translate_x_reverse,
                            context?.resources?.getInteger(R.integer.duration_error_helper_translate_y_anim)
                                ?: 0,
                            null
                        )
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
        }
    }

    /**
     * Common function for Error Helper Animation
     * @param context Context of the class
     * @param helperText error helper text view
     */
    fun setErrorHelperAnimation(
        context: Context?,
        helperText: View?,
    ) {
        if (helperText != null) {
            setViewAnimation(
                context,
                helperText,
                R.anim.anim_numpad_errorhelper_translate_x,
                context?.resources?.getInteger(R.integer.duration_error_helper_translate_x_anim)
                    ?: 0,
                null
            )
        }
    }

    /**
     * common function for  resetting Error Helper Animation
     * @param backSpaceIcon back icon for number pad fields
     * @param numPadInputField textview for number pad fields
     */
    @Suppress("unused")
    fun resetErrorHelperAnimation(backSpaceIcon: View?, numPadInputField: View?) {
        if (backSpaceIcon != null) {
            setViewObjectAnimation(
                backSpaceIcon,
                TRANSLATE_Y,
                ERROR_HELPER_TRANSLATE_Y,
                null
            )
        }
        if (numPadInputField != null) {
            setViewObjectAnimation(
                numPadInputField,
                TRANSLATE_Y,
                ERROR_HELPER_TRANSLATE_Y,
                null
            )
        }
    }

    /**
     * Common function for ObjectAnimator
     * @param textView View on which animation has to apply
     * @param property type of animation to be applied
     * @param f timeout for animation
     * @param animatorListener listener for animation
     */
    private fun setViewObjectAnimation(
        textView: View,
        property: String?,
        f: Float,
        animatorListener: Animator.AnimatorListener?
    ) {
        val objectAnimator = ObjectAnimator.ofFloat(textView, property, f)
        objectAnimator.setDuration(
            textView.context.resources.getInteger(R.integer.duration_common_view_animator).toLong()
        )
        objectAnimator.start()
        if (animatorListener != null) objectAnimator.addListener(animatorListener)
    }

    /**
     * Common function for FadeIn
     * @param viewToAnimate View on which animation has to apply
     * @param durationToLoad time frame to load animation
     */
    fun setFadeInViewAnimation(viewToAnimate: View, durationToLoad: Long) {
        viewToAnimate.animation =
            AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.fade_in).apply {
                duration = durationToLoad
            }
    }

    /**
     * Common function for FadeOut
     * @param viewToAnimate View on which animation has to apply
     * @param durationToLoad time frame to load animation
     */
    fun setFadeOutViewAnimation(viewToAnimate: View, durationToLoad: Long) {
        viewToAnimate.animation =
            AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.fade_out).apply {
                duration = durationToLoad
            }
    }

    /**
     * Common function to play lottie animation
     * @param lottieView View on which animation has to apply
     * @param rawAnimFile to load animation
     */
    fun playLottieAnimation(lottieView: LottieAnimationView?, rawAnimFile: Int) {
        lottieView?.visibility = View.VISIBLE
        lottieView?.setAnimation(rawAnimFile)
        lottieView?.repeatCount = ValueAnimator.INFINITE
        lottieView?.playAnimation()
    }

    /**
     * Extension function for fragments to play animation on views
     * @param viewToAnimate
     * @param animRes
     * @param time
     * @param delay
     */
    fun Fragment.playAnimation(viewToAnimate: View, animRes : Int, time: Long = 500, delay : Long = 0){
        val animation = AnimationUtils.loadAnimation(requireContext(), animRes).apply {
            duration = time
            startOffset = delay
        }
        viewToAnimate.startAnimation(animation)
    }
    /**
     * Extension function for fragments to just to animate views
     * @param viewToAnimate
     * @param animRes
     */
    fun Fragment.justAnimate(viewToAnimate: View, animRes : Int){
        val animation = AnimationUtils.loadAnimation(requireContext(), animRes)
        viewToAnimate.startAnimation(animation)
    }

    /**
     * to animate any view in X axis
     *
     * @param view
     * @param startX
     * @param endX
     * @param duration
     */
    fun animateViewX(view: View, startX: Float, endX: Float, duration: Long) {
        // Set the initial Y position of the view
        view.y = view.y

        // Create an ObjectAnimator to animate the view along the Y-axis
        ObjectAnimator.ofFloat(view, "translationX", startX, endX).apply {
            this.duration = duration
            start()
        }
    }

    /**
     * to animate any view in Y axis
     */
    fun slideUpViews(views: List<View?>, duration: Long) {
        views.forEach { view ->
            view?.let {
                // First animation: Slide up
                ObjectAnimator.ofFloat(it, "translationY", -it.height.toFloat()).apply {
                    this.duration = duration
                    interpolator = AccelerateInterpolator()
                    startDelay = 100
                    start()

                    addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                            // Make the view invisible as it starts sliding up
                            view.alpha = 0f
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            // After sliding up, wait for 100ms, then return the view
                            view.postDelayed({
                                // Second animation: Slide back to original position
                                ObjectAnimator.ofFloat(view, "translationY", 0f).apply {
                                    this.duration = duration
                                    interpolator = AccelerateInterpolator()
                                    start()

                                    addListener(object : Animator.AnimatorListener {
                                        override fun onAnimationStart(animation: Animator) {
                                            // Keep the view invisible during the return motion
                                            view.alpha = 0f
                                        }

                                        override fun onAnimationEnd(animation: Animator) {
                                            // Set the view's visibility back to visible after the return motion
                                        }

                                        override fun onAnimationCancel(animation: Animator) {}
                                        override fun onAnimationRepeat(animation: Animator) {}
                                    })
                                }.start()
                            }, 100) // Delay the second animation by 100ms
                        }

                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                    })
                }
            }
        }
    }


    /**
     * to animate any view in Y axis
     */
    fun animateView(view: View, fromY: Float, toY: Float, duration: Long) {
        view.translationY = fromY
        ObjectAnimator.ofFloat(view, "translationY", toY).apply {
            this.duration = duration
            interpolator = AccelerateInterpolator()
            start()
        }
    }

    /**
     * to animate any view in Y axis
     */
    fun dissolveViews(
        views: List<View?>,
        duration: Long,
        delay: Long,
        endAction: (() -> Unit)? = null
    ) {
        views.forEach { view ->
            view?.postDelayed({
                view.animate()
                    .alpha(0f)
                    .setDuration(duration)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .withEndAction(endAction)
                    .start()
            }, delay)
        }
    }

    /**
     * Animates the transition from "Set Cook Time" view to "Resume Cooking" view.
     * The animation involves sliding out the starting view and sliding in the finishing view,
     * with a fade effect on the parent layout for a smooth transition.
     *
     * @param statusWidget The widget containing the views to be animated.
     */
    fun animateToResumeCookingView(statusWidget: CookingStatusWidget, fragment: Fragment) {
        if(fragment.isAdded){

            val startingView =
                if (statusWidget.statusWidgetHelper.tvSetCookTime()?.isVisible == true) {
                    statusWidget.statusWidgetHelper.tvSetCookTime()
                } else {
                    statusWidget.statusWidgetHelper.tvOvenStateAction()
                }
            val finishingView = statusWidget.statusWidgetHelper.tvResumeCooking()
            val startingViewParent = statusWidget.statusWidgetHelper.clParentWidgetAction()

            val screenWidth: Float = startingView?.width?.toFloat()?:0f

            // Slide out the starting view
            val slideOut = ObjectAnimator.ofFloat(startingView, "translationX", 0f, screenWidth).apply {
                duration = AppConstants.RESUME_COOKING_ANIMATION_START_DURATION
            }

            // Fade out the starting view in sync with the slide
            val fadeOut = ObjectAnimator.ofFloat(startingViewParent, "alpha", AppConstants.RESUME_COOKING_FADE_OUT_ALPHA_START, AppConstants.RESUME_COOKING_FADE_OUT_ALPHA_END).apply {
                duration = AppConstants.RESUME_COOKING_ANIMATION_DELAY_DURATION
                startDelay = AppConstants.RESUME_COOKING_ANIMATION_DELAY_DURATION
            }

            // Slide in the finishing view
            val slideIn = ObjectAnimator.ofFloat(finishingView, "translationX", -screenWidth, 0f).apply {
                duration = AppConstants.RESUME_COOKING_ANIMATION_START_DURATION
            }

            // Fade in the finishing view
            val fadeIn = ObjectAnimator.ofFloat(finishingView, "alpha", AppConstants.RESUME_COOKING_FADE_IN_ALPHA_START, AppConstants.RESUME_COOKING_FADE_IN_ALPHA_END).apply {
                duration = AppConstants.RESUME_COOKING_ANIMATION_START_DURATION
            }

            AnimatorSet().apply {
                playTogether(slideOut, fadeOut, slideIn, fadeIn)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        if (fragment.isAdded) {
                            finishingView?.visible()
                        }
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        if (fragment.isAdded) {
                            startingView?.gone()
                            startingViewParent?.gone()
                        }
                    }
                })
                start()
            }
        }
    }
    /**
     * Animates the transition from "Resume Cooking" view to "Set Cook Time" view.
     * Animates and resume cooking for a particular cavity.
     *
     * @param statusWidget The widget containing the views to be animated.
     * @param fragment fragment instance
     * @param onClickResume - highorder function onClickResume() for callback
     */
    fun animateToCookingView(cookingVM: CookingViewModel, statusWidget: CookingStatusWidget?, fragment: Fragment, onClickResume: ()->Unit) {
        if(fragment.isAdded && cookingVM.recipeExecutionViewModel.recipeName.value != CapabilityKeys.STEAM_CLEAN_KEY){
            val startingView = statusWidget?.statusWidgetHelper?.tvResumeCooking()
            val finishingView =
                if (statusWidget?.statusWidgetHelper?.isCookTimeNotAllowed() == true) {
                    statusWidget.statusWidgetHelper.tvOvenStateAction()// Turn off
                } else {
                    statusWidget?.statusWidgetHelper?.tvSetCookTime() // +5 min , Start Timer , Set Cook Time
                }
            val startingViewParent = statusWidget?.statusWidgetHelper?.clParentWidgetAction()

            val screenWidth: Float = startingView?.width?.toFloat() ?: 0.0f

            // Slide out the starting view
            val slideOut = ObjectAnimator.ofFloat(startingView, "translationX", 0f, -screenWidth).apply {
                duration = AppConstants.RESUME_COOKING_ANIMATION_START_DURATION
            }

            // Fade out the starting view
            val fadeOut = ObjectAnimator.ofFloat(startingView, "alpha", AppConstants.RESUME_COOKING_FADE_OUT_ALPHA_START, AppConstants.RESUME_COOKING_FADE_OUT_ALPHA_END).apply {
                duration = AppConstants.RESUME_COOKING_ANIMATION_START_DURATION
            }

            // Fade in the parent view
            val fadeInParent = ObjectAnimator.ofFloat(startingViewParent, "alpha", AppConstants.RESUME_COOKING_FADE_IN_ALPHA_START, AppConstants.RESUME_COOKING_FADE_IN_ALPHA_END).apply {
                duration = AppConstants.RESUME_COOKING_ANIMATION_DELAY_DURATION
            }

            // Slide in the finishing view
            val slideIn = ObjectAnimator.ofFloat(finishingView, "translationX", screenWidth, 0f).apply {
                duration = AppConstants.RESUME_COOKING_ANIMATION_START_DURATION
            }

            // Fade in the finishing view
            val fadeIn = ObjectAnimator.ofFloat(finishingView, "alpha", AppConstants.RESUME_COOKING_FADE_IN_ALPHA_START, AppConstants.RESUME_COOKING_FADE_IN_ALPHA_END).apply {
                duration = AppConstants.RESUME_COOKING_ANIMATION_START_DURATION
            }

            AnimatorSet().apply {
                playTogether(slideOut, fadeOut, fadeInParent, slideIn, fadeIn)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        if(fragment.isAdded) {
                            finishingView?.visible()
                            startingViewParent?.visible()
                        }
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        if (fragment.isAdded) {
                            startingView?.gone()
                            onClickResume()
                        }
                    }
                })
                start()
            }
        }
        else{
            onClickResume()
        }
    }
}