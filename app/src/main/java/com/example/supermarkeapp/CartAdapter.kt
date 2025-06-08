package com.example.supermarkeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

//Adapter για το καλάθι αγορών – εμφανίζει προϊόντα με όνομα, τιμή, ποσότητα, σύνολο και κουμπί αφαίρεσης
class CartAdapter(private val cartItems: List<Pair<ProductEntity, Int>>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    //ViewHolder που αντιστοιχεί στα στοιχεία του layout item_cart.xml
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.imageViewCartProduct)
        val productName: TextView = view.findViewById(R.id.textViewCartProductName)
        val unitPriceText: TextView = view.findViewById(R.id.textViewCartUnitPrice)
        val quantityText: TextView = view.findViewById(R.id.textViewCartQuantity)
        val totalPriceText: TextView = view.findViewById(R.id.textViewCartTotalPrice)
        val removeButton: Button = view.findViewById(R.id.buttonRemoveItem)
    }

    //Δημιουργεί νέο ViewHolder για κάθε προϊόν στο καλάθι
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    //Επιστρέφει τον αριθμό των προϊόντων στο καλάθι
    override fun getItemCount(): Int = cartItems.size

    //Δεσμεύει τα δεδομένα του κάθε προϊόντος με τα στοιχεία του UI
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (product, quantity) = cartItems[position]
        val unit = product.localizedUnit()

        //Τιμή μονάδας (προσφορά ή κανονική)
        val unitPrice = product.offerPrice ?: product.pricePerUnit

        //Συνολικό κόστος για την ποσότητα
        val total = unitPrice * quantity

        holder.productName.text = product.localizedName()

        //Τιμή μονάδας (π.χ. 2.30 €/kg)
        holder.unitPriceText.text = String.format("%.2f €/ %s", unitPrice, unit)
        holder.quantityText.text = "×$quantity"
        holder.totalPriceText.text = String.format("Σύνολο: %.2f €", total)

        //Φόρτωση εικόνας από το drawable (ή fallback)
        val imageResId = holder.itemView.context.resources.getIdentifier(
            product.imageName,
            "drawable",
            holder.itemView.context.packageName
        )
        holder.productImage.setImageResource(
            if (imageResId != 0) imageResId else R.drawable.ic_launcher_background
        )

        //Localization του κουμπιού "Αφαίρεση"
        holder.removeButton.text = if (Locale.getDefault().language == "el") "Αφαίρεση" else "Remove"

        //Click listener για αφαίρεση του προϊόντος από το καλάθι
        holder.removeButton.setOnClickListener {
            CartManager.removeFromCart(product) //Αφαίρεση από singleton καλάθι
            notifyItemRemoved(position)         //Ενημέρωση RecyclerView

            //Αν το context είναι CartActivity, ενημερώνει και το συνολικό κόστος
            (holder.itemView.context as? CartActivity)?.updateCartUI()
        }
    }
}