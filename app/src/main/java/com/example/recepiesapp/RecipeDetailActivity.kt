package com.example.recepiesapp

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class RecipeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        val recipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("RECIPE", Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("RECIPE") as? Recipe
        } ?: return

        findViewById<TextView>(R.id.tvTitle).text = recipe.title
        findViewById<TextView>(R.id.tvDescription).text = "Описание:\n${recipe.description}"
        findViewById<TextView>(R.id.tvIngredients).text = "Ингредиенты:\n${recipe.ingredients.joinToString("\n")}"
        findViewById<TextView>(R.id.tvInstructions).text = "Инструкции:\n${recipe.instructions}"
        findViewById<TextView>(R.id.tvTags).text = "Теги:\n${recipe.tags.joinToString("\n")}"
    }
}