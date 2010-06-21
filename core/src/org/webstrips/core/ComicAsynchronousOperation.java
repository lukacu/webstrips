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
 * org/webstrips/navigator/ComicDriverAsynchronousOperation.java
 * 
 * Created: October 25,2006
 * Author: lukacu
 * 
 * Log:
 *  - 25/10/2006 (lukacu) created
 *  - 24/11/2006 (lukacu) javadoc
 */

package org.webstrips.core;

import org.webstrips.core.data.Cache;
import org.webstrips.core.data.CacheProvider;
import org.webstrips.core.data.Transfer;
import org.webstrips.core.data.TransferListener;

/**
 * Asyncronous operation owned by a comic
 * 
 * @author luka
 * @see AsynchronousOperation
 * @since WebStrips 0.3.1
 */
public abstract class ComicAsynchronousOperation<T> extends AsynchronousOperation {
	
	private ComicStrip strip;

	private Cache<byte[]> cache;
	
	private T result = null;
	
	private class ContextAwareThread extends Thread implements TransferListener, CacheProvider<byte[]> {
		
		public ContextAwareThread(Runnable r) {
			super(r);
		}
		
		public void transferConnecting(Transfer<?> d) {

			fireProgressEvent(-1);

		}

		public void transferStarted(Transfer<?> d) {
			
			fireProgressEvent(0);
			
		}

		public void transferProgress(Transfer<?> d, float progress) {		
			fireProgressEvent(progress);
		}

		public void transferEnded(Transfer<?> d) {
			fireProgressEvent(1);
		}

		public void transferError(Transfer<?> d, int error, String errorMessage) {
			
			WebStrips.getLogger().report(WebStrips.TRANSFER, "Transfer error %s (%d)", 
					errorMessage, error);

			fireProgressEvent(-1);
		}

		public Cache<byte[]> getCache() {			
			return cache;
		}
		
	}

	public ComicAsynchronousOperation(ComicStrip strip, Cache<byte []> cache) {
		super();
		this.strip = strip;
		this.cache = cache;
	}
	
	public ComicStripIdentifier getComicStripIdentifier() {
		return strip.getIdentifier();
	}

	protected ComicStrip getComicStrip() {
		return strip;
	}
	
	@Override
	protected Thread createThread(Runnable r) {
		return new ContextAwareThread(r);
	}
	
	public T getResult() {
		return result;
	}
	
	protected final void setResult(T result) {
		this.result = result;
	}
	
}