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
 * org/webstrips/navigator/ComicDriverListener.java
 * 
 * Created: November 14,2006
 * Author: lukacu
 * 
 * Log:
 *  - 14/11/2006 (lukacu) created
 *  - 19/11/2006 (lukacu) javadoc
 */

package org.webstrips.core;

import org.webstrips.core.Comic.State;

/**
 * Interface that all listeners of comic drivers must implement
 * 
 * @author luka
 * @since WebStrips 0.3.3
 */
public interface ComicListener {

	/**
	 * Called when number of unread comics in the archive changes
	 * 
	 * @param comicDriver comic that triggered the event
	 * @param unreadStrips number of unread strips
	 */
	public void unreadStripsNumberChanged(Comic comicDriver, int unreadStrips);

	public void stateChanged(Comic comicDriver, State newState, State oldState);
	
	/**
	 * Called when the preload buffer changes its content
	 * 
	 * @param comicDriver comic that triggered the event
	 * @param s new strip
	 */
	public void preloadSatusUpdate(Comic comicDriver, int current, int total);
}
