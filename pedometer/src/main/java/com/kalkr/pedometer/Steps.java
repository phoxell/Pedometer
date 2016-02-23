package com.kalkr.pedometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.phoxell.format.DateFormat;
import com.phoxell.format.NumberFormat;

/**
 * Created by Sologram on 1/23/15.
 */

public class Steps extends TextView {
	private static final String TAG = Steps.class.getSimpleName();
	private static int DRS = 333;
	private Animation anf, ant;
	private int xcn;
	private int yf0, yf1, yf2, yt0, yt1, yt2;
	private Paint pf0, pf1, pf2, pt0, pt1, pt2;
	private char[] dat = new char[16], stp = new char[8];
	private DateFormat dfm = DateFormat.getInstance();
	private NumberFormat sfm = NumberFormat.getInstance();
	private Front frn;
	public Steps(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode())
			frn = (Front) context;
		DateFormat.init(context);
	}

	private static Paint paint(int textSize) {
		Paint re = new Paint();
		re.setAntiAlias(true);
		re.setColor(0xffffffff);
		re.setTextAlign(Paint.Align.CENTER);
		re.setTextSize(textSize);
		return re;
	}

	private AnimationSet animSet(float c, float o, float y) {
		AnimationSet re = new AnimationSet(true);
		Animation a = new ScaleAnimation(c, 1, c, 1, xcn, y);
		a.setDuration(DRS);
		re.addAnimation(a);
		a = new TranslateAnimation(0, 0, o, 0);
		a.setDuration(DRS);
		re.addAnimation(a);
		return re;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!isInEditMode()) {
			int s = frn.steps();
			int t = frn.tick();
			int m = sfm.format(stp, s);
			if (t == 0) {
				if (frn.mode())
					canvas.drawText(stp, 0, m, xcn, yt0, pt0);
				else canvas.drawText(stp, 0, m, xcn, yf0, pf0);
			} else {
				int n = dfm.format(dat, t);
				if (frn.mode()) {
					canvas.drawText(stp, 0, m, xcn, yt1, pt1);
					canvas.drawText(dat, 0, n, xcn, yt2, pt2);
				} else {
					canvas.drawText(stp, 0, m, xcn, yf1, pf1);
					canvas.drawText(dat, 0, n, xcn, yf2, pf2);
				}
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
	                        int bottom) {
		if (ant == null) {
			xcn = (right - left) / 2;
			pf0 = paint(xcn / 3);
			pf1 = paint(xcn / 5);
			pf2 = paint(xcn / 8);

			pt0 = paint(xcn / 4);
			pt1 = paint(xcn * 3 / 20);
			pt2 = paint(xcn * 3 / 32);

			float f0 = (pf0.descent() + pf0.ascent()) / 2;
			float f1 = (pf1.descent() + pf1.ascent() + pf2.descent() +
				pf2.ascent()) / 2;
			float t0 = (pt0.descent() + pt0.ascent()) / 2;
			float t1 = (pt1.descent() + pt1.ascent() + pt2.descent() +
				pt2.ascent()) / 2;

			float y = (top + bottom) / 2;
			yf0 = (int) (y - f0);
			yf1 = (int) (y - f1);
			yf2 = (int) (y + f1);

			y = top - f0 - f0;
			yt0 = (int) (y - t0);
			yt1 = (int) (y - t1);
			yt2 = (int) (y + t1);

			anf = animSet(t0 / f0, yt0 - yf0, yf0);
			ant = animSet(f0 / t0, yf0 - yt0, yt0);
		}
	}

	public void startAnimation() {
		if (anf != null)
			startAnimation(frn.mode() ? ant : anf);
	}
}