package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_profile")
data class ShopProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Amar Boutique",
    val title: String = "Quality Fashion & Lifestyle",
    val phone: String = "01700000000",
    val address: String = "Basundhara City Shopping Mall, Dhaka",
    val logoText: String = "AB", // Lettering/text initials to draw as logo
    val footerGreeting: String = "Thank you for shopping with us!"
)
