package com.spin_cake_con

import MainViewModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.ClipboardManager
import android.util.Log
import android.view.*
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebView.HitTestResult
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import im.delight.android.webview.AdvancedWebView


@Keep
class ResultsFragment : Fragment() {

    companion object {
        const val TAG = "ResultsFragment"
    }

    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var viewPager: ViewPager
    private lateinit var pagesAdapter: SearchPagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setAppbarTitle("")
        viewModel.setAllowGoBack(true)
        viewModel.setShowSettingsIcon(false)
        viewModel.setShowLinkIcon(true)
        viewModel.fragmentTag = HomeFragment.TAG

        pagesAdapter = SearchPagesAdapter(this).apply {
            setSearchResults(viewModel.getSearchResults().value!!)
        }

        viewPager = view.findViewById<ViewPager>(R.id.viewPager).apply {
            adapter = pagesAdapter
            offscreenPageLimit = pagesAdapter.count - 1
        }




    }


}
