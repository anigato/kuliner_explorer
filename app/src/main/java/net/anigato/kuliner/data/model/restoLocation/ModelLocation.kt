package net.anigato.kuliner.data.model.restoLocation

import com.google.gson.annotations.SerializedName

/**
 * Model untuk informasi lokasi (latitude dan longitude) dari objek kuliner.
 */
class ModelLocation {
    @SerializedName("lat")
    var lat: Double = 0.0 // Koordinat latitude dari lokasi objek kuliner

    @SerializedName("lng")
    var lng: Double = 0.0 // Koordinat longitude dari lokasi objek kuliner
}
