package com.example.recepiesapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recepiesapp.databinding.ActivityMainBinding
import androidx.appcompat.widget.SearchView
import java.util.Locale

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

        setAppLanguage(getCurrentLanguage())

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recipeRepository = RecipeRepository(this)
        recipes = recipeRepository.loadRecipes()
        recipesAdapter = RecipesAdapter(
            onItemClick = { selectedRecipe ->
                val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                    putExtra("RECIPE", selectedRecipe)
                }
                startActivity(intent)
            },
            getDescriptionPreview = ::getDescriptionPreview
        )

        setupRecyclerView()
        setupSearch()
        setupAddButton()
        setupSettings()
    }

    @Suppress("DEPRECATION")
    private fun setAppLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
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

    fun getDescriptionPreview(description: String): String {
        return if (description.length > 200) {
            description.substring(0, 200) + "..."
        }
        else {
            description
        }
    }

    private fun setupSettings() {
        binding.ivSettings.setOnClickListener {
            showSettingsDialog()
        }
    }

    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val spLanguage = dialogView.findViewById<Spinner>(R.id.spLanguage)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnClose)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        val currentLanguage = getCurrentLanguage()
        spLanguage.setSelection(if (currentLanguage == "en") 1 else 0)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val selectedLanguage = if (spLanguage.selectedItemPosition == 1) "en" else "ru"
            saveLanguage(selectedLanguage)
            recreate()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun saveLanguage(language: String) {
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        sharedPreferences.edit().putString("language", language).apply()
    }

    private fun getCurrentLanguage(): String {
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        return sharedPreferences.getString("language", "ru") ?: "ru"
    }
}