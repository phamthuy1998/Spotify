package thuypham.ptithcm.spotify.ui.playlist


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_playlists.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.playlist.adapter.PlayListAdapter
import thuypham.ptithcm.spotify.util.gone
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.util.show
import thuypham.ptithcm.spotify.viewmodel.YourMusicViewModel

class FavoritePlaylistsFragment : Fragment() {

    private lateinit var viewModel: YourMusicViewModel
    private val playlistAdapter by lazy {
        PlayListAdapter(this::playlistEvent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders
            .of(this, Injection.provideYourMusicViewModelFactory())
            .get(YourMusicViewModel::class.java)
    }

    private fun playlistEvent(playlistID: String?) {
        val playlistFragment = PlaylistDetailFragment()
        val arguments = Bundle()
        arguments.putString("playlistID", playlistID)
        playlistFragment.arguments = arguments
        requireActivity()
            .replaceFragment(id = R.id.container, fragment = playlistFragment, addToBackStack = true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPlaylist()
        bindViewModel()
        addEvents()
        initRecyclerView()
    }


    private fun addEvents() {
        swRefreshPlaylist.setOnRefreshListener {
            viewModel.getPlaylist()
            swRefreshPlaylist.isRefreshing = false
        }
    }

    private fun initRecyclerView() {
        rvPlaylist.adapter = playlistAdapter
        rvPlaylist.hasFixedSize()
        rvPlaylist.setItemViewCacheSize(20)
    }

    private fun bindViewModel() {
        viewModel.listPlaylist.observe(requireActivity(), Observer {
            playlistAdapter.addDataPlaylist(it)
        })

        viewModel.networkStatePlaylist.observe(requireActivity(), Observer {
            when (it.status) {
                Status.FAILED -> {
                    progressPlaylist?.gone()
                    llPlaylistEmpty?.show()
                }
                Status.RUNNING -> {
                    progressPlaylist?.show()
                    llPlaylistEmpty?.gone()
                }
                Status.SUCCESS -> {
                    progressPlaylist?.gone()
                    llPlaylistEmpty?.gone()
                }
            }
        })
    }

}
