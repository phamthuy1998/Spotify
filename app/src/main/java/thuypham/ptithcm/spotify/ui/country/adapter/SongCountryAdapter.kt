package thuypham.ptithcm.spotify.ui.country.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import thuypham.ptithcm.spotify.base.DynamicSearchAdapter
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.databinding.ItemTopSongBinding

class SongCountryAdapter(
    private var listSong: MutableList<Song>? = arrayListOf(),
    private val songEvents: (songId: String?, type: EventTypeSong) -> Unit
) : DynamicSearchAdapter<Song>(listSong) {

    fun addDataListSong(arr: MutableList<Song>) {
        listSong?.apply {
            clear()
            addAll(arr)
            updateData(arr)
            notifyDataSetChanged()
        }
    }

    fun addDataSearch(arr: MutableList<Song>) {
        listSong?.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    fun removeAllData() {
        listSong?.apply {
            clear()
            notifyDataSetChanged()
        }
    }


    override fun getItemCount(): Int = listSong?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SongViewHolder(
            ItemTopSongBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemId(position: Int): Long {
        return listSong?.get(position)?.id.hashCode().toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        listSong?.get(position)?.let { (holder as SongViewHolder).bind(it, position) }
    }

    inner class SongViewHolder(
        private val binding: ItemTopSongBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Song, _position: Int) {
            binding.apply {
                song = item
                position = (_position + 1).toString()
                executePendingBindings()
                itemSongAlbum.setOnClickListener {
                    songEvents(item.id, EventTypeSong.ITEM_CLICK)
                }
//                btnSongShowMore.setOnClickListener {
//                    listSong
//                    songEvents(item.id, EventTypeSong.SHOW_MORE)
//                }
            }
        }
    }
}


