package net.anigato.kuliner.view.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.anigato.kuliner.R
import net.anigato.kuliner.controller.FoodsController
import net.anigato.kuliner.controller.LoadFoodsController
import net.anigato.kuliner.data.model.food.ModelFoods
import net.anigato.kuliner.databinding.ActivityMainBinding
import net.anigato.kuliner.view.foodInterface.IJsoupDataFood
import java.io.IOException

class FoodsActivity : AppCompatActivity(), IJsoupDataFood {

    private lateinit var binding: ActivityMainBinding
    private var modelFoods: ArrayList<ModelFoods>? = null
    private lateinit var foodsController: FoodsController
    private var strCity: String? = null
    private var currentCity: String? = null
    private lateinit var kotaArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sembunyikan ActionBar
        supportActionBar?.hide()

        // Inisialisasi controller
        foodsController = FoodsController(this, binding)

        // Mendapatkan nama kota dari Intent
        strCity = intent.getStringExtra("strCity")
        currentCity = strCity

        // Inisialisasi array kota
        kotaArray = arrayOf(
            "Pilih Daerah Lain",
            "Kabupaten Bandung",
            "Kabupaten Bandung Barat",
            "Kabupaten Bekasi",
            "Kabupaten Bogor",
            "Kabupaten Ciamis",
            "Kabupaten Cianjur",
            "Kabupaten Cirebon",
            "Kabupaten Garut",
            "Kabupaten Indramayu",
            "Kabupaten Karawang",
            "Kabupaten Kuningan",
            "Kabupaten Majalengka",
            "Kabupaten Pangandaran",
            "Kabupaten Purwakarta",
            "Kabupaten Subang",
            "Kabupaten Sukabumi",
            "Kabupaten Sumedang",
            "Kabupaten Tasikmalaya",
            "Kota Bandung",
            "Kota Banjar",
            "Kota Bekasi",
            "Kota Bogor",
            "Kota Cimahi",
            "Kota Cirebon",
            "Kota Depok",
            "Kota Sukabumi",
            "Kota Tasikmalaya"
        )

        // Setup tampilan dan data dengan controller
        modelFoods?.let {
            foodsController.setupViewAndData(it, strCity)
        }

        // Inisialisasi Spinner
        setupSpinner()

        // Inisialisasi tombol reset
        setupResetButton()

        // Mengatur teks awal pada TextView top_bar
        binding.topBar.text = "Kamu sekarang ada di ${strCity}"

        // Load foods data
        strCity?.let { loadFoods(it) }
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, R.layout.spinner_item, kotaArray)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerKota.adapter = adapter
        binding.spinnerKota.setSelection(0)

        binding.spinnerKota.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Set warna teks item yang dipilih menjadi putih
                (parent.getChildAt(0) as TextView).setTextColor(0xFFFFFFFF.toInt())
                val strCityDropdown = parent.getItemAtPosition(position).toString()
                if (position != 0) {
                    // Tampilkan daftar kuliner khas berdasarkan kota yang dipilih
                    binding.infoKuliner.text = "Daftar Kuliner Khas $strCityDropdown"
                    loadFoods(strCityDropdown)
                } else {
                    // Tampilkan info lokasi saat ini jika tidak ada kota yang dipilih
                    binding.infoKuliner.text = "${checkLocation(strCity)}"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Tampilkan info lokasi saat ini jika tidak ada kota yang dipilih
                binding.infoKuliner.text = "${checkLocation(strCity)}"
            }
        }
    }

    private fun setupResetButton() {
        binding.buttonReset.setOnClickListener {
            // Reset ke kota saat ini dan atur tampilan awal
            strCity = currentCity
            binding.spinnerKota.setSelection(0)
            binding.infoKuliner.text = "${checkLocation(currentCity)}"
            currentCity?.let { loadFoods(it) }
        }
    }

    private fun loadFoods(city: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val foods = withContext(Dispatchers.IO) {
                    // Load data makanan dari kota yang dipilih
                    val loadFoodsController = LoadFoodsController(this@FoodsActivity, city)
                    loadFoodsController.loadFoodsFromCity()
                }
                // Tampilkan data makanan yang berhasil di-load
                getWebData(foods)
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error jika terjadi kesalahan saat load data
            }
        }
    }

    override fun getWebData(datas: ArrayList<ModelFoods>) {
        modelFoods = datas
        // Setup tampilan dan data dengan controller
        foodsController.setupViewAndData(datas, strCity)
    }

    private fun checkLocation(city: String?): String {
        // Cek apakah kota ada dalam daftar kota dan tampilkan pesan sesuai
        return if (city != null && kotaArray.contains(city)) {
            "Daftar Kuliner Khas ${city}"
        } else {
            "Kamu Diluar Jawa Barat! Silahkan Pilih Daerah Lain!"
        }
    }
}
