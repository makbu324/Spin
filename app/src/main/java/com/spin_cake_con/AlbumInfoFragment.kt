package com.spin_cake_con

import MainViewModel
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Keep
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager


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

        val album_title = viewModel.album_to_view.title
        val artist_name = viewModel.album_to_view.artist
        val year = viewModel.album_to_view.year

        view.findViewById<TextView>(R.id.album_text).text = album_title
        view.findViewById<TextView>(R.id.artist_text).text = artist_name
        view.findViewById<TextView>(R.id.year_text).text = year
        val imageBytes = Base64.decode(viewModel.album_to_view.base64_album_art, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        view.findViewById<ImageView>(R.id.albumView).setImageBitmap(decodedImage)

        webView = view.findViewById<WebView>(R.id.webView)
        webView.scrollX = 190
        webView.scrollY = 160
        webView.setInitialScale(110)
        webView.loadUrl("https://www.getcdprices.com/search/?upc=" + ("$album_title $artist_name").replace(' ', '+'))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setAllowBackButton(false)
    }

}