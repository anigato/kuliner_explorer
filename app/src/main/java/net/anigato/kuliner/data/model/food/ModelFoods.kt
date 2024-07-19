package net.anigato.kuliner.data.model.food

/**
 * Kelas model yang merepresentasikan detail sebuah item makanan.
 *
 * @property image URL string yang merepresentasikan gambar dari makanan.
 * @property title Nama atau judul dari makanan.
 * @property detail Deskripsi detail atau informasi tambahan tentang makanan.
 * @property iterasi Identifier atau nomor urutan untuk item makanan ini,
 *                    digunakan untuk tujuan pengurutan atau identifikasi.
 */
class ModelFoods(
    var image: String,
    var title: String,
    var detail: String,
    var iterasi: String
) : java.io.Serializable
