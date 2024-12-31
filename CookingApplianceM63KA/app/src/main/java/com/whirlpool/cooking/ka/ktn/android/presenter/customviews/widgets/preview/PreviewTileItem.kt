@file:Suppress("unused")

package android.presenter.customviews.widgets.preview

import com.whirlpool.hmi.cooking.utils.RecipeOptions
import core.utils.AppConstants

class PreviewTileItem(val previewTileMainText: String, val previewTileSubText: String = AppConstants.EMPTY_STRING, val recipeOptions: RecipeOptions?, val tileType: TileType) {

    enum class TileType (val value: Int) {
        NORMAL_TILE(0),
        IMAGE_TILE(1)
    }
    private val isPreviewTileClickable = false
    var isDividerHidden = false
}