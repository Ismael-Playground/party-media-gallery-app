package com.partygallery.web

import androidx.compose.runtime.*
import com.partygallery.Greeting
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

fun main() {
    renderComposable(rootElementId = "root") {
        App()
    }
}

@Composable
fun App() {
    Div {
        H1 {
            Text("Party Gallery")
        }
        P {
            Text(Greeting().greet())
        }
    }
}
