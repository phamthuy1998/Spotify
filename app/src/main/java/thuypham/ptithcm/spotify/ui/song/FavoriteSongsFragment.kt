package thuypham.ptithcm.spotify.ui.song


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_songs.*
import kotlinx.android.synthetic.main.list_empty.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.song.adapter.SongAdapter
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel
import thuypham.ptithcm.spotify.viewmodel.YourMusicViewModel

class FavoriteSongsFragment : Fragment() {
    private lateinit var viewModel: YourMusicViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private val songAdapter by lazy {
        SongAdapter(mutableListOf(), this::songEvents)
    }

    private fun songEvents(song: Song?,position: Int, type: EventTypeSong) {
        when (type) {
            EventTypeSong.ITEM_CLICK -> {
                val nowPlayingFragment = NowPlayingFragment.getInstance()
                val arguments = Bundle()
                arguments.putParcelable(SONG, song)
                arguments.putInt(POSITION, position)
                nowPlayingFragment.arguments = arguments
                activity.replaceFragment(
                    id = R.id.frmMain,
                    fragment = nowPlayingFragment,
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
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory(requireActivity().application))
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
        btnFind.setOnClickListener { showAllSongFragment() }
    }

    private fun showAllSongFragment() {
        requireActivity().replaceFragment(
            id = R.id.frmMain,
            fragment = AllSongFragment(),
            addToBackStack = true
        )
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
