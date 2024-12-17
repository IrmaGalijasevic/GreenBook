package unsa.etf.rma.rmaprojekat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class Converters {
    private val gson = Gson()

    // Generic functions for list conversion
    private inline fun <reified T> fromList(value: List<T>, typeToken: TypeToken<List<String>>): String {
        val nazivList = value.map { (it as Enum<*>).name }
        return gson.toJson(nazivList, typeToken.type)
    }

    private inline fun <reified T : Enum<T>> toList(value: String, typeToken: TypeToken<List<String>>): List<T> {
        val nazivList: List<String> = gson.fromJson(value, typeToken.type)
        return nazivList.map { enumValueOf<T>(it) }
    }

    // Converters for MedicinskaKorist
    @TypeConverter
    fun fromMedicinskaKorist(value: MedicinskaKorist): String {
        return value.name
    }

    @TypeConverter
    fun toMedicinskaKorist(value: String): MedicinskaKorist {
        return MedicinskaKorist.valueOf(value)
    }

    @TypeConverter
    fun fromMedicinskaKoristList(value: List<MedicinskaKorist>): String {
        return fromList(value, object : TypeToken<List<String>>() {})
    }

    @TypeConverter
    fun toMedicinskaKoristList(value: String): List<MedicinskaKorist> {
        return toList(value, object : TypeToken<List<String>>() {})
    }

    // Converters for ProfilOkusaBiljke
    @TypeConverter
    fun fromProfilOkusaBiljke(value: ProfilOkusaBiljke): String {
        return value.name
    }

    @TypeConverter
    fun toProfilOkusaBiljke(value: String): ProfilOkusaBiljke {
        return ProfilOkusaBiljke.valueOf(value)
    }

    @TypeConverter
    fun fromProfilOkusaBiljkeList(value: List<ProfilOkusaBiljke>): String {
        return fromList(value, object : TypeToken<List<String>>() {})
    }

    @TypeConverter
    fun toProfilOkusaBiljkeList(value: String): List<ProfilOkusaBiljke> {
        return toList(value, object : TypeToken<List<String>>() {})
    }

    // Converters for String List
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return gson.fromJson(value, object : TypeToken<List<String>>() {}.type)
    }

    // Converters for KlimatskiTip
    @TypeConverter
    fun fromKlimatskiTip(value: KlimatskiTip): String {
        return value.name
    }

    @TypeConverter
    fun toKlimatskiTip(value: String): KlimatskiTip {
        return KlimatskiTip.valueOf(value)
    }

    @TypeConverter
    fun fromKlimatskiTipList(value: List<KlimatskiTip>): String {
        return fromList(value, object : TypeToken<List<String>>() {})
    }

    @TypeConverter
    fun toKlimatskiTipList(value: String): List<KlimatskiTip> {
        return toList(value, object : TypeToken<List<String>>() {})
    }

    // Converters for Zemljiste
    @TypeConverter
    fun fromZemljiste(value: Zemljiste): String {
        return value.name
    }

    @TypeConverter
    fun toZemljiste(value: String): Zemljiste {
        return Zemljiste.valueOf(value)
    }

    @TypeConverter
    fun fromZemljisteList(value: List<Zemljiste>): String {
        return fromList(value, object : TypeToken<List<String>>() {})
    }

    @TypeConverter
    fun toZemljisteList(value: String): List<Zemljiste> {
        return toList(value, object : TypeToken<List<String>>() {})
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    @TypeConverter
    fun toBitmap(encodedString: String): Bitmap {
        val byteArray = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}