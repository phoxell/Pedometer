package com.phoxell.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.phoxell.helper.Wakener;

/**
 * Created by sologram on 3/3/15.
 */

public class Pulse extends BroadcastReceiver {
	private static final String TAG = Pulse.class.getSimpleName();
	private static final String PLS = "com.phoxell.activity.PULSE";
	private static final String TCK = "com.phoxell.activity.TICK";

	private static Client clt;
	private static long cld, hld, wrm;
	private static long tmo;
	private static Wakener wak;

	public Pulse() { // for manifest
	}

	public Pulse(Context context, long warm, long cold, long hold,
	             Client client) {
		this();
		clt = client;
		cld = cold;
		hld = hold;
		wrm = warm;
		tmo = System.currentTimeMillis() + hld;
		wak = new Wakener(context, PLS);
		wak.setRepeating(warm);
		android.util.Log.i(TAG, "warm: " + warm);
		android.util.Log.i(TAG, "cold: " + cold);
		android.util.Log.i(TAG, "hold: " + hold);
	}

	public static void active(long current) {
		if (tmo == Long.MAX_VALUE) {
			wak.cancel();
			wak.setRepeating(wrm);
		}
		tmo = current + hld;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		//android.util.Log.e(TAG, intent.getAction());
		if (intent.getAction().equals(PLS)) {
			long t = System.currentTimeMillis();
			if (t >= tmo && cld != wrm) {
				tmo = Long.MAX_VALUE;
				wak.cancel();
				wak.setRepeating(cld);
			}
			if (clt != null)
				clt.onPulse(t);
		}
	}

	public interface Client {
		public void onPulse(long tick);
	}
}
