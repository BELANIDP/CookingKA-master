/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.customviews.listView

import android.presenter.customviews.radiobutton.RadioButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.ItemSettingsListTileBinding
import com.whirlpool.hmi.uicomponents.widgets.list.ListViewInterface
import core.utils.AppConstants
import core.utils.SettingsManagerUtils

/**
 * File        : android.presenter.customviews.listView.SettingsListViewHolderInterface
 * Brief       : Recycler view interface used by List Fragment or Collection Fragment
 * Author      : Nikki
 * Created On  : 17-July-2024
 * Details     : Used by List Fragment or Collection Fragment to display the list tiles
 */
open class SettingsListViewHolderInterface(
    private val dataModelList: ArrayList<ListTileData>,
    private val listItemClickListener: ListItemClickListener
) : ListViewInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemSettingsListTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, any: Any) {
        val listItemViewHolder = holder as ListItemViewHolder
        val position = listItemViewHolder.bindingAdapterPosition
        listItemViewHolder.bind(dataModelList[position],position)
        listItemViewHolder.handleListTileClickListeners(position)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun selectionUpdated(index: Int) {
        //Empty override
    }

    override fun getSelectedObject(): String? {
        return null
    }

    /**
     * Click Listener for List tiles
     */
    interface ListItemClickListener {
        /**
         * Listener method which is called on List tile click
         *
         * @param view     the tile view which is clicked
         * @param position index/position for the tile clicked
         */
        fun onListViewItemClick(view: View?, position: Int)

        /**
         * Listener method which is called on List tile toggle switch click
         *
         * @param position  index/position for the tile whose toggle switch is clicked
         * @param isChecked true - clicked toggle switch is checked
         * false - clicked toggle switch is not checked
         */
        fun  onToggleSwitchClick(position: Int, isChecked: Boolean){}
    }

    protected inner class ListItemViewHolder internal constructor(private var itemListTileLayoutBinding: ItemSettingsListTileBinding) :
        RecyclerView.ViewHolder(itemListTileLayoutBinding.root) {
        /**
         * Method to bind the data to the recycler view items / view holders
         *
         * @param item ListTileData Instance
         */
        fun bind(item: ListTileData,position: Int) {
            itemView.visibility = item.itemViewVisibility
            if (item.headingVisibility == View.VISIBLE) {
                itemListTileLayoutBinding.llListHeading.text = item.headingText
            }
            if (item.titleTextVisibility == View.VISIBLE) {
                itemListTileLayoutBinding.listItemTitleTextView.text = item.titleText
            }
            if (item.subTextVisibility == View.VISIBLE) {
                itemListTileLayoutBinding.listItemSubTextView.text = item.subText
            }
            if (item.itemIconVisibility == View.VISIBLE) {
                itemListTileLayoutBinding.listItemIconImageView.setImageResource(item.itemIconID)
            }
            if (item.rightTextVisibility == View.VISIBLE) {
                itemListTileLayoutBinding.listItemRightTextView.text = item.rightText
            }
            if (item.rightClockTextVisibility == View.VISIBLE) {
                itemListTileLayoutBinding.listItemRightClockTextView.set12HourTimeFormat(
                    SettingsManagerUtils.TimeFormatSettings.H_12 == SettingsManagerUtils.getTimeFormat()
                )
            }
            if (item.rightIconVisibility == View.VISIBLE) {
                itemListTileLayoutBinding.listItemImageViewRightIcon.setImageResource(item.rightIconID)
            }
            itemListTileLayoutBinding.llListHeading.visibility = item.headingVisibility
            itemListTileLayoutBinding.viewListSeperator.visibility = item.seperatorVisibility
            itemListTileLayoutBinding.listItemDividerView.visibility =
                item.listItemDividerViewVisibility
            itemListTileLayoutBinding.listItemTitleTextView.visibility = item.titleTextVisibility
            itemListTileLayoutBinding.listItemSubTextView.visibility = item.subTextVisibility
            itemListTileLayoutBinding.listItemIconImageView.visibility = item.itemIconVisibility
            itemListTileLayoutBinding.listItemRightTextView.visibility = item.rightTextVisibility
            itemListTileLayoutBinding.listItemRightClockTextView.visibility = item.rightClockTextVisibility
            itemListTileLayoutBinding.listItemImageViewRightIcon.visibility =
                item.rightIconVisibility
            itemListTileLayoutBinding.listItemRadioButton.visibility =
                item.radioButtonData.visibility
            itemListTileLayoutBinding.listItemRadioButton.setChecked(item.radioButtonData.isChecked)
            itemListTileLayoutBinding.listItemRadioButton.setEnabled(item.radioButtonData.isEnabled)
            itemView.isEnabled = item.isItemEnabled
            itemView.isClickable = item.isItemEnabled
            if (!itemView.isEnabled) {
                itemListTileLayoutBinding.listItemTitleTextView.setTextColor(
                    itemListTileLayoutBinding.root.context.resources.getColorStateList(
                        R.color.light_grey,
                        null
                    )
                )
                itemListTileLayoutBinding.listItemSubTextView.setTextColor(
                    itemListTileLayoutBinding.root.context.resources.getColorStateList(
                        R.color.light_grey,
                        null
                    )
                )
                itemListTileLayoutBinding.listItemRightTextView.setTextColor(
                    itemListTileLayoutBinding.root.context.resources.getColorStateList(
                        R.color.light_grey,
                        null
                    )
                )
                itemListTileLayoutBinding.listItemImageViewRightIcon.setImageResource(R.drawable.icon_list_item_right_arrow_disable)
                itemListTileLayoutBinding.listItemRadioButton.isEnabled =
                    item.radioButtonData.isEnabled
            }
            if (item.isPaddingView) {
                itemListTileLayoutBinding.listItemMainView.setPadding(
                    AppConstants.LIST_ITEM_VIEW_PADDING_EXTRA,
                    0,
                    AppConstants.LIST_ITEM_VIEW_PADDING_EXTRA,
                    0
                )
            } else {
                itemListTileLayoutBinding.listItemMainView.setPadding(
                    AppConstants.LIST_ITEM_VIEW_PADDING,
                    0,
                    AppConstants.LIST_ITEM_VIEW_PADDING,
                    0
                )
            }

            val toggleSwitch =
                itemListTileLayoutBinding.settingsItemToggleSwitch.findViewById<SwitchCompat>(R.id.toggle_switch)
            itemListTileLayoutBinding.settingsItemToggleSwitch.visibility = item.toggleSwitchData.visibility
            toggleSwitch.isChecked = item.toggleSwitchData.isChecked
            itemListTileLayoutBinding.settingsItemToggleSwitch.isEnabled = item.toggleSwitchData.isEnabled

            toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
                listItemClickListener.onToggleSwitchClick(position, isChecked)
            }
            if(itemListTileLayoutBinding.listItemRadioButton.visibility == View.GONE
                && itemListTileLayoutBinding.listItemIconImageView.visibility == View.GONE) {
                addMarginStartToTitleView(itemListTileLayoutBinding)
            }

            itemListTileLayoutBinding.listItemRadioButton.setRadioItemClickListener(object :RadioButton.ListItemClickListener{
                override fun onToggleSwitchClick(isChecked: Boolean) {
                    listItemClickListener.onToggleSwitchClick(position, isChecked)
                }
            })
        }

        fun handleListTileClickListeners(position: Int) {
            itemListTileLayoutBinding.listItemMainView
                .setOnClickListener { view ->
                    listItemClickListener.onListViewItemClick(
                        view,
                        position
                    )
                }
        }
        /**
         * Runtime add margin to view for adjust aligment
         * @param binding - binding for getting reference ids
         */
        private fun addMarginStartToTitleView(binding: ItemSettingsListTileBinding) {
            val param = (binding.listItemTitleTextView.layoutParams as ViewGroup.MarginLayoutParams).apply {
               marginStart = 0
            }
            binding.listItemTitleTextView.layoutParams = param
            binding.listItemTitleTextView.invalidate()
        }
    }
}