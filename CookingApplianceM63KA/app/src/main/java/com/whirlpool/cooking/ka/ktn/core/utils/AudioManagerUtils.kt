/*
 * ************************************************************************************************
 * ***** Copyright (c) 2020. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.utils

import android.content.Context
import android.media.AudioManager
import com.google.gson.Gson
import com.whirlpool.hmi.uicomponents.audio.AudioCallbackListener
import com.whirlpool.hmi.uicomponents.audio.WHRAudioManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * File        : core.utils.AudioManagerUtils <br></br>
 * Brief       : Util class that Wraps the WHRAudioManager APIs , So any change in the
 * WHRAudioManager will affect only this file<br></br>
 * Author      : GOYALM5 <br></br>
 * Created On  : 27-03-2024 <br></br>
 * Details     : This Util class is to handle all the Audio Related features. This is the only
 * class communicating with WHRAudioManager .<br></br>
 */
object AudioManagerUtils {
    //Queue having all sound file details which we need to play.
    private val soundList: MutableList<SoundQueue> = mutableListOf()
    private var audioCallbackListener: AudioCallbackListener? = null

    @Suppress("unused")
    @JvmStatic
    fun registerAudioCallBackListener(context: Context?) {
        audioCallbackListener = object : AudioCallbackListener {
            override fun onPlaybackComplete(audioId: Int, soundType: Int) {
                HMILogHelper.Logi(
                    "AudioManagerUtilsNew",
                    "onPlaybackComplete Audio Play is Completed"
                )
                pollAndPlaySound(context)
            }

            override fun onPlaybackStarted(audioDuration: Long, audioId: Int, soundType: Int) {
                HMILogHelper.Logi(
                    "AudioManagerUtilsNew", "onPlaybackStarted Audio duration is : " +
                            audioDuration + " audioId = " + audioId + " soundType = " + soundType
                )
            }
        }
        WHRAudioManager.getInstance().registerAudioCallbackListener(audioCallbackListener)
    }

    @Suppress("unused")
    @JvmStatic
    fun unRegisterAudioCallBackListener() {
        WHRAudioManager.getInstance().unregisterAudioCallbackListener()
    }

    @Suppress("unused")
    @JvmStatic
    fun stopAudio(audioId: Int, soundType: Int, uID: Long) {
        if (soundList.size > 0) {
            soundList.removeAll { it.uId == uID }
            HMILogHelper.Logi(
                "AudioManagerUtilsNew", "stopAudio with" +
                        " audioId = " + audioId +
                        " soundType = " + soundType + " uID = " + uID
            )
            WHRAudioManager.getInstance().stopAudio(audioId, soundType)
        }
    }

    @JvmStatic
    fun playOneShotSound(
        context: Context?, audioId: Int, soundType: Int,
        isOneTime: Boolean?, periodicity: Int, frequency: Int
    ): Long {
        val currentTimeStamp = System.currentTimeMillis()

        playAudioQueue(
            context,
            audioId,
            soundType,
            isOneTime,
            periodicity,
            frequency,
            currentTimeStamp
        )
        HMILogHelper.Logi("AudioManagerUtilsNew", "Play Button Press Audio")
        return currentTimeStamp
    }

    @Suppress("unused")
    @JvmStatic
    fun playAtFrequencySound(
        context: Context?, audioId: Int, soundType: Int,
        isOneTime: Boolean?, periodicity: Int, frequency: Int
    ): Long {
        val currentTimeStamp = System.currentTimeMillis()
        playAudioQueue(
            context,
            audioId,
            soundType,
            isOneTime,
            periodicity,
            frequency,
            currentTimeStamp
        )
        HMILogHelper.Logi("AudioManagerUtilsNew", "Play Button Press Audio")
        return currentTimeStamp
    }

    @JvmStatic
    fun playAudioQueue(
        context: Context?, audioId: Int, soundType: Int,
        @Suppress("UNUSED_PARAMETER") isOneTime: Boolean?, periodicity: Int, frequency: Int,
        currentTimeStamp: Long
    ) {
        HMILogHelper.Logi(
            "AudioManagerUtilsNew playAudioQueue",
            " audioId = " + audioId + " periodicity = " + periodicity +
                    " soundType = " + soundType + " freq = " + frequency
        )
        val repeatNumber = (periodicity / frequency)
        HMILogHelper.Logi(
            "AudioManagerUtilsNew playAudioQueue",
            " currentTimeStamp = " + currentTimeStamp +
                    " repeatNumber = " + repeatNumber
        )
        // Creating a temporary list to avoid modifying soundList during iteration
        val tempSoundList = mutableListOf<SoundQueue>()

        for (index in 0..repeatNumber) {
            val tsToPlay = currentTimeStamp + (index * frequency * 60000)
            tempSoundList.add(SoundQueue(currentTimeStamp, audioId, tsToPlay, soundType))
            HMILogHelper.Logi(
                "AudioManagerUtilsNew playAudioQueue",
                " tsToPlay = $tsToPlay"
            )
        }
        tempSoundList.sortBy { it.uId }
        synchronized(soundList) {
            soundList.addAll(tempSoundList)
            HMILogHelper.Logi("AudioManagerUtilsNew playAudioQueue"," -------- soundList --------\n ${Gson().toJson(soundList)}")
        }

        if (soundList.isNotEmpty()) {
            if (!WHRAudioManager.getInstance().isPlaying) {
                //poll one element at a time from the top of queue
                pollAndPlaySound(context)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun pollAndPlaySound(context: Context?) {
        if (soundList.size > 0) {
            var localSoundQueue = soundList[0]
            if (localSoundQueue.tsToPlay < System.currentTimeMillis()) {
                HMILogHelper.Logi(
                    "AudioManagerUtilsNew", " audioId = " + localSoundQueue.audioId +
                            " soundType = " + localSoundQueue.soundType
                )
                when (localSoundQueue.soundType) {
                    AudioManager.STREAM_ALARM -> WHRAudioManager.getInstance()
                        .playAlarmTimerAudio(context, localSoundQueue.audioId)
                    AudioManager.STREAM_SYSTEM -> WHRAudioManager.getInstance()
                        .playButtonEffectAudio(context, localSoundQueue.audioId)
                    else -> {}
                }
                if (soundList.size > 0) {
                    soundList.removeAt(0)
                }
                if (soundList.size > 0) {
                    localSoundQueue = soundList[0]
                    GlobalScope.launch {
                        HMILogHelper.Logi(
                            "localSoundQueue delay = " + (localSoundQueue.tsToPlay - System.currentTimeMillis())
                        )
                        delay(localSoundQueue.tsToPlay - System.currentTimeMillis())
                        pollAndPlaySound(context)
                    }
                }
            } else {
                GlobalScope.launch {
                    delay(localSoundQueue.tsToPlay - System.currentTimeMillis())
                    pollAndPlaySound(context)
                }
            }
        }
    }

}