package net.anigato.kuliner.view.activities.load

import android.os.AsyncTask
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import net.anigato.kuliner.view.foodInterface.ILoadDetailFood
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException

class LoadDetailFoods (var activity: AppCompatActivity, var urlIterasi: String):
    AsyncTask<Void, Void, ArrayList<String>>() {

    private var details: ArrayList<String>? = ArrayList()
    private var loader = activity as ILoadDetailFood
    var strCity: String = "Kota Bandung"

    override fun doInBackground(vararg params: Void?): ArrayList<String> {
        Log.d("cekstrCity", "$strCity")

        try{

            var url: String = ""

            if (strCity.equals("Kota Bandung")) {
                url = "https://www.idntimes.com/food/dining-guide/naufal-al-rahman-1/makanan-khas-bandung-yang-paling-populer?page=all"

            } else if (strCity.equals("Kabupaten Cilacap")) {
                // Ubah URL jika city adalah Kabupaten Cilacap
                url = "https://www.idntimes.com/food/dining-guide/naufal-al-rahman-1/makanan-khas-cilacap-paling-sedap?page=all"

            }

            Log.d("CEK iterasi", urlIterasi)

            val doc: Document = Jsoup.connect(url).get()
            val splitPageElements: Elements = doc.select("div.split-page") // Mengambil semua elemen "split-page"
            val indexToProcess: Int = urlIterasi.toInt() // Indeks elemen "split-page" sesuai iterasi (indeks dimulai dari 0)

            if (indexToProcess >= 0 && indexToProcess < splitPageElements.size) {
                val splitPageToProcess: Element = splitPageElements[indexToProcess] // Mengambil elemen "split-page" sesuai indeks

                val pElementsInSplitPage: Elements = splitPageToProcess.select("p")

                splitPageToProcess.select("a:not([title])").remove()

                for (element in pElementsInSplitPage) {
                    if (element.text().isNotBlank()) {
                        details!!.add(element.text())
                    }
                }

                // Sekarang, Anda memiliki elemen-elemen <p> dari elemen "split-page" kelima dalam variabel "details"
            } else {
                // Tindakan yang sesuai jika elemen "split-page" kelima tidak ditemukan
            }

        }catch (e: IOException){
            e.printStackTrace()
        }

        Log.d("cekdetaildd", "$details")
        return details!!
    }

    override fun onPostExecute(result: ArrayList<String>?) {
        loader.getDetails(result!!)
    }

}