package net.anigato.kuliner.data.response

import net.anigato.kuliner.data.model.nearby.ModelResults
import com.google.gson.annotations.SerializedName

class ModelResultNearby {
    @SerializedName("results")
    lateinit var modelResults: List<ModelResults>
}