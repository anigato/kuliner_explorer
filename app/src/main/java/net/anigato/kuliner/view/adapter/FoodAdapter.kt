package net.anigato.kuliner.view.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.anigato.kuliner.data.model.food.ModelFoods
import net.anigato.kuliner.view.foodInterface.ILoadMoreFood
import net.anigato.kuliner.view.viewHolder.FoodLoadViewHolder
import net.anigato.kuliner.view.viewHolder.FoodsViewHolder
import net.anigato.kuliner.databinding.ListItemFoodsBinding
import net.anigato.kuliner.databinding.ListItemFoodsLoaderBinding
import net.anigato.kuliner.view.activities.DetailsFoodActivity

class FoodAdapter(
    recyclerView: RecyclerView,
    var activity: Activity,
    var modelFoods: MutableList<ModelFoods?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
    private val visibleThreshold = 5
    private var loadMore: ILoadMoreFood? = null
    private var isLoading = false
    private var lastVisibleItem = 0
    private var totalItemCount = 0

    init {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (loadMore != null) {
                        loadMore!!.onLoadMore()
                    }
                    isLoading = true
                }
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return if (modelFoods[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val binding = ListItemFoodsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            FoodsViewHolder(binding)
        } else {
            val binding = ListItemFoodsLoaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            FoodLoadViewHolder(binding)
        }
    }

    fun getLoadMore(iLoaded: ILoadMoreFood) {
        this.loadMore = iLoaded
    }

    override fun getItemCount(): Int {
        return modelFoods.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FoodsViewHolder) {
            holder.bindView(modelFoods[position]!!)
        } else if (holder is FoodLoadViewHolder) {
            holder.bindView()
        }



        val foodsItem:ModelFoods? = modelFoods[position]


        holder.itemView.setOnClickListener {
            val intent = Intent(activity, DetailsFoodActivity::class.java)
            intent.putExtra("IMAGE", foodsItem!!.image)
            intent.putExtra("TITLE", foodsItem!!.title)
            intent.putExtra("DETAIL", foodsItem!!.details)
            intent.putExtra("ITERASI", foodsItem!!.iterasi)
//            Log.d("cek detail adapter",foodsItem!!.iterasi.toString())
            activity.startActivity(intent)
        }
    }

    fun setLoaded() {
        isLoading = false
    }
}
