package thuypham.ptithcm.spotify.ui.artist


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
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentArtistBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.album.AlbumDetailFragment
import thuypham.ptithcm.spotify.ui.song.NowPlayingFragment
import thuypham.ptithcm.spotify.ui.song.adapter.SongAdapter
import thuypham.ptithcm.spotify.util.gone
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.util.show
import thuypham.ptithcm.spotify.viewmodel.ArtistViewModel
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel

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
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory())
            .get(NowPlayingViewModel::class.java)
    }
    private lateinit var binding: FragmentArtistBinding
    private val songAdapter by lazy {
        SongAdapter(
            mutableListOf(),
            this::onItemSongClick
        )
    }

    private fun onItemSongClick(songId: String?, type: EventTypeSong) {
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
        viewObserver()
        addEvents()
    }

    private fun addEvents() {
        binding.swRefreshArtist.setOnRefreshListener { refresh() }
        binding.btnBackArtist.setOnClickListener { requireActivity().onBackPressed() }
        binding.albumLatestRelease.setOnClickListener { showAlbum(albumID) }
        binding.btnLikeArtist.setOnClickListener { artistViewModel.followArtistOnClick(binding.btnLikeArtist) }
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
    }

    private fun viewObserver() {
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
