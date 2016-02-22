package com.kalkr.pedometer;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;

/**
 * Created by sologram on 1/21/15.
 */

public class Nothing extends Drawable {
	public static Drawable drawable = new Nothing();

	@Override
	public void draw(Canvas canvas) {
	}

	@Override
	public void setAlpha(int alpha) {
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
	}

	@Override
	public int getOpacity() {
		return 0;
	}
}
