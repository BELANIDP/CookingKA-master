package android.presenter.fragments.kitchentimer

import android.content.res.Resources
import android.presenter.customviews.textButton.TextButton
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.KitchenTimerWidgetBinding
import com.whirlpool.hmi.kitchentimer.uicomponents.widgets.KitchenTimerTextView
import core.jbase.abstractViewHolders.AbstractKitchenTimerWidgetListItemViewProvider

/**
 * File       : android.presenter.fragments.kitchentimer.KitchenTimerWidgetViewProvider
 * Brief      : Kitchen Timer item list view binding provider
 * Author     : Hiren
 * Created On : 06/19/2024
 * Details    : To show play, pause, Kitchen timer name in the widget and to provide binding data
 **/
class KitchenTimerWidgetViewProvider: AbstractKitchenTimerWidgetListItemViewProvider() {
    private lateinit var itemBinding: KitchenTimerWidgetBinding
    override fun inflate(inflater: LayoutInflater?) {
        itemBinding = KitchenTimerWidgetBinding.inflate(LayoutInflater.from(inflater?.context))
    }

    override val view: View
        get() = itemBinding.root

    private fun provideResources(): Resources {
        return itemBinding.root.resources
    }

    override fun providePlusOneMinButtonText(): CharSequence {
        return provideResources().getString(R.string.text_add_1_min)
    }

    override fun provideKitchenTimerNameTextView(): TextView {
        return itemBinding.tvKitchenTimerName
    }

    override fun provideResumeDrawable(): Int {
        return R.drawable.ic_kt_play
    }

    override fun providePauseDrawable(): Int {
        return R.drawable.ic_kt_pause
    }

    override fun provideCancelDrawable(): Int {
        return R.drawable.ic_kt_cancel
    }

    override fun providePauseResumeKnobUnderline(): View {
       return itemBinding.ktKnobPauseUnderline
    }

    override fun provideOneMinKnobUnderline(): View {
        return itemBinding.ktKnobOneMinUnderline
    }

    override fun provideCancelKnobUnderline(): View {
        return itemBinding.ktKnobCancelUnderline
    }

    override fun provideKitchenTimerTextView(): KitchenTimerTextView {
        return itemBinding.tvKitchenTimeRemaining
    }

    override fun providePauseResumeImageView(): ImageView {
        return  itemBinding.ivKitchenTimerPause
    }

    override fun provideCancelButtonImageView(): ImageView {
        return  itemBinding.ivKitchenTimerCancel
    }

    override fun provideProgressBar(): ProgressBar {
        return itemBinding.kitchenTimerProgressBar
    }

    override fun providePlusOneMinButton(): TextButton {
        return itemBinding.tvKitchenTimerAddOneMin
    }
}