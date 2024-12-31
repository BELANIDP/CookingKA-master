package android.presenter.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.whirlpool.cooking.common.utils.EspressoIdlingResource
import com.whirlpool.cooking.ka.BuildConfig
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentSplashBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.audio.WHRAudioManager
import core.utils.AppConstants
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.SharedViewModel
import core.utils.UriUtils

/**
 * The class provides the launch screen which shows animated KitchenAid brand logo
 * until the SDK is initialized.
 */
class SplashFragment : Fragment() {
    private var splashBinding: FragmentSplashBinding? = null

    /**
     * Observe whether CookingViewModelFactory is initialized or not.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_DIGITAL_UNBOXING)
        splashBinding = FragmentSplashBinding.inflate(inflater, container, false)
        UriUtils.getUriFromRawResource(requireContext(), R.raw.video_brand_logo)
            ?.let { showVideoLogo(it) }
        observeCookingViewModel()
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        return splashBinding!!.root
    }

    /**
     * Observe whether CookingViewModelFactory is initialized or not.
     */
    private fun observeCookingViewModel() {
        //Added for automation testing test cases.  Notify isReady() observe started callback
        if (BuildConfig.DEBUG) {
            HMILogHelper.Logd("Idling +++ ")
            EspressoIdlingResource.getInstance().increment()
        }
        CookingViewModelFactory.isReady().observe(
            viewLifecycleOwner
        ) { isReady: Boolean ->
            Log.e("isReady", "isReady = $isReady")
            checkIsCookingViewModelReady(isReady)
        }
    }

    private var sharedViewModel: SharedViewModel? = null

    /**
     * @param isReady - true/false status when observing the CookingViewModelFactory.
     */
    private fun checkIsCookingViewModelReady(isReady: Boolean) {
        try {
            if (isReady) {
                //Added for automation testing test cases. Notify isReady() observe success callback
                if (BuildConfig.DEBUG) {
                    HMILogHelper.Logd("Idling --- ")
                    EspressoIdlingResource.getInstance().decrement()
                }
                stopVideoPlayback()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * stops the video animation logo if it is playing already
     */
    private fun stopVideoPlayback() {
        try {
            if (splashBinding?.videoViewLogo?.isPlaying == true) {
                splashBinding?.videoViewLogo!!.stopPlayback()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Used to load video file in the video view.
     * @param uri absolute location of the video file from the raw resource folder.
     */
    private fun showVideoLogo(uri: Uri) {
        try {
            splashBinding?.videoViewLogo?.setVideoURI(uri)
            splashBinding?.videoViewLogo?.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
            }
            splashBinding?.videoViewLogo?.setOnErrorListener { _, _, _ -> false }
            splashBinding?.videoViewLogo?.start()
            WHRAudioManager.getInstance().playAudio(context, R.raw.power_on)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        stopVideoPlayback()
    }

    override fun onDestroy() {
        splashBinding = null
        super.onDestroy()
    }
}