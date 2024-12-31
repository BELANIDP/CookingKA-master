package android.presenter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.whirlpool.cooking.ka.databinding.FragmentErrorTypeABinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import core.utils.AppConstants.EMPTY_STRING
import core.utils.BundleKeys
import core.utils.DoorEventUtils
import core.utils.HMIExpansionUtils
import core.utils.faultcodesutils.FaultDetails

/**
 * File       : android.presenter.fragments.FaultErrorTypeAFragment
 * Brief      : This class provides the Type A Fault error
 * Author     : Vishal
 * Created On : 30-04-2024
 */
class FaultErrorTypeAFragment : Fragment(),
    DoorEventUtils.DoorEventListener, HMIExpansionUtils.HMICancelButtonInteractionListener {
    var title: String? = EMPTY_STRING
    var description: String? = EMPTY_STRING
    private var faultCode: String = EMPTY_STRING
    private lateinit var faultDetails:FaultDetails

    lateinit var binding: FragmentErrorTypeABinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErrorTypeABinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMICancelButtonInteractionListener(this)
        DoorEventUtils.setDoorEventListener(this)
        initBundle()
        faultDetails = FaultDetails.getInstance(faultCode)
        manageViews()
    }

    private fun initBundle() {
        faultCode = arguments?.getString(BundleKeys.BUNDLE_FAULT_CODE) ?: EMPTY_STRING
    }

    /**
     * Method for handling the Title and Description of Error of Type A .
     */
    private fun manageViews() {
        binding.tvErrorTypeATitle.text = faultDetails.getFaultName(this)
        binding.tvErrorTypeADescription.text = faultDetails.getFaultInstructions(this)
    }

    override fun onDoorEvent(
        cookingViewModel: CookingViewModel?,
        isDoorOpen: Boolean,
        ovenType: Int
    ) {
        // Do Nothing
    }

    override fun onHMICancelButtonInteraction() {
        // Do Nothing
    }
    /**
     * update the fault code and fault category text
     */
    fun updateFaultValues(bundle: Bundle?) {
        if(bundle!=null) {
            faultCode = bundle.getString(BundleKeys.BUNDLE_FAULT_CODE) ?: EMPTY_STRING
            faultDetails = FaultDetails.getInstance(faultCode)
            manageViews()
        }
    }

}

