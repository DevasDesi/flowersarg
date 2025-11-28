package com.example.flowersarg.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flowersarg.R
import com.example.flowersarg.data.entities.BouquetComposition
import com.example.flowersarg.ui.adapters.FlowerAdapter
import com.example.flowersarg.utils.BouquetAI
import com.example.flowersarg.utils.DataManager
import kotlin.random.Random

class ConstructorActivity : AppCompatActivity() {

    private lateinit var bouquetAI: BouquetAI
    private lateinit var dataManager: DataManager
    private lateinit var flowerAdapter: FlowerAdapter

    // UI элементы
    private lateinit var spinnerStyle: Spinner
    private lateinit var spinnerOccasion: Spinner
    private lateinit var spinnerColor: Spinner
    private lateinit var seekBarPrice: SeekBar
    private lateinit var tvPrice: TextView
    private lateinit var btnGenerate: Button
    private lateinit var btnSave: Button
    private lateinit var rvFlowers: RecyclerView
    private lateinit var tvBouquetInfo: TextView

    private var currentBouquet: BouquetComposition? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_constructor)

        bouquetAI = BouquetAI()
        dataManager = DataManager(this)

        initViews()
        setupSpinners()
        setupRecyclerView()
        setupClickListeners()

        // Генерируем начальный букет
        generateBouquet()
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        return sharedPref.getBoolean("is_logged_in", false)
    }

    private fun initViews() {
        spinnerStyle = findViewById(R.id.spinnerStyle)
        spinnerOccasion = findViewById(R.id.spinnerOccasion)
        spinnerColor = findViewById(R.id.spinnerColor)
        seekBarPrice = findViewById(R.id.seekBarPrice)
        tvPrice = findViewById(R.id.tvPrice)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnSave = findViewById(R.id.btnSave)
        rvFlowers = findViewById(R.id.rvFlowers)
        tvBouquetInfo = findViewById(R.id.tvBouquetInfo)

        // Настройка SeekBar
        seekBarPrice.max = 5000
        seekBarPrice.progress = 2000
        updatePriceText()

        seekBarPrice.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updatePriceText()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updatePriceText() {
        tvPrice.text = "Бюджет: ${seekBarPrice.progress}₽"
    }

    private fun setupSpinners() {
        // Стили
        val styles = bouquetAI.getAvailableStyles()
        val styleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, styles)
        styleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStyle.adapter = styleAdapter

        // Поводы
        val occasions = bouquetAI.getAvailableOccasions()
        val occasionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, occasions)
        occasionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOccasion.adapter = occasionAdapter

        // Цветовые схемы
        val colors = bouquetAI.getAvailableColorSchemes()
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerColor.adapter = colorAdapter
    }

    private fun setupRecyclerView() {
        flowerAdapter = FlowerAdapter()
        rvFlowers.layoutManager = LinearLayoutManager(this)
        rvFlowers.adapter = flowerAdapter
    }

    private fun setupClickListeners() {
        btnGenerate.setOnClickListener {
            generateBouquet()
        }

        btnSave.setOnClickListener {
            saveBouquet()
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            onBackPressed()
        }
    }

    private fun generateBouquet() {
        val style = spinnerStyle.selectedItem as? String
        val occasion = spinnerOccasion.selectedItem as? String
        val colorScheme = spinnerColor.selectedItem as? String
        val maxPrice = seekBarPrice.progress.toDouble()

        currentBouquet = bouquetAI.generateBouquet(style, occasion, colorScheme, maxPrice)
        displayBouquet()
    }

    private fun displayBouquet() {
        currentBouquet?.let { bouquet ->
            // Отображаем цветы
            flowerAdapter.submitList(bouquet.flowers)

            // Отображаем информацию о букете
            val info = """
                💐 Ваш идеальный букет!
                
                🎨 Стиль: ${bouquet.style}
                🎊 Повод: ${bouquet.occasion}
                🌈 Цветовая схема: ${bouquet.colorScheme}
                💰 Стоимость: ${"%.0f".format(bouquet.totalPrice)}₽
                
                ✨ Состав букета:
            """.trimIndent()

            tvBouquetInfo.text = info

            // Активируем кнопку сохранения
            btnSave.isEnabled = true
            btnSave.alpha = 1f
        }
    }

    private fun saveBouquet() {
        currentBouquet?.let { bouquet ->
            // Преобразуем в обычный букет и добавляем в корзину
            val customBouquet = com.example.flowersarg.data.entities.Bouquet(
                id = Random.nextInt(1000, 9999),
                name = "Индивидуальный букет: ${bouquet.style}",
                description = "Состав: ${bouquet.flowers.joinToString { it.name }}",
                price = bouquet.totalPrice,
                imageUrl = "custom",
                category = "Индивидуальные букеты"
            )

            dataManager.addToCart(customBouquet)
            Toast.makeText(this, "✅ Индивидуальный букет добавлен в корзину!", Toast.LENGTH_LONG).show()
        }
    }
}