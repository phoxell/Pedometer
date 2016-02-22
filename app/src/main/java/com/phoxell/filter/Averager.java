package com.phoxell.filter;

/**
 * Created by sologram on 2/5/15.
 */

public class Averager extends Filter {
	private Boot boo;
	private Exec exe, run;
	private float[] buf = null;
	private int len;
	private int ptr;
	private float sum;
	private long itv;
	private long prt;

	public Averager(int length, long interval) {
		super(length, interval);
		boo = new Boot();
		exe = new Exec();
		len = length;
		buf = new float[len];
		itv = interval;
		reset();
	}

	@Override
	public float filter(float data, long timestamp) {
		return run.filter(data, timestamp);
	}

	public void reset() {
		run = boo;
		sum = 0;
	}

	private class Boot extends Exec {
		public float filter(float data, long timestamp) {
			for (int i = 0; i < len; i++)
				buf[i] = data;
			sum = data * len;
			ptr = 0;
			prt = timestamp;
			run = exe;
			return data;
		}
	}

	private class Exec {
		public float filter(float data, long timestamp) {
			sum -= buf[ptr];
			buf[ptr] = data;
			sum += data;
			ptr++;
			ptr %= len;
			prt = timestamp;
			return sum / len;
		}
	}
}
