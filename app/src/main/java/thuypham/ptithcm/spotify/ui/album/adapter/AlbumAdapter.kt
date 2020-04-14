package thuypham.ptithcm.spotify.ui.album.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import thuypham.ptithcm.spotify.base.DynamicSearchAdapter
import thuypham.ptithcm.spotify.data.Album
import thuypham.ptithcm.spotify.databinding.ItemAlbumBinding

class AlbumAdapter(
    private var listAlbum: MutableList<Album>? = arrayListOf(),
    private val itemAlbumClick: (albumID: String?) -> Unit
) :  DynamicSearchAdapter<Album>(listAlbum) {

    fun addDataAlbum(arr: ArrayList<Album>?) {
        if (arr != null)
            listAlbum?.apply {
                clear()
                addAll(arr)
                updateData(arr)
                notifyDataSetChanged()
            }
    }

    fun removeAllData() {
        listAlbum?.apply {
            clear()
            notifyDataSetChanged()
        }
    }

    fun addDataSearch(arr: MutableList<Album>) {
        listAlbum?.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = listAlbum?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AlbumViewHolder(
            ItemAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        listAlbum?.get(position)?.let { (holder as AlbumViewHolder).bind(it) }
    }

    inner class AlbumViewHolder(
        private val binding: ItemAlbumBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Album) {
            binding.apply {
                album = item
                executePendingBindings()
                itemAlbum.setOnClickListener { itemAlbumClick(item.id) }
            }
        }
    }

}