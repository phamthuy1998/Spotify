package thuypham.ptithcm.spotify.ui.song.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import thuypham.ptithcm.spotify.base.DynamicSearchAdapter
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.databinding.ItemSongBinding

class SongAdapter(
    private var listSong: MutableList<Song>? = arrayListOf(),
    private val songEvents: (song: Song?, position: Int, type: EventTypeSong) -> Unit
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
            ItemSongBinding.inflate(
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
        private val binding: ItemSongBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Song, _position: Int) {
            binding.apply {
                song = item
                position = (_position + 1).toString()
                executePendingBindings()
                itemSongAlbum.setOnClickListener {
                    songEvents(item , _position,EventTypeSong.ITEM_CLICK)
                }
//                btnShowMoreSong.setOnClickListener {
//                    listSong
//                    songEvents(item.id, EventTypeSong.SHOW_MORE)
//                }
            }
        }
    }

//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence): FilterResults {
//                if (constraint.isEmpty()) {
//                    listSearch = listSong
//                } else {
//                    val resultList = ArrayList<Song>()
//                    for (row in listSong) {
//                        if (row.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
//                            resultList.add(row)
//                        }
//                    }
//                    listSearch = resultList
//                }
//                val filterResults = FilterResults()
//                filterResults.values = listSearch
//                return filterResults
//            }
//
//            @Suppress("UNCHECKED_CAST")
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                listSearch = results?.values as ArrayList<Song>
//                notifyDataSetChanged()
//            }
//
//        }
//    }
}


