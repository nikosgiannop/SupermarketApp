package com.example.supermarkeapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//DAO (Data Access Object) για την οντότητα ProductEntity
@Dao
interface ProductDao {

    //Εισάγει ή ενημερώνει ένα προιόν (αν υπάρχει ήδη με το ίδιο ID)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    //Επιστρέφει όλα τα προιόντα από τον πίνακα products
    @Query("SELECT * FROM products")
    suspend fun getAll(): List<ProductEntity>

    //Διαγράφει ένα προιόν από τη βάση
    @Delete
    suspend fun delete(product: ProductEntity)

    //Επιστρέφει ένα προιόν με βάση το ID (ή null αν δεν βρεθεί)
    @Query("SELECT * FROM products WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Int): ProductEntity?

    //Μειώνει τη διαθεσιμότητα του προιόντος κατά την ποσότητα που αγοράστηκε μόνο αν υπάρχει επαρκές απόθεμα
    @Query("UPDATE products SET availability = availability - :quantity WHERE id = :productId AND availability >= :quantity")
    suspend fun decreaseAvailability(productId: Int, quantity: Int)
}
