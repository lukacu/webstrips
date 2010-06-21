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
 * org/webstrips/comic/ComicStrip.java
 * 
 * Created: Apr 26, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 26/04/2006 (lukacu) created
 *  - 29/04/2006 (lukacu) javadoc
 *  - 23/07/2006 (lukacu) major reorganisation
 * 
 */


package org.webstrips.core;

import org.coffeeshop.string.StringUtils;

/**
 * This class represents a single image strip identifier.
 * 
 * @author lukacu
 * @since WebStrips 0.1
 *
 */
public class ComicStripIdentifier {

	protected String id = null;
	
	private String comic;

	/**
	 * Constructs new strip image resource
	 * 
	 * @param c Comic engine that this strip belongs to
	 * @param id Id of this strip 
	 */
	public ComicStripIdentifier(String comic, String id) {
		this.comic = comic;
		this.id = id;
	}
	
	/**
	 * Returns the parent comic for this ComicStrip instance.
	 * 
	 * @return
	 */
	public String getComic() {
		return comic;
	}

	/**
	 * Sets the id of this strip
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o == null)
			return false;
		
		if (!ComicStripIdentifier.class.isAssignableFrom(o.getClass()))
			return false;
		
		if (!((ComicStripIdentifier) o).getComic().equals(getComic()))
			return false;
		
		return StringUtils.same(getId(), ((ComicStripIdentifier) o).getId());
		
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "comic://" + comic + "/" + id;
	}
}
