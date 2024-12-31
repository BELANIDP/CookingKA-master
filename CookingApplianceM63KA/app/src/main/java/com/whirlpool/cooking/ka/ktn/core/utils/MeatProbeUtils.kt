package core.utils

import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.ContextProvider

/**
 * File       : com.whirlpool.cooking.utils.MeatProbeUtils.
 * Brief      : Contains helper utility methods for handling probe insertion and removal which could
 *              be used across KA APP
 * Author     : Hiren
 * Created On : 05/29/2024
 * Details    : Repeated logic are put here so as to access from different classes.
 */
class MeatProbeUtils {

    interface MeatProbeListener {
        /**
         * callback on probe insertion
         * @param cookingViewModel of a particular cavity where the probe inserted
         */
        fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?)

        /**
         *  callback on probe removal
         * @param cookingViewModel of a particular cavity where the probe inserted
         */
        fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?)
    }

    companion object {
        private var meatProbeListener: MeatProbeListener? = null

        /**
         * call this listener onStart to get the live data events for meat probe connection
         * @param listener to get events for meat probe live data
         */
        fun setMeatProbeListener(listener: MeatProbeListener) {
            HMILogHelper.Logi("Set MeatProbeListener")
            meatProbeListener = listener
        }

        /**
         * remove listener when a fragment no longer wants to receive live data for Meat probe
         * call onStop
         */
        fun removeMeatProbeListener() {
            HMILogHelper.Logi("Removed MeatProbeListener")
            meatProbeListener = null
        }

        /**
         * remove meat probe listener for meat probe
         * to avoid conflict with fragment lifecycle
         * @param dialogPopupBuilder dialog being visible
         */
        fun removeMeatProbeListenerAndDismissPopup(
            dialogPopupBuilder: ScrollDialogPopupBuilder?,
            fragment: Fragment,
            onDialogDismissCallback:() -> Unit = {}
        ) {
            HMILogHelper.Logi("Removed MeatProbeListener")
            val handler = Handler(Looper.getMainLooper())
            meatProbeListener = null
            fragment.onResume()
            dialogPopupBuilder?.let { builder ->
                handler.postDelayed(
                    { builder.dismiss()
                        onDialogDismissCallback()
                    },
                    AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }
        }

        /**
         * to see if the meat probe is connected for a particular cavity view model
         * @param cookingViewModel
         * @return true if connected false otherwise a particular cavity
         */
        fun isMeatProbeConnected(cookingViewModel: CookingViewModel?): Boolean {
            return java.lang.Boolean.TRUE == cookingViewModel?.meatProbeState?.value
        }
        /**
         * to see if the meat probe is connected for any cavity view model
         * @return true if connected false otherwise a particular cavity
         */
        fun isAnyCavityHasMeatProbeConnected():Pair<Boolean,CookingViewModel?> {
            when(CookingViewModelFactory.getProductVariantEnum()){
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> if(isMeatProbeConnected(CookingViewModelFactory.getPrimaryCavityViewModel())) return Pair(true, CookingViewModelFactory.getPrimaryCavityViewModel())
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                    if(isMeatProbeConnected(CookingViewModelFactory.getPrimaryCavityViewModel())) return Pair(true, CookingViewModelFactory.getPrimaryCavityViewModel())
                    if(isMeatProbeConnected(CookingViewModelFactory.getSecondaryCavityViewModel()))  return Pair(true, CookingViewModelFactory.getSecondaryCavityViewModel())
                }
                CookingViewModelFactory.ProductVariantEnum.COMBO -> if(isMeatProbeConnected(CookingViewModelFactory.getSecondaryCavityViewModel())) return Pair(true, CookingViewModelFactory.getSecondaryCavityViewModel())
                else -> return Pair(false, null)
            }
            HMILogHelper.Logd("MeatProbeUtils", "No cavity has Meat probe connected")
            return Pair(false, null)
        }

        /**
         * Method to initialize HMIExpansionViewModel and to start observe for the HMI Expansion
         * button events
         *
         * @param fragmentActivity The fragment activity
         */
        fun observeMeatProbeEvents(fragmentActivity: FragmentActivity) {
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> handleMeatProbeEvents(
                    fragmentActivity, CookingViewModelFactory.getPrimaryCavityViewModel()
                )

                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                    handleMeatProbeEvents(
                        fragmentActivity, CookingViewModelFactory.getPrimaryCavityViewModel()
                    )
                    handleMeatProbeEvents(
                        fragmentActivity, CookingViewModelFactory.getSecondaryCavityViewModel()
                    )
                }

                CookingViewModelFactory.ProductVariantEnum.COMBO -> handleMeatProbeEvents(
                    fragmentActivity, CookingViewModelFactory.getSecondaryCavityViewModel()
                )

                else -> HMILogHelper.Logd("Meat Probe not applicable for Microwave Only Variant")
            }
        }

        /**
         * Observing a common meat probe for different variants
         *
         * @param activity LifeCycle owner to observe on MeatProbe event data
         * @param cookingViewModel cooking View Model of a particular cavity
         */
        private fun handleMeatProbeEvents(
            activity: FragmentActivity,
            cookingViewModel: CookingViewModel,
        ) {
            cookingViewModel.meatProbeState.observe(activity) { primaryMeatProbeState: Boolean ->
                if (meatProbeListener != null) {
                    if (primaryMeatProbeState) {
                        meatProbeListener?.onMeatProbeInsertion(
                            cookingViewModel
                        )
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.brand_event,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                    } else {
                        meatProbeListener?.onMeatProbeRemoval(
                            cookingViewModel
                        )
                    }
                }
            }
        }
    }
}