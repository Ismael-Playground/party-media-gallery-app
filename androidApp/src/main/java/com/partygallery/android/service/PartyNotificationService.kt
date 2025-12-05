package com.partygallery.android.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Party notification service placeholder.
 *
 * This service is referenced in AndroidManifest.xml but Firebase is currently disabled.
 * It extends Service to satisfy the manifest requirement while Firebase integration
 * is being configured. Once Firebase is enabled, this should extend FirebaseMessagingService
 * to handle FCM push notifications for party invites and updates.
 */
class PartyNotificationService : Service() {

    companion object {
        private const val TAG = "PartyNotificationService"
    }

    /**
     * Required method for Service class.
     * Returns null as this service doesn't support binding.
     */
    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "PartyNotificationService onBind called")
        return null
    }

    /**
     * Called when the service is created.
     * Currently just logs the creation for debugging purposes.
     */
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "PartyNotificationService created")
    }

    /**
     * Called when the service is started.
     * Currently just logs the start for debugging purposes.
     *
     * @param intent The intent that started the service
     * @param flags Additional data about this start request
     * @param startId Unique integer representing this specific request to start
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "PartyNotificationService started")
        return START_NOT_STICKY // Service doesn't need to restart if killed
    }

    /**
     * Called when the service is destroyed.
     * Currently just logs the destruction for debugging purposes.
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "PartyNotificationService destroyed")
    }
}
