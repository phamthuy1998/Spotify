package thuypham.ptithcm.spotify.data
import android.content.Context
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize
import thuypham.ptithcm.spotify.R
import thuypham.ptithcm.spotify.base.DynamicSearchAdapter
import kotlin.math.floor

@IgnoreExtraProperties
data class Song(
    var id: String? = null,
    var songName: String? = null,
    var fileName: String? = null,
    var imageURL: String? = null,
    var albumID: String? = null,
    var albumName: String? = null,
    var artistID: String? = null,
    var artistName: String? = null,
    var lyric: String? = null,
    var dayRelease: String? = null,
    var typeID: String? = null,
    var typeName: String? = null,
    var description: String? = null,
    var likeCounter: Int? = null,
    var viewsCounter: Int? = null,
    var countryName: String? = null,
    var countryID: String? = null,
    var playListId: String? = null,
    var time: Int? = null,
    var isLike: Boolean?=null
) : DynamicSearchAdapter.Searchable {

    override fun getSearchCriteria(): String {
        return songName.toString()
    }

    companion object {
        fun timestampToMSS(context: Context, position: Long): String {
            val totalSeconds = floor(position / 1E3).toInt()
            val minutes = totalSeconds / 60
            val remainingSeconds = totalSeconds - (minutes * 60)
            return if (position < 0) context.getString(R.string.duration_unknown)
            else context.getString(R.string.duration_format).format(minutes, remainingSeconds)
        }

        fun timestampIntToMSS(context: Context, position: Int): String {
            val minutes = position / 60
            val remainingSeconds = position - (minutes * 60)
            return if (position < 0) context.getString(R.string.duration_unknown)
            else context.getString(R.string.duration_format).format(minutes, remainingSeconds)
        }
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "songName" to songName,
            "fileName" to fileName,
            "imageURL" to imageURL,
            "description" to description,
            "albumID" to albumID,
            "artistID" to artistID,
            "artistName" to artistName,
            "albumName" to albumName,
            "lyric" to lyric,
            "dayRelease" to dayRelease,
            "typeID" to typeID,
            "typeName" to typeName,
            "description" to description,
            "likeCounter" to likeCounter,
            "viewsCounter" to viewsCounter,
            "countryName" to countryName,
            "countryID" to countryID,
            "playListId" to playListId,
            "time" to time,
            "isLike" to isLike
        )
    }
}
@Parcelize
data class Country(
    var id: String? = null,
    var countryName: String? = null,
    var countryImage: String? = null,
    var backgroundURL: String? = null
): Parcelable
