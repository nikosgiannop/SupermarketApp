package com.example.supermarkeapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {

    //Επιστρέφει όλες τις παραγγελίες, ταξινομημένες από την πιο πρόσφατη στην πιο παλιά
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    suspend fun getAllOrders(): List<OrderEntity>

    //Επιστρέφει μία συγκεκριμένη παραγγελία βάσει του ID
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: Int): OrderEntity?

    //Επιστρέφει όλα τα προϊόντα που ανήκουν σε μια παραγγελία
    @Query("SELECT * FROM OrderItemEntity WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: Int): List<OrderItemEntity>

    //Εισάγει μία νέα παραγγελία και επιστρέφει το auto-generated ID
    @Insert
    suspend fun insertOrder(order: OrderEntity): Long

    //Εισάγει τη λίστα προιόντων που συνδέονται με μία παραγγελία
    @Insert
    suspend fun insertOrderItems(items: List<OrderItemEntity>)
}