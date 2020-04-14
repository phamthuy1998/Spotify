package thuypham.ptithcm.spotify.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import thuypham.ptithcm.spotify.data.*
import thuypham.ptithcm.spotify.util.*

interface ArtistRepository {
    fun getListSongOfArtist(artistID: String): ResultData<ArrayList<Song>>
    fun getListArtistFollowing(): ResultData<ArrayList<Artist>>
    fun getAllArtist(): ResultData<ArrayList<Artist>>
    fun getLatestAlbum(artistID: String): ResultData<Album>
    fun getArtistInfoByID(artistID: String): ResultData<Artist>
    fun followArtist(artist: Artist): ResultData<Boolean>
    fun updateFollowCounterOfArtist(artistID:String, followCounter:Int)
    fun unFollowArtist(artistID: String): ResultData<Boolean>
    fun checkFollowArtist(artistID: String): MutableLiveData<Boolean>
}


class ArtistRepositoryImpl : ArtistRepository {

    private val firebaseDatabase: FirebaseDatabase? by lazy { FirebaseDatabase.getInstance() }
    private val firebaseAuth: FirebaseAuth? by lazy { FirebaseAuth.getInstance() }
    private fun currentUser() = firebaseAuth?.currentUser
    private fun databaseRef() = firebaseDatabase?.reference

    override fun getListSongOfArtist(artistID: String): ResultData<ArrayList<Song>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListSong = MutableLiveData<ArrayList<Song>>()
        networkState.postValue(NetworkState.LOADING)
        val listSong = ArrayList<Song>()
        var song: Song?
        val query = databaseRef()?.child(SONG)
            ?.orderByChild(ARTIST_ID)
            ?.equalTo(artistID)

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
                    networkState.postValue(NetworkState.error("Artist does not have any song!"))
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

    override fun getListArtistFollowing(): ResultData<ArrayList<Artist>> {
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

    override fun getAllArtist(): ResultData<ArrayList<Artist>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListArtistFollow = MutableLiveData<ArrayList<Artist>>()
        networkState.postValue(NetworkState.LOADING)
        val listSong = ArrayList<Artist>()
        var artist: Artist?
        val query = databaseRef()?.child(ARTIST)

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

    override fun getLatestAlbum(artistID: String): ResultData<Album> {
        val networkState = MutableLiveData<NetworkState>()
        val responseAlbum = MutableLiveData<Album>()
        networkState.postValue(NetworkState.LOADING)
        var album: Album?=null
        val query = databaseRef()?.child(ALBUM)?.orderByChild(ARTIST_ID)?.equalTo(artistID)?.limitToLast(1)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) album = ds.getValue(Album::class.java)
                    if (!album?.albumName.isNullOrEmpty()) {
                        responseAlbum.value = album
                        networkState.postValue(NetworkState.LOADED)
                    } else
                        networkState.postValue(NetworkState.error("No albums available!"))
                } else {
                    networkState.postValue(NetworkState.error("No albums available!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))
        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseAlbum,
            networkState = networkState
        )
    }

    override fun getArtistInfoByID(artistID: String): ResultData<Artist> {
        val networkState = MutableLiveData<NetworkState>()
        val responseArtistInfo = MutableLiveData<Artist>()
        networkState.postValue(NetworkState.LOADING)
        var artist: Artist?
        val query = databaseRef()?.child(ARTIST)?.child(artistID)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    artist = dataSnapshot.getValue(Artist::class.java)
                    if (artist != null) {
                        responseArtistInfo.value = artist
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
            data = responseArtistInfo,
            networkState = networkState
        )
    }

    override fun followArtist(artist: Artist): ResultData<Boolean> {
        val networkState = MutableLiveData<NetworkState>()
        val reposeFollow = MutableLiveData<Boolean>()
        val query = databaseRef()?.child(FOLLOW_ARTISTS)?.child(currentUser()?.uid.toString())
            ?.child(artist.id.toString())
        query?.setValue(artist)?.addOnSuccessListener {
            networkState.postValue(NetworkState.LOADED)
            reposeFollow.postValue(true)
        }?.addOnFailureListener { err ->
            networkState.postValue(NetworkState.error(err.message.toString()))
            reposeFollow.postValue(false)
        }
        return ResultData(
            data = reposeFollow,
            networkState = networkState
        )
    }

    override fun updateFollowCounterOfArtist(artistID: String, followCounter: Int) {
            databaseRef()?.child(ARTIST)?.child(artistID)?.child("followCounter")
                ?.setValue(followCounter)
    }

    override fun unFollowArtist(artistID: String): ResultData<Boolean> {
        val networkState = MutableLiveData<NetworkState>()
        val reposeUnFollow = MutableLiveData<Boolean>()
        val query = databaseRef()?.child(FOLLOW_ARTISTS)?.child(currentUser()?.uid.toString())
            ?.child(artistID)
        query?.removeValue()?.addOnSuccessListener {
            networkState.postValue(NetworkState.LOADED)
            reposeUnFollow.postValue(true)
        }?.addOnFailureListener { err ->
            networkState.postValue(NetworkState.error(err.message.toString()))
            reposeUnFollow.postValue(false)
        }
        return ResultData(
            data = reposeUnFollow,
            networkState = networkState
        )
    }

    override fun checkFollowArtist(artistID: String): MutableLiveData<Boolean> {
        val isLike = MutableLiveData<Boolean>()
        val query = databaseRef()?.child(FOLLOW_ARTISTS)?.child(currentUser()?.uid.toString())
            ?.child(artistID.toString())
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