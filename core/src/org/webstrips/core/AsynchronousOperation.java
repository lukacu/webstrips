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
 * org/webstrips/navigator/AsynchronousOperation.java
 * 
 * Created: Apr 26, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 26/04/2006 (lukacu) created
 *  - 30/04/2006 (lukacu) javadoc
 *  - 24/11/2006 (lukacu) extended to support many observers
 */

package org.webstrips.core;

import java.util.Vector;


/**
 * A base class for all asynchronous operations.
 * 
 * @author lukacu
 * @since WebStrips 0.1
 * @see java.lang.Runnable
 * @see org.webstrips.core.AsynchronousOperationListener
 *
 */
public abstract class AsynchronousOperation implements Runnable {

	private Vector<AsynchronousOperationListener> listeners = new Vector<AsynchronousOperationListener>();

	private boolean interrupted = false;

	private Thread thread;
	
	/**
	 * Constructs a new object.
	 * 
	 */
	public AsynchronousOperation() {
	
	}
	
	/**
	 * Performs the operation.
	 *
	 */
	public final void perform() {
		if (isBeingPerformed()) return;
		thread = createThread(this);
		thread.start();
	}
	
	/**
	 * Implemented because of the Runnable interface.
	 * Do not call this method directly. Call <code>perform()</code> instead.
	 * 
	 * @see Runnable#run()
	 */
	public final void run() {
		fireStartedEvent();

		try {
			
			try {
				performOperation();
			} catch (InterruptedException e) {
			} catch (InterruptedRuntimeException e) {}
			
			if (isInterrupted()) {
				fireInrerruptedEvent();
				return;
			}
			
		}
		catch (Throwable t) {
			fireFatalErrorEvent(t);
		}
		finally {
			fireEndedEvent();
		}
	}

	/**
	 * Tests if the operation is being performed at the moment
	 * 
	 * @return <code>true</code> if the operation is being performed, <code>false</code> otherwise.
	 */
	public boolean isBeingPerformed() {
		if (thread == null) return false;
		return thread.isAlive();
	}
	
	/**
	 * Interrupts the operation. 
	 *
	 */
	public void interrupt() {
		if (!isBeingPerformed()) return;
		interrupted = true;
		thread.interrupt();
	}
	
	/**
	 * Tests weather the operation is interrupted.
	 * 
	 * @return <code>true</code> if the operation is interrupted, <code>false</code> otherwise.
	 */
	protected boolean isInterrupted() {
		return interrupted;
	}
	
	/**
	 * This method is provided for convenience. It causes the thread to
	 * sleep for a specified number of miliseconds
	 * 
	 * @param t a number of miliseconds to pause for.
	 */
	protected final void pause(long t) {
		try {
			Thread.sleep(t);
		}
		catch (InterruptedException e) {
			throw new InterruptedRuntimeException(e);
		}
	}
	
	/**
	 * This method is called by the <code>run()</code> method. Child classes must 
	 * implement this method to give the operation some meaning.
	 *
	 */
	protected abstract void performOperation() throws Exception;

	/**
	 * Add a listener to this operation.
	 * 
	 * @param listener new listener
	 */
	public void addListener(AsynchronousOperationListener listener) {
		if (listener != null)
			listeners.add(listener);
	}

	/**
	 * Removes a listener from this operation.
	 * 
	 * @param listener listener to be removed.
	 */
	public void removeListener(AsynchronousOperationListener listener) {
		if (listener != null)
			listeners.remove(listener);
	}
	
	protected void fireStartedEvent() {
		
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).operationStarted(this);
		}
		
	}
	
	protected void fireEndedEvent() {
		
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).operationEnded(this);
		}
		
	}
	
	protected void fireProgressEvent(float progress) {
		
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).operationProgress(this, progress);
		}
		
	}
	
	protected void fireFatalErrorEvent(Throwable t) {
		
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).operationFatalError(this, t);
		}
		
	}
	
	protected void fireInrerruptedEvent() {
		
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).operationInterrupted(this);
		}
		
	}
	
	/**
	 * Called by the implementation to notify listeners about the 
	 * progress of the operation.
	 * 
	 * @param f progress indicator bound between 0 and 1
	 */
	protected void progress(float f) {
		
		f = Math.max(0, Math.min(1, f));
		
		fireProgressEvent(f);
		
	}
	
	/**
	 * This method generates a Thread object that is used to run the operation. It can be
	 * overriden to insert subclassed Thread objects or add some other funcionality.
	 * 
	 * @param r runnable that is to be used within the generated thread.
	 * @return new thread object
	 * @see Thread
	 */
	protected Thread createThread(Runnable r) {
		return new Thread(r);
	}
	
}
