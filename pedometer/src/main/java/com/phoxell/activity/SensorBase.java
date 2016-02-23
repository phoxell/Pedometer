package com.phoxell.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by sologram on 1/27/15.
 */

public abstract class SensorBase extends ListenerBase {
	private static final String TAG = SensorBase.class.getSimpleName();

	protected int fifoLen;
	protected int rate = SensorManager.SENSOR_DELAY_NORMAL;
	protected Sensor sensor;
	protected SensorEventListener listener;
	protected SensorManager manager;

	public SensorBase(Context context) {
		manager = (SensorManager) context.getSystemService(
			Context.SENSOR_SERVICE);
	}

	public void close() {
		unregister();
		manager = null;
	}

	@SuppressLint("NewApi")
	protected void open(int type) {
		try {
			sensor = manager.getDefaultSensor(type);
			fifoLen = sensor.getFifoMaxEventCount();
		} catch (Throwable e) {
		}
	}

	public boolean register(SensorEventListener listener) {
		return register(listener, rate);
	}

	public synchronized boolean register(SensorEventListener listener,
	                                     int rate) {
		if (this.listener == null) {
			this.listener = listener;
			this.rate = rate;
			manager.registerListener(listener, sensor, rate);
			//Log.i(TAG, "register: " + rate);
			return true;
		}
		return false;
	}

	public void reset() {
		if (listener != null) {
			manager.unregisterListener(listener);
			manager.registerListener(listener, sensor, rate);
			//Log.i(TAG, "reset");
		}
	}

	public synchronized boolean unregister() {
		if (listener != null) {
			SensorEventListener l = listener;
			listener = null;
			manager.unregisterListener(l);
			//Log.i(TAG, "unregister");
			return true;
		}
		return false;
	}
}
