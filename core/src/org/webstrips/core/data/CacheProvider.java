package org.webstrips.core.data;


public interface CacheProvider<T> {

	public Cache<T> getCache();
	
}
