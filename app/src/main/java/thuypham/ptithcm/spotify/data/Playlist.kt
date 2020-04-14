package thuypham.ptithcm.spotify.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import thuypham.ptithcm.spotify.base.DynamicSearchAdapter

@Parcelize
data class Playlist(
    var id: String? = null,
    var playlistName: String? = null,
    var playlistImage: String? = null,
    var playlistBackground: String? = null,
    var dayCreate: String? = null,
    var description: String? = null,
    var artistName: String? = null,
    var likeCounter: Int? = null,
    var songCounter: Int? = null,
    var artistID: String? = null
) : Parcelable, DynamicSearchAdapter.Searchable {
    override fun getSearchCriteria(): String {
        return playlistName.toString()
    }
}