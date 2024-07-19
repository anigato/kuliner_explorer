package net.anigato.kuliner.networking

import net.anigato.kuliner.data.response.ModelResultDetailResto
import net.anigato.kuliner.data.response.ModelResultRestoLocation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface Retrofit untuk mendefinisikan endpoint-endpoint API Google Places.
 * Berisi fungsi-fungsi untuk mendapatkan data restoran berdasarkan teks pencarian dan detail restoran berdasarkan place id.
 */
interface ApiInterface {

    /**
     * Fungsi untuk mendapatkan hasil pencarian restoran berdasarkan teks pencarian.
     * Menggunakan endpoint `/maps/api/place/textsearch/json`.
     */
    @GET("/maps/api/place/textsearch/json")
    fun getDataResult(
        @Query("key") key: String, // API key untuk autentikasi
        @Query("query") keyword: String, // Kata kunci pencarian
        @Query("location") location: String, // Lokasi (koordinat) sebagai basis pencarian
        @Query("rankby") rankby: String, // Metode pengurutan hasil pencarian
        @Query("language") language: String // Bahasa untuk hasil pencarian
    ): Call<ModelResultRestoLocation> // Mendapatkan data hasil pencarian dalam bentuk ModelResultRestoLocation

    /**
     * Fungsi untuk mendapatkan detail restoran berdasarkan place id.
     * Menggunakan endpoint `/maps/api/place/details/json`.
     */
    @GET("/maps/api/place/details/json")
    fun getDetailResult(
        @Query("key") key: String, // API key untuk autentikasi
        @Query("placeid") placeid: String, // ID tempat (place id) dari restoran yang ingin dilihat detailnya
        @Query("language") language: String // Bahasa untuk hasil detail restoran
    ): Call<ModelResultDetailResto> // Mendapatkan detail restoran dalam bentuk ModelResultDetailResto


    /**
     * Fungsi untuk mendapatkan jarak, waktu perjalanan.
     * Menggunakan endpoint `/maps/api/directions/json?origin=&destination=place_id:&key=`.
     */
    @GET("/maps/api/directions/json")
    fun getJarakResto(
        @Query("key") key: String, // API key untuk autentikasi
        @Query("origin") origin: String, // ID tempat (place id) dari restoran yang ingin dilihat detailnya
        @Query("destination") destination: String, // ID tempat (place id) dari restoran yang ingin dilihat detailnya
    ): Call<ModelResultDetailResto> // Mendapatkan detail restoran dalam bentuk ModelResultDetailResto
}
