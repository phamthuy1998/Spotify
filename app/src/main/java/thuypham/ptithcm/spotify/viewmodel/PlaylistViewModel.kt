package thuypham.ptithcm.spotify.viewmodel

import android.view.View
import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.Playlist
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.repository.PlaylistRepository

class PlaylistViewModel(
    private val repository: PlaylistRepository
) : ViewModel() {
    private var requestSongOfCountry = MutableLiveData<ResultData<ArrayList<Song>>>()
    var statusLikePlaylist = MutableLiveData<NetworkState>()
    var statusUnLikePlaylist = MutableLiveData<NetworkState>()
    var checkPlaylistIsLike = MutableLiveData<Boolean>()
    val listSong: LiveData<ArrayList<Song>> =
        Transformations.switchMap(requestSongOfCountry) {
            it.data
        }

    val networkStateListSong: LiveData<NetworkState> =
        Transformations.switchMap(requestSongOfCountry) {
            it.networkState
        }

    fun getListSong(playlistID: String) {
        requestSongOfCountry.value = repository.getListSongOfPlaylist(playlistID)
        checkPlaylistIsLike= repository.checkLikePlaylist(playlistID)
    }

    fun onLikeAlbumClick(view: View, playlist: Playlist) {
        view.isSelected = !view.isSelected
        if (view.isSelected) statusLikePlaylist = repository.addPlaylistFavorite(playlist)
        else statusUnLikePlaylist = repository.removePlaylistFavorite(playlist)
    }


    private var requestAllPlaylist = MutableLiveData<ResultData<ArrayList<Playlist>>>()

    val networkStatePlaylists: LiveData<NetworkState> =
        Transformations.switchMap(requestAllPlaylist) {
            it.networkState
        }

    val listPlaylist: LiveData<ArrayList<Playlist>> =
        Transformations.switchMap(requestAllPlaylist) {
            it.data
        }

    fun getAllPlaylist(){
        requestAllPlaylist.value = repository.getAllPlaylist()
    }

}

@Suppress("UNCHECKED_CAST")
class PlaylistViewModelFactory(
    private val repository: PlaylistRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = PlaylistViewModel(repository) as T

}