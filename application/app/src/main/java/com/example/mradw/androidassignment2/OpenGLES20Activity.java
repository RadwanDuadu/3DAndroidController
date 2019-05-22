package com.example.mradw.androidassignment2;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

public class OpenGLES20Activity extends Activity {

    private MyGLSurfaceView mGLView;
    private Sensor accelerometerSensor;
    private Sensor magneticSensor;
    private SensorManager SM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it as the ContentView for this Activity.
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);

        // Create our Sensor Manager
        //manages used sensors
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        // Accelerometer
        accelerometerSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Magnetic
        magneticSensor = SM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onPause() {
        // The following call pauses the rendering thread.
        super.onPause();
        mGLView.onPause();
        SM.unregisterListener(mGLView.getmRenderer().getSensorEventListener());
    }

    @Override
    protected void onResume() {
        // resumes a paused rendering thread.
        super.onResume();
        mGLView.onResume();
        // Register sensor listener
        SM.registerListener(mGLView.getmRenderer().getSensorEventListener(), accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        SM.registerListener(mGLView.getmRenderer().getSensorEventListener(), magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

}
