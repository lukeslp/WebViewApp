package com.shifthackz.webviewapp

import android.annotation.SuppressLint
import android.content.Intent
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

    private val appWebViewClient = object : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            error?.description?.toString()?.let(::showError)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val url = request?.url?.toString()
            if (url != null) {
                // Check for Google Sign-In or other secure operations
                if (url.startsWith("https://accounts.google.com") ||
                    url.startsWith("your_other_secure_url_prefix")) {
                    // Open these URLs in a Custom Tab or external browser
                    openInCustomTab(url)
                    return true
                } else {
                    // For other URLs, let the WebView handle them
                    return false // This is important to enable JavaScript and other features
                }
            }
            return false
        }
    }

    private val webView: WebView by lazy { findViewById(R.id.webView) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(this, true) {
            if (webView.canGoBack()) webView.goBack()
            else finish()
        }

        webView.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true // Enable DOM storage for JavaScript
            settings.javaScriptCanOpenWindowsAutomatically = true // Allow JavaScript to open windows
            webViewClient = appWebViewClient
            webView.settings.safeBrowsingEnabled = false
            loadUrl(APP_URL)
        }
    }

    private fun openInCustomTab(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    private fun showError(error: String) {
        Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
    }
}