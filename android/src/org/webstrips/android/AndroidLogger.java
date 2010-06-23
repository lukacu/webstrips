package org.webstrips.android;

import org.coffeeshop.log.AbstractLogger;

import android.util.Log;

public class AndroidLogger extends AbstractLogger {

	private int mask;
	
	@Override
	public void disableAllChannels() {
		mask = 0;
	}

	@Override
	public synchronized void disableChannel(int channel) {
		mask &= ~channel;
		
	}

	@Override
	public synchronized void enableAllChannels() {
		mask = 0xFFFFFFFF;
	}

	@Override
	public synchronized void enableChannel(int channel) {
		mask |= channel;
	}

	@Override
	public synchronized boolean isChannelEnabled(int channel) {
		return (mask & channel) != 0;
	}

	@Override
	protected synchronized void print(String str, int channel) {
		
		switch (channel) {
		case ERROR:
			Log.e("WebStrips", str);
			break;
		case WARNING:
			Log.w("WebStrips", str);
			break;		
		case DEFAULT:
			Log.v("WebStrips", str);
			break;
		case APPLICATION_INTERNAL_1:
		case APPLICATION_INTERNAL_2:
		case APPLICATION_INTERNAL_3:
		case APPLICATION_INTERNAL_4:
		case APPLICATION_INTERNAL_5:
		case APPLICATION_INTERNAL_7:
		case APPLICATION_INTERNAL_8:
		case COFFEESHOP:
			Log.i("WebStrips", str);
			break;	
		}
		
		
	}

}
