package com.spin_cake_con

import MainViewModel
import android.Manifest
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout

class MainActivity : AppCompatActivity() {
    private lateinit var appbarLayout: AppBarLayout
    private lateinit var homeButton: Button
    private lateinit var backButton: Button
    private  lateinit var imageView: ImageView
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appbarLayout = findViewById(R.id.appbar_layout)
        homeButton = findViewById<Button>(R.id.home_button)
        backButton = findViewById<Button>(R.id.back_button)

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val clickHome: MediaPlayer = MediaPlayer.create(this, R.raw.search_wood)
        val shelfBack: MediaPlayer = MediaPlayer.create(this, R.raw.shelf_back)

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }

        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        appbarLayout.setBackgroundResource(R.drawable.app_bar_thing)

        imageView = findViewById(R.id.spin_gif)

        Glide.with(this).load(R.drawable.spin_see).into(imageView)

        viewModel.getAllowGoBack().observe(this) {
            supportActionBar?.setDisplayHomeAsUpEnabled(it)
            supportActionBar?.setDisplayShowHomeEnabled(it)
        }

        viewModel.getAllowHomeButton().observe(this) {
            if (it) {
                homeButton.isVisible = true
                homeButton.isClickable = true
            } else {
                homeButton.isVisible = false
                homeButton.isClickable = false
            }
        }

        viewModel.getAllowBackButton().observe(this) {
            if (it) {
                backButton.isVisible = true
                backButton.isClickable = true
            } else {
                backButton.isVisible = false
                backButton.isClickable = false
            }
        }

        homeButton.setOnClickListener{
            if (viewModel.sound_effects_on)
                clickHome.start()
            supportFragmentManager.popBackStack()
            supportFragmentManager.popBackStack()
        }

        backButton.setOnClickListener{
            if (viewModel.sound_effects_on)
                shelfBack.start()
            supportFragmentManager.popBackStack()
        }

        //...

        viewModel.getImageFilePath().observe(this) {
            if (!it.isNullOrEmpty()) {
                supportFragmentManager.commit {
                    setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                    )
                    replace(R.id.nav_host, EditFragment(), EditFragment.TAG)
                    addToBackStack(EditFragment.TAG)
                }
            }
        }

        viewModel.getSearchResults().observe(this) {
            if (!it.isNullOrEmpty()) {
                supportFragmentManager.commit {
                    setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                    )
                    replace(R.id.nav_host, ResultsFragment(), ResultsFragment.TAG)
                    addToBackStack(ResultsFragment.TAG)
                }
            }
        }


    }


}