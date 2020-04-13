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
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_now_playing.*
import org.greenrobot.eventbus.EventBus
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Status
import thuypham.ptithcm.spotify.databinding.FragmentNowPlayingBinding
import thuypham.ptithcm.spotify.di.Injection
import thuypham.ptithcm.spotify.service.SoundService
import thuypham.ptithcm.spotify.util.gone
import thuypham.ptithcm.spotify.util.show
import thuypham.ptithcm.spotify.viewmodel.NowPlayingViewModel


class NowPlayingFragment : Fragment(), MediaController.MediaPlayerControl {

    private lateinit var binding: FragmentNowPlayingBinding
    private var song: Song? = null
    private lateinit var nowPlayingViewModel: NowPlayingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nowPlayingViewModel = ViewModelProviders
            .of(requireActivity(), Injection.provideNowPlayingViewModelFactory())
            .get(NowPlayingViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var soundService:SoundService

    private var checkInitService = false
    //connect to the service
    private val musicConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SoundService.MusicBinder
            // get service
            soundService = binder.getService()
            // pass list
            checkInitService = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            checkInitService = false
        }
    }


    override fun onStart() {
        super.onStart()
        // Start service
        val intent = Intent(requireContext(), soundService.javaClass)
        requireActivity().bindService(intent, musicConnection, Context.BIND_AUTO_CREATE);
        requireContext().startService(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (nowPlayingViewModel.songID.value != null) {
            nowPlayingViewModel.getSongInfo(nowPlayingViewModel.songID.value!!)
            nowPlayingViewModel.getStatusLikeOfSong(nowPlayingViewModel.songID.value!!)
        } else Toast.makeText(requireContext(), "Can't load info of this song!", Toast.LENGTH_LONG)
            .show()
        addEvents()
        bindingViewModel()
    }

    private fun addEvents() {
        binding.btnPlay.setOnClickListener {
            //            nowPlayingViewModel.songData.value?.id?.let { it1 -> appViewModel.playMediaId(it1) }
            stopAudio()
        }
        binding.btnDismissSong.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnFavorite.setOnClickListener { onLikeSong() }
    }

    private fun onLikeSong() {
        if (song != null && song?.id != null) {
            binding.btnFavorite.isSelected = !binding.btnFavorite.isSelected
            if (binding.btnFavorite.isSelected) nowPlayingViewModel.addFavoriteSong(song!!)
            else {
                nowPlayingViewModel.removeFavoriteSong(song?.id!!)
                EventBus.getDefault().post(true)
            }
        } else
            Toast.makeText(requireContext(), "Can't like this song!", Toast.LENGTH_LONG).show()
    }

    private fun playSong(song: Song?) {
        song?.let { soundService.playSong(it) }
    }

    private fun stopAudio() {
        val objIntent = Intent(requireContext(), SoundService.getInstance()::class.java)
        requireContext().stopService(objIntent)
    }

    private fun updateUI(_song: Song?) {
        song = _song
        binding.song = song
        binding.tvTotalTime.text = song?.time?.let { Song.timestampIntToMSS(requireContext(), it) }
        playSong(song)
        addSongIntoHistory(song)
    }

    private fun addSongIntoHistory(_song: Song?) {
        _song?.let { nowPlayingViewModel.addSongIntoHistory(it) }
    }

    private fun bindingViewModel() {
        // Song info
        nowPlayingViewModel.songData.observe(this, Observer {
            updateUI(it)
        })

        nowPlayingViewModel.networkStateSong.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    song?.let { it1 -> nowPlayingViewModel.addSongIntoHistory(it1) }
                    binding.progressSongInfo.gone()
                }
                Status.RUNNING -> {
                    binding.progressSongInfo.show()
                }
                Status.FAILED -> {
                    Toast.makeText(requireContext(), "Err: ${it?.msg}", Toast.LENGTH_LONG).show()
                    binding.progressSongInfo.gone()
                }
            }
        })

        nowPlayingViewModel.currentTimeMedia.observe(this, Observer { positon ->
            binding.tvTimePlay.text = Song.timestampToMSS(requireContext(), positon)
        })

        nowPlayingViewModel.buttonPlayRes.observe(this, Observer { res ->
            btnPlay.setImageResource(res)
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

    override fun isPlaying(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canSeekForward(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDuration(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBufferPercentage(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun seekTo(pos: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentPosition(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canSeekBackward(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun canPause(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
