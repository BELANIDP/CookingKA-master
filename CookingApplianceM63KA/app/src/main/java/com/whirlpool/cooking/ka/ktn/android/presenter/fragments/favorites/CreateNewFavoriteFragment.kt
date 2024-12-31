package android.presenter.fragments.favorites

import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentCreateNewFavoriteBinding
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.NavigationUtils

/**
 * File       : [android.presenter.fragments.favorites.CreateNewFavoriteFragment]
 * Brief      : Implementation [Fragment] class for creating new favorite, when saved fav is zero
 * Author     : PANDES18.
 * Created On : 30/09/2024
 * Details    :
 */
class CreateNewFavoriteFragment : SuperAbstractTimeoutEnableFragment() {
    private var _binding: FragmentCreateNewFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateNewFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            headerBar.apply {
                setTitleText(R.string.text_see_video_favorites)
                setRightIconVisibility(true)
                setRightIcon(R.drawable.ic_add_40)
                setOvenCavityIconVisibility(false)
                setInfoIconVisibility(false)
                setCustomOnClickListener(object :
                    HeaderBarWidgetInterface.CustomClickListenerInterface {
                    override fun leftIconOnClick() {
                        NavigationUtils.navigateSafely(
                            this@CreateNewFavoriteFragment,
                            R.id.action_createNewFavoriteFragment_to_recipeSelectionFragment,
                            null,
                            NavOptions.Builder().setPopUpTo(R.id.recipeSelectionFragment, true).build()
                        )
                    }

                    override fun rightIconOnClick() {
                        CookingAppUtils.setNavigatedFrom(AppConstants.NAVIGATION_FROM_CREATE_FAV)
                        NavigationUtils.navigateSafely(
                            this@CreateNewFavoriteFragment,
                            R.id.action_createNewFavoriteFragment_to_favoriteFromFragment,
                            null,
                            null
                        )
                    }
                })
            }
        }
    }
    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }
}