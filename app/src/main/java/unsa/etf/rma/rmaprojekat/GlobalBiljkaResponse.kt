package unsa.etf.rma.rmaprojekat

data class GlobalBiljkaResponse(
    val data: List<imageBiljka>
)
data class imageBiljka(
    val id: Int,
    val image_url: String
)