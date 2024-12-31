package android.presenter.basefragments.abstract_view_helper

import android.os.Bundle
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.customviews.widgets.headerbar.NoTitleHeaderBarWidget
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel

/**
 * File        : android.presenter.basefragments.abstract_view_helper.AbstractStatusViewHelper
 * Brief       : Abstract Status view helpers
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides view related events and view support for single, double status fragment binding class
 */
abstract class AbstractStatusViewHelper {
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
    abstract fun setupBindingData(fragment: AbstractStatusFragment?)

    /**
     * for upper cavity cooking status widget for combo and double (upper cavity) and single oven and mwo
     */
    abstract fun getDefaultCookingStatusWidget(): CookingStatusWidget?

    /**
     * for lower cavity cooking status widget for combo and double
     */
    abstract fun getLowerCookingStatusWidget(): CookingStatusWidget?

    /**
     * for upper oven cavity selection
     */

    abstract fun provideUpperCavitySelectionLayout(): ConstraintLayout?

    abstract fun provideUpperCavitySelection(): TextView?
    abstract fun provideUpperCavitySelectionIcon(): ImageView?

    /**
     * for lower oven cavity selection
     */
    abstract fun provideLowerCavitySelectionLayout(): ConstraintLayout?

    abstract fun provideLowerCavitySelection(): TextView?

    abstract fun getUpperViewModel(): CookingViewModel?

    abstract fun getLowerViewModel(): CookingViewModel?
    
    abstract fun provideLowerCavitySelectionIcon(): ImageView?

    abstract fun provideHeaderBarWidget(): NoTitleHeaderBarWidget?
}