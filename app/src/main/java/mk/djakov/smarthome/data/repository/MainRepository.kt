package mk.djakov.smarthome.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mk.djakov.smarthome.data.model.Device
import mk.djakov.smarthome.db.DeviceDao
import mk.djakov.smarthome.networking.SmartHomeService
import mk.djakov.smarthome.util.CommonRoutes.Route
import mk.djakov.smarthome.util.Const
import mk.djakov.smarthome.util.Helper.getRoute
import mk.djakov.smarthome.util.Response
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val smartHomeService: SmartHomeService,
    private val deviceDao: DeviceDao
) {

    val devices = deviceDao.getAllDevices()

    suspend fun updateStatus(device: Device, newValue: Boolean) = coroutineScope {
        try {
            deviceDao.setIsLoading(device.id!!, true)
            val url = if (newValue) getRoute(device, Route.DEVICE_ON)
            else getRoute(device, Route.DEVICE_OFF)

            val res = smartHomeService.genericRoute(url)
            if (res.isSuccessful) {
                Response.Success(res.body()!!).also {
                    deviceDao.updateStatus(device.id!!, res.body()?.state == 1)
                    deviceDao.setIsLoading(device.id!!, false)
                }
            } else {
                Response.Error(Const.ERROR, null).also {
                    deviceDao.setIsLoading(device.id!!, false)
                }
            }
        } catch (e: IOException) {
            Response.Error(Const.NO_INTERNET_CONNECTION, null).also {
                deviceDao.setIsLoading(device.id!!, false)
            }
        } catch (e: HttpException) {
            Response.Error(Const.BAD_REQUEST, null).also {
                deviceDao.setIsLoading(device.id!!, false)
            }
        } catch (e: Exception) {
            Response.Error(Const.ERROR, null).also {
                deviceDao.setIsLoading(device.id!!, false)
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
            deviceDao.setIsLoading(device.id!!, true)
            val res = smartHomeService.genericRoute(getRoute(device, Route.STATUS))
            if (res.isSuccessful) {
                Response.Success(res.body()!!).also {
                    deviceDao.updateStatus(device.id!!, res.body()?.state == 1)
                    deviceDao.setIsLoading(device.id!!, false)
                }
            } else {
                Response.Error(Const.ERROR, null).also {
                    deviceDao.setIsLoading(device.id!!, false)
                }
            }
        } catch (e: Exception) {
            Response.Error(Const.ERROR, null).also {
                deviceDao.setIsLoading(device.id!!, false)
            }
        }
    }

    suspend fun updateDevice(id: Int, name: String, address: String, gpio: Int) =
        coroutineScope {
            deviceDao.updateDevice(id, name, address, gpio)
        }

    suspend fun addDevice(name: String, address: String, gpio: Int) = coroutineScope {
        val rowId = deviceDao.insertDevice(Device(name, address, gpio))

        //Check the new inserted device state
        withContext(Dispatchers.IO) {
            deviceDao.getDeviceById(rowId).also {
                launch { checkState(it) }
            }
        }
    }

    suspend fun deleteDevice(device: Device) = coroutineScope {
        deviceDao.deleteDevice(device)
    }

    suspend fun updateDevicePosition(id: Int, position: Int) = coroutineScope {
        deviceDao.updateDevicePosition(id, position)
    }
}