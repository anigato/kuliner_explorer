package net.anigato.kuliner.view.activities.load

import android.os.AsyncTask
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import net.anigato.kuliner.data.model.food.ModelFoods
import net.anigato.kuliner.view.foodInterface.IJsoupDataFood
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import kotlin.properties.Delegates

class LoadFoods(var activity: AppCompatActivity?, var strCity: String?): AsyncTask<Void, Void, ArrayList<ModelFoods>>(){
    private var modelFoods: ArrayList<ModelFoods> = ArrayList()
    private var loadedData = activity as IJsoupDataFood
    override fun doInBackground(vararg params: Void?): ArrayList<ModelFoods> {
        Log.d("cek load food", "$strCity")
        try {


            if (strCity.equals("Kabupaten Bogor") ||
//                strCity.equals("Kota Bandung") ||
                strCity.equals("Kota Bogor")
            ){
                WebTidakTerstruktur("https://www.detik.com/jabar/kuliner/d-6714903/10-makanan-khas-bogor-rekomendasi-untuk-pecinta-kuliner")
            }

            if( strCity.equals("Kabupaten Bandung") ||
                strCity.equals("Kabupaten Bandung Barat") ||
//                strCity.equals("Kota Bandung") ||
                strCity.equals("Kota Cimahi")
            ) {
                WebTidakTerstruktur("https://www.klook.com/id/blog/makanan-khas-bandung/")
            }

            if (strCity.equals("Kabupaten Bekasi") ||
                strCity.equals("Kota Bandung") ||
                strCity.equals("Kota Bekasi")

            ){
                WebTerstruktur("https://www.idntimes.com/food/dining-guide/fina-wahibatun-nisa/10-makanan-khas-bekasi-enak-dan-jadi-favorit-banyak-orang-nih?page=all")
            }



        }catch (e: IOException){
            e.printStackTrace()
        }
//
        return modelFoods

    }

    fun WebTerstruktur(webUrl: String) {
        val url = webUrl
        val doc: Document = Jsoup.connect(url).get()

        val elements: Elements = doc.select("div.split-page")

        val size: Int = elements.size
        for (index: Int in 0 until size){
            //                    ambil gambar dengan img yang memiliki src
            val foodImg: String = elements.select("img").eq(index).attr("data-src")
            //                    ambil judul dari h3

            val foodSplit = elements.select("h2").eq(index).text().split(" ")
            val foodName = foodSplit.subList(1, foodSplit.size).joinToString(" ")

            //                    ambil detail dari p
            val foodDetail: String = elements.select("p").eq(index).text()
            //                    modelFoods.add(ModelFoods("https://www.orami.co.id"+imgUrl, title, details))
            Log.d("cek scrap", "FoodName: $foodName \n Img: $foodImg \n Index: $index \n Detail: $foodDetail")
            modelFoods.add(ModelFoods(foodImg, foodName, foodDetail, index.toString()))
            //                Log.d("cek load food", "$imgUrl, $title, $details, $index")
        }
    }

    fun WebTidakTerstruktur(webUrl: String) {
        var url = webUrl
        val document = Jsoup.connect(url).get()
        val elements = document.select("h2:matchesOwn(\\d+\\.)") // Selector untuk menargetkan h2 yang memiliki format 1. 2. dst
        val size: Int = elements.size
        if (elements.isEmpty()) {
            Log.d("cek scrap", "No elements found")
        } else {
            var currentIndex = 0 // Indeks elemen <h2> saat ini
            val size: Int = elements.size

            while (currentIndex < size) {
                val foodSplit = elements[currentIndex].text().split(" ")
                val foodName = foodSplit.subList(1, foodSplit.size).joinToString(" ")
//                val foodImg = document.select("img[alt*=$foodName]").attr("data-src")
                var foodImg = ""

                for (index: Int in 1 until foodSplit.size){
                    if(
                        strCity.equals("Kabupaten Bogor") ||
//                    strCity.equals("Kota Bandung") ||
                        strCity.equals("Kota Bogor")
                    ){
                        foodImg = document.select("img[alt*="+foodSplit[index]+"]").attr("src")
                    }else {
                        foodImg = document.select("img[alt*="+foodSplit[index]+"]").attr("data-src")
                    }
                    Log.d("Cek scrap blm kondisi", "$foodImg")
                    if(foodImg.isNotEmpty()) break
                    Log.d("Cek scrap udh kondisi", "$foodImg")
                }

                if (
                    (
                        strCity.equals("Kabupaten Bogor") ||
//                        strCity.equals("Kota Bandung") ||
                        strCity.equals("Kota Bogor")
                    ) &&
                    foodName.contains("Dodongkal")){
                    foodImg = document.select("img[alt*=dongkal]").attr("src")
                }

                val foodDetailList = mutableListOf<String>()

                // Mencari elemen-elemen <div> dengan kelas 'p-txt' yang berada tepat di bawah elemen <h2>
                var currentElement = elements[currentIndex].nextElementSibling()

                while (currentElement != null && currentElement.tagName() != "h2") {

                    if (
                        strCity.equals("Kabupaten Bogor") ||
//                        strCity.equals("Kota Bandung") ||
                        strCity.equals("Kota Bogor")
                    ){
                        if (currentElement.hasClass("p-txt")) {
                        } else if(currentElement.tagName() == "p") {
                            foodDetailList.add(currentElement.text())
                        }
                    } else{
                        if (currentElement.hasClass("p-txt")) {
                            foodDetailList.add(currentElement.text())
                        }
                    }

                    currentElement = currentElement.nextElementSibling()
                }

                // Menggabungkan semua teks dari elemen-elemen <div> menjadi satu string
                val foodDetail = foodDetailList.joinToString("\n\n")

                Log.d("cek scrap", "FoodName: $foodName \n Img: $foodImg \n Index: $currentIndex \n Detail: $foodDetail")

                // Pindah ke indeks berikutnya
                currentIndex++

                modelFoods.add(ModelFoods(foodImg, foodName, foodDetail, currentIndex.toString()))
            }
        }
    }

    override fun onPostExecute(result: ArrayList<ModelFoods>?) {
        loadedData.getWebData(result!!)
    }
}











