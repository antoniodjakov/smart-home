package mk.djakov.smarthome.util

import mk.djakov.smarthome.data.model.Device

object Data {
    val devices = arrayListOf(
        Device("Kitchen light", Const.DEVICE_ONE, Credentials.BASE_URL_1),
        Device("Heater", Const.DEVICE_TWO, Credentials.BASE_URL_2)
    )
}