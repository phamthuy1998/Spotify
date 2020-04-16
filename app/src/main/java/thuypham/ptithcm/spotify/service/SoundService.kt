package thuypham.ptithcm.spotify.service

import android.app.Service
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
import thuypham.ptithcm.spotify.data.Song
import java.util.*


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

    private var songPosition: Int = 0
    private var checkSongIsPlaying: Boolean = false

    private var isShuffle = false
    private var isPlayingComplete = false
    private var isRepeat = false
    private val rand: Random? = null

    // 1
    override fun onCreate() {
        super.onCreate()
        initMediaPlayer()
    }

    //2
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun getSongInList() {
        // If list the current song is not the last song in list song
        when {
            isShuffle -> { /*Do nothing*/
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
        isPlayingComplete = true
        playSong()
    }

    fun playSong() {
        try {
            // The current song is not the previous song --> init media player
            // If this song is current song playing --> do nothing
            if (!checkSongIsPlaying) {
                if (mediaPlayer?.isPlaying == true) {
                    stop()
                    resetSong()
                }
                mediaPlayer?.setDataSource(this, Uri.parse(song?.fileName))
                mediaPlayer?.prepareAsync()
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun playNext() {
        if (isShuffle) { // Check if shuffle --> random a new position in list song
            // Random can be duplicated, TODO()--> use stack to random new position
            var newSongPosition: Int = songPosition
            while (newSongPosition == songPosition) {
                newSongPosition = rand?.nextInt(listMusic.size) ?: 0
            }
            songPosition = newSongPosition
        } else if (!isRepeat) {
            songPosition++
            if (songPosition >= listMusic.size) songPosition = 0
        }
        getSongInList()
    }

    fun playPrev() {
        if (isShuffle) { // Check if shuffle --> random a new position in list song
            // Random can be duplicated, TODO()--> use stack to random new position
            var newSongPosition: Int = songPosition
            while (newSongPosition == songPosition) {
                newSongPosition = rand?.nextInt(listMusic.size) ?: 0
            }
            songPosition = newSongPosition
        } else if (!isRepeat) {
            songPosition--
            if (songPosition < 0) songPosition = listMusic.size - 1
        }
        getSongInList()
    }

    fun seek(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun play() {
        mediaPlayer?.start()
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
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
        mediaPlayer?.pause()
    }

    fun releaseSong() {
        mediaPlayer?.release()
    }

    fun setList(listSong: List<Song>?) {
        listMusic = listSong ?: arrayListOf()
    }

    fun setSong(_song: Song, position: Int) {
        checkSongIsPlaying = _song.id == song?.id
        songPosition = position
        song = _song
    }

    fun setShuffle() {
        isShuffle = !isShuffle
    }

    fun setRepeat() {
        isRepeat = !isRepeat
    }

    fun isRepeat() = isRepeat

    fun isShuffle() = isShuffle

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

    // Async thread to update progress bar every second
    private val mProgressRunner: Runnable = object : Runnable {
        override fun run() {
            if (mSeekBar != null) {
                mSeekBar?.progress = mediaPlayer?.currentPosition ?: 0
                if (mediaPlayer?.isPlaying == true) {
                    mSeekBar?.postDelayed(this, mInterval.toLong())
                }
            }
        }
    }

    private var mSeekBar: SeekBar? = null
    private var mCurrentPosition: TextView? = null
    private var mTotalDuration: TextView? = null
    private val mInterval = 1000
    fun setUIControls(
        seekBar: SeekBar,
        currentPosition: TextView,
        totalDuration: TextView
    ) {
        mSeekBar = seekBar
        mCurrentPosition = currentPosition
        mTotalDuration = totalDuration
        mSeekBar?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) { // Change current position of the song playback
                    mediaPlayer?.seekTo(progress)
                }
                // Update our textView to display the correct number of second in format 0:00
                mCurrentPosition?.text = Song.timestampIntToMSS(progress/1000)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mediaPlayer?.start()
        val duration = mp!!.duration
        mSeekBar?.max = duration
        mSeekBar?.postDelayed(mProgressRunner, mInterval.toLong())
        showNotification()
    }

    private fun showNotification() {

    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        resetSong()
        return false;
    }

    // Called when the song end
    override fun onCompletion(mp: MediaPlayer?) {
        mp?.reset();
        playNext();
    }

    override fun onAudioFocusChange(focusChange: Int) {

    }

    inner class MusicBinder : Binder() {
        fun getService(): SoundService {
            return this@SoundService
        }
    }
}
