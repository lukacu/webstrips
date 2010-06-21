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
 * org/webstrips/comic/Archive.java
 * 
 * Created: November 4, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 04/11/2006 (lukacu) created
 *  - 19/11/2006 (lukacu) javadoc
 */

package org.webstrips.core.comic;

/**
 * Interface for archive capabilities of a comic engine.
 * 
 * Capabilities implemented with this interface:
 * <ul>
 * <li><code>archive</code></li>
 * </ul>
 * 
 * 
 * @author lukacu
 * @since WebStrips 0.3.3
 */
public interface Archive {

	public static final String ARCHIVE 				= "archive";
	
	/**
	 * Returnes an archive sequence
	 * 
	 * @param from comic ArchiveEntry that marks the poinf from which
	 * on the archive sequence should be retrieved. If it is <tt>null</tt>
	 * then the whole archive should be returned
	 * @return array of ArchiveEntries sorted from oldest to newest
	 */
	public ArchiveEntry[] archive(ArchiveEntry from);
	
}
