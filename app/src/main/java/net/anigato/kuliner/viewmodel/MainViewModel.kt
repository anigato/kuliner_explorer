package net.anigato.kuliner.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.anigato.kuliner.data.model.details.ModelDetail
import net.anigato.kuliner.data.model.nearby.ModelResults
import net.anigato.kuliner.data.response.ModelResultDetail
import net.anigato.kuliner.data.response.ModelResultNearby
import net.anigato.kuliner.networking.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainViewModel : ViewModel() {

    private val modelResultsMutableLiveData = MutableLiveData<ArrayList<ModelResults>>()
    private val modelDetailMutableLiveData = MutableLiveData<ModelDetail>()


    companion object {
        var strApiKey = "AIzaSyDlEg-GyBBQmy4BmzgrFSk0n-OOI0RpZZA"
        var title: String? = null
    }

    fun setMarkerLocation(strLocation: String) {
        val apiService = ApiClient.getClient()
//        val call = apiService.getDataResult(strApiKey, "takoyaki", strLocation, "distance")
        val call = apiService.getDataResult(strApiKey, title.toString(), strLocation, "distance")
        call.enqueue(object : Callback<ModelResultNearby> {
            override fun onResponse(call: Call<ModelResultNearby>, response: Response<ModelResultNearby>) {
                val body = response.body()
                if (!response.isSuccessful) {
                    Log.e("response", response.toString())
                } else if (body != null) {
                    val items = ArrayList(body.modelResults)
                    modelResultsMutableLiveData.postValue(items)
                }
            }

            override fun onFailure(call: Call<ModelResultNearby>, t: Throwable) {
                Log.e("failure", t.toString())
            }
        })
    }

    fun setDetailLocation(strPlaceID: String) {
        val apiService = ApiClient.getClient()
        val call = apiService.getDetailResult(strApiKey, strPlaceID)
        call.enqueue(object : Callback<ModelResultDetail> {
            override fun onResponse(call: Call<ModelResultDetail>, response: Response<ModelResultDetail>) {
                val body = response.body()
                if (!response.isSuccessful) {
                    Log.e("response", response.toString())
                } else if (body != null) {
                    modelDetailMutableLiveData.postValue(body.modelDetail)
                }
            }

            override fun onFailure(call: Call<ModelResultDetail>, t: Throwable) {
                Log.e("failure", t.toString())
            }
        })
    }

    fun getMarkerLocation(): LiveData<ArrayList<ModelResults>> = modelResultsMutableLiveData

    fun getDetailLocation(): LiveData<ModelDetail> = modelDetailMutableLiveData

}