package com.example.flowersarg.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowersarg.data.entities.Bouquet
import com.example.flowersarg.databinding.ActivityCartBinding
import com.example.flowersarg.ui.adapters.CartAdapter
import com.example.flowersarg.utils.DataManager

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var dataManager: DataManager
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = DataManager(this)

        setupRecyclerView()
        loadCartItems()
        setupClickListeners()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        return sharedPref.getBoolean("is_logged_in", false)
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onRemoveClick = { bouquet ->
                removeFromCart(bouquet)
            }
        )

        binding.rvCartItems.layoutManager = LinearLayoutManager(this)
        binding.rvCartItems.adapter = cartAdapter
    }

    private fun loadCartItems() {
        val cartItems = dataManager.getCart()

        if (cartItems.isEmpty()) {
            binding.tvEmpty.visibility = TextView.VISIBLE
            binding.tvTotal.visibility = TextView.GONE
            binding.btnCheckout.visibility = Button.GONE
        } else {
            binding.tvEmpty.visibility = TextView.GONE
            binding.tvTotal.visibility = TextView.VISIBLE
            binding.btnCheckout.visibility = Button.VISIBLE

            cartAdapter.submitList(cartItems)
            updateTotal()
        }
    }

    private fun removeFromCart(bouquet: Bouquet) {
        dataManager.removeFromCart(bouquet.id)
        Toast.makeText(this, "🗑️ ${bouquet.name} удален из корзины", Toast.LENGTH_SHORT).show()
        loadCartItems()
    }

    private fun updateTotal() {
        val total = dataManager.getCartTotal()
        binding.tvTotal.text = "Общая сумма: %.0f₽".format(total)
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnCheckout.setOnClickListener {
            val cartItems = dataManager.getCart()
            if (cartItems.isNotEmpty()) {
                val intent = Intent(this, CheckoutActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Корзина пуста!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}