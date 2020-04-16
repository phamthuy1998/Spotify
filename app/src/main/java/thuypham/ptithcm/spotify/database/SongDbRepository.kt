package thuypham.ptithcm.spotify.database

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import thuypham.ptithcm.spotify.data.Song
import kotlin.coroutines.CoroutineContext

class SongDbRepository(application: Application) : CoroutineScope {
    private var songDao: SongDAO? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main


    init {
        songDao = AppDatabase.getDatabase(application)?.songDao()
    }

    fun getAllSong(): LiveData<List<Song>>? = songDao?.getSongs()

//    private suspend fun getAllSongBG(){
//        withContext(Dispatchers.IO){
//            songDao?.getSongs()
//        }
//    }

    fun insert(listSong: ArrayList<Song>) {
        AsyncTaskProcessInsert(songDao).execute(*listSong.toTypedArray())
    }

    private class AsyncTaskProcessInsert internal constructor(val dao: SongDAO?) :
        AsyncTask<Song, Void, Void>() {
        override fun doInBackground(vararg song: Song): Void? {
            dao?.insert(*(song.toMutableList()).toTypedArray())
            return null
        }
    }

    fun deleteAll() {
        AsyncTaskProcessDeleteAll(songDao).execute()
    }

    private class AsyncTaskProcessDeleteAll
    internal constructor(val dao: SongDAO?) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            dao?.removeAll()
            return null
        }

    }

    fun update(song: Song) {
        AsyncTaskProcessUpdate(songDao).execute(song)
    }

    private class AsyncTaskProcessUpdate
    internal constructor(val dao: SongDAO?) : AsyncTask<Song, Void, Void>() {

        override fun doInBackground(vararg song: Song): Void? {
            dao?.update(song[0])
            return null
        }
    }
}