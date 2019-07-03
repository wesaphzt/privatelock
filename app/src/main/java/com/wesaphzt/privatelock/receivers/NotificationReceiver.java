package com.wesaphzt.privatelock.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.wesaphzt.privatelock.service.LockService;
import com.wesaphzt.privatelock.widget.LockWidgetProvider;
import com.wesaphzt.privatelock.R;

import static com.wesaphzt.privatelock.service.LockService.activeListener;
import static com.wesaphzt.privatelock.service.LockService.mSensorManager;
import static com.wesaphzt.privatelock.service.LockService.disabled;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("lock_service");

        if (action.equals("lock_service_notification")) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            LockWidgetProvider lockWidgetProvider = new LockWidgetProvider();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String id = context.getString(R.string.notification_main_channel_id);
                notificationManager.deleteNotificationChannel(id);
                if(PauseReceiver.isRunning) {
                    PauseReceiver.mCountdown.cancel(); PauseReceiver.isRunning = false;
                    disabled = true;
                    mSensorManager.unregisterListener(activeListener);
                    lockWidgetProvider.setWidgetStop(context);
                } else {
                    disabled = true;
                    mSensorManager.unregisterListener(activeListener);
                    lockWidgetProvider.setWidgetStop(context);
                }

            } else {
                notificationManager.cancel(LockService.NOTIFICATION_ID);
                if(PauseReceiver.isRunning) {
                    PauseReceiver.mCountdown.cancel(); PauseReceiver.isRunning = false;
                    disabled = true;
                    mSensorManager.unregisterListener(activeListener);
                    lockWidgetProvider.setWidgetStop(context);
                } else {
                    disabled = true;
                    mSensorManager.unregisterListener(activeListener);
                    lockWidgetProvider.setWidgetStop(context);
                }
            }
        }
    }
}