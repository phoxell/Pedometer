package com.kalkr.pedometer;

import android.content.Context;

import java.util.TimeZone;

/**
 * Created by sologram on 3/24/15.
 */

public class Pedo extends Base {
	private static final String TAG = Pedo.class.getSimpleName();

	private int itv;
	private int stc, stv;
	private int tkv, tkx;

	public Pedo(Context context, String filename, long interval) {
		super(context, filename);
		itv = (int) (interval / 1000);
	}

	private int roundInterval(long tick) {
		int t = (int) (tick / 1000);
		return t - (t % itv);
	}

	public boolean put(int steps, long tick) {
		int t = roundInterval(tick);
		if (steps < stv || t < tkv)
			stv = steps;
		else if (steps > stv) {
			if (t > tkv) {
				//  stv  stc  stp
				//---+---------+---------+-------
				//  tkv       tkx

				//  stv  stc            stp
				//---+---------+---------+-------
				//  tkv       tkx        t
				boolean re = false, rt = false;
				int d = stc - stv;
				stv = stc;
				if (d > 0 || t <= tkx)
					re = append(d, tkx);
				if (t > tkx)
					rt = append(0, t);
				stc = steps;
				tkv = t;
				tkx = t + itv;
				return re || rt;
			} else stc = steps;
		} else if (t >= hourOut) {
			tkv = t;
			tkx = t + itv;
			return append(0, t);
		}
		return false;
	}

	@Override
	public int timeZone(int tick, TimeZone timeZone) {
		throw new UnsupportedOperationException();
	}

	public int timeZone(long tick, TimeZone timeZone) {
		int t = roundInterval(tick);
		int re = super.timeZone(t, timeZone);
		tkv = this.tick;
		tkx = tkv + itv;
		return re;
	}

	public void save(Context context, String filename, int delta) {
		//Log.w(TAG, "save", stc - stv);
		super.save(context, filename, delta,
			stc == stv ? -1 : ((stc - stv) << 16) + itv);
	}
}
