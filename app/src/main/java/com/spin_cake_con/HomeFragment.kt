package com.spin_cake_con

import MainViewModel
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import androidx.annotation.Keep
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Keep
class HomeFragment: Fragment(), OnMapReadyCallback {
    companion object {
        const val TAG = "ChooserFragment"
    }

    private var choosePhotoHelper: ChoosePhotoHelper? = null
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setAppbarTitle("")
        viewModel.setAllowGoBack(false)
        viewModel.setShowSettingsIcon(true)
        viewModel.setShowLinkIcon(false)
        viewModel.fragmentTag = TAG

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        view.findViewById<Button>(R.id.camera_button).setOnClickListener {
            choosePhotoHelper?.takePhoto()
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }

        choosePhotoHelper = ChoosePhotoHelper.with(this)
            .asFilePath()
            .withState(savedInstanceState)
            .build {
                viewModel.setImageFilePath(it.orEmpty())
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        choosePhotoHelper?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(rqCode: Int, perms: Array<String>, grantRes: IntArray) {
        super.onRequestPermissionsResult(rqCode, perms, grantRes)
        choosePhotoHelper?.onRequestPermissionsResult(rqCode, perms, grantRes)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        choosePhotoHelper?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
    override fun onMapReady(map: GoogleMap){
        googleMap = map
        val location = LatLng(37.0, -122.0)
        googleMap.addMarker(MarkerOptions().position(location))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))

    }
}