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
import thuypham.ptithcm.spotify.util.SONG

interface SongsRepository {
    fun getAllSong(): ResultData<ArrayList<Song>>
}

class SongsRepositoryImpl : SongsRepository {

    private val firebaseDatabase: FirebaseDatabase? by lazy { FirebaseDatabase.getInstance() }
    private val firebaseAuth: FirebaseAuth? by lazy { FirebaseAuth.getInstance() }
    private fun databaseRef() = firebaseDatabase?.reference

    override fun getAllSong(): ResultData<ArrayList<Song>> {
        val networkState = MutableLiveData<NetworkState>()
        val responseListSong = MutableLiveData<ArrayList<Song>>()
        networkState.postValue(NetworkState.LOADING)
        val listSong = ArrayList<Song>()
        var song: Song?
        val query = databaseRef()?.child(SONG)

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
                    networkState.postValue(NetworkState.error("List song are empty"))
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
}