package org.webstrips.core.data;

public interface Cache<T> {

	/**
	 * Instert an object into cache using specified key
	 * 
	 * @param key key used to retreive cached object
	 * @param object the object to be cached
	 */
	public void insert(String key, T object);
	
	/**
	 * 
	 * 
	 * @param key
	 * @return
	 */
	public T query(String key);
	
	public boolean contains(String key);
	
	public T remove(String key);
	
	public void flush();
	
	public int size();
	
}
