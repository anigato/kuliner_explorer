package net.anigato.kuliner.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import net.anigato.kuliner.data.model.food.ModelFoods
import net.anigato.kuliner.databinding.ActivityMainBinding
import net.anigato.kuliner.view.adapter.FoodAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var modelFoods: ArrayList<ModelFoods>? = null
    private var foodsLoad: MutableList<ModelFoods?> = ArrayList()
    private lateinit var foodAdapter: FoodAdapter
    private var strCity: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelFoods = intent.getSerializableExtra("FOODS") as? ArrayList<ModelFoods>
        if (modelFoods != null) {
            getTenFoods(modelFoods!!)
        }

        strCity = intent.getStringExtra("strCity")

        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        foodAdapter = FoodAdapter(binding.recyclerview, this, foodsLoad, strCity)
        binding.recyclerview.adapter = foodAdapter


    }

    private fun getTenFoods(listFoods: ArrayList<ModelFoods>) {
        for (index: Int in 0 until minOf(20, listFoods.size)) {
            foodsLoad.add(listFoods[index])
        }
    }


}

