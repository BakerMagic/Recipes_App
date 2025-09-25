package com.example.recepiesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView

class TermsOfUseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_use)

        val webView = findViewById<WebView>(R.id.webViewTerms)
        webView.settings.javaScriptEnabled = true

        // Загрузка HTML-файла из ресурсов
        val htmlFile = resources.openRawResource(R.raw.terms_of_use).bufferedReader().use { it.readText() }
        webView.loadDataWithBaseURL(null, htmlFile, "text/html", "UTF-8", null)
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}