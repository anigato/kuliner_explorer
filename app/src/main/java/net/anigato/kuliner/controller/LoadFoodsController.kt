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
                "Kabupaten Bandung", "Kabupaten Bandung Barat", "Kota Bandung", "Kota Cimahi" -> {
                    webWithImage(false, "https://www.klook.com/id/blog/makanan-khas-bandung/", modelFoods)
                }
                "Kabupaten Bekasi", "Kota Bekasi" -> {
                    webWithImage(true, "https://www.idntimes.com/food/dining-guide/fina-wahibatun-nisa/10-makanan-khas-bekasi-enak-dan-jadi-favorit-banyak-orang-nih?page=all", modelFoods)
                }
                "Kabupaten Bogor", "Kota Bogor" -> {
                    webWithImage(false, "https://www.detik.com/jabar/kuliner/d-6714903/10-makanan-khas-bogor-rekomendasi-untuk-pecinta-kuliner", modelFoods)
                }
                "Kabupaten Ciamis" -> {
                    webNoImage(false,"https://jabar.inews.id/berita/10-makanan-khas-ciamis-yang-melegenda-mudah-dikenali-dari-bentuk-dan-rasa/all", modelFoods)
                }
                "Kabupaten Cianjur" -> {
                    webNoImage(false, "https://katadata.co.id/lifestyle/varia/658505205afe7/10-makanan-khas-cianjur-yang-lezat-pecinta-kuliner-harus-coba", modelFoods)
                }
                "Kabupaten Cirebon", "Kota Cirebon" -> {
                    webNoImage(false, "https://www.cnnindonesia.com/gaya-hidup/20231128131811-267-1029985/15-rekomendasi-makanan-khas-cirebon-legendaris", modelFoods)
                }
                "Kabupaten Garut" -> {
                    webNoImage(false, "https://www.orami.co.id/magazine/makanan-khas-garut?page=all", modelFoods)
                }
                "Kabupaten Indramayu" -> {
                    webNoImage(false, "https://jabar.inews.id/berita/makanan-khas-indramayu-ada-kuliner-untuk-tolak-bala", modelFoods)
                }
                "Kabupaten Karawang" -> {
                    webNoImage(false, "https://infokost.id/blog/makanan-khas-karawang/137125/", modelFoods)
                }
                "Kabupaten Kuningan" -> {
                    webWithImage(false, "https://dewatiket.id/blog/makanan-khas-kuningan/", modelFoods)
                }
                "Kabupaten Majalengka" -> {
                    webNoImage(false, "https://bobobox.com/blog/oleh-oleh-khas-majalengka/", modelFoods)
                }
                "Kabupaten Pangandaran" -> {
                    webWithImage(false, "https://jabar.inews.id/berita/10-makanan-khas-pangandaran-lezatnya-bikin-wisatawan-ketagihan/all", modelFoods)
                }
                "Kabupaten Subang" -> {
                    webNoImage(false, "https://jabar.inews.id/berita/15-makanan-khas-subang-nomor-7-diolah-dengan-pasir-panas/all", modelFoods)
                }
                "Kabupaten Sukabumi", "Kota Sukabumi" -> {
                    webNoImage(false, "https://www.detik.com/jabar/kuliner/d-6322136/10-makanan-khas-sukabumi-paling-populer", modelFoods)
                }
                "Kabupaten Sumedang" -> {
                    webNoImage(false, "https://jabar.inews.id/berita/makanan-khas-sumedang-tak-hanya-tahu-goreng-ternyata-banyak-ragam-dan-varian/all", modelFoods)
                }
                "Kabupaten Tasikmalaya", "Kota Tasikmalaya" -> {
                    webNoImage(false, "https://www.orami.co.id/magazine/makanan-khas-tasikmalaya?page=all", modelFoods)
                }
                "Kota Banjar" -> {
                    webNoImage(false, "https://www.yummy.co.id/artikel/kuliner/makanan-khas-banjar-yang-enak", modelFoods)
                }
                "Kota Depok" -> {
                    webNoImage(false, "https://infokost.id/blog/makanan-khas-depok-wajib-coba/110758/", modelFoods)
                }
            }
        } catch (e: IOException) {
            throw e
        }

        return modelFoods
    }

    private suspend fun webNoImage(terstruktur: Boolean, webUrl: String, modelFoods: ArrayList<ModelFoods>) {
        val url = webUrl
//        val document = withContext(Dispatchers.IO) { Jsoup.connect(url).get() }
        val document = withContext(Dispatchers.IO) {
            Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("Referer", "https://www.google.com")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .get()
        }

        val listFoodImage: Map<String, String> = when (strCity) {
            "Kabupaten Ciamis" -> mapOf(
                "Galendo" to "https://dispar.ciamiskab.go.id/wp-content/uploads/2023/08/inCollage_20210607_161427906.jpg",
                "Sale Pisang" to "https://static.republika.co.id/uploads/images/inpicture_slide/sale_200611194502-768.jpg",
                "Mi Golosor" to "https://wisata-id.com/wp-content/uploads/2023/03/cara-membuat-mie-glosor-2-744x445.jpg",
                "Dendeng Sapi" to "https://img.inews.co.id/media/822/files/inews_new/2023/08/11/dendeng_ciamis.jpg",
                "Abon Sapi" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Ilustrasi-abon-sapi.jpg",
                "Gula Kelapa" to "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgF1hnl1aUPsJQIkxO0HFyBFUXhCV5nOpYeHh7-zIG7hLMMu1EPOgQVGnUub2UUfYZNfE_2IR9AY97QscVMdcuOqkr4uq1w2ulQuFBExaMdF9vq-WubyFtFPipLDw0vSd6q9BJW5Zc-Bxu4/w1200-h630-p-k-no-nu/Gulakawung1.jpg",
                "Kicimpring" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Kecimpring-Keripik-Khas-Ciamis.jpg",
                "Surabi" to "https://awsimages.detik.net.id/community/media/visual/2023/07/30/menyantap-serabi-di-jembatan-cirahong-ciamis-2_169.jpeg",
                "Saroja" to "https://images.tokopedia.net/img/cache/500-square/VqbcmM/2022/7/28/0c8382ad-8afe-4d52-ae16-e8c3f694520d.jpg",
                "Cocorot" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Ilustrasi-cerorot.jpg"
            )

            "Kabupaten Cianjur" -> mapOf(
                "Kue Moci" to "https://imgx.sonora.id/crop/0x0:0x0/x/photo/2021/07/19/kue-mochi-1jpg-20210719051643.jpg",
                "Liwet Jantung Pisang" to "https://www.metromedianews.co/wp-content/uploads/2023/01/Screenshot_20230114-235428_1.jpg",
                "Geco" to "https://img.inews.co.id/media/600/files/networks/2022/06/28/4cf71_mengenal-geco-makanan-lezat-khas-cianjur-sejak-1948.jpg",
                "Sate Maranggi" to "https://cdn1-production-images-kly.akamaized.net/qPagiNKtG1Ntacj0Syt2FFH63-o=/1200x675/smart/filters:quality(75):strip_icc():format(jpeg)/kly-media-production/medias/2472773/original/066100700_1543290858-resep-sate-maranggi-khas-cianjur.jpg",
                "Pesmol Kembung Cianjur" to "https://carica.id/wp-content/uploads/2022/01/PesmolKembung.jpg",
                "Roti Manis Ten Keng Cu" to "https://bake.co.id/wp-content/uploads/2024/02/word-image-5463-1.jpeg",
                "Tauco" to "https://awsimages.detik.net.id/community/media/visual/2022/02/16/tauco-cap-meong-ny-tasma_169.jpeg?w=1200",
                "Manisan Buah" to "https://cdn1-production-images-kly.akamaized.net/nCvvuZNJw6574EKzxkcziOp5zB8=/1200x675/smart/filters:quality(75):strip_icc():format(jpeg)/kly-media-production/medias/2540343/original/074570000_1545107797-manisan_cianjur_.jpg",
                "Sambal Hejo" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Sambel-Hejo-Khas-Cianjur.jpg",
                "Laksa Cianjur" to "https://media.sukabumiupdate.com/media/2023/08/19/1692446453_64e0aef56e924_BXVHJpbLwxR8TFTIDMoq.webp",
            )

            "Kabupaten Cirebon", "Kota Cirebon" -> mapOf(
                "Empal Gentong" to "https://asset.kompas.com/crops/jxmPpZY4EpJwyZGuE34cMaFIuHo=/0x0:1000x667/750x500/data/photo/2021/05/18/60a36dc5aa887.jpg",
                "Nasi Jamblang" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Nasi-Jamblang-Khas-Cirebon.jpg",
                "Nasi Lengko" to "https://asset.kompas.com/crops/16NAGd0HBDBRH3EFFYqN687TUp8=/67x0:911x563/750x500/data/photo/2022/01/14/61e16cb8c8133.jpg",
                "Docang" to "https://pict.sindonews.net/dyn/620/pena/news/2020/06/28/701/83718/lezatnya-docang-makanan-khas-cirebon-xba.png",
                "Tahu Gejrot" to "https://cdn0-production-images-kly.akamaized.net/3leWNUOKOf4ScMspokgcFliRB7E=/640x360/smart/filters:quality(75):strip_icc():format(jpeg)/kly-media-production/medias/2535029/original/089721700_1544777395-tahu_gejrot.jpg",
                "Mi Koclok" to "https://asset.kompas.com/crops/R0W23caKi5Yc0dq1COFsWotY5yg=/0x500:750x1000/750x500/data/photo/2022/07/22/62d9e6503726a.jpeg",
                "Es Cuwing Kartini" to "https://asset-2.tstatic.net/tribunnews/foto/bank/images/es-cuwing-khas-cirebon.jpg",
                "Mi Colot" to "https://www.heydayat.com/wp-content/uploads/2021/07/Seporsi-Mie-Colot-Cirebon-Di-Atas-Meja.jpg",
                "Kue Tapel" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Kue-Tapel-Khas-Cirebon.jpg",
                "Tongseng Battembat" to "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEhpiylXprO-do75akRDcFHPBw9wAlOdpiuTmByUs37CZyzpcJuwVsSfhF55wI6YPkBty6-_7pcSbwgKBuqO3eK2AS30vyArZLwbtmnUJ04QuhMn-BYaN6MB8xB50BIcxnwS4LSPjrM3PZI7/s1600/kuliner-cirebon-yang-terkenal-bernavita-fiana-kaskus+%25283%2529.jpg",
                "Sate Kalong" to "https://cdn.idntimes.com/content-images/post/20211102/164272198-261128395639909-8125028002473144564-n-c7b1ad6f29aebfc85321de3217a28a7b_600x400.jpg",
                "Pedesan Entog" to "https://img.kurio.network/W9-pnTiIIbsOsCocXkWHgRpbwAM=/1200x900/filters:quality(80)/https://kurio-img.kurioapps.com/22/03/10/5de39287-3b6d-464f-843d-b91d366a5845.jpe",
                "Es Duren" to "https://awsimages.detik.net.id/community/media/visual/2024/02/27/es-puter-durian-pak-roni-di-cirebon_169.jpeg?w=1200",
                "Mi Petruk" to "https://fastly.4sqi.net/img/general/600x600/2898937_e3IyYPaViBTQERgFvdpkQMh_C64OrueyjS1typ_5LY4.jpg",
                "Serabi" to "https://i.ytimg.com/vi/3igPfhOvAR0/maxresdefault.jpg",
            )

            "Kabupaten Garut" -> mapOf(
                "Dodol" to "https://upload.wikimedia.org/wikipedia/commons/f/f9/Dodol_Garut_Cihampelas_Bandung.JPG",
                "Burayot" to "https://www.harapanrakyat.com/wp-content/uploads/2020/12/18.-Burayot-Khas-Garut-Camilan-Tradisional-dengan-Citarasa-yang-Melegenda.jpg",
                "Jeruk Garut" to "https://gentrapriangan.com/wp-content/uploads/2023/02/IMG-20230215-WA0016.jpg",
                "Ladu" to "https://img-global.cpcdn.com/recipes/3ba5a489209edfbc/680x482cq70/ladu-khas-garut-foto-resep-utama.jpg",
                "Angleng Dan Aneka Wajit" to "https://infogarut.id/upload/postingan/1644808881.jpg",
                "Kerupuk Kulit Khas Garut (Dorokdok)" to "https://indonesiakaya.com/wp-content/uploads/2020/10/1589_2._Dorokdok_terbuat_dari_kulit_sapi_dan_kerbau.jpg",
                "Pindang Ikan" to "https://cdn-brilio-net.akamaized.net/news/2020/01/17/177347/1160144-1000xauto-resep-olahan-pindang.jpg",
                "Sambal Cibiuk" to "https://cdn-1.timesmedia.co.id/images/2022/09/01/sambal-cibiuk-khas-Garut.jpg",
                "Ceprus" to "https://infogarut.id/upload/postingan/1644811081.jpg",
                "Nasi Liwet Garut" to "https://infogarut.id/public/upload/postingan/1645172493.jpg",
                "Soto Ayam Garut" to "https://assets.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/2023/04/30/Soto-ayam-1-2352566866.jpg",
                "Cimol Kering (Moring)" to "https://assets.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/2023/02/06/968582695.jpg",
                "Surabi Garut" to "https://assets.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/2023/04/29/Surabi-1-1437173940.jpg",
                "Opak Garut" to "https://www.jelajahgarut.com/wp-content/uploads/2015/07/DSC_0054.jpg",
                "Endog Lowo Atau Emplod" to "https://infogarut.id/upload/postingan/1664347886_Endog%20Lewo.jpg",
                "Kue Awug" to "https://asset-a.grid.id/crop/0x0:0x0/x/photo/2024/03/14/awug-makanan-khas-garutjpg-20240314105707.jpg",
            )

            "Kabupaten Indramayu" -> mapOf(
                "Pedesan Entog" to "https://pergiyuk.com/wp-content/uploads/2020/11/Pedesan-Entog.jpg",
                "Pindang Gombyang Manyung" to "https://cdn1-production-images-kly.akamaized.net/NfNkwWrw2qtbborKBWbVyRd0tN0=/1200x675/smart/filters:quality(75):strip_icc():format(jpeg)/kly-media-production/medias/2730452/original/020055900_1550325815-Gombyang.jpg",
                "Nasi Lengko" to "https://assets.pikiran-rakyat.com/crop/0x0:0x0/750x500/photo/2023/11/01/3093183287.jpg",
                "Orog-orog" to "https://assets.pikiran-rakyat.com/crop/0x0:0x0/1200x675/photo/2024/06/16/2220346986.jpg",
                "Bubur Glintir" to "https://assets.pikiran-rakyat.com/crop/0x0:0x0/1200x675/photo/2024/06/19/3492931878.jpg",
                "Rumbah" to "https://img-global.cpcdn.com/recipes/ed16c1ef66409e12/680x482cq70/sambal-rumbah-asem-khas-indramayu-foto-resep-utama.jpg",
                "Burbacek" to "https://awsimages.detik.net.id/community/media/visual/2019/10/13/d2730151-2984-4da8-9ce5-5d77884b17e6.jpeg?w=600&q=90",
                "Mi Ragit" to "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1617449879/vjwn5lpzm58rslmqikbk.jpg",
                "Geblog" to "https://assets.pikiran-rakyat.com/crop/0x0:0x0/750x500/photo/2023/07/02/2946220651.jpg",
                "Cimplo" to "https://nabarentcar.com/wp-content/uploads/2015/07/Source-By-Vhiofficial-768x614.webp",
                "Blengep Cotot" to "https://img-global.cpcdn.com/recipes/7a63f1f52af2d98f/680x482cq70/blengep-cotot-khas-indramayu-foto-resep-utama.jpg",
                "Kue Koci" to "https://asset-a.grid.id/crop/0x0:0x0/x/photo/2018/12/27/1770273210.jpg",
                "Jalabriya" to "https://img-global.cpcdn.com/recipes/2d0a150986af6b29/680x482cq70/kue-jalabia-jalabria-khas-jawa-barat-foto-resep-utama.jpg",
            )

            "Kabupaten Karawang" -> mapOf(
                "Soto Tangkar" to "https://img.okezone.com/content/2020/09/30/301/2286369/jalan-jalan-ke-karawang-cicipi-kuliner-malam-soto-tangkar-lUZeX7tR2i.jpg",
                "Soto Gempol" to "https://static.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/p1/803/2024/01/02/image-44-1585368432.png",
                "Pepes Walahar" to "https://awsimages.detik.net.id/community/media/visual/2022/06/17/pepes-walahar-h-dirja-8.jpeg?w=1200",
                "Sangtau" to "https://static.promediateknologi.id/crop/0x0:0x0/0x0/webp/photo/p2/108/2023/07/15/IMG_20230715_111447-253358325.jpg",
                "Jojongkong" to "https://assets-pergikuliner.com/uploads/bootsy/image/17533/Jojongkong__www.gadis.co.id_.jpg",
                "Bandeng Gepuk" to "https://upload.wikimedia.org/wikipedia/commons/3/39/Bandeng_Gepuk.jpeg",
                "Olahan Tutut" to "https://awsimages.detik.net.id/community/media/visual/2021/07/05/tutut-karawang-1.jpeg?w=1200",
                "Gandasturi" to "https://kbeonline.id/wp-content/uploads/2023/11/IgnPbBFdkPZbsfXHWpUe2ymezPHPp2Np-31363033343330343437d41d8cd98f00b204e9800998ecf8427e.webp",
                "Kue Gonjing" to "https://img.herstory.co.id/articles/archive_20220621/kue-pancong-20220621-144645.jpg",
                "Pepes Ikan Jambal" to "https://cdn.antaranews.com/cache/1200x800/2022/05/10/WhatsApp-Image-2022-05-10-at-6.34.03-PM-1.jpeg",
            )

            "Kabupaten Majalengka" -> mapOf(
                "Jalakotek" to "https://img-global.cpcdn.com/recipes/c782fb6bf3710239/680x482cq70/jalakotek-khas-majalengka-foto-resep-utama.jpg",
                "Oncom Goreng" to "https://carica.id/wp-content/uploads/2022/02/OncomGoreng.jpg",
                "Kalua Jeruk" to "https://indo1.id/wp-content/uploads/2023/06/WhatsApp-Image-2023-06-05-at-01.13.35.jpeg",
                "Gula Cakar" to "https://asset.kompas.com/crops/glgAo2mY8P49ySxDfNZrySL-SKs=/1x0:1271x847/750x500/data/photo/2024/05/05/6637b54e87a6c.jpg",
                "Rempeyek" to "https://static.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/p1/287/2024/01/10/rempeyek-kacang-3605645019.jpg",
                "Kecap Majalengka" to "https://www.gotravelly.com/blog/wp-content/uploads/2020/11/kecap-majalengka.jpg",
                "Ampas Kecap" to "https://thumb.viva.id/intipseleb/1265x711/2023/05/29/647466673339e-ampas-kecap.jpg",
                "Sambal Kelud" to "https://cdn.idntimes.com/content-images/community/2020/02/83183007-117031886413010-5283717201158544679-n-c9ea1d6de9d7c5eb8ef8d1f771bcedda.jpg",
                "Pepes Jeroan" to "https://asset-2.tstatic.net/tribunnewswiki/foto/bank/images/Ilustrasi-pepes-jeroan-khas-Majalengka2.jpg",
                "Emping Melinjo" to "https://images.tokopedia.net/img/cache/500-square/product-1/2017/1/23/4045835/4045835_5707d8da-2399-4fc6-949e-9049f9789a3e_1600_1200.jpg",
                "Brem Majalengka" to "https://awsimages.detik.net.id/community/media/visual/2022/03/26/brem-khas-majalengka_169.jpeg?w=1200",
                "Mangga Gedong Gincu" to "https://paxelmarket.co/wp-content/uploads/2022/12/IMG-20221203-WA0013-4.jpg",
                "Pisang Apuy" to "https://www.agronet.co.id/files/media/news/images/645x372/-_181007220209-550.png",
                "Buah Duwet" to "https://carica.id/wp-content/uploads/2022/02/BuahDuwet.jpg",
                "Durian Sinapeul" to "https://cdn0-production-images-kly.akamaized.net/VvjrIe9tSfnJNNlq8fsB8wamDE8=/1200x675/smart/filters:quality(75):strip_icc():format(jpeg)/kly-media-production/medias/3381906/original/027661900_1613753845-Penampakan_durian_sinapeul_di_Kampung_Durian_Majalengka.jpg",
            )

            "Kabupaten Subang" -> mapOf(
                "Oncom Dawuan" to "https://images.bisnis.com/posts/2021/03/05/1364267/oncom-lead2.jpg",
                "Bubuy Ayam" to "https://awsimages.detik.net.id/community/media/visual/2023/03/11/bubuy-ayam-khas-subang-1_169.jpeg?w=650",
                "Nasi Liwet" to "https://img.okezone.com/content/2020/09/14/301/2277391/jalan-jalan-ke-subang-jangan-lupa-santap-nasi-liwet-6b0mrv1xma.jpg",
                "Dodol Nanas" to "https://depostjogja.com/photo/plugin/article/2021/1636448256_1-org.jpg",
                "Tutut Soding" to "https://bobobox.com/blog/wp-content//uploads/2024/01/Tutut-Soding.webp",
                "Abon Jantung Pisang" to "https://bobobox.com/blog/wp-content//uploads/2024/01/Abon-Jantung-Pisang.webp",
                "Abon Nila" to "https://nabarentcar.com/wp-content/uploads/2024/01/Abon-Nila-Source-By-cookpad.com_-768x614.webp",
                "Kurupuk Malarat" to "https://jatinangorekspres.com/wp-content/uploads/2024/04/Screenshot-2024-04-18-140944.png",
                "Gitrek Singkong" to "https://id-test-11.slatic.net/p/f1669618738bf596ea9eeeed7746a857.jpg",
                "Delipel" to "https://www.plutsubang.com/wp-content/uploads/2016/05/f1f0365d94da3ac533b7485963ba90b9.jpg",
                "Papais Cisaat" to "https://cdns.klimg.com/mav-prod-resized/1200x630/bg/newsOg/2024/5/15/1715766662554-1u2ku.jpeg",
                "Bolu Nanas" to "https://bobobox.com/blog/wp-content//uploads/2024/01/Bolu-Nanas-Subang.webp",
                "Gepuk" to "https://cms.pasundanekspres.id/storage/uploads/conten/yp0IpzE7lNaYSJRR.webp",
                "Ikan Lapan Crispy" to "https://nabarentcar.com/wp-content/uploads/2024/01/Ikan-Lapan-Crispy-Source-By-Idn-Times-768x614.webp",
                "Peuyeum Ketan" to "https://radarjabar.disway.id/upload/2d30ca4f33ec1f08dfd80006b5294e55.jpg",
                "Keripik Nanas" to "https://cdn.idntimes.com/content-images/community/2019/01/keripik-buah-nanas-702x410-a19e6916ef1b6395fe6cfc6ad74dcc8a.jpg",
            )

            "Kabupaten Sukabumi", "Kota Sukabumi" -> mapOf(
                "Mochi" to "https://assets.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/2023/04/27/kiw-1851097705.png",
                "Bolu Pisang" to "https://amorcakes.co.id/wp-content/uploads/2020/08/E2-scaled.jpg",
                "Kue Jahe" to "https://cdn.idntimes.com/content-images/community/2021/10/screenshot-2021-10-02-05-03-57-64-1c337646f29875672b5a61192b9010f9-6d2cbb4bbec2340f770674be006d2944-42a8e66b7211486caa32514cd73482a2_600x400.jpg",
                "Roti Priangan" to "https://asset-2.tstatic.net/travel/foto/bank/images/roti-priangan.jpg",
                "Sekoteng Singapore" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Sekoteng-Singapore-Khas-Sukabumi.jpg",
                "Sagon Bakar" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Sagon-Bakar-Khas-Sukabumi.jpg",
                "Serabi Durian" to "https://pdbifiles.nos.jkt-1.neo.id/files/2017/10/26/adadeh_DSCN0122-1.jpg",
                "Geco" to "https://media.sukabumiupdate.com/media/2023/06/19/1687163658_6490130ae2862_ouyFGfrIeQ4NO8dXL0FT.webp",
                "Deblo" to "https://carica.id/wp-content/uploads/2022/01/Deblo.jpg",
                "Bandros" to "https://cdn1-production-images-kly.akamaized.net/m6olHAnn8xCSCNrAvS8YxusLUNk=/0x0:5472x3084/1200x675/filters:quality(75):strip_icc():format(jpeg)/kly-media-production/medias/2890319/original/074537300_1566532605-bandros_HL.jpg",
            )

            "Kabupaten Sumedang" -> mapOf(
                "Tahu Sumedang" to "https://cdn.idntimes.com/content-images/community/2019/12/74797333-542110323274286-8157494286295741274-n-b54ed79f2b1cd7e417c721f536a1d039.jpg",
                "Oncom Sumedang" to "https://cdn.idntimes.com/content-images/community/2021/10/fromandroid-4107563de215b6a9505718a8db328067.jpg",
                "Ubi Cilembu" to "https://emitennews.com/uploads/news/image_1646115693.jpg",
                "Kadedemes" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Kadedemes-kuliner-legend-khas-Sumedang-terbuat-dari-kulit-singkong.jpg",
                "Asinan Sukasari" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Asinan-Sayur-Khas-Sukasari-Sumedang.jpg",
                "Emplod" to "https://sumedang.jabarekspres.com/wp-content/uploads/2022/12/emplod-e1671679495419.jpg",
                "Salak Bongkok" to "https://radarkuningan.disway.id/upload/95b754654c5c82d5b465b9f63887e60c.jpg",
                "Soto Bangko" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Soto-bongko.jpg",
                "Opak Ketan" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcREDWCIA6cKyg583a5VippSwSNivoACoooU8g&s",
                "Sawo Citali" to "https://assets.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/bisnisbandung/2019/10/SAWO.jpg",
            )

            "Kabupaten Tasikmalaya", "Kota Tasikmalaya" -> mapOf(
                "Nasi Cikur" to "https://www.maggi.id/sites/default/files/styles/home_stage_944_531/public/srh_recipes/e047da9238fc8fe892ceee8f46d5828c.jpg?h=26be6c10&itok=1j00IqcY",
                "Rengginang Oyek" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Rengginang-Singkong-Alias-Oyek.jpg",
                "Kolontong" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Kolontong-Camilan-Khas-Tasikmalaya.jpg",
                "Soto Ayam Pataruman" to "https://assets.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/bisnisbandung/2019/03/xvcxzvzv.jpg",
                "Wajit" to "https://assets.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/p1/1015/2023/09/03/images47_copy_700x465-1921303747.jpg",
                "Comet" to "https://awsimages.detik.net.id/community/media/visual/2023/01/16/camilan-comet-khas-tasikmalaya_169.jpeg?w=1200",
                "Citruk" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Citruk.jpg",
                "Bubur Tasik" to "https://www.harapanrakyat.com/wp-content/uploads/2023/10/Tempat-Makan-Bubur-Ayam-di-Tasikmalaya.jpg",
                "Opak" to "https://assets.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/ayobandung/images-bandung/post/articles/2017/01/26/15644/opak.jpg",
                "Sambal Goang" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Sambal-Goang-Khas-Tasikmalaya.jpg",
                "Kemplang" to "https://static.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/p1/130/2023/09/03/kemplang-3142165902.jpg",
                "Soto Tasik" to "https://asset-a.grid.id/crop/0x0:0x0/700x465/photo/2021/02/08/resep-soto-tasik-ini-pasti-rugi-20210208075522.jpg",
                "Keripik Sukun" to "https://assets.pikiran-rakyat.com/crop/0x0:0x0/x/photo/2024/01/10/2870195572.jpg",
                "Kalua Jeruk" to "https://img.okezone.com/content/2021/12/14/301/2517059/liburan-ke-tasikmalaya-ini-15-oleh-oleh-khas-bisa-dibeli-untuk-orang-tersayang-oax86Jelc2.jpg",
                "Cilok Goang" to "https://img-global.cpcdn.com/recipes/ded75c8ee90c661f/1200x630cq70/photo.jpg",
            )

            "Kota Banjar" -> mapOf(
                "Kupat Tahu" to "https://img-global.cpcdn.com/recipes/fd4e170cf92f0145/680x482cq70/kupat-tahu-bumbu-kacang-tanah-khas-banjar-patroman-jawa-barat-foto-resep-utama.jpg",
                "Pepes Sidat" to "https://awsimages.detik.net.id/community/media/visual/2023/02/04/pepes-ikan-sidat-yang-lezat-dan-berkhasiat-3.jpeg?w=4000",
                "Pepes Lubang" to "https://assets.promediateknologi.id/crop/0x0:0x0/750x500/webp/photo/2022/07/14/4119450698.jpeg",
                "Kacang Umpet" to "https://www.tangerangkota.go.id/files/berita/39369nikmatnya-kacang-umpet-manis-pedas-olahan-umkm-karawaci-eksis-39369.jpeg",
                "Rangicok" to "https://miro.medium.com/v2/resize:fit:800/1*88UtTRnFt9d9LAUQLHAZjQ.jpeg",
                "Mie Bebek" to "https://awsimages.detik.net.id/community/media/visual/2019/10/29/c10a0d27-e4e5-4945-aa62-bf28a7545a53_169.jpeg?w=600&q=90",
                "Galendo" to "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSj2Y_RV5-qpN3RyB1geKl6FDOX_DuEhmBMEw&s",
                "Nasi Timbel" to "https://upload.wikimedia.org/wikipedia/commons/9/90/Nasi_Timbel_Dara_Goreng.JPG",
                "Sale Pisang" to "https://asset-2.tstatic.net/priangan/foto/bank/images/Ilustrasi-sale-pisang-dari-Trenggalek.jpg",
                "Otok Owo" to "https://filebroker-cdn.lazada.co.id/kf/Sd4f30920d8684f92ba1e2f2d026f5d0cI.jpg",
                "Mie Lidi" to "https://images.tokopedia.net/img/cache/700/product-1/2017/6/9/11940281/11940281_031c5a37-37e8-4637-a752-d17c2823d790_2048_1872.jpg",
            )

            "Kota Depok" -> mapOf(
                "Rujak Cireng" to "https://cdn1-production-images-kly.akamaized.net/Ba1VBcv1b7mO4Nic1LhVJt409P0=/640x360/smart/filters:quality(75):strip_icc():format(jpeg)/kly-media-production/medias/2227836/original/016151400_1527239579-resep-cireng-bumbu-rujak.jpg",
                "Peyek Gale" to "https://www.astronauts.id/blog/wp-content/uploads/2022/08/Resep-Peyek-Gale-Camilan-Khas-Kota-Depok-yang-Lezat-Nikmat-1024x683.jpg",
                "Nastar Belimbing" to "https://cdn0-production-images-kly.akamaized.net/CYpg2ju7w8hZgaHZORKeDQbBJWQ=/199x374:707x882/1200x900/filters:quality(75):strip_icc():format(jpeg)/kly-media-production/medias/3126538/original/099164200_1589344558-shutterstock_1714073662.jpg",
                "Tempe Cokelat" to "https://ensroom.com/wp-content/uploads/2023/07/Tempe-Coklat-makanan-khas-depok.jpg",
                "Bakso Comberan" to "https://t-2.tstatic.net/wartakota/foto/bank/images/Bakso-Item-Pakde-Bewok-di-Beji-Depok-1.jpg",
                "Perkedel Bakar" to "https://awsimages.detik.net.id/community/media/visual/2020/11/17/perkedel-bakar-3.jpeg?w=700&q=90",
                "Biskuit Temma" to "https://pesonanusantara.co.id/images/upload//p/e/pes-bisma4.jpg",
                "Dodol Jambu Merah" to "https://blue.kumparan.com/image/upload/fl_progressive,fl_lossy,c_fill,q_auto:best,w_640/v1519571824/dodoljambu-nonaeva-asli-ngawi_gvedjg.jpg",
                "Jus Lidah Buaya" to "https://awsimages.detik.net.id/community/media/visual/2020/02/22/3e0dd9b0-14e1-4e46-bb52-5219cd5f0641.jpeg?w=650&q=90",
                "Jus Rumput Laut" to "https://media.istockphoto.com/id/930842038/id/foto/segelas-jus-rumput-gandum-dengan-rumput-gandum-muda-dan-bubuk-gandum-hijau.jpg?s=170667a&w=0&k=20&c=IaGogVE3kbLr6TgMfbKQdexgJeUOVb-o6eBQQxmyFoI=",
                "Selai Belimbing" to "https://infokost.id/blog/wp-content/uploads/2023/02/dcf60cd7-f0c4-401a-bac9-69926af36248-e1710830753448.webp",
                "Es Selendang Mayang" to "https://infokost.id/blog/wp-content/uploads/2023/02/tips_cara_membuat_es_selendang_mayang.webp",
            )
            else -> mapOf()
        }

        if (terstruktur) {
//            val elements = document.select("div.split-page")
//            val size: Int = elements.size
//            for (index: Int in 0 until size) {
//                val foodSplit = elements.select("h2").eq(index).text().split(" ")
//                val foodName = foodSplit.subList(1, foodSplit.size).joinToString(" ")
//                val foodImg = listFoodImage[foodName] ?: ""
//                val foodDetail: String = elements.select("p").eq(index).text()
//
//                Log.d("cek scrap", "FoodName: $foodName \n Img: $foodImg \n Index: $index \n Detail: $foodDetail")
//                modelFoods.add(ModelFoods(foodImg, foodName, foodDetail, index.toString()))
//            }
        } else {

            val elements = when (strCity) {
                "Kabupaten Ciamis", "Kabupaten Cianjur", "Kabupaten Garut", "Kabupaten Indramayu", "Kabupaten Karawang", "Kabupaten Subang","Kabupaten Sukabumi", "Kota Sukabumi","Kabupaten Sumedang", "Kabupaten Tasikmalaya", "Kota Tasikmalaya", "Kota Depok" -> document.select("h3:matchesOwn(\\d+\\.)")
                "Kabupaten Majalengka" -> document.select("h3:has(span:matches(\\d+\\.))")
                "Kabupaten Cirebon", "Kota Cirebon", "Kota Banjar" -> document.select("h2:matchesOwn(\\d+\\.)")
                else -> document.select("h2:matchesOwn(\\d+\\.)")
            }

            if (elements.isEmpty()) {
                Log.d("cek scrap", "No elements found")
            } else {
                var currentIndex = 0
                val size: Int = when(strCity){
                    "Kota Banjar" -> elements.size-4
                    else -> elements.size
                }

                while (currentIndex < size) {
                    val regex = Regex("^\\d+\\.\\s*(.*)")
                    val matchResult = regex.find(elements[currentIndex].text())
                    val foodName = matchResult?.groupValues?.get(1) ?: ""

                    val foodNameCamelCase = toCamelCase(foodName)  // Mengubah foodName menjadi camel case
                    val foodImg = listFoodImage[foodNameCamelCase] ?: ""

                    val foodDetailList = mutableListOf<String>()

                    var currentElement = elements[currentIndex].nextElementSibling()
                    while (currentElement != null && (currentElement.tagName() != "h2" && currentElement.tagName() != "h3")) {
                        if ((strCity.equals("Kabupaten Ciamis") || strCity.equals("Kabupaten Cianjur") || strCity.equals("Kabupaten Cirebon") || strCity.equals("Kota Cirebon") || strCity.equals("Kabupaten Garut") || strCity.equals("Kabupaten Indramayu") || strCity.equals("Kabupaten Karawang") || strCity.equals("Kabupaten Majalengka") || strCity.equals("Kabupaten Subang") || strCity.equals("Kabupaten Sukabumi") || strCity.equals("Kota Sukabumi") || strCity.equals("Kabupaten Sumedang") || strCity.equals("Kabupaten Tasikmalaya") || strCity.equals("Kota Tasikmalaya") || strCity.equals("Kota Banjar") || strCity.equals("Kota Depok")) && currentElement.tagName() == "p"
                        ) {
                            foodDetailList.add(currentElement.text())
                        }

                        currentElement = currentElement.nextElementSibling()
                    }

                    val foodDetail = foodDetailList.joinToString("\n\n")

                    Log.d("cek scrap", "FoodName: $foodNameCamelCase \n Img: $foodImg \n Index: $currentIndex \n Detail: $foodDetail")
                    modelFoods.add(ModelFoods(foodImg, foodNameCamelCase, foodDetail, currentIndex.toString()))

                    currentIndex++
                }

            }
        }
    }


    private suspend fun webWithImage(terstruktur:Boolean, webUrl: String, modelFoods: ArrayList<ModelFoods>) {
        val url = webUrl
        val document = withContext(Dispatchers.IO) {
            Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("Referer", "https://www.google.com")
                .header("Connection", "keep-alive")
                .header("Upgrade-Insecure-Requests", "1")
                .get()
        }

        if (terstruktur){
            val elements = document.select("div.split-page")
            val size: Int = elements.size
            for (index: Int in 0 until size) {
                val foodImg: String = elements.select("img").eq(index).attr("data-src")
                val foodSplit = elements.select("h2").eq(index).text().split(" ")
                val foodName = foodSplit.subList(1, foodSplit.size).joinToString(" ")
                val foodNameCamelCase = toCamelCase(foodName)  // Mengubah foodName menjadi camel case

                val foodDetail: String = elements.select("p").eq(index).text()

                Log.d("cek scrap", "FoodName: $foodNameCamelCase \n Img: $foodImg \n Index: $index \n Detail: $foodDetail")
                modelFoods.add(ModelFoods(foodImg, foodNameCamelCase, foodDetail, index.toString()))
            }
        }else{
            val elements = when (strCity) {
                "Kabupaten Kuningan" -> document.select("h3:matchesOwn(\\d+\\.)")
                "Kabupaten Bandung", "Kabupaten Bandung Barat", "Kota Bandung", "Kota Cimahi", "Kabupaten Bogor", "Kota Bogor" -> document.select("h2:matchesOwn(\\d+\\.)")
                "Kabupaten Pangandaran" -> document.select("p:has(strong:matches(\\d+\\.))")
                else -> document.select("h2:matchesOwn(\\d+\\.)")
            }
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
                        if (strCity.equals("Kabupaten Bogor") || strCity.equals("Kota Bogor") || strCity.equals("Kabupaten Pangandaran")) {
                            foodImg = document.select("img[alt*=${foodSplit[index]}]").attr("src")
                        } else if (strCity.equals("Kabupaten Kuningan")|| strCity.equals("Kabupaten Bandung") || strCity.equals("Kabupaten Bandung Barat") || strCity.equals("Kota Bandung") || strCity.equals("Kota Cimahi")) {
                            foodImg = document.select("img[alt*=${foodSplit[index]}]").attr("data-src")
                        }
                        if (foodImg.isNotEmpty()) break
                    }

                    if ((strCity.equals("Kabupaten Bogor") || strCity.equals("Kota Bogor") ) && foodName.contains("Dodongkal")) {
                        foodImg = document.select("img[alt*=dongkal]").attr("src")
                    }

                    val foodDetailList = mutableListOf<String>()

                    var currentElement = elements[currentIndex].nextElementSibling()

                    while (currentElement != null && (currentElement.tagName() != "h2" && currentElement.tagName() != "h3")) {
                        if (strCity.equals("Kabupaten Bogor") || strCity.equals("Kota Bogor") || strCity.equals("Kabupaten Kuningan")) {
                            if (currentElement.tagName() == "p") {
                                foodDetailList.add(currentElement.text())
                            }
                        } else if(strCity.equals("Kabupaten Bandung") || strCity.equals("Kabupaten Bandung Barat") || strCity.equals("Kota Bandung") || strCity.equals("Kota Cimahi")) {
                            if (currentElement.hasClass("p-txt")) {
                                foodDetailList.add(currentElement.text())
                            }
                        }
                        currentElement = currentElement.nextElementSibling()
                    }

                    if (strCity.equals("Kabupaten Pangandaran")) {
                        currentElement = elements[currentIndex].nextElementSibling()
                        while (currentElement != null && (currentElement.tagName() != "p" || currentElement.select("strong").isNotEmpty())) {
                            currentElement = currentElement.nextElementSibling()
                        }
                        while (currentElement != null && currentElement.tagName() == "p" && currentElement.select("strong").isEmpty()) {
                            foodDetailList.add(currentElement.text())
                            currentElement = currentElement.nextElementSibling()
                        }
                    }



                    val foodDetail = foodDetailList.joinToString("\n\n")

                    Log.d("cek scrap", "FoodName: $foodName \n Img: $foodImg \n Index: $currentIndex \n Detail: $foodDetail")
                    modelFoods.add(ModelFoods(foodImg, foodName, foodDetail, currentIndex.toString()))

                    currentIndex++
                }
            }
        }
    }


    fun toCamelCase(input: String): String {
        return input.split(" ").map { word -> word.capitalize() }.joinToString(" ")
    }

//    private suspend fun webTerstruktur(webUrl: String, modelFoods: ArrayList<ModelFoods>) {
//        val url = webUrl
//        val doc: Document = withContext(Dispatchers.IO) { Jsoup.connect(url).get() }
//
//        val elements: Elements = doc.select("div.split-page")
//
//        val size: Int = elements.size
//        for (index: Int in 0 until size) {
//            val foodImg: String = elements.select("img").eq(index).attr("data-src")
//            val foodSplit = elements.select("h2").eq(index).text().split(" ")
//            val foodName = foodSplit.subList(1, foodSplit.size).joinToString(" ")
//
//            val foodDetail: String = elements.select("p").eq(index).text()
//
//            Log.d("cek scrap", "FoodName: $foodName \n Img: $foodImg \n Index: $index \n Detail: $foodDetail")
//            modelFoods.add(ModelFoods(foodImg, foodName, foodDetail, index.toString()))
//        }
//    }
//
//    private suspend fun webTidakTerstruktur(webUrl: String, modelFoods: ArrayList<ModelFoods>) {
//        var url = webUrl
//        val document = withContext(Dispatchers.IO) { Jsoup.connect(url).get() }
//        val elements = document.select("h2:matchesOwn(\\d+\\.)")
//
//        val size: Int = elements.size
//        if (elements.isEmpty()) {
//            Log.d("cek scrap", "No elements found")
//        } else {
//            var currentIndex = 0
//            val size: Int = elements.size
//
//            while (currentIndex < size) {
//                val foodSplit = elements[currentIndex].text().split(" ")
//                val foodName = foodSplit.subList(1, foodSplit.size).joinToString(" ")
//
//                var foodImg = ""
//
//                for (index: Int in 1 until foodSplit.size) {
//                    if (strCity.equals("Kabupaten Bogor") ||
//                        strCity.equals("Kota Bogor")
//                    ) {
//                        foodImg = document.select("img[alt*=${foodSplit[index]}]").attr("src")
//                    } else {
//                        foodImg = document.select("img[alt*=${foodSplit[index]}]").attr("data-src")
//                    }
//
//                    if (foodImg.isNotEmpty()) break
//                }
//
//                if ((strCity.equals("Kabupaten Bogor") ||
//                            strCity.equals("Kota Bogor")
//                            ) &&
//                    foodName.contains("Dodongkal")
//                ) {
//                    foodImg = document.select("img[alt*=dongkal]").attr("src")
//                }
//
//                val foodDetailList = mutableListOf<String>()
//
//                var currentElement = elements[currentIndex].nextElementSibling()
//
//                while (currentElement != null && currentElement.tagName() != "h2") {
//                    if (strCity.equals("Kabupaten Bogor") ||
//                        strCity.equals("Kota Bogor")
//                    ) {
//                        if (currentElement.hasClass("p-txt")) {
//                        } else if (currentElement.tagName() == "p") {
//                            foodDetailList.add(currentElement.text())
//                        }
//                    } else {
//                        if (currentElement.hasClass("p-txt")) {
//                            foodDetailList.add(currentElement.text())
//                        }
//                    }
//
//                    currentElement = currentElement.nextElementSibling()
//                }
//
//                val foodDetail = foodDetailList.joinToString("\n\n")
//
//                Log.d("cek scrap", "FoodName: $foodName \n Img: $foodImg \n Index: $currentIndex \n Detail: $foodDetail")
//                modelFoods.add(ModelFoods(foodImg, foodName, foodDetail, currentIndex.toString()))
//
//                currentIndex++
//            }
//        }
//    }

}
