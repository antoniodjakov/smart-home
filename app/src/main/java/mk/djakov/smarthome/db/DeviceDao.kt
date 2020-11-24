package mk.djakov.smarthome.db

import androidx.lifecycle.LiveData
import androidx.room.*
import mk.djakov.smarthome.data.model.Device

@Dao
interface DeviceDao {
    @Query("SELECT * FROM device ORDER BY position")
    fun getAllDevices(): LiveData<List<Device>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<Device>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: Device): Long

    @Query("UPDATE device SET state = :state WHERE id = :id")
    suspend fun updateStatus(id: Int, state: Boolean)

    @Query("UPDATE device SET is_loading = :isLoading WHERE id = :id")
    suspend fun setIsLoading(id: Int, isLoading: Boolean)

    @Query("SELECT * FROM device")
    suspend fun getAllDevicesAsync(): List<Device>

    @Query("UPDATE device SET name = :name, address = :address, gpio = :gpio, command = :command, command_status = :deviceStatus, duration = :duration WHERE id = :id")
    suspend fun updateDevice(
        id: Int,
        name: String,
        address: String,
        gpio: Int,
        command: String,
        deviceStatus: Int,
        duration: Int
    )

    @Delete
    suspend fun deleteDevice(device: Device)

    @Query("SELECT * FROM device WHERE id=:id")
    suspend fun getDeviceById(id: Long): Device

    @Query("UPDATE device SET position = :position WHERE id = :id")
    suspend fun updateDevicePosition(id: Int, position: Int)
}