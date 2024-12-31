package android.presenter.fragments.favorites

import android.framework.services.HMIKnobInteractionListener
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.presenter.adapters.favorites.ImageAdapter
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.viewpager2.widget.ViewPager2
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentChooseImageBinding
import core.utils.AppConstants
import core.utils.FavoriteDataHolder
import core.utils.FavoritesPopUpUtils
import core.utils.HMIExpansionUtils
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils

/**
 * File       : [android.presenter.fragments.favorites.ChooseImageFragment]
 * Brief      :  Screen for Image selection for favorite
 * Author     : PANDES18
 * Created On : 014/10/2024
 * Details    : Screen for Image selection for favorite
 */
class ChooseImageFragment : Fragment(), HMIKnobInteractionListener {

    private var _binding: FragmentChooseImageBinding? = null
    private val binding get() = _binding!!
    private var knobClickCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        val favoriteImageArray = FavoritesPopUpUtils.getDrawableImagesByName(requireContext(),FavoritesPopUpUtils.favoritesLargeImageNames)
        binding.apply {

            viewPager.apply {
                adapter = ImageAdapter(
                    requireContext(),
                    favoriteImageArray
                )
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.stepperImages.setStepperCurrentStep(position + 1)
                    }
                })
                FavoriteDataHolder.selectedImageIndex.let {
                    post { it?.let { it1 -> setCurrentItem(it1, true) } }
                }
            }

            headerBar.apply {
                setInfoIconVisibility(false)
                setRightIconVisibility(false)
                setOvenCavityIconVisibility(false)
                setLeftIconVisibility(true)
                setTitleText(context.getString(R.string.text_header_choose_image))
                setCustomOnClickListener(object :
                    HeaderBarWidgetInterface.CustomClickListenerInterface {
                    override fun leftIconOnClick() {
                        super.leftIconOnClick()
                        FavoritesPopUpUtils.leaveImageSelection(this@ChooseImageFragment){
                            FavoriteDataHolder.selectedImageIndex = null
                            NavigationUtils.navigateSafely(
                                this@ChooseImageFragment,
                                R.id.action_chooseImageFragment_to_favoritesPreviewFragment,
                                null,
                                NavOptions.Builder().setPopUpTo(R.id.favoritesPreviewFragment, true).build()
                            )
                        }
                    }
                })
            }

            stepperImages.apply {
                favoriteImageArray.size
            }

            imageArrowLeft.setOnClickListener {
                viewPager.currentItem -= 1
            }

            imageArrowRight.setOnClickListener {
                viewPager.currentItem += 1
            }

            btnLeft.apply {
                setOnClickListener {
                    navigateOnImageSelection(viewPager)
                }
                background = if (FavoriteDataHolder.isFromKnobClick) {
                    FavoriteDataHolder.isFromKnobClick = false
                    getUnderlineRes()
                }else {null}
            }
        }
    }

    private fun navigateOnImageSelection(viewPager: ViewPager2) {
        FavoriteDataHolder.selectedImageIndex = viewPager.currentItem
        NavigationUtils.navigateSafely(
            this@ChooseImageFragment,
            R.id.action_chooseImageFragment_to_favoritesPreviewFragment,
            null,
            NavOptions.Builder().setPopUpTo(R.id.favoritesPreviewFragment, true).build()
        )
    }

    override fun onHMILeftKnobClick() {
       // do nothing
    }

    override fun onHMILongLeftKnobPress() {
        // do nothing
    }

    override fun onHMIRightKnobClick() {
        knobClickCount++
        when (KnobNavigationUtils.ClickState.values().find { it.count == knobClickCount}) {
            KnobNavigationUtils.ClickState.FIRST -> {
                KnobNavigationUtils.knobForwardTrace = true
                navigateOnImageSelection(binding.viewPager)
                knobClickCount = 0
            }
            else -> {}
        }
    }

    override fun onHMILongRightKnobPress() {
        // do nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        // do nothing
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        // do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if(knobId == AppConstants.RIGHT_KNOB_ID){
            binding.btnLeft.background = getUnderlineRes()

          when (knobDirection) {
                KnobDirection.CLOCK_WISE_DIRECTION -> binding.viewPager.currentItem += 1
                KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> binding.viewPager.currentItem -= 1
                else -> { binding.viewPager.currentItem }
            }
        }
    }

    private fun getUnderlineRes(): Drawable? {
        return ResourcesCompat.getDrawable(
            resources, R.drawable.selector_textview_walnut, null
        )
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if(knobId == AppConstants.RIGHT_KNOB_ID) {
            knobClickCount = 0
            binding.btnLeft.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.text_view_ripple_effect, null
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }
}

