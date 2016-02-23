package com.kalkr.pedometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by sologram on 1/21/15.
 */

public class Bar extends TextView {
	private static final String TAG = Bar.class.getSimpleName();
	protected static Paint PNS = init(0xff66ddff);
	protected static Paint PNT = init(0xff22bbdd);
	private float x, y;
	private Front frn;
	private int psn;
	private Pedo.Daily ent;
	public Bar(Context context, AttributeSet attrs) {
		super(context, attrs);
		frn = (Front) context;
	}

	private static Paint init(int color) {
		Paint re = new Paint();
		re.setStyle(Paint.Style.FILL);
		re.setColor(color);
		return re;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint p = frn.position() == psn ? PNS : PNT;
		boolean m = false;//frn.mode();
		if (m)
			canvas.drawRect(0, 6, ent == null ? 0 : x, y, p);
		else canvas.drawRect(0, 6,
			ent == null ? 0 : x * ent.steps() / 10000 + 10, y, p);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
	                        int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		x = getWidth();
		y = getHeight() - 6;
	}

	public void setInfo(int position, Pedo.Daily entry) {
		ent = entry;
		psn = position;
	}
}
