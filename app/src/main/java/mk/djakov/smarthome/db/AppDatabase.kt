package mk.djakov.smarthome.db

import androidx.room.Database
import androidx.room.RoomDatabase
import mk.djakov.smarthome.data.model.Device

@Database(
    entities = [Device::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}