package com.example.supermarkeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.util.*
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.lifecycleScope

class HistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        //Αντιστοίχιση RecyclerView από το layout
        recyclerView = findViewById(R.id.recyclerViewHistory)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = AppDatabase.getDatabase(this)

        //Φόρτωση όλων των παραγγελιών από τη βάση σε background thread
        CoroutineScope(Dispatchers.IO).launch {
            val orders = db.orderDao().getAllOrders()
            withContext(Dispatchers.Main) {
                //Ορισμός του adapter με 2 λειτουργίες click:
                adapter = HistoryAdapter(
                    orders,
                    //Αν κάνουμε κλικ σε παραγγελία μας πάει στο OrderDetailActivity
                    onOrderClick = { order ->
                        val intent = Intent(this@HistoryActivity, OrderDetailActivity::class.java)
                        intent.putExtra("orderId", order.id)
                        startActivity(intent)
                    },
                    //Αν κάνουμε κλικ στην επανάληψη κάνει προσθήκη των προιόντων παραγγελίας στο καλάθι
                    onRepeatClick = { order ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val items = db.orderDao().getOrderItems(order.id)

                            var allAvailable = true

                            //Για κάθε προϊόν της παραγγελίας, το βρίσκουμε στη βάση και το προσθέτουμε στο καλάθι
                            items.forEach { item ->
                                val product = db.productDao().getProductById(item.productId)
                                if (product != null) {
                                    if (product.availability <= 0) {
                                        allAvailable = false
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                this@HistoryActivity,
                                                getString(R.string.out_of_stock, product.localizedName()),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        CartManager.addToCart(product, item.quantity)
                                    }
                                }
                            }

                            if (allAvailable) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@HistoryActivity, getString(R.string.order_repeated), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }


                )
                recyclerView.adapter = adapter  //Ορισμός adapter στο RecyclerView
            }
        }
    }
}