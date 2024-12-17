package unsa.etf.rma.rmaprojekat

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(
    foreignKeys = [ForeignKey(
        entity = Biljka::class,
        parentColumns = ["id"],
        childColumns = ["idBiljke"],
        onDelete = ForeignKey.CASCADE
    )]
)

@TypeConverters(Converters::class)
data class BiljkaBitmap(
    @PrimaryKey(autoGenerate = true)
    var id:Long?=null,
    var idBiljke: Long,
    var bitmap: Bitmap
    )
