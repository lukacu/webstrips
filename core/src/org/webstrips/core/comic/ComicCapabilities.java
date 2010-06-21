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
 * org/webstrips/comic/ComicCapabilities.java
 * 
 * Created: May 3, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 3/05/2006 (lukacu) created
 * 
 */


package org.webstrips.core.comic;

import java.util.ArrayList;

/**
 * Comic engine capabilities descriptor class. This class is used by the
 * comic engine to tell the navigator application what it is capable of
 * providing.
 * 
 * @author lukacu
 * @since WebStrips 0.2
 */
public final class ComicCapabilities {
	
	private ArrayList<String> _capabilities = new ArrayList<String>();
	
	/**
	 * Constructs new descriptor with given capabilities.
	 * 
	 * @param caps array of strings that represent specific capabilities.
	 */
	public ComicCapabilities(String[] caps) {
		if (caps != null) { 
		
			for (int i = 0; i < caps.length; i++) {
				if (caps[i] == null || caps[i].length() == 0)
					continue;
				if (hasCapability(caps[i]))
					continue;
				_capabilities.add(caps[i]);
			}
			
		}
	}
	
	/**
	 * Constructs new descriptor with given capabilities.
	 * 
	 * @param caps ArrayList object of strings that represent specific capabilities.
	 */
	public ComicCapabilities(ArrayList<String> caps) {
		if (caps != null) { 

			for (int i = 0; i < caps.size(); i++) {
				if (caps.get(i) == null || caps.get(i).length() == 0)
					continue;
				if (hasCapability(caps.get(i)))
					continue;
				_capabilities.add(caps.get(i));
			}
			
		}
	}
	
	/**
	 * Tests if this descriptor posesses a certain capability.
	 * 
	 * @param cap capability to test
	 * @return <code>true</code> if this descriptor posesses a certain 
	 * capability, <code>false</code> otherwise.
	 */
	public boolean hasCapability(String cap) {
		if (cap == null || cap.length() == 0) return false;
		
		for (int i = 0; i < _capabilities.size(); i++) {
			if (_capabilities.get(i).compareTo(cap) == 0)
				return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {

		if (_capabilities.size() == 0)
			return "";
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(_capabilities.get(0));
		
		for (int i = 1; i < _capabilities.size(); i++) {
			sb.append(", ");
			sb.append(_capabilities.get(i));
		}
		
		return sb.toString();
	}
	
}
