package mk.djakov.smarthome.data.model

data class RelayResponse(
    val log: String,
    val plugin: Int,
    val pin: Int,
    val mode: String,
    val state: Int
)

