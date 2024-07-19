package net.anigato.kuliner.data.model.restoLocation

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Model untuk hasil pencarian objek kuliner.
 * Berisi informasi geometri, nama, alamat, ID tempat, dan rating.
 */
class ModelResults : Serializable {
    @SerializedName("geometry")
    lateinit var modelGeometry: ModelGeometry // Informasi geometri lokasi dari objek kuliner

    @SerializedName("name")
    lateinit var name: String // Nama objek kuliner

//    @SerializedName("vicinity")
//    lateinit var vicinity: String // Alamat objek kuliner (jika tersedia)

    @SerializedName("formatted_address")
    lateinit var formatted_address: String // Alamat format dari objek kuliner

    @SerializedName("place_id")
    lateinit var placeId: String // ID tempat atau lokasi dari objek kuliner

    @SerializedName("rating")
    var rating = 0.0 // Rating objek kuliner, default 0.0 jika tidak tersedia

//    @SerializedName("lat")
//    var lat: Double = 0.0 // Koordinat latitude dari lokasi objek kuliner

//    @SerializedName("lng")
//    var lng: Double = 0.0 // Koordinat longitude dari lokasi objek kuliner
}
