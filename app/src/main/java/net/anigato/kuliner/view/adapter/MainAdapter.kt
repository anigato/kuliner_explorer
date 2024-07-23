package net.anigato.kuliner.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.anigato.kuliner.data.model.restoLocation.ModelResults
import net.anigato.kuliner.databinding.ListItemLocationBinding
import net.anigato.kuliner.view.activities.NavigationNRestoDetailActivity
import java.util.*

class MainAdapter(private val context: Context) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    // Daftar data hasil lokasi
    private val modelResultArrayList = ArrayList<ModelResults>()

    // Mengatur data yang akan ditampilkan di adapter
    fun setLocationAdapter(items: ArrayList<ModelResults>) {
        modelResultArrayList.clear()
        modelResultArrayList.addAll(items)
        notifyDataSetChanged()
    }

    // Menginflate layout item dan menginisialisasi ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = ListItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    // Mengikat data dari model ke ViewHolder
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val modelResult = modelResultArrayList[position]

        // Mengatur rating
        holder.ratingBar.numStars = 5
        holder.ratingBar.stepSize = 0.5f
        holder.ratingBar.rating = modelResult.rating.toFloat()
        holder.tvNamaJalan.text = modelResult.formatted_address
        holder.tvNamaLokasi.text = modelResult.name
        holder.tvRating.text = "(${modelResult.rating})"

        // Mengambil data lokasi untuk dibagikan atau menampilkan rute
        val strPlaceId = modelResultArrayList[position].placeId
        val strNamaLokasi = modelResultArrayList[position].name
        val strNamaJalan = modelResultArrayList[position].formatted_address
        val strLat = modelResultArrayList[position].modelGeometry.modelLocation.lat
        val strLong = modelResultArrayList[position].modelGeometry.modelLocation.lng

        // Mengirim data ke activity NavigationNRestoDetailActivity untuk menampilkan rute
        holder.linearRute.setOnClickListener {
            val intent = Intent(context, NavigationNRestoDetailActivity::class.java)
            intent.putExtra("placeId", strPlaceId)
            intent.putExtra("formatted_address", strNamaJalan)
            intent.putExtra("lat", strLat)
            intent.putExtra("lng", strLong)
            context.startActivity(intent)
        }

        // Intent untuk berbagi lokasi menggunakan aplikasi lain
        holder.imageShare.setOnClickListener {
            val strUri = "http://maps.google.com/maps?daddr=$strLat,$strLong"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, strNamaLokasi)
            intent.putExtra(Intent.EXTRA_TEXT, strUri)
            context.startActivity(Intent.createChooser(intent, "Bagikan :"))
        }
    }

    // Mengembalikan jumlah item dalam daftar
    override fun getItemCount(): Int {
        return modelResultArrayList.size
    }

    // ViewHolder untuk item lokasi
    class MainViewHolder(binding: ListItemLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        val linearRute: LinearLayout = binding.linearRute
        val tvNamaJalan: TextView = binding.tvNamaJalan
        val tvNamaLokasi: TextView = binding.tvNamaLokasi
        val tvRating: TextView = binding.tvRating
        val imageShare: ImageView = binding.imageShare
        val ratingBar: RatingBar = binding.ratingBar
    }
}
