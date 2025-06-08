package com.example.supermarkeapp

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class ProductDetailActivity : AppCompatActivity() {

    //Το προιόν που εμφανίζεται στη σελίδα λεπτομερειών
    private var currentProduct: ProductEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        //Ανάκτηση του productId από το Intent
        val productId = intent.getIntExtra("productId", -1)

        //Αν το productId είναι έγκυρο, αναζητούμε το προιόν από τη βάση δεδομένων
        if (productId != -1) {
            val db = AppDatabase.getDatabase(this)
            CoroutineScope(Dispatchers.IO).launch {
                val product = db.productDao().getProductById(productId)
                withContext(Dispatchers.Main) {
                    if (product != null) {
                        currentProduct = product
                        showProductDetails(product) //Εμφάνιση λεπτομερειών προιόντος
                    }
                }
            }
        }

        //Κουμπί επιστροφής
        findViewById<Button>(R.id.buttonBack).setOnClickListener {
            finish()
        }

        //Κουμπί "Προσθήκη στο καλάθι"
        val buttonAddToCart = findViewById<Button>(R.id.buttonAddToCart)
        val editTextQuantity = findViewById<EditText>(R.id.editTextQuantity)

        //Προσθήκη στο καλάθι
        buttonAddToCart.setOnClickListener {
            val quantityText = editTextQuantity.text.toString()
            val quantity = quantityText.toIntOrNull() ?: 1  //Αν το πεδίο είναι κενό ή λάθος, βάζουμε 1

            val product = currentProduct
            if (product == null) {
                Toast.makeText(this, getString(R.string.product_load_error), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //Έλεγχος εγκυρότητας ποσότητας
            if (quantity <= 0) {
                Toast.makeText(this, getString(R.string.invalid_quantity), Toast.LENGTH_SHORT).show()
            } else if (quantity > product.availability) {
                Toast.makeText(this, getString(R.string.quantity_exceeds_stock), Toast.LENGTH_SHORT).show()
            } else {
                //Προσθήκη στο καλάθι και μήνυμα επιβεβαίωσης
                CartManager.addToCart(product, quantity)
                Toast.makeText(this, getString(R.string.added_to_cart), Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Εμφανίζει τα πλήρη στοιχεία του προϊόντος στην UI
    private fun showProductDetails(product: ProductEntity) {
        findViewById<TextView>(R.id.textViewDetailName).text = product.localizedName()
        findViewById<TextView>(R.id.textViewDetailDescription).text = product.localizedDescription()
        findViewById<TextView>(R.id.textViewDetailNutrition).text = product.localizedNutrition()
        findViewById<TextView>(R.id.textViewDetailIngredients).text = product.localizedIngredients()

        //Εμφάνιση εικόνας προϊόντος
        val imageId = resources.getIdentifier(product.imageName, "drawable", packageName)
        findViewById<ImageView>(R.id.imageViewDetailProduct).setImageResource(imageId)

        val priceTextView = findViewById<TextView>(R.id.textViewDetailPrice)
        val discountTag = findViewById<TextView>(R.id.textViewDiscountTag)

        val unit = product.localizedUnit()
        //Αν υπάρχει προσφορά, δείχνει την νέα τιμή με κόκκινο και την παλιά διαγραμμένη
        if (product.offerPrice != null) {
            // Τιμή προσφοράς (κόκκινη), διαγραμμένη αρχική τιμή
            priceTextView.text = "${product.offerPrice} $unit  "
            priceTextView.setTextColor(Color.RED)

            val oldPrice = SpannableString("${product.pricePerUnit} $unit").apply {
                setSpan(StrikethroughSpan(), 0, length, 0)
            }
            priceTextView.append(oldPrice)

            discountTag.visibility = TextView.VISIBLE
        } else {
            //Αν δεν υπάρχει προσφορά, απλώς δείχνει την κανονική τιμή
            priceTextView.text = "${product.pricePerUnit} $unit"
            priceTextView.setTextColor(Color.BLACK)
            discountTag.visibility = TextView.GONE
        }
        //Εμφάνιση διαθεσιμότητας
        findViewById<TextView>(R.id.textViewDetailAvailability).text = product.localizedAvailabilityCount()
    }
}