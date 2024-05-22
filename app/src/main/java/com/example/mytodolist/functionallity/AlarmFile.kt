package com.example.mytodolist.functionallity

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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mytodolist.R
import com.example.mytodolist.database.dataClass.TodoDataClass

import com.example.mytodolist.enums.NotifyEnum
import java.util.Calendar
import java.util.Random


fun scheduleAlarm(context: Context, todo: TodoDataClass) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        action = NotifyEnum.ALARMSTART.name
        putExtra(NotifyEnum.todoId.name, todo.todoId)
        putExtra(NotifyEnum.todoUniqueId.name, todo.uniqueNotificationID)
        putExtra("taskName", todo.taskName)
        putExtra("taskDesc", todo.taskDesc)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context, todo.uniqueNotificationID,
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

fun cancelAlarm(context: Context, uniqueNotificationID: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        action = NotifyEnum.ALARMSTART.name
        putExtra(NotifyEnum.todoId.name, uniqueNotificationID)

    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,  uniqueNotificationID,
        intent, PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.cancel(pendingIntent)
}

fun updateNotificationAfterMarkComplete(
    context: Context,
    uniqueNotificationID: Int,
    taskName: String,
    taskDesc: String
) {
    val builder = NotificationCompat.Builder(context, NotifyEnum.NotificationChannelID.name)
        .setSmallIcon(R.drawable.app_logo)
        .setContentTitle("Task Completed: $taskName") // Changed title
        .setContentText("Nice work! You've completed the task: $taskDesc") // Changed text
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setOngoing(false)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(uniqueNotificationID, builder.build())
    }
}
fun showNotification(context: Context, todoId: Long,uniqueNotificationID: Int, taskName: String, taskDesc: String) {
    createNotificationChannel(context)


    val intentForMarkComplete = Intent(context, AlarmReceiver::class.java).apply {
        action = NotifyEnum.MARKCOMPLETETODO.name
        putExtra(NotifyEnum.todoId.name, todoId)
        putExtra("taskName", taskName)
        putExtra("taskDesc", taskDesc)
    }


    val pendingIntentMarkComplete = PendingIntent.getBroadcast(
        context, uniqueNotificationID,
        intentForMarkComplete, PendingIntent.FLAG_IMMUTABLE
    )
    val builder = NotificationCompat.Builder(context, NotifyEnum.NotificationChannelID.name)
        .setSmallIcon(R.drawable.app_logo)
        .setContentTitle("Your Task : $taskName")
        .setContentText(taskDesc)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setOngoing(true)
        .addAction(R.drawable.taskdone,"MarkComplete",pendingIntentMarkComplete)

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) { return }
        notify(uniqueNotificationID, builder.build())
    }
}



fun createNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            NotifyEnum.NotificationChannelID.name,
            NotifyEnum.MY_NOTIFICATION_CHANNEL_NAME.name,
            importance
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

    }
}

