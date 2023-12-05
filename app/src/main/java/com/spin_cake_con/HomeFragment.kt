package com.spin_cake_con

import MainViewModel
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var latit = 0.0
    private var longit = 0.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setAppbarTitle("Spin")
        viewModel.setAllowGoBack(false)
        viewModel.setShowSettingsIcon(true)
        viewModel.setShowLinkIcon(false)
        viewModel.fragmentTag = TAG

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val clickWood: MediaPlayer = MediaPlayer.create(view.context, R.raw.vinyl_on)
        val libraryPullup: MediaPlayer = MediaPlayer.create(view.context, R.raw.wishlist_rollin)
        val gearSound: MediaPlayer = MediaPlayer.create(view.context, R.raw.gear_sound)


        view.findViewById<ImageButton>(R.id.camera_button).setOnClickListener {
            if (viewModel.sound_effects_on)
                clickWood.start()
            choosePhotoHelper?.takePhoto()
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }

        view.findViewById<ImageButton>(R.id.wishlist_button).setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_bottom,
                R.anim.exit_to_top,
                R.anim.enter_from_top,
                R.anim.exit_to_bottom
            )
            fragmentTransaction.replace(R.id.nav_host, WishlistFragment(), WishlistFragment.TAG)
            fragmentTransaction.addToBackStack(ResultsFragment.TAG)
            fragmentTransaction.commit()
            if (viewModel.sound_effects_on)
                libraryPullup.start()
        }

        view.findViewById<ImageButton>(R.id.settings_button).setOnClickListener {
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
                R.anim.enter_from_left,
                R.anim.exit_to_right
            )
            fragmentTransaction.replace(R.id.nav_host, SettingsFragment(), SettingsFragment.TAG)
            fragmentTransaction.addToBackStack(SettingsFragment.TAG)
            fragmentTransaction.commit()
            if (viewModel.sound_effects_on)
                gearSound.start()
        }

        choosePhotoHelper = ChoosePhotoHelper.with(this)
            .asFilePath()
            .withState(savedInstanceState)
            .build {
                viewModel.setImageFilePath(it.orEmpty())
            }

        if (viewModel.go_to_camera) {
            viewModel.go_to_camera = false
            if (viewModel.sound_effects_on)
                clickWood.start()
            choosePhotoHelper?.takePhoto()
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
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
        Log.d("MapDebug", "Map is ready")
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(requireContext())
        getCurrentLocation()
    }

    private fun getCurrentLocation(){
        if(checkPermissions() && isLocationEnabled()){
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPerms()
                return
            }
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val location: Location? = task.result
                    if (location != null) {
                        latit = location.latitude
                        longit = location.longitude
                        val curLatlng = LatLng(latit, longit)
                        googleMap.addMarker(MarkerOptions().position(curLatlng).title("Current Location").snippet("Lat: $latit, Long: $longit"))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatlng, 16f))
                    } else {
                        Log.e("LocError", "Error Getting Location: ${task.exception}")
                    }
                } else {

                }
            }
        } else {
            requestPerms()
        }
    }

    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun requestPerms() {
        val requestCode = 101
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                requestCode
            )
        }
    }

    private fun checkPermissions(): Boolean{
        return (ActivityCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission((requireContext()), android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
    }


}