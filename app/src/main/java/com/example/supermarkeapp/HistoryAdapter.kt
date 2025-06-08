package com.example.supermarkeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

//Adapter για το RecyclerView που εμφανίζει παραγγελίες στο ιστορικό
class HistoryAdapter(
    private val orders: List<OrderEntity>,  //Λίστα με όλες τις παραγγελίες
    private val onOrderClick: (OrderEntity) -> Unit = {},   //Callback όταν ο χρήστης κάνει click σε μια παραγγελία
    private val onRepeatClick: (OrderEntity) -> Unit = {}   //Callback όταν ο χρήστης επιλέγει "Επανάληψη"
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    //ViewHolder για το layout item_order.xml
    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewOrderDate: TextView = view.findViewById(R.id.textViewOrderDate)
        val textViewOrderTotal: TextView = view.findViewById(R.id.textViewOrderTotal)
        val buttonRepeatOrder: Button = view.findViewById(R.id.buttonRepeatOrder)
    }

    //Δημιουργεί νέο ViewHolder για κάθε στοιχείο της λίστας
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return HistoryViewHolder(view)
    }

    //Συνδέει δεδομένα παραγγελίας με τα στοιχεία του UI
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val order = orders[position]

        //Μετατροπή timestamp σε αναγνώσιμη ημερομηνία
        val date = Date(order.timestamp)
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        //Εμφάνιση ημερομηνίας και συνολικού κόστους
        holder.textViewOrderDate.text = formatter.format(date)
        holder.textViewOrderTotal.text = "%.2f €".format(order.totalCost)

        //Αν κλικαρουμε το στοιχείο θα μας πάει στις λεπτομέρειες
        holder.itemView.setOnClickListener {
            onOrderClick(order)
        }

        //Αν κάνουμε κλικ στην επανάληψη γίνεται προσθήκη στο καλάθι
        holder.buttonRepeatOrder.setOnClickListener {
            onRepeatClick(order)
        }
    }

    //Επιστρέφει τον αριθμό των παραγγελιών
    override fun getItemCount(): Int = orders.size
}