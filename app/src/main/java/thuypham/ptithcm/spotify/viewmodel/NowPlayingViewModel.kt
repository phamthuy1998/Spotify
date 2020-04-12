package thuypham.ptithcm.spotify.viewmodel

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.*
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.repository.SongRepository

class NowPlayingViewModel(
    private val repository: SongRepository
) : ViewModel() {
    private var requestSong = MutableLiveData<ResultData<Song>>()
    var requestLikeSong = MutableLiveData<NetworkState>()
    var requestUnLikeSong = MutableLiveData<NetworkState>()
    var checkSongIsLike = MutableLiveData<Boolean>()
    private val handler = Handler(Looper.getMainLooper())
    private var playbackState: PlaybackStateCompat = EMPTY_PLAYBACK_STATE
    private var updatePosition = true

    val songData: LiveData<Song> =
        Transformations.switchMap(requestSong) {
            it.data
        }

    val networkStateSong: LiveData<NetworkState> = Transformations.switchMap(requestSong) {
        it.networkState
    }

    fun getSongInfo(songID: String) {
        requestSong.value = repository.getSongByID(songID)
    }

    fun getStatusLikeOfSong(songID: String) {
        checkSongIsLike = repository.checkSongFavorite(songID)
    }

    fun addSongIntoHistory(song: Song) {
        repository.addSongIntoHistory(song)
    }

    val currentTimeMedia = MutableLiveData<Long>().apply {
        postValue(0L)
    }

    val buttonPlayRes = MutableLiveData<Int>().apply {
        postValue(R.drawable.ic_btn_play)
    }

    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        updateState(playbackState)
    }

    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        updateState(playbackState)
    }

    private fun updateState(playbackState: PlaybackStateCompat) {
//        // Update the media button resource ID
//        buttonPlayRes.postValue(
//            when (playbackState.isPlaying) {
//                true -> R.drawable.ic_btn_pause
//                else -> R.drawable.ic_btn_play
//            }
//        )
    }

    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
//        val currPosition = playbackState.currentPlayBackPosition
//        if (currentTimeMedia.value != currPosition)
//            currentTimeMedia.postValue(currPosition)
//        if (updatePosition)
//            checkPlaybackPosition()
    }, POSITION_UPDATE_INTERVAL_MILLIS)

    override fun onCleared() {
        super.onCleared()
        updatePosition = false
    }

    fun addFavoriteSong(song: Song) {
        requestLikeSong = repository.addSongIntoFavorite(song)
    }

    fun removeFavoriteSong(id: String) {
        requestUnLikeSong = repository.removeFavoriteSong(id)
    }

}

@Suppress("UNCHECKED_CAST")
class NowPlayingViewModelFactory(
    private val repository: SongRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        NowPlayingViewModel(repository) as T

}


@Suppress("PropertyName")
val EMPTY_PLAYBACK_STATE: PlaybackStateCompat = PlaybackStateCompat.Builder()
    .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
    .build()

@Suppress("PropertyName")
val NOTHING_PLAYING: MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
    .build()

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
