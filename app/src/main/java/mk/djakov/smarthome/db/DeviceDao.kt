package mk.djakov.smarthome.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import mk.djakov.smarthome.data.model.Device

@Dao
interface DeviceDao {
    @Query("SELECT * FROM device")
    fun getAllDevices(): LiveData<List<Device>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<Device>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: Device)

    @Query("UPDATE device SET state = :state WHERE tag = :name")
    suspend fun updateStatus(name: String, state: Boolean)

    @Query("UPDATE device SET is_loading = :isLoading WHERE tag = :name")
    suspend fun setIsLoading(name: String, isLoading: Boolean)

    @Query("SELECT * FROM device")
    suspend fun getAllDevicesAsync(): List<Device>

}