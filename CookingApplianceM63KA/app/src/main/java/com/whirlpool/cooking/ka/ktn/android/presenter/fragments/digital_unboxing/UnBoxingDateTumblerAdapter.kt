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
import com.whirlpool.cooking.ka.databinding.UnboxingDateTumblerItemViewBinding
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerItemViewInterface
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerViewHolderInterface
import core.utils.gone
import core.utils.invisible
import core.utils.visible

/**
 * File       : android.presenter.fragments.kitchentimer.UnBoxingDateTumblerAdapter
 * Brief      : Class for custom view for tumbler
 * Author     : Nikki Gharde
 * Created On : 18/sep/2024
 * Details    : passing a xml layout to the view holder
 */
class UnBoxingDateTumblerAdapter(private var numbers: List<String>, private var isFocused: Boolean = false, private var isKnobRotationActive:Boolean = false) : BaseTumblerItemViewInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = UnboxingDateTumblerItemViewBinding.inflate(LayoutInflater.from(parent.context))
        return UnBoxingDateTumblerViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        itemIdentifier: String?,
        index: Int,
        isSelected: Boolean
    ) {
        if (holder is UnBoxingDateTumblerViewHolder) {
            val modePosition: Int = index % numbers.size
            holder.binding.title.text = numbers[modePosition]
            holder.binding.titleUnSelected.text = numbers[modePosition]

            if(isKnobRotationActive){
                val focusedColor = ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.kt_unselected_text_color
                )
                holder.binding.title.setTextColor(focusedColor)
            } else {
                val defaultColor =
                    ContextCompat.getColor(holder.itemView.context, R.color.common_solid_white)
                holder.binding.title.setTextColor(defaultColor)
            }

            if (isFocused) {
                holder.binding.bottomLine.visible()
            } else {
                holder.binding.bottomLine.invisible()
            }

            if (isSelected) {
                holder.binding.title.visible()
                holder.binding.titleUnSelected.gone()
            }else{
                holder.binding.title.gone()
                holder.binding.titleUnSelected.visible()
                holder.binding.bottomLine.invisible()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
}
class UnBoxingDateTumblerViewHolder(var binding: UnboxingDateTumblerItemViewBinding) :
    RecyclerView.ViewHolder(binding.root),
    BaseTumblerViewHolderInterface {
    private var value: String? = null
    override fun getValue(): String {
        return value!!
    }

    override fun setValue(value: String) {
        this.value = value
    }

    override fun getDisplayedText(): String {
        return binding.title.text.toString()
    }
}