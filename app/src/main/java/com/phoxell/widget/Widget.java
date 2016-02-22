package com.phoxell.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by sologram on 1/23/15.
 */

public class Widget extends View {
	private static String TAG = "Widget";
	private static Paint pnt = paint();
	protected int hgh, wdt;

	public Widget(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private static Paint paint() {
		Paint ret = new Paint();
		ret.setStyle(Paint.Style.STROKE);
		ret.setColor(0xff888888);
		return ret;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(0, 0, wdt - 1, hgh - 1, pnt);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
	                        int bottom) {
		wdt = right - left;
		hgh = bottom - top;
	}
}
