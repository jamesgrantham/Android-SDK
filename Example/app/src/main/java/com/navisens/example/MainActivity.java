package com.navisens.example;

import android.os.Bundle;
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

    private static final String DEVELOPER_KEY = "your developer key";
    private static final String LOG_TAG = "MainActivity";
    private static final int REQUEST_MDNA_PERMISSIONS = 1;
    MotionDnaApplication motionDnaApplication_;

    TextView textView1;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");


    void runDna(String s) {
        Log.d(LOG_TAG, "runDna");
        motionDnaApplication_.runMotionDna(s);
        motionDnaApplication_.setLocationAndHeadingGPSMag();
        motionDnaApplication_.setCallbackUpdateRateInMs(0);
        motionDnaApplication_.setMapCorrectionEnabled(true);
        motionDnaApplication_.setBinaryFileLoggingEnabled(true);
//        motionDnaApplication_.setPowerMode(MotionDna.PowerConsumptionMode.LOW_CONSUMPTION);
        motionDnaApplication_.setExternalPositioningState(MotionDna.ExternalPositioningState.HIGH_ACCURACY);
    }

    //
    @Override
    public void receiveMotionDna(final MotionDna motionDna) {
        Log.d(LOG_TAG, "receiveMotionDna");

        if (motionDna.getID().equals(motionDnaApplication_.getDeviceID())) {
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
            if (motion.secondaryMotion != null&&motion.primaryMotion!=null) {
                recognizedMotion = "\nrecognized:S/" + SecondaryMotionModel.MOTION_NAMES[motion.secondaryMotion.ordinal()]
                        + "\nP/" + PrimaryMotionModel.MOTION_NAMES[motion.primaryMotion.ordinal()];
            }
            textView1.setText( motionDnaApplication_.checkSDKVersion()+"\n"+timeStamp + locationInfo + recognizedMotion);
        }
    }

    @Override
    public void failureToAuthenticate(String s) {
        Log.e(LOG_TAG, "Authentication failed");
        Toast.makeText(MainActivity.this, "Authentication failed",
                Toast.LENGTH_LONG).show();
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (MotionDnaApplication.checkMotionDnaPermissions(this) == true) {
            motionDnaApplication_ = new MotionDnaApplication(this, this);
            runDna(DEVELOPER_KEY);
        }

    }

    //
    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            Log.d(LOG_TAG, "will call motionDnaApplication_.stop");
            if (motionDnaApplication_ != null) motionDnaApplication_.stop();
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
