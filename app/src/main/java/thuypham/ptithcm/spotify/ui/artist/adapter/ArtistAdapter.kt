package thuypham.ptithcm.spotify.ui.artist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import thuypham.ptithcm.spotify.data.Artist
import thuypham.ptithcm.spotify.databinding.ItemArtistBinding

class ArtistAdapter(
    private val itemArtistClick: (artistId: String?) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listArtist: ArrayList<Artist>? = arrayListOf()

    fun addDataArtist(arr: ArrayList<Artist>?) {
        if (arr != null)
            listArtist?.apply {
                clear()
                addAll(arr)
                notifyDataSetChanged()
            }
    }

    override fun getItemCount(): Int = listArtist?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AlbumViewHolder(
            ItemArtistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        listArtist?.get(position)?.let { (holder as AlbumViewHolder).bind(it) }
    }

    inner class AlbumViewHolder(
        private val binding: ItemArtistBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Artist) {
            binding.apply {
                artist = item
                executePendingBindings()
                itemArtist.setOnClickListener { itemArtistClick(item.id) }
            }
        }
    }

}