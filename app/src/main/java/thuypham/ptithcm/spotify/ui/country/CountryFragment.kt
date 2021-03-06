package thuypham.ptithcm.spotify.ui.country


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
import thuypham.ptithcm.spotify.data.Country
import thuypham.ptithcm.spotify.data.EventTypeSong
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentCountryBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.service.SoundService
import thuypham.ptithcm.spotify.ui.country.adapter.SongCountryAdapter
import thuypham.ptithcm.spotify.ui.song.NowPlayingFragment
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.CountryViewModel
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel

class CountryFragment : Fragment() {

    private lateinit var binding: FragmentCountryBinding
    private lateinit var countryViewModel: CountryViewModel
    private lateinit var nowPlayingViewModel: NowPlayingViewModel

    private val songAdapter by lazy {
        SongCountryAdapter(
            mutableListOf(),
            this::songEvents
        )
    }
    private var country: Country? = null

    private fun songEvents(song: Song?, position: Int, type: EventTypeSong) {
        when (type) {
            EventTypeSong.ITEM_CLICK -> showFragmentNowPlaying(song, position)
            EventTypeSong.SHOW_MORE -> {
                // Todo() -> show dialog
            }
        }
    }

    private fun showFragmentNowPlaying(song: Song?, position: Int) {
        // Delete all song before insert list song into database
        // insert list song to play song when the current song is end
        nowPlayingViewModel.deleteAllSong()
        nowPlayingViewModel.insertSongs(countryViewModel.listSong.value ?: arrayListOf())
        nowPlayingViewModel.listSongPlaying.value = countryViewModel.listSong.value
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
        countryViewModel = ViewModelProviders.of(this, Injection.provideCountryViewModelFactory())
            .get(CountryViewModel::class.java)
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
        binding = FragmentCountryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        country = arguments?.getParcelable(COUNTRY)
        if (country != null) {
            binding.country = country
            country?.id?.let { countryViewModel.getListSong(it) }
            addEvents()
            initAdapter()
            bindViewModel()
        } else
            Toast.makeText(requireContext(), "Can't load info!", Toast.LENGTH_LONG).show()

    }

    private fun initAdapter() {
        binding.rvSongCountry.adapter = songAdapter
        binding.rvSongCountry.setHasFixedSize(true)
        binding.rvSongCountry.setItemViewCacheSize(20)
    }

    private fun bindViewModel() {
        countryViewModel.networkStateListSong.observe(this, Observer {
            when (it?.status) {
                Status.FAILED -> {
                    binding.progressCountry.gone()
                }
                Status.SUCCESS -> {
                    binding.progressCountry.gone()

                }
                Status.RUNNING -> {
                    binding.progressCountry.show()
                }
            }
        })
        countryViewModel.listSong.observe(this, Observer {
            songAdapter.addDataListSong(it)
        })
    }

    private fun addEvents() {
        binding.btnBackTopHit.setOnClickListener { requireActivity().onBackPressed() }
        binding.swRefreshCountry.setOnRefreshListener {
            country?.id?.let { countryViewModel.getListSong(it) }
            binding.swRefreshCountry.isRefreshing = false
        }
        binding.btnPlayAllSongCounty.setOnClickListener {
            val position =
                countryViewModel.listSong.value?.size?.let { it1 -> randomPositionSong(0, it1) }
            showFragmentNowPlaying(
                countryViewModel.listSong.value?.get(position ?: 0), position ?: 0
            )
            musicService?.setShuffle(true)
        }
    }

}
