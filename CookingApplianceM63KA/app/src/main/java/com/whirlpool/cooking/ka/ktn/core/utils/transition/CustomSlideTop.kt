package core.utils.transition

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionValues
import androidx.transition.Visibility

class CustomSlideTop(private val slideDistance: Float = 200f) : Visibility() {

    override fun onAppear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        // For "enter" transitions, slide in from above
        val endY = 0f
        val startY = endY - slideDistance
        view.translationY = startY
        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY)
    }

    override fun onDisappear(
        sceneRoot: ViewGroup,
        view: View,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        // For "exit" transitions, slide out to above
        val startY = view.translationY
        val endY = startY - slideDistance
        return ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY)
    }
}