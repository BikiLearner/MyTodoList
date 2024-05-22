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

class RingtonePlayingService : Service() {
    private val CHANNEL_ID = "ringtone_channel"
    private var mediaPlayer: MediaPlayer? = null
    private var uniqueNotificationID : Int = -1

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Get the ringtone URI from the intent
        uniqueNotificationID = intent!!.getIntExtra("uniqueNotificationID", -1)
        val ringtoneUri = intent.getStringExtra("ringtone-uri")?.toUri()
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val todoId=intent.getLongExtra(NotifyEnum.todoId.name, -1)
        Log.d("RingtoneService", "Ringtone TOdoID: $todoId")

        // Create a media player to play the ringtone
        mediaPlayer = MediaPlayer.create(this, ringtoneUri)
        Log.d("RingtoneService", "Media player created: $mediaPlayer")
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
        createNotificationChannel()
        // Start the service in the foreground so that it can continue to play the ringtone even if the app is not in the foreground

        startForeground(uniqueNotificationID, showNotification(
            this,
            todoId,
            uniqueNotificationID,
            intent.getStringExtra("taskName") ?: "",
            intent.getStringExtra("taskDesc") ?: ""
        ))



        return START_STICKY
    }

    private fun showNotification(
        context: Context, todoId: Long, uniqueNotificationID:
        Int, taskName: String, taskDesc: String
    ): Notification {
        createNotificationChannel(context)


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
            context, uniqueNotificationID,
            intentForMarkComplete, PendingIntent.FLAG_IMMUTABLE
        )
        val pendingIntentStopAlarm = PendingIntent.getBroadcast(
            context, uniqueNotificationID,
            intentForStopAlarm, PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, NotifyEnum.NotificationChannelID.name)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle("Your Task : $taskName")
            .setContentText(taskDesc)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setOngoing(true)
            .addAction(R.drawable.taskdone, "MarkComplete", pendingIntentMarkComplete)
            .addAction(0, "Stop Alarm", pendingIntentStopAlarm)
            .build()

        return notification;
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Ringtone Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.cancel(uniqueNotificationID)
        // Stop the media player and release resources
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }


}