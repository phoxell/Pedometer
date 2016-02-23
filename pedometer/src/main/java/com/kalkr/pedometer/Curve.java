package com.kalkr.pedometer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sologram on 1/18/15.
 */

public class Curve extends Circle {
	private static final String TAG = Curve.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return new ViewEx(getActivity());
	}

	static class ViewEx extends View {
		private Front frn;
		private Paint pnc = new Paint();
		private Paint pnd = new Paint();
		private Paint pnn = new Paint();
		private Rect rct = new Rect();
		private Rect rc1 = new Rect();
		private Rect rc2 = new Rect();

		public ViewEx(Context context) {
			super(context);
			frn = (Front) context;

			pnc.setAntiAlias(true);
			pnc.setColor(0xffffffff);
			pnc.setStyle(Paint.Style.STROKE);
			pnc.setStrokeWidth(3);

			pnd.setAntiAlias(true);
			pnd.setStyle(Paint.Style.FILL);
			pnd.setColor(0xff19b2d4);

			pnn.setAntiAlias(true);
			pnn.setColor(0xffffffff);
			pnn.setStrokeWidth(1);
		}

		@Override
		public void onDraw(Canvas canvas) {
			Pedo.Daily n = frn.entry();
			canvas.drawColor(0xff22bbdd);
			canvas.drawRect(rc1, pnd);
			canvas.drawRect(rc2, pnd);
			canvas.drawLine(0, rct.top, rct.right + 20, rct.top, pnn);
			canvas.drawLine(0, rct.bottom, rct.right + 20, rct.bottom, pnn);
			if (n != null) {
				Path p = new Path();
				for (Point t : n.points(rct)) {
					p.moveTo(t.x, rct.bottom);
					p.lineTo(t.x, t.y);
				}
				canvas.drawPath(p, pnc);
			}
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
		                        int bottom) {
			super.onLayout(changed, left, top, right, bottom);
			rc1.bottom = rc2.bottom = bottom - top;
			rc2.right = right - left - 20;
			rc1.left = 10;
			rc2.left = rc2.right * 3 / 4;
			rc1.right = rc2.right / 4;
			rc1.top = rc2.top = 50;

			rct.bottom = rc2.bottom - 20;
			rct.left = rc1.left;
			rct.right = rc2.right;
			rct.top = rc2.top + 20;
		}
	}
}
