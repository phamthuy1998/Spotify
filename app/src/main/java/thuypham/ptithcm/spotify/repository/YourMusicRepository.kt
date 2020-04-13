package thuypham.ptithcm.spotify.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import thuypham.ptithcm.spotify.data.*
import thuypham.ptithcm.spotify.util.FAVORITE_ALBUM
import thuypham.ptithcm.spotify.util.FAVORITE_PLAYLIST
import thuypham.ptithcm.spotify.util.FAVORITE_SONG
import thuypham.ptithcm.spotify.util.FOLLOW_ARTISTS

interface YourMusicRepository{
    fun getListSong(): ResultData<ArrayList<Song>>
    fun getListArtist(): ResultData<ArrayList<Artist>>
    fun getPlaylist(): ResultData<ArrayList<Playlist>>
    fun getListAlbum(): ResultData<ArrayList<Album>>
}


class YourMusicRepositoryImpl : YourMusicRepository {

    private val firebaseAuth: FirebaseAuth? by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun currentUser() = firebaseAuth?.currentUser

    private fun databaseRef() = firebaseDatabase?.reference

    override fun getListSong(): ResultData<ArrayList<Song>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListFavoriteSong = MutableLiveData<ArrayList<Song>>()
        networkState.postValue(NetworkState.LOADING)
        val listSong = ArrayList<Song>()
        var song: Song?
        val query = currentUser()?.uid?.let { databaseRef()?.child(FAVORITE_SONG)?.child(it) }

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        song = ds.getValue(Song::class.java)
                        song?.let { listSong.add(it) }
                    }
                    if (listSong.size > 0) {
                        listSong.reverse()
                        responseListFavoriteSong.value = listSong
                        networkState.postValue(NetworkState.LOADED)
                    } else
                        networkState.postValue(NetworkState.error("List favorite song are empty!"))
                } else {
                    networkState.postValue(NetworkState.error("List favorite song are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))
        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListFavoriteSong,
            networkState = networkState
        )
    }

    override fun getListArtist(): ResultData<ArrayList<Artist>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListArtistFollow = MutableLiveData<ArrayList<Artist>>()
        networkState.postValue(NetworkState.LOADING)
        val listSong = ArrayList<Artist>()
        var artist: Artist?
        val query = currentUser()?.uid?.let { databaseRef()?.child(FOLLOW_ARTISTS)?.child(it) }
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        artist = ds.getValue(Artist::class.java)
                        artist?.let { listSong.add(it) }
                    }
                    if (listSong.size > 0) {
                        listSong.reverse()
                        responseListArtistFollow.value = listSong
                        networkState.postValue(NetworkState.LOADED)
                    } else
                        networkState.postValue(NetworkState.error("List artist following are empty!"))
                } else {
                    networkState.postValue(NetworkState.error("List artist following are empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))
        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListArtistFollow,
            networkState = networkState
        )
    }

    override fun getPlaylist(): ResultData<ArrayList<Playlist>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseSongType = MutableLiveData<ArrayList<Playlist>>()
        networkState.postValue(NetworkState.LOADING)
        val listPlaylist = ArrayList<Playlist>()
        var playlist: Playlist?
        val query =  currentUser()?.uid?.let { databaseRef()?.child(FAVORITE_PLAYLIST)?.child(it) }
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        playlist = ds.getValue(Playlist::class.java)
                        playlist?.let { listPlaylist.add(it) }
                    }
                    if (listPlaylist.size > 0) {
                        listPlaylist.reverse()
                        responseSongType.value = listPlaylist ?: arrayListOf()
                        networkState.postValue(NetworkState.LOADED)
                    } else
                        networkState.postValue(NetworkState.error("List empty"))
                } else {
                    networkState.postValue(NetworkState.error("List empty"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)

        return ResultData(
            data = responseSongType,
            networkState = networkState
        )
    }

    override fun getListAlbum(): ResultData<ArrayList<Album>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListFavoriteAlbum = MutableLiveData<ArrayList<Album>>()
        networkState.postValue(NetworkState.LOADING)
        val listFavoriteAlbum = ArrayList<Album>()
        var album: Album?
        val query = databaseRef()?.child(FAVORITE_ALBUM)?.child(currentUser()?.uid.toString())

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        album = ds.getValue(Album::class.java)
                        album?.let { listFavoriteAlbum.add(it) }
                    }
                    listFavoriteAlbum.reverse()
                    responseListFavoriteAlbum.value = listFavoriteAlbum
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List empty!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))
        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListFavoriteAlbum,
            networkState = networkState
        )
    }

}