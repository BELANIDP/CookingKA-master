/*
 *  *----------------------------------------------------------------------------------------------*
 *  * ---- Copyright 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL --------------*
 *  * ---------------------------------------------------------------------------------------------*
 */
package android.presenter.fragments.favorites


import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentKeyboardBinding
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.KEY_FAVORITE_FROM
import core.utils.AppConstants.KEY_FAVORITE_NAME
import core.utils.FavoriteDataHolder
import core.utils.HMIExpansionUtils
import core.utils.NavigationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * File       : com.whirlpool.cooking.diagnostic.DiagnosticsEditSystemInfoScreen
 * Brief      : AbstractDiagnosticsEditSystemInfoViewProvider instance for Diagnostics Edit System Info Screen
 * Author     : Rajendra
 * Created On : 26-06-2024
 * Details    : This fragment allow to service engineer to edit Model number and serial number
 */
@Suppress("DEPRECATION")
class KeyboardFragment : SuperAbstractTimeoutEnableFragment(), KeyboardInputManagerInterface {
    private lateinit var favoriteName: String
    private lateinit var favoriteFrom: AppConstants.FavoriteFrom
    private var _binding: FragmentKeyboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var keyboardViewModel : KeyboardViewModel
    private val FAVORITE_NAME_MAX_LENGTH = 30


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentKeyboardBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keyboardViewModel = KeyboardViewModel.getKeyboardViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        favoriteName = arguments?.getString(KEY_FAVORITE_NAME).toString()
        favoriteFrom = arguments?.getSerializable(KEY_FAVORITE_FROM) as AppConstants.FavoriteFrom

        keyboardViewModel.initKeyboard(this@KeyboardFragment)

        keyboardViewModel.apply {
            setMaxCharacterLength(FAVORITE_NAME_MAX_LENGTH + 1)

            onTextUpdate().observe(viewLifecycleOwner) { text ->
                with(binding.textHelperText) {
                    val helperText = if (text.length > FAVORITE_NAME_MAX_LENGTH) {
                        getString(R.string.text_name_favorite_helper_text_error)
                    } else {
                        if (text.isEmpty()) {
                            binding.keyboard.setShifted(true)
                            getString(R.string.text_name_favorite_helper_text)
                        } else {
                            if (binding.keyboard.isShifted)binding.keyboard.setShifted(false)
                            "${text.length}/$FAVORITE_NAME_MAX_LENGTH"
                        }
                    }
                    val textColor = if (text.length > FAVORITE_NAME_MAX_LENGTH) {
                        R.color.notification_red
                    } else {
                        R.color.light_grey
                    }
                    setTextColor(ContextCompat.getColor(context, textColor))
                    setText(helperText)
                }
                binding.headerBar.setTitleText(text.toString())
            }


            onTextInputFinished().observe(viewLifecycleOwner) {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    onTextInputFinished().removeObservers(viewLifecycleOwner)
                    popAndMoveToPreviewScreen()
                }
            }
        }

        binding.apply {
            headerBar.apply {
                setOvenCavityIconVisibility(false)
                setInfoIconVisibility(false)
                setRightIcon(R.drawable.ic_cancel)
                setTitleText(favoriteName)
                setCustomOnClickListener(object :
                    HeaderBarWidgetInterface.CustomClickListenerInterface {
                    override fun leftIconOnClick() {
                        activity?.onBackPressed()
                    }

                    override fun rightIconOnClick() {
                        keyboardViewModel.performBackspace()
                    }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.headerBar.setTitleText(favoriteName)
    }

    private fun popAndMoveToPreviewScreen() {
        lifecycleScope.launch(Dispatchers.Main) {
            FavoriteDataHolder.favoriteName = binding.headerBar.getTitleText()
            NavigationUtils.navigateSafely(
                this@KeyboardFragment,
                R.id.action_keyboardFragment_to_favoritePreviewFragment,
                Bundle().apply { putString(KEY_FAVORITE_NAME, binding.headerBar.getTitleText()) },
                NavOptions.Builder().setPopUpTo(R.id.favoritesPreviewFragment, true).build()
            )
        }
    }

    override fun getKeyboardView(): KeyboardView {
        binding.keyboard.apply {
            keyboardAlphaReference = R.xml.favorite_keyboard_alpha_local
            keyboardSymbolsReference = R.xml.favorite_keyboard_symbols_local
        }
        return binding.keyboard
    }

    override fun onStop() {
        super.onStop()
        binding.keyboard.invalidateAllKeys()
        binding.keyboard.closing()
        keyboardViewModel.value = ""
        keyboardViewModel.onTextInputFinished().removeObservers(this)
        _binding = null
    }
}