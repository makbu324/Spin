package com.spin_cake_con

import MainViewModel
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels


@Keep
class AlbumInfoFragment: Fragment() {
    companion object {
        const val TAG = "AlbumInfoFragment"
    }

    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.album_info_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setAllowBackButton(true)
        viewModel.setAllowHomeButton(false)

        val album_title = viewModel.album_to_view.title
        val artist_name = viewModel.album_to_view.artist
        val year = viewModel.album_to_view.year

        view.findViewById<TextView>(R.id.album_text).text = album_title
        view.findViewById<TextView>(R.id.artist_text).text = artist_name
        view.findViewById<TextView>(R.id.year_text).text = year
        val imageBytes = Base64.decode(viewModel.album_to_view.base64_album_art, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val spotify_play: MediaPlayer = MediaPlayer.create(context, R.raw.play_spotify)
        val trash_album: MediaPlayer = MediaPlayer.create(context, R.raw.trash_album)
        val open_frag: MediaPlayer = MediaPlayer.create(context, R.raw.album_frag_open)
        view.findViewById<ImageView>(R.id.albumView).setImageBitmap(decodedImage)

        webView = view.findViewById<WebView>(R.id.webView)
        webView.scrollY = 340
        webView.setInitialScale(200)
        webView.fitsSystemWindows = true
        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                Thread.sleep(2000)
                view.loadUrl(url)
                return false // then it is not handled by default action
            }
        })
        webView.loadUrl("https://www.google.com/search?tbm=shop&q=" + ("$album_title $artist_name $year vinyl+album").replace(' ', '+'))

        view.findViewById<ImageButton>(R.id.spotify_link).setOnClickListener {
            val url = viewModel.album_to_view.link
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            if (viewModel.sound_effects_on)
                spotify_play.start()
        }
        view.findViewById<ImageButton>(R.id.trash_can).setOnClickListener {
            viewModel.THE_WISHLIST.remove(viewModel.album_to_view)
            parentFragmentManager.popBackStack()
            if (viewModel.sound_effects_on)
                trash_album.start()
        }

        if (viewModel.sound_effects_on)
            open_frag.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setAllowBackButton(false)
    }

}