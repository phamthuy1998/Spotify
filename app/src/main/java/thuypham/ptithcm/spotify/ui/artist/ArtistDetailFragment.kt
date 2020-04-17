package thuypham.ptithcm.spotify.ui.artist


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
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentArtistBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.service.SoundService
import thuypham.ptithcm.spotify.ui.album.AlbumDetailFragment
import thuypham.ptithcm.spotify.ui.song.NowPlayingFragment
import thuypham.ptithcm.spotify.ui.song.adapter.SongAdapter
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.ArtistViewModel
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel
import kotlin.math.abs

/**
 * A simple [Fragment] subclass.
 */
class ArtistDetailFragment : Fragment() {

    private val artistViewModel: ArtistViewModel by lazy {
        ViewModelProviders
            .of(this, Injection.provideArtistViewModelFactory())
            .get(ArtistViewModel::class.java)
    }
    private val nowPlayingViewModel: NowPlayingViewModel by lazy {
        ViewModelProviders
            .of(
                requireActivity(),
                Injection.provideNowPlayingViewModelFactory(requireActivity().application)
            )
            .get(NowPlayingViewModel::class.java)
    }
    private lateinit var binding: FragmentArtistBinding
    private val songAdapter by lazy {
        SongAdapter(
            mutableListOf(),
            this::songEvents
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Start service music
        val intent = Intent(requireContext(), SoundService.getInstance().javaClass)
        requireActivity().bindService(intent, musicConnection, Context.BIND_AUTO_CREATE);
        requireContext().startService(intent)
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
        nowPlayingViewModel.insertSongs(artistViewModel.listSong.value ?: arrayListOf())
        nowPlayingViewModel.listSongPlaying.value = artistViewModel.listSong.value
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

    private var artistId: String? = null
    private var albumID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        artistId = arguments?.getString("artistID")
        if (artistId != null) {
            artistViewModel.getListSongOfArtist(artistId!!)
            artistViewModel.getArtistInfo(artistId!!)
            artistViewModel.getLatestAlbumOfArtist(artistId!!)
        } else
            Toast.makeText(
                requireContext(),
                "Can't load info of artist!",
                Toast.LENGTH_LONG
            ).show()
        initViews()
        bindViewModel()
        addEvents()
    }

    // Check service init
    private var checkInitService = false
    private var musicService: SoundService? = SoundService()
    private val musicConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SoundService.MusicBinder
            // get service
            musicService = binder.getService()
            musicService?.setShuffle(binding.btnPlayArtist.isSelected)
            checkInitService = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            checkInitService = false
        }
    }

    private fun addEvents() {
        binding.swRefreshArtist.setOnRefreshListener { refresh() }
        binding.btnBackArtist.setOnClickListener { requireActivity().onBackPressed() }
        binding.albumLatestRelease.setOnClickListener { showAlbum(albumID) }
        binding.btnPlayArtist.setOnClickListener {
            if (artistViewModel.listSong.value == null || artistViewModel.listSong.value?.size == 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.errListEmpty),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            binding.btnPlayArtist.isSelected = !binding.btnPlayArtist.isSelected
            if (binding.btnPlayArtist.isSelected) { // playing
                val position =
                    artistViewModel.listSong.value?.size?.let { it1 ->
                        randomPositionSong(
                            0,
                            it1 - 1
                        )
                    }

                showFragmentNowPlaying(
                    artistViewModel.listSong.value?.get(position ?: 0), position ?: 0
                )
            } else {
                musicService?.pausePlayer()
                nowPlayingViewModel.isPlaying.value = false
            }
            musicService?.setShuffle(binding.btnPlayArtist.isSelected)
            nowPlayingViewModel.isPlayShuffleArtist.value = binding.btnPlayArtist.isSelected
        }
        binding.btnLikeArtist.setOnClickListener { artistViewModel.followArtistOnClick(binding.btnLikeArtist) }
        binding.appBarArtist.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout?.totalScrollRange) {
                //Collapsed
                binding.tvArtist.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorText)
                )
                binding.tvSongName.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorText)
                )
                binding.btnBackArtist.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorText)
                )
            } else {
                //Expanded
                binding.tvArtist.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorWhite)
                )
                binding.tvSongName.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorWhite)
                )
                binding.btnBackArtist.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorWhite)
                )
            }
        })
    }

    private fun showAlbum(albumID: String?) {
        val albumFragment = AlbumDetailFragment()
        val arguments = Bundle()
        arguments.putString("albumID", albumID)
        albumFragment.arguments = arguments
        requireActivity()
            .replaceFragment(id = R.id.frmMain, fragment = albumFragment, addToBackStack = true)
    }

    private fun refresh() {
        if (artistId != null) {
            artistViewModel.getListSongOfArtist(artistId!!)
            artistViewModel.getLatestAlbumOfArtist(artistId!!)
        }
        binding.swRefreshArtist.isRefreshing = false
    }

    private fun initViews() {
        binding.rvSongArtist.adapter = songAdapter
        binding.rvSongArtist.setHasFixedSize(true)
        binding.rvSongArtist.setItemViewCacheSize(20)
        binding.btnPlayArtist.isSelected = nowPlayingViewModel.isPlayShuffleArtist.value!!
    }

    private fun bindViewModel() {
        artistViewModel.listSong.observe(this, Observer {
            songAdapter.addDataListSong(it)
        })

        artistViewModel.networkStateListSong.observe(this, Observer {
            if (it.status == Status.FAILED)
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
            binding.progressArtist.visibility =
                if (it?.status == Status.RUNNING) View.VISIBLE else View.GONE
        })

        artistViewModel.artistInfo.observe(this, Observer {
            binding.artist = it
        })

        artistViewModel.networkStateArtist.observe(this, Observer {
            if (it?.status == Status.FAILED)
                Toast.makeText(requireContext(), "err: ${it.msg}", Toast.LENGTH_LONG).show()
        })

        artistViewModel.statusFollowArtist.observe(this, Observer {
            when (it?.status) {
                Status.FAILED -> Toast
                    .makeText(
                        requireContext(),
                        "Unfollow artist",
                        Toast.LENGTH_LONG
                    ).show()
                Status.SUCCESS -> Toast
                    .makeText(
                        requireContext(),
                        "Follow artist",
                        Toast.LENGTH_LONG
                    ).show()
                Status.RUNNING -> {
                }
            }
        })
        artistViewModel.statusUnFollowArtist.observe(this, Observer {
            when (it?.status) {
                Status.FAILED -> Toast
                    .makeText(
                        requireContext(),
                        "UnFollow failed! ${it.msg} ",
                        Toast.LENGTH_LONG
                    ).show()
                Status.SUCCESS -> {
                    Toast
                        .makeText(
                            requireContext(),
                            "UnFollow successful!",
                            Toast.LENGTH_LONG
                        ).show()
                }

                Status.RUNNING -> {
                }
            }
        })

        artistViewModel.checkFollowArtist.observe(this, Observer {
            binding.btnLikeArtist.isSelected = it ?: false
        })

        //  Latest release album
        artistViewModel.albumArtist.observe(this, Observer {
            if (it != null) {
                albumID = it.id
                binding.album = it
            }
        })

        artistViewModel.statusAlbumArtist.observe(this, Observer {
            when (it?.status) {
                Status.FAILED -> {
                    binding.albumLatestRelease.gone()
                    binding.tvLatestRelease.gone()
                }

                Status.SUCCESS -> {
                    binding.albumLatestRelease.show()
                    binding.tvLatestRelease.show()
                }
                Status.RUNNING -> {
                    binding.albumLatestRelease.gone()
                    binding.tvLatestRelease.gone()
                }
            }
        })
    }
}
