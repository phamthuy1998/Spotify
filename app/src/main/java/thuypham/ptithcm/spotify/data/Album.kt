package thuypham.ptithcm.spotify.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Album(
    var id: String? = null,
    var albumName: String? = null,
    var artistID: String? = null,
    var artistName: String? = null,
    var imageURL: String? = null,
    var description: String? = null,
    var songCounter: Int? = null
) {
    fun artistAppendSongCounter() =
        if (songCounter != 1) "$artistName - $songCounter songs" else "$artistName - $songCounter song"

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "albumName" to albumName,
            "artistID" to artistID,
            "artistName" to artistName,
            "imageURL" to imageURL,
            "songCounter" to songCounter,
            "description" to description
        )
    }
}
