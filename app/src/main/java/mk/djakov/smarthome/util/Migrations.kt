package mk.djakov.smarthome.util

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_5_6 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE device ADD COLUMN command TEXT NOT NULL DEFAULT 'GPIO'")
        database.execSQL("ALTER TABLE device ADD COLUMN command_status INT NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE device ADD COLUMN duration INT NOT NULL DEFAULT 0")
    }
}
