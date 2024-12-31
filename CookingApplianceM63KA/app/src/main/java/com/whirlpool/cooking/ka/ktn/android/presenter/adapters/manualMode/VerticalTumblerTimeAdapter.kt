/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.adapters.manualMode

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.VerticalTumblerTimeItemViewBinding
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerItemViewInterface
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerViewHolderInterface
import core.utils.gone
import core.utils.invisible
import core.utils.visible

/**
 * File       : android.presenter.fragments.kitchentimer.KitchenTimerTumblerAdapter
 * Brief      : Class for custom view for tumbler
 * Author     : PANDES18
 * Created On : 06/27/2024
 * Details    : passing a xml layout to the view holder
 */
class VerticalTumblerTimeAdapter(private var numbers : List<String>, private var isFocused: Boolean = false, private var isKnobRotationActive: Boolean) : BaseTumblerItemViewInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val verticalTumblerTimeItemViewBinding =
            VerticalTumblerTimeItemViewBinding.inflate(LayoutInflater.from(parent.context))
        return VerticalTumblerTimeViewHolder(verticalTumblerTimeItemViewBinding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        itemIdentifier: String?,
        index: Int,
        isSelected: Boolean
    ) {
        if (holder is VerticalTumblerTimeViewHolder) {
            val modePosition: Int = index % numbers.size
            holder.verticalTumblerTimeItemViewBinding.title.text = numbers[modePosition]
            holder.verticalTumblerTimeItemViewBinding.titleUnSelected.text = numbers[modePosition]

            if(isKnobRotationActive){
                val focusedColor = ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.kt_unselected_text_color
                )
                holder.verticalTumblerTimeItemViewBinding.title.setTextColor(focusedColor)
            } else {
                val defaultColor =
                    ContextCompat.getColor(holder.itemView.context, R.color.common_solid_white)
                holder.verticalTumblerTimeItemViewBinding.title.setTextColor(defaultColor)
            }

            if ( isFocused) {
                holder.verticalTumblerTimeItemViewBinding.bottomLine.visible()
            }else{
                holder.verticalTumblerTimeItemViewBinding.bottomLine.invisible()
            }

            if (isSelected) {
                holder.verticalTumblerTimeItemViewBinding.title.visible()
                holder.verticalTumblerTimeItemViewBinding.titleUnSelected.gone()
            }else{
                holder.verticalTumblerTimeItemViewBinding.title.gone()
                holder.verticalTumblerTimeItemViewBinding.titleUnSelected.visible()
                holder.verticalTumblerTimeItemViewBinding.bottomLine.invisible()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
}
class VerticalTumblerTimeViewHolder(var verticalTumblerTimeItemViewBinding: VerticalTumblerTimeItemViewBinding) :
    RecyclerView.ViewHolder(verticalTumblerTimeItemViewBinding.root),
    BaseTumblerViewHolderInterface {
    private var value: String? = null
    override fun getValue(): String {
        return value!!
    }

    override fun setValue(value: String) {
        this.value = value
    }

    override fun getDisplayedText(): String {
        return verticalTumblerTimeItemViewBinding.title.text.toString()
    }
}