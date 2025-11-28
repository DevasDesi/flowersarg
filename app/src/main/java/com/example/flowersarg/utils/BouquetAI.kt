package com.example.flowersarg.utils

import com.example.flowersarg.data.entities.BouquetComposition
import com.example.flowersarg.data.entities.Flower
import kotlin.random.Random

class BouquetAI {

    // База знаний флориста
    private val flowerDatabase = listOf(
        Flower(1, "Красная роза", "красный", 150.0, "red_rose", "круглый год", "Любовь и страсть", listOf("белый", "зеленый", "фиолетовый")),
        Flower(2, "Белая роза", "белый", 140.0, "white_rose", "круглый год", "Чистота и невинность", listOf("красный", "розовый", "синий")),
        Flower(3, "Розовая роза", "розовый", 130.0, "pink_rose", "круглый год", "Нежность и восхищение", listOf("белый", "фиолетовый", "зеленый")),
        Flower(4, "Подсолнух", "желтый", 100.0, "sunflower", "лето", "Солнце и радость", listOf("оранжевый", "коричневый", "зеленый")),
        Flower(5, "Тюльпан", "разноцветный", 80.0, "tulip", "весна", "Совершенная любовь", listOf("белый", "фиолетовый", "розовый")),
        Flower(6, "Орхидея", "фиолетовый", 200.0, "orchid", "круглый год", "Роскошь и красота", listOf("белый", "розовый", "зеленый")),
        Flower(7, "Лилии", "белый", 120.0, "lily", "лето", "Чистота и возрождение", listOf("розовый", "оранжевый", "зеленый")),
        Flower(8, "Хризантема", "оранжевый", 90.0, "chrysanthemum", "осень", "Долголетие и радость", listOf("желтый", "красный", "зеленый")),
        Flower(9, "Пион", "розовый", 180.0, "peony", "весна", "Процветание и счастье", listOf("белый", "фиолетовый", "зеленый")),
        Flower(10, "Лаванда", "фиолетовый", 110.0, "lavender", "лето", "Спокойствие и элегантность", listOf("белый", "розовый", "синий"))
    )

    // Стили букетов
    private val bouquetStyles = listOf(
        "Романтический", "Свадебный", "Современный", "Классический", "Деревенский", "Экзотический"
    )

    // Поводы для букетов
    private val occasions = listOf(
        "День рождения", "Свадьба", "8 Марта", "День влюбленных", "Юбилей", "Просто так"
    )

    // Цветовые схемы
    private val colorSchemes = listOf(
        "Монохромная", "Контрастная", "Пастельная", "Яркая", "Нежная"
    )

    // Генерация букета на основе предпочтений
    fun generateBouquet(
        style: String? = null,
        occasion: String? = null,
        colorScheme: String? = null,
        maxPrice: Double = 2000.0
    ): BouquetComposition {

        val selectedStyle = style ?: bouquetStyles.random()
        val selectedOccasion = occasion ?: occasions.random()
        val selectedColorScheme = colorScheme ?: colorSchemes.random()

        val selectedFlowers = mutableListOf<Flower>()
        var currentPrice = 0.0

        // Основной цветок (фокусный элемент)
        val mainFlower = getMainFlower(selectedStyle, selectedOccasion)
        selectedFlowers.add(mainFlower)
        currentPrice += mainFlower.price

        // Дополнительные цветы (2-4 штуки)
        val additionalFlowers = getAdditionalFlowers(mainFlower, selectedStyle, 2 + Random.nextInt(3))
        selectedFlowers.addAll(additionalFlowers)
        currentPrice += additionalFlowers.sumOf { it.price }

        // Зелень и наполнители (1-2 штуки)
        val fillers = getFillerFlowers(selectedFlowers, 1 + Random.nextInt(2))
        selectedFlowers.addAll(fillers)
        currentPrice += fillers.sumOf { it.price }

        // Корректировка цены
        val finalComposition = adjustForPrice(selectedFlowers, maxPrice)

        return BouquetComposition(
            flowers = finalComposition,
            totalPrice = finalComposition.sumOf { it.price },
            style = selectedStyle,
            occasion = selectedOccasion,
            colorScheme = selectedColorScheme
        )
    }

    private fun getMainFlower(style: String, occasion: String): Flower {
        return when {
            style == "Романтический" || occasion == "День влюбленных" ->
                flowerDatabase.first { it.name.contains("роза") && it.color == "красный" }

            style == "Свадебный" || occasion == "Свадьба" ->
                flowerDatabase.first { it.name.contains("роза") && it.color == "белый" }

            occasion == "8 Марта" ->
                flowerDatabase.first { it.name.contains("тюльпан") }

            style == "Экзотический" ->
                flowerDatabase.first { it.name.contains("орхидея") }

            else -> flowerDatabase.random()
        }
    }

    private fun getAdditionalFlowers(mainFlower: Flower, style: String, count: Int): List<Flower> {
        val compatibleFlowers = flowerDatabase.filter { flower ->
            flower.id != mainFlower.id &&
                    (mainFlower.compatibility.contains(flower.color) ||
                            flower.compatibility.contains(mainFlower.color))
        }

        return compatibleFlowers.shuffled().take(count)
    }

    private fun getFillerFlowers(mainFlowers: List<Flower>, count: Int): List<Flower> {
        val greenFlowers = flowerDatabase.filter { it.color == "зеленый" }
        return if (greenFlowers.isNotEmpty()) {
            greenFlowers.shuffled().take(count)
        } else {
            flowerDatabase.filter { it.price < 50.0 }.shuffled().take(count)
        }
    }

    private fun adjustForPrice(flowers: List<Flower>, maxPrice: Double): List<Flower> {
        var currentFlowers = flowers.toMutableList()
        var total = currentFlowers.sumOf { it.price }

        // Если превышает бюджет, убираем самые дорогие
        while (total > maxPrice && currentFlowers.size > 3) {
            val mostExpensive = currentFlowers.maxByOrNull { it.price }
            currentFlowers.remove(mostExpensive)
            total = currentFlowers.sumOf { it.price }
        }

        return currentFlowers
    }

    // Получить все доступные стили
    fun getAvailableStyles(): List<String> = bouquetStyles

    // Получить все доступные поводы
    fun getAvailableOccasions(): List<String> = occasions

    // Получить все цветовые схемы
    fun getAvailableColorSchemes(): List<String> = colorSchemes
}