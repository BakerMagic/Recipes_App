package com.example.recepiesapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var ingredientsLayout: LinearLayout
    private lateinit var ingredientsList: List<String>
    private lateinit var units: List<String>
    private var ingredientsFields: MutableList<Triple<AutoCompleteTextView, EditText, Spinner>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        ingredientsLayout = findViewById(R.id.ingredientsLayout)
        ingredientsList = listOf(
            "мука", "яйца", "соль", "сахар", "масло", "молоко",
            "картофель", "лук", "помидоры", "чеснок", "рис", "капуста"
        )

        units = listOf(
            "г", "кг", "мл", "л", "ч.л.", "ст.л.", "щепотка", "шт"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ingredientsList)

        // Инициализация первого поля ингредиента
        addIngredientField(adapter)

        // Кнопка для добавления нового поля ингредиента
        findViewById<Button>(R.id.btnAddIngredient).setOnClickListener {
            addIngredientField(adapter)
        }

        // Кнопка сохранения рецепта
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveRecipe()
        }
    }

    private fun addIngredientField(adapter: ArrayAdapter<String>) {
        val inflater = layoutInflater
        val ingredientView = inflater.inflate(R.layout.item_ingredient, ingredientsLayout, false)

        val autoCompleteTextView = ingredientView.findViewById<AutoCompleteTextView>(R.id.etIngredient)
        autoCompleteTextView.setAdapter(adapter)

        val quantityEditText = ingredientView.findViewById<EditText>(R.id.etQuantity)
        val unitSpinner = ingredientView.findViewById<Spinner>(R.id.spUnit)

        // настройка выпадающего списка для единиц измерения
        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = unitAdapter

        unitSpinner.setSelection(0)

        val removeButton = ingredientView.findViewById<ImageButton>(R.id.btnRemove)

        // Добавление обработчика для кнопки удаления
        removeButton?.setOnClickListener {
            ingredientsLayout.removeView(ingredientView) // удаляем вид
            ingredientsFields.removeIf { it.first == autoCompleteTextView } // удаляем из списка
        }

        ingredientsLayout.addView(ingredientView)
        ingredientsFields.add(Triple(autoCompleteTextView, quantityEditText, unitSpinner))
    }

    private fun saveRecipe() {
        val title = findViewById<EditText>(R.id.etTitle).text.toString().trim()
        val description = findViewById<EditText>(R.id.etDescription).text.toString().trim()
        val instructions = findViewById<EditText>(R.id.etInstructions).text.toString().trim()
        val tags = findViewById<EditText>(R.id.etTags).text.toString().split(",").map { it.trim() }.filter { it.isNotEmpty() }

        val ingredients = ingredientsFields.mapNotNull { (ingredient, quantity, unit) ->
            val ingredientText = ingredient.text.toString().trim()
            val quantityText = quantity.text.toString().trim()
            val unitText = (unit as Spinner).selectedItem.toString()
            if (ingredientText.isNotEmpty() && quantityText.isNotEmpty()) {
                "$ingredientText $quantityText $unitText"
            } else {
                null
            }
        }

        if (title.isNotEmpty() && ingredients.isNotEmpty()) {
            val newRecipe = Recipe(
                id = generateUniqueId(),
                title = title,
                ingredients = ingredients,
                description = description,
                instructions = instructions,
                tags = tags
            )

            val resultIntent = Intent().apply {
                putExtra("NEW_RECIPE", newRecipe)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        } else {
            Toast.makeText(this, "Некорректные данные рецепта", Toast.LENGTH_SHORT).show()
            Log.e("AddRecipeActivity", "Некорректные данные рецепта")
        }
    }

    private fun generateUniqueId(): Int {
        // Генерация уникального ID
        return (System.currentTimeMillis() % 1000000).toInt()
    }
}