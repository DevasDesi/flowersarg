package com.example.flowersarg.utils

import android.content.Context
import com.example.flowersarg.data.entities.Bouquet

class DataManager(private val context: Context) {

    private val sharedPref = context.getSharedPreferences("app_data", Context.MODE_PRIVATE)

    // Простой способ хранения через разделители
    private fun bouquetsToString(bouquets: List<Bouquet>): String {
        return bouquets.joinToString("|") { bouquet ->
            "${bouquet.id},${bouquet.name},${bouquet.description},${bouquet.price},${bouquet.imageUrl},${bouquet.category}"
        }
    }

    private fun stringToBouquets(data: String): List<Bouquet> {
        if (data.isEmpty()) return emptyList()

        return data.split("|").mapNotNull { item ->
            val parts = item.split(",")
            if (parts.size == 6) {
                Bouquet(
                    id = parts[0].toIntOrNull() ?: 0,
                    name = parts[1],
                    description = parts[2],
                    price = parts[3].toDoubleOrNull() ?: 0.0,
                    imageUrl = parts[4],
                    category = parts[5]
                )
            } else {
                null
            }
        }
    }

    // Сохранение избранного
    fun saveFavorites(favorites: List<Bouquet>) {
        val data = bouquetsToString(favorites)
        sharedPref.edit().putString("favorites", data).apply()
    }

    // Загрузка избранного
    fun getFavorites(): List<Bouquet> {
        val data = sharedPref.getString("favorites", "") ?: ""
        return stringToBouquets(data)
    }

    // Сохранение корзины
    fun saveCart(cart: List<Bouquet>) {
        val data = bouquetsToString(cart)
        sharedPref.edit().putString("cart", data).apply()
    }

    // Загрузка корзины
    fun getCart(): List<Bouquet> {
        val data = sharedPref.getString("cart", "") ?: ""
        return stringToBouquets(data)
    }

    // Добавление в избранное
    fun addToFavorites(bouquet: Bouquet) {
        val favorites = getFavorites().toMutableList()
        if (favorites.none { it.id == bouquet.id }) {
            favorites.add(bouquet)
            saveFavorites(favorites)
        }
    }

    // Удаление из избранного
    fun removeFromFavorites(bouquetId: Int) {
        val favorites = getFavorites().toMutableList()
        favorites.removeAll { it.id == bouquetId }
        saveFavorites(favorites)
    }

    // Добавление в корзину
    fun addToCart(bouquet: Bouquet) {
        val cart = getCart().toMutableList()
        cart.add(bouquet)
        saveCart(cart)
    }

    // Удаление из корзины
    fun removeFromCart(bouquetId: Int) {
        val cart = getCart().toMutableList()
        val index = cart.indexOfFirst { it.id == bouquetId }
        if (index != -1) {
            cart.removeAt(index)
            saveCart(cart)
        }
    }

    // Очистка корзины
    fun clearCart() {
        sharedPref.edit().remove("cart").apply()
    }

    // Получение общей суммы корзины
    fun getCartTotal(): Double {
        return getCart().sumOf { it.price }
    }
}