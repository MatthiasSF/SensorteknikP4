package com.example.matth.project4;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.widget.Toast;
import java.util.Calendar;

public class StepService extends Service implements SensorEventListener {
    private static final String TAG = "StepService";
    private LocalBinder binder;
    private Controller controller;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isAccelerometerPresent;
    private Sensor stepDetectorSensor;
    private boolean isStepDetectorPresent;
    private String username;
    private int steps;

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new LocalBinder();
        controller = controller.getInstance(getApplicationContext());
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccelerometerPresent = true;
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            isStepDetectorPresent = true;
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        username = controller.getUserName();
        steps = 0;
        Toast.makeText(this, "The service was started",Toast.LENGTH_LONG).show();
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "The service was binded",Toast.LENGTH_LONG).show();
        return binder;
    }

    @Override
    public boolean stopService(Intent name) {
        if (isStepDetectorPresent) sensorManager.unregisterListener(this, stepDetectorSensor);
        if (isAccelerometerPresent) sensorManager.unregisterListener(this, accelerometerSensor);
        Toast.makeText(this, "The service was stopped",Toast.LENGTH_LONG).show();
        return super.stopService(name);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(this, "The service was unbinded",Toast.LENGTH_LONG).show();
        return super.onUnbind(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == stepDetectorSensor){
            long date = Calendar.getInstance().getTimeInMillis();
            steps ++;
            controller.setSteps(steps, username);
            controller.setTimeStamp(date);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    public class LocalBinder extends Binder {
        StepService getService() {
            return StepService.this;
        }
    }
}
