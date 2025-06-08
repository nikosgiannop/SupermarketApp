package com.example.supermarkeapp
import androidx.room.Entity
import androidx.room.PrimaryKey

//Οντότητα που αναπαριστά μία παραγγελία order στον πίνακα orders
@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,   //Μοναδικό ID παραγγελίας
    val timestamp: Long,                                //Χρόνος καταγραφής παραγγελίας
    val totalCost: Double                               //Συνολικό κόστος της παραγγελίας
)

//Οντότητα που αναπαριστά ένα προιόν μέσα σε μία παραγγελία
//Κάθε γραμμή συνδέει ένα προιόν με μία παραγγελία
@Entity(primaryKeys = ["orderId", "productId"])
data class OrderItemEntity(
    val orderId: Int,
    val productId: Int,
    val name_el: String,
    val name_en: String,
    val quantity: Int,
    val unitPrice: Double,
    val unit_el: String,
    val unit_en: String,
    val imageName: String?
)