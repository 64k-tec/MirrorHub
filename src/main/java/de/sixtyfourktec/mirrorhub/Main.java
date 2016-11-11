/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Christian Pötzsch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.sixtyfourktec.mirrorhub;

import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;
import de.sixtyfourktec.mirrorhub.modules.ModuleManager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import android.annotation.TargetApi;

import java.util.ArrayList;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.os.Handler;

public class Main extends Activity
    implements ActivityCompat.OnRequestPermissionsResultCallback, SensorEventListener {

    private static final String TAG = "MirrorHub";

    private static final int PERMISSIONS_REQUEST_READ_CALENDAR = 1;

    private ModuleManager moduleManager;

    private SensorManager sensorManager = null;
    private Sensor proximitySensor;
    private Handler dimHandler;
    private static final int dimDelay = 1000 * 10;

    private Runnable dimRunnable = new Runnable() {
        @Override
        public void run() {
            WindowManager.LayoutParams l = getWindow().getAttributes();
            l.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
            getWindow().setAttributes(l);
        }
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG, "Sensors: " + event.values[0] + "(" + proximitySensor.getMaximumRange() + ")");
        if (event.values[0] == 0.0) {
            dimHandler.removeCallbacks(dimRunnable);
            WindowManager.LayoutParams l = getWindow().getAttributes();
            l.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
            getWindow().setAttributes(l);
            dimHandler.postDelayed(dimRunnable, dimDelay);
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode,
            String[] permissions,
            int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_CALENDAR) {
            if (grantResults.length == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Calendar permission has now been granted.");
                moduleManager.enableCalendarModule();
            } else {
                Log.i(TAG, "Calendar permission was NOT granted.");
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @TargetApi(19)
    private void setFullscreenLevel19() {
        getWindow().getDecorView()
            .setSystemUiVisibility(
                      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION        // API 14; hide nav bar
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE          // API 16
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // API 16
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN      // API 16
                    | View.SYSTEM_UI_FLAG_FULLSCREEN             // API 16; hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);     // API 19
    }

    @TargetApi(16)
    private void setFullscreenLevel16() {
        getWindow().getDecorView()
            .setSystemUiVisibility(
                      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION        // API 14; hide nav bar
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE          // API 16
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // API 16
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN      // API 16
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);           // API 16; hide status bar
    }

    @TargetApi(14)
    private void setFullscreenLevel14() {
        getWindow().getDecorView()
            .setSystemUiVisibility(
                      View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);      // API 14; hide nav bar
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Starting MirrorHub v" + BuildConfig.VERSION_NAME +
                " (" + BuildConfig.BUILD_TIME + "/" + BuildConfig.GIT_SHA +")");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.main);

        moduleManager = ModuleManager.init(this);

        if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    PERMISSIONS_REQUEST_READ_CALENDAR);
        }
        else
            moduleManager.enableCalendarModule();

        if (BuildConfig.ENABLE_PROXIMITY_SENSOR) {
            sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            dimHandler = new Handler();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        moduleManager.start();

        if (sensorManager != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            dimHandler.postDelayed(dimRunnable, dimDelay);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            dimHandler.removeCallbacks(dimRunnable);
        }

        moduleManager.stop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= 19)
                setFullscreenLevel19();
            else if (Build.VERSION.SDK_INT >= 16)
                setFullscreenLevel16();
            else if (Build.VERSION.SDK_INT >= 14)
                setFullscreenLevel14();
            else
                getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
