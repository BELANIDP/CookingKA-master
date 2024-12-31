package android.presenter.adapters.video_preview

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentDemoModeVideoViewBinding
import com.whirlpool.hmi.settings.SettingsViewModel
import core.utils.AppConstants
import core.utils.AppLanguageDetails
import core.utils.visible
import core.viewHolderHelpers.DemoModeVideoPreviewViewHolderHelper

/**
 * File        : android.presenter.adapters.video_preview.VideoPreviewPageAdapter
 * Brief       : Demo Mode See Video Preview adapter class
 * Author      : Karthikeyan D S
 * Created On  : 11/12/2024
 * Details     : Demo Mode Video Preview Adapter class to hold the video preview
 */
class VideoPreviewPageAdapter(
    private val context: Context,
    private val videoFiles: Array<String>,
    private val videoPlayerControlsListener: VideoPlayerControlsListener,
    private val demoModeVideoPreviewViewHolderHelper: DemoModeVideoPreviewViewHolderHelper?
) : RecyclerView.Adapter<VideoPreviewPageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: FragmentDemoModeVideoViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentDemoModeVideoViewBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        demoModeVideoPreviewViewHolderHelper?.getPlayPauseView()?.tag = holder
        holder.binding.videoViewDemoMode.visible()
        var appLocaleCode = SettingsViewModel.getSettingsViewModel().appLanguage.value
        if (appLocaleCode == AppConstants.EMPTY_STRING)
            appLocaleCode = AppLanguageDetails.English.languageCode

        val filename = videoFiles[position] + "_" + appLocaleCode
        val path = "${AppConstants.ANDROID_RESOURCE_PATH}${context.packageName}/raw/${filename}"

        holder.binding.videoViewDemoMode.setVideoURI(Uri.parse(path))
        holder.binding.videoViewDemoMode.start()
        demoModeVideoPreviewViewHolderHelper?.getPlayPauseView()
            ?.setImageResource(R.drawable.ic_kt_pause)

        holder.binding.videoViewDemoMode.setOnInfoListener { _, i, _ ->
            if (i == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                videoPlayerControlsListener.makeVideoControlsVisible()
            }
            false
        }

        holder.binding.videoViewDemoMode.setOnTouchListener { _, _ ->
            videoPlayerControlsListener.makeVideoControlsVisible()
            false
        }

        holder.binding.videoViewDemoMode.setOnCompletionListener {
            if (demoModeVideoPreviewViewHolderHelper?.getNextVideoImageView()?.visibility == View.VISIBLE) {
                videoPlayerControlsListener.onNextImageButtonClick()
            } else {
                demoModeVideoPreviewViewHolderHelper?.getPlayPauseView()
                    ?.setImageResource(R.drawable.ic_kt_play)
                videoPlayerControlsListener.makeVideoControlsVisible()
            }
        }
    }

    override fun getItemCount(): Int {
        return videoFiles.size
    }
}

/**
 * Interface to handle the click listeners on button controls
 */
interface VideoPlayerControlsListener {
    fun makeVideoControlsVisible()
    fun onNextImageButtonClick()
}