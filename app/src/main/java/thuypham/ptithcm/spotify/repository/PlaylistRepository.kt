package thuypham.ptithcm.spotify.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.Playlist
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.util.FAVORITE_PLAYLIST
import thuypham.ptithcm.spotify.util.PLAYLIST
import thuypham.ptithcm.spotify.util.SONG

interface PlaylistRepository {
    fun getListSongOfPlaylist(playlistId: String): ResultData<ArrayList<Song>>
    fun addPlaylistFavorite(playlist: Playlist): MutableLiveData<NetworkState>
    fun removePlaylistFavorite(playlist: Playlist): MutableLiveData<NetworkState>
    fun checkLikePlaylist(playlistID:String): MutableLiveData<Boolean>
    fun getAllPlaylist(): ResultData<ArrayList<Playlist>>
}

class PlaylistRepositoryImpl : PlaylistRepository {

    private val firebaseDatabase: FirebaseDatabase? by lazy { FirebaseDatabase.getInstance() }
    private val firebaseAuth: FirebaseAuth? by lazy { FirebaseAuth.getInstance() }
    private fun currentUser() = firebaseAuth?.currentUser
    private fun databaseRef() = firebaseDatabase?.reference

    override fun getListSongOfPlaylist(playlistId: String): ResultData<ArrayList<Song>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListSong = MutableLiveData<ArrayList<Song>>()
        networkState.postValue(NetworkState.LOADING)
        val listSong = ArrayList<Song>()
        var song: Song?
        val query = databaseRef()?.child(SONG)
            ?.orderByChild("playListId")
            ?.equalTo(playlistId)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        song = ds.getValue(Song::class.java)
                        song?.let { listSong.add(it) }
                    }
                    var sortedList = listSong.sortedWith(compareBy { it.viewsCounter })
                    sortedList = sortedList.reversed()
                    listSong.clear()
                    listSong.addAll(sortedList)
                    responseListSong.value = listSong
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List song of playlist are empty"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))
        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseListSong,
            networkState = networkState
        )
    }

    override fun addPlaylistFavorite(playlist: Playlist): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        val query = databaseRef()?.child(FAVORITE_PLAYLIST)?.child(currentUser()?.uid.toString())
            ?.child(playlist.id.toString())
        query?.setValue(playlist)?.addOnSuccessListener {
            databaseRef()?.child(PLAYLIST)?.child(playlist.id.toString())?.child("likeCounter")
                ?.setValue(playlist.likeCounter?.plus(1))
            networkState.postValue(NetworkState.LOADED)
        }?.addOnFailureListener { err ->
            networkState.postValue(NetworkState.error(err.message.toString()))
        }
        return networkState
    }

    override fun removePlaylistFavorite(playlist: Playlist): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        val query = databaseRef()?.child(FAVORITE_PLAYLIST)?.child(currentUser()?.uid.toString())
            ?.child(playlist.id.toString())
        query?.removeValue()?.addOnSuccessListener {
            databaseRef()?.child(PLAYLIST)?.child(playlist.id.toString())?.child("likeCounter")
                ?.setValue(playlist.likeCounter?.minus(1))
            networkState.postValue(NetworkState.LOADED)
        }?.addOnFailureListener { err ->
            networkState.postValue(NetworkState.error(err.message.toString()))
        }
        return networkState
    }

    override fun checkLikePlaylist(playlistID:String): MutableLiveData<Boolean> {
        val isLike = MutableLiveData<Boolean>()
        val query = databaseRef()?.child(FAVORITE_PLAYLIST)?.child(currentUser()?.uid.toString())
            ?.child(playlistID.toString())
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists())
                    isLike.postValue(true)
                else
                    isLike.postValue(false)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                isLike.postValue(false)
            }
        }
        query?.addValueEventListener(valueEventListener)
        return isLike
    }

    override fun getAllPlaylist(): ResultData<ArrayList<Playlist>>{
        val networkState = MutableLiveData<NetworkState>()
        val responsePlaylists = MutableLiveData<ArrayList<Playlist>>()
        networkState.postValue(NetworkState.LOADING)
        val listPlaylist = ArrayList<Playlist>()
        var playlist: Playlist?
        val query = databaseRef()?.child(PLAYLIST)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        playlist = ds.getValue(Playlist::class.java)
                        playlist?.let { listPlaylist.add(it) }
                    }
                    if (listPlaylist.size > 0) {
                        listPlaylist.reverse()
                        responsePlaylists.value = listPlaylist ?: arrayListOf()
                        networkState.postValue(NetworkState.LOADED)
                    } else
                        networkState.postValue(NetworkState.error("List playlist are empty"))
                } else {
                    networkState.postValue(NetworkState.error("List playlist are empty"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responsePlaylists,
            networkState = networkState
        )
    }
}