package org.webstrips.core.data;

public interface TransferManagerListener {

	public void transferStart(TransferManagerEvent e);
	
	public void transferEnd(TransferManagerEvent e);
	
	public void transferJoin(TransferManagerEvent e);
	
	
}
