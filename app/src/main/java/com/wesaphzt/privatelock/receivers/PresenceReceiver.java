package com.wesaphzt.privatelock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.wesaphzt.privatelock.service.LockService.disabled;
import static com.wesaphzt.privatelock.service.LockService.mInitialized;

public class PresenceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            //prevent lock animation artifacts
            mInitialized = false;

            disabled = false;

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            if(! prefs.getBoolean("RUN_CONSTANT", false)) {
                disabled = true;
            }
        }
    }
}