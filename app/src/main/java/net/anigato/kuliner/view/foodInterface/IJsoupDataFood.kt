package net.anigato.kuliner.view.foodInterface

import net.anigato.kuliner.data.model.food.ModelFoods

interface IJsoupDataFood {
    fun getWebData(datas: ArrayList<ModelFoods>)
}