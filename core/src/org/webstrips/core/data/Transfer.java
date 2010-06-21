package org.webstrips.core.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Vector;

import org.webstrips.core.WebStrips;

public abstract class Transfer<T> {

	protected static final int TIMEOUT = 20000;

	private Vector<TransferListener> listeners = new Vector<TransferListener>();

	private URL url;

	private Proxy proxy = null;
	
	protected Transfer() {
	}

	protected Transfer(URL url) {
		this.url = url;
	}

	protected Transfer(URL url, Proxy proxy) {
		this.url = url;
		this.proxy = proxy;
	}
		
	protected abstract T perform();

	protected URLConnection setupConnection() throws IOException {
		
		URLConnection connection = getURL().openConnection(proxy);
		
		connection.setConnectTimeout(TIMEOUT);
		connection.addRequestProperty("User-Agent", WebStrips.getApplicationIdentifier());
		connection.addRequestProperty("Host", getURL().getHost());

		return connection;
	}
	
	public void addTransferListener(TransferListener l) {
		if (l == null)
			return;

		synchronized (listeners) {
			listeners.add(l);
		}
	}

	public void removeTransferListener(TransferListener l) {
		if (l == null)
			return;

		synchronized (listeners) {
			listeners.remove(l);
		}
	}

	public URL getURL() {
		return url;
	}

	protected void notifyTransferConnecting() {
		synchronized (listeners) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					listeners.get(i).transferConnecting(this);

				} catch (Exception e) {
					WebStrips.getLogger().report(e);
				}
			}
		}
	}

	protected void notifyTransferStarted() {
		synchronized (listeners) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					listeners.get(i).transferStarted(this);

				} catch (Exception e) {
					WebStrips.getLogger().report(e);
				}
			}
		}
	}

	protected void notifyTransferProgress(float p) {
		synchronized (listeners) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					listeners.get(i).transferProgress(this, p);

				} catch (Exception e) {
					WebStrips.getLogger().report(e);
				}
			}
		}
	}

	protected void notifyTransferEnded() {
		synchronized (listeners) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					listeners.get(i).transferEnded(this);

				} catch (Exception e) {
					WebStrips.getLogger().report(e);
				}
			}
		}
	}

	protected void notifyTransferError(int error, String errorMessage) {
		synchronized (listeners) {
			for (int i = 0; i < listeners.size(); i++) {
				try {
					listeners.get(i).transferError(this, error, errorMessage);

				} catch (Exception e) {
					WebStrips.getLogger().report(e);
				}
			}
		}
	}

	protected void notifyTransferError(IOException e) {

		if (e instanceof UnknownHostException) {
			notifyTransferError(TransferListener.ERROR_UNKNOWN_HOST,
					"Unknown host: " + url.getHost());
			return;
		}

		if (e instanceof SocketTimeoutException) {
			notifyTransferError(TransferListener.ERROR_TIMEOUT,
					"Connection timed out: " + url);
			return;
		}

		if (e instanceof FileNotFoundException) {
			notifyTransferError(TransferListener.ERROR_NOT_FOUND,
					"Resource not found: " + url);
			return;
		}
		
		if (e instanceof CanceledException) {
			notifyTransferError(TransferListener.ERROR_CANCELED,
					"Request canceled: " + url);
			return;
		}
		
		WebStrips.getLogger().report(e);

		notifyTransferError(TransferListener.ERROR_UNKNOWN, e.getMessage());

	}

	private boolean canceled = false;
	
	public void cancel() {
		canceled = true;
	}
	
	public boolean isCanceled() {
		return canceled;
	}
	
}
