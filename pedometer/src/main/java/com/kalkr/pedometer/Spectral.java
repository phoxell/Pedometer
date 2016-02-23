package com.kalkr.pedometer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by sologram on 2/16/15.
 */

public class Spectral extends ListView
	implements ListView.OnItemClickListener, ListView.OnScrollListener,
	View.OnTouchListener {
	private static final String TAG = Spectral.class.getSimpleName();

	private Front frn;
	private int bah;
	private int len, ps0, ty0;

	public Spectral(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDivider(Nothing.drawable);
		setSelector(Nothing.drawable);
		setVerticalScrollBarEnabled(false);
		setOnItemClickListener(this);
		setOnTouchListener(this);
		setOnScrollListener(this);
		if (!isInEditMode()) {
			frn = (Front) context;
			setAdapter(new Adapter());
		}
	}

	@Override
	public void onItemClick(AdapterView view, View item, int position,
	                        long id) {
		//Log.i(TAG, "onItemClick: " + position);
		//frn.flip();
		if (frn.entries()[position] != null)
			frn.position(position);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
	                        int bottom) {
		//Log.e(TAG, "onLayout: " + (bottom - top));
		super.onLayout(changed, left, top, right, bottom);
		if (bah == 0) {
			bah = (bottom - top + 7) / 8;
			notifyDataSetChanged();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
	                     int visibleItemCount, int totalItemCount) {
		//Log.e(TAG, "onScroll");
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//scY = ((ListView) view).getChildAt(0).getTop();
				ty0 = (int) event.getRawY();
				ps0 = frn.position();
				len = frn.entries().length;
				break;
			case MotionEvent.ACTION_MOVE:
				int d = ((int) event.getRawY() - ty0) / bah;
				int p = ps0 - d;
				if (p < 0)
					p = 0;
				if (p >= len)
					p = len - 1;
				if (frn.entries()[p] != null)
					frn.position(p);
				//Log.w(TAG, "onTouch: " + d);
				break;
		}
		return false;
	}

	public void notifyDataSetChanged() {
		ArrayAdapter a = (ArrayAdapter) getAdapter();
		if (a != null)
			a.notifyDataSetChanged();
	}

	public class Adapter extends ArrayAdapter {
		public Adapter() {
			super(frn, android.R.layout.simple_list_item_1,
				frn.entries());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Bar re = new Bar(frn, null);
			re.setHeight(bah);
			re.setInfo(position, frn.entries()[position]);
			return re;
		}
	}
}
