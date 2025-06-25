package com.example.supermarkeapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object WishlistManager {

    //Η βάση δεδομένων πρέπει να αρχικοποιηθεί στην εκκίνηση της εφαρμογής
    private lateinit var db: AppDatabase


    fun init(database: AppDatabase) {
        db = database
    }

    //Προσθέτει προιόν σε wishlist
    suspend fun addToWishlist(productId: Int) {
        withContext(Dispatchers.IO) {
            db.wishlistDao().addToWishlist(WishlistEntity(productId))
        }
    }

    //Αφαιρεί προιόν απο wishlist
    suspend fun removeFromWishlist(productId: Int) {
        withContext(Dispatchers.IO) {
            db.wishlistDao().removeFromWishlist(productId)
        }
    }

    //Αδειάζει wishlist
    suspend fun clearWishlist() {
        withContext(Dispatchers.IO) {
            db.wishlistDao().clearWishlist()
        }
    }

    //Παίρνει όλα τα προιόντα απο wishlist
    suspend fun getWishlistItems(): List<ProductEntity> {
        return withContext(Dispatchers.IO) {
            db.wishlistDao().getAllWishlistProducts()
        }
    }

    //Έλεγχος αν υπάρχει το προιόν στη wishlist
    suspend fun isInWishlist(productId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            db.wishlistDao().isInWishlist(productId)
        }
    }
}