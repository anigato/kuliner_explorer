package net.anigato.kuliner.view.foodInterface

import net.anigato.kuliner.data.model.food.ModelFoods

/**
 * Interface untuk mendapatkan data makanan dari web menggunakan Jsoup.
 */
interface IJsoupDataFood {

    /**
     * Metode untuk mendapatkan data makanan dari web.
     * @param datas ArrayList yang berisi ModelFoods yang berisi data makanan.
     */
    fun getWebData(datas: ArrayList<ModelFoods>)
}
