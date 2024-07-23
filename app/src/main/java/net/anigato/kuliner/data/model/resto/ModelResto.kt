package net.anigato.kuliner.data.model.resto

import net.anigato.kuliner.data.model.restoLocation.ModelGeometry
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Model untuk detail objek kuliner.
 * Berisi informasi geometri, jam buka, nama, nomor telepon, dan rating.
 */
data class ModelResto(
    @SerializedName("geometry")
    val modelGeometry: ModelGeometry, // Informasi geometri objek

    @SerializedName("opening_hours")
    val modelOperationalResto: ModelOperationalResto, // Informasi jam buka objek

    @SerializedName("name")
    val name: String, // Nama objek kuliner

    @SerializedName("formatted_phone_number")
    val formatted_phone_number: String = "0", // Nomor telepon, default "0" jika tidak tersedia

    @SerializedName("rating")
    val rating: Double = 0.0 // Rating objek, default 0.0 jika tidak tersedia
) : Serializable
