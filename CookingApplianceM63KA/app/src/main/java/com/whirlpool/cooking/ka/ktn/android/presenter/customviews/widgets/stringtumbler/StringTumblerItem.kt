/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.customviews.widgets.stringtumbler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.TumblerCustomItemViewBinding
import core.utils.TimeUtils
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerItemViewInterface
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerViewHolderInterface
import core.utils.AppConstants.RESOURCE_TYPE_STRING
import core.utils.AppConstants.TEXT_TEMP

/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.widgets.tumbler.StringTumblerItem
 * Brief       : This class will provide item view.
 * Author      : SINGHJ25
 * Created On  : 05/02/2024
 */

class StringTumblerItem(cookTimeOptionList: HashMap<String, Long>?) :
    BaseTumblerItemViewInterface {

    private val cookTimeOptionList: HashMap<String, Long>?

    init {
        this.cookTimeOptionList = cookTimeOptionList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val tumblerCustomItemViewBinding = TumblerCustomItemViewBinding.inflate(LayoutInflater.from(parent.context))
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
            val id = holder.tumblerCustomItemViewBinding.title.context.resources.getIdentifier(TEXT_TEMP + itemIdentifier, RESOURCE_TYPE_STRING,
                holder.tumblerCustomItemViewBinding.title.context.packageName)
            holder.tumblerCustomItemViewBinding.title.resourceIdentifier =
                holder.tumblerCustomItemViewBinding.title.context.resources.getString(id)
            if (cookTimeOptionList != null && cookTimeOptionList.size > 0) {
                if (cookTimeOptionList[itemIdentifier] != null) {
                    val cleanCycleCookTime = cookTimeOptionList[itemIdentifier]
                    val currentTime =
                        TimeUtils.convertTimeToHoursAndMinutes(cleanCycleCookTime ?: 0)
                    val context = holder.tumblerCustomItemViewBinding.subTitle.context
                    holder.tumblerCustomItemViewBinding.subTitle.text = context.getString(
                        R.string.text_hr_min_self_clean,
                        currentTime[1],
                        context.getString(R.string.text_label_hr),
                        currentTime.substring(2, 4),
                        context.getString(R.string.text_label_min)
                    )
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
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    private class StringTumblerViewHolder(var tumblerCustomItemViewBinding: TumblerCustomItemViewBinding) :
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

