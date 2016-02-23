package com.phoxell.format;

/**
 * Created by sologram on 1/24/15.
 */

public class NumberFormat extends Format {
	private static final NumberFormat ins = new NumberFormat();

	public static final NumberFormat getInstance() {
		return ins;
	}

	public final int format(char[] buffer, int number) {
		String s = Integer.toString(number);
		if (buffer.length < s.length())
			return 0;

		for (int i = 0; i < s.length(); i++)
			buffer[i] = s.charAt(i);
		return s.length();
	}
}
