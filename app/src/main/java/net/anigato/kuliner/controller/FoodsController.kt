package net.anigato.kuliner.controller

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import net.anigato.kuliner.data.model.food.ModelFoods
import net.anigato.kuliner.databinding.ActivityMainBinding
import net.anigato.kuliner.view.adapter.FoodAdapter

class FoodsController(private val activity: AppCompatActivity, private val binding: ActivityMainBinding) {

    fun setupViewAndData(listFoods: ArrayList<ModelFoods>, strCity: String?, ) {
        val foodsLoad = getFoods(listFoods)
        setupRecyclerView(foodsLoad, strCity)
    }

    private fun setupRecyclerView(foodsLoad: MutableList<ModelFoods?>, strCity: String?) {
        // Setup RecyclerView dengan LinearLayoutManager
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)

        // Inisialisasi dan mengatur adapter untuk RecyclerView
        val foodAdapter = FoodAdapter(binding.recyclerview, activity, foodsLoad, strCity)
        binding.recyclerview.adapter = foodAdapter
    }

    private fun getFoods(listFoods: ArrayList<ModelFoods>): MutableList<ModelFoods?> {
        val foodsLoad: MutableList<ModelFoods?> = ArrayList()
        val limit = listFoods.size // Ambil semua data jika kurang dari atau sama dengan 10

        for (index: Int in 0 until limit) {
            foodsLoad.add(listFoods[index])
        }
        return foodsLoad
    }



}
