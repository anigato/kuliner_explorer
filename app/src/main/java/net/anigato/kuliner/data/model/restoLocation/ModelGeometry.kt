package net.anigato.kuliner.data.model.restoLocation

import com.google.gson.annotations.SerializedName

/**
 * Model untuk geometri lokasi dari objek kuliner.
 * Berisi informasi lokasi dari objek kuliner.
 */
class ModelGeometry {
    @SerializedName("location")
    lateinit var modelLocation: ModelLocation // Informasi lokasi dari objek kuliner
}
