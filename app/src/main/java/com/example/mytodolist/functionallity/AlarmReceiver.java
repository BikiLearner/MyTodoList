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
        Log.e("TODOIDNEw", "todoId: " + todoId);
        String taskName = intent.getStringExtra("taskName");
        int uniqueNotificationID = intent.getIntExtra(NotifyEnum.todoUniqueId.name(),-1);
        String taskDesc = intent.getStringExtra("taskDesc");

        if (NotifyEnum.ALARMSTART.name().equals(intent.getAction())) {
            handleAlarmStart(context, todoId,uniqueNotificationID, taskName, taskDesc);
        } else if (NotifyEnum.MARKCOMPLETETODO.name().equals(intent.getAction())) {
            handleMarkComplete(context, todoId,uniqueNotificationID, taskName, taskDesc);
        } else if (NotifyEnum.STOPALARM.name().equals(intent.getAction())) {
            handleStopAlarm(context, todoId,uniqueNotificationID, taskName, taskDesc);
        }
    }

    private void handleAlarmStart(Context context, Long todoId, int uniqueNotificationID, String taskName, String taskDesc) {
        if (uniqueNotificationID != -1 && taskName != null && taskDesc != null && todoId != -1) {

            Intent startIntent = new Intent(context, RingtonePlayingService.class);
            Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            startIntent.putExtra("ringtone-uri", ringtoneUri.toString());
            startIntent.putExtra("uniqueNotificationID", uniqueNotificationID);
            startIntent.putExtra(NotifyEnum.todoId.name(), todoId);
            startIntent.putExtra("taskName", taskName);
            startIntent.putExtra("taskDesc", taskDesc);
            ContextCompat.startForegroundService(context, startIntent);

            cancelAlarm(context, uniqueNotificationID);
        } else {
            Log.e(TAG, "Invalid alarm data: todoId=" + todoId + ", taskName=" + taskName + ", taskDesc=" + taskDesc);
        }
    }

    private void handleMarkComplete(Context context, Long todoId,int uniqueNotificationID, String taskName, String taskDesc) {
        Log.e(TAG, "Marked complete: " + todoId);
        Log.e("InsideMarkCompelte", "Marked complete: " + todoId);
        if (todoId != -1) {
            if (dbBroadCastHelper.setMarkCompleted(todoId)) {
                updateNotificationAfterMarkComplete(context, uniqueNotificationID,
                        (taskName != null) ? taskName : " ",
                        (taskDesc != null) ? taskDesc : " ");
            } else {
                Log.e("InsideMarkCompelte", "Failed to mark todo as complete in database: " + todoId);
                // Show an error notification or log an error message here
            }
        } else {
            Log.e("InsideMarkCompelte", "Invalid todoId for marking complete: " + todoId);
        }
    }

    private void handleStopAlarm(Context context, Long todoId, int uniqueNotificationID, String taskName, String taskDesc) {
        Log.e(TAG, "Stopping alarm");
        Intent stopIntent = new Intent(context, RingtonePlayingService.class);
        context.stopService(stopIntent);
        showNotification(context,todoId,uniqueNotificationID+1, taskName, taskDesc);
    }
}
