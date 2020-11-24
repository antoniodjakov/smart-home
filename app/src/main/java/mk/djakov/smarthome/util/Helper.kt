package mk.djakov.smarthome.util

import android.content.Context
import android.widget.Toast
import mk.djakov.smarthome.data.model.Device

object Helper {

    fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    fun getRoute(device: Device, route: CommonRoutes.Route) = when (route) {
        CommonRoutes.Route.DEVICE_ON -> "${device.address}/${CommonRoutes.deviceOn(device.gpio)}"
        CommonRoutes.Route.DEVICE_OFF -> "${device.address}/${CommonRoutes.deviceOff(device.gpio)}"
        CommonRoutes.Route.PULSE -> "${device.address}/${
            CommonRoutes.pulse(
                device.command,
                device.gpio,
                device.commandStatus.toString(),
                device.duration.toString()
            )
        }"
        CommonRoutes.Route.STATUS -> "${device.address}/${CommonRoutes.deviceStatus(device.gpio)}"
    }

    fun ArrayList<Device>.swapDevices(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) return
        val device = this[fromPosition]
        this[fromPosition] = this[toPosition]
        this[toPosition] = device
    }
}