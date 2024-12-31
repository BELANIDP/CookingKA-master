package android.presenter.customviews.widgets.preview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.databinding.ItemPreviewTileNormalBinding
import com.whirlpool.hmi.uicomponents.widgets.list.ListViewInterface

class PreviewRecyclerViewInterface(
    private val previewTileListItems: ArrayList<PreviewTileItem>,
    private val clickListenerOnPreviewTile: PreviewListItemClickListener,
): ListViewInterface {

    /**
     * This is used externally by the implementing fragment. Internally, for the component, there will be no operation definition.
     *
     * @param index int
     */
    override fun selectionUpdated(index: Int) {
        // No op
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            PreviewTileItem.TileType.NORMAL_TILE.value -> {
                return PreviewNormalTileViewHolder(
                    ItemPreviewTileNormalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    clickListenerOnPreviewTile
                )
            }
            else ->{
                // in case of other preview tile required, put another tile tile view holder
                return PreviewNormalTileViewHolder(
                    ItemPreviewTileNormalBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    clickListenerOnPreviewTile
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, any: Any?) {
        val listItemViewHolder = holder as PreviewTileBaseViewHolder
        val position = listItemViewHolder.bindingAdapterPosition
        if (previewTileListItems.isNotEmpty()) {
            listItemViewHolder.bind(previewTileListItems[position])
        }
        listItemViewHolder.onClickTileListener(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (previewTileListItems.isNotEmpty()) {
            previewTileListItems[position].tileType.value
        } else -1
    }
    /****************************** View Holder *****************************/
    /**
     * interface to provide callbacks of grid tile/item click events
     */
    interface PreviewListItemClickListener {
        fun onPreviewTileClick(view: View, position: Int)
    }
}