package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sale_records")
data class SaleRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val itemsJson: String, // JSON representation of sold items (name, quantity, sellPrice, buyPrice)
    val totalAmount: Double,
    val discountAmount: Double,
    val netAmount: Double,
    val profitAmount: Double, // netAmount - total buyPrice of items
    val paymentMethod: String, // bKash, Nagad, Rocket, Cash
    val paymentDetails: String? = null, // Rx ID, Account number
    val cashierName: String // "Salesman" or "Owner"
)
