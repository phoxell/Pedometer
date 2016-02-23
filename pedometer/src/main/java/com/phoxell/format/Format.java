package com.phoxell.format;

/**
 * Created by sologram on 1/24/15.
 */

public class Format {
	public final int format(char[] buffer, Object object) {
		String s = object.getClass().getSimpleName();
		if (buffer.length < s.length())
			return 0;

		for (int i = 0; i < s.length(); i++)
			buffer[i] = s.charAt(i);
		return s.length();
	}
}
