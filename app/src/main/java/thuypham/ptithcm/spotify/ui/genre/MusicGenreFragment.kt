package thuypham.ptithcm.spotify.ui.genre


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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.MusicGenre
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentMusicGenreBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.service.SoundService
import thuypham.ptithcm.spotify.ui.country.adapter.SongCountryAdapter
import thuypham.ptithcm.spotify.ui.song.NowPlayingFragment
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.MusicGenreViewModel
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel

/**
 * A simple [Fragment] subclass.
 */
class MusicGenreFragment : Fragment() {

    private lateinit var binding: FragmentMusicGenreBinding
    private lateinit var musicGenreViewModel: MusicGenreViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel

    private val songAdapter by lazy {
        SongCountryAdapter(
            mutableListOf(),
            this::songEvents
        )
    }

    private fun songEvents(song: Song?, position: Int, type: EventTypeSong) {
        when (type) {
            EventTypeSong.ITEM_CLICK -> showFragmentNowPlaying(song, position)
            EventTypeSong.SHOW_MORE -> { /* Todo() -> show dialog*/ }
        }
    }

    private fun showFragmentNowPlaying(song: Song?, position: Int) {
        nowPlayingViewModel.deleteAllSong()
        nowPlayingViewModel.insertSongs(musicGenreViewModel.listSong.value ?: arrayListOf())
        nowPlayingViewModel.listSongPlaying.value = musicGenreViewModel.listSong.value
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

    private var musicGenre: MusicGenre? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicGenreViewModel =
            ViewModelProviders.of(this, Injection.provideMusicGenreViewModelFactory())
                .get(MusicGenreViewModel::class.java)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMusicGenreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        musicGenre = arguments?.getParcelable(MUSIC_GENRE)
        if (musicGenre != null) {
            binding.musicGenre = musicGenre
            musicGenre?.id?.let { musicGenreViewModel.getListSong(it) }
            addEvents()
            initAdapter()
            bindViewModel()
        } else
            Toast.makeText(requireContext(), "Can't load info!", Toast.LENGTH_LONG).show()
    }

    private fun initAdapter() {
        binding.rvSongMusicGenre.adapter = songAdapter
        binding.rvSongMusicGenre.setHasFixedSize(true)
        binding.rvSongMusicGenre.setItemViewCacheSize(20)
    }

    private fun bindViewModel() {
        musicGenreViewModel.networkStateListSong.observe(this, Observer {
            when (it?.status) {
                Status.FAILED -> {
                    binding.progressMusicGenre.gone()
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
                }
                Status.SUCCESS -> {
                    binding.progressMusicGenre.gone()

                }
                Status.RUNNING -> {
                    binding.progressMusicGenre.show()
                }
            }
        })
        musicGenreViewModel.listSong.observe(this, Observer {
            songAdapter.addDataListSong(it)
        })
    }

    private fun addEvents() {
        binding.btnBackTopHit.setOnClickListener { requireActivity().onBackPressed() }
        binding.swRefreshMusicGenre.setOnRefreshListener {
            musicGenre?.id?.let { musicGenreViewModel.getListSong(it) }
            binding.swRefreshMusicGenre.isRefreshing = false
        }
        binding.btnPlayAllSong.setOnClickListener {
            val position =
                musicGenreViewModel.listSong.value?.size?.let { it1 -> randomPositionSong(0, it1) }
            showFragmentNowPlaying(
                musicGenreViewModel.listSong.value?.get(position ?: 0), position ?: 0
            )
            showFragmentNowPlaying(
                musicGenreViewModel.listSong.value?.get(position ?: 0),
                position ?: 0
            )
            musicService?.setShuffle(true)
        }
    }

}
