package android.presenter.fragments.favorites

import android.framework.services.HMIKnobInteractionListener
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.presenter.adapters.favorites.SmallImageAdapter
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentChooseSmallImageBinding
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.FavoriteDataHolder
import core.utils.FavoritesPopUpUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import kotlinx.coroutines.launch

/**
 * File       : [android.presenter.fragments.favorites.ChooseSmallImageFragment]
 * Brief      :  Screen for Image selection for favorite
 * Author     : PANDES18
 * Created On : 014/10/2024
 * Details    : Screen for Image selection for favorite
 */
class ChooseSmallImageFragment : Fragment(), HMIKnobInteractionListener {
    private var _binding: FragmentChooseSmallImageBinding? = null
    private val binding get() = _binding!!
    private var currentPosition = -1
    private var lastItemSelectedPos = -1
    private lateinit var imagesListSet: List<Drawable?>
    private lateinit var smallImageAdapter: SmallImageAdapter
    private val TAG = "ChooseSmallImageFragment "

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseSmallImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        // Set the layout manager programmatically to horizontal.
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.listImages.layoutManager = layoutManager
        imagesListSet = FavoritesPopUpUtils.getDrawableImagesByName(requireContext(),FavoritesPopUpUtils.favoritesSquareImageNames)
        binding.apply {
            listImages.apply {
                smallImageAdapter = SmallImageAdapter(
                    requireContext(),
                    imagesListSet
                ) {
                    it.let {
                        FavoriteDataHolder.selectedImageIndex = it
                        NavigationUtils.navigateSafely(
                            this@ChooseSmallImageFragment,
                            R.id.action_chooseSmallImageFragment_to_chooseImageFragment,
                            null,
                            null
                        )
                    }
                }
                adapter = smallImageAdapter
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
                        activity?.onBackPressed()
                    }
                })
            }
        }
        setKnobScroll()
    }

    private fun setKnobScroll() {
        //Knob scroll set up
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highlightNewSelection(lastItemSelectedPos)
        }
        if (KnobNavigationUtils.isBackPress()) {
            KnobNavigationUtils.removeLastAction()
        }
    }

    private fun highlightNewSelection(position: Int) {
        smallImageAdapter.setScrolledItemPosition(position)
    }

    override fun onHMILeftKnobClick() {
        // do nothing
    }

    override fun onHMILongLeftKnobPress() {
        // do nothing
    }

    override fun onHMIRightKnobClick() {
        FavoriteDataHolder.isFromKnobClick = true
        val imagesItemCount = imagesListSet.size
        if (lastItemSelectedPos in 0 until imagesItemCount) {
            smallImageAdapter.clickSelectedItem(lastItemSelectedPos)
        } else {
            HMILogHelper.Logd(
                TAG,
                "Invalid position: $lastItemSelectedPos (List size: $imagesItemCount)"
            )
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (knobId == AppConstants.RIGHT_KNOB_ID) {
                    val imagesItemCount = binding.listImages.adapter?.itemCount ?:0
                    HMILogHelper.Logd(TAG,"images item count $imagesItemCount")
                    val newPosition = imagesItemCount.let {
                        CookingAppUtils.getKnobPositionIndex(
                            knobDirection, currentPosition, it
                        )
                    }
                    if (newPosition in 0 until imagesItemCount) {
                        currentPosition = newPosition
                        lastItemSelectedPos = currentPosition
                        binding.listImages.smoothScrollToPosition(lastItemSelectedPos)
                        HMILogHelper.Logd(TAG,"lastItemSelectedPos $lastItemSelectedPos")
                        smallImageAdapter.setScrolledItemPosition(lastItemSelectedPos)
                    }
                }
            }
        }

    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            if (lastItemSelectedPos != -1) {
                smallImageAdapter.knobTimedOut()
            }
            FavoriteDataHolder.isFromKnobClick = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }
}

