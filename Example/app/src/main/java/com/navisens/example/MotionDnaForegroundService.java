package com.navisens.example;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.navisens.motiondnaapi.MotionDna;
import com.navisens.motiondnaapi.MotionDnaApplication;
import com.navisens.motiondnaapi.MotionDnaInterface;

public class MotionDnaForegroundService extends Service {
    public MotionDnaForegroundService() {
    }

    public MotionDnaApplication motionDnaApplication;

    int myTid = android.os.Process.myTid();

    public void runDna(MotionDnaInterface dnaClient, String developerKey) {
        Log.d(this.getClass().getSimpleName(), "runDna");
        if (motionDnaApplication == null) {
            //Log.d(this.getClass().getName(),"dna service priority before change = " + android.os.Process.getThreadPriority(myTid));
            android.os.Process.setThreadPriority(myTid, android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            motionDnaApplication = new MotionDnaApplication(dnaClient);

        }
        Log.d(this.getClass().getName(), "runDna");
        motionDnaApplication.runMotionDna(developerKey);
        motionDnaApplication.setLocationAndHeadingGPSMag();
        motionDnaApplication.setCallbackUpdateRateInMs(0);
        motionDnaApplication.setMapCorrectionEnabled(true);
        motionDnaApplication.setBinaryFileLoggingEnabled(true);
        motionDnaApplication.setExternalPositioningState(MotionDna.ExternalPositioningState.HIGH_ACCURACY);
    }

    private NotificationManager notificationManager;
    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;

    static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                new Intent(),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Navisens")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("Navisens")
                .setContentText("Navisens")
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .getNotification();

        startForeground(NOTIFICATION_ID, notification);

        if (wakeLock == null) {
            wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK, "MotionDnaWakeLock");
        }
        wakeLock.acquire();

        if (wifiLock == null) {
            wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(0x3, "motionDnaWifiLock");
        }
        wifiLock.acquire();

    }

    //takes single client
    public class LocalMotionDnaServiceBinder extends Binder {
        MotionDnaForegroundService getService() {
            return MotionDnaForegroundService.this;
        }
    }

    final IBinder motionDnaServiceBinder = new LocalMotionDnaServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return motionDnaServiceBinder;
    }

    @Override
    public void onDestroy() {

        if (motionDnaApplication != null) {
            motionDnaApplication.stop();
        }

        stopForeground(true);

        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
    }
}
