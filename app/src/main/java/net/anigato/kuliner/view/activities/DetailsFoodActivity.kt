package net.anigato.kuliner.view.activities

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import net.anigato.kuliner.databinding.ActivityDetailsFoodBinding
import com.squareup.picasso.Picasso
import net.anigato.kuliner.view.activities.load.LoadDetailFoods
import net.anigato.kuliner.view.foodInterface.ILoadDetailFood
import net.anigato.kuliner.viewmodel.MainViewModel

class DetailsFoodActivity : AppCompatActivity(), ILoadDetailFood {
    private lateinit var binding: ActivityDetailsFoodBinding
    private var urlImage: String? = null
    private var urlDetail: String? = null
    private var strCity: String? = null
    private var urlIterasi: String? = null
    private var loadDetailFoods: AsyncTask<Void, Void, ArrayList<String>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        urlImage = intent.getStringExtra("IMAGE")
        urlDetail = intent.getStringExtra("DETAIL")
        urlIterasi = intent.getStringExtra("ITERASI")
        strCity = intent.getStringExtra("strCity")


        Log.d("cek detailfood","$urlIterasi")
        Log.d("cek detaildetail","$urlDetail")

        binding.txtTitleDetail.text = intent.getStringExtra("TITLE")
        Picasso.get().load(urlImage).into(binding.imageDetail)
        loadDetailFoods = LoadDetailFoods(this, urlDetail)
        loadDetailFoods!!.execute()

        setupListeners()
    }

    override fun getDetails(details: ArrayList<String>) {

//        for (index: Int in 0 until details.size-1){
//            if (index == details.size-1){
//                binding.txtDetail.append("\n" + details[index] + "\n")
//
//            } else {
//                binding.txtDetail.append(details[index] + "\n\n")
//            }
//            Log.d("cek detailfood ya",details[index])
//        }

        for (index: Int in 0 until details.size) {
            binding.txtDetail.append(details[index])

            if (index < details.size - 1) {
                binding.txtDetail.append("\n\n")
            }

//            Log.d("cek detailfood ya", details[index])
        }
    }


    private fun setupListeners() {
        MainViewModel.title = intent.getStringExtra("TITLE")
        binding.btnGoToMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }
}