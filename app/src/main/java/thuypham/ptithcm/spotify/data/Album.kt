package thuypham.ptithcm.spotify.data

import thuypham.ptithcm.spotify.base.DynamicSearchAdapter

data class Album(
    var id: String? = null,
    var albumName: String? = null,
    var artistID: String? = null,
    var artistName: String? = null,
    var imageURL: String? = null,
    var description: String? = null,
    var songCounter: Int? = null,
    var likeCounter: Int? = null,
    var viewCounter: Int? = null
) : DynamicSearchAdapter.Searchable {
    override fun getSearchCriteria(): String {
        return albumName.toString()
    }

    fun artistAppendSongCounter() =
        if (songCounter != 1) "$artistName - $songCounter songs" else "$artistName - $songCounter song"
}
