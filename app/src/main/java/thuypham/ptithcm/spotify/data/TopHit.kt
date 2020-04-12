package thuypham.ptithcm.spotify.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class TopHit(
    var id: String? = null,
    var name: String? = null,
    var title: String? = null,
    var genreID: String? = null,
    var imageURL: String? = null
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "genreID" to genreID,
            "title" to title,
            "imageURL" to imageURL
        )
    }
}