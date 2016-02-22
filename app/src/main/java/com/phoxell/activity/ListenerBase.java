package com.phoxell.activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by sologram on 3/7/15.
 */

public abstract class ListenerBase implements SensorEventListener {
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public abstract void onSensorChanged(SensorEvent event);
}