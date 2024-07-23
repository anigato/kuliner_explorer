package net.anigato.kuliner.view.activities

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import im.delight.android.location.SimpleLocation
import net.anigato.kuliner.R
import java.io.IOException
import java.util.*

class SplashActivity : AppCompatActivity() {

    private lateinit var simpleLocation: SimpleLocation
    private var strCity: String = ""
    private var strCurrentLatitude = 0.0
    private var strCurrentLongitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Inisialisasi SimpleLocation
        simpleLocation = SimpleLocation(this)

        // Mulai pembaruan lokasi
        simpleLocation.beginUpdates()

        // Periksa dan dapatkan lokasi
        checkLocation()
    }

    private fun checkLocation() {
        // Jika lokasi tidak diaktifkan, buka pengaturan lokasi
        if (!simpleLocation.hasLocationEnabled()) {
            openLocationSettings()
        } else {
            // Jika lokasi aktif, dapatkan lokasi
            getLocation()
        }
    }

    private fun getLocation() {
        // Periksa lokasi setiap detik
        Handler(Looper.getMainLooper()).postDelayed({
            if (simpleLocation.latitude != 0.0 && simpleLocation.longitude != 0.0) {
                strCurrentLatitude = simpleLocation.latitude
                strCurrentLongitude = simpleLocation.longitude
                continueWithLocation()
            } else {
                Log.d("Current Location", "Location not available.")
                // Coba lagi jika lokasi tidak tersedia
                getLocation()
            }
        }, 1000) // Coba lagi setiap detik
    }

    private fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun continueWithLocation() {
        Log.d("Current Location", "Latitude: $strCurrentLatitude")
        Log.d("Current Location", "Longitude: $strCurrentLongitude")

        // Gunakan Geocoder untuk mendapatkan nama kota berdasarkan koordinat
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                strCity = addressList[0].subAdminArea ?: ""
                Log.d("Current Location", "City: $strCity")
            } else {
                Log.d("Current Location", "No address found.")
                strCity = "nama kota ga tau"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Current Location", "Failed to get city name", e)
            strCity = "belum dapat lokasi"
        }

        // Navigasi ke FoodsActivity dengan nama kota sebagai extra
        val intent = Intent(this, FoodsActivity::class.java)
        intent.putExtra("strCity", strCity)
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Mulai pembaruan lokasi saat aktivitas dilanjutkan
        simpleLocation.beginUpdates()
        // Periksa lokasi kembali saat aktivitas dilanjutkan
        checkLocation()
    }

    override fun onPause() {
        super.onPause()
        // Hentikan pembaruan lokasi saat aktivitas dijeda
        simpleLocation.endUpdates()
    }
}
