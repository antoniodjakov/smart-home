package mk.djakov.smarthome.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import mk.djakov.smarthome.util.Const
import mk.djakov.smarthome.util.Data

@Entity(tableName = "device")
data class Device(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "gpio") val gpio: Int = Const.GPIO,
    @ColumnInfo(name = "command") val command: String = Data.commands[0],
    @ColumnInfo(name = "command_status") val commandStatus: Int = 1,
    @ColumnInfo(name = "duration") val duration: Int = 0,
    @ColumnInfo(name = "position") val position: Int? = 1,
    @ColumnInfo(name = "state") val state: Boolean = false,
    @ColumnInfo(name = "is_loading") val isLoading: Boolean = false
) {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int? = null
}