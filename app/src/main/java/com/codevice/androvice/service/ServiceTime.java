package com.codevice.androvice.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.codevice.androvice.MainActivity;
import com.codevice.androvice.R;
import com.codevice.androvice.config.Config;
import com.codevice.androvice.handler.ServiceHandler;
import android.support.annotation.Nullable;

import static com.codevice.androvice.config.Config.FOREGROUND_SERVICE_ID;
import static com.codevice.androvice.config.Config.MSG_UPDATE;
import static com.codevice.androvice.config.Config.NOTIFICATION_ID;

public class ServiceTime extends Service {

    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Send empty message to background thread
//        mServiceHandler.sendEmptyMessageDelayed(0, 500);
//        // or run code in background
//        mServiceHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                sendMessage();
//            }
//        });

        // Prepare to do update reports.
        mServiceHandler.removeMessages(MSG_UPDATE);
        Message msg = mServiceHandler.obtainMessage(MSG_UPDATE);
        mServiceHandler.sendMessageDelayed(msg, 1000);
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();

        // An Android handler thread internally operates on a looper.
        mHandlerThread = new HandlerThread("ServiceTime.HandlerThread");
        mHandlerThread.start();

        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper(), getApplicationContext());
    }

    private void buildNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            String description = "Androvice is working";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel(getPackageName(), name, importance);
            mChannel.setDescription(description);

            mNotificationManager = getSystemService(NotificationManager.class);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        } else {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder = new NotificationCompat.Builder(this, getPackageName())
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle(getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setChannelId(getPackageName())
                .setOngoing(true)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentIntent(resultPendingIntent);
        startForeground(FOREGROUND_SERVICE_ID, mNotificationBuilder.build());
    }

    private void sendMessage() {
        mNotificationBuilder.setContentText("Hello Service");
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());

        // Send broadcast out with action filter and extras
//        Intent intent = new Intent(Config.ACTION_NAME);
//        intent.putExtra("result", "Hello Service");
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        // Cleanup service before destruction
        mHandlerThread.quit();
        super.onDestroy();
    }
}
