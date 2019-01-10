package com.example.matth.project4;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UIActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;
    private Sensor stepSensor;
    private ImageView compass;
    private SensorListener sensorListener;
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private boolean isFirstValue = false;
    private float[] rotationMatrix = new float[9];
    private float[] orientation =  new float[3];
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float currentDegree = 0f;
    private float x = 0f;
    private float y = 0f;
    private float z = 0f;
    private float last_x = 0f;
    private float last_y = 0f;
    private float last_z = 0f;
    private float shakeThreshold = 1f;
    private int steps = 0;
    private TextView stepTV;
    private String userName;
    private Controller controller;
    private TextView stepsPerSecondTV;
    private double stepsPerSecond;
    private ArrayList<Double> times = new ArrayList<Double>();
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
        stepsPerSecondTV = findViewById(R.id.ui_stepsPerSecondTV);
        userName = getIntent().getStringExtra("Username");
        getStepHistory();
        setSensors();
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
    private void compassAnimator(@NonNull SensorEvent event){

        if (event.sensor == accelerometerSensor) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0,event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometerSensor) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0,event.values.length);
            lastMagnetometerSet = true;
        }
        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null,lastAccelerometer, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);
            float azimuthInRadians = orientation[0];
            float azimuthInDegrees = (float)(Math.toDegrees(azimuthInRadians) + 360) % 360;
            RotateAnimation rotateAnimation =
                    new RotateAnimation(currentDegree, -azimuthInDegrees,Animation.RELATIVE_TO_SELF,
                            0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(5000);
            rotateAnimation.setFillAfter(true);
            compass.startAnimation(rotateAnimation);
            currentDegree = -azimuthInDegrees;
            lastAccelerometerSet = false;
        }
    }
    private void updateStepView(int steps){
        stepTV.setText(R.string.ui_text + steps);
    }
    private void getStepHistory(){
        steps = controller.getSteps(userName);
        updateStepView(steps);
    }
    private void saveStepHistory(int steps){
        controller.setSteps(steps, userName);
    }
    private void updateStepsPerSecondView(double stepsPerSecond){
        stepsPerSecondTV.setText(R.string.steps_per_second + String.valueOf(stepsPerSecond));
    }
    private void calculateStepsPerSecond(){

    }
    private class SensorListener implements SensorEventListener{
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == accelerometerSensor){
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                if (isFirstValue){
                    float deltaX = Math.abs(last_x - x);
                    float deltaY = Math.abs(last_y - y);
                    float deltaZ = Math.abs(last_z - z);
                    if ((deltaX > shakeThreshold && deltaY > shakeThreshold)
                            || (deltaX > shakeThreshold && deltaZ > shakeThreshold)
                            || (deltaY > shakeThreshold && deltaZ > shakeThreshold)){
                        compassAnimator(event);
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
                isFirstValue = true;
            }
            if (event.sensor == magnetometerSensor){
                compassAnimator(event);
            }
            if (event.sensor == stepSensor){
                steps ++;
                updateStepView(steps);
                double time = System.currentTimeMillis()/100;
                times.add(time);
                calculateStepsPerSecond();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    }
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener, accelerometerSensor);
        sensorManager.unregisterListener(sensorListener, magnetometerSensor);
        saveStepHistory(steps);
    }
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(sensorListener, accelerometerSensor);
        sensorManager.unregisterListener(sensorListener, magnetometerSensor);
        saveStepHistory(steps);
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
