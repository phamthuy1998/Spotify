package thuypham.ptithcm.spotify.service

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import java.io.IOException


class MusicBinderService : Binder() {
    // Save local audio file uri ( local storage file. ).
    var audioFileUri: Uri? = null
    // Save web audio file url.
    var audioFileUrl = ""
    // Check if stream audio.
    var isStreamAudio = false
    // Media player that play audio.
    private var audioPlayer: MediaPlayer? = null
    // Caller activity context, used when play local audio file.
    var context: Context? = null
    // This Handler object is a reference to the caller activity's Handler.
// In the caller activity's handler, it will update the audio play progress.
    private var audioProgressUpdateHandler: Handler? = null
    // This is the message signal that inform audio progress updater to update audio progress.
    val UPDATE_AUDIO_PROGRESS_BAR = 1

    fun getAudioProgressUpdateHandler(): Handler? {
        return audioProgressUpdateHandler
    }

    fun setAudioProgressUpdateHandler(audioProgressUpdateHandler: Handler?) {
        this.audioProgressUpdateHandler = audioProgressUpdateHandler
    }

    // Start play audio.
    fun startAudio() {
        initAudioPlayer()
        if (audioPlayer != null) {
            audioPlayer!!.start()
        }
    }

    // Pause playing audio.
    fun pauseAudio() {
        if (audioPlayer != null) {
            audioPlayer!!.pause()
        }
    }

    // Stop play audio.
    fun stopAudio() {
        if (audioPlayer != null) {
            audioPlayer!!.stop()
            destroyAudioPlayer()
        }
    }

    // Initialise audio player.
    private fun initAudioPlayer() {
        try {
            if (audioPlayer == null) {
                audioPlayer = MediaPlayer()
                if (!TextUtils.isEmpty(audioFileUrl)) {
                    if (isStreamAudio) {
                        audioPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                    }
                    audioPlayer!!.setDataSource(audioFileUrl)
                } else {
                    context?.let { audioFileUri?.let { it1 -> audioPlayer!!.setDataSource(it, it1) } }
                }
                audioPlayer!!.prepare()
                // This thread object will send update audio progress message to caller activity every 1 second.
                val updateAudioProgressThread: Thread = object : Thread() {
                    override fun run() {
                        while (true) { // Create update audio progress message.
                            val updateAudioProgressMsg = Message()
                            updateAudioProgressMsg.what = UPDATE_AUDIO_PROGRESS_BAR
                            // Send the message to caller activity's update audio prgressbar Handler object.
                            audioProgressUpdateHandler?.sendMessage(updateAudioProgressMsg)
                            // Sleep one second.
                            try {
                                sleep(1000)
                            } catch (ex: InterruptedException) {
                                ex.printStackTrace()
                            }
                        }
                    }
                }
                // Run above thread object.
                updateAudioProgressThread.start()
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    // Destroy audio player.
    private fun destroyAudioPlayer() {
        if (audioPlayer != null) {
            if (audioPlayer!!.isPlaying) {
                audioPlayer!!.stop()
            }
            audioPlayer!!.release()
            audioPlayer = null
        }
    }

    // Return current audio play position.
    val currentAudioPosition: Int
        get() {
            var ret = 0
            if (audioPlayer != null) {
                ret = audioPlayer!!.currentPosition
            }
            return ret
        }

    // Return total audio file duration.
    val totalAudioDuration: Int
        get() {
            var ret = 0
            if (audioPlayer != null) {
                ret = audioPlayer!!.duration
            }
            return ret
        }

    // Return current audio player progress value.
    val audioProgress: Int
        get() {
            var ret = 0
            val currAudioPosition = currentAudioPosition
            val totalAudioDuration = totalAudioDuration
            if (totalAudioDuration > 0) {
                ret = currAudioPosition * 100 / totalAudioDuration
            }
            return ret
        }
}