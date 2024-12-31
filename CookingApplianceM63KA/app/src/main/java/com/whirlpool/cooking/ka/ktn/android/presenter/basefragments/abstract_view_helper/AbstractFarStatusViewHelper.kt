package android.presenter.basefragments.abstract_view_helper

import android.os.Bundle
import android.presenter.basefragments.AbstractFarStatusFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import android.presenter.customviews.widgets.status.CookingFarStatusWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ViewDataBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel

/**
 * File        : android.presenter.basefragments.abstract_view_helper.AbstractFarStatusViewHelper
 * Brief       : Abstract Status view helpers
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides view related events and view support for single, double far status fragment binding class
 */
abstract class AbstractFarStatusViewHelper {
    abstract fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View?

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    abstract fun onDestroyView()

    abstract fun getLayoutViewBinding(): ViewDataBinding?

    /**
     * set up data binding to get events of all status widget listeners
     */
    abstract fun setupBindingData(fragment: AbstractFarStatusFragment?)

    /**
     * for upper cavity cooking status widget for combo and double (upper cavity) and single oven and mwo
     */
    abstract fun getUpperCookingStatusWidget(): CookingFarStatusWidget?

    /**
     * for lower cavity cooking status widget for combo and double
     */
    abstract fun getLowerCookingStatusWidget(): CookingFarStatusWidget?

    /**
     * Cooking view model of upper cavity
     *
     * @return CookingViewModel
     */
    abstract fun getUpperViewModel(): CookingViewModel?
    /**
     * Cooking view model of Lower cavity
     *
     * @return CookingViewModel
     */
    abstract fun getLowerViewModel(): CookingViewModel?

    /**
     * kitchen timer far view
     *
     */
    abstract fun isKitchenTimerEnding(isKitchenTimerEnding:Boolean)

    /**
     * Far View Kitchen Timer running textview
     * @return [AppCompatTextView]? - The TextView instance
     */
   abstract fun getKitchenTimerRunningText(): AppCompatTextView?
    /**
     * Far View Kitchen Timer Icon Image View
     *@return [AppCompatImageView]? - The ImageView instance
     */
   abstract fun getKitchenTimerIcon():AppCompatImageView?
    /**
     * Far View Kitchen Timer Header bar Widget
     * @return [HeaderBarWidget]? - The HeaderBarWidget instance
     */
   abstract fun getHeaderBar():HeaderBarWidget?
}