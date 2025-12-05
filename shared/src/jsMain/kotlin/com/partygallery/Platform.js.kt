package com.partygallery

class WasmPlatform : Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = WasmPlatform()
