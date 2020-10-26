package mk.djakov.smarthome.networking

import mk.djakov.smarthome.data.model.RelayResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface SmartHomeService {


    /**
     * All routes are GET method, only the base url is dynamic
     * URL params are defined in [CommonRoutes]
     */
    @GET
    suspend fun genericRoute(@Url url: String): Response<RelayResponse>
}