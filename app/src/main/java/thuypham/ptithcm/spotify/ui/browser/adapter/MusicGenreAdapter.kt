package thuypham.ptithcm.spotify.ui.browser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import thuypham.ptithcm.spotify.data.MusicGenre
import thuypham.ptithcm.spotify.databinding.ItemMusicGenreBinding

class MusicGenreAdapter(
    private val musicGenreClick: (musicGenre: MusicGenre?) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listMusicGenre: ArrayList<MusicGenre>? = arrayListOf()

    fun addDataMusicGenre(arr: ArrayList<MusicGenre>?) {
        if (arr != null)
            listMusicGenre?.apply {
                clear()
                addAll(arr)
                notifyDataSetChanged()
            }
    }

    override fun getItemCount(): Int = listMusicGenre?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MusicGenreViewHolder(
            ItemMusicGenreBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        listMusicGenre?.get(position)?.let { (holder as MusicGenreViewHolder).bind(it) }
    }

    inner class MusicGenreViewHolder(
        private val binding: ItemMusicGenreBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MusicGenre) {
            binding.apply {
                musicGenre = item
                executePendingBindings()
                itemMusicGenre.setOnClickListener { musicGenreClick(item) }
            }
        }
    }

}