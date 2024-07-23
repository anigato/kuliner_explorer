package net.anigato.kuliner.controller

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import net.anigato.kuliner.view.foodInterface.ILoadDetailFood
import java.io.IOException

// Controller untuk memuat detail makanan dari URL yang diberikan menggunakan Jsoup
class LoadFoodDetailController(
    private val activity: AppCompatActivity, // Aktivitas yang menggunakan controller ini
    private val urlDetail: String? // URL detail makanan yang akan dimuat
) {

    private val loader = activity as ILoadDetailFood // Interface loader untuk mengirimkan hasil pemrosesan

    // Metode untuk memulai proses pemrosesan latar belakang menggunakan Coroutine
    fun startLoading() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Memuat detail makanan dari URL dalam konteks IO
                val details = withContext(Dispatchers.IO) {
                    loadDetailsFromUrl()
                }
                Log.d("cekdetaildd", "$details") // Logging detail yang dimuat untuk keperluan debugging
                loader.getDetails(details) // Memanggil metode loader untuk mengirimkan hasil pemrosesan
            } catch (e: IOException) {
                e.printStackTrace()
                // Tangani error jika diperlukan
            }
        }
    }

    // Metode untuk memuat detail makanan dari URL menggunakan Jsoup di dalam Coroutine
    private suspend fun loadDetailsFromUrl(): ArrayList<String> {
        val details = ArrayList<String>()
        urlDetail?.let { url ->
            try {
                // Di sini seharusnya dilakukan parsing HTML dengan Jsoup
                details.add(url) // Menambahkan URL detail ke daftar detail (contoh saja)
            } catch (e: IOException) {
                throw e
            }
        }
        return details
    }
}
