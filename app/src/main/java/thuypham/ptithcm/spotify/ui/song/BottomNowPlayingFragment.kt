package thuypham.ptithcm.spotify.ui.song


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_bottom_now_playing.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.databinding.FragmentBottomNowPlayingBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.event.SongChangedListener
import thuypham.ptithcm.spotify.service.PLAYING
import thuypham.ptithcm.spotify.service.SoundService
import thuypham.ptithcm.spotify.util.SONG
import thuypham.ptithcm.spotify.util.replaceFragment
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel

class BottomNowPlayingFragment : Fragment() , SongChangedListener {

    private lateinit var nowPlayingViewModel: NowPlayingViewModel
    private lateinit var binding: FragmentBottomNowPlayingBinding

    private var song: Song? = null

    // Check service init
    private var checkInitService = false
    private var musicService: SoundService? = SoundService()
    private val musicConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SoundService.MusicBinder
            // get service
            musicService = binder.getService()
            musicService?.setSongChangedListener(this@BottomNowPlayingFragment)
            checkInitService = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            checkInitService = false
        }
    }

    override fun onSongChanged(song: Song) {
        binding.song= song
    }

    override fun onStatusPlayingChanged(status: Int) {
       btnBotPlaySong?.isSelected = status == PLAYING
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding = FragmentBottomNowPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
        addEvent()
    }

    private fun addEvent() {
        binding.layoutBotPlaying.setOnClickListener {
            showSongFragment()
        }
        binding.btnBotPlaySong.setOnClickListener {
            btnPlaySongOnclick()
        }
    }

    private fun btnPlaySongOnclick() {
        binding.btnBotPlaySong.isSelected = !binding.btnBotPlaySong.isSelected
        nowPlayingViewModel.isPlaying.value = binding.btnBotPlaySong.isSelected
        if (binding.btnBotPlaySong  .isSelected) { // Playing
            musicService?.play()
        } else {
            musicService?.pausePlayer()
        }
    }

    private fun showSongFragment() {
        val nowPlayingFragment = NowPlayingFragment.getInstance()
        val arguments = Bundle()
        arguments.putParcelable(SONG, song)
        nowPlayingFragment.arguments = arguments
        activity.replaceFragment(
            id = R.id.frmMain,
            fragment = nowPlayingFragment,
            tag = "NowPlaying",
            addToBackStack = true
        )
        musicService?.setContinuePlaying()
    }

    private fun bindViewModel() {
        nowPlayingViewModel.songPlaying.observe(this, Observer { songInfo ->
            songInfo.let {
                song = songInfo
                binding.song = it
            }
        })
        nowPlayingViewModel.isPlaying.observe(this, Observer {
            btnBotPlaySong?.isSelected = it ?: false
        })

        nowPlayingViewModel.isShowFragmentNowPlaying.observe(this, Observer {isShowNowPlaying->
//            if (isShowNowPlaying) layoutBotPlaying?.gone()
//            else layoutBotPlaying?.show()
        })
    }
}
