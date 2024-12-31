package android.presenter.customviews.widgets.gridview.viewholders

import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
import androidx.core.view.isVisible
import com.whirlpool.cooking.ka.databinding.ItemDrawerMenuBinding

class DrawerViewHolder(
    private val itemDrawerMenuBinding: ItemDrawerMenuBinding,
    itemClickListener: GridRecyclerViewInterface.GridItemClickListener
) : BaseViewHolder(itemDrawerMenuBinding.getRoot()) {
    private val gridItemClickListener: GridRecyclerViewInterface.GridItemClickListener =
        itemClickListener

    override fun bind(gridListItemModel: GridListItemModel) {
        itemDrawerMenuBinding.menuText.text = gridListItemModel.titleText
        itemDrawerMenuBinding.menuIcon.setImageResource(gridListItemModel.tileImageSrc)
        itemDrawerMenuBinding.viewDrawerSelectedLine.isVisible = gridListItemModel.isSelected == true
    }

    override fun handleClickListeners(position: Int) {
        itemDrawerMenuBinding.getRoot().setOnClickListener { v ->
            gridItemClickListener.onListItemClick(
                v, position
            )
        }
    }
}
