package thuypham.ptithcm.spotify.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.event.SongChangedListener
import thuypham.ptithcm.spotify.notification.MusicNotification
import thuypham.ptithcm.spotify.util.*


const val STOPPED = 0
const val PAUSED = 1
const val PLAYING = 2
const val PLAY_NEXT = 3
const val PLAY_PREV = 4
const val SHUFFLE = 5
const val REPEAT = 6
const val PREPARE_PLAYING = 7

class SoundService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    companion object {
        private var instance: SoundService? = null
        fun getInstance() = instance ?: SoundService()
    }

    private val musicBind by lazy {
        MusicBinder()
    }
    private var listMusic: List<Song> = arrayListOf()
    private var song: Song? = null
    private var mediaPlayer: MediaPlayer? = null

    // Save current index of song is playing  in list song
    private var songPosition: Int = 0

    // Status
    private var isShuffle = false
    private var isPlayingComplete = false
    private var isRepeat = false

    // Views
    private var mSeekBar: SeekBar? = null
    private var mCurrentPosition: TextView? = null
    private var mTotalDuration: TextView? = null
    private val mTimer = 1000


    private val playerState = STOPPED

    // SOng changed listener
    private var songChangedListener: SongChangedListener? = null


    private var continuePlaying = false
    private var timeContinue = false
    fun setContinuePlaying() {
        continuePlaying = true
        timeContinue = true
    }

    private var musicNotification = MusicNotification()

    // 1
    override fun onCreate() {
        super.onCreate()
        initMediaPlayer()
    }

    //2
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    // Async thread to update progress bar every second
    private val mProgressRunner: Runnable = object : Runnable {
        override fun run() {
            if (mSeekBar != null) {
                mSeekBar?.progress = mediaPlayer?.currentPosition?.div(1000) ?: 0
                if (mediaPlayer?.isPlaying == true) {
                    mSeekBar?.postDelayed(this, mTimer.toLong())
                }
            }
        }
    }

    /*
   * WAKE LOCK allow to continue playing the song when device in Idle mode and we can set up stream into music
   * */
    private fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setWakeMode(
            applicationContext,
            PowerManager.PARTIAL_WAKE_LOCK
        )
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer?.setOnPreparedListener(this)
        mediaPlayer?.setOnCompletionListener(this)
        mediaPlayer?.setOnErrorListener(this)
    }

    fun setUISong(seekBar: SeekBar?, currentPosition: TextView?, totalDuration: TextView?) {
        mSeekBar = seekBar
        mCurrentPosition = currentPosition
        mTotalDuration = totalDuration
        mSeekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) { // Change current position of the song playback
                    mediaPlayer?.seekTo(progress*1000)
                }
                // Update our textView to display the correct number of second in format 0:00
                mCurrentPosition?.text = Song.timestampIntToMSS(progress)
                    /*String.format(
                        "%d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(progress.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(progress.toLong()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(progress.toLong()))
                    )*/
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    fun setSongChangedListener(songChanged: SongChangedListener) {
        this.songChangedListener = songChanged
    }

    private fun getSongInList() {
        // If list the current song is not the last song in list song
        when {
            isRepeat -> { /*Do nothing*/
            }
            songPosition < listMusic.size -> {
                song = listMusic[songPosition]
            }
            else -> {
                songPosition = 0
                /*   reset position song to begin song in list song,
                   if list is empty -> the song will play on repeat mode
               */
                song = if (listMusic.isNotEmpty()) listMusic[0] else song
            }
        }
        playSong()
    }

    fun setUpState(state: Int) {
        when (state) {
            PLAYING -> {
                playSong()
            }
            STOPPED -> {

            }
            PAUSED -> {
                pausePlayer()
            }
            PLAY_NEXT -> {
                playNext()
            }
            PLAY_PREV -> {
                playPrev()
            }
        }
    }

    private fun playSong() {
        try {
            if (!continuePlaying) {
                stop()
                resetSong()
                mediaPlayer?.setDataSource(this, Uri.parse(song?.fileName))
                mediaPlayer?.prepareAsync()
            }
            continuePlaying = false
            song?.let { songChangedListener?.onSongChanged(it) }
            mProgressRunner.run()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun playNext() {
        if (isShuffle) { // Check if shuffle --> random a new position in list song
            songPosition = randomPositionSong(0, listMusic.size-1)
        } else if (!isRepeat) {
            songPosition++
            if (songPosition >= listMusic.size) songPosition = 0
        }
        getSongInList()
        songChangedListener?.onStatusPlayingChanged(PLAY_NEXT)
    }

    private fun playPrev() {
        if (isShuffle) { // Check if shuffle --> random a new position in list song
            songPosition = randomPositionSong(0, listMusic.size-1)
        } else if (!isRepeat) {
            songPosition--
            if (songPosition < 0) songPosition = listMusic.size - 1
        }
        getSongInList()
        songChangedListener?.onStatusPlayingChanged(PLAY_PREV)
    }

    fun play() {
        mediaPlayer?.start()
//        mProgressRunner.run()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    private fun stop() {
        mediaPlayer?.stop()
    }

    private fun resetSong() {
        mediaPlayer?.reset()
    }

    fun getPosition(): Int? {
        return mediaPlayer?.currentPosition
    }

    fun getDuration(): Int? {
        return mediaPlayer?.duration
    }

    fun isPlaying(): Boolean? {
        return mediaPlayer?.isPlaying
    }

    fun pausePlayer() {
        if (mediaPlayer?.isPlaying == true)
            mediaPlayer?.pause()
        songChangedListener?.onStatusPlayingChanged(PAUSED)
    }

    private fun releaseSong() {
        mediaPlayer?.release()
    }

    fun setList(listSong: List<Song>?) {
        listMusic = listSong ?: arrayListOf()
    }

    fun setSong(_song: Song, position: Int) {
        songPosition = position
        song = _song
    }

    fun setShuffle(_isShuffle: Boolean) {
        isShuffle = _isShuffle
    }

    fun setRepeat(_isRepeat: Boolean) {
        isRepeat = _isRepeat
    }

    fun isRepeat() = isRepeat

    fun isShuffle() = isShuffle

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            try {
                stop()
                resetSong()
                releaseSong()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        stopForeground(true)
    }

    // Release data when user exit app
    override fun onUnbind(intent: Intent?): Boolean {
        stop()
        resetSong()
        releaseSong()
        return false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return musicBind
    }

    fun getCurrentPosition() = mediaPlayer?.currentPosition?.div(1000)

    fun isPlayingSongComplete() = isPlayingComplete

    fun getSongIsPlaying() = song

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer?.start()
        mSeekBar?.max = mp?.duration ?: 0
        mSeekBar?.postDelayed(mProgressRunner, mTimer.toLong())
//        songChangedListener?.onStatusPlayingChanged(PLAYING)
    }

    fun getStatusOfPlayButton() =
        if (isPlaying() == true) R.drawable.ic_pause else R.drawable.ic_play

    // receive action from notification
    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.extras?.getString(NOTIFICATION)) {
                ACT_PREV -> {
                    song?.let {
                        MusicNotification().createNotification(
                            context,
                            it, getStatusOfPlayButton(), songPosition, listMusic.size
                        )
                    }
                    playPrev()
                }
                ACT_PLAY -> {
                    if (isPlaying() == true) pausePlayer() else play()
                    song?.let {
                        MusicNotification().createNotification(
                            context,
                            it, getStatusOfPlayButton(), songPosition, listMusic.size
                        )
                    }
                }
                ACT_NEXT -> {
                    song?.let {
                        MusicNotification().createNotification(
                            context,
                            it, getStatusOfPlayButton(), songPosition, listMusic.size
                        )
                    }
                    playNext()
                }
                ACT_EXIT -> {
                    exitNotification()
                }
            }
        }
    }

    private fun exitNotification() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        resetSong()
        return false;
    }

    // Called when the song end
    override fun onCompletion(mp: MediaPlayer?) {
        mp?.reset();
        mSeekBar?.removeCallbacks(mProgressRunner)
        playNext()
    }

    override fun onAudioFocusChange(focusChange: Int) {

    }

    inner class MusicBinder : Binder() {
        fun getService(): SoundService {
            return this@SoundService
        }
    }
}
