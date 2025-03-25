package com.example.recepiesapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recepiesapp.databinding.ItemRecipeBinding

class RecipesAdapter(
    private val onItemClick: (Recipe) -> Unit // Добавлен параметр
) : ListAdapter<Recipe, RecipesAdapter.RecipeViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
            oldItem == newItem
    }

    class RecipeViewHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Recipe) {
            binding.tvTitle.text = recipe.title
            binding.tvIngredients.text = "Ингредиенты:\n${recipe.ingredients.joinToString("\n")}"
            binding.tvDescription.text = "Описание:\n${recipe.description}" // Заменил инструкции на описание
            binding.tvTags.text = "Теги:\n${recipe.tags.joinToString("\n")}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            val recipe = getItem(position)
            onItemClick(recipe) // Передача клика в MainActivity
        }
        holder.bind(getItem(position))
    }
}