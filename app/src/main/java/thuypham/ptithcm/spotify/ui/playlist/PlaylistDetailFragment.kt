package thuypham.ptithcm.spotify.ui.playlist

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.Playlist
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentPlaylistDetailBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.service.SoundService
import thuypham.ptithcm.spotify.ui.country.adapter.SongCountryAdapter
import thuypham.ptithcm.spotify.ui.song.NowPlayingFragment
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel
import thuypham.ptithcm.spotify.viewmodel.PlaylistViewModel
import kotlin.math.abs

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

    private fun songEvents(song: Song?, position: Int, type: EventTypeSong) {
        when (type) {
            EventTypeSong.ITEM_CLICK -> showFragmentNowPlaying(song, position)
            EventTypeSong.SHOW_MORE -> {
                // Todo() -> show dialog
            }
        }
    }

    private fun showFragmentNowPlaying(song: Song?, position: Int) {
        nowPlayingViewModel.deleteAllSong()
        nowPlayingViewModel.insertSongs(playlistViewModel.listSong.value ?: arrayListOf())
        nowPlayingViewModel.listSongPlaying.value = playlistViewModel.listSong.value
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playlistViewModel = ViewModelProviders
            .of(this, Injection.providePlaylistViewModelFactory())
            .get(PlaylistViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(
                requireActivity(),
                Injection.provideNowPlayingViewModelFactory(requireActivity().application)
            )
            .get(NowPlayingViewModel::class.java)
        // Start service music
        val intent = Intent(requireContext(), SoundService.getInstance().javaClass)
        requireActivity().bindService(intent, musicConnection, Context.BIND_AUTO_CREATE);
        requireContext().startService(intent)
    }


    // Check service init
    private var checkInitService = false
    private var musicService: SoundService? = SoundService()
    private val musicConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SoundService.MusicBinder
            // get service
            musicService = binder.getService()
            checkInitService = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            checkInitService = false
        }
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
        binding.btnPlayPlaylist.setOnClickListener {
            if (playlistViewModel.listSong.value == null || playlistViewModel.listSong.value?.size == 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.errListEmpty),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            binding.btnPlayPlaylist.isSelected = !binding.btnPlayPlaylist.isSelected
            // Play music
            if (binding.btnPlayPlaylist.isSelected) {
                val position =
                    playlistViewModel.listSong.value?.size?.let { it1 ->
                        randomPositionSong(
                            0,
                            it1
                        )
                    }
                showFragmentNowPlaying(
                    playlistViewModel.listSong.value?.get(position ?: 0), position ?: 0
                )
            } else {
                nowPlayingViewModel.isPlaying.value = false
                musicService?.pausePlayer()
            }// Pause music
            musicService?.setShuffle(binding.btnPlayPlaylist.isSelected)
            nowPlayingViewModel.isPlayShufflePlaylist.value = binding.btnPlayPlaylist.isSelected
        }
        binding.appBarPlaylist.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout?.totalScrollRange) {
                //Collapsed
                binding.btnBackPlaylist.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorText)
                )
            } else {
                //Expanded
                binding.btnBackPlaylist.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorWhite)
                )
            }
        })
    }

    private fun refreshAlbum() {
        if (playlist != null) playlist?.id?.let { playlistViewModel.getListSong(it) }
    }

    private fun initViews() {
        binding.rvPlaylist.adapter = songAdapter
        binding.rvPlaylist.setHasFixedSize(true)
        binding.rvPlaylist.setItemViewCacheSize(20)
        binding.btnPlayPlaylist.isSelected =
            nowPlayingViewModel.isPlayShufflePlaylist.value ?: false
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
