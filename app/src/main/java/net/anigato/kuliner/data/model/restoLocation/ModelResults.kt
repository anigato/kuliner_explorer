package net.anigato.kuliner.data.model.restoLocation

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Model untuk hasil pencarian objek kuliner.
 * Berisi informasi geometri, nama, alamat, ID tempat, dan rating.
 */
class ModelResults (
    @SerializedName("geometry")
    val modelGeometry: ModelGeometry, // Informasi geometri lokasi dari objek kuliner

    @SerializedName("name")
    val name: String, // Nama objek kuliner

    @SerializedName("formatted_address")
    val formatted_address: String, // Alamat format dari objek kuliner

    @SerializedName("place_id")
    val placeId: String, // ID tempat atau lokasi dari objek kuliner

    @SerializedName("rating")
    val rating: Double = 0.0 // Rating objek kuliner, default 0.0 jika tidak tersedia
): Serializable
