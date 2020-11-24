package mk.djakov.smarthome.util

object CommonRoutes {

    fun deviceOn(gpio: Int) = "control?cmd=GPIO,$gpio,1"

    fun deviceOff(gpio: Int) = "control?cmd=GPIO,$gpio,0"

    fun pulse(command: String, gpio: Int, state: String, duration: String) =
        "control?cmd=$command,$gpio,$state,$duration"

    fun deviceStatus(gpio: Int) = "control?cmd=status,gpio,$gpio"

    enum class Route {
        DEVICE_ON,
        DEVICE_OFF,
        PULSE,
        STATUS
    }
}