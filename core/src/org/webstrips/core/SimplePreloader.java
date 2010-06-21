/* WebStrips
 * 
 * License:
 * 
 * WebStrips is a lightweight web comics browser written in Java.
 * 
 * Copyright (C) 2007 Luka Cehovin
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
 * org/webstrips/navigator/Preloader.java
 * 
 * Created: February 18, 2007
 * Author: lukacu
 * 
 * Log:
 *  - 18/02/2007 (lukacu) created
 *  
 */

package org.webstrips.core;

import org.webstrips.core.Comic;
import org.webstrips.core.Preloader;

public class SimplePreloader extends Preloader {

	private int strategyCounter = 0;

	public SimplePreloader(Comic comic, int size) {
		super(comic, size);
	}
	
	protected void resetPlotting() {
		strategyCounter = 0;
	}
	
	protected Action plotNextStep() {
		
		if (strategyCounter >= capacity())
			return Action.HALT;
		
		return ((strategyCounter++) % 3) < 2 ? Action.EXPAND_FORTH : Action.EXPAND_BACK;
		
	}

}
