package com.partygallery

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
