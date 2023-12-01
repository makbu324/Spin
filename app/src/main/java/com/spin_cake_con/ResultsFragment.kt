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

        val youTubePlayerView = view.findViewById<YouTubePlayerView>(R.id.youtube_player_view)

        Thread.sleep(2000)
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = viewModel.youtube_id
                youTubePlayer.loadVideo(videoId, 0f)
            }
        })

        //Dialog for showing album
        val child: View = layoutInflater.inflate(R.layout.suggested_album_dialog, null)
        val img: ImageView = child.findViewById(R.id.possible_album)
        if (viewModel.image_album_cover != "") {
            img.getLayoutParams().height = 500
            img.getLayoutParams().width = 500
            img.requestLayout()
            Glide.with(child).load(viewModel.image_album_cover).into(img)


        }
        view.findViewById<EditText>(R.id.hihihi).setText(viewModel.url_thing)

        val builder: AlertDialog.Builder = AlertDialog.Builder(view.context)
        builder
            .setTitle(viewModel.url_thing)
            .setMessage(viewModel.album + " - by " + viewModel.artist)
            .setView(child)
            .setPositiveButton("Positive") { dialog, which ->
                // Do something.
            }
            .setNegativeButton("Negative") { dialog, which ->
                // Do something else.
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()


    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val webView = getCurrentWebView()
        val result = webView.hitTestResult
        val url = result.extra
        var link = url
        var isLink = false
        val filename = URLUtil.guessFileName(url, null, null)

        if ((result.type == HitTestResult.IMAGE_TYPE ||
                    result.type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) &&
            URLUtil.isNetworkUrl(url)
        ) {
            Log.d(TAG, "Long clicked image $url")
            menu.add(0, 1, 0, "R.string.save_image").setOnMenuItemClickListener {
                Log.d(TAG, "Downloading image $filename from $url")
                AdvancedWebView.handleDownload(context, url, filename)
                true
            }

            val message = Handler().obtainMessage()
            webView.requestFocusNodeHref(message)
            val maybeLink = message.data.getString("url")
            maybeLink?.let {
                Log.d(TAG, "Found link on image $maybeLink")
                isLink = true
                link = maybeLink
            }
        }
        if (isLink || (result.type == HitTestResult.SRC_ANCHOR_TYPE && URLUtil.isNetworkUrl(link))) {
            Log.d(TAG, "Long clicked link $link")
            menu.add(0, 2, 0, "R.string.open_external_browser").setOnMenuItemClickListener {
                openBrowser(link)
                true
            }
        }
    }

    fun openBrowser() {
        openBrowser(getCurrentWebView().url)
    }

    fun openBrowser(url: String?) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        startActivity(intent)
    }

    fun getCurrentWebView(): WebView {
        return pagesAdapter.getViewPage(viewPager.currentItem)
    }
}