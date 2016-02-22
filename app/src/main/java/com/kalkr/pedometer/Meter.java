package com.kalkr.pedometer;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.phoxell.activity.StepCounter;
import com.phoxell.activity.Stepping;
import com.phoxell.helper.Log;

import java.util.TimeZone;

/**
 * Created by sologram on 3/22/15.
 */

public class Meter extends Application implements StepCounter.Listener,
	Thread.UncaughtExceptionHandler {
	private static final String TAG = Meter.class.getSimpleName();
	private static final String NAM = "0.bin";

	private int sti, sto, stp;
	private Listener lsn;
	private Pedo pdo;
	private Stepping ing;

	private void fire() {
		if (lsn != null)
			lsn.onStep();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(this);
		Log.load(this, "app");
		Log.w(TAG, "onCreate");

		pdo = new Pedo(this, NAM, 300000L);
		sti = timeZone();
		//pdo.dump();
		new Receiver();
		ing = new Stepping(this, this);
		Log.save(this, null);
	}

	@Override
	public void onSteps(int steps, long tick) {
		if (sti >= 0) {
			sto = sti - steps;
			sti = -1;
			pdo.put(steps, 0);
		}
		if (steps < stp)
			sto = stp + sto - steps;
		if (pdo.put(steps, tick))
			sto = -steps;
		stp = steps;
		fire();
	}

	public Pedo pedo() {
		return pdo;
	}

	public String rank() {
		return ing.rank();
	}

	public void silent() {
		lsn = null;
		ing.silent();
		pdo.save(this, NAM, 0);
		Log.save(this, null);
	}

	public int steps() {
		return stp + sto;
	}

	private int timeZone() {
		return pdo.timeZone(System.currentTimeMillis(), TimeZone.getDefault());
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		ex.printStackTrace();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void verbose(Listener listener) {
		lsn = listener;
		ing.verbose();
		fire();
	}

	public interface Listener {
		public void onStep();
	}

	private class Receiver extends BroadcastReceiver {
		private Receiver() {
			IntentFilter f = new IntentFilter();
			f.addAction(Intent.ACTION_SCREEN_ON);
			f.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			Meter.this.registerReceiver(this, f);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED))
				timeZone();
			else pdo.save(Meter.this, NAM, 12);
		}
	}
}
