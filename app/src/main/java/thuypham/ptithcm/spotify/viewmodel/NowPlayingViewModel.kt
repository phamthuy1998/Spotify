package thuypham.ptithcm.spotify.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.database.SongDbRepository
import thuypham.ptithcm.spotify.repository.SongRepository

class NowPlayingViewModel(
    private val repository: SongRepository,
    private val repositoryDb: SongDbRepository
) : ViewModel() {
    var requestLikeSong = MutableLiveData<NetworkState>()
    var requestUnLikeSong = MutableLiveData<NetworkState>()
    var listSongDb = MutableLiveData<List<Song>>().apply { value = arrayListOf() }
    var checkSongIsLike = MutableLiveData<Boolean>()
    var songPlaying = MutableLiveData<Song>().apply { value = null }
    var isShowFragmentNowPlaying = MutableLiveData<Boolean>().apply { value = false }
    var isPlaying = MutableLiveData<Boolean>().apply { value = false }
    var isShuffle = MutableLiveData<Boolean>().apply { value = false }

    //    fun getAllSongInDb() = repositoryDb.getAllSong()
    fun getAllSongInDb() = repositoryDb.getAllSong()

    fun insertSongs(listSong: ArrayList<Song>) {
        repositoryDb.insert(listSong)
    }

    fun deleteAllSong() {
        repositoryDb.deleteAll()
    }

    fun getStatusLikeOfSong(songID: String) {
        checkSongIsLike = repository.checkSongFavorite(songID)
    }

    fun addSongIntoHistory(song: Song) {
        repository.addSongIntoHistory(song)
    }

    fun addFavoriteSong(song: Song) {
        requestLikeSong = repository.addSongIntoFavorite(song)
    }

    fun removeFavoriteSong(id: String) {
        requestUnLikeSong = repository.removeFavoriteSong(id)
    }

    val currentTimeMedia = MutableLiveData<Long>().apply {
        postValue(0L)
    }

    val buttonPlayRes = MutableLiveData<Int>().apply {
        postValue(R.drawable.ic_btn_play)
    }

//    fun setSateButtonPlaySong(v: View, isPlaying: Boolean): Int{
//        if(isPlaying) return R.drawable.ic_bot_pause
//        else return R.drawable.ic_bot_play
//    }

}

@Suppress("UNCHECKED_CAST")
class NowPlayingViewModelFactory(
    private val repository: SongRepository,
    private val repositoryDb: SongDbRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        NowPlayingViewModel(repository, repositoryDb) as T

}

