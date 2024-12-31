/***Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL***/
package android.presenter.adapters.manualMode

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.TumblerManualModeCustomItemViewBinding
import com.whirlpool.hmi.cooking.model.capability.recipe.options.TemperatureMap
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerItemViewInterface
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerViewHolderInterface
import core.utils.AppConstants.RESOURCE_TYPE_STRING
import core.utils.AppConstants.TEXT_TEMP
import core.utils.TemperatureUtils

/*
 * File : android.presenter.adapters.manualMode.ManualModeStringTumblerAdapterItem
 * Author : SINGHA80.
 * Created On : 3/26/24
 * Details : Provides tumbler adapter item data
 */
class ManualModeStringTumblerAdapterItem(
    cookTimeOptionList: Any?,
    isTimePreheatBasedRecipe: Boolean
) :
    BaseTumblerItemViewInterface {

    private val temperatureMap = cookTimeOptionList as TemperatureMap
    private val isTimePreheat = isTimePreheatBasedRecipe

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val tumblerCustomItemViewBinding =
            TumblerManualModeCustomItemViewBinding.inflate(LayoutInflater.from(parent.context))
        return StringTumblerViewHolder(tumblerCustomItemViewBinding)
    }

    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        itemIdentifier: String,
        index: Int,
        isSelected: Boolean
    ) {
        if (holder is StringTumblerViewHolder) {
            val id =
                holder.tumblerCustomItemViewBinding.title.context.resources.getIdentifier(
                    TEXT_TEMP + itemIdentifier,
                    RESOURCE_TYPE_STRING,
                    holder.tumblerCustomItemViewBinding.title.context.packageName
                )
            holder.tumblerCustomItemViewBinding.title.resourceIdentifier =
                holder.tumblerCustomItemViewBinding.title.context.resources.getString(id)
            if(isTimePreheat){
                if(itemIdentifier.contentEquals(temperatureMap.defaultString)){
                    holder.tumblerCustomItemViewBinding.subTitle.setValue(holder.tumblerCustomItemViewBinding.title.context.getString(R.string.text_tile_decision_large_recommended))
                }
            }else {
                val temperatureFormat =
                    TemperatureUtils.getTemperatureFormat(holder.tumblerCustomItemViewBinding.subTitle.context)
                holder.tumblerCustomItemViewBinding.subTitle.setSuffix(temperatureFormat)
                holder.tumblerCustomItemViewBinding.subTitle.setValue(temperatureMap.temperatureMap[itemIdentifier])
            }
            if (isSelected) {
                holder.tumblerCustomItemViewBinding.title.setTextColor(
                    holder.tumblerCustomItemViewBinding.title.context.getColor(
                        R.color.common_solid_white
                    )
                )
                holder.tumblerCustomItemViewBinding.subTitle.setTextColor(
                    holder.tumblerCustomItemViewBinding.subTitle.context.getColor(
                        R.color.common_solid_white
                    )
                )
                holder.tumblerCustomItemViewBinding.subTitle.visibility = View.VISIBLE
            } else {
                holder.tumblerCustomItemViewBinding.title.setTextColor(
                    holder.tumblerCustomItemViewBinding.title.context.getColor(
                        R.color.dark_grey
                    )
                )
                holder.tumblerCustomItemViewBinding.subTitle.setTextColor(
                    holder.tumblerCustomItemViewBinding.subTitle.context.getColor(
                        R.color.dark_grey
                    )
                )
                holder.tumblerCustomItemViewBinding.subTitle.visibility = View.INVISIBLE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    private class StringTumblerViewHolder(var tumblerCustomItemViewBinding: TumblerManualModeCustomItemViewBinding) :
        RecyclerView.ViewHolder(tumblerCustomItemViewBinding.root),
        BaseTumblerViewHolderInterface {
        private var value: String? = null
        override fun getValue(): String {
            return value!!
        }

        override fun setValue(value: String) {
            this.value = value
        }

        override fun getDisplayedText(): String {
            return tumblerCustomItemViewBinding.title.text.toString()
        }
    }
}