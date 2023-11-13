package net.anigato.kuliner.view.activities

// Impor impor yang diperlukan...
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
import net.anigato.kuliner.viewmodel.MainViewModel
import im.delight.android.location.SimpleLocation
import net.anigato.kuliner.view.activities.load.LoadFoods
import java.io.IOException
import java.util.*

class MapInitiationActivity : AppCompatActivity() {

    var permissionArrays = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    lateinit var simpleLocation: SimpleLocation
    lateinit var progressDialog: ProgressDialog
    var strCity: String = "" // Inisialisasi strCity dengan string kosong
    var strCurrentLatitude = 0.0
    var strCurrentLongitude = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            MapActivity.setWindowFlag(
                this,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                false
            )
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

        // Inisialisasi simpleLocation
        simpleLocation = SimpleLocation(this)
        simpleLocation.beginUpdates()

        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        // Dapatkan lokasi terkini
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude


        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(strCurrentLatitude, strCurrentLongitude, 1)
            if (addressList != null && addressList.size > 0) {
                strCity = addressList[0].subAdminArea
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        Log.d("Cek Lokasi sekarang", "$strCity")

        // Buka MainActivity setelah mendapatkan lokasi terkini
        val intent = Intent(this, SplashActivity::class.java)
        intent.putExtra("strCity",strCity)
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()
        simpleLocation.endUpdates()
    }

    override fun onResume() {
        super.onResume()
        simpleLocation.beginUpdates()
    }

    companion object {
        const val REQ_PERMISSION = 1
    }

    private fun checkIfAlreadyhavePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun checkIfAlreadyhavePermission2(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED
    }
}
