
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
 * org/webstrips/data/ObjectCache.java
 * 
 * Created: Apr 26, 2006
 * Author: lukacu
 * 
 * Log:
 * 	- 26/04/2006 (lukacu) created
 *  - 30/04/2006 (lukacu) javadoc
 *  - 14/02/2007 (lukacu) added global object capacity
 *  - 19/02/2007 (lukacu) importacne factor
 *  - 19/04/2007 (lukacu) splitted to this class and DataCache class, removed global cache
 *  - 09/05/2007 (lukacu) added generic Cache interface
 */

package org.webstrips.core.data;

import java.util.PriorityQueue;
import java.util.TreeMap;


/**
 * ObjectCache provides a caching mechanism for retrieval operations that would
 * otherwise require some time to complete. The resources are identified by a
 * string key that is usually an URL of the resource.
 * 
 * @author lukacu
 * @since WebStrips 0.1
 */
public class ObjectCache<T> implements Cache<T> {

	/**
	 * Maximum capacity of the cache
	 */
	private int limit;

	private PriorityQueue<ObjectWrapper> orderedAccess;

	private TreeMap<String, ObjectWrapper> orderedContainer;

	/**
	 * Internal wrapper of the objects that records the time
	 * 
	 * @author lukacu
	 * @since WebStrips 0.1
	 * @see ObjectCache
	 */
	private class ObjectWrapper implements Comparable<ObjectWrapper> {

		private long timestamp;

		private T object;

		private String key;

		/**
		 * Construct a new wrapper for the object. The wrapper stores the key,
		 * the object and the time of the last access to the object.
		 * 
		 * The constructor sets the timestamp of the last access to the time of
		 * object construction.
		 * 
		 * @param key
		 *            key of the object
		 * @param o
		 *            object to wrap
		 */
		public ObjectWrapper(String key, T o) {
			object = o;
			this.key = key;
			timestamp = System.currentTimeMillis();
		}

		/**
		 * 
		 * @param o
		 * @return
		 * 
		 * @see Comparable#compareTo(T)
		 */
		public int compareTo(ObjectWrapper o) {

			return (o.timestamp < timestamp ? 1
					: o.timestamp > timestamp ? -1 : 0);
		}

		/**
		 * Returns the object that is wrapped in this wrapper
		 * 
		 * @return
		 */
		public T getObject() {
			return this.object;
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
	}

	public ObjectCache(int objectLimit) {

		limit = objectLimit;

		orderedAccess = new PriorityQueue<ObjectWrapper>();

		orderedContainer = new TreeMap<String, ObjectWrapper>();

	}

	/**
	 * Searches for the object by its key. If the object is found, the method
	 * also updates its access time.
	 * 
	 * @param key
	 *            the key to search with
	 * @return the object or <code>null</code> if no object is found
	 */
	public synchronized T query(String key) {

		if (key == null)
			return null;
		
		ObjectWrapper ow = (ObjectWrapper) orderedContainer.get(key);

		if (ow == null)
			return null;

		// update the cache access list
		orderedAccess.remove(ow);
		ow.touch();
		orderedAccess.add(ow);

		return ow.getObject();
	}
	
	/**
	 * Inserts an object to the cache. If the same (with same key) object
	 * already exists, nothing is done.
	 * 
	 * @param key
	 *            a key that is used to identify the object
	 * @param o
	 *            the object itself
	 */
	public synchronized void insert(String key, T o) {

		// prevent duplicates
		if (contains(key))
			return;

		ObjectWrapper ow = new ObjectWrapper(key, o);

		orderedAccess.add(ow);

		orderedContainer.put(key, ow);

		performPurge();
		
	}

	/**
	 * Performs a Least Recently Used picking and removes the suitable object
	 * from the cache.
	 */
	private synchronized void performPurge() {

		performPurge(orderedContainer.size() - limit);

	}

	/**
	 * Performs a Least Recently Used picking and removes the cpecified number
	 * of object from the cache.
	 * 
	 * @param purge
	 *            number of elements to remove
	 */
	private synchronized void performPurge(int purge) {

		if (purge < 1)
			return;

		for (int i = 0; i < purge; i++) {
			ObjectWrapper ow = orderedAccess.poll();

			if (ow == null)
				return;

			orderedContainer.remove(ow.getKey());

		}

	}

	public synchronized void flush() {
		
		orderedAccess.clear();
		orderedContainer.clear();
		
	}
	
	public synchronized int size() {
		return orderedAccess.size();
	}

	public int capacity() {
		return limit;
	}
	
	public synchronized boolean contains(String key) {
		if (key == null)
			return false;
		
		ObjectWrapper ow = (ObjectWrapper) orderedContainer.get(key);

		return (ow != null);
	}

	public synchronized T remove(String key) {
	
		if (key == null)
			return null;
		
		ObjectWrapper ow = orderedContainer.remove(key);

		if (ow == null)
			return null;

		// update the cache access list
		orderedAccess.remove(ow);

		return ow.getObject();
		
	}

}
