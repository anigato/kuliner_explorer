package net.anigato.kuliner.data.model.resto

import net.anigato.kuliner.data.model.restoLocation.ModelGeometry
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Model untuk detail objek kuliner.
 * Berisi informasi geometri, jam buka, nama, nomor telepon, dan rating.
 */
class ModelResto : Serializable {
    @SerializedName("geometry")
    lateinit var modelGeometry: ModelGeometry // Informasi geometri objek

    @SerializedName("opening_hours")
    lateinit var modelOperationalResto: ModelOperationalResto // Informasi jam buka objek

    @SerializedName("name")
    lateinit var name: String // Nama objek kuliner

    @SerializedName("formatted_phone_number")
    var formatted_phone_number = "0" // Nomor telepon, default "0" jika tidak tersedia

    @SerializedName("rating")
    var rating = 0.0 // Rating objek, default 0.0 jika tidak tersedia
}
