package net.anigato.kuliner.controller

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import net.anigato.kuliner.data.model.food.ModelFoods
import net.anigato.kuliner.databinding.ActivityMainBinding
import net.anigato.kuliner.view.adapter.FoodAdapter

class FoodsController(private val activity: AppCompatActivity, private val binding: ActivityMainBinding) {

    // Fungsi untuk mengatur tampilan dan data
    fun setupViewAndData(listFoods: ArrayList<ModelFoods>, strCity: String?) {
        val foodsLoad = getFoods(listFoods)
        setupRecyclerView(foodsLoad, strCity)
    }

    // Fungsi untuk mengatur RecyclerView
    private fun setupRecyclerView(foodsLoad: MutableList<ModelFoods?>, strCity: String?) {
        // Setup RecyclerView dengan LinearLayoutManager
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)

        // Inisialisasi dan mengatur adapter untuk RecyclerView
        val foodAdapter = FoodAdapter(binding.recyclerview, activity, foodsLoad, strCity)
        binding.recyclerview.adapter = foodAdapter
    }

    // Fungsi untuk mengambil data makanan
    private fun getFoods(listFoods: ArrayList<ModelFoods>): MutableList<ModelFoods?> {
        // Menyiapkan list untuk menampung data makanan yang di-load
        val foodsLoad: MutableList<ModelFoods?> = ArrayList()

        // Mengambil semua data makanan dari listFoods
        foodsLoad.addAll(listFoods)

        return foodsLoad
    }
}
