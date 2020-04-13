package thuypham.ptithcm.spotify.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import thuypham.ptithcm.spotify.data.*
import thuypham.ptithcm.spotify.util.*

interface BrowserRepository {
    fun getAdvertise(): ResultData<ArrayList<Advertise>>
    fun getListArtist(): ResultData<ArrayList<Artist>>
    fun getMusicGenre(): ResultData<ArrayList<MusicGenre>>
    fun getTopHit(): ResultData<ArrayList<Country>>
    fun getAlbum(): ResultData<Album>
    fun getPlaylist(): ResultData<ArrayList<Playlist>>
    fun getListAlbum(): ResultData<ArrayList<Album>>
    fun getUserInfo(): ResultData<User>
}


class BrowserRepositoryImpl : BrowserRepository {

    private val firebaseAuth: FirebaseAuth? by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun currentUser() = firebaseAuth?.currentUser

    private fun databaseRef() = firebaseDatabase?.reference

    override fun getAdvertise(): ResultData<ArrayList<Advertise>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseAdvertise = MutableLiveData<ArrayList<Advertise>>()
        networkState.postValue(NetworkState.LOADING)
        val listAdv = ArrayList<Advertise>()
        var advertise: Advertise?
        val query = databaseRef()?.child(ADVERTISE)?.limitToLast(ITEM_COUNT_BANNER)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        advertise = ds.getValue(Advertise::class.java)
                        advertise?.let { listAdv.add(it) }
                    }
                    listAdv.reverse()
                    responseAdvertise.value = listAdv ?: arrayListOf()
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List empty"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseAdvertise,
            networkState = networkState
        )
    }

    override fun getListArtist(): ResultData<ArrayList<Artist>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseArtist = MutableLiveData<ArrayList<Artist>>()
        networkState.postValue(NetworkState.LOADING)
        val listArtist = ArrayList<Artist>()
        var artist: Artist?
        val query = databaseRef()?.child(ARTIST)?.limitToLast(ITEM_VIEW)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        artist = ds.getValue(Artist::class.java)
                        artist?.let { listArtist.add(it) }
                    }
                    responseArtist.value = listArtist ?: arrayListOf()
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List empty"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))

        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseArtist,
            networkState = networkState
        )
    }

    override fun getMusicGenre(): ResultData<ArrayList<MusicGenre>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseSongType = MutableLiveData<ArrayList<MusicGenre>>()
        networkState.postValue(NetworkState.LOADING)
        val listMusicGenre = ArrayList<MusicGenre>()
        var musicGenre: MusicGenre?
        val query = databaseRef()?.child(MUSIC_GENRE)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        musicGenre = ds.getValue(MusicGenre::class.java)
                        musicGenre?.let { listMusicGenre.add(it) }
                    }
                    listMusicGenre.reverse()
                    responseSongType.value = listMusicGenre ?: arrayListOf()
                    networkState.postValue(NetworkState.LOADED)
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

    override fun getTopHit(): ResultData<ArrayList<Country>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseTopHit = MutableLiveData<ArrayList<Country>>()
        networkState.postValue(NetworkState.LOADING)
        val listTopHit = ArrayList<Country>()
        var country: Country?
        val query = databaseRef()?.child(COUNTRY)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        country = ds.getValue(Country::class.java)
                        country?.let { listTopHit.add(it) }
                    }
                    listTopHit.reverse()
                    responseTopHit.value = listTopHit ?: arrayListOf()
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("List empty"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))
        }
        query?.addValueEventListener(valueEventListener)

        return ResultData(
            data = responseTopHit,
            networkState = networkState
        )
    }

    override fun getAlbum(): ResultData<Album> {
        val networkState = MutableLiveData<NetworkState>()
        val responseAlbum = MutableLiveData<Album>()
        networkState.postValue(NetworkState.LOADING)
        var album: Album? = null
        val query = databaseRef()?.child(ALBUM)?.limitToLast(1)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children)
                        album = ds.getValue(Album::class.java)
                    if (album != null) {
                        responseAlbum.value = album
                        networkState.postValue(NetworkState.LOADED)
                    } else
                        networkState.postValue(NetworkState.error("Can't load album!"))
                } else {
                    networkState.postValue(NetworkState.error("Can't load album!"))
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

    override fun getPlaylist(): ResultData<ArrayList<Playlist>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseSongType = MutableLiveData<ArrayList<Playlist>>()
        networkState.postValue(NetworkState.LOADING)
        val listPlaylist = ArrayList<Playlist>()
        var playlist: Playlist?
        val query = databaseRef()?.child(PLAYLIST)?.limitToFirst(ITEM_VIEW)
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
        val responseSongType = MutableLiveData<ArrayList<Album>>()
        networkState.postValue(NetworkState.LOADING)
        val listAlbum = ArrayList<Album>()
        var album: Album?
        val query = databaseRef()?.child(ALBUM)?.limitToFirst(ITEM_VIEW)
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

    override fun getUserInfo(): ResultData<User> {
        val networkState = MutableLiveData<NetworkState>()
        val responseUser = MutableLiveData<User>()
        networkState.postValue(NetworkState.LOADING)
        var user: User?
        val userID = currentUser()?.uid
        val query = userID?.let { databaseRef()?.child(USER)?.child(it) }
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        responseUser.value = user
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
            data = responseUser,
            networkState = networkState
        )
    }

}