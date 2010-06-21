package org.webstrips.core.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

class Download extends Transfer<byte[]> {
	
	private static final int BUFFER_INCREMENT = 1024;

	private static final int BUFFER_LIMIT = 2 * 1024 * 1024;

	private byte[] data = null;
	
	//private Object mutex = new Object();
	
	protected Download(URL url, Proxy proxy) {
		super(url, proxy);
	}
	
	/**
	 * 
	 * @return byte array containing data retrieved for the resource
	 * @throws IOException
	 */
	protected synchronized byte[] perform() {

		try {
		
			//synchronized (mutex) {
				if (data != null)
					return data;
			//}
			
			notifyTransferConnecting();
			
			URLConnection connection = setupConnection();
			
			connection.connect();
	
			int length = connection.getContentLength();
	
			int verify = length;
	
			int notifyCounter = 0;
	
			if (length == -1) {
				length = BUFFER_INCREMENT;
				verify = -1;
			} else {
				if (length > BUFFER_LIMIT)
					throw new IOException("Content exceeds maximum allowed size");
			}
	
			byte[] data = new byte[length];
	
			InputStream in = connection.getInputStream();
	
			int position = 0;
	
			notifyTransferStarted();
	
			if (verify != -1) {
	
				int d;
	
				while (verify > position) {
						
					if (isCanceled())
						throw new CanceledException();
					
					d = in.read();
	
					if (d == -1) {
						throw new IOException("Not enough data (expected " + verify
								+ " bytes, received " + position + ", difference "
								+ (verify - position) + ")");
					}
	
					data[position] = (byte) (d);
					position++;
	
					notifyCounter++;
	
					if (notifyCounter > 5000) {
						notifyTransferProgress(( (float) position / (float) verify));
	
						notifyCounter = 0;
					}
	
				}
			} else {
				
				notifyTransferProgress(-1);
	
				int read = BUFFER_INCREMENT;
	
				while (true) {
	
					if (isCanceled())
						throw new CanceledException();
					
					int v = in.read(data, position, read);
	
					position += v;
	
					if (v == -1) {
						if (position < 1)
							throw new IOException("No data received");
						
						// trim the array
						byte[] newArray = new byte[position];
						System.arraycopy(data, 0, newArray, 0, position);
						data = newArray;
	
						break;
					}
	
					read -= v;
	
					if (read == 0) {
						read = BUFFER_INCREMENT;
					} else
						continue;
	
					if (position > BUFFER_LIMIT)
						throw new IOException(
								"Content exceeds maximum allowed size");
	
					byte[] newArray = new byte[position + BUFFER_INCREMENT];
	
					System.arraycopy(data, 0, newArray, 0, position);
	
					data = newArray;
	
				}
	
			}
	
			notifyTransferEnded();
			
			//synchronized (mutex) {
				this.data = data;
			//}
	
			return data;

		} catch (IOException e) {
			
			notifyTransferError(e);
			
		} 
		
		return null;
		
	}
}
