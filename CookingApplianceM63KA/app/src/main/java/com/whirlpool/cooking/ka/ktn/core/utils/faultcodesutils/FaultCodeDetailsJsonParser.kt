package core.utils.faultcodesutils

import android.annotation.SuppressLint
import android.content.Context
import com.whirlpool.hmi.cooking.utils.FaultCategory
import com.whirlpool.hmi.cooking.utils.FaultSubCategory
import core.utils.HMILogHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class FaultCodeDetailsJsonParser {
    private lateinit var jsonObject: JSONObject
    private val parseSaveOnlyFaultsCodes: MutableList<String> = mutableListOf()
    private var parseFaultCategoryACodes: MutableMap<String, ArrayList<Int>> = mutableMapOf()
    private var parseFaultCategoryBCodes: MutableMap<String, ArrayList<Int>> = mutableMapOf()
    private var parseFaultCategoryB2Codes: MutableMap<String, ArrayList<Int>> = mutableMapOf()
    private var parseFaultCategoryCCodes: MutableMap<String, ArrayList<Int>> = mutableMapOf()

    /**
     * This loads JSON from raw
     * @param context
     * @param fileName
     * @return
     */
    @SuppressLint("DiscouragedApi")
    fun loadJson(context: Context, fileName: String?): Boolean {
        val resIdentifier = context.resources
            .getIdentifier(
                fileName, "raw",
                context.packageName
            )
        if (0 != resIdentifier) {
            val inputStream = context.resources.openRawResource(resIdentifier)
            // Load Tools JSON File
            val buffer: ByteArray
            try {
                buffer = ByteArray(inputStream.available())
                val readLength = inputStream.read(buffer)
                inputStream.close()
                return if (readLength > 0) {
                    HMILogHelper.Logd("Successful in reading package")
                    jsonObject = JSONObject(String(buffer))
                    true
                } else {
                    HMILogHelper.Loge("Failed to read file")
                    false
                }
            } catch (e: IOException) {
                HMILogHelper.Loge("Failed to read file" + e.message)
                e.printStackTrace()
                return false
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            HMILogHelper.Loge("File not present")
        }
        return false
    }

    /**
     * This method parse the fault.json file and call the method to map the fault codes
     */
    fun parseFaultListIntoCategory() = try {
        if (jsonObject.length() > 0) {
            val keys: Iterator<*> = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next() as String
                if (jsonObject[key] is JSONObject) {
                    val faultDetails = jsonObject.getJSONObject(key)
                    val category = faultDetails.getInt(FaultCodesJsonKeys.FAULT_CATEGORY)
                    val subCategory = faultDetails.getInt(FaultCodesJsonKeys.FAULT_SUB_CATEGORY)
                    val faultPriorities = ArrayList<Int>()
                    faultPriorities.add(faultDetails.optInt(FaultCodesJsonKeys.FAULT_DOUBLE_OVEN_PRIORITY))
                    faultPriorities.add(faultDetails.optInt(FaultCodesJsonKeys.FAULT_COMBO_OVEN_PRIORITY))
                    HMILogHelper.Logd(
                        "Fault Code $key Fault category : " + FaultCategory.getById(
                            category
                        ) + " Fault subcategory : " + FaultSubCategory.getById(subCategory)
                    )
                    filterToCategory(category, subCategory, key, faultPriorities)
                }
            }
        } else {
            HMILogHelper.Loge("Fault JSON Object is empty")
        }
    } catch (e: JSONException) {
        e.message?.let { HMILogHelper.Loge(it) }
        e.printStackTrace()
    }

    /**
     * This method is used to map the fault codes into different fault sub category
     * Subcategory are CategoryA, CategoryB, CategoryB2, CategoryC, Store Only
     */
    private fun filterToCategory(
        category: Int,
        subCategory: Int,
        key: String,
        faultPriorities: ArrayList<Int>
    ) {
        when (category) {
            FaultCategory.STORE_ONLY.ordinal -> {
                if (subCategory == FaultSubCategory.SAVE_ONLY.ordinal)
                    parseSaveOnlyFaultsCodes.add(key)
            }
            FaultCategory.HARD_STOP.ordinal -> {
                if (subCategory == FaultSubCategory.CATEGORY_A.ordinal)
                    parseFaultCategoryACodes[key] = faultPriorities
                else if (subCategory == FaultSubCategory.CATEGORY_C.ordinal)
                    parseFaultCategoryCCodes[key] = faultPriorities
            }
            FaultCategory.SHOW_AND_CONTINUE.ordinal -> {
                if (subCategory == FaultSubCategory.CATEGORY_B.ordinal)
                    parseFaultCategoryBCodes[key] = faultPriorities
                else if (subCategory == FaultSubCategory.CATEGORY_B2.ordinal)
                    parseFaultCategoryB2Codes[key] = faultPriorities
            }
        }
    }

    /**
     * @return list of save only faults
     */
    fun parseSaveOnlyFaultsListJson(): List<String> {
        return parseSaveOnlyFaultsCodes
    }

    /**
     * @return list of category A faults
     */
    fun parseCategoryAFaultCodesJson(): Map<String, ArrayList<Int>> {
        return parseFaultCategoryACodes
    }

    /**
     * @return list of category B faults
     */
    fun parseCategoryBFaultCodesJson(): Map<String, ArrayList<Int>> {
        return parseFaultCategoryBCodes
    }

    /**
     * @return list of category B2 faults
     */
    fun parseCategoryB2FaultCodesJson(): Map<String, ArrayList<Int>> {
        return parseFaultCategoryB2Codes
    }

    /**
     * @return list of category C faults
     */
    fun parseCategoryCFaultCodesJson(): Map<String, ArrayList<Int>> {
        return parseFaultCategoryCCodes
    }
}