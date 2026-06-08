package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleRecordDao {
    @Query("SELECT * FROM sale_records ORDER BY timestamp DESC")
    fun getAllSales(): Flow<List<SaleRecord>>

    @Query("SELECT * FROM sale_records WHERE id = :id")
    suspend fun getSaleById(id: Int): SaleRecord?

    @Query("SELECT * FROM sale_records WHERE timestamp >= :startTime")
    fun getSalesSince(startTime: Long): Flow<List<SaleRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: SaleRecord)

    @Delete
    suspend fun deleteSale(sale: SaleRecord)
}
