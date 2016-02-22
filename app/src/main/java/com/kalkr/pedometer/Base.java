package com.kalkr.pedometer;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * Created by sologram on 2/13/15.
 */

public class Base {
	private static final String TAG = Base.class.getSimpleName();
	private static final int HUR = 3600;
	private static final int DAY = HUR * 24;

	protected int hourOut;
	protected int tick;

	private Daily ens[];
	private int cur; // cursor
	private int[] bts;
	private int dtx;
	private int dlt;
	private int len;
	private int zon; // timezone

	public Base(Context context, String filename) {
		try {
			FileInputStream f = context.openFileInput(filename);
			int n = f.available();
			Log.i(TAG, "File length: " + n);
			if (n < 0x100 || (n & 3) != 0)
				throw new Exception();

			byte[] b = new byte[n];
			f.read(b);
			f.close();
			len = n >>> 2; // byte length to int length
			bts = new int[len];
			cur = -1;
			boolean e = false;
			int r = 0, t = 0;
			for (int i = 0; i < len; ) {
				int k = i << 2;
				int p = (b[k++] & 0xff) + (b[k++] << 8 & 0xffff);
				int v = (b[k++] & 0xff) + (b[k] << 8 & 0xffff);
				int c = p + (v << 16);
				if (e) {
					t = c;
					e = false;
					Log.v(TAG, c + " " + str(c));
				} else if (c == -1) {
					if (cur < 0) {
						cur = i;
						r = t;
						Log.v(TAG, String.format("===== %d", i));
					}
				} else {
					t += p;
					e = p == 0;
					Log.v(TAG, String.format("%04d: %d /%d", i, v, p));
				}
				bts[i++] = c;
			}
			//cur = -1;
			if (cur < 0)
				throw new Exception();
			tick = r < t ? r + t : r;
			dlt = 0;
			Log.i(TAG, "tck: " + tick + ' ' + str(tick));
		} catch (Exception e) {
			Log.i(TAG, "New file");
			len = 0x1000;
			//len = 0x200;
			bts = new int[len];
			for (int i = 0; i < len; )
				bts[i++] = -1;
			cur = 0;
			dlt = 1;
			tick = 0;
		}
	}

	private static String str(int t) {
		return new Date(t * 1000L).toString();
	}

	private static int nextHour(int tick) {
		return tick - (tick % HUR) + HUR;
	}

	public boolean append(int delta, int tick) {
		Log.i("append", tick + ", " + delta + ", " + cur + " /" + str(tick));
		bits(delta, tick);
		ens[0].stp += delta;
		if (tick >= dtx) {
			dtx = roundDay(tick) + DAY;
			daily();
			Log.v(TAG, "dtx: " + dtx + ' ' + str(dtx));
			return true;
		}
		return false;
	}

	private void bits(int value) {
		bts[cur++] = value;
		if (cur == len)
			cur = 0;
	}

	private void bits(int delta, int tick) {
		int p = tick - this.tick;
		int v = delta << 16;
		if (p > 0 && p < 0x4000 && tick < hourOut) {
			bits(p + v);
			this.tick = tick;
		} else {
			bits(v);
			if (tick >= this.tick) {
				bits(tick);
				hourOut = nextHour(tick);
				this.tick = tick;
			} else bits(this.tick);
		}
		dlt += delta + 1;
	}

	/*public void dump() {
		android.util.Log.i("dump", "--------");
		boolean f = true;
		int i = ext() ? cur + 2 : cur + 1;
		while (f || i <= cur) {
			String b = Integer.toString(i);
			int v = bts[i++];
			if (i >= len) {
				i = 0;
				f = false;
			}
			int s = v >>> 16;
			int p = v & 0xffff;
			switch (p) {
				case 0:
					int t = bts[i++];
					if (i >= len) {
						i = 0;
						f = false;
					}
					android.util.Log.i(b, s + " /" + t);
					break;
				case 0xffff:
					continue;
				default:
					android.util.Log.i(b, s + " /" + p);
			}
		}
		android.util.Log.i("dump", "--- cur=" + cur);
	}//*/

	private void daily() {
		boolean f = true;
		Daily e = null;
		int c = ext() ? cur + 2 : cur + 1;
		int i = c;
		if (i >= len)
			i -= len;
		int s = 0, t = 0, x = Integer.MAX_VALUE;
		ens = null;
		while (f || i < cur) {
			int b = i;
			int v = bts[i++];
			if (i >= len) {
				i = 0;
				f = false;
			}
			int p = v & 0xffff;
			switch (p) {
				case 0:
					t = bts[i++];
					if (i >= len) {
						i = 0;
						f = false;
					}
					if (x == Integer.MAX_VALUE) {
						e = new Daily(t, c);
						x = roundDay(t) + DAY;
					}
					break;
				case 0xffff:
					c = i;
					continue;
				default:
					t += p;
			}
			s += v >>> 16;
			if (t >= x) {
				e.end = b;
				e.stp = s;
				daily(e);
				e = new Daily(t, b);
				s = 0;
				x = roundDay(t) + DAY;
			}
		}
		if (e == null)
			e = new Daily(tick, 0);
		e.stp = s;
		e.end = cur;
		int n = daily(e);
		if (n == 0)
			e.end = 0;
		else {
			e = new Daily(dtx - 1, cur);
			e.stp = 0;
			e.end = 0;
			daily(e);
		}
	}

	private int daily(Daily entry) {
		int t = roundDay(entry.tck);
		int re = (dtx - t) / DAY;
		if (ens == null)
			ens = new Daily[re < 8 ? 8 : re];
		entry.tck = t;
		re--;
		ens[re] = entry;
		Log.w(Integer.toString(t), String.format("%05d, %04d, %04d", entry.stp, entry.bgn, entry.end) + " /" + str(entry.tck));
		return re;
	}

	public Daily[] entries() {
		return ens;
	}

	private boolean ext() {
		return (bts[cur] & 0xffff) == 0;
	}

	/*public Points points(Rect rect, int tick) {
		throw new UnsupportedOperationException();
	}//*/

	private int roundDay(int tick) {
		return tick - ((tick + zon) % DAY);
	}

	public void save(Context context, String filename, int delta) {
		save(context, filename, delta, -1);
	}

	protected void save(Context context, String filename, int delta,
	                    int extra) {
		if (dlt > delta) {
			Log.w(TAG, "save");
			try {
				if (ext())
					bts[cur < (len - 1) ? cur + 1 : 0] = -1;
				bts[cur] = extra;
				FileOutputStream f = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
				byte[] b = new byte[bts.length * 4];
				for (int i = 0, k = 0; k < bts.length; ) {
					int v = bts[k++];
					b[i++] = (byte) v;
					b[i++] = (byte) (v >>> 8);
					b[i++] = (byte) (v >>> 16);
					b[i++] = (byte) (v >>> 24);
				}
				f.write(b);
				f.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			dlt = 0;
		} else Log.i(TAG, "save - skip");
	}

	public int timeZone(int tick, TimeZone timeZone) {
		zon = timeZone.getRawOffset() / 1000;
		dtx = roundDay(tick) + DAY;
		if (this.tick == 0)
			bits(0, tick);
		hourOut = nextHour(this.tick);
		daily();
		Log.i(TAG, "dtx: " + dtx + ' ' + str(dtx));
		Log.i(TAG, "hrx: " + hourOut + ' ' + str(hourOut));
		Log.i(TAG, "tck: " + this.tick + ' ' + str(this.tick));
		return ens[0].stp;
	}

	public class Daily {
		private int bgn, end, stp, tck;

		private Daily(int tick, int begin) {
			bgn = begin;
			end = 0;
			stp = 0;
			tck = tick;
		}

		public int steps() {
			return stp;
		}

		public int tick() {
			return tck;
		}

		public Points points(Rect rect) {
			return new PointsEx(rect);
		}

		private class PointsEx extends Points {
			private static final int M10 = 600;

			private boolean ro;
			private int en, pr, tk;
			private int hg, wd, x0, y0;
			private Point pt = new Point();

			private PointsEx(Rect rect) {
				pr = bgn;
				en = end == 0 ? cur : end;
				tk = (bts[pr] & 0xffff) == 0 ? bts[pr + 1] : tck;
				ro = pr > en;

				hg = rect.bottom - rect.top;
				wd = rect.right - rect.left;
				x0 = rect.left;
				y0 = rect.bottom;
				//Log.i(TAG, "hg:" + hg + ", wd:" + wd);
			}

			@Override
			public boolean hasNext() {
				return ro || pr < en;
			}

			@Override
			public Point next() {
				int n = tk + M10;
				int y = 0;
				while (tk < n && hasNext()) {
					int v = bts[pr++];
					if (pr >= len) {
						pr = 0;
						ro = false;
					}
					int p = v & 0xffff;
					v >>>= 16;
					y += v;
					if (p == 0) {
						tk = bts[pr++];
						if (pr >= len) {
							pr = 0;
							ro = false;
						}
					} else tk += p;
				}
				pt.x = x0 + (n - tck) * wd / DAY;
				pt.y = y0 - y * hg / 2000;
				return pt;
			}
		}
	}

	public abstract static class Points implements Iterable<Point>, Iterator<Point> {
		@Override
		public abstract boolean hasNext();

		@Override
		public Iterator<Point> iterator() {
			return this;
		}

		@Override
		public abstract Point next();

		@Override
		public void remove() {
		}
	}
}
