package thuypham.ptithcm.spotify.ui.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.Playlist
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentPlaylistDetailBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.country.adapter.SongCountryAdapter
import thuypham.ptithcm.spotify.ui.song.NowPlayingFragment
import thuypham.ptithcm.spotify.util.PLAYLIST
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel
import thuypham.ptithcm.spotify.viewmodel.PlaylistViewModel

/**
 * A simple [Fragment] subclass.
 */
class PlaylistDetailFragment : Fragment() {

    private lateinit var playlistViewModel: PlaylistViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private lateinit var binding: FragmentPlaylistDetailBinding
    private val songAdapter by lazy {
        SongCountryAdapter(
            mutableListOf(),
            this::songEvents
        )
    }

    private fun songEvents(songId: String?, type: EventTypeSong) {
        when (type) {
            EventTypeSong.ITEM_CLICK -> {
                nowPlayingViewModel.songID.value = songId
                activity.replaceFragment(
                    id = R.id.frmMain,
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
        playlistViewModel = ViewModelProviders
            .of(this, Injection.providePlaylistViewModelFactory())
            .get(PlaylistViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory())
            .get(NowPlayingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private var playlist: Playlist? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playlist = arguments?.getParcelable(PLAYLIST)
        if (playlist != null) {
            binding.playlist = playlist
            playlist?.id?.let { playlistViewModel.getListSong(it) }
            initViews()
            viewObserver()
            addEvents()
        } else
            Toast.makeText(
                requireContext(),
                "Can't load info of this album!",
                Toast.LENGTH_LONG
            ).show()
    }

    private fun addEvents() {
        binding.swRefreshPlaylist.setOnRefreshListener {
            refreshAlbum()
            binding.swRefreshPlaylist.isRefreshing = false
        }
        binding.btnBackPlaylist.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnLikePlaylist.setOnClickListener {
            playlist?.let { it1 ->
                playlistViewModel.onLikeAlbumClick(binding.btnLikePlaylist, it1)
            }
        }
    }

    private fun refreshAlbum() {
        if (playlist != null) playlist?.id?.let { playlistViewModel.getListSong(it) }
    }

    private fun initViews() {
        binding.rvPlaylist.adapter = songAdapter
        binding.rvPlaylist.setHasFixedSize(true)
        binding.rvPlaylist.setItemViewCacheSize(20)
    }

    private fun viewObserver() {
        playlistViewModel.listSong.observe(this, Observer {
            songAdapter.addDataListSong(it)
        })

        playlistViewModel.networkStateListSong.observe(this, Observer {
            if (it.status == Status.FAILED)
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
            binding.progressPlaylist.visibility =
                if (it?.status == Status.RUNNING) View.VISIBLE else View.GONE
        })

        playlistViewModel.statusLikePlaylist.observe(this, Observer {
            when (it?.status) {
                Status.FAILED -> Toast
                    .makeText(
                        requireContext(),
                        "Failed to add to favorite playlist!",
                        Toast.LENGTH_LONG
                    ).show()
                Status.SUCCESS -> Toast
                    .makeText(
                        requireContext(),
                        "Add to favorite playlist successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                Status.RUNNING -> {
                }
            }
        })
        playlistViewModel.statusUnLikePlaylist.observe(this, Observer {
            when (it?.status) {
                Status.FAILED -> Toast
                    .makeText(
                        requireContext(),
                        "Failed to remove favorite album!",
                        Toast.LENGTH_LONG
                    ).show()
                Status.SUCCESS -> Toast
                    .makeText(
                        requireContext(),
                        "Remove favorite album successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                Status.RUNNING -> {
                }
            }
        })

        playlistViewModel.checkPlaylistIsLike.observe(this, Observer {
            binding.btnLikePlaylist.isSelected = it ?: false
        })
    }

}
