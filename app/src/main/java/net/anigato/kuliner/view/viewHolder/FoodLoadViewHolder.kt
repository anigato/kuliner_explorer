package net.anigato.kuliner.view.viewHolder

import androidx.recyclerview.widget.RecyclerView
import net.anigato.kuliner.databinding.ListItemFoodsLoaderBinding

class FoodLoadViewHolder(private val binding: ListItemFoodsLoaderBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bindView() {
        binding.progressBar.isIndeterminate = true
    }
}
