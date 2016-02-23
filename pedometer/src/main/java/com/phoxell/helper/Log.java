package com.phoxell.helper;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by sologram on 3/3/15.
 */

public class Log {
	public final static int ASSERT = android.util.Log.ASSERT;
	public final static int DEBUG = android.util.Log.DEBUG;
	public final static int ERROR = android.util.Log.ERROR;
	public final static int INFO = android.util.Log.INFO;
	public final static int VERBOSE = android.util.Log.VERBOSE;
	public final static int WARN = android.util.Log.WARN;
	private static final String TAG = Log.class.getSimpleName();
	private static final String EXL = ".log";
	private static final String EXM = ".msg";
	private static final String EXT = ".tag";

	private final static int LEN = 0x1000;

	private static Encode msg;
	private static Encode tag;
	private static int cur, len;
	private static long[] log;
	private static String fnm;

	public static void close(Context context) {
		save(context, fnm);
	}

	public static void dump() {
		if (log != null) for (int i = 0; i < log.length; ) {
			long v = log[i++];
			if (v != 0 && v != -1L) {
				String g = tag.code((int) (v >>> 4 & 0xfff));
				String m = msg.code((int) (v >>> 16 & 0xffff));
				String t = tick((int) (v >>> 32));
				switch ((int) (v & 15)) {
					case ERROR:
						android.util.Log.e(t, g + " - " + m);
						break;
					case INFO:
						android.util.Log.i(t, g + " - " + m);
						break;
					case WARN:
						android.util.Log.w(t, g + " - " + m);
						break;
				}
			}
		}
	}

	public static void load(Context context, String filename) {
		byte[] b;
		b = load(context, filename + EXL, LEN << 3);
		len = b.length >>> 3; // byte length to long length
		log = new long[len];
		cur = 0;
		for (int i = 0, k = 0; i < len; ) {
			long v = ((long) b[k++] & 0xffL);
			v += ((long) b[k++] << 8 & 0xff00L);
			v += ((long) b[k++] << 16 & 0xff0000L);
			v += ((long) b[k++] << 24 & 0xff000000L);
			v += ((long) b[k++] << 32 & 0xff00000000L);
			v += ((long) b[k++] << 40 & 0xff0000000000L);
			v += ((long) b[k++] << 48 & 0xff000000000000L);
			v += ((long) b[k++] << 56 & 0xff00000000000000L);
			if (v == -1L)
				cur = i;
			log[i++] = v;
		}
		tag = Encode.load(context, filename + EXT);
		msg = Encode.load(context, filename + EXM);
		fnm = filename;
	}

	private static byte[] load(Context context, String filename, int length) {
		byte[] re;
		try {
			//android.util.Log.i(TAG, "load - " + filename);
			FileInputStream f = context.openFileInput(filename);
			int n = f.available();
			if (n != 0) {
				//android.util.Log.i(TAG, "load - " + n);
				re = new byte[n];
				f.read(re);
				f.close();
			} else throw new Exception();
		} catch (Exception e) {
			re = new byte[length];
		}
		return re;
	}

	@SuppressWarnings("deprecation")
	private static String tick(int time) {
		Date d = new Date((long) time * 1000);
		return d.getHours() + ":" + d.getMinutes();
	}

	public static void save(Context context, String filename) {
		if (filename != null)
			fnm = filename;
		if (log != null) {
			log[cur] = -1L;
			byte[] b = new byte[log.length << 3];
			for (int i = 0, k = 0; i < log.length; ) {
				long v = log[i++];
				b[k++] = (byte) v;
				b[k++] = (byte) (v >>> 8);
				b[k++] = (byte) (v >>> 16);
				b[k++] = (byte) (v >>> 24);
				b[k++] = (byte) (v >>> 32);
				b[k++] = (byte) (v >>> 40);
				b[k++] = (byte) (v >>> 48);
				b[k++] = (byte) (v >>> 56);
			}
			save(context, fnm + EXL, b);
			tag.save(context, fnm + EXT);
			msg.save(context, fnm + EXM);
		}
	}

	private static void save(Context context, String filename, byte[] bits) {
		try {
			//android.util.Log.i(TAG, "save - " + filename);
			FileOutputStream f = context.openFileOutput(filename,
				Context.MODE_PRIVATE);
			f.write(bits);
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int d(String tag, String msg) {
		return println(DEBUG, tag, msg, null);
	}

	public static int d(String tag, String msg, long extra) {
		return println(DEBUG, tag, msg, extra);
	}

	public static int d(String tag, String msg, String extra) {
		return println(DEBUG, tag, msg, extra);
	}

	public static int e(String tag, String msg) {
		return println(ERROR, tag, msg, null);
	}

	public static int e(String tag, String msg, long extra) {
		return println(ERROR, tag, msg, extra);
	}

	public static int e(String tag, String msg, String extra) {
		return println(ERROR, tag, msg, extra);
	}

	public static int i(String tag, String msg) {
		return println(INFO, tag, msg, null);
	}

	public static int i(String tag, String msg, int extra) {
		return println(INFO, tag, msg, extra);
	}

	public static int i(String tag, String msg, long extra) {
		return println(INFO, tag, msg, extra);
	}

	public static int i(String tag, String msg, String extra) {
		return println(INFO, tag, msg, extra);
	}

	public static int println(int priority, String tag, String msg, int extra) {
		return println(priority, tag, msg, Integer.toString(extra));
	}

	public static int println(int priority, String tag, String msg, long extra) {
		return println(priority, tag, msg, Long.toString(extra));
	}

	public static int println(int priority, String tag, String msg, String extra) {
		if (priority > INFO && log != null) {
			int t = (int) (System.currentTimeMillis() / 1000);
			int g = Log.tag.code(tag);
			int v = Log.msg.code(msg);
			log[cur++] = ((long) t << 32) + ((long) v << 16) +
				((long) g << 4) + priority;
			if (cur >= log.length)
				cur = 0;
		}
		return extra == null ? android.util.Log.println(priority, tag, msg) :
			android.util.Log.println(priority, tag, msg + " - " + extra);
	}

	public static int v(String tag, String msg) {
		return println(VERBOSE, tag, msg, null);
	}

	public static int v(String tag, String msg, long extra) {
		return println(VERBOSE, tag, msg, extra);
	}

	public static int v(String tag, String msg, String extra) {
		return println(VERBOSE, tag, msg, extra);
	}

	public static int w(String tag, String msg) {
		return println(WARN, tag, msg, null);
	}

	public static int w(String tag, String msg, long extra) {
		return println(WARN, tag, msg, extra);
	}

	public static int w(String tag, String msg, String extra) {
		return println(WARN, tag, msg, extra);
	}

	private static class Encode extends TreeMap {
		private Map<Integer, String> dec;

		private static Encode load(Context context, String filename) {
			byte[] b = Log.load(context, filename, 0);
			Encode re = new Encode();
			Map d = new TreeMap();
			for (int i = 0, k = 0, n = 1; i < b.length; i++) {
				byte c = b[i];
				if (c == '|') {
					String s = new String(b, k, i - k);
					re.put(s, n);
					d.put(n, s);
					k = i + 1;
					n++;
				}
			}
			re.dec = d;
			return re;
		}

		private String code(int code) {
			return dec.get(code);
		}

		private int code(String string) {
			Integer n = (Integer) get(string);
			if (get(string) == null) {
				int re = this.size() + 1;
				put(string, re);
				dec.put(re, string);
				return re;
			} else return n;
		}

		private void save(Context context, String filename) {
			int n = 0;
			for (String e : dec.values())
				n += e.length() + 1;
			if (n > 0) {
				byte[] b = new byte[n];
				for (String e : dec.values()) {
					//android.util.Log.i(TAG, "save - " + e);
					int k = 0;
					for (int i = 0; i < e.length(); )
						b[k++] = (byte) e.charAt(i++);
					b[k++] = '|';
				}
				Log.save(context, filename, b);
			}
		}
	}
}
