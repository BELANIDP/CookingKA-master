package android.presenter.adapters.favorites

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.utils.Constants.PRIMARY_CAVITY_KEY
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.cookbook.records.FavoriteRecord
import core.utils.CookingAppUtils
import core.utils.FavoritesPopUpUtils
import core.utils.HMILogHelper
import core.utils.gone
import core.utils.visible
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * File       : [android.presenter.adapters.favorites.FavoritesFromAdapter]
 * Brief      :   FavoritesFromAdapter for favorites screen
 * Author     : PANDES18
 * Created On : 14/10/2024
 */
class FavoriteLandingAdapter(
    private var favoriteRecords: MutableList<FavoriteRecord>,
    private var lifecycleOwner: LifecycleOwner,
    private var onItemClick: ((FavoriteRecord) -> Unit)?,
    private var onItemLongClick: ((FavoriteRecord) -> Unit)?
) :
    RecyclerView.Adapter<FavoriteLandingAdapter.FavoriteLandingViewHolder>() {

    private var selectedItemPosition: Int? = -1
    private var longPressJob: Job? = null
    private val longPressDuration = 3000L


    class FavoriteLandingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val imageFavoritedRecipe: ImageView = itemView.findViewById(R.id.imageFavoritedRecipe)
        val imageProbeIcon: ImageView = itemView.findViewById(R.id.imageProbeIcon)
        val imageCavityIcon: ImageView = itemView.findViewById(R.id.imageCavityIcon)
        val holderLongClick: View = itemView.findViewById(R.id.holderLongClick)
        val imageDeleteFav: View = itemView.findViewById(R.id.imageDeleteFav)
        val imageCancel: View = itemView.findViewById(R.id.imageCancel)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteLandingViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_favorites, parent, false)
        return FavoriteLandingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favoriteRecords.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetSelectedIItem() {
        selectedItemPosition = null
        notifyDataSetChanged()
    }

    fun updateList(newList: MutableList<FavoriteRecord>) {
        favoriteRecords = newList
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged", "ClickableViewAccessibility")
    override fun onBindViewHolder(holder: FavoriteLandingViewHolder, position: Int) {
        val record = favoriteRecords[position]
        val animInDuration = 500L
        val animOutDuration = 300L
        val alpha0 = 0.0f
        val alpha50 = 0.5f
        val alpha100 = 1f
        val scale50 = 0.5f
        holder.apply {
            // Set title text with utility method
            textTitle.text = record.favoriteName

            imageFavoritedRecipe.apply {
                setImageDrawable(
                    FavoritesPopUpUtils.getDrawableImagesByName(
                        itemView.context,
                        listOf(record.imageUrl)
                    )[0]
                )
                val targetAlpha = if (position == selectedItemPosition) alpha50 else alpha100
                val duration =
                    if (alpha == alpha100 && targetAlpha == alpha50) animInDuration else animOutDuration

                this.animate()
                    .alpha(targetAlpha)
                    .setDuration(duration)
                    .start()
            }

            holderLongClick.apply {
                val targetAlpha = if (position == selectedItemPosition) alpha100 else alpha0
                val duration =
                    if (alpha == alpha100 && targetAlpha == alpha0) animOutDuration else animInDuration
                this.animate()
                    .alpha(targetAlpha)
                    .setDuration(duration)
                    .start()
            }

            // Set cavity icon visibility based on product variant and selection state
            imageCavityIcon.apply {
                when (CookingViewModelFactory.getProductVariantEnum()) {
                    CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
                    CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> gone()

                    CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                    CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                        setImageResource(
                            when (record.cavity.toString()) {
                                PRIMARY_CAVITY_KEY -> R.drawable.cavity_upper
                                else -> R.drawable.cavity_lower
                            }
                        )
                        val targetAlpha = if (position == selectedItemPosition) alpha0 else alpha100
                        val duration = if (alpha == alpha100 && targetAlpha == alpha0) animInDuration/2 else animOutDuration/2

                        this.animate()
                            .alpha(targetAlpha)
                            .setDuration(duration)
                            .start()
                    }

                    else -> {}
                }
            }

            imageProbeIcon.apply {
                background =
                    if (position == selectedItemPosition) {
                        null
                    } else ResourcesCompat.getDrawable(resources, R.drawable.ic_probe_oven, null)

                visibility =
                    if (CookingAppUtils.isProbeRequiredForRecipe(
                            record.recipeName,
                            record.cavity ?: PRIMARY_CAVITY_KEY
                        )
                    ) View.VISIBLE else View.GONE
            }

            // ItemView interactions
            itemView.apply {

                // Handle item click
                setOnClickListener {
                    onItemClick?.invoke(record)
                }

                // Handle long press with a touch listener
                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // Launch a coroutine for delayed long press detection
                            longPressJob = lifecycleOwner.lifecycleScope.launch {
                                delay(longPressDuration)
                                selectedItemPosition =
                                    if (selectedItemPosition == position) null else position
                                notifyDataSetChanged()
                            }
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            // Cancel long press job if touch is released or canceled
                            longPressJob?.cancel()
                        }
                    }
                    false
                }
                background = if (position == selectedItemPosition) {
                    ResourcesCompat.getDrawable(resources, R.drawable.bg_selected_favorite, null)
                } else {
                    ResourcesCompat.getDrawable(resources, R.color.very_dark_grey, null)
                }

                when (CookingViewModelFactory.getProductVariantEnum()) {
                    CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
                    CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                        if (CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.isRunning) this.alpha = 0.8F else this.alpha = 1F

                    }
                    CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                    CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                        val isRunning = when (record.cavity.toString()) {
                            PRIMARY_CAVITY_KEY -> CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.isRunning
                            else -> CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.isRunning
                        }
                        if (isRunning) this.alpha = 0.8F else this.alpha = 1F
                    }
                    else -> {
                        HMILogHelper.Loge("Unexpected Product configuration")
                    }
                }
            }

            imageDeleteFav.apply {
                setOnClickListener {
                    onItemLongClick?.invoke(record)
                }
                if (position == selectedItemPosition) {
                    scaleX = scale50
                    scaleY = scale50
                    visible()
                    animate()
                        .scaleX(alpha100)
                        .scaleY(alpha100)
                        .setDuration(animInDuration/2)
                        .start()
                } else {
                    if (selectedItemPosition == -1) {
                        gone()
                    } else {
                        animate()
                            .scaleX(alpha50)
                            .scaleY(alpha50)
                            .setDuration(animOutDuration).withEndAction { gone() }
                            .start()
                    }
                }
            }

            imageCancel.apply {
                setOnClickListener {
                    selectedItemPosition = null
                    notifyDataSetChanged()
                }
                if (position == selectedItemPosition) {
                    scaleX = scale50
                    scaleY = scale50
                    visible()
                    animate()
                        .scaleX(alpha100)
                        .scaleY(alpha100)
                        .setDuration(animInDuration/2)
                        .start()
                } else {
                    if (selectedItemPosition == -1) {
                        gone()
                    } else {
                        animate()
                            .scaleX(alpha50)
                            .scaleY(alpha50)
                            .setDuration(animOutDuration).withEndAction { gone() }
                            .start()
                    }
                }
            }
        }
    }
}