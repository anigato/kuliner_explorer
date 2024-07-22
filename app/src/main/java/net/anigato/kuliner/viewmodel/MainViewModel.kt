package net.anigato.kuliner.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.anigato.kuliner.data.model.resto.ModelResto
import net.anigato.kuliner.data.model.restoLocation.ModelResults
import net.anigato.kuliner.data.response.ModelResultDetailResto
import net.anigato.kuliner.data.response.ModelResultRestoLocation
import net.anigato.kuliner.networking.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

/**
 * ViewModel untuk aktivitas utama, mengelola data yang diperlukan untuk menampilkan lokasi restoran.
 * Menggunakan LiveData untuk mengamati perubahan data dari API dan memberikan respons ke antarmuka pengguna.
 */
class MainViewModel : ViewModel() {

    // LiveData untuk hasil pencarian lokasi restoran
    private val modelResultsMutableLiveData = MutableLiveData<ArrayList<ModelResults>>()

    // LiveData untuk detail restoran
    private val modelRestoMutableLiveData = MutableLiveData<ModelResto>()

    companion object {
        var strApiKey = "AIzaSyDlEg-GyBBQmy4BmzgrFSk0n-OOI0RpZZA" // Kunci API untuk layanan Google
        var title: String? = null // Judul untuk pencarian lokasi restoran
    }

    /**
     * Metode untuk mengatur lokasi marker berdasarkan pencarian teks di Google Places API.
     * Mengambil data dari API berdasarkan lokasi dan judul restoran.
     */
    fun setMarkerLocation(strLocation: String) {
        val apiService = ApiClient.getClient()
        val title = title.toString()
        val call = apiService.getDataResult(strApiKey, "tempat yang menjual $title", strLocation, "distance", "id")

        call.enqueue(object : Callback<ModelResultRestoLocation> {
            override fun onResponse(call: Call<ModelResultRestoLocation>, response: Response<ModelResultRestoLocation>) {
                val body = response.body()
                Log.d("MainViewModel", "Respon API: ${response.raw()}")

                if (response.isSuccessful && body != null) {
                    Log.d("MainViewModel", "Jumlah hasil yang ditemukan: ${body.modelResults.size}")
                    if (body.modelResults.isNotEmpty()) {
                        processResults(body.modelResults, strLocation)
                    } else {
                        modelResultsMutableLiveData.postValue(ArrayList())
                    }
                } else {
                    Log.e("response", response.toString())
                }
            }

            override fun onFailure(call: Call<ModelResultRestoLocation>, t: Throwable) {
                Log.e("failure", t.toString())
            }
        })
        Log.d("MainViewModel", "Set Marker Location: $strLocation")
    }

    private fun processResults(results: List<ModelResults>, strLocation: String) {
        val items = ArrayList<ModelResults>()
        var checkedCount = 0

        for (result in results) {
            checkJarak(result.placeId, strLocation) { isWithinRange ->
                checkedCount++
                if (isWithinRange) {
                    items.add(result)
                }
                if (checkedCount == results.size) {
                    if (items.isEmpty()) {
                        modelResultsMutableLiveData.postValue(ArrayList())
                    } else {
                        modelResultsMutableLiveData.postValue(items)
                    }
                    Log.d("MainViewModel", "Data berhasil diambil, jumlah item yang valid: ${items.size}")
                }
            }
        }
    }




    /**
     * Metode untuk mengambil detail lokasi restoran berdasarkan ID tempat.
     */
    fun setDetailLocation(strPlaceID: String) {
        val apiService = ApiClient.getClient() // Mendapatkan klien retrofit untuk layanan API
        val call = apiService.getDetailResult(strApiKey, strPlaceID, "id")
        call.enqueue(object : Callback<ModelResultDetailResto> {
            override fun onResponse(call: Call<ModelResultDetailResto>, response: Response<ModelResultDetailResto>) {
                val body = response.body()
                Log.d("MainViewModel", "Detail Respon API: ${response.raw()}")  // Log detail respon mentah dari API
                if (!response.isSuccessful) {
                    Log.e("response", response.toString()) // Log jika respon tidak berhasil
                } else if (body != null) {
                    modelRestoMutableLiveData.postValue(body.modelResto) // Mengirimkan detail restoran ke LiveData
                }
            }

            override fun onFailure(call: Call<ModelResultDetailResto>, t: Throwable) {
                Log.e("failure", t.toString()) // Log jika terjadi kegagalan saat mengambil data
            }
        })
    }

    /**
     * Metode untuk mengambil jarak lokasi restoran berdasarkan ID tempat.
     */
    fun checkJarak(strPlaceID: String, strLocation: String, callback: (Boolean) -> Unit) {
        val apiService = ApiClient.getClient() // Mendapatkan klien retrofit untuk layanan API
        val call = apiService.getJarakResto(strApiKey, strLocation, "place_id:$strPlaceID")
        call.enqueue(object : Callback<ModelResultDetailResto> {
            override fun onResponse(call: Call<ModelResultDetailResto>, response: Response<ModelResultDetailResto>) {
                val body = response.body()
                Log.d("MainViewModel", "Detail Respon API: ${response.raw()}")  // Log detail respon mentah dari API
                if (!response.isSuccessful) {
                    Log.e("MainViewModel", "gagal " + response.toString()) // Log jika respon tidak berhasil
                    callback(false)
                } else if (body != null) {
                    val distance = body.routes?.firstOrNull()?.legs?.firstOrNull()?.distance?.value ?: 0
                    val result = distance < 15000
                    callback(result)
                }
            }

            override fun onFailure(call: Call<ModelResultDetailResto>, t: Throwable) {
                Log.e("failure", t.toString()) // Log jika terjadi kegagalan saat mengambil data
                callback(false)
            }
        })
    }



    /**
     * Mengambil LiveData untuk hasil pencarian lokasi restoran.
     */
    fun getMarkerLocation(): LiveData<ArrayList<ModelResults>> {
        modelResultsMutableLiveData.value?.let {
            Log.d("MapActivity getmarker", "Get Marker Location: ${it.size} items")
        } ?: run {
            Log.d("MapActivity getmarker", "Get Marker Location: null")
        }
        return modelResultsMutableLiveData
    }

    /**
     * Mengambil LiveData untuk detail lokasi restoran.
     */
    fun getDetailLocation(): LiveData<ModelResto> = modelRestoMutableLiveData
}
