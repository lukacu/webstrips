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
 * org/webstrips/comic/ComicException.java
 * 
 * Created: Apr 26, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 26/04/2006 (lukacu) created
 * 
 */


package org.webstrips.core.comic;

import org.webstrips.core.ComicDescription;

/**
 * Base class for custom WebStrips application exceptions. Despite the
 * possibly funny name this exception is not meant to be hilarious.
 * 
 * @author lukacu
 * @since WebStrip 0.1
 */
public class ComicException extends Exception {

	public static final long serialVersionUID = 1;
	
	private ComicDescription comic;
	
	public ComicException(String message) {
		super(message);
	}
	
	public ComicException(String message, ComicDescription comic) {
		super(message);
		this.comic = comic;
	}
	
	public ComicException(Throwable t, ComicDescription comic) {
		super(t);
		this.comic = comic;
	}
	
	public String toString() {
		
		if (comic == null)
			return super.toString();
		
		return getMessage() + " (" + comic.comicName() + ")";
		
	}
	
}
