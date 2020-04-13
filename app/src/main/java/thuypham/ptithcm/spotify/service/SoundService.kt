package thuypham.ptithcm.spotify.service

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.widget.Toast
import thuypham.ptithcm.spotify.data.Song


class SoundService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    companion object {
        private var instance: SoundService? = null
        fun getInstance() = instance ?: SoundService()
    }

    private val musicBind by lazy {
        MusicBinder()
    }

    private lateinit var listMusic: ArrayList<Song>
    private lateinit var song: Song

    private var mediaPlayer: MediaPlayer? = null
    private var uri: String = ""

    private var position: Int = 0

//    //2 , if(exist) --> skip on create
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        uri = intent?.getStringExtra("uriSong") ?: ""
//        song.fileName?.let { playAudio() }
//        return START_STICKY
//    }

    fun playList() {
        song = listMusic[position]
        try {
            mediaPlayer?.setDataSource(this, Uri.parse(song.fileName))
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun playSong(_song: Song) {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
                mediaPlayer?.reset()
            }
            mediaPlayer?.setDataSource(this, Uri.parse(_song.fileName))
            mediaPlayer?.prepareAsync()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun resetSong() {
        mediaPlayer?.reset()
    }


    fun setList(listSong: ArrayList<Song>) {
        listMusic = listSong
    }

    // 1
    override fun onCreate() {
        super.onCreate()
        initMediaPlayer()
    }

    /*
    * WAKE LOCK sẽ cho phép tiếp tục phát nhạc khi thiết bị ở trong chế độ idle và chúng ta thiết lập kiểu stream thành music.
    * */
    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setWakeMode(
            applicationContext,
            PowerManager.PARTIAL_WAKE_LOCK
        )
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer?.setOnPreparedListener(this);
        mediaPlayer?.setOnCompletionListener(this);
        mediaPlayer?.setOnErrorListener(this);
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Giải phóng service khi người dùng thoát khỏi ứng dụng
    override fun onUnbind(intent: Intent?): Boolean {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        return false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return musicBind
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer?.start()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompletion(mp: MediaPlayer?) {

    }

    override fun onAudioFocusChange(focusChange: Int) {

    }

    inner class MusicBinder : Binder() {
        fun getService(): SoundService {
            return this@SoundService
        }
    }
}
