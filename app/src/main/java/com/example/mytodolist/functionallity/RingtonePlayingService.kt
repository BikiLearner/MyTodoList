package com.example.mytodolist.functionallity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.mytodolist.R
import com.example.mytodolist.enums.NotifyEnum
import java.io.File


class RingtonePlayingService : Service() {
    private val CHANNEL_ID = "ringtone_channel"
    private var mediaPlayer: MediaPlayer? = null
    private var uniqueNotificationID: Int = -1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { it ->
            uniqueNotificationID = it.getIntExtra("uniqueNotificationID", -1)
            val ringtoneUri = it.getStringExtra("ringtone-uri")?.toUri()
            val todoId = it.getLongExtra(NotifyEnum.todoId.name, -1)
            val taskName = it.getStringExtra("taskName") ?: ""
            val taskDesc = it.getStringExtra("taskDesc") ?: ""


            try {
                // Create and start the media player
                val file = File(ringtoneUri.toString())
                if (file.exists()) {
                    mediaPlayer = MediaPlayer.create(applicationContext, ringtoneUri)
                    mediaPlayer?.start()
                }else{
                    mediaPlayer = MediaPlayer.create(applicationContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                    mediaPlayer?.start()
                }
            } catch (e: Exception) {
                Log.e("RingtoneService", "Error initializing media player", e)
                stopSelf(startId)
                return START_NOT_STICKY
            }
            // Create and start the foreground notification
            createNotificationChannel()
            val notification= showNotification(this, todoId, uniqueNotificationID, taskName, taskDesc)

            startForeground(
                uniqueNotificationID,
              notification
            )
        } ?: run {
            stopSelf(startId)
        }
        return START_STICKY
    }

    private fun showNotification(
        context: Context, todoId: Long, uniqueNotificationID: Int,
        taskName: String, taskDesc: String
    ): Notification {
        val intentForMarkComplete = Intent(context, AlarmReceiver::class.java).apply {
            action = NotifyEnum.MARKCOMPLETETODO.name
            putExtra(NotifyEnum.todoId.name, todoId)
            putExtra("taskName", taskName)
            putExtra("taskDesc", taskDesc)
        }
        val intentForStopAlarm = Intent(context, AlarmReceiver::class.java).apply {
            action = NotifyEnum.STOPALARM.name
            putExtra(NotifyEnum.todoId.name, todoId)
            putExtra("taskName", taskName)
            putExtra("taskDesc", taskDesc)
            putExtra(NotifyEnum.todoUniqueId.name, uniqueNotificationID)
        }

        val pendingIntentMarkComplete = PendingIntent.getBroadcast(
            context, uniqueNotificationID, intentForMarkComplete, PendingIntent.FLAG_IMMUTABLE
        )
        val pendingIntentStopAlarm = PendingIntent.getBroadcast(
            context, uniqueNotificationID, intentForStopAlarm, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle("Your Task: $taskName")
            .setContentText(taskDesc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setOngoing(true)
            .addAction(R.drawable.taskdone, "Mark Complete", pendingIntentMarkComplete)
            .addAction(0, "Stop Alarm", pendingIntentStopAlarm)
            .build()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Ringtone Service Channel", NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null

        if (uniqueNotificationID != -1) {
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.cancel(uniqueNotificationID)
        }
    }
}
