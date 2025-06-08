package com.example.supermarkeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Paint
import androidx.core.content.ContextCompat
import android.content.Intent

//Adapter που συνδέει τη λίστα προιόντων με το RecyclerView
class ProductAdapter(private var productList: List<ProductEntity>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    //Ενημερώνει τη λίστα προιόντων και κάνει ανανέωση της εμφάνισης
    fun updateList(newList: List<ProductEntity>) {
        productList = newList
        notifyDataSetChanged()
    }
    //ViewHolder ορίζει τις συνδέσεις μεταξύ του layout item_product.xml και των μεταβλητών του προιόντος
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewProductName)
        val textViewOriginalPrice: TextView = itemView.findViewById(R.id.textViewOriginalPrice)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val imageViewProduct: ImageView = itemView.findViewById(R.id.imageViewProduct)
        val textViewAvailability: TextView = itemView.findViewById(R.id.textViewAvailability)
    }

    //Δημιουργεί ένα νέο ViewHolder από το XML layout κάθε φορά που χρειάζεται νέα γραμμή στη λίστα
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    //Δεσμεύει τα δεδομένα του προιόντος στα στοιχεία του layout
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        //Εμφάνιση localized ονόματος προιόντος
        holder.textViewName.text = product.localizedName()

        //Υπολογισμός και εμφάνιση τιμής προσφοράς (αν υπάρχει) με localized μονάδα
        holder.textViewPrice.text = "€${String.format("%.2f", product.offerPrice ?: product.pricePerUnit)} ${product.localizedUnit()}"

        //Αν υπάρχει προσφορά, εμφανίζεται η αρχική τιμή διαγραμμένη
        val originalPrice = product.originalPriceOrNull()
        if (product.offerPrice != null) {
            holder.textViewOriginalPrice.visibility = View.VISIBLE
            holder.textViewOriginalPrice.text = "€${String.format("%.2f", product.pricePerUnit)}"
            holder.textViewOriginalPrice.paintFlags = holder.textViewOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            //Η προσφορά εμφανίζεται με κόκκινο χρώμα
            holder.textViewPrice.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
        } else {
            //Αν δεν υπάρχει προσφορά, κρύβουμε την αρχική τιμή και επαναφέρουμε το χρώμα
            holder.textViewOriginalPrice.visibility = View.GONE
            holder.textViewPrice.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
        }

        //Αντιστοίχιση εικόνας από το drawable folder με βάση το imageName
        product.imageName?.let {
            val resId = holder.itemView.context.resources.getIdentifier(
                it, "drawable", holder.itemView.context.packageName
            )
            if (resId != 0) {
                holder.imageViewProduct.setImageResource(resId)
            } else {
                //Εικόνα fallback σε περίπτωση που δεν βρεθεί το resId (exception handling)
                holder.imageViewProduct.setImageResource(R.drawable.ic_launcher_background)
            }
        }

        //Click listener για άνοιγμα της οθόνης λεπτομερειών του προιόντος
        holder.itemView.setOnClickListener {    //click listener για μεταβαση σε activity του προιοντος
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailActivity::class.java)
            intent.putExtra("productId", product.id)
            context.startActivity(intent)
        }

        //Εμφάνιση της localized διαθεσιμότητας
        holder.textViewAvailability.text = product.localizedAvailability()
    }

    override fun getItemCount(): Int = productList.size

}

