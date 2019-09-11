package com.wesaphzt.privatelock.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import android.preference.PreferenceManager;
import android.widget.Toast;

import com.wesaphzt.privatelock.MainActivity;
import com.wesaphzt.privatelock.R;
import com.wesaphzt.privatelock.receivers.DeviceAdminReceiver;
import com.wesaphzt.privatelock.receivers.NotificationReceiver;
import com.wesaphzt.privatelock.receivers.PauseReceiver;
import com.wesaphzt.privatelock.receivers.PresenceReceiver;
import com.wesaphzt.privatelock.widget.LockWidgetProvider;

import static androidx.core.app.NotificationCompat.PRIORITY_LOW;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

public class LockService extends JobIntentService {

    private Context context;

    private float mLastX, mLastY, mLastZ;
    public static boolean mInitialized;
    public static SensorManager mSensorManager;
    private final float NOISE = (float) 2.0;
    public static Sensor mAccelerometer;

    public static SensorEventListener activeListener;
    public static boolean disabled = true;

    //DevicePolicyManager
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdmin;

    //notifications
    Notification notification;
    NotificationManager notificationManager;
    //ids
    public static String CHANNEL_ID;
    public static String CHANNEL_NAME;
    public static final int NOTIFICATION_ID = 1000;
    //intents
    public static PendingIntent pendingIntent;
    public static PendingIntent pendingCloseIntent;
    public static PendingIntent pendingPauseIntent;

    //sensitivity
    public static final int DEFAULT_SENSITIVITY = 10;
    public static int SENSITIVITY;

    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";

    PresenceReceiver presenceReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getApplicationContext();
        CHANNEL_ID = getString(R.string.notification_main_channel_id);
        CHANNEL_NAME = getString(R.string.notification_main_channel_name);
        //------------------------------------------------------------------------------------------
        if (intent != null) {
            String action = intent.getAction();

            LockWidgetProvider lockWidgetProvider = new LockWidgetProvider();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    presenceReceiver = new PresenceReceiver();

                    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
                    intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(presenceReceiver, intentFilter);
                    //------------------------------------------------------------------------------------------
                    try {
                        SENSITIVITY = prefs.getInt(MainActivity.PREFS_THRESHOLD, DEFAULT_SENSITIVITY);
                    } catch (Exception e) {
                        Toast.makeText(context, "Unable to retrieve threshold", Toast.LENGTH_LONG).show();
                    }
                    //------------------------------------------------------------------------------------------
                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    //dpm
                    mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                    mDeviceAdmin = new ComponentName(this, DeviceAdminReceiver.class);

                    //prevent lock animation artifacts
                    mInitialized = false;

                    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
                    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

                    setSensorListener();
                    mSensorManager.registerListener(activeListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

                    setNotification();
                    //------------------------------------------------------------------------------------------
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //create foreground service
                        startForeground(NOTIFICATION_ID, notification);
                        lockWidgetProvider.setWidgetStart(context);
                        disabled = false;

                    } else {
                        notificationManager.notify(NOTIFICATION_ID, notification);
                        lockWidgetProvider.setWidgetStart(context);
                        disabled = false;
                    }

                    break;

                case ACTION_STOP_FOREGROUND_SERVICE:
                    try {
                        mSensorManager.unregisterListener(activeListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    unregisterReceiver(presenceReceiver);

                    disabled = true;
                    mInitialized = false;

                    if (PauseReceiver.isRunning) {
                        PauseReceiver.mCountdown.cancel();
                        PauseReceiver.isRunning = false;
                    }

                    try {
                        NotificationManager notificationManager =
                                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            String id = context.getString(R.string.notification_main_channel_id);
                            notificationManager.deleteNotificationChannel(id);
                        } else {
                            notificationManager.cancel(LockService.NOTIFICATION_ID);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    lockWidgetProvider.setWidgetStop(context);
                    stopService(intent);

                    break;
            }
        } else {
            Toast.makeText(context, "Something went wrong...", Toast.LENGTH_LONG).show();
        }


        return LockService.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {  }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setSensorListener() {
        activeListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if(LockService.disabled)
                    return;

                sensorCalc(event);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {  }
        };
    }

    private void sensorCalc(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (!mInitialized) {
            mLastX = x;
            mLastY = y;
            mLastZ = z;

            mInitialized = true;
        } else {
            float deltaX = Math.abs(mLastX - x);
            float deltaY = Math.abs(mLastY - y);
            float deltaZ = Math.abs(mLastZ - z);

            if (deltaX < NOISE) deltaX = (float) 0.0;
            if (deltaY < NOISE) deltaY = (float) 0.0;
            if (deltaZ < NOISE) deltaZ = (float) 0.0;

            mLastX = x;
            mLastY = y;
            mLastZ = z;

            float total = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

            if (total > SENSITIVITY) {
                try {
                    if (isActiveAdmin()) {
                        mDPM.lockNow();
                    } else {
                        Toast.makeText(context, "Device admin not enabled", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Unknown locking error", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdmin);
    }

    private void setNotification() {
        //notification
        pendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                0);

        //notification stop button
        Intent intentStopAction = new Intent(context, NotificationReceiver.class);
        intentStopAction.putExtra("lock_service","lock_service_notification");
        pendingCloseIntent = PendingIntent.getBroadcast(context,0, intentStopAction, PendingIntent.FLAG_UPDATE_CURRENT);

        //notification pause button
        Intent intentPauseAction = new Intent(context, PauseReceiver.class);
        intentPauseAction.putExtra("pause_service","pause_service_time");
        pendingPauseIntent = PendingIntent.getBroadcast(context,0, intentPauseAction, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            notificationBuilder = new NotificationCompat.Builder(this);
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_lock_white_24dp)
                    .setContentTitle(getString(R.string.app_name) + " is running")
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(getString(R.string.app_name) + " is running")
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "PAUSE", pendingPauseIntent)
                    .setPriority(PRIORITY_LOW)
                    .setOngoing(true)
                    .build();

        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N| Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_lock_white_24dp)
                    .setContentTitle(getString(R.string.app_name) + " is running")
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setColor(getColor(R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(getString(R.string.app_name) + " is running")
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "PAUSE", pendingPauseIntent)
                    .setPriority(PRIORITY_LOW)
                    .setOngoing(true)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setImportance(NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            notificationManager.createNotificationChannel(channel);

            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
            notification = notificationBuilder
                    .setSmallIcon(R.drawable.ic_lock_white_24dp)
                    .setContentTitle(getString(R.string.app_name) + " is running")
                    .setPriority(PRIORITY_MIN)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setColor(getColor(R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .setTicker(getString(R.string.app_name) + " is running")
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "STOP", pendingCloseIntent)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "PAUSE", pendingPauseIntent)
                    .setOngoing(true)
                    .build();
        }
    }
}
