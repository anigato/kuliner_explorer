package net.anigato.kuliner.data.model.resto

/**
 * Kelas data yang merepresentasikan sebuah Rute
 *
 * @property legs Daftar objek Leg yang merepresentasikan setiap  rute.
 */
data class Route(
    val legs: List<Leg>
)
