package net.anigato.kuliner.data.model.resto

/**
 * Kelas model yang merepresentasikan jarak ke sebuah restoran.
 *
 * @property text Deskripsi jarak dalam bentuk string, biasanya berisi nilai yang lebih mudah dibaca manusia (misalnya, "2 km").
 * @property value Nilai jarak dalam bentuk integer, biasanya diukur dalam meter untuk akurasi yang lebih tinggi.
 */
data class Distance(
    val text: String,
    val value: Int
)
