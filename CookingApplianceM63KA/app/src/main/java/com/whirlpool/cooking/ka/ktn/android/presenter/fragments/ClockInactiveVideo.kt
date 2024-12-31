package android.presenter.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentClockInactiveBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.settings.SettingsViewModel.SabbathMode.SABBATH_COMPLIANT
import core.utils.AppConstants
import core.utils.CookingAppUtils.Companion.manageHMIPanelLights
import core.utils.DoorEventUtils.Companion.DOOR_STATE
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.setNavigatedFrom
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.SharedViewModel
import core.utils.UriUtils

class ClockInactiveVideo : Fragment(), HMIExpansionUtils.UserInteractionListener {

    private var inactiveVideo: FragmentClockInactiveBinding? = null
    private var currentVideoIndex = 0
    private val videoUris = mutableListOf<Uri>()
    private val handler = Handler(Looper.getMainLooper())
    //this array variable keeps track of door event based on door event
    private val doorStateToAction = arrayOf(DOOR_STATE.INITIAL, DOOR_STATE.INITIAL)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        inactiveVideo = FragmentClockInactiveBinding.inflate(inflater, container, false)
        setupVideoUris()

        // Retrieve the last played video index from SettingsViewModel
        currentVideoIndex = SettingsViewModel.getSettingsViewModel()
            .getUserDataIntValue("currentVideoIndex", false)

        // Play the current video
        playVideoAndNavigateBack()

        onClickView()
        return inactiveVideo!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeDoorInteraction(CookingViewModelFactory.getPrimaryCavityViewModel())
        if(CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO ||
            CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN){
            observeDoorInteraction(CookingViewModelFactory.getSecondaryCavityViewModel())
        }
    }

    /**
     * door interaction observer, if the door is open and the value is not same as when Fragment was created then move to recipe selection for that cavity
     * @param cookingViewModel
     */
    private fun observeDoorInteraction(cookingViewModel: CookingViewModel) {
        //always update door action to initial before observing, because upon registering observer it would give the value of current door which we want to ignore
        //as door have to open and close, the initial values are ignored as event triggered
        updateDoorState(cookingViewModel, DOOR_STATE.INITIAL)
        cookingViewModel.doorState.observe(viewLifecycleOwner){ doorState ->
            if (SettingsViewModel.getSettingsViewModel().controlLock.value == true ){
                if (doorState) {
                    NavigationUtils.navigateSafely(
                        this,
                        R.id.action_to_controlUnlockFragment,
                        null,
                        null
                    )
                }
            } else {
                manageDoorInteraction(doorState, cookingViewModel)
            }
        }
    }

    /**
     * get door state action variable based on cooking View Model
     */
    private fun getDoorState(cookingViewModel: CookingViewModel): DOOR_STATE {
        return doorStateToAction[if(cookingViewModel.isPrimaryCavity) 0 else 1]
    }

    /**
     * update doorStateToAction variable based on cavity
     *
     * @param cookingViewModel for a particular cavity that has triggered door event
     * @param doorState enum that represents opening/close event
     */
    private fun updateDoorState(cookingViewModel: CookingViewModel, doorState: DOOR_STATE) {
        HMILogHelper.Logd(tag, "DoorState: ${cookingViewModel.cavityName.value} updateDoorState to $doorState")
        doorStateToAction[if(cookingViewModel.isPrimaryCavity) 0 else 1] = doorState
    }


    /**
     * manage door interaction for ClockFragment
     * the opening and closing event has been triggered if in clock screen door was open before then closing door event would not triggered recipe selection
     * @param doorState true if door was opened, false otherwise
     * @param cookingViewModel for a particular cavity
     */
    private fun manageDoorInteraction(doorState: Boolean, cookingViewModel: CookingViewModel){
        //if initial then door observer was registered and ready to receive next events
        if (getDoorState(cookingViewModel) == DOOR_STATE.INITIAL) {
            //if door is already open then we don't want to update its state, as closing the door would again got to recipe selection
            if(doorState) return
            updateDoorState(cookingViewModel, DOOR_STATE.SHOW_OPEN_CLOSE_DOOR_POPUP)
            return
        }

        //if door is open and passed initial check then mark as ready
        if (doorState && getDoorState(cookingViewModel) == DOOR_STATE.SHOW_OPEN_CLOSE_DOOR_POPUP) {
            updateDoorState(cookingViewModel, DOOR_STATE.DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP)
            return
        }

        //If sabbath mode is activated then do not navigate to the recipe selection screen
        if (SettingsViewModel.getSettingsViewModel().sabbathMode.value == SABBATH_COMPLIANT) {
            HMILogHelper.Logd("Door closed : Sabbath mode is activated , do not navigate to the recipe selection screen")
            return
        }

        if(ScrollDialogPopupBuilder.isAnyPopupShowing()){
            HMILogHelper.Logd(tag, "Door open /close condition satisfy for ${cookingViewModel.cavityName.value}, but a DialogFragment is Visible so not moving to recipeSelectionFragment")
            return
        }

        //if door is closed and passed initial checks then move on to recipe selection
        if (!doorState && getDoorState(cookingViewModel) == DOOR_STATE.DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP) {
            HMILogHelper.Logd(
                tag,
                "door opened and closed satisfied for ${cookingViewModel.cavityName.value}, moving to recipe selection fragment"
            )
            if (cookingViewModel.isOfTypeOven) SharedViewModel.getSharedViewModel(this.requireActivity())
                .setCurrentRecipeBeingProgrammed(AppConstants.QUICK_START)
            stopVideoPlayback()
            handler.removeCallbacksAndMessages(null)
            setNavigatedFrom(AppConstants.CLOCK_FAR_OR_VIDEO_VIEW_FRAGMENT)
            if (cookingViewModel.isPrimaryCavity) {
                NavigationUtils.navigateToUpperRecipeSelection(this)
            } else {
                NavigationUtils.navigateToLowerRecipeSelection(this)
            }
            manageHMIPanelLights(homeLight = true, cancelLight = true, cleanLight = false)
        }
    }

    private fun setupVideoUris() {
        videoUris.apply {
            UriUtils.getUriFromRawResource(requireContext(), R.raw.clock_video_1)?.let { add(it) }
            UriUtils.getUriFromRawResource(requireContext(), R.raw.clock_video_2)?.let { add(it) }
            UriUtils.getUriFromRawResource(requireContext(), R.raw.clock_video_3)?.let { add(it) }
        }
    }

    private fun playVideoAndNavigateBack() {
        if (videoUris.isEmpty()) return

        // Play the current video
        if (currentVideoIndex < 0 || (currentVideoIndex > (videoUris.size - 1))) currentVideoIndex =
            0
        showVideoLogo(videoUris[currentVideoIndex])

        // Navigate back and schedule the next video when the current video completes
        inactiveVideo?.videoView?.setOnCompletionListener {
            // Save the current video index
            currentVideoIndex = (currentVideoIndex + 1) % videoUris.size
            SettingsViewModel.getSettingsViewModel()
                .setUserDataIntValue("currentVideoIndex", currentVideoIndex, false)

            // Navigate back to the clock screen
            NavigationUtils.navigateSafely(
                this,
                R.id.action_showInactivityVideoFragment_to_clockFragment,
                null,
                null
            )

            // Schedule the next video to play after 1 minute
            handler.postDelayed({
                playVideoAndNavigateBack()
            }, 60000) // 1 minute delay
        }
    }

    private fun showVideoLogo(uri: Uri) {
        try {
            inactiveVideo?.videoView?.setVideoURI(uri)
            inactiveVideo?.videoView?.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = false // Ensure the video plays only once
            }
            inactiveVideo?.videoView?.setOnErrorListener { _, _, _ -> false }
            inactiveVideo?.videoView?.start()
            CookingAppUtils.setApplianceOtaState()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onClickView() {
        inactiveVideo?.root?.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            NavigationUtils.navigateSafely(
                this,
                R.id.action_showInactivityVideoFragment_to_clockFragment,
                null,
                null
            )
        }
    }

    private fun stopVideoPlayback() {
        try {
            if (inactiveVideo?.videoView?.isPlaying == true) {
                inactiveVideo?.videoView!!.stopPlayback()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        stopVideoPlayback()
        OTAVMFactory.getOTAViewModel().setApplianceBusyState(true)
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroy() {
        inactiveVideo = null
        super.onDestroy()
    }

    override fun onUserInteraction() {
        handler.removeCallbacksAndMessages(null)
        NavigationUtils.navigateSafely(
            this,
            R.id.action_showInactivityVideoFragment_to_clockFragment,
            null,
            null
        )
    }
}
