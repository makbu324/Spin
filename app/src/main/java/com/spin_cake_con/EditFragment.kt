package com.spin_cake_con

import MainViewModel
import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.Keep
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropFragment
import com.yalantis.ucrop.callback.BitmapCropCallback
import com.yalantis.ucrop.view.GestureCropImageView
import com.yalantis.ucrop.view.UCropView
import java.io.File

/**
 * Modified from UCropFragment.
 */
@Keep
class EditFragment : Fragment() {

    companion object {
        const val TAG = "EditFragment"
    }

    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var progressBar: ProgressBar
    private lateinit var uCropView: UCropView
    private lateinit var gestureCropImageView: GestureCropImageView
    private lateinit var blockingView: View
    private lateinit var outputUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.setAppbarTitle("")
        viewModel.setAllowGoBack(true)
        viewModel.setShowSettingsIcon(true)
        viewModel.setShowLinkIcon(false)
        viewModel.setAllowBackButton(true)
        viewModel.setAllowHomeButton(false)
        viewModel.fragmentTag = HomeFragment.TAG

        val searchClick: MediaPlayer = MediaPlayer.create(context, R.raw.wood_click)

        val rootView: View = inflater.inflate(R.layout.fragment_edit_pic, container, false)
        rootView.findViewById<ImageView>(R.id.search_now).setOnClickListener {
            if (viewModel.sound_effects_on)
                searchClick.start()
            cropAndSaveImage()
            progressBar.visibility = View.VISIBLE
            rootView.findViewById<ImageView>(R.id.search_now).isClickable = false
        }
        blockingView = rootView.findViewById(R.id.blocking_view)
        progressBar = rootView.findViewById(R.id.progress_bar)
        uCropView = rootView.findViewById(R.id.ucrop)
        gestureCropImageView = uCropView.cropImageView
        outputUri = File(requireContext().cacheDir, "out").toUri()
        setImageData(Uri.fromFile(File(viewModel.getImageFilePath().value!!)), outputUri)
        uCropView.overlayView.isFreestyleCropEnabled = true
        return rootView
    }

    private fun onCropFinish(result: Int) {
        if (result == Activity.RESULT_OK) {
            viewModel.croppedImgPath = outputUri.path!!
            viewModel.uploadImage()
        } else {
            Toast.makeText(context, "crop error", Toast.LENGTH_SHORT).show()
            activity?.onBackPressed()
        }
    }

    override fun onPause() {
        blockingView.isClickable = false
        progressBar.visibility = View.GONE
        super.onPause()
    }

    private fun setImageData(input: Uri, output: Uri) {
        try {
            gestureCropImageView.setImageUri(input, output)
        } catch (e: Exception) {
            onCropFinish(UCrop.RESULT_ERROR)
        }
    }

    private fun cropAndSaveImage() {
        blockingView.isClickable = true
        gestureCropImageView.cropAndSaveImage(
            UCropFragment.DEFAULT_COMPRESS_FORMAT,
            UCropFragment.DEFAULT_COMPRESS_QUALITY,
            object : BitmapCropCallback {

                override fun onBitmapCropped(uri: Uri, offX: Int, offY: Int, imgW: Int, imgH: Int) {
                    onCropFinish(Activity.RESULT_OK)
                }

                override fun onCropFailure(t: Throwable) {
                    onCropFinish(UCrop.RESULT_ERROR)
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setAllowBackButton(false)
    }
}
