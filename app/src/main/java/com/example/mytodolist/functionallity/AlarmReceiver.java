package com.example.mytodolist.functionallity;

import static com.example.mytodolist.functionallity.AlarmFileKt.cancelAlarm;
import static com.example.mytodolist.functionallity.AlarmFileKt.showNotification;
import static com.example.mytodolist.functionallity.AlarmFileKt.updateNotificationAfterMarkComplete;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.mytodolist.database.DBBroadCastHelper;
import com.example.mytodolist.enums.NotifyEnum;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    @Inject
    DBBroadCastHelper dbBroadCastHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Alarm is going on");
        Long todoId = intent.getLongExtra(NotifyEnum.todoId.name(), -1);
        String taskName = intent.getStringExtra("taskName");
        int uniqueNotificationID = intent.getIntExtra("uniqueNotificationID",-1);
        String taskDesc = intent.getStringExtra("taskDesc");

        if (NotifyEnum.ALARMSTART.name().equals(intent.getAction())) {
            handleAlarmStart(context, todoId,uniqueNotificationID, taskName, taskDesc);
        } else if (NotifyEnum.MARKCOMPLETETODO.name().equals(intent.getAction())) {
            handleMarkComplete(context, todoId,uniqueNotificationID, taskName, taskDesc);
        } else if (NotifyEnum.STOPALARM.name().equals(intent.getAction())) {
            handleStopAlarm(context);
        }
    }

    private void handleAlarmStart(Context context, Long todoId,int uniqueNotificationID, String taskName, String taskDesc) {
        if (uniqueNotificationID != -1 && taskName != null && taskDesc != null && todoId!=-1) {
            showNotification(context, todoId,uniqueNotificationID, taskName, taskDesc);

            // Start Ringtone service
            Intent startIntent = new Intent(context, RingtonePlayingService.class);
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            startIntent.putExtra("ringtone-uri", ringtoneUri.toString());
            ContextCompat.startForegroundService(context,startIntent);
            // Cancel the alarm after it starts
            cancelAlarm(context, uniqueNotificationID);
        } else {
            Log.e(TAG, "Invalid alarm data: todoId=" + todoId + ", taskName=" + taskName + ", taskDesc=" + taskDesc);
        }
    }

    private void handleMarkComplete(Context context, Long todoId,int uniqueNotificationID, String taskName, String taskDesc) {
        Log.e(TAG, "Marked complete: " + todoId);
        if (todoId != -1) {
            if (dbBroadCastHelper.setMarkCompleted(todoId)) {
                updateNotificationAfterMarkComplete(context, uniqueNotificationID,
                        (taskName != null) ? taskName : " ",
                        (taskDesc != null) ? taskDesc : " ");
            } else {
                Log.e(TAG, "Failed to mark todo as complete in database: " + todoId);
            }
        } else {
            Log.e(TAG, "Invalid todoId for marking complete: " + todoId);
        }
    }

    private void handleStopAlarm(Context context) {
        Log.e(TAG, "Stopping alarm");
        Intent stopIntent = new Intent(context, RingtonePlayingService.class);
        context.stopService(stopIntent);
    }
}
