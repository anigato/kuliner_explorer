package net.anigato.kuliner.controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import net.anigato.kuliner.databinding.ActivityDetailsFoodBinding
import net.anigato.kuliner.view.activities.MapActivity

class FoodDetailController(
    private val context: Context,
    private val binding: ActivityDetailsFoodBinding
) {

    // Fungsi untuk mengatur listener pada tombol dan menginisialisasi data yang diperlukan
    fun setupListeners(strCity: String?) {
        // Mengambil judul dari intent dan menyimpannya di MapsController
        MapsController.title = (context as AppCompatActivity).intent.getStringExtra("TITLE")

        // Menambahkan listener untuk tombol "Go to Map"
        binding.btnGoToMap.setOnClickListener {
            // Membuat Intent untuk memulai MapActivity dan mengirimkan data strCity
            val intent = Intent(context, MapActivity::class.java).apply {
                putExtra("strCity", strCity)
            }
            context.startActivity(intent)
        }
    }
}
