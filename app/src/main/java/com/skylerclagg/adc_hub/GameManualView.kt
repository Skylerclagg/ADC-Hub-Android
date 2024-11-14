package com.skylerclagg.adc_hub

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.ramcosta.composedestinations.annotation.Destination
import com.skylerclagg.adc_hub.ui.theme.ADCHubTheme

class GameManualActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ADCHubTheme {
                GameManualView()
            }
        }
    }
}
@Destination
@Composable
fun GameManualView(navController: NavController? = null) {
    // Embedding a WebView using AndroidView to display web content
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                loadUrl("https://online.flippingbook.com/view/201482508/")
                settings.javaScriptEnabled = true // Enable JavaScript if needed
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
