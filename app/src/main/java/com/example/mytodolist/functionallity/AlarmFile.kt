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
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mytodolist.R
import com.example.mytodolist.database.dataClass.TodoDataClass
import com.example.mytodolist.enums.NotifyEnum
import java.util.Calendar
import java.util.Date


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

    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP, todo.startTime,
        pendingIntent
    )

}

fun setForEveryWeekParticularDays(context: Context, todo: TodoDataClass, range: List<Int>) {
    Log.e("EveryDay", "setForEveryWeekParticularDays: ")
    range.forEach {i->
        setForDay(context, todo, i)
    }
}

fun setForDay(context: Context, todo: TodoDataClass, dayOfWeek: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, AlarmReceiver::class.java).apply {
        action = NotifyEnum.ALARMSTART.name
        putExtra(NotifyEnum.todoId.name, todo.todoId)
        putExtra(NotifyEnum.todoUniqueId.name, todo.uniqueNotificationID)
        when {
            todo.taskName.isNotEmpty() -> putExtra("taskName", todo.taskName)
            todo.taskDesc.isNotEmpty() -> putExtra("taskDesc", todo.taskDesc)
        }
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context, todo.uniqueNotificationID,
        intent, PendingIntent.FLAG_IMMUTABLE
    )



    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP, todo.startTime, AlarmManager.INTERVAL_DAY * 7,
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
        context, uniqueNotificationID,
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
    val notification = NotificationCompat.Builder(context, NotifyEnum.NotificationChannelID.name)
        .setSmallIcon(R.drawable.app_logo)
        .setContentTitle("Task Completed: $taskName") // Changed title
        .setContentText("Nice work! You've completed the task: $taskDesc") // Changed text
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_REMINDER)
        .setOngoing(false)
        .build()

    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notify(uniqueNotificationID, notification)
    }
}

fun showNotification(
    context: Context,
    todoId: Long,
    uniqueNotificationID: Int,
    taskName: String,
    taskDesc: String
) {
    createNotificationChannel(context)


    val intentForMarkComplete = Intent(context, AlarmReceiver::class.java).apply {
        action = NotifyEnum.MARKCOMPLETETODO.name
        putExtra(NotifyEnum.todoId.name, todoId)
        putExtra(NotifyEnum.todoUniqueId.name, uniqueNotificationID)
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
        .addAction(R.drawable.taskdone, "MarkComplete", pendingIntentMarkComplete)

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

