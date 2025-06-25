package com.example.supermarkeapp
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//Ορισμός της βάσης δεδομένων Room για την εφαρμογή.
//Περιλαμβάνει τις οντότητες ProductEntity, OrderEntity, OrderItemEntity
@Database(entities = [ProductEntity::class,  OrderEntity::class, OrderItemEntity::class,  WishlistEntity::class], version = 10)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao   //Παρέχει το DAO για τα προϊόντα
    abstract fun productDao(): ProductDao   //Παρέχει το DAO για τις παραγγελίες
    abstract fun wishlistDao(): WishlistDao //Παρέχει το DAO για το wishlist

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null //Singleton instance της βάσης για αποφυγή πολλαπλών αρχικοποιήσεων

        fun getDatabase(context: Context): AppDatabase {    //Επιστρέφει την instance της βάσης δεδομένων ή την δημιουργεί αν δεν υπάρχει.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "supermarket_database"
                )
                    .addCallback(object : RoomDatabase.Callback() { //Callback κατά τη δημιουργία της βάσης για να εισαχθούν αρχικά δεδομένα
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            CoroutineScope(Dispatchers.IO).launch {  //Εκτελεί εισαγωγή των αρχικών προϊόντων σε background thread
                                getDatabase(context).productDao().apply {
                                    insert( //Εισαγωγή προϊόντων με τιμές στα ελληνικά και αγγλικά, διατροφικά στοιχεία, τιμές, προσφορές και διαθεσιμότητα.
                                        ProductEntity(
                                            name_el = "Γάλα",
                                            name_en = "Milk",
                                            category_el = "Γαλακτοκομικά",
                                            category_en = "Dairy",
                                            description_el = "Φρέσκο γάλα 1lt",
                                            description_en = "Fresh milk 1lt",
                                            pricePerUnit = 1.50,
                                            unit_el = "€/λίτρο",
                                            unit_en = "€/L",
                                            imageName = "milk",
                                            nutrition_el = "Θερμίδες: 64 kcal\nΠρωτεΐνη: 3.4g\nΥδατάνθρακες: 5g\nΛιπαρά: 3.6g\nΑσβέστιο: 120mg\nΑλάτι: 0.1g",
                                            nutrition_en = "Calories: 64 kcal\nProtein: 3.4g\nCarbohydrates: 5g\nFat: 3.6g\nCalcium: 120mg\nSalt: 0.1g",
                                            ingredients_el = "Γάλα",
                                            ingredients_en = "Milk",
                                            offerPrice = null,
                                            availability = 26

                                        )
                                    )
                                    insert(
                                        ProductEntity(
                                            name_el = "Ψωμί",
                                            name_en = "Bread",
                                            category_el = "Αρτοποιείο",
                                            category_en = "Bakery",
                                            description_el = "Φρέσκο ψωμί ημέρας",
                                            description_en = "Fresh daily bread",
                                            pricePerUnit = 0.90,
                                            unit_el = "€/τεμάχιο",
                                            unit_en = "€/each",
                                            imageName = "bread",
                                            nutrition_el = "Θερμίδες: 265 kcal\nΠρωτεΐνη: 9g\nΥδατάνθρακες: 49g\nΛιπαρά: 3g\nΦυτικές Ίνες: 2.7g\nΑλάτι: 0.8g",
                                            nutrition_en = "Calories: 265 kcal\nProtein: 9g\nCarbohydrates: 49g\nFat: 3g\nFiber: 2.7g\nSalt: 0.8g",
                                            ingredients_el = "Αλεύρι, νερό, μαγιά",
                                            ingredients_en = "Flour, water, yeast",
                                            offerPrice = null,
                                            availability = 12
                                        )
                                    )
                                    insert(
                                        ProductEntity(
                                            name_el = "Μακαρόνια",
                                            name_en = "Pasta",
                                            category_el = "Τρόφιμα",
                                            category_en = "Food",
                                            description_el = "500γρ Σπαγγέτι",
                                            description_en = "500g Spaghetti",
                                            pricePerUnit = 1.20,
                                            unit_el = "€/πακέτο",
                                            unit_en = "€/pack",
                                            imageName = "pasta",
                                            nutrition_el = "Θερμίδες: 350 kcal\nΠρωτεΐνη: 12g\nΥδατάνθρακες: 70g\nΛιπαρά: 1.5g\nΦυτικές Ίνες: 3g\nΑλάτι: 0.01g",
                                            nutrition_en = "Calories: 350 kcal\nProtein: 12g\nCarbohydrates: 70g\nFat: 1.5g\nFiber: 3g\nSalt: 0.01g",
                                            ingredients_el = "Σιμιγδάλι σκληρού σίτου",
                                            ingredients_en = "Durum wheat semolina",
                                            offerPrice = 0.99,
                                            availability = 45
                                        )
                                    )
                                    insert(
                                        ProductEntity(
                                            name_el = "Μήλα",
                                            name_en = "Apples",
                                            category_el = "Φρέσκα Τρόφιμα",
                                            category_en = "Fresh Food",
                                            description_el = "Κόκκινα μήλα ανά κιλό",
                                            description_en = "Red apples per kilo",
                                            pricePerUnit = 2.30,
                                            unit_el = "€/κιλό",
                                            unit_en = "€/kg",
                                            imageName = "apple",
                                            nutrition_el = "Θερμίδες: 52 kcal\nΠρωτεΐνη: 0.3g\nΥδατάνθρακες: 14g\nΛιπαρά: 0.2g\nΦυτικές Ίνες: 2.4g\nΒιταμίνη C: 7mg",
                                            nutrition_en = "Calories: 52 kcal\nProtein: 0.3g\nCarbohydrates: 14g\nFat: 0.2g\nFiber: 2.4g\nVitamin C: 7mg",
                                            ingredients_el = "Μήλα",
                                            ingredients_en = "Apples",
                                            offerPrice = null,
                                            availability = 21
                                        )
                                    )
                                    insert(
                                        ProductEntity(
                                            name_el = "Μπανάνες",
                                            name_en = "Bananas",
                                            category_el = "Φρέσκα Τρόφιμα",
                                            category_en = "Fresh Food",
                                            description_el = "Μπανάνες εισαγωγής ανά κιλό",
                                            description_en = "Imported bananas per kilo",
                                            pricePerUnit = 1.80,
                                            unit_el = "€/κιλό",
                                            unit_en = "€/kg",
                                            imageName = "banana",
                                            nutrition_el = "Θερμίδες: 89 kcal\nΠρωτεΐνη: 1.1g\nΥδατάνθρακες: 23g\nΛιπαρά: 0.3g\nΚάλιο: 358mg\nΦυτικές Ίνες: 2.6g",
                                            nutrition_en = "Calories: 89 kcal\nProtein: 1.1g\nCarbohydrates: 23g\nFat: 0.3g\nPotassium: 358mg\nFiber: 2.6g",
                                            ingredients_el = "Μπανάνες",
                                            ingredients_en = "Bananas",
                                            offerPrice = null,
                                            availability = 0
                                        )
                                    )

                                    insert(
                                        ProductEntity(
                                            name_el = "Απορρυπαντικό ρούχων",
                                            name_en = "Laundry Detergent",
                                            category_el = "Καθαριστικά",
                                            category_en = "Cleaning",
                                            description_el = "Υγρό απορρυπαντικό 2L",
                                            description_en = "Liquid detergent 2L",
                                            pricePerUnit = 5.50,
                                            unit_el = "€/τεμάχιο",
                                            unit_en = "€/each",
                                            imageName = "detergent",
                                            nutrition_el = null,
                                            nutrition_en = null,
                                            ingredients_el = "Ανιονικά/μη ιονικά επιφανειοδραστικά",
                                            ingredients_en = "Anionic/non-ionic surfactants",
                                            offerPrice = null,
                                            availability = 5
                                        )
                                    )
                                    insert(
                                        ProductEntity(
                                            name_el = "Χαρτί κουζίνας",
                                            name_en = "Kitchen Paper",
                                            category_el = "Είδη καθαριότητας",
                                            category_en = "Cleaning Supplies",
                                            description_el = "Ρολό 2 τεμαχίων",
                                            description_en = "2-pack roll",
                                            pricePerUnit = 2.40,
                                            unit_el = "€/συσκευασία",
                                            unit_en = "€/pack",
                                            imageName = "kitchen_paper",
                                            nutrition_el = null,
                                            nutrition_en = null,
                                            ingredients_el = "Χαρτί",
                                            ingredients_en = "Paper",
                                            offerPrice = null,
                                            availability = 12
                                        )
                                    )

                                    insert(
                                        ProductEntity(
                                            name_el = "Κατεψυγμένη πίτσα",
                                            name_en = "Frozen Pizza",
                                            category_el = "Κατεψυγμένα",
                                            category_en = "Frozen",
                                            description_el = "Πίτσα με ζαμπόν και τυρί 400g",
                                            description_en = "Pizza with ham and cheese 400g",
                                            pricePerUnit = 4.80,
                                            unit_el = "€/τεμάχιο",
                                            unit_en = "€/each",
                                            imageName = "frozen_pizza",
                                            nutrition_el = "Θερμίδες: 260 kcal\nΠρωτεΐνη: 11g\nΥδατάνθρακες: 33g\nΛιπαρά: 10g\nΑλάτι: 1.1g\nΚορεσμένα Λιπαρά: 4.5g",
                                            nutrition_en = "Calories: 260 kcal\nProtein: 11g\nCarbohydrates: 33g\nFat: 10g\nSalt: 1.1g\nSaturated Fat: 4.5g",
                                            ingredients_el = "Ζυμάρι, τυρί, ζαμπόν, σάλτσα ντομάτας",
                                            ingredients_en = "Dough, cheese, ham, tomato sauce",
                                            offerPrice = null,
                                            availability = 32
                                        )
                                    )

                                    insert(
                                        ProductEntity(
                                            name_el = "Κατεψυγμένες πατάτες",
                                            name_en = "Frozen Fries",
                                            category_el = "Κατεψυγμένα",
                                            category_en = "Frozen",
                                            description_el = "Πατάτες για τηγάνι ή φούρνο 1kg",
                                            description_en = "Fries for oven or frying, 1kg",
                                            pricePerUnit = 2.90,
                                            unit_el = "€/συσκευασία",
                                            unit_en = "€/pack",
                                            imageName = "frozen_fries",
                                            nutrition_el = "Θερμίδες: 150 kcal\nΠρωτεΐνη: 2.5g\nΥδατάνθρακες: 20g\nΛιπαρά: 7g\nΦυτικές Ίνες: 2g\nΑλάτι: 0.2g",
                                            nutrition_en = "Calories: 150 kcal\nProtein: 2.5g\nCarbohydrates: 20g\nFat: 7g\nFiber: 2g\nSalt: 0.2g",
                                            ingredients_el = "Πατάτες, ηλιέλαιο",
                                            ingredients_en = "Potatoes, sunflower oil",
                                            offerPrice = 2.40,
                                            availability = 6

                                        )
                                    )

                                    insert(
                                        ProductEntity(
                                            name_el = "Ψαροκροκέτες",
                                            name_en = "Fish Fingers",
                                            category_el = "Κατεψυγμένα",
                                            category_en = "Frozen",
                                            description_el = "Κατεψυγμένες ψαροκροκέτες 400g",
                                            description_en = "Frozen fish fingers 400g",
                                            pricePerUnit = 3.70,
                                            unit_el = "€/συσκευασία",
                                            unit_en = "€/pack",
                                            imageName = "fish_fingers",
                                            nutrition_el = "Θερμίδες: 210 kcal\nΠρωτεΐνη: 12g\nΥδατάνθρακες: 18g\nΛιπαρά: 10g\nΦυτικές Ίνες: 1g\nΑλάτι: 0.9g",
                                            nutrition_en = "Calories: 210 kcal\nProtein: 12g\nCarbohydrates: 18g\nFat: 10g\nFiber: 1g\nSalt: 0.9g",
                                            ingredients_el = "Φιλέτο ψαριού, ψίχουλα, λάδι",
                                            ingredients_en = "Fish fillet, breadcrumbs, oil",
                                            offerPrice = null,
                                            availability = 3
                                        )
                                    )
                                    //Το κάθε insert εισάγει ένα αντικείμενο ProductEntity με τις απαραίτητες πληροφορίες

                                }
                            }
                        }
                    })

                    .build()
                INSTANCE = instance //Εκχωρεί την instance στο singleton και την επιστρέφει
                instance
            }
        }
    }
}