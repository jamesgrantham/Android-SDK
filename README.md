### Quick Start

Put your developer key in ./Example/app/src/main/java/com/navisens/example/MotionDnaClient.java
```
private static final String DEVELOPER_KEY = "your developer key";
```
For version 0.1-beta, put your developer key in ./Example/app/src/main/java/com/navisens/example/MainActivity.java
```
private static final String DEVELOPER_KEY = "your developer key";
```

Sync project with gradle and run it

### Documentation

http://docs.navisens.com

### aar download

https://oss.sonatype.org/content/groups/public/com/navisens/motiondnaapi/

### Usage
1.Configure the project build.gradle to add two maven URLs
```
allprojects {
    repositories {
        ...
        maven {
            url 'https://oss.sonatype.org/content/groups/public'
        }
        maven { 
            url 'https://maven.fabric.io/public'
        }
    }
}
```
2.Configure the module build.gradle to add two dependencies 

```

dependencies {
    compile group: "com.navisens", name: "motiondnaapi", version: "0.2-SNAPSHOT", changing: true
    compile 'org.altbeacon:android-beacon-library:2.+'
    ...
}
```

for version 0.1-beta
```

dependencies {
    compile group: "com.navisens", name: "motiondnaapi", version: "0.1-SNAPSHOT", changing: true
    compile 'org.altbeacon:android-beacon-library:2.+'
    ...
}
```
####3.Doze and power saving mode<br />
This example works with power saving mode, works with doze mode in Android 6.0.1 and later.<br />
We are going to push a workaround for doze bug in Android 6.0 (https://code.google.com/p/android/issues/detail?id=193802)

####4.Without MotionDnaForegroundService

MainActivity.java, replace
```
package com.navisens.example;

import android.app.Service;
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
import java.util.List;

public class MainActivity extends AppCompatActivity implements MotionDnaInterface {
    private static final String DEVELOPER_KEY = "your developer key";
    private static final String LOG_TAG = "MainActivity";
    private static final int REQUEST_MDNA_PERMISSIONS = 1;
    ;

    TextView textView1;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    //
    private MotionDnaApplication motionDnaApplication;
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

    void runDna(String s) {
        Log.d(LOG_TAG, "runDna");
        motionDnaApplication.runMotionDna(s);
        motionDnaApplication.setLocationAndHeadingGPSMag();
        motionDnaApplication.setCallbackUpdateRateInMs(0);
        motionDnaApplication.setMapCorrectionEnabled(true);
        motionDnaApplication.setBinaryFileLoggingEnabled(true);
        motionDnaApplication.setExternalPositioningState(MotionDna.ExternalPositioningState.HIGH_ACCURACY);
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

            textView1.setText(motionDnaApplication.checkSDKVersion() + "\n" + timeStamp + locationInfo + recognizedMotion + "\nreceiveCount:" + receiveCount + " \n" + ratePerSecond + "/Second");
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
            if (motionDnaApplication == null) {
                motionDnaApplication = new MotionDnaApplication(this);
            }
           runDna(DEVELOPER_KEY);
        }
    }

    //
    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            Log.d(LOG_TAG, "will call motionDnaApplication_.stop");
            if (motionDnaApplication != null) {
                motionDnaApplication.stop();
            }
        }
        super.onDestroy();
    }

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

```


### Change Log

####January 20, 2017
Changed:<br />
1.The MotionDnaApplication class' constructor has been changed to 
```
public MotionDnaApplication(MotionDnaInterface motionDna)
```
2.Added two methods in MotionDnaInterface
```
    public Context getAppContext();
    public PackageManager getPkgManager();
```
implementing the interface's two methods
```
@Override
    public Context getAppContext() {
        return getApplicationContext();
}

@Override
    public PackageManager getPkgManager() {
        return getPackageManager();
}
```






