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
import com.whirlpool.cooking.ka.databinding.ItemListTileBinding
import com.whirlpool.hmi.uicomponents.widgets.list.ListViewInterface
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.changeTextColor

/**
 * File        : android.presenter.customviews.listView.ListViewHolderInterface
 * Brief       : Recycler view interface used by List Fragment or Collection Fragment
 * Author      : PATELJ7
 * Created On  : 06-Feb-2024
 * Details     : Used by List Fragment or Collection Fragment to display the list tiles
 */
open class ListViewHolderInterface(
    private val dataModelList: ArrayList<ListTileData>,
    private val listItemClickListener: ListItemClickListener
) : ListViewInterface {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val listTileBinding =
            ItemListTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListItemViewHolder(listTileBinding)
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

    protected inner class ListItemViewHolder internal constructor(private var itemListTileLayoutBinding: ItemListTileBinding) :
        RecyclerView.ViewHolder(itemListTileLayoutBinding.root) {
        /**
         * Method to bind the data to the recycler view items / view holders
         *
         * @param item ListTileData Instance
         */
        fun bind(item: ListTileData, position: Int) {
            itemView.visibility = item.itemViewVisibility
            if (item.headingVisibility == View.VISIBLE) {
                itemListTileLayoutBinding.llListHeading.text = item.headingText
                itemListTileLayoutBinding.listItemMainView.visibility = View.GONE
            }
            if (item.titleTextVisibility == View.VISIBLE) {
                itemListTileLayoutBinding.listItemTitleTextView.text = item.titleText
                itemListTileLayoutBinding.listItemMainView.visibility = View.VISIBLE
                itemListTileLayoutBinding.viewListSeperator.visibility = View.GONE
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
            itemListTileLayoutBinding.listItemImageViewRightIcon.visibility =
                item.rightIconVisibility
            itemListTileLayoutBinding.listItemRadioButton.visibility =
                item.radioButtonData.visibility
            itemListTileLayoutBinding.listItemRadioButton.setChecked(item.radioButtonData.isChecked)
            itemListTileLayoutBinding.listItemRadioButton.setEnabled(item.radioButtonData.isEnabled)
            itemView.isEnabled = item.isItemEnabled
            itemView.isClickable = item.isItemEnabled
            //if radio button and item icon are GONE then there should be no space ahead of Main and Sub textview appearance
            if(itemListTileLayoutBinding.listItemRadioButton.visibility == View.GONE && item.itemIconVisibility == View.GONE){
                itemListTileLayoutBinding.textSeparator.visibility = View.GONE
            }
            if(itemListTileLayoutBinding.listItemImageViewRightIcon.visibility == View.GONE){
                itemListTileLayoutBinding.textSeparatorRight.visibility = View.GONE
            }
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

            if(CookingAppUtils.isDemoModeEnabled() && item.titleText == itemListTileLayoutBinding.root.context.getString(R.string.temperature_calibration) ) {
                itemListTileLayoutBinding.listItemSubTextView.changeTextColor(
                    itemListTileLayoutBinding.root.context,
                        R.color.light_grey
                    )

                itemListTileLayoutBinding.listItemTitleTextView.changeTextColor(
                    itemListTileLayoutBinding.root.context,
                    R.color.light_grey
                )


                itemListTileLayoutBinding.listItemRightTextView.changeTextColor(
                    itemListTileLayoutBinding.root.context,
                    R.color.light_grey
                )
            }
            else if(itemView.isEnabled){
                itemListTileLayoutBinding.listItemSubTextView.changeTextColor(
                    itemListTileLayoutBinding.root.context,
                    R.color.color_white
                )

                itemListTileLayoutBinding.listItemTitleTextView.changeTextColor(
                    itemListTileLayoutBinding.root.context,
                    R.color.color_white
                )


                itemListTileLayoutBinding.listItemRightTextView.changeTextColor(
                    itemListTileLayoutBinding.root.context,
                    R.color.color_white
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
    }
}