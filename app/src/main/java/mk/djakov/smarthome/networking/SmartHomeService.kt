package mk.djakov.smarthome.networking

import mk.djakov.smarthome.data.model.RelayResponse
import retrofit2.Response
import retrofit2.http.GET

interface SmartHomeService {

    @GET("control?cmd=GPIO,12,1")
    suspend fun deviceOneOn(): Response<RelayResponse>

    @GET("control?cmd=GPIO,12,0")
    suspend fun deviceOneOff(): Response<RelayResponse>

    @GET("control?cmd=status,gpio,12")
    suspend fun deviceOneStatus(): Response<RelayResponse>
}