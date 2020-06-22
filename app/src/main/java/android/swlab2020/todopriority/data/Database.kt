package android.swlab2020.todopriority.data

import android.content.Context
import androidx.room.*

enum class Status {
    SUCCESS, FAIL, IN_PROGRESS
}

class StatusConverters {
    @TypeConverter
    fun fromString(value: String): Status? {
        return value.let { Status.valueOf(value) }
    }

    @TypeConverter
    fun toString(value: Status): String? {
        return value.let { value.name }
    }
}

@Database(entities = [Project::class, Task::class], version = 2, exportSchema = false)
@TypeConverters(StatusConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            } else {
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                    return instance
                }
            }
        }
    }
}