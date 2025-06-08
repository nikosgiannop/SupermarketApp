package com.example.supermarkeapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch


class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalCostTextView: TextView
    private lateinit var buttonCheckout: Button
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        val db = AppDatabase.getDatabase(this)  //Απόκτηση πρόσβασης στη Room DB
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        //Αντιστοίχιση UI στοιχείων
        recyclerView = findViewById(R.id.recyclerViewCart)
        totalCostTextView = findViewById(R.id.textViewTotalCost)
        buttonCheckout = findViewById(R.id.buttonCheckout)

        recyclerView.layoutManager = LinearLayoutManager(this)

        //Λήψη αντικειμένων καλαθιού από τον CartManager
        val cartItems = CartManager.getCartItems().toList()
        adapter = CartAdapter(cartItems)
        recyclerView.adapter = adapter

        //Ενημέρωση συνολικού κόστους
        updateTotalCost()

        //Κουμπί checkout
        buttonCheckout.setOnClickListener {
            Toast.makeText(this, getString(R.string.checkout_clicked), Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                //Εισαγωγή παραγγελίας στον πίνακα OrderEntity
                val orderId = db.orderDao().insertOrder(
                    OrderEntity(
                        timestamp = System.currentTimeMillis(), //Χρόνος παραγγελίας
                        totalCost = CartManager.getTotalPrice() //Τελικό ποσό
                    )
                ).toInt()

                //Δημιουργία λίστας προϊόντων της παραγγελίας (OrderItemEntity)
                val orderItems = CartManager.getCartItems().map { (product, qty) ->
                    OrderItemEntity(
                        orderId = orderId,
                        productId = product.id,
                        name_el = product.name_el,
                        name_en = product.name_en,
                        quantity = qty,
                        unitPrice = product.offerPrice ?: product.pricePerUnit,
                        unit_el = product.unit_el,
                        unit_en = product.unit_en,
                        imageName = product.imageName
                    )
                }

                //Εισαγωγή των προϊόντων στο ιστορικό
                db.orderDao().insertOrderItems(orderItems)

                //Ενημέρωση διαθεσιμότητας προϊόντων
                for ((product, qty) in CartManager.getCartItems()) {
                    db.productDao().decreaseAvailability(product.id, qty)
                }

                //Εκκαθάριση καλαθιού μετά την ολοκλήρωση
                CartManager.clearCart()
                Toast.makeText(this@CartActivity, getString(R.string.purchase_completed), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //Υπολογίζει και εμφανίζει το συνολικό κόστος όλων των προιόντων
    private fun updateTotalCost() {
        var total = 0.0
        for ((product, quantity) in CartManager.getCartItems()) {
            total += (product.offerPrice ?: product.pricePerUnit) * quantity
        }
        val totalFormatted = String.format("%.2f", total)
        totalCostTextView.text = getString(R.string.total_cost_format, totalFormatted)
    }

    //Ανανεώνει το περιεχόμενο του RecyclerView και το συνολικό ποσό
    fun updateCartUI() {
        adapter.notifyDataSetChanged()
        updateTotalCost()
    }
}