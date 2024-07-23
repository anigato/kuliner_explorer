package net.anigato.kuliner.controller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import net.anigato.kuliner.databinding.ActivityDetailsFoodBinding
import net.anigato.kuliner.view.activities.MapActivity
import net.anigato.kuliner.viewmodel.MainViewModel

class FoodDetailController(private val context: Context, private val binding: ActivityDetailsFoodBinding) {
    fun setupListeners(strCity: String?) {
        // Menyimpan judul sebagai title di MainViewModel
        MainViewModel.title = (context as AppCompatActivity).intent.getStringExtra("TITLE")

        // Listener untuk tombol "Go to Map"
        binding.btnGoToMap.setOnClickListener {
            // Memulai activity MapActivity dengan mengirimkan data strCity
            val intent = Intent(context, MapActivity::class.java)
            intent.putExtra("strCity", strCity)
            context.startActivity(intent)
        }
    }
}
