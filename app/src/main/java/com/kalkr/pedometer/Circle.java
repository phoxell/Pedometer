package com.kalkr.pedometer;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sologram on 1/18/15.
 */

public class Circle extends Fragment {
	private static final String TAG = Circle.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		return new ViewEx(getActivity());
	}

	static class ViewEx extends View {
		private Paint pnt;
		private float cx, cy, rdu;

		public ViewEx(Context context) {
			super(context);
			pnt = new Paint();
			pnt.setAntiAlias(true);
			pnt.setStyle(Style.STROKE);
			pnt.setColor(0xffffffff);
			pnt.setTextAlign(Align.CENTER);
		}

		@Override
		public void onDraw(Canvas canvas) {
			canvas.drawCircle(cx, cy, rdu, pnt);
		}

		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
		                        int bottom) {
			super.onLayout(changed, left, top, right, bottom);
			cx = (right - left) / 2;
			cy = (bottom - top) / 2;
			rdu = cy * 3 / 4;
		}
	}
}
