package mk.djakov.smarthome.data.repository

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import mk.djakov.smarthome.data.model.Device
import mk.djakov.smarthome.db.DeviceDao
import mk.djakov.smarthome.networking.SmartHomeService
import mk.djakov.smarthome.util.CommonRoutes
import mk.djakov.smarthome.util.CommonRoutes.Route
import mk.djakov.smarthome.util.Const
import mk.djakov.smarthome.util.Response
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val smartHomeService: SmartHomeService,
    private val deviceDao: DeviceDao
) {
    val devices = deviceDao.getAllDevices()

    suspend fun updateStatus(device: Device, newValue: Boolean) =
        coroutineScope {
            try {
                deviceDao.setIsLoading(device.tag, true)
                val url = if (newValue) getRoute(device, Route.DEVICE_ON)
                else getRoute(device, Route.DEVICE_OFF)

                val res = smartHomeService.genericRoute(url)
                if (res.isSuccessful) {
                    Response.Success(res.body()!!).also {
                        deviceDao.updateStatus(device.tag, res.body()?.state == 1)
                        deviceDao.setIsLoading(device.tag, false)
                    }
                } else {
                    Response.Error(Const.ERROR, null).also {
                        deviceDao.setIsLoading(device.tag, false)
                    }
                }
            } catch (e: IOException) {
                Response.Error(Const.NO_INTERNET_CONNECTION, null).also {
                    deviceDao.setIsLoading(device.tag, false)
                }
            } catch (e: HttpException) {
                Response.Error(Const.ERROR, null).also {
                    deviceDao.setIsLoading(device.tag, false)
                }
            } catch (e: Exception) {
                Response.Error(Const.ERROR, null).also {
                    deviceDao.setIsLoading(device.tag, false)
                }
            }
        }

    suspend fun checkAllDevicesStateAsync() = coroutineScope {
        deviceDao.getAllDevicesAsync().map { device ->
            launch {
                checkState(device)
            }
        }
    }

    private suspend fun checkState(device: Device) = coroutineScope {
        try {
            deviceDao.setIsLoading(device.tag, true)
            val res = smartHomeService.genericRoute(getRoute(device, Route.STATUS))
            if (res.isSuccessful) {
                Response.Success(res.body()!!).also {
                    deviceDao.updateStatus(device.tag, res.body()?.state == 1)
                    deviceDao.setIsLoading(device.tag, false)
                }
            } else {
                Response.Error(Const.ERROR, null).also {
                    deviceDao.setIsLoading(device.tag, false)
                }
            }
        } catch (e: Exception) {
            Response.Error(Const.ERROR, null).also {
                deviceDao.setIsLoading(device.tag, false)
            }
        }
    }

    suspend fun insertAll(devices: List<Device>) = coroutineScope {
        deviceDao.insertDevices(devices)
    }

    private fun getRoute(device: Device, route: Route) = when (route) {
        Route.DEVICE_ON -> "${device.address}/${CommonRoutes.DEVICE_ON}"
        Route.DEVICE_OFF -> "${device.address}/${CommonRoutes.DEVICE_OFF}"
        Route.STATUS -> "${device.address}/${CommonRoutes.DEVICE_STATUS}"
    }
}