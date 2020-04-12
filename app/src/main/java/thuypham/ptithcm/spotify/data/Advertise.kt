package thuypham.ptithcm.spotify.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Advertise(
    var id: String? = null,
    var image_url: String? = null,
    var song_id: String? = null,
    var content: String? = null,
    var song_name: String? = null,
    var image_artist: String? = null
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "image_url" to image_url,
            "song_id" to song_id,
            "content" to content,
            "song_name" to song_name,
            "image_artist" to image_artist
        )
    }
}