package thuypham.ptithcm.spotify.ui.song


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_songs.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.song.adapter.SongAdapter
import thuypham.ptithcm.spotify.util.gone
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.util.show
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel
import thuypham.ptithcm.spotify.viewmodel.YourMusicViewModel

class FavoriteSongsFragment : Fragment() {
    private lateinit var viewModel: YourMusicViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private val songAdapter by lazy {
        SongAdapter(mutableListOf(), this::songEvents)
    }

    private fun songEvents(songId: String?, type: EventTypeSong) {
        when (type) {
            EventTypeSong.ITEM_CLICK -> {
                nowPlayingViewModel.songID.value = songId
                activity.replaceFragment(
                    id= R.id.frmMain,
                    fragment = NowPlayingFragment(),
                    tag = "NowPlaying",
                    addToBackStack = true
                )
            }
            EventTypeSong.SHOW_MORE -> {
                // Todo() -> show dialog
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders
            .of(this, Injection.provideYourMusicViewModelFactory())
            .get(YourMusicViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory())
            .get(NowPlayingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_songs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getListSong()
        bindViewModel()
        addEvents()
        initRecyclerView()
    }

    private fun addEvents() {
        swRefreshSong.setOnRefreshListener {
            viewModel.getListSong()
            swRefreshSong.isRefreshing = false
        }
    }

    private fun initRecyclerView() {
        rvSongs.adapter = songAdapter
        rvSongs.hasFixedSize()
        rvSongs.setItemViewCacheSize(20)
    }

    private fun bindViewModel() {
        viewModel.listSong.observe(requireActivity(), Observer {
            songAdapter.addDataListSong(it)
        })

        viewModel.networkStateSongs.observe(requireActivity(), Observer {
            when (it.status) {
                Status.FAILED -> {
                    progressSong?.gone()
                    llSongEmpty?.show()
                }
                Status.RUNNING -> {
                    progressSong?.show()
                    llSongEmpty?.gone()
                }
                Status.SUCCESS -> {
                    progressSong?.gone()
                    llSongEmpty?.gone()
                }
            }
        })
    }
}
