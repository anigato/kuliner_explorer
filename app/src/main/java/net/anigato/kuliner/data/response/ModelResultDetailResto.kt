package net.anigato.kuliner.data.response

import net.anigato.kuliner.data.model.resto.ModelResto
import com.google.gson.annotations.SerializedName

/**
 * Model untuk respons detail objek kuliner dari API.
 * Berisi objek ModelResto sebagai hasil dari respons.
 */
class ModelResultDetailResto {
    @SerializedName("result")
    lateinit var modelResto: ModelResto // Objek ModelResto yang berisi detail dari objek kuliner
}
