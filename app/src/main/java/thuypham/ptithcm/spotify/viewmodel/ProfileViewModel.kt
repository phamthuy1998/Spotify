package thuypham.ptithcm.spotify.viewmodel

import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import  thuypham.ptithcm.spotify.data.User
import  thuypham.ptithcm.spotify.repository.ProfileRepository

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {
    private var requestUser = MutableLiveData<ResultData<User>>()
    private var requestSignOut = MutableLiveData<ResultData<Boolean>>()

    init {
        getUserInfo()
    }

    val userInfo: LiveData<User> =
        Transformations.switchMap(requestUser) {
            it.data
        }

    val networkStateUserInfo: LiveData<NetworkState> =
        Transformations.switchMap(requestUser) {
            it.networkState
        }

    val networkSignOut: LiveData<NetworkState> =
        Transformations.switchMap(requestSignOut) {
            it.networkState
        }

    fun getUserInfo() {
        requestUser.value = repository.getUserInfo()
    }

    fun signOut() {
        requestSignOut.value = repository.signOut()
    }
}


@Suppress("UNCHECKED_CAST")
class ProfileViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = ProfileViewModel(repository) as T

}