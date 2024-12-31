package android.presenter.customviews.widgets.preview

import android.view.View
import com.whirlpool.cooking.ka.databinding.ItemPreviewTileNormalBinding

class PreviewNormalTileViewHolder(
    private val  previewTileNormalBinding: ItemPreviewTileNormalBinding,
    private val  previewListItemClickListener: PreviewRecyclerViewInterface.PreviewListItemClickListener,
) : PreviewTileBaseViewHolder(previewTileNormalBinding.root) {
    override fun bind(previewTileItem: PreviewTileItem) {
        previewTileNormalBinding.previewTilePrimaryTextView.text = previewTileItem.previewTileMainText
        previewTileNormalBinding.previewTileSecondaryTextView.text = previewTileItem.previewTileSubText
        if(previewTileItem.isDividerHidden) previewTileNormalBinding.tileDivider.visibility = View.GONE
    }

    override fun onClickTileListener(position: Int) {
        previewTileNormalBinding.root.setOnClickListener { v: View ->
            previewListItemClickListener.onPreviewTileClick(
                v,
                position
            )
        }
    }
}