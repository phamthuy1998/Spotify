package thuypham.ptithcm.spotify.ui.playlist

import android.app.Application
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_playlist.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Playlist
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.playlist.adapter.PlayListAdapter
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel
import thuypham.ptithcm.spotify.viewmodel.PlaylistViewModel

/**
 * A simple [Fragment] subclass.
 */
class PlaylistFragment : Fragment() {

    private lateinit var playlistViewModel: PlaylistViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel

    private val playlistAdapter by lazy {
        PlayListAdapter(mutableListOf(), this::playlistEvent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistViewModel = ViewModelProviders
            .of(this, Injection.providePlaylistViewModelFactory())
            .get(PlaylistViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory(requireActivity().application))
            .get(NowPlayingViewModel::class.java)
    }

    private fun playlistEvent(playlist: Playlist?) {
        requireActivity().hideKeyboard()
        val playlistFragment = PlaylistDetailFragment()
        val arguments = Bundle()
        arguments.putParcelable(PLAYLIST, playlist)
        playlistFragment.arguments = arguments
        requireActivity()
            .replaceFragment(id = R.id.frmMain, fragment = playlistFragment, addToBackStack = true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlistViewModel.getAllPlaylist()
        initRecyclerView()
        addEvents()
        bindViewModel()
    }


    private fun addEvents() {
        swRefreshPlaylists.setOnRefreshListener {
            playlistViewModel.getAllPlaylist()
            swRefreshPlaylists.isRefreshing = false
        }
        btnBackPlaylists.setOnClickListener {
            requireActivity().hideKeyboard()
            requireActivity().onBackPressed()
        }
        edtSearchPlaylist.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                playlistAdapter.search(s.toString(), {
                    llSearchPlaylistNotFound.show()
                    playlistAdapter.removeAllData()
                }, {
                    playlistAdapter.addDataSearch(it)
                    llSearchPlaylistNotFound.gone()
                })
            }
        })
    }

    private fun bindViewModel() {
        playlistViewModel.listPlaylist.observe(requireActivity(), Observer {
            playlistAdapter.addDataPlaylist(it)
        })

        playlistViewModel.networkStatePlaylists.observe(requireActivity(), Observer {
            when (it.status) {
                Status.FAILED -> {
                    progressPlaylists?.gone()
                    playlistEmpty?.show()
                }
                Status.RUNNING -> {
                    progressPlaylists?.show()
                    playlistEmpty?.gone()
                }
                Status.SUCCESS -> {
                    progressPlaylists?.gone()
                    playlistEmpty?.gone()
                }
            }
        })
    }

    private fun initRecyclerView() {
        rvAllPlaylist.setItemViewCacheSize(20)
        rvAllPlaylist.setHasFixedSize(true)
        rvAllPlaylist.adapter = playlistAdapter
    }

}
