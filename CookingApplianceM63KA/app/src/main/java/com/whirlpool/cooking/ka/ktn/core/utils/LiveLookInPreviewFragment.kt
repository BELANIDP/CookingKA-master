package core.utils

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.utils.LogHelper
import com.whirlpool.hmi.vision.vision2.CameraViewModel

class LiveLookInPreviewFragment: Fragment(),
    View.OnClickListener, SurfaceTextureListener {
    private var mTextureView: TextureView? = null
    private var cameraViewModel: CameraViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_live_look_preview, container, false)

        val buttonCaptureImage = view.findViewById<Button>(R.id.btn_preview_capture_image)
        val backButtonImage = view.findViewById<ImageView>(R.id.backButtonImage)
        mTextureView = view.findViewById(R.id.hmi_preview)
        mTextureView?.surfaceTextureListener = this
        // Get the cameraViewModel instance from the activity
        cameraViewModel = CameraViewModel.getInstance()
        backButtonImage.setImageResource(R.drawable.ic_back_arrow)
        backButtonImage.setOnClickListener(this)
        buttonCaptureImage.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View) {
        if (view.id == R.id.backButtonImage) {
            Navigation.findNavController(view).popBackStack()
        } else if (view.id == R.id.btn_preview_capture_image) {
            cameraViewModel?.captureImage()
        }
    }

    companion object {
        const val TAG: String = "HmiPreviewFragment"
    }

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
        LogHelper.Logd(TAG, "Surface Texture Available: $width, $height")
        transformTexture()
        val surface = Surface(surfaceTexture)
        cameraViewModel?.startHmiLocalPreview(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        LogHelper.Logd(TAG, "Surface Texture resized: $width, $height")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        LogHelper.Logd(TAG, "onSurfaceTextureDestroyed() is called.")
        cameraViewModel?.stopHmiLocalPreview()
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        // do nothing
    }

    private fun transformTexture() {
        val adjustment = Matrix()
        val centerX: Float = mTextureView?.width?.div(2f) ?: 0f
        val centerY: Float = mTextureView?.height?.div(2f) ?: 0f

        val scalex: Float =
            mTextureView?.width?.toFloat()?.div(mTextureView?.height?.toFloat()!!) ?: 0f
        val scaley: Float = mTextureView?.height?.toFloat()?.div(mTextureView?.width?.toFloat()!!) ?: 0f

        adjustment.postRotate(180f, centerX, centerY)

        mTextureView?.setTransform(adjustment)
    }

    override fun onDestroyView() {
        cameraViewModel = null
        super.onDestroyView()
    }
}