package core.utils.transition

import androidx.transition.Transition

object TransitionListener {

    fun onTransitionListener(
        onTransitionStart: (transition: Transition) -> Unit = {},
        onTransitionEnd: (transition: Transition) -> Unit = {},
        onTransitionCancel: (transition: Transition) -> Unit = {},
        onTransitionPause: (transition: Transition) -> Unit = {},
        onTransitionResume: (transition: Transition) -> Unit = {},
    ):Transition.TransitionListener {
        return object : Transition.TransitionListener{
            /**
             * Notification about the start of the transition.
             *
             * @param transition The started transition.
             */
            override fun onTransitionStart(transition: Transition) {
                onTransitionStart(transition)
            }

            /**
             * Notification about the end of the transition. Canceled transitions
             * will always notify listeners of both the cancellation and end
             * events. That is, [.onTransitionEnd] is always called,
             * regardless of whether the transition was canceled or played
             * through to completion.
             *
             * @param transition The transition which reached its end.
             */
            override fun onTransitionEnd(transition: Transition) {
                onTransitionEnd(transition)
            }

            /**
             * Notification about the cancellation of the transition.
             * Note that cancel may be called by a parent [TransitionSet] on
             * a child transition which has not yet started. This allows the child
             * transition to restore state on target objects which was set at
             * [ createAnimator()][.createAnimator] time.
             *
             * @param transition The transition which was canceled.
             */
            override fun onTransitionCancel(transition: Transition) {
                onTransitionCancel(transition)
            }

            /**
             * Notification when a transition is paused.
             * Note that createAnimator() may be called by a parent [TransitionSet] on
             * a child transition which has not yet started. This allows the child
             * transition to restore state on target objects which was set at
             * [ createAnimator()][.createAnimator] time.
             *
             * @param transition The transition which was paused.
             */
            override fun onTransitionPause(transition: Transition) {
                onTransitionPause(transition)
            }

            /**
             * Notification when a transition is resumed.
             * Note that resume() may be called by a parent [TransitionSet] on
             * a child transition which has not yet started. This allows the child
             * transition to restore state which may have changed in an earlier call
             * to [.onTransitionPause].
             *
             * @param transition The transition which was resumed.
             */
            override fun onTransitionResume(transition: Transition) {
                onTransitionResume(transition)
            }

        }
    }
}