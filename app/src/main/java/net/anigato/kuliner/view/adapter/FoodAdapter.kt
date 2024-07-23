package net.anigato.kuliner.view.adapter

import android.app.Activity
import android.content.Intent
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
import net.anigato.kuliner.view.activities.FoodDetailActivity

class FoodAdapter(
    recyclerView: RecyclerView, // RecyclerView yang akan digunakan untuk menampilkan item-item
    var activity: Activity, // Activity yang memanggil adapter ini
    var modelFoods: MutableList<ModelFoods?>, // Daftar model makanan yang akan ditampilkan
    var strCity: String? // Nama kota yang digunakan untuk detail makanan
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Tipe view untuk item makanan dan loading
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    // Threshold untuk memicu fungsi 'load more'
    private val visibleThreshold = 5

    // Interface untuk 'load more'
    private var loadMore: ILoadMoreFood? = null

    // Status loading dan penanda posisi terakhir
    private var isLoading = false
    private var lastVisibleItem = 0
    private var totalItemCount = 0

    init {
        // Menginisialisasi LinearLayoutManager dari RecyclerView
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

        // Menambahkan listener saat scroll untuk implementasi 'load more'
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // Memperbarui jumlah total item dan posisi terakhir yang terlihat
                totalItemCount = linearLayoutManager.itemCount
                lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition()

                // Memeriksa apakah tidak sedang loading dan mendekati akhir daftar
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    // Memicu fungsi 'load more' jika tersedia
                    if (loadMore != null) {
                        loadMore!!.onLoadMore()
                    }
                    isLoading = true
                }
            }
        })
    }

    // Mendapatkan tipe view untuk posisi tertentu
    override fun getItemViewType(position: Int): Int {
        return if (modelFoods[position] == null) {
            VIEW_TYPE_LOADING // Jika item adalah loading indicator
        } else {
            VIEW_TYPE_ITEM // Jika item adalah item makanan
        }
    }

    // Membuat ViewHolder sesuai dengan tipe view yang diberikan
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            // Inflate layout untuk item makanan
            val binding = ListItemFoodsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            FoodsViewHolder(binding)
        } else {
            // Inflate layout untuk loading indicator
            val binding = ListItemFoodsLoaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            FoodLoadViewHolder(binding)
        }
    }

    // Mendapatkan jumlah item dalam daftar
    override fun getItemCount(): Int {
        return modelFoods.size
    }

    // Mengikat data dari model ke ViewHolder
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FoodsViewHolder) {
            holder.bindView(modelFoods[position]!!) // Bind data makanan ke FoodsViewHolder
        } else if (holder is FoodLoadViewHolder) {
            holder.bindView() // Bind tampilan loading ke FoodLoadViewHolder
        }

        // Mendapatkan item makanan pada posisi saat ini
        val foodsItem: ModelFoods? = modelFoods[position]

        // Mengatur click listener untuk membuka FoodDetailActivity saat item diklik
        holder.itemView.setOnClickListener {
            val intent = Intent(activity, FoodDetailActivity::class.java)
            // Mengirim data makanan yang dipilih ke FoodDetailActivity
            intent.putExtra("IMAGE", foodsItem!!.image)
            intent.putExtra("TITLE", foodsItem.title)
            intent.putExtra("ITERASI", foodsItem.iterasi)
            intent.putExtra("DETAIL", foodsItem.detail)
            intent.putExtra("strCity", strCity)
            activity.startActivity(intent) // Memulai FoodDetailActivity
        }
    }

    // Fungsi untuk menandai bahwa proses loading sudah selesai
    fun setLoaded() {
        isLoading = false
    }
}
