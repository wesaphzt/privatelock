package com.wesaphzt.privatelock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.wesaphzt.privatelock.service.LockService;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if(prefs.getBoolean("START_ON_BOOT", false)) {
                Intent startIntent  = new Intent(context, LockService.class);
                startIntent.setAction(LockService.ACTION_START_FOREGROUND_SERVICE);

                //check android api
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(startIntent);
                } else {
                    context.startService(startIntent);
                }
            }
        }
    }
}
