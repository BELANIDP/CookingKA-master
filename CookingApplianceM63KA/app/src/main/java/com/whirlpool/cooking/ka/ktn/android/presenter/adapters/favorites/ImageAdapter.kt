package android.presenter.adapters.favorites
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R

/**
 * File       : [android.presenter.adapters.favorites.ImageAdapter]
 * Brief      :   Image adapter for displaying images in a RecyclerView.
 * Author     : PANDES18
 * Created On : 14/10/2024
 */
class ImageAdapter(private val context: Context, private val images: List<Drawable?>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_images, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(imageResId: Drawable?) {
            imageView.setImageDrawable(imageResId)
        }
    }
}
