package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String,
    val name: String,
    val category: String,
    val size: String,
    val buyPrice: Double,
    val sellPrice: Double,
    val stock: Int,
    val imageUri: String? = null
)
