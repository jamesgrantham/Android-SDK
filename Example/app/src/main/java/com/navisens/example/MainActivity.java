package com.navisens.example;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.navisens.motiondnaapi.MotionDna;
import com.navisens.motiondnaapi.MotionDnaApplication;
import com.navisens.motiondnaapi.MotionDnaInterface;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MotionDnaInterface {

    private static final String LOG_TAG = "MainActivity";
    private static final int REQUEST_MDNA_PERMISSIONS = 1;
    ;

    TextView textView1;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    //
    private MotionDnaClient motionDnaClient;
    long receiveCount;
    long firstReceiveTimeMillis = System.currentTimeMillis();

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public PackageManager getPkgManager() {
        return getPackageManager();
    }

    @Override
    public void receiveMotionDna(final MotionDna motionDna) {

        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.d(LOG_TAG, "receiveMotionDna on UI thread");
        } else {
            Log.d(LOG_TAG, "receiveMotionDna NOT on UI thread");
        }

        if (motionDna.getID().equals(motionDnaClient.getMotionDnaApplication().getDeviceID())) {
            String timeStamp = simpleDateFormat.format(new Date());
            MotionDna.Location location = motionDna.getLocation();
            MotionDna.MotionStatistics globalStatistics = motionDna.getMotionStatistics();
            String locationInfo = "\nx:" + location.localLocation.x
                    + "\ny:" + location.localLocation.y
                    + "\nz:" + location.localLocation.z
                    + "\ndwelling:" + globalStatistics.dwelling
                    + "\nwalking:" + globalStatistics.walking
                    + "\nstationary:" + globalStatistics.stationary;

            final MotionDna.Motion motion = motionDna.getMotion();

            String recognizedMotion = null;
            if (motion.secondaryMotion != null && motion.primaryMotion != null) {
                recognizedMotion = "\nrecognized:S/" + SecondaryMotionModel.MOTION_NAMES[motion.secondaryMotion.ordinal()]
                        + "\nP/" + PrimaryMotionModel.MOTION_NAMES[motion.primaryMotion.ordinal()];
            }

            receiveCount++;
            if (receiveCount == 1) {
                firstReceiveTimeMillis = System.currentTimeMillis();
            }
            int ratePerSecond = (int) (receiveCount / ((System.currentTimeMillis() - firstReceiveTimeMillis) / 1000.0));

            textView1.setText(motionDnaClient.getMotionDnaApplication().checkSDKVersion() + "\n" + timeStamp + locationInfo + recognizedMotion + "\nreceiveCount:" + receiveCount + " \n" + ratePerSecond + "/Second");
        }
    }

    @Override
    public void reportError(MotionDna.ErrorCode errorCode, String s) {
        switch (errorCode) {
            case ERROR_AUTHENTICATION_FAILED:
                System.out.println("Error: authentication failed " + s); // Authentication to our servers failed. Email us for information as of why. This causes SDK to shut down.
                break;
            case ERROR_SDK_EXPIRED:
                System.out.println("Error: SDK expired " + s); // SDK hasn't been updated in 1 year. Update your SDK. This causes SDK to shut down.
                break;
            case ERROR_PERMISSIONS:
                System.out.println("Error: permissions not granted " + s); // Some permissions haven't been granted.
                break;
            case ERROR_SENSOR_MISSING:
                System.out.println("Error: sensor missing " + s);// Will be or Accelerometer or Gyroscope, this helps handle incompatible phones. SDK will not work if this triggers.
                break;
            case ERROR_SENSOR_TIMING:
                System.out.println("Error: sensor timing " + s);// Timing between sensor samples is inconsistent, this allows you to handle behaviors appropriately.
                break;
        }
    }
    //
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (MotionDnaApplication.checkMotionDnaPermissions(this) == true) {
            if (motionDnaClient == null) {
                motionDnaClient = new MotionDnaClient();
            }
            motionDnaClient.bindDnaService(this);
        }

    }

    //
    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "onStop(),will unbindDnaService");
        if (motionDnaClient != null) {
            motionDnaClient.unbindDnaService();
        }

        super.onDestroy();
    }

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView1 = (TextView) findViewById(R.id.textView1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(this, MotionDnaApplication.needsRequestingPermissions()
                , REQUEST_MDNA_PERMISSIONS);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
