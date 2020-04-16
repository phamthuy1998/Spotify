package thuypham.ptithcm.spotify.ui.song


import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_now_playing.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentNowPlayingBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.notification.MusicNotification
import thuypham.ptithcm.spotify.service.SoundService
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel


class NowPlayingFragment : Fragment() {//, MediaController.MediaPlayerControl {

    private lateinit var binding: FragmentNowPlayingBinding
    private var song: Song? = null
    private var position: Int? = 0
    private lateinit var nowPlayingViewModel: NowPlayingViewModel

    private var notificationManager: NotificationManager? = null
    private var musicService: SoundService? = SoundService()

    // Check service init
    private var checkInitService = false
    // Check  user is changing the status of seek bar?
    private var checkChangeSeekBar = false

    //connect to the service
    private val musicConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SoundService.MusicBinder
            // get service
            musicService = binder.getService()
            song?.let {
                musicService?.setSong(it, position ?: 0)
                nowPlayingViewModel.songPlaying.postValue(song)
            }
            musicService?.setList(nowPlayingViewModel.listSongDb.value)
            musicService?.playSong()
            musicService?.setUIControls(seekBarSong, tvTimePlay, tvTotalTime)
            updateUI()
            notificationPlay()
            checkInitService = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            checkInitService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // init View model
        nowPlayingViewModel = ViewModelProviders
            .of(
                requireActivity(),
                Injection.provideNowPlayingViewModelFactory(requireActivity().application)
            )
            .get(NowPlayingViewModel::class.java)
        nowPlayingViewModel.isShowFragmentNowPlaying.value = true

        // Start service music
        val intent = Intent(requireContext(), SoundService.getInstance().javaClass)
        requireActivity().bindService(intent, musicConnection, Context.BIND_AUTO_CREATE);
        requireContext().startService(intent)

        // Start service notification
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            MusicNotification().createChannel(requireContext())
//            requireActivity().registerReceiver(broadcastReceiver, IntentFilter(NOTIFICATION))
//            requireActivity().startService(
//                Intent(
//                    requireActivity().baseContext,
//                    SoundService::class.java
//                )
//            )
//        }
    }

    // receive action from notification
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.extras?.getString(NOTIFICATION_ACT)) {
                ACT_PREV -> notificationPrev()
                ACT_PLAY -> if (isPlaying()) {
                    notificationPause()
                } else {
                    notificationPlay()
                }
                ACT_NEXT -> notificationNext()
                ACT_EXIT -> notificationExit()
            }
        }
    }

    private fun notificationNext() {

    }

    private fun notificationExit() {

    }

    private fun notificationPlay() {
        song?.let {
            MusicNotification().createNotification(requireContext(), it, 0, R.drawable.ic_pause, 1)
        }
    }

    private fun notificationPrev() {

    }

    private fun notificationPause() {
        song?.let {
            MusicNotification().createNotification(requireContext(), it, 0, R.drawable.ic_play, 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initMusicController()
        song = arguments?.getParcelable(SONG)
        position = arguments?.getInt(POSITION)
        if (song != null) {
            song?.id?.let { nowPlayingViewModel.getStatusLikeOfSong(it) }
            updateUI()
            addSongIntoHistory(song)
        } else Toast.makeText(requireContext(), "Can't load info of this song!", Toast.LENGTH_LONG)
            .show()
        bindingViewModel()
        addEvents()
    }

    private fun playPrev() {
        musicService?.playPrev()
    }

    private fun playNext() {
        musicService?.playNext()
    }

    private fun addEvents() {
        binding.btnPlay.setOnClickListener {
            eventButtonPlay()
        }
        binding.btnDismissSong.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnFavorite.setOnClickListener { onLikeSong() }
        binding.btnShuffle.setOnClickListener { setShuffle() }
        binding.btnRepeat.setOnClickListener { repeatSong() }
        binding.btnNext.setOnClickListener { playNext() }
        binding.btnPre.setOnClickListener { playPrev() }
    }

    override fun onResume() {
        super.onResume()
        nowPlayingViewModel.isShowFragmentNowPlaying.value = true
    }

    override fun onDestroy() {
        super.onDestroy()
        nowPlayingViewModel.isShowFragmentNowPlaying.value = false
    }

    private fun repeatSong() {
        musicService?.setRepeat()
        binding.btnRepeat.isSelected = musicService?.isRepeat() ?: false
    }

    private fun setShuffle() {
        binding.btnShuffle.isSelected = !binding.btnShuffle.isSelected
        musicService?.setShuffle()
    }

    private fun eventButtonPlay() {
        binding.btnPlay.isSelected = !binding.btnPlay.isSelected
        if (binding.btnPlay.isSelected) { // Playing
            musicService?.play()
        } else {
            musicService?.pausePlayer()
        }
    }

    private fun onLikeSong() {
        if (song != null && song?.id != null) {
            binding.btnFavorite.isSelected = !binding.btnFavorite.isSelected
            if (binding.btnFavorite.isSelected) nowPlayingViewModel.addFavoriteSong(song!!)
            else {
                nowPlayingViewModel.removeFavoriteSong(song?.id!!)
            }
        } else
            Toast.makeText(requireContext(), "Can't like this song!", Toast.LENGTH_LONG).show()
    }

    private fun updateUI() {
        binding.song = song
        tvTotalTime?.text = song?.time?.let { Song.timestampIntToMSS(it) }
        seekBarSong?.max = song?.time ?: 0
        btnPlay?.isSelected = isPlaying()
        btnRepeat?.isSelected = musicService?.isRepeat() ?: false
        btnShuffle?.isSelected = musicService?.isShuffle() ?: false
    }

    // Add this song into list history song of account
    private fun addSongIntoHistory(_song: Song?) {
        _song?.let { nowPlayingViewModel.addSongIntoHistory(it) }
    }

    private fun bindingViewModel() {
        nowPlayingViewModel.getAllSongInDb()?.observe(this, Observer { listSong ->
            if (!listSong.isNullOrEmpty()) nowPlayingViewModel.listSongDb.value = listSong
        })

        nowPlayingViewModel.requestLikeSong.observe(requireActivity(), Observer { isLike ->
            if (isLike?.status == Status.SUCCESS)
                Toast.makeText(requireContext(), "Saved to your music!", Toast.LENGTH_LONG).show()
            nowPlayingViewModel.requestLikeSong.value = null
        })

        nowPlayingViewModel.requestUnLikeSong.observe(requireActivity(), Observer { isUnlike ->
            if (isUnlike?.status == Status.SUCCESS)
                Toast.makeText(requireContext(), "Removed from your music!", Toast.LENGTH_LONG)
                    .show()
            nowPlayingViewModel.requestUnLikeSong.value = null
        })

        nowPlayingViewModel.checkSongIsLike.observe(requireActivity(), Observer {
            binding.btnFavorite.isSelected = it ?: false
        })

    }

    // Get status of media player in service
    fun isPlaying(): Boolean = musicService?.isPlaying() == true

}
