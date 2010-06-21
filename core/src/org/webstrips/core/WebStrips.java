package org.webstrips.core;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.coffeeshop.log.AbstractLogger;
import org.coffeeshop.log.Logger;
import org.coffeeshop.string.StringUtils;
import org.webstrips.core.data.Cache;
import org.webstrips.core.data.DataCache;
import org.webstrips.core.data.ObjectCache;

public class WebStrips {

	private static AbstractLogger logger = null;
	
	public static AbstractLogger getLogger() {
		if (logger == null)
			logger = new Logger();
		
		return logger;
	}
		
	public static void setLogger(AbstractLogger logger) {
		WebStrips.logger = logger;
	}
		
	public static final String[][] Authors = {
		{"Luka \u010Cehovin", "http://luka.tnode.com"}
	};
	
	public static final String[][] ThanksTo = {
		{"Marko Toplak", "http://www2.arnes.si/~sodmtopl/"},
		{"Ciril Bohak", null},
	};
	
	public static String getApplicationIdentifier() {
		String idt = System.getProperty("org.webstrips.identifier");
		if (StringUtils.empty(idt))
			return "WebStrips";
		return idt;
	}
	
	/**
	 * Allocated internal channels constants
	 */
	
	public static final int COMICS = Logger.APPLICATION_INTERNAL_1;
	
	public static final int CACHE = Logger.APPLICATION_INTERNAL_2;
	
	public static final int PRELOADER = Logger.APPLICATION_INTERNAL_6;
	
	public static final int TRANSFER = Logger.APPLICATION_INTERNAL_5;
	
	public static final int JSOUT = Logger.APPLICATION_INTERNAL_4;
	
	public static final int JSINFO = Logger.APPLICATION_INTERNAL_7;
	
	public static final int JSERROR = Logger.APPLICATION_INTERNAL_8;
	
	private static StorageManager storageManager = null;
	
	public static StorageManager getStorageManager() {
		
		if (storageManager == null) {
		
			String sm = System.getProperty("org.webstrips.StorageManager");
			
	        try {
	            Class<?> smClass = Class.forName(sm);
	            Constructor<?> smConstructor = smClass.getConstructor();
	            storageManager = (StorageManager) smConstructor.newInstance();
	            
	        } catch (Exception e) {
	        	throw new RuntimeException(e);
	        } 
		}
		return storageManager;
	}

	private static SettingsManager settings = null;
	
	public static SettingsManager getSettings() {
		
		if (settings == null) {
		
			String sm = System.getProperty("org.webstrips.SettingsManager");
			
	        try {
	            Class<?> smClass = Class.forName(sm);
	            Constructor<?> smConstructor = smClass.getConstructor();
	            settings = (SettingsManager) smConstructor.newInstance();
	            
	        } catch (Exception e) {
	        	throw new RuntimeException(e);
	        } 
		}
		return settings;
	}
	
	private static Cache<byte[]> cache = null;
	
	public static Cache<byte[]> getCache() {
		
		if (cache != null)
			return cache;
		
		try {
			
			cache = new DataCache(Long.parseLong(System.getProperty("org.webstrips.memory")),
					Long.parseLong(System.getProperty("org.webstrips.cache")));
			
		} catch (IOException e) {
			
			WebStrips.getLogger().report(Logger.WARNING, "Unable to create disk cache because of: ", e);
			WebStrips.getLogger().report(Logger.WARNING, "Caching performance will be limited.");
			
			// using plain Object cache object. Just 10 images are stored into memory and that is all.
			cache = new ObjectCache<byte[]>(10);
			
		}
		
		return cache;
		
	}
}
