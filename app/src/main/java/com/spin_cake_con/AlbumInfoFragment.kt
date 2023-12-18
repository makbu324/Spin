package com.spin_cake_con

import MainViewModel
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

// Mak - wishlist info fragment. This appears when you click on an album
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


        // Google shopping link embedded
        webView = view.findViewById<WebView>(R.id.webView)
        webView.scrollY = 340
        webView.setInitialScale(200)
        webView.fitsSystemWindows = true
        webView.settings.javaScriptEnabled = true
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

        // Click on album to view info
        view.findViewById<ImageButton>(R.id.spotify_link).setOnClickListener {
            val url = viewModel.album_to_view.link
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
            if (viewModel.sound_effects_on)
                spotify_play.start()
        }

        // Remove wishlist item
        view.findViewById<ImageButton>(R.id.trash_can).setOnClickListener {
            viewModel.THE_WISHLIST.remove(viewModel.album_to_view)
            parentFragmentManager.popBackStack()
            if (viewModel.sound_effects_on)
                trash_album.start()
        }

        // Go back
        view.findViewById<ImageButton>(R.id.prev_page).setOnClickListener {
            webView.goBack()
        }

        view.findViewById<ImageButton>(R.id.edit_button).setOnClickListener{
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            fragmentTransaction.replace(R.id.nav_host, NotelistFragment(), NotelistFragment.TAG)
            fragmentTransaction.addToBackStack(NotelistFragment.TAG)
            fragmentTransaction.commit()
        }

        //Bar chart
        var barArrayList: ArrayList<BarEntry> = arrayListOf()
        var labels = mutableListOf<String>()
        var x_setter = -1f
        for (local_price in viewModel.album_to_view.list_of_prices) {
            x_setter += 1f
            labels.add(local_price.first)
            barArrayList.add(BarEntry(x_setter, local_price.second.toFloat()))
        }
        val barChart = view.findViewById<BarChart>(R.id.bar_chart)
        val barDataSet = BarDataSet(barArrayList, "$$$ Prices")
        val barData = BarData(barDataSet)
        barChart.setData(barData)
        barChart.description.text = "$  "
        barChart.description.textColor = Color.GRAY
        barChart.description.textSize = 25.5f
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.labelCount = labels.size
        barChart.getAxisLeft().setDrawLabels(false)
        barChart.getAxisRight().setDrawLabels(false)
        barChart.xAxis.textColor = Color.WHITE
        barDataSet.setValueTextColor(Color.GRAY)
        barDataSet.setColors(ColorTemplate.createColors(ColorTemplate.COLORFUL_COLORS))
        barDataSet.valueTextSize = 16f
        //***********

        if (viewModel.sound_effects_on)
            open_frag.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}