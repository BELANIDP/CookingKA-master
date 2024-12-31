package com.whirlpool.cooking;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.whirlpool.cooking.databinding.FragmentSplashScreenBinding;
import com.whirlpool.cooking.utils.CookingAppUtils;
import com.whirlpool.cooking.utils.EspressoIdlingResource;
import com.whirlpool.cooking.utils.SettingsUtils;
import com.whirlpool.cooking.utils.logging.HMILogHelper;
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory;
import com.whirlpool.hmi.settings.SettingsViewModel;
import com.whirlpool.hmi.uicomponents.fragments.AbstractSplashFragment;
import com.whirlpool.hmi.uicomponents.tools.util.DisplayUtils;

import org.jetbrains.annotations.NotNull;


public class SplashFragment extends  AbstractSplashFragment {
    private FragmentSplashScreenBinding fragmentSplashScreenBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSplashScreenBinding = FragmentSplashScreenBinding.inflate(inflater);
        fragmentSplashScreenBinding.setLifecycleOwner(this);
        prepareVideoView();
        return fragmentSplashScreenBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentSplashScreenBinding = null;
    }

    private void prepareVideoView() {
        fragmentSplashScreenBinding.videoView.setVideoURI(Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + R.raw.wp_logo_video));
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentSplashScreenBinding.videoView.start();
        //ToDo:// Is there a better way to play loop, In kotlin there is a isloop parameter in video view
        fragmentSplashScreenBinding.videoView.setOnCompletionListener (mediaPlayer -> fragmentSplashScreenBinding.videoView.start());
    }

    @Override
    protected void setupApplication() {

    }

    /**
     * Method to release the media player during fragment callbacks
     */
    private void releasePlayer() {
        fragmentSplashScreenBinding.videoView.stopPlayback();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HMILogHelper.Logd("Started listening to SDK ready event - isReady()");
        // Register Idling Resource to wait for the simulator/ACU to respond with the model running
        if(BuildConfig.DEBUG) {
            HMILogHelper.Logd("Idling +++ ");
            EspressoIdlingResource.getInstance().increment();
        }
        CookingViewModelFactory.isReady().observe(this, isReady -> {
            // Start only when cooking is ready
            if (isReady) {
                HMILogHelper.Logd("Selected Product Variant is: " + CookingViewModelFactory.getProductVariantEnum());
                if(BuildConfig.DEBUG) {
                    HMILogHelper.Logd("Idling --- ");
                    EspressoIdlingResource.getInstance().decrement();
                }
                DisplayUtils.updateTheme(requireContext(), requireActivity(), CookingAppUtils.getCurrentTheme(SettingsViewModel.getSettingsViewModel()));
                releasePlayer();
            }
        });
    }


    @Override
    public void onDestroy() {
        CookingViewModelFactory.isReady().removeObservers(this);
        super.onDestroy();
    }
}
