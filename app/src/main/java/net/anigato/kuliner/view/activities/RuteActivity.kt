package net.anigato.kuliner.view.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.TransportMode
import net.anigato.kuliner.R
import net.anigato.kuliner.data.model.details.ModelDetail
import net.anigato.kuliner.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import im.delight.android.location.SimpleLocation
import net.anigato.kuliner.databinding.ActivityRuteBinding
//import kotlinx.android.synthetic.main.activity_rute.*
import java.util.*
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter


class RuteActivity : AppCompatActivity(), OnMapReadyCallback, DirectionCallback {

    private lateinit var binding: ActivityRuteBinding
    lateinit var mapsView: GoogleMap
    lateinit var progressDialog: ProgressDialog
    lateinit var mainViewModel: MainViewModel
    lateinit var simpleLocation: SimpleLocation
    lateinit var strPlaceId: String
    lateinit var strNamaLokasi: String
    lateinit var strNamaJalan: String
    lateinit var strRating: String
    lateinit var strPhone: String
    lateinit var fromLatLng: LatLng
    lateinit var toLatLng: LatLng
    lateinit var strCurrentLocation: String
    var strCurrentLatitude = 0.0
    var strLatitude = 0.0
    var strCurrentLongitude = 0.0
    var strLongitude = 0.0
    var strOpenHour: List<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRuteBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_rute)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu…")
        progressDialog.setCancelable(false)
        progressDialog.setMessage("sedang menampilkan detail rute")

//        setSupportActionBar(binding.toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        simpleLocation = SimpleLocation(this)
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

        //get data intent from adapter
        val intent = intent
        val bundle = intent.extras
        if (bundle != null) {
            strPlaceId = bundle["placeId"] as String
            strLatitude = bundle["lat"] as Double
            strLongitude = bundle["lng"] as Double
            strNamaJalan = bundle["vicinity"] as String

            //latlong origin & destination
            fromLatLng = LatLng(strCurrentLatitude, strCurrentLongitude)
            toLatLng = LatLng(strLatitude, strLongitude)

            //viewmodel
            mainViewModel = ViewModelProvider(this, NewInstanceFactory()).get(MainViewModel::class.java)
            mainViewModel.setDetailLocation(strPlaceId)
            progressDialog.show()

            mainViewModel.getDetailLocation().observe(this, { modelDetail: ModelDetail ->

                strNamaLokasi = modelDetail.name
                strPhone = modelDetail.formatted_phone_number
                strRating = (modelDetail.rating.toString())
                if (strPhone == "0") {
                    binding.llPhone.visibility = View.GONE
                } else {
                    strPhone = modelDetail.formatted_phone_number
                }

                //rating
                val newValue = modelDetail.rating.toFloat()
                binding.ratingBar.setNumStars(5)
                binding.ratingBar.setStepSize(0.5.toFloat())
                binding.ratingBar.setRating(newValue)

                //set text detail location
                binding.tvNamaLokasi.setText(strNamaLokasi)
                binding.tvNamaJalan.setText(strNamaJalan)

                if (strRating == "0.0") {
                    binding.tvRating.setText("Tempat belum memiliki rating!")
                    binding.tvRating.setTextColor(Color.RED)
                } else {
                    binding.tvRating.setText(strRating)
                }

                //jam operasional
                try {
                    strOpenHour = modelDetail.modelOpening.weekdayText

                    val stringBuilder = StringBuilder()
                    for (strList in strOpenHour) {
                        stringBuilder.append(strList+"\n")
                    }
                    binding.tvJamOperasional.text = "Jam Operasional :"
                    binding.tvJamOperasional.setTextColor(Color.BLACK)
                    binding.tvJamBuka.text = stringBuilder.toString()
                    binding.tvJamBuka.setTextColor(Color.BLACK)
                    binding.imageTime.setBackgroundResource(R.drawable.ic_time)
                } catch (e: Exception) {
                    e.printStackTrace()
                    binding.tvJamOperasional.text = "Resto Tutup Sementara atau belum mengatur jam operasional"
                    binding.tvJamOperasional.setTextColor(Color.RED)
                    binding.tvJamBuka.visibility = View.GONE
                    binding.imageTime.setBackgroundResource(R.drawable.ic_block)
                }

                //intent open google maps
                binding.llRoute.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=$strLatitude,$strLongitude"))
                    startActivity(intent)
                }

                //intent to call number
                binding.llPhone.setOnClickListener {
                    val intent: Intent
                    intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$strPhone"))
                    startActivity(intent)
                }

                //intent to share location
                binding.llShare.setOnClickListener {
                    val strUri = "http://maps.google.com/maps?daddr=$strLatitude,$strLongitude"
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_SUBJECT, strNamaLokasi)
                    intent.putExtra(Intent.EXTRA_TEXT, strUri)
                    startActivity(Intent.createChooser(intent, "Bagikan :"))
                }

                //show route
                showDirection()
                progressDialog.dismiss()
            })
        }
    }

    private fun showDirection() {
        //get latlong for polyline
        GoogleDirection.withServerKey("AIzaSyDlEg-GyBBQmy4BmzgrFSk0n-OOI0RpZZA")
            .from(fromLatLng)
            .to(toLatLng)
            .transportMode(TransportMode.DRIVING)
            .execute(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapsView = googleMap
        mapsView.isMyLocationEnabled = true
        mapsView.setPadding(0, 60, 0, 0)
        mapsView.moveCamera(CameraUpdateFactory.newLatLng(fromLatLng))
        mapsView.animateCamera(CameraUpdateFactory.newLatLngZoom(fromLatLng, 15f))
    }

    override fun onDirectionSuccess(direction: Direction) {
        if (direction.isOK) {
            //show distance & duration
//            Log.d("cek direction","$direction")
            val route = direction.routeList[0]
//            Log.d("cek rutee","$route")
            val leg = route.legList[0]
//            Log.d("cek leg","$leg")

            val distanceInfo = leg.distance
//                Log.d("cek disance","$distanceInfo")
//                val jarak = distanceInfo.value
//                Log.d("cek distance val","$jarak")
            val durationInfo = leg.duration
//                Log.d("cek duration","$durationInfo")
//                val lama = durationInfo.value
//                Log.d("cek duration val","$lama")
            val strDistance = distanceInfo.text
//                Log.d("cek strDistance","$strDistance")
            val strDuration = durationInfo.text.replace("mins", "mnt")
//                Log.d("cek strDuration","$strDuration")

//            val strDuration = durationInfo.text.replace("mins", "mnt")
            binding.tvDistance.text = "Jarak lokasi tujuan dari lokasi kamu sekarang $strDistance dan waktu tempuh sekitar $strDuration"

            //set marker current location
            mapsView.addMarker(MarkerOptions()
                .title("Lokasi Kamu")
                .position(fromLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))

            //set marker destination
            mapsView.addMarker(MarkerOptions()
                .title(strNamaLokasi)
                .position(toLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

            //show polyline
            val directionPositionList = direction.routeList[0].legList[0].directionPoint
            mapsView.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 6, Color.RED))
        }
    }

    override fun onDirectionFailure(t: Throwable) {
        Toast.makeText(this, "Oops, gagal menampilkan rute!", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
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
    }

}