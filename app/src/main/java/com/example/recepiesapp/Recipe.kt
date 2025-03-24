package com.example.recepiesapp

import java.io.Serializable

data class Recipe(
    val id: Int,
    val title: String,
    val ingredients: List<String>,
    val instructions: String,
    val tags: List<String>
) : Serializable