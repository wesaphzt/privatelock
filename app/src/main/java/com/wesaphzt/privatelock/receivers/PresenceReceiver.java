package com.wesaphzt.privatelock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.wesaphzt.privatelock.service.LockService.disabled;
import static com.wesaphzt.privatelock.service.LockService.mInitialized;

public class PresenceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            //prevent lock animation artifacts
            mInitialized = false;

            disabled = false;

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            disabled = true;
        }
    }
}