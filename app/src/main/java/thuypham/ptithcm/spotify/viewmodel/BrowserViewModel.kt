package thuypham.ptithcm.spotify.viewmodel

import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.*
import thuypham.ptithcm.spotify.repository.BrowserRepository

class BrowserViewModel(
    private val repository: BrowserRepository
) : ViewModel() {
    private var requestMusicGenre = MutableLiveData<ResultData<ArrayList<MusicGenre>>>()
    private var requestTopMusic = MutableLiveData<ResultData<ArrayList<Country>>>()
    private var requestAlbum = MutableLiveData<ResultData<Album>>()

    init {
        getMusicGenre()
        getAlbumFeature()
        getTopHit()
    }

    fun refresh() {
        getMusicGenre()
        getAlbumFeature()
        getTopHit()
    }

    private fun getMusicGenre() {
        requestMusicGenre.value = repository.getMusicGenre()
    }

    val listMusicGenre: LiveData<ArrayList<MusicGenre>> =
        Transformations.switchMap(requestMusicGenre) {
            it.data
        }

    val networkStateMusicGenre: LiveData<NetworkState> =
        Transformations.switchMap(requestMusicGenre) {
            it.networkState
        }


    private fun getTopHit() {
        requestTopMusic.value = repository.getTopHit()
    }

    val listTopHit: LiveData<ArrayList<Country>> =
        Transformations.switchMap(requestTopMusic) {
            it.data
        }

    val networkStateTopMusic: LiveData<NetworkState> =
        Transformations.switchMap(requestTopMusic) {
            it.networkState
        }

    private fun getAlbumFeature() {
        requestAlbum.value = repository.getAlbum()
    }

    val album: LiveData<Album> =
        Transformations.switchMap(requestAlbum) {
            it.data
        }

    val networkStateAlbum: LiveData<NetworkState> =
        Transformations.switchMap(requestAlbum) {
            it.networkState
        }

}


@Suppress("UNCHECKED_CAST")
class BrowserViewModelFactory(
    private val repository: BrowserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = BrowserViewModel(repository) as T

}