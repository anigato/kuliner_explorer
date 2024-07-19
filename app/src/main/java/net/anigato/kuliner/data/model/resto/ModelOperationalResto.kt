package net.anigato.kuliner.data.model.resto

import com.google.gson.annotations.SerializedName

/**
 * Model data untuk status operasional restoran.
 *
 * @property openNow Menyatakan apakah restoran buka saat ini atau tidak.
 * @property weekdayText Daftar teks hari kerja yang berisi informasi jadwal operasional restoran.
 */
class ModelOperationalResto {
    @SerializedName("open_now")
    var openNow: Boolean? = null

    @SerializedName("weekday_text")
    lateinit var weekdayText: List<String>
}
