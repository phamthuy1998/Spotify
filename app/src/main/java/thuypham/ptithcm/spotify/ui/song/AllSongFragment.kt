package thuypham.ptithcm.spotify.ui.song

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_all_song.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.country.adapter.SongCountryAdapter
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel
import thuypham.ptithcm.spotify.viewmodel.SongViewModel

class AllSongFragment : Fragment() {

    private lateinit var songViewModel: SongViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private val songAdapter by lazy {
        SongCountryAdapter(mutableListOf(), this::songEvents)
    }

    private fun songEvents(song: Song?,position: Int, type: EventTypeSong) {
        when (type) {
            EventTypeSong.ITEM_CLICK -> {
                val nowPlayingFragment = NowPlayingFragment()
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
        songViewModel = ViewModelProviders
            .of(this, Injection.provideSongViewModelFactory())
            .get(SongViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory(requireActivity().application))
            .get(NowPlayingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_song, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        songViewModel.getListSong()
        initRecyclerView()
        addEvents()
        bindViewModel()
    }

    private fun addEvents() {
        swRefreshSongs.setOnRefreshListener {
            songViewModel.getListSong()
            swRefreshSongs.isRefreshing = false
        }
        btnBackAllSong.setOnClickListener { requireActivity().onBackPressed() }
        edtSearchSong.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                songAdapter.search(s.toString(), {
                    llSearchAllSongNotFound.show()
                    songAdapter.removeAllData()
                }, {
                    songAdapter.addDataSearch(it)
                    llSearchAllSongNotFound.gone()
                })
            }
        })
    }

    private fun bindViewModel() {
        songViewModel.listSong.observe(requireActivity(), Observer {
            songAdapter.addDataListSong(it)
        })

        songViewModel.networkStateListSong.observe(requireActivity(), Observer {
            when (it.status) {
                Status.FAILED -> {
                    progressAllSong?.gone()
                    llSongEmpty?.show()
                }
                Status.RUNNING -> {
                    progressAllSong?.show()
                    llSongEmpty?.gone()
                }
                Status.SUCCESS -> {
                    progressAllSong?.gone()
                    llSongEmpty?.gone()
                }
            }
        })
    }

    private fun initRecyclerView() {
        rvAllSong.setItemViewCacheSize(20)
        rvAllSong.setHasFixedSize(true)
        rvAllSong.adapter = songAdapter
    }
}
