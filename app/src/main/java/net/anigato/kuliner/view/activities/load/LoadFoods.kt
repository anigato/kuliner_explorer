package net.anigato.kuliner.view.activities.load

import android.os.AsyncTask
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import net.anigato.kuliner.data.model.food.ModelFoods
import net.anigato.kuliner.view.foodInterface.IJsoupDataFood
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException

class LoadFoods(var activity: AppCompatActivity?): AsyncTask<Void, Void, ArrayList<ModelFoods>>(){
    private var modelFoods: ArrayList<ModelFoods> = ArrayList()
    private var loadedData = activity as IJsoupDataFood
    var strCity: String = "Kota Bandung"
    override fun doInBackground(vararg params: Void?): ArrayList<ModelFoods> {
        try {
            var url: String = ""

            if (strCity.equals("Kota Bandung")) {
                url = "https://www.idntimes.com/food/dining-guide/naufal-al-rahman-1/makanan-khas-bandung-yang-paling-populer?page=all"

            } else if (strCity.equals("Kabupaten Cilacap")) {
                // Ubah URL jika city adalah Kabupaten Cilacap
                url = "https://www.idntimes.com/food/dining-guide/naufal-al-rahman-1/makanan-khas-cilacap-paling-sedap?page=all"

            }



            val doc: Document = Jsoup.connect(url).get()

            val div: Elements = doc.select("div.split-page")

            val size: Int = div.size
            for (index: Int in 0 until size){
//                    ambil gambar dengan img yang memiliki src
                val imgUrl: String = div.select("img").eq(index).attr("data-src")
//                    ambil judul dari h3
                val title: String = div.select("h2").eq(index).text()
//                    ambil detail dari p
                val details: String = div.select("p").eq(index).text()
//                    modelFoods.add(ModelFoods("https://www.orami.co.id"+imgUrl, title, details))
                modelFoods.add(ModelFoods(imgUrl, title, details, index.toString()))
//                Log.d("cek load food", "$imgUrl, $title, $details, $index")
            }


        }catch (e: IOException){
            e.printStackTrace()
        }

        return modelFoods

    }

    override fun onPostExecute(result: ArrayList<ModelFoods>?) {
        loadedData.getWebData(result!!)
    }
}