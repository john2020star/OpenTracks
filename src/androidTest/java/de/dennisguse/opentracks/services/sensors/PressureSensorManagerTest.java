package de.dennisguse.opentracks.services.sensors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * NOTE: Test data is completely artificial.
 */
public class PressureSensorManagerTest {

    private PressureSensorManager pressureSensorManager;

    @Before
    public void setUp() {
        pressureSensorManager = new PressureSensorManager();
    }

    @Test
    public void getElevationGainLoss_withoutPrior() {
        List<Float> sensorValues = Arrays.asList(1000f, 1000.01f, 1000.0f, 999.9f, 999.85f);

        pressureSensorManager.setSensorValues(sensorValues);

        // then / when
        float[] elevationGainLoss = pressureSensorManager.getElevationGainLoss_m();
        Assert.assertEquals(1.34, elevationGainLoss[0], 0.01);
        Assert.assertEquals(0.08, elevationGainLoss[1], 0.01);
        Assert.assertEquals(999.85, elevationGainLoss[2], 0.01);
    }

    @Test
    public void getElevationGainLoss_withPrior() {
        List<Float> sensorValues = Arrays.asList(1000f, 1000.01f, 1000.0f, 999.9f, 999.85f);

        pressureSensorManager.setSensorValues(sensorValues);

        // then / when
        float[] elevationGainLoss = pressureSensorManager.getElevationGainLoss_m(999.9f);
        Assert.assertEquals(1.34, elevationGainLoss[0], 0.01);
        Assert.assertEquals(0.92, elevationGainLoss[1], 0.01);
        Assert.assertEquals(999.85f, elevationGainLoss[2], 0.01);
    }
}