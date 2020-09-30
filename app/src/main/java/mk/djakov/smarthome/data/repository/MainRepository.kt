package mk.djakov.smarthome.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mk.djakov.smarthome.data.model.RelayResponse
import mk.djakov.smarthome.networking.SmartHomeService
import mk.djakov.smarthome.util.Response
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val service: SmartHomeService
) {

    suspend fun updateValue(device: String, value: Boolean): Response<RelayResponse> =
        try {
            withContext(Dispatchers.IO) {
                val res = if (value) service.deviceOneOn() else service.deviceOneOff()
                if (res.isSuccessful) {
                    Response.Success(res.body()!!)
                } else {
                    Response.Error("error")
                }
            }
        } catch (e: Exception) {
            Response.Error("")
        }

    suspend fun checkState(device: String): Response<RelayResponse> =
        try {
            withContext(Dispatchers.IO) {
                val res = service.deviceOneStatus()
                if (res.isSuccessful) {
                    Response.Success(res.body()!!)
                } else {
                    Response.Error("error")
                }
            }
        } catch (e: Exception) {
            Response.Error("")
        }
}