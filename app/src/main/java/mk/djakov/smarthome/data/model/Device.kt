package mk.djakov.smarthome.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device")
data class Device(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "state") val state: Boolean = false,
    @ColumnInfo(name = "is_loading") val isLoading: Boolean = false
) {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int? = null
}