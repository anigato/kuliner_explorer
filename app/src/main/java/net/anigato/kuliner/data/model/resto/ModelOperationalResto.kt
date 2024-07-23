package net.anigato.kuliner.data.model.resto

import com.google.gson.annotations.SerializedName

/**
 * Model data untuk status operasional restoran.
 *
 * @property openNow Menyatakan apakah restoran buka saat ini atau tidak.
 * @property weekdayText Daftar teks hari kerja yang berisi informasi jadwal operasional restoran.
 */
data class ModelOperationalResto(
    @SerializedName("open_now")
    val openNow: Boolean? = null,

    @SerializedName("weekday_text")
    val weekdayText: List<String> = emptyList()
)
