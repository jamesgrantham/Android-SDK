package com.navisens.example;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.navisens.motiondnaapi.MotionDnaApplication;
import com.navisens.motiondnaapi.MotionDnaInterface;

/**
 * Created by Peter on 1/12/17.
 */
public class MotionDnaClient {

    public MotionDnaClient() {

    }

    private static final String DEVELOPER_KEY = "your developer key";

    MotionDnaForegroundService dnaForegroundService;
    private MotionDnaInterface contextDnaInterface;

    boolean isDnaServiceBound;

    public MotionDnaApplication getMotionDnaApplication() {
        if (dnaForegroundService != null) {
            return dnaForegroundService.motionDnaApplication;
        } else {
            return null;
        }
    }

    private ServiceConnection dnaServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            dnaForegroundService = ((MotionDnaForegroundService.LocalMotionDnaServiceBinder) service).getService();
            isDnaServiceBound = true;

            dnaForegroundService.runDna(contextDnaInterface, DEVELOPER_KEY);
        }

        public void onServiceDisconnected(ComponentName className) {
            //---Toast.makeText((Context) contextDnaInterface, "motiondna service disconnected",Toast.LENGTH_LONG).show();
            Log.e(this.getClass().getName(), "motiondna service disconnected");
        }
    };

    public void bindDnaService(Context context) {
        contextDnaInterface = (MotionDnaInterface) context;
        context.bindService(new Intent(context,
                MotionDnaForegroundService.class), dnaServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindDnaService() {
        if (isDnaServiceBound) {
            ((Context) contextDnaInterface).unbindService(dnaServiceConnection);
            isDnaServiceBound = false;
            Log.d(this.getClass().getName(), "motiondna service unbind");
        }
    }


}