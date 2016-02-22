package com.phoxell.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;

/**
 * Created by sologram on 1/27/15.
 */

public class StepCounter extends SensorBase {
	private static final String TAG = StepCounter.class.getSimpleName();
	protected int steps;
	protected Listener lsn;
	private int stv = -1;
	@TargetApi(Build.VERSION_CODES.KITKAT)
	protected StepCounter(Context context) {
		super(context);
		open(Sensor.TYPE_STEP_COUNTER);
	}

	public static StepCounter create(Context context) {
		StepCounter re = new StepCounter(context);
		if (re.sensor == null) {
			re.close();
			re = null;
		}
		return re;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		steps = (int) event.values[0];
		stepsChanged(event.timestamp);
	}

	public void register(Listener listener) {
		register(this, SensorManager.SENSOR_DELAY_FASTEST);
		lsn = listener;
	}

	protected void stepsChanged(long timestamp) {
		if (steps != stv) {
			if (lsn != null) {
				stv = steps;
				lsn.onSteps(steps, timestamp);
			}
		}
	}

	public void unregister(Listener listener) {
		if (lsn == listener)
			lsn = null;
		super.unregister();
	}

	public interface Listener {
		public void onSteps(int step, long timestamp);
	}
}
