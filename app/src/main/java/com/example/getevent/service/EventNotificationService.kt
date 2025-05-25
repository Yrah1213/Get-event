package com.example.getevent.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.getevent.MainActivity
import com.example.getevent.R
import com.example.getevent.data.Event
import com.example.getevent.data.EventDatabase
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class EventNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val eventDao = EventDatabase.getDatabase(context).eventDao()
        val currentTime = System.currentTimeMillis()
        val events = eventDao.getUpcomingEvents(java.util.Date(currentTime)).value

        events?.forEach { event ->
            if (shouldNotify(event)) {
                showNotification(event)
            }
        }

        return Result.success()
    }

    private fun shouldNotify(event: Event): Boolean {
        val currentTime = System.currentTimeMillis()
        val eventTime = event.date.time
        val timeDiff = eventTime - currentTime

        // Notifier 1 heure avant l'événement
        return timeDiff in 0..3600000
    }

    private fun showNotification(event: Event) {
        createNotificationChannel()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle(event.title)
            .setContentText("L'événement commence dans moins d'une heure à ${event.location}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(Random.nextInt(), notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Événements GET"
            val descriptionText = "Notifications pour les événements à venir"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "event_notifications"

        fun scheduleWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<EventNotificationWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "event_notifications",
                    ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
        }
    }
} 