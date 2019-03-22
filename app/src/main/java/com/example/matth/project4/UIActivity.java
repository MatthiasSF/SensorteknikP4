package com.example.matth.project4;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class UIActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;
    private Sensor stepSensor;
    private ImageView compass;
    private SensorListener sensorListener;
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private float[] rotationMatrix = new float[9];
    private float[] orientation =  new float[3];
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float currentDegree = 0f;
    private float lastDegree = 0f;
    private long lastTime = 0;
    private long stepStartTime = 0;
    private TextView stepTV;
    private TextView nmbrOfStepsTv;
    private String userName;
    private Controller controller;
    private TextView stepsPerSecondTV;
    private Button resetButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);
        initialize();
    }

    private void initialize() {
        controller = new Controller(this);
        sensorListener = new SensorListener();
        compass = findViewById(R.id.ui_image);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepTV = findViewById(R.id.ui_steps);
        nmbrOfStepsTv = findViewById(R.id.nmbrOfStepsTv);
        stepsPerSecondTV = findViewById(R.id.stepsPerSecondTV);
        resetButton = findViewById(R.id.ui_button);
        resetButton.setOnClickListener(new ButtonListener());
        userName = getIntent().getStringExtra("Username");
        controller.setUserName(userName);
        setSensors();
        this.startService(new Intent(this, StepService.class));
        stepStartTime = Calendar.getInstance().getTimeInMillis();
    }
    public void setStepsTV(int steps){
        final String s = steps +"";
        setStepsPerSecond(steps);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nmbrOfStepsTv.setText(s);

            }
        });

    }
    private void setStepsPerSecond(int steps){
        long startTime = TimeUnit.MILLISECONDS.toSeconds(stepStartTime);
        long endTime = TimeUnit.MILLISECONDS.toSeconds(controller.getStepTimestamp());
        double persec = (double) endTime - startTime;
        persec = (double)steps / persec;
        final String ps = String.format("%.1f",persec);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stepsPerSecondTV.setText(ps);
            }
        });
    }
    private void restart(){
        this.startService(new Intent(this, StepService.class));
        stepStartTime = Calendar.getInstance().getTimeInMillis();
        stepsPerSecondTV.setText(0 + "");
        nmbrOfStepsTv.setText(0 + "");
    }
    private void setSensors() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Your device is missing the accelerometer sensor", Toast.LENGTH_LONG).show();
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(sensorListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
        else{
            Toast.makeText(this, "Your device is missing the step detector sensor", Toast.LENGTH_LONG).show();
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(sensorListener, magnetometerSensor, SensorManager.SENSOR_DELAY_UI);
        }else {
            Toast.makeText(this, "Your device is missing the magnetometer sensor", Toast.LENGTH_LONG).show();
        }
    }
    private void compassAnimator(@NonNull float angleInDegrees){

        RotateAnimation rotateAnimation = new RotateAnimation(currentDegree, - angleInDegrees,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(250);
        rotateAnimation.setFillAfter(true);
        compass.startAnimation(rotateAnimation);
        lastDegree = currentDegree;
        currentDegree =  - angleInDegrees;
    }
    private class SensorListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentTime = System.currentTimeMillis();
            if (event.sensor == accelerometerSensor) {
                lastAccelerometer = event.values;
                lastAccelerometerSet = true;
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                double total = Math.sqrt(x*x + y*y + z*z);
                if (total > 20){
                    compassAnimator( currentDegree + 360f);
                    compassAnimator(currentDegree - 360f);
                }
            }
            if (event.sensor == magnetometerSensor){
                lastMagnetometer = event.values;
                lastMagnetometerSet = true;
            }
            if (lastAccelerometerSet && lastMagnetometerSet && currentTime - lastTime >= 250) {
                rotationMatrix = new float[9];
                SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer);
                orientation = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientation);
                float azimuthInRadians = orientation[0];
                float azimuthInDegrees = (float) ((Math.toDegrees(azimuthInRadians) + 360) % 360);
                compassAnimator(azimuthInDegrees);
                lastTime = currentTime;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }
    private class ButtonListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            controller.deleteSteps();
            restart();
        }
    }
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener, accelerometerSensor);
        sensorManager.unregisterListener(sensorListener, magnetometerSensor);
    }
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorListener, accelerometerSensor);
        sensorManager.unregisterListener(sensorListener, magnetometerSensor);
    }
    protected void onResume(){
        super.onResume();
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(sensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Your device is missing the accelerometer sensor", Toast.LENGTH_LONG).show();
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            sensorManager.registerListener(sensorListener, magnetometerSensor, SensorManager.SENSOR_DELAY_UI);
        }else {
            Toast.makeText(this, "Your device is missing the magnetometer sensor", Toast.LENGTH_LONG).show();
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(sensorListener, stepSensor, SensorManager.SENSOR_DELAY_UI);
        }
        else{
            Toast.makeText(this, "Your device is missing the step detector sensor", Toast.LENGTH_LONG).show();
        }
    }
}
