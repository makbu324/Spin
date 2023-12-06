package com.spin_cake_con

import MainViewModel
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.*
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebView.HitTestResult
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import im.delight.android.webview.AdvancedWebView
import java.util.UUID


@Keep
class ResultsFragment : Fragment() {

    companion object {
        const val TAG = "ResultsFragment"
    }

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setAllowHomeButton(true)
        viewModel.setAppbarTitle("")
        viewModel.setAllowGoBack(true)
        viewModel.setShowSettingsIcon(false)
        viewModel.setShowLinkIcon(true)
        viewModel.fragmentTag = HomeFragment.TAG
        var linkButton: Button = view.findViewById(R.id.openLink)
        val play_this: MediaPlayer = MediaPlayer.create(context, R.raw.scan_success)
        if (viewModel.sound_effects_on)
            play_this.start()

        // open album in spotify button
        linkButton.setOnClickListener {
            val url = viewModel.getSearchResults().value!![3]
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        //Mak: add album to wishlist
        view.findViewById<Button>(R.id.add_to_wishlist).setOnClickListener {
            viewModel.THE_WISHLIST.add(0, Album(
                artist = viewModel.searchResults.value!![1],
                title = viewModel.searchResults.value!![0],
                year = viewModel.searchResults.value!![2],
                base64_album_art = viewModel.spotifyImageEncoded,
                id = UUID.randomUUID(),
                link = viewModel.searchResults.value!![3]
            ))
            Snackbar.make(
                view,
                "ADDED TO WISHLIST",
                Snackbar.LENGTH_LONG
            ).show()
            view.findViewById<Button>(R.id.add_to_wishlist).isClickable = false
        }

        view.findViewById<Button>(R.id.try_again).setOnClickListener {
            viewModel.go_to_camera = true
            parentFragmentManager.popBackStack()
            parentFragmentManager.popBackStack()
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
            view.findViewById<Button>(R.id.try_again).isClickable = false
        }




        Thread.sleep(2000)



        // display album info
        view.findViewById<MaterialTextView>(R.id.albumNameTextView).text = viewModel.searchResults.value!![0]
        view.findViewById<MaterialTextView>(R.id.artistTextView).text = viewModel.searchResults.value!![1]
        view.findViewById<MaterialTextView>(R.id.yearTextView).text = viewModel.searchResults.value!![2]


        //Mak: display album image (placeholder: image taken by user) -> Chris, pls fix
        val imageBytes = Base64.decode(viewModel.spotifyImageEncoded, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        view.findViewById<ImageView>(R.id.album_image).setImageBitmap(decodedImage)



    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setAllowHomeButton(false)
    }
}