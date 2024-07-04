package net.anigato.kuliner.networking

import net.anigato.kuliner.data.response.ModelResultDetail
import net.anigato.kuliner.data.response.ModelResultNearby
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiInterface {
//    @GET("/maps/api/place/nearbysearch/json")
//    fun getDataResult(@Query("key") key: String,
//                      @Query("keyword") keyword: String,
//                      @Query("location") location: String,
//                      @Query("rankby") rankby: String): Call<ModelResultNearby>

    @GET("/maps/api/place/textsearch/json")
    fun getDataResult(@Query("key") key: String,
                      @Query("query") keyword: String,
                      @Query("location") location: String,
                      @Query("rankby") rankby: String,
                      @Query("language") language: String
    ): Call<ModelResultNearby>

    @GET("/maps/api/place/details/json")
    fun getDetailResult(@Query("key") key: String,
                        @Query("placeid") placeid: String,
                        @Query("language") language: String
    ): Call<ModelResultDetail>
}