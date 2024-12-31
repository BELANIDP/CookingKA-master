package android.presenter.customviews.widgets.preview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class PreviewTileBaseViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(previewTileItem: PreviewTileItem)
    open fun onClickTileListener(position: Int) {
        //Defined in the respective derived classes
    }
}