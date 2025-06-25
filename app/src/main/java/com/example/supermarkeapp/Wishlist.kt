package com.example.supermarkeapp
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

@Entity(
    tableName = "wishlist",
    primaryKeys = ["productId"]
)
data class WishlistEntity(
    val productId: Int
)