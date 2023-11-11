package net.anigato.kuliner.view.foodInterface

import net.anigato.kuliner.data.model.food.ModelFoods

interface FoodIJsoupData {
    fun getWebData(datas: ArrayList<ModelFoods>)
}