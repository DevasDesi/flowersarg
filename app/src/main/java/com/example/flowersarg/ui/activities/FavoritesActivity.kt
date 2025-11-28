package com.example.flowersarg.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowersarg.databinding.ActivityFavoritesBinding
import com.example.flowersarg.ui.adapters.BouquetAdapter
import com.example.flowersarg.utils.DataManager

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var dataManager: DataManager
    private lateinit var favoritesAdapter: BouquetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = DataManager(this)

        setupRecyclerView()
        loadFavorites()
        setupClickListeners()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        return sharedPref.getBoolean("is_logged_in", false)
    }

    private fun setupRecyclerView() {
        favoritesAdapter = BouquetAdapter(
            onItemClick = { bouquet ->
                Toast.makeText(this, "${bouquet.name}\nЦена: ${bouquet.price}₽", Toast.LENGTH_SHORT).show()
            },
            onAddToCart = { bouquet ->
                dataManager.addToCart(bouquet)
                Toast.makeText(this, "✅ ${bouquet.name} добавлен в корзину!", Toast.LENGTH_SHORT).show()
            },
            onAddToFavorite = { bouquet ->
                dataManager.removeFromFavorites(bouquet.id)
                Toast.makeText(this, "💔 ${bouquet.name} удален из избранного", Toast.LENGTH_SHORT).show()
                loadFavorites()
            }
        )

        binding.rvFavorites.layoutManager = GridLayoutManager(this, 2)
        binding.rvFavorites.adapter = favoritesAdapter
    }

    private fun loadFavorites() {
        val favorites = dataManager.getFavorites()

        if (favorites.isEmpty()) {
            binding.tvEmpty.visibility = TextView.VISIBLE
            binding.rvFavorites.visibility = RecyclerView.GONE
        } else {
            binding.tvEmpty.visibility = TextView.GONE
            binding.rvFavorites.visibility = RecyclerView.VISIBLE
            favoritesAdapter.submitList(favorites)
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}