package mk.djakov.smarthome.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mk.djakov.smarthome.data.model.RelayResponse
import mk.djakov.smarthome.networking.SmartHomeService
import mk.djakov.smarthome.util.Const
import mk.djakov.smarthome.util.Response
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val deviceOneService: SmartHomeService,
    private val deviceTwoService: SmartHomeService
) {

    suspend fun updateValue(device: String, value: Boolean): Response<RelayResponse> =
        try {
            withContext(Dispatchers.IO) {
                val res =
                    if (value) getService(device).deviceOneOn() else getService(device).deviceOneOff()
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
                val res = getService(device).deviceOneStatus()
                if (res.isSuccessful) {
                    Response.Success(res.body()!!)
                } else {
                    Response.Error("error")
                }
            }
        } catch (e: Exception) {
            Response.Error("")
        }

    private fun getService(device: String) =
        when (device) {
            Const.DEVICE_ONE -> deviceOneService
            else -> deviceTwoService
        }
}