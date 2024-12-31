package android.presenter.fragments

import android.content.Context
import android.view.LayoutInflater
import android.widget.ScrollView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.FragmentInstructionWidgetBinding
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget


class InstructionWidget
/**
 * Constructor for creating ToggleSwitch programmatically.
 *
 * @param context The context in which the ToggleSwitch is created.
 */(context: Context) : ConstraintLayout(context) {
    private lateinit var binding: FragmentInstructionWidgetBinding

    init {
        init(context)
    }


    private fun init(context: Context) {
        binding =
            FragmentInstructionWidgetBinding.inflate(LayoutInflater.from(context), this, true)
    }


    fun getHeaderBarWidget(): HeaderBarWidget {
        return binding.instructionHeader
    }

    fun getDescriptionTextView(): AppCompatTextView {
        return binding.textViewDescription
    }

    fun getScrollView(): ScrollView {
        return binding.scrollView
    }

    fun inflateLayout(inflater: LayoutInflater): FragmentInstructionWidgetBinding {
        binding =
            FragmentInstructionWidgetBinding.inflate(inflater)
        return binding
    }
}
