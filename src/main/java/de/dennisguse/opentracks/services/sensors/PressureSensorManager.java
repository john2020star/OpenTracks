package de.dennisguse.opentracks.services.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

import de.dennisguse.opentracks.util.PressureSensorUtils;

/**
 * Estimates the elevation gain and elevation loss using the device's pressure sensor (i.e., barometer).
 * <p>
 * TODO Account for (varying) temporal resolution of the sensor.
 */
public class PressureSensorManager implements SensorEventListener {

    private static final String TAG = PressureSensorManager.class.getSimpleName();

    private boolean isConnected = false;

    //TODO Do we need to synchronize access?
    private List<Float> sensorValues = new ArrayList<>();

    public void start(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (pressureSensor == null) {
            Log.i(TAG, "No pressure sensor available.");
            isConnected = false;
        }

        isConnected = sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public float[] getElevationGainLoss_m() {
        if (!isConnected) {
            return new float[]{Float.NaN, Float.NaN, Float.NaN};
        }

        float[] sensorValuesArray = new float[sensorValues.size()];
        for (int i = 0; i < sensorValues.size(); i++) {
            sensorValuesArray[i] = sensorValues.get(i);
        }

        return PressureSensorUtils.computeChanges_m(sensorValuesArray);
    }

    public float[] getElevationGainLoss_m(Float sensorValueFirst) {
        if (!isConnected) {
            return new float[]{Float.NaN, Float.NaN, Float.NaN};
        }

        float[] sensorValuesArray = new float[sensorValues.size() + 1];

        sensorValuesArray[0] = sensorValueFirst;
        for (int i = 0; i < sensorValues.size(); i++) {
            sensorValuesArray[i + 1] = sensorValues.get(i);
        }

        return PressureSensorUtils.computeChanges_m(sensorValuesArray);
    }

    public void reset() {
        sensorValues.clear();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "PressureSensorManager received");
        sensorValues.add(event.values[0]);
        Log.e(TAG, "PressureSensorManager received " + event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //TODO take accuracy changes into account?
    }

    @VisibleForTesting
    void setSensorValues(List<Float> sensorValues) {
        this.sensorValues = sensorValues;
    }
}
