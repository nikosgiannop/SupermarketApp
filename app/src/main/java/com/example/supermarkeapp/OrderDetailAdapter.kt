package com.example.supermarkeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

//Adapter για την εμφάνιση των προιόντων μίας παραγγελίας στο RecyclerView
class OrderDetailAdapter(private val items: List<OrderItemEntity>) :
    RecyclerView.Adapter<OrderDetailAdapter.OrderItemViewHolder>() {

    //ViewHolder που αντιστοιχεί στα στοιχεία του layout item_cart.xml
    class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewProduct: ImageView = view.findViewById(R.id.imageViewCartProduct)
        val textViewName: TextView = view.findViewById(R.id.textViewCartProductName)
        val textViewUnitPrice: TextView = view.findViewById(R.id.textViewCartUnitPrice)
        val textViewQuantity: TextView = view.findViewById(R.id.textViewCartQuantity)
        val textViewTotal: TextView = view.findViewById(R.id.textViewCartTotalPrice)
    }


    //Δημιουργεί νέο ViewHolder από το layout item_cart
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return OrderItemViewHolder(view)
    }

    //Συνδέει τα δεδομένα του OrderItemEntity με το UI
    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]

        //Επιλογή γλώσσας συσκευής για localization
        val currentLocale = Locale.getDefault().language
        val name = if (currentLocale == "el") item.name_el else item.name_en
        val unit = if (currentLocale == "el") item.unit_el else item.unit_en
        val total = item.unitPrice * item.quantity

        //Όνομα προϊόντος
        holder.textViewName.text = name

        //Ποσότητα
        holder.textViewQuantity.text = "x${item.quantity}"

        //Μονάδα τιμής (π.χ. 1.50 €/L ή €/λίτρο)
        holder.textViewUnitPrice.text = "%.2f %s".format(item.unitPrice, unit)

        //Σύνολο
        val totalLabel = if (currentLocale == "el") "Σύνολο" else "Total"
        holder.textViewTotal.text = "$totalLabel: %.2f €".format(total)

        //Εικόνα
        if (!item.imageName.isNullOrEmpty()) {
            val imageResId = holder.itemView.context.resources.getIdentifier(
                item.imageName, "drawable", holder.itemView.context.packageName
            )
            if (imageResId != 0) {
                holder.imageViewProduct.setImageResource(imageResId)
            }
        }
    }
    //Επιστρέφει τον αριθμό των στοιχείων της λίστας
    override fun getItemCount(): Int = items.size
}