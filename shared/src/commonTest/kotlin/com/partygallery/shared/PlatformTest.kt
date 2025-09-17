package com.partygallery.shared

import com.partygallery.getPlatform
import com.partygallery.Greeting
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertNotNull

/**
 * Basic test for Platform functionality across all supported platforms.
 * 
 * These tests validate that the expect/actual platform implementations work correctly
 * on Android, iOS, Desktop, and Web platforms. Each test ensures platform-specific
 * behavior is properly abstracted while maintaining consistent API contracts.
 */
class PlatformTest {

    /**
     * Tests that the platform name is correctly identified.
     * 
     * This verifies the expect/actual implementation returns a valid platform name
     * and that it's not empty or null across all supported platforms.
     */
    @Test
    fun testPlatformName() {
        val platform = getPlatform()
        assertNotNull(platform, "Platform should not be null")
        assertTrue(platform.name.isNotEmpty(), "Platform name should not be empty")
        
        // Platform name should be one of the expected values
        val validPlatforms = setOf("Android", "iOS", "Java", "JS")
        assertTrue(
            validPlatforms.any { platform.name.contains(it, ignoreCase = true) },
            "Platform name '${platform.name}' should contain one of: $validPlatforms"
        )
    }

    /**
     * Tests that the greeting function works across platforms.
     * 
     * This ensures the multiplatform greeting functionality works consistently
     * and includes the platform-specific name in the output.
     */
    @Test
    fun testGreeting() {
        val greeting = Greeting().greet()
        assertNotNull(greeting, "Greeting should not be null")
        assertTrue(greeting.isNotEmpty(), "Greeting should not be empty")
        
        // Greeting should include "Hello" and the platform name
        assertTrue(greeting.contains("Hello", ignoreCase = true), 
                   "Greeting should contain 'Hello'")
        
        val platform = getPlatform()
        assertTrue(greeting.contains(platform.name), 
                   "Greeting should contain platform name: ${platform.name}")
    }

    /**
     * Tests platform-specific behavior remains consistent.
     * 
     * This test ensures that calling the same functions multiple times
     * returns consistent results, which is important for platform abstraction.
     */
    @Test
    fun testPlatformConsistency() {
        val platform1 = getPlatform()
        val platform2 = getPlatform()
        
        assertTrue(platform1.name == platform2.name, 
                   "Platform name should be consistent across calls")
        
        val greeting1 = Greeting().greet()
        val greeting2 = Greeting().greet()
        
        assertTrue(greeting1 == greeting2, 
                   "Greeting should be consistent across calls")
    }
}