/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.digital_unboxing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.UnboxingTimerTumblerItemViewBinding
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerItemViewInterface
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerViewHolderInterface
import core.utils.gone
import core.utils.invisible
import core.utils.visible

/**
 * File       : android.presenter.fragments.digital_unboxing.UnboxingCustomTumblerAdapter
 * Brief      : Class for custom view for tumbler
 * Author     : Rajendra
 * Created On : 10/09/2024
 * Details    : passing a xml layout to the view holder
 */
class UnboxingCustomTumblerAdapter(
    private var numbers: List<String>,
    private var isFocused: Boolean = false,
    private var isAMPMSelected: Boolean = false,
    private var isKnobRotationActive:Boolean = false
) : BaseTumblerItemViewInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val unboxingTimerTumblerItemViewBinding =
            UnboxingTimerTumblerItemViewBinding.inflate(LayoutInflater.from(parent.context))
        return UnboxingCustomTumblerViewHolder(unboxingTimerTumblerItemViewBinding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        itemIdentifier: String?,
        index: Int,
        isSelected: Boolean
    ) {
        if (holder is UnboxingCustomTumblerViewHolder) {
            val modePosition: Int = index % numbers.size
            holder.unboxingCustomTumblerItemViewBinding.title.text = numbers[modePosition]
            holder.unboxingCustomTumblerItemViewBinding.titleUnSelected.text = numbers[modePosition]
            holder.unboxingCustomTumblerItemViewBinding.titleAmPm.text = numbers[modePosition]
            holder.unboxingCustomTumblerItemViewBinding.titleAmPmUnSelected.text = numbers[modePosition]

            if(isKnobRotationActive){
                val focusedColor = ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.kt_unselected_text_color
                )
                holder.unboxingCustomTumblerItemViewBinding.title.setTextColor(focusedColor)
                if (isAMPMSelected) holder.unboxingCustomTumblerItemViewBinding.titleAmPm.setTextColor(focusedColor)
            } else {
                val defaultColor =
                    ContextCompat.getColor(holder.itemView.context, R.color.common_solid_white)
                holder.unboxingCustomTumblerItemViewBinding.title.setTextColor(defaultColor)
                if (isAMPMSelected) holder.unboxingCustomTumblerItemViewBinding.titleAmPm.setTextColor(defaultColor)
            }

            if (isFocused) {
                holder.unboxingCustomTumblerItemViewBinding.bottomLine.visible()
            } else {
                holder.unboxingCustomTumblerItemViewBinding.bottomLine.invisible()
            }
            if (isSelected) {
                if (isAMPMSelected) {
                    holder.unboxingCustomTumblerItemViewBinding.title.gone()
                    holder.unboxingCustomTumblerItemViewBinding.titleUnSelected.gone()
                    holder.unboxingCustomTumblerItemViewBinding.titleAmPm.visible()
                    holder.unboxingCustomTumblerItemViewBinding.titleAmPmUnSelected.gone()

                } else {
                    holder.unboxingCustomTumblerItemViewBinding.title.visible()
                    holder.unboxingCustomTumblerItemViewBinding.titleUnSelected.gone()
                    holder.unboxingCustomTumblerItemViewBinding.titleAmPm.gone()
                    holder.unboxingCustomTumblerItemViewBinding.titleAmPmUnSelected.gone()
                }

            } else {
                holder.unboxingCustomTumblerItemViewBinding.title.gone()
                holder.unboxingCustomTumblerItemViewBinding.titleUnSelected.visible()
                holder.unboxingCustomTumblerItemViewBinding.bottomLine.invisible()
                if (isAMPMSelected) {
                    holder.unboxingCustomTumblerItemViewBinding.titleAmPm.gone()
                    holder.unboxingCustomTumblerItemViewBinding.titleAmPmUnSelected.visible()
                    holder.unboxingCustomTumblerItemViewBinding.title.gone()
                    holder.unboxingCustomTumblerItemViewBinding.titleUnSelected.gone()
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
}
class UnboxingCustomTumblerViewHolder(var unboxingCustomTumblerItemViewBinding: UnboxingTimerTumblerItemViewBinding) :
    RecyclerView.ViewHolder(unboxingCustomTumblerItemViewBinding.root),
    BaseTumblerViewHolderInterface {
    private var value: String? = null
    override fun getValue(): String? {
        return value
    }

    override fun setValue(value: String) {
        this.value = value
    }

    override fun getDisplayedText(): String {
        return unboxingCustomTumblerItemViewBinding.title.text.toString()
    }
}