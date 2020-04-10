package thuypham.ptithcm.spotify.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var id: String? = null,
    var email: String? = null,
    var username: String? = null,
    var dayOfBirth: String? = "",
    var gender: Boolean? = null,
    var password: String? = null,
    var profilePhoto: String? = null,
    var active: Boolean? = null,
    var dayCreateAcc: String? = null
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "email" to email,
            "username" to username,
            "dayOfBirth" to dayOfBirth,
            "gender" to gender,
            "password" to password,
            "profilePhoto" to profilePhoto,
            "active" to active,
            "dayCreateAcc" to dayCreateAcc
        )
    }
}