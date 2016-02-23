package com.kalkr.pedometer;

import com.phoxell.helper.ForegroundService;

/**
 * Created by sologram on 3/3/15.
 */

public class Back extends ForegroundService {
	public static final String ACT = "com.kalkr.BACK";

	@Override
	public void onCreate() {
		super.onCreate();
		//new Wakener(this, ACT).setRepeating(10_000L); // 30 sec
	}
}
