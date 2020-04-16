package thuypham.ptithcm.spotify.viewmodel

import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.repository.SongsRepositoryImpl

class SongViewModel(
    private val repository: SongsRepositoryImpl
) : ViewModel() {
    private var requestSongOfCountry = MutableLiveData<ResultData<ArrayList<Song>>>()

    val listSong: LiveData<ArrayList<Song>> =
        Transformations.switchMap(requestSongOfCountry) {
            it.data
        }

    val networkStateListSong: LiveData<NetworkState> =
        Transformations.switchMap(requestSongOfCountry) {
            it.networkState
        }

    fun getListSong() {
        requestSongOfCountry.value = repository.getAllSong()
    }
}

@Suppress("UNCHECKED_CAST")
class SongViewModelFactory(
    private val repository: SongsRepositoryImpl
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = SongViewModel(repository) as T

}