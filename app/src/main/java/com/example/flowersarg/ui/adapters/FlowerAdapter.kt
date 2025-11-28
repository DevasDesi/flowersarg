package com.example.flowersarg.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flowersarg.R
import com.example.flowersarg.data.entities.Flower

class FlowerAdapter : ListAdapter<Flower, FlowerAdapter.FlowerViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flower, parent, false)
        return FlowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
        val flower = getItem(position)
        holder.bind(flower)
    }

    inner class FlowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivFlower: TextView = itemView.findViewById(R.id.ivFlower)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvColor: TextView = itemView.findViewById(R.id.tvColor)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val tvMeaning: TextView = itemView.findViewById(R.id.tvMeaning)

        fun bind(flower: Flower) {
            tvName.text = flower.name
            tvColor.text = "Цвет: ${flower.color}"
            tvPrice.text = "%.0f₽".format(flower.price)
            tvMeaning.text = "💫 ${flower.meaning}"

            // Устанавливаем emoji
            val flowerEmoji = when (flower.imageUrl) {
                "red_rose" -> "🌹"
                "white_rose" -> "🌹"
                "pink_rose" -> "🌹"
                "sunflower" -> "🌻"
                "tulip" -> "🌷"
                "orchid" -> "🌺"
                "lily" -> "💮"
                "chrysanthemum" -> "❀"
                "peony" -> "🌸"
                "lavender" -> "☘️"
                else -> "🌼"
            }
            ivFlower.text = flowerEmoji
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Flower>() {
        override fun areItemsTheSame(oldItem: Flower, newItem: Flower): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Flower, newItem: Flower): Boolean {
            return oldItem == newItem
        }
    }
}