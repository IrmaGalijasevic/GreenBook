package unsa.etf.rma.rmaprojekat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class TrefleDAO {

    private var defaultBitmap: Bitmap? = null
    private val trefle_api_key : String = BuildConfig.TREFLE_API_KEY


    fun setDefaultBitmap(bitmap: Bitmap){
        defaultBitmap=bitmap
    }
    suspend fun getImage(plant: Biljka): Bitmap? {
        defaultBitmap = createNoPhotoBitmap()
        return withContext(Dispatchers.IO) {
            try {
                val latinName = plant.naziv.substringAfter('(').substringBefore(')')
                val response = ApiAdapter.retrofit.getPlantWithLatinName(trefle_api_key, latinName)

                if (response.isSuccessful) {
                    val globalBiljkaResponse: GlobalBiljkaResponse? = response.body()
                    if (globalBiljkaResponse != null && globalBiljkaResponse.data.isNotEmpty()) {

                        val imageUrl = globalBiljkaResponse.data[0].image_url
                        return@withContext loadImageBitmap(imageUrl)
                    } else {
                        return@withContext defaultBitmap
                    }
                } else {
                    return@withContext defaultBitmap
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext defaultBitmap
            }
        }
    }

    suspend fun fixData(plant: Biljka): Biljka {
        return withContext(Dispatchers.IO) {
            try {
                val latinName = plant.naziv.substringAfter('(').substringBefore(')')

                val response = ApiAdapter.retrofit.getPlantWithLatinName(trefle_api_key, latinName)
                if (response.isSuccessful) {
                    val listaBiljaka: GlobalBiljkaResponse? = response.body()
                    if (listaBiljaka != null && listaBiljaka.data.isNotEmpty()) {
                        val idBiljke = listaBiljaka.data[0].id

                        val detailResponse = ApiAdapter.retrofit.getPlantDetails(idBiljke,trefle_api_key)
                        if (detailResponse.isSuccessful) {
                            val detailBiljka = detailResponse.body()?.data ?: return@withContext plant

                            if (detailBiljka.family.name != null && detailBiljka.family.name != plant.porodica) {
                                plant.porodica = detailBiljka.family.name
                                plant.onlineChecked=true
                            }
                            if (detailBiljka.edible != null && detailBiljka.edible == false) {
                                if (!plant.medicinskoUpozorenje.contains("NIJE JESTIVO", ignoreCase = true)) {
                                    plant.medicinskoUpozorenje += " NIJE JESTIVO"
                                }
                                plant.jela = emptyList()
                                plant.onlineChecked=true

                            }
                            if (detailBiljka.main_species?.specifications?.toxicity != null && detailBiljka.main_species?.specifications?.toxicity != "none"
                                && !plant.medicinskoUpozorenje.contains("TOKSIČNO", ignoreCase = true)) {
                                plant.medicinskoUpozorenje += " TOKSIČNO"
                                plant.onlineChecked=true

                            }

                            val soilTexture = detailBiljka.main_species?.growth?.soil_texture
                            if (soilTexture != null && soilTexture in 1..10) {
                                plant.zemljisniTipovi = listOf(getZemljiste(soilTexture))
                                plant.onlineChecked=true

                            }

                            val light = detailBiljka.main_species?.growth?.light
                            val humidity = detailBiljka.main_species?.growth?.atmospheric_humidity
                            if (light != null && humidity != null) {
                                val klimatskiTip = getKlimatskiTip(light, humidity)
                                if (klimatskiTip != null) {
                                    plant.klimatskiTipovi = klimatskiTip
                                }
                                plant.onlineChecked=true

                            }
                            return@withContext plant
                        } else {
                        }
                    } else {
                    }
                } else {
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DAO", "Error occurred: ${e.message}")
            }
            return@withContext plant
        }
    }


    fun getZemljiste(inputValue: Int): Zemljiste {
        return when (inputValue) {
            1 -> Zemljiste.GLINENO
            2 -> Zemljiste.GLINENO
            3 -> Zemljiste.PJESKOVITO
            4 -> Zemljiste.PJESKOVITO
            5 -> Zemljiste.ILOVACA
            6 -> Zemljiste.ILOVACA
            7 -> Zemljiste.CRNICA
            8 -> Zemljiste.CRNICA
            9 -> Zemljiste.SLJUNOVITO
            10 -> Zemljiste.KRECNJACKO
            else ->Zemljiste.KRECNJACKO// Default value
        }
    }

    fun getKlimatskiTip(light: Int, humidity: Int) : List<KlimatskiTip>?{
        var list : MutableList<KlimatskiTip> = mutableListOf()
        val combinations = arrayOf(
            Pair(4..7, 3..7) to KlimatskiTip.UMJERENA,
            Pair(6..9, 1..5) to KlimatskiTip.SREDOZEMNA,
            Pair(8..10, 7..10) to KlimatskiTip.TROPSKA,
            Pair(6..9, 5..8) to KlimatskiTip.SUBTROPSKA,
            Pair(7..9, 1..2) to KlimatskiTip.SUHA,
            Pair(0..5, 3..7) to KlimatskiTip.PLANINSKA,
        )
        for ((ranges, tip) in combinations) {
            val (lightRange, humidityRange) = ranges
            if (light in lightRange && humidity in humidityRange) {
                list.add(tip)
            }
        }
        if(list.isEmpty()){
            for ((ranges, tip) in combinations) {
                val (lightRange, humidityRange) = ranges
                if (light in lightRange || humidity in humidityRange) {
                    list.add(tip)
                }
            }
        }
        if(list.isNotEmpty()){
            return list
        }else
        return null

    }
    suspend fun getPlantswithFlowerColor(flowerColor: String, substr: String): List<Biljka> {
        val listaBiljaka: MutableList<Biljka> = mutableListOf()
        return withContext(Dispatchers.IO) {
            try {
                val response = ApiAdapter.retrofit.getPlantsByColorAndName(trefle_api_key, flowerColor.lowercase(), substr)
                if (response.isSuccessful) {
                    val listaBiljakaResponse: GlobalBiljkaResponse? = response.body()
                    if (listaBiljakaResponse != null && listaBiljakaResponse.data.isNotEmpty()) {
                        for (biljka in listaBiljakaResponse.data) {
                            val idBiljke = biljka.id
                            val detailResponse = ApiAdapter.retrofit.getPlantDetails(idBiljke, trefle_api_key)
                            if (detailResponse.isSuccessful) {
                                val detailBiljka = detailResponse.body()?.data ?: continue
                                val soilTexture = detailBiljka.main_species?.growth?.soil_texture
                                val zemljisniTipovi: List<Zemljiste> = soilTexture?.let {
                                    if (it in 1..10) listOf(getZemljiste(it)) else listOf(Zemljiste.ILOVACA)
                                } ?: listOf(Zemljiste.ILOVACA)

                                val light = detailBiljka.main_species?.growth?.light
                                val humidity = detailBiljka.main_species?.growth?.atmospheric_humidity
                                val klimatskiTipovi: List<KlimatskiTip> = if (light != null && humidity != null) {
                                    getKlimatskiTip(light, humidity)?.let { it } ?: listOf(KlimatskiTip.UMJERENA)
                                } else {
                                    listOf(KlimatskiTip.UMJERENA)
                                }

                                val novaBiljka = Biljka(0,
                                    "${detailBiljka.common_name} (${detailBiljka.scientific_name})",
                                    detailBiljka.family.name,
                                    "",
                                    listOf(MedicinskaKorist.PODRSKAIMUNITETU),
                                    ProfilOkusaBiljke.BEZUKUSNO,
                                    emptyList(),
                                    klimatskiTipovi,
                                    zemljisniTipovi, false
                                )
                                listaBiljaka.add(novaBiljka)
                            } else {
                            }
                        }
                    } else {
                    }
                } else {
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext listaBiljaka
        }
    }





    public fun createNoPhotoBitmap(): Bitmap {
        // Define fixed width and height for the bitmap
        val width = 200
        val height = 100

        // Create a mutable bitmap with ARGB_8888 configuration
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Set a fixed color for the bitmap (e.g., gray)
        val color = Color.GRAY

        // Fill the bitmap with the fixed color
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, color)
            }
        }

        return bitmap
    }

    private fun loadImageBitmap(imageUrl: String): Bitmap? {
            val inputStream = URL(imageUrl).openStream()
            return BitmapFactory.decodeStream(inputStream)
    }
}