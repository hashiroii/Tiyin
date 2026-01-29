package kz.hashiroii.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM subscriptions ORDER BY serviceName ASC")
    fun getAllFlow(): Flow<List<SubscriptionRoomEntity>>

    @Query("SELECT * FROM subscriptions WHERE serviceName = :serviceName AND serviceDomain = :serviceDomain LIMIT 1")
    suspend fun getByServiceNameAndDomain(serviceName: String, serviceDomain: String): SubscriptionRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SubscriptionRoomEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<SubscriptionRoomEntity>)

    @Update
    suspend fun update(entity: SubscriptionRoomEntity)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM subscriptions WHERE serviceName = :serviceName AND serviceDomain = :serviceDomain")
    suspend fun deleteByServiceNameAndDomain(serviceName: String, serviceDomain: String)

    @Query("DELETE FROM subscriptions")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(entities: List<SubscriptionRoomEntity>) {
        deleteAll()
        if (entities.isNotEmpty()) {
            insertAll(entities)
        }
    }
}
