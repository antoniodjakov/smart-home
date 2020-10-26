package mk.djakov.smarthome.util

object CommonRoutes {
    const val DEVICE_ON = "control?cmd=GPIO,12,1"
    const val DEVICE_OFF = "control?cmd=GPIO,12,0"
    const val DEVICE_STATUS = "control?cmd=status,gpio,12"


    enum class Route {
        DEVICE_ON,
        DEVICE_OFF,
        STATUS
    }
}