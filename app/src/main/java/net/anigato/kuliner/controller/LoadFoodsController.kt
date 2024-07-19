package net.anigato.kuliner.controller

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.anigato.kuliner.data.model.food.ModelFoods
import net.anigato.kuliner.view.foodInterface.IJsoupDataFood
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException

class LoadFoodsController(
    private val activity: AppCompatActivity?, // Aktivitas yang menggunakan controller ini
    private val strCity: String? // Nama kota untuk memuat data makanan
) {

    private lateinit var loadedData: IJsoupDataFood // Interface untuk mengirimkan hasil pemrosesan

    init {
        activity?.let {
            if (it is IJsoupDataFood) {
                loadedData = it
            } else {
                throw IllegalArgumentException("Activity must implement IJsoupDataFood interface")
            }
        }
    }

    fun startLoading() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val foods = withContext(Dispatchers.IO) {
                    loadFoodsFromCity()
                }
                loadedData.getWebData(foods) // Mengirimkan hasil data makanan ke interface setelah selesai
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error if needed
            }
        }
    }

    suspend fun loadFoodsFromCity(): ArrayList<ModelFoods> {
        val modelFoods = ArrayList<ModelFoods>()

        Log.d("cek load food", "$strCity")

        try {
            when (strCity) {
                "Kabupaten Bogor", "Kota Bogor" -> {
                    webTidakTerstruktur("https://www.detik.com/jabar/kuliner/d-6714903/10-makanan-khas-bogor-rekomendasi-untuk-pecinta-kuliner", modelFoods)
                }
                "Kabupaten Bandung", "Kabupaten Bandung Barat", "Kota Bandung", "Kota Cimahi" -> {
                    webTidakTerstruktur("https://www.klook.com/id/blog/makanan-khas-bandung/", modelFoods)
                }
                "Kabupaten Bekasi", "Kota Bekasi" -> {
                    webTerstruktur("https://www.idntimes.com/food/dining-guide/fina-wahibatun-nisa/10-makanan-khas-bekasi-enak-dan-jadi-favorit-banyak-orang-nih?page=all", modelFoods)
                }
            }
        } catch (e: IOException) {
            throw e
        }

        return modelFoods
    }

    private suspend fun webTerstruktur(webUrl: String, modelFoods: ArrayList<ModelFoods>) {
        val url = webUrl
        val doc: Document = withContext(Dispatchers.IO) { Jsoup.connect(url).get() }

        val elements: Elements = doc.select("div.split-page")

        val size: Int = elements.size
        for (index: Int in 0 until size) {
            val foodImg: String = elements.select("img").eq(index).attr("data-src")
            val foodSplit = elements.select("h2").eq(index).text().split(" ")
            val foodName = foodSplit.subList(1, foodSplit.size).joinToString(" ")

            val foodDetail: String = elements.select("p").eq(index).text()

            Log.d("cek scrap", "FoodName: $foodName \n Img: $foodImg \n Index: $index \n Detail: $foodDetail")
            modelFoods.add(ModelFoods(foodImg, foodName, foodDetail, index.toString()))
        }
    }

    private suspend fun webTidakTerstruktur(webUrl: String, modelFoods: ArrayList<ModelFoods>) {
        var url = webUrl
        val document = withContext(Dispatchers.IO) { Jsoup.connect(url).get() }
        val elements = document.select("h2:matchesOwn(\\d+\\.)")

        val size: Int = elements.size
        if (elements.isEmpty()) {
            Log.d("cek scrap", "No elements found")
        } else {
            var currentIndex = 0
            val size: Int = elements.size

            while (currentIndex < size) {
                val foodSplit = elements[currentIndex].text().split(" ")
                val foodName = foodSplit.subList(1, foodSplit.size).joinToString(" ")

                var foodImg = ""

                for (index: Int in 1 until foodSplit.size) {
                    if (strCity.equals("Kabupaten Bogor") ||
                        strCity.equals("Kota Bogor")
                    ) {
                        foodImg = document.select("img[alt*=${foodSplit[index]}]").attr("src")
                    } else {
                        foodImg = document.select("img[alt*=${foodSplit[index]}]").attr("data-src")
                    }

                    if (foodImg.isNotEmpty()) break
                }

                if ((strCity.equals("Kabupaten Bogor") ||
                            strCity.equals("Kota Bogor")
                            ) &&
                    foodName.contains("Dodongkal")
                ) {
                    foodImg = document.select("img[alt*=dongkal]").attr("src")
                }

                val foodDetailList = mutableListOf<String>()

                var currentElement = elements[currentIndex].nextElementSibling()

                while (currentElement != null && currentElement.tagName() != "h2") {
                    if (strCity.equals("Kabupaten Bogor") ||
                        strCity.equals("Kota Bogor")
                    ) {
                        if (currentElement.hasClass("p-txt")) {
                        } else if (currentElement.tagName() == "p") {
                            foodDetailList.add(currentElement.text())
                        }
                    } else {
                        if (currentElement.hasClass("p-txt")) {
                            foodDetailList.add(currentElement.text())
                        }
                    }

                    currentElement = currentElement.nextElementSibling()
                }

                val foodDetail = foodDetailList.joinToString("\n\n")

                Log.d("cek scrap", "FoodName: $foodName \n Img: $foodImg \n Index: $currentIndex \n Detail: $foodDetail")
                modelFoods.add(ModelFoods(foodImg, foodName, foodDetail, currentIndex.toString()))

                currentIndex++
            }
        }
    }
}
