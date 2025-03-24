package com.example.recepiesapp

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class RecipeRepository(private val context: Context) {

    private val fileName = "recipes.json"
    private val gson = Gson()

    private val file: File
        get() = File(context.filesDir, fileName)

    fun loadRecipes(): MutableList<Recipe> {
        return try {
            if (file.exists()) {
                val json = file.readText()
                val type = object : TypeToken<MutableList<Recipe>>() {}.type
                gson.fromJson(json, type) ?: mutableListOf()
            } else {
                mutableListOf()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mutableListOf()
        }
    }

    fun saveRecipes(recipes: MutableList<Recipe>) {
        try {
            val json = gson.toJson(recipes)
            file.writeText(json)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}