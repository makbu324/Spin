package com.spin_cake_con

import MainViewModel
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager


@Keep
class AlbumInfoFragment: Fragment() {
    companion object {
        const val TAG = "AlbumInfoFragment"
    }

    private val viewModel by activityViewModels<MainViewModel>()

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

        view.findViewById<TextView>(R.id.album_text).text = viewModel.album_to_view.title
        view.findViewById<TextView>(R.id.artist_text).text = viewModel.album_to_view.artist
        view.findViewById<TextView>(R.id.year_text).text = viewModel.album_to_view.year
        val imageBytes = Base64.decode(viewModel.album_to_view.base64_album_art, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        view.findViewById<ImageView>(R.id.albumView).setImageBitmap(decodedImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setAllowBackButton(false)
    }

}