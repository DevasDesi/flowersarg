package com.example.flowersarg.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flowersarg.R
import com.example.flowersarg.data.entities.Bouquet

class CartAdapter(
    private val onRemoveClick: (Bouquet) -> Unit
) : ListAdapter<Bouquet, CartAdapter.CartViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val bouquet = getItem(position)
        holder.bind(bouquet)
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivBouquet: TextView = itemView.findViewById(R.id.ivBouquet)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val btnRemove: TextView = itemView.findViewById(R.id.btnRemove)

        fun bind(bouquet: Bouquet) {
            tvName.text = bouquet.name
            tvPrice.text = "%.0f₽".format(bouquet.price)

            // Устанавливаем emoji
            val flowerEmoji = when (bouquet.imageUrl) {
                "rose" -> "🌹"
                "tulip" -> "🌷"
                "sunflower" -> "🌻"
                "peony" -> "🌸"
                else -> "💐"
            }
            ivBouquet.text = flowerEmoji

            btnRemove.setOnClickListener {
                onRemoveClick(bouquet)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Bouquet>() {
        override fun areItemsTheSame(oldItem: Bouquet, newItem: Bouquet): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Bouquet, newItem: Bouquet): Boolean {
            return oldItem == newItem
        }
    }
}