package android.presenter.fragments.favorites

import android.presenter.basefragments.BaseConvectSlowRoastFragment
import android.view.View
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.NavigationUtils

/**
 * File       : android.presenter.fragments.favorites.FavoritesConvectSlowRoastSelectionFragment.
 * Brief      : implementation fragment class for Convect Slow Roast cycles grid list for Favorites Feature.
 * Author     : VYASM
 * Created On : 23/10/2024
 * Details    :
 */
class FavoritesConvectSlowRoastSelectionFragment : BaseConvectSlowRoastFragment() {
    override fun viewOnClick(view: View?) {
        val id = view?.id
        if (id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.btnPrimary?.id || id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.constraintPrimaryButton?.id) {
            onClickNextButton()
        }
    }

    override fun setViewByProductVariant() {
        super.setViewByProductVariant()
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(false)
    }
    private fun onClickNextButton() {
        val selectedSlowRoastOption =
            tumblerViewHolderHelper?.provideNumericTumbler()?.selectedIndex?.let {
                slowRoastList[it]
            }
        parentFragment?.let {
            NavigationUtils.navigateAfterFavoriteSelection(
                it,
                CookingViewModelFactory.getInScopeViewModel(),
                selectedSlowRoastOption
            )
        }
    }
}

