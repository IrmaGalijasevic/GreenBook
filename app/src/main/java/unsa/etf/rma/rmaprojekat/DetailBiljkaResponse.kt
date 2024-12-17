package unsa.etf.rma.rmaprojekat

data class DetailBiljkaResponse(
    val data: BiljkaData
)

data class BiljkaData(
    val id: Int,
    val family_name: String,
    val edible: Boolean,
    val main_species: MainSpecies?,
    val family: Family,
    val common_name: String,
    val scientific_name: String
)

data class MainSpecies(
    val specifications: Specifications?,
    val growth: Growth?
)

data class Specifications(
    val toxicity: String?
)

data class Growth(
    val soil_texture: Int?,
    val light: Int?,
    val atmospheric_humidity: Int?
)
data class Family(
    val name: String
)