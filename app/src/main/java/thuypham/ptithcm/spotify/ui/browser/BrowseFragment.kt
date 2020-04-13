package thuypham.ptithcm.spotify.ui.browser


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Country
import thuypham.ptithcm.spotify.data.MusicGenre
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentBrowseBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.ui.album.AllAlbumFragment
import thuypham.ptithcm.spotify.ui.browser.adapter.MusicGenreAdapter
import thuypham.ptithcm.spotify.ui.browser.adapter.TopHitAdapter
import thuypham.ptithcm.spotify.ui.country.CountryFragment
import thuypham.ptithcm.spotify.ui.genre.MusicGenreFragment
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.BrowserViewModel
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel

class BrowseFragment : Fragment() {

    companion object {
        private var instance: BrowseFragment? = null
        fun getInstance() = instance ?: BrowseFragment()
    }

    private val musicGenreAdapter by lazy {
        MusicGenreAdapter(this::musicGenreOnClick)
    }
    private val topMusicAdapter by lazy {
        TopHitAdapter(this::topMusicClick)
    }

    private fun topMusicClick(country: Country?) {
        val countryFragment = CountryFragment()
        val arguments = Bundle()
        arguments.putParcelable(COUNTRY, country)
        countryFragment.arguments = arguments
        requireActivity().replaceFragment(
            id = R.id.frmMain,
            fragment = countryFragment,
            addToBackStack = true
        )
    }

    private lateinit var browserViewModel: BrowserViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private lateinit var binding: FragmentBrowseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        browserViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideBrowserViewModelFactory())
            .get(BrowserViewModel::class.java)
        nowPlayingViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory())
            .get(NowPlayingViewModel::class.java)
    }

    private fun musicGenreOnClick(genre: MusicGenre?) {
        val musicGenreFragment = MusicGenreFragment()
        val arguments = Bundle()
        arguments.putParcelable(MUSIC_GENRE, genre)
        musicGenreFragment.arguments = arguments
        requireActivity().replaceFragment(
            id = R.id.frmMain,
            fragment = musicGenreFragment,
            addToBackStack = true
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBrowseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMusicGenre()
        initTopMusic()
        bindViewModel()
        addEvents()
    }


    private fun initMusicGenre() {
        binding.rvGenreMood.adapter = musicGenreAdapter
        binding.rvGenreMood.hasFixedSize()
        binding.rvGenreMood.setItemViewCacheSize(20)
    }

    private fun initTopMusic() {
        binding.rvTopHit.adapter = topMusicAdapter
        binding.rvTopHit.hasFixedSize()
        binding.rvTopHit.setItemViewCacheSize(20)
    }

    private fun addEvents() {
        binding.swRefreshBrowser.setOnRefreshListener {
            browserViewModel.refresh()
            binding.swRefreshBrowser.isRefreshing = false
        }
        binding.itemFeaturedAlbum.setOnClickListener { showAllAlbum() }
    }

    private fun showAllAlbum() {
        requireActivity().replaceFragment(
            id = R.id.frmMain,
            fragment = AllAlbumFragment(),
            addToBackStack = true
        )
    }

    private fun bindViewModel() {
        // List music genre
        browserViewModel.listMusicGenre.observe(requireActivity(), Observer {
            musicGenreAdapter.addDataMusicGenre(it)
        })

        browserViewModel.networkStateMusicGenre.observe(requireActivity(), Observer {
            when (it.status) {
                Status.FAILED -> {
                    binding.progressBrowser.gone()
                    binding.tvGenresMoods.gone()
                }
                Status.RUNNING -> {
                    binding.progressBrowser.show()
                    binding.tvGenresMoods.gone()
                }
                Status.SUCCESS -> {
                    binding.tvGenresMoods.show()
                    binding.progressBrowser.gone()
                }
            }
        })

        // List top hit
        browserViewModel.listTopHit.observe(this, Observer {
            topMusicAdapter.addDataTopHit(it)
        })

        browserViewModel.networkStateTopMusic.observe(requireActivity(), Observer {
            when (it.status) {
                Status.FAILED -> {
                    binding.progressBrowser.gone()
                }
                Status.RUNNING -> {
                    binding.progressBrowser.show()
                }
                Status.SUCCESS -> {
                    binding.progressBrowser.gone()
                }
            }
        })

        // feature album
        browserViewModel.album.observe(this, Observer {
            binding.album = it
        })
        browserViewModel.networkStateAlbum.observe(this, Observer {
            when (it.status) {
                Status.FAILED -> {
                    binding.itemFeaturedAlbum.gone()
                    binding.tvFeatureAlbum.gone()
                    binding.lineFeature.gone()
                }
                Status.RUNNING -> {
                    binding.itemFeaturedAlbum.gone()
                    binding.tvFeatureAlbum.gone()
                    binding.lineFeature.gone()
                }
                Status.SUCCESS -> {
                    binding.itemFeaturedAlbum.show()
                    binding.tvFeatureAlbum.show()
                    binding.lineFeature.show()
                }
            }
        })
    }
}
