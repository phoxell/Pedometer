package com.phoxell.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.PowerManager;

import com.phoxell.filter.Averager;

/**
 * Created by sologram on 3/7/15.
 */

public class Stepping extends StepCounter implements Pulse.Client,
	MotionTrigger.Listener, StepCounter.Listener {
	private final static String TAG = Stepping.class.getSimpleName();
	private boolean vbo;
	private Estimator est;
	private long tck;
	private MotionTrigger mot;
	private PowerManager.WakeLock lck;
	private StepCounter.Listener lsn;
	private String rnk;
	public Stepping(Context context, StepCounter.Listener listener) {
		super(context);
		lck = wakeLock(context);
		lsn = listener;
		rnk = "A";
		tck = System.currentTimeMillis();

		long c = 1800_000L; // cold: 30 min
		long h = 300_000L; // hold: 5 min
		long w = 300_000L; // warm: 5 min
		sensor = null;
		if (sensor == null) {
			w = 30_000L; // 30 sec
			open(Sensor.TYPE_ACCELEROMETER);
			est = new Estimator();
			mot = MotionTrigger.create(context);
			rnk = "B";
			if (mot == null) {
				c = 300_000L; // cold: 5 min
				if (fifoLen == 0)
					rnk = "C";
			} else mot.request(this);
		} else register((StepCounter.Listener) this);
		new Pulse(context, w, c, h, this);
		new Receiver(context);
	}

	private static PowerManager.WakeLock wakeLock(Context context) {
		PowerManager m = (PowerManager) context.getSystemService(
			Context.POWER_SERVICE);
		PowerManager.WakeLock re = m.newWakeLock(
			PowerManager.PARTIAL_WAKE_LOCK, TAG);
		re.setReferenceCounted(false);
		return re;
	}

	@Override
	public void close() {
	}

	private void fireSteps(long tick) {
		if (lsn != null)
			lsn.onSteps(steps, tick);
	}

	@Override
	public void onMotion() {
		long t = System.currentTimeMillis();
		if (t - tck > 350_000L)
			tck = t;
		Pulse.active(t);
		//android.util.Log.w(TAG, "onMotion");
	}

	@Override
	public void onPulse(long tick) {
		fireSteps(tick);
		if (est != null)
			est.start();
		if (mot != null)
			mot.request(this);
	}

	@Override
	public void onSteps(int step, long timestamp) {
		fireSteps(System.currentTimeMillis());
		//Beep.b0();
	}

	public String rank() {
		return rnk;
	}

	public void verbose() {
		if (!vbo) {
			vbo = true;
			fireSteps(tck);
			if (est != null) {
				est.start();
				est.verbose();
			}
		}
	}

	public void silent() {
		if (vbo) {
			vbo = false;
			if (est != null)
				est.silent();
		}
	}

	private class Estimator extends ListenerBase {
		private static final float THD = 0.5F;
		protected int stepnxt;
		protected int stepout;
		protected long timenxt;
		protected long timeout;
		private Averager bas = new Averager(200, 20_000000L);    // 0.25 Hz
		private Averager flt = new Averager(5, 20_000000L);      // 10 Hz
		private boolean trg;
		private long tm1;
		private long pr1, pr2, pr3, prd, prt;

		private Estimator() {
			rate = SensorManager.SENSOR_DELAY_GAME;
		}

		private boolean accept(long value1, long value2, long thd) {
			long d = value2 - value1;
			return d < 0 ? d > -thd : d < thd;
		}

		protected void next() {
			stepnxt = steps + stepout;
			timenxt = Long.MAX_VALUE;
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			long t = event.timestamp;
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			float v = flt.filter(Math.sqrt(x * x + y * y + z * z), t);
			float d = bas.filter(v, t) - v;
			if (d < -THD && !trg) {
				trg = true;
				prd = t - prt;
				prt = t;
				if (prd > 166_666667L && prd < 1_666_666667L) { // 0.6-6 Hz
					timenxt = t + timeout;
					long h = prd / 3;
					if (accept(prd, pr1, h) && accept(pr1, pr2, h)) {
						long k = System.currentTimeMillis();
						Pulse.active(k);
						if (accept(pr2, pr3, h))
							steps++;
						else if (tm1 > 0) {
							long n = k - tm1;
							tm1 = 0;
							if (n > 159000L)
								n >>>= 1;
							steps += n * 1000000L / prd;
						} else steps += 4;
						if (steps >= stepnxt) {
							stepnxt = Integer.MAX_VALUE;
							stop();
						}
						fireSteps(k);
					}
				}
				pr3 = pr2;
				pr2 = pr1;
				pr1 = prd;
			} else if (d > THD)
				trg = false;
			else if (t > timenxt) {
				stop();
				fireSteps(System.currentTimeMillis());
			} else if (timenxt == Long.MAX_VALUE)
				timenxt = t + timeout;
		}

		private void silent() {
			stepout = 240;
			timeout = 120_000_000000L;
			tm1 = 0;
			next();
		}

		private boolean start() {
			if (register(this)) {
				lck.acquire();
				android.util.Log.w(TAG, "<---");
				stepout = 4;
				timeout = 2_000000000L;
				pr1 = 0; // ---- r.11
				tm1 = tck;
				next();
				return true;
			}
			return false;
		}

		private void stop() {
			if (Stepping.super.unregister()) {
				tck = System.currentTimeMillis();
				lck.release();
				android.util.Log.w(TAG, ">");
			}
		}

		private void verbose() {
			stepout = 1000000;
			timeout = 3600000_000000000L;
			tm1 = 0;
			next();
		}
	}

	private class Receiver extends BroadcastReceiver {
		private Receiver(Context context) {
			IntentFilter f = new IntentFilter();
			f.addAction(Intent.ACTION_SCREEN_OFF);
			f.addAction(Intent.ACTION_SCREEN_ON);
			context.registerReceiver(this, f);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) &&
				est != null)
				new Handler().postDelayed(new Runnable() {
					public void run() {
						reset();
					}
				}, 2000);
			Pulse.active(System.currentTimeMillis());
		}
	}
}
