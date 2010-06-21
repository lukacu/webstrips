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
 * org/webstrips/data/DataCache.java
 * 
 * Created: Apr 19, 2007
 * Author: lukacu
 * 
 * Log:
 * 	- 26/04/2006 (lukacu) created
 *  - 30/04/2006 (lukacu) javadoc
 *  - 14/02/2007 (lukacu) added global object capacity
 *  - 19/02/2007 (lukacu) importacne factor
 *  - 19/04/2007 (lukacu) reorganisation, disk cache, splitted from ObjectCache
 *  - 09/05/2007 (lukacu) added generic Cache interface
 */

package org.webstrips.core.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.TreeMap;

import org.coffeeshop.io.TempDirectory;
import org.webstrips.core.WebStrips;

/**
 * DataCache provides a caching mechanism for retrieval operations that would
 * otherwise require some time to complete. The resources are identified by a
 * string key that is usually an URL of the resource.
 * 
 * @author lukacu
 * @since WebStrips 0.4.2
 */
public class DataCache implements Cache<byte[]> {

	/**
	 * Maximum capacity of the cache
	 */
	private long memoryLimit, totalLimit, totalUsage = 0, memoryUsage = 0;

	private PriorityQueue<DataWrapper> orderedAccess;

	private PriorityQueue<DataWrapper> orderedAccessMemory;
	
	private TreeMap<String, DataWrapper> orderedContainer;

	private TempDirectory tempDir;
	
	/**
	 * Internal wrapper of the objects that records the time
	 * 
	 * @author lukacu
	 * @since WebStrips 0.1
	 * @see ObjectCache
	 */
	private class DataWrapper implements Comparable<DataWrapper> {

		private long timestamp;

		private byte[] data;

		private long length = 0;
		
		private String key;

		private File fileHandle = null;
		
		/**
		 * Construct a new wrapper for the data. The wrapper stores the key,
		 * the data and the time of the last access to the data.
		 * 
		 * The constructor sets the timestamp of the last access to the time of
		 * object construction.
		 * 
		 * @param key
		 *            key of the object
		 * @param o
		 *            data to wrap
		 */
		public DataWrapper(String key, byte[] o) {
			data = o;
			this.key = key;
			timestamp = System.currentTimeMillis();
			length = data.length;
		}

		public long getTimestamp() {
			
			return timestamp;
			
		}
		
		/**
		 * 
		 * @param o
		 * @return
		 * 
		 * @see Comparable#compareTo(T)
		 */
		public int compareTo(DataWrapper o) {
			DataWrapper ow = (DataWrapper) o;

			return ow.getTimestamp() < getTimestamp() ? 1
					: (ow.getTimestamp() > getTimestamp() ? -1 : 0);
		}

		/**
		 * Returns the object that is wrapped in this wrapper
		 * 
		 * @return
		 */
		public byte[] getData() throws IOException {
			if (data == null) {
				try {
					readFromDisk();
				}
				catch (IOException e) {
					data = null;
					throw e;
				}
			}
			
			return data;
		}

		public boolean pushToDisk() {
			try {
			
				writeToDisk();
			
				data = null;
				
				return true;
				
			} catch (IOException e) {
				return false;
			}
		}
		
		private void readFromDisk() throws IOException {
			if (fileHandle == null || !fileHandle.canRead())
				throw new IOException("Data does not exist or is not readable.");
			
			if (fileHandle.length() != length)
				throw new IOException("File length is wrong.");

			WebStrips.getLogger().report(WebStrips.CACHE, "Reading from disk: " + fileHandle);
			
			data = new byte[(int)length];
			
			FileInputStream r = new FileInputStream(fileHandle);
			r.read(data);
			
			r.close();
		}
		
		private void writeToDisk() throws IOException {
			if (fileHandle != null || data == null)
				return;
			
			synchronized (tempDir) {
				fileHandle = tempDir.tempFileName("cache");
				
				WebStrips.getLogger().report(WebStrips.CACHE, "Writing to disk: " + fileHandle);
				
				FileOutputStream w = new FileOutputStream(fileHandle);
				
				w.write(data);
				
				w.close();
			}
		}
		
		public long getLength() {
			return length;
		}
		
		/**
		 * Returns the key of the object
		 * 
		 * @return the key
		 */
		public String getKey() {
			return key;
		}

		/**
		 * Updates the timestamp to the current time.
		 * 
		 */
		public void touch() {
			timestamp = System.currentTimeMillis();
		}

		public void delete() {
			
			if (fileHandle != null)
				fileHandle.delete();
			
			data = null;
			
		}
		
	}

	public DataCache(long memoryLimit, long totalLimit) throws IOException {

		if (memoryLimit > totalLimit)
			throw new IllegalArgumentException("Memory limit must be lower or equal that the total limit.");
		
		this.memoryLimit = memoryLimit;
		this.totalLimit = totalLimit;
		
		orderedAccess = new PriorityQueue<DataWrapper>();

		orderedAccessMemory = new PriorityQueue<DataWrapper>();
		
		orderedContainer = new TreeMap<String, DataWrapper>();

		tempDir = new TempDirectory("webstrips", WebStrips.getStorageManager().getCacheDirectory());
		
	}

	/**
	 * Searches for the object by its key. If the object is found, the method
	 * also updates its access time.
	 * 
	 * @param key
	 *            the key to search with
	 * @return the object or <code>null</code> if no object is found
	 */
	public synchronized byte[] query(String key) {

		DataWrapper ow = (DataWrapper) orderedContainer.get(key);

		if (ow == null)
			return null;

		// update the cache access list
		orderedAccess.remove(ow);
		if (!orderedAccessMemory.remove(ow)) {
			memoryUsage += ow.getLength();
		}
		
		ow.touch();
		orderedAccess.add(ow);
		orderedAccessMemory.add(ow);
		
		try {
			
			return ow.getData();
		
		} catch (IOException e) {
			
			WebStrips.getLogger().report(WebStrips.CACHE, "Error: " + e.getMessage());
			
			orderedAccess.remove(ow);
			if (orderedAccessMemory.remove(ow)) 
				memoryUsage -= ow.getLength();
			
			totalUsage -= ow.getLength();
			
			return null;
		
		}
	}

	/**
	 * Inserts an data to the cache. If the same (with same key) data object
	 * already exists, nothing is done.
	 * 
	 * @param key
	 *            a key that is used to identify the object
	 * @param o
	 *            the data itself
	 */
	public synchronized void insert(String key, byte[] o) {

		// prevent duplicates
		if (orderedContainer.get(key) != null)
			return;

		DataWrapper ow = new DataWrapper(key, o);

		orderedAccess.add(ow);
		orderedContainer.put(key, ow);
		orderedAccessMemory.add(ow);
		memoryUsage += ow.getLength();
		totalUsage += ow.getLength();			
	
		WebStrips.getLogger().report(WebStrips.CACHE, "Status - memory: " + memoryUsage + "/" + 
				memoryLimit + "; total: " + totalUsage + "/" + totalLimit);
		
		
		performPurge();

	}

	/**
	 * Finds the least recently used element and removes the suitable object
	 * from the cache.
	 */
	private synchronized void performPurge() {

		while (totalUsage > totalLimit) {
			
			removeOldest();

		}
		
		while (memoryUsage > memoryLimit) {
			
			removeOldestFromMemory();

		}

	}

	private synchronized void removeOldest() {

		synchronized (this) {
			DataWrapper ow = orderedAccess.poll();
			
			if (ow == null)
				return;

			orderedAccessMemory.remove(ow);
			orderedContainer.remove(ow.getKey());
			
			memoryUsage -= ow.getLength();
			totalUsage -= ow.getLength();	
			ow.delete();
		}

	}

	private synchronized void removeOldestFromMemory() {

		synchronized (this) {
			DataWrapper ow = orderedAccessMemory.poll();
			
			if (ow == null)
				return;

			memoryUsage -= ow.getLength();

			ow.pushToDisk();
		}

	}
	
	public synchronized void flush() {
		
		synchronized (this) {
			for (DataWrapper d : orderedAccess) {
				d.delete();
			}
			
			orderedAccess.clear();
			orderedContainer.clear();
			memoryUsage = 0;
			totalUsage = 0;
		}
		
	}
	
	public int size() {
		return orderedAccess.size();
	}

	public synchronized boolean contains(String key) {
		DataWrapper ow = (DataWrapper) orderedContainer.get(key);

		return (ow != null);

	}

	public synchronized byte[] remove(String key) {

		DataWrapper ow = (DataWrapper) orderedContainer.get(key);

		if (ow == null)
			return null;

		// update the cache access list
		orderedAccess.remove(ow);
		totalUsage -= ow.getLength();
		if (!orderedAccessMemory.remove(ow)) {
			memoryUsage -= ow.getLength();
		}

		try {
			
			return ow.getData();
		
		} catch (IOException e) {
			return null;
		}
	}

}


