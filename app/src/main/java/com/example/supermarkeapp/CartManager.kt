package com.example.supermarkeapp

//Singleton αντικείμενο που διαχειρίζεται το καλάθι αγορών
object CartManager {
    //Χάρτης προϊόντος -> ποσότητα
    private val cartItems = mutableMapOf<ProductEntity, Int>()

    //Προσθέτει προιον στο καλάθι, αν υπάρχει ήδη, αυξάνει την ποσότητα
    fun addToCart(product: ProductEntity, quantity: Int) {
        val currentQuantity = cartItems[product] ?: 0
        cartItems[product] = currentQuantity + quantity
    }

    //Ενημερώνει την ποσότητα για ένα προϊόν. Αν quantity <= 0 το αφαιρεί
    fun updateQuantity(product: ProductEntity, quantity: Int) {
        if (quantity <= 0) {
            cartItems.remove(product)
        } else {
            cartItems[product] = quantity
        }
    }

    //Αφαιρεί εντελώς το προιόν από το καλάθι
    fun removeFromCart(product: ProductEntity) {
        cartItems.remove(product)
    }

    //Αδειάζει εντελώς το καλάθι
    fun clearCart() {
        cartItems.clear()
    }


    //Επιστρέφει το περιεχόμενο του καλαθιού ως αντιγραφή για να αποφεύγονται εξωτερικές τροποποιήσεις
    fun getCartItems(): Map<ProductEntity, Int> {
        return cartItems.toMap()  // για να μην τροποποιείται απ' έξω
    }

    //Υπολογίζει το συνολικό κόστος όλων των προϊόντων στο καλάθι
    fun getTotalPrice(): Double {
        return cartItems.entries.sumOf { (product, quantity) ->
            val price = product.offerPrice ?: product.pricePerUnit
            price * quantity
        }
    }

}