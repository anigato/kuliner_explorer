package net.anigato.kuliner.view.viewHolder

import androidx.recyclerview.widget.RecyclerView
import net.anigato.kuliner.databinding.ListItemFoodsLoaderBinding

/**
 * ViewHolder untuk menampilkan tampilan loader makanan dalam RecyclerView.
 * Digunakan untuk menampilkan indikator progress saat data sedang dimuat.
 */
class FoodLoadViewHolder(private val binding: ListItemFoodsLoaderBinding) : RecyclerView.ViewHolder(binding.root) {

    /**
     * Metode untuk mengikat data loader.
     * Mengatur indikator progress menjadi indeterminate.
     */
    fun bindView() {
        binding.progressBar.isIndeterminate = true
    }
}
