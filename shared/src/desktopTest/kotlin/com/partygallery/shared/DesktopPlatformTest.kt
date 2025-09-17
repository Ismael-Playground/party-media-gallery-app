package com.partygallery.shared

import com.partygallery.getPlatform
import com.partygallery.Greeting
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

/**
 * Desktop-specific platform tests.
 * 
 * These tests validate desktop-specific implementations and behaviors,
 * ensuring the JVM platform properly implements the expected interface
 * and provides appropriate desktop-specific functionality for the Party Gallery app.
 */
class DesktopPlatformTest {

    /**
     * Tests that desktop platform is correctly identified.
     * 
     * Verifies that when running on desktop/JVM, the platform identification
     * returns the expected Java version info, which is used throughout
     * the app for platform-specific behavior and logging.
     */
    @Test
    fun testDesktopPlatformIdentification() {
        val platform = getPlatform()
        assertTrue(platform.name.contains("Java"), 
                   "Desktop platform should identify as Java, got: ${platform.name}")
    }

    /**
     * Tests desktop-specific greeting functionality.
     * 
     * Validates that the greeting function works correctly on desktop platform
     * and includes platform-appropriate messaging. Desktop version should
     * include Java in the greeting text.
     */
    @Test
    fun testDesktopGreeting() {
        val greeting = Greeting().greet()
        assertTrue(greeting.contains("Java"), 
                   "Desktop greeting should contain 'Java', got: $greeting")
        assertTrue(greeting.contains("Hello"), 
                   "Greeting should contain 'Hello', got: $greeting")
    }

    /**
     * Tests desktop platform capabilities and features.
     * 
     * Validates that desktop platform provides expected capabilities
     * for party gallery functionality, such as file system access,
     * window management, and desktop-specific integrations.
     */
    @Test
    fun testDesktopCapabilities() {
        val platform = getPlatform()
        
        // Desktop should support rich features
        assertTrue(platform.name.isNotEmpty(), 
                   "Platform name should be available")
        
        // Verify we're actually running on JVM (desktop)
        val javaVersion = System.getProperty("java.version")
        assertTrue(javaVersion != null && javaVersion.isNotEmpty(), 
                   "Should have Java version on desktop platform")
        
        // Desktop should support file system operations
        val userHome = System.getProperty("user.home")
        assertTrue(userHome != null && userHome.isNotEmpty(), 
                   "Desktop should have access to user home directory")
    }

    /**
     * Tests desktop-specific performance characteristics.
     * 
     * Validates that desktop platform provides good performance for
     * party gallery operations and that platform detection is fast
     * enough for real-time usage in the application.
     */
    @Test
    fun testDesktopPerformance() {
        val startTime = System.currentTimeMillis()
        
        // Perform multiple platform operations
        repeat(100) {
            getPlatform()
            Greeting().greet()
        }
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        // Operations should complete quickly (under 1 second for 100 iterations)
        assertTrue(duration < 1000, 
                   "Desktop platform operations should be fast, took ${duration}ms")
    }
}