package com.example.supermarkeapp
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Locale

//Οντότητα Room που αντιπροσωπεύει ένα προιόν στον κατάλογο του supermarket.
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    //Στοιχεία προιόντος στα ελληνικά και αγγλικά
    val name_el: String,
    val name_en: String,

    val category_el: String,
    val category_en: String,

    val description_el: String?,
    val description_en: String?,

    val pricePerUnit: Double,
    val unit_el: String,
    val unit_en: String,

    val imageName: String?,
    val nutrition_el: String?,
    val nutrition_en: String?,

    val ingredients_el: String?,
    val ingredients_en: String?,

    val offerPrice: Double?,
    val availability: Int = 0
)


//Επιστρέφει το όνομα του προϊόντος ανάλογα με τη γλώσσα της συσκευής
fun ProductEntity.localizedName(): String {
    return if (Locale.getDefault().language == "el") name_el else name_en
}
//Επιστρέφει την κατηγορία στη σωστή γλώσσα
fun ProductEntity.localizedCategory(): String {
    return if (Locale.getDefault().language == "el") category_el else category_en
}
//Επιστρέφει την περιγραφή στη σωστή γλώσσα
fun ProductEntity.localizedDescription(): String? {
    return if (Locale.getDefault().language == "el") description_el else description_en
}
//Επιστρέφει τη μονάδα μέτρησης ανάλογα με τη γλώσσα
fun ProductEntity.localizedUnit(): String {
    return if (Locale.getDefault().language == "el") unit_el else unit_en
}
//Επιστρέφει τα διατροφικά στοιχεία στη σωστή γλώσσα
fun ProductEntity.localizedNutrition(): String? {
    return if (Locale.getDefault().language == "el") nutrition_el else nutrition_en
}
//Επιστρέφει τα συστατικά στη σωστή γλώσσα
fun ProductEntity.localizedIngredients(): String? {
    return if (Locale.getDefault().language == "el") ingredients_el else ingredients_en
}
//Εμφανίζει την τιμή, λαμβάνοντας υπόψη πιθανή προσφορά
fun ProductEntity.displayPrice(): String {
    return if (offerPrice != null) "€%.2f".format(offerPrice) else "€%.2f".format(pricePerUnit)
}
//Επιστρέφει την αρχική τιμή μόνο αν υπάρχει προσφορά (χρήσιμο για διαγραφή τιμής στην UI)
fun ProductEntity.originalPriceOrNull(): String? {
    return if (offerPrice != null) "€%.2f".format(pricePerUnit) else null
}
//Επιστρέφει κείμενο τύπου "Διαθεσιμότητα: 10" στη σωστή γλώσσα
fun ProductEntity.localizedAvailabilityCount(): String {
    return if (Locale.getDefault().language == "el")
        "Διαθεσιμότητα: $availability"
    else
        "Availability: $availability"
}
//Επιστρέφει "Διαθέσιμο" ή "Μη διαθέσιμο" στη σωστή γλώσσα
fun ProductEntity.localizedAvailability(): String {
    return if (availability > 0) {
        if (Locale.getDefault().language == "el") "Διαθέσιμο" else "In stock"
    } else {
        if (Locale.getDefault().language == "el") "Μη διαθέσιμο" else "Out of stock"
    }
}
//Ελέγχει αν το προϊόν ταιριάζει με την αναζήτηση (όνομα, περιγραφή ή κατηγορί
fun ProductEntity.matchesQuery(query: String): Boolean {
    val lowerQuery = query.lowercase()
    return localizedName().lowercase().contains(lowerQuery) ||
            localizedDescription()?.lowercase()?.contains(lowerQuery) == true ||
            localizedCategory().lowercase().contains(lowerQuery)
}
//Ελέγχει αν η τελική τιμή του προϊόντος βρίσκεται εντός εύρους τιμών
fun ProductEntity.isInPriceRange(min: Double?, max: Double?): Boolean {
    val effectivePrice = offerPrice ?: pricePerUnit
    return (min == null || effectivePrice >= min) && (max == null || effectivePrice <= max)
}
//Ελέγχει αν υπάρχει επαρκές απόθεμα, βάση ενός ελάχιστου
fun ProductEntity.matchesAvailability(minAvailable: Int?): Boolean {
    return minAvailable == null || availability >= minAvailable
}
//Επιστρέφει true αν υπάρχει προσφορά
fun ProductEntity.isOnOffer(): Boolean {
    return offerPrice != null
}
