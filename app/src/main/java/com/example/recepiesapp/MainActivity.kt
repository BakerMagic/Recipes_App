package com.example.recepiesapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recepiesapp.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recipesAdapter: RecipesAdapter
    private lateinit var recipeRepository: RecipeRepository
    private var recipes: MutableList<Recipe> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeRepository = RecipeRepository(this)
        recipes = recipeRepository.loadRecipes()
        recipesAdapter = RecipesAdapter { selectedRecipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("RECIPE", selectedRecipe)
            }
            Log.d("RecipesApp Add", "ТЕСТ")

            startActivity(intent)
        }

        setupRecyclerView()
        setupSearch()
        setupAddButton()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewRecipes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewRecipes.adapter = recipesAdapter
        recipesAdapter.submitList(recipes)
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchRecipes(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRecipes(newText ?: "")
                return true
            }
        })
    }

    private fun searchRecipes(query: String) {
        val filteredRecipes = if (query.isEmpty()) {
            recipes // Показать все рецепты, если строка пуста
        } else {
            recipes.filter { recipe ->
                recipe.title.contains(query, ignoreCase = true) ||
                        recipe.ingredients.any { it.contains(query, ignoreCase = true) } ||
                        recipe.tags.any { it.contains(query, ignoreCase = true) }
            }
        }
        recipesAdapter.submitList(filteredRecipes)
    }

    @Suppress("DEPRECATION")
    private fun setupAddButton() {
        binding.btnAddRecipe.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val newRecipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data.getSerializableExtra("NEW_RECIPE", Recipe::class.java)
            }
            else {
                @Suppress("DEPRECATION")
                data.getSerializableExtra("NEW_RECIPE") as? Recipe
            }
            newRecipe?.let { addRecipe(it) }
        }
    }

    fun addRecipe(newRecipe: Recipe) {
        recipes.add(newRecipe)
        recipesAdapter.submitList(recipes.toMutableList())
        recipeRepository.saveRecipes(recipes)
        Log.d("RecipesApp Add", "Рецепт добавлен и сохранён: $newRecipe")
    }
}