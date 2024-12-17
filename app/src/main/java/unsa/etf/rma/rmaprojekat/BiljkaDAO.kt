package unsa.etf.rma.rmaprojekat

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Dao
interface BiljkaDAO {
    val trefleDao: TrefleDAO
        get() = TrefleDAO()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBiljka(biljka: Biljka): Long

    @Transaction
    suspend fun saveBiljka(biljka: Biljka): Boolean {
        return try {
            insertBiljka(biljka) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Transaction
    suspend fun fixOfflineBiljka(): Int {
        return withContext(Dispatchers.IO) {
            val offlinePlants = getOnlineCheckedFalse()
            var updatedCount = 0
            for (plant in offlinePlants) {
                val updatedPlant = trefleDao.fixData(plant)
                if (plant != updatedPlant) {
                    updateBiljka(updatedPlant)
                    updatedCount++
                }
            }
            // Update the onlineChecked flag for fixed plants
            updatedCount
        }
    }

    @Query("SELECT * FROM Biljka WHERE online_checked = 0")
    suspend fun getOnlineCheckedFalse(): List<Biljka>

    @Update
    suspend fun updateBiljka(biljka: Biljka)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBiljkaBitmap(biljkaBitmap: BiljkaBitmap): Long

    @Transaction
    suspend fun addImage(idBiljke: Long, bitmap: Bitmap): Boolean {
        return try {
            val biljkaBitmap = BiljkaBitmap(idBiljke = idBiljke, bitmap = bitmap)
            insertBiljkaBitmap(biljkaBitmap) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    @Query("SELECT * FROM Biljka")
    suspend fun getAllBiljkas(): List<Biljka>

    @Query("SELECT * FROM Biljka")
    fun getAllBiljkasLiveData(): LiveData<List<Biljka>>

    @Query("DELETE FROM BiljkaBitmap")
    suspend fun deleteFromBiljkaBitmapTable()

    @Query("DELETE FROM Biljka")
    suspend fun deleteFromBiljkaTable()

    @Transaction
    suspend fun clearData() {
        deleteFromBiljkaBitmapTable()
        deleteFromBiljkaTable()
    }

    @Query("SELECT * FROM BiljkaBitmap WHERE id = :plantId LIMIT 1")
    suspend fun getBitmapByPlantId(plantId: Long): BiljkaBitmap?

    @Query("SELECT MAX(id) FROM biljka")
    suspend fun getMaxIdForBiljka(): Long?
}