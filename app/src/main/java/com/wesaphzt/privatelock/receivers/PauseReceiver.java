package com.wesaphzt.privatelock.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.wesaphzt.privatelock.widget.LockWidgetProvider;

import static com.wesaphzt.privatelock.service.LockService.disabled;
import static com.wesaphzt.privatelock.service.LockService.mInitialized;

public class PauseReceiver extends BroadcastReceiver {

    public static CountDownTimer mCountdown;
    public static boolean isRunning = false;

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getStringExtra("pause_service");

        if (action.equals("pause_service_time")) {
            //close notification tray
            Intent i = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(i);

            if(disabled) {
                Toast.makeText(context, "Service is already paused", Toast.LENGTH_SHORT).show();
                return;
            } else {
                disabled = true;
                LockWidgetProvider lockWidgetProvider = new LockWidgetProvider();
                lockWidgetProvider.setWidgetPause(context);
            }

            //shared prefs
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            int pauseMinuteTime = Integer.parseInt(prefs.getString("PAUSE_TIME", String.valueOf(1)));
            int milliPauseTime = pauseMinuteTime * 60 * 1000;

            String plural;
            if(pauseMinuteTime == 1) { plural = "minute"; } else { plural = "minutes"; }

            Toast.makeText(context, "Service paused for " + pauseMinuteTime + " " + plural, Toast.LENGTH_LONG).show();

            mCountdown = new CountDownTimer(milliPauseTime, 1000) {
                public void onTick(long millisUntilFinished) {
                    isRunning = true;
                }
                public void onFinish() {
                    //prevent lock animation artifacts
                    mInitialized = false;
                    //init
                    disabled = false;
                    isRunning = false;
                    LockWidgetProvider lockWidgetProvider = new LockWidgetProvider();
                    lockWidgetProvider.setWidgetStart(context);

                    Toast.makeText(context, "Service resumed", Toast.LENGTH_LONG).show();
                }
            }.start();
        }
    }
}