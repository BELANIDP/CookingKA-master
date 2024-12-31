package android.presenter.adapters.favorites

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.utils.Constants.PRIMARY_CAVITY_KEY
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.utils.cookbook.records.HistoryRecord
import core.utils.AppConstants.DEGREE_SYMBOL
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AppConstants.SECONDARY_CAVITY_KEY
import core.utils.AppConstants.SPACE_CONSTANT
import core.utils.CookingAppUtils
import core.utils.TimeUtils
import core.utils.gone

/**
 * File       : [android.presenter.adapters.favorites.HistoryAdapter]
 * Brief      :   History adapter for favorites
 * Author     : PANDES18
 * Created On : 14/10/2024
 */
class HistoryAdapter(
    private var allHistoryRecords: MutableList<HistoryRecord>,
    private var context: Context,
    private var onItemClick: ((HistoryRecord) -> Unit)? = null

) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textTempAndDuration: TextView = itemView.findViewById(R.id.textTempAndDuration)
        val textDay: TextView = itemView.findViewById(R.id.textDay)
        val imageCavityIcon: ImageView = itemView.findViewById(R.id.imageCavityIcon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cycle_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return allHistoryRecords.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: MutableList<HistoryRecord>) {
        allHistoryRecords = newList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val record = allHistoryRecords[position]
        holder.apply {
            textTitle.text = CookingAppUtils.getRecipeNameText(context, record.recipeName)
            textTempAndDuration.text = getDescriptionText(record)

            val (day, time) = TimeUtils.getRelativeTime(
                record.timestamp,
                context,
                !SettingsViewModel.getSettingsViewModel().is12HourTimeFormat.value!!
            ).split(",")
            textDay.text = day
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN, CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                    imageCavityIcon.gone()
                }

                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN, CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    val cavityIconRes: Int? = when (record.cavity.toString()) {
                        PRIMARY_CAVITY_KEY -> R.drawable.cavity_upper
                        SECONDARY_CAVITY_KEY -> R.drawable.cavity_lower
                        else -> {null}
                    }
                    if (cavityIconRes != null) {
                        imageCavityIcon.setImageResource(cavityIconRes)
                    }
                }

                else -> {}
            }
            itemView.setOnClickListener {
                onItemClick?.invoke(record)
            }
        }
    }

    private fun getDescriptionText(record: HistoryRecord): String {
        return when {
            // Handle doneness and weight
            !record.doneness.isNullOrEmpty() -> {
                var weight: String = EMPTY_STRING
                if (!TextUtils.isEmpty(record.weight)) {
                    val weightUnit = SettingsViewModel.getSettingsViewModel().weightUnit.value
                    weight = CookingAppUtils.displayWeightToUser(
                        context,
                        record.weight!!.toFloat(),
                        weightUnit.toString()
                    )
                }
                return record.doneness.toString() + SPACE_CONSTANT + weight
            }

            // Handle weight and weight unit
            !record.weight.isNullOrEmpty() -> {
                val weightUnit = SettingsViewModel.getSettingsViewModel().weightUnit.value
                return CookingAppUtils.displayWeightToUser(
                    context,
                    record.weight!!.toFloat(),
                    weightUnit.toString()
                )
            }

            // Handle recipe with probe temp/ oven temp
            !record.targetMeatProbeTemperature.isNullOrEmpty() -> {
                context.getString(R.string.text_history_subtitle_probe,
                    record.targetMeatProbeTemperature+DEGREE_SYMBOL,
                    record.targetTemperature+ DEGREE_SYMBOL)
            }

            // Handle recipe with target temperature and cook time
            !record.targetTemperature.isNullOrEmpty() -> {
                var timeString: String = EMPTY_STRING
                record.cookTime?.takeIf { it.isNotEmpty() }?.let { cookTime ->
                    timeString = CookingAppUtils.displayCookTimeToUser(
                        context,
                        cookTime.toLong(),
                        true,
                        arrayOf(
                            R.string.text_label_hour, R.string.text_label_min, R.string.text_label_sec
                        )
                    )
                }
                if (TextUtils.isEmpty(timeString)) {
                    buildString {
                        append(record.targetTemperature)
                        append(DEGREE_SYMBOL)
                    }
                } else {
                    context.getString(R.string.text_ovenManual_temp_time,record.targetTemperature, timeString)
                }
            }

            // Handle recipe with MWO power level and cook time
            !record.mwoPowerLevel.isNullOrEmpty() -> {
                var timeString: String = EMPTY_STRING
                record.cookTime?.takeIf { it.isNotEmpty() }?.let { cookTime ->
                    timeString = CookingAppUtils.displayCookTimeToUser(
                        context,
                        cookTime.toLong(),
                        true,
                        arrayOf(
                            R.string.text_label_hour, R.string.text_label_min, R.string.text_label_sec
                        )
                    )
                }
                if (TextUtils.isEmpty(timeString)) {
                    buildString {
                        append(record.mwoPowerLevel!!.toFloat().toInt())
                        append(DEGREE_SYMBOL)
                    }
                } else {
                    context.getString(R.string.text_ovenManual_temp_time,record.mwoPowerLevel!!.toFloat().toInt().toString(), timeString)
                }
            }

            // Handle recipe with only cook time
            !record.cookTime.isNullOrEmpty() -> {
                CookingAppUtils.displayCookTimeToUser(
                    context,
                    record.cookTime!!.toLong(),
                    true,
                    arrayOf(
                        R.string.text_label_hour, R.string.text_label_min, R.string.text_label_sec
                    )
                )
            }
            // Default case: return empty string
            else -> EMPTY_STRING
        }
    }
}