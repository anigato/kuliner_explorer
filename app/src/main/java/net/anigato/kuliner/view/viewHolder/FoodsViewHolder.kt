package net.anigato.kuliner.view.viewHolder

//import android.widget.ImageView
//import android.widget.TextView
//import com.squareup.picasso.Picasso
//import net.anigato.kuliner.data.model.food.ModelFoods
//import net.anigato.kuliner.databinding.ListItemFoodsBinding
//
//
//class FoodsViewHolder() {
// //class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
//
//    private lateinit var binding: ListItemFoodsBinding
//
//    val img_news: ImageView = binding.imageCard
//    val txt_title: TextView = binding.txtTitle
//
//    fun bindView(foods: ModelFoods){
//        txt_title.text = foods.title
//        Picasso.get().load(foods.image).into(img_news)
//    }
//
//}

import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.anigato.kuliner.data.model.food.ModelFoods
import net.anigato.kuliner.databinding.ListItemFoodsBinding

class FoodsViewHolder(private val binding: ListItemFoodsBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindView(foods: ModelFoods) {
        binding.txtTitle.text = foods.title
        Picasso.get().load(foods.image).into(binding.imageCard)
    }
}
