package org.webstrips.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;

public abstract class StorageManager {

	private static StorageManager manager = null;
	
	public static StorageManager getStorageManager() {
		
		if (manager == null) {
		
			String sm = System.getProperty("org.webstrips.StorageManager");
			
	        try {
	            Class<?> smClass = Class.forName(sm);
	            Constructor<?> smConstructor = smClass.getConstructor();
	            manager = (StorageManager) smConstructor.newInstance();
	            
	        } catch (Exception e) {
	        	throw new RuntimeException(e);
	        } 
		}
		return manager;
	}
	
	public abstract InputStream getInputStream(String resource) throws IOException;
	
	public abstract OutputStream getOutputStream(String resource) throws IOException;

	public abstract void delete(String resource) throws IOException;
	
	public abstract File getStorageDirectory();
	
	public abstract File getCacheDirectory();
	
}
