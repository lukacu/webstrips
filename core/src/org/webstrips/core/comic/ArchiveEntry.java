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
 * org/webstrips/comic/ArchiveEntry.java
 * 
 * Created: November 4, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 04/11/2006 (lukacu) created
 *  - 19/11/2006 (lukacu) javadoc
 */

package org.webstrips.core.comic;

import java.text.ParseException;

import org.coffeeshop.string.EscapeSequences;
import org.webstrips.core.ComicStripIdentifier;

/**
 * This class represents a single entry in a comic archive
 * 
 * @author luka
 * @since WebStrips 0.3.3
 */
public class ArchiveEntry extends ComicStripIdentifier {

	private static final String DELIMITER = ";";
	
	/**
	 * Parses data from a string into an ArchiveEntry object.
	 * 
	 * The fromat is the following: {stripID};{visitCount};{stripTitle}
	 * 
	 * @param c parent comic engine
	 * @param s string to parse
	 * @return new ArchiveEntry object
	 * @throws ParseException if the format is not appropriate
	 */
	public static ArchiveEntry parseArchiveEntry(String comic, String s) throws ParseException {
		
		if (s == null)
			throw new ParseException("Null string", -1);
		
		int i = s.indexOf(DELIMITER);
		
		if (i < 1)
			throw new ParseException("Parse error", 0);
		
		int j = s.indexOf(DELIMITER, i+1);
		
		if (j < 2)
			throw new ParseException("Parse error", 0);
		
		ArchiveEntry e = new ArchiveEntry(comic);
		
		try {
		
			e.setId(s.substring(0, i));
			
			try {
				e.visits = Integer.parseInt(s.substring(i+1, j));
			
			} catch (NumberFormatException ex) {
				throw new ParseException("Parse error", 0);
			}
			
			e.setTitle(s.substring(j + 1));
		
		} catch (StringIndexOutOfBoundsException ex) {
			throw new ParseException("Parse error", 0);
		}
		
		return e;
	}
	
	private String title = "";
	
	private int visits = 0;
	
	/**
	 * Creates new archive entry
	 * 
	 * @param c parent comic engine
	 */
	public ArchiveEntry(String comic) {
		super(comic, "");
	}

	/**
	 * Get strip title
	 * 
	 * @return strip title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set strip title
	 * 
	 * @param title new title
	 */
	public void setTitle(String title) {
		this.title = EscapeSequences.stripAmpersandSequence(title);
	}
	
	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return getId().replace(DELIMITER, "") + DELIMITER + visits + DELIMITER + title;
	}

	/**
	 * Checks if the strip has been visited. The strip has been visited if the
	 * visit count is greater than 0.
	 * 
	 * @return <code>true</code> if the strip has been visited, <code>false</code> otherwise.
	 */
	public boolean isVisited() {
		return visits != 0;
	}

	/**
	 * Increment the visit counter by 1.
	 *
	 */
	public void visit() {
		visits++;
	}
	
	/**
	 * Resets the visits counter
	 *
	 */
	public void reset() {
		visits = 0;
	}
	
	/**
	 * Sets the id of this strip
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
}
