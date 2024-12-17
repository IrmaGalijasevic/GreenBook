package unsa.etf.rma.rmaprojekat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
@TypeConverters(Converters::class)

data class Biljka(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id:Long?=null,
    @ColumnInfo(name = "naziv") var naziv: String,
    @ColumnInfo(name = "family") var porodica: String,
    @ColumnInfo(name = "medicinskoUpozorenje") var medicinskoUpozorenje: String,
    @ColumnInfo(name = "medicinskeKoristi") var medicinskeKoristi: List<MedicinskaKorist>,
    @ColumnInfo(name = "profil_okusa") var profilOkusa: ProfilOkusaBiljke,
    @ColumnInfo(name = "jela") var jela: List<String>,
    @ColumnInfo(name = "klimatskiTipovi") var klimatskiTipovi: List<KlimatskiTip>,
    @ColumnInfo(name = "zemljisniTipovi") var zemljisniTipovi: List<Zemljiste>,
    @ColumnInfo(name = "online_checked") var onlineChecked: Boolean = false
)
