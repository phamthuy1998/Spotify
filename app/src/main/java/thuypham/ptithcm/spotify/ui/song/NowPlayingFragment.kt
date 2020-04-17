package thuypham.ptithcm.spotify.ui.song


import android.app.NotificationManager
import android.content.*
import android.os.Build
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
import thuypham.ptithcm.spotify.event.SongChangedListener
import thuypham.ptithcm.spotify.notification.MusicNotification
import thuypham.ptithcm.spotify.service.PLAYING
import thuypham.ptithcm.spotify.service.PLAY_NEXT
import thuypham.ptithcm.spotify.service.PLAY_PREV
import thuypham.ptithcm.spotify.service.SoundService
import thuypham.ptithcm.spotify.util.*
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel


class NowPlayingFragment : Fragment(), SongChangedListener {

    companion object {
        private var instance: NowPlayingFragment? = null
        fun getInstance() = instance ?: NowPlayingFragment()
    }

    private lateinit var binding: FragmentNowPlayingBinding
    private var song: Song? = null
    private var position: Int? = 0
    private lateinit var nowPlayingViewModel: NowPlayingViewModel

    private var notificationManager: NotificationManager? = null
    private var musicService: SoundService? = SoundService()

    // Check service init
    private var checkInitService = false
    // Check  user is changing the status of seek bar?

    //connect to the service
    private val musicConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SoundService.MusicBinder
            // get service
            musicService = binder.getService()
            // Set up service
            song?.let {
                musicService?.setSong(it, position ?: 0)
                nowPlayingViewModel.songPlaying.postValue(song)
            }
            musicService?.setList(nowPlayingViewModel.listSongPlaying.value)
            musicService?.setSongChangedListener(this@NowPlayingFragment)
            musicService?.setUISong(seekBarSong, tvTimePlay, tvTotalTime)
            musicService?.setUpState(PLAYING)
            updateUI()
            checkInitService = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            checkInitService = false
        }
    }

    override fun onSongChanged(song: Song) {
        this.song = song
        nowPlayingViewModel.songPlaying.postValue(song)
        // check this fragment is attach on activity
        if (isAdded) updateUI()
    }

    override fun onStatusPlayingChanged(status: Int) {
//        nowPlayingViewModel.isPlaying.value = status == PLAYING
//        progressSongInfo?.visibility = if (status == PREPARE_PLAYING) View.VISIBLE else View.GONE
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
        nowPlayingViewModel.isPlaying.value = true
        // Start service music
        val intent = Intent(requireContext(), SoundService.getInstance().javaClass)
        requireActivity().bindService(intent, musicConnection, Context.BIND_AUTO_CREATE);
        requireContext().startService(intent)

        // Start service notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MusicNotification().createChannel(requireContext())
            requireActivity().registerReceiver(broadcastReceiver, IntentFilter(NOTIFICATION))
        }
    }

    // receive action from notification
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.extras?.getString(NOTIFICATION)) {
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

        Toast.makeText(requireContext(), "Play next", Toast.LENGTH_LONG).show()
    }

    private fun notificationExit() {
        Toast.makeText(requireContext(), "Exit", Toast.LENGTH_LONG).show()

    }

    private fun notificationPlay() {
        Toast.makeText(requireContext(), "Play song", Toast.LENGTH_LONG).show()
        song?.let {
            MusicNotification().createNotification(requireContext(), it, 0, R.drawable.ic_pause, 1)
        }
    }

    private fun notificationPrev() {

        Toast.makeText(requireContext(), "Play Prev ", Toast.LENGTH_LONG).show()
    }

    private fun notificationPause() {
        Toast.makeText(requireContext(), "Pause song", Toast.LENGTH_LONG).show()
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

    private fun addEvents() {
        binding.btnPlay.setOnClickListener {
            eventButtonPlay()
        }
        binding.btnDismissSong.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnFavorite.setOnClickListener { onLikeSong() }
        binding.btnShuffle.setOnClickListener { setShuffle() }
        binding.btnRepeat.setOnClickListener { repeatSong() }
        binding.btnNext.setOnClickListener { musicService?.setUpState(PLAY_NEXT) }
        binding.btnPre.setOnClickListener { musicService?.setUpState(PLAY_PREV) }
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
        binding.btnRepeat.isSelected = !binding.btnRepeat.isSelected
        musicService?.setRepeat(binding.btnRepeat.isSelected)
    }

    private fun setShuffle() {
        binding.btnShuffle.isSelected = !binding.btnShuffle.isSelected
        musicService?.setShuffle(binding.btnShuffle.isSelected)
    }

    private fun eventButtonPlay() {
        binding.btnPlay.isSelected = !binding.btnPlay.isSelected
        nowPlayingViewModel.isPlaying.value = binding.btnPlay.isSelected
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
        btnPlay?.isSelected = nowPlayingViewModel.isPlaying.value ?: true
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

        nowPlayingViewModel.isPlaying.observe(this, Observer {
            btnPlay?.isSelected = it ?: false
        })

    }

    // Get status of media player in service
    fun isPlaying(): Boolean = musicService?.isPlaying() == true

}
