package org.webstrips.android;

import java.io.IOException;

import org.webstrips.core.WebStrips;

import android.app.Application;
import android.content.Intent;

public class WebStripsAndroid extends Application {

	private static WebStripsAndroid singleton = null;

	public static WebStripsAndroid getApplication() {
		return singleton;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		WebStripsAndroid.singleton = this;

		System.setProperty("org.webstrips.identifier", "WebStrips Android 0.1");
		System.setProperty("org.webstrips.StorageManager",
				"org.webstrips.android.AndroidStorageManager");
		System.setProperty("org.webstrips.SettingsManager",
				"org.webstrips.android.AndroidSettingsManager");
		System.setProperty("org.webstrips.StripImageClass",
				"org.webstrips.android.AndroidStripImage");
		System.setProperty("org.webstrips.javascript.interpret", "true");

		System.setProperty("org.webstrips.transfer.debug", "true");

		System.setProperty("org.webstrips.memory", "1000000");
		System.setProperty("org.webstrips.cache", "5000000");

		WebStrips.setLogger(new AndroidLogger());
		WebStrips.getLogger().enableAllChannels();

		startService(new Intent(this.getApplicationContext(),
				WebStripsService.class));

	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		stopService(new Intent(this.getApplicationContext(),
				WebStripsService.class));

		try {
			WebStrips.getSettings().commit();
		} catch (IOException e) {
			WebStrips.getLogger().report(e);
		}
		WebStrips.getLogger().report("WebStrips terminated");

		super.onTerminate();
	}

	/*
	 * FOR NETWORK TYPE DETECTION ConnectivityManager.getActiveNetworkInfo()
	 * 
	 * int netType = info.getType(); int netSubtype = info.getSubtype(); if
	 * (netType == ConnectivityManager.TYPE_WIFI) { return info.isConnected(); }
	 * else if (netType == ConnectivityManager.TYPE_MOBILE && netSubtype ==
	 * TelephonyManager.NETWORK_TYPE_UMTS && !mTelephony.isNetworkRoaming()) {
	 * return info.isConnected(); } else { return false; }
	 */
}
