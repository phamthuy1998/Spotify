package thuypham.ptithcm.spotify.ui.album


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_album.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentAlbumBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.song.NowPlayingFragment
import thuypham.ptithcm.spotify.ui.song.adapter.SongAdapter
import thuypham.ptithcm.spotify.util.POSITION
import thuypham.ptithcm.spotify.util.SONG
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.viewmodel.AlbumViewModel
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel

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
        albumViewModel = ViewModelProviders
            .of(this, Injection.provideAlbumViewModelFactory())
            .get(AlbumViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory(requireActivity().application))
            .get(NowPlayingViewModel::class.java)
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
            Toast.makeText(
                requireContext(),
                "Can't load info of this album!",
                Toast.LENGTH_LONG
            ).show()
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
        binding.btnLikeAlbum.setOnClickListener { albumViewModel.onLikeAlbumClick(binding.btnLikeAlbum) }
    }

    private fun refreshAlbum() {
        if (albumID != null) albumViewModel.getListAlbum(albumID!!)
    }

    private fun initViews() {
        rvSongAlbum.adapter = songAdapter
        rvSongAlbum.setHasFixedSize(true)
        rvSongAlbum.setItemViewCacheSize(20)
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
                Status.SUCCESS -> Toast
                    .makeText(
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

        albumViewModel.checkAlbumIsLike.observe(this, Observer {
            binding.btnLikeAlbum.isSelected = it ?: false
        })
    }
}
