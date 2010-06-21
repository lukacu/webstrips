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
 * org/webstrips/comic/Comic.java
 * 
 * Created: Apr 26, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 26/04/2006 (lukacu) created
 *  - 29/04/2006 (lukacu) javadoc
 *  - 23/07/2006 (lukacu) major reorganisation
 *  - 12/03/2007 (lukacu) comic properties methods are made depricated
 * 
 */

package org.webstrips.core.comic;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.webstrips.core.ComicStripIdentifier;
import org.webstrips.core.data.ContentProvider;

/**
 * Abstract base class for all comic engines. This class provides the base API for
 * a comic and some static methods for convinience.
 * 
 * 
 * @author lukacu
 * @since WebStrips 0.1
 */
public abstract class ComicDriver {

	// content provider provides a cached api for http queries
	private ContentProvider cp;

	/**
	 * Formats the date encoded as YYYYMMDD or YYMMDD to a more eye pleasing form
	 * 
	 * @param s the original date
	 * @return pretty date or empty string if conversion was not possible
	 */
	protected static final String formatDate(String s) {
			
		Calendar c = parseDate(s);
		
		if (c == null)
			return "";
		
		return String.format("%1$tB %1$te, %1$tY", c);	
	}
	
	/**
	 * Parses the date encoded as YYYYMMDD or YYMMDD to a <code>Calendar</code> object.
	 * 
	 * @param s date
	 * @return Calendar instance that represent the input date or null if conversion was
	 * not possible
	 * 
	 * @see Calendar
	 * @see GregorianCalendar
	 */
	protected static final Calendar parseDate(String s) {
		try {
			int year = 0, month = 0, day = 0;
			
			if (s.length() == 8) {
				year = Integer.parseInt(s.substring(0, 4));
				
				month = Integer.parseInt(s.substring(4, 6)) - 1;
				
				day = Integer.parseInt(s.substring(6, 8));
			} else if (s.length() == 6) {
				year = Integer.parseInt(s.substring(0, 2));
				// we have to guess the century. lets say that above 70 is 20th century
				// and below that is 21 century
				year += ((year > 70) ? 1900 : 2000); // + 1900; 
				
				month = Integer.parseInt(s.substring(2, 4)) - 1;
				
				day = Integer.parseInt(s.substring(4, 6));
			} else return null;
			
			Calendar c = new GregorianCalendar(year, month, day);
			
			return c;
		}
		catch (NumberFormatException e) {
			return null;
		}
	}
	
	/**
	 * Get todays date as an Calendar object (GregorianCalendar is used).
	 * This date is suitable for comparing two dates without the time of
	 * the day information.
	 * 
	 * @return a calendar object that reperesents today's date with no
	 * information about the time of the day (it is midnight).
	 * @see Calendar
	 * @see GregorianCalendar
	 */
	protected static Calendar getToday() {
		
		// new calendar, current date and time
		Calendar c = new GregorianCalendar();
		
		// now we clear the fields we do not need (even more: we must delete
		// them in order to compute compareTo properly)
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		
		// return the modified calendar
		return c;
	}
	
	/**
	 * Constructs a new comic engine
	 * 
	 * @param cp a content provider for this comic
	 */
	public ComicDriver(ContentProvider cp) {
		if (cp == null)
			throw new IllegalArgumentException("Must specify a content provider");
		this.cp = cp;
	}
	
	/**
	 * Returns a content provider that can be used for retreiving data from
	 * the internet. 
	 * 
	 * @return content provider for this engine
	 */
	protected final ContentProvider getContentProvider() {
		return cp;
	}
	
	/**
	 * Returns the capabilities of this comic engine.
	 * 
	 * @return a capabilities descriptor.
	 * @see ComicCapabilities
	 */
	public abstract ComicCapabilities getCapabilities();
		
	/**
	 * Returns a string that represents the title for a ComicStrip instance.
	 * 
	 * @param cs ComicStrip instance
	 * @return title
	 */
	public abstract String stripTitle(ComicStripIdentifier cs);

	/**
	 * Generates a URL string for a ComicStrip instance.
	 * 
	 * @param cs ComicStrip instance
	 * @return URL string
	 */
	public abstract String stripImageUrl(ComicStripIdentifier cs);
	
	@Override
	public final String toString() {
		return this.getClass().getName();
	}
	
	public abstract String getComicIdentifier();
	
	
}
