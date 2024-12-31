package android.presenter.fragments.demo

import android.os.Bundle
import android.presenter.adapters.video_preview.VideoPlayerControlsListener
import android.presenter.adapters.video_preview.VideoPreviewPageAdapter
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.SharedViewModel
import core.utils.gone
import core.utils.visible
import core.viewHolderHelpers.DemoModeVideoPreviewViewHolderHelper
import java.lang.Boolean.TRUE

/**
 * File        : android.presenter.fragments.demo.DemoModeVideoPreviewFragment
 * Brief       : Demo Mode See Video Preview fragment
 * Author      : Karthikeyan D S
 * Created On  : 06/12/2024
 * Details     : User can View the demo video when selecting
 */
class DemoModeVideoPreviewFragment : Fragment(), VideoPlayerControlsListener, HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIExpansionUtils.HMICancelButtonInteractionListener  {

    /** To binding Fragment variables */
    private var demoModeVideoPreviewViewHolderHelper: DemoModeVideoPreviewViewHolderHelper? = null
    private lateinit var pagerView: ViewPager2
    private var index = 0
    private lateinit var videoFiles : Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        demoModeVideoPreviewViewHolderHelper = DemoModeVideoPreviewViewHolderHelper()
        demoModeVideoPreviewViewHolderHelper?.onCreateView(inflater, container, savedInstanceState)
        return demoModeVideoPreviewViewHolderHelper?.getFragmentDemoModeVideoPreviewBinding()?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initHeaderBar()
        loadAndPlayDemoVideo()
    }

    /**
     * Init view for controls and listeners
     */
    private fun initViews(){
        pagerView = demoModeVideoPreviewViewHolderHelper?.getViewPagerPreviewView()!!
        demoModeVideoPreviewViewHolderHelper?.getHeaderBarWidget()?.apply {
            visibility = View.VISIBLE
            getLeftImageView()?.visible()
            getHeaderTitle()?.visible()
        }

        index = provideSelectedVideoIndex()
        demoModeVideoPreviewViewHolderHelper?.getPlayPauseView()?.setOnClickListener {
            onVideoControllerClick()
            makeVideoControlsVisible()
        }

        demoModeVideoPreviewViewHolderHelper?.getNextVideoImageView()?.setOnClickListener {
            onNextImageButtonClick()
        }

        demoModeVideoPreviewViewHolderHelper?.getPreviousVideoImageView()?.setOnClickListener {
            onPreviousImageButtonClick()
        }
    }

    /**
     * Method to handle video pause and play
     */
    private fun onVideoControllerClick() {
        val viewHolder: VideoPreviewPageAdapter.ViewHolder =
            demoModeVideoPreviewViewHolderHelper?.getPlayPauseView()?.tag as VideoPreviewPageAdapter.ViewHolder
        if (viewHolder.binding.videoViewDemoMode.isPlaying) {
            viewHolder.binding.videoViewDemoMode.pause()
            demoModeVideoPreviewViewHolderHelper?.getPlayPauseView()?.setImageResource(R.drawable.ic_kt_play)
        } else {
            viewHolder.binding.videoViewDemoMode.start()
            demoModeVideoPreviewViewHolderHelper?.getPlayPauseView()?.setImageResource(R.drawable.ic_kt_pause)
        }
    }

    /**
     * Selected video index from previous screen
     */
    private fun provideSelectedVideoIndex(): Int {
        var selectedIndex = 0
        if (requireArguments().containsKey(BundleKeys.BUNDLE_VIDEO_OPTION)) {
            selectedIndex = requireArguments().getInt(BundleKeys.BUNDLE_VIDEO_OPTION)
        }
        return selectedIndex
    }

    /**
     * Initialize header bar settings
     */
    private fun initHeaderBar(){
        demoModeVideoPreviewViewHolderHelper?.getHeaderBarWidget()?.apply {
            setRightIconVisibility(false)
            setLeftIcon(R.drawable.ic_back_arrow)
            setTitleText(R.string.text_header_see_video)
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
        }
        demoModeVideoPreviewViewHolderHelper?.getHeaderBarWidget()?.setCustomOnClickListener(this)
    }

    /**
     * Left icon click in header bar to navigate previous screen
     */
    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                getViewSafely(this) ?: requireView()
            )
        )
    }

    /**
     * To get video assets name and load it in video view
     */
    private fun loadAndPlayDemoVideo(){
        videoFiles = resources.getStringArray(R.array.demo_mode_video_files)
        initialiseViewPagerForVideos(videoFiles)
    }

    /**
     * Method to create and bind data to View Pager for videos
     */
    private fun initialiseViewPagerForVideos(videoFiles: Array<String>) {
        demoModeVideoPreviewViewHolderHelper?.getViewPagerPreviewView()?.adapter =
            VideoPreviewPageAdapter(requireContext(), videoFiles,this, demoModeVideoPreviewViewHolderHelper)

        demoModeVideoPreviewViewHolderHelper?.getViewPagerPreviewView()?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handlePreviewIcons(position)
            }
        })

        demoModeVideoPreviewViewHolderHelper?.getViewPagerPreviewView()?.apply {
            setCurrentItem(index, false)
            adapter?.notifyItemChanged(index)
        }
    }

    /**
     * Method will be executed On Click of left ImageView of Stepper button - PREVIOUS
     */
    private fun onPreviousImageButtonClick() {
        if (index > 0) {
            index--
        } else if (index == 0) {
            index = videoFiles.size - 1
        }
        viewMove()
    }

    /**
     * Method will be executed On Click of Right ImageView of Stepper button - NEXT
     */
    override fun onNextImageButtonClick() {
        if (index < (videoFiles.size - 1)) {
            index++
        }else if(index == videoFiles.size - 1){
            index = 0
        }
        viewMove()
    }

    /**
     * Method will be executed On index
     */
    private fun viewMove() {
        demoModeVideoPreviewViewHolderHelper?.getViewPagerPreviewView()?.apply {
            setCurrentItem(index, false)
            adapter?.notifyItemChanged(index)
        }
    }

    /**
     * Method to handle preview icons
     */
    private fun handlePreviewIcons(position: Int) {
        index = position
        demoModeVideoPreviewViewHolderHelper?.getVideoControlLayout()?.visible()
        demoModeVideoPreviewViewHolderHelper?.getHeaderBarWidget()?.visible()
        demoModeVideoPreviewViewHolderHelper?.getPlayPauseView()?.visible()
        demoModeVideoPreviewViewHolderHelper?.getNextVideoImageView()?.visible()
        demoModeVideoPreviewViewHolderHelper?.getPreviousVideoImageView()?.visible()
        demoModeVideoPreviewViewHolderHelper?.getPlayPauseView()?.setEnabled(true)
        demoModeVideoPreviewViewHolderHelper?.getNextVideoImageView()?.setEnabled(true)
        demoModeVideoPreviewViewHolderHelper?.getPreviousVideoImageView()?.setEnabled(true)
    }

    /**
     * To make controls visible to handle next, previous, play/pause
     */
    override fun makeVideoControlsVisible(){
        demoModeVideoPreviewViewHolderHelper?.getHeaderBarWidget()?.removeCallbacks(runnable)
        demoModeVideoPreviewViewHolderHelper?.getHeaderBarWidget()?.visible()
        demoModeVideoPreviewViewHolderHelper?.getVideoControlLayout()?.visible()

        val viewHolder: VideoPreviewPageAdapter.ViewHolder =
            demoModeVideoPreviewViewHolderHelper?.getPlayPauseView()?.tag as VideoPreviewPageAdapter.ViewHolder
        if (viewHolder.binding.videoViewDemoMode.isPlaying) {
            demoModeVideoPreviewViewHolderHelper?.getHeaderBarWidget()?.postDelayed(
                runnable, AppConstants.VIDEO_CONTROLS_TIME_OUT
            )
        }
    }

    /**
     * Runnable thread to hide the controls and header bar after certain timeout
     */
    private val runnable = Runnable {
        demoModeVideoPreviewViewHolderHelper?.getHeaderBarWidget()?.gone()
        demoModeVideoPreviewViewHolderHelper?.getVideoControlLayout()?.gone()
    }

    /**
     * cancel button interaction listener to navigate to home/status
     */
    override fun onHMICancelButtonInteraction() {
        val sharedViewModel: SharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        if (!sharedViewModel.isApplianceInAOrCCategoryFault()) {
            if (CookingAppUtils.isSelfCleanFlow() && TRUE == CookingViewModelFactory.getInScopeViewModel().doorLockState.value) {
                navigateSafely(this, R.id.action_goToSelfCleanStatus, null, null)
            } else {
                CookingAppUtils.navigateToStatusOrClockScreen(this)
            }
        }
    }

}