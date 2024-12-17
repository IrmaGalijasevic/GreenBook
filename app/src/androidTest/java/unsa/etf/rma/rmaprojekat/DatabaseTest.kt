package unsa.etf.rma.rmaprojekat

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var biljkaDao: BiljkaDAO
    private lateinit var db: BiljkaDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BiljkaDatabase::class.java).build()
        biljkaDao = db.biljkaDAO()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveBiljka() = runBlocking {
        // Given a new Biljka object
        val biljka = Biljka(10001,
            naziv = "Bosiljak (Ocimum basilicum)",
            porodica = "Netacno (usnate)",
            medicinskoUpozorenje = "Može iritati kožu osjetljivu na sunce. Preporučuje se oprezna upotreba pri korištenju ulja bosiljka.",
            medicinskeKoristi = listOf(
                MedicinskaKorist.SMIRENJE,
                MedicinskaKorist.REGULACIJAPROBAVE
            ),
            profilOkusa = ProfilOkusaBiljke.BEZUKUSNO,
            jela = listOf("Salata od paradajza", "Punjene tikvice"),
            klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.SUBTROPSKA),
            zemljisniTipovi = listOf(Zemljiste.PJESKOVITO, Zemljiste.ILOVACA), false

        )

        // When inserting the Biljka into the database
        biljkaDao.saveBiljka(biljka)

        // Then verify that the retrieved Biljka matches the inserted Biljka
        val allBiljkas = biljkaDao.getAllBiljkas()
        assertEquals(1, allBiljkas.size)
        assertEquals(biljka, allBiljkas[0])
    }

    @Test
    fun clearDataReturnsEmptyList() = runBlocking {
        // Given two Biljka objects inserted into the database
        val biljka1 = Biljka(1,
            naziv = "Ruža (Rosa)",
            porodica = "Rosaceae",
            medicinskoUpozorenje = "Bodljikavo",
            medicinskeKoristi = listOf(MedicinskaKorist.SMIRENJE),
            profilOkusa = ProfilOkusaBiljke.BEZUKUSNO,
            jela = listOf("Sok od ruže"),
            klimatskiTipovi = listOf(KlimatskiTip.UMJERENA),
            zemljisniTipovi = listOf(Zemljiste.CRNICA),
            false
        )
        val biljka2 = Biljka(2,
            naziv = "Bosiljak (Ocimum basilicum)",
            porodica = "Lamiaceae (usnate)",
            medicinskoUpozorenje = "Može iritati kožu osjetljivu na sunce. Preporučuje se oprezna upotreba pri korištenju ulja bosiljka.",
            medicinskeKoristi = listOf(MedicinskaKorist.SMIRENJE, MedicinskaKorist.REGULACIJAPROBAVE),
            profilOkusa = ProfilOkusaBiljke.BEZUKUSNO,
            jela = listOf("Salata od paradajza", "Punjene tikvice"),
            klimatskiTipovi = listOf(KlimatskiTip.SREDOZEMNA, KlimatskiTip.SUBTROPSKA),
            zemljisniTipovi = listOf(Zemljiste.PJESKOVITO, Zemljiste.ILOVACA),
            false
        )

        // Inserting both Biljka objects
        biljkaDao.saveBiljka(biljka1)
        biljkaDao.saveBiljka(biljka2)

        // Assert that there are two Biljka entries in the database
        val allBiljkasBeforeClear = biljkaDao.getAllBiljkas()
        assertEquals(2, allBiljkasBeforeClear.size)

        // When clearing the data
        biljkaDao.clearData()

        // Then verify that getAllBiljkas returns an empty list
        val allBiljkasAfterClear = biljkaDao.getAllBiljkas()
        assertEquals(0, allBiljkasAfterClear.size)
    }

}
