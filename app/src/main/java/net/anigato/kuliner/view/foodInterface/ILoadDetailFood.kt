package net.anigato.kuliner.view.foodInterface

/**
 * Interface untuk memuat detail makanan.
 */
interface ILoadDetailFood {

    /**
     * Metode untuk mendapatkan detail makanan.
     * @param details ArrayList yang berisi detail makanan dalam bentuk String.
     */
    fun getDetails(details: ArrayList<String>)
}
