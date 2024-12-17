package unsa.etf.rma.rmaprojekat

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("plants/search")
    suspend fun getPlantWithLatinName(
        @Query("token") token: String,
        @Query("q") query: String
    ): Response<GlobalBiljkaResponse>
    @GET("plants/{id}")
    suspend fun getPlantDetails(
        @Path("id") id: Int,
        @Query("token") token: String
    ): Response<DetailBiljkaResponse>
    @GET("plants/search")
    suspend fun getPlantsByColorAndName(
        @Query("token") token: String,
        @Query("filter[flower_color]") color: String,
        @Query("q") commonName: String
    ): Response<GlobalBiljkaResponse>
}