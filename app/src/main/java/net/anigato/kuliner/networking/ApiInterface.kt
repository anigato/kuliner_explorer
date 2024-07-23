package net.anigato.kuliner.networking

import net.anigato.kuliner.data.response.ModelResultDetailResto
import net.anigato.kuliner.data.response.ModelResultRestoLocation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface Retrofit untuk mendefinisikan endpoint-endpoint API Google Places.
 * Berisi fungsi-fungsi untuk mendapatkan data restoran berdasarkan teks pencarian,
 * detail restoran berdasarkan place id, dan informasi jarak serta waktu perjalanan.
 */
interface ApiInterface {

    /**
     * Fungsi untuk mendapatkan hasil pencarian restoran berdasarkan teks pencarian.
     * Menggunakan endpoint `/maps/api/place/textsearch/json`.
     *
     * @param key API key untuk autentikasi
     * @param keyword Kata kunci pencarian
     * @param location Lokasi (koordinat) sebagai basis pencarian
     * @param rankby Metode pengurutan hasil pencarian
     * @param language Bahasa untuk hasil pencarian
     * @return Call<ModelResultRestoLocation> Mendapatkan data hasil pencarian dalam bentuk ModelResultRestoLocation
     */
    @GET("/maps/api/place/textsearch/json")
    fun getDataResult(
        @Query("key") key: String,
        @Query("query") keyword: String,
        @Query("location") location: String,
        @Query("rankby") rankby: String,
        @Query("language") language: String
    ): Call<ModelResultRestoLocation>

    /**
     * Fungsi untuk mendapatkan detail restoran berdasarkan place id.
     * Menggunakan endpoint `/maps/api/place/details/json`.
     *
     * @param key API key untuk autentikasi
     * @param placeid ID tempat (place id) dari restoran yang ingin dilihat detailnya
     * @param language Bahasa untuk hasil detail restoran
     * @return Call<ModelResultDetailResto> Mendapatkan detail restoran dalam bentuk ModelResultDetailResto
     */
    @GET("/maps/api/place/details/json")
    fun getDetailResult(
        @Query("key") key: String,
        @Query("placeid") placeid: String,
        @Query("language") language: String
    ): Call<ModelResultDetailResto>

    /**
     * Fungsi untuk mendapatkan jarak dan waktu perjalanan.
     * Menggunakan endpoint `/maps/api/directions/json`.
     *
     * @param key API key untuk autentikasi
     * @param origin Lokasi asal sebagai basis perhitungan jarak
     * @param destination Lokasi tujuan untuk perhitungan jarak
     * @return Call<ModelResultDetailResto> Mendapatkan data jarak dan waktu perjalanan dalam bentuk ModelResultDetailResto
     */
    @GET("/maps/api/directions/json")
    fun getJarakResto(
        @Query("key") key: String,
        @Query("origin") origin: String,
        @Query("destination") destination: String
    ): Call<ModelResultDetailResto>
}
