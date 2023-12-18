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
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.google.android.material.textview.MaterialTextView
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
        viewModel.setAllowBackButton(false)
        viewModel.setAppbarTitle("")
        viewModel.setAllowGoBack(true)
        viewModel.setShowSettingsIcon(false)
        viewModel.setShowLinkIcon(true)
        viewModel.fragmentTag = HomeFragment.TAG
        var linkButton: Button = view.findViewById(R.id.openLink)
        val add_to_wishlist: MediaPlayer = MediaPlayer.create(context, R.raw.add_to_wish_list)
        val wishlist_rolling: MediaPlayer = MediaPlayer.create(context, R.raw.wishlist_rollin)

        // open album in spotify button
        linkButton.setOnClickListener {
            val url = viewModel.getSearchResults().value!![3]
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        //Mak: add album to wishlist
        view.findViewById<Button>(R.id.add_to_wishlist).setOnClickListener {
            viewModel.already_added_album = true
            viewModel.THE_WISHLIST.add(0, Album(
                artist = viewModel.searchResults.value!![1],
                title = viewModel.searchResults.value!![0],
                year = viewModel.searchResults.value!![2],
                base64_album_art = viewModel.spotifyImageEncoded,
                id = UUID.randomUUID(),
                link = viewModel.searchResults.value!![3],
                list_of_prices = mutableListOf<Triple<String, Double, String>>()
            ))

            val snackbar = Snackbar.make(
                view,
                "",
                Snackbar.LENGTH_LONG
            )
            val snackbarLayout = snackbar.view as SnackbarLayout
            val customSnackView: View = layoutInflater.inflate(R.layout.snackbar_custom_view, null)
            snackbarLayout.setPadding(0, 0, 0, 0)
            snackbarLayout.addView(customSnackView, 0);
            snackbar.show();
            val GoToWishlist = customSnackView.findViewById<Button>(R.id.gotoWishlistButton)
            GoToWishlist.setOnClickListener {
                val fragmentTransaction = parentFragmentManager.beginTransaction()
                fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_bottom,
                    R.anim.exit_to_top,
                    R.anim.enter_from_top,
                    R.anim.exit_to_bottom
                )
                fragmentTransaction.replace(R.id.nav_host, WishlistFragment(), WishlistFragment.TAG)
                fragmentTransaction.addToBackStack(WishlistFragment.TAG)
                fragmentTransaction.commit()
                if (viewModel.sound_effects_on)
                    wishlist_rolling.start()
                GoToWishlist.isClickable = false
                viewModel.getAllowHomeButton()
            }

            //only clicked once
            view.findViewById<Button>(R.id.add_to_wishlist).isClickable = false
            if (viewModel.sound_effects_on)
                add_to_wishlist.start()
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


        //Mak/Chris: display album image
        val imageBytes = Base64.decode(viewModel.spotifyImageEncoded, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        view.findViewById<ImageView>(R.id.album_image).setImageBitmap(decodedImage)

        // cant add album again if it's already added to wishlist
        if (viewModel.already_added_album == true)
            view.findViewById<Button>(R.id.add_to_wishlist).isClickable = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}