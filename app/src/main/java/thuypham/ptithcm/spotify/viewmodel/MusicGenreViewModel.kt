package thuypham.ptithcm.spotify.viewmodel

import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.repository.MusicGenreRepository

class MusicGenreViewModel(
    private val repository: MusicGenreRepository
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

    fun getListSong(countryId: String) {
        requestSongOfCountry.value = repository.getListSongOfMusicGenre(countryId)
    }
}

@Suppress("UNCHECKED_CAST")
class MusicGenreViewModelFactory(
    private val repository: MusicGenreRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = MusicGenreViewModel(repository) as T

}