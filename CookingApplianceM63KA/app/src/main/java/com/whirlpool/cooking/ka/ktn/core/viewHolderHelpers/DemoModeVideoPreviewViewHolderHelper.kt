package core.viewHolderHelpers

import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.whirlpool.cooking.ka.databinding.FragmentDemoModeVideoPreviewBinding
import core.jbase.abstractViewHolders.AbstractDemoModeVideoPreviewViewHolder

/**
 * File        : core.viewHolderHelpers.DemoModeVideoPreviewViewHolderHelper.
 * Brief       : Demo Mode Video Preview view holder responsible for holding all views and provided when required
 * Author      : Karthikeyan D S
 * Created On  : 06-12-2024
 */
class DemoModeVideoPreviewViewHolderHelper : AbstractDemoModeVideoPreviewViewHolder() {

    /** To binding Fragment variables */
    private var fragmentDemoModeVideoPreviewBinding: FragmentDemoModeVideoPreviewBinding? = null

    /**
     * Inflate the customized view
     *
     * @param inflater           [LayoutInflater]
     * @param container          [ViewGroup]
     * @param savedInstanceState [Bundle]
     * @return [View]
     */
    override fun onCreateView(
        inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        fragmentDemoModeVideoPreviewBinding =
            inflater?.let { FragmentDemoModeVideoPreviewBinding.inflate(it, container, false) }
        return fragmentDemoModeVideoPreviewBinding?.root
    }

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    override fun onDestroyView() {
        fragmentDemoModeVideoPreviewBinding = null
    }

    /**
     * Provides the interface to access the binding class
     *
     * @return [FragmentDemoModeVideoPreviewBinding]
     */
    override fun getFragmentDemoModeVideoPreviewBinding(): FragmentDemoModeVideoPreviewBinding? =
        fragmentDemoModeVideoPreviewBinding

    /**
     * Provides the interface to access bottom previous button
     *
     * @return [ImageView]
     */
    override fun getPreviousVideoImageView(): ImageView? =
        fragmentDemoModeVideoPreviewBinding?.imageViewPreviousButton

    /**
     * Provides the interface to access bottom next button
     *
     * @return [ImageView]
     */
    override fun getNextVideoImageView(): ImageView? =
        fragmentDemoModeVideoPreviewBinding?.imageViewNextButton

    /**
     * Provides the interface to access bottom Play/Pause button
     *
     * @return [ImageView]
     */
    override fun getPlayPauseView(): ImageView? =
        fragmentDemoModeVideoPreviewBinding?.imageViewPauseButton

    /**
     * Provides the interface to access view pager to handle video list
     *
     * @return [ViewPager2]
     */
    override fun getViewPagerPreviewView(): ViewPager2? =
        fragmentDemoModeVideoPreviewBinding?.demoModePageView

    /**
     * Provides the interface to access navigation controls
     *
     * @return [HeaderBarWidget]
     */
    override fun getHeaderBarWidget(): HeaderBarWidget? =
        fragmentDemoModeVideoPreviewBinding?.seeVideoDemoModeHeaderBar

    override fun getVideoControlLayout(): ConstraintLayout? =
        fragmentDemoModeVideoPreviewBinding?.viewPlayPauseControls
}