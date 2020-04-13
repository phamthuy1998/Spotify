package thuypham.ptithcm.spotify.ui.playlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import thuypham.ptithcm.spotify.data.Playlist
import thuypham.ptithcm.spotify.databinding.ItemPlaylistBinding

class PlayListAdapter(
    private val itemPlaylistClick: (songTypeId: String?) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listPlaylist: ArrayList<Playlist>? = arrayListOf()

    fun addDataPlaylist(arr: ArrayList<Playlist>?) {
        if (arr != null)
            listPlaylist?.apply {
                clear()
                addAll(arr)
                notifyDataSetChanged()
            }
    }

    override fun getItemCount(): Int = listPlaylist?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SongTypeViewHolder(
            ItemPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        listPlaylist?.get(position)?.let { (holder as SongTypeViewHolder).bind(it) }
    }

    inner class SongTypeViewHolder(
        private val binding: ItemPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Playlist) {
            binding.apply {
                playlist = item
                executePendingBindings()
                itemPlaylist.setOnClickListener { itemPlaylistClick(item.id) }
            }
        }
    }

}