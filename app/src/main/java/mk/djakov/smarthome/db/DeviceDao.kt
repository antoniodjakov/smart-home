package mk.djakov.smarthome.db

import androidx.lifecycle.LiveData
import androidx.room.*
import mk.djakov.smarthome.data.model.Device

@Dao
interface DeviceDao {
    @Query("SELECT * FROM device")
    fun getAllDevices(): LiveData<List<Device>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<Device>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: Device)

    @Query("UPDATE device SET state = :state WHERE id = :id")
    suspend fun updateStatus(id: Int, state: Boolean)

    @Query("UPDATE device SET is_loading = :isLoading WHERE id = :id")
    suspend fun setIsLoading(id: Int, isLoading: Boolean)

    @Query("SELECT * FROM device")
    suspend fun getAllDevicesAsync(): List<Device>

    @Query("UPDATE device SET name = :name, address = :address WHERE id = :id")
    suspend fun updateDevice(id: Int, name: String, address: String)

    @Delete
    suspend fun deleteDevice(device: Device)
}