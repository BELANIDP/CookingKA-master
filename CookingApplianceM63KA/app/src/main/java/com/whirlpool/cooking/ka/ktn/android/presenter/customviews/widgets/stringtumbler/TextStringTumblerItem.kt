/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.customviews.widgets.stringtumbler

import android.presenter.basefragments.AbstractStringTumblerFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.TumblerCustomItemViewBinding
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerItemViewInterface
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerViewHolderInterface
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getRecipeNameText
import core.utils.customTypeFace

/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.stringtumbler
 * Brief       : This class will provide item view for Strings Value
 * Author      : PATELJ7
 * Created On  : 21/03/2024
 */

class TextStringTumblerItem(
    cookTimeOptionList: String,
    private val textStringItemClickInterface: TextStringItemClickInterface,
    font: Int = R.font.roboto_regular,
    isVisionDetectedValue: Boolean = false,
    isVisionView: Boolean = false
) :
    BaseTumblerItemViewInterface {

    private val recipeOptionList: String
    private val font: Int
    private val isVisionDetected:Boolean
    private val isVisionView:Boolean

    init {
        this.recipeOptionList = cookTimeOptionList
        this.font = font
        this.isVisionDetected = isVisionDetectedValue
        this.isVisionView = isVisionView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val tumblerCustomItemViewBinding =
            TumblerCustomItemViewBinding.inflate(LayoutInflater.from(parent.context))
        return StringTumblerViewHolder(tumblerCustomItemViewBinding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        itemIdentifier: String,
        index: Int,
        isSelected: Boolean
    ) {
        if (holder is StringTumblerViewHolder) {
            val context = holder.itemView.context
            holder.tumblerCustomItemViewBinding.title.customTypeFace =
                CookingAppUtils.getTypeFace(context, font)
            if (CookingAppUtils.checkResIdAvailable(
                    holder.itemView,
                    itemIdentifier,
                    AppConstants.RESOURCE_TYPE_STRING
                ).toString() == itemIdentifier
            ) {
                holder.tumblerCustomItemViewBinding.title.text = itemIdentifier
            } else{
                val itemTitleText = getRecipeNameText(context, itemIdentifier)
                holder.tumblerCustomItemViewBinding.title.text = itemTitleText
            }
            holder.tumblerCustomItemViewBinding.subTitle.visibility = View.GONE
            if(isVisionDetected){
                holder.tumblerCustomItemViewBinding.title.textSize = 40f
            }
            if (recipeOptionList.isNotEmpty()) {
                if (isSelected) {
                    holder.tumblerCustomItemViewBinding.title.setTextColor(
                        holder.tumblerCustomItemViewBinding.title.context.getColor(
                            R.color.common_solid_white
                        )
                    )
                } else {
                    holder.tumblerCustomItemViewBinding.title.setTextColor(
                        holder.tumblerCustomItemViewBinding.title.context.getColor(
                            R.color.dark_grey
                        )
                    )
                }
            }
            if (isVisionView) {
                holder.tumblerCustomItemViewBinding.title.setOnClickListener { _ ->
                    textStringItemClickInterface.onItemClickVision(
                        index,
                        recyclerViewType = if (isVisionDetected) AbstractStringTumblerFragment.RecyclerViewType.MANUAL else
                            AbstractStringTumblerFragment.RecyclerViewType.VISUAL
                    )
                }
            }
            else {
                holder.tumblerCustomItemViewBinding.title.setOnClickListener { _ ->
                    textStringItemClickInterface.onItemClick(index)
                }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return 0
    }

    private class StringTumblerViewHolder(var tumblerCustomItemViewBinding: TumblerCustomItemViewBinding) :
        RecyclerView.ViewHolder(tumblerCustomItemViewBinding.root), BaseTumblerViewHolderInterface {

        private var value: String? = null
        override fun getValue(): String? {
            return value
        }

        override fun setValue(value: String) {
            this.value = value
        }

        override fun getDisplayedText(): String {
            return tumblerCustomItemViewBinding.title.text.toString()
        }
    }
}

