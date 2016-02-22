package com.kalkr.pedometer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Front extends Activity
	implements Meter.Listener, View.OnClickListener {
	private static final String TAG = Front.class.getSimpleName();

	private boolean mod;
	private Fragment day;
	private int psn;
	private Spectral spc;
	private Steps stp;

	public Base.Daily[] entries() {
		return ((Meter) getApplication()).pedo().entries();
	}

	public Base.Daily entry() {
		return ((Meter) getApplication()).pedo().entries()[psn];
	}

	public void flip() {
		layout(!mod);
		stp.startAnimation();
	}

	private void layout(boolean mode) {
		mod = mode;
		day = mode ? new Curve() : new Circle();
		FragmentTransaction t = getFragmentManager().beginTransaction();
		t.replace(R.id.day, day);
		t.commit();
	}

	public boolean mode() {
		return mod;
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.mor) {
			Intent i = new Intent();
			i.setClass(Front.this, More.class);
			startActivity(i);
			overridePendingTransition(R.anim.slide_in_right,
				R.anim.slide_out_left);
		} else flip();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.front);
		startService(new Intent(this, Back.class));

		findViewById(R.id.day).setOnClickListener(this);
		findViewById(R.id.mor).setOnClickListener(this);
		findViewById(R.id.pat).setOnClickListener(this);

		spc = (Spectral) findViewById(R.id.spc);
		stp = (Steps) findViewById(R.id.stp);
	}

	@Override
	public void onPause() {
		super.onPause();
		((Meter) getApplication()).silent();
	}

	@Override
	public void onResume() {
		super.onResume();
		layout(false);
		((Meter) getApplication()).verbose(this);
	}

	@Override
	public void onStep() {
		stp.postInvalidate();
		spc.notifyDataSetChanged();
		View v = day.getView();
		if (v != null)
			v.postInvalidate();
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	public int position() {
		return psn;
	}

	public void position(int position) {
		psn = position;
		onStep();
	}

	public int steps() {
		return psn == 0 ? ((Meter) getApplication()).steps() : entry().steps();
	}

	public int tick() {
		return psn == 0 ? 0 : entry().tick();
	}
}
