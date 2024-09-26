package com.shifthackz.webviewapp

import android.annotation.SuppressLint
//import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent

class MainActivity : AppCompatActivity() {

    companion object {
        private const val APP_URL = "https://ai.assisted.space"
    }

    // WebViewClient to handle URL loading and errors
    private val appWebViewClient = object : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            error?.description?.toString()?.let { showError(it) }
        }

        // For API 21 and above
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url?.toString()
            if (url != null) {
                // Check for Google Sign-In or other secure operations
                if (url.startsWith("https://accounts.google.com"))
//                    url.startsWith("https://assisted.space")) ||
//                    url.startsWith("https://lukesteuber") ||
//                    url.startsWith("https://coolhand"))

                {
                    // Open these URLs in a Custom Tab or external browser
                    openInCustomTab(url)
                    return true
//                } else if (!url.startsWith(APP_URL)) {
//                    // Open external URLs in a Custom Tab or external browser
//                    openInCustomTab(url)
//                    return true
                }
            }
            return false
        }

        // For older API versions (below API 21)
        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            if (url != null && !url.startsWith(APP_URL)) {
                openInCustomTab(url)
                return true
            }
            return false
        }
    }

    private val webView: WebView by lazy { findViewById(R.id.webView) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Handle back button press in WebView
        onBackPressedDispatcher.addCallback(this, true) {
            if (webView.canGoBack()) webView.goBack()
            else finish()
        }

        // WebView settings
        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true // Enable DOM storage for JavaScript
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE // Allow mixed content (HTTP & HTTPS)
            webViewClient = appWebViewClient
            settings.safeBrowsingEnabled = false // Disable Safe Browsing (optional)
            loadUrl(APP_URL)
        }
    }

    // Function to open URL in a Custom Tab
    private fun openInCustomTab(url: String) {
        val builder = CustomTabsIntent.Builder()
        builder.setShowTitle(false) // Hide the title bar
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    // Function to show error messages in a Toast
    private fun showError(error: String) {
        Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
    }
}