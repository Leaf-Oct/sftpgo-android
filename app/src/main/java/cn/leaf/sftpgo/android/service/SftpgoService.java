package cn.leaf.sftpgo.android.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import cn.leaf.sftpgo.android.R;
import cn.leaf.sftpgo.android.activity.MainActivity;
import sftpgo_android.Sftpgo_android;

public class SftpgoService extends Service {

    private static final String CHANNEL_ID = "sftpgo_channel";
//    private static final int NOTIFICATION_ID = 1;
    private HandlerThread sftpgo_thread;
    private Handler handler;

    public static boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sftpgo_thread = new HandlerThread("sftpgo_thread");
        sftpgo_thread.start();
        handler = new Handler(sftpgo_thread.getLooper());
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Sftpgo Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        serviceChannel.setDescription("SFTPGO 前台服务");
        getSystemService(NotificationManager.class).createNotificationChannel(serviceChannel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) {
            return START_NOT_STICKY;
        }
//        startForeground(NOTIFICATION_ID, createNotification());
        startForeground(1, createNotification());
        handler.post(() -> {
            try {
                Sftpgo_android.sftpgoStart(getExternalFilesDir("conf").getAbsolutePath(), new File(getExternalFilesDir("logs"), "sftpgo.log").getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        isRunning = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRunning) {
            Sftpgo_android.sftpgoStop();
        }
        getSystemService(NotificationManager.class).deleteNotificationChannel(CHANNEL_ID);
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SFTPGo 服务")
                .setContentText("SFTPGo 服务正在运行")
                .setSmallIcon(R.mipmap.sftpgo_logo)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }
}