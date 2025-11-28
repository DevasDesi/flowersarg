package com.example.flowersarg.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowersarg.R
import com.example.flowersarg.ui.adapters.CartAdapter
import com.example.flowersarg.utils.DataManager
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var dataManager: DataManager
    private lateinit var cartAdapter: CartAdapter

    // UI элементы
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etDeliveryDate: EditText
    private lateinit var tvTotal: TextView
    private lateinit var btnConfirm: Button
    private lateinit var rvOrderItems: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_checkout)

        dataManager = DataManager(this)

        initViews()
        setupRecyclerView()
        loadOrderData()
        setupClickListeners()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        return sharedPref.getBoolean("is_logged_in", false)
    }

    private fun initViews() {
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etDeliveryDate = findViewById(R.id.etDeliveryDate)
        tvTotal = findViewById(R.id.tvTotal)
        btnConfirm = findViewById(R.id.btnConfirm)
        rvOrderItems = findViewById(R.id.rvOrderItems)

        // Устанавливаем дату доставки (завтра по умолчанию)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        etDeliveryDate.setText(dateFormat.format(calendar.time))

        // Заполняем данные пользователя
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "")
        etName.setText(userName)
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onRemoveClick = { bouquet ->
                // В оформлении заказа удаление не разрешено
                Toast.makeText(this, "Для удаления вернитесь в корзину", Toast.LENGTH_SHORT).show()
            }
        )

        rvOrderItems.layoutManager = LinearLayoutManager(this)
        rvOrderItems.adapter = cartAdapter
    }

    private fun loadOrderData() {
        val cartItems = dataManager.getCart()
        val total = dataManager.getCartTotal()

        cartAdapter.submitList(cartItems)
        tvTotal.text = "Итого: %.0f₽".format(total)

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Корзина пуста!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupClickListeners() {
        findViewById<Button>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }

        btnConfirm.setOnClickListener {
            confirmOrder()
        }

        // Выбор даты доставки
        etDeliveryDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                etDeliveryDate.setText(dateFormat.format(selectedDate.time))
            },
            year,
            month,
            day
        )

        // Минимальная дата - завтра
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        // Максимальная дата - через месяц
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun confirmOrder() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val deliveryDate = etDeliveryDate.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || deliveryDate.isEmpty()) {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone.length < 10) {
            Toast.makeText(this, "Введите корректный номер телефона", Toast.LENGTH_SHORT).show()
            return
        }

        // Создаем заказ
        val cartItems = dataManager.getCart()
        val total = dataManager.getCartTotal()
        val orderNumber = generateOrderNumber()

        // Очищаем корзину
        dataManager.clearCart()

        // Показываем успешное сообщение
        showOrderSuccess(orderNumber, total)
    }

    private fun generateOrderNumber(): String {
        return "ORD-${System.currentTimeMillis()}"
    }

    private fun showOrderSuccess(orderNumber: String, total: Double) {
        Toast.makeText(this, "✅ Заказ №$orderNumber оформлен!\nСумма: ${"%.0f".format(total)}₽", Toast.LENGTH_LONG).show()

        // Возвращаемся в главное меню
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}