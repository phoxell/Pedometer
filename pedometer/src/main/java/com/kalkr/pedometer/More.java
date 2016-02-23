package com.kalkr.pedometer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.phoxell.helper.Log;

/**
 * Created by sologram on 1/18/15.
 */

public class More extends Activity implements View.OnClickListener,
	View.OnLongClickListener {
	public static final String TAG = "Settings";
	private TextView dev, rnk, ver;

	@Override
	public void onClick(View view) {
		//Log.i(TAG, "onClick: " + view.getId());
		Intent i = new Intent();
		setResult(Activity.RESULT_OK, i);
		finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more);

		dev = (TextView) findViewById(R.id.dev);
		rnk = (TextView) findViewById(R.id.rnk);
		ver = (TextView) findViewById(R.id.ver);

		dev.setText(Build.MODEL);
		rnk.setText(((Meter) getApplication()).rank());
		try {
			String s = getPackageManager().
				getPackageInfo(getPackageName(), 0).versionName;
			ver.setText(s);
		} catch (PackageManager.NameNotFoundException e) {
		}

		View v = this.findViewById(android.R.id.content);
		v.setOnClickListener(this);
		v.setOnLongClickListener(this);
	}

	@Override
	public boolean onLongClick(View view) {
		Log.dump();
		return false;
	}

	@Override
	public void onPause() {
		super.onPause();
		finish();
	}
}
