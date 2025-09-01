package com.partygallery.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.text.font.FontWeight
import com.partygallery.Greeting

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PartyGalleryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        GreetingView(Greeting().greet())
                        VersionInfo()
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingView(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
    )
}

@Composable
fun VersionInfo(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "v${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Build: ${BuildConfig.BUILD_NUMBER}",
            style = MaterialTheme.typography.bodySmall
        )
        if (BuildConfig.IS_CI_BUILD) {
            Text(
                text = "CI Build ✅",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PartyGalleryTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PartyGalleryTheme {
        GreetingView("Hello, Android!")
    }
}