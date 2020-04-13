package thuypham.ptithcm.spotify.ui.browser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import thuypham.ptithcm.spotify.data.Country
import thuypham.ptithcm.spotify.databinding.ItemMusicTopHitBinding

class TopHitAdapter(
    private val topHitClick: (country: Country) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listTopHit: ArrayList<Country>? = arrayListOf()

    fun addDataTopHit(arr: ArrayList<Country>?) {
        if (arr != null)
            listTopHit?.apply {
                clear()
                addAll(arr)
                notifyDataSetChanged()
            }
    }

    override fun getItemCount(): Int = listTopHit?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TopHitViewHolder(
            ItemMusicTopHitBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        listTopHit?.get(position)?.let { (holder as TopHitViewHolder).bind(it) }
    }

    inner class TopHitViewHolder(
        private val binding: ItemMusicTopHitBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Country) {
            binding.apply {
                country = item
                executePendingBindings()
                itemTopHit.setOnClickListener { topHitClick(item) }
            }
        }
    }

}