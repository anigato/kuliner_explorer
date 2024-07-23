package net.anigato.kuliner.data.response

import net.anigato.kuliner.data.model.resto.ModelResto
import com.google.gson.annotations.SerializedName
import net.anigato.kuliner.data.model.resto.Route

/**
 * Model untuk respons detail objek kuliner dari API.
 * Berisi objek ModelResto sebagai hasil dari respons.
 */
class ModelResultDetailResto {
    @SerializedName("result")
    lateinit var modelResto: ModelResto // Objek ModelResto yang berisi detail dari objek kuliner

    @SerializedName("routes")
    var routes: List<Route>? = null // Daftar rute yang berisi informasi jarak, bisa null jika tidak tersedia
}
