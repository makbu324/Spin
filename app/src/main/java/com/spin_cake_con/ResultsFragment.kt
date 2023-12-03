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
        var linkButton: Button = view.findViewById(R.id.openLink)
        val play_this: MediaPlayer = MediaPlayer.create(context, R.raw.scan_success)
        val clickWood: MediaPlayer = MediaPlayer.create(view.context, R.raw.vinyl_on)
        play_this.start()


        pagesAdapter = SearchPagesAdapter(this).apply {
            setSearchResults(viewModel.getSearchResults().value!!)
        }

        // open album in spotify button
        linkButton.setOnClickListener {
            val url = viewModel.getSearchResults().value!![3]
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        //Mak: add album to wishlist
        view.findViewById<Button>(R.id.add_to_wishlist).setOnClickListener {
            viewModel.THE_WISHLIST += Album(
                artist = viewModel.searchResults.value!![1],
                title = viewModel.searchResults.value!![0],
                year = viewModel.searchResults.value!![2],
                base64_album_art = viewModel.url_thing,
                id = UUID.randomUUID()
            )
            Snackbar.make(
                view,
                "ADDED TO WISHLIST",
                Snackbar.LENGTH_LONG
            ).show()
        }

        view.findViewById<Button>(R.id.try_again).setOnClickListener {
            viewModel.go_to_camera = true
            parentFragmentManager.popBackStack()
            parentFragmentManager.popBackStack()
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }




        Thread.sleep(2000)



        // display album info
        view.findViewById<MaterialTextView>(R.id.albumNameTextView).text = viewModel.searchResults.value!![0]
        view.findViewById<MaterialTextView>(R.id.artistTextView).text = viewModel.searchResults.value!![1]
        view.findViewById<MaterialTextView>(R.id.yearTextView).text = viewModel.searchResults.value!![2]


        //Mak: display album image (placeholder: image taken by user) -> Chris, pls fix
        val imageBytes = Base64.decode(viewModel.url_thing, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        view.findViewById<ImageView>(R.id.album_image).setImageBitmap(decodedImage)



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