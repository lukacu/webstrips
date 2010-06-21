package org.webstrips.core.data;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import org.webstrips.core.WebStrips;

class TransferManager {

	private Vector<TransferManagerListener> listeners = new Vector<TransferManagerListener>();
	
	private TreeMap<String, Download> downloads = new TreeMap<String, Download>();
	
	private Object mutex = new Object();
	
	private int transferId = 0;
	
	private Proxy getProxy(URL url) {
		
		List<Proxy> l = null;
		try {
		  l = ProxySelector.getDefault().select(url.toURI());
		}
		catch (URISyntaxException e) {
		  e.printStackTrace();
		}

		if (l != null && l.size() > 0) {
		   return l.get(0);
		}

		return null;
	}
	
	public synchronized void addListener(TransferManagerListener l) {
		
		if (l == null)
			return;
		
		if (listeners.contains(l))
			return;
		
		listeners.add(l);
		
	}
	
	public synchronized void removeListener(TransferManagerListener l) {

		listeners.remove(l);
		
	}
	
	public byte[] download(URL url, TransferListener l, String owner) {
		
		Download d = null;
		TransferManagerEvent e = null;
		
		synchronized (mutex) {
			
			d = downloads.get(url.toString());
			
			if (d == null) {
			
				d = new Download(url, getProxy(url));
			
				downloads.put(url.toString(), d);
				e = new TransferManagerEvent(d, getNewTransferId(), owner);
				fireTransferStart(e);
			
			} else {
				WebStrips.getLogger().report(WebStrips.TRANSFER, "Waiting for already began transfer: " + url);
				e = new TransferManagerEvent(d, getNewTransferId(), owner);
				fireTransferJoin(e);
			}
			
		}
		
		d.addTransferListener(l);

		
		byte[] data = null;
			
		data = d.perform();

		synchronized (mutex) {
			
			downloads.remove(url.toString());
			
		}
		
		fireTransferEnd(e);
		
		return data;
	}
	
	public boolean validateURL(URL url, TransferListener l, String owner) {
		
		URLValidation d = new URLValidation(url, getProxy(url));
			
		d.addTransferListener(l);
		
		TransferManagerEvent e = new TransferManagerEvent(d, getNewTransferId(), owner);
		
		fireTransferStart(e);
		
		boolean data = d.perform();
		
		fireTransferEnd(e);
		
		return data;
		
	}
	
	private synchronized int getNewTransferId() {
		return transferId++;
	}
	
	private synchronized void fireTransferStart(TransferManagerEvent e) {
		
		for (TransferManagerListener l : listeners) {
			try {
				l.transferStart(e);
			} catch (Exception ex) {
				WebStrips.getLogger().report(ex);
			}
			
		}
		
	}

	private synchronized void fireTransferEnd(TransferManagerEvent e) {
		
		for (TransferManagerListener l : listeners) {
			try {
				l.transferEnd(e);
			} catch (Exception ex) {
				WebStrips.getLogger().report(ex);
			}
		}
		
	}

	private synchronized void fireTransferJoin(TransferManagerEvent e) {
		
		for (TransferManagerListener l : listeners) {
			try {
				l.transferJoin(e);
			} catch (Exception ex) {
				WebStrips.getLogger().report(ex);
			}
		}
		
	}
	
	public synchronized void cancelAllDownloads() {
		for (Download d : downloads.values()) {
			d.cancel();
		}
	}
}
