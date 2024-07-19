package net.anigato.kuliner.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.squareup.picasso.Picasso
import net.anigato.kuliner.controller.LoadFoodDetailController
import net.anigato.kuliner.controller.FoodDetailController
import net.anigato.kuliner.databinding.ActivityDetailsFoodBinding
import net.anigato.kuliner.view.foodInterface.ILoadDetailFood

/**
 * Activity untuk menampilkan detail makanan.
 * Mengimplementasikan ILoadDetailFood untuk menangani hasil pemrosesan detail makanan.
 */
class FoodDetailActivity : AppCompatActivity(), ILoadDetailFood {
    private lateinit var binding: ActivityDetailsFoodBinding
    private var urlImage: String? = null
    private var urlDetail: String? = null
    private var strCity: String? = null
    private var loadFoodDetailController: LoadFoodDetailController? = null
    private var foodDetailController: FoodDetailController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan data dari Intent
        urlImage = intent.getStringExtra("IMAGE")
        urlDetail = intent.getStringExtra("DETAIL")
        strCity = intent.getStringExtra("strCity")

        // Logging untuk memeriksa data yang diterima
        Log.d("cek detailfood", "$urlImage")
        Log.d("cek detaildetail", "$urlDetail")

        // Menampilkan judul dan gambar makanan
        binding.txtTitleDetail.text = intent.getStringExtra("TITLE")
        Picasso.get().load(urlImage).into(binding.imageDetail)

        // Inisialisasi dan memulai proses memuat detail makanan
        loadFoodDetailController = LoadFoodDetailController(this, urlDetail)
        loadFoodDetailController?.startLoading()

        // Inisialisasi dan setup listener untuk tombol
        foodDetailController = FoodDetailController(this, binding)
        foodDetailController?.setupListeners(strCity)
    }

    // Implementasi dari interface ILoadDetailFood untuk menampilkan detail makanan yang dimuat
    override fun getDetails(details: ArrayList<String>) {
        for (index in details.indices) {
            binding.txtDetail.append(details[index])
            if (index < details.size - 1) {
                binding.txtDetail.append("\n\n")
            }
        }
    }
}
