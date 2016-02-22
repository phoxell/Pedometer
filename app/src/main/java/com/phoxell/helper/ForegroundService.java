package com.phoxell.helper;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by sologram on 3/3/15.
 */

public abstract class ForegroundService extends Service {
	private static final String TAG = ForegroundService.class.getSimpleName();

	protected IBinder binder;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		startForeground(0, new Notification());
	}

	@Override
	public void onDestroy() {
		stopForeground(true);
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}
}