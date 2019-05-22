package com.example.mradw.androidassignment2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer, SensorEventListener {

    private Triangle mTriangle;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private float distance[] = new float[3];
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];
    private float[] accelerometer;
    private float[] magnetometer;
    private int count = 0;
    private long lastTime;
    private float x = 0.0f, y = 0.0f, z = 0.0f;
    private double azimuth = 0.0, pitch = 0.0, roll = 0.0;


    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // initialize a square
        mTriangle = new Triangle();
    }

    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix,0,0,0,-5,0f,0f,0f,0f,1.0f,0.0f);

        // Create a rotation and translation for the cube
        Matrix.setIdentityM(mRotationMatrix, 0);

        // Assign mRotationMatrix a rotation with the seekbar
        Matrix.rotateM(mRotationMatrix,0, (float) this.pitch, 1.0f, 0.0f, 0.0f);

        // Combine the model with the view matrix
        Matrix.multiplyMM(mMVPMatrix,0, mViewMatrix,0,mRotationMatrix,0);

        Matrix.rotateM(mRotationMatrix,0, (float) this.roll,0.0f,1.0f,0.0f);

        Matrix.multiplyMM(mMVPMatrix,0, mMVPMatrix,0,mRotationMatrix,0);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix,0, mProjectionMatrix, 0,mMVPMatrix,0);

        // Translate shape
        Matrix.translateM(mMVPMatrix,0,x,y,z);

        // draw shape
        mTriangle.draw(mMVPMatrix);}


        @Override
        public void onSensorChanged(SensorEvent sensorEvent){
            float[] Newvelocity = new float[3];
            float[] oldVelocity = new float[3];
            final float alpha = 0.8f;
            final float[] gravity = new float[3];
            final float[] linear_acceleration = new float[3];
            final float[] rotationMatrix = new float[9];
            final float[] orientationAngles = new float[3];

            Sensor sensor = sensorEvent.sensor;
            if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                accelerometer = sensorEvent.values;
                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];
                // Remove the gravity contribution with the high-pass filter.
                linear_acceleration[0] = sensorEvent.values[0] - gravity[0];
                linear_acceleration[1] = sensorEvent.values[1] - gravity[1];
                linear_acceleration[2] = sensorEvent.values[2] - gravity[2];

                float timechange = System.currentTimeMillis()-lastTime;
                lastTime = System.currentTimeMillis();
                if (count>100){
                    Newvelocity[0] = oldVelocity[0] + (linear_acceleration[0]*timechange);
                    Newvelocity[1] = oldVelocity[1] + (linear_acceleration[1]*timechange);
                    Newvelocity[2] = oldVelocity[2] + (linear_acceleration[2]*timechange);
                    distance[0] = Newvelocity[0] * timechange;
                    distance[1] = Newvelocity[1] * timechange;
                    distance[2] = Newvelocity[2] * timechange;
                    x = distance[0]/100000f;
                    y = -distance[1]/100000f;
                    z = distance[2]/100000f;

                    oldVelocity = Newvelocity;}
                    count++;
            }

            if(sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magnetometer = sensorEvent.values;
            }
            if (accelerometer != null && magnetometer != null) {
                boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, linear_acceleration, magnetometer);
                if(success){
                    SensorManager.getOrientation(rotationMatrix, orientationAngles);
                    this.azimuth = Math.toDegrees(orientationAngles[0]);
                    this.pitch = Math.toDegrees(orientationAngles[1]);
                    this.roll = Math.toDegrees(orientationAngles[2]);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) { }
        public SensorEventListener getSensorEventListener(){ return this;   }



}
