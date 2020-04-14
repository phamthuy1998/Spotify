package thuypham.ptithcm.spotify.viewmodel

import android.view.View
import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.Album
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.repository.AlbumRepository

class AlbumViewModel(
    private val repository: AlbumRepository
) : ViewModel() {
    private var requestSongOfAlbum = MutableLiveData<ResultData<ArrayList<Song>>>()
    private var requestAlbum = MutableLiveData<ResultData<Album>>()
    private var requestListAlbum = MutableLiveData<ResultData<ArrayList<Album>>>()
    var statusLikeAlbum = MutableLiveData<NetworkState>()
    var statusUnLikeAlbum = MutableLiveData<NetworkState>()
    var checkAlbumIsLike = MutableLiveData<Boolean>()
    var albumID = MutableLiveData<String>()

    val listSong: LiveData<ArrayList<Song>> =
        Transformations.switchMap(requestSongOfAlbum) {
            it.data
        }

    val networkStateListSong: LiveData<NetworkState> =
        Transformations.switchMap(requestSongOfAlbum) {
            it.networkState
        }

    val listAlbum: LiveData<ArrayList<Album>> =
        Transformations.switchMap(requestListAlbum) {
            it.data
        }

    val networkStateListAlbum: LiveData<NetworkState> =
        Transformations.switchMap(requestListAlbum) {
            it.networkState
        }

    var album: LiveData<Album> =
        Transformations.switchMap(requestAlbum) {
            it.data
        }

    val networkStateAlbum: LiveData<NetworkState> =
        Transformations.switchMap(requestAlbum) {
            it.networkState
        }

    fun getListAlbum(albumID: String) {
        requestSongOfAlbum.value = repository.getListSongOfAlbum(albumID)
    }

    fun getAllListAlbum() {
        requestListAlbum.value = repository.getListAlbum()
    }

    fun getAlbumInfo(_albumID: String) {
        requestAlbum.value = repository.getAlbumInfoByID(_albumID)
        this.albumID.value = _albumID
        checkAlbumIsLike = repository.checkLikeAlbum(_albumID)
    }

    fun onLikeAlbumClick(view: View) {
        view.isSelected = !view.isSelected
        if (album.value != null)
            if (view.isSelected) statusLikeAlbum = repository.addAlbumFavorite(album.value!!)
            else statusUnLikeAlbum = repository.removeAlbumFavorite(album.value!!)
    }

    fun onPlayAllAlbumClick(view: View) {
        view.isSelected = !view.isSelected
    }

    fun onShareAlbumClick(view: View) {

    }
}

@Suppress("UNCHECKED_CAST")
class AlbumViewModelFactory(
    private val repository: AlbumRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = AlbumViewModel(repository) as T

}