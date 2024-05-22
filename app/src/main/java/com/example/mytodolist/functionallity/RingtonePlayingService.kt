package com.example.mytodolist.functionallity

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.mytodolist.R

class RingtonePlayingService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Get the ringtone URI from the intent
        val ringtoneUri = intent?.getStringExtra("ringtone_uri")?.toUri()
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)


        // Create a media player to play the ringtone
        mediaPlayer = MediaPlayer.create(this, ringtoneUri)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        // Start the service in the foreground so that it can continue to play the ringtone even if the app is not in the foreground
        val notification = NotificationCompat.Builder(this, "ringtone_channel")
            .setContentTitle("Ringtone Playing")
            .setContentText("Playing ringtone...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        // Stop the media player and release resources
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}