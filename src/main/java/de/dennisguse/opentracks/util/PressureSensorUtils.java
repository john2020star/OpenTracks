package de.dennisguse.opentracks.util;

import android.hardware.SensorManager;

public class PressureSensorUtils {

    private PressureSensorUtils() {
    }

    /**
     * Computes the elevation gain and elevation loss.
     *
     * @param sensorValues Unmodified sensor values of a pressure sensor.
     * @return float[3] {elevationGain, elevationLoss, lastSensorValue|0}
     * <p>
     * TODO: Does not take sensor accuracy (temporal resolution in to account).
     */
    public static float[] computeChanges_m(float... sensorValues) {
        float elevationGain = 0;
        float elevationLoss = 0;

        for (int i = 0; i < sensorValues.length - 1; i++) {
            float sensorValueCurrent = sensorValues[i];
            float sensorValueNext = sensorValues[i + 1];

            float elevationCurrent = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, sensorValueCurrent);
            float elevationNext = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, sensorValueNext);

            float elevationDifference = elevationNext - elevationCurrent;

            if (elevationDifference > 0) {
                elevationGain += elevationDifference;
            } else {
                elevationLoss -= elevationDifference;
            }
        }

        return new float[]{elevationGain, elevationLoss, sensorValues.length == 0 ? 0 : sensorValues[sensorValues.length - 1]};
    }
}
