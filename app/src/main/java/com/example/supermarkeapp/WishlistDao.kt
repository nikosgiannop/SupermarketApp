package com.example.supermarkeapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface WishlistDao {

    //Προσθέτει ένα νέο προϊόν στη wishlist
    @Insert
    suspend fun addToWishlist(item: WishlistEntity)

    //Διαγράφει ένα προϊόν από τη wishlist
    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun removeFromWishlist(productId: Int)

    //Πάει λέγοντας...
    @Query("SELECT productId FROM wishlist")
    suspend fun getAllWishlistProductIds(): List<Int>

    @Query("SELECT * FROM products WHERE id IN (SELECT productId FROM wishlist)")
    suspend fun getAllWishlistProducts(): List<ProductEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :productId)")
    suspend fun isInWishlist(productId: Int): Boolean

    @Query("DELETE FROM wishlist")
    suspend fun clearWishlist()
}