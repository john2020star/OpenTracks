package de.dennisguse.opentracks.services.sensors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * NOTE: Test data is completely artificial.
 */
public class PressureSensorManagerTest {

    private PressureSensorManager pressureSensorManager = new PressureSensorManager();

    private static void addSensorValue(PressureSensorManager pressureSensorManager, float[] values) {
        for (float f : values) {
            pressureSensorManager.onSensorValueChanged(f);
        }
    }

    @Before
    public void setUp() {
        pressureSensorManager.reset();
    }

    @Test
    public void getElevationGainLoss_withoutPrior() {
        // then
        addSensorValue(pressureSensorManager, new float[]{1000f, 1000.01f, 1000.0f, 999.9f, 999.85f});

        // then
        Assert.assertEquals(1.34, pressureSensorManager.getElevationGain(), 0.01);
        Assert.assertEquals(0.08, pressureSensorManager.getElevationLoss(), 0.01);
    }

    @Test
    public void getElevationGainLoss_withPrior() {
        // when
        addSensorValue(pressureSensorManager, new float[]{1000f, 1000.01f, 1000.0f, 999.9f, 999.85f});

        // then
        Assert.assertEquals(1.34, pressureSensorManager.getElevationGain(), 0.01);
        Assert.assertEquals(0.92, pressureSensorManager.getElevationLoss(), 0.01);
    }
}