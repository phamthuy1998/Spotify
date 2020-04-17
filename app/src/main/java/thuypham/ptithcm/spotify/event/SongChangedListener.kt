package thuypham.ptithcm.spotify.event

import thuypham.ptithcm.spotify.data.Song

interface SongChangedListener {
    fun onSongChanged(song: Song)
    fun onStatusPlayingChanged(status: Int)
}