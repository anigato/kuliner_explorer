package net.anigato.kuliner.controller

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import im.delight.android.location.SimpleLocation
import net.anigato.kuliner.view.activities.MapActivity
import net.anigato.kuliner.view.activities.SplashActivity
import java.io.IOException
import java.util.*

// Controller untuk mendapatkan lokasi pengguna dan nama kota saat ini
class GetLocController : AppCompatActivity() {

    // Array izin yang diperlukan untuk akses lokasi
    var permissionArrays = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    // Objek untuk mengelola pembaruan lokasi pengguna
    lateinit var simpleLocation: SimpleLocation

    // Dialog kemajuan yang ditampilkan saat meminta izin atau memproses lokasi
    lateinit var progressDialog: ProgressDialog

    // Variabel untuk menyimpan nama kota saat ini
    var strCity: String = "" // Inisialisasi strCity dengan string kosong

    // Variabel untuk menyimpan koordinat latitude dan longitude saat ini
    var strCurrentLatitude = 0.0
    var strCurrentLongitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Konfigurasi tampilan status bar untuk tampilan yang lebih baik
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        // Mengatur transparansi status bar pada Android versi 21 (Lollipop) ke atas
        if (Build.VERSION.SDK_INT >= 21) {
            MapActivity.setWindowFlag(
                this,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                false
            )
            window.statusBarColor = Color.TRANSPARENT
        }

        // Memeriksa dan meminta izin jika belum diberikan
        val setPermission = Build.VERSION.SDK_INT
        if (setPermission > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (checkIfAlreadyhavePermission() && checkIfAlreadyhavePermission2()) {
                // Lanjutkan jika izin sudah diberikan
            } else {
                ActivityCompat.requestPermissions(this, permissionArrays, 101)
            }
        }

        // Inisialisasi simpleLocation untuk mendapatkan pembaruan lokasi
        simpleLocation = SimpleLocation(this)
        simpleLocation.beginUpdates()

        // Memeriksa dan memproses status lokasi pengguna
        checkAndProcessLocation()
    }

    // Memeriksa status lokasi pengguna dan menentukan tindakan selanjutnya
    private fun checkAndProcessLocation() {
        if (!simpleLocation.hasLocationEnabled()) {
            // Jika GPS tidak aktif, buka pengaturan lokasi
            openLocationSettings()
        } else {
            // Lanjutkan dengan mendapatkan lokasi dan memulai intent ke SplashActivity
            continueWithLocation()
        }
    }

    // Membuka pengaturan lokasi jika GPS belum aktif
    private fun openLocationSettings() {
        val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    // Melanjutkan dengan lokasi terkini setelah mendapatkan izin dan GPS aktif
    private fun continueWithLocation() {
        // Memperbarui simpleLocation untuk mendapatkan pembaruan lokasi
        simpleLocation = SimpleLocation(this)
        simpleLocation.beginUpdates()

        // Mendapatkan koordinat latitude dan longitude terkini
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude

        // Menggunakan Geocoder untuk mendapatkan nama kota berdasarkan koordinat
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
            if (addressList != null && addressList.size > 0) {
                strCity = addressList[0].subAdminArea ?: ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Logging informasi lokasi untuk keperluan debugging
        Log.d("Current Location", "City: $strCity")
        Log.d("Latitude", "Latitude: $strCurrentLatitude")
        Log.d("Longitude", "Longitude: $strCurrentLongitude")

        // Memulai SplashActivity dengan data nama kota
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra("strCity", strCity)
        startActivity(intent)
        finish() // Menutup aktivitas saat ini agar tidak kembali saat tombol back ditekan
    }

    // Mengakhiri pembaruan lokasi saat aktivitas di-pause
    override fun onPause() {
        super.onPause()
        simpleLocation.endUpdates()
    }

    // Memulai kembali pembaruan lokasi saat aktivitas di-resume
    override fun onResume() {
        super.onResume()
        simpleLocation.beginUpdates()
    }

    companion object {
        const val REQ_PERMISSION = 1
    }

    // Memeriksa izin akses FINE_LOCATION
    private fun checkIfAlreadyhavePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    // Memeriksa izin akses COARSE_LOCATION
    private fun checkIfAlreadyhavePermission2(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }
}
