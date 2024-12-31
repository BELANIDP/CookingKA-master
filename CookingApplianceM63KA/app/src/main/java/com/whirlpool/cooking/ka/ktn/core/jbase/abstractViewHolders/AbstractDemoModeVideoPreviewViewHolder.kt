package core.jbase.abstractViewHolders

import android.os.Bundle
import android.presenter.customviews.widgets.headerBarNumpad.HeaderBarNumPadWidget
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.whirlpool.cooking.ka.databinding.FragmentDemoModeVideoPreviewBinding

abstract class AbstractDemoModeVideoPreviewViewHolder {
    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================
    /**
     * onCreateView provides the interface for the Fragment to be able to inflate the customized view.
     *
     * @param inflater [LayoutInflater]
     * @param container [ViewGroup]
     * @param savedInstanceState [Bundle]
     * @return [View]
     */
    abstract fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    abstract fun onDestroyView()

    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access the binding class
     *
     * @return [FragmentDemoModeVideoPreviewBinding]
     */
    abstract fun getFragmentDemoModeVideoPreviewBinding(): FragmentDemoModeVideoPreviewBinding?

    /**
     * Provides the interface to access bottom left image view to go to previous data
     *
     * @return [ImageView]
     */
    abstract fun getPreviousVideoImageView(): ImageView?
    /*---------------------------------------------------------------------------------------------------------------*/ /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access bottom right image view to go to next data
     *
     * @return [ImageView]
     */
    abstract fun getNextVideoImageView(): ImageView?

    /**
     * Provides the interface to access bottom center image view to go to play or pause
     *
     * @return [ImageView]
     */
    abstract fun getPlayPauseView(): ImageView?

    /**
     * Provides the interface to access View Pager to handle the video list
     *
     * @return [ViewPager2]
     */
    abstract fun getViewPagerPreviewView(): ViewPager2?

    /**
     * Provides the interface to access HeaderBar widget
     *
     * @return [HeaderBarWidget]
     */
    abstract fun getHeaderBarWidget(): HeaderBarWidget?

    /**
     * Provides the interface to access HeaderBar widget
     *
     * @return [HeaderBarWidget]
     */
    abstract fun getVideoControlLayout(): ConstraintLayout?
    /*---------------------------------------------------X---X---X---------------------------------------------------*/
}