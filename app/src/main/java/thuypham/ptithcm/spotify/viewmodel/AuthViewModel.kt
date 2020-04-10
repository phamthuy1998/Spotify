package thuypham.ptithcm.spotify.viewmodel

import android.view.View
import androidx.lifecycle.*
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.repository.AccRepository
import thuypham.ptithcm.spotify.util.hideKeyboard
import thuypham.ptithcm.spotify.util.isValidPassword

class AuthViewModel(
    private val repository: AccRepository
) : ViewModel() {

    var email = MutableLiveData<String>().apply { value = "" }
    var password = MutableLiveData<String>().apply { value = "" }
    private var requestLogin = MutableLiveData<ResultData<Boolean>>()
    private var requestSendMail = MutableLiveData<ResultData<Boolean>>()
    private var requestSignUp = MutableLiveData<ResultData<Boolean>>()


    var logInStatus: LiveData<Boolean>? =
        Transformations.switchMap(requestLogin) {
            it.data
        }

    val networkLogin: LiveData<NetworkState> =
        Transformations.switchMap(requestLogin) {
            it.networkState
        }

    var senMailStatus: LiveData<Boolean> =
        Transformations.switchMap(requestSendMail) {
            it.data
        }

    val networkSendMail: LiveData<NetworkState> =
        Transformations.switchMap(requestSendMail) {
            it.networkState
        }

    var registerStatus: LiveData<Boolean> =
        Transformations.switchMap(requestSignUp) {
            it.data
        }

    val networkRegister: LiveData<NetworkState> =
        Transformations.switchMap(requestSignUp) {
            it.networkState
        }

    fun login(view: View) {
        view.hideKeyboard()
        requestLogin.value = repository.login(email.value ?: "", password.value ?: "")
    }

    fun forgotPw(view: View) {
        view.hideKeyboard()
        requestSendMail.value = repository.sendMailResetPassword(email.value ?: "")
    }

    fun signUp(
        email: String,
        username: String,
        dayOfBirth: String,
        gender: Boolean,
        password: String
    ) {
        requestSignUp.value = repository.register(email, username, dayOfBirth, gender, password)
    }

    fun isValidate(email: String): Boolean = email.isNotEmpty()

    fun isValidateEmailPassword(email: String, password: String) =
        isValidate(email) && isValidPassword(password)

}

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val repository: AccRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>) = AuthViewModel(repository) as T

}