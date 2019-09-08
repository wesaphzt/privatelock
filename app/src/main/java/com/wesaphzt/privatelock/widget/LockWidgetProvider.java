package com.wesaphzt.privatelock.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.wesaphzt.privatelock.service.LockService;
import com.wesaphzt.privatelock.R;

public class LockWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    public boolean SERVICE_STATUS;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.app_widget);

            //default status
            remoteViews.setTextViewText(R.id.tvWidgetToggle, context.getResources().getString(R.string.widget_start_text));

            Intent intent = new Intent(context, LockWidgetProvider.class);
            intent.setAction(ACTION_WIDGET_RECEIVER);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, 0);

            remoteViews.setOnClickPendingIntent(R.id.llWidget, pendingIntent);

            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            boolean value = prefs.getBoolean(context.getString(R.string.widget_prefs_service_id), false);

            if (value) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(context.getString(R.string.widget_prefs_service_id), false);
                editor.apply();
            }

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_WIDGET_RECEIVER)) {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(context);
            boolean value = prefs.getBoolean(context.getString(R.string.widget_prefs_service_id), false);
            SharedPreferences.Editor editor = prefs.edit();

            SERVICE_STATUS = value;

            //if service is running
            if (SERVICE_STATUS) {
                editor.putBoolean(context.getString(R.string.widget_prefs_service_id), false);
                editor.apply();

                Intent stopIntent  = new Intent(context, LockService.class);
                stopIntent.setAction(LockService.ACTION_STOP_FOREGROUND_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(stopIntent);

                } else {
                    context.startService(stopIntent);
                }

                //if service is not running
            } else {
                editor.putBoolean(context.getString(R.string.widget_prefs_service_id), true);
                editor.commit();

                Intent startIntent  = new Intent(context, LockService.class);
                startIntent.setAction(LockService.ACTION_START_FOREGROUND_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(startIntent);
                } else {
                    context.startService(startIntent);
                }
            }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        boolean value = prefs.getBoolean(context.getString(R.string.widget_prefs_service_id), false);

        if (value) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(context.getString(R.string.widget_prefs_service_id), false);
            editor.apply();
        }

        super.onDeleted(context, appWidgetIds);
    }

    //update widget methods
    public void setWidgetStart(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(context.getString(R.string.widget_prefs_service_id), true).apply();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, LockWidgetProvider.class);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        remoteViews.setTextViewText(R.id.tvWidgetToggle, context.getResources().getString(R.string.widget_stop_text));
        remoteViews.setImageViewResource(R.id.ivWidgetLock, R.drawable.ic_lock_closed_outline_white_24dp);
        remoteViews.setInt(R.id.llWidget, "setBackgroundResource", R.color.colorWidgetStart);

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    public void setWidgetStop(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(context.getString(R.string.widget_prefs_service_id), false).apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, LockWidgetProvider.class);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        remoteViews.setTextViewText(R.id.tvWidgetToggle, context.getResources().getString(R.string.widget_start_text));
        remoteViews.setImageViewResource(R.id.ivWidgetLock, R.drawable.ic_lock_open_outline_white_24dp);
        remoteViews.setInt(R.id.llWidget, "setBackgroundResource", R.color.colorWidgetStop);

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }

    public void setWidgetPause(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(context.getString(R.string.widget_prefs_service_id), true).apply();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, LockWidgetProvider.class);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        remoteViews.setTextViewText(R.id.tvWidgetToggle, context.getResources().getString(R.string.widget_stop_text));
        remoteViews.setImageViewResource(R.id.ivWidgetLock, R.drawable.ic_lock_open_outline_white_24dp);
        remoteViews.setInt(R.id.llWidget, "setBackgroundResource", R.color.colorWidgetPause);

        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }
}