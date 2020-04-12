package thuypham.ptithcm.spotify.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import thuypham.ptithcm.spotify.base.DynamicSearchAdapter
import kotlin.math.abs

@IgnoreExtraProperties
data class Artist(
    var id: String? = null,
    var imagePhoto: String? = null,
    var imageBgPhoto: String? = null,
    var artistName: String? = null,
    var introduce: String? = null,
    var dayOfBirth: String? = null,
    var gender: Boolean? = null,
    var typeMusic: ArrayList<MusicGenre>? = null,
    var followCounter: Int? = null,
    var followingCounter: Int? = null
): DynamicSearchAdapter.Searchable {
    override fun getSearchCriteria(): String {
        return artistName.toString()
    }

    companion object {
        fun convertCounter( counter: Int): String {
            var numberString = ""
            numberString = when {
                abs(counter / 1000000) >= 1 -> {
                    (counter / 1000000).toString().toString() + "m"
                }
                abs(counter / 1000) >= 1 -> {
                    (counter / 1000).toString().toString() + "k"
                }
                else -> {
                    counter.toString()
                }
            }
            return numberString
        }
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "imagePhoto" to imagePhoto,
            "imageBgPhoto" to imageBgPhoto,
            "artistName" to artistName,
            "introduce" to introduce,
            "dayOfBirth" to dayOfBirth,
            "gender" to gender,
            "typeMusic" to typeMusic,
            "followCounter" to followCounter,
            "followingCounter" to followingCounter
        )
    }
}