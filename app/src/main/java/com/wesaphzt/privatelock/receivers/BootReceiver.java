package com.wesaphzt.privatelock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.wesaphzt.privatelock.service.LockService;
import com.wesaphzt.privatelock.widget.LockWidgetProvider;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);;
            if(prefs.getBoolean("START_ON_BOOT", false)) {
                Intent i = new Intent(context, LockService.class);

                //check android api
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(i);
                    LockWidgetProvider lockWidgetProvider = new LockWidgetProvider();
                    lockWidgetProvider.setWidgetStart(context);
                } else {
                    context.startService(i);
                    LockWidgetProvider lockWidgetProvider = new LockWidgetProvider();
                    lockWidgetProvider.setWidgetStart(context);
                }
            }
        }
    }
}
