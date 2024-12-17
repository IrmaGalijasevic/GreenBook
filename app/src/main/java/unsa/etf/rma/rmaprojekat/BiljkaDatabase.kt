package unsa.etf.rma.rmaprojekat

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Biljka::class,BiljkaBitmap::class), version = 2)
abstract class BiljkaDatabase : RoomDatabase() {
    abstract fun biljkaDAO(): BiljkaDAO
    abstract fun biljkaBItmapDAO(): BiljkaBitmapDAO
    companion object {
        private var INSTANCE: BiljkaDatabase? = null
        fun getInstance(context: Context): BiljkaDatabase {
            if (INSTANCE == null) {
                synchronized(BiljkaDatabase::class) {
                    INSTANCE = buildRoomDB(context)
                }
            }
            return INSTANCE!!
        }
        private fun buildRoomDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                BiljkaDatabase::class.java,
                "biljke-db"
            ).allowMainThreadQueries().build()
    }
}