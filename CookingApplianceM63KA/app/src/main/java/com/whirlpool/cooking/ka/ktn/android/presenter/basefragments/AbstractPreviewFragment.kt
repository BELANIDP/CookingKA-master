package android.presenter.basefragments

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.preview.PreviewRecyclerViewInterface
import android.presenter.customviews.widgets.preview.PreviewTileItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentPreviewBinding
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.cookbook.records.FavoriteRecord
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.CAVITY_SELECTION_KNOB_SIZE
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AppConstants.KNOB_COUNTER_ONE
import core.utils.AppConstants.KNOB_COUNTER_TWO
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.FavoriteDataHolder
import core.utils.HMIExpansionUtils
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils

/**
 * File       : com.whirlpool.cooking.ka.ktn.android.presenter.basefragments.AbstractPreviewFragment.
 * Brief      : Base class for showing preview of recipes before starts
 * Author     : Hiren
 * Created On : 05/13/2024
 * Details    : Extends this to show assisted, my creation/favorites, history recipes
 */
abstract class AbstractPreviewFragment : SuperAbstractTimeoutEnableFragment(), HeaderBarWidgetInterface.CustomClickListenerInterface,
    View.OnClickListener, PreviewRecyclerViewInterface.PreviewListItemClickListener,
    HMIKnobInteractionListener {

    /**
     * fragment binding, always make it private DO NOT CHANGE access modifier, have abstract method to provide view id and action and let all child class implement this
     */
    private var fragmentPreviewBinding: FragmentPreviewBinding? = null
    protected lateinit var cookingViewModel: CookingViewModel
    private var isKnobRotated = false
    private var isRightOptionSelected = false
    private var knobRotationCount = 0
    private var isLeftCTAButtonVisible: Boolean = false
    private var isPrimaryCTAButtonVisible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentPreviewBinding = FragmentPreviewBinding.inflate(inflater, container, false)
        return fragmentPreviewBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentPreviewBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        CookingAppUtils.loadCookingGuide(cookingViewModel.recipeExecutionViewModel.recipeName.value ?: "")
        manageChildViews()
        isLeftCTAButtonVisible = fragmentPreviewBinding?.btnLeft?.isVisible == true
        isPrimaryCTAButtonVisible = fragmentPreviewBinding?.btnPrimary?.isVisible == true
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            isRightOptionSelected = true
            isKnobRotated = true
            setLeftButtonBackground(false)
            setRightButtonBackground(true)
        }
        CookingAppUtils.clearOrEraseCookingGuideList()
    }

    /**
     * manage all child view associated with this fragment here like what to show as part of view creation
     */
    private fun manageChildViews() {
        manageButtonActions()
        managePreviewListView()
        manageHeaderBar()
    }

    /**
     * initialize recycler view data
     * @param scrollToPosition scroll to position
     */
    private fun managePreviewListView(scrollToPosition: Int = 0) {
        val tilesData: ArrayList<PreviewTileItem> = provideRecyclerViewTilesData()
        val listItems: ArrayList<Any?> = ArrayList(tilesData)
        val previewRecyclerViewInterface = PreviewRecyclerViewInterface(tilesData, this)
        fragmentPreviewBinding?.recyclerViewPreview?.setupListWithObjects(
            listItems, previewRecyclerViewInterface
        )
        //If the number of tiles more than 4, recycler view should be left aligned.
        if (tilesData.size > 3) {
            fragmentPreviewBinding?.recyclerViewPreview?.scrollToPosition(scrollToPosition)
        }
    }
    /**
     * Method for setting the decision tiles data.
     * To be overridden  by derived class to update the recycler view data based on the use case
     *
     * @return ArrayList<DecisionTileData>
     */
    abstract fun provideRecyclerViewTilesData(): ArrayList<PreviewTileItem>

    /**
     * manage buttons associated with this fragment here like what to show as part of view creation text
     *
     */
    private fun manageButtonActions() {
        fragmentPreviewBinding?.btnLeft?.visibility = if(provideLeftActionButtonVisibility()) View.VISIBLE else View.GONE
        fragmentPreviewBinding?.constraintPreviewLeft?.visibility = if(provideLeftActionButtonVisibility()) View.VISIBLE else View.GONE
        fragmentPreviewBinding?.btnLeft?.text = provideLeftActionButtonText()
        fragmentPreviewBinding?.btnLeft?.setOnClickListener(this)
        fragmentPreviewBinding?.constraintPreviewLeft?.setOnClickListener(this)
        fragmentPreviewBinding?.btnPrimary?.visibility = View.VISIBLE
        fragmentPreviewBinding?.btnPrimary?.text = providePrimaryActionButtonText()
        fragmentPreviewBinding?.btnPrimary?.setOnClickListener(this)
        fragmentPreviewBinding?.constraintPreviewRight?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id){
            fragmentPreviewBinding?.btnLeft?.id ->{
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                provideLeftActionButtonClickEvent()
            }
            fragmentPreviewBinding?.constraintPreviewLeft?.id ->{
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                provideLeftActionButtonClickEvent()
            }
            fragmentPreviewBinding?.btnPrimary?.id ->{
                val buttonText = fragmentPreviewBinding?.btnPrimary?.text
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    if(buttonText?.equals(getString(R.string.text_button_next)) == true){
                        R.raw.button_press
                    }else{
                        R.raw.start_press
                    },
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                providePrimaryActionButtonClickEvent()
            }
            fragmentPreviewBinding?.constraintPreviewRight?.id ->{
                val buttonText = fragmentPreviewBinding?.btnPrimary?.text
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    if(buttonText?.equals(getString(R.string.text_button_next)) == true){
                        R.raw.button_press
                    }else{
                        R.raw.start_press
                    },
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                providePrimaryActionButtonClickEvent()
            }
        }
    }

    /**
     * Handle click event when left button is pressed for ex. delay if visibility is false then do nothing
     */
    abstract fun providePrimaryActionButtonClickEvent()

    /**
     * Handle click event when right button is pressed for ex. start or next
     */
    abstract fun provideLeftActionButtonClickEvent()

    /**
     * provide the txt to display on button left
     * @return text to show on left action button like delay, cancel
     */
    abstract fun provideLeftActionButtonText(): CharSequence

    /**
     * provide the txt to display on button right/primary
     * @return text to show on right action button like start
     */
    abstract fun providePrimaryActionButtonText(): CharSequence

    /**
     * manage header bar related events here
     */
    private fun manageHeaderBar() {
        fragmentPreviewBinding?.headerBar?.setInfoIconVisibility(provideHeaderBarInfoIconVisibility())
        provideInfoIconRes()?.let { fragmentPreviewBinding?.headerBar?.setInfoIcon(it) }
        fragmentPreviewBinding?.headerBar?.setRightIconVisibility(false)
        fragmentPreviewBinding?.headerBar?.setLeftIconVisibility(true)
        fragmentPreviewBinding?.headerBar?.setTitleText(provideHeaderBarTitleText())
        val productVariant = CookingViewModelFactory.getProductVariantEnum()
        if(provideHeaderBarLeftIconVisibility() && productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO || productVariant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN){
            fragmentPreviewBinding?.headerBar?.setOvenCavityIcon(if(cookingViewModel.isPrimaryCavity) R.drawable.ic_oven_cavity_large else R.drawable.ic_lower_cavity_large)
        }else{
            fragmentPreviewBinding?.headerBar?.setOvenCavityIconVisibility(false)
        }
        fragmentPreviewBinding?.headerBar?.setCustomOnClickListener(this)
    }

    /**
     * On click action when back icon click happens on header bar
     */
    override fun leftIconOnClick() {
        if (CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.favoriteName.value.toString().isEmpty()) {
            NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    NavigationUtils.getViewSafely(
                        this
                    ) ?: requireView()
                )
            )
        } else {
            FavoriteDataHolder.favoriteName = EMPTY_STRING
            FavoriteDataHolder.favoriteRecord = FavoriteRecord()
            NavigationUtils.navigateSafely(
                this,
                R.id.action_favoritesPreviewFragment_to_favoriteLandingFragment,
                null,
                null
            )
        }
    }

    /**
     * provide text to show on the header bar title
     * @return string to show as title
     */
    abstract fun provideHeaderBarTitleText(): String
    /**
     * provide text to show on the header bar title
     * @return string to show as title
     */
    abstract fun provideHeaderBarLeftIconVisibility() : Boolean

    /**
     * provide visibility of info icon
     * @return true if VISIBLE false if GONE
     */
    abstract fun provideHeaderBarInfoIconVisibility(): Boolean
    /**
     * provide visibility of left action button, ex delay not applicable to mwo cycles
     * @return true if VISIBLE false if GONE
     */
    abstract fun provideLeftActionButtonVisibility(): Boolean

    /**
     * set info icon resource
     */
    abstract fun provideInfoIconRes(): Int?

    private fun setUpViewModels() {
        cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
    }

    override fun onResume() {
        super.onResume()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
    }

    override fun onPause() {
        super.onPause()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }

    private fun setRightButtonBackground(isKnobSelected: Boolean) {
        if (isKnobSelected)
            fragmentPreviewBinding?.btnPrimary?.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.selector_textview_walnut, null
                )
            }
        else
            fragmentPreviewBinding?.btnPrimary?.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.text_view_ripple_effect, null
                )
            }
    }

    private fun setLeftButtonBackground(isKnobSelected: Boolean) {
        if (isKnobSelected)
            fragmentPreviewBinding?.btnLeft?.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.selector_textview_walnut, null
                )
            }
        else
            fragmentPreviewBinding?.btnLeft?.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.text_view_ripple_effect, null
                )
            }
    }

    /************************************ Knob related Methods Only **************************************/
    override fun onHMILeftKnobClick() {

    }

    override fun onHMILongLeftKnobPress() {

    }

    override fun onHMIRightKnobClick() {
        KnobNavigationUtils.knobForwardTrace = true
        if (isKnobRotated && isRightOptionSelected && isPrimaryCTAButtonVisible) {
            providePrimaryActionButtonClickEvent()
        } else if (isKnobRotated && !isRightOptionSelected && isLeftCTAButtonVisible) {
            provideLeftActionButtonClickEvent()
        }
    }

    override fun onHMILongRightKnobPress() {

    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            if (isPrimaryCTAButtonVisible && isLeftCTAButtonVisible) {
                if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < CAVITY_SELECTION_KNOB_SIZE) knobRotationCount++
                else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > 0) knobRotationCount--
            }
            isKnobRotated = true
            when {
                isPrimaryCTAButtonVisible && isLeftCTAButtonVisible -> {
                    handleButtonSelectionForBothCTAVisible()
                }
                isPrimaryCTAButtonVisible -> {
                    isRightOptionSelected = true
                    setRightButtonBackground(true)
                }
                isLeftCTAButtonVisible -> {
                    isRightOptionSelected = false
                    setRightButtonBackground(false)
                }
            }
        }
    }

    private fun handleButtonSelectionForBothCTAVisible(){
        when (knobRotationCount) {
            KNOB_COUNTER_ONE -> {
                isRightOptionSelected = false
                setRightButtonBackground(false)
                setLeftButtonBackground(true)
            }
            KNOB_COUNTER_TWO -> {
                isRightOptionSelected = true
                setRightButtonBackground(true)
                setLeftButtonBackground(false)
            }
        }
    }
    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            isKnobRotated = false
            isRightOptionSelected = false
            setRightButtonBackground(false)
            setLeftButtonBackground(false)
        }
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}