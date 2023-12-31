package net.anigato.kuliner.view.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import androidx.recyclerview.widget.LinearLayoutManager
import net.anigato.kuliner.R
import net.anigato.kuliner.data.model.nearby.ModelResults
import net.anigato.kuliner.view.adapter.MainAdapter
import net.anigato.kuliner.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import im.delight.android.location.SimpleLocation
import net.anigato.kuliner.databinding.ActivityMapBinding
import net.anigato.kuliner.databinding.ToolbarBinding
import java.io.IOException
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapBinding
    private lateinit var toolbarBinding: ToolbarBinding

    var title = MainViewModel.title

    var permissionArrays = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    lateinit var mapsView: GoogleMap
    lateinit var simpleLocation: SimpleLocation
    lateinit var progressDialog: ProgressDialog
    lateinit var mainViewModel: MainViewModel
    lateinit var mainAdapter: MainAdapter
    lateinit var strCurrentLocation: String
    var strCurrentLatitude = 0.0
    var strCurrentLongitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbarBinding = ToolbarBinding.bind(toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        val setPermission = Build.VERSION.SDK_INT
        if (setPermission > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkIfAlreadyhavePermission() && checkIfAlreadyhavePermission2()) {
            } else {
                ActivityCompat.requestPermissions(this, permissionArrays, 101)
            }
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu…")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("sedang menampilkan lokasi Kuliner")

        simpleLocation = SimpleLocation(this)
        simpleLocation.beginUpdates()

        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        //get location
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude

        //set location lat long
        strCurrentLocation = "$strCurrentLatitude,$strCurrentLongitude"

        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        mainAdapter = MainAdapter(this)
        binding.rvListLocation.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvListLocation.adapter = mainAdapter
        binding.rvListLocation.setHasFixedSize(true)
    }

    override fun onPause() {
        super.onPause()
        simpleLocation.endUpdates()
    }

    override fun onResume() {
        super.onResume()
        simpleLocation.beginUpdates()
    }

    private fun checkIfAlreadyhavePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkIfAlreadyhavePermission2(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                val intent = intent
                finish()
                startActivity(intent)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapsView = googleMap

        toolbarBinding.tvFoodName.text = title + " disekitarmu"

        //set text location
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
            if (addressList != null && addressList.size > 0) {
                val strCity = addressList[0].subAdminArea
                toolbarBinding.tvCity.text = "Anda di " + strCity
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //viewmodel
        getLocationViewModel()
    }

    //get multiple marker
    private fun getLocationViewModel() {
        mainViewModel = ViewModelProvider(this, NewInstanceFactory()).get(MainViewModel::class.java)
        mainViewModel.setMarkerLocation(strCurrentLocation)
        progressDialog.show()

        mainViewModel.getMarkerLocation().observe(this, { modelResults: ArrayList<ModelResults> ->
            if (modelResults.size != 0) {
                mainAdapter.setLocationAdapter(modelResults)
                //get multiple marker
                getMarker(modelResults)
                progressDialog.dismiss()
            } else {
                Toast.makeText(this, "Oops, tidak bisa mendapatkan lokasi kamu!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getMarker(modelResultsArrayList: ArrayList<ModelResults>) {
        val currentLatLng = LatLng(strCurrentLatitude, strCurrentLongitude)
        mapsView.addMarker(MarkerOptions()
            .position(currentLatLng)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            .title("Current Location"))

        for (i in modelResultsArrayList.indices) {
            //set LatLong from API
            val latLngMarker = LatLng(modelResultsArrayList[i].modelGeometry.modelLocation.lat,
                modelResultsArrayList[i].modelGeometry.modelLocation.lng)

//        click marker for change position recyclerview

            //get LatLong to Marker
            mapsView.addMarker(MarkerOptions()
                .position(latLngMarker)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(modelResultsArrayList[i].name))

            //show Marker
            val latLngResult = LatLng(modelResultsArrayList[0].modelGeometry.modelLocation.lat,
                modelResultsArrayList[0].modelGeometry.modelLocation.lng)

            //set position marker
            mapsView.moveCamera(CameraUpdateFactory.newLatLng(latLngResult))
            mapsView.animateCamera(CameraUpdateFactory
                .newLatLngZoom(LatLng(
                    latLngResult.latitude,
                    latLngResult.longitude), 14f))
            mapsView.uiSettings.setAllGesturesEnabled(true)
            mapsView.uiSettings.isZoomGesturesEnabled = true
        }


        mapsView.setOnMarkerClickListener { marker ->
            // Periksa title marker
            if (marker.title != "Current Location") {
                val markerPosition = marker.position
                // Tambahkan logika untuk marker yang dapat diklik di sini
                var markerSelected = -1
                for (i in modelResultsArrayList.indices) {
                    if (markerPosition.latitude == modelResultsArrayList[i].modelGeometry.modelLocation.lat && markerPosition.longitude == modelResultsArrayList[i].modelGeometry.modelLocation.lng) {
                        markerSelected = i
                    }
                }
                val cameraPosition = CameraPosition.Builder().target(markerPosition).zoom(14f).build()
                mapsView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                mainAdapter.notifyDataSetChanged()
                binding.rvListLocation.smoothScrollToPosition(markerSelected)
                marker.showInfoWindow()
            }
            // Kembalikan true untuk mengizinkan tindakan klik pada marker selain "Current Location"
            marker.title != "Current Location"
        }
    }

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val window = activity.window
            val layoutParams = window.attributes
            if (on) {
                layoutParams.flags = layoutParams.flags or bits
            } else {
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }
        const val REQ_PERMISSION = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_PERMISSION && resultCode == RESULT_OK) {
            getLocationViewModel()
        }
    }

}