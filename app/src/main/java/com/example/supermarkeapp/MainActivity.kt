package com.example.supermarkeapp

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.util.*
import android.content.Intent
import android.content.res.Configuration

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var spinnerCategory: Spinner
    private lateinit var editTextMinPrice: EditText
    private lateinit var editTextMaxPrice: EditText
    private lateinit var checkBoxOffer: CheckBox
    private lateinit var checkBoxAvailability: CheckBox

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var db: AppDatabase
    private var originalProducts: List<ProductEntity> = listOf()     //Η αρχική λίστα προϊόντων, πριν την εφαρμογή φίλτρων

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Αντιστοίχιση των στοιχείων του layout με μεταβλητές
        searchView = findViewById(R.id.searchView)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        editTextMinPrice = findViewById(R.id.editTextMinPrice)
        editTextMaxPrice = findViewById(R.id.editTextMaxPrice)
        checkBoxOffer = findViewById(R.id.checkBoxOffer)
        checkBoxAvailability = findViewById(R.id.checkBoxAvailability)
        recyclerView = findViewById(R.id.recyclerViewProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getDatabase(this)  //Απόκτηση instance της βάσης δεδομένων

        WishlistManager.init(db)

        //Φόρτωση των προϊόντων σε background thread
        CoroutineScope(Dispatchers.IO).launch {
            val products = db.productDao().getAll()
            originalProducts = products
            //Εξαγωγή των διαθέσιμων κατηγοριών για το Spinner
            val categoryList = products.map { it.localizedCategory() }.distinct().sorted()

            withContext(Dispatchers.Main) {
                //Δημιουργία adapter και σύνδεσή του με το RecyclerView
                adapter = ProductAdapter(products)
                recyclerView.adapter = adapter

                //Προετοιμασία του Spinner για επιλογή κατηγορίας
                val allLabel = getLocalizedLabel("filter_category_all")
                val categoryAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, listOf(allLabel) + categoryList)
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = categoryAdapter

                //Localized ετικέτες και Hints
                checkBoxOffer.text = getLocalizedLabel("filter_offer")
                checkBoxAvailability.text = getLocalizedLabel("filter_availability")
                editTextMinPrice.hint = if (Locale.getDefault().language == "el") "Ελάχ. τιμή" else "Min price"
                editTextMaxPrice.hint = if (Locale.getDefault().language == "el") "Μέγ. τιμή" else "Max price"


                //Listeners για τα φίλτρα
                setupListeners()
            }
        }
        //Μεταφορά στην οθόνη του καλαθιού
        findViewById<ImageButton>(R.id.buttonCart).setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.buttonWishlist).setOnClickListener {
            val intent = Intent(this, WishlistActivity::class.java)
            startActivity(intent)
        }

        //Μεταφορά στην οθόνη του ιστορικού αγορών
        findViewById<ImageButton>(R.id.buttonHistory).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        val buttonLanguage = findViewById<Button>(R.id.buttonLanguage)

        //Listener για κουμπί αλλαγής γλώσσας
        buttonLanguage.setOnClickListener {
            val currentLang = Locale.getDefault().language
            val newLocale = if (currentLang == "el") Locale("en") else Locale("el")
            setLocale(this, newLocale)
            recreate() //Επανεκκινεί το activity για να εφαρμοστεί η νέα γλώσσα
        }
    }

    //Ορίζει listeners για όλα τα φίλτρα και αναζητήσεις
    private fun setupListeners() {
        val triggerFilter = {

            //Λήψη τιμών φίλτρων
            val query = searchView.query.toString()
            val selectedCategory = spinnerCategory.selectedItem.toString()
            val minPrice = editTextMinPrice.text.toString().toDoubleOrNull()
            val maxPrice = editTextMaxPrice.text.toString().toDoubleOrNull()
            val offerOnly = checkBoxOffer.isChecked
            val availabilityRequired = checkBoxAvailability.isChecked
            val allLabel = getLocalizedLabel("filter_category_all")

            //Εφαρμογή φίλτρων στη λίστα
            val filtered = originalProducts.filter { product ->
                product.matchesQuery(query)
                        && product.isInPriceRange(minPrice, maxPrice)
                        && (!offerOnly || product.isOnOffer())
                        && (selectedCategory == allLabel || product.localizedCategory() == selectedCategory)
                        && product.matchesAvailability(availabilityRequired)
            }

            //Ενημέρωση adapter με τα φιλτραρισμένα προϊόντα
            adapter.updateList(filtered)
        }
        //Όταν αλλάζει το κείμενο στο search
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                triggerFilter()
                return true
            }
        })
        //Όταν επιλέγεται νέα κατηγορία
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                triggerFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        //Όταν αλλάζει η ελάχιστη ή μέγιστη τιμή
        editTextMinPrice.setOnEditorActionListener { _, _, _ ->
            triggerFilter()
            false
        }

        editTextMaxPrice.setOnEditorActionListener { _, _, _ ->
            triggerFilter()
            false
        }
        // Όταν επιλέγεται ή αποεπιλέγεται η προσφορά
        checkBoxOffer.setOnCheckedChangeListener { _, _ ->
            triggerFilter()
        }
        // Όταν επιλέγεται ή αποεπιλέγεται η διαθεσιμότητα
        checkBoxAvailability.setOnCheckedChangeListener { _, _ ->
            triggerFilter()
        }
    }

    // Επιστρέφει localized ετικέτα βάσει της γλώσσας
    fun getLocalizedLabel(key: String): String {
        return when (key) {
            "filter_category" -> if (Locale.getDefault().language == "el") "Κατηγορία" else "Category"
            "filter_price" -> if (Locale.getDefault().language == "el") "Τιμή" else "Price"
            "filter_offer" -> if (Locale.getDefault().language == "el") "Μόνο με προσφορά" else "Only on offer"
            "filter_availability" -> if (Locale.getDefault().language == "el") "Διαθεσιμότητα" else "Availability"
            "filter_category_all" -> if (Locale.getDefault().language == "el") "Όλες" else "All"
            else -> key
        }
    }

    //Ελέγχει αν το προιόν είναι διαθέσιμο (διαθέσιμες ποσότητες > 0)
    fun ProductEntity.matchesAvailability(required: Boolean): Boolean {
        return !required || availability > 0
    }

    private fun setLocale(context: Context, locale: Locale) {
        Locale.setDefault(locale)
        val resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}
