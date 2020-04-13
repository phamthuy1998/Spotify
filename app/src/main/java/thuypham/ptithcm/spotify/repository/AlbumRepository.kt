package thuypham.ptithcm.spotify.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import thuypham.ptithcm.spotify.data.Album
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.util.ALBUM
import thuypham.ptithcm.spotify.util.FAVORITE_ALBUM
import thuypham.ptithcm.spotify.util.SONG

interface AlbumRepository{
    fun getListSongOfAlbum(albumID: String): ResultData<ArrayList<Song>>
    fun getListAlbum(): ResultData<ArrayList<Album>>
    fun getAlbumInfoByID(albumID: String): ResultData<Album>
    fun addAlbumFavorite(album: Album): MutableLiveData<NetworkState>
    fun removeAlbumFavorite(albumID: String): MutableLiveData<NetworkState>
    fun checkLikeAlbum(albumID: String): MutableLiveData<Boolean>
}


class AlbumRepositoryImpl : AlbumRepository {

    private val firebaseDatabase: FirebaseDatabase? by lazy { FirebaseDatabase.getInstance() }
    private val firebaseAuth: FirebaseAuth? by lazy { FirebaseAuth.getInstance() }
    private fun currentUser() = firebaseAuth?.currentUser
    private fun databaseRef() = firebaseDatabase?.reference

    override fun getListSongOfAlbum(albumID: String): ResultData<ArrayList<Song>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListSong = MutableLiveData<ArrayList<Song>>()
        networkState.postValue(NetworkState.LOADING)
        val listSong = ArrayList<Song>()
        var song: Song?
        val query = databaseRef()?.child(SONG)
            ?.orderByChild("albumID")
            ?.equalTo(albumID)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        song = ds.getValue(Song::class.java)
                        song?.let { listSong.add(it) }
                    }
                    listSong.reverse()
                    responseListSong.value = listSong
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
            data = responseListSong,
            networkState = networkState
        )
    }

    override fun getListAlbum(): ResultData<ArrayList<Album>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseSongType = MutableLiveData<ArrayList<Album>>()
        networkState.postValue(NetworkState.LOADING)
        val listAlbum = ArrayList<Album>()
        var album: Album?
        val query = databaseRef()?.child(ALBUM)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        album = ds.getValue(Album::class.java)
                        album?.let { listAlbum.add(it) }
                    }
                    if (listAlbum.size > 0) {
                        listAlbum.reverse()
                        responseSongType.value = listAlbum ?: arrayListOf()
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

    override fun getAlbumInfoByID(albumID: String): ResultData<Album> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListSong = MutableLiveData<Album>()
        networkState.postValue(NetworkState.LOADING)
        var album: Album?
        val query = databaseRef()?.child(ALBUM)?.child(albumID)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    album = dataSnapshot.getValue(Album::class.java)
                    if (album != null) {
                        responseListSong.value = album
                        networkState.postValue(NetworkState.LOADED)
                    } else
                        networkState.postValue(NetworkState.error("Can't load info this album!"))
                } else {
                    networkState.postValue(NetworkState.error("Can't load info this album!"))
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

    override fun addAlbumFavorite(album: Album): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        val query = databaseRef()?.child(FAVORITE_ALBUM)?.child(currentUser()?.uid.toString())
            ?.child(album.id.toString())
        query?.setValue(album)?.addOnSuccessListener {
            networkState.postValue(NetworkState.LOADED)
        }?.addOnFailureListener { err ->
            networkState.postValue(NetworkState.error(err.message.toString()))
        }
        return networkState
    }

    override fun removeAlbumFavorite(albumID: String): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        val query = databaseRef()?.child(FAVORITE_ALBUM)?.child(currentUser()?.uid.toString())
            ?.child(albumID)
        query?.removeValue()?.addOnSuccessListener {
            networkState.postValue(NetworkState.LOADED)
        }?.addOnFailureListener { err ->
            networkState.postValue(NetworkState.error(err.message.toString()))
        }
        return networkState
    }

    override fun checkLikeAlbum(albumID: String): MutableLiveData<Boolean> {
        val isLike = MutableLiveData<Boolean>()
        val query = databaseRef()?.child(FAVORITE_ALBUM)?.child(currentUser()?.uid.toString())
            ?.child(albumID.toString())
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

}