package kz.hashiroii.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SubscriptionRoomEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TiyinDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
}
