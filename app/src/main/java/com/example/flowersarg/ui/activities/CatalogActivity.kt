package com.example.flowersarg.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flowersarg.data.entities.Bouquet
import com.example.flowersarg.databinding.ActivityCatalogBinding
import com.example.flowersarg.ui.adapters.BouquetAdapter
import com.example.flowersarg.utils.DataManager

class CatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogBinding
    private lateinit var bouquetAdapter: BouquetAdapter
    private lateinit var dataManager: DataManager

    private val sampleBouquets = listOf(
        Bouquet(
            id = 1,
            name = "Романтический закат",
            description = "Красные розы с белыми хризантемами",
            price = 2800.0,
            imageUrl = "rose",
            category = "Готовые букеты"
        ),
        Bouquet(
            id = 2,
            name = "Весенняя радость",
            description = "Яркий микс весенних цветов",
            price = 2200.0,
            imageUrl = "tulip",
            category = "Готовые букеты"
        ),
        Bouquet(
            id = 3,
            name = "Солнечный букет",
            description = "Подсолнухи и розы",
            price = 1900.0,
            imageUrl = "sunflower",
            category = "Готовые букеты"
        ),
        Bouquet(
            id = 4,
            name = "Нежный пион",
            description = "Розовые пионы в обрамлении зелени",
            price = 3500.0,
            imageUrl = "peony",
            category = "Готовые букеты"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = DataManager(this)

        setupRecyclerView()
        loadBouquets()
        setupClickListeners()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        return sharedPref.getBoolean("is_logged_in", false)
    }

    private fun setupRecyclerView() {
        bouquetAdapter = BouquetAdapter(
            onItemClick = { bouquet ->
                showBouquetDetails(bouquet)
            },
            onAddToCart = { bouquet ->
                addToCart(bouquet)
            },
            onAddToFavorite = { bouquet ->
                addToFavorite(bouquet)
            }
        )

        binding.rvBouquets.layoutManager = GridLayoutManager(this, 2)
        binding.rvBouquets.adapter = bouquetAdapter
    }

    private fun loadBouquets() {
        bouquetAdapter.submitList(sampleBouquets)
    }

    private fun showBouquetDetails(bouquet: Bouquet) {
        Toast.makeText(this, "${bouquet.name}\n${bouquet.description}\nЦена: ${bouquet.price}₽", Toast.LENGTH_LONG).show()
    }

    private fun addToCart(bouquet: Bouquet) {
        dataManager.addToCart(bouquet)
        Toast.makeText(this, "✅ ${bouquet.name} добавлен в корзину!", Toast.LENGTH_SHORT).show()
    }

    private fun addToFavorite(bouquet: Bouquet) {
        dataManager.addToFavorites(bouquet)
        Toast.makeText(this, "❤ ${bouquet.name} добавлен в избранное!", Toast.LENGTH_SHORT).show()
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        binding.btnFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }
    }
}