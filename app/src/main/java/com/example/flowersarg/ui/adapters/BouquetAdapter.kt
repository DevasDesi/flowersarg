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

class BouquetAdapter(
    private val onItemClick: (Bouquet) -> Unit,
    private val onAddToCart: (Bouquet) -> Unit,
    private val onAddToFavorite: (Bouquet) -> Unit
) : ListAdapter<Bouquet, BouquetAdapter.BouquetViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BouquetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bouquet, parent, false)
        return BouquetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BouquetViewHolder, position: Int) {
        val bouquet = getItem(position)
        holder.bind(bouquet)
    }

    inner class BouquetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivBouquet: TextView = itemView.findViewById(R.id.ivBouquet)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        private val btnAddToCart: TextView = itemView.findViewById(R.id.btnAddToCart)
        private val btnFavorite: TextView = itemView.findViewById(R.id.btnFavorite)

        fun bind(bouquet: Bouquet) {
            tvName.text = bouquet.name
            tvDescription.text = bouquet.description
            tvPrice.text = "%.0f₽".format(bouquet.price)

            // Устанавливаем emoji в зависимости от типа цветка
            val flowerEmoji = when (bouquet.imageUrl) {
                "rose" -> "🌹"
                "tulip" -> "🌷"
                "sunflower" -> "🌻"
                "peony" -> "🌸"
                else -> "💐"
            }
            ivBouquet.text = flowerEmoji

            itemView.setOnClickListener {
                onItemClick(bouquet)
            }

            btnAddToCart.setOnClickListener {
                onAddToCart(bouquet)
            }

            btnFavorite.setOnClickListener {
                onAddToFavorite(bouquet)
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