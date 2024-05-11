package com.example.mytodolist.functionallity;

import static com.example.mytodolist.functionallity.AlarmFileKt.cancelAlarm;
import static com.example.mytodolist.functionallity.AlarmFileKt.showNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private Ringtone ringtonePlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Alarm is going on");

        if ("com.example.mytodolist.ALARM".equals(intent.getAction())) {
            int todoId = intent.getIntExtra("todoId", -1);
            String taskName = intent.getStringExtra("taskName");
            String taskDesc = intent.getStringExtra("taskDesc");

            if (todoId != -1 && taskName != null && taskDesc != null) {
                showNotification(context, todoId, taskName, taskDesc);
//                Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//                ringtonePlayer = RingtoneManager.getRingtone(context, ringtoneUri);
//                if (ringtonePlayer != null) {
//                    ringtonePlayer.play();
//                }
                cancelAlarm(context,todoId);
            }
        }
//        else if ("StopAlarm".equals(intent.getAction())) {
//            int todoId = intent.getIntExtra("todoId", -1);
//            if (todoId != -1) {
////                if (ringtonePlayer != null && ringtonePlayer.isPlaying()) {
////                    ringtonePlayer.stop();
////                }
//                cancelAlarm(context, todoId);
//            }
//        }
    }
}
