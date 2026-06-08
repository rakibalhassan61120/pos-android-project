package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopProfileDao {
    @Query("SELECT * FROM shop_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<ShopProfile?>

    @Query("SELECT * FROM shop_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileDirect(): ShopProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: ShopProfile)
}
