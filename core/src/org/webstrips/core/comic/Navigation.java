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
 * org/webstrips/comic/SimpleNavigation.java
 * 
 * Created: Apr 30, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 30/04/2006 (lukacu) created
 * 
 */


package org.webstrips.core.comic;

import org.webstrips.core.ComicStripIdentifier;

/**
 * Interface for simple navigation of the comic. Although the comic
 * must implement all of these methods to use this interface it is not
 * necessary that it actually implements them in a functual way. It can
 * specify its true capabilities with the ComicCapabilities class
 * 
 * Capabilities implemented with this interface:
 * <ul>
 * <li><code>navigation.previous</code></li>
 * <li><code>navigation.next</code></li>
 * <li><code>navigation.first</code></li>
 * <li><code>navigation.newest</code></li>
 * </ul>
 * 
 * 
 * @author lukacu
 * @since WebStrips 0.2
 */
public interface Navigation {

	public static final String NAVIGATION_NEXT 		= "navigation.next";
	public static final String NAVIGATION_PREVIOUS 	= "navigation.previous";
	public static final String NAVIGATION_NEWEST 		= "navigation.newest";
	public static final String NAVIGATION_FIRST 		= "navigation.first";
	
	public ComicStripIdentifier previous(ComicStripIdentifier id);
	
	public ComicStripIdentifier next(ComicStripIdentifier id);
	
	public ComicStripIdentifier first();
	
	public ComicStripIdentifier newest();
	
}
