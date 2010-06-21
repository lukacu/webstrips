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
 * org/webstrips/navigator/ComicInformationProvider.java
 * 
 * Created: October 19,2006
 * Author: lukacu
 * 
 * Log:
 *  - 19/10/2006 (lukacu) created
 */

package org.webstrips.core;

/**
 * Interface for an object that provides information about a comic
 * 
 * @author lukacu
 * @since WebStrips 0.3.2
 */
public interface ComicInformationProvider {

	/**
	 * Returns name of the comic
	 * 
	 * @return comic name
	 */
	public abstract String getComicName();

	/**
	 * Returns author(s) of the comic
	 * 
	 * @return comic author
	 */
	public abstract String getComicAuthor();

	/**
	 * Returns description of the comic
	 * 
	 * @return comic description
	 */
	public abstract String getComicDescription();

	/**
	 * Returns homepage of the comic. The string is supposed to
	 * contain a valid URL address.
	 * 
	 * @return comic homepage
	 */
	public abstract String getComicHomapage();
}