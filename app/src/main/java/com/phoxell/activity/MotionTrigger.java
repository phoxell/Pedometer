package com.phoxell.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Build;

/**
 * Created by sologram on 1/28/15.
 */

public class MotionTrigger extends SensorBase {
	private final static String TAG = MotionTrigger.class.getSimpleName();
	protected Listener lsn;
	private EventListener els;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected MotionTrigger(Context context) {
		super(context);
		open(Sensor.TYPE_SIGNIFICANT_MOTION);
		if (sensor != null)
			els = new EventListener();
	}

	public static MotionTrigger create(Context context) {
		MotionTrigger re = new MotionTrigger(context);
		if (re.sensor == null) {
			re.close();
			re = null;
		}
		return re;
	}

	@SuppressLint("NewApi")
	protected void cancel(Listener listener) {
		if (lsn == listener)
			lsn = null;
		manager.cancelTriggerSensor(els, sensor);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
	}

	@SuppressLint("NewApi")
	protected boolean request(Listener listener) {
		if (lsn == null) {
			lsn = listener;
			manager.requestTriggerSensor(els, sensor);
			//android.util.Log.e(TAG, "request");
			return true;
		}
		return false;
	}

	public interface Listener {
		public void onMotion();
	}

	@SuppressLint("NewApi")
	private class EventListener extends TriggerEventListener {
		@Override
		public void onTrigger(TriggerEvent event) {
			if (lsn != null) {
				Listener l = lsn;
				lsn = null;
				l.onMotion();
			}
		}
	}
}
