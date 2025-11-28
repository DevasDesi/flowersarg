package com.example.flowersarg.data.entities

data class Flower(
    val id: Int,
    val name: String,
    val color: String,
    val price: Double,
    val imageUrl: String,
    val season: String,
    val meaning: String,
    val compatibility: List<String>
)

data class BouquetComposition(
    val flowers: List<Flower>,
    val totalPrice: Double,
    val style: String,
    val occasion: String,
    val colorScheme: String
)