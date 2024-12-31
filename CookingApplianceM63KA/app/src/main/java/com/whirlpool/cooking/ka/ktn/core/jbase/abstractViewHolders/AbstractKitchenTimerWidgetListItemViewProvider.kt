package core.jbase.abstractViewHolders

import android.presenter.customviews.textButton.TextButton
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.whirlpool.hmi.kitchentimer.uicomponents.widgets.KitchenTimerTextView

/**
 * File       : core.jbase.abstractViewHolders.AbstractKitchenTimerWidgetListItemViewProvider
 * Brief      : Kitchen Timer list item view provider
 * Author     : SDK (can be leverage from SDK if they update based on KA requirements)
 * Created On : 06/19/2024
 * Details    : To show item component in the Kitchen Timer Widget list user can add, delete or pause a particular Kitchen Timer Widget
 **/
abstract class AbstractKitchenTimerWidgetListItemViewProvider {
    /**
     * This is the view provider for the list items in the [android.presenter.basefragments.AbstractKitchenTimerTumblerListFragment].
     * This view enables the ability for the fragment to inflate the view and then maps required UI elements
     * for the given view to display the kitchen timer time, and pause, resume, and cancel the kitchen timer.
     * It also optionally allows the ability to set a TextView to display the Kitchen Timer name.
     */
    abstract fun inflate(inflater: LayoutInflater?)

    /**
     * Instance of the view
     * @return [View]
     */
    abstract val view: View?

    /**
     * Interface to return the the kitchen timer text view interface
     * @return [KitchenTimerTextView]
     */
    abstract fun provideKitchenTimerTextView(): KitchenTimerTextView?

    /**
     * Interface to implement the Pause/Resume button interface.
     * The button is implemented in the form of a text view
     * @return [TextView]
     */
    abstract fun providePauseResumeImageView(): ImageView?

    /**
     * Interface to implement the Cancel button interface.
     * @return [TextView]
     */
    abstract fun provideCancelButtonImageView(): ImageView?

    /**
     * Interface to provide the progress bar for kitchen timer progress
     * @return [android.widget.ProgressBar]
     */
    abstract fun provideProgressBar(): ProgressBar?


    /**
     * Interface to implement the +1 min button interface.
     * The button is implemented in the form of a button
     * @return [TextView]
     */
    abstract fun providePlusOneMinButton(): TextButton?

    /**
     * Interface to implement the +1 min button text.
     * This interface is for providing the text of the +1min button
     * @return [CharSequence]
     */
    open fun providePlusOneMinButtonText(): CharSequence? {
        return null
    }

    /**
     * This interface is for the Kitchen Timer Name
     * @return Textview with Kitchen Timer name
     */
    open fun provideKitchenTimerNameTextView(): TextView? {
        return null
    }

    abstract fun provideResumeDrawable(): Int
    abstract fun providePauseDrawable(): Int
    abstract fun provideCancelDrawable(): Int
    abstract fun providePauseResumeKnobUnderline(): View
    abstract fun provideOneMinKnobUnderline(): View
    abstract fun provideCancelKnobUnderline(): View
}