package com.phoxell.filter;

/**
 * Created by sologram on 2/5/15.
 */
public abstract class Filter {
	public Filter(int length) {
		this(length, 0L);
	}

	public Filter(int length, long interval) {
	}

	public float filter(float data) {
		return filter(data, 0L);
	}

	public float filter(double data, long timestamp) {
		return filter((float) data, timestamp);
	}

	public abstract float filter(float data, long timestamp);
}
