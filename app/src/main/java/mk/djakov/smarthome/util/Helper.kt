package mk.djakov.smarthome.util

import android.content.Context
import android.widget.Toast
import mk.djakov.smarthome.data.model.Device

object Helper {

    fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    fun formatAddress(address: String) =
        if (address.startsWith("http://")) address else "http://${address}"

    fun getRoute(device: Device, route: CommonRoutes.Route) = when (route) {
        CommonRoutes.Route.DEVICE_ON -> "${device.address}/${CommonRoutes.DEVICE_ON}"
        CommonRoutes.Route.DEVICE_OFF -> "${device.address}/${CommonRoutes.DEVICE_OFF}"
        CommonRoutes.Route.STATUS -> "${device.address}/${CommonRoutes.DEVICE_STATUS}"
    }
}