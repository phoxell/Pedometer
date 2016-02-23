package com.phoxell.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by sologram on 4/1/15.
 */

public class More extends Widget {
	private static String TAG = "Widget";
	private static Paint pnt = paint();
	private int r, x, y1, y2, y3;

	public More(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private static Paint paint() {
		Paint ret = new Paint();
		ret.setStyle(Paint.Style.FILL);
		ret.setColor(0xffffffff);
		return ret;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawCircle(x, y1, r, pnt);
		canvas.drawCircle(x, y2, r, pnt);
		canvas.drawCircle(x, y3, r, pnt);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
	                        int bottom) {
		x = (right - left) >> 1;
		y2 = (bottom - top) >> 1;
		int y = y2 >> 1;
		y1 = y2 - y;
		y3 = y2 + y;
		r = y2 / 7;
	}
}
