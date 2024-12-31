package android.presenter.adapters.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import core.utils.invisible
import core.utils.visible

/**
 * File       : [android.presenter.adapters.favorites.FavoritesFromAdapter]
 * Brief      :   FavoritesFromAdapter is a RecyclerView adapter for displaying a list of fav
 * Author     : PANDES18
 * Created On : 14/10/2024
 */
class FavoritesFromAdapter(
    private var fromList: List<String>,
    private val onItemClickListener: ((String) -> Unit)?
) :
    RecyclerView.Adapter<FavoritesFromAdapter.FavoritesFromViewHolder>() {

    class FavoritesFromViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textTitle)
        val divider: View = itemView.findViewById(R.id.divider)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesFromViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_favorites_from, parent, false)
        return FavoritesFromViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fromList.size
    }

    override fun onBindViewHolder(holder: FavoritesFromViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(fromList[position])
        }
        holder.textView.text = fromList[position]
        if (position == fromList.size - 1) {
            holder.divider.invisible()
        } else {
            holder.divider.visible()
        }
    }
}