package thuypham.ptithcm.spotify.event

import thuypham.ptithcm.spotify.data.Song

interface PopupMenuListener {

    fun play(song: Song)

    fun goToAlbum(song: Song)

    fun goToArtist(song: Song)

    fun playNext(song: Song)
}
