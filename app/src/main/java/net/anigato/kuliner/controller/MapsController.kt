package net.anigato.kuliner.controller

import android.util.Log
import net.anigato.kuliner.data.model.resto.ModelResto
import net.anigato.kuliner.data.model.restoLocation.ModelResults
import net.anigato.kuliner.data.response.ModelResultDetailResto
import net.anigato.kuliner.data.response.ModelResultRestoLocation
import net.anigato.kuliner.networking.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

/**
 * Controller untuk aktivitas utama, mengelola data yang diperlukan untuk menampilkan lokasi restoran.
 */
class MapsController {

    // Callback interface untuk hasil pencarian lokasi restoran
    interface OnMarkerLocationResult {
        fun onSuccess(results: ArrayList<ModelResults>)
        fun onFailure()
    }

    // Callback interface untuk detail restoran
    interface OnDetailLocationResult {
        fun onSuccess(modelResto: ModelResto)
        fun onFailure()
    }

    // Callback interface untuk pengecekan jarak restoran
    interface OnCheckJarakResult {
        fun onResult(isWithinRange: Boolean)
    }

    companion object {
        var strApiKey = "AIzaSyDlEg-GyBBQmy4BmzgrFSk0n-OOI0RpZZA" // Kunci API untuk layanan Google
        var title: String? = null // Judul untuk pencarian lokasi restoran
    }

    /**
     * Metode untuk mengatur lokasi marker berdasarkan pencarian teks di Google Places API.
     * Mengambil data dari API berdasarkan lokasi dan judul restoran.
     */
    fun setMarkerLocation(strLocation: String, callback: OnMarkerLocationResult) {
        val apiService = ApiClient.getClient()
        val title = title.toString()
        val call = apiService.getDataResult(strApiKey, "tempat yang menjual $title", strLocation, "distance", "id")

        call.enqueue(object : Callback<ModelResultRestoLocation> {
            override fun onResponse(call: Call<ModelResultRestoLocation>, response: Response<ModelResultRestoLocation>) {
                val body = response.body()
                Log.d("MapsController", "Respon API: ${response.raw()}")

                if (response.isSuccessful && body != null) {
                    Log.d("MapsController", "Jumlah hasil yang ditemukan: ${body.modelResults.size}")
                    if (body.modelResults.isNotEmpty()) {
                        processResults(body.modelResults, strLocation, callback)
                    } else {
                        callback.onSuccess(ArrayList())
                    }
                } else {
                    Log.e("response", response.toString())
                    callback.onFailure()
                }
            }

            override fun onFailure(call: Call<ModelResultRestoLocation>, t: Throwable) {
                Log.e("failure", t.toString())
                callback.onFailure()
            }
        })
        Log.d("MapsController", "Set Marker Location: $strLocation")
    }

    /**
     * Memproses hasil pencarian restoran.
     * @param results Daftar hasil pencarian.
     * @param strLocation Lokasi pencarian.
     */
    private fun processResults(results: List<ModelResults>, strLocation: String, callback: OnMarkerLocationResult) {
        val items = ArrayList<ModelResults>()
        var checkedCount = 0

        for (result in results) {
            checkJarak(result.placeId, strLocation, object : OnCheckJarakResult {
                override fun onResult(isWithinRange: Boolean) {
                    checkedCount++
                    if (isWithinRange) {
                        items.add(result)
                    }
                    if (checkedCount == results.size) {
                        if (items.isEmpty()) {
                            callback.onSuccess(ArrayList())
                        } else {
                            callback.onSuccess(items)
                        }
                        Log.d("MapsController", "Data berhasil diambil, jumlah item yang valid: ${items.size}")
                    }
                }
            })
        }
    }

    /**
     * Metode untuk mengambil detail lokasi restoran berdasarkan ID tempat.
     * @param strPlaceID ID tempat restoran.
     */
    fun setDetailLocation(strPlaceID: String, callback: OnDetailLocationResult) {
        val apiService = ApiClient.getClient() // Mendapatkan klien retrofit untuk layanan API
        val call = apiService.getDetailResult(strApiKey, strPlaceID, "id")
        call.enqueue(object : Callback<ModelResultDetailResto> {
            override fun onResponse(call: Call<ModelResultDetailResto>, response: Response<ModelResultDetailResto>) {
                val body = response.body()
                Log.d("MapsController", "Detail Respon API: ${response.raw()}")  // Log detail respon mentah dari API
                if (!response.isSuccessful) {
                    Log.e("response", response.toString()) // Log jika respon tidak berhasil
                    callback.onFailure()
                } else if (body != null) {
                    callback.onSuccess(body.modelResto) // Mengirimkan detail restoran ke callback
                }
            }

            override fun onFailure(call: Call<ModelResultDetailResto>, t: Throwable) {
                Log.e("failure", t.toString()) // Log jika terjadi kegagalan saat mengambil data
                callback.onFailure()
            }
        })
    }

    /**
     * Metode untuk memeriksa jarak lokasi restoran berdasarkan ID tempat.
     * @param strPlaceID ID tempat restoran.
     * @param strLocation Lokasi pencarian.
     * @param callback Fungsi callback untuk mengembalikan hasil pemeriksaan.
     */
    fun checkJarak(strPlaceID: String, strLocation: String, callback: OnCheckJarakResult) {
        val apiService = ApiClient.getClient() // Mendapatkan klien retrofit untuk layanan API
        val call = apiService.getJarakResto(strApiKey, strLocation, "place_id:$strPlaceID")
        call.enqueue(object : Callback<ModelResultDetailResto> {
            override fun onResponse(call: Call<ModelResultDetailResto>, response: Response<ModelResultDetailResto>) {
                val body = response.body()
                Log.d("MapsController", "Detail Respon API: ${response.raw()}")  // Log detail respon mentah dari API
                if (!response.isSuccessful) {
                    Log.e("MapsController", "gagal " + response.toString()) // Log jika respon tidak berhasil
                    callback.onResult(false)
                } else if (body != null) {
                    val distance = body.routes?.firstOrNull()?.legs?.firstOrNull()?.distance?.value ?: 0
                    val result = distance < 15000
                    callback.onResult(result)
                }
            }

            override fun onFailure(call: Call<ModelResultDetailResto>, t: Throwable) {
                Log.e("failure", t.toString()) // Log jika terjadi kegagalan saat mengambil data
                callback.onResult(false)
            }
        })
    }

    /**
     * Mengambil hasil pencarian lokasi restoran.
     */
//    fun getMarkerLocation(callback: OnMarkerLocationResult) {
//        val apiService = ApiClient.getClient()
//        val title = title.toString()
//        val call = apiService.getDataResult(strApiKey, "tempat yang menjual $title", "", "distance", "id")
//
//        call.enqueue(object : Callback<ModelResultRestoLocation> {
//            override fun onResponse(call: Call<ModelResultRestoLocation>, response: Response<ModelResultRestoLocation>) {
//                val body = response.body()
//                Log.d("MapsController", "Respon API: ${response.raw()}")
//
//                if (response.isSuccessful && body != null) {
//                    Log.d("MapsController", "Jumlah hasil yang ditemukan: ${body.modelResults.size}")
//                    callback.onSuccess(ArrayList(body.modelResults))
//                } else {
//                    Log.e("response", response.toString())
//                    callback.onFailure()
//                }
//            }
//
//            override fun onFailure(call: Call<ModelResultRestoLocation>, t: Throwable) {
//                Log.e("failure", t.toString())
//                callback.onFailure()
//            }
//        })
//    }
}
