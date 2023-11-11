package net.anigato.kuliner.view.activities

//import android.content.Intent
//import android.graphics.ColorSpace.Model
//import android.os.Bundle
//import android.os.PersistableBundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import net.anigato.kuliner.data.model.food.ModelFoods
//import net.anigato.kuliner.databinding.ActivityMainBinding
//import net.anigato.kuliner.view.adapter.FoodAdapter
//
//class MainActivity : AppCompatActivity() {

//    private lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupListeners()
//    }
//
//    private fun setupListeners() {
//        binding.btnGoToMap.setOnClickListener {
//            val intent = Intent(this, MapActivity::class.java)
//            startActivity(intent)
//        }
//
//        // Add other listeners for different features of your app
//    }



import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        modelFoods = intent.getSerializableExtra("FOODS") as? ArrayList<ModelFoods>
        if (modelFoods != null) {
            getTenFoods(modelFoods!!)
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        foodAdapter = FoodAdapter(binding.recyclerview, this, foodsLoad)
        binding.recyclerview.adapter = foodAdapter

        setupListeners()
    }

    private fun getTenFoods(listFoods: ArrayList<ModelFoods>) {
        for (index: Int in 0 until minOf(10, listFoods.size)) {
            foodsLoad.add(listFoods[index])
        }
    }

    private fun setupListeners() {
        binding.btnGoToMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }
}

