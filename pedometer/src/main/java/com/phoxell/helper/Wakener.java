package com.phoxell.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sologram on 3/5/15.
 */

public class Wakener {
	private static final String TAG = Wakener.class.getSimpleName();

	private AlarmManager mgr;
	private long itv = 0;
	private PendingIntent pnd;

	public Wakener(Context context, String action) {
		mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(action);
		i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		pnd = PendingIntent.getBroadcast(context, 0, i, 0);
	}

	public void cancel() {
		if (itv != 0) {
			android.util.Log.w(TAG, "cancel");
			mgr.cancel(pnd);
			itv = 0;
		}
	}

	public void setRepeating(long interval) {
		if (itv == 0 && interval != 0) {
			android.util.Log.w(TAG, "setRepeating: " + interval);
			long t = System.currentTimeMillis();
			mgr.setRepeating(AlarmManager.RTC_WAKEUP,
				t - (t % interval) + interval, interval, pnd);
			itv = interval;
		}
	}
}
