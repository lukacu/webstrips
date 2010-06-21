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
 * org/webstrips/navigator/AsynchronousOperationAdapter.java
 * 
 * Created: November 28, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 28/11/2006 (lukacu) created
 */

package org.webstrips.core;

/**
 * Adapter helper for AsynchronousOperationListener
 * 
 * @author luka
 * @since WebStrips 0.3.3
 * @see AsynchronousOperationListener
 *
 */
public class AsynchronousOperationAdapter implements
		AsynchronousOperationListener {

	public void operationEnded(AsynchronousOperation o) {}

	public void operationFatalError(AsynchronousOperation o, Throwable e) {}

	public void operationInterrupted(AsynchronousOperation o) {}

	public void operationProgress(AsynchronousOperation o, float progress) {}

	public void operationStarted(AsynchronousOperation o) {}

	public void operationError(AsynchronousOperation o, Throwable e) {}

}
