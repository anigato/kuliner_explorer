package net.anigato.kuliner.view.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import net.anigato.kuliner.R
import net.anigato.kuliner.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import im.delight.android.location.SimpleLocation
import net.anigato.kuliner.databinding.ActivityRuteBinding
import java.util.*
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.google.android.gms.maps.model.*

// Import semua dependensi yang diperlukan

class NavigationNRestoDetailActivity : AppCompatActivity(), OnMapReadyCallback, DirectionCallback {

    // Deklarasi variabel lateinit untuk komponen UI, ViewModel, lokasi, dan informasi restoran
    private lateinit var binding: ActivityRuteBinding
    private lateinit var mapsView: GoogleMap
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mainViewModel: MainViewModel
    private lateinit var simpleLocation: SimpleLocation
    private lateinit var strPlaceId: String
    private lateinit var strNamaLokasi: String
    private lateinit var strNamaJalan: String
    private lateinit var strRating: String
    private lateinit var strPhone: String
    private lateinit var fromLatLng: LatLng
    private lateinit var toLatLng: LatLng
    private lateinit var strCurrentLocation: String
    private var strCurrentLatitude = 0.0
    private var strLatitude = 0.0
    private var strCurrentLongitude = 0.0
    private var strLongitude = 0.0
    private var strOpenHour: List<String> = ArrayList()

    // Default transport mode adalah mobil
    private var activeTransportMode = TransportMode.DRIVING
    private var currentPolyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout menggunakan ViewBinding
        binding = ActivityRuteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Konfigurasi status bar transparan untuk Android M ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        // Set status bar transparan untuk Android Lollipop ke atas
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        // Setup ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu…")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Sedang menampilkan detail rute")

        // Setup ActionBar
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        // Inisialisasi SimpleLocation untuk mendapatkan lokasi saat ini
        simpleLocation = SimpleLocation(this)
        if (!simpleLocation.hasLocationEnabled()) {
            SimpleLocation.openSettings(this)
        }

        // Ambil lokasi saat ini
        strCurrentLatitude = simpleLocation.latitude
        strCurrentLongitude = simpleLocation.longitude
        strCurrentLocation = "$strCurrentLatitude,$strCurrentLongitude"

        // Ambil data intent dari adapter sebelumnya
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            strPlaceId = bundle.getString("placeId", "")
            strLatitude = bundle.getDouble("lat", 0.0)
            strLongitude = bundle.getDouble("lng", 0.0)
            strNamaJalan = bundle.getString("formatted_address", "") ?: ""

            // LatLong origin & destination
            fromLatLng = LatLng(strCurrentLatitude, strCurrentLongitude)
            toLatLng = LatLng(strLatitude, strLongitude)

            // Setup ViewModel untuk detail lokasi
            mainViewModel = ViewModelProvider(this, NewInstanceFactory()).get(MainViewModel::class.java)
            mainViewModel.setDetailLocation(strPlaceId)
            progressDialog.show()

            // Observasi data dari ViewModel untuk detail restoran
            mainViewModel.getDetailLocation().observe(this, { modelResto ->
                // Ambil detail dari model restoran
                strNamaLokasi = modelResto.name
                strPhone = modelResto.formatted_phone_number
                strRating = modelResto.rating.toString()

                // Sembunyikan tampilan nomor telepon jika tidak tersedia
                if (strPhone == "0") {
                    binding.llPhone.visibility = View.GONE
                } else {
                    strPhone = modelResto.formatted_phone_number
                }

                // Tampilkan rating restoran
                binding.ratingBar.numStars = 5
                binding.ratingBar.stepSize = 0.5.toFloat()
                binding.ratingBar.rating = strRating.toFloat()

                // Tampilkan nama dan alamat lokasi restoran
                binding.tvNamaLokasi.text = strNamaLokasi
                binding.tvNamaJalan.text = strNamaJalan

                // Tampilkan pesan jika restoran belum memiliki rating
                if (strRating == "0.0") {
                    binding.tvRating.text = "Tempat belum memiliki rating!"
                    binding.tvRating.setTextColor(Color.RED)
                } else {
                    binding.tvRating.text = strRating
                }

                // Ambil jam operasional restoran
                try {
                    strOpenHour = modelResto.modelOperationalResto.weekdayText
                    val stringBuilder = StringBuilder()
                    val maxLength = strOpenHour.maxOfOrNull { it.split(":")[0].length } ?: 0

                    for (strList in strOpenHour) {
                        val day = strList.split(":")[0]
                        val hours = strList.split(":")[1]
                        stringBuilder.append(String.format("%-${maxLength}s : %s\n", day.trim(), hours.trim()))
                    }

                    binding.tvJamOperasional.text = "Jam Operasional :"
                    binding.tvJamOperasional.setTextColor(Color.BLACK)
                    binding.tvJamBuka.text = stringBuilder.toString()
                    binding.tvJamBuka.setTextColor(Color.BLACK)
                    binding.tvJamBuka.typeface = Typeface.MONOSPACE
                    binding.imageTime.setBackgroundResource(R.drawable.ic_time)
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.tvJamOperasional.text = "Resto Tutup Sementara atau belum mengatur jam operasional"
                    binding.tvJamOperasional.setTextColor(Color.RED)
                    binding.tvJamBuka.visibility = View.GONE
                    binding.imageTime.setBackgroundResource(R.drawable.ic_block)
                }

                // Intent untuk membuka Google Maps dengan tujuan lokasi restoran
                binding.llRoute.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=$strLatitude,$strLongitude"))
                    startActivity(intent)
                }

                // Intent untuk melakukan panggilan telepon ke restoran
                binding.llPhone.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$strPhone"))
                    startActivity(intent)
                }

                // Intent untuk berbagi lokasi restoran
                binding.llShare.setOnClickListener {
                    val strUri = "http://maps.google.com/maps?daddr=$strLatitude,$strLongitude"
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_SUBJECT, strNamaLokasi)
                    intent.putExtra(Intent.EXTRA_TEXT, strUri)
                    startActivity(Intent.createChooser(intent, "Bagikan :"))
                }

                // Setup mode transportasi default (mobil)
                setTransportMode(TransportMode.DRIVING)

                // Listener untuk memilih mode mobil
                binding.llCar.setOnClickListener {
                    setTransportMode(TransportMode.DRIVING)
                }

                // Listener untuk memilih mode berjalan kaki
                binding.llWalking.setOnClickListener {
                    setTransportMode(TransportMode.WALKING)
                }

                progressDialog.dismiss() // Tutup ProgressDialog setelah selesai memuat data
            })
        }

        // Setup fragment Google Maps
        val supportMapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    // Method untuk mengatur mode transportasi (mobil atau berjalan kaki)
    private fun setTransportMode(transportMode: String) {
        activeTransportMode = transportMode

        // Reset semua ikon ke keadaan default (tint background transparan dan padding default)
        binding.imageCar.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent))
        binding.imageWalking.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent))
        binding.imageCar.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
        binding.imageWalking.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))

        // Set padding besar secara langsung (sesuaikan kebutuhan)
        val largePaddingPx = 16

        // Atur berdasarkan mode transportasi yang dipilih
        when (transportMode) {
            TransportMode.DRIVING -> {
                binding.imageCar.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
                binding.imageCar.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
                binding.imageCar.setPadding(largePaddingPx, largePaddingPx, largePaddingPx, largePaddingPx)
            }
            TransportMode.WALKING -> {
                binding.imageWalking.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
                binding.imageWalking.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.white))
                binding.imageWalking.setPadding(largePaddingPx, largePaddingPx, largePaddingPx, largePaddingPx)
            }
        }

        // Memperbarui rute berdasarkan mode transportasi yang dipilih
        showDirection(transportMode)
    }

    // Method untuk menampilkan rute menggunakan Google Direction API
    private fun showDirection(transportMode: String) {
        GoogleDirection.withServerKey("AIzaSyDlEg-GyBBQmy4BmzgrFSk0n-OOI0RpZZA")
            .from(fromLatLng)
            .to(toLatLng)
            .transportMode(transportMode)
            .execute(this)
    }

    // Method dari interface OnMapReadyCallback yang dipanggil saat Google Maps siap digunakan
    override fun onMapReady(googleMap: GoogleMap) {
        mapsView = googleMap
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mapsView.isMyLocationEnabled = true
        mapsView.setPadding(0, 0, 0, 0)
        mapsView.moveCamera(CameraUpdateFactory.newLatLng(fromLatLng))
        mapsView.animateCamera(CameraUpdateFactory.newLatLngZoom(fromLatLng, 15f))
    }

    // Method dari interface DirectionCallback yang dipanggil jika rute berhasil diambil
    override fun onDirectionSuccess(direction: Direction) {
        if (direction.isOK) {
            val route = direction.routeList[0]
            val leg = route.legList[0]
            val distanceInfo = leg.distance
            val durationInfo = leg.duration
            val strDistance = distanceInfo.text
            val strDuration = durationInfo.text.replace("mins", "menit")

            Log.d("cek kodam","${distanceInfo}")

            // Tampilkan informasi jarak dan waktu tempuh
            binding.tvDistance.text = "Jarak Resto tujuan dengan lokasimu $strDistance dan waktu tempuh sekitar $strDuration"

            // Hapus semua marker dan polyline sebelumnya
            mapsView.clear()

            // Tambahkan marker lokasi saat ini (pengguna)
            mapsView.addMarker(MarkerOptions()
                .title("Lokasimu")
                .position(fromLatLng)
                .icon(resizeMapIcons(R.drawable.ic_loc_user, 96, 96)) // Adjust width and height as needed
            )

            // Tambahkan marker lokasi tujuan (restoran)
            mapsView.addMarker(MarkerOptions()
                .title(strNamaLokasi)
                .position(toLatLng)
                .icon(resizeMapIcons(R.drawable.ic_loc_restaurant, 96, 96)) // Adjust width and height as needed
            )

            // Tampilkan polyline rute dari pengguna ke restoran
            val directionPositionList = direction.routeList[0].legList[0].directionPoint
            mapsView.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 6, Color.BLUE))
        }
    }

    // Method dari interface DirectionCallback yang dipanggil jika gagal mengambil rute
    override fun onDirectionFailure(t: Throwable) {
        Toast.makeText(this, "Oops, gagal menampilkan rute!", Toast.LENGTH_SHORT).show()
    }

    // Method untuk meng-handle item pada ActionBar, khususnya tombol kembali
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Companion object untuk mengatur flag window pada activity
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
    }

    private fun resizeMapIcons(iconDrawableId: Int, width: Int, height: Int): BitmapDescriptor {
        val imageBitmap = BitmapFactory.decodeResource(resources, iconDrawableId)
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }

}
