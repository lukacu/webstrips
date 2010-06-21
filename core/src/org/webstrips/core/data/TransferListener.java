/* WebStrips
 * 
 * License:
 * 
 * WebStrips is a lightweight web comics browser written in Java.
 * 
 * Copyright (C) 2006 Luka Cehovin
 * 
 * This program is free software; you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation; either version 2 of the License, or (at 
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General 
 * Public License for more details.
 * 
 * http://www.opensource.org/licenses/gpl-license.php
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * -----------------------------------------------------------------------
 * 
 * File info:
 * 
 * org/webstrips/data/TransferListener.java
 * 
 * Created: Jul 24, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 24/07/2006 (lukacu) created
 *  - 13/12/2006 (lukacu) renamed from ContentProviderMonitor to TransferListener
 * 
 */


package org.webstrips.core.data;


/**
 * This interface can be used to monitor the progress and state of 
 * a DownloadManager operations
 * class.
 * 
 * @author lukacu
 * @since WebStrips 0.4
 * @see TransferManager
 * @see Transfer
 */
public interface TransferListener {

	public static final int ERROR_UNKNOWN = 0;
	public static final int ERROR_TIMEOUT = 1;
	public static final int ERROR_UNKNOWN_HOST = 2;
	public static final int ERROR_CONNECTION_TERMINATED = 3;
	public static final int ERROR_NOT_FOUND = 4;
	public static final int ERROR_CANCELED = 5;
	
	/**
	 * Callback function that is called when the ContentProvider attempts to connect
	 * to the server.
	 * 
	 * @param d Transfer object that triggered this callback
	 */
	public void transferConnecting(Transfer<?> d);
	
	/**
	 * Callback function that is called when the ContentProvider starts the transfer.
	 * 
	 * @param d Transfer object that triggered this callback
	 */
	public void transferStarted(Transfer<?> d);
	
	/**
	 * Call-back function that reports the progress of a transfer. The progress value can
	 * normally be bounded between 0 and 1. If the size of the resource is not known in
	 * advance this method is triggered only once with the progress value -1.
	 * 
	 * @param d Transfer object that triggered this callback
	 * @param progress progress value 
	 */
	public void transferProgress(Transfer<?> d, float progress);

	/**
	 * Callback function that is called when the transfer successfuly finishes.
	 * 
	 * @param d Transfer object that triggered this callback
	 */
	public void transferEnded(Transfer<?> d);
	
	/**
	 * Callback function that is called when the transfer is aborted for some reason
	 * 
	 * @param d Transfer object that triggered this callback
	 * @param error error code for this error. HTML errors have the same codes as in the protocol, other codes are provided by this interface
	 * @param errorMessage optional message that describes the error
	 */
	public void transferError(Transfer<?> d, int error, String errorMessage);
	
}
