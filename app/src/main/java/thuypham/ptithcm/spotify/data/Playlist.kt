package thuypham.ptithcm.spotify.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Playlist(
    var id: String? = null,
    var playlistName: String? = null,
    var playlistImage: String? = null,
    var dayCreate: String? = null,
    var description: String? = null,
    var songCounter: Int? = null
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "playlistName" to playlistName,
            "playlistImage" to playlistImage,
            "dayCreate" to dayCreate,
            "description" to description,
            "songCounter" to songCounter
        )
    }
}