package thuypham.ptithcm.spotify.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import thuypham.ptithcm.spotify.data.NetworkState
import thuypham.ptithcm.spotify.data.ResultData
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.util.FAVORITE_SONG
import thuypham.ptithcm.spotify.util.HISTORY
import thuypham.ptithcm.spotify.util.SONG

interface SongRepository {
    fun getSongByID(songID: String): ResultData<Song>
    fun addSongIntoHistory(song: Song)
    fun checkSongFavorite(songID: String): MutableLiveData<Boolean>
    fun addSongIntoFavorite(song: Song): MutableLiveData<NetworkState>
    fun removeFavoriteSong(songID: String): MutableLiveData<NetworkState>
}


class SongRepositoryImpl(
) : SongRepository {
    private val firebaseAuth: FirebaseAuth? by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseDatabase: FirebaseDatabase? by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun currentUser() = firebaseAuth?.currentUser
    private fun databaseRef() = firebaseDatabase?.reference

    override fun getSongByID(songID: String): ResultData<Song> {
        val networkState = MutableLiveData<NetworkState>()
        val responseSong = MutableLiveData<Song>()
        networkState.postValue(NetworkState.LOADING)
        var song: Song?
        val query = databaseRef()?.child(SONG)?.child(songID)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    song = dataSnapshot.getValue(Song::class.java)
                    responseSong.value = song
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(NetworkState.error("Can't load info this song!"))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) =
                networkState.postValue(NetworkState.error(databaseError.toException().toString()))
        }
        query?.addValueEventListener(valueEventListener)
        return ResultData(
            data = responseSong,
            networkState = networkState
        )
    }

    override fun addSongIntoHistory(song: Song) {
        val query = databaseRef()?.child(HISTORY)?.child(currentUser()?.uid.toString())
            ?.child(song.id.toString())
        query?.setValue(song)
    }

    override fun checkSongFavorite(songID: String): MutableLiveData<Boolean> {
        val isLike = MutableLiveData<Boolean>()
        val query = databaseRef()?.child(FAVORITE_SONG)?.child(currentUser()?.uid.toString())
            ?.child(songID.toString())
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

    override fun addSongIntoFavorite(song: Song): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        val query = databaseRef()?.child(FAVORITE_SONG)?.child(currentUser()?.uid.toString())
            ?.child(song.id.toString())
        query?.setValue(song)?.addOnSuccessListener {
            networkState.postValue(NetworkState.LOADED)
        }?.addOnFailureListener { err ->
            networkState.postValue(NetworkState.error(err.message.toString()))
        }
        return networkState
    }

    override fun removeFavoriteSong(songID: String): MutableLiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        val query = databaseRef()?.child(FAVORITE_SONG)?.child(currentUser()?.uid.toString())
            ?.child(songID)
        query?.removeValue()?.addOnSuccessListener {
            networkState.postValue(NetworkState.LOADED)
        }?.addOnFailureListener { err ->
            networkState.postValue(NetworkState.error(err.message.toString()))
        }
        return networkState
    }


}