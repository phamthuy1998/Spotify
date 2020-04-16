package thuypham.ptithcm.spotify.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import thuypham.ptithcm.spotify.data.Song
import thuypham.ptithcm.spotify.util.DATABASE_NAME

@Database(entities = [Song::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase? {
            // tránh tạo nhiều databse
            if (instance == null) {
                // chạy đồng bộ, chỉ có 1 class truy cập tại 1 thời điểm
                // khi 1 class truy cập nó sẽ lock lại, khi nào thực hiện xong sẽ unlock
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }
    }

}