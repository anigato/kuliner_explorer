package net.anigato.kuliner.data.response

import net.anigato.kuliner.data.model.restoLocation.ModelResults
import com.google.gson.annotations.SerializedName

/**
 * Model untuk respons lokasi objek kuliner dari API.
 * Berisi daftar ModelResults sebagai hasil dari respons.
 */
class ModelResultRestoLocation {
    @SerializedName("results")
    lateinit var modelResults: List<ModelResults> // Daftar ModelResults yang berisi lokasi dari objek kuliner
}
