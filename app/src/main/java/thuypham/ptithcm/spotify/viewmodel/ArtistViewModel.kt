package thuypham.ptithcm.spotify.viewmodel

import android.view.View
import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.*
import thuypham.ptithcm.spotify.repository.ArtistRepository

class ArtistViewModel(
    private val repository: ArtistRepository
) : ViewModel() {
    private var requestSongOfArtist = MutableLiveData<ResultData<ArrayList<Song>>>()
    private var requestArtist = MutableLiveData<ResultData<Artist>>()
    private var requestAlbumOfArtist = MutableLiveData<ResultData<Album>>()
    private var requestArtistFollowing = MutableLiveData<ResultData<ArrayList<Artist>>>()
    private var requestAllArtist = MutableLiveData<ResultData<ArrayList<Artist>>>()
    var checkFollowArtist = MutableLiveData<Boolean>()
    var artistID = MutableLiveData<String>()
    var followCounter: Int? = -1

    var listAllArtist: LiveData<ArrayList<Artist>> =
        Transformations.switchMap(requestAllArtist) {
            it.data
        }

    var statusListAllArtist: LiveData<NetworkState> =
        Transformations.switchMap(requestAllArtist) {
            it.networkState
        }

    fun getAllArtist(){
        requestAllArtist.value  = repository.getAllArtist()
    }

    val listSong: LiveData<ArrayList<Song>> =
        Transformations.switchMap(requestSongOfArtist) {
            it.data
        }

    var requestFollowArtist = MutableLiveData<ResultData<Boolean>>()
    var requestUnFollowArtist = MutableLiveData<ResultData<Boolean>>()

    val networkStateListSong: LiveData<NetworkState> =
        Transformations.switchMap(requestSongOfArtist) {
            it.networkState
        }

    fun convertCounter(number: Int): String {
        return Artist.convertCounter(number)
    }

    var artistInfo: LiveData<Artist> =
        Transformations.switchMap(requestArtist) {
            it.data
        }

    var statusUnFollowArtist: LiveData<NetworkState> =
        Transformations.switchMap(requestUnFollowArtist) {
            it.networkState
        }

    var statusFollowArtist: LiveData<NetworkState> =
        Transformations.switchMap(requestFollowArtist) {
            it.networkState
        }

    val networkStateArtist: LiveData<NetworkState> =
        Transformations.switchMap(requestArtist) {
            it.networkState
        }

    fun getListSongOfArtist(albumID: String) {
        requestSongOfArtist.value = repository.getListSongOfArtist(albumID)
    }

    var listArtist: LiveData<ArrayList<Artist>> =
        Transformations.switchMap(requestArtistFollowing) {
            it.data
        }

    var statusListArtist: LiveData<NetworkState> =
        Transformations.switchMap(requestArtistFollowing) {
            it.networkState
        }

    var albumArtist: LiveData<Album> =
        Transformations.switchMap(requestAlbumOfArtist) {
            it.data
        }

    var statusAlbumArtist: LiveData<NetworkState> =
        Transformations.switchMap(requestAlbumOfArtist)
        {
            it.networkState
        }

    fun getLatestAlbumOfArtist(artistID: String) {
        requestAlbumOfArtist.value = repository.getLatestAlbum(artistID)
    }

    fun getListArtistFollowing() {
        requestArtistFollowing.value = repository.getListArtistFollowing()
    }

    fun getArtistInfo(_artistID: String) {
        requestArtist.value = repository.getArtistInfoByID(_artistID)
        checkFollowArtist = repository.checkFollowArtist(_artistID)
        this.artistID.value = _artistID
    }

    fun followArtistOnClick(view: View) {
        view.isSelected = !view.isSelected
        if (view.isSelected) {
            if (artistInfo.value != null) {
                requestFollowArtist.value = repository.followArtist(artistInfo.value!!)

                artistInfo.value?.followCounter?.plus(1)?.let {
                    artistInfo.value?.id?.let { it1 ->
                        repository.updateFollowCounterOfArtist(it1, it)
                    }
                }
            }
        } else {
            if (artistID.value != null) {
                requestUnFollowArtist.value = repository.unFollowArtist(artistID.value!!)
                artistInfo.value?.followCounter?.plus(1)?.let {
                    repository.updateFollowCounterOfArtist(artistID.value!!, it)
                }
            }
        }
    }

    fun onShareArtistClick(view: View) {

    }

    fun onPlayAllArtistClick(view: View) {
        view.isSelected = !view.isSelected
    }
}


@Suppress("UNCHECKED_CAST")
class ArtistViewModelFactory(
    private val repository: ArtistRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = ArtistViewModel(repository) as T

}