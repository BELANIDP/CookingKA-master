@file:Suppress("unused")

package android.presenter.fragments.self_clean

import android.annotation.SuppressLint
import android.graphics.ColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentDoorLockLoaderBinding
import com.whirlpool.hmi.cooking.utils.Constants
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants
import core.utils.CommonAnimationUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * File       : android.presenter.fragments.self_clean.DoorLockingIntermediateLoader
 * Brief      : This class is used for checking Door lock,
 * Author     : PATELJ7
 * Created On : 4-March-2024
 */
open class DoorLockingIntermediateLoader : Fragment(),
    HMIExpansionUtils.HMICancelButtonInteractionListener, MeatProbeUtils.MeatProbeListener {
    private var fragmentDoorLockLoaderBinding: FragmentDoorLockLoaderBinding? = null
    private var productVariant: CookingViewModelFactory.ProductVariantEnum? = null
    private var primaryCavityViewModel: CookingViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDoorLockLoaderBinding = FragmentDoorLockLoaderBinding.inflate(inflater)
        fragmentDoorLockLoaderBinding?.lifecycleOwner = this
        fragmentDoorLockLoaderBinding?.doorLockingIntermediateLoader = this
        HMIExpansionUtils.setHMICancelButtonInteractionListener(this)
        return fragmentDoorLockLoaderBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_DURING_DOOR_LOCK)
        MeatProbeUtils.setMeatProbeListener(this)
        setUpViewModels()
        manageChildViews()
        changeProgressBarProgressColor()
    }

    private fun changeProgressBarProgressColor() {
//        fragmentDoorLockLoaderBinding?.progressBarUpdateImage?.changeLayersColor(R.color.black_walnut)
    }

    /**
     * Method to set progress bar color
     */
    fun LottieAnimationView.changeLayersColor(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(context, colorRes)
        val filter = SimpleColorFilter(color)
        val keyPath = KeyPath("**")
        val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)

        addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
    }

    /**
     * Method to setup view models
     */
    protected fun setUpViewModels() {
        observeLiveDoorLockState()
    }

    /**
     * Method for setting the view properties and data.
     */
    protected fun manageChildViews() {
        manageUpdateProgressBar()
        lockStatusImageView(false, fragmentDoorLockLoaderBinding?.lockImage)
        setOvenLockingString()
    }

    /**
     * Method to set the software update progress bar
     */
    @SuppressLint("Loop")
    private fun manageUpdateProgressBar() {
        CommonAnimationUtils.playLottieAnimation(
            fragmentDoorLockLoaderBinding?.progressBarUpdateImage,
            R.raw.loop_progress_bar_single_amber
        )
    }

    /**
     * Method to observe live data in child classes.
     */
    @Suppress("KotlinConstantConditions")
    private fun observeLiveDoorLockState() {
        CookingViewModelFactory.getInScopeViewModel().doorLockState.observe(
            this.viewLifecycleOwner
        ) { doorLockState: Boolean ->
            //when door is door is locked set lock animation and progress bar
            lockStatusImageView(doorLockState, fragmentDoorLockLoaderBinding?.lockImage)
            if (doorLockState) {
                //Once cooling completed and unlocks the door , which should take us to
                // the self clean complete popup
                this.viewLifecycleOwner.let {
                    CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                        .recipeExecutionState.observe(
                            this.viewLifecycleOwner
                        ) { pyroExecutionState: RecipeExecutionState ->
                            HMILogHelper.Logi("pyroExecutionState :$pyroExecutionState")
                            when (pyroExecutionState) {
                                RecipeExecutionState.DELAYED -> {
                                    CookingViewModelFactory.getInScopeViewModel().doorState.observe(
                                        this.viewLifecycleOwner
                                    ) {
                                        if (it) {
                                            CookingViewModelFactory.getInScopeViewModel()
                                                .recipeExecutionViewModel
                                                .cancel()
                                            PopUpBuilderUtils.doorLockErrorPopupBuilder(this)
                                            HMILogHelper.Logi("pyroExecutionState doorState:" + CookingViewModelFactory.getInScopeViewModel().doorState.value.toString())
                                        } else {
                                            this.lifecycleScope.launch {
                                                delay(1000)
                                                navigateSafely(
                                                    this@DoorLockingIntermediateLoader,
                                                    R.id.action_goToSelfCleanStatus,
                                                    null,
                                                    null
                                                )
                                            }
                                        }
                                    }

                                }
                                RecipeExecutionState.CANCELLED_EXT, RecipeExecutionState.IDLE -> {

                                    //If Door fails to lock, then SDK would give CANCELLED_EXT, in normal case we should dismiss and navigate as usual
                                    HMILogHelper.Logi("Pyro door lock failed calling cancel######## $doorLockState")
                                    HMILogHelper.Logi("pyroExecutionState########## $pyroExecutionState")
                                    CookingViewModelFactory.getInScopeViewModel()
                                        .recipeExecutionViewModel
                                        .cancel()
                                    PopUpBuilderUtils.doorLockErrorPopupBuilder(this@DoorLockingIntermediateLoader)
                                }
                                else -> {
                                    this.lifecycleScope.launch {
                                        delay(1000)
                                        navigateSafely(
                                            this@DoorLockingIntermediateLoader,
                                            R.id.action_goToSelfCleanStatus,
                                            null,
                                            null
                                        )
                                    }
                                }
                            }
                        }
                }
            } else {
                HMILogHelper.Logi("pyroExecutionState lock state:$doorLockState")
            }
        }
    }

    /**
     * Method to set lockImageView animation
     */
    private fun lockStatusImageView(
        doorLockState: Boolean,
        locKAnimationView: LottieAnimationView?,
    ) {
        fragmentDoorLockLoaderBinding?.progressBarUpdateImage?.isVisible = true
        locKAnimationView?.setAnimation(R.raw.lottie_view_lock_animaton)
        if (doorLockState) {
            locKAnimationView?.apply {
                this.playAnimation()
                stopProgressBarAnimationAndChangeColor(
                    R.drawable.progress_round_white_background,
                    View.INVISIBLE
                )
            }
        } else {
            locKAnimationView?.apply {
                this.pauseAnimation()
            }
            stopProgressBarAnimationAndChangeColor(
                R.drawable.progress_round_grey_background,
                View.VISIBLE
            )
        }
    }

    /**
     * Method to stop progressBar anim and change color
     */
    private fun stopProgressBarAnimationAndChangeColor(
        progressBarBackground: Int,
        visibility: Int,
    ) {
        fragmentDoorLockLoaderBinding?.progressBarUpdateImage?.visibility = visibility
        fragmentDoorLockLoaderBinding?.ivLockProgressbarInfinite?.setBackgroundResource(
            progressBarBackground
        )
    }

    override fun onHMICancelButtonInteraction() {
        HMIExpansionUtils.cancelCycleAndNavigateToClock()
    }

    override fun onDestroyView() {
        MeatProbeUtils.removeMeatProbeListener()
        HMIExpansionUtils.removeHMICancelButtonInteractionListener(this)
        super.onDestroyView()
    }


    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
    }

    private fun setOvenLockingString() {
        val productVariant = CookingViewModelFactory.getProductVariantEnum()
        val ovenLockingString: String

        when (productVariant) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                ovenLockingString = getString(R.string.text_locking_upper_oven_self_clean, getString(R.string.cavity_selection_oven))
            }
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                val primaryCavityViewModel = CookingViewModelFactory.getInScopeViewModel()
                ovenLockingString = if (primaryCavityViewModel?.cavityName?.value.toString() == Constants.PRIMARY_CAVITY_KEY) {
                    getString(R.string.text_locking_upper_oven_self_clean, getString(R.string.cavity_selection_upper_oven))
                } else {
                    getString(R.string.text_locking_upper_oven_self_clean, getString(R.string.cavity_selection_lower_oven))
                }
            }
            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                ovenLockingString = getString(R.string.text_locking_upper_oven_self_clean, getString(R.string.cavity_selection_lower_oven) )
            }
            else -> {
                ovenLockingString = getString(R.string.text_locking_upper_oven_self_clean, getString(R.string.cavity_selection_oven))
            }
        }

        fragmentDoorLockLoaderBinding?.titleText?.text = ovenLockingString
    }
}
