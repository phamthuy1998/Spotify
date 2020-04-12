package thuypham.ptithcm.spotify.viewmodel

import android.view.View
import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.data.Artist
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.repository.ArtistRepository

class ArtistViewModel(
    private val repository: ArtistRepository
) : ViewModel() {
    private var requestSongOfArtist = MutableLiveData<ResultData<ArrayList<Song>>>()
    private var requestArtist = MutableLiveData<ResultData<Artist>>()
    private var requestArtistFollowing = MutableLiveData<ResultData<ArrayList<Artist>>>()
    var checkFollowArtist = MutableLiveData<Boolean>()
    var artistID = MutableLiveData<String>()
    var followCounter: Int? = -1

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

    fun getListArtistFollowing() {
        requestArtistFollowing.value = repository.getListArtistFollowing()
    }

    fun getArtistInfo(_albumID: String) {
        requestArtist.value = repository.getArtistInfoByID(_albumID)
        checkFollowArtist = repository.checkFollowArtist(_albumID)
        this.artistID.value = _albumID
    }

    fun followArtistOnClick(view: View) {
        view.isSelected = !view.isSelected
        if (view.isSelected) {
            if (artistInfo.value != null) {
                requestFollowArtist.value = repository.followArtist(artistInfo.value!!)
            }
        } else {
            if (artistID.value != null) {
                requestUnFollowArtist.value = repository.unFollowArtist(artistID.value!!)
            }
        }
    }

    fun onLikeArtistClick(v: View) {}

    fun updateCounter(artistID: String, followCounter: Int) {
        repository.updateFollowCounterOfArtist(artistID, followCounter)
    }

    fun onPlayAllSongOfArtist(view: View) {
        view.isSelected = !view.isSelected
    }

    fun onShareArtistClick(view: View) {
    }

    fun onPlayAllArtistClick(v: View) {}
}


@Suppress("UNCHECKED_CAST")
class ArtistViewModelFactory(
    private val repository: ArtistRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = ArtistViewModel(repository) as T

}