package android.presenter.adapters.favorites

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R

/**
 * File       : [android.presenter.adapters.favorites.SmallImageAdapter]
 * Brief      :  Small image adapter for favorites
 * Author     : PANDES18
 * Created On : 14/10/2024
 */
class SmallImageAdapter(
    private val context: Context,
    private val images: List<Drawable?>,
    private var onImageClick: (Int) -> Unit
) :
    RecyclerView.Adapter<SmallImageAdapter.ImageViewHolder>() {

    private var scrolledItemPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_small_images, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
        holder.itemView.setOnClickListener {
            onImageClick.invoke(position)
            scrolledItemPosition = -1
        }
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(imageResId: Drawable?) {
            imageView.background = imageResId
            if (scrolledItemPosition == position) {
                imageView.setImageDrawable( ResourcesCompat.getDrawable(itemView.resources,R.drawable.background_selected_image,null))
            }else{
                imageView.setImageDrawable( ResourcesCompat.getDrawable(itemView.resources,R.drawable.background_unselected_image,null))
            }
        }
    }

    fun setScrolledItemPosition(newSelectedPosition: Int){
        val previousSelectedPos = scrolledItemPosition
        scrolledItemPosition = newSelectedPosition

        notifyItemChanged(previousSelectedPos)
        notifyItemChanged(scrolledItemPosition)
    }

    fun clickSelectedItem(position: Int){
        onImageClick.invoke(position)
        scrolledItemPosition = -1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun knobTimedOut(){
        scrolledItemPosition = -1
        notifyDataSetChanged()
    }
}
