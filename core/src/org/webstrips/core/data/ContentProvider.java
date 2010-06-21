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
 * org/webstrips/data/ContentProvider.java
 * 
 * Created: Apr 26, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 26/04/2006 (lukacu) created
 *  - 30/04/2006 (lukacu) javadoc
 *  - 23/07/2006 (lukacu) custom content loading (progress monitoring)
 *  - 18/02/2007 (lukacu) listeners mechanism
 *  - 19/02/2007 (lukacu) importance weighting
 */

package org.webstrips.core.data;

import java.net.URL;

import org.coffeeshop.log.Logger;
import org.coffeeshop.string.StringUtils;
import org.webstrips.core.WebStrips;

/**
 * ContentProvider is a gateway for retrieving internet resources souch as HTML
 * documents, images, etc. It posseses an internal cache buffer that speeds up
 * multiple downloads of the same resource.
 * 
 * @author lukacu
 * @since WebStrips 0.1
 * 
 */
public class ContentProvider {

	private static final TransferManager manager = new TransferManager();
	
	public static void addListener(TransferManagerListener l) {
		
		manager.addListener(l);
		
	}
	
	public static void removeListener(TransferManagerListener l) {

		manager.removeListener(l);
		
	}
	
	private String name;
	
	public ContentProvider(String name) {
		this.name = name;
	}
	
	/**
	 * Retrieves a HTML document and returns its content in a string. <b>Note:</b>
	 * this method does not verify that the specified resource is indeed a HTML
	 * document so virtualy anything can be downloaded with it.
	 * 
	 * @param link
	 *            location of the document
	 * @return content of the document or <code>null</code> if it could not be
	 *         retrieved.
	 */
	public String retriveHtml(URL link) {

		byte[] data = retrive(link);
		
		if (data == null)
			return "";
		
		return new String(data);
		
	}

	/**
	 * Retrieves an image and returns its content in an Image object.
	 * 
	 * @param link
	 *            location of the image
	 * @return Image object
	 * @see Image
	 */
	public byte[] retrive(URL link) {

		Cache<byte []> cache = getAvailableCache();
		
		if (cache != null)
			synchronized (cache) {
				byte[] o = (byte[])cache.query(link.toString());
				if (o != null) {
					WebStrips.getLogger().report(
							Logger.APPLICATION_INTERNAL_5, "Cached: " + link);
					
					if (StringUtils.same(System.getProperty("org.webstrips.transfer.debug"), "true")) {
						WebStrips.getLogger().report(WebStrips.TRANSFER, "Got %d bytes for %s", o.length, link);
					}
					
					return o;
				}
			}

		WebStrips.getLogger().report(
				Logger.APPLICATION_INTERNAL_5, "Not Cached: " + link);

		byte[] data = manager.download(link, getAvailableListener(), name);

		if (data == null)
			return null;
		
		if (cache != null)
			synchronized (cache) {
				cache.insert(link.toString(), data);
			}

		if (StringUtils.same(System.getProperty("org.webstrips.transfer.debug"), "true")) {
			WebStrips.getLogger().report(WebStrips.TRANSFER, "Got %d bytes for %s", data.length, link);
		}
		
		return data;
	}

	/**
	 * Retrieves an image and returns its content in an Image object.
	 * 
	 * @param link
	 *            location of the image
	 * @return Image object
	 * @see Image
	 */
	public boolean urlExists(URL link) {
		return manager.validateURL(link, getAvailableListener(), name);
	}
	
	private TransferListener getAvailableListener() {
		
		Thread t = Thread.currentThread();
		
		if (TransferListener.class.isAssignableFrom(t.getClass())) {
			
			return (TransferListener)t;
			
		} else return null;

	}
	
	@SuppressWarnings("unchecked")
	public Cache<byte[]> getAvailableCache() {
		
		Thread t = Thread.currentThread();
		
		if (CacheProvider.class.isAssignableFrom(t.getClass())) {
			
			return ((CacheProvider<byte[]>)t).getCache();
			
		} else return null;
		
	}
	
}
