package thuypham.ptithcm.spotify.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.database.FirebaseDatabase
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.data.User
import thuypham.ptithcm.spotify.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface AccRepository {
    fun login(email: String, password: String): ResultData<Boolean>
    fun sendMailResetPassword(email: String): ResultData<Boolean>
    fun register(
        email: String,
        username: String,
        dayOfBirth: String,
        gender: Boolean,
        password: String
    ): ResultData<Boolean>
}

class AccRepositoryImpl : AccRepository {

    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth? by lazy {
        com.google.firebase.auth.FirebaseAuth.getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun currentUser() = firebaseAuth?.currentUser

    private fun databaseRef() = firebaseDatabase?.reference

    override fun login(email: String, password: String): ResultData<Boolean> {
        val networkState = MutableLiveData<NetworkState>()
        val responseLogin = MutableLiveData<Boolean>()
        networkState.postValue(NetworkState.LOADING)
        firebaseAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    // Check email Verified
                    if (currentUser()?.isEmailVerified == true) {
                        // Set email active
                        databaseRef()?.child(USER)?.child(currentUser()?.uid.toString())
                            ?.child("active")?.setValue(true)
                        responseLogin.postValue(true)
                        networkState.postValue(NetworkState.LOADED)
                    } else
                        networkState.postValue(NetworkState.error(Throwable(ERR_EMAIL_VERIFY).message))
                } else
                    try {
                        throw it.exception!!
                    } catch (emailNotExist: FirebaseAuthInvalidUserException) {
                        networkState.postValue(NetworkState.error(Throwable(ERR_EMAIL_NOT_Exist).message))
                    } catch (password: FirebaseAuthInvalidCredentialsException) {
                        networkState.postValue(NetworkState.error(Throwable(ERR_INCORRECT_PW).message))
                    } catch (error: Exception) {
                        networkState.postValue(NetworkState.error(Throwable(it.exception).message))
                    }
            }
        return ResultData(
            data = responseLogin,
            networkState = networkState
        )
    }

    override fun sendMailResetPassword(email: String): ResultData<Boolean> {
        val networkState = MutableLiveData<NetworkState>()
        val responseSendMail = MutableLiveData<Boolean>()
        networkState.postValue(NetworkState.LOADING)
        firebaseAuth?.sendPasswordResetEmail(email)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    responseSendMail.value = true
                    networkState.postValue(NetworkState.LOADED)
                } else
                    try {
                        throw it.exception!!
                    } catch (emailNotExist: FirebaseAuthInvalidUserException) {
                        networkState.postValue(NetworkState.error(Throwable(ERR_EMAIL_NOT_Exist).message))
                    } catch (error: Exception) {
                        networkState.postValue(NetworkState.error(Throwable(it.exception).message))
                    }
            }
        return ResultData(
            data = responseSendMail,
            networkState = networkState
        )
    }

    override fun register(
        email: String,
        username: String,
        dayOfBirth: String,
        gender: Boolean,
        password: String
    ): ResultData<Boolean> {
        val networkState = MutableLiveData<NetworkState>()
        val responseRegister = MutableLiveData<Boolean>()
        networkState.postValue(NetworkState.LOADING)
        firebaseAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                verifyEmail()
                addUserDatabase(email, username, dayOfBirth, gender, password)
                responseRegister.value = true
                networkState.postValue(NetworkState.LOADED)
            } else
                try {
                    throw it.exception!!
                } catch (weakPassword: FirebaseAuthWeakPasswordException) {
                    networkState.postValue(NetworkState.error(Throwable(ERR_WEAK_PASSWORD).message))
                } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                    networkState.postValue(NetworkState.error(Throwable(ERR_EMAIL_INVALID).message))
                } catch (existEmail: FirebaseAuthUserCollisionException) {
                    networkState.postValue(NetworkState.error(Throwable(ERR_EMAIL_EXIST).message))
                } catch (error: Exception) {
                    networkState.postValue(NetworkState.error(Throwable(it.exception).message))
                }
        }
        return ResultData(
            data = responseRegister,
            networkState = networkState
        )
    }

    private fun verifyEmail() {
        currentUser()?.sendEmailVerification()
    }

    private fun addUserDatabase(
        _email: String,
        _username: String,
        _dayOfBirth: String,
        _gender: Boolean,
        _password: String
    ) {
        val uerUID = currentUser()?.uid
        val user = User().apply {
            id = uerUID
            email = _email
            username = _username
            dayOfBirth = _dayOfBirth
            gender = _gender
            password = _password
            active = false
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val dayCreate = current.format(formatter)
            dayCreateAcc = dayCreate
        }
        databaseRef()?.child(USER)?.child(uerUID.toString())?.setValue(user)
    }
}