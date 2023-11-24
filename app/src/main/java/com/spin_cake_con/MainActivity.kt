package com.spin_cake_con

import MainViewModel
import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.commit
import com.google.android.material.appbar.AppBarLayout

class MainActivity : AppCompatActivity() {
    private lateinit var appbarLayout: AppBarLayout
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        appbarLayout = findViewById(R.id.appbar_layout)

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

        viewModel.getAppbarTitle().observe(this) {
            title = "SPIN"
        }

        viewModel.getAllowGoBack().observe(this) {
            supportActionBar?.setDisplayHomeAsUpEnabled(it)
            supportActionBar?.setDisplayShowHomeEnabled(it)
        }

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
    }
}