package thuypham.ptithcm.spotify.viewmodel

import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.repository.CountryRepository

class CountryViewModel(
    private val repository: CountryRepository
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
        requestSongOfCountry.value = repository.getListSongOfCountry(countryId)
    }
}

@Suppress("UNCHECKED_CAST")
class CountryViewModelFactory(
    private val repository: CountryRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = CountryViewModel(repository) as T

}