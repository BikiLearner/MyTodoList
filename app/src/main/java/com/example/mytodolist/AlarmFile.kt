package com.example.mytodolist

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mytodolist.database.TodoDataClass
import java.util.Calendar

const val CANNELID = "MyNotification"
lateinit var builder:NotificationCompat.Builder
fun scheduleAlarm(context: Context, todo: TodoDataClass) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        action = "com.example.mytodolist.ALARM"
        putExtra("todoId", todo.todoId)
        putExtra("taskName", todo.taskName)
        putExtra("taskDesc", todo.taskDesc)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context, todo.todoId,
        intent, PendingIntent.FLAG_IMMUTABLE
    )


    val calendar = Calendar.getInstance().apply {
        time = todo.date
        set(Calendar.HOUR_OF_DAY, todo.startTime.hours)
        set(Calendar.MINUTE, todo.startTime.minutes)
        set(Calendar.SECOND, 0)
    }

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
        pendingIntent
    )

}

fun cancelAlarm(context: Context, todoId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        action = "com.example.mytodolist.ALARM"
        putExtra("todoId", todoId)

    }
    val pendingIntent = PendingIntent.getBroadcast(
        context, todoId,
        intent, PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}

fun showNotification(context: Context, todoId: Int, taskName: String, taskDesc: String) {
    createNotificationChannel(context)


    val intent1 = Intent(context, AlarmReceiver::class.java).apply {
        action = "StopAlarm"
        putExtra("todoId", todoId)
    }


    val pendingIntentStopAlarm = PendingIntent.getBroadcast(
        context, todoId,
        intent1, PendingIntent.FLAG_IMMUTABLE
    )
    builder = NotificationCompat.Builder(context, CANNELID)
        .setSmallIcon(R.drawable.app_logo)
        .setContentTitle(taskName)
        .setContentText(taskDesc)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(true)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(todoId, builder.build())
    }
}


fun createNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            CANNELID,
            "MyNotificationG88",
            importance
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

    }


}

