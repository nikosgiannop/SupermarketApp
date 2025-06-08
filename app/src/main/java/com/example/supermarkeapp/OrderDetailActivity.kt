package com.example.supermarkeapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailActivity : AppCompatActivity() {

    //UI στοιχεία για εμφάνιση προιόντων και λεπτομερειών παραγγελίας
    private lateinit var recyclerView: RecyclerView
    private lateinit var textViewDate: TextView
    private lateinit var textViewTotal: TextView
    private lateinit var adapter: OrderDetailAdapter

    //Βάση δεδομένων (singleton Room DB)
    val db = AppDatabase.getDatabase(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        //Ανάκτηση του orderId από το intent
        val orderId = intent.getIntExtra("orderId", -1)

        //Αντιστοίχιση UI στοιχείων με τα views
        recyclerView = findViewById(R.id.recyclerViewOrderItems)
        textViewDate = findViewById(R.id.textViewOrderDetailDate)
        textViewTotal = findViewById(R.id.textViewOrderDetailTotal)

        recyclerView.layoutManager = LinearLayoutManager(this)


        //Αν είναι έγκυρο το orderId, ανακτούμε τα δεδομένα από τη βάση
        if (orderId != -1) {
            val db = AppDatabase.getDatabase(this)

            CoroutineScope(Dispatchers.IO).launch {
                val items = db.orderDao().getOrderItems(orderId)
                val order = db.orderDao().getOrderById(orderId)

                withContext(Dispatchers.Main) {

                    //Ρύθμιση του adapter με τα προιόντα της παραγγελίας
                    val adapter = OrderDetailAdapter(items)
                    recyclerView.adapter = adapter

                    if (order != null) {
                        //Εμφάνιση της ημερομηνίας παραγγελίας με format "yyyy-MM-dd HH:mm"
                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val date = formatter.format(Date(order.timestamp))
                        textViewDate.text = date
                    }
                }
            }
        }
    }
}