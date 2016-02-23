package com.phoxell.format;

import android.content.Context;
import android.content.res.Resources;

import com.kalkr.pedometer.R;

import java.util.Calendar;

/**
 * Created by sologram on 1/24/15.
 */

public class DateFormat extends Format {
	private static final DateFormat ins = new DateFormat();

	private static Calendar cal = Calendar.getInstance();
	private static String cmm = ", ";
	private static String day = "";
	private static String mon = "/";
	private static String[] wik = {null, "Sun", "Mon", "Tue", "Wed", "Thu",
		"Fri", "Sat"};

	public static final DateFormat getInstance() {
		return ins;
	}

	public final static void init(Context context) {
		Resources r = context.getResources();
		cmm = r.getString(R.string.d_cmm);
		day = r.getString(R.string.d_day);
		mon = r.getString(R.string.d_mon);
		wik[1] = r.getString(R.string.w_sun);
		wik[2] = r.getString(R.string.w_mon);
		wik[3] = r.getString(R.string.w_tue);
		wik[4] = r.getString(R.string.w_wed);
		wik[5] = r.getString(R.string.w_thu);
		wik[6] = r.getString(R.string.w_fri);
		wik[7] = r.getString(R.string.w_sat);
	}

	public final int format(char[] buffer, int tick) {
		cal.setTimeInMillis((long) tick * 1000);
		String s = wik[cal.get(Calendar.DAY_OF_WEEK)] + cmm + " " +
			Integer.toString(cal.get(Calendar.MONTH) + 1) + mon +
			Integer.toString(cal.get(Calendar.DAY_OF_MONTH)) + day;
		if (buffer.length < s.length())
			return 0;

		for (int i = 0; i < s.length(); i++)
			buffer[i] = s.charAt(i);
		return s.length();
	}
}
