package org.webstrips.core.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

class URLValidation extends Transfer<Boolean> {

	public URLValidation(URL url, Proxy proxy) {
		super(url, proxy);
	}
	
	
	/**
	 * Verifies if the resource exists by sending a HTTP HEAD request.
	 * 
	 * @return <code>true</code> if resource exists, <code>false</code> otherwise.
	 * @throws IOException
	 */
	protected Boolean perform() {

		try {
			notifyTransferConnecting();
	
			URLConnection connection = setupConnection();
	
			if (!HttpURLConnection.class.isAssignableFrom(connection.getClass())) {
				
				// TODO: if connection is not HTTP figure out what to do.
				return true;
				
			}
			
			HttpURLConnection httpConnection = (HttpURLConnection) connection;

			httpConnection.setInstanceFollowRedirects(true);
			httpConnection.setRequestMethod("HEAD");

			notifyTransferStarted();
			
			httpConnection.connect();
			
			int code = httpConnection.getResponseCode();
	
			notifyTransferEnded();
			
			return (code >= 200 && code < 300);

		}
		catch (IOException e) {
		
			notifyTransferError(e);
			
			
		}
		return false;
	}
	
}
