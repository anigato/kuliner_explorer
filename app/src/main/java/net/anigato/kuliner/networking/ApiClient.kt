package net.anigato.kuliner.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Kelas untuk mengatur Retrofit client.
 * Berisi konfigurasi dasar untuk membangun Retrofit instance.
 */
class ApiClient {

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/" // URL dasar dari API Google Maps

        /**
         * Fungsi untuk mendapatkan instance Retrofit untuk koneksi dengan API.
         * Menggunakan GsonConverterFactory untuk mengonversi JSON response menjadi objek Kotlin.
         */
        fun getClient(): ApiInterface {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // Menggunakan Gson untuk konversi JSON
                .build()
            return retrofit.create(ApiInterface::class.java) // Membuat instance ApiInterface dari Retrofit
        }
    }
}
