package com.example.supermarkeapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class WishlistActivity : AppCompatActivity() {

    //RecyclerView για εμφάνιση των προϊόντων της wishlist
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    //Button που εκκαθαρίζει τη wishlist
    private lateinit var buttonClearWishlist: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        recyclerView = findViewById(R.id.recyclerViewWishlist)
        buttonClearWishlist = findViewById(R.id.buttonClearWishlist)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(emptyList())
        recyclerView.adapter = adapter

        //Φόρτωση wishlist μέσα από coroutine
        loadWishlist()

        //Κουμπί εκκαθάρισης λίστας επιθυμιών
        buttonClearWishlist.setOnClickListener {
            lifecycleScope.launch {
                WishlistManager.clearWishlist()

                // Εμφάνιση μηνύματος επιβεβαίωσης στον χρήστη
                Toast.makeText(this@WishlistActivity, getString(R.string.wishlist_cleared), Toast.LENGTH_SHORT).show()

                //Ενημέρωση UI για να φαίνεται η αλλαγή
                updateWishlistUI()
            }
        }
    }

    //Φόρτωση wishlist από databse και ενημέρωση του adapter
    private fun loadWishlist() {
        lifecycleScope.launch {
            val wishlistItems = WishlistManager.getWishlistItems()
            adapter.updateList(wishlistItems)
        }
    }


    //Update UI και ξαναφορτώνει λίστα
    private fun updateWishlistUI() {
        loadWishlist()  //ξαναφορτώνει λίστα
    }
}