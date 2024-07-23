package net.anigato.kuliner.view.viewHolder

import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.anigato.kuliner.data.model.food.ModelFoods
import net.anigato.kuliner.databinding.ListItemFoodsBinding

/**
 * ViewHolder untuk menampilkan item makanan dalam RecyclerView.
 * Digunakan untuk mengikat data dari ModelFoods ke tampilan menggunakan data binding.
 */
class FoodsViewHolder(private val binding: ListItemFoodsBinding) : RecyclerView.ViewHolder(binding.root) {

    /**
     * Metode untuk mengikat data ModelFoods ke tampilan ViewHolder.
     * Mengatur teks judul dan gambar makanan menggunakan Picasso.
     */
    fun bindView(foods: ModelFoods) {
        // Mengatur teks judul makanan
        binding.txtTitle.text = foods.title

        // Menggunakan Picasso untuk memuat gambar makanan ke dalam imageCard
        Picasso.get().load(foods.image).into(binding.imageCard)
    }
}
