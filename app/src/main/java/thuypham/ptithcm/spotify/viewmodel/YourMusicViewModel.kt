package thuypham.ptithcm.spotify.viewmodel

import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.*
import thuypham.ptithcm.spotify.repository.YourMusicRepository

class YourMusicViewModel(
    private val repository: YourMusicRepository
) : ViewModel() {
    private var requestArtistList = MutableLiveData<ResultData<ArrayList<Artist>>>()
    private var requestPlaylist = MutableLiveData<ResultData<ArrayList<Playlist>>>()
    private var requestAlbum = MutableLiveData<ResultData<ArrayList<Album>>>()
    private var requestSong = MutableLiveData<ResultData<ArrayList<Song>>>()

    val listPlaylist: LiveData<ArrayList<Playlist>> =
        Transformations.switchMap(requestPlaylist) {
            it.data
        }

    val networkStatePlaylist: LiveData<NetworkState> = Transformations.switchMap(requestPlaylist) {
        it.networkState
    }
    val listSong: LiveData<ArrayList<Song>> =
        Transformations.switchMap(requestSong) {
            it.data
        }

    val networkStateSongs: LiveData<NetworkState> = Transformations.switchMap(requestSong) {
        it.networkState
    }

    val listAlbum: LiveData<java.util.ArrayList<Album>> =
        Transformations.switchMap(requestAlbum) {
            it.data
        }

    val networkStateAlbum: LiveData<NetworkState> = Transformations.switchMap(requestAlbum) {
        it.networkState
    }

    val listArtist: LiveData<java.util.ArrayList<Artist>> =
        Transformations.switchMap(requestArtistList) {
            it.data
        }

    val networkStateArtist: LiveData<NetworkState> = Transformations.switchMap(requestArtistList) {
        it.networkState
    }

    fun getListArtist() {
        requestArtistList.value = repository.getListArtist()
    }

    fun getPlaylist() {
        requestPlaylist.value = repository.getPlaylist()
    }

    fun getListSong() {
        requestSong.value = repository.getListSong()
    }

    fun getListAlbum() {
        requestAlbum.value = repository.getListAlbum()
    }
}

@Suppress("UNCHECKED_CAST")
class YourMusicViewModelFactory(
    private val repository: YourMusicRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = YourMusicViewModel(repository) as T

}