package com.example.flowersarg.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.flowersarg.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ПРОВЕРКА АВТОРИЗАЦИИ
        if (!isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        setupUI()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        return sharedPref.getBoolean("is_logged_in", false)
    }

    private fun setupUI() {
        val tvWelcome: TextView = findViewById(R.id.tvWelcome)
        val tvUserInfo: TextView = findViewById(R.id.tvUserInfo)
        val btnCatalog: Button = findViewById(R.id.btnCatalog)
        val btnConstructor: Button = findViewById(R.id.btnConstructor)
        val btnFavorites: Button = findViewById(R.id.btnFavorites)
        val btnCart: Button = findViewById(R.id.btnCart)
        val btnLogout: Button = findViewById(R.id.btnLogout)

        // Получаем данные пользователя
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "Пользователь")
        val userEmail = sharedPref.getString("user_email", "")

        tvWelcome.text = "Добро пожаловать, $userName!"
        tvUserInfo.text = "Email: $userEmail\nВы успешно вошли в приложение"

        // Навигация
        btnCatalog.setOnClickListener {
            val intent = Intent(this, CatalogActivity::class.java)
            startActivity(intent)
        }

        btnConstructor.setOnClickListener {
            val intent = Intent(this, ConstructorActivity::class.java)
            startActivity(intent)
        }

        btnFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            logoutUser()
        }
    }

    private fun logoutUser() {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        Toast.makeText(this, "Вы вышли из системы", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}