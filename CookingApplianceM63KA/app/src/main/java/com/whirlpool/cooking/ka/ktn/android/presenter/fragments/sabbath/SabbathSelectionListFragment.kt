package android.presenter.fragments.sabbath

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.ListViewHolderInterface
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.SabbathListFragmentBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory.setInScopeViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.SabbathUtils
import core.utils.ToolsMenuJsonKeys
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_ITEM_SABBATH_BAKE
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_ITEM_SABBATH_MODE
import kotlinx.coroutines.launch

/**
 * File       : android.presenter.fragments.sabbath.SabbathSelectionListFragment
 * Brief      : This class provides list fragment for Sabbath mode selection
 * Author     : Hiren
 * Created On : 08/15/2024
 */
class SabbathSelectionListFragment : SuperAbstractTimeoutEnableFragment(),
    ListViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIKnobInteractionListener {
    private var fragmentPreferenceBinding: SabbathListFragmentBinding? = null
    private var preferencesListItems: ArrayList<String>? = null
    private var currentPosition = -1
    private var lastItemSelectedPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
        fragmentPreferenceBinding = SabbathListFragmentBinding.inflate(inflater)
        fragmentPreferenceBinding!!.lifecycleOwner = this.viewLifecycleOwner
        return fragmentPreferenceBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        manageChildViews()
        setMeatProbeApplicable(false)
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highlightSelectedItem(lastItemSelectedPos)
        }
    }

    private fun highlightSelectedItem(position: Int) {
        fragmentPreferenceBinding?.preferencesRecyclerList?.post {
            val viewHolderOld =
                fragmentPreferenceBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(
                    position
                )
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }
    }

    private fun manageChildViews() {
        managePreferencesCollectionHeaderBar()
        managePreferencesListRecyclerView()
    }

    private fun managePreferencesCollectionHeaderBar() {
        fragmentPreferenceBinding?.headerBarPreferences?.setLeftIcon(R.drawable.ic_back_arrow)
        fragmentPreferenceBinding?.headerBarPreferences?.setRightIconVisibility(false)
        fragmentPreferenceBinding?.headerBarPreferences?.setTitleText(getString(R.string.text_sabbath))
        fragmentPreferenceBinding?.headerBarPreferences?.setOvenCavityIconVisibility(false)
        fragmentPreferenceBinding?.headerBarPreferences?.setInfoIconVisibility(false)
        fragmentPreferenceBinding?.headerBarPreferences?.setCustomOnClickListener(this)
    }

    private fun managePreferencesListRecyclerView() {
        CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SABBATH_KCF)
            ?.let {
                preferencesListItems = it
            }

        preferencesListItems?.let {
            fragmentPreferenceBinding?.preferencesRecyclerList?.visibility = View.VISIBLE
            val listTileData: java.util.ArrayList<ListTileData> =
                providePreferencesListRecyclerViewTilesData()
            listTileData.let {
                val listItems: ArrayList<Any> = ArrayList(listTileData)
                val toolsListViewInterface =
                    ListViewHolderInterface(
                        listTileData, this
                    )
                fragmentPreferenceBinding?.preferencesRecyclerList?.setupListWithObjects(
                    listItems,
                    toolsListViewInterface
                )
            }
        }
    }

    private fun providePreferencesListRecyclerViewTilesData(): ArrayList<ListTileData> {
        val preferencesListTileData = ArrayList<ListTileData>()

        return preferencesListTileData.also {
            preferencesListItems?.let { listItems ->
                for (listItem in listItems) {
                    val listTileData = ListTileData()
                    listTileData.titleTextVisibility = View.VISIBLE
                    listTileData.headingText = listItem
                    listTileData.subTextVisibility = View.VISIBLE
                    listTileData.rightTextVisibility = View.GONE
                    listTileData.rightIconVisibility = View.VISIBLE
                    listTileData.itemIconVisibility = View.GONE
                    val radioButtonData = ListTileData.RadioButtonData()
                    radioButtonData.visibility = View.GONE
                    listTileData.radioButtonData = radioButtonData
                    val textResId =
                        CookingAppUtils.getResIdFromResName(
                            this.requireContext(),
                            listItem,
                            AppConstants.RESOURCE_TYPE_STRING
                        )
                    listTileData.titleText = getString(textResId)
                    listTileData.rightIconID = R.drawable.ic_rightarrowicon
                    listTileData.listItemDividerViewVisibility = View.VISIBLE
                    listTileData.isPaddingView = false
                    if (listItem == JSON_KEY_TOOLS_MENU_ITEM_SABBATH_MODE) {
                        listTileData.subText = getString(R.string.text_subtitle_sabbath_mode)
                    }else if (listItem == JSON_KEY_TOOLS_MENU_ITEM_SABBATH_BAKE) {
                        listTileData.subText = getString(R.string.text_subtitle_sabbath_bake)
                        listTileData.listItemDividerViewVisibility = View.GONE
                    }
                    if (!TextUtils.isEmpty(listTileData.titleText))
                        it.add(listTileData)
                }
            }
        }
    }

    /**
     * Listener method which is called on List tile click
     *
     * @param view the tile view which is clicked
     * @param position index/position for the tile clicked
     */
    override fun onListViewItemClick(view: View?, position: Int) {
        if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO) {
            setInScopeViewModel(CookingViewModelFactory.getSecondaryCavityViewModel())
        }
        if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN) {
            setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
        }
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        if ((preferencesListItems?.get(position) ?: "") == JSON_KEY_TOOLS_MENU_ITEM_SABBATH_MODE) {
            if(SabbathUtils.isSabbathModeInstructionScreenShown()) {
                SabbathUtils.startSabbathMode(this)
            }else{
                val bundle = Bundle()
                bundle.putString(BundleKeys.RECIPE_NAME, AppConstants.RECIPE_INSTRUCTION_SABBATH_MODE)
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_recipeSelectionFragment_to_sabbathRecipeInstructionFragment,
                    bundle,
                    null
                )
            }
        }
        else if ((preferencesListItems?.get(position) ?: "") == JSON_KEY_TOOLS_MENU_ITEM_SABBATH_BAKE) {
            val (isProbeConnected, connectedCavityViewModel) = MeatProbeUtils.isAnyCavityHasMeatProbeConnected()
            if(isProbeConnected){
                HMILogHelper.Logd(tag, "Sabbath Meat probe is connected for ${connectedCavityViewModel?.cavityName?.value}, not applicable for Sabbath Bake selection and showing probeDetectedBeforeSabbathProgramming popup")
                SabbathUtils.probeDetectedBeforeSabbathProgramming(this, connectedCavityViewModel, {
                    SabbathUtils.navigateToSabbathSettingSelection(this)
                }, {})
                return
            }
            if(SabbathUtils.isSabbathBakeInstructionScreenShown()) {
                if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
                    NavigationUtils.navigateSafely(
                        this,
                        R.id.action_sabbathSelectionListFragment_to_sabbathCavitySelectionFragment,
                        null,
                        null
                    )
                }else {
                    NavigationUtils.navigateAfterSabbathRecipeSelection(this, CookingViewModelFactory.getInScopeViewModel())
                }
            }else {
                val bundle = Bundle()
                bundle.putString(
                    BundleKeys.RECIPE_NAME,
                    AppConstants.RECIPE_INSTRUCTION_SABBATH_BAKE
                )
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_recipeSelectionFragment_to_sabbathRecipeInstructionFragment,
                    bundle,
                    null
                )
            }
        }
    }

    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                getViewSafely(
                    this
                ) ?: requireView()
            )
        )
    }

    override fun rightIconOnClick() {
        //do nothing
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return resources.getInteger(R.integer.session_long_timeout)
    }

    override fun onHMILeftKnobClick() {
        if (lastItemSelectedPos != -1) {
            KnobNavigationUtils.knobForwardTrace = true
            onListViewItemClick(fragmentPreferenceBinding?.preferencesRecyclerList,lastItemSelectedPos)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (lastItemSelectedPos != -1) {
                val viewHolder =
                    fragmentPreferenceBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            currentPosition = -1
        }
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (knobId == AppConstants.LEFT_KNOB_ID) {
                    currentPosition = CookingAppUtils.getKnobPositionIndex(
                        knobDirection,
                        currentPosition,
                        providePreferencesListRecyclerViewTilesData().size
                    )
                    if (currentPosition >= 0) {
                        HMILogHelper.Logd("LEFT_KNOB: rotate right current knob index = $currentPosition")
                        fragmentPreferenceBinding?.preferencesRecyclerList?.smoothScrollToPosition(currentPosition)

                        fragmentPreferenceBinding?.preferencesRecyclerList?.postDelayed({
                            if (lastItemSelectedPos != -1) {
                                val viewHolder = fragmentPreferenceBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
                            }
                            lastItemSelectedPos = currentPosition
                            val viewHolderOld = fragmentPreferenceBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
                        }, 50) // Adjust delay as needed

                    }else{
                        HMILogHelper.Logd("LEFT_KNOB: rotate left current knob index = $currentPosition")
                        currentPosition = 0
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }
}