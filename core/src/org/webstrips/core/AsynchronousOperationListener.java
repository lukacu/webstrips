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
 * org/webstrips/navigator/AsynchronousOperationListener.java
 * 
 * Created: Apr 26, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 26/04/2006 (lukacu) created
 *  - 30/04/2006 (lukacu) javadoc
 *  - 24/11/2006 (lukacu) renamed from AsynchronousOperationMonitor to AsynchronousOperationListener
 * 
 */

package org.webstrips.core;

/**
 * This interface can be used to monitor the progress and state of 
 * an asynchronous operation defined with the AsynchronousOperation
 * class.
 * 
 * @author lukacu
 * @since WebStrips 0.1
 * @see org.webstrips.core.AsynchronousOperation
 */
public interface AsynchronousOperationListener {

	/**
	 * Called when the operation starts.
	 * 
	 * @param o the AsynchronousOperation object that called this
	 * method
	 */
	public void operationStarted(AsynchronousOperation o);
	
	/**
	 * Called when the operation ends.
	 * 
	 * @param o the AsynchronousOperation object that called this
	 * method
	 */
	public void operationEnded(AsynchronousOperation o);
	
	/**
	 * Called during the execution of the operation to report progress.
	 * <b>Note:</b> this method usualy gives very inaccurate information.
	 * 
	 * @param o the AsynchronousOperation object that called this method
	 * @param progress floating point number between <code>0</code> and
	 * <code>1</code> that defines the percentage of the task completed.
	 */
	public void operationProgress(AsynchronousOperation o, float progress);
	
	/**
	 * Called when the operation encounters an unhandled exception so that
	 * it must be terminated without completing its original objective.
	 * 
	 * @param o the AsynchronousOperation object that called this
	 * method
	 * @param e the exception object that was not handled within the operation.
	 */
	public void operationFatalError(AsynchronousOperation o, Throwable e);
	
	/**
	 * Called when the operation is interrupted while it is performed. It can
	 * not finish its work.
	 * 
	 * @param o the AsynchronousOperation object that called this
	 * method
	 */
	public void operationInterrupted(AsynchronousOperation o);
	
	/**
	 * Called when the operation encounters a handled exception that is
	 * reported for logging or any other purposes. The operation is not terminated.
	 * 
	 * @param o the AsynchronousOperation object that called this
	 * method
	 * @param e the exception object.
	 */
	public void operationError(AsynchronousOperation o, Throwable e);
}
