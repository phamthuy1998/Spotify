package thuypham.ptithcm.spotify.ui.album


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
import kotlinx.android.synthetic.main.fragment_album.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentAlbumBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.service.SoundService
import thuypham.ptithcm.spotify.ui.song.NowPlayingFragment
import thuypham.ptithcm.spotify.ui.song.adapter.SongAdapter
import thuypham.ptithcm.spotify.util.POSITION
import thuypham.ptithcm.spotify.util.SONG
import thuypham.ptithcm.spotify.util.randomPositionSong
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.viewmodel.AlbumViewModel
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel
import kotlin.math.abs

class AlbumDetailFragment : Fragment() {
    private lateinit var albumViewModel: AlbumViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private lateinit var binding: FragmentAlbumBinding
    private val songAdapter by lazy {
        SongAdapter(
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
        nowPlayingViewModel.insertSongs(albumViewModel.listSong.value ?: arrayListOf())
        nowPlayingViewModel.listSongPlaying.value = albumViewModel.listSong.value
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        albumViewModel = ViewModelProviders
            .of(this, Injection.provideAlbumViewModelFactory())
            .get(AlbumViewModel::class.java)
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

    private var albumID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        albumID = arguments?.getString("albumID")
        if (albumID != null) {
            albumViewModel.getListAlbum(albumID!!)
            albumViewModel.getAlbumInfo(albumID!!)
        } else
            Toast.makeText(requireContext(), "Can't load info of this album!", Toast.LENGTH_LONG)
                .show()
        initViews()
        viewObserver()
        addEvents()
    }

    private fun addEvents() {
        binding.swRefreshAlbum.setOnRefreshListener {
            refreshAlbum()
            binding.swRefreshAlbum.isRefreshing = false
        }
        binding.btnBackAlbum.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnPlayAlbum.setOnClickListener {
            if (albumViewModel.listSong.value == null || albumViewModel.listSong.value?.size == 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.errListEmpty),
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            binding.btnPlayAlbum.isSelected = !binding.btnPlayAlbum.isSelected
            // Play music
            if (binding.btnPlayAlbum.isSelected) {
                val position =
                    albumViewModel.listSong.value?.size?.let { it1 -> randomPositionSong(0, it1) }
                showFragmentNowPlaying(
                    albumViewModel.listSong.value?.get(position ?: 0), position ?: 0
                )
            } else {
                nowPlayingViewModel.isPlaying.value = false
                musicService?.pausePlayer()
            }// Pause music
            musicService?.setShuffle(binding.btnPlayAlbum.isSelected)
            nowPlayingViewModel.isPlayShuffleAlbum.value = binding.btnPlayAlbum.isSelected
        }
        binding.btnLikeAlbum.setOnClickListener { albumViewModel.onLikeAlbumClick(binding.btnLikeAlbum) }
        binding.appBarAlbum.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) == appBarLayout?.totalScrollRange) {
                //Collapsed
                binding.tvAlbum.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorText)
                )
                binding.tvAlbumName.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorText)
                )
                binding.btnBackAlbum.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorText)
                )
            } else {
                //Expanded
                binding.tvAlbum.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorWhite)
                )
                binding.tvAlbumName.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.colorWhite)
                )
                binding.btnBackAlbum.setColorFilter(
                    ContextCompat.getColor(requireContext(), R.color.colorWhite)
                )
            }
        })

    }

    private fun refreshAlbum() {
        if (albumID != null) albumViewModel.getListAlbum(albumID!!)
    }

    private fun initViews() {
        rvSongAlbum.adapter = songAdapter
        rvSongAlbum.setHasFixedSize(true)
        rvSongAlbum.setItemViewCacheSize(20)
        binding.btnPlayAlbum.isSelected = nowPlayingViewModel.isPlayShuffleAlbum.value!!
    }

    private fun viewObserver() {
        albumViewModel.listSong.observe(this, Observer {
            songAdapter.addDataListSong(it)
        })

        albumViewModel.networkStateListSong.observe(this, Observer {
            if (it.status == Status.FAILED)
                Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG).show()
            progressAlbumInfo.visibility =
                if (it?.status == Status.RUNNING) View.VISIBLE else View.GONE
        })

        albumViewModel.album.observe(this, Observer {
            binding.album = it
        })

        albumViewModel.networkStateAlbum.observe(this, Observer {
            if (it?.status == Status.FAILED)
                Toast.makeText(requireContext(), "err: ${it.msg}", Toast.LENGTH_LONG).show()
        })

        albumViewModel.statusLikeAlbum.observe(this, Observer {
            when (it?.status) {
                Status.FAILED -> Toast
                    .makeText(
                        requireContext(),
                        "Failed to add to favorite album!",
                        Toast.LENGTH_LONG
                    ).show()
                Status.SUCCESS -> Toast.makeText(
                    requireContext(),
                    "Add to favorite album successfully!",
                    Toast.LENGTH_LONG
                ).show()
                Status.RUNNING -> {
                }
            }
        })
        albumViewModel.statusUnLikeAlbum.observe(this, Observer {
            when (it?.status) {
                Status.FAILED -> Toast.makeText(
                    requireContext(),
                    "Failed to remove favorite album!",
                    Toast.LENGTH_LONG
                ).show()
                Status.SUCCESS -> Toast.makeText(
                    requireContext(),
                    "Remove favorite album successfully!",
                    Toast.LENGTH_LONG
                ).show()
                Status.RUNNING -> {
                }
            }
        })

        albumViewModel.checkAlbumIsLike.observe(this, Observer {
            binding.btnLikeAlbum.isSelected = it ?: false
        })
    }
}
