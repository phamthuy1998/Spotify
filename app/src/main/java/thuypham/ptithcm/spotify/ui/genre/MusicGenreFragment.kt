package thuypham.ptithcm.spotify.ui.genre


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
import thuypham.ptithcm.spotify.data.MusicGenre
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentMusicGenreBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.country.adapter.SongCountryAdapter
import thuypham.ptithcm.spotify.ui.song.NowPlayingFragment
import thuypham.ptithcm.spotify.util.MUSIC_GENRE
import thuypham.ptithcm.spotify.util.gone
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.util.show
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

    private var musicGenre: MusicGenre? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicGenreViewModel =
            ViewModelProviders.of(this, Injection.provideMusicGenreViewModelFactory())
                .get(MusicGenreViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory())
            .get(NowPlayingViewModel::class.java)
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
            Toast.makeText(
                requireContext(),
                "Can't load info!",
                Toast.LENGTH_LONG
            ).show()
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
    }

}
