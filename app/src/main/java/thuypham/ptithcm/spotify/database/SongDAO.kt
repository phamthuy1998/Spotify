package thuypham.ptithcm.spotify.database

import androidx.lifecycle.LiveData
import androidx.room.*
import thuypham.ptithcm.spotify.data.Song

@Dao
interface SongDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg song: Song)

    @Query("Select * from Song")
    fun getSongs(): LiveData<List<Song>>

    @Query("Delete from Song")
    fun removeAll()

    @Update
    fun update(vararg song: Song)
}