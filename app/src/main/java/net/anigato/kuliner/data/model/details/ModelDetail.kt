package net.anigato.kuliner.data.model.details

import net.anigato.kuliner.data.model.nearby.ModelGeometry
import com.google.gson.annotations.SerializedName


class ModelDetail {
    @SerializedName("geometry")
    lateinit var modelGeometry: ModelGeometry

    @SerializedName("opening_hours")
    lateinit var modelOpening: ModelOpening

    @SerializedName("name")
    lateinit var name: String

    @SerializedName("formatted_phone_number")
//    lateinit var formatted_phone_number: String
    var formatted_phone_number = "0"

    @SerializedName("rating")
    var rating = 0.0
}