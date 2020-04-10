package de.dennisguse.opentracks.services.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import de.dennisguse.opentracks.util.PressureSensorUtils;

/**
 * Estimates the elevation gain and elevation loss using the device's pressure sensor (i.e., barometer).
 * <p>
 * TODO Account for (varying) temporal resolution of the sensor.
 */
public class PressureSensorManager implements SensorEventListener {

    private static final String TAG = PressureSensorManager.class.getSimpleName();

    private boolean isConnected = false;

    private float previousSensorValue;

    private float elevationGain;
    private float elevationLoss;

    public void start(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (pressureSensor == null) {
            Log.i(TAG, "No pressure sensor available.");
            isConnected = false;
        }

        isConnected = sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        reset();
    }

    public void stop(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);

        isConnected = false;
        reset();
    }

    public boolean isConnected() {
        return isConnected;
    }

    public float getElevationGain() {
        return elevationGain;
    }

    public float getElevationLoss() {
        return elevationLoss;
    }

    public void reset() {
        previousSensorValue = Float.NaN;
        elevationGain = 0;
        elevationLoss = 0;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "PressureSensorManager received");
        onSensorValueChanged(event.values[0]);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO take accuracy changes into account?
    }

    @VisibleForTesting
    void onSensorValueChanged(float value) {
        Log.e(TAG, "PressureSensorManager received " + value);
        if (!Float.isNaN(previousSensorValue)) {
            float[] gainValues = PressureSensorUtils.computeChanges_m(previousSensorValue, value);
            elevationGain += gainValues[0];
            elevationLoss += gainValues[1];

            Log.e(TAG, "Gain: " + elevationGain);
        }

        previousSensorValue = value;
    }
}
